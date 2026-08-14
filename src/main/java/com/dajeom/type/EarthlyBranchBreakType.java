package com.dajeom.type;

import java.util.Objects;

/**
 * 정밀화 8차-8.
 *
 * 지지 파(地支破).
 *
 * =================================================
 *
 * 子酉破
 * 丑辰破
 * 寅亥破
 * 卯午破
 * 巳申破
 * 未戌破
 *
 * =================================================
 *
 * 파는 기본적으로 두 지지 사이의 Pair 관계다.
 *
 * 이 단계에서는 어느 한쪽을:
 *
 * - 공격자
 * - 피공격자
 *
 * 로 나누지 않는다.
 *
 * 관계 자체를 대칭적인 구조 Evidence로 보존한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 동일 Pair가 다른 관계를 동시에 가질 수 있다.
 *
 * 예:
 *
 * 寅亥
 * -> 六合
 * -> 破
 *
 * 巳申
 * -> 六合
 * -> 破
 *
 * 따라서:
 *
 * "六合이므로 破가 없다"
 *
 * 또는
 *
 * "破이므로 六合이 무효다"
 *
 * 처럼 8차에서 관계를 삭제하지 않는다.
 *
 * 실제 상호작용 우선도는 8-10 및 9차에서 검토한다.
 */
public enum EarthlyBranchBreakType {

    ZI_YOU(
            "자유파",
            EarthlyBranch.ZI,
            EarthlyBranch.YOU
    ),

    CHOU_CHEN(
            "축진파",
            EarthlyBranch.CHOU,
            EarthlyBranch.CHEN
    ),

    YIN_HAI(
            "인해파",
            EarthlyBranch.YIN,
            EarthlyBranch.HAI
    ),

    MAO_WU(
            "묘오파",
            EarthlyBranch.MAO,
            EarthlyBranch.WU
    ),

    SI_SHEN(
            "사신파",
            EarthlyBranch.SI,
            EarthlyBranch.SHEN
    ),

    WEI_XU(
            "미술파",
            EarthlyBranch.WEI,
            EarthlyBranch.XU
    );

    private final String koreanName;

    private final EarthlyBranch firstBranch;

    private final EarthlyBranch secondBranch;

    EarthlyBranchBreakType(
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
     * 입력 순서를 무시하고
     * 해당 파 관계인지 확인한다.
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
     * 해당 지지의 파 상대.
     *
     * 이 BreakType의 구성원이 아니면 null.
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