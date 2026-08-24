package com.dajeom.analyzer;

import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.model.ConsultationUserContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 상담 전 입력을 명리 계산과 분리된 User Context로 변환한다.
 *
 * 호출 위치는 반드시 블라인드 명리 Judgment 및 Prompt Context가 확정된 뒤여야 한다.
 */
@Component
public class ConsultationUserContextAnalyzer {

    public ConsultationUserContext analyze(
            SajuAnalysisRequest request
    ) {

        Objects.requireNonNull(
                request,
                "ConsultationUserContextAnalyzer에는 SajuAnalysisRequest가 필요합니다."
        );

        return new ConsultationUserContext(
                request.relationshipTarget(),
                request.relationshipStatus(),
                request.workStatus(),
                request.consultationQuestionOptional()
        );
    }
}