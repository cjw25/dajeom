package com.dajeom.type;

/**
 * 정밀화 5차-9.
 *
 * 최종 격국 확정 직전,
 * 각 후보에게 부여하는 역할.
 *
 * PRIMARY
 * 현재 원국에서 가장 중심적인 주격 후보.
 *
 * SECONDARY
 * 주격과 함께 의미 있게 작동하는 보조격 후보.
 *
 * DEFERRED
 * 후보 근거는 있으나
 * 현재 단계에서 확정적으로 사용하기 어려운 상태.
 *
 * REJECTED
 * 월령 내부에는 존재하지만
 * 현재 원국의 실제 격 후보로 사용하지 않는 상태.
 *
 * 주의:
 *
 * PRIMARY라고 하더라도
 * 5-10 StructureRule 통합 이전에는
 * 최종 API 격국 확정값이 아니다.
 */
public enum StructureCandidateRole {

    PRIMARY(
            "주격 후보",
            4
    ),

    SECONDARY(
            "보조격 후보",
            3
    ),

    DEFERRED(
            "판정 보류",
            2
    ),

    REJECTED(
            "후보 제외",
            1
    );

    private final String koreanName;

    private final int rank;

    StructureCandidateRole(
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

        return primary()
                || secondary();
    }
}