package com.dajeom.time;

import com.dajeom.domain.BirthDateTime;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneRules;
import java.util.List;
import java.util.Objects;

@Component
public class BirthTimeCorrectionCalculator {

    /**
     * 만세력 천을귀인 기준
     * 대한민국 경도 보정.
     */
    public static final int KOREA_LONGITUDE_CORRECTION_MINUTES =
            -30;

    /**
     * 대한민국 역사적 표준시 / 썸머타임 판정에 사용.
     *
     * 단순 UTC+9 고정값을 사용하지 않는다.
     */
    public static final ZoneId KOREA_ZONE =
            ZoneId.of("Asia/Seoul");

    public BirthTimeCorrection calculate(
            BirthDateTime birth
    ) {

        Objects.requireNonNull(
                birth,
                "출생정보가 필요합니다."
        );

        if (birth.hasUnknownTime()) {

            throw new IllegalStateException(
                    "출생시간을 알 수 없어 시간보정을 수행할 수 없습니다."
            );
        }

        LocalDateTime originalDateTime =
                birth.requireDateTime();

        int longitudeCorrectionMinutes =
                KOREA_LONGITUDE_CORRECTION_MINUTES;

        int daylightSavingCorrectionMinutes =
                determineDaylightSavingCorrectionMinutes(
                        originalDateTime
                );

        int totalCorrectionMinutes =
                longitudeCorrectionMinutes
                        + daylightSavingCorrectionMinutes;

        LocalDateTime correctedDateTime =
                originalDateTime.plusMinutes(
                        totalCorrectionMinutes
                );

        return new BirthTimeCorrection(
                originalDateTime,
                longitudeCorrectionMinutes,
                daylightSavingCorrectionMinutes,
                correctedDateTime
        );
    }

    /**
     * 출생일시가 당시 대한민국 썸머타임 적용 중이었는지
     * Java의 Asia/Seoul 역사 시간대 데이터로 판정한다.
     *
     * 예:
     *
     * 일반 시기
     * DST = 0분
     * → 보정값 0
     *
     * 1988년 여름
     * DST = +60분으로 기록된 시기
     * → 사주 계산에서는 그 60분을 제거해야 하므로 -60
     */
    private int determineDaylightSavingCorrectionMinutes(
            LocalDateTime originalDateTime
    ) {

        ZoneRules rules =
                KOREA_ZONE.getRules();

        List<ZoneOffset> validOffsets =
                rules.getValidOffsets(
                        originalDateTime
                );

        /*
         * 썸머타임 시작 때는
         *
         * 예:
         * 02:00 → 03:00
         *
         * 처럼 실제 존재하지 않는 현지 시간이 생길 수 있다.
         *
         * 이런 시간은 임의로 앞/뒤 시간으로 바꾸지 않는다.
         */
        if (validOffsets.isEmpty()) {

            throw new IllegalArgumentException(
                    "해당 출생시각은 한국 시간대 변경으로 인해 "
                            + "실제로 존재하지 않는 현지시각입니다. "
                            + "birthDateTime="
                            + originalDateTime
            );
        }

        /*
         * 썸머타임 종료 때 시계가 뒤로 돌아가면
         * 같은 현지시각이 두 번 존재할 수 있다.
         *
         * 어느 쪽인지 천을귀인 규칙을 아직 확인하지 않았으므로
         * 임의 선택하지 않는다.
         */
        if (validOffsets.size() > 1) {

            throw new IllegalArgumentException(
                    "해당 출생시각은 한국 썸머타임 종료 경계의 "
                            + "중복 시각입니다. "
                            + "천을귀인 경계 규칙 확인이 필요합니다. "
                            + "birthDateTime="
                            + originalDateTime
            );
        }

        ZoneOffset actualOffset =
                validOffsets.get(0);

        Instant instant =
                originalDateTime.toInstant(
                        actualOffset
                );

        Duration daylightSaving =
                rules.getDaylightSavings(
                        instant
                );

        long daylightSavingMinutes =
                daylightSaving.toMinutes();

        /*
         * 당시 기록된 시각이 썸머타임 때문에
         * +60분 앞서 있었다면
         *
         * 사주 계산에서는 -60분을 해야 한다.
         */
        return Math.toIntExact(
                -daylightSavingMinutes
        );
    }
}