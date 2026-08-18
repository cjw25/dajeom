package com.dajeom.analyzer;

import com.dajeom.engine.LifeDomainAnalysisEngine;
import com.dajeom.engine.LuckIntegratedAnalysisEngine;
import com.dajeom.engine.ShinsalAnalysisEngine;
import com.dajeom.model.ConsultationTimelineRangeAnalysis;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainEvidenceResolution;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.ShinsalAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.resolver.LifeDomainDecadeContextSourceResolver;
import com.dajeom.resolver.LifeDomainEvidenceResolver;
import com.dajeom.resolver.LifeDomainNatalEvidenceSourceResolver;
import com.dajeom.resolver.LifeDomainNatalEvidenceSourceResolverFactory;
import com.dajeom.resolver.LuckIntegratedLayerAnalysisResolver;
import com.dajeom.resolver.LuckIntegratedLayerAnalysisResolverFactory;
import com.dajeom.resolver.ShinsalNatalPointSourceResolver;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 21차-2.
 *
 * PAST_LUCK / FUTURE_LUCK용
 * deterministic Timeline Range Analyzer.
 *
 * =================================================
 *
 * 이 클래스는 새로운 명리 공식을 만들지 않는다.
 *
 * 각 기준 시점마다 기존:
 *
 * 16차 LuckIntegratedAnalysis
 * 17차 ShinsalAnalysis
 * 18차 LifeDomainAnalysis
 *
 * 를 그대로 실행한다.
 *
 * =================================================
 *
 * 기본 Range:
 *
 * 과거 5년
 * 현재연도 제외
 *
 * 미래 5년
 * 현재연도 제외
 *
 * =================================================
 *
 * 각 명리연도는 12개월의 대표 내부 시점을 사용한다.
 *
 * 2월 ~ 12월 + 다음해 1월의 15일.
 *
 * 이유:
 *
 * 명리연도는 입춘 기준이며
 * 월운은 절입 기준으로 움직이므로
 * Gregorian 1월~12월만 단순 사용하지 않는다.
 *
 * 각 날짜에서도 실제 운 선택은
 * 16차 Candidate/Timeline Engine이 다시 수행한다.
 */
@Component
public class ConsultationTimelineRangeAnalyzer {

    public static final int PAST_YEAR_COUNT =
            5;

    public static final int FUTURE_YEAR_COUNT =
            5;

    private static final int REPRESENTATIVE_DAY =
            15;


    private final LuckIntegratedAnalysisEngine
            luckIntegratedAnalysisEngine;

    private final LuckIntegratedLayerAnalysisResolverFactory
            layerAnalysisResolverFactory;

    private final ShinsalAnalysisEngine
            shinsalAnalysisEngine;

    private final ShinsalNatalPointSourceResolver
            shinsalNatalPointSourceResolver;

    private final LifeDomainEvidenceResolver
            lifeDomainEvidenceResolver;

    private final LifeDomainAnalysisEngine
            lifeDomainAnalysisEngine;

    private final LifeDomainNatalEvidenceSourceResolverFactory
            natalEvidenceSourceResolverFactory;

    private final LifeDomainDecadeContextSourceResolver
            decadeContextSourceResolver;


