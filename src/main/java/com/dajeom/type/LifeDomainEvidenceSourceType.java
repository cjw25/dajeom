package com.dajeom.type;

/**
 * 정밀화 18차-1.
 *
 * 생활영역 분석에 사용할 수 있는
 * 기존 deterministic 분석 Source.
 *
 * =================================================
 *
 * 18차는 기존 계산을 다시 하지 않는다.
 *
 * 기존 분석 결과를 Source로 연결한다.
 *
 * =================================================
 *
 * ANNUAL_CONTEXT
 *
 * 기존 14-6 AnnualLuckContextAnalysis.
 *
 * =================================================
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * 공식 Annual Functional Chain의
 * 최종 기준선을 현재 16차 Timeline Segment에
 * 연결한 typed temporal context.
 *
 * 기존 ANNUAL_CONTEXT를 대체하지 않는다.
 *
 * =================================================
 *
 * MONTHLY_CONTEXT
 *
 * 기존 15-7 MonthlyLuckContextAnalysis.
 *
 * =================================================
 *
 * MONTHLY_FUNCTIONAL_CONTEXT
 *
 * 공식 Annual Functional Baseline 위에서
 * 현재 실제 MonthlyLuckAnalysis가
 * 어떤 Function에 접촉하는지를 계산한
 * Monthly Functional Activation의
 * typed temporal context.
 *
 * 기존 MONTHLY_CONTEXT를 대체하지 않는다.
 *
 * 또한 MONTHLY_CONTEXT와
 * 서로 독립된 명리근거 2개로
 * 단순 합산해서는 안 된다.
 */
public enum LifeDomainEvidenceSourceType {

    ELEMENT_STRENGTH(
            "오행 세력",
            LifeDomainEvidenceTier.CORE_STRUCTURE
    ),

    DAY_MASTER_STRENGTH(
            "신강·신약",
            LifeDomainEvidenceTier.CORE_STRUCTURE
    ),

    STRUCTURE(
            "격국",
            LifeDomainEvidenceTier.CORE_STRUCTURE
    ),

    CLIMATE_DISEASE_FLOW(
            "조후·병약·통관",
            LifeDomainEvidenceTier.CORE_STRUCTURE
    ),

    USEFUL_GOD(
            "용신",
            LifeDomainEvidenceTier.CORE_STRUCTURE
    ),

    TEN_GOD_COMPOSITE(
            "십성 복합구조",
            LifeDomainEvidenceTier.CORE_STRUCTURE
    ),

    TWELVE_LIFE_STAGE(
            "12운성",
            LifeDomainEvidenceTier.STRUCTURAL_CONTEXT
    ),

    PALACE(
            "궁위",
            LifeDomainEvidenceTier.STRUCTURAL_CONTEXT
    ),

    NATAL_RELATION(
            "원국 천간·지지 관계",
            LifeDomainEvidenceTier.STRUCTURAL_CONTEXT
    ),

    TRANSFORMATION(
            "합화·변환",
            LifeDomainEvidenceTier.STRUCTURAL_CONTEXT
    ),

    DECADE_CONTEXT(
            "대운 Context",
            LifeDomainEvidenceTier.TEMPORAL_CONTEXT
    ),

    ANNUAL_CONTEXT(
            "세운 Context",
            LifeDomainEvidenceTier.TEMPORAL_CONTEXT
    ),

    /**
     * 기존 ANNUAL_CONTEXT와 별개다.
     *
     * Annual Functional 계산의:
     *
     * - Function Activation
     * - Transformation
     * - Natal Fixed Capacity
     * - Decade × Annual Segment
     * - CrossFunctional Pattern
     * - 14-6 Context Synthesis
     *
     * 를 보존하는 typed temporal Evidence.
     */
    ANNUAL_FUNCTIONAL_CONTEXT(
            "세운 기능 Context",
            LifeDomainEvidenceTier.TEMPORAL_CONTEXT
    ),

    MONTHLY_CONTEXT(
            "월운 Context",
            LifeDomainEvidenceTier.TEMPORAL_CONTEXT
    ),

    /**
     * 기존 MONTHLY_CONTEXT와 별개다.
     *
     * Monthly Functional Activation 계산의:
     *
     * - Annual Functional Baseline
     * - Monthly Direct TenGod Contact
     * - Monthly Structured Context
     * - 현재 Annual-Decade Segment
     * - Monthly Decade Context
     * - Function별 Activation Status
     *
     * 를 현재 Timeline Segment에 연결하는
     * derived temporal Evidence.
     *
     * 기존 MONTHLY_CONTEXT에서 파생되는
     * 구조가 포함되므로 둘을 독립 근거 개수로
     * 단순 합산해서는 안 된다.
     */
    MONTHLY_FUNCTIONAL_CONTEXT(
            "월운 기능 Context",
            LifeDomainEvidenceTier.TEMPORAL_CONTEXT
    ),

    SHINSAL(
            "신살",
            LifeDomainEvidenceTier.AUXILIARY
    );

    private final String koreanName;

    private final LifeDomainEvidenceTier tier;

    LifeDomainEvidenceSourceType(

            String koreanName,

            LifeDomainEvidenceTier tier

    ) {

        this.koreanName =
                koreanName;

        this.tier =
                tier;
    }

    public String koreanName() {

        return koreanName;
    }

    public LifeDomainEvidenceTier tier() {

        return tier;
    }

    public boolean auxiliaryOnly() {

        return tier.auxiliary();
    }

    /**
     * 시간축 Source인가.
     */
    public boolean temporalContext() {

        return tier
                == LifeDomainEvidenceTier
                .TEMPORAL_CONTEXT;
    }
}