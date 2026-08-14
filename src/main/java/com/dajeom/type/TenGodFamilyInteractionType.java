package com.dajeom.type;

/**
 * 정밀화 10차-7.
 *
 * 십성 Family 사이의 방향성 관계.
 *
 * =================================================
 *
 * GENERATION
 *
 * 생(生) 관계.
 *
 * 예:
 *
 * 식상 -> 재성
 * 재성 -> 관살
 * 관살 -> 인성
 *
 * =================================================
 *
 * CONTROL
 *
 * 극(剋) 관계.
 *
 * 예:
 *
 * 비겁 -> 재성
 * 재성 -> 인성
 * 인성 -> 식상
 * 식상 -> 관살
 *
 * =================================================
 *
 * 이 enum은 길흉을 의미하지 않는다.
 */
public enum TenGodFamilyInteractionType {

    GENERATION(
            "생"
    ),

    CONTROL(
            "극"
    );

    private final String koreanName;

    TenGodFamilyInteractionType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean generation() {

        return this == GENERATION;
    }

    public boolean control() {

        return this == CONTROL;
    }
}