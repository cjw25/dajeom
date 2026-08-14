package com.dajeom.type;

/**
 * 정밀화 6차-6.
 *
 * 서로 상극하는 두 오행의
 * 상대적인 세력 균형 상태.
 *
 * BALANCED
 * 양쪽이 매우 비슷하게 대립.
 *
 * COMPETITIVE
 * 한쪽이 조금 강하지만
 * 서로 실질적인 대립이 가능한 상태.
 *
 * UNEVEN
 * 세력 차이가 상당하지만
 * 약한 쪽도 무시할 수준은 아님.
 *
 * ONE_SIDED
 * 한쪽이 다른 쪽을
 * 명확하게 압도하는 상태.
 *
 * 통관은 일반적으로
 * BALANCED / COMPETITIVE 관계에서
 * 더 중요하게 검토된다.
 */
public enum PassageBalanceState {

    BALANCED(
            "팽팽한 대립",
            4
    ),

    COMPETITIVE(
            "경쟁적 대립",
            3
    ),

    UNEVEN(
            "불균형 대립",
            2
    ),

    ONE_SIDED(
            "일방 우세",
            1
    );

    private final String koreanName;

    private final int rank;

    PassageBalanceState(
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

    public boolean balanced() {

        return this == BALANCED;
    }

    public boolean competitive() {

        return this == COMPETITIVE;
    }

    public boolean uneven() {

        return this == UNEVEN;
    }

    public boolean oneSided() {

        return this == ONE_SIDED;
    }

    public boolean stronglyOpposed() {

        return this == BALANCED
                || this == COMPETITIVE;
    }
}