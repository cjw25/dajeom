package com.dajeom.type;

/**
 * 정밀화 9차-5.
 *
 * 지지 삼합계 관계의 방해 검토 전
 * 국(局) 성립 상태.
 *
 * =================================================
 *
 * PROVISIONALLY_ESTABLISHED
 *
 * 완전 삼합 또는 매우 강한 삼합 구조가
 * 목표 오행 방향으로 충분한 성립 조건을
 * 갖춘 잠정 국 성립 상태.
 *
 * 아직 충·형·파·해 및 다른 결합 구조와의
 * 경쟁을 최종 적용하지 않았으므로
 * "잠정" 상태다.
 *
 * =================================================
 *
 * PROVISIONALLY_PARTIAL
 *
 * 목표 오행 방향의 국 작용이
 * 의미 있게 형성되지만,
 * 완전한 국 성립으로 확정하기에는
 * 조건이 부족한 상태.
 *
 * 대표적으로:
 *
 * - 왕지 포함 반합
 * - 환경이 충분하지 않은 완전 삼합
 *
 * 등이 들어올 수 있다.
 *
 * =================================================
 *
 * CONDITIONAL
 *
 * 삼합계 관계는 존재하고
 * 목표 오행 방향도 검토할 수 있으나
 * 실제 국 작용에는 추가 조건이 필요한 상태.
 *
 * =================================================
 *
 * RELATION_ONLY
 *
 * 삼합계 구성 관계 자체는 존재하지만
 * 현재 기본 환경만으로는
 * 목표 오행의 국 작용이 형성되었다고
 * 보기 어려운 상태.
 *
 * 특히 왕지가 없는 생지+고지 Pair는
 * 이 상태까지 내려갈 수 있다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 단계에서는 원래 지지와 지장간을
 * 목표 오행으로 치환하지 않는다.
 *
 * 실제 기능 보존/변화는 9-9 책임이다.
 */
public enum EarthlyBranchThreeCombinationFormationState {

    PROVISIONALLY_ESTABLISHED(
            "잠정 국 성립",
            4
    ),

    PROVISIONALLY_PARTIAL(
            "잠정 부분 국",
            3
    ),

    CONDITIONAL(
            "조건부 국",
            2
    ),

    RELATION_ONLY(
            "삼합계 관계만 존재",
            1
    );

    private final String koreanName;

    private final int rank;

    EarthlyBranchThreeCombinationFormationState(
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

    public boolean provisionallyEstablished() {

        return this == PROVISIONALLY_ESTABLISHED;
    }

    public boolean provisionallyPartial() {

        return this == PROVISIONALLY_PARTIAL;
    }

    public boolean conditional() {

        return this == CONDITIONAL;
    }

    public boolean relationOnly() {

        return this == RELATION_ONLY;
    }

    /**
     * 방해 검토 전 기준에서
     * 목표 오행의 국 작용이
     * 의미 있게 형성되었는가.
     */
    public boolean meaningfulFormation() {

        return provisionallyEstablished()
                || provisionallyPartial();
    }

    /**
     * 9-7 / 9-8에서 국 성립 후보로
     * 계속 검토할 필요가 있는가.
     */
    public boolean formationCandidateRemains() {

        return !relationOnly();
    }
}