package com.dajeom.engine;

import com.dajeom.model.AnnualCrossFunctionalSynthesisSegmentAnalysis;
import com.dajeom.model.AnnualFunctionCapacityContext;
import com.dajeom.model.AnnualFunctionalBaselineAnalysis;
import com.dajeom.model.AnnualResolvedFunctionEffect;
import com.dajeom.model.MonthlyFunctionalActivationAnalysis;
import com.dajeom.model.MonthlyFunctionalActivationEffect;
import com.dajeom.model.MonthlyFunctionalActivationSegmentAnalysis;
import com.dajeom.model.MonthlyLuckAnalysis;
import com.dajeom.model.MonthlyLuckContextSignal;
import com.dajeom.model.MonthlyLuckDecadeContextAnalysis;
import com.dajeom.type.MonthlyFunctionalActivationStatus;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Monthly Functional Activation Engine.
 *
 * =================================================
 *
 * 공식 입력:
 *
 * AnnualFunctionalBaselineAnalysis
 *
 * +
 *
 * MonthlyLuckAnalysis
 *
 * =================================================
 *
 * 역할:
 *
 * Annual Functional Baseline을 다시 계산하지 않고
 * 실제 월운이 그 기준선에
 * 어떻게 접촉하는지만 결정한다.
 *
 * =================================================
 *
 * Annual Common:
 *
 * AnnualFunctionCapacityContext
 *
 * +
 *
 * Monthly TenGod Input
 *
 * +
 *
 * Monthly Common Context Signal
 *
 * =================================================
 *
 * Segment:
 *
 * AnnualResolvedFunctionEffect
 *
 * +
 *
 * Monthly Decade Context Signal
 *
 * =================================================
 *
 * 절대 하지 않는 것:
 *
 * - 신강신약 재계산
 * - 용신 재선정
 * - Annual Baseline 변경
 * - Function Source 개수 점수화
 * - 임의의 대운 Segment 선택
 * - 교운 Segment 평균
 * - Global Context를 특정 Function에 강제 배정
 */
@Component
public class MonthlyFunctionalActivationAnalysisEngine {


    public MonthlyFunctionalActivationAnalysis analyze(

            AnnualFunctionalBaselineAnalysis annualFunctionalBaseline,

            MonthlyLuckAnalysis monthlyLuckAnalysis

    ) {

        Objects.requireNonNull(
                annualFunctionalBaseline,
                "Monthly Functional Activation에는 Annual Functional Baseline이 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckAnalysis,
                "Monthly Functional Activation에는 MonthlyLuckAnalysis가 필요합니다."
        );


        /*
         * =================================================
         * AnnualLuckAnalysis Identity
         * =================================================
         */

        if (
                annualFunctionalBaseline
                        .annualLuckAnalysis()
                        != monthlyLuckAnalysis
                        .annualLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Activation은 반드시 동일한 AnnualLuckAnalysis Identity를 사용하는 "
                            + "Annual Functional Baseline과 MonthlyLuckAnalysis를 입력받아야 합니다."
            );
        }


        boolean completeChart =
                annualFunctionalBaseline
                        .completeChart();


        /*
         * =================================================
         * Annual Common 5 Function
         * =================================================
         */

        List<MonthlyFunctionalActivationEffect>
                annualEffects =
                new ArrayList<>(
                        NatalTenGodFunctionType
                                .values()
                                .length
                );


        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            AnnualFunctionCapacityContext annualFunctionContext =
                    annualFunctionalBaseline
                            .requireAnnualFunctionContext(
                                    functionType
                            );


            boolean annualBaselineActive =
                    containsIdentity(
                            annualFunctionalBaseline
                                    .annualActiveFunctionContexts(),
                            annualFunctionContext
                    );


            Set<StructureTenGodType>
                    monthlyInputTenGodTypes =
                    resolveMonthlyInputTenGodTypes(
                            monthlyLuckAnalysis,
                            functionType
                    );


            List<MonthlyLuckContextSignal>
                    functionSignals =
                    signalsForFunction(
                            monthlyLuckAnalysis
                                    .monthlyContextSignals(),
                            functionType
                    );


