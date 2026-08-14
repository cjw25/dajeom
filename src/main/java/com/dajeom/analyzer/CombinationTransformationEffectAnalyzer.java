package com.dajeom.analyzer;

import com.dajeom.model.CombinationTransformationEffectAnalysis;
import com.dajeom.model.EarthlyBranchCombinationIntegratedCandidate;
import com.dajeom.model.EarthlyBranchCombinationIntegrationAnalysis;
import com.dajeom.model.EarthlyBranchCombinationObstructionEvidence;
import com.dajeom.model.EarthlyBranchTransformationMemberEffect;
import com.dajeom.model.HeavenlyStemCombinationTransformationAnalysis;
import com.dajeom.model.HeavenlyStemCombinationTransformationEvidence;
import com.dajeom.model.HeavenlyStemTransformationMemberEffect;
import com.dajeom.rule.CombinationTransformationEffectRule;
import com.dajeom.type.CombinationTransformationEffectReasonType;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-9.
 *
 * 9-3 천간 합화 결과
 *
 * +
 *
 * 9-8 지지 결합 통합 결과
 *
 * 를 구성원 단위 기능 변화 Evidence로 변환한다.
 *
 * =================================================
 *
 * 천간:
 *
 * 합화 Evidence 하나
 * ->
 * 천간 구성원 2개의 MemberEffect
 *
 * =================================================
 *
 * 지지:
 *
 * 결합 Candidate 하나
 * ->
 * 지지 구성원 2~3개의 MemberEffect
 *
 * =================================================
 *
 * 동일 지지가 여러 결합에 참여하는 경우
 * Effect를 합쳐버리지 않는다.
 *
 * 예:
 *
 * 酉
 *
 * 辰酉六合
 * 酉丑半合
 * 申酉方合
 *
 * 각각의 Effect를 그대로 남긴다.
 *
 * 구성원별 최종 중첩 효과는
 * 이후 통합 단계에서 처리할 수 있다.
 */
@Component
public class CombinationTransformationEffectAnalyzer {

    private final CombinationTransformationEffectRule
            combinationTransformationEffectRule;

    public CombinationTransformationEffectAnalyzer(
            CombinationTransformationEffectRule
                    combinationTransformationEffectRule
    ) {

        this.combinationTransformationEffectRule =
                Objects.requireNonNull(
                        combinationTransformationEffectRule,
                        "CombinationTransformationEffectRule이 필요합니다."
                );
    }

