package com.dajeom.dto;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationRelationshipStatus;
import com.dajeom.type.ConsultationRelationshipTarget;
import com.dajeom.type.ConsultationWorkStatus;

import java.util.List;

/**
 * 상담 전 Context가 최종 서술에 어떻게 사용되었는지 보여주는 비계산 메타데이터.
 *
 * blindAnalysisPreserved=true는 관계/직업/질문 Context가
 * 원국 및 운 계산/Judgment 생성 이후에만 적용되었음을 뜻한다.
 */
public record ConsultationPersonalizationResponse(

        boolean blindAnalysisPreserved,
        String narrativePlanVersion,
        ConsultationRelationshipTarget relationshipTarget,
        ConsultationRelationshipStatus relationshipStatus,
        ConsultationWorkStatus workStatus,
        String consultationQuestion,
        List<ConsultationCategory> focusedCategories

) {

    public ConsultationPersonalizationResponse {

        focusedCategories =
                focusedCategories == null
                        ? List.of()
                        : List.copyOf(
                        focusedCategories
                );

        consultationQuestion =
                consultationQuestion == null
                        ? ""
                        : consultationQuestion.trim();
    }
}