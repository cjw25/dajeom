package com.dajeom.type;

/**
 * 정밀화 7차-2.
 *
 * 억부용신법 관점에서의 五行 후보 역할.
 *
 * PRIMARY
 * 현재 일간 강약을 조정할 때
 * 가장 우선적으로 검토할 후보.
 *
 * SECONDARY
 * 억부 방향에는 맞지만
 * PRIMARY보다 우선도가 낮은 보조 후보.
 *
 * CONDITIONAL
 * 억부 방향에는 의미가 있으나:
 *
 * - 종격 모드
 * - 현재 오행 과다
 * - 다른 구조 검토 필요
 *
 * 등의 이유로 즉시 선택하기 어려운 후보.
 *
 * NOT_NEEDED
 * 현재 억부용신법에서는
 * 적극적으로 사용할 근거가 없는 오행.
 *
 * 주의:
 *
 * PRIMARY는 최종 용신이 아니다.
 */
public enum BalancingUsefulGodCandidateRole {

    PRIMARY(
            "억부 1순위 후보",
            4
    ),

    SECONDARY(
            "억부 보조 후보",
            3
    ),

    CONDITIONAL(
            "조건부 억부 후보",
            2
    ),

    NOT_NEEDED(
            "현재 억부 후보 아님",
            1
    );

    private final String koreanName;

    private final int rank;

    BalancingUsefulGodCandidateRole(
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