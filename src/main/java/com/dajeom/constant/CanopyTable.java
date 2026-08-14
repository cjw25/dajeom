package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.ThreeHarmonyBranchGroup;

import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 17차-3.
 *
 * 화개 deterministic Table.
 *
 * =================================================
 *
 * 다점 Convention:
 *
 * 申子辰 -> 辰
 * 寅午戌 -> 戌
 * 亥卯未 -> 未
 * 巳酉丑 -> 丑
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
 * 화개가 있다고:
 *
 * 종교인
 * 예술가
 * 고독
 * 출가
 *
 * 등을 단정하지 않는다.
 *
 * 신살 Match만 보존한다.
 */
public final class CanopyTable {

    private static final Map<
            ThreeHarmonyBranchGroup,
            EarthlyBranch
            > TABLE =
            Map.of(

                    ThreeHarmonyBranchGroup.SHEN_ZI_CHEN,
                    EarthlyBranch.CHEN,

                    ThreeHarmonyBranchGroup.YIN_WU_XU,
                    EarthlyBranch.XU,

                    ThreeHarmonyBranchGroup.HAI_MAO_WEI,
                    EarthlyBranch.WEI,

                    ThreeHarmonyBranchGroup.SI_YOU_CHOU,
                    EarthlyBranch.CHOU
            );

    private CanopyTable() {
    }

    public static EarthlyBranch targetBranch(
            ThreeHarmonyBranchGroup group
    ) {

        Objects.requireNonNull(
                group,
                "화개 계산에는 삼합 그룹이 필요합니다."
        );

        EarthlyBranch target =
                TABLE.get(
                        group
                );

        if (
                target == null
        ) {

            throw new IllegalStateException(
                    "화개 Table에 등록되지 않은 삼합 그룹입니다: "
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
                "화개 검사 대상 지지가 필요합니다."
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