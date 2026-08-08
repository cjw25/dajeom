package com.dajeom.engine;

import com.dajeom.calculator.ElementDistributionCalculator;
import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.calculator.TenGodCalculator;
import com.dajeom.model.ElementDistribution;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.HiddenStemAnalysis;
import com.dajeom.model.NatalChartBaseAnalysis;
import com.dajeom.model.Pillar;
import com.dajeom.model.PillarAnalysis;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.TenGod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 원국의 기초 데이터를 하나로 조립하는 Engine.
 *
 * 실제 계산은 Calculator가 수행한다.
 *
 * 이 Engine의 역할:
 *
 * - 일간 결정
 * - 각 천간 십성 계산
 * - 지장간 조회
 * - 지장간 십성 계산
 * - 오행 기본 분포 계산
 * - 결과를 NatalChartBaseAnalysis로 조립
 *
 * 신강신약, 용신, 관계 판정은
 * 아직 하지 않는다.
 */
@Component
public class NatalChartBaseAnalysisEngine {

    private final HiddenStemCalculator
            hiddenStemCalculator;

    private final TenGodCalculator
            tenGodCalculator;

    private final ElementDistributionCalculator
            elementDistributionCalculator;

    public NatalChartBaseAnalysisEngine(
            HiddenStemCalculator hiddenStemCalculator,
            TenGodCalculator tenGodCalculator,
            ElementDistributionCalculator
                    elementDistributionCalculator
    ) {

        this.hiddenStemCalculator =
                hiddenStemCalculator;

        this.tenGodCalculator =
                tenGodCalculator;

        this.elementDistributionCalculator =
                elementDistributionCalculator;
    }

    public NatalChartBaseAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        HeavenlyStem dayMaster =
                fourPillars.dayMaster();

        List<PillarAnalysis> pillarAnalyses =
                new ArrayList<>();

        pillarAnalyses.add(
                analyzePillar(
                        PillarPosition.YEAR,
                        fourPillars.yearPillar(),
                        dayMaster
                )
        );

        pillarAnalyses.add(
                analyzePillar(
                        PillarPosition.MONTH,
                        fourPillars.monthPillar(),
                        dayMaster
                )
        );

        pillarAnalyses.add(
                analyzePillar(
                        PillarPosition.DAY,
                        fourPillars.dayPillar(),
                        dayMaster
                )
        );

        /*
         * 출생시간을 모르는 경우
         * 시주는 존재하지 않으므로
         * 분석 목록에도 넣지 않는다.
         */
        if (
                fourPillars.hasHourPillar()
        ) {

            pillarAnalyses.add(
                    analyzePillar(
                            PillarPosition.HOUR,
                            fourPillars.hourPillar(),
                            dayMaster
                    )
            );
        }

        ElementDistribution
                elementDistribution =
                elementDistributionCalculator
                        .calculate(
                                fourPillars
                        );

        return new NatalChartBaseAnalysis(
                fourPillars,
                dayMaster,
                pillarAnalyses,
                elementDistribution
        );
    }

    /**
     * 하나의 기둥을 분석한다.
     */
    private PillarAnalysis analyzePillar(
            PillarPosition position,
            Pillar pillar,
            HeavenlyStem dayMaster
    ) {

        TenGod stemTenGod =
                tenGodCalculator.calculate(
                        dayMaster,
                        pillar.stem()
                );

        List<HiddenStem> hiddenStems =
                hiddenStemCalculator.calculate(
                        pillar
                );

        List<HiddenStemAnalysis>
                hiddenStemAnalyses =
                hiddenStems
                        .stream()
                        .map(
                                hiddenStem ->
                                        analyzeHiddenStem(
                                                dayMaster,
                                                hiddenStem
                                        )
                        )
                        .toList();

        return new PillarAnalysis(
                position,
                pillar,
                stemTenGod,
                hiddenStemAnalyses
        );
    }

    /**
     * 지장간 하나의 십성을 계산한다.
     */
    private HiddenStemAnalysis analyzeHiddenStem(
            HeavenlyStem dayMaster,
            HiddenStem hiddenStem
    ) {

        TenGod tenGod =
                tenGodCalculator.calculate(
                        dayMaster,
                        hiddenStem.stem()
                );

        return new HiddenStemAnalysis(
                hiddenStem,
                tenGod
        );
    }
}