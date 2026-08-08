package com.dajeom.engine;

import com.dajeom.calculator.AnnualLuckCalculator;
import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.calculator.TenGodCalculator;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.FourPillars;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.rule.AnnualLuckRule;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TenGod;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 특정 연도의 세운 분석 Engine.
 *
 * 처리:
 *
 * targetYear
 *      ↓
 * AnnualLuckCalculator
 *      ↓
 * 세운 간지 + 입춘 기간
 *      ↓
 * 원국 일간과 비교
 *      ↓
 * 세운 천간 십성
 *      ↓
 * 세운 지지 정기 십성
 *      ↓
 * AnnualLuckAnalysis
 */
@Component
public class AnnualLuckAnalysisEngine {

    private final AnnualLuckCalculator
            annualLuckCalculator;

    private final HiddenStemCalculator
            hiddenStemCalculator;

    private final TenGodCalculator
            tenGodCalculator;

    public AnnualLuckAnalysisEngine(
            AnnualLuckCalculator annualLuckCalculator,
            HiddenStemCalculator hiddenStemCalculator,
            TenGodCalculator tenGodCalculator
    ) {

        this.annualLuckCalculator =
                annualLuckCalculator;

        this.hiddenStemCalculator =
                hiddenStemCalculator;

        this.tenGodCalculator =
                tenGodCalculator;
    }

    public AnnualLuckAnalysis analyze(
            NormalizedBirthData birthData,
            FourPillars fourPillars,
            int targetYear
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        /*
         * 1.
         * 해당 연도의 세운 간지와
         * 입춘 기간을 계산한다.
         */
        AnnualLuckPeriod annualLuck =
                annualLuckCalculator.calculate(
                        targetYear,
                        birthData.zoneId()
                );

        HeavenlyStem dayMaster =
                fourPillars.dayMaster();

        /*
         * 2.
         * 세운 천간의 십성.
         */
        TenGod stemTenGod =
                tenGodCalculator.calculate(
                        dayMaster,
                        annualLuck
                                .pillar()
                                .stem()
                );

        /*
         * 3.
         * 세운 지지의 정기(본기).
         */
        HeavenlyStem branchMainStem =
                hiddenStemCalculator
                        .calculateMainStem(
                                annualLuck
                                        .pillar()
                                        .branch()
                        );

        /*
         * 4.
         * 세운 지지 정기의 십성.
         */
        TenGod branchMainTenGod =
                tenGodCalculator.calculate(
                        dayMaster,
                        branchMainStem
                );

        return new AnnualLuckAnalysis(
                targetYear,
                annualLuck,
                stemTenGod,
                branchMainStem,
                branchMainTenGod,
                AnnualLuckRule.RULE_VERSION
        );
    }
}