package com.dajeom.type;

/**
 * 정밀화 9차-6.
 *
 * 지지 방합(方合)의
 * 방해·경쟁 검토 전 국 성립 상태.
 *
 * =================================================
 *
 * PROVISIONALLY_ESTABLISHED
 *
 * 방합 구조와 계절 방향성이 충분히 일치하여
 * 목표 오행의 方局이 강하게 성립하는
 * 잠정 상태.
 *
 * 아직:
 *
 * - 沖
 * - 刑
 * - 破
 * - 害
 * - 六合
 * - 三合
 *
 * 과의 최종 경쟁을 반영하지 않았으므로
 * 잠정 상태다.
 *
 * =================================================
 *
 * PROVISIONALLY_PARTIAL
 *
 * 방합의 계절 방향 작용이
 * 의미 있게 형성되지만
 * 완전한 方局으로 확정하기에는
 * 조건이 부족한 상태.
 *
 * =================================================
 *
 * CONDITIONAL
 *
 * 방합계 관계가 존재하고
 * 목표 오행 방향도 검토할 수 있으나
 * 실제 方局 작용에는 추가 조건이 필요한 상태.
 *
 * =================================================
 *
 * RELATION_ONLY
 *
 * 방합 계열의 구성 관계 자체는 존재하지만
 * 현재 기본 환경에서는
 * 목표 오행의 方局 작용이 성립했다고
 * 보기 어려운 상태.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 단계에서는 원래 지지·지장간을
 * 목표 오행으로 치환하지 않는다.
 *
 * 실제 기능 변화는 9-9에서 처리한다.
 */
public enum EarthlyBranchDirectionalCombinationFormationState {

    PROVISIONALLY_ESTABLISHED(
            "잠정 방국 성립",
            4
    ),

    PROVISIONALLY_PARTIAL(
            "잠정 부분 방국",
            3
    ),

    CONDITIONAL(
            "조건부 방국",
            2
    ),

    RELATION_ONLY(
            "방합계 관계만 존재",
            1
    );

    private final String koreanName;

    private final int rank;

    EarthlyBranchDirectionalCombinationFormationState(
            String koreanName,
            int rank
    ) {

        this.koreanName = koreanName;
        this.rank = rank;
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
     * 현재 기본 환경 기준으로
     * 목표 오행의 方局 작용이 의미 있게
     * 형성되었는가.
     */
    public boolean meaningfulFormation() {

        return provisionallyEstablished()
                || provisionallyPartial();
    }

    /**
     * 이후 9-7 / 9-8에서
     * 方局 성립 후보로 계속 검토할 필요가 있는가.
     */
    public boolean formationCandidateRemains() {

        return !relationOnly();
    }
}