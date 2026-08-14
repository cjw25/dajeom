package com.dajeom.type;

/**
 * 정밀화 9차-2.
 *
 * 천간합 후보의 경쟁·방해 상태.
 *
 * =================================================
 *
 * UNCONTESTED
 *
 * 동일 천간을 공유하는 다른 합 후보가 없고,
 * 외부 천간 충·극의 직접 방해도 없는 상태.
 *
 * =================================================
 *
 * COMPETING
 *
 * 동일 천간을 공유하는 다른 합 후보가 존재하지만,
 * 현재 후보보다 기본 적용도가 낮은 경쟁 후보만 존재.
 *
 * 즉 경쟁 구조는 있지만
 * 현재 후보의 상대적 우세가 기본 단계에서 확인되는 상태.
 *
 * =================================================
 *
 * CONTESTED
 *
 * 동일 천간을 공유하는 경쟁 후보 중
 * 현재 후보와 동급 또는 더 높은
 * 기본 적용도의 후보가 존재.
 *
 * 어느 합을 실제로 채택할지는
 * 9-3에서 판단한다.
 *
 * =================================================
 *
 * OBSTRUCTED
 *
 * 합 Pair 외부의 제3 천간으로부터
 * 충 또는 직접 피극 구조가 존재.
 *
 * 경쟁 후보가 동시에 존재할 수도 있다.
 *
 * 이 enum은 대표 상태일 뿐이며,
 * CompetitionEvidence에는
 * 경쟁·방해 Evidence를 모두 별도로 보존한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 어떤 상태도 실제 合化 성공/실패를
 * 확정하지 않는다.
 */
public enum HeavenlyStemCombinationCompetitionState {

    UNCONTESTED(
            "비경쟁",
            1
    ),

    COMPETING(
            "경쟁",
            2
    ),

    CONTESTED(
            "경합",
            3
    ),

    OBSTRUCTED(
            "외부 방해",
            4
    );

    private final String koreanName;

    private final int rank;

    HeavenlyStemCombinationCompetitionState(
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

    public boolean uncontested() {

        return this == UNCONTESTED;
    }

    public boolean competing() {

        return this == COMPETING;
    }

    public boolean contested() {

        return this == CONTESTED;
    }

    public boolean obstructed() {

        return this == OBSTRUCTED;
    }

    public boolean hasCompetition() {

        return competing()
                || contested();
    }

    public boolean requiresCompetitionReview() {

        return contested()
                || obstructed();
    }
}