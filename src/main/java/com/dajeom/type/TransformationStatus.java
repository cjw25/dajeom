package com.dajeom.type;

/**
 * 합화 판정 상태.
 *
 * ESTABLISHED:
 * 현재 V1 기준에서 합화 조건이 충분함
 *
 * NOT_ESTABLISHED:
 * 합 관계는 있으나 합화 조건이 부족함
 *
 * UNDETERMINED:
 * 합화 가능성은 있으나
 * 추가 조건 검토가 필요함
 */
public enum TransformationStatus {

    ESTABLISHED(
            "합화 성립"
    ),

    NOT_ESTABLISHED(
            "합화 불성립"
    ),

    UNDETERMINED(
            "추가 판정 필요"
    );

    private final String koreanName;

    TransformationStatus(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}