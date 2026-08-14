package com.dajeom.model;

import com.dajeom.type.AnnualDecadeLuckRelationLayer;

import java.util.Objects;

/**
 * 정밀화 14차-5.
 *
 * 특정 세운과 특정 대운 사이에서
 * 발생하는 하나의 관계 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 대운 甲寅
 * 세운 己酉
 *
 * 천간:
 *
 * 甲 ↔ 己
 * -> 천간합
 *
 * =================================================
 *
 * relationCode / relationName은
 * 기존 8차 관계 분석 결과를
 * Resolver가 정규화해서 전달한다.
 *
 * 이 클래스 자체가 합·충 공식을
 * 계산하지 않는다.
 */
public record AnnualDecadeLuckRelationReference(

        AnnualDecadeLuckRelationLayer layer,

        String relationCode,

        String relationName

) {

    public AnnualDecadeLuckRelationReference {

        Objects.requireNonNull(
                layer,
                "대운-세운 관계 Layer가 필요합니다."
        );

        if (
                relationCode == null
                        || relationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-세운 관계 Code가 필요합니다."
            );
        }

        relationCode =
                relationCode.trim();

        if (
                relationName == null
                        || relationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-세운 관계 이름이 필요합니다."
            );
        }

        relationName =
                relationName.trim();
    }

    public boolean stemRelation() {

        return layer
                .heavenlyStem();
    }

    public boolean branchRelation() {

        return layer
                .earthlyBranch();
    }
}