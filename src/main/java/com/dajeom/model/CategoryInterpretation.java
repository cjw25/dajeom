package com.dajeom.model;

import com.dajeom.type.SajuReportCategory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 하나의 사주 보고서 카테고리에 포함되는
 * 해석 신호 묶음이다.
 */
public record CategoryInterpretation(

        SajuReportCategory category,

        double favorableScore,

        double challengingScore,

        double netScore,

        List<InterpretationSignal> signals
) {

    public CategoryInterpretation {

        Objects.requireNonNull(
                category,
                "보고서 카테고리는 필수입니다."
        );

        Objects.requireNonNull(
                signals,
                "해석 신호 목록은 필수입니다."
        );

        favorableScore =
                round(
                        favorableScore
                );

        challengingScore =
                round(
                        challengingScore
                );

        netScore =
                round(
                        netScore
                );

        signals =
                signals.stream()
                        .sorted(
                                Comparator.comparingDouble(
                                        (InterpretationSignal signal) ->
                                                Math.abs(
                                                        signal.score()
                                                )
                                ).reversed()
                        )
                        .toList();
    }

    public InterpretationSignal primarySignal() {

        return signals.stream()
                .findFirst()
                .orElse(null);
    }

    private static double round(
            double value
    ) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }
}