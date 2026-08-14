package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchDirectionalCombinationAnalysis;
import com.dajeom.model.EarthlyBranchDirectionalCombinationEvidence;
import com.dajeom.model.FourPillars;
import com.dajeom.rule.EarthlyBranchDirectionalCombinationRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchDirectionalCombinationForm;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-5.
 *
 * 年支 / 月支 / 日支 / 時支에서:
 *
 * - 완전 방합
 * - 왕지 포함 부분 방합
 * - 왕지 없는 부분 방합
 *
 * 을 탐지한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 삼합과 방합을 별도 구조로 보존한다.
 *
 * 2.
 * 방합이 있다고 삼합 Evidence를 삭제하지 않는다.
 *
 * 3.
 * 완전 방합 내부 Pair Evidence도 삭제하지 않는다.
 *
 * 4.
 * 대신 coveredByCompleteCombination으로
 *    완전 방합 종속 관계를 표시한다.
 *
 * 5.
 * 출생시간 미상에서는 時支를 만들지 않는다.
 *
 * 6.
 * 실제 方局 성립 및 합화 여부는 판단하지 않는다.
 */
@Component
public class EarthlyBranchDirectionalCombinationAnalyzer {

    private final EarthlyBranchDirectionalCombinationRule
            earthlyBranchDirectionalCombinationRule;

    public EarthlyBranchDirectionalCombinationAnalyzer(
            EarthlyBranchDirectionalCombinationRule
                    earthlyBranchDirectionalCombinationRule
    ) {

        this.earthlyBranchDirectionalCombinationRule =
                Objects.requireNonNull(
                        earthlyBranchDirectionalCombinationRule,
                        "EarthlyBranchDirectionalCombinationRule이 필요합니다."
                );
    }

    public EarthlyBranchDirectionalCombinationAnalysis analyze(
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

    public EarthlyBranchDirectionalCombinationAnalysis analyze(

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

        /*
         * =================================================
         * 1단계
         *
         * 완전 방합
         * =================================================
         */
        List<EarthlyBranchDirectionalCombinationEvidence>
                completeEvidences =
                findCompleteCombinations(
                        knownBranches
                );

        /*
         * =================================================
         * 2단계
         *
         * 부분 방합
         * =================================================
         */
        List<EarthlyBranchDirectionalCombinationEvidence>
                partialEvidences =
                findPartialCombinations(
                        knownBranches,
                        completeEvidences
                );

        List<EarthlyBranchDirectionalCombinationEvidence>
                evidences =
                new ArrayList<>();

        evidences.addAll(
                completeEvidences
        );

        evidences.addAll(
                partialEvidences
        );

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * COMPLETE
         *
         * ↓
         *
         * PARTIAL_WITH_CARDINAL
         *
         * ↓
         *
         * PARTIAL_WITHOUT_CARDINAL
         *
         * 이후 실제 원국 위치 순.
         *
         * 최종 작용 강도 순위가 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchDirectionalCombinationEvidence evidence
                                ) ->
                                        evidence
                                                .form()
                                                .rank()
                        )
                        .reversed()

                        .thenComparingInt(
                                evidence ->
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

                        .thenComparingInt(
                                evidence ->
                                        evidence.thirdPosition() == null
                                                ? Integer.MAX_VALUE
                                                : evidence
                                                .thirdPosition()
                                                .ordinal()
                        )
        );

        boolean completeChart =
                hourBranch != null;

        return new EarthlyBranchDirectionalCombinationAnalysis(
                dayBranch,
                evidences,
                knownBranches.size(),
                completeChart
        );
    }

    /**
     * 알려진 모든 3개 위치 조합에서
     * 완전 방합을 찾는다.
     */
    private List<EarthlyBranchDirectionalCombinationEvidence>
    findCompleteCombinations(
            List<BranchSlot> knownBranches
    ) {

        List<EarthlyBranchDirectionalCombinationEvidence> results =
                new ArrayList<>();

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

                for (
                        int thirdIndex =
                        secondIndex + 1;

                        thirdIndex
                                < knownBranches.size();

                        thirdIndex++
                ) {

                    BranchSlot third =
                            knownBranches.get(
                                    thirdIndex
                            );

                    Optional<EarthlyBranchDirectionalCombinationType> type =
                            earthlyBranchDirectionalCombinationRule
                                    .findCompleteCombinationType(
                                            first.branch(),
                                            second.branch(),
                                            third.branch()
                                    );

                    if (
                            type.isEmpty()
                    ) {

                        continue;
                    }

                    results.add(
                            new EarthlyBranchDirectionalCombinationEvidence(
                                    type.get(),
                                    EarthlyBranchDirectionalCombinationForm
                                            .COMPLETE,
                                    first.position(),
                                    first.branch(),
                                    second.position(),
                                    second.branch(),
                                    third.position(),
                                    third.branch(),
                                    false
                            )
                    );
                }
            }
        }

        return results;
    }

    /**
     * 모든 Pair에서 부분 방합을 찾는다.
     *
     * 완전 방합 내부에 포함된 Pair도
     * Evidence는 보존한다.
     */
    private List<EarthlyBranchDirectionalCombinationEvidence>
    findPartialCombinations(

            List<BranchSlot> knownBranches,

            List<EarthlyBranchDirectionalCombinationEvidence>
                    completeEvidences

    ) {

        List<EarthlyBranchDirectionalCombinationEvidence> results =
                new ArrayList<>();

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

                Optional<EarthlyBranchDirectionalCombinationType> type =
                        earthlyBranchDirectionalCombinationRule
                                .findPartialCombinationType(
                                        first.branch(),
                                        second.branch()
                                );

                if (
                        type.isEmpty()
                ) {

                    continue;
                }

                EarthlyBranchDirectionalCombinationForm form =
                        earthlyBranchDirectionalCombinationRule
                                .determinePairForm(
                                        type.get(),
                                        first.branch(),
                                        second.branch()
                                );

                boolean coveredByCompleteCombination =
                        isCoveredByCompleteCombination(
                                type.get(),
                                first.position(),
                                second.position(),
                                completeEvidences
                        );

                results.add(
                        new EarthlyBranchDirectionalCombinationEvidence(
                                type.get(),
                                form,
                                first.position(),
                                first.branch(),
                                second.position(),
                                second.branch(),
                                null,
                                null,
                                coveredByCompleteCombination
                        )
                );
            }
        }

        return results;
    }

    /**
     * 동일 方의 완전 방합 Evidence가
     * 현재 Pair 위치를 모두 포함하는지 확인한다.
     */
    private boolean isCoveredByCompleteCombination(

            EarthlyBranchDirectionalCombinationType type,

            EarthlyBranchPosition firstPosition,

            EarthlyBranchPosition secondPosition,

            List<EarthlyBranchDirectionalCombinationEvidence>
                    completeEvidences

    ) {

        return completeEvidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationEvidence
                                ::completeCombination
                )
                .filter(
                        evidence ->
                                evidence.combinationType()
                                        == type
                )
                .anyMatch(
                        evidence ->
                                evidence.involves(
                                        firstPosition
                                )
                                        && evidence.involves(
                                        secondPosition
                                )
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