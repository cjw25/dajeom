package com.dajeom.analyzer;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.model.ConsultationRequestScopeAnalysis;
import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 19차.
 *
 * SajuAnalysisRequest의 상담 관련 입력을
 * ConsultationRequestScopeAnalysis로 정규화한다.
 *
 * =================================================
 *
 * 역할:
 *
 * 1.
 * 아무 상담 조건이 없으면
 * -> 11개 분야 종합운
 *
 * 2.
 * ConsultationCategory가 명시되어 있으면
 * -> 해당 분야 선택
 *
 * 3.
 * 자유 질문만 있으면
 * -> 질문은 보존
 * -> Category는 아직 결정하지 않음
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 사주 계산
 * - 명리 Evidence 생성
 * - 자유 질문 키워드 추측
 * - 질문을 임의 Category로 강제 분류
 * - 사용자 상담문 생성
 *
 * =================================================
 *
 * 자유 질문을 실제 Category와 연결하는 것은
 * 이후 별도의 19차 Question Routing 경계가 담당한다.
 */
@Component
public class ConsultationRequestScopeAnalyzer {

    public ConsultationRequestScopeAnalysis analyze(
            SajuAnalysisRequest request
    ) {

        Objects.requireNonNull(
                request,
                "ConsultationRequestScopeAnalyzer에는 SajuAnalysisRequest가 필요합니다."
        );

        /*
         * =================================================
         * 1.
         * 종합운
         * =================================================
         */

        if (
                request
                        .comprehensiveConsultationRequested()
        ) {

            return new ConsultationRequestScopeAnalysis(
                    allCategories(),
                    Optional.empty(),
                    true
            );
        }

        /*
         * =================================================
         * 2.
         * 명시적 Category 선택
         * =================================================
         *
         * 질문이 같이 존재해도
         * 사용자에게서 명시적으로 받은 Category를
         * 임의로 변경하지 않는다.
         */

        if (
                request
                        .hasSelectedConsultationCategories()
        ) {

            return new ConsultationRequestScopeAnalysis(
                    request
                            .consultationCategories(),
                    request
                            .consultationQuestionOptional(),
                    false
            );
        }

        /*
         * =================================================
         * 3.
         * 자유 질문만 존재
         * =================================================
         *
         * 현재 단계에서:
         *
         * 돈
         * -> WEALTH
         *
         * 결혼
         * -> PARTNER_LUCK
         *
         * 같은 문자열 키워드 매핑을
         * 임의로 만들지 않는다.
         *
         * 질문 원문만 보존하고
         * Category Resolution 필요 상태로 둔다.
         */

        if (
                request
                        .hasConsultationQuestion()
        ) {

            return new ConsultationRequestScopeAnalysis(
                    List.of(),
                    request
                            .consultationQuestionOptional(),
                    false
            );
        }

        /*
         * SajuAnalysisRequest의 Convenience 계약상
         * 여기까지 올 수 없어야 한다.
         */

        throw new IllegalStateException(
                "상담 요청 범위를 결정할 수 없습니다."
        );
    }

    private List<ConsultationCategory>
    allCategories() {

        return Arrays
                .stream(
                        ConsultationCategory
                                .values()
                )
                .sorted(
                        Comparator.comparingInt(
                                ConsultationCategory
                                        ::getOrder
                        )
                )
                .toList();
    }
}