package com.dajeom.type;

/**
 * 원국 기본성향 및 기능 분석에서 사용하는
 * 십성 5대 Family의 기능축.
 *
 * =================================================
 *
 * SELF_ASSERTION
 *
 * 비견·겁재
 *
 * 자기 기반과 같은 오행의 힘이
 * 자기주도·경쟁·동료 기능에서
 * 어떻게 존재하는지를 다루는 축.
 *
 * =================================================
 *
 * EXPRESSION
 *
 * 식신·상관
 *
 * 일간에서 밖으로 빠져나가는
 * 표현·산출·행동 기능축.
 *
 * =================================================
 *
 * RESOURCE_REALITY
 *
 * 정재·편재
 *
 * 일간이 제어하는 현실·자원·성과
 * 기능을 다루는 축.
 *
 * =================================================
 *
 * RESPONSIBILITY
 *
 * 정관·칠살
 *
 * 일간을 제어하는 책임·규범·압력
 * 기능을 다루는 축.
 *
 * =================================================
 *
 * PROCESSING
 *
 * 정인·편인
 *
 * 일간을 생조하는 수용·처리·내부화
 * 기능을 다루는 축.
 *
 * =================================================
 *
 * 주의:
 *
 * 이 enum은 곧바로:
 *
 * - 성격
 * - 좋고 나쁨
 * - 직업
 * - 재물
 *
 * 을 뜻하지 않는다.
 *
 * 원국의 십성 Family를
 * 후속 Functional Analysis가
 * 안정적으로 참조하기 위한 구조화 타입이다.
 */
public enum NatalTenGodFunctionType {

    SELF_ASSERTION(
            "자기주도·경쟁",
            "비겁"
    ),

    EXPRESSION(
            "표현·산출",
            "식상"
    ),

    RESOURCE_REALITY(
            "현실·자원",
            "재성"
    ),

    RESPONSIBILITY(
            "책임·규범",
            "관살"
    ),

    PROCESSING(
            "수용·처리",
            "인성"
    );

    private final String koreanName;

    private final String tenGodFamilyKoreanName;

    NatalTenGodFunctionType(
            String koreanName,
            String tenGodFamilyKoreanName
    ) {

        this.koreanName =
                koreanName;

        this.tenGodFamilyKoreanName =
                tenGodFamilyKoreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public String tenGodFamilyKoreanName() {

        return tenGodFamilyKoreanName;
    }

    /**
     * 정확한 십성이
     * 현재 기능축에 포함되는지 확인한다.
     */
    public boolean contains(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return false;
        }

        return switch (this) {

            case SELF_ASSERTION ->
                    tenGodType
                            .companionGroup();

            case EXPRESSION ->
                    tenGodType
                            .outputGroup();

            case RESOURCE_REALITY ->
                    tenGodType
                            .wealthGroup();

            case RESPONSIBILITY ->
                    tenGodType
                            .officerGroup();

            case PROCESSING ->
                    tenGodType
                            .resourceGroup();
        };
    }
}