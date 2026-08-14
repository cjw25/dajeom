package com.dajeom.analyzer;

import com.dajeom.calculator.StructureTenGodCalculator;
import com.dajeom.model.DayMasterForceEvidence;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.StructureDamageAnalysis;
import com.dajeom.model.StructureDamageEvidence;
import com.dajeom.model.StructureMixingSignal;
import com.dajeom.model.StructurePurityAnalysis;
import com.dajeom.model.StructurePurityEvidence;
import com.dajeom.rule.StructurePurityRule;
import com.dajeom.type.StructureMixingSeverity;
import com.dajeom.type.StructureMixingSignalType;
import com.dajeom.type.StructurePurityLevel;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-7.
 *
 * 격 후보별 순도·혼잡도를 분석한다.
 *
 * 입력:
 *
 * - 4차 신강·신약
 * - 5-6 파격 분석
 *
 * 확인 항목:
 *
 * 1.
 * 의미 있는 일반 내격 후보의 복수 성립
 *
 * 2.
 * 월령 후보의 복수 투출
 *
 * 3.
 * 정기 미투출 + 중기/여기 투출
 *
 * 4.
 * 정관/칠살 또는 같은 십성 그룹의
 *    정·편 혼재
 *
 * 5.
 * 성격과 파격의 공존
 *
 * 6.
 * 강한 파격 Evidence
 *
 * 중요한 원칙:
 *
 * - 순도는 길흉이 아니다.
 * - 아직 주격을 결정하지 않는다.
 * - 특수격은 5-8로 넘긴다.
 */
@Component
public class StructurePurityAnalyzer {

    private final StructureDamageAnalyzer
            structureDamageAnalyzer;

    private final StructureTenGodCalculator
            structureTenGodCalculator;

    private final StructurePurityRule
            structurePurityRule;

    public StructurePurityAnalyzer(

            StructureDamageAnalyzer
                    structureDamageAnalyzer,

            StructureTenGodCalculator
                    structureTenGodCalculator,

            StructurePurityRule
                    structurePurityRule

    ) {

        this.structureDamageAnalyzer =
                Objects.requireNonNull(
                        structureDamageAnalyzer,
                        "StructureDamageAnalyzer가 필요합니다."
                );

        this.structureTenGodCalculator =
                Objects.requireNonNull(
                        structureTenGodCalculator,
                        "StructureTenGodCalculator가 필요합니다."
                );

        this.structurePurityRule =
                Objects.requireNonNull(
                        structurePurityRule,
                        "StructurePurityRule이 필요합니다."
                );
    }

    /**
     * 5-1 ~ 5-6을 내부에서 실행하고
     * 5-7을 수행한다.
     */
    public StructurePurityAnalysis analyze(
            DayMasterStrengthAnalysis strength
    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        StructureDamageAnalysis damage =
                structureDamageAnalyzer
                        .analyze(
                                strength
                        );

        return analyze(
                strength,
                damage
        );
    }

    /**
     * 이미 계산된 5-6 결과를 재사용한다.
     *
     * 향후 5차 전체 Engine에서는
     * 이 메서드를 사용한다.
     */
    public StructurePurityAnalysis analyze(

            DayMasterStrengthAnalysis strength,

            StructureDamageAnalysis damage

    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                damage,
                "파격 분석 결과가 필요합니다."
        );

        validateConsistency(
                strength,
                damage
        );

        List<StructureDamageEvidence>
                meaningfulStandardCandidates =
                damage
                        .evidences()
                        .stream()
                        .filter(
                                StructureDamageEvidence
                                        ::standardStructureCandidate
                        )
                        .filter(
                                evidence ->
                                        evidence
                                                .establishmentLevel()
                                                .meaningfulCandidate()
                        )
                        .toList();

        List<StructureDamageEvidence>
                establishedStandardCandidates =
                meaningfulStandardCandidates
                        .stream()
                        .filter(
                                evidence ->
                                        evidence
                                                .establishmentLevel()
                                                .established()
                        )
                        .toList();

        List<StructureDamageEvidence>
                exposedStandardCandidates =
                damage
                        .evidences()
                        .stream()
                        .filter(
                                StructureDamageEvidence
                                        ::standardStructureCandidate
                        )
                        .filter(
                                this::candidateExposed
                        )
                        .toList();

