package com.dajeom.type;

/**
 * 정밀화 9차-9.
 *
 * 합화·국 성립 이후에도
 * 원래 천간·지지가 가지고 있던
 * 본래 오행 기능이 어느 정도 유지되는지를 표현한다.
 *
 * =================================================
 *
 * PRESERVED
 *
 * 본래 오행 기능을 그대로 유지한다.
 *
 * 합 관계 자체는 존재할 수 있지만
 * 실제 化 작용이 성립하지 않았거나
 * 적용이 억제된 경우.
 *
 * =================================================
 *
 * MOSTLY_PRESERVED
 *
 * 본래 기능이 주로 유지되지만
 * 합화 방향에 일부 영향을 받는 상태.
 *
 * =================================================
 *
 * PARTIALLY_REDUCED
 *
 * 합화·국 방향이 의미 있게 작동하여
 * 본래 기능이 눈에 띄게 감소하지만
 * 여전히 본래 기능도 살아 있는 상태.
 *
 * =================================================
 *
 * STRONGLY_REDUCED
 *
 * 완전한 천간 合化처럼
 * 목표 오행 방향의 작용이 매우 강해
 * 원래 오행 기능이 크게 약화된 상태.
 *
 * =================================================
 *
 * 중요:
 *
 * STRONGLY_REDUCED 역시
 * 원래 오행을 객체에서 삭제한다는 뜻이 아니다.
 */
public enum TransformationOriginalFunctionState {

    PRESERVED(
            "원래 기능 유지",
            4
    ),

    MOSTLY_PRESERVED(
            "원래 기능 대부분 유지",
            3
    ),

    PARTIALLY_REDUCED(
            "원래 기능 부분 감소",
            2
    ),

    STRONGLY_REDUCED(
            "원래 기능 크게 감소",
            1
    );

    private final String koreanName;

    /**
     * 본래 기능 보존 정도.
     *
     * 숫자 계산용 점수가 아니라
     * 조회/정렬용 순서값이다.
     */
    private final int preservationRank;

    TransformationOriginalFunctionState(
            String koreanName,
            int preservationRank
    ) {

        this.koreanName = koreanName;
        this.preservationRank = preservationRank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int preservationRank() {

        return preservationRank;
    }

    public boolean preserved() {

        return this == PRESERVED;
    }

    public boolean mostlyPreserved() {

        return this == MOSTLY_PRESERVED;
    }

    public boolean partiallyReduced() {

        return this == PARTIALLY_REDUCED;
    }

    public boolean stronglyReduced() {

        return this == STRONGLY_REDUCED;
    }

    public boolean reduced() {

        return partiallyReduced()
                || stronglyReduced();
    }

    public boolean originalFunctionStillMeaningful() {

        return preserved()
                || mostlyPreserved()
                || partiallyReduced();
    }
}