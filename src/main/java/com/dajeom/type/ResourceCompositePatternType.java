package com.dajeom.type;

/**
 * 정밀화 10차-6.
 *
 * 정인·편인 복합구조 유형.
 *
 * =================================================
 *
 * 하나의 원국에서 여러 Pattern이
 * 동시에 존재할 수 있다.
 *
 * 예:
 *
 * 인성 발달
 * +
 * 정인 중심
 * +
 * 신약 인성 보조
 *
 * =================================================
 *
 * 이번 단계의 Context Pattern:
 *
 * OUTPUT_CONSTRAINT_CONTEXT
 * OFFICER_GENERATION_CONTEXT
 * WEALTH_PRESSURE_CONTEXT
 *
 * 는 각각:
 *
 * 효신탈식 / 편인도식
 * 상관패인
 * 살인상생 / 관인상생
 * 재극인
 *
 * 을 확정한 결과가 아니다.
 *
 * 후속 교차검증을 위한 선행 Evidence다.
 */
public enum ResourceCompositePatternType {

    RESOURCE_DEVELOPED(
            "인성 발달"
    ),

    DIRECT_RESOURCE_CENTERED(
            "정인 중심"
    ),

    INDIRECT_RESOURCE_CENTERED(
            "편인 중심"
    ),

    MIXED_RESOURCE(
            "정인·편인 혼재"
    ),

    WEAK_DAY_MASTER_RESOURCE_SUPPORT(
            "신약 인성 보조"
    ),

    STRONG_DAY_MASTER_EXCESSIVE_RESOURCE(
            "신강 인성 과다"
    ),

    OUTPUT_CONSTRAINT_CONTEXT(
            "식상 제약 가능 구조"
    ),

    OFFICER_GENERATION_CONTEXT(
            "관살 생인 가능 구조"
    ),

    WEALTH_PRESSURE_CONTEXT(
            "재성 압박 가능 구조"
    );

    private final String koreanName;

    ResourceCompositePatternType(
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
                == RESOURCE_DEVELOPED;
    }

    public boolean directResourceCentered() {

        return this
                == DIRECT_RESOURCE_CENTERED;
    }

    public boolean indirectResourceCentered() {

        return this
                == INDIRECT_RESOURCE_CENTERED;
    }

    public boolean mixed() {

        return this
                == MIXED_RESOURCE;
    }

    public boolean weakDayMasterSupport() {

        return this
                == WEAK_DAY_MASTER_RESOURCE_SUPPORT;
    }

    public boolean strongDayMasterExcessive() {

        return this
                == STRONG_DAY_MASTER_EXCESSIVE_RESOURCE;
    }

    public boolean outputConstraintContext() {

        return this
                == OUTPUT_CONSTRAINT_CONTEXT;
    }

    public boolean officerGenerationContext() {

        return this
                == OFFICER_GENERATION_CONTEXT;
    }

    public boolean wealthPressureContext() {

        return this
                == WEALTH_PRESSURE_CONTEXT;
    }
}