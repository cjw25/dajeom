package com.dajeom.type;

/**
 * 상담 전에 사용자가 선택하는 현재 관계 상태.
 *
 * 이 값은 사주 계산값이 아니다.
 *
 * 원국 / 대운 / 세운 / 월운 /
 * deterministic Judgment 생성에는 사용하지 않는다.
 *
 * 모든 명리 판단이 완료된 이후
 * 상담 표현의 현실 적합성을 높이기 위한
 * User Context로만 사용한다.
 */
public enum ConsultationRelationshipStatus {

    UNSPECIFIED("미지정"),

    SINGLE("솔로"),

    INTERESTED_PERSON(
            "마음에 두고 있는 사람이 있음"
    ),

    DATING("연애 중"),

    RECENT_BREAKUP(
            "이별 직후"
    ),

    MARRIED("기혼"),

    OTHER("기타");

    private final String koreanName;

    ConsultationRelationshipStatus(
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