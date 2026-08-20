package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.AnnualLuckNatalCrossResolution;
import com.dajeom.model.AnnualLuckNatalRelationReference;
import com.dajeom.model.AnnualLuckNatalTransformationReference;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.EarthlyBranchCombinationObstructionEvidence;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.HeavenlyStemCombinationCandidate;
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
import com.dajeom.type.AnnualLuckNatalRelationKind;
import com.dajeom.type.AnnualLuckNatalRelationLayer;
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
import com.dajeom.type.PalacePosition;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 14차-4.
 *
 * 원국 × 세운 관계 및 합화 정밀 분석.
 *
 * =================================================
 *
 * 핵심:
 *
 * 세운을 YEAR/MONTH/DAY/HOUR 궁위로 위장하지 않는다.
 *
 * 세운은 외부 시간축 Source이므로
 * 원국 공간 규칙인 adjacent / compact를
 * 허위로 부여하지 않는다.
 *
 * 관계 존재와:
 *
 * - 합화 후보
 * - 실제 활성
 * - 목표 오행
 * - 목표 활성 단계
 * - 세운 Participant 원기능 상태
 * - 원국 Participant 원기능 상태
 *
 * 를 분리한다.
 *
 * 기존 8차/9차 Rule을 그대로 재사용한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 기존에는 Reference 생성 시:
 *
 * - Relation Kind
 * - FiveElement
 * - TransformationTargetActivationState
 * - TransformationOriginalFunctionState
 *
 * 를 String / boolean으로 압축했다.
 *
 * 이제 계산 단계에서 이미 존재하는
 * typed provenance를 그대로 Reference까지
 * 전달한다.
 *
 * =================================================
 *
 * 새 명리 계산 없음.
 *
 * 기존 8차/9차 규칙 변경 없음.
 *
 * 문자열 파싱 없음.
 */
@Component
public class AnnualLuckNatalCrossAnalyzer {

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

    public AnnualLuckNatalCrossAnalyzer(

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

    public AnnualLuckNatalCrossResolution analyze(

            NatalChartAdvancedAnalysis advancedAnalysis,

            PalaceAnalysis natalAnalysis,

            AnnualLuckPeriod annualLuckPeriod

    ) {

        validateInputs(
                advancedAnalysis,
                natalAnalysis,
                annualLuckPeriod
        );

        List<NatalStemSlot> natalStems =
                natalStemSlots(
                        natalAnalysis
                );

        List<NatalBranchSlot> natalBranches =
                natalBranchSlots(
                        natalAnalysis
                );

        HeavenlyStem annualStem =
                annualLuckPeriod.stem();

        EarthlyBranch annualBranch =
                annualLuckPeriod.branch();

        List<AnnualLuckNatalRelationReference> relations =
                new ArrayList<>();

        buildStemRelations(
                natalStems,
                annualStem,
                relations
        );

        buildBranchPairRelations(
                natalBranches,
                annualBranch,
                relations
        );

        buildBranchGroupRelations(
                natalBranches,
                annualBranch,
                relations
        );

        List<AnnualLuckNatalTransformationReference>
                transformations =
                new ArrayList<>();

        transformations.addAll(
                buildHeavenlyTransformations(
                        advancedAnalysis,
                        natalStems,
                        annualStem
                )
        );

        transformations.addAll(
                buildEarthlyTransformations(
                        advancedAnalysis,
                        natalBranches,
                        annualBranch
                )
        );

        relations.sort(
                Comparator
                        .comparingInt(
                                (
                                        AnnualLuckNatalRelationReference reference
                                ) ->
                                        reference
                                                .layer()
                                                .ordinal()
                        )
                        .thenComparing(
                                reference ->
                                        positionKey(
                                                reference
                                                        .natalPositions()
                                        )
                        )
                        .thenComparing(
                                AnnualLuckNatalRelationReference
                                        ::relationCode
                        )
        );

        transformations.sort(
                Comparator
                        .comparingInt(
                                (
                                        AnnualLuckNatalTransformationReference reference
                                ) ->
                                        reference
                                                .layer()
                                                .ordinal()
                        )
                        .thenComparing(
                                reference ->
                                        positionKey(
                                                reference
                                                        .natalPositions()
                                        )
                        )
                        .thenComparing(
                                AnnualLuckNatalTransformationReference
                                        ::transformationCode
                        )
        );

        return new AnnualLuckNatalCrossResolution(
                annualLuckPeriod,
                relations,
                transformations
        );
    }

    private void validateInputs(

            NatalChartAdvancedAnalysis advancedAnalysis,

            PalaceAnalysis natalAnalysis,

            AnnualLuckPeriod annualLuckPeriod

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "14-4에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "14-4에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckPeriod,
                "14-4에는 AnnualLuckPeriod가 필요합니다."
        );

        if (
                advancedAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
                        != natalAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "3차 오행 세력과 12차 Palace의 일간이 일치하지 않습니다."
            );
        }

