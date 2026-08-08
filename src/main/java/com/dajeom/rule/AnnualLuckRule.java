package com.dajeom.rule;

import com.dajeom.constant.SajuConstants;

/**
 * 세운 계산 Rule.
 *
 * 1984년은 갑자년이다.
 *
 * 따라서:
 *
 * targetYear - 1984
 *
 * 값을 육십갑자 60으로 순환시켜
 * 해당 연도의 간지를 구한다.
 *
 * 세운의 실제 시작시각은
 * 해당 연도의 입춘이다.
 */
public final class AnnualLuckRule {

    public static final String RULE_VERSION =
            "DAJEOM_ANNUAL_LUCK_V1";

    private AnnualLuckRule() {

        throw new IllegalStateException(
                "AnnualLuckRule은 생성할 수 없습니다."
        );
    }

    /**
     * 세운 연도를 육십갑자 index로 변환한다.
     *
     * 1984:
     * 0 = 甲子
     *
     * 1985:
     * 1 = 乙丑
     *
     * ...
     */
    public static int calculateCycleIndex(
            int sajuYear
    ) {

        return Math.floorMod(
                sajuYear
                        - SajuConstants
                        .GAPJA_BASE_YEAR,
                SajuConstants
                        .GANJI_CYCLE_SIZE
        );
    }
}