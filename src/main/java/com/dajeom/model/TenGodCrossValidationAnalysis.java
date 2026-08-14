package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TenGodCrossValidationStatus;
import com.dajeom.type.TenGodCorePatternType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-9.
 *
 * 십성 핵심 Pattern 교차검증 최종 분석.
 *
 * =================================================
 *
 * 원본:
 *
 * 10-8 TenGodCorePatternAnalysis
 *
 * =================================================
 *
 * 검증축:
 *
 * 5차 StructureAnalysis
 * 4차 DayMasterStrengthAnalysis
 * 7차 UsefulGodAnalysis
 * 9차 CombinationTransformationAnalysis
 */
public record TenGodCrossValidationAnalysis(

        HeavenlyStem dayMaster,

        TenGodCorePatternAnalysis corePatternAnalysis,

        StructureAnalysis structureAnalysis,

        DayMasterStrengthAnalysis dayMasterStrengthAnalysis,

        UsefulGodAnalysis usefulGodAnalysis,

        CombinationTransformationAnalysis combinationTransformationAnalysis,

        TenGodCrossValidationContext context,

        List<TenGodCrossValidatedPattern> patterns,

        boolean completeChart

) {

    public TenGodCrossValidationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                corePatternAnalysis,
                "10-8 핵심 십성 Pattern 분석이 필요합니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "5차 격국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterStrengthAnalysis,
                "4차 신강·신약 분석이 필요합니다."
        );

        Objects.requireNonNull(
                usefulGodAnalysis,
                "7차 용신 분석이 필요합니다."
        );

        Objects.requireNonNull(
                combinationTransformationAnalysis,
                "9차 합화 분석이 필요합니다."
        );

        Objects.requireNonNull(
                context,
                "10-9 교차검증 Context가 필요합니다."
        );

        patterns =
                patterns == null
                        ? List.of()
                        : List.copyOf(
                        patterns
                );

        /*
         * =================================================
         * 일간 동일성
         * =================================================
         */

        if (
                corePatternAnalysis.dayMaster()
                        != dayMaster

                        || dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
                        != dayMaster

                        || usefulGodAnalysis.dayMaster()
                        != dayMaster

                        || combinationTransformationAnalysis
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "10-9 입력 분석들의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 완전성
         * =================================================
         */

        if (
                corePatternAnalysis.completeChart()
                        != completeChart

                        || dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .completeChart()
                        != completeChart

                        || usefulGodAnalysis.completeChart()
                        != completeChart

                        || combinationTransformationAnalysis
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "10-9 입력 분석들의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 10-8 Signal과 10-9 Pattern 정합성
         * =================================================
         */

        if (
                patterns.size()
                        != corePatternAnalysis
                        .signals()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "10-8 Pattern 수와 10-9 교차검증 Pattern 수가 일치하지 않습니다."
            );
        }

        for (
                TenGodCorePatternSignal signal
                : corePatternAnalysis.signals()
        ) {

            boolean found =
                    patterns
                            .stream()
                            .anyMatch(
                                    pattern ->
                                            pattern.sourceSignal()
                                                    == signal
                            );

            if (
                    !found
            ) {

                throw new IllegalArgumentException(
                        "10-8 원본 Pattern이 10-9 결과에서 누락되었습니다: "
                                + signal.patternType()
                );
            }
        }
    }

    public Optional<TenGodCrossValidatedPattern> patternOf(
            TenGodCorePatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return Optional.empty();
        }

        return patterns
                .stream()
                .filter(
                        pattern ->
                                pattern.patternType()
                                        == patternType
                )
                .findFirst();
    }

    public boolean hasPattern(
            TenGodCorePatternType patternType
    ) {

        return patternOf(
                patternType
        )
                .isPresent();
    }

    public boolean isSupported(
            TenGodCorePatternType patternType
    ) {

        return patternOf(
                patternType
        )
                .map(
                        TenGodCrossValidatedPattern
                                ::supported
                )
                .orElse(false);
    }

    public boolean isQualified(
            TenGodCorePatternType patternType
    ) {

        return patternOf(
                patternType
        )
                .map(
                        TenGodCrossValidatedPattern
                                ::qualified
                )
                .orElse(false);
    }

    public boolean isWeakened(
            TenGodCorePatternType patternType
    ) {

        return patternOf(
                patternType
        )
                .map(
                        TenGodCrossValidatedPattern
                                ::weakened
                )
                .orElse(false);
    }

    public boolean isConflicted(
            TenGodCorePatternType patternType
    ) {

        return patternOf(
                patternType
        )
                .map(
                        TenGodCrossValidatedPattern
                                ::conflicted
                )
                .orElse(false);
    }

    public List<TenGodCrossValidatedPattern> supportedPatterns() {

        return patterns
                .stream()
                .filter(
                        TenGodCrossValidatedPattern
                                ::supported
                )
                .toList();
    }

    public List<TenGodCrossValidatedPattern> qualifiedPatterns() {

        return patterns
                .stream()
                .filter(
                        TenGodCrossValidatedPattern
                                ::qualified
                )
                .toList();
    }

    public List<TenGodCrossValidatedPattern> weakenedPatterns() {

        return patterns
                .stream()
                .filter(
                        TenGodCrossValidatedPattern
                                ::weakened
                )
                .toList();
    }

    public List<TenGodCrossValidatedPattern> conflictedPatterns() {

        return patterns
                .stream()
                .filter(
                        TenGodCrossValidatedPattern
                                ::conflicted
                )
                .toList();
    }

    public List<TenGodCrossValidatedPattern> orderedPatterns() {

        return patterns
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        (
                                                TenGodCrossValidatedPattern pattern
                                        ) ->
                                                pattern.status()
                                                        .rank()
                                )
                                .reversed()

                                .thenComparingInt(
                                        pattern ->
                                                pattern.patternType()
                                                        .ordinal()
                                )
                )
                .toList();
    }

    public long countByStatus(
            TenGodCrossValidationStatus status
    ) {

        if (
                status == null
        ) {

            return 0;
        }

        return patterns
                .stream()
                .filter(
                        pattern ->
                                pattern.status()
                                        == status
                )
                .count();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}