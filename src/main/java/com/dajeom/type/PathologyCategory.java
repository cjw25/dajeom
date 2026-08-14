package com.dajeom.type;

/**
 * 정밀화 6차-4.
 *
 * 병(病) 구조의 큰 분류.
 */
public enum PathologyCategory {

    ELEMENT_CONCENTRATION(
            "오행 편중"
    ),

    DAY_MASTER_PRESSURE(
            "일간 부담"
    ),

    CLIMATE_IMBALANCE(
            "조후 불균형"
    ),

    STRUCTURE_DAMAGE(
            "격국 구조 손상"
    );

    private final String koreanName;

    PathologyCategory(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean elementConcentration() {

        return this == ELEMENT_CONCENTRATION;
    }

    public boolean dayMasterPressure() {

        return this == DAY_MASTER_PRESSURE;
    }

    public boolean climateImbalance() {

        return this == CLIMATE_IMBALANCE;
    }

    public boolean structureDamage() {

        return this == STRUCTURE_DAMAGE;
    }
}