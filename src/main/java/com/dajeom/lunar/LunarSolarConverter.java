package com.dajeom.lunar;

import com.dajeom.domain.BirthDateTime;
import com.dajeom.domain.CalendarType;
import com.dajeom.domain.Gender;

import net.time4j.PlainDate;
import net.time4j.calendar.EastAsianMonth;
import net.time4j.calendar.EastAsianYear;
import net.time4j.calendar.KoreanCalendar;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Component
public class LunarSolarConverter {

    /**
     * 양력 입력을 BirthDateTime으로 정규화한다.
     */
    public BirthDateTime normalizeSolar(
            String birthDate,
            LocalTime birthTime,
            Gender gender
    ) {

        Objects.requireNonNull(
                birthDate,
                "출생일이 필요합니다."
        );

        Objects.requireNonNull(
                gender,
                "성별이 필요합니다."
        );

        LocalDate solarDate;

        try {

            solarDate =
                    LocalDate.parse(
                            birthDate
                    );

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "올바른 양력 생년월일이 아닙니다. birthDate="
                            + birthDate,
                    e
            );
        }

        return new BirthDateTime(
                CalendarType.SOLAR,
                solarDate,
                birthTime,
                gender,
                false
        );
    }

    /**
     * 한국 음력 입력을 양력 BirthDateTime으로 변환한다.
     *
     * 음력 날짜 자체를 LocalDate로 만들지 않는다.
     *
     * 이유:
     * 음력 2월 30일처럼
     * 음력에서는 존재할 수 있지만 java.time.LocalDate에서는
     * 표현할 수 없는 날짜가 있기 때문이다.
     */
    public BirthDateTime normalizeLunar(
            String birthDate,
            LocalTime birthTime,
            Gender gender,
            boolean leapMonth
    ) {

        Objects.requireNonNull(
                birthDate,
                "출생일이 필요합니다."
        );

        Objects.requireNonNull(
                gender,
                "성별이 필요합니다."
        );

        LunarDateParts parts =
                parseLunarDate(
                        birthDate
                );

        EastAsianYear lunarYear =
                EastAsianYear.forGregorian(
                        parts.year()
                );

        EastAsianMonth lunarMonth;

        try {

            lunarMonth =
                    EastAsianMonth.valueOf(
                            parts.month()
                    );

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "음력 월은 1~12 사이여야 합니다. month="
                            + parts.month(),
                    e
            );
        }

        if (leapMonth) {

            lunarMonth =
                    lunarMonth.withLeap();
        }

        /*
         * 해당 연도에 실제로 존재하는 평달/윤달인지,
         * 해당 월에 그 날짜가 실제로 존재하는지 검증.
         */
        boolean valid =
                KoreanCalendar.isValid(
                        lunarYear,
                        lunarMonth,
                        parts.day()
                );

        if (!valid) {

            throw new IllegalArgumentException(
                    "존재하지 않는 한국 음력 날짜입니다. "
                            + "birthDate="
                            + birthDate
                            + ", leapMonth="
                            + leapMonth
            );
        }

        KoreanCalendar koreanDate =
                KoreanCalendar.of(
                        lunarYear,
                        lunarMonth,
                        parts.day()
                );

        PlainDate solarPlainDate =
                koreanDate.transform(
                        PlainDate.class
                );

        LocalDate solarDate =
                solarPlainDate.toTemporalAccessor();

        /*
         * 이후 사주 계산기는 전부 SOLAR만 받는다.
         *
         * 출생시간은 여기서 보정하지 않는다.
         * -30분 보정은 HourPillarCalculator 앞의
         * BirthTimeCorrectionCalculator에서만 한다.
         */
        return new BirthDateTime(
                CalendarType.SOLAR,
                solarDate,
                birthTime,
                gender,
                false
        );
    }

    private LunarDateParts parseLunarDate(
            String value
    ) {

        String[] parts =
                value.split(
                        "-"
                );

        if (parts.length != 3) {

            throw new IllegalArgumentException(
                    "생년월일 형식은 yyyy-MM-dd 이어야 합니다. birthDate="
                            + value
            );
        }

        try {

            int year =
                    Integer.parseInt(
                            parts[0]
                    );

            int month =
                    Integer.parseInt(
                            parts[1]
                    );

            int day =
                    Integer.parseInt(
                            parts[2]
                    );

            if (year <= 0) {

                throw new IllegalArgumentException(
                        "출생연도가 올바르지 않습니다. year="
                                + year
                );
            }

            if (month < 1 || month > 12) {

                throw new IllegalArgumentException(
                        "월은 1~12 사이여야 합니다. month="
                                + month
                );
            }

            /*
             * 음력 한 달은 29일 또는 30일.
             *
             * 실제 29/30 여부는 KoreanCalendar.isValid()가
             * 최종 판정한다.
             */
            if (day < 1 || day > 30) {

                throw new IllegalArgumentException(
                        "음력 일은 1~30 사이여야 합니다. day="
                                + day
                );
            }

            return new LunarDateParts(
                    year,
                    month,
                    day
            );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "생년월일 형식은 yyyy-MM-dd 이어야 합니다. birthDate="
                            + value,
                    e
            );
        }
    }

    private record LunarDateParts(
            int year,
            int month,
            int day
    ) {
    }
}