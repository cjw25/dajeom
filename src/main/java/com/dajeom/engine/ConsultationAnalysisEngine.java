package com.dajeom.engine;

import com.dajeom.analyzer.ConsultationSectionAnalyzer;
import com.dajeom.model.ConsultationAnalysis;
import com.dajeom.model.ConsultationSection;
import com.dajeom.model.InterpretationSignalAnalysis;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.rule.ConsultationRule;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Interpretation 계층의 결과를
 * 실제 사용자 상담 구조로 변환하는 Engine.
 *
 * 입력:
 *
 * InterpretationSignalAnalysis
 * +
 * LuckInteractionAnalysis
 *
 * 처리:
 *
 * InterpretationSignal × 11
 *      ↓
 * ConsultationSectionAnalyzer
 *      ↓
 * ConsultationSection × 11
 *      ↓
 * ConsultationAnalysis
 *
 * 아직 OpenAI 호출은 하지 않는다.
 */
@Component
public class ConsultationAnalysisEngine {

    private final ConsultationSectionAnalyzer
            consultationSectionAnalyzer;

    public ConsultationAnalysisEngine(
            ConsultationSectionAnalyzer
                    consultationSectionAnalyzer
    ) {

        this.consultationSectionAnalyzer =
                consultationSectionAnalyzer;
    }

    public ConsultationAnalysis analyze(
            InterpretationSignalAnalysis signalAnalysis,
            LuckInteractionAnalysis luckInteractionAnalysis
    ) {

        Objects.requireNonNull(
                signalAnalysis,
                "InterpretationSignal 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                luckInteractionAnalysis,
                "운 상호작용 분석 결과는 필수입니다."
        );

        int targetYear =
                luckInteractionAnalysis
                        .annualLuckAnalysis()
                        .targetYear();

        /*
         * 1.
         * 11개 InterpretationSignal을
         * 사용자 상담 영역으로 변환한다.
         */
        List<ConsultationSection> sections =
                signalAnalysis
                        .signals()
                        .stream()
                        .map(
                                consultationSectionAnalyzer::analyze
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        ConsultationSection::order
                                )
                        )
                        .toList();

        /*
         * 2.
         * 전체 상담 결과 조립.
         */
        return new ConsultationAnalysis(
                targetYear,
                ConsultationRule.overallOpening(
                        targetYear
                ),
                sections,
                ConsultationRule.overallClosing(),
                ConsultationRule.RULE_VERSION
        );
    }
}