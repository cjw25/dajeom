package com.dajeom.type;

/**
 * 정밀화 5차-7.
 *
 * 하나의 격 후보가
 * 원국에서 얼마나 단일하고 명확하게
 * 구조를 유지하는지를 나타낸다.
 *
 * PURE
 * 다른 격 후보나 파격 구조의 간섭이
 * 거의 없는 비교적 선명한 구조.
 *
 * MOSTLY_PURE
 * 일부 혼재 신호가 있지만
 * 중심 구조는 비교적 명확한 상태.
 *
 * MIXED
 * 여러 격 후보나 십성 구조가
 * 실제로 함께 작동하는 상태.
 *
 * HIGHLY_MIXED
 * 강한 후보·투출·파격 등이
 * 여러 방향으로 동시에 형성된 상태.
 *
 * NOT_APPLICABLE
 * 비활성 후보 또는
 * 비견·겁재와 같은 특수구조 검토 대상.
 *
 * 주의:
 *
 * 순도가 높다고 해서 좋은 격이고,
 * 혼잡도가 높다고 해서 나쁜 격이라는 뜻은 아니다.
 */
public enum StructurePurityLevel {

    PURE(
            "순수",
            4
    ),

    MOSTLY_PURE(
            "대체로 순수",
            3
    ),

    MIXED(
            "혼재",
            2
    ),

    HIGHLY_MIXED(
            "강한 혼잡",
            1
    ),

    NOT_APPLICABLE(
            "평가 대상 아님",
            0
    );

    private final String koreanName;

    private final int rank;

    StructurePurityLevel(
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

    public boolean pure() {

        return this == PURE;
    }

    public boolean mostlyPure() {

        return this == MOSTLY_PURE;
    }

    public boolean mixed() {

        return this == MIXED;
    }

    public boolean highlyMixed() {

        return this == HIGHLY_MIXED;
    }

    public boolean notApplicable() {

        return this == NOT_APPLICABLE;
    }

    public boolean evaluated() {

        return this != NOT_APPLICABLE;
    }

    public boolean relativelyPure() {

        return this == PURE
                || this == MOSTLY_PURE;
    }

    public boolean meaningfullyMixed() {

        return this == MIXED
                || this == HIGHLY_MIXED;
    }
}