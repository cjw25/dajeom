package com.dajeom.engine;

import com.dajeom.analyzer.AnnualLuckCandidateAnalyzer;
import com.dajeom.analyzer.DecadeLuckCandidateAnalyzer;
import com.dajeom.analyzer.LuckTimelineAnalyzer;
import com.dajeom.analyzer.LuckUncertaintyAnalyzer;
import com.dajeom.analyzer.MonthlyLuckCandidateAnalyzer;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckCandidate;
import com.dajeom.model.AnnualLuckSelectionAnalysis;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckSelectionAnalysis;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckIntegratedSegmentAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.model.LuckTimelineAnalysis;
import com.dajeom.model.LuckTimelineSegment;
import com.dajeom.model.LuckUncertaintyAnalysis;
import com.dajeom.model.MonthlyLuckAnalysis;
import com.dajeom.model.MonthlyLuckCandidate;
import com.dajeom.model.MonthlyLuckSelectionAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.resolver.LuckIntegratedLayerAnalysisResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 16차-7.
 *
 * 원국 + 대운 + 세운 + 월운
 * 최종 통합 분석 Engine.
 *
 * =================================================
 *
 * 처리 순서:
 *
 * 16-2 대운 Candidate
 *
 * 16-3 세운 Candidate
 *
 * 16-4 월운 Candidate
 *
 * 16-5 실제 Timeline
 *
 * 16-6 불확실성
 *
 * 14차 세운 분석 연결
 *
 * 15차 월운 분석 연결
 *
 * 16-7 LuckIntegratedAnalysis
 *
 * =================================================
 *
 * 이 Engine은 새로운 명리 규칙을
 * 계산하지 않는다.
 */
@Component
public class LuckIntegratedAnalysisEngine {

    private final DecadeLuckCandidateAnalyzer
            decadeLuckCandidateAnalyzer;

    private final AnnualLuckCandidateAnalyzer
            annualLuckCandidateAnalyzer;

    private final MonthlyLuckCandidateAnalyzer
            monthlyLuckCandidateAnalyzer;

    private final LuckTimelineAnalyzer
            luckTimelineAnalyzer;

    private final LuckUncertaintyAnalyzer
            luckUncertaintyAnalyzer;

