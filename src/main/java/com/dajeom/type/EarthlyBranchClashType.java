package com.dajeom.type;

import java.util.Objects;

/**
 * 정밀화 8차-6.
 *
 * 지지 육충(地支六沖).
 *
 * =================================================
 *
 * 子午沖
 * 丑未沖
 * 寅申沖
 * 卯酉沖
 * 辰戌沖
 * 巳亥沖
 *
 * =================================================
 *
 * 지지충은 기본적으로 Pair 관계다.
 *
 * 천간 극제처럼:
 *
 * A가 B를 극한다
 *
 * 라는 단일 방향으로 축약하지 않는다.
 *
 * 충 관계 자체를 양방향 구조 Evidence로 보존한다.
 *
 * =================================================
 *
 * 여기서 판단하지 않는 것:
 *
 * - 길흉
 * - 실제 사건 발생
 * - 어느 지지가 더 강한가
 * - 충으로 합이 깨지는가
 * - 충으로 지장간이 어떻게 변하는가
 * - 용신이 손상되는가
 *
 * 위 내용은 이후 관계 종합 및 운세 결합 단계에서 처리한다.
 */
public enum EarthlyBranchClashType {

    ZI_WU(
            "자오충",
            EarthlyBranch.ZI,
            EarthlyBranch.WU
    ),

    CHOU_WEI(
            "축미충",
            EarthlyBranch.CHOU,
            EarthlyBranch.WEI
    ),

    YIN_SHEN(
            "인신충",
            EarthlyBranch.YIN,
            EarthlyBranch.SHEN
    ),

    MAO_YOU(
            "묘유충",
            EarthlyBranch.MAO,
            EarthlyBranch.YOU
    ),

    CHEN_XU(
            "진술충",
            EarthlyBranch.CHEN,
            EarthlyBranch.XU
    ),

    SI_HAI(
            "사해충",
            EarthlyBranch.SI,
            EarthlyBranch.HAI
    );

    private final String koreanName;

    private final EarthlyBranch firstBranch;

    private final EarthlyBranch secondBranch;

    EarthlyBranchClashType(

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
     * 해당 육충 Pair인지 확인한다.
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
     * 해당 지지의 충 상대.
     *
     * 이 ClashType의 구성원이 아니면 null.
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