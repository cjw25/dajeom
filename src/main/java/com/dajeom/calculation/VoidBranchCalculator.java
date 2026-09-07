package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.Ganji;
import com.dajeom.domain.SexagenaryCycle;
import com.dajeom.domain.VoidBranches;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 공망 계산기.
 *
 * 60갑자는 10개씩 6개의 旬으로 나뉜다.
 *
 * 0 ~ 9
 * 甲子旬
 * → 戌亥 공망
 *
 * 10 ~ 19
 * 甲戌旬
 * → 申酉 공망
 *
 * 20 ~ 29
 * 甲申旬
 * → 午未 공망
 *
 * 30 ~ 39
 * 甲午旬
 * → 辰巳 공망
 *
 * 40 ~ 49
 * 甲辰旬
 * → 寅卯 공망
 *
 * 50 ~ 59
 * 甲寅旬
 * → 子丑 공망
 */
@Component
public class VoidBranchCalculator {

    private static final int XUN_SIZE =
            10;

    public VoidBranches calculate(
            Ganji ganji
    ) {

        Objects.requireNonNull(
                ganji,
                "공망 계산 기준 간지가 필요합니다."
        );

        /*
         * 현재 60갑자 index가 속한
         * 10개 단위 旬의 시작 index.
         *
         * 예:
         *
         * 癸酉 index 9
         * → 0
         * → 甲子
         *
         * 丁丑 index 13
         * → 10
         * → 甲戌
         */
        int xunStartIndex =
                (ganji.cycleIndex() / XUN_SIZE)
                        * XUN_SIZE;

        Ganji xunStartGanji =
                SexagenaryCycle.ofIndex(
                        xunStartIndex
                );

        /*
         * 旬 시작 지지 바로 앞의 두 지지가
         * 해당 旬의 공망이다.
         *
         * 甲子旬:
         *
         * 子 index 0
         *
         * -2 → 戌
         * -1 → 亥
         *
         * 따라서 戌亥.
         */
        int startBranchIndex =
                xunStartGanji
                        .branch()
                        .index();

        EarthlyBranch first =
                EarthlyBranch.ofIndex(
                        startBranchIndex - 2
                );

        EarthlyBranch second =
                EarthlyBranch.ofIndex(
                        startBranchIndex - 1
                );

        return new VoidBranches(
                ganji,
                xunStartGanji,
                first,
                second
        );
    }
}