package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.ThreeHarmonyBranchGroup;

import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 17차-3.
 *
 * 도화 / 咸池 deterministic Table.
 *
 * =================================================
 *
 * 다점 Convention:
 *
 * 申子辰 -> 酉
 * 寅午戌 -> 卯
 * 亥卯未 -> 子
 * 巳酉丑 -> 午
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
 * 도화 Match 자체로:
 *
 * 연애 발생
 * 외도
 * 인기
 * 성적 성향
 *
 * 등을 deterministic하게 확정하지 않는다.
 *
 * 오직 신살 조건 Match만 생성한다.
 */
public final class PeachBlossomTable {

    private static final Map<
            ThreeHarmonyBranchGroup,
            EarthlyBranch
            > TABLE =
            Map.of(

                    ThreeHarmonyBranchGroup.SHEN_ZI_CHEN,
                    EarthlyBranch.YOU,

                    ThreeHarmonyBranchGroup.YIN_WU_XU,
                    EarthlyBranch.MAO,

                    ThreeHarmonyBranchGroup.HAI_MAO_WEI,
                    EarthlyBranch.ZI,

                    ThreeHarmonyBranchGroup.SI_YOU_CHOU,
                    EarthlyBranch.WU
            );

    private PeachBlossomTable() {
    }

    public static EarthlyBranch targetBranch(
            ThreeHarmonyBranchGroup group
    ) {

        Objects.requireNonNull(
                group,
                "도화 계산에는 삼합 그룹이 필요합니다."
        );

        EarthlyBranch target =
                TABLE.get(
                        group
                );

        if (
                target == null
        ) {

            throw new IllegalStateException(
                    "도화 Table에 등록되지 않은 삼합 그룹입니다: "
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
                "도화 검사 대상 지지가 필요합니다."
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