package com.dajeom.analyzer;

import com.dajeom.model.FourPillars;
import com.dajeom.model.HeavenlyStemCombinationAnalysis;
import com.dajeom.model.HeavenlyStemCombinationEvidence;
import com.dajeom.rule.HeavenlyStemCombinationRule;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationType;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-1.
 *
 * 원국의 年干 / 月干 / 日干 / 時干을 비교하여
 * 천간 오합 관계를 전부 탐지한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 모든 알려진 천간 Pair를 비교한다.
 *
 * 2.
 * 출생시간 미상이면 時干을 만들지 않는다.
 *
 * 3.
 * 하나의 천간이 복수의 상대 천간과
 *    합 관계를 만드는 경우 전부 보존한다.
 *
 * 4.
 * 인접 합과 원거리 합을 모두 보존한다.
 *
 * 5.
 * 일간이 포함된 합을 별도로 추적할 수 있다.
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
public class HeavenlyStemCombinationAnalyzer {

    private final HeavenlyStemCombinationRule
            heavenlyStemCombinationRule;

    public HeavenlyStemCombinationAnalyzer(
            HeavenlyStemCombinationRule
                    heavenlyStemCombinationRule
    ) {

        this.heavenlyStemCombinationRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationRule,
                        "HeavenlyStemCombinationRule이 필요합니다."
                );
    }

    public HeavenlyStemCombinationAnalysis analyze(
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
     * 명시적 천간 입력 경로.
     *
     * hourStem은 출생시간 미상일 경우 null 허용.
     *
     * 8차 내부 테스트나 상위 Engine 연결에서도
     * 재사용할 수 있다.
     */
    public HeavenlyStemCombinationAnalysis analyze(

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

        List<HeavenlyStemCombinationEvidence> evidences =
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
         *
         * 를 모두 본다.
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

                Optional<HeavenlyStemCombinationType> combinationType =
                        heavenlyStemCombinationRule
                                .findCombinationType(
                                        first.stem(),
                                        second.stem()
                                );

                if (
                        combinationType.isEmpty()
                ) {

                    continue;
                }

                int positionDistance =
                        heavenlyStemCombinationRule
                                .positionDistance(
                                        first.position(),
                                        second.position()
                                );

                boolean adjacent =
                        heavenlyStemCombinationRule
                                .adjacent(
                                        first.position(),
                                        second.position()
                                );

                boolean dayMasterInvolved =
                        heavenlyStemCombinationRule
                                .dayMasterInvolved(
                                        first.position(),
                                        second.position()
                                );

                evidences.add(
                        new HeavenlyStemCombinationEvidence(
                                combinationType.get(),
                                first.position(),
                                first.stem(),
                                second.position(),
                                second.stem(),
                                positionDistance,
                                adjacent,
                                dayMasterInvolved
                        )
                );
            }
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 1. 앞 기둥 위치
         * 2. 뒤 기둥 위치
         *
         * 합의 강약 순서가 아니다.
         *
         * 단순히 원국 순서대로
         * 안정적으로 출력하기 위한 정렬이다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        HeavenlyStemCombinationEvidence evidence
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
                hourStem != null;

        return new HeavenlyStemCombinationAnalysis(
                dayStem,
                evidences,
                knownStems.size(),
                completeChart
        );
    }

    /**
     * Analyzer 내부에서만 사용하는
     * 천간 + 위치 Pair.
     */
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