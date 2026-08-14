package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchBreakAnalysis;
import com.dajeom.model.EarthlyBranchBreakEvidence;
import com.dajeom.model.FourPillars;
import com.dajeom.rule.EarthlyBranchBreakRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchBreakType;
import com.dajeom.type.EarthlyBranchPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-8.
 *
 * 年支 / 月支 / 日支 / 時支의
 * 모든 Pair를 비교하여 지지 파를 탐지한다.
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
 * 파 관계는 대칭 Pair로 보존한다.
 *
 * 4.
 * 일지 관여 여부를 보존한다.
 *
 * 5.
 * 월지 관여 여부를 보존한다.
 *
 * 6.
 * 인접/원거리 관계를 모두 보존한다.
 *
 * 7.
 * 하나의 위치가 복수 파 관계에 참여하면
 *    모든 Evidence를 보존한다.
 *
 * 8.
 * 기존 六合·三合·方合·沖·刑 관계를
 *    이 단계에서 삭제하지 않는다.
 */
@Component
public class EarthlyBranchBreakAnalyzer {

    private final EarthlyBranchBreakRule
            earthlyBranchBreakRule;

    public EarthlyBranchBreakAnalyzer(
            EarthlyBranchBreakRule
                    earthlyBranchBreakRule
    ) {

        this.earthlyBranchBreakRule =
                Objects.requireNonNull(
                        earthlyBranchBreakRule,
                        "EarthlyBranchBreakRule이 필요합니다."
                );
    }

    public EarthlyBranchBreakAnalysis analyze(
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
    public EarthlyBranchBreakAnalysis analyze(

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

        List<EarthlyBranchBreakEvidence> evidences =
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
         * 최대 6개 Pair.
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

                Optional<EarthlyBranchBreakType> breakType =
                        earthlyBranchBreakRule
                                .findBreakType(
                                        first.branch(),
                                        second.branch()
                                );

                if (
                        breakType.isEmpty()
                ) {

                    continue;
                }

                int positionDistance =
                        earthlyBranchBreakRule
                                .positionDistance(
                                        first.position(),
                                        second.position()
                                );

                boolean adjacent =
                        earthlyBranchBreakRule
                                .adjacent(
                                        first.position(),
                                        second.position()
                                );

                boolean dayBranchInvolved =
                        earthlyBranchBreakRule
                                .dayBranchInvolved(
                                        first.position(),
                                        second.position()
                                );

                boolean monthBranchInvolved =
                        earthlyBranchBreakRule
                                .monthBranchInvolved(
                                        first.position(),
                                        second.position()
                                );

                evidences.add(
                        new EarthlyBranchBreakEvidence(
                                breakType.get(),
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
         * 원국 위치 순서대로만 정렬한다.
         *
         * 파의 강약이나 중요도 순서가 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchBreakEvidence evidence
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

        return new EarthlyBranchBreakAnalysis(
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