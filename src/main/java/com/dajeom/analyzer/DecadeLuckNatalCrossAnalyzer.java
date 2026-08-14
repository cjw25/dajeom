package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.DecadeLuckNatalCrossResolution;
import com.dajeom.model.DecadeLuckNatalRelationReference;
import com.dajeom.model.DecadeLuckNatalTransformationReference;
import com.dajeom.model.DecadeLuckPeriod;
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
import com.dajeom.type.DecadeLuckNatalRelationLayer;
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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 13차-5.
 *
 * 원국 × 대운 관계 및 합화 정밀 분석.
 *
 * =================================================
 *
 * 핵심 정책
 *
 * 1.
 * 대운은 YEAR/MONTH/DAY/HOUR가 아니다.
 *
 * 따라서 대운을 기존 원국 궁위 중 하나로
 * 위장하지 않는다.
 *
 * 2.
 * 대운이 참여하는 관계의 공간적:
 *
 * - adjacent
 * - compact
 *
 * 조건은 false로 처리한다.
 *
 * 이것은 "대운이 멀리 있다"라는 명리 단정이 아니라,
 * 원국의 물리적 궁위 인접성을 대운에
 * 허위 부여하지 않는 보수적 Boundary 정책이다.
 *
 * 3.
 * 원국 MONTH/DAY가 실제 구성원인 경우
 * 월지/일지 관여 Evidence는 그대로 보존한다.
 *
 * 4.
 * 합 관계 존재와 실제 합화 활성은 분리한다.
 *
 * 5.
 * 기존 9차의:
 *
 * - 목표 오행 기존 세력
 * - 계절 지지
 * - 월지 기반
 * - 천간 경쟁
 * - 천간 충·극 방해
 * - 지지 충·형·파·해 방해
 * - 결합 수렴/경쟁
 * - 최종 목표 오행 활성
 * - 원래 기능 감소
 *
 * 판정축을 재사용한다.
 */
@Component
public class DecadeLuckNatalCrossAnalyzer {

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

