package com.dajeom.calculator;

import com.dajeom.model.SexagenaryPillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 육십갑자 순환 계산기.
 *
 * =================================================
 *
 * 甲子 = 0
 *
 * 천간:
 * 甲 乙 丙 丁 戊 己 庚 辛 壬 癸
 *
 * 지지:
 * 子 丑 寅 卯 辰 巳 午 未 申 酉 戌 亥
 *
 * =================================================
 *
 * 이 Calculator는:
 *
 * - 대운
 * - 세운
 * - 월운
 *
 * 에서 공통으로 사용한다.
 *
 * 길흉이나 십성은 계산하지 않는다.
 */
@Component
public class SexagenaryCycleCalculator {

    private static final List<HeavenlyStem> STEMS =
            List.of(
                    HeavenlyStem.JIA,
                    HeavenlyStem.YI,
                    HeavenlyStem.BING,
                    HeavenlyStem.DING,
                    HeavenlyStem.WU,
                    HeavenlyStem.JI,
                    HeavenlyStem.GENG,
                    HeavenlyStem.XIN,
                    HeavenlyStem.REN,
                    HeavenlyStem.GUI
            );

    private static final List<EarthlyBranch> BRANCHES =
            List.of(
                    EarthlyBranch.ZI,
                    EarthlyBranch.CHOU,
                    EarthlyBranch.YIN,
                    EarthlyBranch.MAO,
                    EarthlyBranch.CHEN,
                    EarthlyBranch.SI,
                    EarthlyBranch.WU,
                    EarthlyBranch.WEI,
                    EarthlyBranch.SHEN,
                    EarthlyBranch.YOU,
                    EarthlyBranch.XU,
                    EarthlyBranch.HAI
            );

    /**
     * =================================================
     * cycleIndex -> Pillar
     * =================================================
     */
    public SexagenaryPillar pillarAt(
            int cycleIndex
    ) {

        int normalized =
                Math.floorMod(
                        cycleIndex,
                        60
                );

        HeavenlyStem stem =
                STEMS.get(
                        normalized % 10
                );

        EarthlyBranch branch =
                BRANCHES.get(
                        normalized % 12
                );

        return new SexagenaryPillar(
                normalized,
                stem,
                branch
        );
    }

    /**
     * =================================================
     * 천간 + 지지 -> cycleIndex
     * =================================================
     */
    public int cycleIndexOf(

            HeavenlyStem stem,

            EarthlyBranch branch

    ) {

        Objects.requireNonNull(
                stem,
                "육십갑자 계산에는 천간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "육십갑자 계산에는 지지가 필요합니다."
        );

        int stemIndex =
                STEMS.indexOf(
                        stem
                );

        int branchIndex =
                BRANCHES.indexOf(
                        branch
                );

        if (
                stemIndex < 0
                        || branchIndex < 0
        ) {

            throw new IllegalArgumentException(
                    "등록되지 않은 천간 또는 지지입니다."
            );
        }

        for (
                int cycleIndex = 0;
                cycleIndex < 60;
                cycleIndex++
        ) {

            if (
                    cycleIndex % 10
                            == stemIndex

                            && cycleIndex % 12
                            == branchIndex
            ) {

                return cycleIndex;
            }
        }

        /*
         * 천간·지지 음양이 맞지 않는
         * 존재하지 않는 조합.
         *
         * 예:
         *
         * 甲丑
         *
         * 같은 조합은 육십갑자에 없다.
         */
        throw new IllegalArgumentException(
                "육십갑자에 존재하지 않는 천간·지지 조합입니다: "
                        + stem
                        + " / "
                        + branch
        );
    }

    /**
     * =================================================
     * 천간 + 지지 -> Pillar
     * =================================================
     */
    public SexagenaryPillar pillarOf(

            HeavenlyStem stem,

            EarthlyBranch branch

    ) {

        int cycleIndex =
                cycleIndexOf(
                        stem,
                        branch
                );

        return pillarAt(
                cycleIndex
        );
    }

    /**
     * =================================================
     * 이동
     * =================================================
     *
     * step:
     *
     * +1 = 다음 간지
     * -1 = 이전 간지
     */
    public SexagenaryPillar shift(

            SexagenaryPillar base,

            int step

    ) {

        Objects.requireNonNull(
                base,
                "육십갑자 이동 기준 Pillar가 필요합니다."
        );

        return pillarAt(
                base.cycleIndex()
                        + step
        );
    }

    public SexagenaryPillar shift(

            HeavenlyStem stem,

            EarthlyBranch branch,

            int step

    ) {

        return shift(
                pillarOf(
                        stem,
                        branch
                ),
                step
        );
    }

    public SexagenaryPillar next(
            SexagenaryPillar base
    ) {

        return shift(
                base,
                1
        );
    }

    public SexagenaryPillar previous(
            SexagenaryPillar base
    ) {

        return shift(
                base,
                -1
        );
    }
}