package com.dajeom.type;

/**
 * 정밀화 8차-2.
 *
 * 천간 사이의 극제 관계 유형.
 *
 * =================================================
 *
 * CLASH
 *
 * 다점 엔진에서 천간충으로 별도 취급하는:
 *
 * 甲庚
 * 乙辛
 * 丙壬
 * 丁癸
 *
 * 관계.
 *
 * 천간충 역시 오행 상극 관계를 포함한다.
 *
 * =================================================
 *
 * CONTROL_ONLY
 *
 * 오행상 극제 관계는 존재하지만
 * 천간충 Pair에는 해당하지 않는 관계.
 *
 * 예:
 *
 * 甲剋戊
 * 乙剋己
 * 丙剋庚
 * 戊剋壬
 *
 * 등.
 *
 * =================================================
 *
 * 주의:
 *
 * 이 단계에서는 길흉을 판단하지 않는다.
 *
 * "충이다"
 * "극한다"
 *
 * 라는 구조적 관계만 기록한다.
 */
public enum HeavenlyStemControlRelationType {

    CLASH(
            "천간충",
            true
    ),

    CONTROL_ONLY(
            "일반 극제",
            false
    );

    private final String koreanName;

    private final boolean clash;

    HeavenlyStemControlRelationType(
            String koreanName,
            boolean clash
    ) {

        this.koreanName =
                koreanName;

        this.clash =
                clash;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean clash() {

        return clash;
    }

    public boolean controlOnly() {

        return this == CONTROL_ONLY;
    }
}