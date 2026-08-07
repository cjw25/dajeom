package com.dajeom.dto;

import com.dajeom.model.RelationAnalysis;

import java.util.List;

/**
 * 원국의 천간·지지 관계 종합 Response다.
 */
public record RelationAnalysisResponse(
        List<StemRelationResponse> stemRelations,
        List<BranchRelationResponse> branchRelations,
        int totalRelationCount
) {

    public RelationAnalysisResponse {
        stemRelations =
                List.copyOf(stemRelations);

        branchRelations =
                List.copyOf(branchRelations);
    }

    public static RelationAnalysisResponse from(
            RelationAnalysis analysis
    ) {
        List<StemRelationResponse> stemRelations =
                analysis.stemRelations()
                        .stream()
                        .map(StemRelationResponse::from)
                        .toList();

        List<BranchRelationResponse>
                branchRelations =
                analysis.branchRelations()
                        .stream()
                        .map(BranchRelationResponse::from)
                        .toList();

        return new RelationAnalysisResponse(
                stemRelations,

                branchRelations,

                stemRelations.size()
                        + branchRelations.size()
        );
    }
}