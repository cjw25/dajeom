package com.dajeom.model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 사용자가 입력한 원래 생년월일.
 *
 * 중요한 점:
 *
 * 이 값 자체는 양력 LocalDate가 아니다.
 *
 * 양력 입력:
 * Gregorian 규칙으로 검증
 *
 * 음력 입력:
 * KoreanLunarCalendar 규칙으로 검증
 *
 * 하도록 달력 종류와 날짜 검증 책임을 분리한다.
 */
public record BirthDateValue(

        int year,

        int month,

        int day

) {

    private static final Pattern ISO_PATTERN =
            Pattern.compile(
                    "^(\\d{4})-(\\d{2})-(\\d{2})$"
            );

    public BirthDateValue {

        if (year < 1 || year > 9999) {

            throw new IllegalArgumentException(
                    "생년은 1부터 9999 사이여야 합니다."
            );
        }

        if (month < 1 || month > 12) {

            throw new IllegalArgumentException(
                    "생월은 1부터 12 사이여야 합니다."
            );
        }

        /*
         * 여기서는 특정 달력 규칙을
         * 적용하지 않는다.
         *
         * 양력은 LocalDate,
         * 음력은 KoreanLunarCalendar에서
         * 최종 날짜 유효성을 검사한다.
         */
        if (day < 1 || day > 31) {

            throw new IllegalArgumentException(
                    "생일은 1부터 31 사이여야 합니다."
            );
        }
    }

    /**
     * API의 yyyy-MM-dd 문자열을
     * BirthDateValue로 변환한다.
     */
    public static BirthDateValue parse(
            String value
    ) {

        Objects.requireNonNull(
                value,
                "생년월일은 필수입니다."
        );

        Matcher matcher =
                ISO_PATTERN.matcher(
                        value.trim()
                );

        if (!matcher.matches()) {

            throw new IllegalArgumentException(
                    "생년월일은 yyyy-MM-dd 형식이어야 합니다."
            );
        }

        int year =
                Integer.parseInt(
                        matcher.group(1)
                );

        int month =
                Integer.parseInt(
                        matcher.group(2)
                );

        int day =
                Integer.parseInt(
                        matcher.group(3)
                );

        return new BirthDateValue(
                year,
                month,
                day
        );
    }

    /**
     * 이 값을 양력 Gregorian 날짜로 해석한다.
     *
     * SOLAR 입력일 때만 사용한다.
     */
    public LocalDate toGregorianDate() {

        try {

            return LocalDate.of(
                    year,
                    month,
                    day
            );

        } catch (DateTimeException exception) {

            throw new IllegalArgumentException(
                    "올바른 양력 생년월일이 아닙니다: "
                            + isoFormat(),
                    exception
            );
        }
    }

    public String isoFormat() {

        return String.format(
                "%04d-%02d-%02d",
                year,
                month,
                day
        );
    }
}