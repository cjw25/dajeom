package com.dajeom.analyzer;

import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.ClimateHealthCrossValidationAnalysis;
import com.dajeom.model.ClimateHealthValidatedCandidate;
import com.dajeom.model.RemedyAnalysis;
import com.dajeom.model.RemedyCandidate;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.StructureUsefulGodAnalysis;
import com.dajeom.model.StructureUsefulGodCandidate;
import com.dajeom.model.UsefulGodMethodAnalysis;
import com.dajeom.model.UsefulGodMethodEvidence;
import com.dajeom.rule.StructureUsefulGodRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StructureUsefulGodCandidateRole;
import com.dajeom.type.UsefulGodMethodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-4.
 *
 * 7-1에서 분리된 격국용신법을 이용해
 * 木火土金水 격국용신 후보를 계산한다.
 *
 * =================================================
 *
 * 표준 내격
 *
 * 5-5 성격 요소
 * +
 * 5-6 파격 요소
 * +
 * 6-5 파격 복구 오행
 * +
 * 6-9 신강신약 검증
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
 * 특수격
 *
 * 일반 내격 공식을 강제로 적용하지 않는다.
 *
 * =================================================
 *
 * 종격
 *
 * 일반 격국용신 후보를 제한하고
 * 7-7 종격용신법에 넘긴다.
 *
 * =================================================
 *
 * 아직 최종 용신은 결정하지 않는다.
 */
@Component
public class StructureUsefulGodAnalyzer {

    private final StructureUsefulGodRule
            structureUsefulGodRule;

    public StructureUsefulGodAnalyzer(
            StructureUsefulGodRule structureUsefulGodRule
    ) {

        this.structureUsefulGodRule =
                Objects.requireNonNull(
                        structureUsefulGodRule,
                        "StructureUsefulGodRule이 필요합니다."
                );
    }

    public StructureUsefulGodAnalysis analyze(
            UsefulGodMethodAnalysis methodAnalysis
    ) {

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        UsefulGodMethodEvidence methodEvidence =
                methodAnalysis
                        .methodOf(
                                UsefulGodMethodType.STRUCTURE
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "STRUCTURE 용신법 Evidence가 없습니다."
                                        )
                        );

        ClimateHealthAnalysis climateHealth =
                methodAnalysis
                        .climateHealthAnalysis();

        StructureAnalysis structure =
                climateHealth
                        .structureAnalysis();

        RemedyAnalysis remedyAnalysis =
                climateHealth
                        .remedyAnalysis();

        ClimateHealthCrossValidationAnalysis crossValidation =
                climateHealth
                        .crossValidationAnalysis();

        boolean standardPrimaryStructure =
                structure
                        .primaryIsStandard();

        boolean specialPrimaryStructure =
                structure
                        .primaryIsSpecial();

        boolean followStructureSuppressed =
                methodEvidence
                        .suppressedByFollowStructure();

        List<StructureUsefulGodCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

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

            RemedyCandidate remedyCandidate =
                    remedyAnalysis
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "6-5 병약 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            if (
                    validatedCandidate
                            .existingShareBasisPoints()

                            != remedyCandidate
                            .existingShareBasisPoints()
            ) {

                throw new IllegalStateException(
                        "6-5와 6-9의 오행 비중이 일치하지 않습니다: "
                                + element
                );
            }

            boolean formationSupportMatch =
                    validatedCandidate
                            .structureSupportMatch();

            boolean damageElementMatch =
                    validatedCandidate
                            .structureDamageMatch();

            boolean damageRepairMatch =
                    structureUsefulGodRule
                            .damageRepairMatch(
                                    remedyCandidate
                            );

            StructureUsefulGodCandidateRole role =
                    structureUsefulGodRule
                            .determineRole(
                                    methodEvidence.applicability(),
                                    structure.status(),
                                    standardPrimaryStructure,
                                    specialPrimaryStructure,
                                    formationSupportMatch,
                                    damageElementMatch,
                                    damageRepairMatch,
                                    validatedCandidate.strengthState(),
                                    followStructureSuppressed
                            );

            candidates.add(
                    new StructureUsefulGodCandidate(
                            element,
                            validatedCandidate.dayMasterRole(),
                            role,
                            methodEvidence.applicability(),
                            structure.status(),
                            validatedCandidate.strengthState(),
                            validatedCandidate,
                            standardPrimaryStructure,
                            specialPrimaryStructure,
                            formationSupportMatch,
                            damageElementMatch,
                            damageRepairMatch,
                            specialPrimaryStructure,
                            followStructureSuppressed,
                            structureUsefulGodRule
                                    .reasonsFor(
                                            methodEvidence.applicability(),
                                            structure.status(),
                                            standardPrimaryStructure,
                                            specialPrimaryStructure,
                                            formationSupportMatch,
                                            damageElementMatch,
                                            damageRepairMatch,
                                            validatedCandidate.strengthState(),
                                            followStructureSuppressed,
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
         * 1.
         * PRIMARY
         * SECONDARY
         * CONDITIONAL
         * NOT_NEEDED
         *
         * 2.
         * 파격 복구 오행
         *
         * 3.
         * 성격 요소
         *
         * 4.
         * 순수 파격 오행은 뒤로
         *
         * 5.
         * 신강신약 검증 상태
         *
         * 6.
         * 현재 오행량
         *
         * 순.
         *
         * 이것은 용신 종합점수가 아니다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        StructureUsefulGodCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                StructureUsefulGodCandidate
                                                        ::damageRepairMatch
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                StructureUsefulGodCandidate
                                                        ::formationSupportMatch
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                StructureUsefulGodCandidate
                                        ::pureDamageElement
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        StructureUsefulGodCandidate candidate
                                                ) ->
                                                        candidate
                                                                .strengthState()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                StructureUsefulGodCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new StructureUsefulGodAnalysis(
                methodAnalysis.dayMaster(),
                methodAnalysis.dayMasterElement(),
                methodAnalysis.monthBranch(),
                methodAnalysis,
                methodEvidence,
                structure,
                remedyAnalysis,
                crossValidation,
                candidates,
                followStructureSuppressed,
                specialPrimaryStructure,
                methodAnalysis.completeChart()
        );
    }
}