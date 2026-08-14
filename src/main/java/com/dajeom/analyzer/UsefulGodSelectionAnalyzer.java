package com.dajeom.analyzer;

import com.dajeom.model.BalancingUsefulGodAnalysis;
import com.dajeom.model.BalancingUsefulGodCandidate;
import com.dajeom.model.ClimateUsefulGodAnalysis;
import com.dajeom.model.ClimateUsefulGodCandidate;
import com.dajeom.model.FollowStructureUsefulGodAnalysis;
import com.dajeom.model.FollowStructureUsefulGodCandidate;
import com.dajeom.model.PassageUsefulGodAnalysis;
import com.dajeom.model.PassageUsefulGodCandidate;
import com.dajeom.model.RemedyUsefulGodAnalysis;
import com.dajeom.model.RemedyUsefulGodCandidate;
import com.dajeom.model.StructureUsefulGodAnalysis;
import com.dajeom.model.StructureUsefulGodCandidate;
import com.dajeom.model.UsefulGodCandidate;
import com.dajeom.model.UsefulGodMethodAnalysis;
import com.dajeom.model.UsefulGodMethodContribution;
import com.dajeom.model.UsefulGodSelectionAnalysis;
import com.dajeom.rule.UsefulGodSelectionRule;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodCandidateRole;
import com.dajeom.type.UsefulGodConvergenceLevel;
import com.dajeom.type.UsefulGodMethodType;
import com.dajeom.type.UsefulGodSourceCandidateState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-8.
 *
 * 7-2 ~ 7-7의 여섯 용신법 결과를
 * 五行별로 하나씩 모아 통합한다.
 *
 * =================================================
 *
 * 처리
 *
 * 木
 * ├─ 억부
 * ├─ 조후
 * ├─ 격국
 * ├─ 병약
 * ├─ 통관
 * └─ 종격
 *
 * 火
 * ├─ ...
 *
 * ...
 *
 * ↓
 *
 * 각 오행별 수렴/충돌 판정
 *
 * ↓
 *
 * PRIMARY_USEFUL_GOD
 * SUPPORTING_USEFUL_GOD
 * DEFERRED
 * NOT_SELECTED
 *
 * =================================================
 *
 * 숫자 점수 총합으로 용신을 고르지 않는다.
 *
 * =================================================
 *
 * 종격 모드에서는 FOLLOW_STRUCTURE CORE가
 * 일반 용신법보다 우선한다.
 *
 * =================================================
 *
 * 복수 PRIMARY_USEFUL_GOD를 허용한다.
 *
 * 복수 후보가 남았다고 해서
 * FiveElement.ordinal() 등으로
 * 하나를 강제 선택하지 않는다.
 */
@Component
public class UsefulGodSelectionAnalyzer {

    private final BalancingUsefulGodAnalyzer
            balancingUsefulGodAnalyzer;

    private final ClimateUsefulGodAnalyzer
            climateUsefulGodAnalyzer;

    private final StructureUsefulGodAnalyzer
            structureUsefulGodAnalyzer;

    private final RemedyUsefulGodAnalyzer
            remedyUsefulGodAnalyzer;

    private final PassageUsefulGodAnalyzer
            passageUsefulGodAnalyzer;

    private final FollowStructureUsefulGodAnalyzer
            followStructureUsefulGodAnalyzer;

    private final UsefulGodSelectionRule
            usefulGodSelectionRule;

