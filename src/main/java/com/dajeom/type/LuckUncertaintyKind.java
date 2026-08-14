package com.dajeom.type;

/**
 * 정밀화 16차-6.
 *
 * 운 분석 불확실성의 상위 분류.
 *
 * =================================================
 *
 * INPUT_PRECISION
 *
 * 사용자가 날짜만 제공하는 등
 * 조회 입력 정밀도가 제한된 경우.
 *
 * =================================================
 *
 * NATAL_DATA_LIMITATION
 *
 * 출생시간 미상 등
 * 원국 입력 자체에 제한이 있는 경우.
 *
 * =================================================
 *
 * BOUNDARY_AMBIGUITY
 *
 * 대운 교운, 입춘, 절입 등의
 * 경계 때문에 하나의 운으로
 * 단정할 수 없는 경우.
 *
 * =================================================
 *
 * ANALYSIS_UNAVAILABLE
 *
 * 필요한 하위 분석 자체가
 * 사용 불가능한 경우.
 *
 * =================================================
 *
 * DATA_RANGE_LIMITATION
 *
 * 분석은 가능하지만
 * 현재 생성한 데이터 범위를 벗어난 경우.
 *
 * 예:
 *
 * 대운을 10개까지만 생성했는데
 * 그 이후 시점을 조회함.
 */
public enum LuckUncertaintyKind {

    INPUT_PRECISION(
            "입력 정밀도 제한"
    ),

    NATAL_DATA_LIMITATION(
            "출생정보 제한"
    ),

    BOUNDARY_AMBIGUITY(
            "운 경계 불확실성"
    ),

    ANALYSIS_UNAVAILABLE(
            "분석 사용 불가"
    ),

    DATA_RANGE_LIMITATION(
            "생성 데이터 범위 제한"
    );

    private final String koreanName;

    LuckUncertaintyKind(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }
}