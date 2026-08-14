package com.dajeom.type;

/**
 * 정밀화 7차-9.
 *
 * 하나의 五行 후보가
 * 7-8에서 선택된 주용신 오행과
 * 어떤 관계를 가지는지 나타낸다.
 *
 * 하나의 후보와 하나의 주용신 사이에
 * 둘 이상의 관계가 동시에 존재할 수 있다.
 *
 * 예:
 *
 * Fire가 주용신이고 Earth를 평가하는 경우:
 *
 * Fire -> Earth
 * 즉 주용신이 Earth를 생함
 * -> DRAINS_PRIMARY
 *
 * 동시에 Earth는
 * Fire를 극하는 Water를 제어함
 * -> CONTROLS_PRIMARY_THREAT
 *
 * 따라서 단순한 하나의 관계로
 * 축약하지 않는다.
 */
public enum UsefulGodPrimaryRelationType {

    /**
     * 평가 대상 오행 자체가 주용신.
     */
    SAME_AS_PRIMARY(
            "주용신 자체"
    ),

    /**
     * 평가 대상 오행이 주용신을 생함.
     *
     * 후보 -> 주용신
     */
    GENERATES_PRIMARY(
            "주용신을 생조"
    ),

    /**
     * 평가 대상 오행이 주용신을 직접 극함.
     */
    CONTROLS_PRIMARY(
            "주용신을 직접 극함"
    ),

    /**
     * 주용신이 평가 대상 오행을 생함.
     *
     * 주용신 -> 후보
     *
     * 주용신의 기운이 빠져나가는
     * 설기 방향이 될 수 있다.
     */
    DRAINS_PRIMARY(
            "주용신의 기운을 설기"
    ),

    /**
     * 주용신이 평가 대상 오행을 극함.
     *
     * 주용신이 해당 오행을 제어하기 위해
     * 힘을 써야 하는 관계.
     */
    CONTROLLED_BY_PRIMARY(
            "주용신의 극제 부담"
    ),

    /**
     * 평가 대상 오행이
     * 주용신을 직접 극하는 오행을 다시 제어함.
     *
     * 예:
     *
     * Fire가 주용신
     * Water가 Fire를 극함
     * Earth가 Water를 극함
     *
     * Earth
     * -> CONTROLS_PRIMARY_THREAT
     */
    CONTROLS_PRIMARY_THREAT(
            "주용신을 공격하는 오행을 제어"
    );

    private final String koreanName;

    UsefulGodPrimaryRelationType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    /**
     * 주용신을 직접 또는 간접적으로
     * 돕는 관계인가.
     */
    public boolean supportive() {

        return this == SAME_AS_PRIMARY
                || this == GENERATES_PRIMARY
                || this == CONTROLS_PRIMARY_THREAT;
    }

    /**
     * 주용신을 직접 손상시키는 관계인가.
     */
    public boolean harmful() {

        return this == CONTROLS_PRIMARY;
    }

    /**
     * 직접 극하는 것은 아니지만
     * 주용신에 추가 부담을 줄 수 있는 관계인가.
     */
    public boolean burdening() {

        return this == DRAINS_PRIMARY
                || this == CONTROLLED_BY_PRIMARY;
    }
}