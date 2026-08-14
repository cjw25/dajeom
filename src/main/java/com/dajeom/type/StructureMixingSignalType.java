package com.dajeom.type;

/**
 * 정밀화 5차-7.
 *
 * 격국 순도를 낮출 수 있는
 * 구조적인 혼재 신호.
 *
 * 주의:
 *
 * 이것들은 파격 자체와는 다르다.
 *
 * 예:
 *
 * 정재격과 정관격 후보가
 * 둘 다 강하게 성립하는 경우는
 * 반드시 파격이라고 할 수는 없지만
 * 격의 단일성은 떨어질 수 있다.
 */
public enum StructureMixingSignalType {

    /**
     * 의미 있는 일반 내격 후보가
     * 둘 이상 동시에 존재.
     */
    MULTIPLE_MEANINGFUL_MONTH_CANDIDATES(
            "복수 격 후보 성립",
            "월령에서 의미 있는 일반 내격 후보가 둘 이상 동시에 성립합니다."
    ),

    /**
     * 둘 이상의 월령 지장간 후보가
     * 실제 천간으로 동시에 투출.
     */
    MULTIPLE_EXPOSED_MONTH_CANDIDATES(
            "월령 후보 복수 투출",
            "월령의 둘 이상의 지장간 후보가 천간에 동시에 표현됩니다."
    ),

    /**
     * 정기는 숨겨져 있으나
     * 중기·여기가 천간에 투출한 구조.
     */
    SECONDARY_EXPRESSION_OVER_HIDDEN_MAIN(
            "부기 투출 우세",
            "월령 정기는 숨겨져 있지만 중기 또는 여기가 천간에서 더 직접적으로 표현됩니다."
    ),

    /**
     * 정관 + 편관/칠살이 함께 표현되는
     * 관살혼잡.
     */
    OFFICER_KILLING_MIXING(
            "관살혼잡",
            "정관과 편관·칠살이 함께 표현되어 관살 구조가 혼재합니다."
    ),

    /**
     * 정재+편재,
     * 식신+상관,
     * 정인+편인 등
     * 동일 십성 그룹의 정·편이
     * 함께 표현되는 경우.
     */
    SAME_FAMILY_POLARITY_MIXING(
            "정편 혼재",
            "같은 십성 그룹 안에서 정·편 십성이 함께 표현됩니다."
    ),

    /**
     * 같은 격 후보에
     * 성격 요소와 파격 요소가
     * 동시에 실질적으로 존재.
     */
    SUPPORT_AND_DAMAGE_COEXIST(
            "성격·파격 공존",
            "격을 살리는 요소와 깨는 요소가 동시에 실제로 작동합니다."
    ),

    /**
     * 5-6에서 강한 파격 Evidence가
     * 확인된 경우.
     */
    STRONG_DAMAGE_PRESENT(
            "강한 파격 개입",
            "격 후보에 강한 파격 Evidence가 존재합니다."
    );

    private final String koreanName;

    private final String description;

    StructureMixingSignalType(
            String koreanName,
            String description
    ) {

        this.koreanName =
                koreanName;

        this.description =
                description;
    }

    public String koreanName() {

        return koreanName;
    }

    public String description() {

        return description;
    }
}