package com.dajeom.engine;

import com.dajeom.analyzer.ShinsalEvidenceAnalyzer;
import com.dajeom.analyzer.ShinsalTimelineAnalyzer;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.ShinsalAnalysis;
import com.dajeom.model.ShinsalRuleContextResolution;
import com.dajeom.model.ShinsalTimelineAnalysis;
import com.dajeom.resolver.ShinsalNatalPointSourceResolver;
import com.dajeom.resolver.ShinsalRuleContextResolver;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 17차-6.
 *
 * 신살 분석 최종 Engine.
 *
 * =================================================
 *
 * 처리:
 *
 * LuckIntegratedAnalysis
 *
 * ↓
 *
 * 17-4
 * ShinsalRuleContextResolution
 *
 * ↓
 *
 * 17-5
 * ShinsalTimelineAnalysis
 *
 * ↓
 *
 * 17-6
 * ShinsalAnalysis
 *
 * =================================================
 *
 * 이 Engine은 새로운 신살 Rule을 계산하지 않는다.
 *
 * 실제 천을귀인/문창/역마/도화/화개 계산은
 * 등록된 ShinsalRule 구현체들이 담당한다.
 */
@Component
public class ShinsalAnalysisEngine {

    private final ShinsalRuleContextResolver
            shinsalRuleContextResolver;

    private final ShinsalTimelineAnalyzer
            shinsalTimelineAnalyzer;

    private final ShinsalEvidenceAnalyzer
            shinsalEvidenceAnalyzer;

    public ShinsalAnalysisEngine(

            ShinsalRuleContextResolver
                    shinsalRuleContextResolver,

            ShinsalTimelineAnalyzer
                    shinsalTimelineAnalyzer,

            ShinsalEvidenceAnalyzer
                    shinsalEvidenceAnalyzer

    ) {

        this.shinsalRuleContextResolver =
                Objects.requireNonNull(
                        shinsalRuleContextResolver,
                        "ShinsalRuleContextResolver가 필요합니다."
                );

        this.shinsalTimelineAnalyzer =
                Objects.requireNonNull(
                        shinsalTimelineAnalyzer,
                        "ShinsalTimelineAnalyzer가 필요합니다."
                );

        this.shinsalEvidenceAnalyzer =
                Objects.requireNonNull(
                        shinsalEvidenceAnalyzer,
                        "ShinsalEvidenceAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 17차 최종 API
     * =================================================
     */
    public ShinsalAnalysis analyze(

            LuckIntegratedAnalysis luckIntegratedAnalysis,

            ShinsalNatalPointSourceResolver
                    natalPointSourceResolver

    ) {

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "신살 분석에는 LuckIntegratedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalPointSourceResolver,
                "신살 분석에는 ShinsalNatalPointSourceResolver가 필요합니다."
        );

        /*
         * =================================================
         * 17-4
         * =================================================
         */

        ShinsalRuleContextResolution contextResolution =
                shinsalRuleContextResolver
                        .resolve(
                                luckIntegratedAnalysis,
                                natalPointSourceResolver
                        );

        /*
         * =================================================
         * 17-5
         * =================================================
         */

        ShinsalTimelineAnalysis timelineAnalysis =
                shinsalTimelineAnalyzer
                        .analyze(
                                contextResolution
                        );

        /*
         * =================================================
         * 17-6
         * =================================================
         */

        return shinsalEvidenceAnalyzer
                .analyze(
                        timelineAnalysis
                );
    }
}