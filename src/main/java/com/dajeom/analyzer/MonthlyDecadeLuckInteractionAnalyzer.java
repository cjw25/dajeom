package com.dajeom.analyzer;

import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.MonthlyDecadeLuckCrossResolution;
import com.dajeom.model.MonthlyDecadeLuckInteraction;
import com.dajeom.model.MonthlyDecadeLuckInteractionAnalysis;
import com.dajeom.model.MonthlyDecadeLuckOverlap;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.resolver.MonthlyDecadeLuckCrossResolver;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-5.
 *
 * 대운 × 월운 Interaction Analyzer.
 *
 * =================================================
 *
 * 처리:
 *
 * MonthlyLuckPeriod
 * ×
 * 모든 DecadeLuckPeriod
 *
 * ↓
 *
 * 실제 시간 Overlap 확인
 *
 * ↓
 *
 * 겹치는 대운에 대해서만
 * 관계 / 합화 Resolver 호출
 *
 * =================================================
 *
 * 월운 시작시각의 대운 하나를
 * 월운 전체에 강제로 적용하지 않는다.
 */
@Component
public class MonthlyDecadeLuckInteractionAnalyzer {

    /**
     * 공식 15-5 API.
     */
    public MonthlyDecadeLuckInteractionAnalysis analyze(

            MonthlyLuckPeriod monthlyPeriod,

            DecadeLuckAnalysis decadeLuckAnalysis,

            MonthlyDecadeLuckCrossResolver crossResolver

    ) {

        Objects.requireNonNull(
                monthlyPeriod,
                "15-5 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "15-5 분석에는 DecadeLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                crossResolver,
                "MonthlyDecadeLuckCrossResolver가 필요합니다."
        );

        /*
         * =================================================
         * 대운 Sequence 자체가 사용 불가능.
         * =================================================
         */

        if (
                !decadeLuckAnalysis
                        .sequenceAvailable()
        ) {

            return new MonthlyDecadeLuckInteractionAnalysis(
                    monthlyPeriod,
                    decadeLuckAnalysis,
                    List.of(),
                    false,
                    resolveUnavailableReason(
                            decadeLuckAnalysis
                    )
            );
        }

        List<MonthlyDecadeLuckInteraction>
                interactions =
                new ArrayList<>();

        for (
                DecadeLuckPeriod decadePeriod
                : decadeLuckAnalysis.periods()
        ) {

            Optional<MonthlyDecadeLuckOverlap>
                    overlap =
                    calculateOverlap(
                            monthlyPeriod,
                            decadePeriod
                    );

            if (
                    overlap.isEmpty()
            ) {

                continue;
            }

            MonthlyDecadeLuckCrossResolution resolution =
                    Objects.requireNonNull(
                            crossResolver.resolve(
                                    monthlyPeriod,
                                    decadePeriod
                            ),
                            "MonthlyDecadeLuckCrossResolver가 null을 반환했습니다."
                    );

            /*
             * =================================================
             * Identity 검증
             * =================================================
             */

            if (
                    resolution.monthlyPeriod()
                            != monthlyPeriod
            ) {

                throw new IllegalArgumentException(
                        "Resolver가 현재 월운과 다른 MonthlyLuckPeriod를 반환했습니다."
                );
            }

            if (
                    resolution.decadePeriod()
                            != decadePeriod
            ) {

                throw new IllegalArgumentException(
                        "Resolver가 현재 대운과 다른 DecadeLuckPeriod를 반환했습니다."
                );
            }

            interactions.add(
                    new MonthlyDecadeLuckInteraction(
                            monthlyPeriod,
                            overlap.orElseThrow(),
                            resolution.relations(),
                            resolution.transformations()
                    )
            );
        }

        /*
         * 가능한 시작시각 기준 정렬.
         */
        interactions.sort(
                Comparator
                        .comparing(
                                (
                                        MonthlyDecadeLuckInteraction interaction
                                ) ->
                                        interaction
                                                .overlap()
                                                .possibleStartInclusive()
                        )
                        .thenComparingInt(
                                MonthlyDecadeLuckInteraction
                                        ::decadeSequenceNumber
                        )
        );

        validateEmptyInteractionState(
                monthlyPeriod,
                decadeLuckAnalysis,
                interactions
        );

        return new MonthlyDecadeLuckInteractionAnalysis(
                monthlyPeriod,
                decadeLuckAnalysis,
                interactions,
                true,
                ""
        );
    }

    /**
     * =================================================
     * 월운 × 대운 시간 Overlap
     * =================================================
     *
     * 대운 possible:
     *
     * [earliestStart, latestEnd)
     *
     * 대운 definite:
     *
     * [latestStart, earliestEnd)
     */
    private Optional<MonthlyDecadeLuckOverlap>
    calculateOverlap(

            MonthlyLuckPeriod monthlyPeriod,

            DecadeLuckPeriod decadePeriod

    ) {

        ZonedDateTime monthlyStart =
                monthlyPeriod
                        .startDateTimeInclusive();

        ZonedDateTime monthlyEnd =
                monthlyPeriod
                        .endDateTimeExclusive();

        /*
         * 대운 날짜를 월운 Zone으로 정규화.
         */

        ZonedDateTime earliestDecadeStart =
                decadePeriod
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                monthlyPeriod.zoneId()
                        );

