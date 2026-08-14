package com.dajeom.analyzer;

import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.ClimateHealthCrossValidationAnalysis;
import com.dajeom.model.ClimateHealthValidatedCandidate;
import com.dajeom.model.PathologyAnalysis;
import com.dajeom.model.RemedyAnalysis;
import com.dajeom.model.RemedyCandidate;
import com.dajeom.model.RemedyUsefulGodAnalysis;
import com.dajeom.model.RemedyUsefulGodCandidate;
import com.dajeom.model.UsefulGodMethodAnalysis;
import com.dajeom.model.UsefulGodMethodEvidence;
import com.dajeom.rule.RemedyUsefulGodRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.RemedyUsefulGodCandidateRole;
import com.dajeom.type.UsefulGodMethodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-5.
 *
 * 7-1에서 분리한 병약용신법을 이용해
 * 木火土金水 병약용신 후보를 계산한다.
 *
 * =================================================
 *
 * 입력
 *
 * 6-4 PathologyAnalysis
 *
 * 6-5 RemedyAnalysis
 *
 * 6-9 ClimateHealthCrossValidationAnalysis
 *
 * 7-1 UsefulGodMethodAnalysis
 *
 * =================================================
 *
 * 확인 내용
 *
 * - 병약용신법 적용도
 * - 전체 병 구조 심각도
 * - 강한 병 직접 해결 여부
 * - 의미 있는 병 몇 개를 해결하는지
 * - 다른 병 악화 Conflict
 * - 현재 오행 과다
 * - 신강신약 적합성
 * - 격국 적합성
 * - 아직 미해결 병이 존재하는지
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
public class RemedyUsefulGodAnalyzer {

    private final RemedyUsefulGodRule
            remedyUsefulGodRule;

    public RemedyUsefulGodAnalyzer(
            RemedyUsefulGodRule remedyUsefulGodRule
    ) {

        this.remedyUsefulGodRule =
                Objects.requireNonNull(
                        remedyUsefulGodRule,
                        "RemedyUsefulGodRule이 필요합니다."
                );
    }

    public RemedyUsefulGodAnalysis analyze(
            UsefulGodMethodAnalysis methodAnalysis
    ) {

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        UsefulGodMethodEvidence methodEvidence =
                methodAnalysis
                        .methodOf(
                                UsefulGodMethodType.REMEDY
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "REMEDY 용신법 Evidence가 없습니다."
                                        )
                        );

        ClimateHealthAnalysis climateHealth =
                methodAnalysis
                        .climateHealthAnalysis();

        PathologyAnalysis pathology =
                climateHealth
                        .pathologyAnalysis();

        RemedyAnalysis remedyAnalysis =
                climateHealth
                        .remedyAnalysis();

        ClimateHealthCrossValidationAnalysis crossValidation =
                climateHealth
                        .crossValidationAnalysis();

        PathologySeverity overallSeverity =
                pathology
                        .overallSeverity();

        boolean unresolvedMeaningfulPathology =
                remedyAnalysis
                        .hasUnresolvedMeaningfulPathology();

        List<RemedyUsefulGodCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            RemedyCandidate sourceCandidate =
                    remedyAnalysis
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "6-5 약 후보를 찾을 수 없습니다: "
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
                        "6-5와 6-9 후보의 오행이 일치하지 않습니다: "
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
                        "6-5와 6-9의 오행 비중이 일치하지 않습니다: "
                                + element
                );
            }

            if (
                    sourceCandidate.dayMasterRole()
                            != validatedCandidate.dayMasterRole()
            ) {

                throw new IllegalStateException(
                        "6-5와 6-9의 일간 기준 오행 역할이 일치하지 않습니다: "
                                + element
                );
            }

            RemedyUsefulGodCandidateRole role =
                    remedyUsefulGodRule
                            .determineRole(
                                    methodEvidence.applicability(),
                                    overallSeverity,
                                    sourceCandidate,
                                    validatedCandidate.strengthState(),
                                    validatedCandidate.structureState(),
                                    unresolvedMeaningfulPathology
                            );

            candidates.add(
                    new RemedyUsefulGodCandidate(
                            element,
                            sourceCandidate.dayMasterRole(),
                            role,
                            methodEvidence.applicability(),
                            overallSeverity,
                            sourceCandidate,
                            validatedCandidate.strengthState(),
                            validatedCandidate.structureState(),
                            sourceCandidate
                                    .meaningfulAddressedPathologyCount(),
                            sourceCandidate
                                    .addressesSeverePathology(),
                            unresolvedMeaningfulPathology,
                            sourceCandidate.hasConflict(),
                            sourceCandidate.saturated(),
                            remedyUsefulGodRule
                                    .reasonsFor(
                                            methodEvidence.applicability(),
                                            overallSeverity,
                                            sourceCandidate,
                                            validatedCandidate.strengthState(),
                                            validatedCandidate.structureState(),
                                            unresolvedMeaningfulPathology,
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
         * 강한 병 직접 대응
         *
         * 3.
         * 해결하는 의미 있는 병 구조 수
         *
         * 4.
         * 6-5 원본 역할
         *
         * 5.
         * 현재 오행 비중이 낮은 후보
         *
         * 순.
         *
         * 이것은 용신 종합 점수가 아니다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        RemedyUsefulGodCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                RemedyUsefulGodCandidate
                                                        ::addressesSeverePathology
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                RemedyUsefulGodCandidate
                                                        ::meaningfulAddressedPathologyCount
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        RemedyUsefulGodCandidate candidate
                                                ) ->
                                                        candidate
                                                                .sourceCandidate()
                                                                .role()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                RemedyUsefulGodCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new RemedyUsefulGodAnalysis(
                methodAnalysis.dayMaster(),
                methodAnalysis.dayMasterElement(),
                methodAnalysis.monthBranch(),
                methodAnalysis,
                methodEvidence,
                pathology,
                remedyAnalysis,
                crossValidation,
                candidates,
                unresolvedMeaningfulPathology,
                methodAnalysis.completeChart()
        );
    }
}