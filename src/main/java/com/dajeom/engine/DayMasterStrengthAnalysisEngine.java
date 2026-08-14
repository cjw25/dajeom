package com.dajeom.engine;

import com.dajeom.analyzer.DayMasterElementRelationAnalyzer;
import com.dajeom.analyzer.DayMasterForceFormationAnalyzer;
import com.dajeom.analyzer.DayMasterGroundAnalyzer;
import com.dajeom.analyzer.DayMasterPressurePowerAnalyzer;
import com.dajeom.analyzer.DayMasterSeasonalAnalyzer;
import com.dajeom.analyzer.DayMasterSupportPowerAnalyzer;
import com.dajeom.analyzer.ExtremeStrengthStructureAnalyzer;
import com.dajeom.model.DayMasterElementRelationAnalysis;
import com.dajeom.model.DayMasterForceFormationAnalysis;
import com.dajeom.model.DayMasterGroundAnalysis;
import com.dajeom.model.DayMasterPressurePowerAnalysis;
import com.dajeom.model.DayMasterSeasonalAnalysis;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.DayMasterStrengthEvidence;
import com.dajeom.model.DayMasterSupportPowerAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.ExtremeStrengthStructureAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.rule.DayMasterStrengthRule;
import com.dajeom.type.DayMasterStrengthLevel;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 4차 전체 신강·신약 분석 Engine.
 *
 * 처리 순서:
 *
 * 3차 ElementStrengthAnalysis
 *
 * ↓
 *
 * 4-1 일간 기준 오행 관계
 *
 * ↓
 *
 * 4-2 득령
 *
 * ↓
 *
 * 4-3 득지
 *
 * ↓
 *
 * 4-4 득세
 *
 * ↓
 *
 * 4-5 비겁·인성 지원력
 *
 * ↓
 *
 * 4-6 식상·재성·관살 압력
 *
 * ↓
 *
 * 4-7 극단 구조 검토
 *
 * ↓
 *
 * 4-8 최종 신강·신약
 */
@Component
public class DayMasterStrengthAnalysisEngine {

    private final ElementStrengthAnalysisEngine
            elementStrengthAnalysisEngine;

    private final DayMasterElementRelationAnalyzer
            dayMasterElementRelationAnalyzer;

    private final DayMasterSeasonalAnalyzer
            dayMasterSeasonalAnalyzer;

    private final DayMasterGroundAnalyzer
            dayMasterGroundAnalyzer;

    private final DayMasterForceFormationAnalyzer
            dayMasterForceFormationAnalyzer;

    private final DayMasterSupportPowerAnalyzer
            dayMasterSupportPowerAnalyzer;

    private final DayMasterPressurePowerAnalyzer
            dayMasterPressurePowerAnalyzer;

    private final ExtremeStrengthStructureAnalyzer
            extremeStrengthStructureAnalyzer;

    private final DayMasterStrengthRule
            dayMasterStrengthRule;

    public DayMasterStrengthAnalysisEngine(

            ElementStrengthAnalysisEngine
                    elementStrengthAnalysisEngine,

            DayMasterElementRelationAnalyzer
                    dayMasterElementRelationAnalyzer,

            DayMasterSeasonalAnalyzer
                    dayMasterSeasonalAnalyzer,

            DayMasterGroundAnalyzer
                    dayMasterGroundAnalyzer,

            DayMasterForceFormationAnalyzer
                    dayMasterForceFormationAnalyzer,

            DayMasterSupportPowerAnalyzer
                    dayMasterSupportPowerAnalyzer,

            DayMasterPressurePowerAnalyzer
                    dayMasterPressurePowerAnalyzer,

            ExtremeStrengthStructureAnalyzer
                    extremeStrengthStructureAnalyzer,

            DayMasterStrengthRule
                    dayMasterStrengthRule

    ) {

        this.elementStrengthAnalysisEngine =
                Objects.requireNonNull(
                        elementStrengthAnalysisEngine,
                        "ElementStrengthAnalysisEngine이 필요합니다."
                );

        this.dayMasterElementRelationAnalyzer =
                Objects.requireNonNull(
                        dayMasterElementRelationAnalyzer,
                        "DayMasterElementRelationAnalyzer가 필요합니다."
                );

        this.dayMasterSeasonalAnalyzer =
                Objects.requireNonNull(
                        dayMasterSeasonalAnalyzer,
                        "DayMasterSeasonalAnalyzer가 필요합니다."
                );

        this.dayMasterGroundAnalyzer =
                Objects.requireNonNull(
                        dayMasterGroundAnalyzer,
                        "DayMasterGroundAnalyzer가 필요합니다."
                );

        this.dayMasterForceFormationAnalyzer =
                Objects.requireNonNull(
                        dayMasterForceFormationAnalyzer,
                        "DayMasterForceFormationAnalyzer가 필요합니다."
                );

        this.dayMasterSupportPowerAnalyzer =
                Objects.requireNonNull(
                        dayMasterSupportPowerAnalyzer,
                        "DayMasterSupportPowerAnalyzer가 필요합니다."
                );

        this.dayMasterPressurePowerAnalyzer =
                Objects.requireNonNull(
                        dayMasterPressurePowerAnalyzer,
                        "DayMasterPressurePowerAnalyzer가 필요합니다."
                );

        this.extremeStrengthStructureAnalyzer =
                Objects.requireNonNull(
                        extremeStrengthStructureAnalyzer,
                        "ExtremeStrengthStructureAnalyzer가 필요합니다."
                );

        this.dayMasterStrengthRule =
                Objects.requireNonNull(
                        dayMasterStrengthRule,
                        "DayMasterStrengthRule이 필요합니다."
                );
    }

