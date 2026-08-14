package com.dajeom.analyzer;

import com.dajeom.model.ClimateAdjustmentNeedAnalysis;
import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.model.ClimateElementCandidateAnalysis;
import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.ClimateHealthCrossValidationAnalysis;
import com.dajeom.model.ClimateHealthValidatedCandidate;
import com.dajeom.model.ClimateUsefulGodAnalysis;
import com.dajeom.model.ClimateUsefulGodCandidate;
import com.dajeom.model.UsefulGodMethodAnalysis;
import com.dajeom.model.UsefulGodMethodEvidence;
import com.dajeom.rule.ClimateUsefulGodRule;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.ClimateUsefulGodCandidateRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodMethodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-3.
 *
 * 7-1에서 분리된 조후용신법을 이용해
 * 木火土金水 조후용신 후보를 계산한다.
 *
 * =================================================
 *
 * 입력
 *
 * 7-1
 * UsefulGodMethodAnalysis
 *
 * ↓
 *
 * 6-2
 * ClimateAdjustmentNeedAnalysis
 *
 * ↓
 *
 * 6-3
 * ClimateElementCandidateAnalysis
 *
 * ↓
 *
 * 6-9
 * ClimateHealthCrossValidationAnalysis
 *
 * =================================================
 *
 * 처리
 *
 * 6-3 후보
 *
 * +
 *
 * 조후용신법 적용도
 *
 * +
 *
 * 한난/조습 경쟁 여부
 *
 * +
 *
 * 현재 오행량
 *
 * +
 *
 * 신강신약 검증
 *
 * +
 *
 * 격국 검증
 *
 * ↓
 *
 * PRIMARY
 * SECONDARY
 * CONDITIONAL
 * NOT_NEEDED
 *
 * =================================================
 *
 * 아직 최종 용신은 결정하지 않는다.
 */
@Component
public class ClimateUsefulGodAnalyzer {

    private final ClimateUsefulGodRule
            climateUsefulGodRule;

    public ClimateUsefulGodAnalyzer(
            ClimateUsefulGodRule
                    climateUsefulGodRule
    ) {

        this.climateUsefulGodRule =
                Objects.requireNonNull(
                        climateUsefulGodRule,
                        "ClimateUsefulGodRule이 필요합니다."
                );
    }

    public ClimateUsefulGodAnalysis analyze(
            UsefulGodMethodAnalysis methodAnalysis
    ) {

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        UsefulGodMethodEvidence methodEvidence =
                methodAnalysis
                        .methodOf(
                                UsefulGodMethodType.CLIMATE_CONTROL
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "CLIMATE_CONTROL 용신법 Evidence가 없습니다."
                                        )
                        );

        ClimateHealthAnalysis climateHealth =
                methodAnalysis
                        .climateHealthAnalysis();

        ClimateAdjustmentNeedAnalysis climateNeed =
                climateHealth
                        .climateAdjustmentNeedAnalysis();

        ClimateElementCandidateAnalysis sourceClimateAnalysis =
                climateHealth
                        .climateElementCandidateAnalysis();

        ClimateHealthCrossValidationAnalysis crossValidation =
                climateHealth
                        .crossValidationAnalysis();

        List<ClimateUsefulGodCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            ClimateElementCandidate sourceCandidate =
                    sourceClimateAnalysis
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "6-3 조후 후보를 찾을 수 없습니다: "
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

            /*
             * 같은 원국의 같은 오행을
             * 비교하고 있는지 확인.
             */
            if (
                    validatedCandidate.element()
                            != sourceCandidate.element()
            ) {

                throw new IllegalStateException(
                        "6-3과 6-9 후보 오행이 일치하지 않습니다: "
                                + element
                );
            }

            if (
                    validatedCandidate
                            .existingShareBasisPoints()

                            != sourceCandidate
                            .existingShareBasisPoints()
            ) {

                throw new IllegalStateException(
                        "6-3과 6-9의 오행 비중이 일치하지 않습니다: "
                                + element
                );
            }

            ClimateHealthCrossValidationState strengthState =
                    validatedCandidate
                            .strengthState();

            ClimateHealthCrossValidationState structureState =
                    validatedCandidate
                            .structureState();

            ClimateUsefulGodCandidateRole role =
                    climateUsefulGodRule
                            .determineRole(
                                    methodEvidence.applicability(),
                                    sourceCandidate,
                                    strengthState,
                                    structureState,
                                    climateNeed
                            );

            candidates.add(
                    new ClimateUsefulGodCandidate(
                            element,
                            role,
                            methodEvidence.applicability(),
                            sourceCandidate,
                            strengthState,
                            structureState,
                            climateNeed
                                    .correctionConflict(),
                            sourceCandidate
                                    .bridgeCandidate(),
                            climateUsefulGodRule
                                    .reasonsFor(
                                            methodEvidence.applicability(),
                                            sourceCandidate,
                                            strengthState,
                                            structureState,
                                            climateNeed,
                                            role
                                    )
                    )
            );
        }

        /*
         * ================================================
         * 결과 정렬
         * ================================================
         *
         * 1.
         * PRIMARY
         * SECONDARY
         * CONDITIONAL
         * NOT_NEEDED
         *
         * 2.
         * 해당 오행이 다루는 조후 필요도
         *
         * 3.
         * 조후 충돌 중재 후보
         *
         * 4.
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
                                        ClimateUsefulGodCandidate candidate
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
                                                        ClimateUsefulGodCandidate candidate
                                                ) ->
                                                        candidate
                                                                .sourceCandidate()
                                                                .addressedNeedLevel()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                ClimateUsefulGodCandidate
                                                        ::bridgeCandidate
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                ClimateUsefulGodCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new ClimateUsefulGodAnalysis(
                methodAnalysis.dayMaster(),
                methodAnalysis.dayMasterElement(),
                methodAnalysis.monthBranch(),
                methodAnalysis,
                methodEvidence,
                climateNeed,
                sourceClimateAnalysis,
                crossValidation,
                candidates,
                methodAnalysis.completeChart()
        );
    }
}