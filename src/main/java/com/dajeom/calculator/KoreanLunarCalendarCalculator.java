package com.dajeom.calculator;

import com.dajeom.exception.InvalidLunarDateException;
import com.dajeom.model.BirthDateValue;
import com.dajeom.model.CalendarNormalizationResult;
import com.dajeom.type.CalendarType;
import com.github.usingsky.calendar.KoreanLunarCalendar;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 한국 음력 ↔ 양력 변환 및
 * 사주 계산용 날짜 정규화를 담당한다.
 *
 * 사용 라이브러리:
 *
 * com.github.usingsky:KoreanLunarCalendar:0.4.0
 *
 * 다점 정책
 *
 * 1. 양력 입력은 날짜 자체를 계산 기준으로 사용한다.
 * 2. 음력 입력은 반드시 양력으로 변환한 뒤 계산한다.
 * 3. 윤달 여부를 반드시 구분한다.
 * 4. 평달과 윤달을 임의로 서로 대체하지 않는다.
 * 5. 존재하지 않는 윤달 입력은 즉시 오류 처리한다.
 * 6. 변환 결과를 다시 임의 보정하지 않는다.
 * 7. 음력 라이브러리의 간지를 사주팔자로 직접 사용하지 않는다.
 *
 * 중요:
 *
 * 이 클래스는 달력 변환만 담당한다.
 *
 * 년주/월주는 SolarTermCalculator,
 * 일주는 DayPillarCalculator,
 * 시주는 HourPillarCalculator가 계산한다.
 */
@Component
public class KoreanLunarCalendarCalculator {

    /**
     * KoreanLunarCalendar 공식 지원 범위.
     */
    private static final LocalDate MIN_LUNAR_DATE =
            LocalDate.of(
                    1000,
                    1,
                    1
            );

    private static final LocalDate MAX_LUNAR_DATE =
            LocalDate.of(
                    2050,
                    11,
                    18
            );

    private static final LocalDate MIN_SOLAR_DATE =
            LocalDate.of(
                    1000,
                    2,
                    13
            );

    private static final LocalDate MAX_SOLAR_DATE =
            LocalDate.of(
                    2050,
                    12,
                    31
            );

    /**
     * 라이브러리는 getInstance() 방식의
     * 상태 보유 singleton converter를 사용한다.
     *
     * setXXXDate() 호출 후 getter로 값을 읽는 구조이므로
     * 여러 요청이 동시에 들어왔을 때
     * set/read 사이에 다른 요청이 끼어들면 안 된다.
     *
     * 따라서 converter 자체를 lock으로 사용한다.
     */
    private final KoreanLunarCalendar calendar =
            KoreanLunarCalendar.getInstance();

    /**
     * 사용자 입력 달력을 양력 기준으로 정규화한다.
     */
    public CalendarNormalizationResult normalize(
            CalendarType calendarType,
            LocalDate inputDate,
            boolean leapMonth
    ) {

        Objects.requireNonNull(
                calendarType,
                "달력 구분이 필요합니다."
        );

        Objects.requireNonNull(
                inputDate,
                "생년월일이 필요합니다."
        );

        return switch (
                calendarType
                ) {

            case SOLAR ->
                    normalizeSolar(
                            inputDate,
                            leapMonth
                    );

            case LUNAR ->
                    normalizeLunar(
                            inputDate,
                            leapMonth
                    );
        };
    }

    /**
     * 양력 입력 정규화.
     */
    public CalendarNormalizationResult normalizeSolar(
            LocalDate solarDate,
            boolean leapMonth
    ) {

        Objects.requireNonNull(
                solarDate,
                "양력 생년월일이 필요합니다."
        );

        if (
                leapMonth
        ) {

            throw new IllegalArgumentException(
                    "양력 입력에는 윤달을 지정할 수 없습니다."
            );
        }

        validateSolarRange(
                solarDate
        );

        synchronized (
                calendar
        ) {

            boolean success =
                    calendar.setSolarDate(
                            solarDate.getYear(),
                            solarDate.getMonthValue(),
                            solarDate.getDayOfMonth()
                    );

            if (
                    !success
            ) {

                throw new IllegalArgumentException(
                        "변환할 수 없는 양력 날짜입니다: "
                                + solarDate
                );
            }

            LocalDate lunarDate =
                    LocalDate.of(
                            calendar.getLunarYear(),
                            calendar.getLunarMonth(),
                            calendar.getLunarDay()
                    );

            boolean lunarLeapMonth =
                    calendar.isIntercalation();

            return new CalendarNormalizationResult(
                    CalendarType.SOLAR,
                    solarDate,
                    false,
                    solarDate,
                    lunarDate,
                    lunarLeapMonth
            );
        }
    }

