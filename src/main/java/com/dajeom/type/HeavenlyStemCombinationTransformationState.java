package com.dajeom.type;

/**
 * 정밀화 9차-3.
 *
 * 천간합의 실제 합화 성립 상태.
 *
 * =================================================
 *
 * TRANSFORMED
 *
 * 합 관계가 존재하고,
 * 합화 목표 오행으로의 변화 조건이
 * 충분히 형성된 상태.
 *
 * =================================================
 *
 * PARTIALLY_TRANSFORMED
 *
 * 합화 방향이 의미 있게 작동하지만
 * 완전한 化로 보기에는 부족한 상태.
 *
 * =================================================
 *
 * CONDITIONAL
 *
 * 합 관계와 합화 가능성은 존재하지만
 * 실제 化 성립 여부를 확정하기 어려운 상태.
 *
 * =================================================
 *
 * COMBINED_NOT_TRANSFORMED
 *
 * 合 관계는 존재하지만
 * 化는 성립하지 않는 것으로 보는 상태.
 *
 * 즉:
 *
 * 合而不化
 *
 * =================================================
 *
 * SUPPRESSED
 *
 * 합 관계 Evidence 자체는 존재하지만
 *
 * - 쟁합
 * - 강한 경쟁
 * - 외부 충·극
 * - 매우 약한 합화 환경
 *
 * 때문에 합화 작용이 사실상 억제된 상태.
 *
 * =================================================
 *
 * 중요:
 *
 * 9-3에서도 원래 천간의 오행을
 * 즉시 삭제하거나 치환하지 않는다.
 *
 * 실제 원래 기능 보존/변화는
 * 정밀화 9-9에서 처리한다.
 */
public enum HeavenlyStemCombinationTransformationState {

    TRANSFORMED(
            "합화 성립",
            5
    ),

    PARTIALLY_TRANSFORMED(
            "부분 합화",
            4
    ),

    CONDITIONAL(
            "조건부 합화",
            3
    ),

    COMBINED_NOT_TRANSFORMED(
            "합은 있으나 불화",
            2
    ),

    SUPPRESSED(
            "합화 억제",
            1
    );

    private final String koreanName;

    private final int rank;

    HeavenlyStemCombinationTransformationState(
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

    public boolean transformed() {

        return this == TRANSFORMED;
    }

    public boolean partiallyTransformed() {

        return this == PARTIALLY_TRANSFORMED;
    }

    public boolean conditional() {

        return this == CONDITIONAL;
    }

    public boolean combinedNotTransformed() {

        return this == COMBINED_NOT_TRANSFORMED;
    }

    public boolean suppressed() {

        return this == SUPPRESSED;
    }

    /**
     * 실제 합화가 의미 있게 작동한다고
     * 볼 수 있는 상태.
     */
    public boolean meaningfulTransformation() {

        return transformed()
                || partiallyTransformed();
    }

    /**
     * 합화 가능성을 여전히
     * 후속 분석에서 고려해야 하는가.
     */
    public boolean transformationCandidateRemains() {

        return transformed()
                || partiallyTransformed()
                || conditional();
    }

    /**
     * 현재 9-3 단계에서는
     * 합화가 성립하지 않았다고 보는 상태.
     */
    public boolean transformationNotEstablished() {

        return combinedNotTransformed()
                || suppressed();
    }

    /**
     * 완전한 합화가 확정된 상태인가.
     */
    public boolean fullyTransformed() {

        return transformed();
    }
}