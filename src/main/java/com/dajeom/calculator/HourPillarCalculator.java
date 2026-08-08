package com.dajeom.calculator;

import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 시주 계산기.
 *
 * 다점 정책:
 *
 * - 입력된 현지 출생시간을 그대로 사용
 * - 진태양시 보정 없음
 * - 경도 보정 없음
 * - 임의 분 단위 차감 없음
 *
 * 출생시간을 모르는 경우
 * 시주를 임의 생성하지 않는다.
 */
@Component
public class HourPillarCalculator {

    public Optional<Pillar> calculate(
            NormalizedBirthData birthData,
            Pillar dayPillar
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        Objects.requireNonNull(
                dayPillar,
                "일주는 필수입니다."
        );

        if (
                !birthData.hasKnownBirthTime()
        ) {

            return Optional.empty();
        }

        LocalTime birthTime =
                birthData
                        .optionalBirthTime()
                        .orElseThrow();

        int hour =
                birthTime.getHour();

        /*
         * 시지 계산.
         *
         * 자시 = 23:00 ~ 00:59
         * 축시 = 01:00 ~ 02:59
         * 인시 = 03:00 ~ 04:59
         * ...
         *
         * 23시인 경우:
         * (23 + 1) / 2 = 12
         * 12 mod 12 = 0
         * → 子
         */
        int branchIndex =
                Math.floorMod(
                        (
                                hour + 1
                        ) / 2,
                        12
                );

        EarthlyBranch hourBranch =
                EarthlyBranch.fromIndex(
                        branchIndex
                );

        /*
         * 일간별 자시 천간.
         *
         * 갑/기일 → 갑자
         * 을/경일 → 병자
         * 병/신일 → 무자
         * 정/임일 → 경자
         * 무/계일 → 임자
         */
        int ziHourStemIndex =
                Math.floorMod(
                        (
                                dayPillar
                                        .stem()
                                        .getIndex()
                                        % 5
                        )
                                * 2,
                        10
                );

        HeavenlyStem hourStem =
                HeavenlyStem.fromIndex(
                        ziHourStemIndex
                                + branchIndex
                );

        return Optional.of(
                new Pillar(
                        hourStem,
                        hourBranch
                )
        );
    }
}