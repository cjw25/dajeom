package com.dajeom.rule;

import com.dajeom.domain.BranchCombinationFormationState;
import com.dajeom.domain.BranchCombinationKind;
import com.dajeom.domain.BranchCombinationTransformationAssessment;
import com.dajeom.domain.BranchCombinationTransformationContext;
import com.dajeom.domain.BranchCombinationTransformationState;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 지지 합 구조와 실제 합화를 분리해서 판정한다.
 *
 * =================================================
 * 엔진의 엄격한 합화 조건
 * =================================================
 *
 * 1. 합 관계 자체가 완성되어 있을 것
 *
 *    六合
 *    → 두 지지가 존재
 *
 *    三合
 *    → 세 지지가 모두 존재
 *
 *    方合
 *    → 세 지지가 모두 존재
 *
 * 2. 화신이 월령에서 旺 또는 相
 *
 * 3. 화신 오행이 천간에 실제 드러남
 *
 * 4. 화신 오행이 지장간에도 기반을 가짐
 *
 * 5. 합 참여 지지에 충이 없음
 *
 * 6. 다른 합과 경쟁하지 않음
 *
 * =================================================
 *
 * 이것은 점수식이 아니다.
 *
 * 조건 하나당 몇 점 같은 계산도 없다.
 *
 * 엄격한 조건을 모두 만족할 때만
 * ACCEPTED 상태가 된다.
 */
@Component
public class BranchCombinationTransformationRule {

    public BranchCombinationTransformationAssessment assess(
            BranchCombinationTransformationContext context
    ) {

        Objects.requireNonNull(
                context,
                "지지 합화 Context는 필수입니다."
        );

        BranchCombinationFormationState formationState =
                resolveFormationState(
                        context
                                .occurrence()
                                .kind()
                );

        BranchCombinationTransformationState transformationState =
                resolveTransformationState(
                        context
                );

        return new BranchCombinationTransformationAssessment(
                context.occurrence(),
                formationState,
                transformationState,
                context
        );
    }

    private BranchCombinationFormationState resolveFormationState(
            BranchCombinationKind kind
    ) {

        return switch (kind) {

            case SIX_HARMONY ->
                    BranchCombinationFormationState
                            .SIX_HARMONY_RELATION;

            case THREE_HARMONY ->
                    BranchCombinationFormationState
                            .THREE_HARMONY_COMPLETE;

            case DIRECTIONAL_HARMONY ->
                    BranchCombinationFormationState
                            .DIRECTIONAL_HARMONY_COMPLETE;
        };
    }

    private BranchCombinationTransformationState
    resolveTransformationState(

            BranchCombinationTransformationContext context

    ) {

        /*
         * 화신이 월령에서 旺/相인가.
         */
        boolean seasonalSupport =
                context
                        .transformationElementWangOrXiang();

        /*
         * 화신 오행이 천간에 드러났는가.
         */
        boolean visibleSupport =
                context
                        .hasVisibleTransformationElement();

        /*
         * 화신 오행이 지장간에 실제 기반을 가지는가.
         */
        boolean hiddenSupport =
                context
                        .hasHiddenTransformationElement();

        /*
         * 합 참여 지지 자체에 충이 걸려 있는가.
         */
        boolean clash =
                context
                        .hasClashedParticipant();

        /*
         * 다른 합 구조와 쟁합/경쟁하는가.
         */
        boolean competition =
                context
                        .hasCompetingCombination();

        boolean transformationSupport =
                seasonalSupport
                        && visibleSupport
                        && hiddenSupport;

        /*
         * 합화 조건은 갖춰졌으나
         * 충이나 다른 합 경쟁이 함께 존재.
         *
         * 이런 경우 합화를 확정하지 않는다.
         */
        if (
                transformationSupport
                        &&
                        (
                                clash
                                        ||
                                        competition
                        )
        ) {

            return BranchCombinationTransformationState
                    .CONTESTED;
        }

        /*
         * 엄격한 조건 전체 충족.
         */
        if (
                transformationSupport
                        &&
                        !clash
                        &&
                        !competition
        ) {

            return BranchCombinationTransformationState
                    .ACCEPTED;
        }

        /*
         * 화신을 받쳐주는 Context 일부가 있음.
         *
         * 예:
         *
         * - 월령은 받쳐주지만 천간 투출 없음
         * - 천간에는 드러났지만 월령에서 휴수
         * - 천간과 월령은 맞지만 지장간 기반이 없음
         *
         * 이런 경우 합화로 확정하지 않는다.
         */
        if (
                seasonalSupport
                        ||
                        visibleSupport
                        ||
                        hiddenSupport
        ) {

            /*
             * 조건 자체가 불완전한데
             * 동시에 충/경쟁까지 있다면
             * 역시 CONTESTED로 남긴다.
             */
            if (
                    clash
                            ||
                            competition
            ) {

                return BranchCombinationTransformationState
                        .CONTESTED;
            }

            return BranchCombinationTransformationState
                    .SUPPORTED_BUT_INCOMPLETE;
        }

        /*
         * 합 관계 자체만 존재.
         *
         * 실제 화신을 뒷받침할 Context를
         * 현재 원국에서 확인하지 못함.
         */
        return BranchCombinationTransformationState
                .NOT_ESTABLISHED;
    }
}