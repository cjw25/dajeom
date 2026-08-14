package com.dajeom.type;

/**
 * 정밀화 6차-3.
 *
 * 조후 관점에서 오행 후보가 가지는 역할.
 *
 * PRIMARY
 * 현재 조후 불균형을 교정할 때
 * 가장 먼저 검토해야 하는 직접 후보.
 *
 * SECONDARY
 * 직접 후보를 보조하거나
 * 우선도는 낮지만 의미 있는 후보.
 *
 * CONDITIONAL
 * 필요한 작용은 있으나
 * 다른 조후 축을 악화시키거나
 * 이미 원국에 과도하게 존재하여
 * 조건부 검토가 필요한 후보.
 *
 * NOT_NEEDED
 * 현재 조후 단계에서
 * 적극적으로 사용할 근거가 없는 오행.
 *
 * 주의:
 *
 * PRIMARY는 용신 확정이 아니다.
 */
public enum ClimateElementCandidateRole {

    PRIMARY(
            "조후 1순위 후보",
            4
    ),

    SECONDARY(
            "조후 보조 후보",
            3
    ),

    CONDITIONAL(
            "조건부 후보",
            2
    ),

    NOT_NEEDED(
            "현재 조후 후보 아님",
            1
    );

    private final String koreanName;

    private final int rank;

    ClimateElementCandidateRole(
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