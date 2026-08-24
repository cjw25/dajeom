package com.dajeom.type;

/**
 * 정밀화 19차.
 *
 * 사용자의 자유 상담 질문이
 * ConsultationCategory와 어떤 관계에 있는지 나타낸다.
 *
 * =================================================
 *
 * NOT_APPLICABLE
 *
 * 자유 질문 자체가 없음.
 *
 * =================================================
 *
 * EXPLICITLY_SCOPED
 *
 * 자유 질문이 존재하고,
 * 사용자가 ConsultationCategory를 직접 선택함.
 *
 * Java에서 질문 의미를 추측한 것이 아니다.
 *
 * =================================================
 *
 * BLIND_ANALYSIS_READY
 *
 * 자유 질문은 존재하지만
 * 사용자가 ConsultationCategory를 직접 지정하지 않음.
 *
 * 질문 내용으로 사주 계산이나
 * deterministic Judgment를 바꾸지 않는다.
 *
 * 이미 블라인드로 계산된 전체 ConsultationCategory를
 * 보존한 뒤 후속 Narrative 단계에서
 * 질문을 서술 우선순위에만 사용한다.
 *
 * =================================================
 *
 * GROUNDING_REQUIRED
 *
 * 자유 질문은 존재하지만
 * 아직 Category Routing 자체가 해결되지 않은
 * 호환/예외 상태.
 *
 * 현재 신규 흐름에서는
 * 자유 질문만 존재하는 정상 요청은
 * BLIND_ANALYSIS_READY를 사용한다.
 */
public enum ConsultationQuestionRoutingStatus {

    NOT_APPLICABLE(
            "질문 없음"
    ),

    EXPLICITLY_SCOPED(
            "사용자 지정 분야"
    ),

    BLIND_ANALYSIS_READY(
            "블라인드 전체분석 후 질문 우선순위 적용"
    ),

    GROUNDING_REQUIRED(
            "질문 분야 해석 필요"
    );

    private final String koreanName;

    ConsultationQuestionRoutingStatus(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String getKoreanName() {

        return koreanName;
    }

    public boolean notApplicable() {

        return this
                == NOT_APPLICABLE;
    }

    public boolean explicitlyScoped() {

        return this
                == EXPLICITLY_SCOPED;
    }

    public boolean blindAnalysisReady() {

        return this
                == BLIND_ANALYSIS_READY;
    }

    public boolean groundingRequired() {

        return this
                == GROUNDING_REQUIRED;
    }
}