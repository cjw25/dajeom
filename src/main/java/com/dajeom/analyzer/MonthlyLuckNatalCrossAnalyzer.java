package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.EarthlyBranchCombinationObstructionEvidence;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.HeavenlyStemCombinationCandidate;
import com.dajeom.model.MonthlyLuckNatalCrossResolution;
import com.dajeom.model.MonthlyLuckNatalRelationReference;
import com.dajeom.model.MonthlyLuckNatalTransformationReference;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.RelationAnalysis;
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
import com.dajeom.type.EarthlyBranchCombinationCandidateKind;
import com.dajeom.type.EarthlyBranchCombinationIntegrationRole;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import com.dajeom.type.EarthlyBranchDirectionalCombinationForm;
import com.dajeom.type.EarthlyBranchDirectionalCombinationFormationState;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchPosition;
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
import com.dajeom.type.MonthlyLuckNatalRelationLayer;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 15차-4.
 *
 * 원국 × 월운 관계 및 합화 정밀 분석.
 *
 * =================================================
 *
 * 핵심:
 *
 * 월운을 YEAR / MONTH / DAY / HOUR
 * 원국 궁위로 위장하지 않는다.
 *
 * 월운은 외부 시간축 Source이며,
 * 원국 PalacePosition은 원국 글자에만 부여한다.
 *
 * 따라서 원국 내부의:
 *
 * - 인접성
 * - 월지 포함 여부
 * - 궁위 분포
 *
 * 는 실제 원국 위치에 대해서만 판단한다.
 *
 * =================================================
 *
 * 관계 존재와:
 *
 * - 합화 후보
 * - 실제 Target 활성
 * - 원래 기능 감소
 *
 * 를 분리한다.
 *
 * =================================================
 *
 * 사용:
 *
 * 8차 관계 canonical source
 *
 * - StemRelationTable
 * - BranchRelationTable
 * - RelationAnalysis
 *
 * 9차 합화 Rule
 *
 * - HeavenlyStemCombinationCandidateRule
 * - HeavenlyStemCombinationCompetitionRule
 * - HeavenlyStemCombinationTransformationRule
 * - EarthlyBranchSixCombinationTransformationRule
 * - EarthlyBranchThreeCombinationFormationRule
 * - EarthlyBranchDirectionalCombinationFormationRule
 * - EarthlyBranchCombinationObstructionRule
 * - EarthlyBranchCombinationIntegrationRule
 * - CombinationTransformationEffectRule
 *
 * =================================================
 *
 * 이 Analyzer에서 하지 않는 것:
 *
 * - 새로운 합충형파해 표 작성
 * - 새로운 합화 공식 작성
 * - 월운을 원국 Palace로 변환
 * - 합 존재만으로 activated=true 처리
 * - 출생시간 미상 시 HOUR 생성
 * - 길흉 점수 계산
 */
@Component
public class MonthlyLuckNatalCrossAnalyzer {

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

    public MonthlyLuckNatalCrossAnalyzer(

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
     * 공식 15차-4 Cross 분석 API
     * =================================================
     */
    public MonthlyLuckNatalCrossResolution analyze(

            NatalChartAdvancedAnalysis advancedAnalysis,

            PalaceAnalysis natalAnalysis,

            MonthlyLuckPeriod monthlyLuckPeriod

    ) {

        validateInputs(
                advancedAnalysis,
                natalAnalysis,
                monthlyLuckPeriod
        );

        List<NatalStemSlot> natalStems =
                natalStemSlots(
                        natalAnalysis
                );

        List<NatalBranchSlot> natalBranches =
                natalBranchSlots(
                        natalAnalysis
                );

        HeavenlyStem monthlyStem =
                monthlyLuckPeriod
                        .stem();

        EarthlyBranch monthlyBranch =
                monthlyLuckPeriod
                        .branch();

        /*
         * =================================================
         * 관계 존재
         * =================================================
         */

        List<MonthlyLuckNatalRelationReference>
                relations =
                new ArrayList<>();

        buildStemRelations(
                natalStems,
                monthlyStem,
                relations
        );

        buildBranchPairRelations(
                natalBranches,
                monthlyBranch,
                relations
        );

        buildBranchGroupRelations(
                natalBranches,
                monthlyBranch,
                relations
        );

        /*
         * =================================================
         * 합화 / 기능 변화
         * =================================================
         */

        List<MonthlyLuckNatalTransformationReference>
                transformations =
                new ArrayList<>();

        transformations.addAll(
                buildHeavenlyTransformations(
                        advancedAnalysis,
                        natalStems,
                        monthlyStem
                )
        );

        transformations.addAll(
                buildEarthlyTransformations(
                        advancedAnalysis,
                        natalBranches,
                        monthlyBranch
                )
        );

        /*
         * =================================================
         * 결정적 정렬
         * =================================================
         */

        relations.sort(
                Comparator
                        .comparingInt(
                                (
                                        MonthlyLuckNatalRelationReference reference
                                ) ->
                                        reference
                                                .layer()
                                                .ordinal()
                        )
                        .thenComparing(
                                (
                                        MonthlyLuckNatalRelationReference reference
                                ) ->
                                        positionKey(
                                                reference
                                                        .natalPositions()
                                        )
                        )
                        .thenComparing(
                                MonthlyLuckNatalRelationReference
                                        ::relationCode
                        )
        );

        transformations.sort(
                Comparator
                        .comparing(
                                (
                                        MonthlyLuckNatalTransformationReference reference
                                ) ->
                                        positionKey(
                                                reference
                                                        .natalPositions()
                                        )
                        )
                        .thenComparing(
                                MonthlyLuckNatalTransformationReference
                                        ::transformationCode
                        )
        );

        return new MonthlyLuckNatalCrossResolution(
                monthlyLuckPeriod,
                relations,
                transformations
        );
    }

    /*
     * =================================================
     * 입력 검증
     * =================================================
     */

    private void validateInputs(

            NatalChartAdvancedAnalysis advancedAnalysis,

            PalaceAnalysis natalAnalysis,

            MonthlyLuckPeriod monthlyLuckPeriod

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "15-4에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "15-4에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckPeriod,
                "15-4에는 MonthlyLuckPeriod가 필요합니다."
        );

        if (
                advancedAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
                        != natalAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "3차 원국 분석과 12차 PalaceAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                advancedAnalysis.hasHourPillar()
                        != natalAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "원국 고급 분석과 PalaceAnalysis의 completeChart 상태가 일치하지 않습니다."
            );
        }

        CombinationTransformationAnalysis transformationAnalysis =
                advancedAnalysis
                        .combinationTransformationAnalysis();

