package com.dajeom.analyzer;

import com.dajeom.model.ClimateHealthCandidate;
import com.dajeom.model.ClimateHealthCrossValidationAnalysis;
import com.dajeom.model.ClimateHealthIntegrationAnalysis;
import com.dajeom.model.ClimateHealthValidatedCandidate;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.ClimateHealthCrossValidationRule;
import com.dajeom.type.ClimateHealthCandidateRole;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-9.
 *
 * 6-8 조후·병약·통관 통합 후보를:
 *
 * - 4차 신강·신약
 * - 5차 격국
 *
 * 과 다시 교차검증한다.
 *
 * =================================================
 *
 * 처리
 *
 * 6-8 Candidate
 *
 * ↓
 *
 * 일간 기준 오행 역할
 *
 * ↓
 *
 * 일반 신강신약 방향
 * 또는
 * 종격 방향
 *
 * ↓
 *
 * 주격 성격 요소 일치
 *
 * ↓
 *
 * 주격 파격 요소 일치
 *
 * ↓
 *
 * SUPPORTED
 * NEUTRAL
 * STRAINED
 * CONTRADICTORY
 *
 * ↓
 *
 * 기존 역할 유지 또는 하향
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 6-9에서는 후보를 승격하지 않는다.
 *
 * 검증 단계이므로:
 *
 * PRIMARY -> PRIMARY
 * PRIMARY -> SECONDARY
 * PRIMARY -> DEFERRED
 *
 * SECONDARY -> SECONDARY
 * SECONDARY -> DEFERRED
 *
 * DEFERRED -> DEFERRED
 *
 * REJECTED -> REJECTED
 *
 * 만 허용한다.
 */
@Component
public class ClimateHealthCrossValidationAnalyzer {

    private final ClimateHealthCrossValidationRule
            crossValidationRule;

    public ClimateHealthCrossValidationAnalyzer(
            ClimateHealthCrossValidationRule
                    crossValidationRule
    ) {

        this.crossValidationRule =
                Objects.requireNonNull(
                        crossValidationRule,
                        "ClimateHealthCrossValidationRule이 필요합니다."
                );
    }

    public ClimateHealthCrossValidationAnalysis analyze(

            StructureAnalysis structure,

            ClimateHealthIntegrationAnalysis integration

    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                integration,
                "6-8 통합 분석 결과가 필요합니다."
        );

        validateConsistency(
                structure,
                integration
        );

        DayMasterStrengthLevel strengthLevel =
                structure
                        .dayMasterStrengthAnalysis()
                        .level();

        FiveElement dayMasterElement =
                structure
                        .dayMasterStrengthAnalysis()
                        .dayMasterElement();

        boolean followStructureReview =
                structure
                        .primaryCandidate()
                        .followStructureCandidate();

        boolean primarySpecialStructure =
                structure
                        .primaryIsSpecial();

        List<ClimateHealthValidatedCandidate> candidates =
                new ArrayList<>();

        for (
                ClimateHealthCandidate integrationCandidate
                : integration.candidates()
        ) {

            FiveElement element =
                    integrationCandidate
                            .element();

            DayMasterElementRole dayMasterRole =
                    integrationCandidate
                            .remedyCandidate()
                            .dayMasterRole();

            /*
             * ================================================
             * 1.
             * 신강신약 / 종격 방향 검증
             * ================================================
             */
            ClimateHealthCrossValidationState
                    strengthState =
                    crossValidationRule
                            .determineStrengthState(
                                    strengthLevel,
                                    dayMasterRole,
                                    followStructureReview
                            );

            /*
             * ================================================
             * 2.
             * 주격 성격 요소 일치
             * ================================================
             */
            boolean structureSupportMatch =
                    crossValidationRule
                            .matchesPrimaryStructureSupport(
                                    structure,
                                    element
                            );

            /*
             * ================================================
             * 3.
             * 주격 파격 요소 일치
             * ================================================
             */
            boolean structureDamageMatch =
                    crossValidationRule
                            .matchesPrimaryStructureDamage(
                                    structure,
                                    element
                            );

            /*
             * ================================================
             * 4.
             * 격국 검증
             * ================================================
             */
            ClimateHealthCrossValidationState
                    structureState =
                    crossValidationRule
                            .determineStructureState(
                                    structure,
                                    structureSupportMatch,
                                    structureDamageMatch
                            );

            /*
             * ================================================
             * 5.
             * 전체 교차검증
             * ================================================
             */
            ClimateHealthCrossValidationState overallState =
                    crossValidationRule
                            .determineOverallState(
                                    strengthState,
                                    structureState
                            );

            /*
             * ================================================
             * 6.
             * 6-8 역할 검증
             * ================================================
             */
            ClimateHealthCandidateRole validatedRole =
                    crossValidationRule
                            .determineValidatedRole(
                                    integrationCandidate.role(),
                                    overallState
                            );

            candidates.add(
                    new ClimateHealthValidatedCandidate(
                            element,
                            dayMasterRole,
                            integrationCandidate,
                            strengthState,
                            structureState,
                            overallState,
                            validatedRole,
                            structureSupportMatch,
                            structureDamageMatch,
                            followStructureReview,
                            crossValidationRule
                                    .reasonsFor(
                                            integrationCandidate,
                                            strengthLevel,
                                            dayMasterRole,
                                            followStructureReview,
                                            primarySpecialStructure,
                                            structureSupportMatch,
                                            structureDamageMatch,
                                            strengthState,
                                            structureState,
                                            validatedRole
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
         * 검증 이후 역할
         *
         * 2.
         * 전체 교차검증 상태
         *
         * 3.
         * 6-8 역할
         *
         * 4.
         * 6-8 다중 수렴도
         *
         * 5.
         * 현재 오행 비중
         *
         * 순.
         *
         * 종합 점수를 만들지 않는다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        ClimateHealthValidatedCandidate candidate
                                ) ->
                                        candidate
                                                .validatedRole()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        ClimateHealthValidatedCandidate candidate
                                                ) ->
                                                        candidate
                                                                .overallState()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        ClimateHealthValidatedCandidate candidate
                                                ) ->
                                                        candidate
                                                                .baseRole()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        ClimateHealthValidatedCandidate candidate
                                                ) ->
                                                        candidate
                                                                .integrationCandidate()
                                                                .selectedAxisCount()
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                ClimateHealthValidatedCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new ClimateHealthCrossValidationAnalysis(
                structure.dayMaster(),
                dayMasterElement,
                structure.monthBranch(),
                structure,
                integration,
                candidates,
                followStructureReview,
                structure.completeChart()
        );
    }

    private void validateConsistency(

            StructureAnalysis structure,

            ClimateHealthIntegrationAnalysis integration

    ) {

        if (
                structure.dayMaster()
                        != integration.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 6-8 통합 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                structure.monthBranch()
                        != integration.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 6-8 통합 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                structure
                        .dayMasterStrengthAnalysis()
                        .dayMasterElement()

                        != integration
                        .dayMasterElement()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 6-8 통합 분석의 일간 오행이 일치하지 않습니다."
            );
        }

        if (
                structure.completeChart()
                        != integration.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 6-8 통합 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }
}