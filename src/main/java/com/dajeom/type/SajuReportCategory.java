package com.dajeom.type;

/**
 * 다점 사주 보고서의 해석 카테고리다.
 *
 * 9차에서는 각 카테고리에 필요한 해석 신호를 생성하고,
 * 10차에서 해당 신호를 2,000자 내외의 문장으로 변환한다.
 */
public enum SajuReportCategory {

    PERSONALITY(
            "기본 성향",
            "성격, 사고방식, 행동 패턴과 내면의 기질"
    ),

    PAST_LUCK(
            "과거운",
            "유년기와 과거 환경에서 형성된 경험의 흐름"
    ),

    PARENTS(
            "부모운",
            "부모와의 관계, 보호·통제·거리감의 경향"
    ),

    SIBLINGS(
            "형제운",
            "형제자매, 친구, 동료와의 경쟁·협력 관계"
    ),

    LOVE_STYLE(
            "연애 스타일",
            "감정 표현, 관계 주도권, 애착과 연애 방식"
    ),

    PARTNER_LUCK(
            "배우자·인연운",
            "만나는 인연의 성향과 장기 관계의 특징"
    ),

    WEALTH(
            "재물운",
            "돈을 벌고 지키고 사용하는 방식과 재물 흐름"
    ),

    CAREER(
            "직업운",
            "직업 성향, 조직 적응, 전문성 및 사업 가능성"
    ),

    LEGAL_RISK(
            "관재수",
            "분쟁, 계약, 규칙 위반, 행정·법적 마찰 경향"
    ),

    HEALTH_TENDENCY(
            "건강 경향",
            "오행 불균형에 따른 생활관리 참고 신호"
    ),

    CURRENT_LUCK(
            "현재운",
            "현재 대운을 중심으로 보는 장기적인 환경"
    ),

    YEAR_LUCK(
            "선택 연도 종합운",
            "선택한 세운과 현재 대운을 결합한 연간 흐름"
    ),

    FUTURE_LUCK(
            "미래운",
            "앞으로의 대운 변화와 장기적인 발전 방향"
    );

    private final String koreanName;
    private final String description;

    SajuReportCategory(
            String koreanName,
            String description
    ) {
        this.koreanName = koreanName;
        this.description = description;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getDescription() {
        return description;
    }
}