package com.dajeom.constant;

import java.time.ZoneId;

/**
 * 사주 계산 전체에서 공통으로 사용하는
 * 기본 상수.
 */
public final class SajuConstants {

    /**
     * 서비스 기본 시간대.
     */
    public static final ZoneId DEFAULT_ZONE_ID =
            ZoneId.of(
                    "Asia/Seoul"
            );

    /**
     * 천간 개수.
     */
    public static final int HEAVENLY_STEM_COUNT =
            10;

    /**
     * 지지 개수.
     */
    public static final int EARTHLY_BRANCH_COUNT =
            12;

    /**
     * 육십갑자 개수.
     */
    public static final int GANJI_CYCLE_SIZE =
            60;

    /**
     * 갑자년 기준 연도.
     *
     * 1984년은 갑자년이다.
     */
    public static final int GAPJA_BASE_YEAR =
            1984;

    private SajuConstants() {
        throw new IllegalStateException(
                "SajuConstants는 생성할 수 없습니다."
        );
    }
}