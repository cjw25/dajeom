package com.dajeom.domain;

import java.util.Objects;

/**
 * 원국 한 위치에 실제 존재하는 陽刃.
 *
 * =================================================
 *
 * 예:
 *
 * 甲日
 * 月支 卯
 *
 * 卯中乙 = 劫財
 *
 * →
 *
 * position = MONTH
 * branch = MAO
 * robWealthHiddenStem = 卯中乙 node
 *
 * =================================================
 */
public record PatternYangBladeOccurrence(

        PillarPosition position,

        EarthlyBranch branch,

        PatternTenGodNode robWealthHiddenStem

) {

    public PatternYangBladeOccurrence {

        Objects.requireNonNull(
                position,
                "陽刃 위치는 필수입니다."
        );

        Objects.requireNonNull(
                branch,
                "陽刃 지지는 필수입니다."
        );

        Objects.requireNonNull(
                robWealthHiddenStem,
                "陽刃 속 劫財 지장간 노드는 필수입니다."
        );

        if (!robWealthHiddenStem.hidden()) {

            throw new IllegalArgumentException(
                    "陽刃의 劫財 노드는 지장간이어야 합니다."
            );
        }

        if (
                robWealthHiddenStem.position()
                        != position
        ) {

            throw new IllegalArgumentException(
                    "陽刃 위치와 劫財 지장간 위치가 일치하지 않습니다."
            );
        }

        EarthlyBranch nodeBranch =
                robWealthHiddenStem
                        .branch()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "陽刃 劫財 지장간에 지지 정보가 없습니다."
                                        )
                        );

        if (nodeBranch != branch) {

            throw new IllegalArgumentException(
                    "陽刃 지지와 劫財 지장간의 지지가 일치하지 않습니다."
            );
        }

        if (
                !robWealthHiddenStem.hasTenGodType(
                        MonthCommandPatternCandidateType
                                .MONTH_ROB_WEALTH
                )
        ) {

            throw new IllegalArgumentException(
                    "陽刃 속 핵심 지장간은 일간 기준 劫財여야 합니다."
            );
        }
    }

    public boolean monthBlade() {

        return position
                == PillarPosition.MONTH;
    }

    public HeavenlyStem bladeRobStem() {

        return robWealthHiddenStem.stem();
    }
}