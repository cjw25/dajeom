package com.dajeom.calculator;

import com.dajeom.constant.GanjiCycleTable;
import com.dajeom.constant.HiddenStemTable;
import com.dajeom.model.DecadeLuck;
import com.dajeom.model.FourPillars;
import com.dajeom.model.Pillar;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.LuckDirection;
import com.dajeom.type.TenGod;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 월주를 기준으로 10년 단위 대운을 생성한다.
 */
@Component
public class DecadeLuckCalculator {

    private static final int DEFAULT_DECADE_COUNT =
            10;

    private final TenGodCalculator
            tenGodCalculator;

    public DecadeLuckCalculator(
            TenGodCalculator tenGodCalculator
    ) {
        this.tenGodCalculator =
                tenGodCalculator;
    }

    public List<DecadeLuck> calculate(
            FourPillars fourPillars,
            LuckDirection direction,
            LocalDate luckStartDate,
            LocalDate targetDate
    ) {
        int monthPillarIndex =
                GanjiCycleTable.findIndex(
                        fourPillars
                                .monthPillar()
                                .stem(),

                        fourPillars
                                .monthPillar()
                                .branch()
                );

        if (monthPillarIndex < 0) {
            throw new IllegalStateException(
                    "월주의 육십갑자 인덱스를 찾을 수 없습니다."
            );
        }

        HeavenlyStem dayMaster =
                fourPillars
                        .dayPillar()
                        .stem();

        List<DecadeLuck> result =
                new ArrayList<>();

        for (int sequence = 1;
             sequence <= DEFAULT_DECADE_COUNT;
             sequence++) {

            int cycleIndex =
                    monthPillarIndex
                            + direction.getStep()
                            * sequence;

            Pillar luckPillar =
                    GanjiCycleTable.get(
                            cycleIndex
                    );

            LocalDate startDate =
                    luckStartDate.plusYears(
                            (long) (sequence - 1) * 10
                    );

            LocalDate endDate =
                    startDate
                            .plusYears(10)
                            .minusDays(1);

            int startAge =
                    Math.max(
                            0,
                            startDate.getYear()
                                    - luckStartDate.getYear()
                    );

            int endAge =
                    startAge + 9;

            TenGod stemTenGod =
                    tenGodCalculator.calculateTenGod(
                            dayMaster,
                            luckPillar.stem()
                    );

            HeavenlyStem branchMainStem =
                    HiddenStemTable
                            .get(luckPillar.branch())
                            .stream()
                            .filter(definition ->
                                    definition.role()
                                            == com.dajeom.type
                                            .HiddenStemRole.MAIN
                            )
                            .findFirst()
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "대운 지지 본기를 찾을 수 없습니다."
                                    )
                            )
                            .stem();

            TenGod branchMainTenGod =
                    tenGodCalculator.calculateTenGod(
                            dayMaster,
                            branchMainStem
                    );

            boolean current =
                    !targetDate.isBefore(startDate)
                            && !targetDate.isAfter(endDate);

            result.add(
                    new DecadeLuck(
                            sequence,
                            luckPillar,
                            startDate,
                            endDate,
                            startAge,
                            endAge,
                            stemTenGod,
                            branchMainTenGod,
                            current
                    )
            );
        }

        return List.copyOf(result);
    }
}