package com.dajeom.type;

/**
 * 정밀화 16차-6.
 *
 * 운 분석에서 발생하는
 * 구체적인 불확실성 원인.
 *
 * =================================================
 *
 * 하나의 분석에서 여러 Cause가
 * 동시에 존재할 수 있다.
 *
 * 예:
 *
 * REFERENCE_TIME_UNSPECIFIED
 * +
 * NATAL_TIME_UNKNOWN
 * +
 * DECADE_BOUNDARY_UNCERTAIN
 *
 * =================================================
 *
 * Cause 자체에 길흉 의미는 없다.
 */
public enum LuckUncertaintyCause {

    REFERENCE_TIME_UNSPECIFIED(
            LuckUncertaintyKind.INPUT_PRECISION,
            "조회시간 미입력"
    ),

    NATAL_TIME_UNKNOWN(
            LuckUncertaintyKind.NATAL_DATA_LIMITATION,
            "출생시간 미상"
    ),

    DECADE_BOUNDARY_UNCERTAIN(
            LuckUncertaintyKind.BOUNDARY_AMBIGUITY,
            "대운 교운시점 불확실"
    ),

    ANNUAL_BOUNDARY_WITHOUT_REFERENCE_TIME(
            LuckUncertaintyKind.BOUNDARY_AMBIGUITY,
            "입춘 경계 조회시간 미상"
    ),

    MONTHLY_BOUNDARY_WITHOUT_REFERENCE_TIME(
            LuckUncertaintyKind.BOUNDARY_AMBIGUITY,
            "월 절입 경계 조회시간 미상"
    ),

    DECADE_ANALYSIS_UNAVAILABLE(
            LuckUncertaintyKind.ANALYSIS_UNAVAILABLE,
            "대운 분석 사용 불가"
    ),

    DECADE_GENERATED_RANGE_EXHAUSTED(
            LuckUncertaintyKind.DATA_RANGE_LIMITATION,
            "생성 대운 범위 초과"
    );

    private final LuckUncertaintyKind kind;

    private final String koreanName;

    LuckUncertaintyCause(

            LuckUncertaintyKind kind,

            String koreanName

    ) {

        this.kind =
                kind;

        this.koreanName =
                koreanName;
    }

    public LuckUncertaintyKind kind() {

        return kind;
    }

    public String koreanName() {

        return koreanName;
    }
}