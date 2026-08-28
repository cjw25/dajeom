package com.dajeom.analyzer;

import com.dajeom.model.ConsultationGroundingDeterministicConclusion;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Consultation deterministic conclusion의
 * 공식 표현 강도 요청값을 계산한다.
 *
 * =================================================
 * 중요한 분리
 * =================================================
 *
 * 이전에는 ConsultationNarrativePlanAnalyzer가
 * 직접 status 문자열을 읽어:
 *
 * - strongExpressionAllowed
 * - qualificationRequired
 *
 * 를 결정했다.
 *
 * 이제 이 책임을 별도 Analyzer로 분리한다.
 *
 * =================================================
 * 이 Analyzer가 결정하는 것
 * =================================================
 *
 * deterministic conclusion이 요청하는:
 *
 * - strong 표현 가능 여부
 * - 제한 표현 필요 여부
 *
 * =================================================
 * 이 Analyzer가 결정하지 않는 것
 * =================================================
 *
 * - Meaning Origin
 * - Specificity
 * - 사용자 최종 Expression Mode
 *
 * Origin 정책은
 * ConsultationMeaningBoundaryDecisionEngine이
 * 최종 결정한다.
 */
@Component
public class ConsultationDeterministicConclusionExpressionAnalyzer {

    public boolean strongExpressionAllowed(
            ConsultationGroundingDeterministicConclusion conclusion
    ) {

        if (
                conclusion == null
        ) {

            return false;
        }

        String status =
                normalizedStatus(
                        conclusion
                );

        return status.contains(
                "CONFIRMED"
        )
                ||
                status.contains(
                        "STRONG"
                )
                ||
                status.contains(
                        "ACTIVE"
                )
                ||
                status.contains(
                        "ESTABLISHED"
                )
                ||
                status.contains(
                        "SUPPORTIVE"
                );
    }

    public boolean qualificationRequired(
            ConsultationGroundingDeterministicConclusion conclusion
    ) {

        if (
                conclusion == null
        ) {

            return true;
        }

        String status =
                normalizedStatus(
                        conclusion
                );

        return status.contains(
                "UNCERTAIN"
        )
                ||
                status.contains(
                        "MIXED"
                )
                ||
                status.contains(
                        "CONDITIONAL"
                )
                ||
                status.contains(
                        "LIMITED"
                )
                ||
                status.contains(
                        "CAUTION"
                )
                ||
                status.contains(
                        "PRESSURE"
                );
    }

    private String normalizedStatus(
            ConsultationGroundingDeterministicConclusion conclusion
    ) {

        return String.valueOf(
                        conclusion.status()
                )
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }
}