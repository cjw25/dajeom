package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.constant.SajuConstants;
import com.dajeom.model.Pillar;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * 사주의 년주를 계산한다.
 *
 * 핵심 원칙
 *
 * 1. 양력 1월 1일을 년주 경계로 사용하지 않는다.
 * 2. 실제 입춘 시각을 년주 변경 기준으로 사용한다.
 * 3. 입춘 이전은 전년도 간지를 사용한다.
 * 4. 입춘 시각부터 새해 간지를 사용한다.
 * 5. 출생시간 미상 + 입춘 당일이면 임의로 년주를 확정하지 않는다.
 *
 * 기준:
 * 1984년 입춘 이후 = 甲子년
 */
@Component
public class YearPillarCalculator {

    private static final int GANJI_CYCLE_SIZE =
            60;

    private final SolarTermCalculator
            solarTermCalculator;

    public YearPillarCalculator(
            SolarTermCalculator solarTermCalculator
    ) {
        this.solarTermCalculator =
                Objects.requireNonNull(
                        solarTermCalculator,
                        "SolarTermCalculator가 필요합니다."
                );
    }

    /**
     * 출생 날짜와 시간이 모두 있는 경우 년주 계산.
     */
    public Pillar calculate(
            LocalDateTime birthDateTime,
            ZoneId zoneId
    ) {
        int sajuYear =
                determineSajuYear(
                        birthDateTime,
                        zoneId
                );

        return calculateFromSajuYear(
                sajuYear
        );
    }

    /**
     * 출생시간이 없는 경우 년주 계산.
     *
     * 입춘 당일이면 출생 시각에 따라
     * 이전 년주 / 새로운 년주가 갈릴 수 있으므로
     * Optional.empty()를 반환한다.
     */
    public Optional<Pillar> calculate(
            LocalDate birthDate,
            ZoneId zoneId
    ) {
        OptionalInt sajuYear =
                determineSajuYear(
                        birthDate,
                        zoneId
                );

        if (
                sajuYear.isEmpty()
        ) {
            return Optional.empty();
        }

        return Optional.of(
                calculateFromSajuYear(
                        sajuYear.getAsInt()
                )
        );
    }

    /**
     * 실제 입춘 시각을 기준으로
     * 명리상의 연도를 계산한다.
     */
    public int determineSajuYear(
            LocalDateTime birthDateTime,
            ZoneId zoneId
    ) {
        Objects.requireNonNull(
                birthDateTime,
                "출생 날짜와 시간이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "시간대가 필요합니다."
        );

        return solarTermCalculator
                .determineSajuYear(
                        birthDateTime,
                        zoneId
                );
    }

    /**
     * 출생시간 미상인 경우 명리상의 연도를 계산한다.
     *
     * 입춘 날짜와 정확히 같은 날짜라면
     * OptionalInt.empty()가 반환된다.
     */
    public OptionalInt determineSajuYear(
            LocalDate birthDate,
            ZoneId zoneId
    ) {
        Objects.requireNonNull(
                birthDate,
                "출생 날짜가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "시간대가 필요합니다."
        );

        return solarTermCalculator
                .determineSajuYear(
                        birthDate,
                        zoneId
                );
    }

    /**
     * 이미 명리 연도가 결정된 경우
     * 해당 연도의 간지를 계산한다.
     *
     * 1984 = 甲子를 기준으로
     * 60갑자 순환을 계산한다.
     */
    public Pillar calculateFromSajuYear(
            int sajuYear
    ) {
        int cycleIndex =
                Math.floorMod(
                        sajuYear
                                - SajuConstants.GAPJA_BASE_YEAR,
                        GANJI_CYCLE_SIZE
                );

        return GanjiCycleTable.get(
                cycleIndex
        );
    }
}