package com.dajeom.type;

/**
 * 정밀화 6차-8.
 *
 * 조후·병약·통관 세 분석이
 * 동일 오행에 대해 어떤 관계를 가지는지 나타낸다.
 *
 * CONVERGENT
 * 서로 다른 두 개 이상의 분석이
 * 동일 오행을 의미 있는 후보로 선택한 상태.
 *
 * SUPPORTIVE
 * 하나의 분석이 해당 오행을 분명하게 요구하고,
 * 다른 분석에서는 직접적인 반대 근거가 없는 상태.
 *
 * CONDITIONAL
 * 해당 오행의 작용 필요성은 존재하지만
 * 과다·조건부·경쟁 구조 등으로
 * 추가 검토가 필요한 상태.
 *
 * CONFLICTED
 * 한 분석에서는 필요하지만
 * 다른 분석에서는 실제 악화 또는 충돌 Evidence가
 * 존재하는 상태.
 *
 * INACTIVE
 * 조후·병약·통관 어디에서도
 * 현재 의미 있는 후보로 사용되지 않는 상태.
 *
 * 주의:
 *
 * 이 상태는 아직 최종 용신 판정이 아니다.
 */
public enum ClimateHealthIntegrationState {

    CONVERGENT(
            "다중 근거 수렴",
            5
    ),

    SUPPORTIVE(
            "단일·보조 근거",
            4
    ),

    CONDITIONAL(
            "조건부",
            3
    ),

    CONFLICTED(
            "분석 간 충돌",
            2
    ),

    INACTIVE(
            "비활성",
            1
    );

    private final String koreanName;

    private final int rank;

    ClimateHealthIntegrationState(
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

    public boolean convergent() {

        return this == CONVERGENT;
    }

    public boolean supportive() {

        return this == SUPPORTIVE;
    }

    public boolean conditional() {

        return this == CONDITIONAL;
    }

    public boolean conflicted() {

        return this == CONFLICTED;
    }

    public boolean inactive() {

        return this == INACTIVE;
    }

    public boolean active() {

        return this != INACTIVE;
    }

    public boolean clean() {

        return this == CONVERGENT
                || this == SUPPORTIVE;
    }

    public boolean requiresReview() {

        return this == CONDITIONAL
                || this == CONFLICTED;
    }
}