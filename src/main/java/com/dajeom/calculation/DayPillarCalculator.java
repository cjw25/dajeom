package com.dajeom.calculation;

import com.dajeom.domain.BirthDateTime;
import com.dajeom.domain.Ganji;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.SexagenaryCycle;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Component
public class DayPillarCalculator {

    /**
     * 검증 기준일.
     *
     * 1993-08-24 = 丁丑
     */
    private static final LocalDate ANCHOR_DATE =
            LocalDate.of(
                    1993,
                    8,
                    24
            );

    /**
     * 丁丑 = 60갑자 index 13
     */
    private static final int ANCHOR_CYCLE_INDEX =
            13;

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

        /*
         * 일주 날짜 경계 규칙은 아직
         * 천을귀인 실제 결과로 확정하지 않았다.
         *
         * 따라서 지금 단계에서는
         * 양력으로 정규화된 출생 날짜 자체만 사용한다.
         *
         * -30분 경도보정이나 DST 때문에
         * 일주 날짜를 임의로 변경하지 않는다.
         */
        LocalDate targetDate =
                birth.birthDate();

        long days =
                ChronoUnit.DAYS.between(
                        ANCHOR_DATE,
                        targetDate
                );

        long targetIndex =
                ANCHOR_CYCLE_INDEX + days;

        Ganji ganji =
                SexagenaryCycle.ofIndex(
                        Math.toIntExact(
                                targetIndex
                        )
                );

        return new Pillar(
                ganji
        );
    }

    private void requireSolarCalendar(
            BirthDateTime birth
    ) {

        if (!birth.isSolar()) {

            throw new IllegalArgumentException(
                    "DayPillarCalculator에는 양력으로 정규화된 출생정보만 입력할 수 있습니다."
            );
        }
    }
}