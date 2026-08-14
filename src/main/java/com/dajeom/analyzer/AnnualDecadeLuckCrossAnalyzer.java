package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.AnnualDecadeLuckCrossResolution;
import com.dajeom.model.AnnualDecadeLuckRelationReference;
import com.dajeom.model.AnnualDecadeLuckTransformationReference;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.rule.CombinationTransformationEffectRule;
import com.dajeom.rule.EarthlyBranchCombinationIntegrationRule;
import com.dajeom.rule.EarthlyBranchCombinationObstructionRule;
import com.dajeom.rule.EarthlyBranchDirectionalCombinationFormationRule;
import com.dajeom.rule.EarthlyBranchDirectionalCombinationRule;
import com.dajeom.rule.EarthlyBranchSixCombinationTransformationRule;
import com.dajeom.rule.EarthlyBranchThreeCombinationFormationRule;
import com.dajeom.rule.EarthlyBranchThreeCombinationRule;
import com.dajeom.rule.HeavenlyStemCombinationCandidateRule;
import com.dajeom.rule.HeavenlyStemCombinationCompetitionRule;
import com.dajeom.rule.HeavenlyStemCombinationTransformationRule;
import com.dajeom.rule.HeavenlyStemControlRule;
import com.dajeom.type.AnnualDecadeLuckRelationLayer;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchCombinationIntegrationRole;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import com.dajeom.type.EarthlyBranchDirectionalCombinationForm;
import com.dajeom.type.EarthlyBranchDirectionalCombinationFormationState;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchRelationKind;
import com.dajeom.type.EarthlyBranchSixCombinationTransformationState;
import com.dajeom.type.EarthlyBranchThreeCombinationForm;
import com.dajeom.type.EarthlyBranchThreeCombinationFormationState;
import com.dajeom.type.EarthlyBranchThreeCombinationType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationApplicability;
import com.dajeom.type.HeavenlyStemCombinationCompetitionState;
import com.dajeom.type.HeavenlyStemCombinationTransformationState;
import com.dajeom.type.HeavenlyStemControlRelationType;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 14차-5.
 *
 * 대운 × 세운 관계 및 합화 Analyzer.
 *
 * =================================================
 *
 * 범위:
 *
 * 대운 천간
 * ↕
 * 세운 천간
 *
 * 대운 지지
 * ↕
 * 세운 지지
 *
 * =================================================
 *
 * 중요:
 *
 * 1.
 * 대운과 세운은 둘 다 외부 시간축이다.
 *
 * 2.
 * YEAR / MONTH / DAY / HOUR 같은
 * 원국 Palace 위치를 부여하지 않는다.
 *
 * 3.
 * 따라서:
 *
 * adjacent = false
 * compact = false
 * monthBranchInvolved = false
 *
 * 로 평가한다.
 *
 * 4.
 * 원국 글자와의 경쟁·방해는 여기서 추가하지 않는다.
 * 그것은 14-6 원국 × 대운 × 세운 Context 책임이다.
 *
 * 5.
 * 관계 존재와 실제 합화 활성은 분리한다.
 *
 * 6.
 * 여러 관계가 동시에 성립하면 모두 보존한다.
 */
@Component
public class AnnualDecadeLuckCrossAnalyzer {

    private final HeavenlyStemControlRule
            heavenlyStemControlRule;

    private final EarthlyBranchThreeCombinationRule
            earthlyBranchThreeCombinationRule;

    private final EarthlyBranchDirectionalCombinationRule
            earthlyBranchDirectionalCombinationRule;

    private final HeavenlyStemCombinationCandidateRule
            heavenlyStemCombinationCandidateRule;

    private final HeavenlyStemCombinationCompetitionRule
            heavenlyStemCombinationCompetitionRule;

    private final HeavenlyStemCombinationTransformationRule
            heavenlyStemCombinationTransformationRule;

    private final EarthlyBranchSixCombinationTransformationRule
            earthlyBranchSixCombinationTransformationRule;

    private final EarthlyBranchThreeCombinationFormationRule
            earthlyBranchThreeCombinationFormationRule;

    private final EarthlyBranchDirectionalCombinationFormationRule
            earthlyBranchDirectionalCombinationFormationRule;

