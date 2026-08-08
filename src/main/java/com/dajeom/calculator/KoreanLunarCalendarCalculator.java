package com.dajeom.calculator;

import com.dajeom.exception.InvalidLunarDateException;
import com.dajeom.model.BirthDateValue;
import com.github.usingsky.calendar.KoreanLunarCalendar;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 한국 음력 -> 양력 변환 Calculator.
 *
 * KoreanLunarCalendar 0.3.1 사용.
 *
 * 평달/윤달을 구분해서
 * 실제 Gregorian 양력 날짜로 변환한다.
 */
@Component
public class KoreanLunarCalendarCalculator {

    private static final Object CALENDAR_LOCK =
            new Object();

    public LocalDate toSolarDate(
            BirthDateValue lunarDate,
            boolean leapMonth
    ) {

        Objects.requireNonNull(
                lunarDate,
                "음력 생년월일은 필수입니다."
        );

        if (
                lunarDate.day() > 30
        ) {

            throw new InvalidLunarDateException(
                    "음력 날짜의 일은 1일부터 30일까지 입력할 수 있습니다."
            );
        }

        synchronized (
                CALENDAR_LOCK
        ) {

            KoreanLunarCalendar calendar =
                    KoreanLunarCalendar
                            .getInstance();

            boolean success =
                    calendar.setLunarDate(
                            lunarDate.year(),
                            lunarDate.month(),
                            lunarDate.day(),
                            leapMonth
                    );

            if (!success) {

                throw new InvalidLunarDateException(
                        createInvalidMessage(
                                lunarDate,
                                leapMonth
                        )
                );
            }

            String solarDateText =
                    calendar
                            .getSolarIsoFormat();

            if (
                    solarDateText == null
                            || solarDateText.isBlank()
            ) {

                throw new InvalidLunarDateException(
                        "음력 날짜를 양력으로 변환하지 못했습니다."
                );
            }

            try {

                return LocalDate.parse(
                        solarDateText.trim()
                );

            } catch (
                    DateTimeException exception
            ) {

                throw new InvalidLunarDateException(
                        "음력 날짜를 양력으로 변환하는 과정에서 "
                                + "올바르지 않은 양력 날짜가 생성되었습니다: "
                                + solarDateText,
                        exception
                );
            }
        }
    }

    private String createInvalidMessage(
            BirthDateValue lunarDate,
            boolean leapMonth
    ) {

        String monthType =
                leapMonth
                        ? "윤달"
                        : "평달";

        return "올바른 한국 음력 날짜가 아니거나 "
                + "변환 지원 범위를 벗어났습니다. "
                + "입력값: "
                + lunarDate.isoFormat()
                + " "
                + monthType
                + ". 윤달 여부도 함께 확인해 주세요.";
    }
}