    public DecadeLuckNatalCrossAnalyzer(

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

    public DecadeLuckNatalCrossResolution analyze(

            NatalChartAdvancedAnalysis advancedAnalysis,

            PalaceAnalysis natalAnalysis,

            DecadeLuckPeriod period

    ) {

        validateInputs(
                advancedAnalysis,
                natalAnalysis,
                period
        );

        List<NatalStemSlot> natalStems =
                natalStemSlots(
                        natalAnalysis
                );

        List<NatalBranchSlot> natalBranches =
                natalBranchSlots(
                        natalAnalysis
                );

        HeavenlyStem luckStem =
                period
                        .pillar()
                        .stem();

        EarthlyBranch luckBranch =
                period
                        .pillar()
                        .branch();

        List<DecadeLuckNatalRelationReference> relations =
                new ArrayList<>();

        buildStemRelations(
                natalStems,
                luckStem,
                relations
        );

        buildBranchPairRelations(
                natalBranches,
                luckBranch,
                relations
        );

        buildBranchGroupRelations(
                natalBranches,
                luckBranch,
                relations
        );

        List<DecadeLuckNatalTransformationReference>
                transformations =
                new ArrayList<>();

        transformations.addAll(
                buildHeavenlyTransformations(
                        advancedAnalysis,
                        natalStems,
                        luckStem
                )
        );

        transformations.addAll(
                buildEarthlyTransformations(
                        advancedAnalysis,
                        natalBranches,
                        luckBranch
                )
        );

        /*
         * Java generic inference가 첫 람다를 Object로
         * 추론하지 않도록 타입을 명시한다.
         */
        relations.sort(
                Comparator
                        .comparingInt(
                                (
                                        DecadeLuckNatalRelationReference reference
                                ) ->
                                        reference
                                                .layer()
                                                .ordinal()
                        )
                        .thenComparing(
                                (
                                        DecadeLuckNatalRelationReference reference
                                ) ->
                                        positionKey(
                                                reference
                                                        .natalPositions()
                                        )
                        )
                        .thenComparing(
                                DecadeLuckNatalRelationReference
                                        ::relationCode
                        )
        );

        transformations.sort(
                Comparator
                        .comparing(
                                (
                                        DecadeLuckNatalTransformationReference reference
                                ) ->
                                        positionKey(
                                                reference
                                                        .natalPositions()
                                        )
                        )
                        .thenComparing(
                                DecadeLuckNatalTransformationReference
                                        ::transformationCode
                        )
        );

        return new DecadeLuckNatalCrossResolution(
                period,
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

            DecadeLuckPeriod period

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "13-5에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "13-5에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "13-5에는 DecadeLuckPeriod가 필요합니다."
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

            HeavenlyStem luckStem,

            List<DecadeLuckNatalRelationReference> results

    ) {

        for (
                NatalStemSlot natal
                : natalStems
        ) {

            StemRelationTable
                    .findCombinationElement(
                            natal.stem(),
                            luckStem
                    )
                    .ifPresent(
                            ignored ->
                                    results.add(
                                            relation(
                                                    DecadeLuckNatalRelationLayer
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
                                    luckStem
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
                                DecadeLuckNatalRelationLayer
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
                                DecadeLuckNatalRelationLayer
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
     * 지지 Pair 관계
     * =================================================
     */

    private void buildBranchPairRelations(

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch luckBranch,

            List<DecadeLuckNatalRelationReference> results

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
                                    luckBranch
                            )
                            .isPresent()
            ) {

                results.add(
                        relation(
                                DecadeLuckNatalRelationLayer
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
                                    luckBranch
                            )
            ) {

                results.add(
                        relation(
                                DecadeLuckNatalRelationLayer
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
                                    luckBranch
                            )
            ) {

                results.add(
                        relation(
                                DecadeLuckNatalRelationLayer
                                        .EARTHLY_BRANCH,
                                positions,
                                "PUNISHMENT",
                                "형"
                        )
                );
            }

            if (
                    natal.branch()
                            == luckBranch

                            && BranchRelationTable
                            .isSelfPunishmentBranch(
                                    luckBranch
                            )
            ) {

                results.add(
                        relation(
                                DecadeLuckNatalRelationLayer
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
                                    luckBranch
                            )
            ) {

                results.add(
                        relation(
                                DecadeLuckNatalRelationLayer
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
                                    luckBranch
                            )
            ) {

                results.add(
                        relation(
                                DecadeLuckNatalRelationLayer
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
                            luckBranch
                    )
                    .ifPresent(
                            type -> {

                                EarthlyBranchThreeCombinationForm form =
                                        earthlyBranchThreeCombinationRule
                                                .determinePairForm(
                                                        type,
                                                        natal.branch(),
                                                        luckBranch
                                                );

                                results.add(
                                        relation(
                                                DecadeLuckNatalRelationLayer
                                                        .EARTHLY_BRANCH,
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
                            luckBranch
                    )
                    .ifPresent(
                            type -> {

                                EarthlyBranchDirectionalCombinationForm form =
                                        earthlyBranchDirectionalCombinationRule
                                                .determinePairForm(
                                                        type,
                                                        natal.branch(),
                                                        luckBranch
                                                );

                                results.add(
                                        relation(
                                                DecadeLuckNatalRelationLayer
                                                        .EARTHLY_BRANCH,
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
     * 지지 3자 관계
     * =================================================
     */

    private void buildBranchGroupRelations(

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch luckBranch,

            List<DecadeLuckNatalRelationReference> results

    ) {

        for (
                int firstIndex = 0;
                firstIndex < natalBranches.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex
                            < natalBranches.size();
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
                                        luckBranch
                                )
                                .isPresent()
                ) {

                    results.add(
                            relation(
                                    DecadeLuckNatalRelationLayer
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
                                        luckBranch
                                )
                                .isPresent()
                ) {

                    results.add(
                            relation(
                                    DecadeLuckNatalRelationLayer
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
                                        luckBranch
                                )
                ) {

                    results.add(
                            relation(
                                    DecadeLuckNatalRelationLayer
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
     * 천간 합화
     * =================================================
     */

    private List<DecadeLuckNatalTransformationReference>
    buildHeavenlyTransformations(

            NatalChartAdvancedAnalysis advancedAnalysis,

            List<NatalStemSlot> natalStems,

            HeavenlyStem luckStem

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
                                    luckStem
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
                                    environment
                                            .shareBasisPoints(),
                                    environment
                                            .seasonalRate(),
                                    environment
                                            .monthBranchPresenceWeight()
                            );

            candidates.add(
                    new DynamicStemCandidate(
                            natal,
                            target.get(),
                            applicability
                    )
            );
        }

        List<DecadeLuckNatalTransformationReference> results =
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
                            luckStem
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
                    new DecadeLuckNatalTransformationReference(
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
                            targetActivationState.active(),
                            originalFunctionState.reduced()
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

            HeavenlyStem luckStem

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
                            luckStem
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
     * 지지 합화
     * =================================================
     */

    private List<DecadeLuckNatalTransformationReference>
    buildEarthlyTransformations(

            NatalChartAdvancedAnalysis advancedAnalysis,

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch luckBranch

    ) {

        ElementStrengthAnalysis strength =
                advancedAnalysis
                        .elementStrengthAnalysis();

        List<DynamicBranchCandidate> dynamicCandidates =
                buildDynamicBranchCandidates(
                        natalBranches,
                        luckBranch,
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
                                                luckBranch,
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

        List<DecadeLuckNatalTransformationReference> results =
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
                                    candidate
                                            .obstructionLevel(),
                                    candidate.baseCandidateRemains()
                            );

            results.add(
                    new DecadeLuckNatalTransformationReference(
                            candidate.natalPositions(),
                            candidate.transformationCode(),
                            candidate.transformationName(),
                            candidate
                                    .targetElement()
                                    .getKoreanName(),
                            targetActivationState.active(),
                            originalFunctionState.reduced()
                    )
            );
        }

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * 지지 Candidate 생성
     * =================================================
     */

    private List<DynamicBranchCandidate>
    buildDynamicBranchCandidates(

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch luckBranch,

            ElementStrengthAnalysis strength

    ) {

        List<DynamicBranchCandidate> results =
                new ArrayList<>();

        /*
         * 六合
         */
        for (
                NatalBranchSlot natal
                : natalBranches
        ) {

            Optional<FiveElement> target =
                    BranchRelationTable
                            .findSixCombinationElement(
                                    natal.branch(),
                                    luckBranch
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
                                    environment
                                            .monthBranchPresenceWeight()
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
                                    BranchMember.luck(
                                            luckBranch
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
         * 완전 三合
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
                                        luckBranch
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
                                        BranchMember.luck(
                                                luckBranch
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
         * 부분 三合
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
                                    luckBranch
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
                                    luckBranch
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
                                    environment
                                            .monthBranchPresenceWeight()
                            );

            boolean cardinalIncluded =
                    form
                            == EarthlyBranchThreeCombinationForm
                            .HALF_WITH_CARDINAL;

            int structuralRank =
                    cardinalIncluded
                            ? 3
                            : 1;

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
                                    BranchMember.luck(
                                            luckBranch
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
                            structuralRank,
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
         * 완전 方合
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
                                        luckBranch
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
                                        BranchMember.luck(
                                                luckBranch
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
         * 부분 方合
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
                                    luckBranch
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
                                    luckBranch
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
                                    BranchMember.luck(
                                            luckBranch
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
     * 9-7 방해
     * =================================================
     */

    private DynamicBranchCandidate applyObstruction(

            NatalChartAdvancedAnalysis advancedAnalysis,

            List<NatalBranchSlot> natalBranches,

            EarthlyBranch luckBranch,

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
                BranchMember.luck(
                        luckBranch
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
                        candidate.members()
                                .stream()
                                .filter(
                                        member ->
                                                member.sameMember(
                                                        first
                                                )
                                                        || member
                                                        .sameMember(
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

                if (
                        !first.luck()
                                && !second.luck()

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
                            candidate.members()
                                    .stream()
                                    .filter(
                                            member ->
                                                    member.sameMember(
                                                            first
                                                    )
                                                            || member
                                                            .sameMember(
                                                                    second
                                                            )
                                                            || member
                                                            .sameMember(
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

        if (
                !first.luck()
                        && !second.luck()
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
                                            EarthlyBranchRelationKind.CLASH
                                    );
                                }

                                if (
                                        relation.hasPunishment()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind.PUNISHMENT
                                    );
                                }

                                if (
                                        relation.hasBreak()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind.BREAK
                                    );
                                }

                                if (
                                        relation.hasHarm()
                                ) {

                                    result.add(
                                            EarthlyBranchRelationKind.HARM
                                    );
                                }
                            }
                    );

            return Set.copyOf(
                    result
            );
        }

        if (
                BranchRelationTable
                        .isClash(
                                first.branch(),
                                second.branch()
                        )
        ) {

            result.add(
                    EarthlyBranchRelationKind.CLASH
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
                    EarthlyBranchRelationKind.PUNISHMENT
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
                    EarthlyBranchRelationKind.PUNISHMENT
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
                    EarthlyBranchRelationKind.BREAK
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
                    EarthlyBranchRelationKind.HARM
            );
        }

        return Set.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 9-8 비교
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

        if (
                first.luckInvolved()
                        && second.luckInvolved()
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
                "DYNAMIC:"
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
     * 원국 Source
     * =================================================
     */

    private List<NatalStemSlot> natalStemSlots(
            PalaceAnalysis natalAnalysis
    ) {

        List<NatalStemSlot> result =
                new ArrayList<>();

        for (
                PalacePosition position
                : knownPositions(
                natalAnalysis.completeChart()
        )
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
                : knownPositions(
                natalAnalysis.completeChart()
        )
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

    private List<PalacePosition> knownPositions(
            boolean completeChart
    ) {

        List<PalacePosition> result =
                new ArrayList<>();

        result.add(
                PalacePosition.YEAR
        );

        result.add(
                PalacePosition.MONTH
        );

        result.add(
                PalacePosition.DAY
        );

        if (
                completeChart
        ) {

            result.add(
                    PalacePosition.HOUR
            );
        }

        return List.copyOf(
                result
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

    /*
     * =================================================
     * Reference helper
     * =================================================
     */

    private DecadeLuckNatalRelationReference relation(

            DecadeLuckNatalRelationLayer layer,

            Set<PalacePosition> positions,

            String code,

            String name

    ) {

        return new DecadeLuckNatalRelationReference(
                layer,
                positions,
                code,
                name
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
     * 내부 immutable Evidence
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

            boolean luckInvolved,

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

            boolean luck

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

        private static BranchMember luck(
                EarthlyBranch branch
        ) {

            return new BranchMember(
                    null,
                    branch,
                    true
            );
        }

        private String memberKey() {

            return luck
                    ? "LUCK"
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
                    luck
                            || other.luck
            ) {

                return luck
                        && other.luck;
            }

            return natalPosition
                    == other.natalPosition;
        }
    }
}