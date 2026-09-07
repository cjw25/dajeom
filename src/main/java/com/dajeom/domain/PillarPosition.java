package com.dajeom.domain;

/**
 * 사주 네 기둥의 위치.
 *
 * 기존 관계 계산 및 Response에서
 * korean()을 사용하므로 한글 표시명을 함께 보존한다.
 */
public enum PillarPosition {

    YEAR("년"),
    MONTH("월"),
    DAY("일"),
    HOUR("시");

    private final String korean;

    PillarPosition(
            String korean
    ) {
        this.korean = korean;
    }

    public String korean() {
        return korean;
    }
}