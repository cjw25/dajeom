package com.dajeom.type;

/**
 * 정밀화 17차-1.
 *
 * 신살 조건을 검사할 수 있는 시간 Layer.
 *
 * =================================================
 *
 * NATAL
 *
 * 원국 년월일시.
 *
 * =================================================
 *
 * DECADE
 *
 * 현재 대운.
 *
 * =================================================
 *
 * ANNUAL
 *
 * 현재 세운.
 *
 * =================================================
 *
 * MONTHLY
 *
 * 현재 월운.
 */
public enum ShinsalLayer {

    NATAL(
            "원국"
    ),

    DECADE(
            "대운"
    ),

    ANNUAL(
            "세운"
    ),

    MONTHLY(
            "월운"
    );

    private final String koreanName;

    ShinsalLayer(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean natal() {

        return this == NATAL;
    }

    public boolean decade() {

        return this == DECADE;
    }

    public boolean annual() {

        return this == ANNUAL;
    }

    public boolean monthly() {

        return this == MONTHLY;
    }
}