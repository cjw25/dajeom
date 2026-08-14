package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchHarmAnalysis;
import com.dajeom.model.EarthlyBranchHarmEvidence;
import com.dajeom.model.FourPillars;
import com.dajeom.rule.EarthlyBranchHarmRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchHarmType;
import com.dajeom.type.EarthlyBranchPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-9.
 *
 * 年支 / 月支 / 日支 / 時支의
 * 모든 Pair를 비교하여 지지 육해를 탐지한다.
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
 * 해 관계는 대칭 Pair로 보존한다.
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
 * 하나의 위치가 복수 해 관계에 참여하면
 *    모든 Evidence를 보존한다.
 *
 * 8.
 * 기존 六合·三合·方合·沖·刑·破 Evidence를
 *    삭제하지 않는다.
 */
@Component
public class EarthlyBranchHarmAnalyzer {

    private final EarthlyBranchHarmRule
            earthlyBranchHarmRule;

    public EarthlyBranchHarmAnalyzer(
            EarthlyBranchHarmRule
                    earthlyBranchHarmRule
    ) {

        this.earthlyBranchHarmRule =
                Objects.requireNonNull(
                        earthlyBranchHarmRule,
                        "EarthlyBranchHarmRule이 필요합니다."
                );
    }

    public EarthlyBranchHarmAnalysis analyze(
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
    public EarthlyBranchHarmAnalysis analyze(

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

        List<EarthlyBranchHarmEvidence> evidences =
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

                Optional<EarthlyBranchHarmType> harmType =
                        earthlyBranchHarmRule
                                .findHarmType(
                                        first.branch(),
                                        second.branch()
                                );

                if (
                        harmType.isEmpty()
                ) {

                    continue;
                }

                int positionDistance =
                        earthlyBranchHarmRule
                                .positionDistance(
                                        first.position(),
                                        second.position()
                                );

                boolean adjacent =
                        earthlyBranchHarmRule
                                .adjacent(
                                        first.position(),
                                        second.position()
                                );

                boolean dayBranchInvolved =
                        earthlyBranchHarmRule
                                .dayBranchInvolved(
                                        first.position(),
                                        second.position()
                                );

                boolean monthBranchInvolved =
                        earthlyBranchHarmRule
                                .monthBranchInvolved(
                                        first.position(),
                                        second.position()
                                );

                evidences.add(
                        new EarthlyBranchHarmEvidence(
                                harmType.get(),
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
         * 원국 위치 순서로만 정렬한다.
         *
         * 해의 실제 중요도나 길흉 순위가 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchHarmEvidence evidence
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

        return new EarthlyBranchHarmAnalysis(
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