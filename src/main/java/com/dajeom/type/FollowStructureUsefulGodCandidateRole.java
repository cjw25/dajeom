package com.dajeom.type;

/**
 * 정밀화 7차-7.
 *
 * 종격용신법 관점의 五行 후보 역할.
 *
 * PRIMARY
 * 현재 종하는 흐름을 가장 직접적으로
 * 유지하는 핵심 후보.
 *
 * SECONDARY
 * 종하는 흐름을 이어주거나 보조하는 후보.
 *
 * CONDITIONAL
 * 종격 방향에는 의미가 있지만
 * 다른 핵심 구조와 부담 또는 충돌이 있어
 * 추가 검토가 필요한 후보.
 *
 * NOT_NEEDED
 * 현재 종격용신법에서는
 * 사용할 적극적 근거가 없는 오행.
 *
 * 주의:
 *
 * PRIMARY는 아직 최종 주용신이 아니다.
 * 7-8에서 다른 용신법과 다시 통합한다.
 */
public enum FollowStructureUsefulGodCandidateRole {

    PRIMARY(
            "종격 1순위 후보",
            4
    ),

    SECONDARY(
            "종격 보조 후보",
            3
    ),

    CONDITIONAL(
            "조건부 종격 후보",
            2
    ),

    NOT_NEEDED(
            "현재 종격 후보 아님",
            1
    );

    private final String koreanName;

    private final int rank;

    FollowStructureUsefulGodCandidateRole(
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