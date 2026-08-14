package com.dajeom.analyzer;

import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.MonthlyLuckResolvedTenGodSource;
import com.dajeom.model.MonthlyLuckTenGodAnalysis;
import com.dajeom.model.MonthlyLuckTenGodEvidence;
import com.dajeom.resolver.MonthlyLuckTenGodSourceResolver;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthlyLuckTenGodSourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-3.
 *
 * 월운 천간·지지 십성 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * 원국 일간
 * +
 * MonthlyLuckPeriod
 * +
 * 기존 지장간/십성 Resolver
 *
 * =================================================
 *
 * 출력:
 *
 * MonthlyLuckTenGodAnalysis
 *
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 원국 십성 분포를 변경하지 않는다.
 * - 대운 십성 분석을 변경하지 않는다.
 * - 세운 십성 분석을 변경하지 않는다.
 * - 신강신약을 다시 계산하지 않는다.
 * - 격국을 다시 판정하지 않는다.
 *
 * 월운에서 들어오는 Incoming Evidence만
 * 별도로 생성한다.
 */
@Component
public class MonthlyLuckTenGodAnalyzer {

    /**
     * =================================================
     * 공식 15-3 API
     * =================================================
     */
    public MonthlyLuckTenGodAnalysis analyze(

            HeavenlyStem dayMaster,

            MonthlyLuckPeriod monthlyLuckPeriod,

            MonthlyLuckTenGodSourceResolver sourceResolver

    ) {

        Objects.requireNonNull(
                dayMaster,
                "15-3 월운 십성 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckPeriod,
                "15-3 월운 십성 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                sourceResolver,
                "MonthlyLuckTenGodSourceResolver가 필요합니다."
        );

        List<MonthlyLuckResolvedTenGodSource>
                resolvedSources =
                sourceResolver.resolve(
                        dayMaster,
                        monthlyLuckPeriod
                );

        resolvedSources =
                resolvedSources == null
                        ? List.of()
                        : List.copyOf(
                        resolvedSources
                );

        validateResolvedSources(
                monthlyLuckPeriod,
                resolvedSources
        );

        List<MonthlyLuckTenGodEvidence>
                evidences =
                new ArrayList<>();

        for (
                MonthlyLuckResolvedTenGodSource source
                : resolvedSources
        ) {

            evidences.add(
                    new MonthlyLuckTenGodEvidence(
                            dayMaster,
                            monthlyLuckPeriod,
                            source.sourceType(),
                            source.sourceStem(),
                            source.tenGodType()
                    )
            );
        }

        /*
         * =================================================
         * 안정적인 출력 순서
         * =================================================
         *
         * 천간
         * 본기
         * 중기
         * 여기
         *
         * 중요도 점수가 아니라
         * 단순 출력 순서다.
         */

        evidences.sort(
                Comparator.comparingInt(
                        evidence ->
                                sourceOrder(
                                        evidence.sourceType()
                                )
                )
        );

        return new MonthlyLuckTenGodAnalysis(
                dayMaster,
                monthlyLuckPeriod,
                evidences
        );
    }

    /*
     * =================================================
     * Resolver 결과 검증
     * =================================================
     */

    private void validateResolvedSources(

            MonthlyLuckPeriod period,

            List<MonthlyLuckResolvedTenGodSource> sources

    ) {

        if (
                sources.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "월운 십성 Resolver가 빈 결과를 반환했습니다: "
                            + period.sajuYear()
                            + " / monthIndex="
                            + period.monthIndex()
            );
        }

        /*
         * =================================================
         * 천간 정확히 하나
         * =================================================
         */

        long visibleCount =
                sources
                        .stream()
                        .filter(
                                MonthlyLuckResolvedTenGodSource
                                        ::visible
                        )
                        .count();

        if (
                visibleCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월운에는 정확히 하나의 천간 십성 Source가 필요합니다."
            );
        }

        MonthlyLuckResolvedTenGodSource visible =
                sources
                        .stream()
                        .filter(
                                MonthlyLuckResolvedTenGodSource
                                        ::visible
                        )
                        .findFirst()
                        .orElseThrow();

        if (
                visible.sourceStem()
                        != period.stem()
        ) {

            throw new IllegalArgumentException(
                    "Resolver가 반환한 월운 천간이 실제 MonthlyLuckPeriod의 천간과 일치하지 않습니다."
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
                                        source.sourceType()
                                                .mainHiddenStem()
                        )
                        .count();

        if (
                mainCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월운 지지에는 정확히 하나의 본기 Source가 필요합니다."
            );
        }

        /*
         * =================================================
         * Source 중복 방지
         * =================================================
         */

        for (
                MonthlyLuckTenGodSourceType sourceType
                : MonthlyLuckTenGodSourceType.values()
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
                        "동일 월운 십성 Source Type이 중복되었습니다: "
                                + sourceType
                );
            }
        }
    }

    private int sourceOrder(
            MonthlyLuckTenGodSourceType sourceType
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