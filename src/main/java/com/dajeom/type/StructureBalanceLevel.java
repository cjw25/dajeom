package com.dajeom.type;

/**
 * 정밀화 5차-4.
 *
 * 격 후보의 세력과
 * 현재 일간의 신강·신약 상태가
 * 어느 정도 균형을 이루는지를 나타낸다.
 *
 * 주의:
 *
 * 이것은:
 *
 * - 길격 / 흉격
 * - 좋은 격 / 나쁜 격
 * - 용신 / 기신
 *
 * 판정이 아니다.
 *
 * 오직:
 *
 * "현재 일간 강약이
 * 이 격 후보의 작용을
 * 어느 정도 감당하거나 수용할 수 있는가"
 *
 * 를 표현한다.
 */
public enum StructureBalanceLevel {

    /**
     * 현재 일간 강약과
     * 격 후보의 세력이 비교적 안정적으로 맞는다.
     */
    BALANCED(
            "강약 균형",
            4
    ),

    /**
     * 완전히 안정적이라고 보기는 어렵지만
     * 현재 일간이 해당 격 구조를
     * 수용할 수 있는 범위.
     */
    WORKABLE(
            "수용 가능",
            3
    ),

    /**
     * 격 후보의 작용이
     * 현재 일간에게 부담이 되는 구조.
     */
    STRAINED(
            "강약 부담",
            2
    ),

    /**
     * 일간 강약과 격 후보의 세력이
     * 상당히 어긋나는 구조.
     */
    IMBALANCED(
            "강약 불균형",
            1
    ),

    /**
     * 5-3에서 INACTIVE 처리된 후보.
     *
     * 현재 격 후보로 적극적인
     * 강약 평가를 하지 않는다.
     */
    NOT_APPLICABLE(
            "평가 대상 아님",
            0
    );

    private final String koreanName;

    /**
     * 정렬 및 비교용 단계.
     *
     * 확률이나 격국 점수가 아니다.
     */
    private final int rank;

    StructureBalanceLevel(
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

    public boolean workable() {

        return this == WORKABLE;
    }

    public boolean strained() {

        return this == STRAINED;
    }

    public boolean imbalanced() {

        return this == IMBALANCED;
    }

    public boolean notApplicable() {

        return this == NOT_APPLICABLE;
    }

    public boolean acceptable() {

        return this == BALANCED
                || this == WORKABLE;
    }

    public boolean burdened() {

        return this == STRAINED
                || this == IMBALANCED;
    }

    public boolean evaluated() {

        return this != NOT_APPLICABLE;
    }
}