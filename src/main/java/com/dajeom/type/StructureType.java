package com.dajeom.type;

/**
 * 원국의 기본 격국 후보.
 *
 * V1에서는 월령의 정기(본기)를 기준으로
 * 일반적인 8격 구조를 우선 분류한다.
 *
 * 비견/겁재 계열 등
 * 추가적인 구조 판정이 필요한 경우에는
 * UNDETERMINED를 사용한다.
 */
public enum StructureType {

    DIRECT_OFFICER(
            "정관격"
    ),

    SEVEN_KILLINGS(
            "편관격"
    ),

    DIRECT_WEALTH(
            "정재격"
    ),

    INDIRECT_WEALTH(
            "편재격"
    ),

    EATING_GOD(
            "식신격"
    ),

    HURTING_OFFICER(
            "상관격"
    ),

    DIRECT_RESOURCE(
            "정인격"
    ),

    INDIRECT_RESOURCE(
            "편인격"
    ),

    UNDETERMINED(
            "추가 판정 필요"
    );

    private final String koreanName;

    StructureType(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}