    /**
     * 음력 입력 정규화.
     *
     * LocalDate를 입력받는 기존 내부/공용 API.
     */
    public CalendarNormalizationResult normalizeLunar(
            LocalDate lunarDate,
            boolean leapMonth
    ) {

        Objects.requireNonNull(
                lunarDate,
                "음력 생년월일이 필요합니다."
        );

        validateLunarRange(
                lunarDate
        );

        synchronized (
                calendar
        ) {

            boolean success =
                    calendar.setLunarDate(
                            lunarDate.getYear(),
                            lunarDate.getMonthValue(),
                            lunarDate.getDayOfMonth(),
                            leapMonth
                    );

            if (
                    !success
            ) {

                throw new InvalidLunarDateException(
                        lunarDate,
                        leapMonth
                );
            }

            LocalDate solarDate =
                    LocalDate.of(
                            calendar.getSolarYear(),
                            calendar.getSolarMonth(),
                            calendar.getSolarDay()
                    );

            boolean actualLeapMonth =
                    calendar.isIntercalation();

            if (
                    actualLeapMonth
                            != leapMonth
            ) {

                throw new InvalidLunarDateException(
                        lunarDate,
                        leapMonth
                );
            }

            return new CalendarNormalizationResult(
                    CalendarType.LUNAR,
                    lunarDate,
                    leapMonth,
                    solarDate,
                    lunarDate,
                    actualLeapMonth
            );
        }
    }

    /**
     * 음력을 양력으로 변환한다.
     *
     * BirthDateValue는 특정 달력의 LocalDate가 아니므로
     * Gregorian LocalDate로 중간 변환하지 않는다.
     *
     * year/month/day 정수값을 음력 Converter에
     * 직접 전달한다.
     */
    public LocalDate toSolar(
            BirthDateValue lunarDate,
            boolean leapMonth
    ) {

        Objects.requireNonNull(
                lunarDate,
                "음력 생년월일이 필요합니다."
        );

        validateLunarRange(
                lunarDate
        );

        synchronized (
                calendar
        ) {

            boolean success =
                    calendar.setLunarDate(
                            lunarDate.year(),
                            lunarDate.month(),
                            lunarDate.day(),
                            leapMonth
                    );

            if (
                    !success
            ) {

                throw new InvalidLunarDateException(
                        "올바른 음력 생년월일이 아닙니다: "
                                + lunarDate.isoFormat()
                                + " / 윤달="
                                + leapMonth
                );
            }

            boolean actualLeapMonth =
                    calendar.isIntercalation();

            if (
                    actualLeapMonth
                            != leapMonth
            ) {

                throw new InvalidLunarDateException(
                        "요청한 평달/윤달 상태와 실제 음력 상태가 일치하지 않습니다: "
                                + lunarDate.isoFormat()
                                + " / 요청 윤달="
                                + leapMonth
                );
            }

            return LocalDate.of(
                    calendar.getSolarYear(),
                    calendar.getSolarMonth(),
                    calendar.getSolarDay()
            );
        }
    }

    /**
     * 음력을 양력으로만 변환하고 싶은 경우.
     */
    public LocalDate toSolar(
            LocalDate lunarDate,
            boolean leapMonth
    ) {

        return normalizeLunar(
                lunarDate,
                leapMonth
        ).solarDate();
    }

    /**
     * 양력에 대응하는 음력 날짜.
     */
    public LocalDate toLunar(
            LocalDate solarDate
    ) {

        return normalizeSolar(
                solarDate,
                false
        ).lunarDate();
    }

    /**
     * 양력 날짜가 음력 윤달에 대응하는지 확인.
     */
    public boolean isLunarLeapMonth(
            LocalDate solarDate
    ) {

        return normalizeSolar(
                solarDate,
                false
        ).lunarLeapMonth();
    }

