package com.dajeom.type;

/**
 * 정밀화 6차-2.
 *
 * 한난 조정 방향과
 * 조습 조정 방향 사이의 관계.
 *
 * NONE
 * 두 축 모두 실질적인 조정이 필요하지 않음.
 *
 * SINGLE_AXIS
 * 한난 또는 조습 한쪽만 조정이 필요함.
 *
 * ALIGNED
 * 두 축 모두 문제이지만
 * 기본적인 조후 보정 방향이
 * 비교적 같은 방향으로 움직일 수 있음.
 *
 * 예:
 *
 * 寒濕
 * -> 온난 + 건조 방향
 *
 * 熱燥
 * -> 냉각 + 습윤 방향
 *
 * COMPETING
 * 두 축 모두 문제이고
 * 한쪽을 직접 보정하면 다른 축에
 * 부담을 줄 가능성이 있는 구조.
 *
 * 예:
 *
 * 寒燥
 * -> 온난 필요
 * -> 습윤 필요
 *
 * 熱濕
 * -> 냉각 필요
 * -> 건조 필요
 *
 * 실제 사용할 오행은 6-3에서 결정한다.
 */
public enum ClimateCorrectionInteraction {

    NONE(
            "조정 충돌 없음"
    ),

    SINGLE_AXIS(
            "단일 축 조정"
    ),

    ALIGNED(
            "보정 방향 정렬"
    ),

    COMPETING(
            "보정 방향 경쟁"
    );

    private final String koreanName;

    ClimateCorrectionInteraction(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean none() {

        return this == NONE;
    }

    public boolean singleAxis() {

        return this == SINGLE_AXIS;
    }

    public boolean aligned() {

        return this == ALIGNED;
    }

    public boolean competing() {

        return this == COMPETING;
    }

    public boolean dualAxis() {

        return this == ALIGNED
                || this == COMPETING;
    }
}