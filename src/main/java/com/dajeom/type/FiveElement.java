package com.dajeom.type;

/**
 * 목·화·토·금·수 오행을 나타낸다.
 */
public enum FiveElement {

    WOOD("목", "木"),
    FIRE("화", "火"),
    EARTH("토", "土"),
    METAL("금", "金"),
    WATER("수", "水");

    private final String koreanName;
    private final String chineseCharacter;

    FiveElement(
            String koreanName,
            String chineseCharacter
    ) {
        this.koreanName = koreanName;
        this.chineseCharacter = chineseCharacter;
    }

    /**
     * 현재 오행이 대상 오행을 생하는지 확인한다.
     *
     * 목생화
     * 화생토
     * 토생금
     * 금생수
     * 수생목
     */
    public boolean generates(FiveElement target) {
        return switch (this) {
            case WOOD -> target == FIRE;
            case FIRE -> target == EARTH;
            case EARTH -> target == METAL;
            case METAL -> target == WATER;
            case WATER -> target == WOOD;
        };
    }

    /**
     * 현재 오행이 대상 오행을 극하는지 확인한다.
     *
     * 목극토
     * 토극수
     * 수극화
     * 화극금
     * 금극목
     */
    public boolean controls(FiveElement target) {
        return switch (this) {
            case WOOD -> target == EARTH;
            case FIRE -> target == METAL;
            case EARTH -> target == WATER;
            case METAL -> target == WOOD;
            case WATER -> target == FIRE;
        };
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getChineseCharacter() {
        return chineseCharacter;
    }
}