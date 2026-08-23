package com.dajeom.model;

import com.dajeom.type.MonthlyFunctionalActivationStatus;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 Function Family에 대한
 * 월운 Functional Activation 결과.
 *
 * =================================================
 *
 * Source:
 *
 * AnnualFunctionCapacityContext
 *
 * +
 *
 * optional
 * AnnualResolvedFunctionEffect
 *
 * +
 *
 * MonthlyLuckAnalysis
 *
 * +
 *
 * 해당 Function과 구조적으로 연결되는
 * MonthlyLuckContextSignal
 *
 * =================================================
 *
 * 두 Scope가 존재한다.
 *
 * 1.
 * Annual Common Scope
 *
 * segmentResolvedEffect == null
 *
 * 세운 자체 Functional Baseline과
 * 월운 공통 입력 / 공통 Context를 연결한다.
 *
 * 2.
 * Annual-Decade Segment Scope
 *
 * segmentResolvedEffect != null
 *
 * 특정 Annual-Decade Segment의
 * 최종 Resolved Function에
 * 월운 대운 Segment Context까지 연결한다.
 *
 * =================================================
 *
 * 중요:
 *
 * annualBaselineActive는
 * 월운이 다시 결정하는 값이 아니다.
 *
 * 이미 Annual Functional Layer에서 확정된 상태를
 * 그대로 보존한다.
 *
 * Monthly Activation은 그 위에:
 *
 * - 접촉
 * - 지지
 * - 조건부
 * - 약화
 * - 충돌
 * - 혼재
 *
 * 를 붙일 뿐이다.
 */
