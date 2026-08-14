package com.dajeom.engine;

import com.dajeom.analyzer.MonthStructureCandidateAnalyzer;
import com.dajeom.analyzer.SpecialStructureAnalyzer;
import com.dajeom.analyzer.StructureAnalyzer;
import com.dajeom.analyzer.StructureBalanceAnalyzer;
import com.dajeom.analyzer.StructureCandidateEstablishmentAnalyzer;
import com.dajeom.analyzer.StructureDamageAnalyzer;
import com.dajeom.analyzer.StructureExposurePriorityAnalyzer;
import com.dajeom.analyzer.StructureFormationSupportAnalyzer;
import com.dajeom.analyzer.StructurePurityAnalyzer;
import com.dajeom.analyzer.StructureSelectionAnalyzer;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.MonthStructureCandidateAnalysis;
import com.dajeom.model.SpecialStructureAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.StructureBalanceAnalysis;
import com.dajeom.model.StructureCandidateEstablishmentAnalysis;
import com.dajeom.model.StructureDamageAnalysis;
import com.dajeom.model.StructureExposurePriorityAnalysis;
import com.dajeom.model.StructureFormationSupportAnalysis;
import com.dajeom.model.StructurePurityAnalysis;
import com.dajeom.model.StructureSelectionAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 5차 전체 격국 분석 Engine.
 *
 * 처리 순서:
 *
 * 4차 DayMasterStrengthAnalysis
 *
 * ↓
 *
 * 5-1 월령 격 후보
 *
 * ↓
 *
 * 5-2 투출 우선순위
 *
 * ↓
 *
 * 5-3 격 성립도
 *
 * ↓
 *
 * 5-4 격 세력 × 일간 강약
 *
 * ↓
 *
 * 5-5 성격(成格)
 *
 * ↓
 *
 * 5-6 파격(破格)
 *
 * ↓
 *
 * 5-7 순도·혼잡도
 *
 * ↓
 *
 * 5-8 특수·종격 후보
 *
 * ↓
 *
 * 5-9 주격·보조격
 *
 * ↓
 *
 * 5-10 최종 StructureAnalysis
 */
@Component
public class StructureAnalysisEngine {

    private final DayMasterStrengthAnalysisEngine
            dayMasterStrengthAnalysisEngine;

    private final MonthStructureCandidateAnalyzer
            monthStructureCandidateAnalyzer;

    private final StructureExposurePriorityAnalyzer
            structureExposurePriorityAnalyzer;

    private final StructureCandidateEstablishmentAnalyzer
            structureCandidateEstablishmentAnalyzer;

    private final StructureBalanceAnalyzer
            structureBalanceAnalyzer;

    private final StructureFormationSupportAnalyzer
            structureFormationSupportAnalyzer;

    private final StructureDamageAnalyzer
            structureDamageAnalyzer;

    private final StructurePurityAnalyzer
            structurePurityAnalyzer;

    private final SpecialStructureAnalyzer
            specialStructureAnalyzer;

    private final StructureSelectionAnalyzer
            structureSelectionAnalyzer;

    private final StructureAnalyzer
            structureAnalyzer;

