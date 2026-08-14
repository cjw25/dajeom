package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchPunishmentAnalysis;
import com.dajeom.model.EarthlyBranchPunishmentEvidence;
import com.dajeom.model.FourPillars;
import com.dajeom.rule.EarthlyBranchPunishmentRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchPunishmentForm;
import com.dajeom.type.EarthlyBranchPunishmentType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-7.
 *
 * 원국 年支 / 月支 / 日支 / 時支에서:
 *
 * - 완전 삼형
 * - 부분 삼형
 * - 子卯 상형
 * - 辰午酉亥 자형
 *
 * 을 탐지한다.
 *
 * =================================================
 *
 * 처리 순서
 *
 * 1.
 * 완전 삼형 탐지
 *
 * 2.
 * 부분 삼형 탐지
 *
 * 3.
 * 子卯 상형 탐지
 *
 * 4.
 * 자형 탐지
 *
 * =================================================
 *
 * 완전 삼형 내부 Pair는 삭제하지 않고
 * coveredByCompletePunishment 상태로 보존한다.
 *
 * =================================================
 *
 * 출생시간 미상에서는
 * 時支를 생성하지 않는다.
 */
@Component
public class EarthlyBranchPunishmentAnalyzer {

    private final EarthlyBranchPunishmentRule
            earthlyBranchPunishmentRule;

    public EarthlyBranchPunishmentAnalyzer(
            EarthlyBranchPunishmentRule
                    earthlyBranchPunishmentRule
    ) {

        this.earthlyBranchPunishmentRule =
                Objects.requireNonNull(
                        earthlyBranchPunishmentRule,
                        "EarthlyBranchPunishmentRule이 필요합니다."
                );
    }

    public EarthlyBranchPunishmentAnalysis analyze(
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

    public EarthlyBranchPunishmentAnalysis analyze(

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
         * 1. 완전 삼형
         * =================================================
         */
        List<EarthlyBranchPunishmentEvidence>
                completeThreePunishments =
                findCompleteThreePunishments(
                        knownBranches
                );

        /*
         * =================================================
         * 2. 부분 삼형
         * =================================================
         */
        List<EarthlyBranchPunishmentEvidence>
                partialThreePunishments =
                findPartialThreePunishments(
                        knownBranches,
                        completeThreePunishments
                );

        /*
         * =================================================
         * 3. 子卯 상형
         * =================================================
         */
        List<EarthlyBranchPunishmentEvidence>
                mutualPunishments =
                findMutualPunishments(
                        knownBranches
                );

        /*
         * =================================================
         * 4. 자형
         * =================================================
         */
        List<EarthlyBranchPunishmentEvidence>
                selfPunishments =
                findSelfPunishments(
                        knownBranches
                );

        List<EarthlyBranchPunishmentEvidence> evidences =
                new ArrayList<>();

        evidences.addAll(
                completeThreePunishments
        );

        evidences.addAll(
                partialThreePunishments
        );

        evidences.addAll(
                mutualPunishments
        );

        evidences.addAll(
                selfPunishments
        );

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * COMPLETE_THREE
         * PARTIAL_THREE
         * MUTUAL_TWO
         * SELF
         *
         * 순으로 먼저 배치한다.
         *
         * 같은 형태에서는
         * 실제 원국 위치 순으로 정렬한다.
         *
         * 이 순서는 길흉이나 실제 작용 강도 순위가 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchPunishmentEvidence evidence
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

        return new EarthlyBranchPunishmentAnalysis(
                dayBranch,
                monthBranch,
                evidences,
                knownBranches.size(),
                completeChart
        );
    }

    /**
     * =================================================
     * 완전 삼형
     * =================================================
     *
     * 알려진 모든 3개 위치 조합을 검사한다.
     */
    private List<EarthlyBranchPunishmentEvidence>
    findCompleteThreePunishments(
            List<BranchSlot> knownBranches
    ) {

        List<EarthlyBranchPunishmentEvidence> results =
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

                    Optional<EarthlyBranchPunishmentType> type =
                            earthlyBranchPunishmentRule
                                    .findCompleteThreePunishmentType(
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
                            new EarthlyBranchPunishmentEvidence(
                                    type.get(),
                                    EarthlyBranchPunishmentForm
                                            .COMPLETE_THREE,
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
     * 부분 삼형
     * =================================================
     *
     * 삼형 계열의 두 구성원이 있으면
     * Pair Evidence를 생성한다.
     *
     * 완전 삼형 내부 Pair도 삭제하지 않는다.
     */
    private List<EarthlyBranchPunishmentEvidence>
    findPartialThreePunishments(

            List<BranchSlot> knownBranches,

            List<EarthlyBranchPunishmentEvidence>
                    completeThreePunishments

    ) {

        List<EarthlyBranchPunishmentEvidence> results =
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

                Optional<EarthlyBranchPunishmentType> type =
                        earthlyBranchPunishmentRule
                                .findPartialThreePunishmentType(
                                        first.branch(),
                                        second.branch()
                                );

                if (
                        type.isEmpty()
                ) {

                    continue;
                }

                boolean covered =
                        isCoveredByCompletePunishment(
                                type.get(),
                                first.position(),
                                second.position(),
                                completeThreePunishments
                        );

                results.add(
                        new EarthlyBranchPunishmentEvidence(
                                type.get(),
                                EarthlyBranchPunishmentForm
                                        .PARTIAL_THREE,
                                first.position(),
                                first.branch(),
                                second.position(),
                                second.branch(),
                                null,
                                null,
                                covered
                        )
                );
            }
        }

        return results;
    }

    /**
     * =================================================
     * 子卯 상형
     * =================================================
     */
    private List<EarthlyBranchPunishmentEvidence>
    findMutualPunishments(
            List<BranchSlot> knownBranches
    ) {

        List<EarthlyBranchPunishmentEvidence> results =
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

                Optional<EarthlyBranchPunishmentType> type =
                        earthlyBranchPunishmentRule
                                .findMutualPunishmentType(
                                        first.branch(),
                                        second.branch()
                                );

                if (
                        type.isEmpty()
                ) {

                    continue;
                }

                results.add(
                        new EarthlyBranchPunishmentEvidence(
                                type.get(),
                                EarthlyBranchPunishmentForm
                                        .MUTUAL_TWO,
                                first.position(),
                                first.branch(),
                                second.position(),
                                second.branch(),
                                null,
                                null,
                                false
                        )
                );
            }
        }

        return results;
    }

