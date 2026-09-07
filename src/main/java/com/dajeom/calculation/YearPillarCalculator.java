package com.dajeom.calculation;

import com.dajeom.domain.BirthDateTime;
import com.dajeom.domain.Ganji;
import com.dajeom.domain.Pillar;
import com.dajeom.rule.SajuRuleBook;
import com.dajeom.solar.SolarTermInstant;
import com.dajeom.solar.SolarTermRepository;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 년주 계산기.
 *
 * 핵심 규칙:
 *
 * 사주 년주는 1월 1일이 아니라
 * 해당 연도의 실제 입춘 순간에 변경된다.
 *
 * 예:
 *
 * 2002-02-02
 * → 2002년 입춘 이전
 * → 사주 유효 연도 2001
 * → 辛巳
 *
 * 입춘 이후:
 * → 2002
 * → 壬午
 */
@Component
public class YearPillarCalculator {

    private final SolarTermRepository solarTermRepository;

    public YearPillarCalculator(
            SolarTermRepository solarTermRepository
    ) {

        this.solarTermRepository =
                Objects.requireNonNull(
                        solarTermRepository,
                        "SolarTermRepository가 필요합니다."
                );
    }

    public Pillar calculate(
            BirthDateTime birth
    ) {

        Objects.requireNonNull(
                birth,
                "출생정보가 필요합니다."
        );

        requireSolarCalendar(
                birth
        );

        int effectiveYear =
                determineEffectiveSajuYear(
                        birth
                );

        Ganji ganji =
                SajuRuleBook.yearGanji(
                        effectiveYear
                );

        return new Pillar(
                ganji
        );
    }

    /**
     * 입춘을 기준으로 실제 사주 연도를 결정한다.
     */
    public int determineEffectiveSajuYear(
            BirthDateTime birth
    ) {

        Objects.requireNonNull(
                birth,
                "출생정보가 필요합니다."
        );

        requireSolarCalendar(
                birth
        );

        LocalDate birthDate =
                birth.birthDate();

        int gregorianYear =
                birthDate.getYear();

        SolarTermInstant lichun =
                solarTermRepository.getLichun(
                        gregorianYear
                );

        LocalDate lichunDate =
                lichun
                        .koreaLocalDateTime()
                        .toLocalDate();

        /*
         * 입춘 날짜보다 확실하게 이전이면
         * 전년도 사주년.
         */
        if (birthDate.isBefore(
                lichunDate
        )) {

            return gregorianYear - 1;
        }

        /*
         * 입춘 날짜보다 확실하게 이후면
         * 해당 연도 사주년.
         */
        if (birthDate.isAfter(
                lichunDate
        )) {

            return gregorianYear;
        }

        /*
         * 여기까지 왔다는 것은
         *
         * 출생일 == 입춘일
         *
         * 이다.
         *
         * 출생시간을 모르면 정확히
         * 입춘 이전인지 이후인지 판정할 수 없다.
         *
         * 절대 임의의 12:00 등을 넣지 않는다.
         */
        if (birth.hasUnknownTime()) {

            throw new IllegalStateException(
                    "출생일이 입춘 당일이지만 출생시간을 알 수 없어 "
                            + "년주를 확정할 수 없습니다. birthDate="
                            + birthDate
                            + ", lichun="
                            + lichun.koreaLocalDateTime()
            );
        }

        LocalDateTime birthDateTime =
                birth.requireDateTime();

        Instant birthInstant =
                birthDateTime
                        .atZone(
                                SolarTermInstant.KOREA_ZONE
                        )
                        .toInstant();

        /*
         * 정확히 입춘 순간이거나 이후면
         * 새로운 년주를 사용한다.
         */
        if (!birthInstant.isBefore(
                lichun.instant()
        )) {

            return gregorianYear;
        }

        return gregorianYear - 1;
    }

    private void requireSolarCalendar(
            BirthDateTime birth
    ) {

        if (!birth.isSolar()) {

            throw new IllegalArgumentException(
                    "YearPillarCalculator에는 양력으로 정규화된 출생정보만 입력할 수 있습니다."
            );
        }
    }
}