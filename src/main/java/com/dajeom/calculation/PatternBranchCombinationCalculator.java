package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.PatternBranchCombinationAnalysis;
import com.dajeom.domain.PatternBranchCombinationKind;
import com.dajeom.domain.PatternBranchCombinationOccurrence;
import com.dajeom.domain.PatternBranchDisruptionAnalysis;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 원국 네 지지에서
 *
 * - 六合
 * - 완전한 三合
 * - 완전한 三會 / 方合
 *
 * 을 계산한다.
 *
 * =================================================
 *
 * 六合:
 *
 * 子丑
 * 寅亥
 * 卯戌
 * 辰酉
 * 巳申
 * 午未
 *
 * =================================================
 *
 * 三合:
 *
 * 申子辰 → 水
 * 亥卯未 → 木
 * 寅午戌 → 火
 * 巳酉丑 → 金
 *
 * =================================================
 *
 * 三會 / 方合:
 *
 * 寅卯辰 → 木
 * 巳午未 → 火
 * 申酉戌 → 金
 * 亥子丑 → 水
 *
 * =================================================
 *
 * 중요:
 *
 * 현재 半合 / 拱合을
 * COMPLETE_THREE_HARMONY로 올리지 않는다.
 *
 * 三合은 세 지지가 모두 존재할 때만
 * complete occurrence를 생성한다.
 *
 * =================================================
 *
 * 六合의 化氣 오행은
 * 이 공통 계층에서 사용하지 않는다.
 *
 * 이유:
 *
 * 合 관계 존재와 실제 合化는
 * 분리해서 판단하기 위해서다.
 */
@Component
public class PatternBranchCombinationCalculator {

    public PatternBranchCombinationAnalysis analyze(
            PatternBranchDisruptionAnalysis branchAnalysis
    ) {

        Objects.requireNonNull(
                branchAnalysis,
                "PatternBranchDisruptionAnalysis는 필수입니다."
        );

        Map<PillarPosition, EarthlyBranch> branches =
                branchAnalysis.branches();

        List<PatternBranchCombinationOccurrence> combinations =
                new ArrayList<>();

        PillarPosition[] positions =
                PillarPosition.values();

        /*
         * =================================================
         * 1. 六合
         * =================================================
         */

        for (
                int i = 0;
                i < positions.length;
                i++
        ) {

            for (
                    int j = i + 1;
                    j < positions.length;
                    j++
            ) {

                PillarPosition firstPosition =
                        positions[i];

                PillarPosition secondPosition =
                        positions[j];

                EarthlyBranch firstBranch =
                        branches.get(
                                firstPosition
                        );

                EarthlyBranch secondBranch =
                        branches.get(
                                secondPosition
                        );

                if (
                        sixHarmony(
                                firstBranch,
                                secondBranch
                        )
                ) {

                    combinations.add(
                            new PatternBranchCombinationOccurrence(

                                    PatternBranchCombinationKind
                                            .SIX_HARMONY,

                                    List.of(
                                            firstPosition,
                                            secondPosition
                                    ),

                                    List.of(
                                            firstBranch,
                                            secondBranch
                                    ),

                                    Optional.empty()
                            )
                    );
                }
            }
        }

        /*
         * =================================================
         * 2. 완전한 三合 / 三會
         * =================================================
         *
         * 4개 기둥 중 3개 위치 조합을 모두 검사한다.
         *
         * 같은 지지가 중복되어 있으면
         * branchSet.size() != 3 이므로
         * 완전한 三合/三會로 처리하지 않는다.
         */

        for (
                int i = 0;
                i < positions.length;
                i++
        ) {

            for (
                    int j = i + 1;
                    j < positions.length;
                    j++
            ) {

                for (
                        int k = j + 1;
                        k < positions.length;
                        k++
                ) {

                    PillarPosition firstPosition =
                            positions[i];

                    PillarPosition secondPosition =
                            positions[j];

                    PillarPosition thirdPosition =
                            positions[k];

                    EarthlyBranch firstBranch =
                            branches.get(
                                    firstPosition
                            );

                    EarthlyBranch secondBranch =
                            branches.get(
                                    secondPosition
                            );

                    EarthlyBranch thirdBranch =
                            branches.get(
                                    thirdPosition
                            );

                    Set<EarthlyBranch> branchSet =
                            enumSetOf(
                                    firstBranch,
                                    secondBranch,
                                    thirdBranch
                            );

                    if (
                            branchSet.size()
                                    != 3
                    ) {

                        continue;
                    }

                    Optional<FiveElement> threeHarmonyElement =
                            threeHarmonyElement(
                                    branchSet
                            );

                    threeHarmonyElement.ifPresent(
                            element ->
                                    combinations.add(
                                            new PatternBranchCombinationOccurrence(

                                                    PatternBranchCombinationKind
                                                            .COMPLETE_THREE_HARMONY,

                                                    List.of(
                                                            firstPosition,
                                                            secondPosition,
                                                            thirdPosition
                                                    ),

                                                    List.of(
                                                            firstBranch,
                                                            secondBranch,
                                                            thirdBranch
                                                    ),

                                                    Optional.of(
                                                            element
                                                    )
                                            )
                                    )
                    );

                    Optional<FiveElement> threeMeetingElement =
                            threeMeetingElement(
                                    branchSet
                            );

                    threeMeetingElement.ifPresent(
                            element ->
                                    combinations.add(
                                            new PatternBranchCombinationOccurrence(

                                                    PatternBranchCombinationKind
                                                            .COMPLETE_THREE_MEETING,

                                                    List.of(
                                                            firstPosition,
                                                            secondPosition,
                                                            thirdPosition
                                                    ),

                                                    List.of(
                                                            firstBranch,
                                                            secondBranch,
                                                            thirdBranch
                                                    ),

                                                    Optional.of(
                                                            element
                                                    )
                                            )
                                    )
                    );
                }
            }
        }

        return new PatternBranchCombinationAnalysis(
                branches,
                combinations
        );
    }

