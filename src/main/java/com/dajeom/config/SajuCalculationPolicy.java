package com.dajeom.config;

import java.time.LocalTime;
import java.time.ZoneId;

/**
 * 다점 사주 계산에 공통으로 적용하는 백엔드 내부 정책이다.
 *
 * 해당 설정은 일반 사용자 프론트 화면에서 선택받지 않고
 * 백엔드에서 일관되게 적용한다.
 */
public final class SajuCalculationPolicy {

    /**
     * 대한민국 표준 시간대.
     */
    public static final ZoneId DEFAULT_ZONE_ID =
            ZoneId.of("Asia/Seoul");

    /**
     * 사용자가 입력한 출생기록 시간을 그대로 사용한다.
     */
    public static final boolean APPLY_TRUE_SOLAR_TIME = false;

    /**
     * 출생지 경도에 따른 시간 보정을 적용하지 않는다.
     */
    public static final boolean APPLY_LONGITUDE_CORRECTION = false;

    /**
     * 출생시간에서 30분을 일괄 차감하지 않는다.
     */
    public static final boolean APPLY_FIXED_TIME_CORRECTION = false;

    /**
     * 날짜 변경은 자정 00시를 기준으로 한다.
     *
     * 야자시를 적용하지 않는다.
     */
    public static final LocalTime DAY_CHANGE_TIME =
            LocalTime.MIDNIGHT;

    /**
     * 연주와 월주는 24절기 기준으로 계산한다.
     */
    public static final boolean USE_SOLAR_TERM_STANDARD = true;

    /**
     * 1차 엔진이 지원하는 출생연도 범위.
     */
    public static final int MIN_SUPPORTED_YEAR = 1900;

    public static final int MAX_SUPPORTED_YEAR = 2100;

    private SajuCalculationPolicy() {
    }
}