    public UsefulGodSelectionAnalyzer(

            BalancingUsefulGodAnalyzer
                    balancingUsefulGodAnalyzer,

            ClimateUsefulGodAnalyzer
                    climateUsefulGodAnalyzer,

            StructureUsefulGodAnalyzer
                    structureUsefulGodAnalyzer,

            RemedyUsefulGodAnalyzer
                    remedyUsefulGodAnalyzer,

            PassageUsefulGodAnalyzer
                    passageUsefulGodAnalyzer,

            FollowStructureUsefulGodAnalyzer
                    followStructureUsefulGodAnalyzer,

            UsefulGodSelectionRule
                    usefulGodSelectionRule

    ) {

        this.balancingUsefulGodAnalyzer =
                Objects.requireNonNull(
                        balancingUsefulGodAnalyzer,
                        "BalancingUsefulGodAnalyzer가 필요합니다."
                );

        this.climateUsefulGodAnalyzer =
                Objects.requireNonNull(
                        climateUsefulGodAnalyzer,
                        "ClimateUsefulGodAnalyzer가 필요합니다."
                );

        this.structureUsefulGodAnalyzer =
                Objects.requireNonNull(
                        structureUsefulGodAnalyzer,
                        "StructureUsefulGodAnalyzer가 필요합니다."
                );

        this.remedyUsefulGodAnalyzer =
                Objects.requireNonNull(
                        remedyUsefulGodAnalyzer,
                        "RemedyUsefulGodAnalyzer가 필요합니다."
                );

        this.passageUsefulGodAnalyzer =
                Objects.requireNonNull(
                        passageUsefulGodAnalyzer,
                        "PassageUsefulGodAnalyzer가 필요합니다."
                );

        this.followStructureUsefulGodAnalyzer =
                Objects.requireNonNull(
                        followStructureUsefulGodAnalyzer,
                        "FollowStructureUsefulGodAnalyzer가 필요합니다."
                );

        this.usefulGodSelectionRule =
                Objects.requireNonNull(
                        usefulGodSelectionRule,
                        "UsefulGodSelectionRule이 필요합니다."
                );
    }

