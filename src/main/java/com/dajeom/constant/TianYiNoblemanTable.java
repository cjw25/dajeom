package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 17차-2.
 *
 * 천을귀인 deterministic Table.
 *
 * =================================================
 *
 * 다점 Convention:
 *
 * 원국 일간을 기준으로
 * 천을귀인 대상 지지를 찾는다.
 *
 * =================================================
 *
 * 甲 -> 丑, 未
 * 乙 -> 子, 申
 * 丙 -> 亥, 酉
 * 丁 -> 亥, 酉
 * 戊 -> 丑, 未
 * 己 -> 子, 申
 * 庚 -> 丑, 未
 * 辛 -> 寅, 午
 * 壬 -> 卯, 巳
 * 癸 -> 卯, 巳
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Table은:
 *
 * - 길흉 판단을 하지 않는다.
 * - 점수를 만들지 않는다.
 * - 원국/대운/세운/월운을 구분하지 않는다.
 *
 * 단순히:
 *
 * "이 일간의 천을귀인 대상 지지가 무엇인가"
 *
 * 만 제공한다.
 */
public final class TianYiNoblemanTable {

    private static final Map<
            HeavenlyStem,
            Set<EarthlyBranch>
            > TABLE =
            Map.ofEntries(

                    Map.entry(
                            HeavenlyStem.JIA,
                            Set.of(
                                    EarthlyBranch.CHOU,
                                    EarthlyBranch.WEI
                            )
                    ),

                    Map.entry(
                            HeavenlyStem.YI,
                            Set.of(
                                    EarthlyBranch.ZI,
                                    EarthlyBranch.SHEN
                            )
                    ),

                    Map.entry(
                            HeavenlyStem.BING,
                            Set.of(
                                    EarthlyBranch.HAI,
                                    EarthlyBranch.YOU
                            )
                    ),

                    Map.entry(
                            HeavenlyStem.DING,
                            Set.of(
                                    EarthlyBranch.HAI,
                                    EarthlyBranch.YOU
                            )
                    ),

                    Map.entry(
                            HeavenlyStem.WU,
                            Set.of(
                                    EarthlyBranch.CHOU,
                                    EarthlyBranch.WEI
                            )
                    ),

                    Map.entry(
                            HeavenlyStem.JI,
                            Set.of(
                                    EarthlyBranch.ZI,
                                    EarthlyBranch.SHEN
                            )
                    ),

                    Map.entry(
                            HeavenlyStem.GENG,
                            Set.of(
                                    EarthlyBranch.CHOU,
                                    EarthlyBranch.WEI
                            )
                    ),

                    Map.entry(
                            HeavenlyStem.XIN,
                            Set.of(
                                    EarthlyBranch.YIN,
                                    EarthlyBranch.WU
                            )
                    ),

                    Map.entry(
                            HeavenlyStem.REN,
                            Set.of(
                                    EarthlyBranch.MAO,
                                    EarthlyBranch.SI
                            )
                    ),

                    Map.entry(
                            HeavenlyStem.GUI,
                            Set.of(
                                    EarthlyBranch.MAO,
                                    EarthlyBranch.SI
                            )
                    )
            );

    private TianYiNoblemanTable() {
    }

    /**
     * 해당 일간의 천을귀인 대상 지지.
     */
    public static Set<EarthlyBranch> targetBranches(
            HeavenlyStem dayMaster
    ) {

        Objects.requireNonNull(
                dayMaster,
                "천을귀인 계산에는 일간이 필요합니다."
        );

        Set<EarthlyBranch> branches =
                TABLE.get(
                        dayMaster
                );

        if (
                branches == null
        ) {

            throw new IllegalStateException(
                    "천을귀인 Table에 일간이 등록되어 있지 않습니다: "
                            + dayMaster
            );
        }

        return branches;
    }

    /**
     * 특정 지지가 천을귀인 대상인지.
     */
    public static boolean matches(

            HeavenlyStem dayMaster,

            EarthlyBranch branch

    ) {

        Objects.requireNonNull(
                branch,
                "천을귀인 검사에는 지지가 필요합니다."
        );

        return targetBranches(
                dayMaster
        )
                .contains(
                        branch
                );
    }

    public static boolean supports(
            HeavenlyStem dayMaster
    ) {

        return dayMaster != null
                && TABLE.containsKey(
                dayMaster
        );
    }

    public static Map<
            HeavenlyStem,
            Set<EarthlyBranch>
            > table() {

        return TABLE;
    }
}