package com.dajeom.type;

import java.util.Objects;

/**
 * 정밀화 8차-2.
 *
 * 다점 엔진에서 사용하는 천간 사충(四沖).
 *
 * =================================================
 *
 * 甲庚沖
 * 乙辛沖
 * 丙壬沖
 * 丁癸沖
 *
 * =================================================
 *
 * 충 자체는 상호 관계이지만,
 * 오행 극제 방향은 별도로 존재한다.
 *
 * 예:
 *
 * 甲庚沖
 *
 * Metal controls Wood
 *
 * 따라서:
 *
 * 庚 -> 甲
 *
 * 방향으로 controller / controlled를 보존한다.
 *
 * =================================================
 *
 * 戊·己는 천간 사충 Pair에 포함하지 않는다.
 *
 * 다른 상극 관계는 8-2에서
 * CONTROL_ONLY로 별도 보존한다.
 */
public enum HeavenlyStemClashType {

    JIA_GENG(
            "갑경충",
            HeavenlyStem.JIA,
            HeavenlyStem.GENG,
            HeavenlyStem.GENG,
            HeavenlyStem.JIA
    ),

    YI_XIN(
            "을신충",
            HeavenlyStem.YI,
            HeavenlyStem.XIN,
            HeavenlyStem.XIN,
            HeavenlyStem.YI
    ),

    BING_REN(
            "병임충",
            HeavenlyStem.BING,
            HeavenlyStem.REN,
            HeavenlyStem.REN,
            HeavenlyStem.BING
    ),

    DING_GUI(
            "정계충",
            HeavenlyStem.DING,
            HeavenlyStem.GUI,
            HeavenlyStem.GUI,
            HeavenlyStem.DING
    );

    private final String koreanName;

    private final HeavenlyStem firstStem;

    private final HeavenlyStem secondStem;

    private final HeavenlyStem controllerStem;

    private final HeavenlyStem controlledStem;

    HeavenlyStemClashType(

            String koreanName,

            HeavenlyStem firstStem,

            HeavenlyStem secondStem,

            HeavenlyStem controllerStem,

            HeavenlyStem controlledStem

    ) {

        this.koreanName =
                Objects.requireNonNull(
                        koreanName
                );

        this.firstStem =
                Objects.requireNonNull(
                        firstStem
                );

        this.secondStem =
                Objects.requireNonNull(
                        secondStem
                );

        this.controllerStem =
                Objects.requireNonNull(
                        controllerStem
                );

        this.controlledStem =
                Objects.requireNonNull(
                        controlledStem
                );
    }

    public String koreanName() {

        return koreanName;
    }

    public HeavenlyStem firstStem() {

        return firstStem;
    }

    public HeavenlyStem secondStem() {

        return secondStem;
    }

    public HeavenlyStem controllerStem() {

        return controllerStem;
    }

    public HeavenlyStem controlledStem() {

        return controlledStem;
    }

    /**
     * Pair 순서를 무시하고 해당 천간충인지 확인한다.
     */
    public boolean matches(

            HeavenlyStem stem1,

            HeavenlyStem stem2

    ) {

        if (
                stem1 == null
                        || stem2 == null
        ) {

            return false;
        }

        return (
                stem1 == firstStem
                        && stem2 == secondStem
        )
                || (
                stem1 == secondStem
                        && stem2 == firstStem
        );
    }

    public boolean contains(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return false;
        }

        return stem == firstStem
                || stem == secondStem;
    }

    public HeavenlyStem counterpartOf(
            HeavenlyStem stem
    ) {

        if (
                stem == firstStem
        ) {

            return secondStem;
        }

        if (
                stem == secondStem
        ) {

            return firstStem;
        }

        return null;
    }
}