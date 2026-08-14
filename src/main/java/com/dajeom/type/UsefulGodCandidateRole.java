package com.dajeom.type;

/**
 * 정밀화 7차-8.
 *
 * 6개 용신법을 통합한 뒤의
 * 공통 용신 후보 역할.
 *
 * =================================================
 *
 * PRIMARY_USEFUL_GOD
 *
 * 7-8 기준 주용신 후보.
 *
 * 복수 후보가 동시에 존재할 수 있다.
 *
 * =================================================
 *
 * SUPPORTING_USEFUL_GOD
 *
 * 주용신은 아니지만
 * 여러 용신법에서 의미 있게 지지되는 보조 후보.
 *
 * 아직 7-9의 희신과는 다르다.
 *
 * =================================================
 *
 * DEFERRED
 *
 * 필요 근거는 있으나:
 *
 * - CORE 방법 충돌
 * - 조건부 Evidence
 * - 구조적 부담
 * - 오행 과다
 *
 * 등으로 주용신 여부를 확정하기 어려운 상태.
 *
 * =================================================
 *
 * NOT_SELECTED
 *
 * 현재 7-8 용신 통합에서 선택되지 않은 오행.
 *
 * 이것이 기신이라는 의미는 아니다.
 */
public enum UsefulGodCandidateRole {

    PRIMARY_USEFUL_GOD(
            "주용신 후보",
            4
    ),

    SUPPORTING_USEFUL_GOD(
            "보조 용신 후보",
            3
    ),

    DEFERRED(
            "용신 판단 보류",
            2
    ),

    NOT_SELECTED(
            "현재 용신 미선택",
            1
    );

    private final String koreanName;

    private final int rank;

    UsefulGodCandidateRole(
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

    public boolean primaryUsefulGod() {

        return this == PRIMARY_USEFUL_GOD;
    }

    public boolean supportingUsefulGod() {

        return this == SUPPORTING_USEFUL_GOD;
    }

    public boolean deferred() {

        return this == DEFERRED;
    }

    public boolean notSelected() {

        return this == NOT_SELECTED;
    }

    public boolean selected() {

        return this == PRIMARY_USEFUL_GOD
                || this == SUPPORTING_USEFUL_GOD;
    }

    public boolean candidate() {

        return this != NOT_SELECTED;
    }
}