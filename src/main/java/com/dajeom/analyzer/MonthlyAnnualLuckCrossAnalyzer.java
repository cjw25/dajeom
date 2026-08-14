package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.MonthlyAnnualLuckCrossResolution;
import com.dajeom.model.MonthlyAnnualLuckRelationReference;
import com.dajeom.model.MonthlyAnnualLuckTransformationReference;
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
import com.dajeom.type.MonthlyAnnualLuckRelationLayer;
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
 * 정밀화 15차-6.
 *
 * 세운 × 월운 관계 및 합화 Analyzer.
 *
 * =================================================
 *
 * 범위:
 *
 * 세운 천간
 * ↕
 * 월운 천간
 *
 * 세운 지지
 * ↕
 * 월운 지지
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 1.
 * 세운과 월운은 모두 외부 시간축이다.
 *
 * 2.
 * YEAR / MONTH / DAY / HOUR 같은
 * 원국 PalacePosition을 부여하지 않는다.
 *
 * 3.
 * 따라서 이 Pair 분석에서는:
 *
 * adjacent = false
 * monthBranchInvolved = false
 *
 * 로 평가한다.
 *
 * 4.
 * 원국 또는 대운 글자를 이 Analyzer에 추가하지 않는다.
 *
 * 원국 + 세운 + 월운
 * 대운 + 세운 + 월운
 * 원국 + 대운 + 세운 + 월운
 *
 * 같은 다층 Context는 15차-7의 책임이다.
 *
 * 5.
 * 관계 존재와 실제 합화 활성,
 * 원래 기능 감소를 분리한다.
 *
 * 6.
 * 여러 관계가 동시에 존재하면 모두 보존한다.
 *
 * 7.
 * 기존 8차 관계 자료와
 * 기존 9차 합화 Rule을 그대로 재사용한다.
 */
@Component
public class MonthlyAnnualLuckCrossAnalyzer {

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

