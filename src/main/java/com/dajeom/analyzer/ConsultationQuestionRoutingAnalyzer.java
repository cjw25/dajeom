package com.dajeom.analyzer;

import com.dajeom.model.ConsultationQuestionRoutingAnalysis;
import com.dajeom.model.ConsultationRequestScopeAnalysis;
import com.dajeom.type.ConsultationQuestionRoutingStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 19차.
 *
 * ConsultationRequestScopeAnalysis의
 * 자유 질문 상태를 공식 Routing 결과로 변환한다.
 *
 * =================================================
 *
 * 이 Analyzer는 질문의 자연어 의미를
 * 해석하지 않는다.
 *
 * =================================================
 *
 * 1.
 * 질문 없음
 *
 * -> NOT_APPLICABLE
 *
 * =================================================
 *
 * 2.
 * 질문 있음
 * +
 * 사용자가 Category 직접 선택
 *
 * -> EXPLICITLY_SCOPED
 *
 * =================================================
 *
 * 3.
 * 질문 있음
 * +
 * Category 미선택
 *
 * -> GROUNDING_REQUIRED
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 키워드 검색
 * - 문자열 패턴 분류
 * - 명리 계산
 * - 상담분야 추측
 * - 확률/점수 계산
 * - LLM 호출
 */
@Component
public class ConsultationQuestionRoutingAnalyzer {

    public ConsultationQuestionRoutingAnalysis analyze(
            ConsultationRequestScopeAnalysis requestScope
    ) {

        Objects.requireNonNull(
                requestScope,
                "ConsultationQuestionRoutingAnalyzer에는 ConsultationRequestScopeAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 1. 질문 없음
         * =================================================
         */

        if (
                !requestScope
                        .hasQuestion()
        ) {

            return new ConsultationQuestionRoutingAnalysis(
                    ConsultationQuestionRoutingStatus
                            .NOT_APPLICABLE,
                    Optional.empty(),
                    List.of()
            );
        }

        /*
         * =================================================
         * 2. 사용자가 Category를 직접 지정
         * =================================================
         */

        if (
                requestScope
                        .hasSelectedCategories()
        ) {

            return new ConsultationQuestionRoutingAnalysis(
                    ConsultationQuestionRoutingStatus
                            .EXPLICITLY_SCOPED,
                    requestScope
                            .consultationQuestion(),
                    requestScope
                            .selectedCategories()
            );
        }

        /*
         * =================================================
         * 3. 자유 질문만 존재
         * =================================================
         */

        if (
                requestScope
                        .requiresCategoryResolution()
        ) {

            return new ConsultationQuestionRoutingAnalysis(
                    ConsultationQuestionRoutingStatus
                            .GROUNDING_REQUIRED,
                    requestScope
                            .consultationQuestion(),
                    List.of()
            );
        }

        throw new IllegalStateException(
                "자유 상담 질문의 ConsultationCategory Routing 상태를 결정할 수 없습니다."
        );
    }
}