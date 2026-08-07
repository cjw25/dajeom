package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.LuckEvaluationLevel;
import com.dajeom.type.TenGod;
import com.dajeom.type.UsefulGodType;

import java.util.List;
import java.util.Objects;

/**
 * 대운 또는 세운 하나의 종합 평가 결과다.
 */
public record LuckPeriodEvaluation(
        String periodType,
        Pillar pillar,
        FiveElement stemElement,
        FiveElement branchElement,
        TenGod stemTenGod,
        TenGod branchTenGod,
        UsefulGodType stemUsefulGodType,
        UsefulGodType branchUsefulGodType,
        double elementCompatibilityScore,
        double tenGodScore,
        double relationScore,
        double totalScore,
        LuckEvaluationLevel level,
        List<LuckInteraction> interactions,
        List<String> evidence
) {

    public LuckPeriodEvaluation {
        Objects.requireNonNull(
                periodType,
                "운 구분은 필수입니다."
        );

        Objects.requireNonNull(
                pillar,
                "운 간지는 필수입니다."
        );

        Objects.requireNonNull(
                stemElement,
                "운 천간 오행은 필수입니다."
        );

        Objects.requireNonNull(
                branchElement,
                "운 지지 오행은 필수입니다."
        );

        Objects.requireNonNull(
                stemTenGod,
                "운 천간 십성은 필수입니다."
        );

        Objects.requireNonNull(
                branchTenGod,
                "운 지지 십성은 필수입니다."
        );

        Objects.requireNonNull(
                stemUsefulGodType,
                "운 천간 용신 분류는 필수입니다."
        );

        Objects.requireNonNull(
                branchUsefulGodType,
                "운 지지 용신 분류는 필수입니다."
        );

        Objects.requireNonNull(
                level,
                "운 평가 단계는 필수입니다."
        );

        Objects.requireNonNull(
                interactions,
                "상호작용 목록은 필수입니다."
        );

        Objects.requireNonNull(
                evidence,
                "운 평가 근거는 필수입니다."
        );

        interactions = List.copyOf(interactions);
        evidence = List.copyOf(evidence);
    }
}