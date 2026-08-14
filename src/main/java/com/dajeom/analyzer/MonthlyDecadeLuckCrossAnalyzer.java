package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.MonthlyDecadeLuckCrossResolution;
import com.dajeom.model.MonthlyDecadeLuckRelationReference;
import com.dajeom.model.MonthlyDecadeLuckTransformationReference;
import com.dajeom.model.MonthlyLuckPeriod;
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
import com.dajeom.type.MonthlyDecadeLuckRelationLayer;
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
 * 정밀화 15차-5.
 *
 * 대운 × 월운 관계 및 합화 Analyzer.
 *
 * =================================================
 *
 * 범위:
 *
 * 대운 천간
 * ↕
 * 월운 천간
 *
 * 대운 지지
 * ↕
 * 월운 지지
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 1.
 * 대운과 월운은 모두 외부 시간축이다.
 *
 * 2.
 * YEAR / MONTH / DAY / HOUR 같은
 * 원국 PalacePosition을 부여하지 않는다.
 *
 * 3.
 * 따라서 대운 × 월운 자체 분석에서는:
 *
 * adjacent = false
 * monthBranchInvolved = false
 *
 * 로 평가한다.
 *
 * 4.
 * 원국 또는 세운 글자를 이 분석에 추가하지 않는다.
 *
 * 원국 + 대운 + 세운 + 월운 복합 Context는
 * 15차-7의 책임이다.
 *
 * 5.
 * 관계 존재와 실제 합화 활성은 분리한다.
 *
 * 6.
 * 동일 Pair에 여러 관계가 동시에 존재할 수 있으며
 * 이를 하나로 축약하지 않는다.
 *
 * 7.
 * 기존 8차 관계표와 9차 합화 Rule을 재사용한다.
 */
@Component
public class MonthlyDecadeLuckCrossAnalyzer {

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

    public MonthlyDecadeLuckCrossAnalyzer(

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
                        heavenlyStemControlRule,
                        "HeavenlyStemControlRule이 필요합니다."
                );

        this.earthlyBranchThreeCombinationRule =
                Objects.requireNonNull(
                        earthlyBranchThreeCombinationRule,
                        "EarthlyBranchThreeCombinationRule이 필요합니다."
                );

        this.earthlyBranchDirectionalCombinationRule =
                Objects.requireNonNull(
                        earthlyBranchDirectionalCombinationRule,
                        "EarthlyBranchDirectionalCombinationRule이 필요합니다."
                );

        this.heavenlyStemCombinationCandidateRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationCandidateRule,
                        "HeavenlyStemCombinationCandidateRule이 필요합니다."
                );

        this.heavenlyStemCombinationCompetitionRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationCompetitionRule,
                        "HeavenlyStemCombinationCompetitionRule이 필요합니다."
                );

        this.heavenlyStemCombinationTransformationRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationTransformationRule,
                        "HeavenlyStemCombinationTransformationRule이 필요합니다."
                );

        this.earthlyBranchSixCombinationTransformationRule =
                Objects.requireNonNull(
                        earthlyBranchSixCombinationTransformationRule,
                        "EarthlyBranchSixCombinationTransformationRule이 필요합니다."
                );

        this.earthlyBranchThreeCombinationFormationRule =
                Objects.requireNonNull(
                        earthlyBranchThreeCombinationFormationRule,
                        "EarthlyBranchThreeCombinationFormationRule이 필요합니다."
                );

        this.earthlyBranchDirectionalCombinationFormationRule =
                Objects.requireNonNull(
                        earthlyBranchDirectionalCombinationFormationRule,
                        "EarthlyBranchDirectionalCombinationFormationRule이 필요합니다."
                );

        this.earthlyBranchCombinationObstructionRule =
                Objects.requireNonNull(
                        earthlyBranchCombinationObstructionRule,
                        "EarthlyBranchCombinationObstructionRule이 필요합니다."
                );

        this.earthlyBranchCombinationIntegrationRule =
                Objects.requireNonNull(
                        earthlyBranchCombinationIntegrationRule,
                        "EarthlyBranchCombinationIntegrationRule이 필요합니다."
                );

        this.combinationTransformationEffectRule =
                Objects.requireNonNull(
                        combinationTransformationEffectRule,
                        "CombinationTransformationEffectRule이 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15차-5 Cross 분석 API
     * =================================================
     */
    public MonthlyDecadeLuckCrossResolution analyze(

            ElementStrengthAnalysis elementStrengthAnalysis,

            MonthlyLuckPeriod monthlyPeriod,

            DecadeLuckPeriod decadePeriod

    ) {

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "15-5 합화 판단에는 원국 ElementStrengthAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyPeriod,
                "15-5 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                decadePeriod,
                "15-5 분석에는 DecadeLuckPeriod가 필요합니다."
        );

        HeavenlyStem decadeStem =
                decadePeriod
                        .pillar()
                        .stem();

        EarthlyBranch decadeBranch =
                decadePeriod
                        .pillar()
                        .branch();

        HeavenlyStem monthlyStem =
                monthlyPeriod
                        .stem();

        EarthlyBranch monthlyBranch =
                monthlyPeriod
                        .branch();

        /*
         * =================================================
         * 관계 존재
         * =================================================
         */

        List<MonthlyDecadeLuckRelationReference>
                relations =
                new ArrayList<>();

        buildStemRelations(
                decadeStem,
                monthlyStem,
                relations
        );

        buildBranchRelations(
                decadeBranch,
                monthlyBranch,
                relations
        );

        /*
         * =================================================
         * 합화 / 기능 변화
         * =================================================
         */

        List<MonthlyDecadeLuckTransformationReference>
                transformations =
                new ArrayList<>();

        buildHeavenlyTransformation(
                elementStrengthAnalysis,
                decadeStem,
                monthlyStem
        ).ifPresent(
                transformations::add
        );

        transformations.addAll(
                buildEarthlyTransformations(
                        elementStrengthAnalysis,
                        decadeBranch,
                        monthlyBranch
                )
        );

        /*
         * 결정적 순서.
         */
        relations.sort(
                Comparator
                        .comparingInt(
                                (
                                        MonthlyDecadeLuckRelationReference reference
                                ) ->
                                        reference
                                                .layer()
                                                .ordinal()
                        )
                        .thenComparing(
                                MonthlyDecadeLuckRelationReference
                                        ::relationCode
                        )
        );

        transformations.sort(
                Comparator.comparing(
                        MonthlyDecadeLuckTransformationReference
                                ::transformationCode
                )
        );

        return new MonthlyDecadeLuckCrossResolution(
                monthlyPeriod,
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

            HeavenlyStem monthlyStem,

            List<MonthlyDecadeLuckRelationReference> results

    ) {

        StemRelationTable
                .findCombinationElement(
                        decadeStem,
                        monthlyStem
                )
                .ifPresent(
                        ignored ->
                                results.add(
                                        relation(
                                                heavenlyStemLayer(),
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
                                monthlyStem
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
                            heavenlyStemLayer(),
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
                                monthlyStem
                        )
        ) {

            results.add(
                    relation(
                            heavenlyStemLayer(),
                            "CONTROL_DECADE_TO_MONTHLY",
                            "대운 천간이 월운 천간을 극함"
                    )
            );

            return;
        }

        if (
                heavenlyStemControlRule
                        .controls(
                                monthlyStem,
                                decadeStem
                        )
        ) {

            results.add(
                    relation(
                            heavenlyStemLayer(),
                            "CONTROL_MONTHLY_TO_DECADE",
                            "월운 천간이 대운 천간을 극함"
                    )
            );
        }
    }

    /*
     * =================================================
     * 지지 관계
     * =================================================
     *
     * 같은 Pair에서:
     *
     * 육합
     * 충
     * 형
     * 파
     * 해
     * 삼합 부분조합
     * 방합 부분조합
     *
     * 등이 중첩되면 모두 보존한다.
     */

    private void buildBranchRelations(

            EarthlyBranch decadeBranch,

            EarthlyBranch monthlyBranch,

            List<MonthlyDecadeLuckRelationReference> results

    ) {

        if (
                BranchRelationTable
                        .findSixCombinationElement(
                                decadeBranch,
                                monthlyBranch
                        )
                        .isPresent()
        ) {

            results.add(
                    relation(
                            earthlyBranchLayer(),
                            "SIX_COMBINATION",
                            "육합"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isClash(
                                decadeBranch,
                                monthlyBranch
                        )
        ) {

            results.add(
                    relation(
                            earthlyBranchLayer(),
                            "CLASH",
                            "충"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isPairPunishment(
                                decadeBranch,
                                monthlyBranch
                        )
        ) {

            results.add(
                    relation(
                            earthlyBranchLayer(),
                            "PUNISHMENT",
                            "형"
                    )
            );
        }

        if (
                decadeBranch
                        == monthlyBranch

                        && BranchRelationTable
                        .isSelfPunishmentBranch(
                                decadeBranch
                        )
        ) {

            results.add(
                    relation(
                            earthlyBranchLayer(),
                            "SELF_PUNISHMENT",
                            "자형"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isBreak(
                                decadeBranch,
                                monthlyBranch
                        )
        ) {

            results.add(
                    relation(
                            earthlyBranchLayer(),
                            "BREAK",
                            "파"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isHarm(
                                decadeBranch,
                                monthlyBranch
                        )
        ) {

            results.add(
                    relation(
                            earthlyBranchLayer(),
                            "HARM",
                            "해"
                    )
            );
        }

        earthlyBranchThreeCombinationRule
                .findPartialCombinationType(
                        decadeBranch,
                        monthlyBranch
                )
                .ifPresent(
                        type -> {

                            EarthlyBranchThreeCombinationForm form =
                                    earthlyBranchThreeCombinationRule
                                            .determinePairForm(
                                                    type,
                                                    decadeBranch,
                                                    monthlyBranch
                                            );

                            results.add(
                                    relation(
                                            earthlyBranchLayer(),

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
                        monthlyBranch
                )
                .ifPresent(
                        type -> {

                            EarthlyBranchDirectionalCombinationForm form =
                                    earthlyBranchDirectionalCombinationRule
                                            .determinePairForm(
                                                    type,
                                                    decadeBranch,
                                                    monthlyBranch
                                            );

                            results.add(
                                    relation(
                                            earthlyBranchLayer(),

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
     * 천간합 Transformation
     * =================================================
     *
     * 대운과 월운 모두 외부 시간축이다.
     *
     * 따라서:
     *
     * adjacent = false
     *
     * 그리고 이 Pair 분석 자체에서는
     * 원국 또는 세운의 추가 경쟁/방해를 넣지 않는다.
     */
    private Optional<MonthlyDecadeLuckTransformationReference>
    buildHeavenlyTransformation(

            ElementStrengthAnalysis strength,

            HeavenlyStem decadeStem,

            HeavenlyStem monthlyStem

    ) {

        Optional<FiveElement> target =
                StemRelationTable
                        .findCombinationElement(
                                decadeStem,
                                monthlyStem
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

        /*
         * 대운 × 월운 Pair 내부에는
         * 세 번째 천간 경쟁을 임의 생성하지 않는다.
         */
        HeavenlyStemCombinationCompetitionState competitionState =
                heavenlyStemCombinationCompetitionRule
                        .determineState(
                                false,
                                false,
                                false
                        );

        HeavenlyStemCombinationTransformationState
                transformationState =
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
                new MonthlyDecadeLuckTransformationReference(
                        "HEAVENLY_STEM_COMBINATION:"
                                + target
                                .get()
                                .name(),

                        "대운·월운 천간합 합화",

                        target
                                .get()
                                .getKoreanName(),

                        targetActivationState
                                .active(),

                        originalFunctionState
                                .reduced()
                )
        );
    }

    /*
     * =================================================
     * 지지 Transformation
     * =================================================
     *
     * 대운 + 월운은 두 글자이므로:
     *
     * - 육합
     * - 부분 삼합
     * - 부분 방합
     *
     * 후보만 여기서 생성한다.
     *
     * 완전 삼합/방합처럼 세 번째 글자가 필요한 구조는
     * 15-7 다층 Context에서 판단해야 한다.
     */
    private List<MonthlyDecadeLuckTransformationReference>
    buildEarthlyTransformations(

            ElementStrengthAnalysis strength,

            EarthlyBranch decadeBranch,

            EarthlyBranch monthlyBranch

    ) {

        List<DynamicBranchCandidate> candidates =
                new ArrayList<>();

        buildSixCombinationCandidate(
                strength,
                decadeBranch,
                monthlyBranch
        ).ifPresent(
                candidates::add
        );

        buildThreeCombinationCandidate(
                strength,
                decadeBranch,
                monthlyBranch
        ).ifPresent(
                candidates::add
        );

        buildDirectionalCombinationCandidate(
                strength,
                decadeBranch,
                monthlyBranch
        ).ifPresent(
                candidates::add
        );

        if (
                candidates.isEmpty()
        ) {

            return List.of();
        }

        /*
         * 같은 대운·월운 Pair에 겹치는
         * 충/형/파/해 방해를 기존 9차 Rule로 평가한다.
         */
        EarthlyBranchCombinationObstructionLevel obstructionLevel =
                determinePairObstructionLevel(
                        decadeBranch,
                        monthlyBranch
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

        List<MonthlyDecadeLuckTransformationReference>
                results =
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

            /*
             * 동일 대운·월운 Pair에서 만들어진
             * 다른 합화 후보와의 우선순위를 보존한다.
             */
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
                        candidate
                                .targetElement()
                                == other
                                .targetElement()
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
                    new MonthlyDecadeLuckTransformationReference(
                            candidate
                                    .transformationCode(),

                            candidate
                                    .transformationName(),

                            candidate
                                    .targetElement()
                                    .getKoreanName(),

                            targetActivationState
                                    .active(),

                            originalFunctionState
                                    .reduced()
                    )
            );
        }

        results.sort(
                Comparator.comparing(
                        MonthlyDecadeLuckTransformationReference
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

            EarthlyBranch monthlyBranch

    ) {

        Optional<FiveElement> target =
                BranchRelationTable
                        .findSixCombinationElement(
                                decadeBranch,
                                monthlyBranch
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
                                + target
                                .get()
                                .name(),

                        "대운·월운 육합 합화",

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

            EarthlyBranch monthlyBranch

    ) {

        Optional<EarthlyBranchThreeCombinationType> type =
                earthlyBranchThreeCombinationRule
                        .findPartialCombinationType(
                                decadeBranch,
                                monthlyBranch
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
                                monthlyBranch
                        );

        FiveElement target =
                type
                        .get()
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
                                ? "대운·월운 반합 합화"
                                : "대운·월운 삼합 부분조합",

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

            EarthlyBranch monthlyBranch

    ) {

        Optional<EarthlyBranchDirectionalCombinationType> type =
                earthlyBranchDirectionalCombinationRule
                        .findPartialCombinationType(
                                decadeBranch,
                                monthlyBranch
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
                                monthlyBranch
                        );

        FiveElement target =
                type
                        .get()
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
                                ? "대운·월운 왕지 포함 부분 방합"
                                : "대운·월운 부분 방합",

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
     * Pair 내부 방해
     * =================================================
     *
     * 같은 대운 지지 × 월운 지지 Pair에:
     *
     * 충
     * 형
     * 파
     * 해
     *
     * 가 함께 존재하면 기존 9차
     * Obstruction Rule에 전달한다.
     */
    private EarthlyBranchCombinationObstructionLevel
    determinePairObstructionLevel(

            EarthlyBranch decadeBranch,

            EarthlyBranch monthlyBranch

    ) {

        Set<EarthlyBranchRelationKind> kinds =
                pairObstructionKinds(
                        decadeBranch,
                        monthlyBranch
                );

        if (
                kinds.isEmpty()
        ) {

            return EarthlyBranchCombinationObstructionLevel
                    .NONE;
        }

        boolean clash =
                kinds.contains(
                        EarthlyBranchRelationKind
                                .CLASH
                );

        boolean punishment =
                kinds.contains(
                        EarthlyBranchRelationKind
                                .PUNISHMENT
                );

        /*
         * 두 외부 글자가 현재 후보의 전체 Pair다.
         */
        boolean internalObstruction =
                true;

        boolean internalClash =
                clash;

        boolean internalMultipleKinds =
                kinds.size()
                        >= 2;

        /*
         * Palace 기반 판단을 외부축에 부여하지 않는다.
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

    /*
     * =================================================
     * Pair 방해 관계 종류
     * =================================================
     */

    private Set<EarthlyBranchRelationKind>
    pairObstructionKinds(

            EarthlyBranch decadeBranch,

            EarthlyBranch monthlyBranch

    ) {

        EnumSet<EarthlyBranchRelationKind> result =
                EnumSet.noneOf(
                        EarthlyBranchRelationKind.class
                );

        if (
                BranchRelationTable
                        .isClash(
                                decadeBranch,
                                monthlyBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind
                            .CLASH
            );
        }

        if (
                BranchRelationTable
                        .isPairPunishment(
                                decadeBranch,
                                monthlyBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind
                            .PUNISHMENT
            );
        }

        if (
                decadeBranch
                        == monthlyBranch

                        && BranchRelationTable
                        .isSelfPunishmentBranch(
                                decadeBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind
                            .PUNISHMENT
            );
        }

        if (
                BranchRelationTable
                        .isBreak(
                                decadeBranch,
                                monthlyBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind
                            .BREAK
            );
        }

        if (
                BranchRelationTable
                        .isHarm(
                                decadeBranch,
                                monthlyBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind
                            .HARM
            );
        }

        return Set.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 후보 우선순위
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
     *
     * 실제 합화 판정의 환경 근거는
     * 현재 요청에서 이미 계산된
     * 3차 ElementStrengthAnalysis를 사용한다.
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

    /*
     * =================================================
     * Relation 생성
     * =================================================
     */

    private MonthlyDecadeLuckRelationReference relation(

            MonthlyDecadeLuckRelationLayer layer,

            String code,

            String name

    ) {

        return new MonthlyDecadeLuckRelationReference(
                layer,
                code,
                name
        );
    }

    /*
     * =================================================
     * Relation Layer
     * =================================================
     *
     * enum 상수 이름을 다른 계층에서
     * 임의 추정하지 않는다.
     *
     * 현재 enum이 보장하는 의미 accessor:
     *
     * heavenlyStem()
     * earthlyBranch()
     *
     * 로 실제 Layer를 조회한다.
     */

    private MonthlyDecadeLuckRelationLayer heavenlyStemLayer() {

        for (
                MonthlyDecadeLuckRelationLayer layer
                : MonthlyDecadeLuckRelationLayer.values()
        ) {

            if (
                    layer.heavenlyStem()
            ) {

                return layer;
            }
        }

        throw new IllegalStateException(
                "MonthlyDecadeLuckRelationLayer에 천간 Layer가 없습니다."
        );
    }

    private MonthlyDecadeLuckRelationLayer earthlyBranchLayer() {

        for (
                MonthlyDecadeLuckRelationLayer layer
                : MonthlyDecadeLuckRelationLayer.values()
        ) {

            if (
                    layer.earthlyBranch()
            ) {

                return layer;
            }
        }

        throw new IllegalStateException(
                "MonthlyDecadeLuckRelationLayer에 지지 Layer가 없습니다."
        );
    }

    /*
     * =================================================
     * 내부 값
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
                    transformationCode,
                    "DynamicBranchCandidate에는 transformationCode가 필요합니다."
            );

            Objects.requireNonNull(
                    transformationName,
                    "DynamicBranchCandidate에는 transformationName이 필요합니다."
            );

            Objects.requireNonNull(
                    targetElement,
                    "DynamicBranchCandidate에는 목표 오행이 필요합니다."
            );

            Objects.requireNonNull(
                    obstructionLevel,
                    "DynamicBranchCandidate에는 방해 수준이 필요합니다."
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
                            level,
                            "방해 수준이 필요합니다."
                    )
            );
        }
    }
}