    /**
     * =================================================
     * 자형
     * =================================================
     *
     * 동일 지지가 서로 다른 두 위치에
     * 실제로 존재할 때만 생성한다.
     *
     * 辰辰
     * 午午
     * 酉酉
     * 亥亥
     */
    private List<EarthlyBranchPunishmentEvidence>
    findSelfPunishments(
            List<BranchSlot> knownBranches
    ) {

        List<EarthlyBranchPunishmentEvidence> results =
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

                Optional<EarthlyBranchPunishmentType> type =
                        earthlyBranchPunishmentRule
                                .findSelfPunishmentType(
                                        first.branch(),
                                        second.branch()
                                );

                if (
                        type.isEmpty()
                ) {

                    continue;
                }

                results.add(
                        new EarthlyBranchPunishmentEvidence(
                                type.get(),
                                EarthlyBranchPunishmentForm
                                        .SELF,
                                first.position(),
                                first.branch(),
                                second.position(),
                                second.branch(),
                                null,
                                null,
                                false
                        )
                );
            }
        }

        return results;
    }

    /**
     * 동일 삼형의 완전 삼형 Evidence가
     * 현재 Pair의 두 위치를 모두 포함하는가.
     */
    private boolean isCoveredByCompletePunishment(

            EarthlyBranchPunishmentType type,

            EarthlyBranchPosition firstPosition,

            EarthlyBranchPosition secondPosition,

            List<EarthlyBranchPunishmentEvidence>
                    completeThreePunishments

    ) {

        return completeThreePunishments
                .stream()
                .filter(
                        EarthlyBranchPunishmentEvidence
                                ::completeThreePunishment
                )
                .filter(
                        evidence ->
                                evidence.punishmentType()
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