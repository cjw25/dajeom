package com.dajeom.engine;

import com.dajeom.model.ConsultationMeaningBoundaryDecision;
import com.dajeom.model.ConsultationMeaningOriginContract;
import com.dajeom.type.ConsultationMeaningExpressionMode;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Meaning Origin별 최종 표현 권한 결정 Engine.
 *
 * =================================================
 * 책임
 * =================================================
 *
 * 이 Engine은:
 *
 * - 명리 Meaning을 새로 만들지 않는다.
 * - 강약 점수를 새로 계산하지 않는다.
 * - Pattern을 새로 만들지 않는다.
 *
 * 상위 계산에서 이미 내려온:
 *
 * requestedStrongExpressionAllowed
 * requestedQualificationRequired
 *
 * 를 Origin 정책에 맞게 최종 확정한다.
 *
 * =================================================
 * EXACT_TEN_GOD
 * =================================================
 *
 * ExactTenGod Meaning Engine이 이미 결정한
 * strong / qualification을 그대로 존중한다.
 *
 * =================================================
 * FORMAL_PATTERN
 * =================================================
 *
 * Formal Pattern deterministic 계산이 내려준
 * strong / qualification을 그대로 존중한다.
 *
 * =================================================
 * DETERMINISTIC_RULE
 * =================================================
 *
 * Category deterministic conclusion이 내려준
 * strong / qualification을 그대로 존중한다.
 *
 * =================================================
 * TIMELINE_CONTRACT
 * =================================================
 *
 * Timeline deterministic conclusion이 내려준
 * strong / qualification을 그대로 존중한다.
 *
 * =================================================
 * NATAL_CONTEXT
 * =================================================
 *
 * CoreSelf / 5개 기능축 / 대인 Context는
 * 독립적인 강한 Meaning Authority가 아니다.
 *
 * 따라서 requestedStrong=true가 들어와도
 * 최종 strong 권한은 제거한다.
 *
 * qualification은 그대로 보존한다.
 */
@Component
public class ConsultationMeaningBoundaryDecisionEngine {

    public ConsultationMeaningBoundaryDecision decide(

            String sourceCode,

            ConsultationMeaningSpecificity specificity,

            boolean requestedStrongExpressionAllowed,

            boolean requestedQualificationRequired

    ) {

        Objects.requireNonNull(
                specificity,
                "Meaning Boundary 결정에는 specificity가 필요합니다."
        );

        ConsultationMeaningOriginContract originContract =
                ConsultationMeaningOriginContract.resolve(
                        sourceCode,
                        specificity
                );

        return decide(
                originContract,
                specificity,
                requestedStrongExpressionAllowed,
                requestedQualificationRequired
        );
    }

    public ConsultationMeaningBoundaryDecision decide(

            ConsultationMeaningOriginContract originContract,

            ConsultationMeaningSpecificity specificity,

            boolean requestedStrongExpressionAllowed,

            boolean requestedQualificationRequired

    ) {

        Objects.requireNonNull(
                originContract,
                "Meaning Boundary 결정에는 originContract가 필요합니다."
        );

        Objects.requireNonNull(
                specificity,
                "Meaning Boundary 결정에는 specificity가 필요합니다."
        );

        originContract.requireSpecificity(
                specificity
        );

        ConsultationMeaningExpressionMode effectiveMode =
                switch (originContract.origin()) {

                    case EXACT_TEN_GOD ->
                            ConsultationMeaningExpressionMode.of(
                                    requestedStrongExpressionAllowed,
                                    requestedQualificationRequired
                            );

                    case FORMAL_PATTERN ->
                            ConsultationMeaningExpressionMode.of(
                                    requestedStrongExpressionAllowed,
                                    requestedQualificationRequired
                            );

                    case DETERMINISTIC_RULE ->
                            ConsultationMeaningExpressionMode.of(
                                    requestedStrongExpressionAllowed,
                                    requestedQualificationRequired
                            );

                    case TIMELINE_CONTRACT ->
                            ConsultationMeaningExpressionMode.of(
                                    requestedStrongExpressionAllowed,
                                    requestedQualificationRequired
                            );

                    case NATAL_CONTEXT ->
                            ConsultationMeaningExpressionMode.of(
                                    false,
                                    requestedQualificationRequired
                            );
                };

        return new ConsultationMeaningBoundaryDecision(
                originContract,
                specificity,
                requestedStrongExpressionAllowed,
                requestedQualificationRequired,
                effectiveMode
        );
    }

    /**
     * 해당 Origin이 독립적인 strong 표현 권한을
     * 가질 수 있는지 확인한다.
     *
     * 이것은 강도를 새로 계산하는 메서드가 아니다.
     * Origin 정책만 반환한다.
     */
    public boolean strongAuthorityAllowed(
            ConsultationMeaningOrigin origin
    ) {

        if (origin == null) {
            return false;
        }

        return origin
                != ConsultationMeaningOrigin.NATAL_CONTEXT;
    }
}