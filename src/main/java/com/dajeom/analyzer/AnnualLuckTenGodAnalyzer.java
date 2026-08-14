package com.dajeom.analyzer;

import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.AnnualLuckResolvedTenGodSource;
import com.dajeom.model.AnnualLuckTenGodAnalysis;
import com.dajeom.model.AnnualLuckTenGodEvidence;
import com.dajeom.resolver.AnnualLuckTenGodSourceResolver;
import com.dajeom.type.AnnualLuckTenGodSourceType;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 14차-3.
 *
 * 세운 천간·지지 십성 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * 원국 일간
 * +
 * 14-2 AnnualLuckPeriod
 * +
 * 기존 지장간/십성 Resolver
 *
 * =================================================
 *
 * 출력:
 *
 * AnnualLuckTenGodAnalysis
 *
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 원국 십성 분포를 수정하지 않는다.
 * - 대운 십성 분석을 수정하지 않는다.
 * - 신강신약을 다시 계산하지 않는다.
 * - 격국을 다시 판정하지 않는다.
 *
 * 오직 세운에서 유입되는 십성 Evidence를
 * 별도로 생성한다.
 */
@Component
public class AnnualLuckTenGodAnalyzer {

    /**
     * =================================================
     * 공식 14-3 API
     * =================================================
     */
    public AnnualLuckTenGodAnalysis analyze(

            HeavenlyStem dayMaster,

            AnnualLuckPeriod annualLuckPeriod,

            AnnualLuckTenGodSourceResolver sourceResolver

    ) {

        Objects.requireNonNull(
                dayMaster,
                "14-3 세운 십성 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckPeriod,
                "14-3 세운 십성 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                sourceResolver,
                "AnnualLuckTenGodSourceResolver가 필요합니다."
        );

        List<AnnualLuckResolvedTenGodSource>
                resolvedSources =
                sourceResolver
                        .resolve(
                                dayMaster,
                                annualLuckPeriod
                        );

        resolvedSources =
                resolvedSources == null
                        ? List.of()
                        : List.copyOf(
                        resolvedSources
                );

        validateResolvedSources(
                annualLuckPeriod,
                resolvedSources
        );

        List<AnnualLuckTenGodEvidence>
                evidences =
                new ArrayList<>();

        for (
                AnnualLuckResolvedTenGodSource source
                : resolvedSources
        ) {

            evidences.add(
                    new AnnualLuckTenGodEvidence(
                            dayMaster,
                            annualLuckPeriod,
                            source.sourceType(),
                            source.sourceStem(),
                            source.tenGodType()
                    )
            );
        }

        /*
         * =================================================
         * 출력 순서
         * =================================================
         *
         * 세운 천간
         * 지지 본기
         * 지지 중기
         * 지지 여기
         *
         * 중요도 점수가 아니라
         * 단순 출력 안정성용 순서다.
         */

        evidences.sort(
                Comparator.comparingInt(
                        evidence ->
                                sourceOrder(
                                        evidence.sourceType()
                                )
                )
        );

        return new AnnualLuckTenGodAnalysis(
                dayMaster,
                annualLuckPeriod,
                evidences
        );
    }

    /*
     * =================================================
     * Resolver 결과 검증
     * =================================================
     */

    private void validateResolvedSources(

            AnnualLuckPeriod period,

            List<AnnualLuckResolvedTenGodSource> sources

    ) {

        if (
                sources.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "세운 십성 Resolver가 빈 결과를 반환했습니다: "
                            + period.targetYear()
            );
        }

        /*
         * =================================================
         * 천간 Source 정확히 하나
         * =================================================
         */

        long visibleCount =
                sources
                        .stream()
                        .filter(
                                AnnualLuckResolvedTenGodSource
                                        ::visible
                        )
                        .count();

        if (
                visibleCount != 1
        ) {

            throw new IllegalArgumentException(
                    "세운에는 정확히 하나의 천간 십성 Source가 필요합니다."
            );
        }

        AnnualLuckResolvedTenGodSource visible =
                sources
                        .stream()
                        .filter(
                                AnnualLuckResolvedTenGodSource
                                        ::visible
                        )
                        .findFirst()
                        .orElseThrow();

        if (
                visible.sourceStem()
                        != period.stem()
        ) {

            throw new IllegalArgumentException(
                    "Resolver가 반환한 세운 천간이 실제 세운 천간과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 지지 본기 정확히 하나
         * =================================================
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
                    "세운 지지에는 정확히 하나의 본기 Source가 필요합니다."
            );
        }

        /*
         * =================================================
         * Source Type 중복 방지
         * =================================================
         */

        for (
                AnnualLuckTenGodSourceType sourceType
                : AnnualLuckTenGodSourceType.values()
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
                        "동일 세운 십성 Source Type이 중복되었습니다: "
                                + sourceType
                );
            }
        }
    }

    private int sourceOrder(
            AnnualLuckTenGodSourceType sourceType
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