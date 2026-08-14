package com.dajeom.type;

/**
 * 정밀화 10차-2.
 *
 * 비견·겁재 복합구조 유형.
 *
 * =================================================
 *
 * 하나의 명식에 여러 Pattern이
 * 동시에 존재할 수 있다.
 *
 * 예:
 *
 * 비겁 군집
 * +
 * 겁재 경쟁
 * +
 * 재성 쟁탈
 *
 * 을 동시에 보존할 수 있다.
 *
 * =================================================
 *
 * 이 enum 자체는 길흉 판정이 아니다.
 */
public enum CompanionCompositePatternType {

    SUPPORTIVE_COMPANION(
            "신약 보조형 비겁"
    ),

    COMPANION_CLUSTER(
            "비겁 군집"
    ),

    ROB_WEALTH_COMPETITION(
            "겁재 경쟁 구조"
    ),

    WEALTH_CONTEST(
            "재성 쟁탈 구조"
    ),

    EXCESSIVE_COMPANION(
            "신강 비겁 과다"
    ),

    MIXED_COMPANION_ROB_WEALTH(
            "비견·겁재 혼재"
    );

    private final String koreanName;

    CompanionCompositePatternType(
            String koreanName
    ) {

        this.koreanName = koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean supportive() {

        return this == SUPPORTIVE_COMPANION;
    }

    public boolean cluster() {

        return this == COMPANION_CLUSTER;
    }

    public boolean robWealthCompetition() {

        return this == ROB_WEALTH_COMPETITION;
    }

    public boolean wealthContest() {

        return this == WEALTH_CONTEST;
    }

    public boolean excessive() {

        return this == EXCESSIVE_COMPANION;
    }

    public boolean mixed() {

        return this == MIXED_COMPANION_ROB_WEALTH;
    }
}