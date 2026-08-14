package com.dajeom.type;

import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 17차-3.
 *
 * 지지 삼합의 네 기본 그룹.
 *
 * =================================================
 *
 * 申子辰
 * 寅午戌
 * 亥卯未
 * 巳酉丑
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Enum 자체는
 * "삼합이 실제 성립했다"는 판정을 하지 않는다.
 *
 * 신살 계산에서 특정 기준 지지가
 * 어느 그룹에 속하는지를 분류하기 위한
 * deterministic Lookup 단위다.
 *
 * 따라서:
 *
 * 원국에 申 하나만 있다고 해서
 * 申子辰 삼합이 성립했다고 해석하지 않는다.
 *
 * 단순히:
 *
 * 申은 申子辰 그룹에 속한다.
 *
 * 는 분류만 수행한다.
 */
public enum ThreeHarmonyBranchGroup {

    SHEN_ZI_CHEN(
            "申子辰",
            Set.of(
                    EarthlyBranch.SHEN,
                    EarthlyBranch.ZI,
                    EarthlyBranch.CHEN
            )
    ),

    YIN_WU_XU(
            "寅午戌",
            Set.of(
                    EarthlyBranch.YIN,
                    EarthlyBranch.WU,
                    EarthlyBranch.XU
            )
    ),

    HAI_MAO_WEI(
            "亥卯未",
            Set.of(
                    EarthlyBranch.HAI,
                    EarthlyBranch.MAO,
                    EarthlyBranch.WEI
            )
    ),

    SI_YOU_CHOU(
            "巳酉丑",
            Set.of(
                    EarthlyBranch.SI,
                    EarthlyBranch.YOU,
                    EarthlyBranch.CHOU
            )
    );

    private final String chineseName;

    private final Set<EarthlyBranch> branches;

    ThreeHarmonyBranchGroup(

            String chineseName,

            Set<EarthlyBranch> branches

    ) {

        this.chineseName =
                Objects.requireNonNull(
                        chineseName
                );

        this.branches =
                Set.copyOf(
                        Objects.requireNonNull(
                                branches
                        )
                );

        if (
                this.branches.size() != 3
        ) {

            throw new IllegalArgumentException(
                    "삼합 그룹은 정확히 3개의 지지를 가져야 합니다."
            );
        }
    }

    public String chineseName() {

        return chineseName;
    }

    public Set<EarthlyBranch> branches() {

        return branches;
    }

    public boolean contains(
            EarthlyBranch branch
    ) {

        return branch != null
                && branches.contains(
                branch
        );
    }
}