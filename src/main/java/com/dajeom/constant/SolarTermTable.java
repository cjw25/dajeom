package com.dajeom.constant;

import com.dajeom.model.SolarTermDefinition;

import java.util.List;

/**
 * 24절기 기본 정의.
 *
 * 실제 절기 발생 날짜/시각은
 * SolarTermCalculator가 태양 황경을 이용해 계산한다.
 *
 * monthIndex >= 0 인 절기만
 * 사주 월의 경계가 되는 절기(절입)다.
 */
public final class SolarTermTable {

    private static final List<SolarTermDefinition>
            TERMS =
            List.of(

                    term(
                            "춘분",
                            "春分",
                            0.0,
                            -1
                    ),

                    term(
                            "청명",
                            "清明",
                            15.0,
                            2
                    ),

                    term(
                            "곡우",
                            "穀雨",
                            30.0,
                            -1
                    ),

                    term(
                            "입하",
                            "立夏",
                            45.0,
                            3
                    ),

                    term(
                            "소만",
                            "小滿",
                            60.0,
                            -1
                    ),

                    term(
                            "망종",
                            "芒種",
                            75.0,
                            4
                    ),

                    term(
                            "하지",
                            "夏至",
                            90.0,
                            -1
                    ),

                    term(
                            "소서",
                            "小暑",
                            105.0,
                            5
                    ),

                    term(
                            "대서",
                            "大暑",
                            120.0,
                            -1
                    ),

                    term(
                            "입추",
                            "立秋",
                            135.0,
                            6
                    ),

                    term(
                            "처서",
                            "處暑",
                            150.0,
                            -1
                    ),

                    term(
                            "백로",
                            "白露",
                            165.0,
                            7
                    ),

                    term(
                            "추분",
                            "秋分",
                            180.0,
                            -1
                    ),

                    term(
                            "한로",
                            "寒露",
                            195.0,
                            8
                    ),

                    term(
                            "상강",
                            "霜降",
                            210.0,
                            -1
                    ),

                    term(
                            "입동",
                            "立冬",
                            225.0,
                            9
                    ),

                    term(
                            "소설",
                            "小雪",
                            240.0,
                            -1
                    ),

                    term(
                            "대설",
                            "大雪",
                            255.0,
                            10
                    ),

                    term(
                            "동지",
                            "冬至",
                            270.0,
                            -1
                    ),

                    term(
                            "소한",
                            "小寒",
                            285.0,
                            11
                    ),

                    term(
                            "대한",
                            "大寒",
                            300.0,
                            -1
                    ),

                    term(
                            "입춘",
                            "立春",
                            315.0,
                            0
                    ),

                    term(
                            "우수",
                            "雨水",
                            330.0,
                            -1
                    ),

                    term(
                            "경칩",
                            "驚蟄",
                            345.0,
                            1
                    )
            );

    private SolarTermTable() {

        throw new IllegalStateException(
                "SolarTermTable은 생성할 수 없습니다."
        );
    }

    /**
     * 24절기 전체.
     */
    public static List<SolarTermDefinition> getAll() {

        return TERMS;
    }

    /**
     * 사주 월의 경계가 되는
     * 12개 절입만 반환한다.
     */
    public static List<SolarTermDefinition>
    getMonthBoundaries() {

        return TERMS
                .stream()
                .filter(
                        SolarTermDefinition::isMonthBoundary
                )
                .toList();
    }

    /**
     * 월 인덱스로 절입을 조회한다.
     *
     * 0 = 입춘
     * 1 = 경칩
     * 2 = 청명
     * ...
     * 11 = 소한
     */
    public static SolarTermDefinition
    findMonthBoundary(
            int monthIndex
    ) {

        if (
                monthIndex < 0
                        || monthIndex > 11
        ) {

            throw new IllegalArgumentException(
                    "월 인덱스는 0부터 11까지입니다."
            );
        }

        return TERMS
                .stream()
                .filter(
                        term ->
                                term.monthIndex()
                                        == monthIndex
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "월 경계 절기를 찾을 수 없습니다: "
                                                + monthIndex
                                )
                );
    }

    private static SolarTermDefinition term(
            String koreanName,
            String chineseName,
            double solarLongitude,
            int monthIndex
    ) {

        return new SolarTermDefinition(
                koreanName,
                chineseName,
                solarLongitude,
                monthIndex
        );
    }
}