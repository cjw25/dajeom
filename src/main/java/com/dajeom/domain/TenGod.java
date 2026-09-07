package com.dajeom.domain;

/**
 * 십성.
 *
 * 내부 계산용 명리 데이터다.
 *
 * 최종 사용자 풀이에서는
 * "비견", "정재", "편관" 같은 명리 용어를
 * 그대로 노출하지 않고 생활 언어로 변환한다.
 */
public enum TenGod {

    BI_JIAN(
            "比肩",
            "비견"
    ),

    JIE_CAI(
            "劫財",
            "겁재"
    ),

    SHI_SHEN(
            "食神",
            "식신"
    ),

    SHANG_GUAN(
            "傷官",
            "상관"
    ),

    PIAN_CAI(
            "偏財",
            "편재"
    ),

    ZHENG_CAI(
            "正財",
            "정재"
    ),

    PIAN_GUAN(
            "偏官",
            "편관"
    ),

    ZHENG_GUAN(
            "正官",
            "정관"
    ),

    PIAN_YIN(
            "偏印",
            "편인"
    ),

    ZHENG_YIN(
            "正印",
            "정인"
    );

    private final String hanja;
    private final String korean;

    TenGod(
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

    @Override
    public String toString() {
        return korean;
    }
}