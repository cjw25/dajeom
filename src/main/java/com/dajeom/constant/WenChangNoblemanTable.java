package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 17차-2.
 *
 * 문창귀인 deterministic Table.
 *
 * =================================================
 *
 * 다점 Convention:
 *
 * 원국 일간을 기준으로
 * 문창귀인 대상 지지를 찾는다.
 *
 * =================================================
 *
 * 甲 -> 巳
 * 乙 -> 午
 * 丙 -> 申
 * 丁 -> 酉
 * 戊 -> 申
 * 己 -> 酉
 * 庚 -> 亥
 * 辛 -> 子
 * 壬 -> 寅
 * 癸 -> 卯
 *
 * =================================================
 *
 * 문창귀인 역시 보조 Evidence이며:
 *
 * - 학업 성공 확정
 * - 시험 합격 확정
 * - 지능 점수 상승
 *
 * 같은 판정을 이 Table에서 하지 않는다.
 */
public final class WenChangNoblemanTable {

    private static final Map<
            HeavenlyStem,
            EarthlyBranch
            > TABLE =
            Map.ofEntries(

                    Map.entry(
                            HeavenlyStem.JIA,
                            EarthlyBranch.SI
                    ),

                    Map.entry(
                            HeavenlyStem.YI,
                            EarthlyBranch.WU
                    ),

                    Map.entry(
                            HeavenlyStem.BING,
                            EarthlyBranch.SHEN
                    ),

                    Map.entry(
                            HeavenlyStem.DING,
                            EarthlyBranch.YOU
                    ),

                    Map.entry(
                            HeavenlyStem.WU,
                            EarthlyBranch.SHEN
                    ),

                    Map.entry(
                            HeavenlyStem.JI,
                            EarthlyBranch.YOU
                    ),

                    Map.entry(
                            HeavenlyStem.GENG,
                            EarthlyBranch.HAI
                    ),

                    Map.entry(
                            HeavenlyStem.XIN,
                            EarthlyBranch.ZI
                    ),

                    Map.entry(
                            HeavenlyStem.REN,
                            EarthlyBranch.YIN
                    ),

                    Map.entry(
                            HeavenlyStem.GUI,
                            EarthlyBranch.MAO
                    )
            );

    private WenChangNoblemanTable() {
    }

    /**
     * 해당 일간의 문창귀인 대상 지지.
     */
    public static EarthlyBranch targetBranch(
            HeavenlyStem dayMaster
    ) {

        Objects.requireNonNull(
                dayMaster,
                "문창귀인 계산에는 일간이 필요합니다."
        );

        EarthlyBranch branch =
                TABLE.get(
                        dayMaster
                );

        if (
                branch == null
        ) {

            throw new IllegalStateException(
                    "문창귀인 Table에 일간이 등록되어 있지 않습니다: "
                            + dayMaster
            );
        }

        return branch;
    }

    /**
     * 특정 지지가 문창귀인 대상인지.
     */
    public static boolean matches(

            HeavenlyStem dayMaster,

            EarthlyBranch branch

    ) {

        Objects.requireNonNull(
                branch,
                "문창귀인 검사에는 지지가 필요합니다."
        );

        return targetBranch(
                dayMaster
        )
                == branch;
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
            EarthlyBranch
            > table() {

        return TABLE;
    }
}