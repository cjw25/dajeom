package com.dajeom.type;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 8차-4.
 *
 * 지지 삼합국(地支三合局).
 *
 * =================================================
 *
 * 申子辰 水局
 * 亥卯未 木局
 * 寅午戌 火局
 * 巳酉丑 金局
 *
 * =================================================
 *
 * 각 삼합국은:
 *
 * 생지
 * 왕지
 * 고지
 *
 * 세 역할을 가진다.
 *
 * =================================================
 *
 * nominalTransformationElement
 *
 * 삼합국이 명목상 지향하는 오행.
 *
 * 이 값만으로 실제 합화가 완료되었다고
 * 판단하지 않는다.
 *
 * 실제 국 성립 및 합화 여부는
 * 정밀화 9차에서 처리한다.
 */
public enum EarthlyBranchThreeCombinationType {

    SHEN_ZI_CHEN_WATER(
            "신자진 수국",
            EarthlyBranch.SHEN,
            EarthlyBranch.ZI,
            EarthlyBranch.CHEN,
            FiveElement.WATER
    ),

    HAI_MAO_WEI_WOOD(
            "해묘미 목국",
            EarthlyBranch.HAI,
            EarthlyBranch.MAO,
            EarthlyBranch.WEI,
            FiveElement.WOOD
    ),

    YIN_WU_XU_FIRE(
            "인오술 화국",
            EarthlyBranch.YIN,
            EarthlyBranch.WU,
            EarthlyBranch.XU,
            FiveElement.FIRE
    ),

    SI_YOU_CHOU_METAL(
            "사유축 금국",
            EarthlyBranch.SI,
            EarthlyBranch.YOU,
            EarthlyBranch.CHOU,
            FiveElement.METAL
    );

    private final String koreanName;

    private final EarthlyBranch birthBranch;

    private final EarthlyBranch cardinalBranch;

    private final EarthlyBranch storageBranch;

    private final FiveElement nominalTransformationElement;

    EarthlyBranchThreeCombinationType(

            String koreanName,

            EarthlyBranch birthBranch,

            EarthlyBranch cardinalBranch,

            EarthlyBranch storageBranch,

            FiveElement nominalTransformationElement

    ) {

        this.koreanName =
                Objects.requireNonNull(
                        koreanName
                );

        this.birthBranch =
                Objects.requireNonNull(
                        birthBranch
                );

        this.cardinalBranch =
                Objects.requireNonNull(
                        cardinalBranch
                );

        this.storageBranch =
                Objects.requireNonNull(
                        storageBranch
                );

        this.nominalTransformationElement =
                Objects.requireNonNull(
                        nominalTransformationElement
                );
    }

    public String koreanName() {

        return koreanName;
    }

    public EarthlyBranch birthBranch() {

        return birthBranch;
    }

    public EarthlyBranch cardinalBranch() {

        return cardinalBranch;
    }

    public EarthlyBranch storageBranch() {

        return storageBranch;
    }

    /**
     * 명목상 삼합국 오행.
     *
     * 실제 合化 완료 오행이 아니다.
     */
    public FiveElement nominalTransformationElement() {

        return nominalTransformationElement;
    }

    public List<EarthlyBranch> members() {

        return List.of(
                birthBranch,
                cardinalBranch,
                storageBranch
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

        return branch == birthBranch
                || branch == cardinalBranch
                || branch == storageBranch;
    }

    public EarthlyBranchThreeCombinationMemberRole roleOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        if (
                branch == birthBranch
        ) {

            return EarthlyBranchThreeCombinationMemberRole
                    .BIRTH;
        }

        if (
                branch == cardinalBranch
        ) {

            return EarthlyBranchThreeCombinationMemberRole
                    .CARDINAL;
        }

        if (
                branch == storageBranch
        ) {

            return EarthlyBranchThreeCombinationMemberRole
                    .STORAGE;
        }

        throw new IllegalArgumentException(
                "해당 지지는 이 삼합국의 구성원이 아닙니다: "
                        + branch
        );
    }

    /**
     * 세 지지가 정확히 이 삼합국 전체를
     * 구성하는지 확인한다.
     *
     * 입력 순서는 무관하다.
     */
    public boolean matchesComplete(

            EarthlyBranch branch1,

            EarthlyBranch branch2,

            EarthlyBranch branch3

    ) {

        if (
                branch1 == null
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
                        birthBranch,
                        cardinalBranch,
                        storageBranch
                );

        return actual.equals(
                expected
        );
    }

    /**
     * 두 지지가 이 삼합국의 서로 다른 구성원인지.
     */
    public boolean matchesPair(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        if (
                branch1 == null
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
     * 해당 Pair가 왕지를 포함하는가.
     */
    public boolean pairContainsCardinal(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        if (
                !matchesPair(
                        branch1,
                        branch2
                )
        ) {

            return false;
        }

        return branch1 == cardinalBranch
                || branch2 == cardinalBranch;
    }

    /**
     * 생지 + 고지 조합인가.
     *
     * 즉 왕지가 빠진 부분조합.
     */
    public boolean birthStoragePair(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        if (
                !matchesPair(
                        branch1,
                        branch2
                )
        ) {

            return false;
        }

        Set<EarthlyBranch> pair =
                EnumSet.of(
                        branch1,
                        branch2
                );

        return pair.equals(
                EnumSet.of(
                        birthBranch,
                        storageBranch
                )
        );
    }
}