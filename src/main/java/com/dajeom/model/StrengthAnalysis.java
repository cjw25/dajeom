package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StrengthLevel;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 신강·신약 종합 판정 결과다.
 */
public record StrengthAnalysis(
        HeavenlyStem dayMaster,
        FiveElement dayMasterElement,
        StrengthLevel level,
        double score,
        double supportPercentage,
        double pressurePercentage,
        boolean gainsSeason,
        boolean hasRoot,
        int rootCount,
        double relationAdjustmentScore,
        List<StrengthEvidence> evidence,
        String ruleVersion
) {

    public StrengthAnalysis {
        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행은 필수입니다."
        );

        Objects.requireNonNull(
                level,
                "강약 단계는 필수입니다."
        );

        Objects.requireNonNull(
                evidence,
                "강약 판정 근거는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "강약 규칙 버전은 필수입니다."
        );

        evidence = List.copyOf(evidence);
    }
}