package com.dajeom.calculation;

import com.dajeom.domain.BirthDateTime;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.Ganji;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.Pillar;
import com.dajeom.rule.SajuRuleBook;
import com.dajeom.time.BirthTimeCorrection;
import com.dajeom.time.BirthTimeCorrectionCalculator;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Component
public class HourPillarCalculator {

    private final BirthTimeCorrectionCalculator
            birthTimeCorrectionCalculator;

    public HourPillarCalculator(
            BirthTimeCorrectionCalculator birthTimeCorrectionCalculator
    ) {

        this.birthTimeCorrectionCalculator =
                Objects.requireNonNull(
                        birthTimeCorrectionCalculator,
                        "BirthTimeCorrectionCalculator가 필요합니다."
                );
    }

    public Pillar calculate(
            BirthDateTime birth,
            HeavenlyStem dayStem
    ) {

        Objects.requireNonNull(
                birth,
                "출생정보가 필요합니다."
        );

        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        requireSolarCalendar(
                birth
        );

        if (birth.hasUnknownTime()) {

            throw new IllegalStateException(
                    "출생시간을 알 수 없어 시주를 계산할 수 없습니다."
            );
        }

        BirthTimeCorrection correction =
                birthTimeCorrectionCalculator.calculate(
                        birth
                );

        LocalDateTime correctedDateTime =
                correction.correctedDateTime();

        EarthlyBranch hourBranch =
                determineHourBranch(
                        correctedDateTime.toLocalTime()
                );

        Ganji ganji =
                SajuRuleBook.hourGanji(
                        dayStem,
                        hourBranch
                );

        return new Pillar(
                ganji
        );
    }

    /**
     * -30분 보정이 끝난 실제 계산용 시간을
     * 12개 시지로 변환한다.
     *
     * 子 : 23:00 ~ 00:59
     * 丑 : 01:00 ~ 02:59
     * 寅 : 03:00 ~ 04:59
     * 卯 : 05:00 ~ 06:59
     * 辰 : 07:00 ~ 08:59
     * 巳 : 09:00 ~ 10:59
     * 午 : 11:00 ~ 12:59
     * 未 : 13:00 ~ 14:59
     * 申 : 15:00 ~ 16:59
     * 酉 : 17:00 ~ 18:59
     * 戌 : 19:00 ~ 20:59
     * 亥 : 21:00 ~ 22:59
     */
    public EarthlyBranch determineHourBranch(
            LocalTime correctedTime
    ) {

        Objects.requireNonNull(
                correctedTime,
                "보정된 출생시간이 필요합니다."
        );

        int hour =
                correctedTime.getHour();

        if (hour == 23 || hour == 0) {
            return EarthlyBranch.ZI;
        }

        int branchIndex =
                ((hour + 1) / 2);

        return EarthlyBranch.ofIndex(
                branchIndex
        );
    }

    private void requireSolarCalendar(
            BirthDateTime birth
    ) {

        if (!birth.isSolar()) {

            throw new IllegalArgumentException(
                    "HourPillarCalculator에는 양력으로 정규화된 출생정보만 입력할 수 있습니다."
            );
        }
    }
}