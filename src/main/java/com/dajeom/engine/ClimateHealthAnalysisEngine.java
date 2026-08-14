package com.dajeom.engine;

import com.dajeom.analyzer.ClimateAdjustmentNeedAnalyzer;
import com.dajeom.analyzer.ClimateElementCandidateAnalyzer;
import com.dajeom.analyzer.ClimateEnvironmentAnalyzer;
import com.dajeom.analyzer.ClimateHealthCrossValidationAnalyzer;
import com.dajeom.analyzer.ClimateHealthIntegrationAnalyzer;
import com.dajeom.analyzer.PassageCandidateAnalyzer;
import com.dajeom.analyzer.PassageNeedAnalyzer;
import com.dajeom.analyzer.PathologyAnalyzer;
import com.dajeom.analyzer.RemedyAnalyzer;
import com.dajeom.model.ClimateAdjustmentNeedAnalysis;
import com.dajeom.model.ClimateElementCandidateAnalysis;
import com.dajeom.model.ClimateEnvironmentAnalysis;
import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.ClimateHealthCrossValidationAnalysis;
import com.dajeom.model.ClimateHealthIntegrationAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.PassageCandidateAnalysis;
import com.dajeom.model.PassageNeedAnalysis;
import com.dajeom.model.PathologyAnalysis;
import com.dajeom.model.RemedyAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.ClimateHealthRule;
import com.dajeom.type.ClimateHealthStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 6차 전체 조후·병약·통관 분석 Engine.
 *
 * =================================================
 *
 * 처리 순서
 *
 * 5차 StructureAnalysis
 *
 * ↓
 *
 * 6-1
 * ClimateEnvironmentAnalysis
 *
 * ↓
 *
 * 6-2
 * ClimateAdjustmentNeedAnalysis
 *
 * ↓
 *
 * 6-3
 * ClimateElementCandidateAnalysis
 *
 * ↓
 *
 * 6-4
 * PathologyAnalysis
 *
 * ↓
 *
 * 6-5
 * RemedyAnalysis
 *
 * ↓
 *
 * 6-6
 * PassageNeedAnalysis
 *
 * ↓
 *
 * 6-7
 * PassageCandidateAnalysis
 *
 * ↓
 *
 * 6-8
 * ClimateHealthIntegrationAnalysis
 *
 * ↓
 *
 * 6-9
 * ClimateHealthCrossValidationAnalysis
 *
 * ↓
 *
 * 6-10
 * ClimateHealthAnalysis
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 각 단계에서 이미 계산한 결과를
 * 다음 단계에 그대로 전달한다.
 *
 * 동일 계산을 다시 수행하지 않는다.
 *
 * ClimateHealthAnalysis의 PRIMARY 후보는
 * 6차 핵심 후보일 뿐
 * 최종 용신 확정이 아니다.
 */
@Component
public class ClimateHealthAnalysisEngine {

    private final StructureAnalysisEngine
            structureAnalysisEngine;

    private final ClimateEnvironmentAnalyzer
            climateEnvironmentAnalyzer;

    private final ClimateAdjustmentNeedAnalyzer
            climateAdjustmentNeedAnalyzer;

    private final ClimateElementCandidateAnalyzer
            climateElementCandidateAnalyzer;

    private final PathologyAnalyzer
            pathologyAnalyzer;

    private final RemedyAnalyzer
            remedyAnalyzer;

    private final PassageNeedAnalyzer
            passageNeedAnalyzer;

    private final PassageCandidateAnalyzer
            passageCandidateAnalyzer;

    private final ClimateHealthIntegrationAnalyzer
            climateHealthIntegrationAnalyzer;

    private final ClimateHealthCrossValidationAnalyzer
            climateHealthCrossValidationAnalyzer;

    private final ClimateHealthRule
            climateHealthRule;

