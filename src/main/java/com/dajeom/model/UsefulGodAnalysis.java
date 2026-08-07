package com.dajeom.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 용신·희신·기신 종합 분석 결과다.
 */
public record UsefulGodAnalysis(
        ClimateAnalysis climateAnalysis,
        UsefulGodCandidate primaryUsefulGod,
        UsefulGodCandidate secondaryFavorableGod,
        List<UsefulGodCandidate> usefulCandidates,
        List<UsefulGodCandidate> unfavorableCandidates,
        List<UsefulGodCandidate> allCandidates,
        String ruleVersion
) {

    public UsefulGodAnalysis {
        Objects.requireNonNull(
                climateAnalysis,
                "조후 분석은 필수입니다."
        );

        Objects.requireNonNull(
                primaryUsefulGod,
                "주 용신 후보는 필수입니다."
        );

        Objects.requireNonNull(
                usefulCandidates,
                "유리한 오행 후보 목록은 필수입니다."
        );

        Objects.requireNonNull(
                unfavorableCandidates,
                "불리한 오행 후보 목록은 필수입니다."
        );

        Objects.requireNonNull(
                allCandidates,
                "전체 후보 목록은 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "용신 규칙 버전은 필수입니다."
        );

        usefulCandidates =
                usefulCandidates.stream()
                        .sorted(
                                Comparator.comparingDouble(
                                        UsefulGodCandidate::score
                                ).reversed()
                        )
                        .toList();

        unfavorableCandidates =
                unfavorableCandidates.stream()
                        .sorted(
                                Comparator.comparingDouble(
                                        UsefulGodCandidate::score
                                ).reversed()
                        )
                        .toList();

        allCandidates = List.copyOf(allCandidates);
    }
}