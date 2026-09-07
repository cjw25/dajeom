package com.dajeom.solar;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 특정 연도의 특정 절기가 시작되는 실제 순간.
 *
 * Instant를 기준값으로 보관한다.
 * 한국 시간은 필요할 때 Asia/Seoul로 변환한다.
 */
public record SolarTermInstant(
        int gregorianYear,
        SolarTerm term,
        Instant instant
) {

    public static final ZoneId KOREA_ZONE =
            ZoneId.of("Asia/Seoul");

    public SolarTermInstant {

        Objects.requireNonNull(
                term,
                "절기가 필요합니다."
        );

        Objects.requireNonNull(
                instant,
                "절입 순간이 필요합니다."
        );
    }

    public ZonedDateTime atZone(
            ZoneId zoneId
    ) {

        Objects.requireNonNull(
                zoneId,
                "시간대가 필요합니다."
        );

        return instant.atZone(
                zoneId
        );
    }

    public ZonedDateTime koreaDateTime() {

        return instant.atZone(
                KOREA_ZONE
        );
    }

    public LocalDateTime koreaLocalDateTime() {

        return koreaDateTime()
                .toLocalDateTime();
    }

    public boolean isBefore(
            Instant target
    ) {

        Objects.requireNonNull(
                target,
                "비교할 시간이 필요합니다."
        );

        return instant.isBefore(
                target
        );
    }

    public boolean isAfter(
            Instant target
    ) {

        Objects.requireNonNull(
                target,
                "비교할 시간이 필요합니다."
        );

        return instant.isAfter(
                target
        );
    }

    public boolean isAtOrBefore(
            Instant target
    ) {

        Objects.requireNonNull(
                target,
                "비교할 시간이 필요합니다."
        );

        return !instant.isAfter(
                target
        );
    }

    public boolean isAtOrAfter(
            Instant target
    ) {

        Objects.requireNonNull(
                target,
                "비교할 시간이 필요합니다."
        );

        return !instant.isBefore(
                target
        );
    }

    @Override
    public String toString() {

        return term.korean()
                + "("
                + term.hanja()
                + ") "
                + koreaLocalDateTime();
    }
}