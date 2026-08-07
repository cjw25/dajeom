package com.dajeom.engine;

import com.dajeom.analyzer.BranchRelationAnalyzer;
import com.dajeom.analyzer.DayMasterStrengthAnalyzer;
import com.dajeom.analyzer.ElementStrengthAnalyzer;
import com.dajeom.analyzer.RelationImpactAnalyzer;
import com.dajeom.analyzer.StemRelationAnalyzer;
import com.dajeom.analyzer.StructureAnalyzer;
import com.dajeom.analyzer.UsefulGodAnalyzer;
import com.dajeom.calculator.ElementDistributionCalculator;
import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.calculator.TenGodCalculator;
import com.dajeom.model.BranchRelation;
import com.dajeom.model.ElementDistribution;
import com.dajeom.model.ElementStrength;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.model.RelationImpactResult;
import com.dajeom.model.StemRelation;
import com.dajeom.model.StrengthAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.TenGodAssignment;
import com.dajeom.model.UsefulGodAnalysis;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 사주 원국 분석을 통합 실행한다.
 */
@Component
public class NatalChartAnalysisEngine {

    private final HiddenStemCalculator
            hiddenStemCalculator;

    private final TenGodCalculator
            tenGodCalculator;

    private final ElementDistributionCalculator
            elementDistributionCalculator;

    private final ElementStrengthAnalyzer
            elementStrengthAnalyzer;

    private final StemRelationAnalyzer
            stemRelationAnalyzer;

    private final BranchRelationAnalyzer
            branchRelationAnalyzer;

    private final RelationImpactAnalyzer
            relationImpactAnalyzer;

    private final DayMasterStrengthAnalyzer
            dayMasterStrengthAnalyzer;

    private final StructureAnalyzer
            structureAnalyzer;

    private final UsefulGodAnalyzer
            usefulGodAnalyzer;

    public NatalChartAnalysisEngine(
            HiddenStemCalculator
                    hiddenStemCalculator,

            TenGodCalculator
                    tenGodCalculator,

            ElementDistributionCalculator
                    elementDistributionCalculator,

            ElementStrengthAnalyzer
                    elementStrengthAnalyzer,

            StemRelationAnalyzer
                    stemRelationAnalyzer,

            BranchRelationAnalyzer
                    branchRelationAnalyzer,

            RelationImpactAnalyzer
                    relationImpactAnalyzer,

            DayMasterStrengthAnalyzer
                    dayMasterStrengthAnalyzer,

            StructureAnalyzer
                    structureAnalyzer,

            UsefulGodAnalyzer
                    usefulGodAnalyzer
    ) {
        this.hiddenStemCalculator =
                hiddenStemCalculator;

        this.tenGodCalculator =
                tenGodCalculator;

        this.elementDistributionCalculator =
                elementDistributionCalculator;

        this.elementStrengthAnalyzer =
                elementStrengthAnalyzer;

        this.stemRelationAnalyzer =
                stemRelationAnalyzer;

        this.branchRelationAnalyzer =
                branchRelationAnalyzer;

        this.relationImpactAnalyzer =
                relationImpactAnalyzer;

        this.dayMasterStrengthAnalyzer =
                dayMasterStrengthAnalyzer;

        this.structureAnalyzer =
                structureAnalyzer;

        this.usefulGodAnalyzer =
                usefulGodAnalyzer;
    }

    public NatalChartAnalysis analyze(
            FourPillars fourPillars
    ) {
        List<HiddenStem> hiddenStems =
                hiddenStemCalculator.calculate(
                        fourPillars
                );

        List<TenGodAssignment>
                tenGodAssignments =
                tenGodCalculator.calculate(
                        fourPillars,
                        hiddenStems
                );

        ElementDistribution
                elementDistribution =
                elementDistributionCalculator.calculate(
                        fourPillars,
                        hiddenStems
                );

        ElementStrength elementStrength =
                elementStrengthAnalyzer.analyze(
                        fourPillars,
                        hiddenStems
                );

        List<StemRelation> stemRelations =
                stemRelationAnalyzer.analyze(
                        fourPillars
                );

        List<BranchRelation> branchRelations =
                branchRelationAnalyzer.analyze(
                        fourPillars
                );

        RelationAnalysis relationAnalysis =
                new RelationAnalysis(
                        stemRelations,
                        branchRelations
                );

        RelationImpactResult relationImpact =
                relationImpactAnalyzer.analyze(
                        fourPillars
                                .dayPillar()
                                .stem()
                                .getElement(),

                        hiddenStems,

                        relationAnalysis
                );

        StrengthAnalysis strengthAnalysis =
                dayMasterStrengthAnalyzer.analyze(
                        fourPillars,
                        hiddenStems,
                        elementStrength,
                        relationImpact
                );

        StructureAnalysis structureAnalysis =
                structureAnalyzer.analyze(
                        fourPillars,
                        hiddenStems,
                        tenGodAssignments,
                        elementStrength,
                        strengthAnalysis
                );

        UsefulGodAnalysis usefulGodAnalysis =
                usefulGodAnalyzer.analyze(
                        fourPillars,
                        elementStrength,
                        strengthAnalysis
                );

        return new NatalChartAnalysis(
                hiddenStems,
                tenGodAssignments,
                elementDistribution,
                elementStrength,
                relationAnalysis,
                strengthAnalysis,
                structureAnalysis,
                usefulGodAnalysis
        );
    }
}