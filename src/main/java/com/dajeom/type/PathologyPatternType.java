package com.dajeom.type;

/**
 * 정밀화 6차-4.
 *
 * 원국에서 탐지하는 병(病) 구조 유형.
 *
 * 이 타입은:
 *
 * - 의학적 질병
 * - 장기 질환
 * - 실제 건강 진단
 *
 * 을 의미하지 않는다.
 *
 * 명리 구조 안에서의
 * 세력 불균형과 병목 유형이다.
 */
public enum PathologyPatternType {

    /**
     * 특정 오행 하나가
     * 원국 전체 세력에서 지나치게 집중된 상태.
     */
    ELEMENT_OVERCONCENTRATION(
            "오행 과다 집중",
            "특정 오행의 세력이 원국 전체에서 지나치게 집중되어 있습니다.",
            PathologyCategory.ELEMENT_CONCENTRATION,
            true
    ),

    /**
     * 신약한 일간이
     * 강한 식상으로 지나치게 설기되는 구조.
     */
    OUTPUT_DRAIN_OVERLOAD(
            "식상 설기 과다",
            "일간에 비해 식상 세력이 커서 설기 부담이 강하게 형성됩니다.",
            PathologyCategory.DAY_MASTER_PRESSURE,
            true
    ),

    /**
     * 신약한 일간이
     * 재성을 감당하기 어려운 구조.
     */
    WEALTH_EXERTION_OVERLOAD(
            "재성 부담 과다",
            "일간에 비해 재성 세력이 커서 재성을 감당하는 부담이 커집니다.",
            PathologyCategory.DAY_MASTER_PRESSURE,
            true
    ),

    /**
     * 신약한 일간에
     * 관살 압력이 과도한 구조.
     */
    OFFICER_CONTROL_OVERLOAD(
            "관살 압박 과다",
            "일간에 비해 관살 세력이 강하여 극제 압력이 크게 형성됩니다.",
            PathologyCategory.DAY_MASTER_PRESSURE,
            true
    ),

    /**
     * 이미 강한 일간에
     * 비겁·인성 지원이 지나치게 집중된 상태.
     */
    SUPPORT_OVERCONCENTRATION(
            "생조 세력 과다",
            "강한 일간에 비겁·인성 생조 세력이 지나치게 집중되어 있습니다.",
            PathologyCategory.DAY_MASTER_PRESSURE,
            true
    ),

    /**
     * 한기가 극단적으로 강한 환경.
     */
    EXTREME_COLD(
            "한기 과다",
            "원국의 한난 환경이 강한 한기 방향으로 치우쳐 있습니다.",
            PathologyCategory.CLIMATE_IMBALANCE,
            false
    ),

    /**
     * 열기가 극단적으로 강한 환경.
     */
    EXTREME_HEAT(
            "열기 과다",
            "원국의 한난 환경이 강한 열기 방향으로 치우쳐 있습니다.",
            PathologyCategory.CLIMATE_IMBALANCE,
            false
    ),

    /**
     * 건조가 극단적으로 강한 환경.
     */
    EXTREME_DRY(
            "조기 과다",
            "원국의 조습 환경이 강한 건조 방향으로 치우쳐 있습니다.",
            PathologyCategory.CLIMATE_IMBALANCE,
            false
    ),

    /**
     * 습기가 극단적으로 강한 환경.
     */
    EXTREME_WET(
            "습기 과다",
            "원국의 조습 환경이 강한 습기 방향으로 치우쳐 있습니다.",
            PathologyCategory.CLIMATE_IMBALANCE,
            false
    ),

    /**
     * 寒燥 또는 熱濕처럼
     * 한난과 조습의 보정 요구가 서로 경쟁하는 구조.
     */
    CLIMATE_CORRECTION_CONFLICT(
            "조후 보정 충돌",
            "한난과 조습의 보정 방향이 서로 경쟁하여 단일 조정이 어렵습니다.",
            PathologyCategory.CLIMATE_IMBALANCE,
            false
    ),

    /**
     * 5차에서 의미 있는 파격 요소가
     * 주격 구조를 손상시키는 상태.
     */
    PRIMARY_STRUCTURE_DAMAGE(
            "주격 구조 손상",
            "주격에 의미 있는 파격 요소가 개입하여 구조적 손상이 존재합니다.",
            PathologyCategory.STRUCTURE_DAMAGE,
            false
    ),

    /**
     * 5차 최종 StructureStatus가
     * UNSTABLE인 상태.
     */
    PRIMARY_STRUCTURE_INSTABILITY(
            "주격 불안정",
            "주격 후보는 존재하지만 안정적인 격 성립으로 보기 어려운 구조입니다.",
            PathologyCategory.STRUCTURE_DAMAGE,
            false
    );

    private final String koreanName;

    private final String description;

    private final PathologyCategory category;

    /**
     * 해당 병 구조가
     * 하나 이상의 관련 오행을
     * 반드시 가져야 하는지 여부.
     */
    private final boolean relatedElementRequired;

    PathologyPatternType(
            String koreanName,
            String description,
            PathologyCategory category,
            boolean relatedElementRequired
    ) {

        this.koreanName =
                koreanName;

        this.description =
                description;

        this.category =
                category;

        this.relatedElementRequired =
                relatedElementRequired;
    }

    public String koreanName() {

        return koreanName;
    }

    public String description() {

        return description;
    }

    public PathologyCategory category() {

        return category;
    }

    public boolean relatedElementRequired() {

        return relatedElementRequired;
    }

    public boolean elementConcentrationPattern() {

        return category
                .elementConcentration();
    }

    public boolean dayMasterPressurePattern() {

        return category
                .dayMasterPressure();
    }

    public boolean climatePattern() {

        return category
                .climateImbalance();
    }

    public boolean structurePattern() {

        return category
                .structureDamage();
    }
}