    public UsefulGodSelectionAnalysis analyze(
            UsefulGodMethodAnalysis methodAnalysis
    ) {

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        /*
         * =================================================
         * 7-2 ~ 7-7
         * =================================================
         */
        BalancingUsefulGodAnalysis balancing =
                balancingUsefulGodAnalyzer
                        .analyze(
                                methodAnalysis
                        );

        ClimateUsefulGodAnalysis climate =
                climateUsefulGodAnalyzer
                        .analyze(
                                methodAnalysis
                        );

        StructureUsefulGodAnalysis structure =
                structureUsefulGodAnalyzer
                        .analyze(
                                methodAnalysis
                        );

        RemedyUsefulGodAnalysis remedy =
                remedyUsefulGodAnalyzer
                        .analyze(
                                methodAnalysis
                        );

        PassageUsefulGodAnalysis passage =
                passageUsefulGodAnalyzer
                        .analyze(
                                methodAnalysis
                        );

        FollowStructureUsefulGodAnalysis follow =
                followStructureUsefulGodAnalyzer
                        .analyze(
                                methodAnalysis
                        );

        boolean followStructureMode =
                methodAnalysis
                        .followStructureMode();

        List<UsefulGodCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            BalancingUsefulGodCandidate balancingCandidate =
                    balancing
                            .candidateOf(
                                    element
                            )
                            .orElseThrow();

            ClimateUsefulGodCandidate climateCandidate =
                    climate
                            .candidateOf(
                                    element
                            )
                            .orElseThrow();

            StructureUsefulGodCandidate structureCandidate =
                    structure
                            .candidateOf(
                                    element
                            )
                            .orElseThrow();

            RemedyUsefulGodCandidate remedyCandidate =
                    remedy
                            .candidateOf(
                                    element
                            )
                            .orElseThrow();

            PassageUsefulGodCandidate passageCandidate =
                    passage
                            .candidateOf(
                                    element
                            )
                            .orElseThrow();

            FollowStructureUsefulGodCandidate followCandidate =
                    follow
                            .candidateOf(
                                    element
                            )
                            .orElseThrow();

            /*
             * =================================================
             * 동일 五行 세력 검증
             * =================================================
             */
            int existingShare =
                    balancingCandidate
                            .existingShareBasisPoints();

            validateShareConsistency(
                    element,
                    existingShare,
                    climateCandidate
                            .existingShareBasisPoints(),
                    structureCandidate
                            .existingShareBasisPoints(),
                    remedyCandidate
                            .existingShareBasisPoints(),
                    passageCandidate
                            .existingShareBasisPoints(),
                    followCandidate
                            .existingShareBasisPoints()
            );

            ClimateElementPresenceLevel presenceLevel =
                    balancingCandidate
                            .presenceLevel();

            if (
                    ClimateElementPresenceLevel
                            .fromShareBasisPoints(
                                    existingShare
                            )
                            != presenceLevel
            ) {

                throw new IllegalStateException(
                        "7-8 오행 세력 수준이 실제 비중과 일치하지 않습니다: "
                                + element
                );
            }

            /*
             * =================================================
             * 여섯 용신법 Contribution 정규화
             * =================================================
             */
            List<UsefulGodMethodContribution> contributions =
                    List.of(

                            createBalancingContribution(
                                    balancingCandidate
                            ),

                            createClimateContribution(
                                    climateCandidate
                            ),

                            createStructureContribution(
                                    structureCandidate
                            ),

                            createRemedyContribution(
                                    remedyCandidate
                            ),

                            createPassageContribution(
                                    passageCandidate
                            ),

                            createFollowContribution(
                                    followCandidate
                            )
                    );

            UsefulGodConvergenceLevel convergenceLevel =
                    usefulGodSelectionRule
                            .determineConvergenceLevel(
                                    contributions,
                                    followStructureMode
                            );

            UsefulGodCandidateRole role =
                    usefulGodSelectionRule
                            .determineRole(
                                    convergenceLevel,
                                    contributions,
                                    presenceLevel,
                                    followStructureMode
                            );

            candidates.add(
                    new UsefulGodCandidate(
                            element,
                            role,
                            convergenceLevel,
                            presenceLevel,
                            existingShare,
                            contributions,
                            followStructureMode,
                            usefulGodSelectionRule
                                    .reasonsFor(
                                            convergenceLevel,
                                            contributions,
                                            presenceLevel,
                                            followStructureMode,
                                            role
                                    )
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 이 정렬은 점수 총합이 아니다.
         *
         * 최종 역할
         * →
         * 수렴 단계
         * →
         * CORE PRIMARY 방법 수
         * →
         * 의미 있는 선택 방법 수
         *
         * 순서로 보기 좋게 배치한다.
         *
         * 동일 단계라고 해서
         * 첫 번째 요소만 주용신으로 확정하지 않는다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        UsefulGodCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        UsefulGodCandidate candidate
                                                ) ->
                                                        candidate
                                                                .convergenceLevel()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                UsefulGodCandidate
                                                        ::corePrimaryMethodCount
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                UsefulGodCandidate
                                                        ::meaningfulSelectedMethodCount
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new UsefulGodSelectionAnalysis(
                methodAnalysis.dayMaster(),
                methodAnalysis.dayMasterElement(),
                methodAnalysis.monthBranch(),
                methodAnalysis,
                balancing,
                climate,
                structure,
                remedy,
                passage,
                follow,
                candidates,
                followStructureMode,
                methodAnalysis.completeChart()
        );
    }

    /*
     * =================================================
     * 억부 Contribution
     * =================================================
     */
    private UsefulGodMethodContribution
    createBalancingContribution(
            BalancingUsefulGodCandidate candidate
    ) {

        boolean applicable =
                candidate
                        .methodApplicability()
                        .applicable();

        return new UsefulGodMethodContribution(
                UsefulGodMethodType.BALANCING,
                candidate.methodApplicability(),
                balancingStateOf(
                        candidate
                ),
                false,
                applicable
                        && (
                        candidate.saturated()
                                || candidate
                                .followStructureSuppressed()
                ),
                false
        );
    }

    /*
     * =================================================
     * 조후 Contribution
     * =================================================
     */
    private UsefulGodMethodContribution
    createClimateContribution(
            ClimateUsefulGodCandidate candidate
    ) {

        boolean applicable =
                candidate
                        .methodApplicability()
                        .applicable();

        boolean directConflict =
                applicable
                        && (
                        candidate.sourceConflict()
                                || candidate
                                .crossValidationConflict()
                );

        boolean caution =
                applicable
                        && (
                        candidate.saturated()
                                || candidate
                                .crossValidationStrained()
                                || candidate.competingClimate()
                );

        return new UsefulGodMethodContribution(
                UsefulGodMethodType.CLIMATE_CONTROL,
                candidate.methodApplicability(),
                climateStateOf(
                        candidate
                ),
                directConflict,
                caution,
                false
        );
    }

    /*
     * =================================================
     * 격국 Contribution
     * =================================================
     */
    private UsefulGodMethodContribution
    createStructureContribution(
            StructureUsefulGodCandidate candidate
    ) {

        boolean applicable =
                candidate
                        .methodApplicability()
                        .applicable();

        boolean structureContradictory =
                candidate
                        .sourceValidatedCandidate()
                        .structureState()
                        .contradictory();

        boolean structureStrained =
                candidate
                        .sourceValidatedCandidate()
                        .structureState()
                        .strained();

        boolean directConflict =
                applicable
                        && (
                        candidate.pureDamageElement()
                                || candidate
                                .strengthContradictory()
                                || structureContradictory
                );

        boolean caution =
                applicable
                        && (
                        candidate.mixedStructureEvidence()
                                || candidate
                                .strengthStrained()
                                || structureStrained
                                || candidate
                                .specialStructureReview()
                );

        return new UsefulGodMethodContribution(
                UsefulGodMethodType.STRUCTURE,
                candidate.methodApplicability(),
                structureStateOf(
                        candidate
                ),
                directConflict,
                caution,
                false
        );
    }

    /*
     * =================================================
     * 병약 Contribution
     * =================================================
     */
    private UsefulGodMethodContribution
    createRemedyContribution(
            RemedyUsefulGodCandidate candidate
    ) {

        boolean applicable =
                candidate
                        .methodApplicability()
                        .applicable();

        boolean directConflict =
                applicable
                        && (
                        candidate.sourceConflict()
                                || candidate
                                .crossValidationConflict()
                );

        boolean caution =
                applicable
                        && (
                        candidate.saturated()
                                || candidate
                                .crossValidationStrained()
                );

        return new UsefulGodMethodContribution(
                UsefulGodMethodType.REMEDY,
                candidate.methodApplicability(),
                remedyStateOf(
                        candidate
                ),
                directConflict,
                caution,
                false
        );
    }

    /*
     * =================================================
     * 통관 Contribution
     * =================================================
     */
    private UsefulGodMethodContribution
    createPassageContribution(
            PassageUsefulGodCandidate candidate
    ) {

        boolean applicable =
                candidate
                        .methodApplicability()
                        .applicable();

        boolean directConflict =
                applicable
                        && (
                        candidate.sourceConflict()
                                || candidate
                                .crossValidationConflict()
                );

        boolean caution =
                applicable
                        && (
                        candidate.saturated()
                                || candidate
                                .crossValidationStrained()
                );

        return new UsefulGodMethodContribution(
                UsefulGodMethodType.PASSAGE,
                candidate.methodApplicability(),
                passageStateOf(
                        candidate
                ),
                directConflict,
                caution,
                false
        );
    }

    /*
     * =================================================
     * 종격 Contribution
     * =================================================
     */
    private UsefulGodMethodContribution
    createFollowContribution(
            FollowStructureUsefulGodCandidate candidate
    ) {

        boolean applicable =
                candidate
                        .methodApplicability()
                        .applicable();

        boolean directConflict =
                applicable
                        && (
                        candidate.breaksFollowFlow()
                                || candidate
                                .crossValidationConflict()
                );

        boolean caution =
                applicable
                        && candidate
                        .crossValidationStrained();

        return new UsefulGodMethodContribution(
                UsefulGodMethodType.FOLLOW_STRUCTURE,
                candidate.methodApplicability(),
                followStateOf(
                        candidate
                ),
                directConflict,
                caution,
                applicable
                        && candidate
                        .breaksFollowFlow()
        );
    }

    private UsefulGodSourceCandidateState balancingStateOf(
            BalancingUsefulGodCandidate candidate
    ) {

        if (
                candidate.primary()
        ) {

            return UsefulGodSourceCandidateState.PRIMARY;
        }

        if (
                candidate.secondary()
        ) {

            return UsefulGodSourceCandidateState.SECONDARY;
        }

        if (
                candidate.conditional()
        ) {

            return UsefulGodSourceCandidateState.CONDITIONAL;
        }

        return UsefulGodSourceCandidateState.NOT_SELECTED;
    }

    private UsefulGodSourceCandidateState climateStateOf(
            ClimateUsefulGodCandidate candidate
    ) {

        if (
                candidate.primary()
        ) {

            return UsefulGodSourceCandidateState.PRIMARY;
        }

        if (
                candidate.secondary()
        ) {

            return UsefulGodSourceCandidateState.SECONDARY;
        }

        if (
                candidate.conditional()
        ) {

            return UsefulGodSourceCandidateState.CONDITIONAL;
        }

        return UsefulGodSourceCandidateState.NOT_SELECTED;
    }

    private UsefulGodSourceCandidateState structureStateOf(
            StructureUsefulGodCandidate candidate
    ) {

        if (
                candidate.primary()
        ) {

            return UsefulGodSourceCandidateState.PRIMARY;
        }

        if (
                candidate.secondary()
        ) {

            return UsefulGodSourceCandidateState.SECONDARY;
        }

        if (
                candidate.conditional()
        ) {

            return UsefulGodSourceCandidateState.CONDITIONAL;
        }

        return UsefulGodSourceCandidateState.NOT_SELECTED;
    }

    private UsefulGodSourceCandidateState remedyStateOf(
            RemedyUsefulGodCandidate candidate
    ) {

        if (
                candidate.primary()
        ) {

            return UsefulGodSourceCandidateState.PRIMARY;
        }

        if (
                candidate.secondary()
        ) {

            return UsefulGodSourceCandidateState.SECONDARY;
        }

        if (
                candidate.conditional()
        ) {

            return UsefulGodSourceCandidateState.CONDITIONAL;
        }

        return UsefulGodSourceCandidateState.NOT_SELECTED;
    }

    private UsefulGodSourceCandidateState passageStateOf(
            PassageUsefulGodCandidate candidate
    ) {

        if (
                candidate.primary()
        ) {

            return UsefulGodSourceCandidateState.PRIMARY;
        }

        if (
                candidate.secondary()
        ) {

            return UsefulGodSourceCandidateState.SECONDARY;
        }

        if (
                candidate.conditional()
        ) {

            return UsefulGodSourceCandidateState.CONDITIONAL;
        }

        return UsefulGodSourceCandidateState.NOT_SELECTED;
    }

    private UsefulGodSourceCandidateState followStateOf(
            FollowStructureUsefulGodCandidate candidate
    ) {

        if (
                candidate.primary()
        ) {

            return UsefulGodSourceCandidateState.PRIMARY;
        }

        if (
                candidate.secondary()
        ) {

            return UsefulGodSourceCandidateState.SECONDARY;
        }

        if (
                candidate.conditional()
        ) {

            return UsefulGodSourceCandidateState.CONDITIONAL;
        }

        return UsefulGodSourceCandidateState.NOT_SELECTED;
    }

    private void validateShareConsistency(

            FiveElement element,

            int balancingShare,

            int climateShare,

            int structureShare,

            int remedyShare,

            int passageShare,

            int followShare

    ) {

        if (
                balancingShare != climateShare
                        || balancingShare != structureShare
                        || balancingShare != remedyShare
                        || balancingShare != passageShare
                        || balancingShare != followShare
        ) {

            throw new IllegalStateException(
                    "7-2~7-7 오행 비중이 서로 일치하지 않습니다: "
                            + element
            );
        }
    }
}