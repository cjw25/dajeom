package com.dajeom.type;

/**
 * 오행 통관 상태.
 *
 * ACTIVE:
 * 통관 오행이 충분한 세력을 가지고 있음
 *
 * POSSIBLE:
 * 통관 오행은 존재하지만 세력이 약함
 *
 * ABSENT:
 * 충돌은 존재하지만 통관 오행이 부족함
 */
public enum MediationStatus {

    ACTIVE(
            "통관 작용"
    ),

    POSSIBLE(
            "통관 가능"
    ),

    ABSENT(
            "통관 부족"
    );

    private final String koreanName;

    MediationStatus(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}