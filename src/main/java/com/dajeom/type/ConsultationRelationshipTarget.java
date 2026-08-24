package com.dajeom.type;

/**
 * 상담 전에 사용자가 선택하는
 * 관계 대상 Context.
 *
 * 명리 계산값이 아니다.
 *
 * 원국의 배우자성,
 * 십성,
 * 대운,
 * 세운,
 * 관계 Judgment를 변경하지 않는다.
 *
 * 최종 상담문의 호칭 및
 * 현실 상황에 맞는 표현에만 사용한다.
 */
public enum ConsultationRelationshipTarget {

    UNSPECIFIED(
            "미지정"
    ),

    MALE(
            "남성"
    ),

    FEMALE(
            "여성"
    ),

    ANY(
            "상관없음"
    ),

    NOT_APPLICABLE(
            "해당 없음"
    );

    private final String koreanName;

    ConsultationRelationshipTarget(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String getKoreanName() {

        return koreanName;
    }

    public boolean specified() {

        return this
                != UNSPECIFIED;
    }
}