package com.dajeom.analyzer;

import com.dajeom.model.ConsultationSection;
import com.dajeom.model.InterpretationEvidence;
import com.dajeom.model.InterpretationSignal;
import com.dajeom.rule.ConsultationRule;
import com.dajeom.type.AnalysisConfidence;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * InterpretationSignal 하나를
 * 사용자용 ConsultationSection으로
 * 변환한다.
 *
 * 이 Analyzer에서는:
 *
 * - 상담 분야 제목
 * - 상담 시작 문장
 * - 기본 요약 문장
 * - 현실적인 조언
 * - 근거 종류
 * - 신뢰도
 *
 * 를 구성한다.
 */
@Component
public class ConsultationSectionAnalyzer {

    public ConsultationSection analyze(
            InterpretationSignal signal
    ) {

        Objects.requireNonNull(
                signal,
                "InterpretationSignal은 필수입니다."
        );

        List<InterpretationEvidence> evidences =
                signal.evidences();

        AnalysisConfidence confidence =
                ConsultationRule
                        .determineConfidence(
                                evidences
                        );

        List<String> basisLabels =
                evidences
                        .stream()
                        .map(
                                evidence ->
                                        evidence
                                                .type()
                                                .getKoreanName()
                        )
                        .distinct()
                        .toList();

        return new ConsultationSection(
                signal
                        .category()
                        .getOrder(),
                signal.category(),
                signal
                        .category()
                        .getKoreanName(),
                signal.polarity(),
                signal.intensity(),
                confidence,
                ConsultationRule.openingFor(
                        signal.category()
                ),
                ConsultationRule.summaryFor(
                        signal
                ),
                ConsultationRule.guidanceFor(
                        signal.category(),
                        signal.polarity()
                ),
                basisLabels,
                evidences.size()
        );
    }
}