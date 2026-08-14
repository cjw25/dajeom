package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.model.Pillar;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * 사주의 일주(日柱)를 계산한다.
 *
 * 다점 일주 계산 정책
 *
 * 1. 일주의 경계는 현지 민간시 자정(00:00)이다.
 * 2. 23시 자시를 다음 날로 넘기는 야자시 방식은 사용하지 않는다.
 * 3. 진태양시 보정을 적용하지 않는다.
 * 4. 경도 보정을 적용하지 않는다.
 * 5. 사용자가 입력한 출생지역에 따른 시간 이동을 임의로 수행하지 않는다.
 * 6. 음력 입력은 반드시 양력으로 정규화된 날짜를 전달받아 계산한다.
 *
 * 기준 검증값
 *
 * 1970-01-01 = 辛巳日
 * 1993-08-24 = 丁丑日
 *
 * 60갑자 인덱스
 *
 * 0  = 甲子
 * 1  = 乙丑
 * ...
 * 17 = 辛巳
 * ...
 * 59 = 癸亥
 */
@Component
public class DayPillarCalculator {

    /**
     * Unix Epoch 날짜.
     *
     * 1970-01-01의 일진은 辛巳이다.
     */
    private static final LocalDate REFERENCE_DATE =
            LocalDate.of(
                    1970,
                    1,
                    1
            );

    /**
     * 辛巳의 60갑자 인덱스.
     *
     * 辛 = 천간 index 7
     * 巳 = 지지 index 5
     *
     * 60갑자 순서상 辛巳 = index 17
     */
    private static final int REFERENCE_CYCLE_INDEX =
            17;

    /**
     * 60갑자 주기.
     */
    private static final int GANJI_CYCLE_SIZE =
            60;

    /**
     * 1970-01-01의 Julian Day Number.
     *
     * LocalDate의 Epoch Day와 연결해
     * 날짜 계산 검증용으로 사용한다.
     */
    private static final long UNIX_EPOCH_JULIAN_DAY_NUMBER =
            2_440_588L;

    /**
     * 날짜만으로 일주를 계산한다.
     *
     * 일주는 시간과 무관하게
     * 해당 민간 날짜 전체에서 동일하다.
     */
    public Pillar calculate(
            LocalDate birthDate
    ) {
        Objects.requireNonNull(
                birthDate,
                "출생 날짜가 필요합니다."
        );

        int cycleIndex =
                calculateCycleIndex(
                        birthDate
                );

        return GanjiCycleTable.get(
                cycleIndex
        );
    }

    /**
     * 날짜와 시간이 있는 경우.
     *
     * 다점은 자정 기준을 사용하므로
     * LocalDateTime에서 날짜 부분만 사용한다.
     *
     * 예:
     *
     * 1993-08-24 23:59
     * -> 1993-08-24 일주
     *
     * 1993-08-25 00:00
     * -> 1993-08-25 일주
     */
    public Pillar calculate(
            LocalDateTime birthDateTime
    ) {
        Objects.requireNonNull(
                birthDateTime,
                "출생 날짜와 시간이 필요합니다."
        );

        return calculate(
                birthDateTime.toLocalDate()
        );
    }

    /**
     * 다른 PillarCalculator들과 호출 형태를 맞추기 위한 오버로드.
     *
     * 중요:
     *
     * LocalDateTime은 이미 해당 지역의 민간 출생시각으로
     * 정규화되어 들어온다고 본다.
     *
     * 따라서 ZoneId를 이용해 UTC나 다른 시간대로
     * 다시 변환하지 않는다.
     *
     * 이것은 다점의
     * "사용자가 입력한 출생시각을 임의 보정하지 않는다"
     * 정책을 따른다.
     */
    public Pillar calculate(
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

        return calculate(
                birthDateTime.toLocalDate()
        );
    }

    /**
     * 60갑자 cycle index를 계산한다.
     *
     * 기준일에서 며칠이 지났는지를 이용하므로
     * 월 길이, 윤년 등을 별도로 하드코딩하지 않는다.
     *
     * LocalDate가 Gregorian calendar 날짜 차이를
     * 정확하게 처리한다.
     */
    public int calculateCycleIndex(
            LocalDate birthDate
    ) {
        Objects.requireNonNull(
                birthDate,
                "출생 날짜가 필요합니다."
        );

        long referenceJulianDayNumber =
                calculateJulianDayNumber(
                        REFERENCE_DATE
                );

        long targetJulianDayNumber =
                calculateJulianDayNumber(
                        birthDate
                );

        long dayDifference =
                targetJulianDayNumber
                        - referenceJulianDayNumber;

        return Math.floorMod(
                REFERENCE_CYCLE_INDEX
                        + dayDifference,
                GANJI_CYCLE_SIZE
        );
    }

    /**
     * LocalDate를 Julian Day Number(JDN)로 변환한다.
     *
     * Java LocalDate 기준:
     *
     * 1970-01-01 EpochDay = 0
     * 1970-01-01 JDN      = 2440588
     *
     * 따라서:
     *
     * JDN = EpochDay + 2440588
     *
     * 일주 계산에서는 시간 소수값이 필요하지 않기 때문에
     * Julian Date(double)가 아니라
     * 정수 Julian Day Number를 사용한다.
     *
     * 이렇게 하면 부동소수점 경계 오차를 피할 수 있다.
     */
    public long calculateJulianDayNumber(
            LocalDate date
    ) {
        Objects.requireNonNull(
                date,
                "날짜가 필요합니다."
        );

        return date.toEpochDay()
                + UNIX_EPOCH_JULIAN_DAY_NUMBER;
    }

    /**
     * 특정 날짜와 기준일 사이의 날짜 차이를 반환한다.
     *
     * 엔진 검증이나 진단에서 사용할 수 있다.
     */
    public long calculateDaysFromReference(
            LocalDate date
    ) {
        Objects.requireNonNull(
                date,
                "날짜가 필요합니다."
        );

        return calculateJulianDayNumber(
                date
        )
                - calculateJulianDayNumber(
                REFERENCE_DATE
        );
    }
}