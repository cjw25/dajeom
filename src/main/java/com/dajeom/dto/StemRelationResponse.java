package com.dajeom.dto;

import com.dajeom.model.StemRelation;

/**
 * 천간 관계 Response다.
 */
public record StemRelationResponse(
        String relationType,
        String relationTypeKorean,
        String relationTypeChinese,

        String firstPosition,
        String firstPositionKorean,
        String firstStem,
        String firstStemKorean,

        String secondPosition,
        String secondPositionKorean,
        String secondStem,
        String secondStemKorean,

        String transformationElement,
        String transformationElementKorean,

        boolean transformationCandidate,
        boolean transformationEstablished,

        String description
) {

    public static StemRelationResponse from(
            StemRelation relation
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

        return new StemRelationResponse(
                relation.relationType().name(),

                relation
                        .relationType()
                        .getKoreanName(),

                relation
                        .relationType()
                        .getChineseName(),

                relation.firstPosition().name(),

                relation
                        .firstPosition()
                        .getKoreanName(),

                relation
                        .firstStem()
                        .getChineseCharacter(),

                relation
                        .firstStem()
                        .getKoreanName(),

                relation.secondPosition().name(),

                relation
                        .secondPosition()
                        .getKoreanName(),

                relation
                        .secondStem()
                        .getChineseCharacter(),

                relation
                        .secondStem()
                        .getKoreanName(),

                transformationElement,

                transformationElementKorean,

                relation.transformationCandidate(),

                relation.transformationEstablished(),

                relation.description()
        );
    }
}