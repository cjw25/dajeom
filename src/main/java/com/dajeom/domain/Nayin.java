package com.dajeom.domain;

import java.util.Objects;

public enum Nayin {

    HAI_ZHONG_JIN(
            "海中金",
            "해중금",
            FiveElement.METAL
    ),

    LU_ZHONG_HUO(
            "爐中火",
            "노중화",
            FiveElement.FIRE
    ),

    DA_LIN_MU(
            "大林木",
            "대림목",
            FiveElement.WOOD
    ),

    LU_PANG_TU(
            "路旁土",
            "노방토",
            FiveElement.EARTH
    ),

    JIAN_FENG_JIN(
            "劍鋒金",
            "검봉금",
            FiveElement.METAL
    ),

    SHAN_TOU_HUO(
            "山頭火",
            "산두화",
            FiveElement.FIRE
    ),

    JIAN_XIA_SHUI(
            "澗下水",
            "간하수",
            FiveElement.WATER
    ),

    CHENG_TOU_TU(
            "城頭土",
            "성두토",
            FiveElement.EARTH
    ),

    BAI_LA_JIN(
            "白蠟金",
            "백랍금",
            FiveElement.METAL
    ),

    YANG_LIU_MU(
            "楊柳木",
            "양류목",
            FiveElement.WOOD
    ),

    QUAN_ZHONG_SHUI(
            "泉中水",
            "천중수",
            FiveElement.WATER
    ),

    WU_SHANG_TU(
            "屋上土",
            "옥상토",
            FiveElement.EARTH
    ),

    PI_LI_HUO(
            "霹靂火",
            "벽력화",
            FiveElement.FIRE
    ),

    SONG_BAI_MU(
            "松柏木",
            "송백목",
            FiveElement.WOOD
    ),

    CHANG_LIU_SHUI(
            "長流水",
            "장류수",
            FiveElement.WATER
    ),

    SHA_ZHONG_JIN(
            "沙中金",
            "사중금",
            FiveElement.METAL
    ),

    SHAN_XIA_HUO(
            "山下火",
            "산하화",
            FiveElement.FIRE
    ),

    PING_DI_MU(
            "平地木",
            "평지목",
            FiveElement.WOOD
    ),

    BI_SHANG_TU(
            "壁上土",
            "벽상토",
            FiveElement.EARTH
    ),

    JIN_BO_JIN(
            "金箔金",
            "금박금",
            FiveElement.METAL
    ),

    FU_DENG_HUO(
            "覆燈火",
            "복등화",
            FiveElement.FIRE
    ),

    TIAN_HE_SHUI(
            "天河水",
            "천하수",
            FiveElement.WATER
    ),

    DA_YI_TU(
            "大驛土",
            "대역토",
            FiveElement.EARTH
    ),

    CHAI_CHUAN_JIN(
            "釵釧金",
            "차천금",
            FiveElement.METAL
    ),

    SANG_ZHE_MU(
            "桑柘木",
            "상자목",
            FiveElement.WOOD
    ),

    DA_XI_SHUI(
            "大溪水",
            "대계수",
            FiveElement.WATER
    ),

    SHA_ZHONG_TU(
            "沙中土",
            "사중토",
            FiveElement.EARTH
    ),

    TIAN_SHANG_HUO(
            "天上火",
            "천상화",
            FiveElement.FIRE
    ),

    SHI_LIU_MU(
            "石榴木",
            "석류목",
            FiveElement.WOOD
    ),

    DA_HAI_SHUI(
            "大海水",
            "대해수",
            FiveElement.WATER
    );

    private static final Nayin[] VALUES =
            values();

    private final String hanja;
    private final String korean;
    private final FiveElement element;

    Nayin(
            String hanja,
            String korean,
            FiveElement element
    ) {

        this.hanja =
                Objects.requireNonNull(
                        hanja
                );

        this.korean =
                Objects.requireNonNull(
                        korean
                );

        this.element =
                Objects.requireNonNull(
                        element
                );
    }

    public String hanja() {
        return hanja;
    }

    public String korean() {
        return korean;
    }

    public FiveElement element() {
        return element;
    }

    /**
     * 60갑자는 두 개씩 동일한 납음을 가진다.
     *
     * 0,1   → 海中金
     * 2,3   → 爐中火
     * ...
     * 58,59 → 大海水
     */
    public static Nayin ofCycleIndex(
            int cycleIndex
    ) {

        if (cycleIndex < 0
                || cycleIndex >= SexagenaryCycle.CYCLE_SIZE) {

            throw new IllegalArgumentException(
                    "60갑자 index는 0~59 사이여야 합니다. cycleIndex="
                            + cycleIndex
            );
        }

        int nayinIndex =
                cycleIndex / 2;

        return VALUES[nayinIndex];
    }

    public static Nayin of(
            Ganji ganji
    ) {

        Objects.requireNonNull(
                ganji,
                "납음 계산 기준 간지가 필요합니다."
        );

        return ofCycleIndex(
                ganji.cycleIndex()
        );
    }
}