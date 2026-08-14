package com.dajeom.type;

/**
 * 정밀화 7차-1.
 *
 * 특정 용신법이 현재 명식에서
 * 어느 정도 중요하게 적용되는지를 나타낸다.
 *
 * CORE
 * 현재 명식에서 핵심적으로 검토해야 하는 용신법.
 *
 * ACTIVE
 * 분명한 적용 근거가 존재하는 용신법.
 *
 * CONDITIONAL
 * 적용 가능성은 있으나
 * 다른 구조와의 충돌 또는 낮은 필요도 때문에
 * 보조적으로 검토해야 하는 방법.
 *
 * INACTIVE
 * 현재 명식에서는 해당 방법을
 * 적극적으로 사용할 근거가 없는 상태.
 *
 * 주의:
 *
 * CORE라고 해서 그 방법이 뽑은 오행이
 * 자동으로 최종 용신이 되는 것은 아니다.
 */
public enum UsefulGodMethodApplicability {

    CORE(
            "핵심 적용",
            4
    ),

    ACTIVE(
            "적용",
            3
    ),

    CONDITIONAL(
            "조건부 적용",
            2
    ),

    INACTIVE(
            "비활성",
            1
    );

    private final String koreanName;

    private final int rank;

    UsefulGodMethodApplicability(
            String koreanName,
            int rank
    ) {

        this.koreanName =
                koreanName;

        this.rank =
                rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean core() {

        return this == CORE;
    }

    public boolean active() {

        return this == ACTIVE;
    }

    public boolean conditional() {

        return this == CONDITIONAL;
    }

    public boolean inactive() {

        return this == INACTIVE;
    }

    /**
     * 실제 후보 생성 단계에서
     * 고려할 수 있는 방법인가.
     *
     * CONDITIONAL도 후속 검토 대상으로 남긴다.
     */
    public boolean applicable() {

        return this != INACTIVE;
    }

    /**
     * 조건부가 아닌
     * 명확한 적용 근거가 있는가.
     */
    public boolean meaningful() {

        return this == CORE
                || this == ACTIVE;
    }
}