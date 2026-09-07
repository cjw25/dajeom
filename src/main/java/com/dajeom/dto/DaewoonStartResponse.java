package com.dajeom.dto;

import com.dajeom.domain.DaewoonStart;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public record DaewoonStartResponse(
        String direction,
        String directionKorean,

        String referenceTerm,
        String referenceTermHanja,

        Instant referenceInstant,
        LocalDateTime referenceDateTime,

        Instant birthInstant,
        LocalDateTime birthDateTime,

        long distanceTotalMillis,
        long distanceTotalSeconds,

        long distanceDays,
        int distanceHours,
        int distanceMinutes,
        int distanceSeconds,

        BigDecimal preciseStartAgeYears,

        int daewoonNumber,

        int displayStartYears,
        int displayStartMonths,
        int displayStartDays,

        String displayStartAge,

        Instant firstDaewoonInstant,
        LocalDateTime firstDaewoonDateTime,
        LocalDate firstDaewoonApproxDate,

        String rule
) {

    public static DaewoonStartResponse from(
            DaewoonStart value
    ) {

        Objects.requireNonNull(
                value,
                "대운 시작 결과가 필요합니다."
        );

        Duration duration =
                Duration.ofMillis(
                        value.distanceMillis()
                );

        String displayStartAge =
                value.displayStartYears()
                        + "년"
                        + value.displayStartMonths()
                        + "개월"
                        + value.displayStartDays()
                        + "일";

        return new DaewoonStartResponse(
                value.direction().name(),
                value.direction().korean(),

                value.referenceTerm().korean(),
                value.referenceTerm().hanja(),

                value.referenceInstant(),
                value.referenceKoreaDateTime(),

                value.birthInstant(),
                value.birthKoreaDateTime(),

                value.distanceMillis(),
                duration.getSeconds(),

                duration.toDaysPart(),
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart(),

                value.preciseStartAgeYears(),

                value.daewoonNumber(),

                value.displayStartYears(),
                value.displayStartMonths(),
                value.displayStartDays(),

                displayStartAge,

                value.firstDaewoonInstant(),
                value.firstDaewoonKoreaDateTime(),
                value.firstDaewoonApproxDate(),

                "순행=출생→다음 절입 / 역행=이전 절입→출생 / "
                        + "3일=1년, 6시간=1개월, 12분=1일 / "
                        + "첫대운 정밀시점=대운수×365일 / "
                        + "화면 날짜=최근접 날짜"
        );
    }
}