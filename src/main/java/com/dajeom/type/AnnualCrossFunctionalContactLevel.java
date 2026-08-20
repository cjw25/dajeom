package com.dajeom.type;

/**
 * 하나의 Annual-Decade Segment에서
 * 원국 CrossFunctional Pattern의
 * Function Participant가 어느 범위까지
 * 시간축에 접촉했는지 나타낸다.
 *
 * =================================================
 *
 * 대운 + 세운 + 14-5 Transformation Target까지
 * 모두 반영된 최종 Segment Contact다.
 *
 * =================================================
 *
 * 중요:
 *
 * BOTH_FUNCTIONS라고 해서
 * 원국 REFERENCE Pattern을
 * MEANINGFUL / STRONG으로 승격하지 않는다.
 */
public enum AnnualCrossFunctionalContactLevel {

    NO_CONTACT(
            false,
            false,
            false
    ),

    PRIMARY_ONLY(
            true,
            false,
            true
    ),

    SECONDARY_ONLY(
            true,
            false,
            true
    ),

    BOTH_FUNCTIONS(
            true,
            true,
            false
    ),

    FUNCTION_TO_CORE_SELF(
            true,
            true,
            false
    ),

    WITHIN_FUNCTION(
            true,
            true,
            false
    );

    private final boolean contact;

    private final boolean fullPatternContact;

    private final boolean partialContact;

    AnnualCrossFunctionalContactLevel(

            boolean contact,

            boolean fullPatternContact,

            boolean partialContact

    ) {

        this.contact =
                contact;

        this.fullPatternContact =
                fullPatternContact;

        this.partialContact =
                partialContact;
    }

    public boolean contact() {

        return contact;
    }

    public boolean fullPatternContact() {

        return fullPatternContact;
    }

    public boolean partialContact() {

        return partialContact;
    }
}