package com.dajeom.type;

/**
 * 정밀화 6차-5.
 *
 * 특정 오행이 병(病) 구조를
 * 어떤 방식으로 완화하는지 나타낸다.
 *
 * 아직:
 *
 * - 용신
 * - 희신
 * - 기신
 *
 * 을 결정하지 않는다.
 *
 * 오직 병과 약 사이의 작용 근거를
 * Explainability Evidence로 보존한다.
 */
public enum RemedyActionType {

    /**
     * 과도하게 집중된 오행이
     * 다음 오행을 생하도록 하여
     * 세력을 배출하는 방향.
     */
    DRAIN_EXCESS_ELEMENT(
            "과다 오행 설기"
    ),

    /**
     * 과도한 오행을
     * 극제하는 방향.
     */
    CONTROL_EXCESS_ELEMENT(
            "과다 오행 제어"
    ),

    /**
     * 신약한 일간 자체를
     * 비겁·인성 방향으로 보강.
     */
    STRENGTHEN_DAY_MASTER(
            "일간 보강"
    ),

    /**
     * 과도한 식상 설기를
     * 인성 등을 통해 제어.
     */
    CONTROL_OUTPUT_DRAIN(
            "식상 설기 조절"
    ),

    /**
     * 신약한 일간이 재성을
     * 감당할 수 있도록 보조.
     */
    SUPPORT_WEALTH_BEARING_CAPACITY(
            "재성 감당력 보조"
    ),

    /**
     * 관살 압력을 인성 방향으로
     * 순환·전환.
     */
    TRANSFORM_OFFICER_PRESSURE(
            "관살 압력 통화"
    ),

    /**
     * 식상 등을 이용해
     * 관살을 직접 제어.
     */
    CONTROL_OFFICER_PRESSURE(
            "관살 압력 제어"
    ),

    /**
     * 신강한 일간의
     * 과도한 비겁·인성 생조를
     * 설기·소모·극제 방향으로 해소.
     */
    RELEASE_EXCESS_SUPPORT(
            "생조 과다 해소"
    ),

    /**
     * 극단 한기 완화.
     */
    WARM_CLIMATE(
            "한기 완화"
    ),

    /**
     * 극단 열기 완화.
     */
    COOL_CLIMATE(
            "열기 완화"
    ),

    /**
     * 극단 건조 완화.
     */
    MOISTEN_CLIMATE(
            "건조 완화"
    ),

    /**
     * 극단 습기 완화.
     */
    DRY_CLIMATE(
            "습기 완화"
    ),

    /**
     * 寒燥·熱濕 등의
     * 조후 요구 충돌을 중재하는 후보.
     */
    BRIDGE_CLIMATE_CONFLICT(
            "조후 충돌 중재"
    ),

    /**
     * 5차에서 발견된 파격 구조를
     * 보완하는 방향.
     */
    REPAIR_STRUCTURE_DAMAGE(
            "격국 손상 보완"
    );

    private final String koreanName;

    RemedyActionType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean climateAction() {

        return this == WARM_CLIMATE
                || this == COOL_CLIMATE
                || this == MOISTEN_CLIMATE
                || this == DRY_CLIMATE
                || this == BRIDGE_CLIMATE_CONFLICT;
    }

    public boolean dayMasterAction() {

        return this == STRENGTHEN_DAY_MASTER
                || this == CONTROL_OUTPUT_DRAIN
                || this == SUPPORT_WEALTH_BEARING_CAPACITY
                || this == TRANSFORM_OFFICER_PRESSURE
                || this == CONTROL_OFFICER_PRESSURE
                || this == RELEASE_EXCESS_SUPPORT;
    }

    public boolean structureAction() {

        return this == REPAIR_STRUCTURE_DAMAGE;
    }
}