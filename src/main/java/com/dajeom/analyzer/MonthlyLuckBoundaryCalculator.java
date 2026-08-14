package com.dajeom.calculator;

import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.MonthlyLuckBoundaryAnalysis;
import com.dajeom.model.MonthlyLuckBoundarySequenceAnalysis;
import com.dajeom.model.SolarTermPoint;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-1.
 *
 * 월운 절입 경계 Calculator.
 *
 * =================================================
 *
 * 기존 SolarTermCalculator를
 * canonical source로 사용한다.
 *
 * 새로운 절기표를 만들지 않는다.
 *
 * =================================================
 *
 * 경계:
 *
 * 입춘
 * 경칩
 * 청명
 * 입하
 * 망종
 * 소서
 * 입추
 * 백로
 * 한로
 * 입동
 * 대설
 * 소한
 *
 * =================================================
 *
 * 각 월운:
 *
 * [현재 절입, 다음 절입)
 *
 * =================================================
 *
 * 정확한 절입 순간에는
 * 새 월운으로 넘어간다.
 */
@Component
public class MonthlyLuckBoundaryCalculator {

    private static final int MONTH_COUNT = 12;

    private final SolarTermCalculator
            solarTermCalculator;

    public MonthlyLuckBoundaryCalculator(
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
     * 특정 실제 시각의 월운 절입구간
     * =================================================
     *
     * 공식 15-1 API.
     */
    public MonthlyLuckBoundaryAnalysis analyzeAt(

            ZonedDateTime referenceDateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "월운 분석 기준시각이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "월운 분석에는 ZoneId가 필요합니다."
        );

        ZonedDateTime normalized =
                referenceDateTime
                        .withZoneSameInstant(
                                zoneId
                        );

        LocalDateTime localDateTime =
                normalized
                        .toLocalDateTime();

        /*
         * =================================================
         * 현재 월 시작 절입
         * =================================================
         *
         * 기존 findPreviousMonthBoundary는
         * 정확한 절입 순간을 포함한다.
         */

        SolarTermPoint startBoundary =
                solarTermCalculator
                        .findPreviousMonthBoundary(
                                localDateTime,
                                zoneId
                        );

        /*
         * =================================================
         * 다음 월 시작 절입
         * =================================================
         *
         * findNextMonthBoundary는
         * target보다 엄격하게 이후인 절입을 반환한다.
         */

        SolarTermPoint endBoundary =
                solarTermCalculator
                        .findNextMonthBoundary(
                                localDateTime,
                                zoneId
                        );

        /*
         * =================================================
         * 명리 연도
         * =================================================
         *
         * 입춘 이전이면 전년도.
         * 입춘 순간부터 해당 연도.
         */

        int sajuYear =
                solarTermCalculator
                        .determineSajuYear(
                                localDateTime,
                                zoneId
                        );

        MonthlyLuckBoundaryAnalysis analysis =
                new MonthlyLuckBoundaryAnalysis(
                        sajuYear,
                        startBoundary.monthIndex(),
                        zoneId,
                        startBoundary,
                        endBoundary
                );

        /*
         * 기준시각이 실제 반환 구간 안에 있는지
         * 최종 확인한다.
         */

        if (
                !analysis.contains(
                        normalized
                )
        ) {

            throw new IllegalStateException(
                    "월운 절입 경계와 기준시각이 일치하지 않습니다."
            );
        }

        return analysis;
    }

    /**
     * LocalDateTime 버전.
     */
    public MonthlyLuckBoundaryAnalysis analyzeAt(

            LocalDateTime referenceDateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "월운 분석 기준일시가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "월운 분석에는 ZoneId가 필요합니다."
        );

        return analyzeAt(
                referenceDateTime
                        .atZone(
                                zoneId
                        ),
                zoneId
        );
    }

    /**
     * =================================================
     * 하나의 세운 안의 12개 월운 경계 전체
     * =================================================
     *
     * 14차 AnnualLuckPeriod을 기준으로:
     *
     * 입춘
     * ↓
     * 12개 절입월
     * ↓
     * 다음 입춘
     *
     * 을 생성한다.
     */
    public MonthlyLuckBoundarySequenceAnalysis
    analyzeAnnualPeriod(
            AnnualLuckPeriod annualPeriod
    ) {

        Objects.requireNonNull(
                annualPeriod,
                "월운 Sequence 생성에는 AnnualLuckPeriod가 필요합니다."
        );

        ZoneId zoneId =
                annualPeriod
                        .zoneId();

        ZonedDateTime cursor =
                annualPeriod
                        .startDateTimeInclusive();

        List<MonthlyLuckBoundaryAnalysis>
                months =
                new ArrayList<>(
                        MONTH_COUNT
                );

        for (
                int expectedMonthIndex = 0;
                expectedMonthIndex < MONTH_COUNT;
                expectedMonthIndex++
        ) {

            MonthlyLuckBoundaryAnalysis month =
                    analyzeAt(
                            cursor,
                            zoneId
                    );

            /*
             * =================================================
             * 세운 첫 달은 입춘 monthIndex 0이어야 하며
             * 이후 0~11 순으로 진행해야 한다.
             * =================================================
             */

            if (
                    month.monthIndex()
                            != expectedMonthIndex
            ) {

                throw new IllegalStateException(
                        "월운 monthIndex가 예상 순서와 다릅니다. "
                                + "예상="
                                + expectedMonthIndex
                                + ", 실제="
                                + month.monthIndex()
                );
            }

            /*
             * =================================================
             * 12개 월운 모두 같은 명리연도에 속해야 한다.
             *
             * 소한월이 달력상 다음 해 1월이어도
             * 다음 입춘 전이므로 같은 sajuYear다.
             * =================================================
             */

            if (
                    month.sajuYear()
                            != annualPeriod.targetYear()
            ) {

                throw new IllegalStateException(
                        "월운 명리연도와 AnnualLuckPeriod targetYear가 일치하지 않습니다."
                );
            }

            /*
             * cursor는 정확히 현재 절입이어야 한다.
             */

            if (
                    !month
                            .startDateTimeInclusive()
                            .toInstant()
                            .equals(
                                    cursor.toInstant()
                            )
            ) {

                throw new IllegalStateException(
                        "월운 Sequence 시작 절입이 연속되지 않습니다."
                );
            }

            months.add(
                    month
            );

            /*
             * 다음 절입 순간부터
             * 바로 다음 월운.
             */

            cursor =
                    month
                            .endDateTimeExclusive();
        }

        /*
         * =================================================
         * 12개월 후 반드시 다음 입춘이어야 한다.
         * =================================================
         */

        if (
                !cursor
                        .toInstant()
                        .equals(
                                annualPeriod
                                        .endDateTimeExclusive()
                                        .toInstant()
                        )
        ) {

            throw new IllegalStateException(
                    "12개 월운 종료시각과 AnnualLuckPeriod 종료 입춘이 일치하지 않습니다."
            );
        }

        return new MonthlyLuckBoundarySequenceAnalysis(
                annualPeriod,
                months
        );
    }
}