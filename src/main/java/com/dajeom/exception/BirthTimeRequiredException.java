package com.dajeom.exception;

/**
 * 출생시간을 모르는 상태에서
 * 정확한 명리 계산을 위해 시간이 반드시 필요한 경우 발생한다.
 *
 * 예:
 * - 입춘 당일 출생
 * - 월 경계 절입 당일 출생
 *
 * 이런 경우 임의의 시간을 만들어 계산하지 않는다.
 */
public class BirthTimeRequiredException
        extends RuntimeException {

    public BirthTimeRequiredException(
            String message
    ) {
        super(message);
    }
}