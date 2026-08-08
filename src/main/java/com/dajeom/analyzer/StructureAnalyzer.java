package com.dajeom.analyzer;

import com.dajeom.model.HiddenStemAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.NatalChartBaseAnalysis;
import com.dajeom.model.PillarAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.StructureRule;
import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.StructureType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 원국의 기본 격국/구조를 분석한다.
 *
 * V1 기준:
 *
 * 1. 월지 확인
 * 2. 월지 정기 확인
 * 3. 정기의 십성 확인
 * 4. 기본 8격 후보 분류
 * 5. 정기의 천간 투출 여부 확인
 *
 * 특수격이나 종격 등은
 * 현재 단계에서 확정하지 않는다.
 */
@Component
public class StructureAnalyzer {

    public StructureAnalysis analyze(
            NatalChartAnalysis natalChart
    ) {

        Objects.requireNonNull(
                natalChart,
                "원국 분석 결과는 필수입니다."
        );

        NatalChartBaseAnalysis baseAnalysis =
                natalChart.baseAnalysis();

        PillarAnalysis monthAnalysis =
                baseAnalysis
                        .findPillar(
                                PillarPosition.MONTH
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "월주 분석 결과가 없습니다."
                                        )
                        );

        HiddenStemAnalysis mainHiddenStem =
                monthAnalysis
                        .hiddenStems()
                        .stream()
                        .filter(
                                hiddenStem ->
                                        hiddenStem
                                                .hiddenStem()
                                                .role()
                                                == HiddenStemRole.MAIN
                        )
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "월지 정기를 찾을 수 없습니다."
                                        )
                        );

        HeavenlyStem monthMainStem =
                mainHiddenStem
                        .hiddenStem()
                        .stem();

        StructureType structureType =
                StructureRule.classify(
                        mainHiddenStem.tenGod()
                );

        List<PillarPosition> exposedPositions =
                findExposedPositions(
                        baseAnalysis,
                        monthMainStem
                );

        AnalysisConfidence confidence =
                determineConfidence(
                        structureType,
                        exposedPositions,
                        baseAnalysis.hasHourPillar()
                );

        return new StructureAnalysis(
                structureType,
                monthAnalysis
                        .pillar()
                        .branch(),
                monthMainStem,
                mainHiddenStem.tenGod(),
                exposedPositions,
                confidence,
                StructureRule.RULE_VERSION
        );
    }

    /**
     * 월지 정기가 어느 천간에 투출되어 있는지 확인한다.
     *
     * 일간 자체는 투간 판정에서 제외한다.
     */
    private List<PillarPosition>
    findExposedPositions(
            NatalChartBaseAnalysis baseAnalysis,
            HeavenlyStem targetStem
    ) {

        return baseAnalysis
                .pillars()
                .stream()
                .filter(
                        pillar ->
                                pillar.position()
                                        != PillarPosition.DAY
                )
                .filter(
                        pillar ->
                                pillar
                                        .pillar()
                                        .stem()
                                        == targetStem
                )
                .map(
                        PillarAnalysis::position
                )
                .toList();
    }

    private AnalysisConfidence
    determineConfidence(
            StructureType structureType,
            List<PillarPosition> exposedPositions,
            boolean hourPillarKnown
    ) {

        if (
                structureType
                        == StructureType.UNDETERMINED
        ) {

            return AnalysisConfidence.LOW;
        }

        if (
                !exposedPositions.isEmpty()
        ) {

            return AnalysisConfidence.HIGH;
        }

        /*
         * 시주를 모르면 시천간에서
         * 정기가 투출될 가능성을 확인할 수 없다.
         */
        if (
                !hourPillarKnown
        ) {

            return AnalysisConfidence.LOW;
        }

        return AnalysisConfidence.MEDIUM;
    }
}