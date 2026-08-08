package com.dajeom.calculator;

import com.dajeom.model.LuckStart;
import com.dajeom.model.LuckStartAge;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.SolarTermPoint;
import com.dajeom.rule.LuckStartRule;
import com.dajeom.type.LuckDirection;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 대운 시작 나이 및 시점 계산기.
 *
 * 순행:
 * 출생시각 -> 다음 절입
 *
 * 역행:
 * 이전 절입 -> 출생시각
 *
 * 의 시간 간격을 구한 뒤
 * LuckStartRule을 이용해
 * 대운 시작 나이로 환산한다.
 *
 * 출생시간을 모르면
 * 임의 시간을 사용하지 않는다.
 */
@Component
public class LuckStartCalculator {

    private final SolarTermCalculator
            solarTermCalculator;

    public LuckStartCalculator(
            SolarTermCalculator solarTermCalculator
    ) {

        this.solarTermCalculator =
                solarTermCalculator;
    }

    public Optional<LuckStart> calculate(
            NormalizedBirthData birthData,
            LuckDirection direction
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        Objects.requireNonNull(
                direction,
                "대운 방향은 필수입니다."
        );

        /*
         * 출생시간 미상.
         *
         * 정오나 자정 같은 임의 시간을
         * 만들지 않는다.
         */
        if (
                !birthData.hasKnownBirthTime()
        ) {

            return Optional.empty();
        }

        ZonedDateTime birthDateTime =
                birthData
                        .birthDateTime()
                        .orElseThrow()
                        .atZone(
                                birthData.zoneId()
                        );

        SolarTermPoint referenceTerm;

        Duration interval;

        if (
                direction
                        == LuckDirection.FORWARD
        ) {

            /*
             * 순행:
             * 다음 절입까지의 거리.
             */
            referenceTerm =
                    solarTermCalculator
                            .findNextMonthBoundary(
                                    birthDateTime
                            );

            interval =
                    Duration.between(
                            birthDateTime,
                            referenceTerm.dateTime()
                    );

        } else {

            /*
             * 역행:
             * 이전 절입까지의 거리.
             *
             * 정확히 절입 시각과 동일한 출생인 경우
             * 같은 절입을 다시 선택하지 않도록
             * 아주 미세하게 이전 시각을 기준으로 찾는다.
             */
            referenceTerm =
                    solarTermCalculator
                            .findPreviousMonthBoundary(
                                    birthDateTime
                                            .minusNanos(1L)
                            );

            interval =
                    Duration.between(
                            referenceTerm.dateTime(),
                            birthDateTime
                    );
        }

        if (interval.isNegative()) {

            throw new IllegalStateException(
                    "대운 시작 절입 간격 계산에 실패했습니다."
            );
        }

        LuckStartAge startAge =
                LuckStartRule.convert(
                        interval
                );

        ZonedDateTime startDateTime =
                LuckStartRule
                        .calculateStartDateTime(
                                birthDateTime,
                                startAge
                        );

        return Optional.of(
                new LuckStart(
                        direction,
                        referenceTerm,
                        interval.getSeconds(),
                        startAge,
                        startDateTime,
                        LuckStartRule.RULE_VERSION
                )
        );
    }
}