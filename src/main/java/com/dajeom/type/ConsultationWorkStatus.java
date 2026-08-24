package com.dajeom.type;

/**
 * 상담 전에 사용자가 선택하는
 * 현재 일 / 직업 상태.
 *
 * 이 값은 사주 계산에 사용하지 않는다.
 *
 * 원국,
 * 관성,
 * 재성,
 * 식상,
 * 직업 관련 Pattern,
 * 대운 / 세운 Judgment가
 * 모두 확정된 이후
 * 현실 표현 Context로만 사용한다.
 */
public enum ConsultationWorkStatus {

    UNSPECIFIED(
            "미지정"
    ),

    STUDENT(
            "학생"
    ),

    JOB_SEEKER(
            "구직 중"
    ),

    EMPLOYEE(
            "직장인"
    ),

    SELF_EMPLOYED(
            "사업자"
    ),

    FREELANCER(
            "프리랜서"
    ),

    UNEMPLOYED(
            "현재 직업 없음"
    ),

    OTHER(
            "기타"
    );

    private final String koreanName;

    ConsultationWorkStatus(
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