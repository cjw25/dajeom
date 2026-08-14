package com.dajeom.analyzer;

import com.dajeom.model.AnnualDecadeLuckCrossResolution;
import com.dajeom.model.AnnualDecadeLuckInteraction;
import com.dajeom.model.AnnualDecadeLuckInteractionAnalysis;
import com.dajeom.model.AnnualDecadeLuckOverlap;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.resolver.AnnualDecadeLuckCrossResolver;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 14차-5.
 *
 * 대운 × 세운 Interaction Analyzer.
 *
 * =================================================
 *
 * 1.
 * 세운과 실제로 겹치는 대운 Period를 찾는다.
 *
 * 2.
 * 출생시간 미상일 경우:
 *
 * possible overlap
 * definite overlap
 *
 * 을 분리한다.
 *
 * 3.
 * 실제 가능성이 있는 대운에 대해서만
 * 8/9차 관계 Resolver를 실행한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 세운 시작일에 해당하는 대운 하나만
 * 골라서 1년 전체에 적용하지 않는다.
 *
 * 세운 중간에 대운이 바뀌면
 * 두 Interaction을 모두 보존한다.
 */
@Component
public class AnnualDecadeLuckInteractionAnalyzer {

    /**
     * =================================================
     * 공식 14-5 API
     * =================================================
     */
    public AnnualDecadeLuckInteractionAnalysis analyze(

            AnnualLuckPeriod annualPeriod,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualDecadeLuckCrossResolver crossResolver

    ) {

        Objects.requireNonNull(
                annualPeriod,
                "14-5 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "14-5 분석에는 DecadeLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                crossResolver,
                "AnnualDecadeLuckCrossResolver가 필요합니다."
        );

        /*
         * =================================================
         * 13차 Sequence 자체가 불확실한 경우
         * =================================================
         */

        if (
                !decadeLuckAnalysis
                        .sequenceAvailable()
        ) {

            return new AnnualDecadeLuckInteractionAnalysis(
                    annualPeriod,
                    decadeLuckAnalysis,
                    List.of(),
                    false,
                    decadeLuckAnalysis
                            .unavailableReason()
                            .orElse(
                                    "대운 Sequence가 확정되지 않아 세운과의 관계를 계산할 수 없습니다."
                            )
            );
        }

        List<AnnualDecadeLuckInteraction>
                interactions =
                new ArrayList<>();

        for (
                DecadeLuckPeriod decadePeriod
                : decadeLuckAnalysis.periods()
        ) {

            Optional<AnnualDecadeLuckOverlap>
                    overlap =
                    calculateOverlap(
                            annualPeriod,
                            decadePeriod
                    );

            /*
             * 이 세운과 시간적으로 전혀
             * 접촉하지 않는 대운.
             */
            if (
                    overlap.isEmpty()
            ) {

                continue;
            }

            AnnualDecadeLuckCrossResolution resolution =
                    Objects.requireNonNull(
                            crossResolver.resolve(
                                    annualPeriod,
                                    decadePeriod
                            ),
                            "AnnualDecadeLuckCrossResolver가 null을 반환했습니다."
                    );

            /*
             * =================================================
             * Resolver Identity 검증
             * =================================================
             */

            if (
                    resolution.annualPeriod()
                            != annualPeriod
            ) {

                throw new IllegalArgumentException(
                        "14-5 Resolver가 다른 AnnualLuckPeriod를 반환했습니다."
                );
            }

            if (
                    resolution.decadePeriod()
                            != decadePeriod
            ) {

                throw new IllegalArgumentException(
                        "14-5 Resolver가 다른 DecadeLuckPeriod를 반환했습니다."
                );
            }

            interactions.add(
                    new AnnualDecadeLuckInteraction(
                            annualPeriod,
                            overlap.orElseThrow(),
                            resolution.relations(),
                            resolution.transformations()
                    )
            );
        }

        interactions.sort(
                Comparator
                        .comparing(
                                (
                                        AnnualDecadeLuckInteraction interaction
                                ) ->
                                        interaction
                                                .overlap()
                                                .possibleStartInclusive()
                        )
                        .thenComparingInt(
                                AnnualDecadeLuckInteraction
                                        ::decadeSequenceNumber
                        )
        );

        /*
         * =================================================
         * Sequence 범위 안인데 Interaction이 하나도 없다면
         * 데이터 불연속 가능성을 검사한다.
         *
         * 첫 대운 이전 / 마지막 생성 대운 이후는
         * 정상적인 empty 상태이므로 허용한다.
         * =================================================
         */

        validateEmptyInteractionState(
                annualPeriod,
                decadeLuckAnalysis,
                interactions
        );

        return new AnnualDecadeLuckInteractionAnalysis(
                annualPeriod,
                decadeLuckAnalysis,
                interactions,
                true,
                ""
        );
    }

