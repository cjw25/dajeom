package com.dajeom.rule;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.HeavenlyStem;

import java.util.List;
import java.util.Objects;

/**
 * 천을귀인(天乙貴人) 고정 규칙.
 *
 * 甲戊庚 → 丑未
 * 乙己   → 子申
 * 丙丁   → 亥酉
 * 壬癸   → 卯巳
 * 辛     → 午寅
 *
 * 생년월일별 예외를 두지 않고
 * 일간만으로 천을귀인 지지 두 개를 결정한다.
 */
public final class TianyiNoblemanRule {

    private TianyiNoblemanRule() {
    }

    public static List<EarthlyBranch> branches(
            HeavenlyStem dayStem
    ) {

        Objects.requireNonNull(
                dayStem,
                "천을귀인 계산을 위한 일간이 필요합니다."
        );

        return switch (dayStem) {

            case JIA, WU, GENG ->
                    List.of(
                            EarthlyBranch.CHOU,
                            EarthlyBranch.WEI
                    );

            case YI, JI ->
                    List.of(
                            EarthlyBranch.ZI,
                            EarthlyBranch.SHEN
                    );

            case BING, DING ->
                    List.of(
                            EarthlyBranch.HAI,
                            EarthlyBranch.YOU
                    );

            case REN, GUI ->
                    List.of(
                            EarthlyBranch.MAO,
                            EarthlyBranch.SI
                    );

            case XIN ->
                    List.of(
                            EarthlyBranch.WU,
                            EarthlyBranch.YIN
                    );
        };
    }
}