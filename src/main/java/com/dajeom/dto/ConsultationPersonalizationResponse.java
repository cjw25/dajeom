package com.dajeom.dto;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationRelationshipStatus;
import com.dajeom.type.ConsultationRelationshipTarget;
import com.dajeom.type.ConsultationWorkStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * 상담 사용자 Context / Personalization API 응답.
 *
 * =================================================
 * 역할
 * =================================================
 *
 * 사용자 입력 Context가
 * deterministic 사주 계산 이후에만 결합되었다는 사실과
 * 최종 화면에서 필요한 Personalization 정보를 제공한다.
 *
 * =================================================
 * blindAnalysisPreserved
 * =================================================
 *
 * 사용자 질문 / 관계 상태 / 직업 상태가
 * 원국, 대운, 세운 등의 deterministic 계산값을
 * 변경하지 않았음을 나타낸다.
 *
 * =================================================
 * narrativePlanVersion
 * =================================================
 *
 * Narrative Plan의 내부 구현 버전.
 *
 * 서버 내부 추적 및 계약 확인에는 사용할 수 있지만
 * 프론트의 화면 분기 계약이 아니다.
 *
 * 따라서 Java 객체 내부에는 유지하되
 * 공개 JSON에는 노출하지 않는다.
 *
 * =================================================
 * focusedCategories
 * =================================================
 *
 * 사용자가 선택한 Category 전체가 아니다.
 *
 * 사용자 질문 / Context에 의해
 * 표현상 우선 강조되는 Category 목록이다.
 */
public record ConsultationPersonalizationResponse(

        boolean blindAnalysisPreserved,

        /**
         * 내부 Narrative 구현 버전.
         *
         * 공개 API 계약이 아니므로
         * JSON 응답에서는 제외한다.
         */
        @JsonIgnore
        String narrativePlanVersion,

        ConsultationRelationshipTarget relationshipTarget,

        ConsultationRelationshipStatus relationshipStatus,

        ConsultationWorkStatus workStatus,

        String consultationQuestion,

        List<ConsultationCategory> focusedCategories

) {

    public ConsultationPersonalizationResponse {

        narrativePlanVersion =
                narrativePlanVersion == null
                        ? ""
                        : narrativePlanVersion.trim();

        consultationQuestion =
                consultationQuestion == null
                        ? ""
                        : consultationQuestion.trim();

        focusedCategories =
                focusedCategories == null
                        ? List.of()
                        : List.copyOf(
                        focusedCategories
                );
    }
}