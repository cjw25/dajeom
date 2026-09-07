package com.dajeom.domain;

public enum TwelveShinsal {

    GYEOP_SAL(
            "劫煞",
            "겁살"
    ),

    JAE_SAL(
            "災煞",
            "재살"
    ),

    CHEON_SAL(
            "天煞",
            "천살"
    ),

    JI_SAL(
            "地煞",
            "지살"
    ),

    NYEON_SAL(
            "年煞",
            "년살"
    ),

    WOL_SAL(
            "月煞",
            "월살"
    ),

    MANGSIN_SAL(
            "亡身煞",
            "망신살"
    ),

    JANGSEONG_SAL(
            "將星煞",
            "장성살"
    ),

    BANAN_SAL(
            "攀鞍煞",
            "반안살"
    ),

    YEOKMA_SAL(
            "驛馬煞",
            "역마살"
    ),

    YUKHAE_SAL(
            "六害煞",
            "육해살"
    ),

    HWAGAE_SAL(
            "華蓋煞",
            "화개살"
    );

    private static final TwelveShinsal[] VALUES =
            values();

    private final String hanja;
    private final String korean;

    TwelveShinsal(
            String hanja,
            String korean
    ) {
        this.hanja = hanja;
        this.korean = korean;
    }

    public String hanja() {
        return hanja;
    }

    public String korean() {
        return korean;
    }

    public static TwelveShinsal ofIndex(
            int index
    ) {

        return VALUES[
                Math.floorMod(
                        index,
                        VALUES.length
                )
                ];
    }
}