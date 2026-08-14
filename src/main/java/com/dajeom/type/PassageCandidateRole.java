package com.dajeom.type;

/**
 * 정밀화 6차-7.
 *
 * 통관(通關) 오행 후보의 역할.
 *
 * PRIMARY
 * 현재 상극 병목을 연결하기 위해
 * 가장 우선적으로 검토할 통관 후보.
 *
 * SECONDARY
 * 통관 필요성은 있으나
 * 우선도가 PRIMARY보다 낮은 보조 후보.
 *
 * CONDITIONAL
 * 통관 작용 자체는 필요하지만:
 *
 * - 병약 방향과 충돌
 * - 조후 방향과 충돌
 * - 다른 병 구조 악화 가능
 * - 이미 해당 오행이 과도하게 존재
 *
 * 등의 이유로 조건부 검토가 필요한 후보.
 *
 * NOT_NEEDED
 * 현재 원국에서 해당 오행을
 * 통관 목적으로 적극 검토할 필요가 없는 상태.
 *
 * 주의:
 *
 * PRIMARY는 최종 용신 확정이 아니다.
 */
public enum PassageCandidateRole {

    PRIMARY(
            "통관 1순위 후보",
            4
    ),

    SECONDARY(
            "통관 보조 후보",
            3
    ),

    CONDITIONAL(
            "조건부 통관 후보",
            2
    ),

    NOT_NEEDED(
            "현재 통관 후보 아님",
            1
    );

    private final String koreanName;

    private final int rank;

    PassageCandidateRole(
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

    public boolean conditional() {

        return this == CONDITIONAL;
    }

    public boolean notNeeded() {

        return this == NOT_NEEDED;
    }

    public boolean candidate() {

        return this != NOT_NEEDED;
    }

    public boolean selected() {

        return this == PRIMARY
                || this == SECONDARY;
    }
}