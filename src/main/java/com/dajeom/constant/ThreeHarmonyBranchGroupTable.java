package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.ThreeHarmonyBranchGroup;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 17차-3.
 *
 * EarthlyBranch
 * ->
 * ThreeHarmonyBranchGroup
 *
 * 공통 Lookup Table.
 *
 * =================================================
 *
 * 역마
 * 도화
 * 화개
 *
 * 세 Rule이 동일한 그룹 분류표를 공유한다.
 *
 * =================================================
 *
 * Rule마다:
 *
 * switch(branch)
 *
 * 를 다시 작성하지 않는다.
 */
public final class ThreeHarmonyBranchGroupTable {

    private static final Map<
            EarthlyBranch,
            ThreeHarmonyBranchGroup
            > TABLE =
            createTable();

    private ThreeHarmonyBranchGroupTable() {
    }

    private static Map<
            EarthlyBranch,
            ThreeHarmonyBranchGroup
            > createTable() {

        EnumMap<
                EarthlyBranch,
                ThreeHarmonyBranchGroup
                > table =
                new EnumMap<>(
                        EarthlyBranch.class
                );

        for (
                ThreeHarmonyBranchGroup group
                : ThreeHarmonyBranchGroup.values()
        ) {

            for (
                    EarthlyBranch branch
                    : group.branches()
            ) {

                ThreeHarmonyBranchGroup previous =
                        table.put(
                                branch,
                                group
                        );

                if (
                        previous != null
                ) {

                    throw new IllegalStateException(
                            "하나의 지지가 두 삼합 그룹에 중복 등록되었습니다: "
                                    + branch
                    );
                }
            }
        }

        if (
                table.size()
                        != EarthlyBranch.values().length
        ) {

            throw new IllegalStateException(
                    "12지지 전체가 삼합 그룹 Table에 등록되어야 합니다."
            );
        }

        return Map.copyOf(
                table
        );
    }

    public static ThreeHarmonyBranchGroup groupOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "삼합 그룹 조회에는 EarthlyBranch가 필요합니다."
        );

        ThreeHarmonyBranchGroup group =
                TABLE.get(
                        branch
                );

        if (
                group == null
        ) {

            throw new IllegalStateException(
                    "삼합 그룹에 등록되지 않은 지지입니다: "
                            + branch
            );
        }

        return group;
    }

    public static boolean belongsTo(

            EarthlyBranch branch,

            ThreeHarmonyBranchGroup group

    ) {

        Objects.requireNonNull(
                group,
                "ThreeHarmonyBranchGroup이 필요합니다."
        );

        return groupOf(
                branch
        )
                == group;
    }

    public static Map<
            EarthlyBranch,
            ThreeHarmonyBranchGroup
            > table() {

        return TABLE;
    }
}