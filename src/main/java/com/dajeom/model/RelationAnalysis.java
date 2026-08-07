package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 원국 내부의 천간·지지 관계 분석 결과다.
 */
public record RelationAnalysis(
        List<StemRelation> stemRelations,
        List<BranchRelation> branchRelations
) {

    public RelationAnalysis {
        Objects.requireNonNull(
                stemRelations,
                "천간 관계 목록은 필수입니다."
        );

        Objects.requireNonNull(
                branchRelations,
                "지지 관계 목록은 필수입니다."
        );

        stemRelations = List.copyOf(stemRelations);
        branchRelations = List.copyOf(branchRelations);
    }
}