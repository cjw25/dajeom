package com.dajeom.model;

import com.dajeom.type.SajuReportCategory;

import java.util.List;
import java.util.Objects;

/**
 * 모든 카테고리의 해석 신호를 모은 결과다.
 */
public record InterpretationSignalAnalysis(
        List<CategoryInterpretation> categories,
        int totalSignalCount,
        String ruleVersion
) {

    public InterpretationSignalAnalysis {
        Objects.requireNonNull(
                categories,
                "카테고리 해석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "해석 신호 규칙 버전은 필수입니다."
        );

        categories = List.copyOf(categories);
    }

    public CategoryInterpretation findCategory(
            SajuReportCategory category
    ) {
        return categories.stream()
                .filter(result ->
                        result.category() == category
                )
                .findFirst()
                .orElse(null);
    }
}