package com.dajeom.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 원국의 주요 명리 구조 판정 결과다.
 */
public record StructureAnalysis(
        List<StructureResult> results,
        String ruleVersion
) {

    public StructureAnalysis {
        Objects.requireNonNull(
                results,
                "명리 구조 결과는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "명리 구조 규칙 버전은 필수입니다."
        );

        results = results.stream()
                .sorted(
                        Comparator.comparingDouble(
                                StructureResult::score
                        ).reversed()
                )
                .toList();
    }

    /**
     * 실제로 성립한 구조만 반환한다.
     */
    public List<StructureResult> matchedResults() {
        return results.stream()
                .filter(StructureResult::matched)
                .toList();
    }

    /**
     * 가장 점수가 높은 성립 구조를 반환한다.
     */
    public StructureResult primaryStructure() {
        return matchedResults()
                .stream()
                .findFirst()
                .orElse(null);
    }
}