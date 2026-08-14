package com.dajeom.type;

/**
 * 정밀화 9차-1.
 *
 * 천간합 후보의 기본 적용도.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 값은 실제 合化 성립 상태가 아니다.
 *
 * STRONG이라고 해서
 * 이미 합화가 완료되었다는 뜻이 아니다.
 *
 * =================================================
 *
 * STRONG
 *
 * 천간합 Pair의 위치 구조가 좋고,
 * 합화 목표 오행을 계절·월지·원국 세력이
 * 충분히 지지하는 강한 검토 후보.
 *
 * =================================================
 *
 * ACTIVE
 *
 * 실제 합화 검토가 필요한
 * 정상적인 활성 후보.
 *
 * =================================================
 *
 * CONDITIONAL
 *
 * 합 관계는 존재하지만
 * 기본적인 합화 환경이 충분하지 않아
 * 추가 조건 검토가 필요한 후보.
 *
 * =================================================
 *
 * WEAK
 *
 * 천간합 관계 자체는 존재하지만
 * 거리와 목표 오행 환경을 고려할 때
 * 기본 적용도가 낮은 후보.
 *
 * =================================================
 *
 * 이후:
 *
 * 9-2 쟁합·다중합·방해
 * 9-3 최종 합화 성립도
 *
 * 에 의해 추가 검증된다.
 */
public enum HeavenlyStemCombinationApplicability {

    STRONG(
            "강한 합화 후보",
            4
    ),

    ACTIVE(
            "활성 합화 후보",
            3
    ),

    CONDITIONAL(
            "조건부 합화 후보",
            2
    ),

    WEAK(
            "약한 합화 후보",
            1
    );

    private final String koreanName;

    private final int rank;

    HeavenlyStemCombinationApplicability(
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

    public boolean strong() {

        return this == STRONG;
    }

    public boolean active() {

        return this == ACTIVE;
    }

    public boolean conditional() {

        return this == CONDITIONAL;
    }

    public boolean weak() {

        return this == WEAK;
    }

    public boolean meaningful() {

        return strong()
                || active();
    }

    public boolean requiresFurtherReview() {

        return conditional()
                || weak();
    }
}