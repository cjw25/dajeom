package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.LuckStart;
import com.dajeom.model.LuckStartAge;
import com.dajeom.model.Pillar;
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
 * 기준:
 *
 * 순행:
 * 월주 다음 간지부터 진행
 *
 * 역행:
 * 월주 이전 간지부터 진행
 *
 * 각 대운은 10년 단위로 구성한다.
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

        int monthCycleIndex =
                GanjiCycleTable.findIndex(
                        monthPillar.stem(),
                        monthPillar.branch()
                );

        List<DecadeLuckPeriod> periods =
                new ArrayList<>();

        for (
                int order = 1;
                order <= DecadeLuckRule.PERIOD_COUNT;
                order++
        ) {

            /*
             * 첫 대운은 월주 자체가 아니라
             * 순행이면 +1,
             * 역행이면 -1부터 시작한다.
             */
            int cycleOffset =
                    direction.getStep()
                            * order;

            Pillar luckPillar =
                    GanjiCycleTable.get(
                            monthCycleIndex
                                    + cycleOffset
                    );

            int yearOffset =
                    (order - 1)
                            * DecadeLuckRule.PERIOD_YEARS;

            ZonedDateTime periodStart =
                    luckStart
                            .startDateTime()
                            .plusYears(
                                    yearOffset
                            );

            ZonedDateTime periodEnd =
                    periodStart
                            .plusYears(
                                    DecadeLuckRule
                                            .PERIOD_YEARS
                            );

            LuckStartAge startAge =
                    addYears(
                            luckStart.startAge(),
                            yearOffset
                    );

            LuckStartAge endAge =
                    addYears(
                            luckStart.startAge(),
                            yearOffset
                                    + DecadeLuckRule
                                    .PERIOD_YEARS
                    );

            periods.add(
                    new DecadeLuckPeriod(
                            order,
                            luckPillar,
                            startAge,
                            endAge,
                            periodStart,
                            periodEnd
                    )
            );
        }

        return List.copyOf(
                periods
        );
    }

    private LuckStartAge addYears(
            LuckStartAge baseAge,
            int years
    ) {

        return new LuckStartAge(
                baseAge.years()
                        + years,
                baseAge.months(),
                baseAge.days(),
                baseAge.decimalYears()
                        + years
        );
    }
}