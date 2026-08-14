package com.dajeom.analyzer;

import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.ClimateHealthCrossValidationAnalysis;
import com.dajeom.model.ClimateHealthValidatedCandidate;
import com.dajeom.model.PassageCandidate;
import com.dajeom.model.PassageCandidateAnalysis;
import com.dajeom.model.PassageEvidence;
import com.dajeom.model.PassageNeedAnalysis;
import com.dajeom.model.PassageUsefulGodAnalysis;
import com.dajeom.model.PassageUsefulGodCandidate;
import com.dajeom.model.UsefulGodMethodAnalysis;
import com.dajeom.model.UsefulGodMethodEvidence;
import com.dajeom.rule.PassageUsefulGodRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PassageUsefulGodCandidateRole;
import com.dajeom.type.UsefulGodMethodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-6.
 *
 * 7-1에서 분리한 통관용신법을 이용해
 * 木火土金水 통관용신 후보를 계산한다.
 *
 * =================================================
 *
 * 입력
 *
 * 6-6
 * PassageNeedAnalysis
 *
 * 6-7
 * PassageCandidateAnalysis
 *
 * 6-9
 * ClimateHealthCrossValidationAnalysis
 *
 * 7-1
 * UsefulGodMethodAnalysis
 *
 * =================================================
 *
 * 검증
 *
 * - 통관용신법 적용도
 * - 실제 통관 필요도
 * - bridge 오행 부족 여부
 * - 일간 직접 불리 여부
 * - 병약 방향 수렴
 * - 조후 방향 수렴
 * - 현재 오행 과다
 * - 신강신약
 * - 격국
 *
 * =================================================
 *
 * 결과
 *
 * PRIMARY
 * SECONDARY
 * CONDITIONAL
 * NOT_NEEDED
 *
 * =================================================
 *
 * 아직 최종 용신을 결정하지 않는다.
 */
@Component
public class PassageUsefulGodAnalyzer {

    private final PassageUsefulGodRule
            passageUsefulGodRule;

    public PassageUsefulGodAnalyzer(
            PassageUsefulGodRule passageUsefulGodRule
    ) {

        this.passageUsefulGodRule =
                Objects.requireNonNull(
                        passageUsefulGodRule,
                        "PassageUsefulGodRule이 필요합니다."
                );
    }

    public PassageUsefulGodAnalysis analyze(
            UsefulGodMethodAnalysis methodAnalysis
    ) {

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        UsefulGodMethodEvidence methodEvidence =
                methodAnalysis
                        .methodOf(
                                UsefulGodMethodType.PASSAGE
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "PASSAGE 용신법 Evidence가 없습니다."
                                        )
                        );

        ClimateHealthAnalysis climateHealth =
                methodAnalysis
                        .climateHealthAnalysis();

        PassageNeedAnalysis passageNeed =
                climateHealth
                        .passageNeedAnalysis();

        PassageCandidateAnalysis sourcePassageAnalysis =
                climateHealth
                        .passageCandidateAnalysis();

        ClimateHealthCrossValidationAnalysis crossValidation =
                climateHealth
                        .crossValidationAnalysis();

        List<PassageUsefulGodCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            PassageCandidate sourceCandidate =
                    sourcePassageAnalysis
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "6-7 통관 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            ClimateHealthValidatedCandidate validatedCandidate =
                    crossValidation
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "6-9 교차검증 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            if (
                    sourceCandidate.element()
                            != validatedCandidate.element()
            ) {

                throw new IllegalStateException(
                        "6-7과 6-9 후보의 오행이 일치하지 않습니다: "
                                + element
                );
            }

            if (
                    sourceCandidate
                            .existingShareBasisPoints()

                            != validatedCandidate
                            .existingShareBasisPoints()
            ) {

                throw new IllegalStateException(
                        "6-7과 6-9의 오행 비중이 일치하지 않습니다: "
                                + element
                );
            }

            int meaningfulEvidenceCount =
                    (int) sourceCandidate
                            .passageEvidences()
                            .stream()
                            .filter(
                                    PassageEvidence
                                            ::meaningfulPassageRequired
                            )
                            .count();

            int highPriorityEvidenceCount =
                    (int) sourceCandidate
                            .passageEvidences()
                            .stream()
                            .filter(
                                    PassageEvidence
                                            ::highPriority
                            )
                            .count();

            PassageUsefulGodCandidateRole role =
                    passageUsefulGodRule
                            .determineRole(
                                    methodEvidence.applicability(),
                                    sourceCandidate,
                                    validatedCandidate.strengthState(),
                                    validatedCandidate.structureState()
                            );

            candidates.add(
                    new PassageUsefulGodCandidate(
                            element,
                            role,
                            methodEvidence.applicability(),
                            sourceCandidate.strongestNeed(),
                            sourceCandidate,
                            validatedCandidate.strengthState(),
                            validatedCandidate.structureState(),
                            meaningfulEvidenceCount,
                            highPriorityEvidenceCount,
                            sourceCandidate.bridgeInsufficient(),
                            sourceCandidate.bridgeAlreadyFunctions(),
                            sourceCandidate.dayMasterDisadvantaged(),
                            sourceCandidate.remedyAligned(),
                            sourceCandidate.climateAligned(),
                            sourceCandidate.crossAnalysisConflict(),
                            sourceCandidate.saturated(),
                            passageUsefulGodRule
                                    .reasonsFor(
                                            methodEvidence.applicability(),
                                            sourceCandidate,
                                            validatedCandidate.strengthState(),
                                            validatedCandidate.structureState(),
                                            role
                                    )
                    )
            );
        }

        /*
         * =================================================
         * 결과 정렬
         * =================================================
         *
         * 1.
         * PRIMARY
         * SECONDARY
         * CONDITIONAL
         * NOT_NEEDED
         *
         * 2.
         * 통관 필요도
         *
         * 3.
         * HIGH/CRITICAL 통관 Evidence 수
         *
         * 4.
         * 일간 직접 불리
         *
         * 5.
         * 병약·조후 양쪽 수렴
         *
         * 6.
         * bridge 부족
         *
         * 7.
         * 현재 오행 비중
         *
         * 순.
         *
         * 이것은 용신 종합 점수가 아니다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        PassageUsefulGodCandidate candidate
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
                                                        PassageUsefulGodCandidate candidate
                                                ) ->
                                                        candidate
                                                                .strongestNeed()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                PassageUsefulGodCandidate
                                                        ::highPriorityPassageEvidenceCount
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                PassageUsefulGodCandidate
                                                        ::dayMasterDisadvantaged
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                PassageUsefulGodCandidate
                                                        ::dualCrossMethodAlignment
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                PassageUsefulGodCandidate
                                                        ::bridgeInsufficient
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                PassageUsefulGodCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new PassageUsefulGodAnalysis(
                methodAnalysis.dayMaster(),
                methodAnalysis.dayMasterElement(),
                methodAnalysis.monthBranch(),
                methodAnalysis,
                methodEvidence,
                passageNeed,
                sourcePassageAnalysis,
                crossValidation,
                candidates,
                methodAnalysis.completeChart()
        );
    }
}