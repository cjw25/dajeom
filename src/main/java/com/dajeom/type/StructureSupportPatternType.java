package com.dajeom.type;

/**
 * 정밀화 5차-5.
 *
 * 격 후보를 살려주는 대표적인
 * 성격(成格) 보조 패턴.
 *
 * 이 단계에서는
 * 실제 오행의 존재와 세력을 확인한다.
 *
 * 합충형파해 및 합화는
 * 아직 8차/9차 영역이므로
 * 여기서 선반영하지 않는다.
 */
public enum StructureSupportPatternType {

    /**
     * 재격에서 식상이 재성을 생하는 구조.
     */
    WEALTH_SUPPORTED_BY_OUTPUT(
            "식상생재",
            "식상 세력이 재성을 생하여 재격의 흐름을 이어줍니다.",
            DayMasterElementRole.OUTPUT
    ),

    /**
     * 식신·상관격에서 재성이 존재하여
     * 식상의 기운이 재성으로 이어지는 구조.
     */
    OUTPUT_CONNECTED_TO_WEALTH(
            "식상생재",
            "식상의 기운이 재성으로 이어지는 흐름을 형성합니다.",
            DayMasterElementRole.WEALTH
    ),

    /**
     * 정관격에서 재성이 관성을 생하는 구조.
     */
    OFFICER_SUPPORTED_BY_WEALTH(
            "재생관",
            "재성이 관성을 생하여 정관 구조를 보조합니다.",
            DayMasterElementRole.WEALTH
    ),

    /**
     * 정관격에서 관성과 인성이 연결되는 구조.
     */
    OFFICER_CONNECTED_TO_RESOURCE(
            "관인상생",
            "관성과 인성이 연결되어 관인상생의 흐름을 만듭니다.",
            DayMasterElementRole.RESOURCE
    ),

    /**
     * 편관/칠살격에서 식상으로
     * 관살을 제어하는 구조.
     *
     * 정확한 식신/상관 polarity 세분화는
     * 이후 십성 복합구조 분석에서
     * 더 정밀하게 검토할 수 있다.
     */
    KILLING_CONTROLLED_BY_OUTPUT(
            "식상제살",
            "식상 세력이 편관·칠살의 압력을 제어하는 방향으로 작용합니다.",
            DayMasterElementRole.OUTPUT
    ),

    /**
     * 편관/칠살격에서 인성을 통해
     * 관살의 기운을 일간 쪽으로 순환시키는 구조.
     */
    KILLING_TRANSFORMED_BY_RESOURCE(
            "인성화살",
            "인성이 편관·칠살의 기운을 받아 일간으로 연결하는 흐름을 만듭니다.",
            DayMasterElementRole.RESOURCE
    ),

    /**
     * 상관격에서 인성으로
     * 상관의 과도한 설기를 조절하는 구조.
     */
    HURTING_OFFICER_REGULATED_BY_RESOURCE(
            "상관패인",
            "인성이 상관 구조의 과도한 설기를 조절하는 방향으로 작용합니다.",
            DayMasterElementRole.RESOURCE
    ),

    /**
     * 인성격에서 관성이 인성을 생하는 구조.
     */
    RESOURCE_SUPPORTED_BY_OFFICER(
            "관인상생",
            "관성이 인성을 생하여 인성 구조의 흐름을 연결합니다.",
            DayMasterElementRole.OFFICER
    );

    private final String koreanName;

    private final String description;

    /**
     * 이 성격 패턴을 형성하기 위해
     * 원국에서 확인해야 하는 상대 십성 그룹.
     */
    private final DayMasterElementRole supportingRole;

    StructureSupportPatternType(
            String koreanName,
            String description,
            DayMasterElementRole supportingRole
    ) {

        this.koreanName =
                koreanName;

        this.description =
                description;

        this.supportingRole =
                supportingRole;
    }

    public String koreanName() {

        return koreanName;
    }

    public String description() {

        return description;
    }

    public DayMasterElementRole supportingRole() {

        return supportingRole;
    }
}