package com.dajeom.type;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 8차-7.
 *
 * 다점 엔진에서 사용하는 지지 형(刑) 유형.
 *
 * =================================================
 *
 * 삼형 계열
 *
 * 寅巳申
 * 丑戌未
 *
 * =================================================
 *
 * 상형
 *
 * 子卯
 *
 * =================================================
 *
 * 자형
 *
 * 辰辰
 * 午午
 * 酉酉
 * 亥亥
 *
 * =================================================
 *
 * 유파에 따라 형의 명칭이나 세부 적용법은
 * 차이가 있으므로,
 *
 * 다점 엔진에서는:
 *
 * "무슨 전통 명칭의 형인가"
 *
 * 보다:
 *
 * "어떤 지지들이 어떤 구조로 형 관계를 만드는가"
 *
 * 를 우선 보존한다.
 *
 * =================================================
 *
 * 삼형 계열의 방향성
 *
 * 寅 -> 巳
 * 巳 -> 申
 * 申 -> 寅
 *
 * 丑 -> 戌
 * 戌 -> 未
 * 未 -> 丑
 *
 * 을 구조 Evidence로 보존할 수 있도록
 * punishes() 메서드를 제공한다.
 *
 * 단,
 * COMPLETE_THREE 자체를 하나의 단방향 관계로
 * 축약하지는 않는다.
 */
public enum EarthlyBranchPunishmentType {

    YIN_SI_SHEN(
            "인사신 삼형",
            EarthlyBranch.YIN,
            EarthlyBranch.SI,
            EarthlyBranch.SHEN
    ),

    CHOU_XU_WEI(
            "축술미 삼형",
            EarthlyBranch.CHOU,
            EarthlyBranch.XU,
            EarthlyBranch.WEI
    ),

    ZI_MAO(
            "자묘 상형",
            EarthlyBranch.ZI,
            EarthlyBranch.MAO,
            null
    ),

    CHEN_SELF(
            "진진 자형",
            EarthlyBranch.CHEN,
            EarthlyBranch.CHEN,
            null
    ),

    WU_SELF(
            "오오 자형",
            EarthlyBranch.WU,
            EarthlyBranch.WU,
            null
    ),

    YOU_SELF(
            "유유 자형",
            EarthlyBranch.YOU,
            EarthlyBranch.YOU,
            null
    ),

    HAI_SELF(
            "해해 자형",
            EarthlyBranch.HAI,
            EarthlyBranch.HAI,
            null
    );

    private final String koreanName;

    private final EarthlyBranch firstBranch;

    private final EarthlyBranch secondBranch;

    private final EarthlyBranch thirdBranch;

    EarthlyBranchPunishmentType(

            String koreanName,

            EarthlyBranch firstBranch,

            EarthlyBranch secondBranch,

            EarthlyBranch thirdBranch

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

        this.thirdBranch =
                thirdBranch;
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

    public EarthlyBranch thirdBranch() {

        return thirdBranch;
    }

    public boolean threePunishment() {

        return this == YIN_SI_SHEN
                || this == CHOU_XU_WEI;
    }

    public boolean mutualPunishment() {

        return this == ZI_MAO;
    }

    public boolean selfPunishment() {

        return this == CHEN_SELF
                || this == WU_SELF
                || this == YOU_SELF
                || this == HAI_SELF;
    }

    public List<EarthlyBranch> members() {

        if (
                threePunishment()
        ) {

            return List.of(
                    firstBranch,
                    secondBranch,
                    thirdBranch
            );
        }

        if (
                mutualPunishment()
        ) {

            return List.of(
                    firstBranch,
                    secondBranch
            );
        }

        return List.of(
                firstBranch
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

        if (
                selfPunishment()
        ) {

            return branch == firstBranch;
        }

        if (
                mutualPunishment()
        ) {

            return branch == firstBranch
                    || branch == secondBranch;
        }

        return branch == firstBranch
                || branch == secondBranch
                || branch == thirdBranch;
    }

    /**
     * 세 지지가 해당 삼형의
     * 세 구성원을 정확히 갖추었는지.
     *
     * 입력 순서는 무관하다.
     */
    public boolean matchesCompleteThree(

            EarthlyBranch branch1,

            EarthlyBranch branch2,

            EarthlyBranch branch3

    ) {

        if (
                !threePunishment()
                        || branch1 == null
                        || branch2 == null
                        || branch3 == null
        ) {

            return false;
        }

        EnumSet<EarthlyBranch> actual =
                EnumSet.of(
                        branch1,
                        branch2,
                        branch3
                );

        if (
                actual.size() != 3
        ) {

            return false;
        }

        EnumSet<EarthlyBranch> expected =
                EnumSet.of(
                        firstBranch,
                        secondBranch,
                        thirdBranch
                );

        return actual.equals(
                expected
        );
    }

    /**
     * 삼형 계열의 서로 다른 구성원 두 지지인가.
     */
    public boolean matchesPartialThree(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        if (
                !threePunishment()
                        || branch1 == null
                        || branch2 == null
                        || branch1 == branch2
        ) {

            return false;
        }

        return contains(
                branch1
        )
                && contains(
                branch2
        );
    }

    /**
     * 子卯 상형 Pair인지.
     *
     * 순서는 무관하다.
     */
    public boolean matchesMutual(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        if (
                !mutualPunishment()
                        || branch1 == null
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

    /**
     * 동일 자형 지지 두 개가 실제로 존재하는가.
     */
    public boolean matchesSelf(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        if (
                !selfPunishment()
                        || branch1 == null
                        || branch2 == null
        ) {

            return false;
        }

        return branch1 == firstBranch
                && branch2 == firstBranch;
    }

    /**
     * 삼형 Pair의 전통적인 방향성을 확인한다.
     *
     * 寅 -> 巳
     * 巳 -> 申
     * 申 -> 寅
     *
     * 丑 -> 戌
     * 戌 -> 未
     * 未 -> 丑
     *
     * 子卯 상형은 상호 관계이므로
     * 양 방향 모두 true.
     *
     * 자형 역시 동일 지지끼리의 관계이므로
     * 같은 지지 Pair이면 true.
     */
    public boolean punishes(

            EarthlyBranch source,

            EarthlyBranch target

    ) {

        if (
                source == null
                        || target == null
        ) {

            return false;
        }

        if (
                this == YIN_SI_SHEN
        ) {

            return (
                    source == EarthlyBranch.YIN
                            && target == EarthlyBranch.SI
            )
                    || (
                    source == EarthlyBranch.SI
                            && target == EarthlyBranch.SHEN
            )
                    || (
                    source == EarthlyBranch.SHEN
                            && target == EarthlyBranch.YIN
            );
        }

        if (
                this == CHOU_XU_WEI
        ) {

            return (
                    source == EarthlyBranch.CHOU
                            && target == EarthlyBranch.XU
            )
                    || (
                    source == EarthlyBranch.XU
                            && target == EarthlyBranch.WEI
            )
                    || (
                    source == EarthlyBranch.WEI
                            && target == EarthlyBranch.CHOU
            );
        }

        if (
                mutualPunishment()
        ) {

            return matchesMutual(
                    source,
                    target
            );
        }

        if (
                selfPunishment()
        ) {

            return matchesSelf(
                    source,
                    target
            );
        }

        return false;
    }
}