    public MonthlyAnnualLuckCrossAnalyzer(

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
     * 공식 15차-6 Cross 분석 API
     * =================================================
     */
    public MonthlyAnnualLuckCrossResolution analyze(

            ElementStrengthAnalysis elementStrengthAnalysis,

            AnnualLuckPeriod annualPeriod,

            MonthlyLuckPeriod monthlyPeriod

    ) {

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "15-6 합화 판단에는 ElementStrengthAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualPeriod,
                "15-6 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyPeriod,
                "15-6 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        /*
         * 16차 Period Identity 정책까지 연결되는
         * 중요한 검증이다.
         *
         * 같은 연도라는 값 비교가 아니라
         * MonthlyLuckPeriod가 실제로 참조하고 있는
         * 동일 AnnualLuckPeriod 객체여야 한다.
         */
        if (
                monthlyPeriod.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-6 분석의 MonthlyLuckPeriod가 현재 AnnualLuckPeriod에 속하지 않습니다."
            );
        }

        HeavenlyStem annualStem =
                annualPeriod
                        .stem();

        EarthlyBranch annualBranch =
                annualPeriod
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

        List<MonthlyAnnualLuckRelationReference>
                relations =
                new ArrayList<>();

        buildStemRelations(
                annualStem,
                monthlyStem,
                relations
        );

        buildBranchRelations(
                annualBranch,
                monthlyBranch,
                relations
        );

        /*
         * =================================================
         * 합화 / 기능 변화
         * =================================================
         */

        List<MonthlyAnnualLuckTransformationReference>
                transformations =
                new ArrayList<>();

        buildHeavenlyTransformation(
                elementStrengthAnalysis,
                annualStem,
                monthlyStem
        ).ifPresent(
                transformations::add
        );

        transformations.addAll(
                buildEarthlyTransformations(
                        elementStrengthAnalysis,
                        annualBranch,
                        monthlyBranch
                )
        );

        /*
         * =================================================
         * 결정적 순서
         * =================================================
         */

        relations.sort(
                Comparator
                        .comparingInt(
                                (
                                        MonthlyAnnualLuckRelationReference reference
                                ) ->
                                        reference
                                                .layer()
                                                .ordinal()
                        )
                        .thenComparing(
                                MonthlyAnnualLuckRelationReference
                                        ::relationCode
                        )
        );

        transformations.sort(
                Comparator.comparing(
                        MonthlyAnnualLuckTransformationReference
                                ::transformationCode
                )
        );

        return new MonthlyAnnualLuckCrossResolution(
                annualPeriod,
                monthlyPeriod,
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

            HeavenlyStem annualStem,

            HeavenlyStem monthlyStem,

            List<MonthlyAnnualLuckRelationReference> results

    ) {

        /*
         * 합 존재.
         *
         * 이것만으로 실제 합화 활성로 처리하지 않는다.
         */
        StemRelationTable
                .findCombinationElement(
                        annualStem,
                        monthlyStem
                )
                .ifPresent(
                        ignored ->
                                results.add(
                                        relation(
                                                MonthlyAnnualLuckRelationLayer
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
                                annualStem,
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
                            MonthlyAnnualLuckRelationLayer
                                    .HEAVENLY_STEM,
                            "CLASH",
                            "천간충"
                    )
            );

            return;
        }

        /*
         * 극 방향 보존:
         *
         * 세운 -> 월운
         * 월운 -> 세운
         */

        if (
                heavenlyStemControlRule
                        .controls(
                                annualStem,
                                monthlyStem
                        )
        ) {

            results.add(
                    relation(
                            MonthlyAnnualLuckRelationLayer
                                    .HEAVENLY_STEM,
                            "CONTROL_ANNUAL_TO_MONTHLY",
                            "세운 천간이 월운 천간을 극함"
                    )
            );

            return;
        }

        if (
                heavenlyStemControlRule
                        .controls(
                                monthlyStem,
                                annualStem
                        )
        ) {

            results.add(
                    relation(
                            MonthlyAnnualLuckRelationLayer
                                    .HEAVENLY_STEM,
                            "CONTROL_MONTHLY_TO_ANNUAL",
                            "월운 천간이 세운 천간을 극함"
                    )
            );
        }
    }

    /*
     * =================================================
     * 지지 관계
     * =================================================
     *
     * 같은 Pair에서 여러 관계가 겹쳐도
     * 하나로 축약하지 않는다.
     */

    private void buildBranchRelations(

            EarthlyBranch annualBranch,

            EarthlyBranch monthlyBranch,

            List<MonthlyAnnualLuckRelationReference> results

    ) {

        if (
                BranchRelationTable
                        .findSixCombinationElement(
                                annualBranch,
                                monthlyBranch
                        )
                        .isPresent()
        ) {

            results.add(
                    relation(
                            MonthlyAnnualLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "SIX_COMBINATION",
                            "육합"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isClash(
                                annualBranch,
                                monthlyBranch
                        )
        ) {

            results.add(
                    relation(
                            MonthlyAnnualLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "CLASH",
                            "충"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isPairPunishment(
                                annualBranch,
                                monthlyBranch
                        )
        ) {

            results.add(
                    relation(
                            MonthlyAnnualLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "PUNISHMENT",
                            "형"
                    )
            );
        }

        if (
                annualBranch
                        == monthlyBranch

                        && BranchRelationTable
                        .isSelfPunishmentBranch(
                                annualBranch
                        )
        ) {

            results.add(
                    relation(
                            MonthlyAnnualLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "SELF_PUNISHMENT",
                            "자형"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isBreak(
                                annualBranch,
                                monthlyBranch
                        )
        ) {

            results.add(
                    relation(
                            MonthlyAnnualLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "BREAK",
                            "파"
                    )
            );
        }

        if (
                BranchRelationTable
                        .isHarm(
                                annualBranch,
                                monthlyBranch
                        )
        ) {

            results.add(
                    relation(
                            MonthlyAnnualLuckRelationLayer
                                    .EARTHLY_BRANCH,
                            "HARM",
                            "해"
                    )
            );
        }

        earthlyBranchThreeCombinationRule
                .findPartialCombinationType(
                        annualBranch,
                        monthlyBranch
                )
                .ifPresent(
                        type -> {

                            EarthlyBranchThreeCombinationForm form =
                                    earthlyBranchThreeCombinationRule
                                            .determinePairForm(
                                                    type,
                                                    annualBranch,
                                                    monthlyBranch
                                            );

                            results.add(
                                    relation(
                                            MonthlyAnnualLuckRelationLayer
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
                        annualBranch,
                        monthlyBranch
                )
                .ifPresent(
                        type -> {

                            EarthlyBranchDirectionalCombinationForm form =
                                    earthlyBranchDirectionalCombinationRule
                                            .determinePairForm(
                                                    type,
                                                    annualBranch,
                                                    monthlyBranch
                                            );

                            results.add(
                                    relation(
                                            MonthlyAnnualLuckRelationLayer
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
     * 천간합 Transformation
     * =================================================
     *
     * 세운/월운은 외부축이므로:
     *
     * adjacent = false
     *
     * 이 Pair 자체에는 원국/대운 경쟁을
     * 추가하지 않는다.
     */
    private Optional<MonthlyAnnualLuckTransformationReference>
    buildHeavenlyTransformation(

            ElementStrengthAnalysis strength,

            HeavenlyStem annualStem,

            HeavenlyStem monthlyStem

    ) {

        Optional<FiveElement> target =
                StemRelationTable
                        .findCombinationElement(
                                annualStem,
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
         * 원국/대운 천간을 15-6 Pair 안에
         * 억지로 끼워 넣지 않는다.
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
                new MonthlyAnnualLuckTransformationReference(
                        "HEAVENLY_STEM_COMBINATION:"
                                + target
                                .get()
                                .name(),

                        "세운·월운 천간합 합화",

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
     * 세운 + 월운 두 글자만 있으므로:
     *
     * - 육합
     * - 부분 삼합
     * - 부분 방합
     *
     * 후보만 생성한다.
     *
     * 완전 삼합/완전 방합은
     * 세 번째 실제 글자가 필요한 구조이므로
     * 15차-7 다층 Context에서 처리한다.
     */

    private List<MonthlyAnnualLuckTransformationReference>
    buildEarthlyTransformations(

            ElementStrengthAnalysis strength,

            EarthlyBranch annualBranch,

            EarthlyBranch monthlyBranch

    ) {

        List<DynamicBranchCandidate> candidates =
                new ArrayList<>();

        buildSixCombinationCandidate(
                strength,
                annualBranch,
                monthlyBranch
        ).ifPresent(
                candidates::add
        );

        buildThreeCombinationCandidate(
                strength,
                annualBranch,
                monthlyBranch
        ).ifPresent(
                candidates::add
        );

        buildDirectionalCombinationCandidate(
                strength,
                annualBranch,
                monthlyBranch
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
                        annualBranch,
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

        List<MonthlyAnnualLuckTransformationReference>
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
                    new MonthlyAnnualLuckTransformationReference(
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
                        MonthlyAnnualLuckTransformationReference
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

            EarthlyBranch annualBranch,

            EarthlyBranch monthlyBranch

    ) {

        Optional<FiveElement> target =
                BranchRelationTable
                        .findSixCombinationElement(
                                annualBranch,
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

                        "세운·월운 육합 합화",

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

            EarthlyBranch annualBranch,

            EarthlyBranch monthlyBranch

    ) {

        Optional<EarthlyBranchThreeCombinationType> type =
                earthlyBranchThreeCombinationRule
                        .findPartialCombinationType(
                                annualBranch,
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
                                annualBranch,
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
                                ? "세운·월운 반합 합화"
                                : "세운·월운 삼합 부분조합",

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

            EarthlyBranch annualBranch,

            EarthlyBranch monthlyBranch

    ) {

        Optional<EarthlyBranchDirectionalCombinationType> type =
                earthlyBranchDirectionalCombinationRule
                        .findPartialCombinationType(
                                annualBranch,
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
                                annualBranch,
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
                                ? "세운·월운 왕지 포함 부분 방합"
                                : "세운·월운 부분 방합",

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
     * 동일 Pair 내부 방해
     * =================================================
     */

    private EarthlyBranchCombinationObstructionLevel
    determinePairObstructionLevel(

            EarthlyBranch annualBranch,

            EarthlyBranch monthlyBranch

    ) {

        Set<EarthlyBranchRelationKind> kinds =
                pairObstructionKinds(
                        annualBranch,
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
         * 세운 지지와 월운 지지 둘 다
         * 현재 후보 Pair 내부 Member다.
         */
        boolean internalObstruction =
                true;

        boolean internalClash =
                clash;

        boolean internalMultipleKinds =
                kinds.size() >= 2;

        /*
         * 외부 운축 Pair이므로
         * 원국 궁위 기반 속성을 부여하지 않는다.
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

            EarthlyBranch annualBranch,

            EarthlyBranch monthlyBranch

    ) {

        EnumSet<EarthlyBranchRelationKind> result =
                EnumSet.noneOf(
                        EarthlyBranchRelationKind.class
                );

        if (
                BranchRelationTable
                        .isClash(
                                annualBranch,
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
                                annualBranch,
                                monthlyBranch
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind
                            .PUNISHMENT
            );
        }

        if (
                annualBranch
                        == monthlyBranch

                        && BranchRelationTable
                        .isSelfPunishmentBranch(
                                annualBranch
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
                                annualBranch,
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
                                annualBranch,
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
     * 합화 목표 오행의 원국 환경
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

    private MonthlyAnnualLuckRelationReference relation(

            MonthlyAnnualLuckRelationLayer layer,

            String code,

            String name

    ) {

        return new MonthlyAnnualLuckRelationReference(
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