package com.dajeom.calculator;

import com.dajeom.model.AnnualLuckBoundaryAnalysis;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.SexagenaryPillar;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 14차-2.
 *
 * 세운 Period Calculator.
 *
 * =================================================
 *
 * 14-1
 *
 * AnnualLuckBoundaryAnalysis
 *
 * +
 *
 * 육십갑자 연도 계산
 *
 * ↓
 *
 * AnnualLuckPeriod
 *
 * =================================================
 *
 * 이 Calculator는 입춘을 다시 계산하지 않는다.
 *
 * 이미 14-1에서 확정한 Boundary를
 * 그대로 사용한다.
 */
@Component
public class AnnualLuckPeriodCalculator {

    private final AnnualLuckBoundaryCalculator
            annualLuckBoundaryCalculator;

    private final SexagenaryYearCalculator
            sexagenaryYearCalculator;

    public AnnualLuckPeriodCalculator(

            AnnualLuckBoundaryCalculator
                    annualLuckBoundaryCalculator,

            SexagenaryYearCalculator
                    sexagenaryYearCalculator

    ) {

        this.annualLuckBoundaryCalculator =
                Objects.requireNonNull(
                        annualLuckBoundaryCalculator,
                        "AnnualLuckBoundaryCalculator가 필요합니다."
                );

        this.sexagenaryYearCalculator =
                Objects.requireNonNull(
                        sexagenaryYearCalculator,
                        "SexagenaryYearCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 14-2 API
     * =================================================
     *
     * 14-1 결과가 이미 존재하는 경우.
     */
    public AnnualLuckPeriod calculate(
            AnnualLuckBoundaryAnalysis boundaryAnalysis
    ) {

        Objects.requireNonNull(
                boundaryAnalysis,
                "14-2 세운 계산에는 AnnualLuckBoundaryAnalysis가 필요합니다."
        );

        SexagenaryPillar pillar =
                sexagenaryYearCalculator
                        .calculate(
                                boundaryAnalysis
                                        .targetYear()
                        );

        return new AnnualLuckPeriod(
                boundaryAnalysis,
                pillar
        );
    }

    /**
     * =================================================
     * targetYear 직접 입력
     * =================================================
     *
     * 내부적으로 14-1을 먼저 실행한다.
     */
    public AnnualLuckPeriod calculate(

            int targetYear,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                zoneId,
                "세운 계산에는 ZoneId가 필요합니다."
        );

        AnnualLuckBoundaryAnalysis boundaryAnalysis =
                annualLuckBoundaryCalculator
                        .analyze(
                                targetYear,
                                zoneId
                        );

        return calculate(
                boundaryAnalysis
        );
    }

    /**
     * =================================================
     * 기준 시각으로 현재 세운 계산
     * =================================================
     */
    public AnnualLuckPeriod calculateAt(

            ZonedDateTime referenceDateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "세운 계산 기준시각이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "세운 계산에는 ZoneId가 필요합니다."
        );

        AnnualLuckBoundaryAnalysis boundaryAnalysis =
                annualLuckBoundaryCalculator
                        .analyzeAt(
                                referenceDateTime,
                                zoneId
                        );

        AnnualLuckPeriod result =
                calculate(
                        boundaryAnalysis
                );

        /*
         * 14-1과 14-2가 동일한 시점을
         * 가리키는지 최종 무결성 검사.
         */

        if (
                !result.contains(
                        referenceDateTime
                )
        ) {

            throw new IllegalStateException(
                    "세운 간지와 입춘 경계 계산 결과가 일치하지 않습니다."
            );
        }

        return result;
    }

    /**
     * LocalDateTime 버전.
     */
    public AnnualLuckPeriod calculateAt(

            LocalDateTime referenceDateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "세운 계산 기준일시가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "세운 계산에는 ZoneId가 필요합니다."
        );

        return calculateAt(
                referenceDateTime
                        .atZone(
                                zoneId
                        ),
                zoneId
        );
    }
}