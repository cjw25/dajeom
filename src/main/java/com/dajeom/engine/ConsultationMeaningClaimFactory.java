package com.dajeom.engine;

import com.dajeom.model.ConsultationMeaningBoundaryDecision;
import com.dajeom.model.ConsultationMeaningClaim;
import com.dajeom.type.ConsultationMeaningSpecificity;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Consultation Meaning Claim의 공식 생성 경계.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * NarrativePlanAnalyzer를 포함한 상위 코드가
 * 직접:
 *
 * new ConsultationMeaningClaim(...)
 *
 * 을 하면서 Origin / Expression 정책을
 * 우회하지 않도록 공식 생성 책임을 모은다.
 *
 * =================================================
 * 생성 흐름
 * =================================================
 *
 * sourceCode
 * +
 * specificity
 * +
 * requested strong
 * +
 * requested qualification
 *
 * ->
 *
 * ConsultationMeaningBoundaryDecisionEngine
 *
 * ->
 *
 * Origin 정책 적용
 *
 * ->
 *
 * 최종 ConsultationMeaningClaim
 *
 * =================================================
 * 대표적인 효과
 * =================================================
 *
 * NATAL_CONTEXT가:
 *
 * requestedStrong=true
 *
 * 로 들어와도:
 *
 * effectiveStrong=false
 *
 * Claim으로 생성된다.
 */
@Component
public class ConsultationMeaningClaimFactory {

    private final ConsultationMeaningBoundaryDecisionEngine
            boundaryDecisionEngine;

    public ConsultationMeaningClaimFactory(

            ConsultationMeaningBoundaryDecisionEngine
                    boundaryDecisionEngine

    ) {

        this.boundaryDecisionEngine =
                Objects.requireNonNull(
                        boundaryDecisionEngine,
                        "ConsultationMeaningBoundaryDecisionEngine이 필요합니다."
                );
    }

    public ConsultationMeaningClaim create(

            String claimId,

            String sourceCode,

            String text,

            ConsultationMeaningSpecificity specificity,

            boolean requestedStrongExpressionAllowed,

            boolean requestedQualificationRequired

    ) {

        ConsultationMeaningBoundaryDecision decision =
                boundaryDecisionEngine.decide(
                        sourceCode,
                        specificity,
                        requestedStrongExpressionAllowed,
                        requestedQualificationRequired
                );

        return new ConsultationMeaningClaim(
                claimId,
                sourceCode,
                text,
                specificity,
                decision.strongExpressionAllowed(),
                decision.qualificationRequired(),
                decision.originContract()
        );
    }
}