    public ConsultationTimelineRangeAnalyzer(

            LuckIntegratedAnalysisEngine
                    luckIntegratedAnalysisEngine,

            LuckIntegratedLayerAnalysisResolverFactory
                    layerAnalysisResolverFactory,

            ShinsalAnalysisEngine
                    shinsalAnalysisEngine,

            ShinsalNatalPointSourceResolver
                    shinsalNatalPointSourceResolver,

            LifeDomainEvidenceResolver
                    lifeDomainEvidenceResolver,

            LifeDomainAnalysisEngine
                    lifeDomainAnalysisEngine,

            LifeDomainNatalEvidenceSourceResolverFactory
                    natalEvidenceSourceResolverFactory,

            LifeDomainDecadeContextSourceResolver
                    decadeContextSourceResolver

    ) {

        this.luckIntegratedAnalysisEngine =
                Objects.requireNonNull(
                        luckIntegratedAnalysisEngine,
                        "LuckIntegratedAnalysisEngine이 필요합니다."
                );

        this.layerAnalysisResolverFactory =
                Objects.requireNonNull(
                        layerAnalysisResolverFactory,
                        "LuckIntegratedLayerAnalysisResolverFactory가 필요합니다."
                );

        this.shinsalAnalysisEngine =
                Objects.requireNonNull(
                        shinsalAnalysisEngine,
                        "ShinsalAnalysisEngine이 필요합니다."
                );

        this.shinsalNatalPointSourceResolver =
                Objects.requireNonNull(
                        shinsalNatalPointSourceResolver,
                        "ShinsalNatalPointSourceResolver가 필요합니다."
                );

        this.lifeDomainEvidenceResolver =
                Objects.requireNonNull(
                        lifeDomainEvidenceResolver,
                        "LifeDomainEvidenceResolver가 필요합니다."
                );

        this.lifeDomainAnalysisEngine =
                Objects.requireNonNull(
                        lifeDomainAnalysisEngine,
                        "LifeDomainAnalysisEngine이 필요합니다."
                );

        this.natalEvidenceSourceResolverFactory =
                Objects.requireNonNull(
                        natalEvidenceSourceResolverFactory,
                        "LifeDomainNatalEvidenceSourceResolverFactory가 필요합니다."
                );

        this.decadeContextSourceResolver =
                Objects.requireNonNull(
                        decadeContextSourceResolver,
                        "LifeDomainDecadeContextSourceResolver가 필요합니다."
                );
    }


    public ConsultationTimelineRangeAnalysis analyze(

            LocalDate currentReferenceDate,

            ZoneId zoneId,

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis

    ) {

        Objects.requireNonNull(
                currentReferenceDate,
                "Timeline Range 기준날짜가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "Timeline Range ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                advancedAnalysis,
                "Timeline Range에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "Timeline Range에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "Timeline Range에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "Timeline Range에는 DecadeLuckAnalysis가 필요합니다."
        );


        int currentYear =
                currentReferenceDate
                        .getYear();


        LuckIntegratedLayerAnalysisResolver
                layerResolver =
                layerAnalysisResolverFactory
                        .create(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                palaceAnalysis
                        );


        LifeDomainNatalEvidenceSourceResolver
                natalEvidenceResolver =
                natalEvidenceSourceResolverFactory
                        .create(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                palaceAnalysis
                        );


        List<LifeDomainAnalysis>
                past =
                new ArrayList<>();


        for (
                int year =
                currentYear
                        - PAST_YEAR_COUNT;

                year
                        < currentYear;

                year++
        ) {

            past.addAll(
                    analyzeSajuYear(
                            year,
                            zoneId,
                            advancedAnalysis,
                            palaceAnalysis,
                            decadeLuckAnalysis,
                            layerResolver,
                            natalEvidenceResolver
                    )
            );
        }


        List<LifeDomainAnalysis>
                future =
                new ArrayList<>();


        for (
                int year =
                currentYear + 1;

                year
                        <= currentYear
                        + FUTURE_YEAR_COUNT;

                year++
        ) {

            future.addAll(
                    analyzeSajuYear(
                            year,
                            zoneId,
                            advancedAnalysis,
                            palaceAnalysis,
                            decadeLuckAnalysis,
                            layerResolver,
                            natalEvidenceResolver
                    )
            );
        }


        return new ConsultationTimelineRangeAnalysis(
                currentReferenceDate,
                past,
                future
        );
    }


    /*
     * =================================================
     * 하나의 명리연도
     * =================================================
     *
     * targetYear:
     *
     * 2월 ~ 12월
     * +
     * targetYear + 1의 1월
     *
     * 총 12개 기준점.
     *
     * 실제 Annual/Monthly Candidate 판단은
     * 16차가 수행한다.
     */

