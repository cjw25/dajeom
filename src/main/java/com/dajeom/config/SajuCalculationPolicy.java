package com.dajeom.config;

import java.time.ZoneId;

/**
 * 다점 명리 계산의 공통 정책.
 *
 * 계산기마다 시간대, 지원 연도 등의 기준을
 * 따로 하드코딩하지 않고 이 클래스에서 통일한다.
 */
public final class SajuCalculationPolicy {

    /**
     * 다점 기본 시간대.
     *
     * 현재 정책:
     * 대한민국 민간시 기준.
     */
    public static final ZoneId DEFAULT_ZONE_ID =
            ZoneId.of(
                    "Asia/Seoul"
            );

    /**
     * 실제 서비스에서 허용하는 최소 출생 연도.
     *
     * 현대 한국 사주 서비스 범위와
     * 음양력 변환 안정성을 고려한 기준.
     */
    public static final int MIN_SUPPORTED_YEAR =
            1900;

    /**
     * 실제 서비스에서 허용하는 최대 계산 연도.
     *
     * 현재 사용하는 한국 음력 변환기의
     * 안정적인 지원 범위와 맞춘다.
     */
    public static final int MAX_SUPPORTED_YEAR =
            2050;

    /**
     * 외부에서 인스턴스를 만들 수 없도록 한다.
     */
    private SajuCalculationPolicy() {
    }

    /**
     * 지원 연도 여부.
     */
    public static boolean isSupportedYear(
            int year
    ) {

        return year >= MIN_SUPPORTED_YEAR
                && year <= MAX_SUPPORTED_YEAR;
    }

    /**
     * 지원 연도가 아니면 즉시 예외 처리한다.
     */
    public static void validateYear(
            int year
    ) {

        if (
                !isSupportedYear(
                        year
                )
        ) {

            throw new IllegalArgumentException(
                    "지원하는 연도는 "
                            + MIN_SUPPORTED_YEAR
                            + "년부터 "
                            + MAX_SUPPORTED_YEAR
                            + "년까지입니다: "
                            + year
            );
        }
    }
}