package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchSixCombinationAnalysis;
import com.dajeom.model.EarthlyBranchSixCombinationEvidence;
import com.dajeom.model.FourPillars;
import com.dajeom.rule.EarthlyBranchSixCombinationRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchSixCombinationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-3.
 *
 * 年支 / 月支 / 日支 / 時支의
 * 모든 Pair를 비교하여 육합 관계를 탐지한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 알려진 지지 Pair를 전부 비교한다.
 *
 * 2.
 * 출생시간 미상이면 時支를 만들지 않는다.
 *
 * 3.
 * 하나의 지지가 복수 상대와 육합 관계를 가지면
 *    모든 Evidence를 보존한다.
 *
 * 4.
 * 인접 여부를 별도로 저장한다.
 *
 * 5.
 * 일지 관여 여부를 별도로 저장한다.
 *
 * 6.
 * 합화 성립 여부는 판단하지 않는다.
 *
 * =================================================
 *
 * 최대 Pair 수
 *
 * 4주 완전:
 *
 * 4C2 = 6
 *
 * 시주 미상:
 *
 * 3C2 = 3
 */
@Component
public class EarthlyBranchSixCombinationAnalyzer {

    private final EarthlyBranchSixCombinationRule
            earthlyBranchSixCombinationRule;

    public EarthlyBranchSixCombinationAnalyzer(
            EarthlyBranchSixCombinationRule
                    earthlyBranchSixCombinationRule
    ) {

        this.earthlyBranchSixCombinationRule =
                Objects.requireNonNull(
                        earthlyBranchSixCombinationRule,
                        "EarthlyBranchSixCombinationRule이 필요합니다."
                );
    }

    public EarthlyBranchSixCombinationAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        EarthlyBranch yearBranch =
                fourPillars
                        .yearPillar()
                        .earthlyBranch();

        EarthlyBranch monthBranch =
                fourPillars
                        .monthPillar()
                        .earthlyBranch();

        EarthlyBranch dayBranch =
                fourPillars
                        .dayPillar()
                        .earthlyBranch();

        EarthlyBranch hourBranch =
                fourPillars.hourPillar() == null
                        ? null
                        : fourPillars
                        .hourPillar()
                        .earthlyBranch();

        return analyze(
                yearBranch,
                monthBranch,
                dayBranch,
                hourBranch
        );
    }

    /**
     * 명시적 지지 입력 경로.
     *
     * hourBranch는 출생시간 미상일 경우 null 허용.
     */
    public EarthlyBranchSixCombinationAnalysis analyze(

            EarthlyBranch yearBranch,

            EarthlyBranch monthBranch,

            EarthlyBranch dayBranch,

            EarthlyBranch hourBranch

    ) {

        Objects.requireNonNull(
                yearBranch,
                "년지가 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                dayBranch,
                "일지가 필요합니다."
        );

        List<BranchSlot> knownBranches =
                new ArrayList<>();

        knownBranches.add(
                new BranchSlot(
                        EarthlyBranchPosition.YEAR,
                        yearBranch
                )
        );

        knownBranches.add(
                new BranchSlot(
                        EarthlyBranchPosition.MONTH,
                        monthBranch
                )
        );

        knownBranches.add(
                new BranchSlot(
                        EarthlyBranchPosition.DAY,
                        dayBranch
                )
        );

        if (
                hourBranch != null
        ) {

            knownBranches.add(
                    new BranchSlot(
                            EarthlyBranchPosition.HOUR,
                            hourBranch
                    )
            );
        }

        List<EarthlyBranchSixCombinationEvidence> evidences =
                new ArrayList<>();

        /*
         * =================================================
         * 모든 Pair 비교
         * =================================================
         *
         * 4주:
         *
         * 年月
         * 年日
         * 年時
         * 月日
         * 月時
         * 日時
         */
        for (
                int firstIndex = 0;
                firstIndex < knownBranches.size();
                firstIndex++
        ) {

            BranchSlot first =
                    knownBranches.get(
                            firstIndex
                    );

            for (
                    int secondIndex =
                    firstIndex + 1;

                    secondIndex
                            < knownBranches.size();

                    secondIndex++
            ) {

                BranchSlot second =
                        knownBranches.get(
                                secondIndex
                        );

                Optional<EarthlyBranchSixCombinationType> combinationType =
                        earthlyBranchSixCombinationRule
                                .findCombinationType(
                                        first.branch(),
                                        second.branch()
                                );

                if (
                        combinationType.isEmpty()
                ) {

                    continue;
                }

                int positionDistance =
                        earthlyBranchSixCombinationRule
                                .positionDistance(
                                        first.position(),
                                        second.position()
                                );

                boolean adjacent =
                        earthlyBranchSixCombinationRule
                                .adjacent(
                                        first.position(),
                                        second.position()
                                );

                boolean dayBranchInvolved =
                        earthlyBranchSixCombinationRule
                                .dayBranchInvolved(
                                        first.position(),
                                        second.position()
                                );

                evidences.add(
                        new EarthlyBranchSixCombinationEvidence(
                                combinationType.get(),
                                first.position(),
                                first.branch(),
                                second.position(),
                                second.branch(),
                                positionDistance,
                                adjacent,
                                dayBranchInvolved
                        )
                );
            }
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * YEAR
         * MONTH
         * DAY
         * HOUR
         *
         * 원국 위치 순서대로 정렬한다.
         *
         * 육합의 강약 순위가 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchSixCombinationEvidence evidence
                                ) ->
                                        evidence
                                                .firstPosition()
                                                .ordinal()
                        )
                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .secondPosition()
                                                .ordinal()
                        )
        );

        boolean completeChart =
                hourBranch != null;

        return new EarthlyBranchSixCombinationAnalysis(
                dayBranch,
                evidences,
                knownBranches.size(),
                completeChart
        );
    }

    private record BranchSlot(

            EarthlyBranchPosition position,

            EarthlyBranch branch

    ) {

        private BranchSlot {

            Objects.requireNonNull(
                    position,
                    "지지 위치가 필요합니다."
            );

            Objects.requireNonNull(
                    branch,
                    "지지가 필요합니다."
            );
        }
    }
}