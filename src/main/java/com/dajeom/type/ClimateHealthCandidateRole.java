package com.dajeom.type;

/**
 * 정밀화 6차-8.
 *
 * 조후·병약·통관 통합 이후
 * 각 오행 후보가 가지는 역할.
 *
 * PRIMARY
 * 여러 분석이 동일 방향으로 수렴하거나,
 * 단일 분석이라도 매우 강한 필요성이 있으며
 * 다른 분석과 충돌하지 않는 후보.
 *
 * SECONDARY
 * 의미 있는 후보이지만
 * 아직 독립적인 최우선 근거까지는 아닌 상태.
 *
 * DEFERRED
 * 필요한 작용은 존재하지만
 * 분석 간 충돌 또는 조건부 Evidence 때문에
 * 최종 선택을 미루는 후보.
 *
 * REJECTED
 * 현재 6차 통합 관점에서
 * 적극적으로 사용할 근거가 없는 후보.
 *
 * 주의:
 *
 * PRIMARY 역시 아직 용신 확정이 아니다.
 * 6-9 교차검증을 통과해야 한다.
 */
public enum ClimateHealthCandidateRole {

    PRIMARY(
            "통합 1순위 후보",
            4
    ),

    SECONDARY(
            "통합 보조 후보",
            3
    ),

    DEFERRED(
            "통합 판정 보류",
            2
    ),

    REJECTED(
            "통합 후보 제외",
            1
    );

    private final String koreanName;

    private final int rank;

    ClimateHealthCandidateRole(
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

    public boolean primary() {

        return this == PRIMARY;
    }

    public boolean secondary() {

        return this == SECONDARY;
    }

    public boolean deferred() {

        return this == DEFERRED;
    }

    public boolean rejected() {

        return this == REJECTED;
    }

    public boolean selected() {

        return this == PRIMARY
                || this == SECONDARY;
    }

    public boolean candidate() {

        return this != REJECTED;
    }
}