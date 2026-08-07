package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 사주 원국의 종합 분석 결과다.
 */
public record NatalChartAnalysis(
        List<HiddenStem> hiddenStems,
        List<TenGodAssignment> tenGodAssignments,
        ElementDistribution elementDistribution,
        ElementStrength elementStrength,
        RelationAnalysis relationAnalysis,
        StrengthAnalysis strengthAnalysis,
        StructureAnalysis structureAnalysis,
        UsefulGodAnalysis usefulGodAnalysis
) {

    public NatalChartAnalysis {
        Objects.requireNonNull(
                hiddenStems,
                "지장간 목록은 필수입니다."
        );

        Objects.requireNonNull(
                tenGodAssignments,
                "십성 목록은 필수입니다."
        );

        Objects.requireNonNull(
                elementDistribution,
                "오행 분포는 필수입니다."
        );

        Objects.requireNonNull(
                elementStrength,
                "오행 세력은 필수입니다."
        );

        Objects.requireNonNull(
                relationAnalysis,
                "원국 관계 분석은 필수입니다."
        );

        Objects.requireNonNull(
                strengthAnalysis,
                "신강·신약 분석은 필수입니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "명리 구조 분석은 필수입니다."
        );

        Objects.requireNonNull(
                usefulGodAnalysis,
                "용신 분석은 필수입니다."
        );

        hiddenStems = List.copyOf(hiddenStems);

        tenGodAssignments =
                List.copyOf(tenGodAssignments);
    }
}