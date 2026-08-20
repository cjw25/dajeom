package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 전체 대운 Sequence의
 * Natal Capacity Context Aggregate.
 *
 * =================================================
 *
 * NatalFunctionalAnalysis
 *
 * +
 *
 * DecadeFunctionalOperationAnalysis
 *
 * ↓
 *
 * Period별 Capacity Analysis
 *
 * =================================================
 *
 * 핵심:
 *
 * NatalFunctionalAnalysis.coreSelf()
 *
 * 는 모든 대운에서 동일한
 * 원국 Anchor로 사용한다.
 *
 * =================================================
 *
 * 대운마다 DayMasterStrengthAnalysis를
 * 다시 만드는 구조가 아니다.
 */
public record DecadeFunctionalCapacityAnalysis(

        NatalFunctionalAnalysis natalFunctionalAnalysis,

        DecadeFunctionalOperationAnalysis
        operationAnalysis,

        List<DecadePeriodFunctionalCapacityAnalysis>
        periodAnalyses,

        boolean completeChart

) {

    public DecadeFunctionalCapacityAnalysis {

        Objects.requireNonNull(
                natalFunctionalAnalysis,
                "DecadeFunctionalCapacityAnalysis에는 NatalFunctionalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                operationAnalysis,
                "DecadeFunctionalCapacityAnalysis에는 DecadeFunctionalOperationAnalysis가 필요합니다."
        );

        periodAnalyses =
                periodAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        periodAnalyses
                );

        /*
         * =================================================
         * 동일 원국
         * =================================================
         */

        if (
                natalFunctionalAnalysis.dayMaster()
                        != operationAnalysis
                        .functionalAnalysis()
                        .decadeLuckAnalysis()
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "NatalFunctionalAnalysis와 대운 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * completeChart
         * =================================================
         */

        if (
                natalFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "NatalFunctionalAnalysis와 Capacity Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                operationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Operation Analysis와 Capacity Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !operationAnalysis.analysisAvailable()
        ) {

            if (
                    !periodAnalyses.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "대운 Operation Analysis가 unavailable인데 Capacity Period가 존재합니다."
                );
            }

        } else {

            /*
             * =================================================
             * Period Coverage
             * =================================================
             */

            if (
                    periodAnalyses.size()
                            != operationAnalysis
                            .periodAnalyses()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Operation Period 수와 Capacity Period 수가 일치하지 않습니다."
                );
            }

            Set<Integer> sequenceNumbers =
                    new HashSet<>();

            for (
                    DecadePeriodFunctionalCapacityAnalysis periodAnalysis
                    : periodAnalyses
            ) {

                Objects.requireNonNull(
                        periodAnalysis,
                        "대운 Capacity Period는 null일 수 없습니다."
                );

                if (
                        periodAnalysis.natalFunctionalAnalysis()
                                != natalFunctionalAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "Capacity Period가 현재 NatalFunctionalAnalysis를 참조하지 않습니다."
                    );
                }

                if (
                        periodAnalysis.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Capacity Period의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !sequenceNumbers.add(
                                periodAnalysis.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일 대운 Capacity Period가 중복되었습니다: "
                                    + periodAnalysis.sequenceNumber()
                    );
                }

                DecadePeriodFunctionalOperationAnalysis expected =
                        operationAnalysis
                                .periodAnalysis(
                                        periodAnalysis.sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "원본 Operation Period를 찾을 수 없습니다: "
                                                                + periodAnalysis.sequenceNumber()
                                                )
                                );

                if (
                        periodAnalysis.operationAnalysis()
                                != expected
                ) {

                    throw new IllegalArgumentException(
                            "Capacity Period가 원본 Operation Period Identity를 보존하지 않았습니다: "
                                    + periodAnalysis.sequenceNumber()
                    );
                }
            }

            for (
                    DecadePeriodFunctionalOperationAnalysis sourcePeriod
                    : operationAnalysis.periodAnalyses()
            ) {

                if (
                        !sequenceNumbers.contains(
                                sourcePeriod.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "대운 Capacity Period가 누락되었습니다: "
                                    + sourcePeriod.sequenceNumber()
                    );
                }
            }
        }
    }

    public boolean analysisAvailable() {

        return operationAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return operationAnalysis
                .unavailableReason();
    }

    public NatalCoreSelfEffect coreSelf() {

        return natalFunctionalAnalysis
                .coreSelf();
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    public Optional<DecadePeriodFunctionalCapacityAnalysis>
    periodAnalysis(

            int sequenceNumber

    ) {

        if (
                sequenceNumber < 1
        ) {

            return Optional.empty();
        }

        return periodAnalyses
                .stream()
                .filter(
                        analysis ->
                                analysis.sequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    public DecadePeriodFunctionalCapacityAnalysis
    requirePeriodAnalysis(

            int sequenceNumber

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "대운 Capacity Period를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * Function across decades
     * =================================================
     */

    public List<DecadeFunctionCapacityContext>
    contextsOf(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        List<DecadeFunctionCapacityContext> result =
                new ArrayList<>();

        for (
                DecadePeriodFunctionalCapacityAnalysis period
                : periodAnalyses
        ) {

            period
                    .contextOf(
                            functionType
                    )
                    .ifPresent(
                            result::add
                    );
        }

        return List.copyOf(
                result
        );
    }

    public List<DecadePeriodFunctionalCapacityAnalysis>
    periodsRequiringCapacityReview(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return periodAnalyses
                .stream()
                .filter(
                        period ->
                                period
                                        .requireContext(
                                                functionType
                                        )
                                        .capacityReviewRequired()
                )
                .toList();
    }
}