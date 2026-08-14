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
 * GROUNDING_REQUIRED
 *
 * 자유 질문은 존재하지만
 * 사용자가 ConsultationCategory를 지정하지 않음.
 *
 * 이 상태에서는 19차 Java가
 * 문자열 키워드로 Category를 강제 결정하지 않는다.
 *
 * 이후 Grounding 계층에서
 * 질문 의미를 다룰 수 있도록 원문을 보존한다.
 */
public enum ConsultationQuestionRoutingStatus {

    NOT_APPLICABLE(
            "질문 없음"
    ),

    EXPLICITLY_SCOPED(
            "사용자 지정 분야"
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

    public boolean groundingRequired() {

        return this
                == GROUNDING_REQUIRED;
    }
}