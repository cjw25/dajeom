package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchClashAnalysis;
import com.dajeom.model.EarthlyBranchClashEvidence;
import com.dajeom.model.FourPillars;
import com.dajeom.rule.EarthlyBranchClashRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchClashType;
import com.dajeom.type.EarthlyBranchPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-6.
 *
 * 年支 / 月支 / 日支 / 時支의
 * 모든 Pair를 비교하여 지지 육충을 탐지한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 모든 알려진 지지 Pair를 검사한다.
 *
 * 2.
 * 출생시간 미상이면 時支를 만들지 않는다.
 *
 * 3.
 * 충은 양방향 관계로 저장한다.
 *
 * 4.
 * 일지 포함 여부를 보존한다.
 *
 * 5.
 * 월지 포함 여부를 보존한다.
 *
 * 6.
 * 인접/원거리 충을 모두 보존한다.
 *
 * 7.
 * 하나의 지지가 복수 상대 위치와
 *    충 관계를 가지는 경우 모두 보존한다.
 *
 * 8.
 * 육합·삼합·방합 Evidence가 있어도
 *    충을 제거하지 않는다.
 */
@Component
public class EarthlyBranchClashAnalyzer {

    private final EarthlyBranchClashRule
            earthlyBranchClashRule;

    public EarthlyBranchClashAnalyzer(
            EarthlyBranchClashRule
                    earthlyBranchClashRule
    ) {

        this.earthlyBranchClashRule =
                Objects.requireNonNull(
                        earthlyBranchClashRule,
                        "EarthlyBranchClashRule이 필요합니다."
                );
    }

    public EarthlyBranchClashAnalysis analyze(
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
     * hourBranch는 출생시간 미상일 경우 null 허용.
     */
    public EarthlyBranchClashAnalysis analyze(

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

        List<EarthlyBranchClashEvidence> evidences =
                new ArrayList<>();

        /*
         * =================================================
         * 모든 Pair 비교
         * =================================================
         *
         * 4주 완전:
         *
         * YEAR-MONTH
         * YEAR-DAY
         * YEAR-HOUR
         * MONTH-DAY
         * MONTH-HOUR
         * DAY-HOUR
         *
         * 최대 6 Pair.
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

                Optional<EarthlyBranchClashType> clashType =
                        earthlyBranchClashRule
                                .findClashType(
                                        first.branch(),
                                        second.branch()
                                );

                if (
                        clashType.isEmpty()
                ) {

                    continue;
                }

                int positionDistance =
                        earthlyBranchClashRule
                                .positionDistance(
                                        first.position(),
                                        second.position()
                                );

                boolean adjacent =
                        earthlyBranchClashRule
                                .adjacent(
                                        first.position(),
                                        second.position()
                                );

                boolean dayBranchInvolved =
                        earthlyBranchClashRule
                                .dayBranchInvolved(
                                        first.position(),
                                        second.position()
                                );

                boolean monthBranchInvolved =
                        earthlyBranchClashRule
                                .monthBranchInvolved(
                                        first.position(),
                                        second.position()
                                );

                evidences.add(
                        new EarthlyBranchClashEvidence(
                                clashType.get(),
                                first.position(),
                                first.branch(),
                                second.position(),
                                second.branch(),
                                positionDistance,
                                adjacent,
                                dayBranchInvolved,
                                monthBranchInvolved
                        )
                );
            }
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 실제 원국 위치 순으로만 정렬한다.
         *
         * 충의 강약 순위가 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchClashEvidence evidence
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

        return new EarthlyBranchClashAnalysis(
                dayBranch,
                monthBranch,
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