        if (
                transformationAnalysis
                        .relationAnalysis()
                        != advancedAnalysis
                        .relationAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "9차 CombinationTransformationAnalysis가 현재 8차 RelationAnalysis를 참조하지 않습니다."
            );
        }

        if (
                transformationAnalysis
                        .elementStrengthAnalysis()
                        != advancedAnalysis
                        .elementStrengthAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "9차 CombinationTransformationAnalysis가 현재 3차 ElementStrengthAnalysis를 참조하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 천간 관계
     * =================================================
     */

    private void buildStemRelations(

            List<NatalStemSlot> natalStems,

            HeavenlyStem monthlyStem,

            List<MonthlyLuckNatalRelationReference> results

    ) {

        for (
                NatalStemSlot natal
                : natalStems
        ) {

            /*
             * 천간합 존재.
             *
             * 여기서는 실제 합화 활성로 간주하지 않는다.
             */
            StemRelationTable
                    .findCombinationElement(
                            natal.stem(),
                            monthlyStem
                    )
                    .ifPresent(
                            ignored ->
                                    results.add(
                                            relation(
                                                    MonthlyLuckNatalRelationLayer
                                                            .HEAVENLY_STEM,

                                                    Set.of(
                                                            natal.position()
                                                    ),

                                                    "COMBINATION",

                                                    "천간합"
                                            )
                                    )
                    );

            Optional<HeavenlyStemControlRelationType>
                    controlType =
                    heavenlyStemControlRule
                            .findRelationType(
                                    natal.stem(),
                                    monthlyStem
                            );

            if (
                    controlType.isEmpty()
            ) {

                continue;
            }

            if (
                    controlType.get()
                            == HeavenlyStemControlRelationType.CLASH
            ) {

                results.add(
                        relation(
                                MonthlyLuckNatalRelationLayer
                                        .HEAVENLY_STEM,

                                Set.of(
                                        natal.position()
                                ),

                                "CLASH",

                                "천간충"
                        )
                );

            } else {

                results.add(
                        relation(
                                MonthlyLuckNatalRelationLayer
                                        .HEAVENLY_STEM,

                                Set.of(
                                        natal.position()
                                ),

                                "CONTROL_ONLY",

                                "천간 극제"
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 지지 1:1 관계
     * =================================================
     */

    private void buildBranchPairRelations(

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch monthlyBranch,

            List<MonthlyLuckNatalRelationReference> results

    ) {

        for (
                NatalBranchSlot natal
                : natalBranches
        ) {

            Set<PalacePosition> positions =
                    Set.of(
                            natal.position()
                    );

            if (
                    BranchRelationTable
                            .findSixCombinationElement(
                                    natal.branch(),
                                    monthlyBranch
                            )
                            .isPresent()
            ) {

                results.add(
                        relation(
                                MonthlyLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,

                                positions,

                                "SIX_COMBINATION",

                                "육합"
                        )
                );
            }

            if (
                    BranchRelationTable
                            .isClash(
                                    natal.branch(),
                                    monthlyBranch
                            )
            ) {

                results.add(
                        relation(
                                MonthlyLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,

                                positions,

                                "CLASH",

                                "충"
                        )
                );
            }

            if (
                    BranchRelationTable
                            .isPairPunishment(
                                    natal.branch(),
                                    monthlyBranch
                            )
            ) {

                results.add(
                        relation(
                                MonthlyLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,

                                positions,

                                "PUNISHMENT",

                                "형"
                        )
                );
            }

            if (
                    natal.branch()
                            == monthlyBranch

                            && BranchRelationTable
                            .isSelfPunishmentBranch(
                                    monthlyBranch
                            )
            ) {

                results.add(
                        relation(
                                MonthlyLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,

                                positions,

                                "SELF_PUNISHMENT",

                                "자형"
                        )
                );
            }

            if (
                    BranchRelationTable
                            .isHarm(
                                    natal.branch(),
                                    monthlyBranch
                            )
            ) {

                results.add(
                        relation(
                                MonthlyLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,

                                positions,

                                "HARM",

                                "해"
                        )
                );
            }

            if (
                    BranchRelationTable
                            .isBreak(
                                    natal.branch(),
                                    monthlyBranch
                            )
            ) {

                results.add(
                        relation(
                                MonthlyLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,

                                positions,

                                "BREAK",

                                "파"
                        )
                );
            }

            earthlyBranchThreeCombinationRule
                    .findPartialCombinationType(
                            natal.branch(),
                            monthlyBranch
                    )
                    .ifPresent(
                            type -> {

                                EarthlyBranchThreeCombinationForm form =
                                        earthlyBranchThreeCombinationRule
                                                .determinePairForm(
                                                        type,
                                                        natal.branch(),
                                                        monthlyBranch
                                                );

                                results.add(
                                        relation(
                                                MonthlyLuckNatalRelationLayer
                                                        .EARTHLY_BRANCH,

                                                positions,

                                                "THREE_COMBINATION",

                                                form
                                                        == EarthlyBranchThreeCombinationForm
                                                        .HALF_WITH_CARDINAL
                                                        ? "반합"
                                                        : "삼합 부분구조"
                                        )
                                );
                            }
                    );

            earthlyBranchDirectionalCombinationRule
                    .findPartialCombinationType(
                            natal.branch(),
                            monthlyBranch
                    )
                    .ifPresent(
                            type -> {

                                EarthlyBranchDirectionalCombinationForm form =
                                        earthlyBranchDirectionalCombinationRule
                                                .determinePairForm(
                                                        type,
                                                        natal.branch(),
                                                        monthlyBranch
                                                );

                                results.add(
                                        relation(
                                                MonthlyLuckNatalRelationLayer
                                                        .EARTHLY_BRANCH,

                                                positions,

                                                "DIRECTIONAL_COMBINATION",

                                                form
                                                        == EarthlyBranchDirectionalCombinationForm
                                                        .PARTIAL_WITH_CARDINAL
                                                        ? "정기 포함 부분 방합"
                                                        : "부분 방합"
                                        )
                                );
                            }
                    );
        }
    }

    /*
     * =================================================
     * 월운 유입으로 완성되는 다중 관계
     * =================================================
     */

    private void buildBranchGroupRelations(

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch monthlyBranch,

            List<MonthlyLuckNatalRelationReference> results

    ) {

        for (
                int firstIndex = 0;
                firstIndex < natalBranches.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < natalBranches.size();
                    secondIndex++
            ) {

                NatalBranchSlot first =
                        natalBranches.get(
                                firstIndex
                        );

                NatalBranchSlot second =
                        natalBranches.get(
                                secondIndex
                        );

                Set<PalacePosition> positions =
                        Set.of(
                                first.position(),
                                second.position()
                        );

                if (
                        earthlyBranchThreeCombinationRule
                                .findCompleteCombinationType(
                                        first.branch(),
                                        second.branch(),
                                        monthlyBranch
                                )
                                .isPresent()
                ) {

                    results.add(
                            relation(
                                    MonthlyLuckNatalRelationLayer
                                            .EARTHLY_BRANCH,

                                    positions,

                                    "THREE_COMBINATION",

                                    "삼합"
                            )
                    );
                }

                if (
                        earthlyBranchDirectionalCombinationRule
                                .findCompleteCombinationType(
                                        first.branch(),
                                        second.branch(),
                                        monthlyBranch
                                )
                                .isPresent()
                ) {

                    results.add(
                            relation(
                                    MonthlyLuckNatalRelationLayer
                                            .EARTHLY_BRANCH,

                                    positions,

                                    "DIRECTIONAL_COMBINATION",

                                    "방합"
                            )
                    );
                }

                if (
                        BranchRelationTable
                                .isThreePunishment(
                                        first.branch(),
                                        second.branch(),
                                        monthlyBranch
                                )
                ) {

                    results.add(
                            relation(
                                    MonthlyLuckNatalRelationLayer
                                            .EARTHLY_BRANCH,

                                    positions,

                                    "PUNISHMENT",

                                    "삼형"
                            )
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 천간 Transformation
     * =================================================
     */

    private List<MonthlyLuckNatalTransformationReference>
    buildHeavenlyTransformations(

            NatalChartAdvancedAnalysis advancedAnalysis,

            List<NatalStemSlot> natalStems,

            HeavenlyStem monthlyStem

    ) {

        ElementStrengthAnalysis strength =
                advancedAnalysis
                        .elementStrengthAnalysis();

        List<DynamicStemCandidate> candidates =
                new ArrayList<>();

        for (
                NatalStemSlot natal
                : natalStems
        ) {

            Optional<FiveElement> target =
                    StemRelationTable
                            .findCombinationElement(
                                    natal.stem(),
                                    monthlyStem
                            );

            if (
                    target.isEmpty()
            ) {

                continue;
            }

            TargetEnvironment environment =
                    targetEnvironment(
                            strength,
                            target.get()
                    );

            /*
             * 외부 월운 천간에는
             * 원국 Palace 위치나 인접성을 부여하지 않는다.
             */
            HeavenlyStemCombinationApplicability applicability =
                    heavenlyStemCombinationCandidateRule
                            .determineApplicability(
                                    false,
                                    environment.shareBasisPoints(),
                                    environment.seasonalRate(),
                                    environment.monthBranchPresenceWeight()
                            );

            candidates.add(
                    new DynamicStemCandidate(
                            natal,
                            target.get(),
                            applicability
                    )
            );
        }

        List<MonthlyLuckNatalTransformationReference>
                results =
                new ArrayList<>();

        List<HeavenlyStemCombinationCandidate>
                natalCandidates =
                advancedAnalysis
                        .combinationTransformationAnalysis()
                        .heavenlyStemCandidateAnalysis()
                        .candidates();

        for (
                DynamicStemCandidate candidate
                : candidates
        ) {

            boolean competitionExists =
                    false;

            boolean equalOrStrongerCompetitionExists =
                    false;

            /*
             * 같은 외부 월운 천간을 둘러싼
             * 다른 동적 후보와 경쟁.
             */
            for (
                    DynamicStemCandidate other
                    : candidates
            ) {

                if (
                        other == candidate
                ) {

                    continue;
                }

                competitionExists =
                        true;

                if (
                        other
                                .applicability()
                                .rank()
                                >= candidate
                                .applicability()
                                .rank()
                ) {

                    equalOrStrongerCompetitionExists =
                            true;
                }
            }

            /*
             * 기존 원국 천간합 후보와의 경쟁도
             * 9차 결과를 그대로 사용한다.
             */
            VisibleStemPosition natalVisiblePosition =
                    VisibleStemPosition
                            .valueOf(
                                    candidate
                                            .natal()
                                            .position()
                                            .name()
                            );

            for (
                    HeavenlyStemCombinationCandidate natalCandidate
                    : natalCandidates
            ) {

                if (
                        !natalCandidate.involves(
                                natalVisiblePosition
                        )
                ) {

                    continue;
                }

                competitionExists =
                        true;

                if (
                        natalCandidate
                                .applicability()
                                .rank()
                                >= candidate
                                .applicability()
                                .rank()
                ) {

                    equalOrStrongerCompetitionExists =
                            true;
                }
            }

            boolean externalObstructionExists =
                    hasExternalStemObstruction(
                            natalStems,
                            candidate,
                            monthlyStem
                    );

            HeavenlyStemCombinationCompetitionState competitionState =
                    heavenlyStemCombinationCompetitionRule
                            .determineState(
                                    competitionExists,
                                    equalOrStrongerCompetitionExists,
                                    externalObstructionExists
                            );

            HeavenlyStemCombinationTransformationState
                    transformationState =
                    heavenlyStemCombinationTransformationRule
                            .determineState(
                                    candidate.applicability(),
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

            results.add(
                    new MonthlyLuckNatalTransformationReference(
                            Set.of(
                                    candidate
                                            .natal()
                                            .position()
                            ),

                            "HEAVENLY_STEM_COMBINATION:"
                                    + candidate
                                    .natal()
                                    .position()
                                    .name()
                                    + ":"
                                    + candidate
                                    .targetElement()
                                    .name(),

                            "천간합 합화",

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

        return List.copyOf(
                results
        );
    }

    private boolean hasExternalStemObstruction(

            List<NatalStemSlot> natalStems,

            DynamicStemCandidate candidate,

            HeavenlyStem monthlyStem

    ) {

        for (
                NatalStemSlot other
                : natalStems
        ) {

            if (
                    other.position()
                            == candidate
                            .natal()
                            .position()
            ) {

                continue;
            }

            if (
                    obstructsStem(
                            other.stem(),
                            candidate
                                    .natal()
                                    .stem()
                    )
            ) {

                return true;
            }

            if (
                    obstructsStem(
                            other.stem(),
                            monthlyStem
                    )
            ) {

                return true;
            }
        }

        return false;
    }

    private boolean obstructsStem(

            HeavenlyStem externalStem,

            HeavenlyStem memberStem

    ) {

        Optional<HeavenlyStemControlRelationType>
                relationType =
                heavenlyStemControlRule
                        .findRelationType(
                                externalStem,
                                memberStem
                        );

        if (
                relationType.isEmpty()
        ) {

            return false;
        }

        if (
                relationType.get()
                        == HeavenlyStemControlRelationType.CLASH
        ) {

            return true;
        }

        return heavenlyStemControlRule
                .controls(
                        externalStem,
                        memberStem
                );
    }

    /*
     * =================================================
     * 지지 Transformation
     * =================================================
     */

    private List<MonthlyLuckNatalTransformationReference>
    buildEarthlyTransformations(

            NatalChartAdvancedAnalysis advancedAnalysis,

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch monthlyBranch

    ) {

        ElementStrengthAnalysis strength =
                advancedAnalysis
                        .elementStrengthAnalysis();

        List<DynamicBranchCandidate> dynamicCandidates =
                buildDynamicBranchCandidates(
                        natalBranches,
                        monthlyBranch,
                        strength
                );

        dynamicCandidates =
                dynamicCandidates
                        .stream()
                        .map(
                                candidate ->
                                        applyObstruction(
                                                advancedAnalysis,
                                                natalBranches,
                                                monthlyBranch,
                                                candidate
                                        )
                        )
                        .toList();

        /*
         * 현재 월운 동적 후보
         * +
         * 기존 원국 9차 후보
         *
         * 를 동일 Integration 비교축에 둔다.
         */
        List<ComparableBranchCandidate> allComparables =
                new ArrayList<>();

        for (
                DynamicBranchCandidate candidate
                : dynamicCandidates
        ) {

            allComparables.add(
                    comparable(
                            candidate
                    )
            );
        }

        for (
                EarthlyBranchCombinationObstructionEvidence natalCandidate
                : advancedAnalysis
                .combinationTransformationAnalysis()
                .earthlyBranchObstructionAnalysis()
                .evidences()
        ) {

            allComparables.add(
                    comparable(
                            natalCandidate
                    )
            );
        }

        List<MonthlyLuckNatalTransformationReference>
                results =
                new ArrayList<>();

        for (
                DynamicBranchCandidate candidate
                : dynamicCandidates
        ) {

            ComparableBranchCandidate current =
                    comparable(
                            candidate
                    );

            boolean meaningfulConvergentSupport =
                    false;

            boolean higherPriorityConvergentExists =
                    false;

            boolean higherPriorityCompetingExists =
                    false;

            boolean equalPriorityCompetingExists =
                    false;

            for (
                    ComparableBranchCandidate other
                    : allComparables
            ) {

                if (
                        other
                                .identityKey()
                                .equals(
                                        current
                                                .identityKey()
                                )
                ) {

                    continue;
                }

                if (
                        !sharesMember(
                                current,
                                other
                        )
                ) {

                    continue;
                }

                int priorityComparison =
                        comparePriority(
                                current,
                                other
                        );

                if (
                        current
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
                    new MonthlyLuckNatalTransformationReference(
                            candidate
                                    .natalPositions(),

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

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * 월운 지지 동적 후보 생성
     * =================================================
     */

    private List<DynamicBranchCandidate>
    buildDynamicBranchCandidates(

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch monthlyBranch,

            ElementStrengthAnalysis strength

    ) {

        List<DynamicBranchCandidate> results =
                new ArrayList<>();

        /*
         * =================================================
         * 육합
         * =================================================
         */

        for (
                NatalBranchSlot natal
                : natalBranches
        ) {

            Optional<FiveElement> target =
                    BranchRelationTable
                            .findSixCombinationElement(
                                    natal.branch(),
                                    monthlyBranch
                            );

            if (
                    target.isEmpty()
            ) {

                continue;
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

                                    natal.position()
                                            == PalacePosition.MONTH,

                                    environment
                                            .shareBasisPoints(),

                                    environment
                                            .seasonalRate(),

                                    environment
                                            .monthBranchPresenceWeight()
                            );

            results.add(
                    new DynamicBranchCandidate(
                            "SIX:"
                                    + natal
                                    .position()
                                    .name()
                                    + ":"
                                    + target
                                    .get()
                                    .name(),

                            EarthlyBranchCombinationCandidateKind
                                    .SIX_COMBINATION,

                            Set.of(
                                    natal.position()
                            ),

                            List.of(
                                    BranchMember
                                            .natal(
                                                    natal
                                            ),

                                    BranchMember
                                            .monthly(
                                                    monthlyBranch
                                            )
                            ),

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

                            natal.position()
                                    == PalacePosition.MONTH,

                            false,

                            2,

                            EarthlyBranchCombinationObstructionLevel
                                    .NONE,

                            "SIX_COMBINATION:"
                                    + natal
                                    .position()
                                    .name()
                                    + ":"
                                    + target
                                    .get()
                                    .name(),

                            "육합 합화"
                    )
            );
        }

        /*
         * =================================================
         * 완전 삼합
         * =================================================
         */

        List<CompleteBranchCombination>
                completeThreeCombinations =
                new ArrayList<>();

        for (
                int firstIndex = 0;
                firstIndex < natalBranches.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < natalBranches.size();
                    secondIndex++
            ) {

                NatalBranchSlot first =
                        natalBranches.get(
                                firstIndex
                        );

                NatalBranchSlot second =
                        natalBranches.get(
                                secondIndex
                        );

                Optional<EarthlyBranchThreeCombinationType>
                        type =
                        earthlyBranchThreeCombinationRule
                                .findCompleteCombinationType(
                                        first.branch(),
                                        second.branch(),
                                        monthlyBranch
                                );

                if (
                        type.isEmpty()
                ) {

                    continue;
                }

                FiveElement target =
                        type.get()
                                .cardinalBranch()
                                .getElement();

                TargetEnvironment environment =
                        targetEnvironment(
                                strength,
                                target
                        );

                boolean monthInvolved =
                        first.position()
                                == PalacePosition.MONTH

                                || second.position()
                                == PalacePosition.MONTH;

                EarthlyBranchThreeCombinationFormationState state =
                        earthlyBranchThreeCombinationFormationRule
                                .determineState(
                                        EarthlyBranchThreeCombinationForm
                                                .COMPLETE,

                                        false,

                                        monthInvolved,

                                        environment
                                                .shareBasisPoints(),

                                        environment
                                                .seasonalRate(),

                                        environment
                                                .monthBranchPresenceWeight()
                                );

                Set<PalacePosition> positions =
                        Set.of(
                                first.position(),
                                second.position()
                        );

                completeThreeCombinations.add(
                        new CompleteBranchCombination(
                                "THREE:"
                                        + type
                                        .get()
                                        .name(),

                                positions
                        )
                );

                results.add(
                        new DynamicBranchCandidate(
                                "THREE_COMPLETE:"
                                        + positionKey(
                                        positions
                                )
                                        + ":"
                                        + target.name(),

                                EarthlyBranchCombinationCandidateKind
                                        .THREE_COMBINATION,

                                positions,

                                List.of(
                                        BranchMember
                                                .natal(
                                                        first
                                                ),

                                        BranchMember
                                                .natal(
                                                        second
                                                ),

                                        BranchMember
                                                .monthly(
                                                        monthlyBranch
                                                )
                                ),

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

                                monthInvolved,

                                true,

                                5,

                                EarthlyBranchCombinationObstructionLevel
                                        .NONE,

                                "THREE_COMBINATION_COMPLETE:"
                                        + positionKey(
                                        positions
                                )
                                        + ":"
                                        + target.name(),

                                "삼합 구조 성립"
                        )
                );
            }
        }

        /*
         * =================================================
         * 부분 삼합
         * =================================================
         */

        for (
                NatalBranchSlot natal
                : natalBranches
        ) {

            Optional<EarthlyBranchThreeCombinationType>
                    type =
                    earthlyBranchThreeCombinationRule
                            .findPartialCombinationType(
                                    natal.branch(),
                                    monthlyBranch
                            );

            if (
                    type.isEmpty()
            ) {

                continue;
            }

            boolean covered =
                    completeThreeCombinations
                            .stream()
                            .anyMatch(
                                    complete ->
                                            complete
                                                    .familyKey()
                                                    .equals(
                                                            "THREE:"
                                                                    + type
                                                                    .get()
                                                                    .name()
                                                    )

                                                    && complete
                                                    .natalPositions()
                                                    .contains(
                                                            natal.position()
                                                    )
                            );

            if (
                    covered
            ) {

                continue;
            }

            EarthlyBranchThreeCombinationForm form =
                    earthlyBranchThreeCombinationRule
                            .determinePairForm(
                                    type.get(),
                                    natal.branch(),
                                    monthlyBranch
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

            boolean monthInvolved =
                    natal.position()
                            == PalacePosition.MONTH;

            EarthlyBranchThreeCombinationFormationState state =
                    earthlyBranchThreeCombinationFormationRule
                            .determineState(
                                    form,

                                    false,

                                    monthInvolved,

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

            results.add(
                    new DynamicBranchCandidate(
                            "THREE_PARTIAL:"
                                    + natal
                                    .position()
                                    .name()
                                    + ":"
                                    + form.name()
                                    + ":"
                                    + target.name(),

                            EarthlyBranchCombinationCandidateKind
                                    .THREE_COMBINATION,

                            Set.of(
                                    natal.position()
                            ),

                            List.of(
                                    BranchMember
                                            .natal(
                                                    natal
                                            ),

                                    BranchMember
                                            .monthly(
                                                    monthlyBranch
                                            )
                            ),

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

                            monthInvolved,

                            cardinalIncluded,

                            cardinalIncluded
                                    ? 3
                                    : 1,

                            EarthlyBranchCombinationObstructionLevel
                                    .NONE,

                            "THREE_COMBINATION_"
                                    + form.name()
                                    + ":"
                                    + natal
                                    .position()
                                    .name()
                                    + ":"
                                    + target.name(),

                            cardinalIncluded
                                    ? "반합 구조 성립"
                                    : "삼합 부분구조"
                    )
            );
        }

        /*
         * =================================================
         * 완전 방합
         * =================================================
         */

        List<CompleteBranchCombination>
                completeDirectionalCombinations =
                new ArrayList<>();

        for (
                int firstIndex = 0;
                firstIndex < natalBranches.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < natalBranches.size();
                    secondIndex++
            ) {

                NatalBranchSlot first =
                        natalBranches.get(
                                firstIndex
                        );

                NatalBranchSlot second =
                        natalBranches.get(
                                secondIndex
                        );

                Optional<EarthlyBranchDirectionalCombinationType>
                        type =
                        earthlyBranchDirectionalCombinationRule
                                .findCompleteCombinationType(
                                        first.branch(),
                                        second.branch(),
                                        monthlyBranch
                                );

                if (
                        type.isEmpty()
                ) {

                    continue;
                }

                FiveElement target =
                        type.get()
                                .cardinalBranch()
                                .getElement();

                TargetEnvironment environment =
                        targetEnvironment(
                                strength,
                                target
                        );

                boolean monthInvolved =
                        first.position()
                                == PalacePosition.MONTH

                                || second.position()
                                == PalacePosition.MONTH;

                EarthlyBranchDirectionalCombinationFormationState state =
                        earthlyBranchDirectionalCombinationFormationRule
                                .determineState(
                                        EarthlyBranchDirectionalCombinationForm
                                                .COMPLETE,

                                        false,

                                        monthInvolved,

                                        environment
                                                .shareBasisPoints(),

                                        environment
                                                .seasonalRate(),

                                        environment
                                                .monthBranchPresenceWeight()
                                );

                Set<PalacePosition> positions =
                        Set.of(
                                first.position(),
                                second.position()
                        );

                completeDirectionalCombinations.add(
                        new CompleteBranchCombination(
                                "DIRECTIONAL:"
                                        + type
                                        .get()
                                        .name(),

                                positions
                        )
                );

                results.add(
                        new DynamicBranchCandidate(
                                "DIRECTIONAL_COMPLETE:"
                                        + positionKey(
                                        positions
                                )
                                        + ":"
                                        + target.name(),

                                EarthlyBranchCombinationCandidateKind
                                        .DIRECTIONAL_COMBINATION,

                                positions,

                                List.of(
                                        BranchMember
                                                .natal(
                                                        first
                                                ),

                                        BranchMember
                                                .natal(
                                                        second
                                                ),

                                        BranchMember
                                                .monthly(
                                                        monthlyBranch
                                                )
                                ),

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

                                monthInvolved,

                                true,

                                5,

                                EarthlyBranchCombinationObstructionLevel
                                        .NONE,

                                "DIRECTIONAL_COMBINATION_COMPLETE:"
                                        + positionKey(
                                        positions
                                )
                                        + ":"
                                        + target.name(),

                                "방합 구조 성립"
                        )
                );
            }
        }

        /*
         * =================================================
         * 부분 방합
         * =================================================
         */

        for (
                NatalBranchSlot natal
                : natalBranches
        ) {

            Optional<EarthlyBranchDirectionalCombinationType>
                    type =
                    earthlyBranchDirectionalCombinationRule
                            .findPartialCombinationType(
                                    natal.branch(),
                                    monthlyBranch
                            );

            if (
                    type.isEmpty()
            ) {

                continue;
            }

            boolean covered =
                    completeDirectionalCombinations
                            .stream()
                            .anyMatch(
                                    complete ->
                                            complete
                                                    .familyKey()
                                                    .equals(
                                                            "DIRECTIONAL:"
                                                                    + type
                                                                    .get()
                                                                    .name()
                                                    )

                                                    && complete
                                                    .natalPositions()
                                                    .contains(
                                                            natal.position()
                                                    )
                            );

            if (
                    covered
            ) {

                continue;
            }

            EarthlyBranchDirectionalCombinationForm form =
                    earthlyBranchDirectionalCombinationRule
                            .determinePairForm(
                                    type.get(),
                                    natal.branch(),
                                    monthlyBranch
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

            boolean monthInvolved =
                    natal.position()
                            == PalacePosition.MONTH;

            EarthlyBranchDirectionalCombinationFormationState state =
                    earthlyBranchDirectionalCombinationFormationRule
                            .determineState(
                                    form,

                                    false,

                                    monthInvolved,

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

            results.add(
                    new DynamicBranchCandidate(
                            "DIRECTIONAL_PARTIAL:"
                                    + natal
                                    .position()
                                    .name()
                                    + ":"
                                    + form.name()
                                    + ":"
                                    + target.name(),

                            EarthlyBranchCombinationCandidateKind
                                    .DIRECTIONAL_COMBINATION,

                            Set.of(
                                    natal.position()
                            ),

                            List.of(
                                    BranchMember
                                            .natal(
                                                    natal
                                            ),

                                    BranchMember
                                            .monthly(
                                                    monthlyBranch
                                            )
                            ),

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

                            monthInvolved,

                            cardinalIncluded,

                            cardinalIncluded
                                    ? 3
                                    : 1,

                            EarthlyBranchCombinationObstructionLevel
                                    .NONE,

                            "DIRECTIONAL_COMBINATION_"
                                    + form.name()
                                    + ":"
                                    + natal
                                    .position()
                                    .name()
                                    + ":"
                                    + target.name(),

                            cardinalIncluded
                                    ? "정기 포함 부분 방합 구조 성립"
                                    : "부분 방합 구조 성립"
                    )
            );
        }

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * 방해 구조
     * =================================================
     */

    private DynamicBranchCandidate applyObstruction(

            NatalChartAdvancedAnalysis advancedAnalysis,

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch monthlyBranch,

            DynamicBranchCandidate candidate

    ) {

        List<BranchMember> allMembers =
                new ArrayList<>();

        for (
                NatalBranchSlot natal
                : natalBranches
        ) {

            allMembers.add(
                    BranchMember
                            .natal(
                                    natal
                            )
            );
        }

        allMembers.add(
                BranchMember
                        .monthly(
                                monthlyBranch
                        )
        );

        Set<String> affectedCandidateMembers =
                new HashSet<>();

        EnumSet<EarthlyBranchRelationKind> allKinds =
                EnumSet.noneOf(
                        EarthlyBranchRelationKind.class
                );

        boolean internalObstruction =
                false;

        boolean internalClash =
                false;

        boolean internalMultipleKinds =
                false;

        boolean cardinalAffected =
                false;

        boolean cardinalClashed =
                false;

        boolean adjacentObstruction =
                false;

        for (
                int firstIndex = 0;
                firstIndex < allMembers.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < allMembers.size();
                    secondIndex++
            ) {

                BranchMember first =
                        allMembers.get(
                                firstIndex
                        );

                BranchMember second =
                        allMembers.get(
                                secondIndex
                        );

                Set<EarthlyBranchRelationKind> kinds =
                        obstructionKinds(
                                advancedAnalysis
                                        .relationAnalysis(),

                                first,

                                second
                        );

                if (
                        kinds.isEmpty()
                ) {

                    continue;
                }

                List<BranchMember> affected =
                        candidate
                                .members()
                                .stream()
                                .filter(
                                        member ->
                                                member.sameMember(
                                                        first
                                                )
                                                        || member.sameMember(
                                                        second
                                                )
                                )
                                .toList();

                if (
                        affected.isEmpty()
                ) {

                    continue;
                }

                allKinds.addAll(
                        kinds
                );

                for (
                        BranchMember member
                        : affected
                ) {

                    affectedCandidateMembers.add(
                            member.memberKey()
                    );
                }

                boolean pairInternal =
                        candidate.containsMember(
                                first
                        )
                                && candidate.containsMember(
                                second
                        );

                if (
                        pairInternal
                ) {

                    internalObstruction =
                            true;

                    if (
                            kinds.contains(
                                    EarthlyBranchRelationKind.CLASH
                            )
                    ) {

                        internalClash =
                                true;
                    }

                    if (
                            kinds.size() >= 2
                    ) {

                        internalMultipleKinds =
                                true;
                    }
                }

                if (
                        candidate
                                .cardinalMemberAffectedBy(
                                        first,
                                        second
                                )
                ) {

                    cardinalAffected =
                            true;

                    if (
                            kinds.contains(
                                    EarthlyBranchRelationKind.CLASH
                            )
                    ) {

                        cardinalClashed =
                                true;
                    }
                }

                /*
                 * adjacent는 원국 Palace끼리만 적용한다.
                 *
                 * 외부 월운에는 원국 위치를 부여하지 않는다.
                 */
                if (
                        !first.monthly()
                                && !second.monthly()

                                && Math.abs(
                                first
                                        .natalPosition()
                                        .order()

                                        - second
                                        .natalPosition()
                                        .order()
                        ) == 1
                ) {

                    adjacentObstruction =
                            true;
                }
            }
        }

        /*
         * =================================================
         * 완성 삼형
         * =================================================
         */

        for (
                int firstIndex = 0;
                firstIndex < allMembers.size() - 2;
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < allMembers.size() - 1;
                    secondIndex++
            ) {

                for (
                        int thirdIndex =
                        secondIndex + 1;
                        thirdIndex < allMembers.size();
                        thirdIndex++
                ) {

                    BranchMember first =
                            allMembers.get(
                                    firstIndex
                            );

                    BranchMember second =
                            allMembers.get(
                                    secondIndex
                            );

                    BranchMember third =
                            allMembers.get(
                                    thirdIndex
                            );

                    if (
                            !BranchRelationTable
                                    .isThreePunishment(
                                            first.branch(),
                                            second.branch(),
                                            third.branch()
                                    )
                    ) {

                        continue;
                    }

                    List<BranchMember> affected =
                            candidate
                                    .members()
                                    .stream()
                                    .filter(
                                            member ->
                                                    member.sameMember(
                                                            first
                                                    )
                                                            || member.sameMember(
                                                            second
                                                    )
                                                            || member.sameMember(
                                                            third
                                                    )
                                    )
                                    .toList();

                    if (
                            affected.isEmpty()
                    ) {

                        continue;
                    }

                    allKinds.add(
                            EarthlyBranchRelationKind
                                    .PUNISHMENT
                    );

                    for (
                            BranchMember member
                            : affected
                    ) {

                        affectedCandidateMembers.add(
                                member.memberKey()
                        );
                    }

                    if (
                            candidate.containsMember(
                                    first
                            )
                                    && candidate.containsMember(
                                    second
                            )
                                    && candidate.containsMember(
                                    third
                            )
                    ) {

                        internalObstruction =
                                true;
                    }

                    if (
                            candidate
                                    .cardinalMemberAffectedBy(
                                            first,
                                            second,
                                            third
                                    )
                    ) {

                        cardinalAffected =
                                true;
                    }
                }
            }
        }

        boolean obstructionExists =
                !allKinds.isEmpty();

        EarthlyBranchCombinationObstructionLevel level =
                earthlyBranchCombinationObstructionRule
                        .determineLevel(
                                obstructionExists,
                                internalObstruction,
                                internalClash,
                                internalMultipleKinds,
                                cardinalAffected,
                                cardinalClashed,

                                allKinds.contains(
                                        EarthlyBranchRelationKind.CLASH
                                ),

                                allKinds.contains(
                                        EarthlyBranchRelationKind.PUNISHMENT
                                ),

                                adjacentObstruction,

                                affectedCandidateMembers
                                        .size(),

                                allKinds
                                        .size()
                        );

        return candidate
                .withObstructionLevel(
                        level
                );
    }

    /*
     * =================================================
     * 방해 관계 종류
     * =================================================
     */

    private Set<EarthlyBranchRelationKind> obstructionKinds(

            RelationAnalysis relationAnalysis,

            BranchMember first,

            BranchMember second

    ) {

        EnumSet<EarthlyBranchRelationKind> result =
                EnumSet.noneOf(
                        EarthlyBranchRelationKind.class
                );

        /*
         * =================================================
         * 원국 내부 관계
         * =================================================
         *
         * 이미 8차가 계산한 결과를 사용한다.
         */

        if (
                !first.monthly()
                        && !second.monthly()
        ) {

            EarthlyBranchPosition firstPosition =
                    EarthlyBranchPosition
                            .valueOf(
                                    first
                                            .natalPosition()
                                            .name()
                            );

            EarthlyBranchPosition secondPosition =
                    EarthlyBranchPosition
                            .valueOf(
                                    second
                                            .natalPosition()
                                            .name()
                            );

            relationAnalysis
                    .earthlyBranchRelationBetween(
                            firstPosition,
                            secondPosition
                    )
                    .ifPresent(
                            relation -> {

                                if (
                                        relation
                                                .hasClash()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind
                                                    .CLASH
                                    );
                                }

                                if (
                                        relation
                                                .hasPunishment()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind
                                                    .PUNISHMENT
                                    );
                                }

                                if (
                                        relation
                                                .hasBreak()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind
                                                    .BREAK
                                    );
                                }

                                if (
                                        relation
                                                .hasHarm()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind
                                                    .HARM
                                    );
                                }
                            }
                    );

            return Set.copyOf(
                    result
            );
        }

        /*
         * =================================================
         * 월운이 참여하는 Pair
         * =================================================
         *
         * 외부 월운을 원국 Position으로 바꾸지 않고
         * canonical BranchRelationTable을 사용한다.
         */

        if (
                BranchRelationTable
                        .isClash(
                                first.branch(),
                                second.branch()
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
                                first.branch(),
                                second.branch()
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind
                            .PUNISHMENT
            );
        }

        if (
                first.branch()
                        == second.branch()

                        && BranchRelationTable
                        .isSelfPunishmentBranch(
                                first.branch()
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
                                first.branch(),
                                second.branch()
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
                                first.branch(),
                                second.branch()
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
     * Integration 비교
     * =================================================
     */

    private int comparePriority(

            ComparableBranchCandidate first,

            ComparableBranchCandidate second

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

    private boolean sharesMember(

            ComparableBranchCandidate first,

            ComparableBranchCandidate second

    ) {

        /*
         * 같은 월운 분석에서 만들어진 동적 후보들은
         * 동일한 외부 월운 지지를 공유한다.
         */
        if (
                first.monthlyInvolved()
                        && second.monthlyInvolved()
        ) {

            return true;
        }

        /*
         * 원국 후보와 비교할 때는
         * 실제 원국 Palace 공유 여부만 본다.
         */
        return first
                .natalPositions()
                .stream()
                .anyMatch(
                        second
                                .natalPositions()
                                ::contains
                );
    }

    private ComparableBranchCandidate comparable(
            DynamicBranchCandidate candidate
    ) {

        return new ComparableBranchCandidate(
                "MONTHLY:"
                        + candidate
                        .identityKey(),

                true,

                candidate
                        .natalPositions(),

                candidate
                        .targetElement(),

                candidate
                        .baseStateRank(),

                candidate
                        .baseMeaningful(),

                candidate
                        .baseCandidateRemains(),

                candidate
                        .obstructionLevel(),

                candidate
                        .monthBranchInvolved(),

                candidate
                        .cardinalIncluded(),

                candidate
                        .structuralRank()
        );
    }

    private ComparableBranchCandidate comparable(
            EarthlyBranchCombinationObstructionEvidence candidate
    ) {

        Set<PalacePosition> positions =
                candidate
                        .candidatePositions()
                        .stream()
                        .map(
                                position ->
                                        PalacePosition
                                                .valueOf(
                                                        position.name()
                                                )
                        )
                        .collect(
                                java.util.stream.Collectors
                                        .toUnmodifiableSet()
                        );

        return new ComparableBranchCandidate(
                "NATAL:"
                        + candidate
                        .candidateKind()
                        .name()
                        + ":"
                        + positionKey(
                        positions
                )
                        + ":"
                        + candidate
                        .targetElement()
                        .name()
                        + ":"
                        + candidate
                        .baseStateRank(),

                false,

                positions,

                candidate
                        .targetElement(),

                candidate
                        .baseStateRank(),

                candidate
                        .baseMeaningful(),

                candidate
                        .baseCandidateRemains(),

                candidate
                        .level(),

                candidate
                        .monthBranchInvolved(),

                earthlyBranchCombinationIntegrationRule
                        .cardinalIncluded(
                                candidate
                        ),

                earthlyBranchCombinationIntegrationRule
                        .structuralRank(
                                candidate
                        )
        );
    }

    /*
     * =================================================
     * 원국 천간 Slot
     * =================================================
     */

    private List<NatalStemSlot> natalStemSlots(
            PalaceAnalysis natalAnalysis
    ) {

        List<NatalStemSlot> result =
                new ArrayList<>();

        /*
         * 출생시간 미상은 knownPositions()가
         * YEAR/MONTH/DAY만 반환해야 하므로
         * HOUR를 임의 생성하지 않는다.
         */
        for (
                PalacePosition position
                : natalAnalysis
                .knownPositions()
        ) {

            result.add(
                    new NatalStemSlot(
                            position,

                            natalAnalysis
                                    .positionAnalysis()
                                    .stemEvidenceOf(
                                            position
                                    )
                                    .orElseThrow(
                                            () ->
                                                    new IllegalStateException(
                                                            "Palace 천간 Evidence가 없습니다: "
                                                                    + position
                                                    )
                                    )
                                    .stem()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 원국 지지 Slot
     * =================================================
     */

    private List<NatalBranchSlot> natalBranchSlots(
            PalaceAnalysis natalAnalysis
    ) {

        List<NatalBranchSlot> result =
                new ArrayList<>();

        for (
                PalacePosition position
                : natalAnalysis
                .knownPositions()
        ) {

            result.add(
                    new NatalBranchSlot(
                            position,

                            natalAnalysis
                                    .positionAnalysis()
                                    .branchEvidenceOf(
                                            position
                                    )
                                    .orElseThrow(
                                            () ->
                                                    new IllegalStateException(
                                                            "Palace 지지 Evidence가 없습니다: "
                                                                    + position
                                                    )
                                    )
                                    .branch()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Target 환경
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

    /*
     * =================================================
     * 관계 Reference
     * =================================================
     */

    private MonthlyLuckNatalRelationReference relation(

            MonthlyLuckNatalRelationLayer layer,

            Set<PalacePosition> positions,

            String code,

            String name

    ) {

        return new MonthlyLuckNatalRelationReference(
                layer,
                positions,
                code,
                name
        );
    }

    /*
     * =================================================
     * 원국 위치 정렬 Key
     * =================================================
     */

    private String positionKey(
            Set<PalacePosition> positions
    ) {

        return positions
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                PalacePosition
                                        ::order
                        )
                )
                .map(
                        Enum::name
                )
                .reduce(
                        (
                                first,
                                second
                        ) ->
                                first
                                        + "_"
                                        + second
                )
                .orElse(
                        "NONE"
                );
    }

    /*
     * =================================================
     * 내부 구조
     * =================================================
     */

    private record NatalStemSlot(

            PalacePosition position,

            HeavenlyStem stem

    ) {

        private NatalStemSlot {

            Objects.requireNonNull(
                    position,
                    "NatalStemSlot에는 PalacePosition이 필요합니다."
            );

            Objects.requireNonNull(
                    stem,
                    "NatalStemSlot에는 HeavenlyStem이 필요합니다."
            );
        }
    }

    private record NatalBranchSlot(

            PalacePosition position,

            EarthlyBranch branch

    ) {

        private NatalBranchSlot {

            Objects.requireNonNull(
                    position,
                    "NatalBranchSlot에는 PalacePosition이 필요합니다."
            );

            Objects.requireNonNull(
                    branch,
                    "NatalBranchSlot에는 EarthlyBranch가 필요합니다."
            );
        }
    }

    private record DynamicStemCandidate(

            NatalStemSlot natal,

            FiveElement targetElement,

            HeavenlyStemCombinationApplicability
            applicability

    ) {

        private DynamicStemCandidate {

            Objects.requireNonNull(
                    natal,
                    "DynamicStemCandidate에는 원국 천간이 필요합니다."
            );

            Objects.requireNonNull(
                    targetElement,
                    "DynamicStemCandidate에는 합화 목표 오행이 필요합니다."
            );

            Objects.requireNonNull(
                    applicability,
                    "DynamicStemCandidate에는 Applicability가 필요합니다."
            );
        }
    }

    private record TargetEnvironment(

            int shareBasisPoints,

            int seasonalRate,

            int monthBranchPresenceWeight

    ) {
    }

    private record CompleteBranchCombination(

            String familyKey,

            Set<PalacePosition> natalPositions

    ) {

        private CompleteBranchCombination {

            Objects.requireNonNull(
                    familyKey,
                    "CompleteBranchCombination에는 familyKey가 필요합니다."
            );

            natalPositions =
                    Set.copyOf(
                            Objects.requireNonNull(
                                    natalPositions,
                                    "CompleteBranchCombination에는 원국 위치가 필요합니다."
                            )
                    );
        }
    }

    private record ComparableBranchCandidate(

            String identityKey,

            boolean monthlyInvolved,

            Set<PalacePosition> natalPositions,

            FiveElement targetElement,

            int baseStateRank,

            boolean baseMeaningful,

            boolean baseCandidateRemains,

            EarthlyBranchCombinationObstructionLevel
            obstructionLevel,

            boolean monthBranchInvolved,

            boolean cardinalIncluded,

            int structuralRank

    ) {

        private ComparableBranchCandidate {

            Objects.requireNonNull(
                    identityKey,
                    "ComparableBranchCandidate에는 identityKey가 필요합니다."
            );

            natalPositions =
                    Set.copyOf(
                            Objects.requireNonNull(
                                    natalPositions,
                                    "ComparableBranchCandidate에는 원국 위치가 필요합니다."
                            )
                    );

            Objects.requireNonNull(
                    targetElement,
                    "ComparableBranchCandidate에는 목표 오행이 필요합니다."
            );

            Objects.requireNonNull(
                    obstructionLevel,
                    "ComparableBranchCandidate에는 방해 수준이 필요합니다."
            );
        }
    }

    private record DynamicBranchCandidate(

            String identityKey,

            EarthlyBranchCombinationCandidateKind kind,

            Set<PalacePosition> natalPositions,

            List<BranchMember> members,

            FiveElement targetElement,

            int baseStateRank,

            boolean baseMeaningful,

            boolean baseCandidateRemains,

            boolean monthBranchInvolved,

            boolean cardinalIncluded,

            int structuralRank,

            EarthlyBranchCombinationObstructionLevel
            obstructionLevel,

            String transformationCode,

            String transformationName

    ) {

        private DynamicBranchCandidate {

            Objects.requireNonNull(
                    identityKey,
                    "DynamicBranchCandidate에는 identityKey가 필요합니다."
            );

            Objects.requireNonNull(
                    kind,
                    "DynamicBranchCandidate에는 CandidateKind가 필요합니다."
            );

            natalPositions =
                    Set.copyOf(
                            Objects.requireNonNull(
                                    natalPositions,
                                    "DynamicBranchCandidate에는 원국 위치가 필요합니다."
                            )
                    );

            members =
                    List.copyOf(
                            Objects.requireNonNull(
                                    members,
                                    "DynamicBranchCandidate에는 구성원이 필요합니다."
                            )
                    );

            Objects.requireNonNull(
                    targetElement,
                    "DynamicBranchCandidate에는 목표 오행이 필요합니다."
            );

            Objects.requireNonNull(
                    obstructionLevel,
                    "DynamicBranchCandidate에는 방해 수준이 필요합니다."
            );

            Objects.requireNonNull(
                    transformationCode,
                    "DynamicBranchCandidate에는 transformationCode가 필요합니다."
            );

            Objects.requireNonNull(
                    transformationName,
                    "DynamicBranchCandidate에는 transformationName이 필요합니다."
            );
        }

        private DynamicBranchCandidate withObstructionLevel(
                EarthlyBranchCombinationObstructionLevel level
        ) {

            return new DynamicBranchCandidate(
                    identityKey,
                    kind,
                    natalPositions,
                    members,
                    targetElement,
                    baseStateRank,
                    baseMeaningful,
                    baseCandidateRemains,
                    monthBranchInvolved,
                    cardinalIncluded,
                    structuralRank,
                    level,
                    transformationCode,
                    transformationName
            );
        }

        private boolean containsMember(
                BranchMember target
        ) {

            return members
                    .stream()
                    .anyMatch(
                            member ->
                                    member.sameMember(
                                            target
                                    )
                    );
        }

        private boolean cardinalMemberAffectedBy(
                BranchMember... targets
        ) {

            if (
                    !cardinalIncluded
            ) {

                return false;
            }

            EarthlyBranch cardinalBranch =
                    targetElementBranch();

            if (
                    cardinalBranch == null
            ) {

                return false;
            }

            for (
                    BranchMember member
                    : members
            ) {

                if (
                        member.branch()
                                != cardinalBranch
                ) {

                    continue;
                }

                for (
                        BranchMember target
                        : targets
                ) {

                    if (
                            member.sameMember(
                                    target
                            )
                    ) {

                        return true;
                    }
                }
            }

            return false;
        }

        private EarthlyBranch targetElementBranch() {

            return switch (
                    targetElement
                    ) {

                case WATER ->
                        EarthlyBranch.ZI;

                case WOOD ->
                        EarthlyBranch.MAO;

                case FIRE ->
                        EarthlyBranch.WU;

                case METAL ->
                        EarthlyBranch.YOU;

                case EARTH ->
                        null;
            };
        }
    }

    /**
     * 외부 월운 지지는 PalacePosition을 갖지 않는다.
     *
     * natalPosition == null
     *
     * 은 MONTH 궁위라는 뜻이 아니라
     * 외부 월운 Source라는 뜻이다.
     */
    private record BranchMember(

            PalacePosition natalPosition,

            EarthlyBranch branch,

            boolean monthly

    ) {

        private BranchMember {

            Objects.requireNonNull(
                    branch,
                    "BranchMember에는 EarthlyBranch가 필요합니다."
            );

            if (
                    monthly
                            && natalPosition != null
            ) {

                throw new IllegalArgumentException(
                        "외부 월운 BranchMember에는 원국 PalacePosition을 부여할 수 없습니다."
                );
            }

            if (
                    !monthly
                            && natalPosition == null
            ) {

                throw new IllegalArgumentException(
                        "원국 BranchMember에는 PalacePosition이 필요합니다."
                );
            }
        }

        private static BranchMember natal(
                NatalBranchSlot slot
        ) {

            Objects.requireNonNull(
                    slot,
                    "원국 BranchMember에는 NatalBranchSlot이 필요합니다."
            );

            return new BranchMember(
                    slot.position(),
                    slot.branch(),
                    false
            );
        }

        private static BranchMember monthly(
                EarthlyBranch branch
        ) {

            return new BranchMember(
                    null,
                    Objects.requireNonNull(
                            branch,
                            "월운 BranchMember에는 EarthlyBranch가 필요합니다."
                    ),
                    true
            );
        }

        private String memberKey() {

            return monthly
                    ? "MONTHLY"
                    : "NATAL:"
                    + natalPosition
                    .name();
        }

        private boolean sameMember(
                BranchMember other
        ) {

            if (
                    other == null
            ) {

                return false;
            }

            if (
                    monthly
                            || other.monthly
            ) {

                return monthly
                        && other.monthly;
            }

            return natalPosition
                    == other.natalPosition;
        }
    }
}