package com.dajeom.type;

import java.util.Objects;

/**
 * 정밀화 8차-1.
 *
 * 천간 오합(天干五合)의 관계 유형.
 *
 * =================================================
 *
 * 甲己合土
 * 乙庚合金
 * 丙辛合水
 * 丁壬合木
 * 戊癸合火
 *
 * =================================================
 *
 * 주의:
 *
 * nominalTransformationElement는
 * 전통적으로 해당 천간합이 지향한다고 보는
 * "명목상 합화 오행"이다.
 *
 * 이 값이 존재한다고 해서
 * 실제로 해당 오행으로 변화했다는 뜻이 아니다.
 *
 * 실제 합화 성립 여부는
 * 정밀화 9차에서:
 *
 * - 월령
 * - 계절
 * - 통근
 * - 천간 세력
 * - 지지 세력
 * - 방해 천간
 * - 합화 오행의 근
 * - 합화 조건
 *
 * 등을 별도로 검토한다.
 */
public enum HeavenlyStemCombinationType {

    JIA_JI_EARTH(
            "갑기합토",
            HeavenlyStem.JIA,
            HeavenlyStem.JI,
            FiveElement.EARTH
    ),

    YI_GENG_METAL(
            "을경합금",
            HeavenlyStem.YI,
            HeavenlyStem.GENG,
            FiveElement.METAL
    ),

    BING_XIN_WATER(
            "병신합수",
            HeavenlyStem.BING,
            HeavenlyStem.XIN,
            FiveElement.WATER
    ),

    DING_REN_WOOD(
            "정임합목",
            HeavenlyStem.DING,
            HeavenlyStem.REN,
            FiveElement.WOOD
    ),

    WU_GUI_FIRE(
            "무계합화",
            HeavenlyStem.WU,
            HeavenlyStem.GUI,
            FiveElement.FIRE
    );

    private final String koreanName;

    private final HeavenlyStem firstStem;

    private final HeavenlyStem secondStem;

    private final FiveElement nominalTransformationElement;

    HeavenlyStemCombinationType(

            String koreanName,

            HeavenlyStem firstStem,

            HeavenlyStem secondStem,

            FiveElement nominalTransformationElement

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

        this.nominalTransformationElement =
                Objects.requireNonNull(
                        nominalTransformationElement
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

    /**
     * 전통적인 명목상 합화 오행.
     *
     * 실제 합화 완료 오행이 아니다.
     */
    public FiveElement nominalTransformationElement() {

        return nominalTransformationElement;
    }

    /**
     * 순서를 무시하고
     * 두 천간이 해당 합 관계인지 확인한다.
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

    /**
     * 합 상대 천간.
     *
     * 해당 천간이 이 합에 속하지 않으면 null.
     */
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