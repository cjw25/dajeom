package com.dajeom.analyzer;

import com.dajeom.model.FourPillars;
import com.dajeom.model.HeavenlyStemControlAnalysis;
import com.dajeom.model.HeavenlyStemControlEvidence;
import com.dajeom.rule.HeavenlyStemControlRule;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemClashType;
import com.dajeom.type.HeavenlyStemControlRelationType;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-2.
 *
 * 원국 年干 / 月干 / 日干 / 時干의
 * 모든 Pair를 비교하여:
 *
 * - 천간충
 * - 일반 극제
 *
 * 를 탐지한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 모든 알려진 천간 Pair를 본다.
 *
 * 2.
 * 출생시간 미상에서는 時干을 만들지 않는다.
 *
 * 3.
 * 상극 방향을 반드시 보존한다.
 *
 * 4.
 * 천간충과 일반 극제를 분리한다.
 *
 * 5.
 * 일간이 극하는 경우와
 *    일간이 극받는 경우를 구분한다.
 *
 * 6.
 * 이 단계에서는 길흉을 판정하지 않는다.
 */
@Component
public class HeavenlyStemControlAnalyzer {

    private final HeavenlyStemControlRule
            heavenlyStemControlRule;

    public HeavenlyStemControlAnalyzer(
            HeavenlyStemControlRule
                    heavenlyStemControlRule
    ) {

        this.heavenlyStemControlRule =
                Objects.requireNonNull(
                        heavenlyStemControlRule,
                        "HeavenlyStemControlRule이 필요합니다."
                );
    }

    public HeavenlyStemControlAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        HeavenlyStem yearStem =
                fourPillars
                        .yearPillar()
                        .heavenlyStem();

        HeavenlyStem monthStem =
                fourPillars
                        .monthPillar()
                        .heavenlyStem();

        HeavenlyStem dayStem =
                fourPillars
                        .dayPillar()
                        .heavenlyStem();

        HeavenlyStem hourStem =
                fourPillars.hourPillar() == null
                        ? null
                        : fourPillars
                        .hourPillar()
                        .heavenlyStem();

        return analyze(
                yearStem,
                monthStem,
                dayStem,
                hourStem
        );
    }

    /**
     * hourStem은 출생시간 미상일 경우 null 허용.
     */
    public HeavenlyStemControlAnalysis analyze(

            HeavenlyStem yearStem,

            HeavenlyStem monthStem,

            HeavenlyStem dayStem,

            HeavenlyStem hourStem

    ) {

        Objects.requireNonNull(
                yearStem,
                "년간이 필요합니다."
        );

        Objects.requireNonNull(
                monthStem,
                "월간이 필요합니다."
        );

        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        List<StemSlot> knownStems =
                new ArrayList<>();

        knownStems.add(
                new StemSlot(
                        VisibleStemPosition.YEAR,
                        yearStem
                )
        );

        knownStems.add(
                new StemSlot(
                        VisibleStemPosition.MONTH,
                        monthStem
                )
        );

        knownStems.add(
                new StemSlot(
                        VisibleStemPosition.DAY,
                        dayStem
                )
        );

        if (
                hourStem != null
        ) {

            knownStems.add(
                    new StemSlot(
                            VisibleStemPosition.HOUR,
                            hourStem
                    )
            );
        }

        List<HeavenlyStemControlEvidence> evidences =
                new ArrayList<>();

        /*
         * =================================================
         * 모든 Pair
         * =================================================
         *
         * 4주 완전:
         *
         * 年月
         * 年日
         * 年時
         * 月日
         * 月時
         * 日時
         *
         * 최대 6 Pair.
         */
        for (
                int firstIndex = 0;
                firstIndex < knownStems.size();
                firstIndex++
        ) {

            StemSlot first =
                    knownStems.get(
                            firstIndex
                    );

            for (
                    int secondIndex =
                    firstIndex + 1;

                    secondIndex
                            < knownStems.size();

                    secondIndex++
            ) {

                StemSlot second =
                        knownStems.get(
                                secondIndex
                        );

                Optional<HeavenlyStemControlRelationType> relationType =
                        heavenlyStemControlRule
                                .findRelationType(
                                        first.stem(),
                                        second.stem()
                                );

                if (
                        relationType.isEmpty()
                ) {

                    continue;
                }

                /*
                 * =================================================
                 * 극제 방향 결정
                 * =================================================
                 */
                StemSlot controller;

                StemSlot controlled;

                if (
                        heavenlyStemControlRule
                                .controls(
                                        first.stem(),
                                        second.stem()
                                )
                ) {

                    controller =
                            first;

                    controlled =
                            second;

                } else if (
                        heavenlyStemControlRule
                                .controls(
                                        second.stem(),
                                        first.stem()
                                )
                ) {

                    controller =
                            second;

                    controlled =
                            first;

                } else {

                    /*
                     * findRelationType이 존재했다면
                     * 여기로 오면 안 된다.
                     */
                    throw new IllegalStateException(
                            "천간 극제 관계의 방향을 결정할 수 없습니다: "
                                    + first.stem()
                                    + " / "
                                    + second.stem()
                    );
                }

                Optional<HeavenlyStemClashType> clashType =
                        heavenlyStemControlRule
                                .findClashType(
                                        first.stem(),
                                        second.stem()
                                );

                int positionDistance =
                        heavenlyStemControlRule
                                .positionDistance(
                                        first.position(),
                                        second.position()
                                );

                boolean adjacent =
                        heavenlyStemControlRule
                                .adjacent(
                                        first.position(),
                                        second.position()
                                );

                boolean dayMasterController =
                        controller.position()
                                == VisibleStemPosition.DAY;

                boolean dayMasterControlled =
                        controlled.position()
                                == VisibleStemPosition.DAY;

                evidences.add(
                        new HeavenlyStemControlEvidence(
                                relationType.get(),
                                clashType.orElse(
                                        null
                                ),
                                controller.position(),
                                controller.stem(),
                                controlled.position(),
                                controlled.stem(),
                                positionDistance,
                                adjacent,
                                dayMasterController,
                                dayMasterControlled
                        )
                );
            }
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 극제 방향과 상관없이
         * 실제 원국 위치 Pair 순서대로 정렬한다.
         *
         * 이는 강약 순위가 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        HeavenlyStemControlEvidence evidence
                                ) ->
                                        Math.min(
                                                evidence
                                                        .controllerPosition()
                                                        .ordinal(),
                                                evidence
                                                        .controlledPosition()
                                                        .ordinal()
                                        )
                        )

                        .thenComparingInt(
                                evidence ->
                                        Math.max(
                                                evidence
                                                        .controllerPosition()
                                                        .ordinal(),
                                                evidence
                                                        .controlledPosition()
                                                        .ordinal()
                                        )
                        )
        );

        boolean completeChart =
                hourStem != null;

        return new HeavenlyStemControlAnalysis(
                dayStem,
                evidences,
                knownStems.size(),
                completeChart
        );
    }

    private record StemSlot(

            VisibleStemPosition position,

            HeavenlyStem stem

    ) {

        private StemSlot {

            Objects.requireNonNull(
                    position,
                    "천간 위치가 필요합니다."
            );

            Objects.requireNonNull(
                    stem,
                    "천간이 필요합니다."
            );
        }
    }
}