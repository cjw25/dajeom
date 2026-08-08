package com.dajeom.analyzer;

import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.calculator.TenGodCalculator;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.LuckPillarSource;
import com.dajeom.model.LuckTenGodInfluence;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.LuckSourceType;
import com.dajeom.type.TenGod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 대운과 세운이
 * 원국 일간 기준으로 가지는
 * 십성을 분석한다.
 */
@Component
public class LuckTenGodAnalyzer {

    private final TenGodCalculator
            tenGodCalculator;

    private final HiddenStemCalculator
            hiddenStemCalculator;

    public LuckTenGodAnalyzer(
            TenGodCalculator tenGodCalculator,
            HiddenStemCalculator hiddenStemCalculator
    ) {

        this.tenGodCalculator =
                tenGodCalculator;

        this.hiddenStemCalculator =
                hiddenStemCalculator;
    }

    public List<LuckTenGodInfluence> analyze(
            HeavenlyStem dayMaster,
            DecadeLuckPeriod decadeLuckPeriod,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "세운 분석 결과는 필수입니다."
        );

        List<LuckTenGodInfluence> result =
                new ArrayList<>();

        /*
         * 대운 십성.
         */
        if (decadeLuckPeriod != null) {

            LuckPillarSource source =
                    new LuckPillarSource(
                            LuckSourceType.DECADE_LUCK,
                            null,
                            decadeLuckPeriod.order(),
                            decadeLuckPeriod.pillar()
                    );

            HeavenlyStem branchMainStem =
                    hiddenStemCalculator
                            .calculateMainStem(
                                    decadeLuckPeriod
                                            .pillar()
                                            .branch()
                            );

            TenGod stemTenGod =
                    tenGodCalculator.calculate(
                            dayMaster,
                            decadeLuckPeriod
                                    .pillar()
                                    .stem()
                    );

            TenGod branchMainTenGod =
                    tenGodCalculator.calculate(
                            dayMaster,
                            branchMainStem
                    );

            result.add(
                    new LuckTenGodInfluence(
                            source,
                            stemTenGod,
                            branchMainStem,
                            branchMainTenGod
                    )
            );
        }

        /*
         * 세운 십성은 14차에서 이미
         * 계산된 결과를 그대로 재사용한다.
         */
        LuckPillarSource annualSource =
                new LuckPillarSource(
                        LuckSourceType.ANNUAL_LUCK,
                        null,
                        annualLuckAnalysis.targetYear(),
                        annualLuckAnalysis.pillar()
                );

        result.add(
                new LuckTenGodInfluence(
                        annualSource,
                        annualLuckAnalysis.stemTenGod(),
                        annualLuckAnalysis.branchMainStem(),
                        annualLuckAnalysis.branchMainTenGod()
                )
        );

        return List.copyOf(
                result
        );
    }
}