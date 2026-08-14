package com.dajeom.type;

/**
 * 정밀화 7차-5.
 *
 * 병약용신법 관점에서의 五行 후보 역할.
 *
 * PRIMARY
 * 현재 핵심 병 구조를 직접 완화하기 위해
 * 가장 우선적으로 검토할 후보.
 *
 * SECONDARY
 * 병 구조 완화에 의미가 있지만
 * PRIMARY보다 우선도가 낮은 보조 후보.
 *
 * CONDITIONAL
 * 병약 작용 자체는 존재하지만:
 *
 * - 다른 병 구조 악화 가능
 * - 현재 오행 과다
 * - 신강신약과 충돌
 * - 격국과 충돌
 * - 병약용신법 자체가 조건부
 *
 * 등의 이유로 바로 선택하기 어려운 후보.
 *
 * NOT_NEEDED
 * 현재 병약용신법에서는
 * 적극적으로 사용할 근거가 없는 오행.
 *
 * 주의:
 *
 * PRIMARY는 최종 용신이 아니다.
 */
public enum RemedyUsefulGodCandidateRole {

    PRIMARY(
            "병약 1순위 후보",
            4
    ),

    SECONDARY(
            "병약 보조 후보",
            3
    ),

    CONDITIONAL(
            "조건부 병약 후보",
            2
    ),

    NOT_NEEDED(
            "현재 병약 후보 아님",
            1
    );

    private final String koreanName;

    private final int rank;

    RemedyUsefulGodCandidateRole(
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