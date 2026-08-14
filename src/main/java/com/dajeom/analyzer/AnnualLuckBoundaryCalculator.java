package com.dajeom.calculator;

import com.dajeom.model.AnnualLuckBoundaryAnalysis;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 14차-1.
 *
 * 세운 연도 및 입춘 경계 Calculator.
 *
 * =================================================
 *
 * 세운 연도 기준:
 *
 * 해당 연도의 입춘 실제시각
 *
 * ~
 *
 * 다음 연도의 입춘 실제시각
 *
 * =================================================
 *
 * 예:
 *
 * targetYear = 2026
 *
 * start
 * = calculateIpchun(2026)
 *
 * end
 * = calculateIpchun(2027)
 *
 * =================================================
 *
 * 중요한 정책:
 *
 * 1.
 * 1월 1일을 세운 시작으로 사용하지 않는다.
 *
 * 2.
 * 2월 4일 00:00를 고정값으로 사용하지 않는다.
 *
 * 3.
 * 기존 SolarTermCalculator의
 * 실제 입춘시각을 canonical source로 사용한다.
 *
 * 4.
 * 입춘 정확한 순간부터
 * 새 세운 연도로 처리한다.
 *
 * 5.
 * ZoneId를 명시적으로 사용한다.
 */
@Component
public class AnnualLuckBoundaryCalculator {

    private final SolarTermCalculator
            solarTermCalculator;

    public AnnualLuckBoundaryCalculator(
            SolarTermCalculator solarTermCalculator
    ) {

        this.solarTermCalculator =
                Objects.requireNonNull(
                        solarTermCalculator,
                        "SolarTermCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 특정 세운 연도의 범위
     * =================================================
     *
     * 공식 14-1 API.
     *
     * 예:
     *
     * analyze(2026, Asia/Seoul)
     *
     * ->
     *
     * 2026 입춘
     * ~
     * 2027 입춘
     */
    public AnnualLuckBoundaryAnalysis analyze(

            int targetYear,

            ZoneId zoneId

    ) {

        validateYear(
                targetYear
        );

        Objects.requireNonNull(
                zoneId,
                "세운 경계 계산에는 ZoneId가 필요합니다."
        );

        ZonedDateTime start =
                solarTermCalculator
                        .calculateIpchun(
                                targetYear,
                                zoneId
                        );

        ZonedDateTime end =
                solarTermCalculator
                        .calculateIpchun(
                                targetYear + 1,
                                zoneId
                        );

        return new AnnualLuckBoundaryAnalysis(
                targetYear,
                zoneId,
                start,
                end
        );
    }

    /**
     * =================================================
     * 특정 시각이 어느 세운 연도인가
     * =================================================
     *
     * 기존 SolarTermCalculator의
     * determineSajuYear() 정책을 그대로 사용한다.
     *
     * 입춘 이전:
     *
     * calendarYear - 1
     *
     * 입춘 순간 및 이후:
     *
     * calendarYear
     */
    public int determineAnnualLuckYear(

            LocalDateTime dateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                dateTime,
                "세운 연도 판정에는 기준일시가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "세운 연도 판정에는 ZoneId가 필요합니다."
        );

        return solarTermCalculator
                .determineSajuYear(
                        dateTime,
                        zoneId
                );
    }

    /**
     * ZonedDateTime 입력.
     *
     * 입력 Zone과 분석 Zone이 달라도
     * 같은 Instant 기준으로 변환한다.
     */
    public int determineAnnualLuckYear(

            ZonedDateTime dateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                dateTime,
                "세운 연도 판정에는 기준시각이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "세운 연도 판정에는 ZoneId가 필요합니다."
        );

        LocalDateTime normalized =
                dateTime
                        .withZoneSameInstant(
                                zoneId
                        )
                        .toLocalDateTime();

        return determineAnnualLuckYear(
                normalized,
                zoneId
        );
    }

    /**
     * =================================================
     * 기준 시각으로 바로 세운 경계 분석
     * =================================================
     */
    public AnnualLuckBoundaryAnalysis analyzeAt(

            ZonedDateTime referenceDateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "세운 분석 기준시각이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "세운 분석에는 ZoneId가 필요합니다."
        );

        int annualLuckYear =
                determineAnnualLuckYear(
                        referenceDateTime,
                        zoneId
                );

        AnnualLuckBoundaryAnalysis analysis =
                analyze(
                        annualLuckYear,
                        zoneId
                );

        /*
         * determineSajuYear와
         * 실제 입춘 경계 결과가 일치하는지
         * 한 번 더 검증한다.
         */

        if (
                !analysis.contains(
                        referenceDateTime
                )
        ) {

            throw new IllegalStateException(
                    "세운 연도 판정과 입춘 경계 계산 결과가 일치하지 않습니다."
            );
        }

        return analysis;
    }

    /**
     * LocalDateTime 버전.
     */
    public AnnualLuckBoundaryAnalysis analyzeAt(

            LocalDateTime referenceDateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "세운 분석 기준일시가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "세운 분석에는 ZoneId가 필요합니다."
        );

        return analyzeAt(
                referenceDateTime
                        .atZone(
                                zoneId
                        ),
                zoneId
        );
    }

    private void validateYear(
            int targetYear
    ) {

        /*
         * 실제 지원 연도 제한은
         * SolarTermCalculator의 validateYear()가
         * canonical source다.
         *
         * 여기서는 가장 기본적인 값만 막는다.
         */
        if (
                targetYear < 1
        ) {

            throw new IllegalArgumentException(
                    "세운 targetYear는 1 이상이어야 합니다."
            );
        }

        /*
         * targetYear + 1 overflow 방어.
         */
        if (
                targetYear == Integer.MAX_VALUE
        ) {

            throw new IllegalArgumentException(
                    "다음 해 입춘을 계산할 수 없는 targetYear입니다."
            );
        }
    }
}