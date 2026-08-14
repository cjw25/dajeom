package com.dajeom.exception;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 출생시간이 없어서
 * 명리 계산의 경계를 확정할 수 없는 경우 발생한다.
 *
 * 대표적인 경우:
 *
 * 1. 입춘 당일 + 출생시간 미상
 *    -> 년주가 입춘 전/후로 달라질 수 있음
 *
 * 2. 절입 당일 + 출생시간 미상
 *    -> 월주가 절입 전/후로 달라질 수 있음
 *
 * 다점에서는 이 경우
 * 00:00, 12:00 등의 임의 시간을 사용하지 않는다.
 */
public class BirthTimeRequiredException
        extends RuntimeException {

    public BirthTimeRequiredException(
            String message
    ) {
        super(
                message
        );
    }

    public static BirthTimeRequiredException forYearBoundary(
            LocalDate birthDate
    ) {

        Objects.requireNonNull(
                birthDate,
                "출생일이 필요합니다."
        );

        return new BirthTimeRequiredException(
                "출생일 "
                        + birthDate
                        + "은 입춘 경계일입니다. "
                        + "출생시간을 모르면 년주를 정확하게 확정할 수 없습니다."
        );
    }

    public static BirthTimeRequiredException forMonthBoundary(
            LocalDate birthDate
    ) {

        Objects.requireNonNull(
                birthDate,
                "출생일이 필요합니다."
        );

        return new BirthTimeRequiredException(
                "출생일 "
                        + birthDate
                        + "은 절입 경계일입니다. "
                        + "출생시간을 모르면 월주를 정확하게 확정할 수 없습니다."
        );
    }

    public static BirthTimeRequiredException forHourPillar() {

        return new BirthTimeRequiredException(
                "출생시간을 모르므로 시주를 계산할 수 없습니다."
        );
    }
}