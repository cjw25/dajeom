package com.dajeom.analyzer;

import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.DecadeLuckPeriodTenGodAnalysis;
import com.dajeom.model.DecadeLuckResolvedTenGodSource;
import com.dajeom.model.DecadeLuckSequenceAnalysis;
import com.dajeom.model.DecadeLuckTenGodAnalysis;
import com.dajeom.model.DecadeLuckTenGodEvidence;
import com.dajeom.resolver.DecadeLuckTenGodSourceResolver;
import com.dajeom.type.DecadeLuckTenGodSourceType;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 13차-4.
 *
 * 대운 간지의 천간·지지 십성 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * dayMaster
 * +
 * 13-3 DecadeLuckSequenceAnalysis
 * +
 * 기존 십성/지장간 Resolver
 *
 * =================================================
 *
 * 출력:
 *
 * DecadeLuckTenGodAnalysis
 *
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 원국 십성 세력 수정
 * - 신강신약 수정
 * - 격국 수정
 * - 용신 수정
 *
 * 을 하지 않는다.
 *
 * 대운에서 유입되는 십성을
 * 별도 Evidence로 생성한다.
 */
@Component
public class DecadeLuckTenGodAnalyzer {

    /**
     * =================================================
     * 공식 13-4 API
     * =================================================
     */
    public DecadeLuckTenGodAnalysis analyze(

            HeavenlyStem dayMaster,

            DecadeLuckSequenceAnalysis sequenceAnalysis,

            DecadeLuckTenGodSourceResolver sourceResolver

    ) {

        Objects.requireNonNull(
                dayMaster,
                "13-4 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                sequenceAnalysis,
                "13-3 DecadeLuckSequenceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                sourceResolver,
                "DecadeLuckTenGodSourceResolver가 필요합니다."
        );

        /*
         * =================================================
         * 13-3 Sequence 자체가 불확실한 경우
         * =================================================
         */

        if (
                !sequenceAnalysis
                        .sequenceAvailable()
        ) {

            return new DecadeLuckTenGodAnalysis(
                    dayMaster,
                    sequenceAnalysis,
                    List.of(),
                    false,
                    "대운 간지 Sequence가 확정되지 않아 "
                            + "대운 십성도 하나로 확정할 수 없습니다. "
                            + sequenceAnalysis.unavailableReason()
            );
        }

        List<DecadeLuckPeriodTenGodAnalysis>
                periodAnalyses =
                new ArrayList<>();

        for (
                DecadeLuckPeriod period
                : sequenceAnalysis.periods()
        ) {

            List<DecadeLuckResolvedTenGodSource>
                    resolvedSources =
                    sourceResolver
                            .resolve(
                                    dayMaster,
                                    period.pillar()
                            );

            resolvedSources =
                    resolvedSources == null
                            ? List.of()
                            : List.copyOf(
                            resolvedSources
                    );

            validateResolvedSources(
                    period,
                    resolvedSources
            );

            List<DecadeLuckTenGodEvidence>
                    evidences =
                    new ArrayList<>();

            for (
                    DecadeLuckResolvedTenGodSource source
                    : resolvedSources
            ) {

                evidences.add(
                        new DecadeLuckTenGodEvidence(
                                dayMaster,
                                period,
                                source.sourceType(),
                                source.sourceStem(),
                                source.tenGodType()
                        )
                );
            }

            /*
             * =================================================
             * 출력 순서
             *
             * 천간
             * 본기
             * 중기
             * 여기
             *
             * 중요도 순위가 아니다.
             * =================================================
             */

            evidences.sort(
                    Comparator.comparingInt(
                            evidence ->
                                    sourceOrder(
                                            evidence
                                                    .sourceType()
                                    )
                    )
            );

            periodAnalyses.add(
                    new DecadeLuckPeriodTenGodAnalysis(
                            dayMaster,
                            period,
                            evidences
                    )
            );
        }

        periodAnalyses.sort(
                Comparator.comparingInt(
                        DecadeLuckPeriodTenGodAnalysis
                                ::sequenceNumber
                )
        );

        return new DecadeLuckTenGodAnalysis(
                dayMaster,
                sequenceAnalysis,
                periodAnalyses,
                true,
                ""
        );
    }

    /*
     * =================================================
     * Resolver 결과 검증
     * =================================================
     */

    private void validateResolvedSources(

            DecadeLuckPeriod period,

            List<DecadeLuckResolvedTenGodSource> sources

    ) {

        if (
                sources.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "대운 십성 Resolver가 빈 결과를 반환했습니다: "
                            + period.sequenceNumber()
            );
        }

        /*
         * 천간 Source는 정확히 하나.
         */

        long visibleCount =
                sources
                        .stream()
                        .filter(
                                DecadeLuckResolvedTenGodSource
                                        ::visible
                        )
                        .count();

        if (
                visibleCount != 1
        ) {

            throw new IllegalArgumentException(
                    "대운에는 정확히 하나의 천간 십성 Source가 필요합니다."
            );
        }

        DecadeLuckResolvedTenGodSource visible =
                sources
                        .stream()
                        .filter(
                                DecadeLuckResolvedTenGodSource
                                        ::visible
                        )
                        .findFirst()
                        .orElseThrow();

        if (
                visible.sourceStem()
                        != period
                        .pillar()
                        .stem()
        ) {

            throw new IllegalArgumentException(
                    "Resolver가 반환한 대운 천간이 실제 대운 천간과 일치하지 않습니다."
            );
        }

        /*
         * 지지 본기도 정확히 하나.
         */

        long mainCount =
                sources
                        .stream()
                        .filter(
                                source ->
                                        source
                                                .sourceType()
                                                .mainHiddenStem()
                        )
                        .count();

        if (
                mainCount != 1
        ) {

            throw new IllegalArgumentException(
                    "대운 지지에는 정확히 하나의 본기 Source가 필요합니다."
            );
        }

        /*
         * Source Type 중복 방지.
         */

        for (
                DecadeLuckTenGodSourceType sourceType
                : DecadeLuckTenGodSourceType.values()
        ) {

            long count =
                    sources
                            .stream()
                            .filter(
                                    source ->
                                            source.sourceType()
                                                    == sourceType
                            )
                            .count();

            if (
                    count > 1
            ) {

                throw new IllegalArgumentException(
                        "동일한 대운 십성 Source Type이 중복되었습니다: "
                                + sourceType
                );
            }
        }
    }

    private int sourceOrder(
            DecadeLuckTenGodSourceType sourceType
    ) {

        return switch (
                sourceType
                ) {

            case VISIBLE_STEM ->
                    0;

            case BRANCH_MAIN ->
                    1;

            case BRANCH_MIDDLE ->
                    2;

            case BRANCH_RESIDUAL ->
                    3;
        };
    }
}