package com.dajeom.calculator;

import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.MonthlyLuckBoundaryAnalysis;
import com.dajeom.model.MonthlyLuckBoundarySequenceAnalysis;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.MonthlyLuckSequenceAnalysis;
import com.dajeom.model.SexagenaryPillar;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-2.
 *
 * 월운 Period 조립 Calculator.
 *
 * =================================================
 *
 * 15-1:
 *
 * MonthlyLuckBoundaryAnalysis
 *
 * +
 *
 * 15-2:
 *
 * MonthlyLuckPillarCalculator
 *
 * ↓
 *
 * MonthlyLuckPeriod
 *
 * =================================================
 *
 * 하나의 세운 전체를 입력하면
 * 12개 월운 Sequence까지 생성할 수 있다.
 */
@Component
public class MonthlyLuckPeriodCalculator {

    private final MonthlyLuckBoundaryCalculator
            monthlyLuckBoundaryCalculator;

    private final MonthlyLuckPillarCalculator
            monthlyLuckPillarCalculator;

    public MonthlyLuckPeriodCalculator(

            MonthlyLuckBoundaryCalculator
                    monthlyLuckBoundaryCalculator,

            MonthlyLuckPillarCalculator
                    monthlyLuckPillarCalculator

    ) {

        this.monthlyLuckBoundaryCalculator =
                Objects.requireNonNull(
                        monthlyLuckBoundaryCalculator,
                        "MonthlyLuckBoundaryCalculator가 필요합니다."
                );

        this.monthlyLuckPillarCalculator =
                Objects.requireNonNull(
                        monthlyLuckPillarCalculator,
                        "MonthlyLuckPillarCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15-2 API
     * =================================================
     *
     * 이미 15-1 Boundary가 존재하는 경우.
     */
    public MonthlyLuckPeriod calculate(

            AnnualLuckPeriod annualPeriod,

            MonthlyLuckBoundaryAnalysis boundaryAnalysis

    ) {

        Objects.requireNonNull(
                annualPeriod,
                "월운 Period 계산에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                boundaryAnalysis,
                "월운 Period 계산에는 MonthlyLuckBoundaryAnalysis가 필요합니다."
        );

        SexagenaryPillar pillar =
                monthlyLuckPillarCalculator
                        .calculate(
                                annualPeriod,
                                boundaryAnalysis
                        );

        /*
         * =================================================
         * 월지 검증
         * =================================================
         */

        if (
                !monthlyLuckPillarCalculator
                        .matchesMonthBranch(
                                pillar,
                                boundaryAnalysis.monthIndex()
                        )
        ) {

            throw new IllegalStateException(
                    "월운 간지의 지지와 monthIndex가 일치하지 않습니다."
            );
        }

        return new MonthlyLuckPeriod(
                annualPeriod,
                boundaryAnalysis,
                pillar
        );
    }

    /**
     * =================================================
     * 특정 시점의 월운
     * =================================================
     *
     * AnnualLuckPeriod는 이미 14차에서
     * 확정되어 있다고 가정한다.
     */
    public MonthlyLuckPeriod calculateAt(

            AnnualLuckPeriod annualPeriod,

            ZonedDateTime referenceDateTime

    ) {

        Objects.requireNonNull(
                annualPeriod,
                "월운 계산에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                referenceDateTime,
                "월운 계산 기준시각이 필요합니다."
        );

        if (
                !annualPeriod.contains(
                        referenceDateTime
                )
        ) {

            throw new IllegalArgumentException(
                    "기준시각이 현재 AnnualLuckPeriod 범위에 포함되지 않습니다."
            );
        }

        MonthlyLuckBoundaryAnalysis boundary =
                monthlyLuckBoundaryCalculator
                        .analyzeAt(
                                referenceDateTime,
                                annualPeriod.zoneId()
                        );

        if (
                boundary.sajuYear()
                        != annualPeriod.targetYear()
        ) {

            throw new IllegalStateException(
                    "기준시각의 월운 명리연도와 AnnualLuckPeriod가 일치하지 않습니다."
            );
        }

        return calculate(
                annualPeriod,
                boundary
        );
    }

    /**
     * LocalDateTime 버전.
     */
    public MonthlyLuckPeriod calculateAt(

            AnnualLuckPeriod annualPeriod,

            LocalDateTime referenceDateTime

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "월운 계산 기준일시가 필요합니다."
        );

        return calculateAt(
                annualPeriod,
                referenceDateTime.atZone(
                        annualPeriod.zoneId()
                )
        );
    }

    /**
     * =================================================
     * 세운 전체 12개 월운 생성
     * =================================================
     */
    public MonthlyLuckSequenceAnalysis
    calculateAnnualPeriod(
            AnnualLuckPeriod annualPeriod
    ) {

        Objects.requireNonNull(
                annualPeriod,
                "월운 Sequence 계산에는 AnnualLuckPeriod가 필요합니다."
        );

        MonthlyLuckBoundarySequenceAnalysis
                boundarySequence =
                monthlyLuckBoundaryCalculator
                        .analyzeAnnualPeriod(
                                annualPeriod
                        );

        List<MonthlyLuckPeriod> periods =
                new ArrayList<>(
                        boundarySequence.monthCount()
                );

        for (
                MonthlyLuckBoundaryAnalysis boundary
                : boundarySequence.months()
        ) {

            periods.add(
                    calculate(
                            annualPeriod,
                            boundary
                    )
            );
        }

        return new MonthlyLuckSequenceAnalysis(
                annualPeriod,
                boundarySequence,
                periods
        );
    }

    /**
     * =================================================
     * 특정 명리 월 index 직접 조회
     * =================================================
     *
     * 절입 Boundary를 임의 생성하지 않는다.
     * 반드시 15-1 Sequence 안에서 가져온다.
     */
    public MonthlyLuckPeriod calculateByMonthIndex(

            AnnualLuckPeriod annualPeriod,

            int monthIndex

    ) {

        Objects.requireNonNull(
                annualPeriod,
                "월운 계산에는 AnnualLuckPeriod가 필요합니다."
        );

        MonthlyLuckBoundarySequenceAnalysis
                boundarySequence =
                monthlyLuckBoundaryCalculator
                        .analyzeAnnualPeriod(
                                annualPeriod
                        );

        MonthlyLuckBoundaryAnalysis boundary =
                boundarySequence
                        .monthByIndex(
                                monthIndex
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "monthIndex는 0~11이어야 합니다: "
                                                        + monthIndex
                                        )
                        );

        return calculate(
                annualPeriod,
                boundary
        );
    }
}