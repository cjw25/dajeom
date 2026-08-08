package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 지지 관계 기본 테이블.
 *
 * 관계의 존재 여부만 판단한다.
 *
 * 해당 관계가 실제 원국에서
 * 얼마나 강하게 작용하는지,
 * 합화가 성립하는지,
 * 길흉이 어떻게 나타나는지는
 * 이후 Analyzer / Rule에서 별도로 판단한다.
 */
public final class BranchRelationTable {

    /**
     * 육합.
     */
    private static final Map<
            BranchPair,
            FiveElement
            > SIX_COMBINATIONS =
            Map.of(

                    pair(
                            EarthlyBranch.ZI,
                            EarthlyBranch.CHOU
                    ),
                    FiveElement.EARTH,

                    pair(
                            EarthlyBranch.YIN,
                            EarthlyBranch.HAI
                    ),
                    FiveElement.WOOD,

                    pair(
                            EarthlyBranch.MAO,
                            EarthlyBranch.XU
                    ),
                    FiveElement.FIRE,

                    pair(
                            EarthlyBranch.CHEN,
                            EarthlyBranch.YOU
                    ),
                    FiveElement.METAL,

                    pair(
                            EarthlyBranch.SI,
                            EarthlyBranch.SHEN
                    ),
                    FiveElement.WATER,

                    pair(
                            EarthlyBranch.WU,
                            EarthlyBranch.WEI
                    ),
                    FiveElement.EARTH
            );

    /**
     * 육충.
     */
    private static final Set<BranchPair>
            CLASHES =
            Set.of(

                    pair(
                            EarthlyBranch.ZI,
                            EarthlyBranch.WU
                    ),

                    pair(
                            EarthlyBranch.CHOU,
                            EarthlyBranch.WEI
                    ),

                    pair(
                            EarthlyBranch.YIN,
                            EarthlyBranch.SHEN
                    ),

                    pair(
                            EarthlyBranch.MAO,
                            EarthlyBranch.YOU
                    ),

                    pair(
                            EarthlyBranch.CHEN,
                            EarthlyBranch.XU
                    ),

                    pair(
                            EarthlyBranch.SI,
                            EarthlyBranch.HAI
                    )
            );

    /**
     * 육해.
     */
    private static final Set<BranchPair>
            HARMS =
            Set.of(

                    pair(
                            EarthlyBranch.ZI,
                            EarthlyBranch.WEI
                    ),

                    pair(
                            EarthlyBranch.CHOU,
                            EarthlyBranch.WU
                    ),

                    pair(
                            EarthlyBranch.YIN,
                            EarthlyBranch.SI
                    ),

                    pair(
                            EarthlyBranch.MAO,
                            EarthlyBranch.CHEN
                    ),

                    pair(
                            EarthlyBranch.SHEN,
                            EarthlyBranch.HAI
                    ),

                    pair(
                            EarthlyBranch.YOU,
                            EarthlyBranch.XU
                    )
            );

    /**
     * 육파.
     */
    private static final Set<BranchPair>
            BREAKS =
            Set.of(

                    pair(
                            EarthlyBranch.ZI,
                            EarthlyBranch.YOU
                    ),

                    pair(
                            EarthlyBranch.CHOU,
                            EarthlyBranch.CHEN
                    ),

                    pair(
                            EarthlyBranch.YIN,
                            EarthlyBranch.HAI
                    ),

                    pair(
                            EarthlyBranch.MAO,
                            EarthlyBranch.WU
                    ),

                    pair(
                            EarthlyBranch.SI,
                            EarthlyBranch.SHEN
                    ),

                    pair(
                            EarthlyBranch.WEI,
                            EarthlyBranch.XU
                    )
            );

    /**
     * 자묘형.
     *
     * V1에서는 子卯 관계를
     * 두 지지 형으로 구분한다.
     */
    private static final Set<BranchPair>
            PAIR_PUNISHMENTS =
            Set.of(

                    pair(
                            EarthlyBranch.ZI,
                            EarthlyBranch.MAO
                    )
            );

    /**
     * 자형.
     */
    private static final Set<EarthlyBranch>
            SELF_PUNISHMENTS =
            EnumSet.of(

                    EarthlyBranch.CHEN,

                    EarthlyBranch.WU,

                    EarthlyBranch.YOU,

                    EarthlyBranch.HAI
            );

    /**
     * 삼합.
     */
    private static final Map<
            Set<EarthlyBranch>,
            FiveElement
            > THREE_HARMONIES =
            Map.of(

                    group(
                            EarthlyBranch.SHEN,
                            EarthlyBranch.ZI,
                            EarthlyBranch.CHEN
                    ),
                    FiveElement.WATER,

                    group(
                            EarthlyBranch.HAI,
                            EarthlyBranch.MAO,
                            EarthlyBranch.WEI
                    ),
                    FiveElement.WOOD,

                    group(
                            EarthlyBranch.YIN,
                            EarthlyBranch.WU,
                            EarthlyBranch.XU
                    ),
                    FiveElement.FIRE,

                    group(
                            EarthlyBranch.SI,
                            EarthlyBranch.YOU,
                            EarthlyBranch.CHOU
                    ),
                    FiveElement.METAL
            );