            annualEffects.add(
                    createEffect(
                            functionType,
                            annualFunctionContext,
                            null,
                            monthlyLuckAnalysis,
                            annualBaselineActive,
                            monthlyInputTenGodTypes,
                            functionSignals,
                            completeChart
                    )
            );
        }


        /*
         * =================================================
         * Monthly × Annual-Decade Segment
         * =================================================
         *
         * 월운의 decadeContexts가 실제로 존재하는 경우에만
         * Segment Activation을 만든다.
         *
         * Annual Baseline 전체 Segment를 무조건 복사하지 않는다.
         *
         * 현재 월운과 실제 겹치는 대운 Sequence만 사용한다.
         */

        List<MonthlyFunctionalActivationSegmentAnalysis>
                segmentAnalyses =
                new ArrayList<>();


        if (
                monthlyLuckAnalysis
                        .decadeContextAvailable()
        ) {

            if (
                    !annualFunctionalBaseline
                            .decadeSegmentAvailable()
            ) {

                throw new IllegalStateException(
                        "Monthly Decade Context는 사용 가능한데 Annual Functional Baseline에는 "
                                + "Decade Segment가 없습니다."
                );
            }


            for (
                    MonthlyLuckDecadeContextAnalysis monthlyDecadeContext
                    : monthlyLuckAnalysis
                    .decadeContexts()
            ) {

                int sequenceNumber =
                        monthlyDecadeContext
                                .decadeSequenceNumber();


                AnnualCrossFunctionalSynthesisSegmentAnalysis
                        annualSegment =
                        annualFunctionalBaseline
                                .segmentOfDecade(
                                        sequenceNumber
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalStateException(
                                                        "현재 Monthly Decade Context에 대응하는 "
                                                                + "Annual Functional Segment가 없습니다. sequence="
                                                                + sequenceNumber
                                                )
                                );


                List<MonthlyFunctionalActivationEffect>
                        segmentEffects =
                        new ArrayList<>(
                                NatalTenGodFunctionType
                                        .values()
                                        .length
                        );


                for (
                        NatalTenGodFunctionType functionType
                        : NatalTenGodFunctionType.values()
                ) {

                    AnnualResolvedFunctionEffect
                            resolvedAnnualEffect =
                            annualSegment
                                    .sourceSegmentAnalysis()
                                    .resolvedFunctionalSegment()
                                    .requireEffect(
                                            functionType
                                    );


                    AnnualFunctionCapacityContext
                            annualFunctionContext =
                            annualFunctionalBaseline
                                    .requireAnnualFunctionContext(
                                            functionType
                                    );


                    if (
                            resolvedAnnualEffect
                                    .annualFunctionContext()
                                    != annualFunctionContext
                    ) {

                        throw new IllegalStateException(
                                "Annual Resolved Function이 공식 Annual Function Context Identity를 보존하지 않았습니다. "
                                        + "sequence="
                                        + sequenceNumber
                                        + ", function="
                                        + functionType
                        );
                    }


                    Set<StructureTenGodType>
                            monthlyInputTenGodTypes =
                            resolveMonthlyInputTenGodTypes(
                                    monthlyLuckAnalysis,
                                    functionType
                            );


                    List<MonthlyLuckContextSignal>
                            commonSignals =
                            signalsForFunction(
                                    monthlyLuckAnalysis
                                            .monthlyContextSignals(),
                                    functionType
                            );


                    List<MonthlyLuckContextSignal>
                            segmentSignals =
                            signalsForFunction(
                                    monthlyDecadeContext
                                            .segmentSignals(),
                                    functionType
                            );


                    List<MonthlyLuckContextSignal>
                            mergedSignals =
                            mergeSignalsByIdentity(
                                    commonSignals,
                                    segmentSignals
                            );


                    segmentEffects.add(
                            createEffect(
                                    functionType,
                                    annualFunctionContext,
                                    resolvedAnnualEffect,
                                    monthlyLuckAnalysis,
                                    resolvedAnnualEffect
                                            .active(),
                                    monthlyInputTenGodTypes,
                                    mergedSignals,
                                    completeChart
                            )
                    );
                }


                segmentAnalyses.add(
                        new MonthlyFunctionalActivationSegmentAnalysis(
                                annualSegment,
                                monthlyLuckAnalysis,
                                monthlyDecadeContext,
                                segmentEffects,
                                completeChart
                        )
                );
            }
        }


        return new MonthlyFunctionalActivationAnalysis(
                annualFunctionalBaseline,
                monthlyLuckAnalysis,
                annualEffects,
                segmentAnalyses,
                completeChart
        );
    }


    /*
     * =================================================
     * Effect 생성
     * =================================================
     */

    private MonthlyFunctionalActivationEffect createEffect(

            NatalTenGodFunctionType functionType,

            AnnualFunctionCapacityContext annualFunctionContext,

            AnnualResolvedFunctionEffect segmentResolvedEffect,

            MonthlyLuckAnalysis monthlyLuckAnalysis,

            boolean annualBaselineActive,

            Set<StructureTenGodType> monthlyInputTenGodTypes,

            List<MonthlyLuckContextSignal> sourceSignals,

            boolean completeChart

    ) {

        boolean supported =
                sourceSignals
                        .stream()
                        .anyMatch(
                                MonthlyLuckContextSignal
                                        ::supported
                        );


        boolean qualified =
                sourceSignals
                        .stream()
                        .anyMatch(
                                MonthlyLuckContextSignal
                                        ::qualified
                        );


        boolean weakened =
                sourceSignals
                        .stream()
                        .anyMatch(
                                MonthlyLuckContextSignal
                                        ::weakened
                        );


        boolean conflicted =
                sourceSignals
                        .stream()
                        .anyMatch(
                                MonthlyLuckContextSignal
                                        ::conflicted
                        );


        MonthlyFunctionalActivationStatus activationStatus =
                MonthlyFunctionalActivationStatus.of(
                        !monthlyInputTenGodTypes.isEmpty(),
                        !sourceSignals.isEmpty(),
                        supported,
                        qualified,
                        weakened,
                        conflicted
                );


        return new MonthlyFunctionalActivationEffect(
                functionType,
                annualFunctionContext,
                segmentResolvedEffect,
                monthlyLuckAnalysis,
                annualBaselineActive,
                monthlyInputTenGodTypes,
                sourceSignals,
                activationStatus,
                completeChart
        );
    }


    /*
     * =================================================
     * Monthly Direct TenGod Input
     * =================================================
     *
     * 월운 천간 / 지장간에 실제 존재하는 TenGod만 사용한다.
     *
     * 개수는 강도 점수로 쓰지 않는다.
     */

    private Set<StructureTenGodType>
    resolveMonthlyInputTenGodTypes(

            MonthlyLuckAnalysis monthlyLuckAnalysis,

            NatalTenGodFunctionType functionType

    ) {

        Set<StructureTenGodType> result =
                new LinkedHashSet<>();


        for (
                StructureTenGodType tenGodType
                : StructureTenGodType.values()
        ) {

            if (
                    !functionType.contains(
                            tenGodType
                    )
            ) {

                continue;
            }


            if (
                    monthlyLuckAnalysis
                            .containsTenGod(
                                    tenGodType
                            )
            ) {

                result.add(
                        tenGodType
                );
            }
        }


        return Set.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Function-specific Context Signal
     * =================================================
     *
     * Global Signal:
     *
     * tenGodTypes.isEmpty()
     *
     * 은 특정 Function에 강제 연결하지 않는다.
     */

    private List<MonthlyLuckContextSignal>
    signalsForFunction(

            List<MonthlyLuckContextSignal> signals,

            NatalTenGodFunctionType functionType

    ) {

        if (
                signals == null
                        || signals.isEmpty()
        ) {

            return List.of();
        }


        List<MonthlyLuckContextSignal> result =
                new ArrayList<>();


        for (
                MonthlyLuckContextSignal signal
                : signals
        ) {

            Objects.requireNonNull(
                    signal,
                    "Monthly Context Signal은 null일 수 없습니다."
            );


            if (
                    signal.tenGodTypes()
                            .isEmpty()
            ) {

                continue;
            }


            boolean matched =
                    signal
                            .tenGodTypes()
                            .stream()
                            .anyMatch(
                                    functionType::contains
                            );


            if (
                    matched
            ) {

                result.add(
                        signal
                );
            }
        }


        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Signal Identity Merge
     * =================================================
     *
     * MonthlyLuckContextSignal은 provenance 객체다.
     *
     * 동일 값인지가 아니라
     * 동일 Source Signal 객체인지 기준으로 중복 제거한다.
     */

    private List<MonthlyLuckContextSignal>
    mergeSignalsByIdentity(

            List<MonthlyLuckContextSignal> first,

            List<MonthlyLuckContextSignal> second

    ) {

        Set<MonthlyLuckContextSignal> seen =
                Collections.newSetFromMap(
                        new IdentityHashMap<>()
                );


        List<MonthlyLuckContextSignal> result =
                new ArrayList<>();


        appendSignalsByIdentity(
                result,
                seen,
                first
        );


        appendSignalsByIdentity(
                result,
                seen,
                second
        );


        return List.copyOf(
                result
        );
    }


    private void appendSignalsByIdentity(

            List<MonthlyLuckContextSignal> target,

            Set<MonthlyLuckContextSignal> seen,

            List<MonthlyLuckContextSignal> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return;
        }


        for (
                MonthlyLuckContextSignal signal
                : source
        ) {

            Objects.requireNonNull(
                    signal,
                    "Monthly Context Signal은 null일 수 없습니다."
            );


            if (
                    seen.add(
                            signal
                    )
            ) {

                target.add(
                        signal
                );
            }
        }
    }


    /*
     * =================================================
     * Annual Function Context Identity
     * =================================================
     *
     * record equals/hashCode를 사용하지 않는다.
     */

    private boolean containsIdentity(

            List<AnnualFunctionCapacityContext> contexts,

            AnnualFunctionCapacityContext target

    ) {

        Objects.requireNonNull(
                target,
                "Annual Function Context Identity 조회 대상이 필요합니다."
        );


        if (
                contexts == null
                        || contexts.isEmpty()
        ) {

            return false;
        }


        for (
                AnnualFunctionCapacityContext context
                : contexts
        ) {

            if (
                    context
                            == target
            ) {

                return true;
            }
        }


        return false;
    }
}