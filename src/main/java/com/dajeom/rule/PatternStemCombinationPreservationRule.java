package com.dajeom.rule;

import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternStemCombinationActivationAnalysis;
import com.dajeom.domain.PatternStemCombinationActivationOccurrence;
import com.dajeom.domain.PatternStemCombinationPreservationAnalysis;
import com.dajeom.domain.PatternStemTenGodBindingAnalysis;
import com.dajeom.domain.PatternStemTenGodBindingState;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * active 天干合을
 *
 * "어떤 십성이 실제 묶였고
 *  어떤 십성이 남았는가"
 *
 * 로 변환한다.
 *
 * =================================================
 *
 * 핵심:
 *
 * 같은 십성이 여러 개 투출되면
 * 하나만 합됐다고 전체를 제거된 것으로 보지 않는다.
 *
 * 예:
 *
 * 七殺 A → active 합
 * 七殺 B → 그대로
 *
 * → PARTIALLY_BOUND
 * → 合殺留官 미완성
 *
 * =================================================
 *
 * 爭合 검토가 걸린 노드는
 * FULLY_BOUND로 올리지 않는다.
 *
 * =================================================
 *
 * BLOCKED / DISTANT / SELF_COMBINATION은
 * 合去용 active binding으로 사용하지 않는다.
 */
@Component
public class PatternStemCombinationPreservationRule {

    public PatternStemCombinationPreservationAnalysis assess(

            PatternTenGodInteractionAnalysis tenGodInteractions,

            PatternStemCombinationActivationAnalysis activationAnalysis

    ) {

        Objects.requireNonNull(
                tenGodInteractions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                activationAnalysis,
                "PatternStemCombinationActivationAnalysis는 필수입니다."
        );

        if (
                tenGodInteractions.dayMaster()
                        != activationAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "십성 Interaction과 천간합 Activation의 일간이 일치하지 않습니다."
            );
        }

        validateActivationNodes(
                tenGodInteractions,
                activationAnalysis
        );

        List<PatternStemTenGodBindingAnalysis> bindings =
                new ArrayList<>();

        for (
                MonthCommandPatternCandidateType type
                : MonthCommandPatternCandidateType.values()
        ) {

            bindings.add(
                    analyzeBinding(
                            type,
                            tenGodInteractions,
                            activationAnalysis
                    )
            );
        }

        return new PatternStemCombinationPreservationAnalysis(

                tenGodInteractions.dayMaster(),

                activationAnalysis,

                bindings
        );
    }

    private PatternStemTenGodBindingAnalysis analyzeBinding(

            MonthCommandPatternCandidateType type,

            PatternTenGodInteractionAnalysis interactions,

            PatternStemCombinationActivationAnalysis activations

    ) {

        List<PatternTenGodNode> visibleNodes =
                interactions
                        .visibleNodesOf(type);

        if (visibleNodes.isEmpty()) {

            return new PatternStemTenGodBindingAnalysis(

                    type,

                    List.of(),

                    List.of(),

                    List.of(),

                    PatternStemTenGodBindingState
                            .ABSENT
            );
        }

        Set<PatternTenGodNode> activelyBound =
                new LinkedHashSet<>();

        Set<PatternTenGodNode> reviewNodes =
                new LinkedHashSet<>();

        for (
                PatternStemCombinationActivationOccurrence activation
                : activations.activations()
        ) {

            /*
             * =============================================
             * 실제 active 비일간 합
             * =============================================
             */

            if (
                    activation
                            .usableAsNonDayMasterRemoval()
            ) {

                addMatchingNode(
                        activation
                                .combination()
                                .left(),
                        type,
                        activelyBound
                );

                addMatchingNode(
                        activation
                                .combination()
                                .right(),
                        type,
                        activelyBound
                );
            }

            /*
             * =============================================
             * 爭合/妬合 검토
             * =============================================
             *
             * 이 경우는 묶였다고 확정하지 않는다.
             */

            if (
                    activation
                            .competingReviewRequired()
            ) {

                addMatchingNode(
                        activation
                                .combination()
                                .left(),
                        type,
                        reviewNodes
                );

                addMatchingNode(
                        activation
                                .combination()
                                .right(),
                        type,
                        reviewNodes
                );
            }
        }

        /*
         * 동일 노드에 review가 걸렸다면
         * active 확정보다 review를 우선한다.
         */
        activelyBound.removeAll(
                reviewNodes
        );

        PatternStemTenGodBindingState state;

        if (!reviewNodes.isEmpty()) {

            state =
                    PatternStemTenGodBindingState
                            .REVIEW_REQUIRED;

        } else if (activelyBound.isEmpty()) {

            state =
                    PatternStemTenGodBindingState
                            .RETAINED;

        } else if (
                activelyBound.containsAll(
                        visibleNodes
                )
        ) {

            state =
                    PatternStemTenGodBindingState
                            .FULLY_BOUND;

        } else {

            state =
                    PatternStemTenGodBindingState
                            .PARTIALLY_BOUND;
        }

        return new PatternStemTenGodBindingAnalysis(

                type,

                visibleNodes,

                List.copyOf(
                        activelyBound
                ),

                List.copyOf(
                        reviewNodes
                ),

                state
        );
    }

    private void addMatchingNode(

            PatternTenGodNode node,

            MonthCommandPatternCandidateType type,

            Set<PatternTenGodNode> destination

    ) {

        if (
                node.visible()
                        &&
                        node.hasTenGodType(type)
        ) {

            destination.add(
                    node
            );
        }
    }

    /**
     * Activation에 등장하는 투출 천간 노드가
     * 실제 같은 십성 Interaction 분석에
     * 존재하는지 확인한다.
     */
    private void validateActivationNodes(

            PatternTenGodInteractionAnalysis interactions,

            PatternStemCombinationActivationAnalysis activations

    ) {

        List<PatternTenGodNode> nodes =
                interactions.nodes();

        for (
                PatternStemCombinationActivationOccurrence activation
                : activations.activations()
        ) {

            PatternTenGodNode left =
                    activation
                            .combination()
                            .left();

            PatternTenGodNode right =
                    activation
                            .combination()
                            .right();

            if (!nodes.contains(left)) {

                throw new IllegalArgumentException(
                        "천간합 Activation의 left 노드가 십성 Interaction에 존재하지 않습니다."
                );
            }

            if (!nodes.contains(right)) {

                throw new IllegalArgumentException(
                        "천간합 Activation의 right 노드가 십성 Interaction에 존재하지 않습니다."
                );
            }
        }
    }
}