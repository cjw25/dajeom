package com.dajeom.type;

/**
 * 이미 확정된 deterministic Judgment 중
 * 사용자에게 어떤 순서와 깊이로 설명할지 정하는
 * Narrative 우선순위.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 이 값은 명리 판단의 좋고 나쁨이나
 * 길흉 점수가 아니다.
 *
 * 새로운 의미를 만들지도 않는다.
 *
 * Java에서 이미 확정된 Meaning 중
 * 무엇을 더 먼저, 더 자세하게 설명할지를
 * 정하기 위한 서술 우선순위다.
 *
 * =================================================
 *
 * CORE
 *
 * 해당 상담 Category의 중심 판단.
 *
 * =================================================
 *
 * DISTINCTIVE
 *
 * 상대적으로 구별력이 높은 판단.
 *
 * =================================================
 *
 * SUPPORTING
 *
 * 중심 판단을 독립적으로 보조하는 판단.
 *
 * SUPPORTING이라고 해서
 * CORE의 원인이나 결과라는 뜻은 아니다.
 *
 * =================================================
 *
 * CONTEXTUAL
 *
 * 현재 사용자 Context에 따라
 * 현실 표현에 참고할 수 있는 판단.
 *
 * 사주 계산 자체를 변경하지 않는다.
 */
public enum ConsultationNarrativePriority {

    CORE(1),

    DISTINCTIVE(2),

    SUPPORTING(3),

    CONTEXTUAL(4);

    private final int order;

    ConsultationNarrativePriority(
            int order
    ) {

        this.order =
                order;
    }

    public int getOrder() {

        return order;
    }
}