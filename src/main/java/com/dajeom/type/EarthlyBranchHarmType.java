package com.dajeom.type;

import java.util.Objects;

/**
 * 정밀화 8차-9.
 *
 * 지지 육해(地支六害).
 *
 * =================================================
 *
 * 子未害
 * 丑午害
 * 寅巳害
 * 卯辰害
 * 申亥害
 * 酉戌害
 *
 * =================================================
 *
 * 8차에서는 해 관계가 존재한다는
 * 구조적 Evidence만 보존한다.
 *
 * 어느 한쪽을:
 *
 * - 공격자
 * - 피해자
 *
 * 로 강제하지 않는다.
 *
 * =================================================
 *
 * 또한 동일 Pair가:
 *
 * - 六合
 * - 沖
 * - 刑
 * - 破
 * - 害
 *
 * 등 다른 관계와 중첩되더라도
 * 여기서 다른 Evidence를 삭제하지 않는다.
 *
 * 실제 관계 우선도와 작용은
 * 8-10 및 이후 단계에서 검토한다.
 */
public enum EarthlyBranchHarmType {

    ZI_WEI(
            "자미해",
            EarthlyBranch.ZI,
            EarthlyBranch.WEI
    ),

    CHOU_WU(
            "축오해",
            EarthlyBranch.CHOU,
            EarthlyBranch.WU
    ),

    YIN_SI(
            "인사해",
            EarthlyBranch.YIN,
            EarthlyBranch.SI
    ),

    MAO_CHEN(
            "묘진해",
            EarthlyBranch.MAO,
            EarthlyBranch.CHEN
    ),

    SHEN_HAI(
            "신해해",
            EarthlyBranch.SHEN,
            EarthlyBranch.HAI
    ),

    YOU_XU(
            "유술해",
            EarthlyBranch.YOU,
            EarthlyBranch.XU
    );

    private final String koreanName;

    private final EarthlyBranch firstBranch;

    private final EarthlyBranch secondBranch;

    EarthlyBranchHarmType(

            String koreanName,

            EarthlyBranch firstBranch,

            EarthlyBranch secondBranch

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
     * 입력 순서와 관계없이
     * 해당 육해 Pair인지 확인한다.
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
     * 해당 지지의 육해 상대.
     *
     * 이 HarmType에 포함되지 않는 지지라면 null.
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