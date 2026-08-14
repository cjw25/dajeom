package com.dajeom.analyzer;

import com.dajeom.model.ConsultationCategoryEvidenceAnalysis;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationQuestionRoutingAnalysis;
import com.dajeom.model.ConsultationRequestEvidenceAnalysis;
import com.dajeom.model.ConsultationRequestScopeAnalysis;
import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 19차.
 *
 * 사용자 상담 요청 범위,
 * 자유 질문 Routing,
 * 11개 ConsultationCategory Evidence View를
 * 실제로 결합한다.
 *
 * =================================================
 *
 * 입력:
 *
 * ConsultationRequestScopeAnalysis
 *
 * +
 *
 * ConsultationCategoryEvidenceAnalysis
 *
 * =================================================
 *
 * 내부:
 *
 * ConsultationQuestionRoutingAnalyzer
 *
 * =================================================
 *
 * 출력:
 *
 * ConsultationRequestEvidenceAnalysis
 *
 * =================================================
 *
 * 동작:
 *
 * 종합운
 * -> 11개 View 전체
 *
 * 명시 Category
 * -> 명시 View만
 *
 * 자유 질문만
 * -> GROUNDING_REQUIRED
 * -> selectedViews empty
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 질문 키워드 분류
 * - 사주 계산
 * - LifeDomain 재평가
 * - 점수 계산
 * - polarity 계산
 * - 사건 예측
 * - 사용자 상담문 생성
 */
@Component
public class ConsultationRequestEvidenceAnalyzer {

    private final ConsultationQuestionRoutingAnalyzer
            questionRoutingAnalyzer;

    public ConsultationRequestEvidenceAnalyzer(
            ConsultationQuestionRoutingAnalyzer
                    questionRoutingAnalyzer
    ) {

        this.questionRoutingAnalyzer =
                Objects.requireNonNull(
                        questionRoutingAnalyzer,
                        "ConsultationQuestionRoutingAnalyzer가 필요합니다."
                );
    }

    public ConsultationRequestEvidenceAnalysis analyze(

            ConsultationRequestScopeAnalysis requestScope,

            ConsultationCategoryEvidenceAnalysis evidenceAnalysis

    ) {

        Objects.requireNonNull(
                requestScope,
                "ConsultationRequestEvidenceAnalyzer에는 ConsultationRequestScopeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                evidenceAnalysis,
                "ConsultationRequestEvidenceAnalyzer에는 ConsultationCategoryEvidenceAnalysis가 필요합니다."
        );

        ConsultationQuestionRoutingAnalysis
                questionRouting =
                questionRoutingAnalyzer
                        .analyze(
                                requestScope
                        );

        /*
         * =================================================
         * 자유 질문만 존재
         * =================================================
         *
         * Category를 임의로 고르지 않는다.
         */

        if (
                questionRouting
                        .groundingRequired()
        ) {

            return new ConsultationRequestEvidenceAnalysis(
                    requestScope,
                    questionRouting,
                    evidenceAnalysis,
                    List.of()
            );
        }

        /*
         * =================================================
         * Category 확정 요청
         * =================================================
         *
         * 종합운:
         * selectedCategories = 11개
         *
         * 명시 선택:
         * selectedCategories = 선택 목록
         */

        if (
                !requestScope
                        .categorySelectionResolved()
        ) {

            throw new IllegalStateException(
                    "ConsultationCategory 선택 상태가 확정되지 않았습니다."
            );
        }

        List<ConsultationCategoryEvidenceView>
                selectedViews =
                new ArrayList<>();

        for (
                ConsultationCategory category
                : requestScope
                .selectedCategories()
        ) {

            ConsultationCategoryEvidenceView view =
                    evidenceAnalysis
                            .get(
                                    category
                            );

            /*
             * unavailable View도 유지한다.
             *
             * 요청 자체와
             * deterministic Evidence 준비 상태는
             * 별개의 정보다.
             */

            selectedViews.add(
                    view
            );
        }

        return new ConsultationRequestEvidenceAnalysis(
                requestScope,
                questionRouting,
                evidenceAnalysis,
                List.copyOf(
                        selectedViews
                )
        );
    }
}