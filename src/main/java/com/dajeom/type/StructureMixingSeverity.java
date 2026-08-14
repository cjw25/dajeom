package com.dajeom.type;

/**
 * 정밀화 5차-7.
 *
 * 하나의 혼잡 Evidence가
 * 격 구조의 순도에 미치는 상대적 수준.
 *
 * MODERATE
 * 혼재 신호는 분명하지만
 * 그 자체로 중심 격을 크게 흔든다고
 * 단정하기 어려운 상태.
 *
 * HIGH
 * 격 구조의 단일성을 상당히 낮추는
 * 강한 혼잡 Evidence.
 *
 * 확률이나 과학적 측정값이 아니다.
 */
public enum StructureMixingSeverity {

    MODERATE(
            "중간 혼잡",
            1
    ),

    HIGH(
            "강한 혼잡",
            2
    );

    private final String koreanName;

    private final int rank;

    StructureMixingSeverity(
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

    public boolean moderate() {

        return this == MODERATE;
    }

    public boolean high() {

        return this == HIGH;
    }
}