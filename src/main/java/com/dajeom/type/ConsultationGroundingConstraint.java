package com.dajeom.type;

/**
 * 정밀화 20차.
 *
 * LLM Interpretation이 반드시 지켜야 하는
 * Grounding 제약조건.
 *
 * =================================================
 *
 * 이 enum은 명리 계산 Rule이 아니다.
 *
 * Java deterministic 분석 결과를
 * 자연어로 설명할 때 허용되는 범위를
 * 명시적으로 전달하기 위한 계약이다.
 */
public enum ConsultationGroundingConstraint {

    NO_NEW_SAJU_CALCULATION(
            "전달된 deterministic 근거 밖에서 새로운 사주 계산을 하지 않는다."
    ),

    NO_DIRECT_EVENT_ASSERTION(
            "재물 획득, 취업, 결혼, 이별, 질병 발생 등 구체적 사건의 발생을 확정하지 않는다."
    ),

    PRESERVE_MIXED_EVIDENCE(
            "서로 다른 방향의 근거가 함께 있으면 한쪽을 제거하지 않고 함께 설명한다."
    ),

    PRESERVE_TIMELINE_UNCERTAINTY(
            "복수의 가능한 Timeline Scenario가 있으면 하나를 임의로 확정하지 않는다."
    ),

    AUXILIARY_NOT_STANDALONE(
            "신살 등 보조 근거만으로 독립적인 생활영역 결론을 만들지 않는다."
    ),

    NO_MEDICAL_DIAGNOSIS(
            "명리 구조를 질병 진단이나 의학적 판단으로 변환하지 않는다."
    ),

    NO_ORGAN_DISEASE_MAPPING(
            "오행이나 명리 용어를 특정 장기·질병에 직접 대응시키지 않는다."
    ),

    NO_LEGAL_OUTCOME_PREDICTION(
            "소송, 처벌, 승패, 법적 책임 발생 여부나 결과를 예측하지 않는다."
    ),

    REQUIRE_CROSS_EVIDENCE_FOR_LEGAL_RISK(
            "분쟁·주의 해석은 단일 충·형·관살·PRESSURE 하나가 아니라 복수의 관련 근거가 있을 때만 다룬다."
    ),

    NO_MECHANICAL_RELATIONSHIP_EVENT_MAPPING(
            "합·충·궁위·재성·관성을 결혼, 이별, 배우자 등장 같은 사건으로 기계적으로 변환하지 않는다."
    );

    private final String instruction;

    ConsultationGroundingConstraint(
            String instruction
    ) {

        this.instruction =
                instruction;
    }

    public String getInstruction() {

        return instruction;
    }
}