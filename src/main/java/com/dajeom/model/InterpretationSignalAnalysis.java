package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 11개 상담 분야 전체에 대한
 * 해석 신호 분석 결과.
 *
 * 이 모델이 Interpretation 계층과
 * Consultation 계층 사이의 경계가 된다.
 */
public record InterpretationSignalAnalysis(

        List<InterpretationSignal> signals,

        String ruleVersion

) {

    public InterpretationSignalAnalysis {

        Objects.requireNonNull(
                signals,
                "해석 신호 목록은 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        signals =
                List.copyOf(
                        signals
                );
    }

    public Optional<InterpretationSignal> find(
            ConsultationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "상담 분야는 필수입니다."
        );

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.category()
                                        == category
                )
                .findFirst();
    }

    public InterpretationSignal get(
            ConsultationCategory category
    ) {

        return find(
                category
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "상담 분야 신호를 찾을 수 없습니다: "
                                                + category
                                )
                );
    }
}