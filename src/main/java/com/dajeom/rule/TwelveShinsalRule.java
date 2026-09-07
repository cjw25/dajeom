package com.dajeom.rule;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.TwelveShinsal;
import com.dajeom.domain.TwelveShinsalGroup;

import java.util.Objects;

public final class TwelveShinsalRule {

    private TwelveShinsalRule() {
    }

    /**
     * 년지 / 일지를 삼합 그룹으로 변환.
     */
    public static TwelveShinsalGroup groupOf(
            EarthlyBranch referenceBranch
    ) {

        Objects.requireNonNull(
                referenceBranch,
                "12신살 기준 지지가 필요합니다."
        );

        return switch (referenceBranch) {

            case HAI, MAO, WEI ->
                    TwelveShinsalGroup.HAE_MYO_MI;

            case YIN, WU, XU ->
                    TwelveShinsalGroup.IN_O_SUL;

            case SI, YOU, CHOU ->
                    TwelveShinsalGroup.SA_YU_CHUK;

            case SHEN, ZI, CHEN ->
                    TwelveShinsalGroup.SIN_JA_JIN;
        };
    }

    /**
     * 일간을 12신살용 삼합 운동 그룹으로 변환.
     *
     * 甲 癸
     * → 亥卯未
     *
     * 乙 丙 戊
     * → 寅午戌
     *
     * 丁 己 庚
     * → 巳酉丑
     *
     * 辛 壬
     * → 申子辰
     */
    public static TwelveShinsalGroup groupOf(
            HeavenlyStem dayStem
    ) {

        Objects.requireNonNull(
                dayStem,
                "12신살 계산을 위한 일간이 필요합니다."
        );

        return switch (dayStem) {

            case JIA, GUI ->
                    TwelveShinsalGroup.HAE_MYO_MI;

            case YI, BING, WU ->
                    TwelveShinsalGroup.IN_O_SUL;

            case DING, JI, GENG ->
                    TwelveShinsalGroup.SA_YU_CHUK;

            case XIN, REN ->
                    TwelveShinsalGroup.SIN_JA_JIN;
        };
    }

    /**
     * 기준 그룹과 대상 지지로 12신살 하나를 계산한다.
     *
     * 각 그룹의 겁살 위치에서 시작해
     * 지지를 순행하면서:
     *
     * 겁살
     * 재살
     * 천살
     * 지살
     * 년살
     * 월살
     * 망신살
     * 장성살
     * 반안살
     * 역마살
     * 육해살
     * 화개살
     *
     * 순으로 대응한다.
     */
    public static TwelveShinsal calculate(
            TwelveShinsalGroup group,
            EarthlyBranch targetBranch
    ) {

        Objects.requireNonNull(
                group,
                "12신살 기준 그룹이 필요합니다."
        );

        Objects.requireNonNull(
                targetBranch,
                "12신살 대상 지지가 필요합니다."
        );

        EarthlyBranch start =
                group.gyeopSalStartBranch();

        int distance =
                Math.floorMod(
                        targetBranch.index()
                                - start.index(),
                        12
                );

        return TwelveShinsal.ofIndex(
                distance
        );
    }
}