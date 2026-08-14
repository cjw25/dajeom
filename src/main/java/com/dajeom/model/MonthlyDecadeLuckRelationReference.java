package com.dajeom.model;

import com.dajeom.type.MonthlyDecadeLuckRelationLayer;

import java.util.Objects;

/**
 * 정밀화 15차-5.
 *
 * 특정 대운과 특정 월운 사이에 발생한
 * 하나의 관계 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 대운 甲寅
 * 월운 己卯
 *
 * 甲 ↔ 己
 * -> 천간합 관계
 *
 * =================================================
 *
 * 실제 관계 공식은
 * 기존 8차 관계 계층을 사용한다.
 */
public record MonthlyDecadeLuckRelationReference(

        MonthlyDecadeLuckRelationLayer layer,

        String relationCode,

        String relationName

) {

    public MonthlyDecadeLuckRelationReference {

        Objects.requireNonNull(
                layer,
                "대운-월운 관계 Layer가 필요합니다."
        );

        if (
                relationCode == null
                        || relationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-월운 관계 Code가 필요합니다."
            );
        }

        relationCode =
                relationCode.trim();

        if (
                relationName == null
                        || relationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-월운 관계 이름이 필요합니다."
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