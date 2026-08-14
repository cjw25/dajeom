package com.dajeom.type;

/**
 * 정밀화 9차-4.
 *
 * 지지 육합의 기본 합화 성립 상태.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 상태는 충·형·파·해 방해를
 * 최종 적용하기 전의 잠정 상태다.
 *
 * 따라서:
 *
 * PROVISIONALLY_TRANSFORMED
 *
 * 라고 하더라도 9-7 / 9-8을 거치며
 * 조건부 또는 불화 상태로 내려갈 수 있다.
 *
 * =================================================
 *
 * PROVISIONALLY_TRANSFORMED
 *
 * 六合 관계뿐 아니라
 * 합화 목표 오행의 계절·세력 조건도
 * 충분히 형성된 상태.
 *
 * =================================================
 *
 * PROVISIONALLY_PARTIAL
 *
 * 목표 오행으로 향하는 작용은
 * 의미 있게 존재하지만
 * 완전한 化로 확정하기에는
 * 조건이 부족한 상태.
 *
 * =================================================
 *
 * CONDITIONAL
 *
 * 六合 관계는 존재하고
 * 합화 가능성도 남아 있으나
 * 추가 조건 검토가 필요한 상태.
 *
 * =================================================
 *
 * COMBINED_NOT_TRANSFORMED
 *
 * 六合 관계 자체는 존재하지만
 * 현재 기본 환경만으로는
 * 목표 오행으로 化한다고 보기 어려운 상태.
 *
 * 즉:
 *
 * 合而不化
 *
 * 기본 후보.
 */
public enum EarthlyBranchSixCombinationTransformationState {

    PROVISIONALLY_TRANSFORMED(
            "잠정 합화 성립",
            4
    ),

    PROVISIONALLY_PARTIAL(
            "잠정 부분 합화",
            3
    ),

    CONDITIONAL(
            "조건부 합화",
            2
    ),

    COMBINED_NOT_TRANSFORMED(
            "합은 있으나 불화",
            1
    );

    private final String koreanName;

    private final int rank;

    EarthlyBranchSixCombinationTransformationState(
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

    public boolean provisionallyTransformed() {

        return this == PROVISIONALLY_TRANSFORMED;
    }

    public boolean provisionallyPartial() {

        return this == PROVISIONALLY_PARTIAL;
    }

    public boolean conditional() {

        return this == CONDITIONAL;
    }

    public boolean combinedNotTransformed() {

        return this == COMBINED_NOT_TRANSFORMED;
    }

    /**
     * 방해 검토 전 기준으로
     * 실제 化 방향이 의미 있게 작동하는가.
     */
    public boolean meaningfulTransformation() {

        return provisionallyTransformed()
                || provisionallyPartial();
    }

    /**
     * 이후 9-7 / 9-8에서
     * 실제 합화 후보로 계속 검토할 필요가 있는가.
     */
    public boolean transformationCandidateRemains() {

        return !combinedNotTransformed();
    }
}