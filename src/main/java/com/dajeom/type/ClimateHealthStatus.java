package com.dajeom.type;

/**
 * 정밀화 6차-10.
 *
 * 조후·병약·통관 최종 통합 상태.
 *
 * 이 상태는 아직:
 *
 * - 용신
 * - 희신
 * - 기신
 *
 * 을 확정한 결과가 아니다.
 *
 * 7차 용신 분석으로 넘기기 위한
 * 6차 최종 후보 상태다.
 */
public enum ClimateHealthStatus {

    /**
     * 6-9 교차검증 이후
     * PRIMARY 후보가 정확히 하나 존재.
     */
    SINGLE_PRIMARY(
            "단일 핵심 후보"
    ),

    /**
     * 6-9 교차검증 이후
     * PRIMARY 후보가 두 개 이상 존재.
     *
     * 7차에서 우선순위를 추가 비교해야 한다.
     */
    MULTIPLE_PRIMARY(
            "복수 핵심 후보"
    ),

    /**
     * PRIMARY는 없지만
     * SECONDARY 후보가 하나 이상 존재.
     */
    SECONDARY_ONLY(
            "보조 후보 중심"
    ),

    /**
     * PRIMARY/SECONDARY가 없고
     * DEFERRED 후보만 존재.
     *
     * 조후·병약·통관 또는
     * 신강신약·격국 사이에
     * 충돌이 남아 있는 상태일 수 있다.
     */
    DEFERRED_ONLY(
            "판정 보류"
    ),

    /**
     * 6차 관점에서
     * 활성 후보가 존재하지 않음.
     *
     * 이것이 오행 전체가 필요 없다는 뜻은 아니다.
     * 단지 6차 분석에서 적극적인 후보 근거가
     * 만들어지지 않았다는 뜻이다.
     */
    INACTIVE(
            "활성 후보 없음"
    );

    private final String koreanName;

    ClimateHealthStatus(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean singlePrimary() {

        return this == SINGLE_PRIMARY;
    }

    public boolean multiplePrimary() {

        return this == MULTIPLE_PRIMARY;
    }

    public boolean secondaryOnly() {

        return this == SECONDARY_ONLY;
    }

    public boolean deferredOnly() {

        return this == DEFERRED_ONLY;
    }

    public boolean inactive() {

        return this == INACTIVE;
    }

    public boolean hasPrimary() {

        return this == SINGLE_PRIMARY
                || this == MULTIPLE_PRIMARY;
    }

    /**
     * 7차에서 후보 간 추가 선택이 필요한가.
     */
    public boolean requiresFurtherSelection() {

        return this == MULTIPLE_PRIMARY
                || this == SECONDARY_ONLY
                || this == DEFERRED_ONLY;
    }

    public boolean active() {

        return this != INACTIVE;
    }
}