package com.dajeom.model;

import com.dajeom.type.CalendarType;
import com.dajeom.entity.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

/**
 * 사주 계산에 사용되는
 * 정규화된 출생정보.
 *
 * birthDate:
 * 사용자가 입력한 원래 날짜.
 *
 * solarBirthDate:
 * 실제 사주 계산에 사용할 양력 날짜.
 *
 * 양력 입력:
 *
 * birthDate
 * =
 * solarBirthDate
 *
 * 음력 입력:
 *
 * birthDate
 * -> 한국 음양력 변환
 * -> solarBirthDate
 */
public record NormalizedBirthData(

        CalendarType calendarType,

        BirthDateValue birthDate,

        LocalDate solarBirthDate,

        LocalTime birthTime,

        boolean birthTimeUnknown,

        Gender gender,

        boolean leapMonth,

        ZoneId zoneId

) {

    public NormalizedBirthData {

        Objects.requireNonNull(
                calendarType,
                "양력/음력 구분은 필수입니다."
        );

        Objects.requireNonNull(
                birthDate,
                "원본 생년월일은 필수입니다."
        );

        Objects.requireNonNull(
                solarBirthDate,
                "변환된 양력 생년월일은 필수입니다."
        );

        Objects.requireNonNull(
                gender,
                "성별은 필수입니다."
        );

        Objects.requireNonNull(
                zoneId,
                "시간대는 필수입니다."
        );

        if (
                calendarType
                        == CalendarType.SOLAR
                        && leapMonth
        ) {

            throw new IllegalArgumentException(
                    "양력 입력에는 윤달을 사용할 수 없습니다."
            );
        }

        if (
                birthTimeUnknown
                        && birthTime != null
        ) {

            throw new IllegalArgumentException(
                    "출생시간 미상인 경우 birthTime은 없어야 합니다."
            );
        }

        if (
                !birthTimeUnknown
                        && birthTime == null
        ) {

            throw new IllegalArgumentException(
                    "출생시간을 알고 있는 경우 birthTime은 필수입니다."
            );
        }

        /*
         * 양력 입력이라면 원본 날짜와
         * 계산용 양력 날짜가 반드시 같아야 한다.
         */
        if (
                calendarType
                        == CalendarType.SOLAR
        ) {

            LocalDate originalSolarDate =
                    birthDate.toGregorianDate();

            if (
                    !originalSolarDate.equals(
                            solarBirthDate
                    )
            ) {

                throw new IllegalArgumentException(
                        "양력 원본 날짜와 계산용 양력 날짜가 일치하지 않습니다."
                );
            }
        }
    }

    public boolean hasKnownBirthTime() {

        return !birthTimeUnknown
                && birthTime != null;
    }

    public Optional<LocalTime>
    optionalBirthTime() {

        return Optional.ofNullable(
                birthTime
        );
    }

    /**
     * 실제 사주 계산 시각.
     *
     * 음력 입력이라도 반드시
     * 변환된 solarBirthDate를 사용한다.
     */
    public Optional<LocalDateTime>
    birthDateTime() {

        if (!hasKnownBirthTime()) {

            return Optional.empty();
        }

        return Optional.of(
                LocalDateTime.of(
                        solarBirthDate,
                        birthTime
                )
        );
    }

    public String originalBirthDateText() {

        return birthDate.isoFormat();
    }
}