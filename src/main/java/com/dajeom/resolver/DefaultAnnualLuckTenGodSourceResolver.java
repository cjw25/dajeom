package com.dajeom.resolver;

import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.calculator.StructureTenGodCalculator;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.AnnualLuckResolvedTenGodSource;
import com.dajeom.model.HiddenStem;
import com.dajeom.type.AnnualLuckTenGodSourceType;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 14차-3.
 *
 * 세운 십성 Source Resolver 기본 구현체.
 *
 * =================================================
 *
 * canonical 재사용:
 *
 * HiddenStemCalculator
 * +
 * StructureTenGodCalculator
 *
 * =================================================
 *
 * 처리:
 *
 * 1.
 * 세운 천간
 * -> VISIBLE_STEM
 *
 * 2.
 * 세운 지지 지장간
 *
 * 정기
 * -> BRANCH_MAIN
 *
 * 중기
 * -> BRANCH_MIDDLE
 *
 * 여기
 * -> BRANCH_RESIDUAL
 *
 * =================================================
 *
 * 중요:
 *
 * - 지장간표를 14차에서 다시 정의하지 않는다.
 * - 십성 공식을 14차에서 다시 구현하지 않는다.
 * - HiddenStem weight로 역할을 추측하지 않는다.
 * - HiddenStem.role 기반 canonical 구분을 사용한다.
 * - 세운 천간이 일간과 같아도 DAY MASTER Anchor가 아니다.
 *   운에서 유입된 외부 십성으로 계산한다.
 */
@Component
public class DefaultAnnualLuckTenGodSourceResolver
        implements AnnualLuckTenGodSourceResolver {

    private final HiddenStemCalculator
            hiddenStemCalculator;

    private final StructureTenGodCalculator
            structureTenGodCalculator;

    public DefaultAnnualLuckTenGodSourceResolver(

            HiddenStemCalculator
                    hiddenStemCalculator,

            StructureTenGodCalculator
                    structureTenGodCalculator

    ) {

        this.hiddenStemCalculator =
                Objects.requireNonNull(
                        hiddenStemCalculator,
                        "HiddenStemCalculator가 필요합니다."
                );

        this.structureTenGodCalculator =
                Objects.requireNonNull(
                        structureTenGodCalculator,
                        "StructureTenGodCalculator가 필요합니다."
                );
    }

    @Override
    public List<AnnualLuckResolvedTenGodSource> resolve(

            HeavenlyStem dayMaster,

            AnnualLuckPeriod annualLuckPeriod

    ) {

        Objects.requireNonNull(
                dayMaster,
                "세운 십성 계산에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckPeriod,
                "세운 십성 계산에는 AnnualLuckPeriod가 필요합니다."
        );

        List<AnnualLuckResolvedTenGodSource> results =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 세운 천간
         * =================================================
         */

        HeavenlyStem annualStem =
                annualLuckPeriod.stem();

        StructureTenGodType visibleTenGodType =
                structureTenGodCalculator
                        .calculate(
                                dayMaster,
                                annualStem
                        );

        results.add(
                new AnnualLuckResolvedTenGodSource(
                        AnnualLuckTenGodSourceType
                                .VISIBLE_STEM,
                        annualStem,
                        visibleTenGodType
                )
        );

        /*
         * =================================================
         * 2.
         * 세운 지지 지장간
         * =================================================
         *
         * 지장간 순서를 보고
         * 본기/중기/여기를 추정하지 않는다.
         *
         * HiddenStem 자체의 role을 사용한다.
         */

        List<HiddenStem> hiddenStems =
                hiddenStemCalculator
                        .calculate(
                                annualLuckPeriod.branch()
                        );

        for (
                HiddenStem hiddenStem
                : hiddenStems
        ) {

            AnnualLuckTenGodSourceType sourceType =
                    resolveSourceType(
                            hiddenStem
                    );

            StructureTenGodType tenGodType =
                    structureTenGodCalculator
                            .calculate(
                                    dayMaster,
                                    hiddenStem.stem()
                            );

            results.add(
                    new AnnualLuckResolvedTenGodSource(
                            sourceType,
                            hiddenStem.stem(),
                            tenGodType
                    )
            );
        }

        /*
         * =================================================
         * 출력 안정성
         * =================================================
         *
         * 중요도 순서가 아니다.
         *
         * 천간
         * 본기
         * 중기
         * 여기
         */

        results.sort(
                Comparator.comparingInt(
                        result ->
                                sourceOrder(
                                        result.sourceType()
                                )
                )
        );

        validateResolvedSources(
                annualLuckPeriod,
                results
        );

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * HiddenStem Role -> Annual Source Type
     * =================================================
     */

    private AnnualLuckTenGodSourceType resolveSourceType(
            HiddenStem hiddenStem
    ) {

        Objects.requireNonNull(
                hiddenStem,
                "지장간이 필요합니다."
        );

        if (
                hiddenStem.main()
        ) {

            return AnnualLuckTenGodSourceType
                    .BRANCH_MAIN;
        }

        if (
                hiddenStem.middle()
        ) {

            return AnnualLuckTenGodSourceType
                    .BRANCH_MIDDLE;
        }

        if (
                hiddenStem.residual()
        ) {

            return AnnualLuckTenGodSourceType
                    .BRANCH_RESIDUAL;
        }

        /*
         * HiddenStemRole은
         * 정기/중기/여기 중 하나여야 한다.
         *
         * 역할이 추가되더라도 여기서 조용히
         * 잘못 매핑하지 않고 즉시 실패시킨다.
         */
        throw new IllegalStateException(
                "지원하지 않는 지장간 역할입니다: "
                        + hiddenStem.role()
        );
    }

    /*
     * =================================================
     * Resolver 내부 정합성 검증
     * =================================================
     *
     * AnnualLuckTenGodAnalyzer에서도 다시 검증하지만,
     * Resolver 단에서 잘못된 canonical Source를
     * 즉시 차단한다.
     */

    private void validateResolvedSources(

            AnnualLuckPeriod annualLuckPeriod,

            List<AnnualLuckResolvedTenGodSource> sources

    ) {

        if (
                sources.isEmpty()
        ) {

            throw new IllegalStateException(
                    "세운 십성 Source를 생성하지 못했습니다: "
                            + annualLuckPeriod.targetYear()
            );
        }

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

            throw new IllegalStateException(
                    "세운 천간 Source는 정확히 하나여야 합니다."
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
                        != annualLuckPeriod.stem()
        ) {

            throw new IllegalStateException(
                    "세운 천간 Source가 실제 세운 천간과 일치하지 않습니다."
            );
        }

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

            throw new IllegalStateException(
                    "세운 지지에는 정확히 하나의 본기 Source가 필요합니다."
            );
        }

        /*
         * 동일 역할 중복 차단.
         *
         * canonical HiddenStemTable이 잘못 변경된 경우
         * Analyzer까지 넘어가기 전에 잡는다.
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

                throw new IllegalStateException(
                        "동일 세운 십성 Source Type이 중복되었습니다: "
                                + sourceType
                );
            }
        }
    }

    private int sourceOrder(
            AnnualLuckTenGodSourceType sourceType
    ) {

        Objects.requireNonNull(
                sourceType,
                "세운 십성 Source Type이 필요합니다."
        );

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