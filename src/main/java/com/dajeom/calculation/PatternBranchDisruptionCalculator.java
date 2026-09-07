package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.PatternBranchDisruptionAnalysis;
import com.dajeom.domain.PatternBranchDisruptionOccurrence;
import com.dajeom.domain.PatternBranchDisruptionType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternInteractionHiddenStem;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 원국 네 지지에서
 *
 * 沖 / 刑 / 破 / 害
 *
 * 를 직접 계산한다.
 *
 * =================================================
 *
 * 기존 BranchRelation의 내부 API를
 * 추측하지 않기 위해 EarthlyBranch 값에서
 * 독립적으로 계산한다.
 *
 * =================================================
 *
 * 刑 주의:
 *
 * 寅巳申 / 丑戌未는 여기서
 * 각 쌍의 raw 刑 관계를 계산한다.
 *
 * 세 글자 전체가 모였을 때의
 * 三刑 완성 여부와 최종 작용은
 * 이후 별도 구조 Rule에서 판정한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
@Component
public class PatternBranchDisruptionCalculator {

    public PatternBranchDisruptionAnalysis analyze(
            PatternInteractionAnalysis interaction
    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Map<PillarPosition, EarthlyBranch> branches =
                extractBranches(
                        interaction
                );

        List<PatternBranchDisruptionOccurrence> occurrences =
                new ArrayList<>();

        PillarPosition[] positions =
                PillarPosition.values();

        for (
                int i = 0;
                i < positions.length;
                i++
        ) {

            PillarPosition leftPosition =
                    positions[i];

            EarthlyBranch left =
                    branches.get(
                            leftPosition
                    );

            for (
                    int j = i + 1;
                    j < positions.length;
                    j++
            ) {

                PillarPosition rightPosition =
                        positions[j];

                EarthlyBranch right =
                        branches.get(
                                rightPosition
                        );

                if (
                        clash(
                                left,
                                right
                        )
                ) {

                    occurrences.add(
                            occurrence(
                                    leftPosition,
                                    left,
                                    rightPosition,
                                    right,
                                    PatternBranchDisruptionType
                                            .CLASH
                            )
                    );
                }

                if (
                        punishment(
                                left,
                                right
                        )
                ) {

                    occurrences.add(
                            occurrence(
                                    leftPosition,
                                    left,
                                    rightPosition,
                                    right,
                                    PatternBranchDisruptionType
                                            .PUNISHMENT
                            )
                    );
                }

                if (
                        breaking(
                                left,
                                right
                        )
                ) {

                    occurrences.add(
                            occurrence(
                                    leftPosition,
                                    left,
                                    rightPosition,
                                    right,
                                    PatternBranchDisruptionType
                                            .BREAK
                            )
                    );
                }

                if (
                        harm(
                                left,
                                right
                        )
                ) {

                    occurrences.add(
                            occurrence(
                                    leftPosition,
                                    left,
                                    rightPosition,
                                    right,
                                    PatternBranchDisruptionType
                                            .HARM
                            )
                    );
                }
            }
        }

        return new PatternBranchDisruptionAnalysis(
                branches,
                occurrences
        );
    }

    /**
     * PatternInteractionAnalysis의 지장간 정보에서
     * 각 기둥의 지지를 복원한다.
     *
     * 같은 기둥에는 여러 지장간이 있으므로
     * 같은 position이 반복되는 것이 정상이다.
     */
    private Map<PillarPosition, EarthlyBranch> extractBranches(
            PatternInteractionAnalysis interaction
    ) {

        EnumMap<PillarPosition, EarthlyBranch> result =
                new EnumMap<>(
                        PillarPosition.class
                );

        for (
                PatternInteractionHiddenStem hidden
                : interaction.hiddenInteractions()
        ) {

            Objects.requireNonNull(
                    hidden,
                    "지장간 Interaction에 null이 포함될 수 없습니다."
            );

            PillarPosition position =
                    hidden.branchPosition();

            EarthlyBranch branch =
                    hidden.branch();

            EarthlyBranch existing =
                    result.putIfAbsent(
                            position,
                            branch
                    );

            if (
                    existing != null
                            &&
                            existing != branch
            ) {

                throw new IllegalStateException(
                        "같은 기둥 위치에서 서로 다른 지지가 발견되었습니다: "
                                +
                                position
                );
            }
        }

        for (
                PillarPosition position
                : PillarPosition.values()
        ) {

            if (!result.containsKey(position)) {

                throw new IllegalStateException(
                        "십성 Interaction에서 "
                                +
                                position
                                +
                                " 지지를 복원할 수 없습니다."
                );
            }
        }

        return Map.copyOf(
                result
        );
    }

