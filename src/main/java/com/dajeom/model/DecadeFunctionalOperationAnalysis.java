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
 * Function Operation Aggregate.
 *
 * =================================================
 *
 * DecadeFunctionalAnalysis
 *
 * ↓
 *
 * DecadePeriodFunctionalOperationAnalysis
 *
 * =================================================
 *
 * 현재 단계의 의미:
 *
 * 대운에서 들어온 기능이:
 *
 * - 어떤 Layer에서 들어왔는가
 * - 어떤 원국 관계에 접촉하는가
 * - 합화에 참여하는가
 * - 대운 Participant 원기능 감소가 있는가
 * - 어떤 목표 오행 활성 Context가 있는가
 *
 * 를 구조적으로 보존한다.
 *
 * =================================================
 *
 * 아직:
 *
 * - 최종 기능 강화/약화
 * - 길흉
 * - 생활영역
 *
 * 을 판정하지 않는다.
 */
public record DecadeFunctionalOperationAnalysis(

        DecadeFunctionalAnalysis
        functionalAnalysis,

        List<DecadePeriodFunctionalOperationAnalysis>
        periodAnalyses,

        boolean completeChart

) {

    public DecadeFunctionalOperationAnalysis {

        Objects.requireNonNull(
                functionalAnalysis,
                "DecadeFunctionalOperationAnalysis에는 DecadeFunctionalAnalysis가 필요합니다."
        );

        periodAnalyses =
                periodAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        periodAnalyses
                );

        if (
                functionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "DecadeFunctionalOperationAnalysis의 completeChart가 "
                            + "원본 FunctionalAnalysis와 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !functionalAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !periodAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "대운 FunctionalAnalysis가 unavailable인데 "
                                + "Function Operation Period가 존재합니다."
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
                            != functionalAnalysis
                            .periodAnalyses()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Functional Period 수와 Operation Period 수가 일치하지 않습니다."
                );
            }

            Set<Integer> sequenceNumbers =
                    new HashSet<>();

            for (
                    DecadePeriodFunctionalOperationAnalysis periodAnalysis
                    : periodAnalyses
            ) {

                Objects.requireNonNull(
                        periodAnalysis,
                        "DecadePeriodFunctionalOperationAnalysis는 null일 수 없습니다."
                );

                if (
                        periodAnalysis.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Operation Period의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !sequenceNumbers.add(
                                periodAnalysis.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일 대운 Operation Period가 중복되었습니다: "
                                    + periodAnalysis.sequenceNumber()
                    );
                }

                DecadePeriodFunctionalAnalysis
                        expected =
                        functionalAnalysis
                                .periodAnalysis(
                                        periodAnalysis
                                                .sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "원본 Functional Period를 찾을 수 없습니다: "
                                                                + periodAnalysis.sequenceNumber()
                                                )
                                );

                if (
                        expected
                                != periodAnalysis
                                .functionalAnalysis()
                ) {

                    throw new IllegalArgumentException(
                            "Operation Period가 원본 Functional Period Identity를 보존하지 않았습니다: "
                                    + periodAnalysis.sequenceNumber()
                    );
                }
            }

            for (
                    DecadePeriodFunctionalAnalysis sourcePeriod
                    : functionalAnalysis.periodAnalyses()
            ) {

                if (
                        !sequenceNumbers.contains(
                                sourcePeriod.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "대운 Function Operation Period가 누락되었습니다: "
                                    + sourcePeriod.sequenceNumber()
                    );
                }
            }
        }
    }

    public boolean analysisAvailable() {

        return functionalAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return functionalAnalysis
                .unavailableReason();
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    public Optional<DecadePeriodFunctionalOperationAnalysis>
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

    public DecadePeriodFunctionalOperationAnalysis
    requirePeriodAnalysis(

            int sequenceNumber

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "대운 Function Operation Period를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * Function across decades
     * =================================================
     */

    public List<DecadeFunctionOperation>
    operationsOf(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        List<DecadeFunctionOperation> result =
                new ArrayList<>();

        for (
                DecadePeriodFunctionalOperationAnalysis periodAnalysis
                : periodAnalyses
        ) {

            periodAnalysis
                    .operationOf(
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

    public List<DecadePeriodFunctionalOperationAnalysis>
    periodsWithDirectInput(

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
                        periodAnalysis ->
                                periodAnalysis
                                        .requireOperation(
                                                functionType
                                        )
                                        .directlyEntered()
                )
                .toList();
    }

    public List<DecadePeriodFunctionalOperationAnalysis>
    periodsWithOriginalFunctionReduction(

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
                        periodAnalysis ->
                                periodAnalysis
                                        .requireOperation(
                                                functionType
                                        )
                                        .originalFunctionReductionExists()
                )
                .toList();
    }

    public List<DecadePeriodFunctionalOperationAnalysis>
    periodsWithActiveTransformation(

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
                        periodAnalysis ->
                                periodAnalysis
                                        .requireOperation(
                                                functionType
                                        )
                                        .activeTransformationExists()
                )
                .toList();
    }
}