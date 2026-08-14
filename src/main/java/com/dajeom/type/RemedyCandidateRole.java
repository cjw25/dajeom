package com.dajeom.type;

/**
 * 정밀화 6차-5.
 *
 * 병(病) 구조를 완화하기 위한
 * 약(藥) 오행 후보의 역할.
 *
 * PRIMARY
 * 현재 병 구조를 직접적으로 완화하며
 * 다른 주요 구조와 큰 충돌이 없는 핵심 후보.
 *
 * SECONDARY
 * 의미 있는 완화 작용은 있으나
 * PRIMARY보다 우선도가 낮은 보조 후보.
 *
 * CONDITIONAL
 * 병을 완화하는 작용은 있으나:
 *
 * - 다른 병 구조를 악화시킬 수 있거나
 * - 신강·신약 방향과 충돌하거나
 * - 조후 방향과 충돌하거나
 * - 이미 원국에 지나치게 많은 경우
 *
 * 조건부로 검토해야 하는 후보.
 *
 * REJECTED
 * 현재 6-4에서 발견된 병 구조를
 * 직접적으로 완화할 근거가 없는 오행.
 *
 * 주의:
 *
 * PRIMARY는 최종 용신이 아니다.
 */
public enum RemedyCandidateRole {

    PRIMARY(
            "약 1순위 후보",
            4
    ),

    SECONDARY(
            "약 보조 후보",
            3
    ),

    CONDITIONAL(
            "조건부 약 후보",
            2
    ),

    REJECTED(
            "현재 약 후보 아님",
            1
    );

    private final String koreanName;

    private final int rank;

    RemedyCandidateRole(
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

    public boolean rejected() {

        return this == REJECTED;
    }

    public boolean candidate() {

        return this != REJECTED;
    }

    public boolean selected() {

        return this == PRIMARY
                || this == SECONDARY;
    }
}