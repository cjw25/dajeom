package com.dajeom.solar;

import net.time4j.Moment;
import net.time4j.TemporalType;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 24절기의 실제 시작 순간을 계산한다.
 *
 * 사주 계산 규칙 자체를 Time4J에 맡기는 것이 아니다.
 *
 * Time4J는 오직:
 *
 * "태양이 특정 황경에 도달하는 순간"
 *
 * 을 구하기 위한 천문 계산기로만 사용한다.
 */
@Component
public class SolarTermCalculator {

    /**
     * 일반적인 절기 날짜에서 실제 절입은 하루 이상 크게
     * 벗어나지 않지만, 계산 탐색은 넉넉하게 ±5일로 잡는다.
     *
     * 다음 절기까지는 약 15일이므로
     * 이 범위에서는 다음 절기까지 넘어가지 않는다.
     */
    private static final int SEARCH_RADIUS_DAYS = 5;

    /**
     * 밀리초 단위까지 경계를 찾는다.
     */
    public SolarTermInstant calculate(
            int gregorianYear,
            SolarTerm term
    ) {

        Objects.requireNonNull(
                term,
                "계산할 절기가 필요합니다."
        );

        LocalDate approximateDate =
                LocalDate.of(
                        gregorianYear,
                        term.approximateMonth(),
                        term.approximateDay()
                );

        Instant searchStart =
                approximateDate
                        .minusDays(
                                SEARCH_RADIUS_DAYS
                        )
                        .atStartOfDay(
                                ZoneOffset.UTC
                        )
                        .toInstant();

        Instant searchEnd =
                approximateDate
                        .plusDays(
                                SEARCH_RADIUS_DAYS + 1L
                        )
                        .atStartOfDay(
                                ZoneOffset.UTC
                        )
                        .toInstant()
                        .minusMillis(1);

        net.time4j.calendar.SolarTerm target =
                toTime4jSolarTerm(
                        term
                );

        Instant result =
                findFirstInstantOfTerm(
                        searchStart,
                        searchEnd,
                        target
                );

        return new SolarTermInstant(
                gregorianYear,
                term,
                result
        );
    }

    public List<SolarTermInstant> calculateYear(
            int gregorianYear
    ) {

        List<SolarTermInstant> result =
                new ArrayList<>(
                        SolarTerm.values().length
                );

        for (SolarTerm term : SolarTerm.values()) {

            result.add(
                    calculate(
                            gregorianYear,
                            term
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    private Instant findFirstInstantOfTerm(
            Instant searchStart,
            Instant searchEnd,
            net.time4j.calendar.SolarTerm target
    ) {

        if (isTargetTerm(
                searchStart,
                target
        )) {

            throw new IllegalStateException(
                    "절기 검색 시작시각이 이미 대상 절기 안에 있습니다. "
                            + "searchStart="
                            + searchStart
                            + ", target="
                            + target
            );
        }

        if (!isTargetTerm(
                searchEnd,
                target
        )) {

            throw new IllegalStateException(
                    "절기 검색 종료시각까지 대상 절기를 찾지 못했습니다. "
                            + "searchEnd="
                            + searchEnd
                            + ", target="
                            + target
            );
        }

        long low =
                searchStart.toEpochMilli();

        long high =
                searchEnd.toEpochMilli();

        /*
         * 첫 번째로 target 절기가 되는 millisecond를 찾는다.
         */
        while (low < high) {

            long middle =
                    low
                            + ((high - low) / 2L);

            Instant middleInstant =
                    Instant.ofEpochMilli(
                            middle
                    );

            if (isTargetTerm(
                    middleInstant,
                    target
            )) {

                high = middle;

            } else {

                low = middle + 1L;
            }
        }

        Instant result =
                Instant.ofEpochMilli(
                        low
                );

        /*
         * 안전 검증.
         */
        if (!isTargetTerm(
                result,
                target
        )) {

            throw new IllegalStateException(
                    "절기 경계 탐색 결과가 올바르지 않습니다. "
                            + "result="
                            + result
                            + ", target="
                            + target
            );
        }

        return result;
    }

    private boolean isTargetTerm(
            Instant instant,
            net.time4j.calendar.SolarTerm target
    ) {

        Moment moment =
                TemporalType.INSTANT.translate(
                        instant
                );

        net.time4j.calendar.SolarTerm current =
                net.time4j.calendar.SolarTerm.of(
                        moment
                );

        return current == target;
    }

    private net.time4j.calendar.SolarTerm toTime4jSolarTerm(
            SolarTerm term
    ) {

        return switch (term) {

            case XIAOHAN ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_12_XIAOHAN_285;

            case DAHAN ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_12_DAHAN_300;

            case LICHUN ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_01_LICHUN_315;

            case YUSHUI ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_01_YUSHUI_330;

            case JINGZHE ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_02_JINGZHE_345;

            case CHUNFEN ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_02_CHUNFEN_000;

            case QINGMING ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_03_QINGMING_015;

            case GUYU ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_03_GUYU_030;

            case LIXIA ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_04_LIXIA_045;

            case XIAOMAN ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_04_XIAOMAN_060;

            case MANGZHONG ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_05_MANGZHONG_075;

            case XIAZHI ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_05_XIAZHI_090;

            case XIAOSHU ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_06_XIAOSHU_105;

            case DASHU ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_06_DASHU_120;

            case LIQIU ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_07_LIQIU_135;

            case CHUSHU ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_07_CHUSHU_150;

            case BAILU ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_08_BAILU_165;

            case QIUFEN ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_08_QIUFEN_180;

            case HANLU ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_09_HANLU_195;

            case SHUANGJIANG ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_09_SHUANGJIANG_210;

            case LIDONG ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_10_LIDONG_225;

            case XIAOXUE ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_10_XIAOXUE_240;

            case DAXUE ->
                    net.time4j.calendar.SolarTerm
                            .MINOR_11_DAXUE_255;

            case DONGZHI ->
                    net.time4j.calendar.SolarTerm
                            .MAJOR_11_DONGZHI_270;
        };
    }
}