    public CombinationTransformationEffectAnalysis analyze(

            HeavenlyStemCombinationTransformationAnalysis
                    heavenlyStemTransformationAnalysis,

            EarthlyBranchCombinationIntegrationAnalysis
                    earthlyBranchIntegrationAnalysis

    ) {

        Objects.requireNonNull(
                heavenlyStemTransformationAnalysis,
                "9-3 천간 합화 분석이 필요합니다."
        );

        Objects.requireNonNull(
                earthlyBranchIntegrationAnalysis,
                "9-8 지지 결합 통합 분석이 필요합니다."
        );

        if (
                heavenlyStemTransformationAnalysis
                        .dayMaster()
                        != earthlyBranchIntegrationAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "9-3 천간 합화 분석과 9-8 지지 결합 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                heavenlyStemTransformationAnalysis
                        .completeChart()
                        != earthlyBranchIntegrationAnalysis
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "9-3 천간 합화 분석과 9-8 지지 결합 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        List<HeavenlyStemTransformationMemberEffect>
                heavenlyStemEffects =
                buildHeavenlyStemEffects(
                        heavenlyStemTransformationAnalysis
                );

        List<EarthlyBranchTransformationMemberEffect>
                earthlyBranchEffects =
                buildEarthlyBranchEffects(
                        earthlyBranchIntegrationAnalysis
                );

        return new CombinationTransformationEffectAnalysis(
                heavenlyStemTransformationAnalysis
                        .dayMaster(),
                heavenlyStemTransformationAnalysis,
                earthlyBranchIntegrationAnalysis,
                heavenlyStemEffects,
                earthlyBranchEffects,
                heavenlyStemTransformationAnalysis
                        .completeChart()
        );
    }

    /*
     * =================================================
     * 천간 Effect
     * =================================================
     */

    private List<HeavenlyStemTransformationMemberEffect>
    buildHeavenlyStemEffects(
            HeavenlyStemCombinationTransformationAnalysis analysis
    ) {

        List<HeavenlyStemTransformationMemberEffect> results =
                new ArrayList<>();

        for (
                HeavenlyStemCombinationTransformationEvidence evidence
                : analysis.evidences()
        ) {

            TransformationOriginalFunctionState
                    originalFunctionState =
                    combinationTransformationEffectRule
                            .determineHeavenlyOriginalFunctionState(
                                    evidence
                            );

            TransformationTargetActivationState
                    targetActivationState =
                    combinationTransformationEffectRule
                            .determineHeavenlyTargetActivationState(
                                    evidence
                            );

            List<CombinationTransformationEffectReasonType> reasons =
                    combinationTransformationEffectRule
                            .buildHeavenlyReasons(
                                    evidence,
                                    originalFunctionState,
                                    targetActivationState
                            );

            /*
             * 첫 번째 천간
             */
            results.add(
                    new HeavenlyStemTransformationMemberEffect(
                            evidence,
                            evidence.firstPosition(),
                            evidence.firstStem(),
                            originalFunctionState,
                            evidence.targetElement(),
                            targetActivationState,
                            reasons
                    )
            );

            /*
             * 두 번째 천간
             */
            results.add(
                    new HeavenlyStemTransformationMemberEffect(
                            evidence,
                            evidence.secondPosition(),
                            evidence.secondStem(),
                            originalFunctionState,
                            evidence.targetElement(),
                            targetActivationState,
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 1. 궁위 순서
         *
         * 2. 동일 궁위에서는
         *    목표 오행 활성도가 높은 Evidence 우선
         *
         * 첫 comparingInt의 람다 타입을 명시하여
         * Java가 Object로 추론하는 문제를 방지한다.
         */
        Comparator<HeavenlyStemTransformationMemberEffect>
                comparator =
                Comparator
                        .comparingInt(
                                (
                                        HeavenlyStemTransformationMemberEffect effect
                                ) ->
                                        effect
                                                .position()
                                                .ordinal()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        HeavenlyStemTransformationMemberEffect effect
                                                ) ->
                                                        effect
                                                                .targetActivationState()
                                                                .rank()
                                        )
                                        .reversed()
                        );

        results.sort(
                comparator
        );

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * 지지 Effect
     * =================================================
     */

    private List<EarthlyBranchTransformationMemberEffect>
    buildEarthlyBranchEffects(
            EarthlyBranchCombinationIntegrationAnalysis analysis
    ) {

        List<EarthlyBranchTransformationMemberEffect> results =
                new ArrayList<>();

        for (
                EarthlyBranchCombinationIntegratedCandidate candidate
                : analysis.candidates()
        ) {

            TransformationOriginalFunctionState
                    originalFunctionState =
                    combinationTransformationEffectRule
                            .determineEarthlyOriginalFunctionState(
                                    candidate
                            );

            TransformationTargetActivationState
                    targetActivationState =
                    combinationTransformationEffectRule
                            .determineEarthlyTargetActivationState(
                                    candidate
                            );

            for (
                    EarthlyBranchPosition position
                    : candidate.candidatePositions()
            ) {

                EarthlyBranch branch =
                        findBranchAt(
                                candidate,
                                position
                        );

                boolean cardinalMember =
                        isCardinalMember(
                                candidate,
                                branch
                        );

                List<CombinationTransformationEffectReasonType> reasons =
                        combinationTransformationEffectRule
                                .buildEarthlyReasons(
                                        candidate,
                                        cardinalMember,
                                        originalFunctionState,
                                        targetActivationState
                                );

                results.add(
                        new EarthlyBranchTransformationMemberEffect(
                                candidate,
                                position,
                                branch,
                                cardinalMember,
                                originalFunctionState,
                                candidate.targetElement(),
                                targetActivationState,
                                reasons
                        )
                );
            }
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 1. 궁위 순서
         *
         * 2. 동일 궁위에서는
         *    목표 오행 활성도가 높은 Effect 우선
         *
         * 3. 동일 조건에서는
         *    결합 역할 우선순위가 높은 Candidate 우선
         *
         * 주의:
         *
         * 마지막에 전체 Comparator.reversed()를 호출하지 않는다.
         *
         * 그렇게 하면 궁위 순서까지 역순으로 뒤집히기 때문이다.
         *
         * 필요한 두 번째/세 번째 비교 기준만
         * 각각 reversed() 한다.
         */
        Comparator<EarthlyBranchTransformationMemberEffect>
                comparator =
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchTransformationMemberEffect effect
                                ) ->
                                        effect
                                                .position()
                                                .ordinal()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        EarthlyBranchTransformationMemberEffect effect
                                                ) ->
                                                        effect
                                                                .targetActivationState()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        EarthlyBranchTransformationMemberEffect effect
                                                ) ->
                                                        effect
                                                                .sourceCandidate()
                                                                .role()
                                                                .rank()
                                        )
                                        .reversed()
                        );