    /**
     * 六合
     *
     * 子丑
     * 寅亥
     * 卯戌
     * 辰酉
     * 巳申
     * 午未
     */
    private boolean sixHarmony(

            EarthlyBranch first,

            EarthlyBranch second

    ) {

        return pair(
                first,
                second,
                EarthlyBranch.ZI,
                EarthlyBranch.CHOU
        )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.YIN,
                        EarthlyBranch.HAI
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.MAO,
                        EarthlyBranch.XU
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.CHEN,
                        EarthlyBranch.YOU
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.SI,
                        EarthlyBranch.SHEN
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.WU,
                        EarthlyBranch.WEI
                );
    }

    /**
     * 完全 三合.
     *
     * 申子辰 → 水
     * 亥卯未 → 木
     * 寅午戌 → 火
     * 巳酉丑 → 金
     */
    private Optional<FiveElement> threeHarmonyElement(
            Set<EarthlyBranch> branches
    ) {

        if (
                sameSet(
                        branches,
                        EarthlyBranch.SHEN,
                        EarthlyBranch.ZI,
                        EarthlyBranch.CHEN
                )
        ) {

            return Optional.of(
                    FiveElement.WATER
            );
        }

        if (
                sameSet(
                        branches,
                        EarthlyBranch.HAI,
                        EarthlyBranch.MAO,
                        EarthlyBranch.WEI
                )
        ) {

            return Optional.of(
                    FiveElement.WOOD
            );
        }

        if (
                sameSet(
                        branches,
                        EarthlyBranch.YIN,
                        EarthlyBranch.WU,
                        EarthlyBranch.XU
                )
        ) {

            return Optional.of(
                    FiveElement.FIRE
            );
        }

        if (
                sameSet(
                        branches,
                        EarthlyBranch.SI,
                        EarthlyBranch.YOU,
                        EarthlyBranch.CHOU
                )
        ) {

            return Optional.of(
                    FiveElement.METAL
            );
        }

        return Optional.empty();
    }

    /**
     * 完全 三會 / 方合.
     *
     * 寅卯辰 → 木
     * 巳午未 → 火
     * 申酉戌 → 金
     * 亥子丑 → 水
     */
    private Optional<FiveElement> threeMeetingElement(
            Set<EarthlyBranch> branches
    ) {

        if (
                sameSet(
                        branches,
                        EarthlyBranch.YIN,
                        EarthlyBranch.MAO,
                        EarthlyBranch.CHEN
                )
        ) {

            return Optional.of(
                    FiveElement.WOOD
            );
        }

        if (
                sameSet(
                        branches,
                        EarthlyBranch.SI,
                        EarthlyBranch.WU,
                        EarthlyBranch.WEI
                )
        ) {

            return Optional.of(
                    FiveElement.FIRE
            );
        }

        if (
                sameSet(
                        branches,
                        EarthlyBranch.SHEN,
                        EarthlyBranch.YOU,
                        EarthlyBranch.XU
                )
        ) {

            return Optional.of(
                    FiveElement.METAL
            );
        }

        if (
                sameSet(
                        branches,
                        EarthlyBranch.HAI,
                        EarthlyBranch.ZI,
                        EarthlyBranch.CHOU
                )
        ) {

            return Optional.of(
                    FiveElement.WATER
            );
        }

        return Optional.empty();
    }

    private Set<EarthlyBranch> enumSetOf(

            EarthlyBranch first,

            EarthlyBranch second,

            EarthlyBranch third

    ) {

        EnumSet<EarthlyBranch> set =
                EnumSet.noneOf(
                        EarthlyBranch.class
                );

        set.add(first);
        set.add(second);
        set.add(third);

        return set;
    }

    private boolean sameSet(

            Set<EarthlyBranch> actual,

            EarthlyBranch first,

            EarthlyBranch second,

            EarthlyBranch third

    ) {

        EnumSet<EarthlyBranch> expected =
                EnumSet.of(
                        first,
                        second,
                        third
                );

        return actual.equals(
                expected
        );
    }

    private boolean pair(

            EarthlyBranch first,

            EarthlyBranch second,

            EarthlyBranch expectedA,

            EarthlyBranch expectedB

    ) {

        return (
                first == expectedA
                        &&
                        second == expectedB
        )
                ||
                (
                        first == expectedB
                                &&
                                second == expectedA
                );
    }
}