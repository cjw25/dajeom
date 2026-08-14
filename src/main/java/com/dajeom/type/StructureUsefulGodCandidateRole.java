package com.dajeom.type;

/**
 * 정밀화 7차-4.
 *
 * 격국용신법 관점에서의 五行 후보 역할.
 *
 * PRIMARY
 * 현재 주격을 유지하거나 손상된 구조를
 * 회복하기 위해 가장 우선적으로 검토할 후보.
 *
 * SECONDARY
 * 격국에 도움이 되지만
 * PRIMARY보다 우선도가 낮은 보조 후보.
 *
 * CONDITIONAL
 * 격국상 의미는 있으나:
 *
 * - 일간 강약 부담
 * - 성격/파격 양면성
 * - 특수격
 * - 종격
 * - 주격 불안정
 *
 * 등의 이유로 추가 검토가 필요한 후보.
 *
 * NOT_NEEDED
 * 현재 격국용신법 관점에서
 * 적극적으로 사용할 근거가 없는 오행.
 *
 * 주의:
 *
 * PRIMARY는 최종 용신이 아니다.
 */
public enum StructureUsefulGodCandidateRole {

    PRIMARY(
            "격국 1순위 후보",
            4
    ),

    SECONDARY(
            "격국 보조 후보",
            3
    ),

    CONDITIONAL(
            "조건부 격국 후보",
            2
    ),

    NOT_NEEDED(
            "현재 격국 후보 아님",
            1
    );

    private final String koreanName;

    private final int rank;

    StructureUsefulGodCandidateRole(
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