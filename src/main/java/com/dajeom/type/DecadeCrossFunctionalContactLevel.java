package com.dajeom.type;

/**
 * 대운이 기존 원국 CrossFunctional Pattern의
 * Participant에 어느 정도 접촉했는지를 나타낸다.
 *
 * =================================================
 *
 * NO_CONTACT
 *
 * 해당 원국 Pattern의 Function Participant가
 * 이번 대운에서 실제 활성되지 않음.
 *
 * =================================================
 *
 * PRIMARY_ONLY
 *
 * BETWEEN_FUNCTIONS Pattern에서
 * Primary Function만 활성.
 *
 * Pattern 전체 활성 확정이 아니라
 * 한쪽 Participant 접촉이다.
 *
 * =================================================
 *
 * SECONDARY_ONLY
 *
 * BETWEEN_FUNCTIONS Pattern에서
 * Secondary Function만 활성.
 *
 * =================================================
 *
 * BOTH_FUNCTIONS
 *
 * BETWEEN_FUNCTIONS Pattern의
 * Primary / Secondary Function이
 * 같은 대운 Period에서 모두 활성.
 *
 * =================================================
 *
 * FUNCTION_TO_CORE_SELF
 *
 * FUNCTION_TO_CORE_SELF Pattern에서
 * Function이 실제 대운 활성되고,
 * 기존 원국 CoreSelf Anchor와 접촉.
 *
 * =================================================
 *
 * WITHIN_FUNCTION
 *
 * WITHIN_FUNCTION Pattern의
 * 해당 Function이 대운에서 실제 활성.
 *
 * =================================================
 *
 * 중요:
 *
 * Contact Level은:
 *
 * - 강도 점수
 * - 길흉
 * - Pattern 승격
 *
 * 이 아니다.
 *
 * 원국에 이미 존재하는 Pattern에
 * 대운이 어떤 범위로 접촉했는지만 표현한다.
 */
public enum DecadeCrossFunctionalContactLevel {

    NO_CONTACT(
            false,
            false
    ),

    PRIMARY_ONLY(
            true,
            false
    ),

    SECONDARY_ONLY(
            true,
            false
    ),

    BOTH_FUNCTIONS(
            true,
            true
    ),

    FUNCTION_TO_CORE_SELF(
            true,
            true
    ),

    WITHIN_FUNCTION(
            true,
            true
    );

    private final boolean contact;

    private final boolean fullPatternContact;

    DecadeCrossFunctionalContactLevel(

            boolean contact,

            boolean fullPatternContact

    ) {

        this.contact =
                contact;

        this.fullPatternContact =
                fullPatternContact;
    }

    public boolean contact() {

        return contact;
    }

    /**
     * 원국 Pattern이 요구하는
     * temporal participant 범위를
     * 이번 대운에서 모두 접촉했는가.
     *
     * 이것은 Pattern 강도 상승을 의미하지 않는다.
     */
    public boolean fullPatternContact() {

        return fullPatternContact;
    }

    public boolean partialContact() {

        return contact
                && !fullPatternContact;
    }

    public boolean primaryOnly() {

        return this == PRIMARY_ONLY;
    }

    public boolean secondaryOnly() {

        return this == SECONDARY_ONLY;
    }

    public boolean bothFunctions() {

        return this == BOTH_FUNCTIONS;
    }

    public boolean functionToCoreSelf() {

        return this == FUNCTION_TO_CORE_SELF;
    }

    public boolean withinFunction() {

        return this == WITHIN_FUNCTION;
    }
}