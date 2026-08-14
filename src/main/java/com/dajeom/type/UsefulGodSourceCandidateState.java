package com.dajeom.type;

/**
 * 정밀화 7차-8.
 *
 * 서로 다른 용신법의 후보 역할을
 * 하나의 공통 상태로 정규화한다.
 *
 * 7-2 ~ 7-7은 각자 독립적인 CandidateRole을 사용한다.
 *
 * 7-8에서는 그 결과를:
 *
 * PRIMARY
 * SECONDARY
 * CONDITIONAL
 * NOT_SELECTED
 *
 * 로 정규화하여 비교한다.
 *
 * 이 값 자체가 최종 용신 역할은 아니다.
 */
public enum UsefulGodSourceCandidateState {

    PRIMARY(
            "방법별 1순위",
            4
    ),

    SECONDARY(
            "방법별 보조",
            3
    ),

    CONDITIONAL(
            "방법별 조건부",
            2
    ),

    NOT_SELECTED(
            "방법별 미선택",
            1
    );

    private final String koreanName;

    private final int rank;

    UsefulGodSourceCandidateState(
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

    public boolean notSelected() {

        return this == NOT_SELECTED;
    }

    public boolean selected() {

        return this == PRIMARY
                || this == SECONDARY;
    }

    public boolean candidate() {

        return this != NOT_SELECTED;
    }
}