    public LuckIntegratedAnalysisEngine(

            DecadeLuckCandidateAnalyzer
                    decadeLuckCandidateAnalyzer,

            AnnualLuckCandidateAnalyzer
                    annualLuckCandidateAnalyzer,

            MonthlyLuckCandidateAnalyzer
                    monthlyLuckCandidateAnalyzer,

            LuckTimelineAnalyzer
                    luckTimelineAnalyzer,

            LuckUncertaintyAnalyzer
                    luckUncertaintyAnalyzer

    ) {

        this.decadeLuckCandidateAnalyzer =
                Objects.requireNonNull(
                        decadeLuckCandidateAnalyzer,
                        "DecadeLuckCandidateAnalyzer가 필요합니다."
                );

        this.annualLuckCandidateAnalyzer =
                Objects.requireNonNull(
                        annualLuckCandidateAnalyzer,
                        "AnnualLuckCandidateAnalyzer가 필요합니다."
                );

        this.monthlyLuckCandidateAnalyzer =
                Objects.requireNonNull(
                        monthlyLuckCandidateAnalyzer,
                        "MonthlyLuckCandidateAnalyzer가 필요합니다."
                );

        this.luckTimelineAnalyzer =
                Objects.requireNonNull(
                        luckTimelineAnalyzer,
                        "LuckTimelineAnalyzer가 필요합니다."
                );

        this.luckUncertaintyAnalyzer =
                Objects.requireNonNull(
                        luckUncertaintyAnalyzer,
                        "LuckUncertaintyAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 16-7 API
     * =================================================
     */
    public LuckIntegratedAnalysis analyze(

            PalaceAnalysis natalAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            LuckReferenceContext referenceContext,

            LuckIntegratedLayerAnalysisResolver
                    layerAnalysisResolver

    ) {

        Objects.requireNonNull(
                natalAnalysis,
                "16-7에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "16-7에는 DecadeLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                referenceContext,
                "16-7에는 LuckReferenceContext가 필요합니다."
        );

        Objects.requireNonNull(
                layerAnalysisResolver,
                "LuckIntegratedLayerAnalysisResolver가 필요합니다."
        );

        /*
         * =================================================
         * 16-2
         * 대운 Candidate
         * =================================================
         */

        DecadeLuckSelectionAnalysis decadeSelection =
                decadeLuckCandidateAnalyzer
                        .analyze(
                                referenceContext,
                                decadeLuckAnalysis
                        );

        /*
         * =================================================
         * 16-3
         * 세운 Candidate
         * =================================================
         */

        AnnualLuckSelectionAnalysis annualSelection =
                annualLuckCandidateAnalyzer
                        .analyze(
                                referenceContext
                        );

        /*
         * =================================================
         * 16-4
         * 월운 Candidate
         * =================================================
         */

        MonthlyLuckSelectionAnalysis monthlySelection =
                monthlyLuckCandidateAnalyzer
                        .analyze(
                                annualSelection
                        );

        /*
         * =================================================
         * 16-5
         * 실제 시간 Timeline
         * =================================================
         */

        LuckTimelineAnalysis timelineAnalysis =
                luckTimelineAnalyzer
                        .analyze(
                                natalAnalysis,
                                decadeSelection,
                                annualSelection,
                                monthlySelection
                        );

        /*
         * =================================================
         * 16-6
         * 불확실성
         * =================================================
         */

        LuckUncertaintyAnalysis uncertaintyAnalysis =
                luckUncertaintyAnalyzer
                        .analyze(
                                timelineAnalysis
                        );

        /*
         * =================================================
         * 선택된 AnnualLuckPeriod별
         * 14차 분석.
         *
         * IdentityHashMap을 사용한다.
         *
         * equals가 아니라
         * 동일 Candidate 객체 기준이다.
         * =================================================
         */

        Map<AnnualLuckCandidate, AnnualLuckAnalysis>
                annualAnalysisMap =
                new IdentityHashMap<>();

        for (
                AnnualLuckCandidate annualCandidate
                : annualSelection.candidates()
        ) {

            AnnualLuckAnalysis annualLuckAnalysis =
                    Objects.requireNonNull(
                            layerAnalysisResolver
                                    .resolveAnnual(
                                            natalAnalysis,
                                            decadeLuckAnalysis,
                                            annualCandidate
                                    ),
                            "LuckIntegratedLayerAnalysisResolver.resolveAnnual()이 null을 반환했습니다."
                    );

            validateAnnualAnalysis(
                    natalAnalysis,
                    decadeLuckAnalysis,
                    annualCandidate,
                    annualLuckAnalysis
            );

            annualAnalysisMap.put(
                    annualCandidate,
                    annualLuckAnalysis
            );
        }

        /*
         * =================================================
         * 선택된 MonthlyLuckPeriod별
         * 15차 분석.
         * =================================================
         */

        Map<MonthlyLuckCandidate, MonthlyLuckAnalysis>
                monthlyAnalysisMap =
                new IdentityHashMap<>();

        for (
                MonthlyLuckCandidate monthlyCandidate
                : monthlySelection.candidates()
        ) {

            AnnualLuckAnalysis annualLuckAnalysis =
                    annualAnalysisMap
                            .get(
                                    monthlyCandidate
                                            .annualCandidate()
                            );

            if (
                    annualLuckAnalysis == null
            ) {

                throw new IllegalStateException(
                        "월운 Candidate의 상위 AnnualLuckAnalysis를 찾을 수 없습니다."
                );
            }

            MonthlyLuckAnalysis monthlyLuckAnalysis =
                    Objects.requireNonNull(
                            layerAnalysisResolver
                                    .resolveMonthly(
                                            annualLuckAnalysis,
                                            monthlyCandidate
                                    ),
                            "LuckIntegratedLayerAnalysisResolver.resolveMonthly()이 null을 반환했습니다."
                    );

            validateMonthlyAnalysis(
                    annualLuckAnalysis,
                    monthlyCandidate,
                    monthlyLuckAnalysis
            );

            monthlyAnalysisMap.put(
                    monthlyCandidate,
                    monthlyLuckAnalysis
            );
        }

        /*
         * =================================================
         * Timeline Segment
         * +
         * 14/15차 분석
         * =================================================
         */

        List<LuckIntegratedSegmentAnalysis>
                integratedSegments =
                new ArrayList<>(
                        timelineAnalysis
                                .segments()
                                .size()
                );

        for (
                LuckTimelineSegment timelineSegment
                : timelineAnalysis.segments()
        ) {

            AnnualLuckAnalysis annualLuckAnalysis =
                    annualAnalysisMap
                            .get(
                                    timelineSegment
                                            .annualCandidate()
                            );

            if (
                    annualLuckAnalysis == null
            ) {

                throw new IllegalStateException(
                        "Timeline Segment의 AnnualLuckAnalysis를 찾을 수 없습니다."
                );
            }

            MonthlyLuckAnalysis monthlyLuckAnalysis =
                    monthlyAnalysisMap
                            .get(
                                    timelineSegment
                                            .monthlyCandidate()
                            );

            if (
                    monthlyLuckAnalysis == null
            ) {

                throw new IllegalStateException(
                        "Timeline Segment의 MonthlyLuckAnalysis를 찾을 수 없습니다."
                );
            }

            integratedSegments.add(
                    new LuckIntegratedSegmentAnalysis(
                            timelineSegment,
                            annualLuckAnalysis,
                            monthlyLuckAnalysis
                    )
            );
        }

        /*
         * =================================================
         * 최종 16차 Aggregate
         * =================================================
         */

        return new LuckIntegratedAnalysis(
                natalAnalysis,
                decadeLuckAnalysis,
                timelineAnalysis,
                uncertaintyAnalysis,
                integratedSegments
        );
    }

    /*
     * =================================================
     * 14차 Resolution 검증
     * =================================================
     */

    private void validateAnnualAnalysis(

            PalaceAnalysis natalAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualLuckCandidate annualCandidate,

            AnnualLuckAnalysis annualLuckAnalysis

    ) {

        /*
         * Selection Period 객체를
         * 그대로 사용해야 한다.
         */

        if (
                annualLuckAnalysis.period()
                        != annualCandidate.period()
        ) {

            throw new IllegalArgumentException(
                    "14차 AnnualLuckAnalysis가 16-3에서 선택된 AnnualLuckPeriod 객체를 그대로 사용하지 않았습니다."
            );
        }

        if (
                annualLuckAnalysis.natalAnalysis()
                        != natalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14차 AnnualLuckAnalysis가 현재 원국을 참조하지 않습니다."
            );
        }

        if (
                annualLuckAnalysis.decadeLuckAnalysis()
                        != decadeLuckAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14차 AnnualLuckAnalysis가 현재 DecadeLuckAnalysis를 참조하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 15차 Resolution 검증
     * =================================================
     */

    private void validateMonthlyAnalysis(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckCandidate monthlyCandidate,

            MonthlyLuckAnalysis monthlyLuckAnalysis

    ) {

        if (
                monthlyLuckAnalysis.period()
                        != monthlyCandidate.period()
        ) {

            throw new IllegalArgumentException(
                    "15차 MonthlyLuckAnalysis가 16-4에서 선택된 MonthlyLuckPeriod 객체를 그대로 사용하지 않았습니다."
            );
        }

        if (
                monthlyLuckAnalysis.annualLuckAnalysis()
                        != annualLuckAnalysis
        ) {

            throw new IllegalArgumentException(
                    "15차 MonthlyLuckAnalysis가 현재 AnnualLuckAnalysis를 참조하지 않습니다."
            );
        }

        if (
                monthlyLuckAnalysis.period()
                        .annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "15차 월운과 14차 세운의 Period Identity가 일치하지 않습니다."
            );
        }
    }
}