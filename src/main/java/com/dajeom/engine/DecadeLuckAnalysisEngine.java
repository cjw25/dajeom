package com.dajeom.engine;

import com.dajeom.calculator.DecadeLuckCalculator;
import com.dajeom.calculator.LuckDirectionCalculator;
import com.dajeom.calculator.LuckStartCalculator;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.FourPillars;
import com.dajeom.model.LuckStart;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.rule.DecadeLuckRule;
import com.dajeom.type.LuckDirection;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 대운 전체 분석 Engine.
 *
 * 처리:
 *
 * NormalizedBirthData
 *      ↓
 * LuckDirectionCalculator
 *      ↓
 * LuckStartCalculator
 *      ↓
 * DecadeLuckCalculator
 *      ↓
 * 현재 대운 탐색
 *      ↓
 * DecadeLuckAnalysis
 *
 * 출생시간 미상인 경우에는
 * 대운 시작 나이를 임의 계산하지 않는다.
 */
@Component
public class DecadeLuckAnalysisEngine {

    private final LuckDirectionCalculator
            luckDirectionCalculator;

    private final LuckStartCalculator
            luckStartCalculator;

    private final DecadeLuckCalculator
            decadeLuckCalculator;

    public DecadeLuckAnalysisEngine(
            LuckDirectionCalculator luckDirectionCalculator,
            LuckStartCalculator luckStartCalculator,
            DecadeLuckCalculator decadeLuckCalculator
    ) {

        this.luckDirectionCalculator =
                luckDirectionCalculator;

        this.luckStartCalculator =
                luckStartCalculator;

        this.decadeLuckCalculator =
                decadeLuckCalculator;
    }

    public DecadeLuckAnalysis analyze(
            NormalizedBirthData birthData,
            FourPillars fourPillars,
            ZonedDateTime targetDateTime
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        Objects.requireNonNull(
                targetDateTime,
                "조회 기준 시각은 필수입니다."
        );

        /*
         * 조회 시각의 Zone을
         * 사주 계산 Zone과 맞춘다.
         *
         * 시각 자체는 유지하고
         * 표기 시간대만 동일한 계산 기준으로 맞춘다.
         */
        ZonedDateTime normalizedTargetDateTime =
                targetDateTime.withZoneSameInstant(
                        birthData.zoneId()
                );

        /*
         * 1.
         * 성별 + 년간 음양으로
         * 순행/역행 결정.
         */
        LuckDirection direction =
                luckDirectionCalculator
                        .calculate(
                                birthData,
                                fourPillars.yearPillar()
                        );

        /*
         * 2.
         * 출생시간을 이용한
         * 대운 시작 계산.
         */
        Optional<LuckStart> luckStartOptional =
                luckStartCalculator
                        .calculate(
                                birthData,
                                direction
                        );

        /*
         * 출생시간 미상.
         *
         * 순행/역행은 알 수 있지만
         * 정확한 대운 시작시점은
         * 계산하지 않는다.
         */
        if (
                luckStartOptional.isEmpty()
        ) {

            return new DecadeLuckAnalysis(
                    direction,
                    false,
                    null,
                    List.of(),
                    null,
                    normalizedTargetDateTime,
                    "출생시간을 모르기 때문에 "
                            + "대운 시작 나이와 현재 대운을 "
                            + "정확하게 계산할 수 없습니다.",
                    DecadeLuckRule.RULE_VERSION
            );
        }

        LuckStart luckStart =
                luckStartOptional.get();

        /*
         * 3.
         * 10년 대운 생성.
         */
        List<DecadeLuckPeriod> periods =
                decadeLuckCalculator
                        .calculate(
                                fourPillars.monthPillar(),
                                direction,
                                luckStart
                        );

        /*
         * 4.
         * 조회 시각이 포함되는
         * 현재 대운 탐색.
         */
        DecadeLuckPeriod currentPeriod =
                periods
                        .stream()
                        .filter(
                                period ->
                                        period.contains(
                                                normalizedTargetDateTime
                                        )
                        )
                        .findFirst()
                        .orElse(null);

        return new DecadeLuckAnalysis(
                direction,
                true,
                luckStart,
                periods,
                currentPeriod,
                normalizedTargetDateTime,
                null,
                DecadeLuckRule.RULE_VERSION
        );
    }
}