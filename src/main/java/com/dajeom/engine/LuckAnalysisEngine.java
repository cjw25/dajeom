package com.dajeom.engine;

import com.dajeom.calculator.AnnualLuckCalculator;
import com.dajeom.calculator.DecadeLuckCalculator;
import com.dajeom.calculator.LuckDirectionCalculator;
import com.dajeom.calculator.LuckStartCalculator;
import com.dajeom.model.AnnualLuck;
import com.dajeom.model.DecadeLuck;
import com.dajeom.model.FourPillars;
import com.dajeom.model.LuckAnalysis;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.type.LuckDirection;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 대운 방향, 시작 시점, 대운 목록, 세운을 통합 계산한다.
 */
@Component
public class LuckAnalysisEngine {

    public static final String RULE_VERSION =
            "DAJEOM_LUCK_V1";

    private final LuckDirectionCalculator
            luckDirectionCalculator;

    private final LuckStartCalculator
            luckStartCalculator;

    private final DecadeLuckCalculator
            decadeLuckCalculator;

    private final AnnualLuckCalculator
            annualLuckCalculator;

    public LuckAnalysisEngine(
            LuckDirectionCalculator
                    luckDirectionCalculator,
            LuckStartCalculator
                    luckStartCalculator,
            DecadeLuckCalculator
                    decadeLuckCalculator,
            AnnualLuckCalculator
                    annualLuckCalculator
    ) {
        this.luckDirectionCalculator =
                luckDirectionCalculator;

        this.luckStartCalculator =
                luckStartCalculator;

        this.decadeLuckCalculator =
                decadeLuckCalculator;

        this.annualLuckCalculator =
                annualLuckCalculator;
    }

    public LuckAnalysis analyze(
            NormalizedBirthData birthData,
            FourPillars fourPillars,
            int targetYear
    ) {
        LuckDirection direction =
                luckDirectionCalculator.calculate(
                        birthData.gender(),
                        fourPillars.yearPillar()
                );

        LuckStartCalculator.LuckStartResult
                startResult =
                luckStartCalculator.calculate(
                        birthData,
                        direction
                );

        LocalDate targetDate =
                LocalDate.of(
                        targetYear,
                        7,
                        1
                );

        List<DecadeLuck> decadeLucks =
                decadeLuckCalculator.calculate(
                        fourPillars,
                        direction,
                        startResult.startDate(),
                        targetDate
                );

        AnnualLuck annualLuck =
                annualLuckCalculator.calculate(
                        fourPillars,
                        targetYear,
                        birthData.zoneId()
                );

        return new LuckAnalysis(
                direction,
                startResult.startAge(),
                startResult.startDate(),
                startResult.referenceSolarTerm(),
                decadeLucks,
                annualLuck,
                RULE_VERSION
        );
    }
}