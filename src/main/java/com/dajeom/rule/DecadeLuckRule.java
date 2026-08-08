package com.dajeom.rule;

/**
 * 10년 대운 생성 Rule.
 */
public final class DecadeLuckRule {

    public static final String RULE_VERSION =
            "DAJEOM_DECADE_LUCK_V1";

    /**
     * 하나의 대운이 지속되는 연수.
     */
    public static final int PERIOD_YEARS =
            10;

    /**
     * 생성할 대운 개수.
     *
     * 12개:
     * 최대 약 120년 범위를 준비한다.
     */
    public static final int PERIOD_COUNT =
            12;

    private DecadeLuckRule() {

        throw new IllegalStateException(
                "DecadeLuckRule은 생성할 수 없습니다."
        );
    }
}