    /**
     * =================================================
     * 세운 × 대운 시간 Overlap
     * =================================================
     *
     * Possible decade interval:
     *
     * [earliestStart,
     *  latestEnd)
     *
     * =================================================
     *
     * Definite decade interval:
     *
     * [latestStart,
     *  earliestEnd)
     *
     * =================================================
     */

    private Optional<AnnualDecadeLuckOverlap>
    calculateOverlap(

            AnnualLuckPeriod annualPeriod,

            DecadeLuckPeriod decadePeriod

    ) {

        ZonedDateTime annualStart =
                annualPeriod
                        .startDateTimeInclusive();

        ZonedDateTime annualEnd =
                annualPeriod
                        .endDateTimeExclusive();

        /*
         * 모든 시간 비교를 세운 Zone으로 맞춘다.
         */

        ZonedDateTime earliestDecadeStart =
                decadePeriod
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                annualPeriod.zoneId()
                        );

        ZonedDateTime latestDecadeStart =
                decadePeriod
                        .latestStartDateTime()
                        .withZoneSameInstant(
                                annualPeriod.zoneId()
                        );

        ZonedDateTime earliestDecadeEnd =
                decadePeriod
                        .earliestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                annualPeriod.zoneId()
                        );

        ZonedDateTime latestDecadeEnd =
                decadePeriod
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                annualPeriod.zoneId()
                        );

        /*
         * =================================================
         * POSSIBLE
         * =================================================
         *
         * 어떤 가능한 출생시간에서는
         * 이 대운일 수 있는 최대 범위.
         */

        ZonedDateTime possibleStart =
                max(
                        annualStart,
                        earliestDecadeStart
                );

        ZonedDateTime possibleEnd =
                min(
                        annualEnd,
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
         *
         * 모든 가능한 출생시간에서
         * 공통적으로 이 대운인 구간.
         */

        ZonedDateTime definiteStart =
                max(
                        annualStart,
                        latestDecadeStart
                );

        ZonedDateTime definiteEnd =
                min(
                        annualEnd,
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
                new AnnualDecadeLuckOverlap(
                        decadePeriod,
                        possibleStart,
                        possibleEnd,
                        definiteStartOptional,
                        definiteEndOptional
                )
        );
    }

    /**
     * =================================================
     * Empty 상태 검증
     * =================================================
     */
    private void validateEmptyInteractionState(

            AnnualLuckPeriod annualPeriod,

            DecadeLuckAnalysis decadeLuckAnalysis,

            List<AnnualDecadeLuckInteraction> interactions

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
                    "대운 Sequence가 available인데 Period가 없습니다."
            );
        }

        DecadeLuckPeriod first =
                periods.get(0);

        DecadeLuckPeriod last =
                periods.get(
                        periods.size() - 1
                );

        ZonedDateTime annualStart =
                annualPeriod
                        .startDateTimeInclusive();

        ZonedDateTime annualEnd =
                annualPeriod
                        .endDateTimeExclusive();

        ZonedDateTime firstPossibleStart =
                first
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                annualPeriod.zoneId()
                        );

        ZonedDateTime lastPossibleEnd =
                last
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                annualPeriod.zoneId()
                        );

        boolean beforeFirst =
                !annualEnd
                        .isAfter(
                                firstPossibleStart
                        );

        boolean afterLast =
                !annualStart
                        .isBefore(
                                lastPossibleEnd
                        );

        if (
                !beforeFirst
                        && !afterLast
        ) {

            throw new IllegalStateException(
                    "세운이 생성된 대운 범위 안에 있으나 대응하는 대운 Overlap을 찾지 못했습니다."
            );
        }
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