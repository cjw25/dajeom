package com.dajeom.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

/**
 * 출생시간의 존재 여부와 계산용 날짜를 함께 관리한다.
 *
 * 다점 정책
 *
 * 1. 출생시간을 아는 경우
 *    - birthTime 존재
 *    - birthTimeUnknown = false
 *
 * 2. 출생시간을 모르는 경우
 *    - birthTime = null
 *    - birthTimeUnknown = true
 *
 * 3. 시간 미상이라고 해서
 *    00:00, 12:00 등의 임의 시간을 생성하지 않는다.
 *
 * 4. 출생시간 미상이어도
 *    일주는 날짜만으로 계산할 수 있다.
 *
 * 5. 출생시간 미상 + 절입 당일이라면
 *    년주 또는 월주가 확정되지 않을 수 있다.
 *
 * 6. 시주는 출생시간이 있을 때만 계산한다.
 */
public record BirthTimeContext(

        LocalDate solarBirthDate,

        LocalTime birthTime,

        boolean birthTimeUnknown,

        ZoneId zoneId

) {

    public BirthTimeContext {

        Objects.requireNonNull(
                solarBirthDate,
                "계산용 양력 생년월일이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "시간대가 필요합니다."
        );

        /*
         * 시간 미상인데 시간이 같이 들어오는
         * 모순된 상태를 허용하지 않는다.
         */
        if (
                birthTimeUnknown
                        && birthTime != null
        ) {
            throw new IllegalArgumentException(
                    "출생시간 미상 상태에서는 "
                            + "출생시간을 함께 지정할 수 없습니다."
            );
        }

        /*
         * 시간을 안다고 했는데 실제 시간이 없는 것도
         * 허용하지 않는다.
         */
        if (
                !birthTimeUnknown
                        && birthTime == null
        ) {
            throw new IllegalArgumentException(
                    "출생시간 미상이 아니라면 "
                            + "출생시간이 반드시 필요합니다."
            );
        }
    }

    /**
     * 출생시간을 알고 있는 Context 생성.
     */
    public static BirthTimeContext known(
            LocalDate solarBirthDate,
            LocalTime birthTime,
            ZoneId zoneId
    ) {
        Objects.requireNonNull(
                birthTime,
                "출생시간이 필요합니다."
        );

        return new BirthTimeContext(
                solarBirthDate,
                birthTime,
                false,
                zoneId
        );
    }

    /**
     * 출생시간 미상 Context 생성.
     *
     * 임의 시간을 절대 넣지 않는다.
     */
    public static BirthTimeContext unknown(
            LocalDate solarBirthDate,
            ZoneId zoneId
    ) {
        return new BirthTimeContext(
                solarBirthDate,
                null,
                true,
                zoneId
        );
    }

    /**
     * 출생시간을 알고 있는지 여부.
     */
    public boolean hasKnownBirthTime() {

        return !birthTimeUnknown
                && birthTime != null;
    }

    /**
     * 시주 계산 가능 여부.
     */
    public boolean canCalculateHourPillar() {

        return hasKnownBirthTime();
    }

    /**
     * 일주 계산 가능 여부.
     *
     * 날짜가 존재하므로 항상 가능하다.
     */
    public boolean canCalculateDayPillar() {

        return true;
    }

    /**
     * 출생시간을 Optional로 반환한다.
     *
     * 시간 미상일 때 null 직접 접근을 줄이기 위해 사용한다.
     */
    public Optional<LocalTime> birthTimeOptional() {

        return Optional.ofNullable(
                birthTime
        );
    }

    /**
     * 날짜 + 시간이 모두 있는 경우에만
     * LocalDateTime을 생성한다.
     */
    public Optional<LocalDateTime> birthDateTimeOptional() {

        if (
                !hasKnownBirthTime()
        ) {
            return Optional.empty();
        }

        return Optional.of(
                LocalDateTime.of(
                        solarBirthDate,
                        birthTime
                )
        );
    }

    /**
     * 반드시 시간이 필요한 계산에서 사용한다.
     *
     * 출생시간 미상인데 이 메서드를 호출하면
     * 잘못된 계산을 계속 진행하지 않고 즉시 중단한다.
     */
    public LocalDateTime requireBirthDateTime() {

        return birthDateTimeOptional()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "출생시간이 없어 "
                                        + "날짜+시간 계산을 수행할 수 없습니다."
                        )
                );
    }

    /**
     * 시간 미상 여부를 소비자에게 표시할 때 사용할 수 있는 설명.
     */
    public String calculationDescription() {

        if (
                birthTimeUnknown
        ) {
            return "출생시간 미상 · 임의 시간 보정 없음";
        }

        return "사용자 입력 출생시간 사용 · 시간 보정 없음";
    }
}