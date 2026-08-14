package com.dajeom.model;

import com.dajeom.type.MonthlyAnnualLuckRelationLayer;

import java.util.Objects;

/**
 * 정밀화 15차-6.
 *
 * 하나의 세운과 하나의 월운 사이에 발생한
 * 관계 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 세운 丙午
 * 월운 辛卯
 *
 * 천간:
 *
 * 丙 ↔ 辛
 * -> 천간합 관계
 *
 * 지지:
 *
 * 午 ↔ 卯
 * -> 기존 8차 관계 결과
 *
 * =================================================
 *
 * 관계 공식은 이 클래스에서 계산하지 않는다.
 *
 * relationCode와 relationName은
 * 기존 8차 deterministic 관계 분석을
 * Resolver가 정규화해서 전달한다.
 */
public record MonthlyAnnualLuckRelationReference(

        MonthlyAnnualLuckRelationLayer layer,

        String relationCode,

        String relationName

) {

    public MonthlyAnnualLuckRelationReference {

        Objects.requireNonNull(
                layer,
                "세운-월운 관계 Layer가 필요합니다."
        );

        if (
                relationCode == null
                        || relationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운-월운 관계 Code가 필요합니다."
            );
        }

        relationCode =
                relationCode.trim();

        if (
                relationName == null
                        || relationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "세운-월운 관계 이름이 필요합니다."
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