        if (
                advancedAnalysis.hasHourPillar()
                        != natalAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "원국 고급 분석과 Palace의 completeChart 상태가 일치하지 않습니다."
            );
        }

        CombinationTransformationAnalysis transformationAnalysis =
                advancedAnalysis
                        .combinationTransformationAnalysis();

        if (
                transformationAnalysis
                        .relationAnalysis()
                        != advancedAnalysis.relationAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "9차 합화 분석이 현재 8차 RelationAnalysis를 참조하지 않습니다."
            );
        }

        if (
                transformationAnalysis
                        .elementStrengthAnalysis()
                        != advancedAnalysis
                        .elementStrengthAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "9차 합화 분석이 현재 3차 ElementStrengthAnalysis를 참조하지 않습니다."
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

            HeavenlyStem annualStem,

            List<AnnualLuckNatalRelationReference> results

    ) {

        for (
                NatalStemSlot natal
                : natalStems
        ) {

            StemRelationTable
                    .findCombinationElement(
                            natal.stem(),
                            annualStem
                    )
                    .ifPresent(
                            ignored ->
                                    results.add(
                                            relation(
                                                    AnnualLuckNatalRelationLayer
                                                            .HEAVENLY_STEM,
                                                    AnnualLuckNatalRelationKind
                                                            .COMBINATION,
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
                                    annualStem
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
                                AnnualLuckNatalRelationLayer
                                        .HEAVENLY_STEM,
                                AnnualLuckNatalRelationKind
                                        .CLASH,
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
                                AnnualLuckNatalRelationLayer
                                        .HEAVENLY_STEM,
                                AnnualLuckNatalRelationKind
                                        .CONTROL,
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
     * 지지 Pair 관계
     * =================================================
     */

    private void buildBranchPairRelations(

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch annualBranch,

            List<AnnualLuckNatalRelationReference> results

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
                                    annualBranch
                            )
                            .isPresent()
            ) {

                results.add(
                        relation(
                                AnnualLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,
                                AnnualLuckNatalRelationKind
                                        .SIX_COMBINATION,
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
                                    annualBranch
                            )
            ) {

                results.add(
                        relation(
                                AnnualLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,
                                AnnualLuckNatalRelationKind
                                        .CLASH,
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
                                    annualBranch
                            )
            ) {

                results.add(
                        relation(
                                AnnualLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,
                                AnnualLuckNatalRelationKind
                                        .PUNISHMENT,
                                positions,
                                "PUNISHMENT",
                                "형"
                        )
                );
            }

            if (
                    natal.branch()
                            == annualBranch

                            && BranchRelationTable
                            .isSelfPunishmentBranch(
                                    annualBranch
                            )
            ) {

                results.add(
                        relation(
                                AnnualLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,
                                AnnualLuckNatalRelationKind
                                        .SELF_PUNISHMENT,
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
                                    annualBranch
                            )
            ) {

                results.add(
                        relation(
                                AnnualLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,
                                AnnualLuckNatalRelationKind
                                        .HARM,
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
                                    annualBranch
                            )
            ) {

                results.add(
                        relation(
                                AnnualLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,
                                AnnualLuckNatalRelationKind
                                        .BREAK,
                                positions,
                                "BREAK",
                                "파"
                        )
                );
            }

            earthlyBranchThreeCombinationRule
                    .findPartialCombinationType(
                            natal.branch(),
                            annualBranch
                    )
                    .ifPresent(
                            type -> {

                                EarthlyBranchThreeCombinationForm form =
                                        earthlyBranchThreeCombinationRule
                                                .determinePairForm(
                                                        type,
                                                        natal.branch(),
                                                        annualBranch
                                                );

                                results.add(
                                        relation(
                                                AnnualLuckNatalRelationLayer
                                                        .EARTHLY_BRANCH,
                                                AnnualLuckNatalRelationKind
                                                        .THREE_COMBINATION,
                                                positions,
                                                "THREE_COMBINATION",
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
                            natal.branch(),
                            annualBranch
                    )
                    .ifPresent(
                            type -> {

                                EarthlyBranchDirectionalCombinationForm form =
                                        earthlyBranchDirectionalCombinationRule
                                                .determinePairForm(
                                                        type,
                                                        natal.branch(),
                                                        annualBranch
                                                );

                                results.add(
                                        relation(
                                                AnnualLuckNatalRelationLayer
                                                        .EARTHLY_BRANCH,
                                                AnnualLuckNatalRelationKind
                                                        .DIRECTIONAL_COMBINATION,
                                                positions,
                                                "DIRECTIONAL_COMBINATION",
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
    }

    /*
     * =================================================
     * 지지 Group 관계
     * =================================================
     */

    private void buildBranchGroupRelations(

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch annualBranch,

            List<AnnualLuckNatalRelationReference> results

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
                                        annualBranch
                                )
                                .isPresent()
                ) {

                    results.add(
                            relation(
                                    AnnualLuckNatalRelationLayer
                                            .EARTHLY_BRANCH,
                                    AnnualLuckNatalRelationKind
                                            .THREE_COMBINATION,
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
                                        annualBranch
                                )
                                .isPresent()
                ) {

                    results.add(
                            relation(
                                    AnnualLuckNatalRelationLayer
                                            .EARTHLY_BRANCH,
                                    AnnualLuckNatalRelationKind
                                            .DIRECTIONAL_COMBINATION,
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
                                        annualBranch
                                )
                ) {

                    results.add(
                            relation(
                                    AnnualLuckNatalRelationLayer
                                            .EARTHLY_BRANCH,
                                    AnnualLuckNatalRelationKind
                                            .PUNISHMENT,
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

    private List<AnnualLuckNatalTransformationReference>
    buildHeavenlyTransformations(

            NatalChartAdvancedAnalysis advancedAnalysis,

            List<NatalStemSlot> natalStems,

            HeavenlyStem annualStem

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
                                    annualStem
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

        List<AnnualLuckNatalTransformationReference> results =
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
                            annualStem
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

            Set<PalacePosition> natalPositions =
                    Set.of(
                            candidate
                                    .natal()
                                    .position()
                    );

            results.add(
                    new AnnualLuckNatalTransformationReference(
                            AnnualLuckNatalRelationLayer
                                    .HEAVENLY_STEM,
                            natalPositions,
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
                            candidate.targetElement(),
                            targetActivationState,
                            originalFunctionState,
                            participantOriginalFunctionStates(
                                    natalPositions,
                                    originalFunctionState
                            )
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

            HeavenlyStem annualStem

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
                            annualStem
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

    private List<AnnualLuckNatalTransformationReference>
    buildEarthlyTransformations(

            NatalChartAdvancedAnalysis advancedAnalysis,

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch annualBranch

    ) {

        ElementStrengthAnalysis strength =
                advancedAnalysis
                        .elementStrengthAnalysis();

        List<DynamicBranchCandidate> dynamicCandidates =
                buildDynamicBranchCandidates(
                        natalBranches,
                        annualBranch,
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
                                                annualBranch,
                                                candidate
                                        )
                        )
                        .toList();

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

        List<AnnualLuckNatalTransformationReference> results =
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
                        other.identityKey()
                                .equals(
                                        current.identityKey()
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
                        current.targetElement()
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
                                    candidate.baseCandidateRemains(),
                                    candidate.baseMeaningful(),
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
                                    candidate.baseMeaningful(),
                                    candidate.obstructionLevel(),
                                    candidate.baseCandidateRemains()
                            );

            Set<PalacePosition> natalPositions =
                    candidate
                            .natalPositions();

            results.add(
                    new AnnualLuckNatalTransformationReference(
                            AnnualLuckNatalRelationLayer
                                    .EARTHLY_BRANCH,
                            natalPositions,
                            candidate.transformationCode(),
                            candidate.transformationName(),
                            candidate.targetElement(),
                            targetActivationState,
                            originalFunctionState,
                            participantOriginalFunctionStates(
                                    natalPositions,
                                    originalFunctionState
                            )
                    )
            );
        }

        return List.copyOf(
                results
        );
    }

    private List<DynamicBranchCandidate>
    buildDynamicBranchCandidates(

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch annualBranch,

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
                                    annualBranch
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
                                    environment.shareBasisPoints(),
                                    environment.seasonalRate(),
                                    environment.monthBranchPresenceWeight()
                            );

            results.add(
                    new DynamicBranchCandidate(
                            "SIX:"
                                    + natal.position().name()
                                    + ":"
                                    + target.get().name(),
                            EarthlyBranchCombinationCandidateKind
                                    .SIX_COMBINATION,
                            Set.of(
                                    natal.position()
                            ),
                            List.of(
                                    BranchMember.natal(
                                            natal
                                    ),
                                    BranchMember.annual(
                                            annualBranch
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
                                    + natal.position().name()
                                    + ":"
                                    + target.get().name(),
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
                                        annualBranch
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
                                        environment.shareBasisPoints(),
                                        environment.seasonalRate(),
                                        environment.monthBranchPresenceWeight()
                                );

                Set<PalacePosition> positions =
                        Set.of(
                                first.position(),
                                second.position()
                        );

                completeThreeCombinations.add(
                        new CompleteBranchCombination(
                                "THREE:"
                                        + type.get().name(),
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
                                        BranchMember.natal(
                                                first
                                        ),
                                        BranchMember.natal(
                                                second
                                        ),
                                        BranchMember.annual(
                                                annualBranch
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
                                "삼합 국 성립"
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
                                    annualBranch
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

            boolean monthInvolved =
                    natal.position()
                            == PalacePosition.MONTH;

            EarthlyBranchThreeCombinationFormationState state =
                    earthlyBranchThreeCombinationFormationRule
                            .determineState(
                                    form,
                                    false,
                                    monthInvolved,
                                    environment.shareBasisPoints(),
                                    environment.seasonalRate(),
                                    environment.monthBranchPresenceWeight()
                            );

            boolean cardinalIncluded =
                    form
                            == EarthlyBranchThreeCombinationForm
                            .HALF_WITH_CARDINAL;

            results.add(
                    new DynamicBranchCandidate(
                            "THREE_PARTIAL:"
                                    + natal.position().name()
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
                                    BranchMember.natal(
                                            natal
                                    ),
                                    BranchMember.annual(
                                            annualBranch
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
                                    + natal.position().name()
                                    + ":"
                                    + target.name(),
                            cardinalIncluded
                                    ? "반합 국 성립"
                                    : "삼합 부분국"
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
                                        annualBranch
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
                                        environment.shareBasisPoints(),
                                        environment.seasonalRate(),
                                        environment.monthBranchPresenceWeight()
                                );

                Set<PalacePosition> positions =
                        Set.of(
                                first.position(),
                                second.position()
                        );

                completeDirectionalCombinations.add(
                        new CompleteBranchCombination(
                                "DIRECTIONAL:"
                                        + type.get().name(),
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
                                        BranchMember.natal(
                                                first
                                        ),
                                        BranchMember.natal(
                                                second
                                        ),
                                        BranchMember.annual(
                                                annualBranch
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
                                "방합 국 성립"
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
                                    annualBranch
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

            boolean monthInvolved =
                    natal.position()
                            == PalacePosition.MONTH;

            EarthlyBranchDirectionalCombinationFormationState state =
                    earthlyBranchDirectionalCombinationFormationRule
                            .determineState(
                                    form,
                                    false,
                                    monthInvolved,
                                    environment.shareBasisPoints(),
                                    environment.seasonalRate(),
                                    environment.monthBranchPresenceWeight()
                            );

            boolean cardinalIncluded =
                    form
                            == EarthlyBranchDirectionalCombinationForm
                            .PARTIAL_WITH_CARDINAL;

            results.add(
                    new DynamicBranchCandidate(
                            "DIRECTIONAL_PARTIAL:"
                                    + natal.position().name()
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
                                    BranchMember.natal(
                                            natal
                                    ),
                                    BranchMember.annual(
                                            annualBranch
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
                                    + natal.position().name()
                                    + ":"
                                    + target.name(),
                            cardinalIncluded
                                    ? "왕지 포함 부분 방합 국 성립"
                                    : "부분 방합 국 성립"
                    )
            );
        }

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * 지지 합화 방해
     * =================================================
     */

    private DynamicBranchCandidate applyObstruction(

            NatalChartAdvancedAnalysis advancedAnalysis,

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch annualBranch,

            DynamicBranchCandidate candidate

    ) {

        List<BranchMember> allMembers =
                new ArrayList<>();

        for (
                NatalBranchSlot natal
                : natalBranches
        ) {

            allMembers.add(
                    BranchMember.natal(
                            natal
                    )
            );
        }

        allMembers.add(
                BranchMember.annual(
                        annualBranch
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
                        candidate.cardinalMemberAffectedBy(
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
                 * adjacent는 원국 두 궁위 사이의
                 * 기존 방해 관계에만 적용한다.
                 *
                 * 세운 자체에는 원국 위치를 부여하지 않는다.
                 */
                if (
                        !first.annual()
                                && !second.annual()

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
         * 완성 삼형.
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
                            candidate.cardinalMemberAffectedBy(
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
                                affectedCandidateMembers.size(),
                                allKinds.size()
                        );

        return candidate.withObstructionLevel(
                level
        );
    }

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
         * 원국 내부 관계는 기존 8차 결과를 사용한다.
         */
        if (
                !first.annual()
                        && !second.annual()
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
                                        relation.hasClash()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind
                                                    .CLASH
                                    );
                                }

                                if (
                                        relation.hasPunishment()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind
                                                    .PUNISHMENT
                                    );
                                }

                                if (
                                        relation.hasBreak()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind
                                                    .BREAK
                                    );
                                }

                                if (
                                        relation.hasHarm()
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
         * 세운이 참여한 Pair만
         * canonical 관계 Table을 직접 사용한다.
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
     * Candidate Priority
     * =================================================
     */

    private int comparePriority(

            ComparableBranchCandidate first,

            ComparableBranchCandidate second

    ) {

        return earthlyBranchCombinationIntegrationRule
                .comparePriority(
                        first.baseCandidateRemains(),
                        first.baseMeaningful(),
                        first.obstructionLevel(),
                        first.baseStateRank(),
                        first.structuralRank(),
                        first.monthBranchInvolved(),
                        first.cardinalIncluded(),

                        second.baseCandidateRemains(),
                        second.baseMeaningful(),
                        second.obstructionLevel(),
                        second.baseStateRank(),
                        second.structuralRank(),
                        second.monthBranchInvolved(),
                        second.cardinalIncluded()
                );
    }

    private boolean sharesMember(

            ComparableBranchCandidate first,

            ComparableBranchCandidate second

    ) {

        /*
         * 같은 세운에서 생성된 동적 후보끼리는
         * 모두 동일 세운 지지를 공유한다.
         */
        if (
                first.annualInvolved()
                        && second.annualInvolved()
        ) {

            return true;
        }

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
                "ANNUAL:"
                        + candidate.identityKey(),
                true,
                candidate.natalPositions(),
                candidate.targetElement(),
                candidate.baseStateRank(),
                candidate.baseMeaningful(),
                candidate.baseCandidateRemains(),
                candidate.obstructionLevel(),
                candidate.monthBranchInvolved(),
                candidate.cardinalIncluded(),
                candidate.structuralRank()
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
                candidate.targetElement(),
                candidate.baseStateRank(),
                candidate.baseMeaningful(),
                candidate.baseCandidateRemains(),
                candidate.level(),
                candidate.monthBranchInvolved(),
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
     * Natal Slots
     * =================================================
     */

    private List<NatalStemSlot> natalStemSlots(
            PalaceAnalysis natalAnalysis
    ) {

        List<NatalStemSlot> result =
                new ArrayList<>();

        for (
                PalacePosition position
                : natalAnalysis.knownPositions()
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

    private List<NatalBranchSlot> natalBranchSlots(
            PalaceAnalysis natalAnalysis
    ) {

        List<NatalBranchSlot> result =
                new ArrayList<>();

        for (
                PalacePosition position
                : natalAnalysis.knownPositions()
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
     * Environment
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
     * Reference Factory
     * =================================================
     */

    private AnnualLuckNatalRelationReference relation(

            AnnualLuckNatalRelationLayer layer,

            AnnualLuckNatalRelationKind relationKind,

            Set<PalacePosition> positions,

            String code,

            String name

    ) {

        return new AnnualLuckNatalRelationReference(
                layer,
                relationKind,
                positions,
                code,
                name
        );
    }

    /**
     * 현재 14-4 동적 합화 계산에서는
     * 기존 9차 Effect Rule이
     * Candidate 수준 OriginalFunctionState를
     * 반환한다.
     *
     * 따라서 현재는 관련 원국 Participant에
     * 같은 State를 명시적으로 배치한다.
     *
     * 중요한 점은 Representation에서:
     *
     * annualOriginalFunctionState
     *
     * 와
     *
     * natalOriginalFunctionStates
     *
     * 를 분리해 두는 것이다.
     */
    private Map<
            PalacePosition,
            TransformationOriginalFunctionState
            >
    participantOriginalFunctionStates(

            Set<PalacePosition> positions,

            TransformationOriginalFunctionState state

    ) {

        Objects.requireNonNull(
                state,
                "Participant OriginalFunctionState가 필요합니다."
        );

        EnumMap<
                PalacePosition,
                TransformationOriginalFunctionState
                > result =
                new EnumMap<>(
                        PalacePosition.class
                );

        for (
                PalacePosition position
                : positions
        ) {

            result.put(
                    position,
                    state
            );
        }

        return Map.copyOf(
                result
        );
    }

    private String positionKey(
            Set<PalacePosition> positions
    ) {

        return positions
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                PalacePosition::order
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
     * Internal Models
     * =================================================
     */

    private record NatalStemSlot(

            PalacePosition position,

            HeavenlyStem stem

    ) {

        private NatalStemSlot {

            Objects.requireNonNull(
                    position
            );

            Objects.requireNonNull(
                    stem
            );
        }
    }

    private record NatalBranchSlot(

            PalacePosition position,

            EarthlyBranch branch

    ) {

        private NatalBranchSlot {

            Objects.requireNonNull(
                    position
            );

            Objects.requireNonNull(
                    branch
            );
        }
    }

    private record DynamicStemCandidate(

            NatalStemSlot natal,

            FiveElement targetElement,

            HeavenlyStemCombinationApplicability
            applicability

    ) {
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
    }

    private record ComparableBranchCandidate(

            String identityKey,

            boolean annualInvolved,

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

            natalPositions =
                    Set.copyOf(
                            natalPositions
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

            natalPositions =
                    Set.copyOf(
                            natalPositions
                    );

            members =
                    List.copyOf(
                            members
                    );

            Objects.requireNonNull(
                    kind
            );

            Objects.requireNonNull(
                    targetElement
            );

            Objects.requireNonNull(
                    obstructionLevel
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

    private record BranchMember(

            PalacePosition natalPosition,

            EarthlyBranch branch,

            boolean annual

    ) {

        private static BranchMember natal(
                NatalBranchSlot slot
        ) {

            return new BranchMember(
                    slot.position(),
                    slot.branch(),
                    false
            );
        }

        private static BranchMember annual(
                EarthlyBranch branch
        ) {

            return new BranchMember(
                    null,
                    branch,
                    true
            );
        }

        private String memberKey() {

            return annual
                    ? "ANNUAL"
                    : "NATAL:"
                    + natalPosition.name();
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
                    annual
                            || other.annual
            ) {

                return annual
                        && other.annual;
            }

            return natalPosition
                    == other.natalPosition;
        }
    }
}