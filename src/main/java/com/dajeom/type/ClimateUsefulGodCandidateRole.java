package com.dajeom.type;

/**
 * 정밀화 7차-3.
 *
 * 조후용신법 관점에서의 五行 후보 역할.
 *
 * PRIMARY
 * 현재 한난조습을 조정하기 위해
 * 가장 우선적으로 검토할 후보.
 *
 * SECONDARY
 * 조후 방향에는 의미가 있지만
 * PRIMARY보다 우선도가 낮은 보조 후보.
 *
 * CONDITIONAL
 * 조후 작용 자체는 필요하지만:
 *
 * - 한난/조습 요구 충돌
 * - 다른 조후축 악화 가능
 * - 현재 해당 오행 과다
 * - 신강신약과 충돌
 * - 격국과 충돌
 *
 * 등의 이유로 조건부 검토가 필요한 후보.
 *
 * NOT_NEEDED
 * 현재 조후용신법에서는
 * 적극적으로 사용할 근거가 없는 오행.
 *
 * 주의:
 *
 * PRIMARY는 최종 용신이 아니다.
 */
public enum ClimateUsefulGodCandidateRole {

    PRIMARY(
            "조후 1순위 후보",
            4
    ),

    SECONDARY(
            "조후 보조 후보",
            3
    ),

    CONDITIONAL(
            "조건부 조후 후보",
            2
    ),

    NOT_NEEDED(
            "현재 조후 후보 아님",
            1
    );

    private final String koreanName;

    private final int rank;

    ClimateUsefulGodCandidateRole(
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