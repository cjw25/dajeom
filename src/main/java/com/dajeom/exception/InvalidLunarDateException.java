package com.dajeom.exception;

import java.time.LocalDate;

/**
 * 존재하지 않거나 변환할 수 없는
 * 한국 음력 날짜가 입력된 경우 발생한다.
 */
public class InvalidLunarDateException
        extends RuntimeException {

    public InvalidLunarDateException(
            String message
    ) {
        super(
                message
        );
    }

    public InvalidLunarDateException(
            LocalDate lunarDate,
            boolean leapMonth
    ) {
        super(
                createMessage(
                        lunarDate,
                        leapMonth
                )
        );
    }

    private static String createMessage(
            LocalDate lunarDate,
            boolean leapMonth
    ) {

        if (
                lunarDate == null
        ) {
            return "유효한 음력 생년월일이 필요합니다.";
        }

        return "변환할 수 없는 음력 날짜입니다: "
                + lunarDate
                + (
                leapMonth
                        ? " (윤달)"
                        : " (평달)"
        );
    }
}