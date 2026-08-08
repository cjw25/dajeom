package com.dajeom.exception;

/**
 * 존재하지 않는 음력 날짜,
 * 잘못된 윤달,
 * 변환 지원 범위를 벗어난 날짜 등에 사용한다.
 */
public class InvalidLunarDateException
        extends IllegalArgumentException {

    public InvalidLunarDateException(
            String message
    ) {

        super(
                message
        );
    }

    public InvalidLunarDateException(
            String message,
            Throwable cause
    ) {

        super(
                message,
                cause
        );
    }
}