        StructureDamageEvidence mainQiDamageEvidence =
                damage.mainQiEvidence();

        boolean mainQiExposed =
                candidateExposed(
                        mainQiDamageEvidence
                );

        List<StructureDamageEvidence>
                exposedSecondaryCandidates =
                damage
                        .evidences()
                        .stream()
                        .filter(
                                evidence ->
                                        !evidence.mainQi()
                        )
                        .filter(
                                StructureDamageEvidence
                                        ::standardStructureCandidate
                        )
                        .filter(
                                StructureDamageEvidence
                                        ::activeCandidate
                        )
                        .filter(
                                this::candidateExposed
                        )
                        .toList();

        boolean secondaryExposedWhileMainHidden =
                mainQiDamageEvidence
                        .standardStructureCandidate()

                        && !mainQiExposed

                        && !exposedSecondaryCandidates
                        .isEmpty();

        List<StructurePurityEvidence>
                purityEvidences =
                new ArrayList<>();

        StructurePurityEvidence mainQiPurityEvidence =
                null;

        for (
                StructureDamageEvidence damageEvidence
                : damage.evidences()
        ) {

            boolean evaluationApplicable =
                    damageEvidence.activeCandidate()
                            && damageEvidence
                            .standardStructureCandidate();

            List<StructureMixingSignal> signals;

            if (
                    evaluationApplicable
            ) {

                signals =
                        createMixingSignals(
                                strength,
                                damageEvidence,
                                meaningfulStandardCandidates,
                                establishedStandardCandidates,
                                exposedStandardCandidates,
                                exposedSecondaryCandidates,
                                secondaryExposedWhileMainHidden,
                                mainQiDamageEvidence
                        );

            } else {

                signals =
                        List.of();
            }

            int moderateSignalCount =
                    (int) signals
                            .stream()
                            .filter(
                                    StructureMixingSignal
                                            ::moderate
                            )
                            .count();

            int highSignalCount =
                    (int) signals
                            .stream()
                            .filter(
                                    StructureMixingSignal
                                            ::high
                            )
                            .count();

            StructurePurityLevel purityLevel =
                    structurePurityRule
                            .determinePurity(
                                    evaluationApplicable,
                                    signals
                            );

            StructurePurityEvidence purityEvidence =
                    new StructurePurityEvidence(
                            damageEvidence,
                            signals,
                            moderateSignalCount,
                            highSignalCount,
                            purityLevel,
                            damageEvidence
                                    .specialStructureDeferred()
                    );

            purityEvidences.add(
                    purityEvidence
            );

            if (
                    purityEvidence.mainQi()
            ) {

                if (
                        mainQiPurityEvidence != null
                ) {

                    throw new IllegalStateException(
                            "월령 정기 순도 Evidence가 둘 이상 생성되었습니다."
                    );
                }

                mainQiPurityEvidence =
                        purityEvidence;
            }
        }

        if (
                mainQiPurityEvidence == null
        ) {

            throw new IllegalStateException(
                    "월령 정기 순도 Evidence를 생성하지 못했습니다."
            );
        }

