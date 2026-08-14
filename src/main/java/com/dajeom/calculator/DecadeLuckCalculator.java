package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.LuckStart;
import com.dajeom.model.LuckStartAge;
import com.dajeom.model.Pillar;
import com.dajeom.model.SexagenaryPillar;
import com.dajeom.rule.DecadeLuckRule;
import com.dajeom.type.LuckDirection;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 10년 단위 대운 계산기.
 *
 * =================================================
 *
 * 기준:
 *
 * 순행:
 * 월주 다음 간지부터 진행.
 *
 * 역행:
 * 월주 이전 간지부터 진행.
 *
 * =================================================
 *
 * 첫 대운:
 *
 * 순행이면 월주 +1
 * 역행이면 월주 -1
 *
 * 부터 시작한다.
 *
 * =================================================
 *
 * 각 대운은 10년 단위다.
 *
 * =================================================
 *
 * 현재 정밀화 13차에서는:
 *
 * - SexagenaryPillar
 * - 최소 시작 나이
 * - 최대 시작 나이
 * - earliest 시작시점
 * - latest 시작시점
 * - earliest 종료시점
 * - latest 종료시점
 * - exactTiming
 *
 * 을 보존한다.
 *
 * 현재 이 Calculator가 받는 LuckStart는
 * 하나의 확정 시작 나이 / 시작시점을 가지므로
 *
 * minimum == maximum
 * earliest == latest
 *
 * 로 생성한다.
 */
@Component
public class DecadeLuckCalculator {

    public List<DecadeLuckPeriod> calculate(
            Pillar monthPillar,
            LuckDirection direction,
            LuckStart luckStart
    ) {

        Objects.requireNonNull(
                monthPillar,
                "월주는 필수입니다."
        );

        Objects.requireNonNull(
                direction,
                "대운 방향은 필수입니다."
        );

        Objects.requireNonNull(
                luckStart,
                "대운 시작정보는 필수입니다."
        );

        /*
         * =================================================
         * 월주의 육십갑자 위치
         * =================================================
         */
        int monthCycleIndex =
                GanjiCycleTable
                        .findIndex(
                                monthPillar.stem(),
                                monthPillar.branch()
                        );

        List<DecadeLuckPeriod> periods =
                new ArrayList<>();

        for (
                int sequenceNumber = 1;
                sequenceNumber <= DecadeLuckRule.PERIOD_COUNT;
                sequenceNumber++
        ) {

            /*
             * =================================================
             * 대운 간지 이동
             * =================================================
             *
             * 첫 대운은 월주 자체가 아니다.
             *
             * 순행:
             *
             * +1, +2, +3 ...
             *
             * 역행:
             *
             * -1, -2, -3 ...
             */
            int cycleOffset =
                    direction.getStep()
                            * sequenceNumber;

            /*
             * SexagenaryPillar cycleIndex는
             * 반드시 0 ~ 59 범위여야 하므로
             * 명시적으로 정규화한다.
             */
            int luckCycleIndex =
                    Math.floorMod(
                            monthCycleIndex
                                    + cycleOffset,
                            60
                    );

            Pillar calculatedLuckPillar =
                    GanjiCycleTable.get(
                            luckCycleIndex
                    );

            SexagenaryPillar luckPillar =
                    new SexagenaryPillar(
                            luckCycleIndex,
                            calculatedLuckPillar.stem(),
                            calculatedLuckPillar.branch()
                    );

            /*
             * =================================================
             * 현재 대운이 첫 대운으로부터
             * 몇 년 뒤 시작하는지
             * =================================================
             */
            int yearOffset =
                    (sequenceNumber - 1)
                            * DecadeLuckRule.PERIOD_YEARS;

            /*
             * =================================================
             * 대운 시작시점
             * =================================================
             *
             * 현재 LuckStart는 하나의 확정값을 제공한다.
             */
            ZonedDateTime periodStart =
                    luckStart
                            .startDateTime()
                            .plusYears(
                                    yearOffset
                            );

            /*
             * 종료시점은 exclusive.
             *
             * [start, start + 10년)
             */
            ZonedDateTime periodEndExclusive =
                    periodStart
                            .plusYears(
                                    DecadeLuckRule
                                            .PERIOD_YEARS
                            );

            /*
             * =================================================
             * 해당 대운의 시작 나이
             * =================================================
             *
             * 중요:
             *
             * DecadeLuckPeriod의:
             *
             * minimumStartAge
             * maximumStartAge
             *
             * 는
             *
             * "시작 나이 / 종료 나이"
             *
             * 가 아니다.
             *
             * 시간 미상 등에 따른
             * 가능한 시작 나이 범위다.
             *
             * 현재 LuckStart는 정확한 한 값을 가지므로
             * minimum과 maximum에 동일한 값을 넣는다.
             */
            LuckStartAge periodStartAge =
                    addYears(
                            luckStart.startAge(),
                            yearOffset
                    );

            periods.add(
                    new DecadeLuckPeriod(
                            sequenceNumber,

                            luckPillar,

                            /*
                             * minimumStartAge
                             */
                            periodStartAge,

                            /*
                             * maximumStartAge
                             */
                            periodStartAge,

                            /*
                             * earliestStartDateTime
                             */
                            periodStart,

                            /*
                             * latestStartDateTime
                             */
                            periodStart,

                            /*
                             * earliestEndDateTimeExclusive
                             */
                            periodEndExclusive,

                            /*
                             * latestEndDateTimeExclusive
                             */
                            periodEndExclusive,

                            /*
                             * 시작시점 범위가 모두 동일하므로
                             * exactTiming = true
                             */
                            true
                    )
            );
        }

        return List.copyOf(
                periods
        );
    }

    /**
     * 명리 symbolic age에
     * 정수 년수를 추가한다.
     *
     * =================================================
     *
     * 현재 LuckStartAge는:
     *
     * years
     * months
     * days
     *
     * 만 보존한다.
     *
     * 삭제된 decimalYears 값을
     * 다시 만들지 않는다.
     */
    private LuckStartAge addYears(
            LuckStartAge baseAge,
            int years
    ) {

        Objects.requireNonNull(
                baseAge,
                "기준 대운 시작 나이는 필수입니다."
        );

        if (
                years < 0
        ) {

            throw new IllegalArgumentException(
                    "추가할 대운 연수는 음수일 수 없습니다."
            );
        }

        return new LuckStartAge(
                baseAge.years()
                        + years,
                baseAge.months(),
                baseAge.days()
        );
    }
}