    /**
     * 특정 음력 날짜가 실제로 존재하는지 확인.
     */
    public boolean isValidLunarDate(
            LocalDate lunarDate,
            boolean leapMonth
    ) {

        if (
                lunarDate == null
        ) {

            return false;
        }

        if (
                !isWithinLunarRange(
                        lunarDate
                )
        ) {

            return false;
        }

        synchronized (
                calendar
        ) {

            return calendar.setLunarDate(
                    lunarDate.getYear(),
                    lunarDate.getMonthValue(),
                    lunarDate.getDayOfMonth(),
                    leapMonth
            );
        }
    }

    /**
     * 양력 날짜의 라이브러리 지원 여부.
     */
    public boolean isValidSolarDate(
            LocalDate solarDate
    ) {

        if (
                solarDate == null
        ) {

            return false;
        }

        if (
                !isWithinSolarRange(
                        solarDate
                )
        ) {

            return false;
        }

        synchronized (
                calendar
        ) {

            return calendar.setSolarDate(
                    solarDate.getYear(),
                    solarDate.getMonthValue(),
                    solarDate.getDayOfMonth()
            );
        }
    }

    public LocalDate minimumSupportedLunarDate() {

        return MIN_LUNAR_DATE;
    }

    public LocalDate maximumSupportedLunarDate() {

        return MAX_LUNAR_DATE;
    }

    public LocalDate minimumSupportedSolarDate() {

        return MIN_SOLAR_DATE;
    }

    public LocalDate maximumSupportedSolarDate() {

        return MAX_SOLAR_DATE;
    }

    private void validateLunarRange(
            LocalDate lunarDate
    ) {

        if (
                !isWithinLunarRange(
                        lunarDate
                )
        ) {

            throw new InvalidLunarDateException(
                    "지원하는 음력 날짜 범위를 벗어났습니다: "
                            + lunarDate
                            + " / 지원 범위: "
                            + MIN_LUNAR_DATE
                            + " ~ "
                            + MAX_LUNAR_DATE
            );
        }
    }

    /**
     * BirthDateValue 전용 범위 검증.
     *
     * Gregorian LocalDate를 생성하지 않는다.
     */
    private void validateLunarRange(
            BirthDateValue lunarDate
    ) {

        if (
                compare(
                        lunarDate,
                        MIN_LUNAR_DATE
                ) < 0

                        || compare(
                        lunarDate,
                        MAX_LUNAR_DATE
                ) > 0
        ) {

            throw new InvalidLunarDateException(
                    "지원하는 음력 날짜 범위를 벗어났습니다: "
                            + lunarDate.isoFormat()
                            + " / 지원 범위: "
                            + MIN_LUNAR_DATE
                            + " ~ "
                            + MAX_LUNAR_DATE
            );
        }
    }

    private int compare(
            BirthDateValue value,
            LocalDate boundary
    ) {

        int yearCompare =
                Integer.compare(
                        value.year(),
                        boundary.getYear()
                );

        if (
                yearCompare != 0
        ) {

            return yearCompare;
        }

        int monthCompare =
                Integer.compare(
                        value.month(),
                        boundary.getMonthValue()
                );

        if (
                monthCompare != 0
        ) {

            return monthCompare;
        }

        return Integer.compare(
                value.day(),
                boundary.getDayOfMonth()
        );
    }

    private void validateSolarRange(
            LocalDate solarDate
    ) {

        if (
                !isWithinSolarRange(
                        solarDate
                )
        ) {

            throw new IllegalArgumentException(
                    "지원하는 양력 날짜 범위를 벗어났습니다: "
                            + solarDate
                            + " / 지원 범위: "
                            + MIN_SOLAR_DATE
                            + " ~ "
                            + MAX_SOLAR_DATE
            );
        }
    }

    private boolean isWithinLunarRange(
            LocalDate lunarDate
    ) {

        return !lunarDate.isBefore(
                MIN_LUNAR_DATE
        )
                && !lunarDate.isAfter(
                MAX_LUNAR_DATE
        );
    }

    private boolean isWithinSolarRange(
            LocalDate solarDate
    ) {

        return !solarDate.isBefore(
                MIN_SOLAR_DATE
        )
                && !solarDate.isAfter(
                MAX_SOLAR_DATE
        );
    }
}