    /**
     * 삼형.
     *
     * V1에서는 완성된 3지지 조합을 우선 탐지한다.
     *
     * 寅巳申
     * 丑戌未
     *
     * 두 글자만 있는 부분형을
     * 무조건 동일한 강도로 판정하지 않는다.
     */
    private static final Set<
            Set<EarthlyBranch>
            > THREE_PUNISHMENTS =
            Set.of(

                    group(
                            EarthlyBranch.YIN,
                            EarthlyBranch.SI,
                            EarthlyBranch.SHEN
                    ),

                    group(
                            EarthlyBranch.CHOU,
                            EarthlyBranch.XU,
                            EarthlyBranch.WEI
                    )
            );

    private BranchRelationTable() {

        throw new IllegalStateException(
                "BranchRelationTable은 생성할 수 없습니다."
        );
    }

    public static Optional<FiveElement>
    findSixCombinationElement(
            EarthlyBranch first,
            EarthlyBranch second
    ) {

        validatePair(
                first,
                second
        );

        return Optional.ofNullable(
                SIX_COMBINATIONS.get(
                        pair(
                                first,
                                second
                        )
                )
        );
    }

    public static boolean isClash(
            EarthlyBranch first,
            EarthlyBranch second
    ) {

        validatePair(
                first,
                second
        );

        return CLASHES.contains(
                pair(
                        first,
                        second
                )
        );
    }

    public static boolean isHarm(
            EarthlyBranch first,
            EarthlyBranch second
    ) {

        validatePair(
                first,
                second
        );

        return HARMS.contains(
                pair(
                        first,
                        second
                )
        );
    }

    public static boolean isBreak(
            EarthlyBranch first,
            EarthlyBranch second
    ) {

        validatePair(
                first,
                second
        );

        return BREAKS.contains(
                pair(
                        first,
                        second
                )
        );
    }

    public static boolean isPairPunishment(
            EarthlyBranch first,
            EarthlyBranch second
    ) {

        validatePair(
                first,
                second
        );

        return PAIR_PUNISHMENTS.contains(
                pair(
                        first,
                        second
                )
        );
    }

    public static boolean isSelfPunishmentBranch(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        return SELF_PUNISHMENTS.contains(
                branch
        );
    }

    public static Optional<FiveElement>
    findThreeHarmonyElement(
            EarthlyBranch first,
            EarthlyBranch second,
            EarthlyBranch third
    ) {

        validateTriple(
                first,
                second,
                third
        );

        Set<EarthlyBranch> group =
                group(
                        first,
                        second,
                        third
                );

        if (
                group.size() != 3
        ) {

            return Optional.empty();
        }

        return Optional.ofNullable(
                THREE_HARMONIES.get(
                        group
                )
        );
    }

    public static boolean isThreePunishment(
            EarthlyBranch first,
            EarthlyBranch second,
            EarthlyBranch third
    ) {

        validateTriple(
                first,
                second,
                third
        );

        Set<EarthlyBranch> group =
                group(
                        first,
                        second,
                        third
                );

        if (
                group.size() != 3
        ) {

            return false;
        }

        return THREE_PUNISHMENTS.contains(
                group
        );
    }

    private static void validatePair(
            EarthlyBranch first,
            EarthlyBranch second
    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 지지는 필수입니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 지지는 필수입니다."
        );
    }

    private static void validateTriple(
            EarthlyBranch first,
            EarthlyBranch second,
            EarthlyBranch third
    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 지지는 필수입니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 지지는 필수입니다."
        );

        Objects.requireNonNull(
                third,
                "세 번째 지지는 필수입니다."
        );
    }

    private static BranchPair pair(
            EarthlyBranch first,
            EarthlyBranch second
    ) {

        return new BranchPair(
                first,
                second
        );
    }

    private static Set<EarthlyBranch> group(
            EarthlyBranch first,
            EarthlyBranch second,
            EarthlyBranch third
    ) {

        return EnumSet.of(
                first,
                second,
                third
        );
    }

    /**
     * 순서가 달라도 동일한 관계가 되도록
     * index 기준으로 정규화한다.
     */
    private record BranchPair(

            EarthlyBranch first,

            EarthlyBranch second

    ) {

        private BranchPair {

            Objects.requireNonNull(
                    first
            );

            Objects.requireNonNull(
                    second
            );

            if (
                    first.getIndex()
                            > second.getIndex()
            ) {

                EarthlyBranch temp =
                        first;

                first =
                        second;

                second =
                        temp;
            }
        }
    }
}