package com.dajeom.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 원국의 六合 / 三合 / 三會 분석 결과.
 *
 * =================================================
 *
 * 여기서 확정:
 *
 * - 六合 pair 존재
 * - 완전한 三合 세 글자 존재
 * - 완전한 三會 세 글자 존재
 * - 월령이 회합에 참여하는가
 * - 三合/三會 局 오행
 *
 * =================================================
 *
 * 여기서 확정하지 않음:
 *
 * - 合化
 * - 合去
 * - 半合
 * - 拱合
 * - 합이 충을 실제 완전히 제거하는가
 * - 합과 충이 병존할 때 무엇이 우선하는가
 *
 * =================================================
 *
 * 숫자 / 점수 / 확률 없음.
 */
public record PatternBranchCombinationAnalysis(

        Map<PillarPosition, EarthlyBranch> branches,

        List<PatternBranchCombinationOccurrence> combinations

) {

    public PatternBranchCombinationAnalysis {

        branches =
                branches == null
                        ? Map.of()
                        : Map.copyOf(branches);

        combinations =
                combinations == null
                        ? List.of()
                        : List.copyOf(combinations);

        for (
                PillarPosition position
                : PillarPosition.values()
        ) {

            if (!branches.containsKey(position)) {

                throw new IllegalArgumentException(
                        "지지 회합 분석에는 YEAR/MONTH/DAY/HOUR 네 지지가 모두 필요합니다."
                );
            }

            Objects.requireNonNull(
                    branches.get(position),
                    "지지는 null일 수 없습니다."
            );
        }

        for (
                PatternBranchCombinationOccurrence occurrence
                : combinations
        ) {

            Objects.requireNonNull(
                    occurrence,
                    "회합 목록에 null이 포함될 수 없습니다."
            );

            for (
                    PillarPosition position
                    : occurrence.positions()
            ) {

                EarthlyBranch expected =
                        branches.get(position);

                EarthlyBranch actual =
                        occurrence.branchAt(position);

                if (expected != actual) {

                    throw new IllegalArgumentException(
                            "회합 occurrence의 지지가 원국 지지와 일치하지 않습니다."
                    );
                }
            }
        }
    }

    public EarthlyBranch monthBranch() {

        return branches.get(
                PillarPosition.MONTH
        );
    }

    public boolean hasAnyCombination() {

        return !combinations.isEmpty();
    }

    public List<PatternBranchCombinationOccurrence>
    sixHarmonyOccurrences() {

        return combinations
                .stream()
                .filter(
                        PatternBranchCombinationOccurrence
                                ::sixHarmony
                )
                .toList();
    }

    public List<PatternBranchCombinationOccurrence>
    completeThreeHarmonyOccurrences() {

        return combinations
                .stream()
                .filter(
                        PatternBranchCombinationOccurrence
                                ::completeThreeHarmony
                )
                .toList();
    }

    public List<PatternBranchCombinationOccurrence>
    completeThreeMeetingOccurrences() {

        return combinations
                .stream()
                .filter(
                        PatternBranchCombinationOccurrence
                                ::completeThreeMeeting
                )
                .toList();
    }

    /*
     * =================================================
     * 월령 중심 조회
     * =================================================
     */

    public List<PatternBranchCombinationOccurrence>
    monthCommandCombinations() {

        return combinations
                .stream()
                .filter(
                        PatternBranchCombinationOccurrence
                                ::involvesMonthCommand
                )
                .toList();
    }

    public List<PatternBranchCombinationOccurrence>
    monthCommandSixHarmonyOccurrences() {

        return monthCommandCombinations()
                .stream()
                .filter(
                        PatternBranchCombinationOccurrence
                                ::sixHarmony
                )
                .toList();
    }

    public List<PatternBranchCombinationOccurrence>
    monthCommandCompleteThreeHarmonyOccurrences() {

        return monthCommandCombinations()
                .stream()
                .filter(
                        PatternBranchCombinationOccurrence
                                ::completeThreeHarmony
                )
                .toList();
    }

    public List<PatternBranchCombinationOccurrence>
    monthCommandCompleteThreeMeetingOccurrences() {

        return monthCommandCombinations()
                .stream()
                .filter(
                        PatternBranchCombinationOccurrence
                                ::completeThreeMeeting
                )
                .toList();
    }

    public boolean monthCommandHasSixHarmony() {

        return !monthCommandSixHarmonyOccurrences()
                .isEmpty();
    }

    public boolean monthCommandInCompleteThreeHarmony() {

        return !monthCommandCompleteThreeHarmonyOccurrences()
                .isEmpty();
    }

    public boolean monthCommandInCompleteThreeMeeting() {

        return !monthCommandCompleteThreeMeetingOccurrences()
                .isEmpty();
    }

    public boolean monthCommandInAnyCombination() {

        return monthCommandHasSixHarmony()
                ||
                monthCommandInCompleteThreeHarmony()
                ||
                monthCommandInCompleteThreeMeeting();
    }
}