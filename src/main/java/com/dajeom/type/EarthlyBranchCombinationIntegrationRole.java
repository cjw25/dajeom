package com.dajeom.type;

/**
 * 정밀화 9차-8.
 *
 * 六合·三合·方合 후보를
 * 상호 비교한 뒤 부여하는 통합 역할.
 *
 * =================================================
 *
 * PRIMARY
 *
 * 현재 구조에서 실제 결합 작용을
 * 주도하는 후보.
 *
 * 하나만 존재해야 하는 것은 아니다.
 *
 * 서로 구성원을 공유하지 않는 독립 구조나
 * 동급의 동일 오행 수렴 구조에서는
 * 복수 PRIMARY가 존재할 수 있다.
 *
 * =================================================
 *
 * SUPPORTING
 *
 * 같은 목표 오행으로 수렴하지만
 * 더 강한 동일 방향 후보가 존재해
 * 보조 역할을 하는 결합.
 *
 * =================================================
 *
 * COMPETING
 *
 * 같은 구성원을 공유하면서
 * 서로 다른 목표 오행을 향하고,
 * 우선도가 동급이라 하나를 확정적으로
 * 선택할 수 없는 상태.
 *
 * =================================================
 *
 * DEFERRED
 *
 * 결합 가능성 자체는 남아 있지만:
 *
 * - 더 강한 다른 목표 후보가 존재하거나
 * - 기본 성립도가 조건부 수준이거나
 * - 현재 구조에서는 주도 후보가 되기 어려운 경우
 *
 * 후순위로 보류된 상태.
 *
 * =================================================
 *
 * SUPPRESSED
 *
 * 강한 방해 등으로 인해 현재 원국에서
 * 실질적인 합화·국 작용을 적용하기 어려운 상태.
 *
 * 원본 관계 Evidence 자체가 삭제된다는 뜻은 아니다.
 */
public enum EarthlyBranchCombinationIntegrationRole {

    PRIMARY(
            "주결합",
            5
    ),

    SUPPORTING(
            "보조결합",
            4
    ),

    COMPETING(
            "경쟁결합",
            3
    ),

    DEFERRED(
            "보류결합",
            2
    ),

    SUPPRESSED(
            "억제결합",
            1
    );

    private final String koreanName;

    private final int rank;

    EarthlyBranchCombinationIntegrationRole(
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

    public boolean primary() {

        return this == PRIMARY;
    }

    public boolean supporting() {

        return this == SUPPORTING;
    }

    public boolean competing() {

        return this == COMPETING;
    }

    public boolean deferred() {

        return this == DEFERRED;
    }

    public boolean suppressed() {

        return this == SUPPRESSED;
    }

    /**
     * 현재 원국에서 실제 결합 방향에
     * 적극적으로 참여하는 역할.
     */
    public boolean effective() {

        return primary()
                || supporting();
    }

    /**
     * 추가 해석 주의가 필요한 역할.
     */
    public boolean requiresReview() {

        return competing()
                || deferred();
    }

    public boolean inactive() {

        return suppressed();
    }
}