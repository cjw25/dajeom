package com.dajeom.dto;

import com.dajeom.model.UsefulGodAnalysis;

import java.util.List;

public record UsefulGodAnalysisResponse(
        ClimateAnalysisResponse climateAnalysis,
        UsefulGodCandidateResponse primaryUsefulGod,
        UsefulGodCandidateResponse secondaryFavorableGod,
        List<UsefulGodCandidateResponse> usefulCandidates,
        List<UsefulGodCandidateResponse> unfavorableCandidates,
        List<UsefulGodCandidateResponse> allCandidates,
        String ruleVersion
) {

    public UsefulGodAnalysisResponse {
        usefulCandidates =
                List.copyOf(usefulCandidates);

        unfavorableCandidates =
                List.copyOf(
                        unfavorableCandidates
                );

        allCandidates =
                List.copyOf(allCandidates);
    }

    public static UsefulGodAnalysisResponse from(
            UsefulGodAnalysis analysis
    ) {
        return new UsefulGodAnalysisResponse(
                ClimateAnalysisResponse.from(
                        analysis.climateAnalysis()
                ),

                UsefulGodCandidateResponse.from(
                        analysis.primaryUsefulGod()
                ),

                analysis.secondaryFavorableGod()
                        == null
                        ? null
                        : UsefulGodCandidateResponse.from(
                        analysis
                                .secondaryFavorableGod()
                ),

                analysis.usefulCandidates()
                        .stream()
                        .map(
                                UsefulGodCandidateResponse::from
                        )
                        .toList(),

                analysis.unfavorableCandidates()
                        .stream()
                        .map(
                                UsefulGodCandidateResponse::from
                        )
                        .toList(),

                analysis.allCandidates()
                        .stream()
                        .map(
                                UsefulGodCandidateResponse::from
                        )
                        .toList(),

                analysis.ruleVersion()
        );
    }
}