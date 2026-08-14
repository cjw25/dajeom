package com.dajeom.analyzer;

import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.TenGodCorePatternAnalysis;
import com.dajeom.model.TenGodCorePatternSignal;
import com.dajeom.model.TenGodCrossValidatedPattern;
import com.dajeom.model.TenGodCrossValidationAnalysis;
import com.dajeom.model.TenGodCrossValidationContext;
import com.dajeom.model.TenGodCrossValidationEvidence;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.rule.TenGodCrossValidationRule;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.TenGodCrossValidationReasonType;
import com.dajeom.type.TenGodCrossValidationStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-9.
 *
 * 핵심 십성 Pattern 교차검증 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * 10-8 핵심 십성 Pattern
 * +
 * 5차 격국
 * +
 * 4차 신강신약
 * +
 * 7차 용신
 * +
 * 9차 합화
 * +
 * 용신/합화 Pattern Alignment Context
 *
 * =================================================
 *
 * 출력:
 *
 * SUPPORTED
 * QUALIFIED
 * WEAKENED
 * CONFLICTED
 *
 * =================================================
 *
 * Pattern 자체는 절대로 제거하지 않는다.
 */
@Component
public class TenGodCrossValidationAnalyzer {

    private final TenGodCrossValidationRule
            tenGodCrossValidationRule;

    public TenGodCrossValidationAnalyzer(
            TenGodCrossValidationRule tenGodCrossValidationRule
    ) {

        this.tenGodCrossValidationRule =
                Objects.requireNonNull(
                        tenGodCrossValidationRule,
                        "TenGodCrossValidationRule이 필요합니다."
                );
    }

    /**
     * 공식 10-9 분석 진입점.
     */
    public TenGodCrossValidationAnalysis analyze(

            TenGodCorePatternAnalysis
                    corePatternAnalysis,

            StructureAnalysis
                    structureAnalysis,

            DayMasterStrengthAnalysis
                    dayMasterStrengthAnalysis,

            UsefulGodAnalysis
                    usefulGodAnalysis,

            CombinationTransformationAnalysis
                    combinationTransformationAnalysis,

            TenGodCrossValidationContext
                    context

    ) {

        Objects.requireNonNull(
                corePatternAnalysis,
                "10-8 TenGodCorePatternAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "5차 StructureAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterStrengthAnalysis,
                "4차 DayMasterStrengthAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                usefulGodAnalysis,
                "7차 UsefulGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                combinationTransformationAnalysis,
                "9차 CombinationTransformationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                context,
                "10-9 TenGodCrossValidationContext가 필요합니다."
        );

        validateIdentity(
                corePatternAnalysis,
                dayMasterStrengthAnalysis,
                usefulGodAnalysis,
                combinationTransformationAnalysis
        );

        DayMasterStrengthLevel strengthLevel =
                dayMasterStrengthAnalysis
                        .level();

        List<TenGodCrossValidatedPattern> patterns =
                new ArrayList<>();

        /*
         * =================================================
         * 10-8 Pattern 하나씩 그대로 재검증
         * =================================================
         */

        for (
                TenGodCorePatternSignal signal
                : corePatternAnalysis.signals()
        ) {

            List<TenGodCrossValidationEvidence> evidences =
                    new ArrayList<>();

            /*
             * =================================================
             * 1. 격국
             * =================================================
             */

            evidences.add(
                    tenGodCrossValidationRule
                            .evaluateStructure(
                                    structureAnalysis
                                            .status()
                            )
            );

            /*
             * =================================================
             * 2. 신강·신약
             * =================================================
             */

            evidences.add(
                    tenGodCrossValidationRule
                            .evaluateStrength(
                                    signal,
                                    strengthLevel
                            )
            );

            /*
             * =================================================
             * 3. 용신
             * =================================================
             */

            evidences.add(
                    tenGodCrossValidationRule
                            .evaluateUsefulGod(
                                    signal.patternType(),
                                    context
                            )
            );

            /*
             * =================================================
             * 4. 합화
             * =================================================
             */

            evidences.add(
                    tenGodCrossValidationRule
                            .evaluateTransformation(
                                    signal.patternType(),
                                    context
                            )
            );

            /*
             * =================================================
             * 최종
             * =================================================
             */

            TenGodCrossValidationStatus finalStatus =
                    tenGodCrossValidationRule
                            .determineFinalStatus(
                                    signal.level(),
                                    evidences,
                                    corePatternAnalysis
                                            .completeChart()
                            );

            List<TenGodCrossValidationReasonType> finalReasons =
                    tenGodCrossValidationRule
                            .buildFinalReasons(
                                    signal.level(),
                                    finalStatus,
                                    corePatternAnalysis
                                            .completeChart()
                            );

            patterns.add(
                    new TenGodCrossValidatedPattern(
                            signal,
                            finalStatus,
                            evidences,
                            finalReasons
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         *
         * Pattern 삭제가 아니라
         * 출력 안정성을 위한 정렬만 한다.
         * =================================================
         */

        patterns.sort(
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
        );

        return new TenGodCrossValidationAnalysis(
                corePatternAnalysis
                        .dayMaster(),
                corePatternAnalysis,
                structureAnalysis,
                dayMasterStrengthAnalysis,
                usefulGodAnalysis,
                combinationTransformationAnalysis,
                context,
                patterns,
                corePatternAnalysis
                        .completeChart()
        );
    }

    /**
     * 용신·합화 Pattern Alignment가 아직 연결되지 않은 경우의
     * 보수적 분석용.
     *
     * 용신과 합화를 자동으로 "지지"한다고 가정하지 않는다.
     *
     * 두 Factor 모두 QUALIFIED/NEUTRAL로 들어간다.
     */
    public TenGodCrossValidationAnalysis analyzeConservatively(

            TenGodCorePatternAnalysis
                    corePatternAnalysis,

            StructureAnalysis
                    structureAnalysis,

            DayMasterStrengthAnalysis
                    dayMasterStrengthAnalysis,

            UsefulGodAnalysis
                    usefulGodAnalysis,

            CombinationTransformationAnalysis
                    combinationTransformationAnalysis

    ) {

        return analyze(
                corePatternAnalysis,
                structureAnalysis,
                dayMasterStrengthAnalysis,
                usefulGodAnalysis,
                combinationTransformationAnalysis,
                TenGodCrossValidationContext
                        .neutral()
        );
    }

    /*
     * =================================================
     * 동일 원국 검증
     * =================================================
     */

    private void validateIdentity(

            TenGodCorePatternAnalysis
                    corePatternAnalysis,

            DayMasterStrengthAnalysis
                    dayMasterStrengthAnalysis,

            UsefulGodAnalysis
                    usefulGodAnalysis,

            CombinationTransformationAnalysis
                    combinationTransformationAnalysis

    ) {

        if (
                corePatternAnalysis
                        .dayMaster()

                        != dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-8과 4차 신강·신약 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                corePatternAnalysis
                        .dayMaster()

                        != usefulGodAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-8과 7차 용신 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                corePatternAnalysis
                        .dayMaster()

                        != combinationTransformationAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-8과 9차 합화 분석의 일간이 일치하지 않습니다."
            );
        }

        boolean completeChart =
                corePatternAnalysis
                        .completeChart();

        if (
                dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .completeChart()
                        != completeChart

                        || usefulGodAnalysis
                        .completeChart()
                        != completeChart

                        || combinationTransformationAnalysis
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "10-9 입력 분석들의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }
}