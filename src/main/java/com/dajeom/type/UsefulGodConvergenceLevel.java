package com.dajeom.type;

/**
 * 정밀화 7차-8.
 *
 * 하나의 五行에 대해
 * 여러 용신법이 어느 수준으로 수렴했는지를 나타낸다.
 *
 * 숫자 점수를 합산하지 않는다.
 *
 * 방법의 적용도와 후보 역할을 이용해
 * 구조적 단계로만 분류한다.
 */
public enum UsefulGodConvergenceLevel {

    /**
     * 실제 종격 모드이며
     * 종격용신법 CORE가 해당 오행을 PRIMARY로 선택.
     */
    FOLLOW_CORE_PRIMARY(
            "종격 핵심 방향",
            8
    ),

    /**
     * 실제 종격 모드이며
     * 종격용신법 CORE가 해당 오행을 SECONDARY로 선택.
     */
    FOLLOW_CORE_SUPPORT(
            "종격 보조 방향",
            7
    ),

    /**
     * 둘 이상의 CORE 용신법이
     * 동일 오행을 PRIMARY로 지목.
     */
    MULTI_CORE_PRIMARY(
            "복수 핵심 용신법 수렴",
            6
    ),

    /**
     * CORE PRIMARY 하나가 존재하며
     * 다른 유효 용신법도 같은 오행을 지지.
     */
    CORE_PRIMARY_WITH_SUPPORT(
            "핵심 용신법 + 추가 수렴",
            5
    ),

    /**
     * 하나의 CORE 용신법이
     * 해당 오행을 PRIMARY로 지목.
     */
    SINGLE_CORE_PRIMARY(
            "단일 핵심 용신법",
            4
    ),

    /**
     * CORE PRIMARY는 아니지만
     * 둘 이상의 유효 용신법이 같은 오행을 선택.
     */
    MULTI_METHOD_SUPPORT(
            "복수 용신법 보조 수렴",
            3
    ),

    /**
     * 하나의 유효 용신법에서만
     * 선택된 오행.
     */
    SINGLE_METHOD_SUPPORT(
            "단일 용신법 보조",
            2
    ),

    /**
     * 적극 선택은 없고
     * 조건부 Evidence만 존재.
     */
    CONDITIONAL_ONLY(
            "조건부 근거만 존재",
            1
    ),

    /**
     * 현재 활성 용신 근거 없음.
     */
    INACTIVE(
            "활성 근거 없음",
            0
    );

    private final String koreanName;

    private final int rank;

    UsefulGodConvergenceLevel(
            String koreanName,
            int rank
    ) {

        this.koreanName =
                koreanName;

        this.rank =
                rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean followPrimary() {

        return this == FOLLOW_CORE_PRIMARY;
    }

    public boolean followSupport() {

        return this == FOLLOW_CORE_SUPPORT;
    }

    public boolean multiCorePrimary() {

        return this == MULTI_CORE_PRIMARY;
    }

    public boolean hasCorePrimary() {

        return this == MULTI_CORE_PRIMARY
                || this == CORE_PRIMARY_WITH_SUPPORT
                || this == SINGLE_CORE_PRIMARY;
    }

    public boolean multiMethod() {

        return this == MULTI_CORE_PRIMARY
                || this == CORE_PRIMARY_WITH_SUPPORT
                || this == MULTI_METHOD_SUPPORT;
    }

    public boolean conditionalOnly() {

        return this == CONDITIONAL_ONLY;
    }

    public boolean inactive() {

        return this == INACTIVE;
    }
}