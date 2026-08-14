package com.dajeom.type;

/**
 * 정밀화 16차-6.
 *
 * 불확실성 Evidence가 영향을 미치는
 * 분석 계층.
 *
 * =================================================
 *
 * REFERENCE
 *
 * 사용자 조회 기준 자체.
 *
 * NATAL
 *
 * 원국.
 *
 * DECADE
 *
 * 대운.
 *
 * ANNUAL
 *
 * 세운.
 *
 * MONTHLY
 *
 * 월운.
 *
 * TIMELINE
 *
 * 16-5 통합 시간축.
 */
public enum LuckUncertaintyScope {

    REFERENCE(
            "조회 기준"
    ),

    NATAL(
            "원국"
    ),

    DECADE(
            "대운"
    ),

    ANNUAL(
            "세운"
    ),

    MONTHLY(
            "월운"
    ),

    TIMELINE(
            "통합 운 시간축"
    );

    private final String koreanName;

    LuckUncertaintyScope(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }
}