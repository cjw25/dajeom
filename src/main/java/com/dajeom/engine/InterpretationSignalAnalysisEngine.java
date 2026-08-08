package com.dajeom.engine;

import com.dajeom.analyzer.LuckInterpretationSignalAnalyzer;
import com.dajeom.analyzer.NatalInterpretationSignalAnalyzer;
import com.dajeom.model.InterpretationEvidence;
import com.dajeom.model.InterpretationSignal;
import com.dajeom.model.InterpretationSignalAnalysis;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.rule.InterpretationSignalRule;
import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 명리 분석 결과를
 * 11개 상담 분야의 InterpretationSignal로
 * 변환하는 Engine.
 *
 * 처리:
 *
 * NatalChartAdvancedAnalysis
 *          +
 * LuckInteractionAnalysis
 *
 *          ↓
 *
 * NatalInterpretationSignalAnalyzer
 * LuckInterpretationSignalAnalyzer
 *
 *          ↓
 *
 * InterpretationEvidence
 *
 *          ↓
 *
 * 분야별 집계
 *
 *          ↓
 *
 * InterpretationSignal × 11
 *
 *          ↓
 *
 * InterpretationSignalAnalysis
 */
@Component
public class InterpretationSignalAnalysisEngine {

    private final NatalInterpretationSignalAnalyzer
            natalSignalAnalyzer;

    private final LuckInterpretationSignalAnalyzer
            luckSignalAnalyzer;

    public InterpretationSignalAnalysisEngine(
            NatalInterpretationSignalAnalyzer
                    natalSignalAnalyzer,
            LuckInterpretationSignalAnalyzer
                    luckSignalAnalyzer
    ) {

        this.natalSignalAnalyzer =
                natalSignalAnalyzer;

        this.luckSignalAnalyzer =
                luckSignalAnalyzer;
    }

    public InterpretationSignalAnalysis analyze(
            NatalChartAdvancedAnalysis natalChart,
            LuckInteractionAnalysis luckInteraction
    ) {

        Objects.requireNonNull(
                natalChart,
                "고차 원국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                luckInteraction,
                "운 상호작용 분석 결과는 필수입니다."
        );

        /*
         * 1.
         * 원국에서 발생한 상담 근거.
         */
        List<InterpretationEvidence>
                natalEvidences =
                natalSignalAnalyzer.analyze(
                        natalChart
                );

        /*
         * 2.
         * 대운/세운에서 발생한 상담 근거.
         */
        List<InterpretationEvidence>
                luckEvidences =
                luckSignalAnalyzer.analyze(
                        natalChart,
                        luckInteraction
                );

        /*
         * 3.
         * 모든 근거 통합.
         */
        List<InterpretationEvidence>
                allEvidences =
                new ArrayList<>();

        allEvidences.addAll(
                natalEvidences
        );

        allEvidences.addAll(
                luckEvidences
        );

        /*
         * 4.
         * 11개 분야 모두 생성.
         *
         * 현재 근거가 없는 분야도
         * Signal 객체 자체는 유지한다.
         */
        List<InterpretationSignal> signals =
                new ArrayList<>();

        for (
                ConsultationCategory category
                : ConsultationCategory.values()
        ) {

            signals.add(
                    InterpretationSignalRule
                            .createSignal(
                                    category,
                                    allEvidences
                            )
            );
        }

        /*
         * enum 순서에 의존하지 않고
         * ConsultationCategory의 order 기준으로
         * 결과 순서를 명확하게 고정한다.
         */
        signals.sort(
                Comparator.comparingInt(
                        signal ->
                                signal
                                        .category()
                                        .getOrder()
                )
        );

        return new InterpretationSignalAnalysis(
                signals,
                InterpretationSignalRule.RULE_VERSION
        );
    }
}