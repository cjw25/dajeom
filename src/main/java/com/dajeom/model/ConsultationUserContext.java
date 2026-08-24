package com.dajeom.model;

import com.dajeom.type.ConsultationRelationshipStatus;
import com.dajeom.type.ConsultationRelationshipTarget;
import com.dajeom.type.ConsultationWorkStatus;

import java.util.Objects;
import java.util.Optional;

/**
 * 명리 계산과 완전히 분리된
 * 상담 전 사용자 Context.
 *
 * =================================================
 *
 * 이 객체에 들어가는 정보:
 *
 * - 관계 대상
 * - 현재 관계 상태
 * - 현재 직업 상태
 * - 자유 상담 질문
 *
 * =================================================
 *
 * 중요한 경계:
 *
 * 이 정보는
 *
 * - 만세력
 * - 원국
 * - 오행 세력
 * - 신강 / 신약
 * - 십성
 * - 격국
 * - 생극제화
 * - 합충형파해
 * - 대운
 * - 세운
 * - 월운
 * - deterministic Judgment
 *
 * 를 계산하는 근거가 아니다.
 *
 * 위 계산이 모두 끝난 다음
 * Narrative 단계에서만 사용한다.
 *
 * =================================================
 *
 * 사용 목적:
 *
 * 1.
 * 어떤 상담 분야를 먼저 설명할지
 *
 * 2.
 * 이미 확정된 Judgment를
 * 현재 현실 상황에 맞게 어떻게 표현할지
 *
 * 3.
 * 잘못된 현실 전제를 사용하지 않게 할지
 *
 * 를 결정한다.
 */
public record ConsultationUserContext(

        ConsultationRelationshipTarget
        relationshipTarget,

        ConsultationRelationshipStatus
        relationshipStatus,

        ConsultationWorkStatus
        workStatus,

        Optional<String>
        consultationQuestion

) {

    public ConsultationUserContext {

        relationshipTarget =
                relationshipTarget == null
                        ? ConsultationRelationshipTarget
                        .UNSPECIFIED
                        : relationshipTarget;

        relationshipStatus =
                relationshipStatus == null
                        ? ConsultationRelationshipStatus
                        .UNSPECIFIED
                        : relationshipStatus;

        workStatus =
                workStatus == null
                        ? ConsultationWorkStatus
                        .UNSPECIFIED
                        : workStatus;

        consultationQuestion =
                normalizeQuestion(
                        consultationQuestion
                );
    }

    /**
     * 자유 상담 질문이 존재하는가.
     */
    public boolean hasQuestion() {

        return consultationQuestion
                .isPresent();
    }

    /**
     * 관계 관련 User Context가
     * 하나라도 명시되어 있는가.
     */
    public boolean hasRelationshipContext() {

        return relationshipTarget
                .specified()
                ||
                relationshipStatus
                        .specified();
    }

    /**
     * 직업 관련 User Context가
     * 명시되어 있는가.
     */
    public boolean hasWorkContext() {

        return workStatus
                .specified();
    }

    /**
     * 상담 개인화에 사용할 수 있는
     * Context가 하나라도 존재하는가.
     */
    public boolean hasAnyContext() {

        return hasQuestion()
                || hasRelationshipContext()
                || hasWorkContext();
    }

    private static Optional<String>
    normalizeQuestion(
            Optional<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Optional.empty();
        }

        String value =
                Objects.requireNonNull(
                                source.orElseThrow(),
                                "상담 질문은 null일 수 없습니다."
                        )
                        .trim();

        if (
                value.isBlank()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                value
        );
    }
}