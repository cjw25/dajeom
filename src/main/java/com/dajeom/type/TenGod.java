package com.dajeom.type;

/**
 * 십성.
 *
 * 실제 십성 판정은 TenGodCalculator에서 수행한다.
 * 이 enum은 십성의 종류와 표시명만 관리한다.
 */
public enum TenGod {

    BI_GYEON(
            "비견",
            "比肩"
    ),

    GEOP_JAE(
            "겁재",
            "劫財"
    ),

    SIK_SIN(
            "식신",
            "食神"
    ),

    SANG_GWAN(
            "상관",
            "傷官"
    ),

    PYEON_JAE(
            "편재",
            "偏財"
    ),

    JEONG_JAE(
            "정재",
            "正財"
    ),

    PYEON_GWAN(
            "편관",
            "偏官"
    ),

    JEONG_GWAN(
            "정관",
            "正官"
    ),

    PYEON_IN(
            "편인",
            "偏印"
    ),

    JEONG_IN(
            "정인",
            "正印"
    );

    private final String koreanName;
    private final String chineseCharacter;

    TenGod(
            String koreanName,
            String chineseCharacter
    ) {
        this.koreanName = koreanName;
        this.chineseCharacter =
                chineseCharacter;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getChineseCharacter() {
        return chineseCharacter;
    }
}