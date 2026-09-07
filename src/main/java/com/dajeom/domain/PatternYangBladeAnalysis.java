package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 전체의 陽刃 분석.
 *
 * =================================================
 *
 * 五陽干:
 *
 * 甲 → 卯
 * 丙 → 午
 * 戊 → 午
 * 庚 → 酉
 * 壬 → 子
 *
 * =================================================
 *
 * 五陰干:
 *
 * 현재 이 계층에서는 陽刃 없음.
 */
public record PatternYangBladeAnalysis(

        HeavenlyStem dayMaster,

        Optional<EarthlyBranch> bladeBranch,

        List<PatternYangBladeOccurrence> occurrences

) {

    public PatternYangBladeAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        bladeBranch =
                bladeBranch == null
                        ? Optional.empty()
                        : bladeBranch;

        occurrences =
                occurrences == null
                        ? List.of()
                        : List.copyOf(occurrences);

        for (
                PatternYangBladeOccurrence occurrence
                : occurrences
        ) {

            Objects.requireNonNull(
                    occurrence,
                    "陽刃 occurrence에 null이 포함될 수 없습니다."
            );

            if (bladeBranch.isEmpty()) {

                throw new IllegalArgumentException(
                        "陰日 또는 陽刃 비대상 일간에는 陽刃 occurrence가 존재할 수 없습니다."
                );
            }

            if (
                    occurrence.branch()
                            != bladeBranch.get()
            ) {

                throw new IllegalArgumentException(
                        "陽刃 occurrence의 지지가 해당 일간의 刃支와 일치하지 않습니다."
                );
            }
        }
    }

    public boolean yangBladeEligibleDayMaster() {

        return bladeBranch.isPresent();
    }

    public boolean hasYangBlade() {

        return !occurrences.isEmpty();
    }

    public boolean hasMonthBlade() {

        return occurrences
                .stream()
                .anyMatch(
                        PatternYangBladeOccurrence
                                ::monthBlade
                );
    }

    public List<PatternYangBladeOccurrence> occurrencesAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return occurrences
                .stream()
                .filter(
                        occurrence ->
                                occurrence.position()
                                        == position
                )
                .toList();
    }
}