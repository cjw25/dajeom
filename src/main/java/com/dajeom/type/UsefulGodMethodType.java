package com.dajeom.type;

/**
 * 정밀화 7차-1.
 *
 * 다점 엔진에서 구분하는 용신 선정 방법.
 *
 * 중요한 원칙:
 *
 * 여러 용신법은 서로 동일한 것이 아니다.
 *
 * 예:
 *
 * 억부용신
 * -> 일간 강약 조정
 *
 * 조후용신
 * -> 한난조습 조정
 *
 * 격국용신
 * -> 주격 성립·유지·보완
 *
 * 병약용신
 * -> 원국의 핵심 병목 완화
 *
 * 통관용신
 * -> 강한 상극 관계 사이의 흐름 연결
 *
 * 종격용신
 * -> 종하는 세력의 방향 유지
 *
 * 7차에서는 각 방법의 후보를 독립적으로 만든 뒤
 * 후반 단계에서 수렴·충돌을 조정한다.
 */
public enum UsefulGodMethodType {

    BALANCING(
            "억부용신",
            "일간의 신강·신약을 조정하는 방법"
    ),

    CLIMATE_CONTROL(
            "조후용신",
            "원국의 한난조습 환경을 조정하는 방법"
    ),

    STRUCTURE(
            "격국용신",
            "주격을 성립·유지하거나 파격을 보완하는 방법"
    ),

    REMEDY(
            "병약용신",
            "원국의 핵심 병 구조를 완화하는 방법"
    ),

    PASSAGE(
            "통관용신",
            "강한 상극 관계 사이에 생조 흐름을 연결하는 방법"
    ),

    FOLLOW_STRUCTURE(
            "종격용신",
            "종격 후보에서 종하는 세력의 흐름을 유지하는 방법"
    );

    private final String koreanName;

    private final String description;

    UsefulGodMethodType(
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

    public boolean balancingMethod() {

        return this == BALANCING;
    }

    public boolean climateMethod() {

        return this == CLIMATE_CONTROL;
    }

    public boolean structureMethod() {

        return this == STRUCTURE;
    }

    public boolean remedyMethod() {

        return this == REMEDY;
    }

    public boolean passageMethod() {

        return this == PASSAGE;
    }

    public boolean followStructureMethod() {

        return this == FOLLOW_STRUCTURE;
    }
}