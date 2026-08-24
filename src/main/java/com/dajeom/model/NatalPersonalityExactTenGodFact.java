package com.dajeom.model;

import com.dajeom.type.PersonalityStatementSelectionRole;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodPowerLevel;

import java.util.Objects;

/**
 * 정확한 10십성 하나를
 * Personality Statement 단계에서 보존하는 Fact.
 *
 * =================================================
 *
 * 기존:
 *
 * 비견 + 겁재
 * -> SELF_ASSERTION
 *
 * 식신 + 상관
 * -> EXPRESSION
 *
 * 정재 + 편재
 * -> RESOURCE_REALITY
 *
 * 정관 + 편관
 * -> RESPONSIBILITY
 *
 * 정인 + 편인
 * -> PROCESSING
 *
 * 처럼 먼저 하나의 기능축으로 합치는 것을
 * 사용자 Personality Meaning의 공식 기준으로
 * 사용하지 않는다.
 *
 * =================================================
 *
 * 이 Fact는:
 *
 * - 비견
 * - 겁재
 * - 식신
 * - 상관
 * - 편재
 * - 정재
 * - 편관
 * - 정관
 * - 편인
 * - 정인
 *
 * 중 정확히 하나만 보존한다.
 *
 * =================================================
 *
 * sourceProfile
 *
 * 기존 10-1 십성 세력과
 * 5차 주격 중심성을 보존한
 * 공식 Exact TenGod Profile.
 *
 * =================================================
 *
 * role
 *
 * PRIMARY
 * SUPPORTING
 * INTERNAL_REFERENCE
 *
 * 중 하나.
 *
 * 새로운 점수나 심리판정이 아니다.
 */
public record NatalPersonalityExactTenGodFact(

        NatalExactTenGodProfile sourceProfile,

        PersonalityStatementSelectionRole role,

        boolean completeChart

) {

    public NatalPersonalityExactTenGodFact {

        Objects.requireNonNull(
                sourceProfile,
                "Exact TenGod Statement Fact에는 Source Profile이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "Exact TenGod Statement Fact에는 Selection Role이 필요합니다."
        );

        if (
                sourceProfile.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Exact TenGod Profile과 Statement Fact의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 존재하지 않는 십성 금지
         * =================================================
         */

        if (
                !sourceProfile.present()
        ) {

            throw new IllegalArgumentException(
                    "원국에 존재하지 않는 정확한 십성을 "
                            + "Statement Fact로 만들 수 없습니다: "
                            + sourceProfile.tenGodType()
            );
        }

        /*
         * =================================================
         * Role과 기존 10-1 판정 정합성
         * =================================================
         */

        switch (role) {

            /*
             * 실제 주격 중심이거나
             * 기존 10-1 STRONG 이상.
             */
            case PRIMARY -> {

                if (
                        !sourceProfile.primaryStructure()
                                &&
                                !sourceProfile.strongSide()
                ) {

                    throw new IllegalArgumentException(
                            "PRIMARY Exact TenGod Fact는 "
                                    + "주격 중심 또는 기존 10-1 STRONG 이상이어야 합니다: "
                                    + sourceProfile.tenGodType()
                    );
                }
            }

            /*
             * 기존 10-1 MODERATE.
             */
            case SUPPORTING -> {

                if (
                        sourceProfile.primaryStructure()
                                ||
                                sourceProfile.powerLevel()
                                        != TenGodPowerLevel.MODERATE
                ) {

                    throw new IllegalArgumentException(
                            "SUPPORTING Exact TenGod Fact는 "
                                    + "기존 10-1 MODERATE 십성만 사용할 수 있습니다: "
                                    + sourceProfile.tenGodType()
                    );
                }
            }

            /*
             * 기존 10-1 WEAK.
             *
             * 존재는 보존하지만
             * 사용자 Personality의 중심 의미로
             * 직접 확대하지 않는다.
             */
            case INTERNAL_REFERENCE -> {

                if (
                        sourceProfile.primaryStructure()
                                ||
                                sourceProfile.powerLevel()
                                        != TenGodPowerLevel.WEAK
                ) {

                    throw new IllegalArgumentException(
                            "INTERNAL_REFERENCE Exact TenGod Fact는 "
                                    + "기존 10-1 WEAK 십성만 사용할 수 있습니다: "
                                    + sourceProfile.tenGodType()
                    );
                }
            }

            /*
             * 정확한 십성 자체는
             * CoreSelf 같은 Background Context가 아니다.
             */
            case BACKGROUND_CONTEXT ->
                    throw new IllegalArgumentException(
                            "정확한 10십성 Fact는 "
                                    + "BACKGROUND_CONTEXT Role을 사용하지 않습니다."
                    );
        }
    }

    public StructureTenGodType tenGodType() {

        return sourceProfile
                .tenGodType();
    }

    public String koreanName() {

        return sourceProfile
                .koreanName();
    }

    public TenGodPowerLevel powerLevel() {

        return sourceProfile
                .powerLevel();
    }

    public boolean primary() {

        return role
                == PersonalityStatementSelectionRole
                .PRIMARY;
    }

    public boolean supporting() {

        return role
                == PersonalityStatementSelectionRole
                .SUPPORTING;
    }

    public boolean internalReference() {

        return role
                == PersonalityStatementSelectionRole
                .INTERNAL_REFERENCE;
    }

    /**
     * 실제 사용자 설명 후보인지.
     */
    public boolean directOutput() {

        return role
                .directOutput();
    }

    /**
     * 기존 명리 계산상
     * 구조 중심 또는 강한 십성인지.
     *
     * 이것은 심리 강도 점수가 아니다.
     */
    public boolean structurallyStrong() {

        return sourceProfile
                .primaryStructure()

                || sourceProfile
                .strongSide();
    }
}