    private final EarthlyBranchCombinationObstructionRule
            earthlyBranchCombinationObstructionRule;

    private final EarthlyBranchCombinationIntegrationRule
            earthlyBranchCombinationIntegrationRule;

    private final CombinationTransformationEffectRule
            combinationTransformationEffectRule;

    public AnnualDecadeLuckCrossAnalyzer(

            HeavenlyStemControlRule
                    heavenlyStemControlRule,

            EarthlyBranchThreeCombinationRule
                    earthlyBranchThreeCombinationRule,

            EarthlyBranchDirectionalCombinationRule
                    earthlyBranchDirectionalCombinationRule,

            HeavenlyStemCombinationCandidateRule
                    heavenlyStemCombinationCandidateRule,

            HeavenlyStemCombinationCompetitionRule
                    heavenlyStemCombinationCompetitionRule,

            HeavenlyStemCombinationTransformationRule
                    heavenlyStemCombinationTransformationRule,

            EarthlyBranchSixCombinationTransformationRule
                    earthlyBranchSixCombinationTransformationRule,

            EarthlyBranchThreeCombinationFormationRule
                    earthlyBranchThreeCombinationFormationRule,

            EarthlyBranchDirectionalCombinationFormationRule
                    earthlyBranchDirectionalCombinationFormationRule,

            EarthlyBranchCombinationObstructionRule
                    earthlyBranchCombinationObstructionRule,

            EarthlyBranchCombinationIntegrationRule
                    earthlyBranchCombinationIntegrationRule,

            CombinationTransformationEffectRule
                    combinationTransformationEffectRule

    ) {

        this.heavenlyStemControlRule =
                Objects.requireNonNull(
                        heavenlyStemControlRule
                );

        this.earthlyBranchThreeCombinationRule =
                Objects.requireNonNull(
                        earthlyBranchThreeCombinationRule
                );

        this.earthlyBranchDirectionalCombinationRule =
                Objects.requireNonNull(
                        earthlyBranchDirectionalCombinationRule
                );

        this.heavenlyStemCombinationCandidateRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationCandidateRule
                );

        this.heavenlyStemCombinationCompetitionRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationCompetitionRule
                );

        this.heavenlyStemCombinationTransformationRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationTransformationRule
                );

        this.earthlyBranchSixCombinationTransformationRule =
                Objects.requireNonNull(
                        earthlyBranchSixCombinationTransformationRule
                );

        this.earthlyBranchThreeCombinationFormationRule =
                Objects.requireNonNull(
                        earthlyBranchThreeCombinationFormationRule
                );

        this.earthlyBranchDirectionalCombinationFormationRule =
                Objects.requireNonNull(
                        earthlyBranchDirectionalCombinationFormationRule
                );

        this.earthlyBranchCombinationObstructionRule =
                Objects.requireNonNull(
                        earthlyBranchCombinationObstructionRule
                );

        this.earthlyBranchCombinationIntegrationRule =
                Objects.requireNonNull(
                        earthlyBranchCombinationIntegrationRule
                );

        this.combinationTransformationEffectRule =
                Objects.requireNonNull(
                        combinationTransformationEffectRule
                );
    }

    public AnnualDecadeLuckCrossResolution analyze(

            ElementStrengthAnalysis elementStrengthAnalysis,

            AnnualLuckPeriod annualPeriod,

            DecadeLuckPeriod decadePeriod

    ) {

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "14-5 합화 판단에는 원국 ElementStrengthAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualPeriod,
                "14-5 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                decadePeriod,
                "14-5 분석에는 DecadeLuckPeriod가 필요합니다."
        );

        HeavenlyStem decadeStem =
                decadePeriod
                        .pillar()
                        .stem();

        EarthlyBranch decadeBranch =
                decadePeriod
                        .pillar()
                        .branch();

        HeavenlyStem annualStem =
                annualPeriod.stem();

        EarthlyBranch annualBranch =
                annualPeriod.branch();

        List<AnnualDecadeLuckRelationReference>
                relations =
                new ArrayList<>();

        buildStemRelations(
                decadeStem,
                annualStem,
                relations
        );

        buildBranchRelations(
                decadeBranch,
                annualBranch,
                relations
        );

        List<AnnualDecadeLuckTransformationReference>
                transformations =
                new ArrayList<>();

        buildHeavenlyTransformation(
                elementStrengthAnalysis,
                decadeStem,
                annualStem
        ).ifPresent(
                transformations::add
        );

        transformations.addAll(
                buildEarthlyTransformations(
                        elementStrengthAnalysis,
                        decadeBranch,
                        annualBranch
                )
        );

        relations.sort(
                Comparator
                        .comparingInt(
                                (
                                        AnnualDecadeLuckRelationReference reference
                                ) ->
                                        reference
                                                .layer()
                                                .ordinal()
                        )
                        .thenComparing(
                                AnnualDecadeLuckRelationReference
                                        ::relationCode
                        )
        );

        transformations.sort(
                Comparator.comparing(
                        AnnualDecadeLuckTransformationReference
                                ::transformationCode
                )
        );

        return new AnnualDecadeLuckCrossResolution(
                annualPeriod,
                decadePeriod,
                relations,
                transformations
        );
    }

    /*
     * =================================================
     * 천간 관계
     * =================================================
     */

    private void buildStemRelations(

            HeavenlyStem decadeStem,

            HeavenlyStem annualStem,

            List<AnnualDecadeLuckRelationReference> results

    ) {

        StemRelationTable
                .findCombinationElement(
                        decadeStem,
                        annualStem
                )
                .ifPresent(
                        ignored ->
                                results.add(
                                        relation(
                                                AnnualDecadeLuckRelationLayer
                                                        .HEAVENLY_STEM,
                                                "COMBINATION",
                                                "천간합"
                                        )
                                )
                );

        Optional<HeavenlyStemControlRelationType>
                relationType =
                heavenlyStemControlRule
                        .findRelationType(
                                decadeStem,
                                annualStem
                        );

        if (
                relationType.isEmpty()
        ) {

            return;
        }

        if (
                relationType.get()
                        == HeavenlyStemControlRelationType.CLASH
        ) {

            results.add(
                    relation(
                            AnnualDecadeLuckRelationLayer
                                    .HEAVENLY_STEM,
                            "CLASH",
                            "천간충"
                    )
            );

            return;
        }

        /*
         * 극은 방향을 보존한다.
         */

        if (
                heavenlyStemControlRule
                        .controls(
                                decadeStem,
                                annualStem
                        )
        ) {

            results.add(
                    relation(
                            AnnualDecadeLuckRelationLayer
                                    .HEAVENLY_STEM,
                            "CONTROL_DECADE_TO_ANNUAL",
                            "대운 천간이 세운 천간을 극함"
                    )
            );

            return;
        }

        if (
                heavenlyStemControlRule
                        .controls(
                                annualStem,
                                decadeStem
                        )
        ) {

            results.add(
                    relation(
                            AnnualDecadeLuckRelationLayer
                                    .HEAVENLY_STEM,
                            "CONTROL_ANNUAL_TO_DECADE",
                            "세운 천간이 대운 천간을 극함"
                    )
            );
        }
    }

    /*
     * =================================================
     * 지지 관계
     * =================================================
     *
     * 같은 Pair에서 여러 관계가 겹치면
     * 삭제하지 않고 모두 보존한다.
     */

    private void buildBranchRelations(

            EarthlyBranch decadeBranch,

            EarthlyBranch annualBranch,

            List<AnnualDecadeLuckRelationReference> results

    ) {

        if (
                BranchRelationTable
                        .findSixCombinationElement(
                                decadeBranch,
                                annualBranch
                        )
                        .isPresent()
        ) {

            results.add(
                    relation(
                            AnnualDecadeLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "SIX_COMBINATION",
                            "육합"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isClash(
                                decadeBranch,
                                annualBranch
                        )
        ) {

            results.add(
                    relation(
                            AnnualDecadeLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "CLASH",
                            "충"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isPairPunishment(
                                decadeBranch,
                                annualBranch
                        )
        ) {

            results.add(
                    relation(
                            AnnualDecadeLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "PUNISHMENT",
                            "형"
                    )
            );
        }

        if (
                decadeBranch
                        == annualBranch

                        && BranchRelationTable
                        .isSelfPunishmentBranch(
                                decadeBranch
                        )
        ) {

            results.add(
                    relation(
                            AnnualDecadeLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "SELF_PUNISHMENT",
                            "자형"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isBreak(
                                decadeBranch,
                                annualBranch
                        )
        ) {

            results.add(
                    relation(
                            AnnualDecadeLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "BREAK",
                            "파"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isHarm(
                                decadeBranch,
                                annualBranch
                        )
        ) {

            results.add(
                    relation(
                            AnnualDecadeLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "HARM",
                            "해"
                    )
            );
        }

        earthlyBranchThreeCombinationRule
                .findPartialCombinationType(
                        decadeBranch,
                        annualBranch
                )
                .ifPresent(
                        type -> {

                            EarthlyBranchThreeCombinationForm form =
                                    earthlyBranchThreeCombinationRule
                                            .determinePairForm(
                                                    type,
                                                    decadeBranch,
                                                    annualBranch
                                            );

                            results.add(
                                    relation(
                                            AnnualDecadeLuckRelationLayer
                                                    .EARTHLY_BRANCH,
                                            "THREE_COMBINATION_"
                                                    + form.name(),
                                            form
                                                    == EarthlyBranchThreeCombinationForm
                                                    .HALF_WITH_CARDINAL
                                                    ? "반합"
                                                    : "삼합 부분조합"
                                    )
                            );
                        }
                );

        earthlyBranchDirectionalCombinationRule
                .findPartialCombinationType(
                        decadeBranch,
                        annualBranch
                )
                .ifPresent(
                        type -> {

                            EarthlyBranchDirectionalCombinationForm form =
                                    earthlyBranchDirectionalCombinationRule
                                            .determinePairForm(
                                                    type,
                                                    decadeBranch,
                                                    annualBranch
                                            );

                            results.add(
                                    relation(
                                            AnnualDecadeLuckRelationLayer
                                                    .EARTHLY_BRANCH,
                                            "DIRECTIONAL_COMBINATION_"
                                                    + form.name(),
                                            form
                                                    == EarthlyBranchDirectionalCombinationForm
                                                    .PARTIAL_WITH_CARDINAL
                                                    ? "왕지 포함 부분 방합"
                                                    : "부분 방합"
                                    )
                            );
                        }
                );
    }

    /*
     * =================================================
     * 천간합 합화
     * =================================================
     *
     * 대운/세운은 외부 시간축이므로:
     *
     * adjacent = false
     *
     * 대운×세운 자체 내부에는
     * 세 번째 경쟁 천간이나 원국 방해를 넣지 않는다.
     *
     * 3중 경쟁은 14-6의 책임이다.
     */

    private Optional<AnnualDecadeLuckTransformationReference>
    buildHeavenlyTransformation(

            ElementStrengthAnalysis strength,

            HeavenlyStem decadeStem,

            HeavenlyStem annualStem

    ) {

        Optional<FiveElement> target =
                StemRelationTable
                        .findCombinationElement(
                                decadeStem,
                                annualStem
                        );

        if (
                target.isEmpty()
        ) {

            return Optional.empty();
        }

        TargetEnvironment environment =
                targetEnvironment(
                        strength,
                        target.get()
                );

        HeavenlyStemCombinationApplicability applicability =
                heavenlyStemCombinationCandidateRule
                        .determineApplicability(
                                false,
                                environment
                                        .shareBasisPoints(),
                                environment
                                        .seasonalRate(),
                                environment
                                        .monthBranchPresenceWeight()
                        );

        HeavenlyStemCombinationCompetitionState competitionState =
                heavenlyStemCombinationCompetitionRule
                        .determineState(
                                false,
                                false,
                                false
                        );

        HeavenlyStemCombinationTransformationState transformationState =
                heavenlyStemCombinationTransformationRule
                        .determineState(
                                applicability,
                                competitionState
                        );

        TransformationOriginalFunctionState
                originalFunctionState =
                combinationTransformationEffectRule
                        .determineHeavenlyOriginalFunctionState(
                                transformationState
                        );

        TransformationTargetActivationState
                targetActivationState =
                combinationTransformationEffectRule
                        .determineHeavenlyTargetActivationState(
                                transformationState
                        );

        return Optional.of(
                new AnnualDecadeLuckTransformationReference(
                        "HEAVENLY_STEM_COMBINATION:"
                                + target.get().name(),
                        "대운·세운 천간합 합화",
                        target.get()
                                .getKoreanName(),
                        targetActivationState.active(),
                        originalFunctionState.reduced()
                )
        );
    }

    /*
     * =================================================
     * 지지 합화
     * =================================================
     *
     * 후보:
     *
     * 육합
     * 부분 삼합
     * 부분 방합
     *
     * 대운/세운 두 글자만 있으므로
     * 완전 삼합·완전 방합은 여기서 만들지 않는다.
     *
     * 원국 글자까지 들어와 완성되는 국은
     * 14-6의 책임이다.
     */

    private List<AnnualDecadeLuckTransformationReference>
    buildEarthlyTransformations(

            ElementStrengthAnalysis strength,

            EarthlyBranch decadeBranch,

            EarthlyBranch annualBranch

    ) {

        List<DynamicBranchCandidate> candidates =
                new ArrayList<>();

        buildSixCombinationCandidate(
                strength,
                decadeBranch,
                annualBranch
        ).ifPresent(
                candidates::add
        );

        buildThreeCombinationCandidate(
                strength,
                decadeBranch,
                annualBranch
        ).ifPresent(
                candidates::add
        );

        buildDirectionalCombinationCandidate(
                strength,
                decadeBranch,
                annualBranch
        ).ifPresent(
                candidates::add
        );

        if (
                candidates.isEmpty()
        ) {

            return List.of();
        }

        EarthlyBranchCombinationObstructionLevel obstructionLevel =
                determinePairObstructionLevel(
                        decadeBranch,
                        annualBranch
                );

        candidates =
                candidates
                        .stream()
                        .map(
                                candidate ->
                                        candidate
                                                .withObstructionLevel(
                                                        obstructionLevel
                                                )
                        )
                        .toList();

        List<AnnualDecadeLuckTransformationReference> results =
                new ArrayList<>();

        for (
                DynamicBranchCandidate candidate
                : candidates
        ) {

            boolean meaningfulConvergentSupport =
                    false;

            boolean higherPriorityConvergentExists =
                    false;

            boolean higherPriorityCompetingExists =
                    false;

            boolean equalPriorityCompetingExists =
                    false;

            for (
                    DynamicBranchCandidate other
                    : candidates
            ) {

                if (
                        other == candidate
                ) {

                    continue;
                }

                int priorityComparison =
                        comparePriority(
                                candidate,
                                other
                        );

                if (
                        candidate.targetElement()
                                == other.targetElement()
                ) {

                    if (
                            other.baseMeaningful()

                                    && !other
                                    .obstructionLevel()
                                    .severe()
                    ) {

                        meaningfulConvergentSupport =
                                true;
                    }

                    if (
                            priorityComparison < 0
                    ) {

                        higherPriorityConvergentExists =
                                true;
                    }

                } else {

                    if (
                            priorityComparison < 0
                    ) {

                        higherPriorityCompetingExists =
                                true;

                    } else if (
                            priorityComparison == 0
                    ) {

                        equalPriorityCompetingExists =
                                true;
                    }
                }
            }

            EarthlyBranchCombinationIntegrationRole role =
                    earthlyBranchCombinationIntegrationRule
                            .determineRole(
                                    candidate
                                            .obstructionLevel()
                                            .severe(),
                                    candidate
                                            .baseCandidateRemains(),
                                    candidate
                                            .baseMeaningful(),
                                    higherPriorityCompetingExists,
                                    equalPriorityCompetingExists,
                                    meaningfulConvergentSupport,
                                    higherPriorityConvergentExists
                            );

            TransformationOriginalFunctionState
                    originalFunctionState =
                    combinationTransformationEffectRule
                            .determineEarthlyOriginalFunctionState(
                                    role,
                                    candidate
                                            .obstructionLevel()
                            );

            TransformationTargetActivationState
                    targetActivationState =
                    combinationTransformationEffectRule
                            .determineEarthlyTargetActivationState(
                                    role,
                                    candidate
                                            .baseMeaningful(),
                                    candidate
                                            .obstructionLevel(),
                                    candidate
                                            .baseCandidateRemains()
                            );

            results.add(
                    new AnnualDecadeLuckTransformationReference(
                            candidate
                                    .transformationCode(),
                            candidate
                                    .transformationName(),
                            candidate
                                    .targetElement()
                                    .getKoreanName(),
                            targetActivationState.active(),
                            originalFunctionState.reduced()
                    )
            );
        }

        results.sort(
                Comparator.comparing(
                        AnnualDecadeLuckTransformationReference
                                ::transformationCode
                )
        );

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * 육합 후보
     * =================================================
     */

    private Optional<DynamicBranchCandidate>
    buildSixCombinationCandidate(

            ElementStrengthAnalysis strength,

            EarthlyBranch decadeBranch,

            EarthlyBranch annualBranch

    ) {

        Optional<FiveElement> target =
                BranchRelationTable
                        .findSixCombinationElement(
                                decadeBranch,
                                annualBranch
                        );

        if (
                target.isEmpty()
        ) {

            return Optional.empty();
        }

        TargetEnvironment environment =
                targetEnvironment(
                        strength,
                        target.get()
                );

        EarthlyBranchSixCombinationTransformationState state =
                earthlyBranchSixCombinationTransformationRule
                        .determineState(
                                false,
                                false,
                                environment
                                        .shareBasisPoints(),
                                environment
                                        .seasonalRate(),
                                environment
                                        .monthBranchPresenceWeight()
                        );

        return Optional.of(
                new DynamicBranchCandidate(
                        "SIX_COMBINATION:"
                                + target.get().name(),
                        "대운·세운 육합 합화",
                        target.get(),
                        state.rank(),
                        earthlyBranchSixCombinationTransformationRule
                                .meaningful(
                                        state
                                ),
                        earthlyBranchSixCombinationTransformationRule
                                .candidateRemains(
                                        state
                                ),
                        false,
                        false,
                        2,
                        EarthlyBranchCombinationObstructionLevel
                                .NONE
                )
        );
    }

    /*
     * =================================================
     * 부분 삼합 후보
     * =================================================
     */

    private Optional<DynamicBranchCandidate>
    buildThreeCombinationCandidate(

            ElementStrengthAnalysis strength,

            EarthlyBranch decadeBranch,

            EarthlyBranch annualBranch

    ) {

        Optional<EarthlyBranchThreeCombinationType> type =
                earthlyBranchThreeCombinationRule
                        .findPartialCombinationType(
                                decadeBranch,
                                annualBranch
                        );

        if (
                type.isEmpty()
        ) {

            return Optional.empty();
        }

        EarthlyBranchThreeCombinationForm form =
                earthlyBranchThreeCombinationRule
                        .determinePairForm(
                                type.get(),
                                decadeBranch,
                                annualBranch
                        );

        FiveElement target =
                type.get()
                        .cardinalBranch()
                        .getElement();

        TargetEnvironment environment =
                targetEnvironment(
                        strength,
                        target
                );

        EarthlyBranchThreeCombinationFormationState state =
                earthlyBranchThreeCombinationFormationRule
                        .determineState(
                                form,
                                false,
                                false,
                                environment
                                        .shareBasisPoints(),
                                environment
                                        .seasonalRate(),
                                environment
                                        .monthBranchPresenceWeight()
                        );

        boolean cardinalIncluded =
                form
                        == EarthlyBranchThreeCombinationForm
                        .HALF_WITH_CARDINAL;

        return Optional.of(
                new DynamicBranchCandidate(
                        "THREE_COMBINATION_"
                                + form.name()
                                + ":"
                                + target.name(),
                        cardinalIncluded
                                ? "대운·세운 반합 합화"
                                : "대운·세운 삼합 부분조합",
                        target,
                        state.rank(),
                        earthlyBranchThreeCombinationFormationRule
                                .meaningful(
                                        state
                                ),
                        earthlyBranchThreeCombinationFormationRule
                                .candidateRemains(
                                        state
                                ),
                        false,
                        cardinalIncluded,
                        cardinalIncluded
                                ? 3
                                : 1,
                        EarthlyBranchCombinationObstructionLevel
                                .NONE
                )
        );
    }

    /*
     * =================================================
     * 부분 방합 후보
     * =================================================
     */

    private Optional<DynamicBranchCandidate>
    buildDirectionalCombinationCandidate(

            ElementStrengthAnalysis strength,

            EarthlyBranch decadeBranch,

            EarthlyBranch annualBranch

    ) {

        Optional<EarthlyBranchDirectionalCombinationType> type =
                earthlyBranchDirectionalCombinationRule
                        .findPartialCombinationType(
                                decadeBranch,
                                annualBranch
                        );

        if (
                type.isEmpty()
        ) {

            return Optional.empty();
        }

        EarthlyBranchDirectionalCombinationForm form =
                earthlyBranchDirectionalCombinationRule
                        .determinePairForm(
                                type.get(),
                                decadeBranch,
                                annualBranch
                        );

        FiveElement target =
                type.get()
                        .cardinalBranch()
                        .getElement();

        TargetEnvironment environment =
                targetEnvironment(
                        strength,
                        target
                );

        EarthlyBranchDirectionalCombinationFormationState state =
                earthlyBranchDirectionalCombinationFormationRule
                        .determineState(
                                form,
                                false,
                                false,
                                environment
                                        .shareBasisPoints(),
                                environment
                                        .seasonalRate(),
                                environment
                                        .monthBranchPresenceWeight()
                        );

        boolean cardinalIncluded =
                form
                        == EarthlyBranchDirectionalCombinationForm
                        .PARTIAL_WITH_CARDINAL;

        return Optional.of(
                new DynamicBranchCandidate(
                        "DIRECTIONAL_COMBINATION_"
                                + form.name()
                                + ":"
                                + target.name(),
                        cardinalIncluded
                                ? "대운·세운 왕지 포함 부분 방합"
                                : "대운·세운 부분 방합",
                        target,
                        state.rank(),
                        earthlyBranchDirectionalCombinationFormationRule
                                .meaningful(
                                        state
                                ),
                        earthlyBranchDirectionalCombinationFormationRule
                                .candidateRemains(
                                        state
                                ),
                        false,
                        cardinalIncluded,
                        cardinalIncluded
                                ? 3
                                : 1,
                        EarthlyBranchCombinationObstructionLevel
                                .NONE
                )
        );
    }

    /*
     * =================================================
     * 대운 지지 ↔ 세운 지지 내부 방해
     * =================================================
     *
     * 후보가 만들어진 동일 Pair에:
     *
     * 충
     * 형
     * 파
     * 해
     *
     * 가 겹치면 9-7 Obstruction Rule을 적용한다.
     */

    private EarthlyBranchCombinationObstructionLevel
    determinePairObstructionLevel(

            EarthlyBranch decadeBranch,

            EarthlyBranch annualBranch

    ) {

        Set<EarthlyBranchRelationKind> kinds =
                pairObstructionKinds(
                        decadeBranch,
                        annualBranch
                );

        boolean obstructionExists =
                !kinds.isEmpty();

        if (
                !obstructionExists
        ) {

            return EarthlyBranchCombinationObstructionLevel
                    .NONE;
        }

        boolean clash =
                kinds.contains(
                        EarthlyBranchRelationKind.CLASH
                );

        boolean punishment =
                kinds.contains(
                        EarthlyBranchRelationKind.PUNISHMENT
                );

        /*
         * 대운·세운 두 글자 모두 현재 후보의 내부 Member다.
         *
         * 따라서 방해관계가 존재하면
         * internalObstruction = true.
         */

        boolean internalObstruction =
                true;

        boolean internalClash =
                clash;

        boolean internalMultipleKinds =
                kinds.size() >= 2;

        /*
         * cardinal 포함 후보라면
         * 방해 Pair 자체가 후보 전체 Pair이므로
         * 왕지도 영향을 받는다.
         *
         * 실제 cardinal 포함 여부는 후보별로 달라서
         * 이 공통 계산에서는 false로 두고,
         * Integration 우선순위에서 구조 차이를 보존한다.
         *
         * 14-5는 원국 위치 방해를 추가하지 않는다.
         */

        boolean cardinalAffected =
                false;

        boolean cardinalClashed =
                false;

        boolean adjacentObstruction =
                false;

        int affectedMemberCount =
                2;

        return earthlyBranchCombinationObstructionRule
                .determineLevel(
                        true,
                        internalObstruction,
                        internalClash,
                        internalMultipleKinds,
                        cardinalAffected,
                        cardinalClashed,
                        clash,
                        punishment,
                        adjacentObstruction,
                        affectedMemberCount,
                        kinds.size()
                );
    }

    private Set<EarthlyBranchRelationKind>
    pairObstructionKinds(

            EarthlyBranch decadeBranch,

            EarthlyBranch annualBranch

    ) {

        EnumSet<EarthlyBranchRelationKind> result =
                EnumSet.noneOf(
                        EarthlyBranchRelationKind.class
                );

        if (
                BranchRelationTable
                        .isClash(
                                decadeBranch,
                                annualBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind.CLASH
            );
        }

        if (
                BranchRelationTable
                        .isPairPunishment(
                                decadeBranch,
                                annualBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind.PUNISHMENT
            );
        }

        if (
                decadeBranch
                        == annualBranch

                        && BranchRelationTable
                        .isSelfPunishmentBranch(
                                decadeBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind.PUNISHMENT
            );
        }

        if (
                BranchRelationTable
                        .isBreak(
                                decadeBranch,
                                annualBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind.BREAK
            );
        }

        if (
                BranchRelationTable
                        .isHarm(
                                decadeBranch,
                                annualBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind.HARM
            );
        }

        return Set.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 9-8 후보 우선순위
     * =================================================
     */

    private int comparePriority(

            DynamicBranchCandidate first,

            DynamicBranchCandidate second

    ) {

        return earthlyBranchCombinationIntegrationRule
                .comparePriority(
                        first
                                .baseCandidateRemains(),
                        first
                                .baseMeaningful(),
                        first
                                .obstructionLevel(),
                        first
                                .baseStateRank(),
                        first
                                .structuralRank(),
                        first
                                .monthBranchInvolved(),
                        first
                                .cardinalIncluded(),

                        second
                                .baseCandidateRemains(),
                        second
                                .baseMeaningful(),
                        second
                                .obstructionLevel(),
                        second
                                .baseStateRank(),
                        second
                                .structuralRank(),
                        second
                                .monthBranchInvolved(),
                        second
                                .cardinalIncluded()
                );
    }

    /*
     * =================================================
     * 목표 오행 환경
     * =================================================
     */

    private TargetEnvironment targetEnvironment(

            ElementStrengthAnalysis strength,

            FiveElement targetElement

    ) {

        return new TargetEnvironment(
                strength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                targetElement
                        ),

                strength
                        .seasonalElementPowerAnalysis()
                        .seasonalRateOf(
                                targetElement
                        ),

                strength
                        .seasonalElementPowerAnalysis()
                        .monthBranchPresenceWeightOf(
                                targetElement
                        )
        );
    }

    private AnnualDecadeLuckRelationReference relation(

            AnnualDecadeLuckRelationLayer layer,

            String code,

            String name

    ) {

        return new AnnualDecadeLuckRelationReference(
                layer,
                code,
                name
        );
    }

    /*
     * =================================================
     * 내부 Value
     * =================================================
     */

    private record TargetEnvironment(

            int shareBasisPoints,

            int seasonalRate,

            int monthBranchPresenceWeight

    ) {
    }

    private record DynamicBranchCandidate(

            String transformationCode,

            String transformationName,

            FiveElement targetElement,

            int baseStateRank,

            boolean baseMeaningful,

            boolean baseCandidateRemains,

            boolean monthBranchInvolved,

            boolean cardinalIncluded,

            int structuralRank,

            EarthlyBranchCombinationObstructionLevel
            obstructionLevel

    ) {

        private DynamicBranchCandidate {

            Objects.requireNonNull(
                    transformationCode
            );

            Objects.requireNonNull(
                    transformationName
            );

            Objects.requireNonNull(
                    targetElement
            );

            Objects.requireNonNull(
                    obstructionLevel
            );
        }

        private DynamicBranchCandidate
        withObstructionLevel(
                EarthlyBranchCombinationObstructionLevel level
        ) {

            return new DynamicBranchCandidate(
                    transformationCode,
                    transformationName,
                    targetElement,
                    baseStateRank,
                    baseMeaningful,
                    baseCandidateRemains,
                    monthBranchInvolved,
                    cardinalIncluded,
                    structuralRank,
                    Objects.requireNonNull(
                            level
                    )
            );
        }
    }
}