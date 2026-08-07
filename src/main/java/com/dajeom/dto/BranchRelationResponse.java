package com.dajeom.dto;

import com.dajeom.model.BranchRelation;

import java.util.List;

/**
 * 지지 관계 Response다.
 */
public record BranchRelationResponse(
        String relationType,
        String relationTypeKorean,
        String relationTypeChinese,

        List<String> positions,
        List<String> positionsKorean,

        List<String> branches,
        List<String> branchesKorean,

        String transformationElement,
        String transformationElementKorean,

        boolean transformationCandidate,
        boolean transformationEstablished,

        String description
) {

    public BranchRelationResponse {
        positions = List.copyOf(positions);

        positionsKorean =
                List.copyOf(positionsKorean);

        branches = List.copyOf(branches);

        branchesKorean =
                List.copyOf(branchesKorean);
    }

    public static BranchRelationResponse from(
            BranchRelation relation
    ) {
        String transformationElement =
                relation.transformationElement()
                        == null
                        ? null
                        : relation
                        .transformationElement()
                        .name();

        String transformationElementKorean =
                relation.transformationElement()
                        == null
                        ? null
                        : relation
                        .transformationElement()
                        .getKoreanName();

        return new BranchRelationResponse(
                relation.relationType().name(),

                relation
                        .relationType()
                        .getKoreanName(),

                relation
                        .relationType()
                        .getChineseName(),

                relation.positions()
                        .stream()
                        .map(Enum::name)
                        .toList(),

                relation.positions()
                        .stream()
                        .map(position ->
                                position.getKoreanName()
                        )
                        .toList(),

                relation.branches()
                        .stream()
                        .map(branch ->
                                branch.getChineseCharacter()
                        )
                        .toList(),

                relation.branches()
                        .stream()
                        .map(branch ->
                                branch.getKoreanName()
                        )
                        .toList(),

                transformationElement,

                transformationElementKorean,

                relation.transformationCandidate(),

                relation.transformationEstablished(),

                relation.description()
        );
    }
}