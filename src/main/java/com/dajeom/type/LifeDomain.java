package com.dajeom.type;

/**
 * 정밀화 18차-1.
 *
 * 다점 생활영역 분석의 최상위 Domain.
 *
 * =================================================
 *
 * WEALTH
 * 재물·수입·자원 운용.
 *
 * CAREER
 * 직업·업무·사회적 역할.
 *
 * RELATIONSHIP
 * 연애·배우자·대인관계.
 *
 * LEARNING_GROWTH
 * 학습·자격·지식·성장·표현의 발전.
 *
 * HEALTH_LIFESTYLE
 * 건강 자체의 의료진단이 아니라,
 * 명리 구조에서 보는 생활 리듬·균형·소모 Context.
 *
 * =================================================
 *
 * 주의:
 *
 * HEALTH_LIFESTYLE는 의료진단을 생성하지 않는다.
 */
public enum LifeDomain {

    WEALTH(
            "재물"
    ),

    CAREER(
            "직업·사회"
    ),

    RELATIONSHIP(
            "관계"
    ),

    LEARNING_GROWTH(
            "학습·성장"
    ),

    HEALTH_LIFESTYLE(
            "건강·생활리듬"
    );

    private final String koreanName;

    LifeDomain(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String koreanName() {
        return koreanName;
    }
}