package com.dajeom.analyzer;

import com.dajeom.model.ConsultationQuestionRoutingAnalysis;
import com.dajeom.model.ConsultationRequestScopeAnalysis;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationQuestionRoutingStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 자유 상담 질문의 Routing 상태를 결정한다.
 *
 * 중요한 경계:
 * - 질문은 사주 계산/Judgment 생성에 사용하지 않는다.
 * - 질문만 있는 경우에도 이미 완료된 블라인드 사주 분석의 11개 Category를 모두 보존한다.
 * - 어떤 Category를 먼저 보여줄지는 이후 Narrative Plan 단계가 담당한다.
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

        if (!requestScope.hasQuestion()) {
            return new ConsultationQuestionRoutingAnalysis(
                    ConsultationQuestionRoutingStatus.NOT_APPLICABLE,
                    Optional.empty(),
                    List.of()
            );
        }

        if (requestScope.hasSelectedCategories()) {
            return new ConsultationQuestionRoutingAnalysis(
                    ConsultationQuestionRoutingStatus.EXPLICITLY_SCOPED,
                    requestScope.consultationQuestion(),
                    requestScope.selectedCategories()
            );
        }

        if (requestScope.requiresCategoryResolution()) {
            /*
             * 과거에는 GROUNDING_REQUIRED로 두고 Story 생성을 중단했다.
             * 현재는 질문이 명리 결과를 바꾸지 않도록 모든 Category Grounding을 먼저 보존하고,
             * 질문은 이후 Narrative Plan의 우선순위/표현 Context에만 사용한다.
             */
            return new ConsultationQuestionRoutingAnalysis(
                    ConsultationQuestionRoutingStatus.BLIND_ANALYSIS_READY,
                    requestScope.consultationQuestion(),
                    allCategories()
            );
        }

        throw new IllegalStateException(
                "자유 상담 질문의 ConsultationCategory Routing 상태를 결정할 수 없습니다."
        );
    }

    private List<ConsultationCategory> allCategories() {
        return Arrays.stream(ConsultationCategory.values())
                .sorted(Comparator.comparingInt(ConsultationCategory::getOrder))
                .toList();
    }
}