    private PatternBranchDisruptionOccurrence occurrence(

            PillarPosition leftPosition,

            EarthlyBranch left,

            PillarPosition rightPosition,

            EarthlyBranch right,

            PatternBranchDisruptionType type

    ) {

        return new PatternBranchDisruptionOccurrence(
                leftPosition,
                left,
                rightPosition,
                right,
                type
        );
    }

    /**
     * 六沖
     *
     * 子午
     * 丑未
     * 寅申
     * 卯酉
     * 辰戌
     * 巳亥
     */
    private boolean clash(

            EarthlyBranch first,

            EarthlyBranch second

    ) {

        return pair(
                first,
                second,
                EarthlyBranch.ZI,
                EarthlyBranch.WU
        )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.CHOU,
                        EarthlyBranch.WEI
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.YIN,
                        EarthlyBranch.SHEN
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.MAO,
                        EarthlyBranch.YOU
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.CHEN,
                        EarthlyBranch.XU
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.SI,
                        EarthlyBranch.HAI
                );
    }

    /**
     * 刑
     *
     * 寅巳申
     * 丑戌未
     * 子卯
     *
     * 自刑:
     * 辰辰
     * 午午
     * 酉酉
     * 亥亥
     *
     * 여기서는 raw 쌍 관계만 계산한다.
     */
    private boolean punishment(

            EarthlyBranch first,

            EarthlyBranch second

    ) {

        /*
         * 寅巳申
         */
        if (
                pair(
                        first,
                        second,
                        EarthlyBranch.YIN,
                        EarthlyBranch.SI
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
                                EarthlyBranch.SHEN,
                                EarthlyBranch.YIN
                        )
        ) {

            return true;
        }

        /*
         * 丑戌未
         */
        if (
                pair(
                        first,
                        second,
                        EarthlyBranch.CHOU,
                        EarthlyBranch.XU
                )
                        ||
                        pair(
                                first,
                                second,
                                EarthlyBranch.XU,
                                EarthlyBranch.WEI
                        )
                        ||
                        pair(
                                first,
                                second,
                                EarthlyBranch.WEI,
                                EarthlyBranch.CHOU
                        )
        ) {

            return true;
        }

        /*
         * 子卯刑
         */
        if (
                pair(
                        first,
                        second,
                        EarthlyBranch.ZI,
                        EarthlyBranch.MAO
                )
        ) {

            return true;
        }

        /*
         * 自刑
         */
        return first == second
                &&
                selfPunishmentBranch(
                        first
                );
    }

    private boolean selfPunishmentBranch(
            EarthlyBranch branch
    ) {

        return branch
                == EarthlyBranch.CHEN

                ||

                branch
                        == EarthlyBranch.WU

                ||

                branch
                        == EarthlyBranch.YOU

                ||

                branch
                        == EarthlyBranch.HAI;
    }

    /**
     * 六破
     *
     * 子酉
     * 卯午
     * 辰丑
     * 未戌
     * 寅亥
     * 巳申
     */
    private boolean breaking(

            EarthlyBranch first,

            EarthlyBranch second

    ) {

        return pair(
                first,
                second,
                EarthlyBranch.ZI,
                EarthlyBranch.YOU
        )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.MAO,
                        EarthlyBranch.WU
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.CHEN,
                        EarthlyBranch.CHOU
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.WEI,
                        EarthlyBranch.XU
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
                        EarthlyBranch.SI,
                        EarthlyBranch.SHEN
                );
    }

    /**
     * 六害
     *
     * 子未
     * 丑午
     * 寅巳
     * 卯辰
     * 申亥
     * 酉戌
     */
    private boolean harm(

            EarthlyBranch first,

            EarthlyBranch second

    ) {

        return pair(
                first,
                second,
                EarthlyBranch.ZI,
                EarthlyBranch.WEI
        )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.CHOU,
                        EarthlyBranch.WU
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.YIN,
                        EarthlyBranch.SI
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.MAO,
                        EarthlyBranch.CHEN
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.SHEN,
                        EarthlyBranch.HAI
                )
                ||
                pair(
                        first,
                        second,
                        EarthlyBranch.YOU,
                        EarthlyBranch.XU
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