    public ClimateHealthAnalysisEngine(

            StructureAnalysisEngine
                    structureAnalysisEngine,

            ClimateEnvironmentAnalyzer
                    climateEnvironmentAnalyzer,

            ClimateAdjustmentNeedAnalyzer
                    climateAdjustmentNeedAnalyzer,

            ClimateElementCandidateAnalyzer
                    climateElementCandidateAnalyzer,

            PathologyAnalyzer
                    pathologyAnalyzer,

            RemedyAnalyzer
                    remedyAnalyzer,

            PassageNeedAnalyzer
                    passageNeedAnalyzer,

            PassageCandidateAnalyzer
                    passageCandidateAnalyzer,

            ClimateHealthIntegrationAnalyzer
                    climateHealthIntegrationAnalyzer,

            ClimateHealthCrossValidationAnalyzer
                    climateHealthCrossValidationAnalyzer,

            ClimateHealthRule
                    climateHealthRule

    ) {

        this.structureAnalysisEngine =
                Objects.requireNonNull(
                        structureAnalysisEngine,
                        "StructureAnalysisEngine이 필요합니다."
                );

        this.climateEnvironmentAnalyzer =
                Objects.requireNonNull(
                        climateEnvironmentAnalyzer,
                        "ClimateEnvironmentAnalyzer가 필요합니다."
                );

        this.climateAdjustmentNeedAnalyzer =
                Objects.requireNonNull(
                        climateAdjustmentNeedAnalyzer,
                        "ClimateAdjustmentNeedAnalyzer가 필요합니다."
                );

        this.climateElementCandidateAnalyzer =
                Objects.requireNonNull(
                        climateElementCandidateAnalyzer,
                        "ClimateElementCandidateAnalyzer가 필요합니다."
                );

        this.pathologyAnalyzer =
                Objects.requireNonNull(
                        pathologyAnalyzer,
                        "PathologyAnalyzer가 필요합니다."
                );

        this.remedyAnalyzer =
                Objects.requireNonNull(
                        remedyAnalyzer,
                        "RemedyAnalyzer가 필요합니다."
                );

        this.passageNeedAnalyzer =
                Objects.requireNonNull(
                        passageNeedAnalyzer,
                        "PassageNeedAnalyzer가 필요합니다."
                );

        this.passageCandidateAnalyzer =
                Objects.requireNonNull(
                        passageCandidateAnalyzer,
                        "PassageCandidateAnalyzer가 필요합니다."
                );

        this.climateHealthIntegrationAnalyzer =
                Objects.requireNonNull(
                        climateHealthIntegrationAnalyzer,
                        "ClimateHealthIntegrationAnalyzer가 필요합니다."
                );

        this.climateHealthCrossValidationAnalyzer =
                Objects.requireNonNull(
                        climateHealthCrossValidationAnalyzer,
                        "ClimateHealthCrossValidationAnalyzer가 필요합니다."
                );

        this.climateHealthRule =
                Objects.requireNonNull(
                        climateHealthRule,
                        "ClimateHealthRule이 필요합니다."
                );
    }

    /**
     * 사주 원국부터:
     *
     * 2차
     * 3차
     * 4차
     * 5차
     * 6차
     *
     * 를 연결해서 실행한다.
     *
     * 1차 FourPillars 계산이 끝난 이후의
     * 대표 진입점이다.
     */
    public ClimateHealthAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        StructureAnalysis structure =
                structureAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        return analyze(
                structure
        );
    }

    /**
     * 5차 StructureAnalysis가
     * 이미 존재하면 6차만 실행한다.
     */
    public ClimateHealthAnalysis analyze(
            StructureAnalysis structure
    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석 결과가 필요합니다."
        );

        /*
         * ================================================
         * 6-1
         *
         * 한난조습 환경 분석
         * ================================================
         */
        ClimateEnvironmentAnalysis climateEnvironment =
                climateEnvironmentAnalyzer
                        .analyze(
                                structure
                        );

        /*
         * ================================================
         * 6-2
         *
         * 조후 필요성 판정
         * ================================================
         */
        ClimateAdjustmentNeedAnalysis climateNeed =
                climateAdjustmentNeedAnalyzer
                        .analyze(
                                climateEnvironment
                        );

        /*
         * ================================================
         * 6-3
         *
         * 조후 우선 오행 후보
         * ================================================
         */
        ClimateElementCandidateAnalysis climateCandidates =
                climateElementCandidateAnalyzer
                        .analyze(
                                climateNeed
                        );

        /*
         * ================================================
         * 6-4
         *
         * 병(病) 구조 탐지
         * ================================================
         */
        PathologyAnalysis pathology =
                pathologyAnalyzer
                        .analyze(
                                structure,
                                climateCandidates
                        );

        /*
         * ================================================
         * 6-5
         *
         * 약(藥) 후보 추출
         * ================================================
         */
        RemedyAnalysis remedy =
                remedyAnalyzer
                        .analyze(
                                structure,
                                climateCandidates,
                                pathology
                        );

        /*
         * ================================================
         * 6-6
         *
         * 통관 필요 구조 탐지
         * ================================================
         */
        PassageNeedAnalysis passageNeed =
                passageNeedAnalyzer
                        .analyze(
                                structure,
                                remedy
                        );

        /*
         * ================================================
         * 6-7
         *
         * 통관 오행 후보
         * ================================================
         */
        PassageCandidateAnalysis passageCandidates =
                passageCandidateAnalyzer
                        .analyze(
                                passageNeed,
                                remedy
                        );

        /*
         * ================================================
         * 6-8
         *
         * 조후·병약·통관 통합
         * ================================================
         */
        ClimateHealthIntegrationAnalysis integration =
                climateHealthIntegrationAnalyzer
                        .analyze(
                                climateCandidates,
                                remedy,
                                passageCandidates
                        );

        /*
         * ================================================
         * 6-9
         *
         * 신강신약·격국 교차검증
         * ================================================
         */
        ClimateHealthCrossValidationAnalysis validation =
                climateHealthCrossValidationAnalyzer
                        .analyze(
                                structure,
                                integration
                        );

        /*
         * ================================================
         * 6-10
         *
         * 최종 상태
         * ================================================
         */
        ClimateHealthStatus status =
                climateHealthRule
                        .determineStatus(
                                validation
                        );

        return new ClimateHealthAnalysis(
                structure.dayMaster(),
                structure
                        .dayMasterStrengthAnalysis()
                        .dayMasterElement(),
                structure.monthBranch(),
                status,
                climateEnvironment,
                climateNeed,
                climateCandidates,
                pathology,
                remedy,
                passageNeed,
                passageCandidates,
                integration,
                validation,
                structure.completeChart()
        );
    }
}