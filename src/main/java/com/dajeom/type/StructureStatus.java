package com.dajeom.type;

/**
 * 정밀화 5차-10.
 *
 * 최종 격국 구조 상태.
 *
 * 이 상태는 격의 "좋고 나쁨"을 의미하지 않는다.
 *
 * ESTABLISHED
 * 격의 성립 근거가 비교적 안정적이고,
 * 심각한 구조 손상이 없는 상태.
 *
 * ESTABLISHED_WITH_STRAIN
 * 격 자체는 성립하지만
 * 일간 신강·신약과 격 세력 사이에
 * 부담 관계가 존재하는 상태.
 *
 * COMPROMISED
 * 격은 실제로 성립하지만
 * 파격 또는 높은 혼잡도 등의
 * 구조 손상이 함께 존재하는 상태.
 *
 * UNSTABLE
 * 주격 후보는 존재하지만
 * 성립도나 구조 안정성이 낮은 상태.
 *
 * SPECIAL_REVIEW
 * 건록·양인·종격 등의
 * 특수격 후보로 분류되어
 * 일반 내격과 다른 검토가 필요한 상태.
 */
public enum StructureStatus {

    ESTABLISHED(
            "성립",
            "격 구조가 비교적 안정적으로 성립합니다."
    ),

    ESTABLISHED_WITH_STRAIN(
            "성립·강약 부담",
            "격은 성립하지만 일간 강약과 격 세력 사이에 부담이 있습니다."
    ),

    COMPROMISED(
            "성립·구조 손상",
            "격은 성립하지만 파격 또는 높은 혼잡도가 함께 존재합니다."
    ),

    UNSTABLE(
            "불안정",
            "주격 후보는 있으나 현재 원국에서 안정적인 성립으로 보기 어렵습니다."
    ),

    SPECIAL_REVIEW(
            "특수격 검토",
            "일반 내격이 아닌 특수격 후보로 별도 검토가 필요합니다."
    );

    private final String koreanName;

    private final String description;

    StructureStatus(
            String koreanName,
            String description
    ) {

        this.koreanName =
                koreanName;

        this.description =
                description;
    }

    public String koreanName() {

        return koreanName;
    }

    public String description() {

        return description;
    }

    public boolean established() {

        return this == ESTABLISHED;
    }

    public boolean establishedWithStrain() {

        return this == ESTABLISHED_WITH_STRAIN;
    }

    public boolean compromised() {

        return this == COMPROMISED;
    }

    public boolean unstable() {

        return this == UNSTABLE;
    }

    public boolean specialReview() {

        return this == SPECIAL_REVIEW;
    }

    /**
     * 일반 내격으로 실제 성립한 계열.
     */
    public boolean establishedFamily() {

        return this == ESTABLISHED
                || this == ESTABLISHED_WITH_STRAIN
                || this == COMPROMISED;
    }

    /**
     * 사용자 해석에서
     * 구조적인 주의 설명이 필요한 상태.
     */
    public boolean requiresCaution() {

        return this == ESTABLISHED_WITH_STRAIN
                || this == COMPROMISED
                || this == UNSTABLE
                || this == SPECIAL_REVIEW;
    }
}