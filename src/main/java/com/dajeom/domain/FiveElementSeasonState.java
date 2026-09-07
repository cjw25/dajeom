package com.dajeom.domain;

/**
 * 월령을 기준으로 본 오행의 旺相休囚死 상태.
 *
 * 숫자 강도나 점수가 아니다.
 * 전통 명리의 계절 상태 분류다.
 */
public enum FiveElementSeasonState {

    /**
     * 旺
     * 월령과 같은 오행.
     */
    WANG("旺", "왕"),

    /**
     * 相
     * 월령이 해당 오행을 생함.
     */
    XIANG("相", "상"),

    /**
     * 休
     * 해당 오행이 월령을 생함.
     */
    XIU("休", "휴"),

    /**
     * 囚
     * 해당 오행이 월령을 극함.
     */
    QIU("囚", "수"),

    /**
     * 死
     * 월령이 해당 오행을 극함.
     */
    SI("死", "사");

    private final String hanja;
    private final String korean;

    FiveElementSeasonState(
            String hanja,
            String korean
    ) {
        this.hanja = hanja;
        this.korean = korean;
    }

    public String hanja() {
        return hanja;
    }

    public String korean() {
        return korean;
    }
}