    private List<LifeDomainAnalysis> analyzeSajuYear(

            int targetYear,

            ZoneId zoneId,

            NatalChartAdvancedAnalysis advancedAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            LuckIntegratedLayerAnalysisResolver
                    layerResolver,

            LifeDomainNatalEvidenceSourceResolver
                    natalEvidenceResolver

    ) {

        List<LifeDomainAnalysis> result =
                new ArrayList<>(
                        12
                );


        for (
                int month = 2;
                month <= 12;
                month++
        ) {

            result.add(
                    analyzeReferenceDate(
                            LocalDate.of(
                                    targetYear,
                                    month,
                                    REPRESENTATIVE_DAY
                            ),
                            zoneId,
                            palaceAnalysis,
                            decadeLuckAnalysis,
                            layerResolver,
                            natalEvidenceResolver
                    )
            );
        }


        result.add(
                analyzeReferenceDate(
                        LocalDate.of(
                                targetYear + 1,
                                1,
                                REPRESENTATIVE_DAY
                        ),
                        zoneId,
                        palaceAnalysis,
                        decadeLuckAnalysis,
                        layerResolver,
                        natalEvidenceResolver
                )
        );


        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * 한 기준날짜
     * =================================================
     */

    private LifeDomainAnalysis analyzeReferenceDate(

            LocalDate referenceDate,

            ZoneId zoneId,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            LuckIntegratedLayerAnalysisResolver
                    layerResolver,

            LifeDomainNatalEvidenceSourceResolver
                    natalEvidenceResolver

    ) {

        LuckReferenceContext referenceContext =
                LuckReferenceContext
                        .dateOnly(
                                referenceDate,
                                zoneId
                        );


        LuckIntegratedAnalysis
                integratedAnalysis =
                Objects.requireNonNull(
                        luckIntegratedAnalysisEngine
                                .analyze(
                                        palaceAnalysis,
                                        decadeLuckAnalysis,
                                        referenceContext,
                                        layerResolver
                                ),
                        "Timeline Range 16차 분석이 null을 반환했습니다."
                );


        if (
                integratedAnalysis
                        .natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalStateException(
                    "Timeline Range 16차 분석이 현재 PalaceAnalysis를 보존하지 않았습니다."
            );
        }


        if (
                integratedAnalysis
                        .decadeLuckAnalysis()
                        != decadeLuckAnalysis
        ) {

            throw new IllegalStateException(
                    "Timeline Range 16차 분석이 현재 DecadeLuckAnalysis를 보존하지 않았습니다."
            );
        }


        if (
                integratedAnalysis
                        .referenceContext()
                        != referenceContext
        ) {

            throw new IllegalStateException(
                    "Timeline Range 16차 분석이 현재 LuckReferenceContext Identity를 보존하지 않았습니다."
            );
        }


        ShinsalAnalysis
                shinsalAnalysis =
                Objects.requireNonNull(
                        shinsalAnalysisEngine
                                .analyze(
                                        integratedAnalysis,
                                        shinsalNatalPointSourceResolver
                                ),
                        "Timeline Range 17차 분석이 null을 반환했습니다."
                );


        if (
                shinsalAnalysis
                        .luckIntegratedAnalysis()
                        != integratedAnalysis
        ) {

            throw new IllegalStateException(
                    "Timeline Range 17차 분석이 현재 LuckIntegratedAnalysis를 보존하지 않았습니다."
            );
        }


        LifeDomainEvidenceResolution
                evidenceResolution =
                Objects.requireNonNull(
                        lifeDomainEvidenceResolver
                                .resolve(
                                        integratedAnalysis,
                                        shinsalAnalysis,
                                        natalEvidenceResolver,
                                        decadeContextSourceResolver
                                ),
                        "Timeline Range 18차 Evidence Resolution이 null을 반환했습니다."
                );


        LifeDomainAnalysis
                lifeDomainAnalysis =
                Objects.requireNonNull(
                        lifeDomainAnalysisEngine
                                .analyze(
                                        evidenceResolution
                                ),
                        "Timeline Range 18차 분석이 null을 반환했습니다."
                );


        if (
                lifeDomainAnalysis
                        .evidenceResolution()
                        != evidenceResolution
        ) {

            throw new IllegalStateException(
                    "Timeline Range 18차 LifeDomainAnalysis가 현재 EvidenceResolution Identity를 보존하지 않았습니다."
            );
        }


        return lifeDomainAnalysis;
    }
}