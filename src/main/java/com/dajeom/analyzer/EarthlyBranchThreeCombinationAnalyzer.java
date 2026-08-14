package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchThreeCombinationAnalysis;
import com.dajeom.model.EarthlyBranchThreeCombinationEvidence;
import com.dajeom.model.FourPillars;
import com.dajeom.rule.EarthlyBranchThreeCombinationRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchThreeCombinationForm;
import com.dajeom.type.EarthlyBranchThreeCombinationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-4.
 *
 * 원국의 年支 / 月支 / 日支 / 時支에서:
 *
 * - 완전 삼합
 * - 왕지 포함 반합
 * - 왕지 없는 부분조합
 *
 * 을 탐지한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 완전 삼합과 반합을 같은 것으로 처리하지 않는다.
 *
 * 2.
 * 왕지 없는 생지+고지 Pair를
 *    왕지 포함 반합과 같은 것으로 처리하지 않는다.
 *
 * 3.
 * 완전 삼합이 있어도 내부 Pair Evidence를
 *    삭제하지 않는다.
 *
 * 4.
 * 대신 Pair가 완전 삼합 안에 포함되었는지
 *    coveredByCompleteCombination으로 표시한다.
 *
 * 5.
 * 출생시간 미상에서는 時支를 만들지 않는다.
 *
 * 6.
 * 실제 合化·局 성립 여부는 판단하지 않는다.
 */
@Component
public class EarthlyBranchThreeCombinationAnalyzer {

    private final EarthlyBranchThreeCombinationRule
            earthlyBranchThreeCombinationRule;

    public EarthlyBranchThreeCombinationAnalyzer(
            EarthlyBranchThreeCombinationRule
                    earthlyBranchThreeCombinationRule
    ) {

        this.earthlyBranchThreeCombinationRule =
                Objects.requireNonNull(
                        earthlyBranchThreeCombinationRule,
                        "EarthlyBranchThreeCombinationRule이 필요합니다."
                );
    }

    public EarthlyBranchThreeCombinationAnalysis analyze(
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

    public EarthlyBranchThreeCombinationAnalysis analyze(

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
         * 완전 삼합 탐지
         * =================================================
         */
        List<EarthlyBranchThreeCombinationEvidence>
                completeEvidences =
                findCompleteCombinations(
                        knownBranches
                );

        /*
         * =================================================
         * 2단계
         *
         * Pair 관계 탐지
         * =================================================
         */
        List<EarthlyBranchThreeCombinationEvidence>
                partialEvidences =
                findPartialCombinations(
                        knownBranches,
                        completeEvidences
                );

        List<EarthlyBranchThreeCombinationEvidence> evidences =
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
         * 1.
         * 완전 삼합
         *
         * 2.
         * 왕지 포함 반합
         *
         * 3.
         * 왕지 없는 부분조합
         *
         * 그 안에서는 실제 원국 위치 순서.
         *
         * 이 정렬은 실제 작용 강도의 최종 판정이 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchThreeCombinationEvidence evidence
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

        return new EarthlyBranchThreeCombinationAnalysis(
                dayBranch,
                evidences,
                knownBranches.size(),
                completeChart
        );
    }

    /**
     * =================================================
     * 완전 삼합
     * =================================================
     *
     * 알려진 모든 3개 위치 조합을 검사한다.
     *
     * 4주 완전이면 최대:
     *
     * 4C3 = 4
     *
     * 개의 위치 조합을 검사한다.
     */
    private List<EarthlyBranchThreeCombinationEvidence>
    findCompleteCombinations(
            List<BranchSlot> knownBranches
    ) {

        List<EarthlyBranchThreeCombinationEvidence> results =
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

                    Optional<EarthlyBranchThreeCombinationType> type =
                            earthlyBranchThreeCombinationRule
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
                            new EarthlyBranchThreeCombinationEvidence(
                                    type.get(),
                                    EarthlyBranchThreeCombinationForm
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
     * =================================================
     * 반합 / 부분조합
     * =================================================
     *
     * 알려진 모든 Pair를 검사한다.
     *
     * Pair가 완전 삼합 내부에 포함되어 있더라도
     * Evidence 자체는 삭제하지 않는다.
     *
     * 대신:
     *
     * coveredByCompleteCombination = true
     *
     * 로 표시한다.
     */
    private List<EarthlyBranchThreeCombinationEvidence>
    findPartialCombinations(

            List<BranchSlot> knownBranches,

            List<EarthlyBranchThreeCombinationEvidence>
                    completeEvidences

    ) {

        List<EarthlyBranchThreeCombinationEvidence> results =
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

                Optional<EarthlyBranchThreeCombinationType> type =
                        earthlyBranchThreeCombinationRule
                                .findPartialCombinationType(
                                        first.branch(),
                                        second.branch()
                                );

                if (
                        type.isEmpty()
                ) {

                    continue;
                }

                EarthlyBranchThreeCombinationForm form =
                        earthlyBranchThreeCombinationRule
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
                        new EarthlyBranchThreeCombinationEvidence(
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
     * 동일 삼합국의 완전 삼합 Evidence가
     * 해당 두 위치를 모두 포함하고 있는가.
     */
    private boolean isCoveredByCompleteCombination(

            EarthlyBranchThreeCombinationType type,

            EarthlyBranchPosition firstPosition,

            EarthlyBranchPosition secondPosition,

            List<EarthlyBranchThreeCombinationEvidence>
                    completeEvidences

    ) {

        return completeEvidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationEvidence
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