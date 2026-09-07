package com.dajeom.calculation;

import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimCode;
import com.dajeom.domain.LifeInterpretationCategory;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * LifeClaim을 최종 7개 장문 풀이 카테고리에 배치한다.
 *
 * 하나의 Claim은 여러 카테고리에 들어갈 수 있다.
 *
 * 숫자 가중치나 점수 기반 우선순위는 사용하지 않는다.
 */
@Component
public class LifeClaimCategoryMapper {

    public Set<LifeInterpretationCategory> categoriesOf(
            LifeClaim claim
    ) {

        Objects.requireNonNull(
                claim,
                "LifeClaim은 필수입니다."
        );

        return categoriesOf(
                claim.code()
        );
    }

    public Set<LifeInterpretationCategory> categoriesOf(
            LifeClaimCode code
    ) {

        Objects.requireNonNull(
                code,
                "LifeClaimCode는 필수입니다."
        );

        return switch (code) {

            /*
             * =================================================
             * 기본 십성 family
             * =================================================
             */

            case PREPARATION_AND_REFLECTION_SUPPORT_STABILITY ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.LOVE_MARRIAGE,
                            LifeInterpretationCategory.WEALTH,
                            LifeInterpretationCategory.HEALTH_LIFESTYLE
                    );

            case EXPRESSION_NEEDS_A_CONSTRUCTIVE_OUTLET ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.LOVE_MARRIAGE,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT,
                            LifeInterpretationCategory.HEALTH_LIFESTYLE
                    );

            case PRACTICAL_RESOURCE_MANAGEMENT_IS_A_RECURRING_THEME ->

                    EnumSet.of(
                            LifeInterpretationCategory.WEALTH,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case RESPONSIBILITY_AND_EXPECTATIONS_SHAPE_CHOICES ->

                    EnumSet.of(
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.LOVE_MARRIAGE,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case AUTONOMY_AND_PEER_BOUNDARIES_MATTER ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.LOVE_MARRIAGE,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case SUSTAINABLE_ROUTINE_SHOULD_MATCH_ENERGY_PATTERN ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT,
                            LifeInterpretationCategory.HEALTH_LIFESTYLE
                    );

            /*
             * =================================================
             * 기본 강약
             * =================================================
             */

            case SELF_DIRECTION_IS_EASIER_TO_SUSTAIN ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case PACING_AND_SUPPORT_MATTER_FOR_SUSTAINED_OUTPUT ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT,
                            LifeInterpretationCategory.HEALTH_LIFESTYLE
                    );

            case BASELINE_TENDENCIES_REQUIRE_CONTEXTUAL_ADJUSTMENT ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case DOMINANT_CONTEXT_STRONGLY_SHAPES_DECISIONS ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case STRUCTURAL_ALIGNMENT_CHANGES_HOW_STRENGTH_IS_EXPRESSED ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            /*
             * =================================================
             * 食傷 / 회복
             * =================================================
             */

            case RECOVERY_BEFORE_OVEREXTENSION ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT,
                            LifeInterpretationCategory.HEALTH_LIFESTYLE
                    );

            case OUTPUT_CAN_CONSUME_PERSONAL_RESOURCES ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT,
                            LifeInterpretationCategory.HEALTH_LIFESTYLE
                    );

            case OVERCONTROL_CAN_SUPPRESS_EXPRESSION ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            /*
             * =================================================
             * 印
             * =================================================
             */

            case TOO_MUCH_SUPPORT_CAN_DELAY_ACTION ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case BALANCE_STABILITY_AND_MATERIAL_GOALS ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.WEALTH,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            /*
             * =================================================
             * 財
             * =================================================
             */

            case MATERIAL_RESULTS_ARE_IMPORTANT_MOTIVATORS ->

                    EnumSet.of(
                            LifeInterpretationCategory.WEALTH,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case MATERIAL_GOALS_AND_STABILITY_CAN_COEXIST ->

                    EnumSet.of(
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.WEALTH,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case MATERIAL_PRESSURE_CAN_DISRUPT_STABILITY ->

                    EnumSet.of(
                            LifeInterpretationCategory.WEALTH,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT,
                            LifeInterpretationCategory.HEALTH_LIFESTYLE
                    );

            case PRACTICAL_GOALS_CAN_CORRECT_OVERPROTECTION ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.WEALTH,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case PRESSURE_CAN_BE_REDUCED_WITHOUT_LOSING_PRACTICAL_GOALS ->

                    EnumSet.of(
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.WEALTH,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case STABILITY_CAN_BE_PRESERVED_WHEN_MATERIAL_PRESSURE_IS_REDUCED ->

                    EnumSet.of(
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.WEALTH,
                            LifeInterpretationCategory.HEALTH_LIFESTYLE
                    );

            /*
             * =================================================
             * 官殺
             * =================================================
             */

            case COMPLEX_AUTHORITY_EXPECTATIONS_REQUIRE_CLARITY ->

                    EnumSet.of(
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.LOVE_MARRIAGE,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case AUTHORITY_COMPLEXITY_CAN_BE_CHANNELLED ->

                    EnumSet.of(
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case COMPETITIVE_DRIVE_NEEDS_DIRECTION ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            /*
             * =================================================
             * 陽刃
             * =================================================
             */

            case DECISIVE_INDEPENDENT_DRIVE ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.LOVE_MARRIAGE,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case STRONG_DRIVE_BENEFITS_FROM_CLEAR_RESPONSIBILITY ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            /*
             * =================================================
             * 天干合
             * =================================================
             */

            case STRONG_BINDING_DYNAMICS_CAN_REDIRECT_PRIORITIES ->

                    EnumSet.of(
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.LOVE_MARRIAGE,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            /*
             * =================================================
             * 月令
             * =================================================
             */

            case CORE_LIFE_PATTERN_HAS_INTERNAL_FRICTION ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS
                    );

            case INTERNAL_FRICTION_HAS_A_RESOLUTION_ROUTE ->

                    EnumSet.of(
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            /*
             * =================================================
             * 格局
             * =================================================
             */

            case LIFE_DIRECTION_HAS_A_COHERENT_STRUCTURE ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case CORE_STRENGTH_COMES_WITH_A_MANAGEABLE_DEFECT ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case CORE_STRUCTURE_REQUIRES_ACTIVE_REORGANIZATION ->

                    EnumSet.of(
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case DIFFICULTY_CAN_BE_CONVERTED_THROUGH_COMPENSATION ->

                    EnumSet.of(
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );

            case LIFE_DIRECTION_DEPENDS_MORE_ON_CHOSEN_STRUCTURE ->

                    EnumSet.of(
                            LifeInterpretationCategory.TEMPERAMENT,
                            LifeInterpretationCategory.PAST_FLOW,
                            LifeInterpretationCategory.WORK_ACHIEVEMENT
                    );
        };
    }
}