package com.dajeom.type;

/**
 * 정밀화 10차-1.
 *
 * 원국 전체 십성 세력 안에서
 * 하나의 십성이 차지하는 상대 수준.
 *
 * =================================================
 *
 * 이 값은 확률이 아니다.
 *
 * 단순 출현 횟수도 아니다.
 *
 * 3차에서 계산된 오행 최종 세력을
 * 천간·지장간 source에 배분한 뒤,
 * 십성별 점유율을 기준으로 분류한다.
 */
public enum TenGodPowerLevel {

    DOMINANT(
            "주도",
            5
    ),

    STRONG(
            "강함",
            4
    ),

    MODERATE(
            "보통",
            3
    ),

    WEAK(
            "약함",
            2
    ),

    ABSENT(
            "없음",
            1
    );

    private final String koreanName;

    private final int rank;

    TenGodPowerLevel(
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

    public boolean dominant() {

        return this == DOMINANT;
    }

    public boolean strong() {

        return this == STRONG;
    }

    public boolean moderate() {

        return this == MODERATE;
    }

    public boolean weak() {

        return this == WEAK;
    }

    public boolean absent() {

        return this == ABSENT;
    }

    public boolean meaningful() {

        return dominant()
                || strong()
                || moderate();
    }

    public boolean strongSide() {

        return dominant()
                || strong();
    }
}