        results.sort(
                comparator
        );

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * 후보 위치 -> 실제 지지
     * =================================================
     */

    private EarthlyBranch findBranchAt(

            EarthlyBranchCombinationIntegratedCandidate candidate,

            EarthlyBranchPosition position

    ) {

        EarthlyBranchCombinationObstructionEvidence source =
                candidate.sourceEvidence();

        /*
         * 六合
         */
        if (
                source.candidateKind()
                        .sixCombination()
        ) {

            if (
                    source
                            .sixCombinationEvidence()
                            .firstPosition()
                            == position
            ) {

                return source
                        .sixCombinationEvidence()
                        .firstBranch();
            }

            if (
                    source
                            .sixCombinationEvidence()
                            .secondPosition()
                            == position
            ) {

                return source
                        .sixCombinationEvidence()
                        .secondBranch();
            }
        }

        /*
         * 三合
         */
        if (
                source.candidateKind()
                        .threeCombination()
        ) {

            List<EarthlyBranchPosition> positions =
                    source
                            .threeCombinationEvidence()
                            .positions();

            List<EarthlyBranch> branches =
                    source
                            .threeCombinationEvidence()
                            .branches();

            for (
                    int index = 0;
                    index < positions.size();
                    index++
            ) {

                if (
                        positions.get(index)
                                == position
                ) {

                    return branches.get(
                            index
                    );
                }
            }
        }

        /*
         * 方合
         */
        if (
                source.candidateKind()
                        .directionalCombination()
        ) {

            List<EarthlyBranchPosition> positions =
                    source
                            .directionalCombinationEvidence()
                            .positions();

            List<EarthlyBranch> branches =
                    source
                            .directionalCombinationEvidence()
                            .branches();

            for (
                    int index = 0;
                    index < positions.size();
                    index++
            ) {

                if (
                        positions.get(index)
                                == position
                ) {

                    return branches.get(
                            index
                    );
                }
            }
        }

        throw new IllegalStateException(
                "9-8 결합 후보에서 위치에 해당하는 지지를 찾을 수 없습니다: "
                        + position
        );
    }

    /*
     * =================================================
     * 왕지 여부
     * =================================================
     */

    private boolean isCardinalMember(

            EarthlyBranchCombinationIntegratedCandidate candidate,

            EarthlyBranch branch

    ) {

        EarthlyBranchCombinationObstructionEvidence source =
                candidate.sourceEvidence();

        if (
                source.candidateKind()
                        .sixCombination()
        ) {

            return false;
        }

        if (
                source.candidateKind()
                        .threeCombination()
        ) {

            if (
                    !source
                            .threeCombinationEvidence()
                            .cardinalBranchIncluded()
            ) {

                return false;
            }

            return source
                    .threeCombinationEvidence()
                    .combinationType()
                    .cardinalBranch()
                    == branch;
        }

        if (
                !source
                        .directionalCombinationEvidence()
                        .cardinalBranchIncluded()
        ) {

            return false;
        }

        return source
                .directionalCombinationEvidence()
                .combinationType()
                .cardinalBranch()
                == branch;
    }
}