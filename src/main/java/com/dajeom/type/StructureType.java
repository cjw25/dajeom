package com.dajeom.type;

/**
 * 다점 명리 엔진에서 판정하는 주요 원국 구조다.
 */
public enum StructureType {

    WEALTH_HEAVY_WEAK_SELF(
            "재다신약",
            "재성은 강하지만 일간이 약한 구조"
    ),

    OFFICER_KILLING_MIXED(
            "관살혼잡",
            "정관과 편관이 함께 강하게 작용하는 구조"
    ),

    HURTING_OFFICER_SEES_OFFICER(
            "상관견관",
            "상관과 정관이 함께 드러나 충돌하는 구조"
    ),

    EATING_GOD_CONTROLS_KILLING(
            "식신제살",
            "식신이 편관의 강한 압박을 조절하는 구조"
    ),

    KILLING_RESOURCE_GENERATION(
            "살인상생",
            "편관의 기운이 인성을 거쳐 일간을 돕는 구조"
    ),

    OUTPUT_GENERATES_WEALTH(
            "식상생재",
            "식상에서 재성으로 기운이 이어지는 구조"
    ),

    WEALTH_GENERATES_OFFICER(
            "재생관",
            "재성이 관성을 생하는 구조"
    ),

    OFFICER_GENERATES_RESOURCE(
            "관인상생",
            "관성이 인성을 생하고 인성이 일간을 돕는 구조"
    ),

    RESOURCE_PEER_GENERATION(
            "인비상생",
            "인성이 비겁과 일간을 돕는 구조"
    ),

    PEERS_COMPETE_FOR_WEALTH(
            "군겁쟁재",
            "비겁이 많고 재성을 놓고 경쟁하는 구조"
    ),

    WEALTH_DOMINANT(
            "재성다자",
            "재성의 비중이 강한 구조"
    ),

    OFFICER_DOMINANT(
            "관성다자",
            "관성의 비중이 강한 구조"
    ),

    RESOURCE_DOMINANT(
            "인성다자",
            "인성의 비중이 강한 구조"
    ),

    OUTPUT_DOMINANT(
            "식상다자",
            "식상의 비중이 강한 구조"
    ),

    PEER_DOMINANT(
            "비겁다자",
            "비겁의 비중이 강한 구조"
    ),

    NO_WEALTH(
            "무재",
            "원국에서 재성이 확인되지 않는 구조"
    ),

    NO_OFFICER(
            "무관",
            "원국에서 관성이 확인되지 않는 구조"
    ),

    NO_RESOURCE(
            "무인",
            "원국에서 인성이 확인되지 않는 구조"
    ),

    NO_OUTPUT(
            "무식상",
            "원국에서 식상이 확인되지 않는 구조"
    );

    private final String koreanName;
    private final String description;

    StructureType(
            String koreanName,
            String description
    ) {
        this.koreanName = koreanName;
        this.description = description;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getDescription() {
        return description;
    }
}