    /**
     * FourPillars부터:
     *
     * 2차
     * 3차
     * 4차
     *
     * 전체를 연결해서 실행한다.
     */
    public DayMasterStrengthAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        ElementStrengthAnalysis elementStrength =
                elementStrengthAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        return analyze(
                elementStrength
        );
    }

    /**
     * 3차 결과가 이미 계산되어 있다면
     * 4차만 수행한다.
     */
    public DayMasterStrengthAnalysis analyze(
            ElementStrengthAnalysis elementStrength
    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        /*
         * ================================================
         * 4-1
         * 일간 기준 오행 관계
         * ================================================
         */
        DayMasterElementRelationAnalysis relations =
                dayMasterElementRelationAnalyzer
                        .analyze(
                                elementStrength
                        );

        /*
         * ================================================
         * 4-2
         * 득령
         * ================================================
         */
        DayMasterSeasonalAnalysis seasonal =
                dayMasterSeasonalAnalyzer
                        .analyze(
                                elementStrength
                        );

        /*
         * ================================================
         * 4-3
         * 득지
         * ================================================
         */
        DayMasterGroundAnalysis ground =
                dayMasterGroundAnalyzer
                        .analyze(
                                elementStrength
                        );

        /*
         * ================================================
         * 4-4
         * 득세
         * ================================================
         */
        DayMasterForceFormationAnalysis formation =
                dayMasterForceFormationAnalyzer
                        .analyze(
                                elementStrength,
                                relations
                        );

        /*
         * ================================================
         * 4-5
         * 비겁·인성 지원력
         *
         * 이미 계산한 4-1 / 4-4를 재사용.
         * ================================================
         */
        DayMasterSupportPowerAnalysis support =
                dayMasterSupportPowerAnalyzer
                        .analyze(
                                elementStrength,
                                relations,
                                formation
                        );

        /*
         * ================================================
         * 4-6
         * 식상·재성·관살 압력
         *
         * 동일하게 4-1 / 4-4 결과를 재사용.
         * ================================================
         */
        DayMasterPressurePowerAnalysis pressure =
                dayMasterPressurePowerAnalyzer
                        .analyze(
                                elementStrength,
                                relations,
                                formation
                        );

        /*
         * ================================================
         * 4-7
         * 극단 구조 Candidate 검토
         * ================================================
         */
        ExtremeStrengthStructureAnalysis extreme =
                extremeStrengthStructureAnalyzer
                        .analyze(
                                seasonal,
                                ground,
                                formation,
                                support,
                                pressure
                        );

        /*
         * ================================================
         * 4-8
         * 구조 보정
         * ================================================
         */

        int seasonalAdjustment =
                dayMasterStrengthRule
                        .calculateSeasonalAdjustment(
                                seasonal
                        );

        int groundAdjustment =
                dayMasterStrengthRule
                        .calculateGroundAdjustment(
                                ground
                        );

        int forceAdjustment =
                dayMasterStrengthRule
                        .calculateForceAdjustment(
                                formation
                        );

        int structuralAdjustment =
                dayMasterStrengthRule
                        .calculateStructuralAdjustment(
                                seasonalAdjustment,
                                groundAdjustment,
                                forceAdjustment
                        );

        /*
         * 지원/압력의 기본 비중은
         * 4-7에서 이미 정확히 계산되어 있다.
         *
         * 다시 같은 계산식을 만들지 않고
         * 해당 Evidence를 재사용한다.
         */
        int rawSupportShareBasisPoints =
                extreme
                        .evidence()
                        .supportShareBasisPoints();

        int rawPressureShareBasisPoints =
                extreme
                        .evidence()
                        .pressureShareBasisPoints();

        int adjustedStrengthBasisPoints =
                dayMasterStrengthRule
                        .calculateAdjustedStrengthBasisPoints(
                                rawSupportShareBasisPoints,
                                structuralAdjustment
                        );

        DayMasterStrengthLevel level =
                dayMasterStrengthRule
                        .determineLevel(
                                adjustedStrengthBasisPoints,
                                extreme
                        );

        boolean extremeStructureCandidate =
                extreme.extremeStrongCandidate()
                        || extreme.extremeWeakCandidate();

        boolean followStructureCandidate =
                extreme.followStrongCandidate()
                        || extreme.followWeakCandidate();

        DayMasterStrengthEvidence evidence =
                new DayMasterStrengthEvidence(
                        elementStrength.dayMaster(),
                        elementStrength.dayMasterElement(),
                        support.totalSupportPower(),
                        pressure.totalPressurePower(),
                        rawSupportShareBasisPoints,
                        rawPressureShareBasisPoints,
                        seasonalAdjustment,
                        groundAdjustment,
                        forceAdjustment,
                        structuralAdjustment,
                        adjustedStrengthBasisPoints,
                        extremeStructureCandidate,
                        followStructureCandidate
                );

        return new DayMasterStrengthAnalysis(
                elementStrength,
                relations,
                seasonal,
                ground,
                formation,
                support,
                pressure,
                extreme,
                evidence,
                level
        );
    }
}