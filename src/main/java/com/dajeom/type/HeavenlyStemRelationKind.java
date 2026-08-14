package com.dajeom.type;

/**
 * 정밀화 8차-10.
 *
 * 하나의 천간 궁위 Pair에 존재하는
 * 관계 종류를 표현한다.
 *
 * =================================================
 *
 * COMBINATION
 *
 * 8-1 천간 오합.
 *
 * =================================================
 *
 * CLASH
 *
 * 8-2에서 별도 천간충으로 분류된 관계.
 *
 * 甲庚
 * 乙辛
 * 丙壬
 * 丁癸
 *
 * =================================================
 *
 * CONTROL_ONLY
 *
 * 오행 극제는 존재하지만
 * 천간충 Pair에는 속하지 않는 관계.
 *
 * =================================================
 *
 * 하나의 Pair가 여러 관계를 동시에
 * 가질 수 있다.
 *
 * 예:
 *
 * 丙辛
 *
 * -> 丙辛合水
 * -> 火剋金
 *
 * 따라서:
 *
 * COMBINATION
 * CONTROL_ONLY
 *
 * 두 관계가 동시에 존재할 수 있다.
 */
public enum HeavenlyStemRelationKind {

    COMBINATION(
            "천간합"
    ),

    CLASH(
            "천간충"
    ),

    CONTROL_ONLY(
            "천간 극제"
    );

    private final String koreanName;

    HeavenlyStemRelationKind(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean combination() {

        return this == COMBINATION;
    }

    public boolean clash() {

        return this == CLASH;
    }

    public boolean controlOnly() {

        return this == CONTROL_ONLY;
    }

    public boolean controlFamily() {

        return clash()
                || controlOnly();
    }
}