public record MonthlyFunctionalActivationEffect(

        NatalTenGodFunctionType functionType,

        AnnualFunctionCapacityContext annualFunctionContext,

        AnnualResolvedFunctionEffect segmentResolvedEffect,

        MonthlyLuckAnalysis monthlyLuckAnalysis,

        boolean annualBaselineActive,

        Set<StructureTenGodType> monthlyInputTenGodTypes,

        List<MonthlyLuckContextSignal> sourceSignals,

        MonthlyFunctionalActivationStatus activationStatus,

        boolean completeChart

) {

    public MonthlyFunctionalActivationEffect {

        Objects.requireNonNull(
                functionType,
                "Monthly Functional Activation에는 Function Type이 필요합니다."
        );

        Objects.requireNonNull(
                annualFunctionContext,
                "Monthly Functional Activation에는 Annual Function Context가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckAnalysis,
                "Monthly Functional Activation에는 MonthlyLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                activationStatus,
                "Monthly Functional Activation에는 Activation Status가 필요합니다."
        );


        monthlyInputTenGodTypes =
                monthlyInputTenGodTypes == null
                        ? Set.of()
                        : Set.copyOf(
                        monthlyInputTenGodTypes
                );


        sourceSignals =
                sourceSignals == null
                        ? List.of()
                        : List.copyOf(
                        sourceSignals
                );


        /*
         * =================================================
         * Annual Function Family
         * =================================================
         */

        if (
                annualFunctionContext
                        .functionType()
                        != functionType
        ) {

            throw new IllegalArgumentException(
                    "Annual Function Context와 Monthly Activation Function Type이 일치하지 않습니다. "
                            + "expected="
                            + functionType
                            + ", actual="
                            + annualFunctionContext.functionType()
            );
        }


        if (
                annualFunctionContext.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Function Context와 Monthly Functional Activation의 completeChart가 일치하지 않습니다."
            );
        }


        /*
         * =================================================
         * Segment Resolved Function
         * =================================================
         */

        if (
                segmentResolvedEffect
                        != null
        ) {

            if (
                    segmentResolvedEffect
                            .functionType()
                            != functionType
            ) {

                throw new IllegalArgumentException(
                        "Annual Resolved Function과 Monthly Activation Function Type이 일치하지 않습니다."
                );
            }


            if (
                    segmentResolvedEffect
                            .annualFunctionContext()
                            != annualFunctionContext
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Activation이 공식 Annual Function Context Identity를 보존하지 않았습니다."
                );
            }


            if (
                    segmentResolvedEffect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual Resolved Function Effect와 Monthly Functional Activation의 completeChart가 일치하지 않습니다."
                );
            }


            if (
                    segmentResolvedEffect.active()
                            != annualBaselineActive
            ) {

                throw new IllegalArgumentException(
                        "Segment Scope Monthly Activation의 Annual Baseline Active 상태가 "
                                + "AnnualResolvedFunctionEffect와 일치하지 않습니다."
                );
            }
        }


        /*
         * =================================================
         * Monthly direct TenGod input
         * =================================================
         */

        for (
                StructureTenGodType tenGodType
                : monthlyInputTenGodTypes
        ) {

            Objects.requireNonNull(
                    tenGodType,
                    "Monthly Input TenGod Type은 null일 수 없습니다."
            );


            if (
                    !functionType.contains(
                            tenGodType
                    )
            ) {

                throw new IllegalArgumentException(
                        "Monthly Input TenGod Type이 현재 Function Family에 속하지 않습니다. "
                                + "function="
                                + functionType
                                + ", tenGodType="
                                + tenGodType
                );
            }


            if (
                    !monthlyLuckAnalysis
                            .containsTenGod(
                                    tenGodType
                            )
            ) {

                throw new IllegalArgumentException(
                        "MonthlyLuckAnalysis에 존재하지 않는 TenGod Type이 "
                                + "Monthly Functional Activation Input으로 연결되었습니다. "
                                + tenGodType
                );
            }
        }


        /*
         * =================================================
         * Context Signal Function Match
         * =================================================
         *
         * tenGodTypes가 비어 있는 Global Signal은
         * 특정 Function Effect에 연결하지 않는다.
         */

        for (
                MonthlyLuckContextSignal signal
                : sourceSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "Monthly Functional Activation Source Signal은 null일 수 없습니다."
            );


            if (
                    signal.tenGodTypes()
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Global Monthly Context Signal은 특정 Function Effect에 직접 연결할 수 없습니다. code="
                                + signal.code()
                );
            }


            boolean functionMatch =
                    signal
                            .tenGodTypes()
                            .stream()
                            .anyMatch(
                                    functionType::contains
                            );


            if (
                    !functionMatch
            ) {

                throw new IllegalArgumentException(
                        "현재 Function Family와 연결되지 않는 Monthly Context Signal입니다. "
                                + "function="
                                + functionType
                                + ", code="
                                + signal.code()
                );
            }
        }


        /*
         * =================================================
         * Activation Status 검증
         * =================================================
         */

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


        MonthlyFunctionalActivationStatus expectedStatus =
                MonthlyFunctionalActivationStatus.of(
                        !monthlyInputTenGodTypes.isEmpty(),
                        !sourceSignals.isEmpty(),
                        supported,
                        qualified,
                        weakened,
                        conflicted
                );


        if (
                activationStatus
                        != expectedStatus
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Activation Status가 실제 월운 Source와 일치하지 않습니다. "
                            + "function="
                            + functionType
                            + ", expected="
                            + expectedStatus
                            + ", actual="
                            + activationStatus
            );
        }
    }


    /*
     * =================================================
     * Scope
     * =================================================
     */

    public boolean segmentScoped() {

        return segmentResolvedEffect
                != null;
    }


    public boolean annualCommonScoped() {

        return segmentResolvedEffect
                == null;
    }


    public Optional<AnnualResolvedFunctionEffect>
    segmentResolvedEffectOptional() {

        return Optional.ofNullable(
                segmentResolvedEffect
        );
    }


    /*
     * =================================================
     * Monthly Input
     * =================================================
     */

    public boolean monthlyDirectInput() {

        return !monthlyInputTenGodTypes
                .isEmpty();
    }


    public boolean structuredContextExists() {

        return !sourceSignals
                .isEmpty();
    }


    public boolean monthlyContactExists() {

        return activationStatus
                .contacted();
    }


    /*
     * =================================================
     * Annual Baseline + Monthly Contact
     * =================================================
     */

    /**
     * 이미 Annual에서 활성된 Function에
     * 이번 월운이 실제 접촉하는가.
     */
    public boolean contactOnActiveAnnualBaseline() {

        return annualBaselineActive
                && monthlyContactExists();
    }


    /**
     * Annual에서는 활성되지 않았던 Function Family에
     * 월운 입력 또는 Context가 접촉한다.
     *
     * 중요:
     *
     * 이 값은 Annual Baseline을 활성 상태로
     * 변경했다는 뜻이 아니다.
     */
    public boolean monthlyContactAgainstInactiveAnnualBaseline() {

        return !annualBaselineActive
                && monthlyContactExists();
    }


    public boolean supportiveMonthlyContact() {

        return activationStatus
                .supportiveSide();
    }


    public boolean restrictiveMonthlyContact() {

        return activationStatus
                .restrictiveSide();
    }


    public boolean mixedMonthlyContact() {

        return activationStatus
                .mixed();
    }


    /*
     * =================================================
     * Qualification
     * =================================================
     */

    public boolean qualificationRequired() {

        return activationStatus
                .qualified()

                || activationStatus
                .mixed()

                || (
                segmentResolvedEffect != null
                        && segmentResolvedEffect
                        .qualificationRequired()
        );
    }
}