        return new StructurePurityAnalysis(
                strength.dayMaster(),
                damage.monthBranch(),
                meaningfulStandardCandidates.size(),
                establishedStandardCandidates.size(),
                exposedStandardCandidates.size(),
                secondaryExposedWhileMainHidden,
                mainQiPurityEvidence,
                purityEvidences
        );
    }

    private List<StructureMixingSignal>
    createMixingSignals(

            DayMasterStrengthAnalysis strength,

            StructureDamageEvidence candidate,

            List<StructureDamageEvidence>
                    meaningfulStandardCandidates,

            List<StructureDamageEvidence>
                    establishedStandardCandidates,

            List<StructureDamageEvidence>
                    exposedStandardCandidates,

            List<StructureDamageEvidence>
                    exposedSecondaryCandidates,

            boolean secondaryExposedWhileMainHidden,

            StructureDamageEvidence mainQiEvidence

    ) {

        List<StructureMixingSignal> signals =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 의미 있는 월령 격 후보 복수 성립
         * =================================================
         */
        Optional<StructureMixingSeverity>
                competitionSeverity =
                structurePurityRule
                        .candidateCompetitionSeverity(
                                meaningfulStandardCandidates.size(),
                                establishedStandardCandidates.size()
                        );

        competitionSeverity.ifPresent(
                severity ->
                        signals.add(
                                new StructureMixingSignal(
                                        StructureMixingSignalType
                                                .MULTIPLE_MEANINGFUL_MONTH_CANDIDATES,
                                        severity,
                                        distinctTenGods(
                                                meaningfulStandardCandidates
                                        ),
                                        meaningfulStandardCandidates.size()
                                )
                        )
        );

        /*
         * =================================================
         * 2.
         * 월령 후보 복수 투출
         * =================================================
         */
        Optional<StructureMixingSeverity>
                multipleExposureSeverity =
                structurePurityRule
                        .multipleExposureSeverity(
                                exposedStandardCandidates.size()
                        );

        multipleExposureSeverity.ifPresent(
                severity ->
                        signals.add(
                                new StructureMixingSignal(
                                        StructureMixingSignalType
                                                .MULTIPLE_EXPOSED_MONTH_CANDIDATES,
                                        severity,
                                        distinctTenGods(
                                                exposedStandardCandidates
                                        ),
                                        exposedStandardCandidates.size()
                                )
                        )
        );

        /*
         * =================================================
         * 3.
         * 정기 미투출 + 부기 투출
         * =================================================
         *
         * 이 신호는:
         *
         * - 정기 후보
         * - 실제 투출한 부기 후보
         *
         * 에만 적용한다.
         */
        boolean applySecondaryExpressionSignal =
                secondaryExposedWhileMainHidden

                        && (
                        candidate.mainQi()

                                || (
                                !candidate.mainQi()
                                        && candidateExposed(
                                        candidate
                                )
                        )
                );

        Optional<StructureMixingSeverity>
                secondarySeverity =
                structurePurityRule
                        .secondaryExpressionSeverity(
                                applySecondaryExpressionSignal
                        );

        secondarySeverity.ifPresent(
                severity -> {

                    List<StructureTenGodType>
                            relatedTenGods;

                    if (
                            candidate.mainQi()
                    ) {

                        List<StructureTenGodType>
                                related =
                                new ArrayList<>();

                        related.add(
                                mainQiEvidence.tenGod()
                        );

                        for (
                                StructureDamageEvidence secondary
                                : exposedSecondaryCandidates
                        ) {

                            if (
                                    !related.contains(
                                            secondary.tenGod()
                                    )
                            ) {

                                related.add(
                                        secondary.tenGod()
                                );
                            }
                        }

                        relatedTenGods =
                                List.copyOf(
                                        related
                                );

                    } else {

                        relatedTenGods =
                                distinctTenGods(
                                        List.of(
                                                mainQiEvidence,
                                                candidate
                                        )
                                );
                    }

                    signals.add(
                            new StructureMixingSignal(
                                    StructureMixingSignalType
                                            .SECONDARY_EXPRESSION_OVER_HIDDEN_MAIN,
                                    severity,
                                    relatedTenGods,
                                    Math.max(
                                            1,
                                            exposedSecondaryCandidates.size()
                                    )
                            )
                    );
                }
        );

        /*
         * =================================================
         * 4.
         * 같은 십성 그룹의 정·편 혼재
         * =================================================
         *
         * 단순히 지장간에 숨어 있는 것만으로
         * 혼잡이라고 판정하지 않는다.
         *
         * 반대 음양 십성이:
         *
         * - 천간에 직접 존재하거나
         * - 지장간에서 실제 투출
         *
         * 한 경우만 혼재 Signal을 만든다.
         */
        StructureTenGodType counterpart =
                structurePurityRule
                        .counterpartOf(
                                candidate.tenGod()
                        );

        int counterpartExpressionCount =
                countExpressedExactTenGodSources(
                        strength,
                        counterpart
                );

        if (
                counterpartExpressionCount > 0
        ) {

            StructureMixingSeverity severity =
                    structurePurityRule
                            .sameFamilyMixingSeverity(
                                    candidate.tenGod()
                            );

            StructureMixingSignalType type =
                    structurePurityRule
                            .sameFamilyMixingSignalType(
                                    candidate.tenGod()
                            );

            signals.add(
                    new StructureMixingSignal(
                            type,
                            severity,
                            List.of(
                                    candidate.tenGod(),
                                    counterpart
                            ),
                            counterpartExpressionCount
                    )
            );
        }

        /*
         * =================================================
         * 5.
         * 성격 + 파격 공존
         * =================================================
         */
        Optional<StructureMixingSeverity>
                supportDamageSeverity =
                structurePurityRule
                        .supportDamageCoexistenceSeverity(
                                candidate
                                        .supportAndDamageCoexist()
                        );

        supportDamageSeverity.ifPresent(
                severity ->
                        signals.add(
                                new StructureMixingSignal(
                                        StructureMixingSignalType
                                                .SUPPORT_AND_DAMAGE_COEXIST,
                                        severity,
                                        List.of(
                                                candidate.tenGod()
                                        ),
                                        Math.max(
                                                1,
                                                candidate
                                                        .supportEvidence()
                                                        .meaningfulSupportCount()

                                                        + candidate
                                                        .meaningfulDamageCount()
                                        )
                                )
                        )
        );

        /*
         * =================================================
         * 6.
         * 강한 파격 개입
         * =================================================
         */
        Optional<StructureMixingSeverity>
                strongDamageSeverity =
                structurePurityRule
                        .strongDamageSeverity(
                                candidate.hasStrongDamage()
                        );

        strongDamageSeverity.ifPresent(
                severity ->
                        signals.add(
                                new StructureMixingSignal(
                                        StructureMixingSignalType
                                                .STRONG_DAMAGE_PRESENT,
                                        severity,
                                        List.of(
                                                candidate.tenGod()
                                        ),
                                        Math.max(
                                                1,
                                                candidate
                                                        .meaningfulDamageCount()
                                        )
                                )
                        )
        );

        return List.copyOf(
                signals
        );
    }

    /**
     * exact TenGod가
     * 실제 천간 구조에 표현된 source 수.
     *
     * 다음은 표현으로 본다.
     *
     * - 표면 천간
     * - 투출한 지장간
     *
     * 단순히 지장간 안에만 숨어 있는 source는
     * 정편 혼재 판정에서 제외한다.
     */
    private int countExpressedExactTenGodSources(

            DayMasterStrengthAnalysis strength,

            StructureTenGodType targetTenGod

    ) {

        return (int) strength
                .forceFormationAnalysis()
                .evidences()
                .stream()
                .filter(
                        evidence ->
                                evidence.visibleStemSource()
                                        || evidence.exposedHiddenStem()
                )
                .filter(
                        evidence ->
                                structureTenGodCalculator
                                        .calculate(
                                                strength.dayMaster(),
                                                evidence.stem()
                                        )
                                        == targetTenGod
                )
                .count();
    }

    private boolean candidateExposed(
            StructureDamageEvidence evidence
    ) {

        return evidence
                .supportEvidence()
                .balanceEvidence()
                .establishmentEvidence()
                .exposed();
    }

    private List<StructureTenGodType>
    distinctTenGods(
            List<StructureDamageEvidence> evidences
    ) {

        List<StructureTenGodType> result =
                new ArrayList<>();

        for (
                StructureDamageEvidence evidence
                : evidences
        ) {

            StructureTenGodType tenGod =
                    evidence.tenGod();

            if (
                    !result.contains(
                            tenGod
                    )
            ) {

                result.add(
                        tenGod
                );
            }
        }

        if (
                result.isEmpty()
        ) {

            throw new IllegalStateException(
                    "혼잡 Signal에 사용할 십성이 없습니다."
            );
        }

        return List.copyOf(
                result
        );
    }

    private void validateConsistency(

            DayMasterStrengthAnalysis strength,

            StructureDamageAnalysis damage

    ) {

        if (
                strength.dayMaster()
                        != damage.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 분석과 파격 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                damage.evidences()
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "파격 분석 Evidence가 존재하지 않습니다."
            );
        }
    }
}