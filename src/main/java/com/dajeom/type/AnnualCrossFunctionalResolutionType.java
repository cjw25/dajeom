package com.dajeom.type;

/**
 * 하나의 Annual-Decade Segment에서
 * CrossFunctional Pattern Contact가
 * 어떤 시간 구조로 형성되었는지 나타낸다.
 *
 * =================================================
 *
 * NO_CONTACT
 *
 * Pattern Participant Function이
 * 하나도 활성되지 않음.
 *
 * =================================================
 *
 * PARTIAL_CONTACT
 *
 * BETWEEN_FUNCTIONS Pattern에서
 * 한쪽 Participant만 활성.
 *
 * =================================================
 *
 * DECADE_FULL_CONTINUATION
 *
 * 대운에서 이미 Full Contact였고
 * 세운에서도 그 구조가 계속 존재.
 *
 * 이것은 새로운 Pattern 완성이 아니다.
 *
 * =================================================
 *
 * ANNUAL_COMPLETION
 *
 * 대운에서는 양쪽 Participant가 없었고,
 * 세운 자체 Functional Layer에서
 * 양쪽 Function이 함께 활성.
 *
 * =================================================
 *
 * CROSS_LAYER_COMPLETION
 *
 * 대운에서 한쪽 Participant가 활성되고
 * 세운 자체가 빠진 반대쪽 Participant를 채움.
 *
 * =================================================
 *
 * TRANSFORMATION_COMPLETION
 *
 * 14-5 대운 × 세운 Transformation Target이
 * 없으면 Full Contact가 되지 않지만,
 * 그 Target이 들어오면서 Full Contact가 됨.
 *
 * =================================================
 *
 * FULL_CONTACT
 *
 * 위 세부 유형으로 단정할 수 없는
 * Full Contact.
 *
 * =================================================
 *
 * FUNCTION_TO_CORE_SELF_CONTACT
 *
 * Natal CoreSelf는 고정 Anchor이며
 * 해당 Function이 시간축에서 활성.
 *
 * =================================================
 *
 * WITHIN_FUNCTION_CONTACT
 *
 * 동일 Function 내부 Pattern에
 * 해당 Function이 시간축에서 활성.
 *
 * 독립 Function 두 개가 활성된 것으로
 * 세지 않는다.
 */
public enum AnnualCrossFunctionalResolutionType {

    NO_CONTACT,

    PARTIAL_CONTACT,

    DECADE_FULL_CONTINUATION,

    ANNUAL_COMPLETION,

    CROSS_LAYER_COMPLETION,

    TRANSFORMATION_COMPLETION,

    FULL_CONTACT,

    FUNCTION_TO_CORE_SELF_CONTACT,

    WITHIN_FUNCTION_CONTACT;

    public boolean contact() {

        return this != NO_CONTACT;
    }

    public boolean newlyCompleted() {

        return this == ANNUAL_COMPLETION
                || this == CROSS_LAYER_COMPLETION
                || this == TRANSFORMATION_COMPLETION;
    }

    public boolean continuation() {

        return this == DECADE_FULL_CONTINUATION;
    }

    public boolean transformationDependentCompletion() {

        return this == TRANSFORMATION_COMPLETION;
    }

    public boolean partial() {

        return this == PARTIAL_CONTACT;
    }
}