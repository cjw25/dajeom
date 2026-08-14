package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.ThreeHarmonyBranchGroup;

import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 17차-3.
 *
 * 역마 deterministic Table.
 *
 * =================================================
 *
 * 다점 Convention:
 *
 * 申子辰 -> 寅
 * 寅午戌 -> 申
 * 亥卯未 -> 巳
 * 巳酉丑 -> 亥
 *
 * =================================================
 *
 * 기준:
 *
 * 원국 YEAR Branch
 * 또는
 * 원국 DAY Branch
 *
 * =================================================
 *
 * 이 Table은 위치나 길흉을 판단하지 않는다.
 */
public final class TravelHorseTable {

    private static final Map<
            ThreeHarmonyBranchGroup,
            EarthlyBranch
            > TABLE =
            Map.of(

                    ThreeHarmonyBranchGroup.SHEN_ZI_CHEN,
                    EarthlyBranch.YIN,

                    ThreeHarmonyBranchGroup.YIN_WU_XU,
                    EarthlyBranch.SHEN,

                    ThreeHarmonyBranchGroup.HAI_MAO_WEI,
                    EarthlyBranch.SI,

                    ThreeHarmonyBranchGroup.SI_YOU_CHOU,
                    EarthlyBranch.HAI
            );

    private TravelHorseTable() {
    }

    public static EarthlyBranch targetBranch(
            ThreeHarmonyBranchGroup group
    ) {

        Objects.requireNonNull(
                group,
                "역마 계산에는 삼합 그룹이 필요합니다."
        );

        EarthlyBranch target =
                TABLE.get(
                        group
                );

        if (
                target == null
        ) {

            throw new IllegalStateException(
                    "역마 Table에 등록되지 않은 삼합 그룹입니다: "
                            + group
            );
        }

        return target;
    }

    public static EarthlyBranch targetBranch(
            EarthlyBranch referenceBranch
    ) {

        return targetBranch(
                ThreeHarmonyBranchGroupTable
                        .groupOf(
                                referenceBranch
                        )
        );
    }

    public static boolean matches(

            EarthlyBranch referenceBranch,

            EarthlyBranch candidateBranch

    ) {

        Objects.requireNonNull(
                candidateBranch,
                "역마 검사 대상 지지가 필요합니다."
        );

        return targetBranch(
                referenceBranch
        )
                == candidateBranch;
    }

    public static Map<
            ThreeHarmonyBranchGroup,
            EarthlyBranch
            > table() {

        return TABLE;
    }
}