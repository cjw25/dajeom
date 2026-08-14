package com.dajeom.type;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 8차-5.
 *
 * 지지 방합 / 방국.
 *
 * =================================================
 *
 * 寅卯辰 木方
 * 巳午未 火方
 * 申酉戌 金方
 * 亥子丑 水方
 *
 * =================================================
 *
 * 각 方은:
 *
 * OPENING
 * CARDINAL
 * CLOSING
 *
 * 세 구성원을 가진다.
 *
 * =================================================
 *
 * nominalTransformationElement
 *
 * 해당 方이 명목상 형성하는 오행.
 *
 * 실제로 원래 지지의 성질이 사라지고
 * 해당 오행으로 완전히 변화했다는 의미가 아니다.
 *
 * 실제 성립도·합화 여부는 9차에서 검토한다.
 */
public enum EarthlyBranchDirectionalCombinationType {

    YIN_MAO_CHEN_WOOD(
            "인묘진 목방",
            EarthlyBranch.YIN,
            EarthlyBranch.MAO,
            EarthlyBranch.CHEN,
            FiveElement.WOOD
    ),

    SI_WU_WEI_FIRE(
            "사오미 화방",
            EarthlyBranch.SI,
            EarthlyBranch.WU,
            EarthlyBranch.WEI,
            FiveElement.FIRE
    ),

    SHEN_YOU_XU_METAL(
            "신유술 금방",
            EarthlyBranch.SHEN,
            EarthlyBranch.YOU,
            EarthlyBranch.XU,
            FiveElement.METAL
    ),

    HAI_ZI_CHOU_WATER(
            "해자축 수방",
            EarthlyBranch.HAI,
            EarthlyBranch.ZI,
            EarthlyBranch.CHOU,
            FiveElement.WATER
    );

    private final String koreanName;

    private final EarthlyBranch openingBranch;

    private final EarthlyBranch cardinalBranch;

    private final EarthlyBranch closingBranch;

    private final FiveElement nominalTransformationElement;

    EarthlyBranchDirectionalCombinationType(

            String koreanName,

            EarthlyBranch openingBranch,

            EarthlyBranch cardinalBranch,

            EarthlyBranch closingBranch,

            FiveElement nominalTransformationElement

    ) {

        this.koreanName =
                Objects.requireNonNull(
                        koreanName
                );

        this.openingBranch =
                Objects.requireNonNull(
                        openingBranch
                );

        this.cardinalBranch =
                Objects.requireNonNull(
                        cardinalBranch
                );

        this.closingBranch =
                Objects.requireNonNull(
                        closingBranch
                );

        this.nominalTransformationElement =
                Objects.requireNonNull(
                        nominalTransformationElement
                );
    }

    public String koreanName() {

        return koreanName;
    }

    public EarthlyBranch openingBranch() {

        return openingBranch;
    }

    public EarthlyBranch cardinalBranch() {

        return cardinalBranch;
    }

    public EarthlyBranch closingBranch() {

        return closingBranch;
    }

    public FiveElement nominalTransformationElement() {

        return nominalTransformationElement;
    }

    public List<EarthlyBranch> members() {

        return List.of(
                openingBranch,
                cardinalBranch,
                closingBranch
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

        return branch == openingBranch
                || branch == cardinalBranch
                || branch == closingBranch;
    }

    public EarthlyBranchDirectionalCombinationMemberRole roleOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        if (
                branch == openingBranch
        ) {

            return EarthlyBranchDirectionalCombinationMemberRole
                    .OPENING;
        }

        if (
                branch == cardinalBranch
        ) {

            return EarthlyBranchDirectionalCombinationMemberRole
                    .CARDINAL;
        }

        if (
                branch == closingBranch
        ) {

            return EarthlyBranchDirectionalCombinationMemberRole
                    .CLOSING;
        }

        throw new IllegalArgumentException(
                "해당 지지는 이 방합의 구성원이 아닙니다: "
                        + branch
        );
    }

    /**
     * 세 지지가 정확히 해당 方 전체를 구성하는가.
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
                        openingBranch,
                        cardinalBranch,
                        closingBranch
                );

        return actual.equals(
                expected
        );
    }

    /**
     * 서로 다른 두 지지가
     * 동일 方의 구성원인지.
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
     * 왕지가 빠진:
     *
     * 계절 시작 + 계절 마무리
     *
     * Pair인지.
     */
    public boolean openingClosingPair(

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
                        openingBranch,
                        closingBranch
                )
        );
    }
}