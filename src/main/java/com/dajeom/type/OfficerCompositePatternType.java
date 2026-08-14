package com.dajeom.type;

/**
 * 정밀화 10차-5.
 *
 * 정관·칠살 복합구조 유형.
 *
 * 하나의 명식에 여러 Pattern이
 * 동시에 존재할 수 있다.
 *
 * 주의:
 *
 * OUTPUT_CONTROL_CONTEXT
 * RESOURCE_MEDIATION_CONTEXT
 * WEALTH_SUPPORT_CONTEXT
 *
 * 는 각각:
 *
 * 식신제살
 * 상관견관
 * 살인상생
 * 관인상생
 * 재생관
 *
 * 등을 확정한 결과가 아니다.
 *
 * 해당 상호작용이 검토될 수 있는
 * 선행 Context만 표현한다.
 */
public enum OfficerCompositePatternType {

    OFFICER_DEVELOPED(
            "관살 발달"
    ),

    DIRECT_OFFICER_CENTERED(
            "정관 중심"
    ),

    SEVEN_KILLINGS_CENTERED(
            "칠살 중심"
    ),

    MIXED_OFFICER_KILLINGS(
            "정관·칠살 혼재"
    ),

    WEAK_DAY_MASTER_OFFICER_PRESSURE(
            "신약 관살 압박"
    ),

    OUTPUT_CONTROL_CONTEXT(
            "식상 제어 가능 구조"
    ),

    RESOURCE_MEDIATION_CONTEXT(
            "인성 연결 가능 구조"
    ),

    WEALTH_SUPPORT_CONTEXT(
            "재성 생조 가능 구조"
    );

    private final String koreanName;

    OfficerCompositePatternType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean developed() {

        return this
                == OFFICER_DEVELOPED;
    }

    public boolean directOfficerCentered() {

        return this
                == DIRECT_OFFICER_CENTERED;
    }

    public boolean sevenKillingsCentered() {

        return this
                == SEVEN_KILLINGS_CENTERED;
    }

    public boolean mixed() {

        return this
                == MIXED_OFFICER_KILLINGS;
    }

    public boolean weakDayMasterPressure() {

        return this
                == WEAK_DAY_MASTER_OFFICER_PRESSURE;
    }

    public boolean outputControlContext() {

        return this
                == OUTPUT_CONTROL_CONTEXT;
    }

    public boolean resourceMediationContext() {

        return this
                == RESOURCE_MEDIATION_CONTEXT;
    }

    public boolean wealthSupportContext() {

        return this
                == WEALTH_SUPPORT_CONTEXT;
    }
}