package com.dajeom.solar;

import java.time.Month;
import java.util.OptionalInt;

/**
 * 24절기.
 *
 * 핵심 원칙:
 *
 * 1. 절기는 단순한 날짜가 아니라 태양의 황경으로 결정된다.
 * 2. 사주 월 경계에는 24절기 전부가 아니라
 *    입춘, 경칩, 청명, 입하, 망종, 소서,
 *    입추, 백로, 한로, 입동, 대설, 소한
 *    12개의 '절'을 사용한다.
 * 3. 실제 경계 판정은 SolarTermInstant의 정확한 순간으로 한다.
 */
public enum SolarTerm {

    XIAOHAN(
            "小寒",
            "소한",
            285,
            Month.JANUARY,
            5,
            true,
            12
    ),

    DAHAN(
            "大寒",
            "대한",
            300,
            Month.JANUARY,
            20,
            false,
            0
    ),

    LICHUN(
            "立春",
            "입춘",
            315,
            Month.FEBRUARY,
            4,
            true,
            1
    ),

    YUSHUI(
            "雨水",
            "우수",
            330,
            Month.FEBRUARY,
            19,
            false,
            0
    ),

    JINGZHE(
            "驚蟄",
            "경칩",
            345,
            Month.MARCH,
            5,
            true,
            2
    ),

    CHUNFEN(
            "春分",
            "춘분",
            0,
            Month.MARCH,
            20,
            false,
            0
    ),

    QINGMING(
            "清明",
            "청명",
            15,
            Month.APRIL,
            4,
            true,
            3
    ),

    GUYU(
            "穀雨",
            "곡우",
            30,
            Month.APRIL,
            20,
            false,
            0
    ),

    LIXIA(
            "立夏",
            "입하",
            45,
            Month.MAY,
            5,
            true,
            4
    ),

    XIAOMAN(
            "小滿",
            "소만",
            60,
            Month.MAY,
            21,
            false,
            0
    ),

    MANGZHONG(
            "芒種",
            "망종",
            75,
            Month.JUNE,
            5,
            true,
            5
    ),

    XIAZHI(
            "夏至",
            "하지",
            90,
            Month.JUNE,
            21,
            false,
            0
    ),

    XIAOSHU(
            "小暑",
            "소서",
            105,
            Month.JULY,
            7,
            true,
            6
    ),

    DASHU(
            "大暑",
            "대서",
            120,
            Month.JULY,
            22,
            false,
            0
    ),

    LIQIU(
            "立秋",
            "입추",
            135,
            Month.AUGUST,
            7,
            true,
            7
    ),

    CHUSHU(
            "處暑",
            "처서",
            150,
            Month.AUGUST,
            23,
            false,
            0
    ),

    BAILU(
            "白露",
            "백로",
            165,
            Month.SEPTEMBER,
            7,
            true,
            8
    ),

    QIUFEN(
            "秋分",
            "추분",
            180,
            Month.SEPTEMBER,
            23,
            false,
            0
    ),

    HANLU(
            "寒露",
            "한로",
            195,
            Month.OCTOBER,
            8,
            true,
            9
    ),

    SHUANGJIANG(
            "霜降",
            "상강",
            210,
            Month.OCTOBER,
            23,
            false,
            0
    ),

    LIDONG(
            "立冬",
            "입동",
            225,
            Month.NOVEMBER,
            7,
            true,
            10
    ),

    XIAOXUE(
            "小雪",
            "소설",
            240,
            Month.NOVEMBER,
            22,
            false,
            0
    ),

    DAXUE(
            "大雪",
            "대설",
            255,
            Month.DECEMBER,
            7,
            true,
            11
    ),

    DONGZHI(
            "冬至",
            "동지",
            270,
            Month.DECEMBER,
            21,
            false,
            0
    );

    private static final SolarTerm[] VALUES = values();

    private final String hanja;
    private final String korean;
    private final int solarLongitude;
    private final Month approximateMonth;
    private final int approximateDay;
    private final boolean monthBoundary;
    private final int sajuMonthNumber;

    SolarTerm(
            String hanja,
            String korean,
            int solarLongitude,
            Month approximateMonth,
            int approximateDay,
            boolean monthBoundary,
            int sajuMonthNumber
    ) {
        this.hanja = hanja;
        this.korean = korean;
        this.solarLongitude = solarLongitude;
        this.approximateMonth = approximateMonth;
        this.approximateDay = approximateDay;
        this.monthBoundary = monthBoundary;
        this.sajuMonthNumber = sajuMonthNumber;
    }

    public String hanja() {
        return hanja;
    }

    public String korean() {
        return korean;
    }

    public int solarLongitude() {
        return solarLongitude;
    }

    public Month approximateMonth() {
        return approximateMonth;
    }

    public int approximateDay() {
        return approximateDay;
    }

    /**
     * 사주 월이 변경되는 12절입인가?
     */
    public boolean isMonthBoundary() {
        return monthBoundary;
    }

    /**
     * 사주 월 순서.
     *
     * 1 = 寅월 - 입춘
     * 2 = 卯월 - 경칩
     * 3 = 辰월 - 청명
     * ...
     * 11 = 子월 - 대설
     * 12 = 丑월 - 소한
     *
     * 중기(우수, 춘분 등)는 값이 없다.
     */
    public OptionalInt sajuMonthNumber() {

        if (!monthBoundary) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(
                sajuMonthNumber
        );
    }

    public int requireSajuMonthNumber() {

        if (!monthBoundary) {
            throw new IllegalStateException(
                    korean
                            + "("
                            + hanja
                            + ")은 사주 월 경계 절기가 아닙니다."
            );
        }

        return sajuMonthNumber;
    }

    public SolarTerm next() {

        int nextIndex =
                Math.floorMod(
                        ordinal() + 1,
                        VALUES.length
                );

        return VALUES[nextIndex];
    }

    public SolarTerm previous() {

        int previousIndex =
                Math.floorMod(
                        ordinal() - 1,
                        VALUES.length
                );

        return VALUES[previousIndex];
    }

    @Override
    public String toString() {
        return korean + "(" + hanja + ")";
    }
}