        ZonedDateTime latestDecadeStart =
                decadePeriod
                        .latestStartDateTime()
                        .withZoneSameInstant(
                                monthlyPeriod.zoneId()
                        );

        ZonedDateTime earliestDecadeEnd =
                decadePeriod
                        .earliestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                monthlyPeriod.zoneId()
                        );

        ZonedDateTime latestDecadeEnd =
                decadePeriod
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                monthlyPeriod.zoneId()
                        );

        /*
         * =================================================
         * POSSIBLE
         * =================================================
         */

        ZonedDateTime possibleStart =
                max(
                        monthlyStart,
                        earliestDecadeStart
                );

        ZonedDateTime possibleEnd =
                min(
                        monthlyEnd,
                        latestDecadeEnd
                );

        if (
                !possibleEnd
                        .isAfter(
                                possibleStart
                        )
        ) {

            return Optional.empty();
        }

        /*
         * =================================================
         * DEFINITE
         * =================================================
         */

        ZonedDateTime definiteStart =
                max(
                        monthlyStart,
                        latestDecadeStart
                );

        ZonedDateTime definiteEnd =
                min(
                        monthlyEnd,
                        earliestDecadeEnd
                );

        Optional<ZonedDateTime>
                definiteStartOptional;

        Optional<ZonedDateTime>
                definiteEndOptional;

        if (
                definiteEnd
                        .isAfter(
                                definiteStart
                        )
        ) {

            definiteStartOptional =
                    Optional.of(
                            definiteStart
                    );

            definiteEndOptional =
                    Optional.of(
                            definiteEnd
                    );

        } else {

            definiteStartOptional =
                    Optional.empty();

            definiteEndOptional =
                    Optional.empty();
        }

        return Optional.of(
                new MonthlyDecadeLuckOverlap(
                        decadePeriod,
                        possibleStart,
                        possibleEnd,
                        definiteStartOptional,
                        definiteEndOptional
                )
        );
    }

    /**
     * Interaction이 0개일 때
     * 정상적인 범위 밖인지 검증한다.
     */
    private void validateEmptyInteractionState(

            MonthlyLuckPeriod monthlyPeriod,

            DecadeLuckAnalysis decadeLuckAnalysis,

            List<MonthlyDecadeLuckInteraction> interactions

    ) {

        if (
                !interactions
                        .isEmpty()
        ) {

            return;
        }

        List<DecadeLuckPeriod> periods =
                decadeLuckAnalysis
                        .periods();

        if (
                periods.isEmpty()
        ) {

            throw new IllegalStateException(
                    "대운 Sequence가 available인데 DecadeLuckPeriod가 없습니다."
            );
        }

        DecadeLuckPeriod first =
                periods.get(0);

        DecadeLuckPeriod last =
                periods.get(
                        periods.size() - 1
                );

        ZonedDateTime monthlyStart =
                monthlyPeriod
                        .startDateTimeInclusive();

        ZonedDateTime monthlyEnd =
                monthlyPeriod
                        .endDateTimeExclusive();

        ZonedDateTime firstPossibleStart =
                first
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                monthlyPeriod.zoneId()
                        );

        ZonedDateTime lastPossibleEnd =
                last
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                monthlyPeriod.zoneId()
                        );

        boolean beforeFirst =
                !monthlyEnd
                        .isAfter(
                                firstPossibleStart
                        );

        boolean afterLast =
                !monthlyStart
                        .isBefore(
                                lastPossibleEnd
                        );

        if (
                !beforeFirst
                        && !afterLast
        ) {

            throw new IllegalStateException(
                    "월운이 생성된 대운 범위 안에 있으나 대응하는 대운 Overlap을 찾지 못했습니다."
            );
        }
    }

    private String resolveUnavailableReason(
            DecadeLuckAnalysis decadeLuckAnalysis
    ) {

        /*
         * DecadeLuckAnalysis의 실제 convenience API가
         * Optional<String> unavailableReason() 구조라면
         * 이 형태를 사용한다.
         */
        return decadeLuckAnalysis
                .sequenceAnalysis()
                .unavailableReason();
    }

    private ZonedDateTime max(

            ZonedDateTime first,

            ZonedDateTime second

    ) {

        return first.isAfter(
                second
        )
                ? first
                : second;
    }

    private ZonedDateTime min(

            ZonedDateTime first,

            ZonedDateTime second

    ) {

        return first.isBefore(
                second
        )
                ? first
                : second;
    }
}