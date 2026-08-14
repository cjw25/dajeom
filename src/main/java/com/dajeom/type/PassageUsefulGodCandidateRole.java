package com.dajeom.type;

/**
 * 정밀화 7차-6.
 *
 * 통관용신법 관점에서의 五行 후보 역할.
 *
 * PRIMARY
 * 현재 강한 상극 병목을 연결하기 위해
 * 가장 우선적으로 검토할 후보.
 *
 * SECONDARY
 * 통관 방향에는 의미가 있으나
 * PRIMARY보다 우선도가 낮은 보조 후보.
 *
 * CONDITIONAL
 * 통관 작용 자체는 필요하지만:
 *
 * - 병약과 충돌
 * - 조후와 충돌
 * - 신강신약과 충돌
 * - 격국과 충돌
 * - 현재 해당 오행 과다
 * - 통관용신법 자체가 조건부
 *
 * 등의 이유로 즉시 선택하기 어려운 후보.
 *
 * NOT_NEEDED
 * 현재 통관용신법 관점에서는
 * 적극적으로 사용할 근거가 없는 오행.
 *
 * 주의:
 *
 * PRIMARY는 최종 용신이 아니다.
 */
public enum PassageUsefulGodCandidateRole {

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

    PassageUsefulGodCandidateRole(
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