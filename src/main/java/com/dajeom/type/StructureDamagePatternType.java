package com.dajeom.type;

/**
 * 정밀화 5차-6.
 *
 * 격 후보를 손상시키거나
 * 구조적 부담을 만드는 대표 파격 패턴.
 *
 * 이 enum은
 * "최종 파격 확정"이 아니다.
 *
 * 원국 안에서 해당 패턴을
 * 검사해야 한다는 규칙 정의다.
 */
public enum StructureDamagePatternType {

    /**
     * 재격에서 비겁·겁재가
     * 재성을 다투고 침해하는 구조.
     */
    WEALTH_CONTESTED_BY_COMPANION(
            "비겁탈재",
            "비겁·겁재 세력이 재성을 다투는 방향으로 작용합니다.",
            DayMasterElementRole.COMPANION,
            null,
            false
    ),

    /**
     * 정관 후보에 상관이 존재해
     * 정관을 직접 극하는 구조.
     */
    DIRECT_OFFICER_HURT_BY_HURTING_OFFICER(
            "상관견관",
            "상관이 정관을 직접 극하여 정관 구조를 손상시킬 수 있습니다.",
            DayMasterElementRole.OUTPUT,
            StructureTenGodType.HURTING_OFFICER,
            false
    ),

    /**
     * 식신 후보에서 편인이 식신을 극하는
     * 편인도식 구조.
     */
    EATING_GOD_BLOCKED_BY_INDIRECT_RESOURCE(
            "편인도식",
            "편인이 식신을 제어하여 식신의 흐름을 막는 구조입니다.",
            DayMasterElementRole.RESOURCE,
            StructureTenGodType.INDIRECT_RESOURCE,
            false
    ),

    /**
     * 인성 후보를 재성이 극하는 구조.
     */
    RESOURCE_CONTROLLED_BY_WEALTH(
            "재극인",
            "재성이 인성을 극하여 인성 구조를 손상시킬 수 있습니다.",
            DayMasterElementRole.WEALTH,
            null,
            false
    ),

    /**
     * 칠살 구조에서 재성이 관살을 생하여
     * 살의 압력을 더 키우는 구조.
     */
    SEVEN_KILLINGS_STRENGTHENED_BY_WEALTH(
            "재생살",
            "재성이 칠살을 생하여 관살 압력을 강화하는 방향으로 작용합니다.",
            DayMasterElementRole.WEALTH,
            null,
            false
    ),

    /**
     * 칠살 후보가 존재하지만:
     *
     * - 식상제살
     * - 인성화살
     *
     * 어느 쪽도 충분하지 않은 구조.
     *
     * 특정 공격 source의 존재가 아니라
     * 제화 구조의 부족을 보는 패턴이다.
     */
    SEVEN_KILLINGS_LACKS_REGULATION(
            "제화 부족",
            "칠살을 제어하거나 순환시키는 제살·화살 구조가 부족합니다.",
            null,
            null,
            true
    ),

    /**
     * 상관 후보에 정관이 함께 존재하면서
     * 상관과 정관이 직접 대립하는 구조.
     */
    HURTING_OFFICER_MEETS_DIRECT_OFFICER(
            "상관견관",
            "상관과 정관이 동시에 작동하여 두 구조가 직접 충돌할 수 있습니다.",
            DayMasterElementRole.OFFICER,
            StructureTenGodType.DIRECT_OFFICER,
            false
    );

    private final String koreanName;

    private final String description;

    /**
     * 파격 source의 큰 십성 그룹.
     *
     * absenceBased=true인 패턴은 null.
     */
    private final DayMasterElementRole damagingRole;

    /**
     * 정확한 음양 십성이 필요한 경우.
     *
     * 예:
     *
     * 상관견관
     * -> 반드시 HURTING_OFFICER
     *
     * 편인도식
     * -> 반드시 INDIRECT_RESOURCE
     *
     * null이면 큰 십성 그룹 전체를 사용한다.
     */
    private final StructureTenGodType requiredExactTenGod;

    /**
     * 특정 공격 source가 아니라
     * 필요한 제화 구조가 없음을 판단하는 패턴인지.
     */
    private final boolean absenceBased;

    StructureDamagePatternType(
            String koreanName,
            String description,
            DayMasterElementRole damagingRole,
            StructureTenGodType requiredExactTenGod,
            boolean absenceBased
    ) {

        this.koreanName =
                koreanName;

        this.description =
                description;

        this.damagingRole =
                damagingRole;

        this.requiredExactTenGod =
                requiredExactTenGod;

        this.absenceBased =
                absenceBased;
    }

    public String koreanName() {

        return koreanName;
    }

    public String description() {

        return description;
    }

    public DayMasterElementRole damagingRole() {

        return damagingRole;
    }

    public StructureTenGodType requiredExactTenGod() {

        return requiredExactTenGod;
    }

    public boolean requiresExactTenGod() {

        return requiredExactTenGod != null;
    }

    public boolean absenceBased() {

        return absenceBased;
    }

    public boolean sourceBased() {

        return !absenceBased;
    }
}