    public StructureAnalysisEngine(

            DayMasterStrengthAnalysisEngine
                    dayMasterStrengthAnalysisEngine,

            MonthStructureCandidateAnalyzer
                    monthStructureCandidateAnalyzer,

            StructureExposurePriorityAnalyzer
                    structureExposurePriorityAnalyzer,

            StructureCandidateEstablishmentAnalyzer
                    structureCandidateEstablishmentAnalyzer,

            StructureBalanceAnalyzer
                    structureBalanceAnalyzer,

            StructureFormationSupportAnalyzer
                    structureFormationSupportAnalyzer,

            StructureDamageAnalyzer
                    structureDamageAnalyzer,

            StructurePurityAnalyzer
                    structurePurityAnalyzer,

            SpecialStructureAnalyzer
                    specialStructureAnalyzer,

            StructureSelectionAnalyzer
                    structureSelectionAnalyzer,

            StructureAnalyzer
                    structureAnalyzer

    ) {

        this.dayMasterStrengthAnalysisEngine =
                Objects.requireNonNull(
                        dayMasterStrengthAnalysisEngine,
                        "DayMasterStrengthAnalysisEngine이 필요합니다."
                );

        this.monthStructureCandidateAnalyzer =
                Objects.requireNonNull(
                        monthStructureCandidateAnalyzer,
                        "MonthStructureCandidateAnalyzer가 필요합니다."
                );

        this.structureExposurePriorityAnalyzer =
                Objects.requireNonNull(
                        structureExposurePriorityAnalyzer,
                        "StructureExposurePriorityAnalyzer가 필요합니다."
                );

        this.structureCandidateEstablishmentAnalyzer =
                Objects.requireNonNull(
                        structureCandidateEstablishmentAnalyzer,
                        "StructureCandidateEstablishmentAnalyzer가 필요합니다."
                );

        this.structureBalanceAnalyzer =
                Objects.requireNonNull(
                        structureBalanceAnalyzer,
                        "StructureBalanceAnalyzer가 필요합니다."
                );

        this.structureFormationSupportAnalyzer =
                Objects.requireNonNull(
                        structureFormationSupportAnalyzer,
                        "StructureFormationSupportAnalyzer가 필요합니다."
                );

        this.structureDamageAnalyzer =
                Objects.requireNonNull(
                        structureDamageAnalyzer,
                        "StructureDamageAnalyzer가 필요합니다."
                );

        this.structurePurityAnalyzer =
                Objects.requireNonNull(
                        structurePurityAnalyzer,
                        "StructurePurityAnalyzer가 필요합니다."
                );

        this.specialStructureAnalyzer =
                Objects.requireNonNull(
                        specialStructureAnalyzer,
                        "SpecialStructureAnalyzer가 필요합니다."
                );

        this.structureSelectionAnalyzer =
                Objects.requireNonNull(
                        structureSelectionAnalyzer,
                        "StructureSelectionAnalyzer가 필요합니다."
                );

        this.structureAnalyzer =
                Objects.requireNonNull(
                        structureAnalyzer,
                        "StructureAnalyzer가 필요합니다."
                );
    }

    /**
     * FourPillars부터:
     *
     * 2차
     * 3차
     * 4차
     * 5차
     *
     * 를 연결해서 실행한다.
     */
    public StructureAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        DayMasterStrengthAnalysis strength =
                dayMasterStrengthAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        return analyze(
                strength
        );
    }

    /**
     * 4차까지 이미 계산되어 있다면
     * 5차만 수행한다.
     */
    public StructureAnalysis analyze(
            DayMasterStrengthAnalysis strength
    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        /*
         * ================================================
         * 5-1
         * 월령 격 후보
         * ================================================
         */
        MonthStructureCandidateAnalysis candidates =
                monthStructureCandidateAnalyzer
                        .analyze(
                                strength
                        );

        /*
         * ================================================
         * 5-2
         * 투출 우선순위
         * ================================================
         */
        StructureExposurePriorityAnalysis exposure =
                structureExposurePriorityAnalyzer
                        .analyze(
                                candidates
                        );

        /*
         * ================================================
         * 5-3
         * 격 성립도
         * ================================================
         */
        StructureCandidateEstablishmentAnalysis establishment =
                structureCandidateEstablishmentAnalyzer
                        .analyze(
                                strength,
                                candidates,
                                exposure
                        );

        /*
         * ================================================
         * 5-4
         * 격국 세력 × 일간 강약
         * ================================================
         */
        StructureBalanceAnalysis balance =
                structureBalanceAnalyzer
                        .analyze(
                                strength,
                                establishment
                        );

        /*
         * ================================================
         * 5-5
         * 성격(成格)
         * ================================================
         */
        StructureFormationSupportAnalysis support =
                structureFormationSupportAnalyzer
                        .analyze(
                                strength,
                                balance
                        );

        /*
         * ================================================
         * 5-6
         * 파격(破格)
         * ================================================
         */
        StructureDamageAnalysis damage =
                structureDamageAnalyzer
                        .analyze(
                                strength,
                                support
                        );

        /*
         * ================================================
         * 5-7
         * 순도·혼잡도
         * ================================================
         */
        StructurePurityAnalysis purity =
                structurePurityAnalyzer
                        .analyze(
                                strength,
                                damage
                        );

        /*
         * ================================================
         * 5-8
         * 특수격·종격 후보
         * ================================================
         */
        SpecialStructureAnalysis special =
                specialStructureAnalyzer
                        .analyze(
                                strength,
                                purity
                        );

        /*
         * ================================================
         * 5-9
         * 주격·보조격
         * ================================================
         */
        StructureSelectionAnalysis selection =
                structureSelectionAnalyzer
                        .analyze(
                                strength,
                                purity,
                                special
                        );

        /*
         * ================================================
         * 5-10
         * StructureRule 최종 통합
         * ================================================
         */
        return structureAnalyzer
                .analyze(
                        strength,
                        candidates,
                        exposure,
                        establishment,
                        balance,
                        support,
                        damage,
                        purity,
                        special,
                        selection
                );
    }
}