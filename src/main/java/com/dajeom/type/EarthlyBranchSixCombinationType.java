package com.dajeom.type;

import java.util.Objects;

/**
 * 정밀화 8차-3.
 *
 * 지지 육합(地支六合).
 *
 * =================================================
 *
 * 子丑合土
 * 寅亥合木
 * 卯戌合火
 * 辰酉合金
 * 巳申合水
 * 午未合土
 *
 * =================================================
 *
 * nominalTransformationElement는
 * 전통적으로 해당 육합이 지향한다고 보는
 * 명목상 합화 오행이다.
 *
 * 중요:
 *
 * 육합 Pair가 원국에 존재한다고 해서
 * 실제로 해당 오행으로 변화했다고
 * 판정하지 않는다.
 *
 * 실제 합화는 정밀화 9차에서:
 *
 * - 월령
 * - 계절
 * - 지지 세력
 * - 투간
 * - 통근
 * - 다른 합
 * - 충
 * - 형
 * - 합화 오행의 세력
 * - 주변 구조
 *
 * 등을 함께 검토한다.
 */
public enum EarthlyBranchSixCombinationType {

    ZI_CHOU_EARTH(
            "자축합토",
            EarthlyBranch.ZI,
            EarthlyBranch.CHOU,
            FiveElement.EARTH
    ),

    YIN_HAI_WOOD(
            "인해합목",
            EarthlyBranch.YIN,
            EarthlyBranch.HAI,
            FiveElement.WOOD
    ),

    MAO_XU_FIRE(
            "묘술합화",
            EarthlyBranch.MAO,
            EarthlyBranch.XU,
            FiveElement.FIRE
    ),

    CHEN_YOU_METAL(
            "진유합금",
            EarthlyBranch.CHEN,
            EarthlyBranch.YOU,
            FiveElement.METAL
    ),

    SI_SHEN_WATER(
            "사신합수",
            EarthlyBranch.SI,
            EarthlyBranch.SHEN,
            FiveElement.WATER
    ),

    WU_WEI_EARTH(
            "오미합토",
            EarthlyBranch.WU,
            EarthlyBranch.WEI,
            FiveElement.EARTH
    );

    private final String koreanName;

    private final EarthlyBranch firstBranch;

    private final EarthlyBranch secondBranch;

    private final FiveElement nominalTransformationElement;

    EarthlyBranchSixCombinationType(

            String koreanName,

            EarthlyBranch firstBranch,

            EarthlyBranch secondBranch,

            FiveElement nominalTransformationElement

    ) {

        this.koreanName =
                Objects.requireNonNull(
                        koreanName
                );

        this.firstBranch =
                Objects.requireNonNull(
                        firstBranch
                );

        this.secondBranch =
                Objects.requireNonNull(
                        secondBranch
                );

        this.nominalTransformationElement =
                Objects.requireNonNull(
                        nominalTransformationElement
                );
    }

    public String koreanName() {

        return koreanName;
    }

    public EarthlyBranch firstBranch() {

        return firstBranch;
    }

    public EarthlyBranch secondBranch() {

        return secondBranch;
    }

    /**
     * 명목상 합화 목표 오행.
     *
     * 실제 합화 완료 오행이 아니다.
     */
    public FiveElement nominalTransformationElement() {

        return nominalTransformationElement;
    }

    /**
     * 순서를 무시하고
     * 두 지지가 해당 육합 Pair인지 확인한다.
     */
    public boolean matches(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        if (
                branch1 == null
                        || branch2 == null
        ) {

            return false;
        }

        return (
                branch1 == firstBranch
                        && branch2 == secondBranch
        )
                || (
                branch1 == secondBranch
                        && branch2 == firstBranch
        );
    }

    public boolean contains(
            EarthlyBranch branch
    ) {

        if (
                branch == null
        ) {

            return false;
        }

        return branch == firstBranch
                || branch == secondBranch;
    }

    /**
     * 육합 상대 지지.
     *
     * 해당 지지가 이 육합에 포함되지 않으면 null.
     */
    public EarthlyBranch counterpartOf(
            EarthlyBranch branch
    ) {

        if (
                branch == firstBranch
        ) {

            return secondBranch;
        }

        if (
                branch == secondBranch
        ) {

            return firstBranch;
        }

        return null;
    }
}