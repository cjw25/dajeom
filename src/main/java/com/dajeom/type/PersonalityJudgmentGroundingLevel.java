package com.dajeom.type;

/**
 * 기본성향 판정의 근거 강도.
 *
 * =================================================
 *
 * 이것은:
 *
 * - 확률
 * - 적중률
 * - 점수
 *
 * 가 아니다.
 *
 * =================================================
 *
 * REFERENCE
 *
 * 구조적으로 참고할 근거는 있으나
 * 직접적인 성향 단정에는 사용하지 않는다.
 *
 * =================================================
 *
 * CONTEXTUAL
 *
 * 실제 기능 또는 Pattern이 존재하지만
 * 독립적인 보강 근거가 충분하지 않거나
 * 조건 설명이 필요한 단계.
 *
 * =================================================
 *
 * GROUNDED
 *
 * 서로 다른 구조적 Evidence가
 * 같은 방향을 지지하거나,
 *
 * 기존 10차 교차검증에서
 * 직접 사용할 수 있는 수준으로
 * 확인된 단계.
 *
 * =================================================
 *
 * STRONGLY_GROUNDED
 *
 * 강한 원국 Pattern
 *
 * +
 *
 * 교차검증
 *
 * +
 *
 * 중심성 또는 추가 독립 Evidence
 *
 * 가 함께 확인된 단계.
 *
 * =================================================
 *
 * 주의:
 *
 * STRONGLY_GROUNDED
 * =
 * 좋은 성격
 *
 * 이 아니다.
 *
 * 해당 판단을 명리구조상
 * 강하게 말할 수 있다는 뜻이다.
 */
public enum PersonalityJudgmentGroundingLevel {

    REFERENCE(
            "참고"
    ),

    CONTEXTUAL(
            "맥락"
    ),

    GROUNDED(
            "근거확정"
    ),

    STRONGLY_GROUNDED(
            "강한 근거확정"
    );

    private final String koreanName;

    PersonalityJudgmentGroundingLevel(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    /**
     * 사용자에게 비교적 직접적인
     * 성향 설명을 할 수 있는 단계.
     */
    public boolean directAssertionAllowed() {

        return this == GROUNDED
                || this == STRONGLY_GROUNDED;
    }

    public boolean stronglyGrounded() {

        return this == STRONGLY_GROUNDED;
    }
}