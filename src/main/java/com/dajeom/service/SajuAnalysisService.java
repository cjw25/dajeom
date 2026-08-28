package com.dajeom.service;

import com.dajeom.adapter.ConsultationStoryGenerator;
import com.dajeom.analyzer.ConsultationCategoryEvidenceAnalyzer;
import com.dajeom.analyzer.ConsultationGroundingAnalyzer;
import com.dajeom.analyzer.ConsultationGroundingPolicyAnalyzer;
import com.dajeom.analyzer.ConsultationInterpretationPromptContextAnalyzer;
import com.dajeom.analyzer.ConsultationNarrativePlanAnalyzer;
import com.dajeom.analyzer.ConsultationRequestEvidenceAnalyzer;
import com.dajeom.analyzer.ConsultationRequestScopeAnalyzer;
import com.dajeom.analyzer.ConsultationTimelineRangeAnalyzer;
import com.dajeom.analyzer.ConsultationUserContextAnalyzer;
import com.dajeom.calculator.KoreanLunarCalendarCalculator;
import com.dajeom.constant.SajuConstants;
import com.dajeom.dto.AnnualLuckResponse;
import com.dajeom.dto.ConsultationPersonalizationResponse;
import com.dajeom.dto.ConsultationSectionResponse;
import com.dajeom.dto.ConsultationSectionResponse.ConsultationSectionStatus;
import com.dajeom.dto.DecadeLuckPeriodResponse;
import com.dajeom.dto.DecadeLuckResponse;
import com.dajeom.dto.PillarResponse;
import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.engine.AnnualFunctionalBaselinePipelineEngine;
import com.dajeom.engine.AnnualLuckAnalysisEngine;
import com.dajeom.engine.DecadeLuckAnalysisEngine;
import com.dajeom.engine.FourPillarsCalculationEngine;
import com.dajeom.engine.LifeDomainAnalysisEngine;
import com.dajeom.engine.LuckIntegratedAnalysisEngine;
import com.dajeom.engine.MonthlyFunctionalActivationPipelineEngine;
import com.dajeom.engine.NatalChartAdvancedAnalysisEngine;
import com.dajeom.engine.NatalChartAnalysisEngine;
import com.dajeom.engine.NatalPersonalityStatementContractPipelineEngine;
import com.dajeom.engine.PalaceAnalysisEngine;
import com.dajeom.engine.ShinsalAnalysisEngine;
import com.dajeom.engine.TenGodCompositeAnalysisEngine;
import com.dajeom.engine.TwelveLifeStageAnalysisEngine;
import com.dajeom.model.AnnualFunctionalBaselineAnalysis;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckTenGodEvidence;
import com.dajeom.model.BirthDateValue;
import com.dajeom.model.BirthTimeContext;
import com.dajeom.model.ConsultationCategoryEvidenceAnalysis;
import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingPolicyAnalysis;
import com.dajeom.model.ConsultationInterpretationPromptContext;
import com.dajeom.model.ConsultationInterpretationPromptSection;
import com.dajeom.model.ConsultationNarrativePlan;
import com.dajeom.model.ConsultationRequestEvidenceAnalysis;
import com.dajeom.model.ConsultationRequestScopeAnalysis;
import com.dajeom.model.ConsultationStoryGeneration;
import com.dajeom.model.ConsultationTimelineRangeAnalysis;
import com.dajeom.model.ConsultationUserContext;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.FourPillars;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainEvidenceResolution;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.model.MonthlyFunctionalActivationAnalysis;
import com.dajeom.model.MonthlyLuckAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.NatalPersonalityStatementContract;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.PalaceCrossContext;
import com.dajeom.model.Pillar;
import com.dajeom.model.SexagenaryPillar;
import com.dajeom.model.ShinsalAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.model.TwelveLifeStageAnalysis;
import com.dajeom.resolver.AnnualDecadeLuckCrossResolver;
import com.dajeom.resolver.AnnualDecadeLuckCrossResolverFactory;
import com.dajeom.resolver.AnnualLuckContextResolver;
import com.dajeom.resolver.AnnualLuckContextResolverFactory;
import com.dajeom.resolver.AnnualLuckNatalCrossResolver;
import com.dajeom.resolver.AnnualLuckNatalCrossResolverFactory;
import com.dajeom.resolver.AnnualLuckTenGodSourceResolver;
import com.dajeom.resolver.DecadeLuckContextResolver;
import com.dajeom.resolver.DecadeLuckContextResolverFactory;
import com.dajeom.resolver.DecadeLuckNatalCrossResolver;
import com.dajeom.resolver.DecadeLuckNatalCrossResolverFactory;
import com.dajeom.resolver.DecadeLuckTenGodSourceResolver;
import com.dajeom.resolver.LifeDomainAnnualFunctionalSourceResolver;
import com.dajeom.resolver.LifeDomainDecadeContextSourceResolver;
import com.dajeom.resolver.LifeDomainEvidenceResolver;
import com.dajeom.resolver.LifeDomainMonthlyFunctionalSourceResolver;
import com.dajeom.resolver.LifeDomainNatalEvidenceSourceResolver;
import com.dajeom.resolver.LifeDomainNatalEvidenceSourceResolverFactory;
import com.dajeom.resolver.LuckIntegratedLayerAnalysisResolver;
import com.dajeom.resolver.LuckIntegratedLayerAnalysisResolverFactory;
import com.dajeom.resolver.PalaceCrossContextAdapter;
import com.dajeom.resolver.PalaceTenGodSourceResolver;
import com.dajeom.resolver.ShinsalNatalPointSourceResolver;
import com.dajeom.type.CalendarType;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.FiveElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 사주 전체 분석 Application Service.
 *
 * =================================================
 * 공식 deterministic 실행선
 * =================================================
 *
 * 1 ~ 12차 원국
 *
 * ↓
 *
 * Personality Direct-Natal Pipeline
 *
 * ↓
 *
 * 13차 대운
 *
 * ↓
 *
 * 16차
 * 원국 + 대운 + 세운 + 월운 통합 Timeline
 *
 * ↓
 *
 * 17차
 * 신살 보조 Evidence
 *
 * ↓
 *
 * Annual Functional Baseline
 *
 * ↓
 *
 * Monthly Functional Activation
 *
 * ↓
 *
 * 18차
 * LifeDomain Evidence / Rule Match
 *
 * ↓
 *
 * 19차
 * ConsultationCategory / Request Evidence Routing
 *
 * ↓
 *
 * Timeline Range
 *
 * ↓
 *
 * 20차
 * AI Grounding
 * +
 * Personality Direct-Natal Contract
 * +
 * Grounding Policy
 * +
 * Interpretation Prompt Context
 *
 * ↓
 *
 * ConsultationStoryGenerator
 *
 * ↓
 *
 * ConsultationSectionResponse
 *
 * =================================================
 * 공식 Meaning Anchor
 * =================================================
 *
 * ConsultationInterpretationPromptContext가
 * 외부 LLM Adapter의 공식 deterministic 입력 Anchor다.
 *
 * 외부 LLM 결과는:
 *
 * - storyText
 * - storyEmphasis
 *
 * 에만 연결한다.
 *
 * =================================================
 * 제거한 Legacy Meaning 실행선
 * =================================================
 *
 * 다음 실행선은 더 이상 사용하지 않는다.
 *
 * LuckInteractionAnalysisEngine
 *
 * ->
 *
 * InterpretationSignalAnalysisEngine
 *
 * ->
 *
 * ConsultationAnalysisEngine
 *
 * ->
 *
 * ConsultationRule
 *
 * 이 경로에서 생성되던:
 *
 * - supportScore
 * - cautionScore
 * - polarity
 * - intensity
 * - confidence
 * - summaryMessage
 * - guidanceMessage
 * - narrationText
 *
 * 는 최종 상담 의미로 사용하지 않는다.
 *
 * =================================================
 * Story 실패 처리
 * =================================================
 *
 * 외부 LLM Story가 없다고 해서
 * Legacy numeric 상담문으로 fallback하지 않는다.
 *
 * Prompt Section이 존재하지만
 * Story Generation 결과만 없는 경우에는
 * 계산되지 않은 의미를 새로 만들지 않았다는
 * 기술적 안내만 반환한다.
 *
 * =================================================
 * 121 API 상태 계약
 * =================================================
 *
 * 프론트는 storyText 내용으로
 * Section 상태를 추측하지 않는다.
 *
 * 각 ConsultationSectionResponse에는
 * Java가 직접 판정한 status를 제공한다.
 *
 * ANALYSIS_UNAVAILABLE
 * NOT_REQUESTED
 * UNAVAILABLE
 * GENERATION_UNAVAILABLE
 * READY
 *
 * focused는 사용자 질문/Context에 따른
 * 화면 우선 강조 여부이며
 * status와 독립된 개념이다.
 */
@Service
public class SajuAnalysisService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SajuAnalysisService.class
            );

    private static final String
            STORY_GENERATION_UNAVAILABLE_MESSAGE =
            "이 장의 계산 근거는 준비되어 있지만 상담문 생성 결과가 없어 "
                    + "계산되지 않은 내용을 임의로 채우지 않았습니다.";

    private static final String
            CATEGORY_UNAVAILABLE_MESSAGE =
            "현재 계산 근거만으로는 이 분야의 독립적인 해석을 확정하지 않습니다. "
                    + "근거가 없는 내용을 추정해서 채우지 않았습니다.";

    private final KoreanLunarCalendarCalculator
            koreanLunarCalendarCalculator;

    private final FourPillarsCalculationEngine
            fourPillarsCalculationEngine;

    private final NatalChartAnalysisEngine
            natalChartAnalysisEngine;

    private final NatalChartAdvancedAnalysisEngine
            natalChartAdvancedAnalysisEngine;

    private final TenGodCompositeAnalysisEngine
            tenGodCompositeAnalysisEngine;

    private final NatalPersonalityStatementContractPipelineEngine
            natalPersonalityStatementContractPipelineEngine;

    private final TwelveLifeStageAnalysisEngine
            twelveLifeStageAnalysisEngine;

    private final PalaceAnalysisEngine
            palaceAnalysisEngine;

    private final PalaceCrossContextAdapter
            palaceCrossContextAdapter;

    private final DecadeLuckAnalysisEngine
            decadeLuckAnalysisEngine;

    private final AnnualLuckAnalysisEngine
            annualLuckAnalysisEngine;

    private final AnnualFunctionalBaselinePipelineEngine
            annualFunctionalBaselinePipelineEngine;

    private final MonthlyFunctionalActivationPipelineEngine
            monthlyFunctionalActivationPipelineEngine;

    private final LuckIntegratedAnalysisEngine
            luckIntegratedAnalysisEngine;

    private final LuckIntegratedLayerAnalysisResolverFactory
            luckIntegratedLayerAnalysisResolverFactory;

    private final ShinsalAnalysisEngine
            shinsalAnalysisEngine;

    private final ShinsalNatalPointSourceResolver
            shinsalNatalPointSourceResolver;

    private final LifeDomainEvidenceResolver
            lifeDomainEvidenceResolver;

    private final LifeDomainAnalysisEngine
            lifeDomainAnalysisEngine;

    private final LifeDomainNatalEvidenceSourceResolverFactory
            lifeDomainNatalEvidenceSourceResolverFactory;

    private final LifeDomainDecadeContextSourceResolver
            lifeDomainDecadeContextSourceResolver;

    private final ConsultationRequestScopeAnalyzer
            consultationRequestScopeAnalyzer;

    private final ConsultationCategoryEvidenceAnalyzer
            consultationCategoryEvidenceAnalyzer;

    private final ConsultationTimelineRangeAnalyzer
            consultationTimelineRangeAnalyzer;

    private final ConsultationRequestEvidenceAnalyzer
            consultationRequestEvidenceAnalyzer;

    private final ConsultationGroundingAnalyzer
            consultationGroundingAnalyzer;

    private final ConsultationGroundingPolicyAnalyzer
            consultationGroundingPolicyAnalyzer;

    private final ConsultationInterpretationPromptContextAnalyzer
            consultationInterpretationPromptContextAnalyzer;

    private final ConsultationUserContextAnalyzer
            consultationUserContextAnalyzer;

    private final ConsultationNarrativePlanAnalyzer
            consultationNarrativePlanAnalyzer;

    private final ConsultationStoryGenerator
            consultationStoryGenerator;

    private final PalaceTenGodSourceResolver
            palaceTenGodSourceResolver;

    private final DecadeLuckTenGodSourceResolver
            decadeLuckTenGodSourceResolver;

    private final DecadeLuckNatalCrossResolverFactory
            decadeLuckNatalCrossResolverFactory;

    private final DecadeLuckContextResolverFactory
            decadeLuckContextResolverFactory;

    private final AnnualLuckTenGodSourceResolver
            annualLuckTenGodSourceResolver;

    private final AnnualLuckNatalCrossResolverFactory
            annualLuckNatalCrossResolverFactory;

    private final AnnualDecadeLuckCrossResolverFactory
            annualDecadeLuckCrossResolverFactory;

    private final AnnualLuckContextResolverFactory
            annualLuckContextResolverFactory;

    public SajuAnalysisService(

            KoreanLunarCalendarCalculator
                    koreanLunarCalendarCalculator,

            FourPillarsCalculationEngine
                    fourPillarsCalculationEngine,

            NatalChartAnalysisEngine
                    natalChartAnalysisEngine,

            NatalChartAdvancedAnalysisEngine
                    natalChartAdvancedAnalysisEngine,

            TenGodCompositeAnalysisEngine
                    tenGodCompositeAnalysisEngine,

            NatalPersonalityStatementContractPipelineEngine
                    natalPersonalityStatementContractPipelineEngine,

            TwelveLifeStageAnalysisEngine
                    twelveLifeStageAnalysisEngine,

            PalaceAnalysisEngine
                    palaceAnalysisEngine,

            PalaceCrossContextAdapter
                    palaceCrossContextAdapter,

            DecadeLuckAnalysisEngine
                    decadeLuckAnalysisEngine,

            AnnualLuckAnalysisEngine
                    annualLuckAnalysisEngine,

            AnnualFunctionalBaselinePipelineEngine
                    annualFunctionalBaselinePipelineEngine,

            MonthlyFunctionalActivationPipelineEngine
                    monthlyFunctionalActivationPipelineEngine,

            LuckIntegratedAnalysisEngine
                    luckIntegratedAnalysisEngine,

            LuckIntegratedLayerAnalysisResolverFactory
                    luckIntegratedLayerAnalysisResolverFactory,

            ShinsalAnalysisEngine
                    shinsalAnalysisEngine,

            ShinsalNatalPointSourceResolver
                    shinsalNatalPointSourceResolver,

            LifeDomainEvidenceResolver
                    lifeDomainEvidenceResolver,

            LifeDomainAnalysisEngine
                    lifeDomainAnalysisEngine,

            LifeDomainNatalEvidenceSourceResolverFactory
                    lifeDomainNatalEvidenceSourceResolverFactory,

            LifeDomainDecadeContextSourceResolver
                    lifeDomainDecadeContextSourceResolver,

            ConsultationRequestScopeAnalyzer
                    consultationRequestScopeAnalyzer,

            ConsultationCategoryEvidenceAnalyzer
                    consultationCategoryEvidenceAnalyzer,

            ConsultationTimelineRangeAnalyzer
                    consultationTimelineRangeAnalyzer,

            ConsultationRequestEvidenceAnalyzer
                    consultationRequestEvidenceAnalyzer,

            ConsultationGroundingAnalyzer
                    consultationGroundingAnalyzer,

            ConsultationGroundingPolicyAnalyzer
                    consultationGroundingPolicyAnalyzer,

            ConsultationInterpretationPromptContextAnalyzer
                    consultationInterpretationPromptContextAnalyzer,

            ConsultationUserContextAnalyzer
                    consultationUserContextAnalyzer,

            ConsultationNarrativePlanAnalyzer
                    consultationNarrativePlanAnalyzer,

            ConsultationStoryGenerator
                    consultationStoryGenerator,

            PalaceTenGodSourceResolver
                    palaceTenGodSourceResolver,

            DecadeLuckTenGodSourceResolver
                    decadeLuckTenGodSourceResolver,

            DecadeLuckNatalCrossResolverFactory
                    decadeLuckNatalCrossResolverFactory,

            DecadeLuckContextResolverFactory
                    decadeLuckContextResolverFactory,

            AnnualLuckTenGodSourceResolver
                    annualLuckTenGodSourceResolver,

            AnnualLuckNatalCrossResolverFactory
                    annualLuckNatalCrossResolverFactory,

            AnnualDecadeLuckCrossResolverFactory
                    annualDecadeLuckCrossResolverFactory,

            AnnualLuckContextResolverFactory
                    annualLuckContextResolverFactory

    ) {

        this.koreanLunarCalendarCalculator =
                Objects.requireNonNull(
                        koreanLunarCalendarCalculator
                );

        this.fourPillarsCalculationEngine =
                Objects.requireNonNull(
                        fourPillarsCalculationEngine
                );

        this.natalChartAnalysisEngine =
                Objects.requireNonNull(
                        natalChartAnalysisEngine
                );

        this.natalChartAdvancedAnalysisEngine =
                Objects.requireNonNull(
                        natalChartAdvancedAnalysisEngine
                );

        this.tenGodCompositeAnalysisEngine =
                Objects.requireNonNull(
                        tenGodCompositeAnalysisEngine
                );

        this.natalPersonalityStatementContractPipelineEngine =
                Objects.requireNonNull(
                        natalPersonalityStatementContractPipelineEngine
                );

        this.twelveLifeStageAnalysisEngine =
                Objects.requireNonNull(
                        twelveLifeStageAnalysisEngine
                );

        this.palaceAnalysisEngine =
                Objects.requireNonNull(
                        palaceAnalysisEngine
                );

        this.palaceCrossContextAdapter =
                Objects.requireNonNull(
                        palaceCrossContextAdapter
                );

        this.decadeLuckAnalysisEngine =
                Objects.requireNonNull(
                        decadeLuckAnalysisEngine
                );

        this.annualLuckAnalysisEngine =
                Objects.requireNonNull(
                        annualLuckAnalysisEngine
                );

        this.annualFunctionalBaselinePipelineEngine =
                Objects.requireNonNull(
                        annualFunctionalBaselinePipelineEngine
                );

        this.monthlyFunctionalActivationPipelineEngine =
                Objects.requireNonNull(
                        monthlyFunctionalActivationPipelineEngine
                );

        this.luckIntegratedAnalysisEngine =
                Objects.requireNonNull(
                        luckIntegratedAnalysisEngine
                );

        this.luckIntegratedLayerAnalysisResolverFactory =
                Objects.requireNonNull(
                        luckIntegratedLayerAnalysisResolverFactory
                );

        this.shinsalAnalysisEngine =
                Objects.requireNonNull(
                        shinsalAnalysisEngine
                );

        this.shinsalNatalPointSourceResolver =
                Objects.requireNonNull(
                        shinsalNatalPointSourceResolver
                );

        this.lifeDomainEvidenceResolver =
                Objects.requireNonNull(
                        lifeDomainEvidenceResolver
                );

        this.lifeDomainAnalysisEngine =
                Objects.requireNonNull(
                        lifeDomainAnalysisEngine
                );

        this.lifeDomainNatalEvidenceSourceResolverFactory =
                Objects.requireNonNull(
                        lifeDomainNatalEvidenceSourceResolverFactory
                );

        this.lifeDomainDecadeContextSourceResolver =
                Objects.requireNonNull(
                        lifeDomainDecadeContextSourceResolver
                );

        this.consultationRequestScopeAnalyzer =
                Objects.requireNonNull(
                        consultationRequestScopeAnalyzer
                );

        this.consultationCategoryEvidenceAnalyzer =
                Objects.requireNonNull(
                        consultationCategoryEvidenceAnalyzer
                );

        this.consultationTimelineRangeAnalyzer =
                Objects.requireNonNull(
                        consultationTimelineRangeAnalyzer
                );

        this.consultationRequestEvidenceAnalyzer =
                Objects.requireNonNull(
                        consultationRequestEvidenceAnalyzer
                );

        this.consultationGroundingAnalyzer =
                Objects.requireNonNull(
                        consultationGroundingAnalyzer
                );

        this.consultationGroundingPolicyAnalyzer =
                Objects.requireNonNull(
                        consultationGroundingPolicyAnalyzer
                );

        this.consultationInterpretationPromptContextAnalyzer =
                Objects.requireNonNull(
                        consultationInterpretationPromptContextAnalyzer
                );

        this.consultationUserContextAnalyzer =
                Objects.requireNonNull(
                        consultationUserContextAnalyzer
                );

        this.consultationNarrativePlanAnalyzer =
                Objects.requireNonNull(
                        consultationNarrativePlanAnalyzer
                );

        this.consultationStoryGenerator =
                Objects.requireNonNull(
                        consultationStoryGenerator
                );

        this.palaceTenGodSourceResolver =
                Objects.requireNonNull(
                        palaceTenGodSourceResolver
                );

        this.decadeLuckTenGodSourceResolver =
                Objects.requireNonNull(
                        decadeLuckTenGodSourceResolver
                );

        this.decadeLuckNatalCrossResolverFactory =
                Objects.requireNonNull(
                        decadeLuckNatalCrossResolverFactory
                );

        this.decadeLuckContextResolverFactory =
                Objects.requireNonNull(
                        decadeLuckContextResolverFactory
                );

        this.annualLuckTenGodSourceResolver =
                Objects.requireNonNull(
                        annualLuckTenGodSourceResolver
                );

        this.annualLuckNatalCrossResolverFactory =
                Objects.requireNonNull(
                        annualLuckNatalCrossResolverFactory
                );

        this.annualDecadeLuckCrossResolverFactory =
                Objects.requireNonNull(
                        annualDecadeLuckCrossResolverFactory
                );

        this.annualLuckContextResolverFactory =
                Objects.requireNonNull(
                        annualLuckContextResolverFactory
                );
    }

    public SajuAnalysisResponse analyze(
            SajuAnalysisRequest request
    ) {

        log.info(
                "[SAJU-SERVICE-DIAG] analyze-start thread={}",
                Thread.currentThread().getName()
        );

        Objects.requireNonNull(
                request,
                "사주 분석 요청은 필수입니다."
        );

        log.info(
                "[SAJU-SERVICE-DIAG] normalize-birth-start"
        );

        NormalizedBirthData birthData =
                normalizeBirthData(
                        request
                );

        log.info(
                "[SAJU-SERVICE-DIAG] normalize-birth-complete"
        );

        BirthTimeContext birthTimeContext =
                createBirthTimeContext(
                        birthData
                );

        log.info(
                "[SAJU-SERVICE-DIAG] four-pillars-start"
        );

        FourPillars fourPillars =
                fourPillarsCalculationEngine
                        .calculate(
                                birthTimeContext
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] four-pillars-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] natal-chart-start"
        );

        NatalChartAnalysis natalChart =
                natalChartAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] natal-chart-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] natal-advanced-start"
        );

        NatalChartAdvancedAnalysis advancedAnalysis =
                natalChartAdvancedAnalysisEngine
                        .analyze(
                                natalChart
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] natal-advanced-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] ten-god-composite-start"
        );

        TenGodCompositeAnalysis tenGodCompositeAnalysis =
                tenGodCompositeAnalysisEngine
                        .analyzeConservatively(
                                fourPillars,

                                advancedAnalysis
                                        .elementStrengthAnalysis(),

                                advancedAnalysis
                                        .dayMasterStrengthAnalysis(),

                                advancedAnalysis
                                        .structureAnalysis(),

                                advancedAnalysis
                                        .usefulGodAnalysis(),

                                advancedAnalysis
                                        .combinationTransformationAnalysis()
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] ten-god-composite-complete"
        );

        /*
         * =================================================
         * Personality Direct-Natal Pipeline
         * =================================================
         */

        log.info(
                "[SAJU-SERVICE-DIAG] personality-contract-start"
        );

        NatalPersonalityStatementContract
                personalityStatementContract =
                natalPersonalityStatementContractPipelineEngine
                        .analyze(
                                advancedAnalysis,
                                tenGodCompositeAnalysis
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] personality-contract-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] twelve-life-stage-start"
        );

        TwelveLifeStageAnalysis twelveLifeStageAnalysis =
                twelveLifeStageAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] twelve-life-stage-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] palace-cross-context-start"
        );

        PalaceCrossContext palaceCrossContext =
                palaceCrossContextAdapter
                        .resolve(
                                advancedAnalysis
                                        .relationAnalysis(),

                                advancedAnalysis
                                        .combinationTransformationAnalysis()
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] palace-cross-context-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] palace-analysis-start"
        );

        PalaceAnalysis palaceAnalysis =
                palaceAnalysisEngine
                        .analyze(
                                fourPillars,
                                twelveLifeStageAnalysis,
                                palaceTenGodSourceResolver,
                                palaceCrossContext
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] palace-analysis-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] decade-resolver-start"
        );

        DecadeLuckNatalCrossResolver
                decadeLuckNatalCrossResolver =
                decadeLuckNatalCrossResolverFactory
                        .create(
                                advancedAnalysis,
                                palaceAnalysis
                        );

        DecadeLuckContextResolver
                decadeLuckContextResolver =
                decadeLuckContextResolverFactory
                        .create(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                palaceAnalysis
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] decade-resolver-complete"
        );

        DecadeLuckAnalysis decadeLuckAnalysis;

        log.info(
                "[SAJU-SERVICE-DIAG] decade-analysis-start knownBirthTime={}",
                birthTimeContext.hasKnownBirthTime()
        );

        if (
                birthTimeContext
                        .hasKnownBirthTime()
        ) {

            decadeLuckAnalysis =
                    decadeLuckAnalysisEngine
                            .analyze(
                                    birthData.gender(),

                                    fourPillars,

                                    birthTimeContext
                                            .requireBirthDateTime(),

                                    birthData.zoneId(),

                                    palaceAnalysis,

                                    decadeLuckTenGodSourceResolver,

                                    decadeLuckNatalCrossResolver,

                                    decadeLuckContextResolver
                            );

        } else {

            decadeLuckAnalysis =
                    decadeLuckAnalysisEngine
                            .analyzeUnknownTime(
                                    birthData.gender(),

                                    fourPillars,

                                    birthData.solarBirthDate(),

                                    birthData.zoneId(),

                                    palaceAnalysis,

                                    decadeLuckTenGodSourceResolver,

                                    decadeLuckNatalCrossResolver,

                                    decadeLuckContextResolver
                            );
        }

        log.info(
                "[SAJU-SERVICE-DIAG] decade-analysis-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] integrated-luck-start"
        );

        Optional<LuckIntegratedAnalysis>
                luckIntegratedAnalysis =
                analyzeIntegratedLuckIfRequested(
                        request,
                        birthData,
                        advancedAnalysis,
                        tenGodCompositeAnalysis,
                        palaceAnalysis,
                        decadeLuckAnalysis
                );

        log.info(
                "[SAJU-SERVICE-DIAG] integrated-luck-complete present={}",
                luckIntegratedAnalysis.isPresent()
        );

        log.info(
                "[SAJU-SERVICE-DIAG] shinsal-start"
        );

        Optional<ShinsalAnalysis>
                shinsalAnalysis =
                analyzeShinsalIfIntegrated(
                        luckIntegratedAnalysis
                );

        log.info(
                "[SAJU-SERVICE-DIAG] shinsal-complete present={}",
                shinsalAnalysis.isPresent()
        );

        if (
                luckIntegratedAnalysis.isPresent()
                        != shinsalAnalysis.isPresent()
        ) {

            throw new IllegalStateException(
                    "16차 LuckIntegratedAnalysis와 17차 ShinsalAnalysis 생성 상태가 일치하지 않습니다."
            );
        }

        log.info(
                "[SAJU-SERVICE-DIAG] life-domain-start"
        );

        Optional<LifeDomainAnalysis>
                lifeDomainAnalysis =
                analyzeLifeDomainIfIntegrated(
                        luckIntegratedAnalysis,
                        shinsalAnalysis,
                        advancedAnalysis,
                        tenGodCompositeAnalysis,
                        palaceAnalysis
                );

        log.info(
                "[SAJU-SERVICE-DIAG] life-domain-complete present={}",
                lifeDomainAnalysis.isPresent()
        );

        if (
                luckIntegratedAnalysis.isPresent()
                        != lifeDomainAnalysis.isPresent()
        ) {

            throw new IllegalStateException(
                    "16차 LuckIntegratedAnalysis와 18차 LifeDomainAnalysis 생성 상태가 일치하지 않습니다."
            );
        }

        if (
                shinsalAnalysis.isPresent()
                        != lifeDomainAnalysis.isPresent()
        ) {

            throw new IllegalStateException(
                    "17차 ShinsalAnalysis와 18차 LifeDomainAnalysis 생성 상태가 일치하지 않습니다."
            );
        }

        log.info(
                "[SAJU-SERVICE-DIAG] consultation-scope-start"
        );

        Optional<ConsultationRequestScopeAnalysis>
                consultationRequestScopeAnalysis =
                analyzeConsultationRequestScopeIfAvailable(
                        request,
                        lifeDomainAnalysis
                );

        log.info(
                "[SAJU-SERVICE-DIAG] consultation-scope-complete present={}",
                consultationRequestScopeAnalysis.isPresent()
        );

        if (
                lifeDomainAnalysis.isPresent()
                        != consultationRequestScopeAnalysis.isPresent()
        ) {

            throw new IllegalStateException(
                    "18차 LifeDomainAnalysis와 19차 ConsultationRequestScopeAnalysis 생성 상태가 일치하지 않습니다."
            );
        }

        log.info(
                "[SAJU-SERVICE-DIAG] timeline-range-start"
        );

        Optional<ConsultationTimelineRangeAnalysis>
                consultationTimelineRangeAnalysis =
                analyzeConsultationTimelineRangeIfRequested(
                        request,
                        birthData,
                        advancedAnalysis,
                        tenGodCompositeAnalysis,
                        palaceAnalysis,
                        decadeLuckAnalysis,
                        lifeDomainAnalysis,
                        consultationRequestScopeAnalysis
                );

        log.info(
                "[SAJU-SERVICE-DIAG] timeline-range-complete present={}",
                consultationTimelineRangeAnalysis.isPresent()
        );

        log.info(
                "[SAJU-SERVICE-DIAG] request-evidence-start"
        );

        Optional<ConsultationRequestEvidenceAnalysis>
                consultationRequestEvidenceAnalysis =
                analyzeConsultationRequestEvidenceIfAvailable(
                        consultationRequestScopeAnalysis,
                        lifeDomainAnalysis,
                        consultationTimelineRangeAnalysis
                );

        log.info(
                "[SAJU-SERVICE-DIAG] request-evidence-complete present={}",
                consultationRequestEvidenceAnalysis.isPresent()
        );

        if (
                lifeDomainAnalysis.isPresent()
                        != consultationRequestEvidenceAnalysis.isPresent()
        ) {

            throw new IllegalStateException(
                    "18차 LifeDomainAnalysis와 19차 ConsultationRequestEvidenceAnalysis 생성 상태가 일치하지 않습니다."
            );
        }

        log.info(
                "[SAJU-SERVICE-DIAG] prompt-context-start"
        );

        Optional<ConsultationInterpretationPromptContext>
                interpretationPromptContext =
                analyzeInterpretationPromptContextIfAvailable(
                        consultationRequestEvidenceAnalysis,
                        personalityStatementContract
                );

        log.info(
                "[SAJU-SERVICE-DIAG] prompt-context-complete present={}",
                interpretationPromptContext.isPresent()
        );

        if (
                consultationRequestEvidenceAnalysis.isPresent()
                        != interpretationPromptContext.isPresent()
        ) {

            throw new IllegalStateException(
                    "19차 ConsultationRequestEvidenceAnalysis와 20차 ConsultationInterpretationPromptContext 생성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 상담 전 사용자 Context 결합 경계
         * =================================================
         *
         * 이 지점까지 원국/대운/세운/LifeDomain/Category Deterministic
         * Judgment 및 Prompt Context가 모두 확정되어 있다.
         *
         * 관계 상태, 직업 상태, 자유 질문은 여기서 처음 결합하며
         * 앞선 명리 계산값을 절대 변경하지 않는다.
         */

        log.info(
                "[SAJU-SERVICE-DIAG] user-context-start"
        );

        ConsultationUserContext consultationUserContext =
                consultationUserContextAnalyzer
                        .analyze(
                                request
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] user-context-complete hasContext={}",
                consultationUserContext.hasAnyContext()
        );

        Optional<ConsultationNarrativePlan> narrativePlan =
                interpretationPromptContext
                        .map(
                                promptContext ->
                                        consultationNarrativePlanAnalyzer
                                                .analyze(
                                                        promptContext,
                                                        consultationUserContext
                                                )
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] narrative-plan-complete present={}",
                narrativePlan.isPresent()
        );

        log.info(
                "[SAJU-SERVICE-DIAG] story-generation-start"
        );

        Optional<ConsultationStoryGeneration>
                consultationStoryGeneration =
                generateConsultationStoriesIfAvailable(
                        narrativePlan
                );

        log.info(
                "[SAJU-SERVICE-DIAG] story-generation-complete present={}",
                consultationStoryGeneration.isPresent()
        );

        /*
         * =================================================
         * API 응답용 Annual Luck
         * =================================================
         *
         * Legacy InterpretationSignal 생성을 위한 것이 아니다.
         *
         * 현재 SajuAnalysisResponse의 annualLuck 필드를 위해
         * targetYear AnnualLuckAnalysis를 계산한다.
         */

        log.info(
                "[SAJU-SERVICE-DIAG] annual-response-analysis-start"
        );

        AnnualLuckNatalCrossResolver
                annualLuckNatalCrossResolver =
                annualLuckNatalCrossResolverFactory
                        .create(
                                advancedAnalysis,
                                palaceAnalysis
                        );

        AnnualDecadeLuckCrossResolver
                annualDecadeLuckCrossResolver =
                annualDecadeLuckCrossResolverFactory
                        .create(
                                advancedAnalysis
                        );

        AnnualLuckContextResolver
                annualLuckContextResolver =
                annualLuckContextResolverFactory
                        .create(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                palaceAnalysis
                        );

        AnnualLuckAnalysis annualLuckAnalysis =
                annualLuckAnalysisEngine
                        .analyze(
                                request.targetYear(),
                                birthData.zoneId(),
                                palaceAnalysis,
                                decadeLuckAnalysis,
                                annualLuckTenGodSourceResolver,
                                annualLuckNatalCrossResolver,
                                annualDecadeLuckCrossResolver,
                                annualLuckContextResolver
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] annual-response-analysis-complete"
        );

        ZonedDateTime targetDateTime =
                resolveDecadeReferenceDateTime(
                        birthData,
                        luckIntegratedAnalysis
                );

        log.info(
                "[SAJU-SERVICE-DIAG] response-create-start"
        );

        SajuAnalysisResponse response =
                createResponse(
                        birthData,
                        fourPillars,
                        advancedAnalysis,
                        decadeLuckAnalysis,
                        annualLuckAnalysis,
                        interpretationPromptContext,
                        narrativePlan,
                        consultationStoryGeneration,
                        request.targetYear(),
                        targetDateTime
                );

        log.info(
                "[SAJU-SERVICE-DIAG] response-create-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] analyze-complete"
        );

        return response;
    }

    private Optional<ConsultationStoryGeneration>
    generateConsultationStoriesIfAvailable(

            Optional<ConsultationNarrativePlan>
                    narrativePlan

    ) {

        Objects.requireNonNull(
                narrativePlan,
                "외부 LLM 문장화에는 ConsultationNarrativePlan Optional이 필요합니다."
        );

        if (
                narrativePlan.isEmpty()
        ) {

            return Optional.empty();
        }

        ConsultationNarrativePlan plan =
                narrativePlan
                        .orElseThrow();

        if (
                !plan.hasSections()
        ) {

            return Optional.empty();
        }

        log.info(
                "[SAJU-SERVICE-DIAG] story-generator-adapter-call-start"
        );

        Optional<ConsultationStoryGeneration> generated =
                consultationStoryGenerator
                        .generate(
                                plan
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] story-generator-adapter-call-complete present={}",
                generated.isPresent()
        );

        if (
                generated.isPresent()
                        &&
                        generated
                                .orElseThrow()
                                .promptContext()
                                != plan.promptContext()
        ) {

            throw new IllegalStateException(
                    "외부 LLM Story Generation이 현재 Narrative Plan의 Prompt Context Identity를 보존하지 않았습니다."
            );
        }

        return generated;
    }

    private Optional<ConsultationInterpretationPromptContext>
    analyzeInterpretationPromptContextIfAvailable(

            Optional<ConsultationRequestEvidenceAnalysis>
                    requestEvidenceAnalysis,

            NatalPersonalityStatementContract
                    personalityStatementContract

    ) {

        Objects.requireNonNull(
                requestEvidenceAnalysis,
                "20차에는 ConsultationRequestEvidenceAnalysis Optional이 필요합니다."
        );

        Objects.requireNonNull(
                personalityStatementContract,
                "20차 Personality Grounding에는 NatalPersonalityStatementContract가 필요합니다."
        );

        if (
                requestEvidenceAnalysis.isEmpty()
        ) {

            return Optional.empty();
        }

        ConsultationRequestEvidenceAnalysis
                resolvedRequestEvidence =
                requestEvidenceAnalysis
                        .orElseThrow();

        log.info(
                "[SAJU-SERVICE-DIAG] grounding-analyzer-start"
        );

        ConsultationGroundingAnalysis
                groundingAnalysis =
                Objects.requireNonNull(
                        consultationGroundingAnalyzer
                                .analyze(
                                        resolvedRequestEvidence,
                                        personalityStatementContract
                                ),
                        "ConsultationGroundingAnalyzer가 null을 반환했습니다."
                );

        log.info(
                "[SAJU-SERVICE-DIAG] grounding-analyzer-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] grounding-policy-start"
        );

        ConsultationGroundingPolicyAnalysis
                policyAnalysis =
                Objects.requireNonNull(
                        consultationGroundingPolicyAnalyzer
                                .analyze(
                                        groundingAnalysis
                                ),
                        "ConsultationGroundingPolicyAnalyzer가 null을 반환했습니다."
                );

        log.info(
                "[SAJU-SERVICE-DIAG] grounding-policy-complete"
        );

        if (
                policyAnalysis
                        .groundingAnalysis()
                        != groundingAnalysis
        ) {

            throw new IllegalStateException(
                    "20차 ConsultationGroundingPolicyAnalysis가 현재 ConsultationGroundingAnalysis Identity를 보존하지 않았습니다."
            );
        }

        log.info(
                "[SAJU-SERVICE-DIAG] prompt-context-analyzer-start"
        );

        ConsultationInterpretationPromptContext
                promptContext =
                Objects.requireNonNull(
                        consultationInterpretationPromptContextAnalyzer
                                .analyze(
                                        policyAnalysis
                                ),
                        "ConsultationInterpretationPromptContextAnalyzer가 null을 반환했습니다."
                );

        log.info(
                "[SAJU-SERVICE-DIAG] prompt-context-analyzer-complete"
        );

        return Optional.of(
                promptContext
        );
    }

    private Optional<ConsultationRequestScopeAnalysis>
    analyzeConsultationRequestScopeIfAvailable(

            SajuAnalysisRequest request,

            Optional<LifeDomainAnalysis>
                    lifeDomainAnalysis

    ) {

        Objects.requireNonNull(
                request,
                "19차 요청 범위 분석에는 SajuAnalysisRequest가 필요합니다."
        );

        Objects.requireNonNull(
                lifeDomainAnalysis,
                "19차 요청 범위 분석에는 LifeDomainAnalysis Optional이 필요합니다."
        );

        if (
                lifeDomainAnalysis.isEmpty()
        ) {

            return Optional.empty();
        }

        ConsultationRequestScopeAnalysis requestScope =
                Objects.requireNonNull(
                        consultationRequestScopeAnalyzer
                                .analyze(
                                        request
                                ),
                        "ConsultationRequestScopeAnalyzer가 null을 반환했습니다."
                );

        return Optional.of(
                requestScope
        );
    }

    private Optional<ConsultationTimelineRangeAnalysis>
    analyzeConsultationTimelineRangeIfRequested(

            SajuAnalysisRequest request,

            NormalizedBirthData birthData,

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            Optional<LifeDomainAnalysis>
                    lifeDomainAnalysis,

            Optional<ConsultationRequestScopeAnalysis>
                    requestScopeAnalysis

    ) {

        Objects.requireNonNull(
                request,
                "21차 Timeline Range에는 SajuAnalysisRequest가 필요합니다."
        );

        Objects.requireNonNull(
                birthData,
                "21차 Timeline Range에는 NormalizedBirthData가 필요합니다."
        );

        Objects.requireNonNull(
                advancedAnalysis,
                "21차 Timeline Range에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "21차 Timeline Range에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "21차 Timeline Range에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "21차 Timeline Range에는 DecadeLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                lifeDomainAnalysis,
                "21차 Timeline Range에는 LifeDomainAnalysis Optional이 필요합니다."
        );

        Objects.requireNonNull(
                requestScopeAnalysis,
                "21차 Timeline Range에는 ConsultationRequestScopeAnalysis Optional이 필요합니다."
        );

        if (
                lifeDomainAnalysis.isEmpty()
                        &&
                        requestScopeAnalysis.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                lifeDomainAnalysis.isEmpty()
                        ||
                        requestScopeAnalysis.isEmpty()
        ) {

            throw new IllegalStateException(
                    "21차 Timeline Range 실행 조건의 LifeDomainAnalysis와 ConsultationRequestScopeAnalysis 상태가 일치하지 않습니다."
            );
        }

        ConsultationRequestScopeAnalysis requestScope =
                requestScopeAnalysis
                        .orElseThrow();

        boolean pastRequested =
                requestScope
                        .selectedCategories()
                        .contains(
                                ConsultationCategory.PAST_LUCK
                        );

        boolean futureRequested =
                requestScope
                        .selectedCategories()
                        .contains(
                                ConsultationCategory.FUTURE_LUCK
                        );

        if (
                !pastRequested
                        &&
                        !futureRequested
        ) {

            return Optional.empty();
        }

        LocalDate currentReferenceDate =
                request
                        .luckReferenceDateOptional()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "PAST_LUCK/FUTURE_LUCK Timeline Range에는 luckReferenceDate가 필요합니다."
                                        )
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] timeline-range-analyzer-start"
        );

        ConsultationTimelineRangeAnalysis analysis =
                Objects.requireNonNull(
                        consultationTimelineRangeAnalyzer
                                .analyze(
                                        currentReferenceDate,
                                        birthData.zoneId(),
                                        advancedAnalysis,
                                        tenGodCompositeAnalysis,
                                        palaceAnalysis,
                                        decadeLuckAnalysis
                                ),
                        "ConsultationTimelineRangeAnalyzer가 null을 반환했습니다."
                );

        log.info(
                "[SAJU-SERVICE-DIAG] timeline-range-analyzer-complete"
        );

        if (
                pastRequested
                        &&
                        !analysis.hasPastRange()
        ) {

            throw new IllegalStateException(
                    "PAST_LUCK이 요청되었지만 과거 Timeline Range가 생성되지 않았습니다."
            );
        }

        if (
                futureRequested
                        &&
                        !analysis.hasFutureRange()
        ) {

            throw new IllegalStateException(
                    "FUTURE_LUCK이 요청되었지만 미래 Timeline Range가 생성되지 않았습니다."
            );
        }

        return Optional.of(
                analysis
        );
    }

    private Optional<ConsultationRequestEvidenceAnalysis>
    analyzeConsultationRequestEvidenceIfAvailable(

            Optional<ConsultationRequestScopeAnalysis>
                    requestScopeAnalysis,

            Optional<LifeDomainAnalysis>
                    lifeDomainAnalysis,

            Optional<ConsultationTimelineRangeAnalysis>
                    timelineRangeAnalysis

    ) {

        Objects.requireNonNull(
                requestScopeAnalysis,
                "19차에는 ConsultationRequestScopeAnalysis Optional이 필요합니다."
        );

        Objects.requireNonNull(
                lifeDomainAnalysis,
                "19차에는 LifeDomainAnalysis Optional이 필요합니다."
        );

        Objects.requireNonNull(
                timelineRangeAnalysis,
                "19차에는 ConsultationTimelineRangeAnalysis Optional이 필요합니다."
        );

        if (
                requestScopeAnalysis.isEmpty()
                        &&
                        lifeDomainAnalysis.isEmpty()
        ) {

            if (
                    timelineRangeAnalysis.isPresent()
            ) {

                throw new IllegalStateException(
                        "18차/19차 입력이 없는데 21차 Timeline Range만 존재할 수 없습니다."
                );
            }

            return Optional.empty();
        }

        if (
                requestScopeAnalysis.isEmpty()
                        ||
                        lifeDomainAnalysis.isEmpty()
        ) {

            throw new IllegalStateException(
                    "19차 ConsultationRequestScopeAnalysis와 LifeDomainAnalysis 생성 상태가 일치하지 않습니다."
            );
        }

        ConsultationRequestScopeAnalysis requestScope =
                requestScopeAnalysis
                        .orElseThrow();

        LifeDomainAnalysis resolvedLifeDomainAnalysis =
                lifeDomainAnalysis
                        .orElseThrow();

        log.info(
                "[SAJU-SERVICE-DIAG] category-evidence-start"
        );

        ConsultationCategoryEvidenceAnalysis
                categoryEvidenceAnalysis =
                Objects.requireNonNull(
                        consultationCategoryEvidenceAnalyzer
                                .analyze(
                                        resolvedLifeDomainAnalysis,
                                        timelineRangeAnalysis
                                                .orElse(
                                                        null
                                                )
                                ),
                        "ConsultationCategoryEvidenceAnalyzer가 null을 반환했습니다."
                );

        log.info(
                "[SAJU-SERVICE-DIAG] category-evidence-complete"
        );

        log.info(
                "[SAJU-SERVICE-DIAG] request-evidence-analyzer-start"
        );

        ConsultationRequestEvidenceAnalysis
                requestEvidenceAnalysis =
                Objects.requireNonNull(
                        consultationRequestEvidenceAnalyzer
                                .analyze(
                                        requestScope,
                                        categoryEvidenceAnalysis
                                ),
                        "ConsultationRequestEvidenceAnalyzer가 null을 반환했습니다."
                );

        log.info(
                "[SAJU-SERVICE-DIAG] request-evidence-analyzer-complete"
        );

        return Optional.of(
                requestEvidenceAnalysis
        );
    }

    private Optional<LifeDomainAnalysis>
    analyzeLifeDomainIfIntegrated(

            Optional<LuckIntegratedAnalysis>
                    luckIntegratedAnalysis,

            Optional<ShinsalAnalysis>
                    shinsalAnalysis,

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis

    ) {

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "18차에는 LuckIntegratedAnalysis Optional이 필요합니다."
        );

        Objects.requireNonNull(
                shinsalAnalysis,
                "18차에는 ShinsalAnalysis Optional이 필요합니다."
        );

        Objects.requireNonNull(
                advancedAnalysis,
                "18차에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "18차에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "18차에는 PalaceAnalysis가 필요합니다."
        );

        if (
                luckIntegratedAnalysis.isEmpty()
                        &&
                        shinsalAnalysis.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                luckIntegratedAnalysis.isEmpty()
                        ||
                        shinsalAnalysis.isEmpty()
        ) {

            throw new IllegalStateException(
                    "18차 분석에는 16차 LuckIntegratedAnalysis와 17차 ShinsalAnalysis가 모두 필요합니다."
            );
        }

        LuckIntegratedAnalysis integratedAnalysis =
                luckIntegratedAnalysis
                        .orElseThrow();

        ShinsalAnalysis resolvedShinsalAnalysis =
                shinsalAnalysis
                        .orElseThrow();

        if (
                resolvedShinsalAnalysis
                        .luckIntegratedAnalysis()
                        != integratedAnalysis
        ) {

            throw new IllegalArgumentException(
                    "18차 입력의 LuckIntegratedAnalysis와 ShinsalAnalysis Anchor가 일치하지 않습니다."
            );
        }

        if (
                integratedAnalysis
                        .natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "18차 입력 LuckIntegratedAnalysis가 현재 PalaceAnalysis를 참조하지 않습니다."
            );
        }

        LifeDomainNatalEvidenceSourceResolver
                natalEvidenceSourceResolver =
                lifeDomainNatalEvidenceSourceResolverFactory
                        .create(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                palaceAnalysis
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] annual-functional-baseline-start"
        );

        Map<
                AnnualLuckAnalysis,
                AnnualFunctionalBaselineAnalysis
                > annualFunctionalBaselines =
                annualFunctionalBaselinePipelineEngine
                        .analyze(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                integratedAnalysis
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] annual-functional-baseline-complete size={}",
                annualFunctionalBaselines.size()
        );

        log.info(
                "[SAJU-SERVICE-DIAG] monthly-functional-activation-start"
        );

        Map<
                MonthlyLuckAnalysis,
                MonthlyFunctionalActivationAnalysis
                > monthlyFunctionalActivations =
                monthlyFunctionalActivationPipelineEngine
                        .analyze(
                                integratedAnalysis,
                                annualFunctionalBaselines
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] monthly-functional-activation-complete size={}",
                monthlyFunctionalActivations.size()
        );

        LifeDomainAnnualFunctionalSourceResolver
                annualFunctionalSourceResolver =
                segmentAnalysis -> {

                    AnnualLuckAnalysis segmentAnnualLuckAnalysis =
                            segmentAnalysis
                                    .annualLuckAnalysis();

                    AnnualFunctionalBaselineAnalysis baseline =
                            annualFunctionalBaselines
                                    .get(
                                            segmentAnnualLuckAnalysis
                                    );

                    if (
                            baseline == null
                    ) {

                        throw new IllegalStateException(
                                "18차 Annual Functional Baseline이 Timeline Segment에 존재하지 않습니다. "
                                        + "targetYear="
                                        + segmentAnnualLuckAnalysis.targetYear()
                        );
                    }

                    if (
                            baseline
                                    .annualLuckAnalysis()
                                    != segmentAnnualLuckAnalysis
                    ) {

                        throw new IllegalStateException(
                                "18차 Annual Functional Baseline이 Timeline AnnualLuckAnalysis Identity를 보존하지 않았습니다. "
                                        + "targetYear="
                                        + segmentAnnualLuckAnalysis.targetYear()
                        );
                    }

                    return Optional.of(
                            baseline
                    );
                };

        LifeDomainMonthlyFunctionalSourceResolver
                monthlyFunctionalSourceResolver =
                segmentAnalysis -> {

                    MonthlyLuckAnalysis segmentMonthlyLuckAnalysis =
                            segmentAnalysis
                                    .monthlyLuckAnalysis();

                    MonthlyFunctionalActivationAnalysis activation =
                            monthlyFunctionalActivations
                                    .get(
                                            segmentMonthlyLuckAnalysis
                                    );

                    if (
                            activation == null
                    ) {

                        throw new IllegalStateException(
                                "18차 Monthly Functional Activation이 Timeline Segment에 존재하지 않습니다. "
                                        + "sajuYear="
                                        + segmentMonthlyLuckAnalysis.sajuYear()
                                        + ", monthIndex="
                                        + segmentMonthlyLuckAnalysis.monthIndex()
                        );
                    }

                    if (
                            activation
                                    .monthlyLuckAnalysis()
                                    != segmentMonthlyLuckAnalysis
                    ) {

                        throw new IllegalStateException(
                                "18차 Monthly Functional Activation이 Timeline MonthlyLuckAnalysis Identity를 보존하지 않았습니다. "
                                        + "sajuYear="
                                        + segmentMonthlyLuckAnalysis.sajuYear()
                                        + ", monthIndex="
                                        + segmentMonthlyLuckAnalysis.monthIndex()
                        );
                    }

                    if (
                            activation
                                    .annualFunctionalBaseline()
                                    .annualLuckAnalysis()
                                    != segmentAnalysis
                                    .annualLuckAnalysis()
                    ) {

                        throw new IllegalStateException(
                                "18차 Monthly Functional Activation의 Annual Functional Baseline이 "
                                        + "현재 Timeline AnnualLuckAnalysis Identity를 보존하지 않았습니다. "
                                        + "sajuYear="
                                        + segmentMonthlyLuckAnalysis.sajuYear()
                                        + ", monthIndex="
                                        + segmentMonthlyLuckAnalysis.monthIndex()
                        );
                    }

                    return Optional.of(
                            activation
                    );
                };

        log.info(
                "[SAJU-SERVICE-DIAG] life-domain-evidence-resolver-start"
        );

        LifeDomainEvidenceResolution evidenceResolution =
                lifeDomainEvidenceResolver
                        .resolve(
                                integratedAnalysis,
                                resolvedShinsalAnalysis,
                                natalEvidenceSourceResolver,
                                lifeDomainDecadeContextSourceResolver,
                                annualFunctionalSourceResolver,
                                monthlyFunctionalSourceResolver
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] life-domain-evidence-resolver-complete"
        );

        if (
                evidenceResolution
                        .luckIntegratedAnalysis()
                        != integratedAnalysis
        ) {

            throw new IllegalStateException(
                    "18차 LifeDomainEvidenceResolution이 현재 LuckIntegratedAnalysis를 보존하지 않았습니다."
            );
        }

        if (
                evidenceResolution
                        .shinsalAnalysis()
                        != resolvedShinsalAnalysis
        ) {

            throw new IllegalStateException(
                    "18차 LifeDomainEvidenceResolution이 현재 ShinsalAnalysis를 보존하지 않았습니다."
            );
        }

        log.info(
                "[SAJU-SERVICE-DIAG] life-domain-analysis-engine-start"
        );

        LifeDomainAnalysis analysis =
                Objects.requireNonNull(
                        lifeDomainAnalysisEngine
                                .analyze(
                                        evidenceResolution
                                ),
                        "LifeDomainAnalysisEngine이 null을 반환했습니다."
                );

        log.info(
                "[SAJU-SERVICE-DIAG] life-domain-analysis-engine-complete"
        );

        if (
                analysis
                        .evidenceResolution()
                        != evidenceResolution
        ) {

            throw new IllegalStateException(
                    "18차 LifeDomainAnalysis가 현재 LifeDomainEvidenceResolution Identity를 보존하지 않았습니다."
            );
        }

        return Optional.of(
                analysis
        );
    }

    private Optional<ShinsalAnalysis>
    analyzeShinsalIfIntegrated(

            Optional<LuckIntegratedAnalysis>
                    luckIntegratedAnalysis

    ) {

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "17차에는 LuckIntegratedAnalysis Optional이 필요합니다."
        );

        if (
                luckIntegratedAnalysis.isEmpty()
        ) {

            return Optional.empty();
        }

        LuckIntegratedAnalysis integratedAnalysis =
                luckIntegratedAnalysis
                        .orElseThrow();

        ShinsalAnalysis analysis =
                Objects.requireNonNull(
                        shinsalAnalysisEngine
                                .analyze(
                                        integratedAnalysis,
                                        shinsalNatalPointSourceResolver
                                ),
                        "ShinsalAnalysisEngine이 null을 반환했습니다."
                );

        if (
                analysis
                        .luckIntegratedAnalysis()
                        != integratedAnalysis
        ) {

            throw new IllegalStateException(
                    "17차 ShinsalAnalysis가 현재 16차 LuckIntegratedAnalysis Anchor를 보존하지 않았습니다."
            );
        }

        return Optional.of(
                analysis
        );
    }

    private Optional<LuckIntegratedAnalysis>
    analyzeIntegratedLuckIfRequested(

            SajuAnalysisRequest request,

            NormalizedBirthData birthData,

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis

    ) {

        Objects.requireNonNull(
                request,
                "16차 분석에는 SajuAnalysisRequest가 필요합니다."
        );

        Objects.requireNonNull(
                birthData,
                "16차 분석에는 NormalizedBirthData가 필요합니다."
        );

        Objects.requireNonNull(
                advancedAnalysis,
                "16차 분석에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "16차 분석에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "16차 분석에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "16차 분석에는 DecadeLuckAnalysis가 필요합니다."
        );

        if (
                !request.hasLuckReferenceDate()
        ) {

            return Optional.empty();
        }

        LuckReferenceContext referenceContext =
                createLuckReferenceContext(
                        request,
                        birthData
                );

        LuckIntegratedLayerAnalysisResolver
                layerAnalysisResolver =
                luckIntegratedLayerAnalysisResolverFactory
                        .create(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                palaceAnalysis
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] luck-integrated-engine-start"
        );

        LuckIntegratedAnalysis analysis =
                luckIntegratedAnalysisEngine
                        .analyze(
                                palaceAnalysis,
                                decadeLuckAnalysis,
                                referenceContext,
                                layerAnalysisResolver
                        );

        log.info(
                "[SAJU-SERVICE-DIAG] luck-integrated-engine-complete"
        );

        if (
                analysis.natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalStateException(
                    "16차 LuckIntegratedAnalysis가 현재 PalaceAnalysis를 보존하지 않았습니다."
            );
        }

        if (
                analysis.decadeLuckAnalysis()
                        != decadeLuckAnalysis
        ) {

            throw new IllegalStateException(
                    "16차 LuckIntegratedAnalysis가 현재 DecadeLuckAnalysis를 보존하지 않았습니다."
            );
        }

        if (
                analysis.referenceContext()
                        != referenceContext
        ) {

            throw new IllegalStateException(
                    "16차 LuckIntegratedAnalysis가 현재 LuckReferenceContext Identity를 보존하지 않았습니다."
            );
        }

        return Optional.of(
                analysis
        );
    }

    private LuckReferenceContext createLuckReferenceContext(

            SajuAnalysisRequest request,

            NormalizedBirthData birthData

    ) {

        LocalDate referenceDate =
                request
                        .luckReferenceDateOptional()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "16차 운 분석 기준날짜가 필요합니다."
                                        )
                        );

        if (
                request.exactLuckReferenceRequested()
        ) {

            ZonedDateTime exactDateTime =
                    ZonedDateTime.of(
                            referenceDate,

                            request
                                    .luckReferenceTimeOptional()
                                    .orElseThrow(),

                            birthData.zoneId()
                    );

            return LuckReferenceContext
                    .exact(
                            exactDateTime,
                            birthData.zoneId()
                    );
        }

        if (
                request.dateOnlyLuckReferenceRequested()
        ) {

            return LuckReferenceContext
                    .dateOnly(
                            referenceDate,
                            birthData.zoneId()
                    );
        }

        throw new IllegalStateException(
                "luckReferenceDate는 존재하지만 운 분석 기준 Precision을 결정할 수 없습니다."
        );
    }

    private ZonedDateTime resolveDecadeReferenceDateTime(

            NormalizedBirthData birthData,

            Optional<LuckIntegratedAnalysis>
                    luckIntegratedAnalysis

    ) {

        Objects.requireNonNull(
                birthData,
                "대운 응답 기준에는 NormalizedBirthData가 필요합니다."
        );

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "LuckIntegratedAnalysis Optional이 필요합니다."
        );

        if (
                luckIntegratedAnalysis.isPresent()
        ) {

            LuckReferenceContext referenceContext =
                    luckIntegratedAnalysis
                            .orElseThrow()
                            .referenceContext();

            if (
                    referenceContext.exact()
            ) {

                return referenceContext
                        .requireExactDateTime();
            }
        }

        return ZonedDateTime.now(
                birthData.zoneId()
        );
    }

    private BirthTimeContext createBirthTimeContext(
            NormalizedBirthData birthData
    ) {

        if (
                birthData.hasKnownBirthTime()
        ) {

            return BirthTimeContext.known(
                    birthData.solarBirthDate(),
                    birthData.birthTime(),
                    birthData.zoneId()
            );
        }

        return BirthTimeContext.unknown(
                birthData.solarBirthDate(),
                birthData.zoneId()
        );
    }

    private NormalizedBirthData normalizeBirthData(
            SajuAnalysisRequest request
    ) {

        Objects.requireNonNull(
                request.calendarType(),
                "양력/음력 구분은 필수입니다."
        );

        Objects.requireNonNull(
                request.birthDate(),
                "생년월일은 필수입니다."
        );

        Objects.requireNonNull(
                request.gender(),
                "성별은 필수입니다."
        );

        Objects.requireNonNull(
                request.targetYear(),
                "분석 대상 연도는 필수입니다."
        );

        BirthDateValue originalBirthDate =
                BirthDateValue.parse(
                        request.birthDate()
                );

        if (
                request.birthTimeUnknown()
                        &&
                        request.birthTime() != null
        ) {

            throw new IllegalArgumentException(
                    "출생시간 미상인 경우 birthTime은 비워야 합니다."
            );
        }

        if (
                !request.birthTimeUnknown()
                        &&
                        request.birthTime() == null
        ) {

            throw new IllegalArgumentException(
                    "출생시간을 알고 있는 경우 birthTime은 필수입니다."
            );
        }

        LocalDate solarBirthDate;

        if (
                request.calendarType()
                        == CalendarType.SOLAR
        ) {

            if (
                    request.leapMonth()
            ) {

                throw new IllegalArgumentException(
                        "양력 입력에는 윤달을 사용할 수 없습니다."
                );
            }

            solarBirthDate =
                    originalBirthDate
                            .toGregorianDate();

        } else {

            solarBirthDate =
                    koreanLunarCalendarCalculator
                            .toSolar(
                                    originalBirthDate,
                                    request.leapMonth()
                            );
        }

        LocalDate today =
                LocalDate.now(
                        SajuConstants.DEFAULT_ZONE_ID
                );

        if (
                solarBirthDate.isAfter(
                        today
                )
        ) {

            throw new IllegalArgumentException(
                    "생년월일은 미래 날짜일 수 없습니다."
            );
        }

        return new NormalizedBirthData(
                request.calendarType(),
                originalBirthDate,
                solarBirthDate,
                request.birthTime(),
                request.birthTimeUnknown(),
                request.gender(),
                request.leapMonth(),
                SajuConstants.DEFAULT_ZONE_ID
        );
    }

    private SajuAnalysisResponse createResponse(

            NormalizedBirthData birthData,

            FourPillars fourPillars,

            NatalChartAdvancedAnalysis advancedAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis,

            Optional<ConsultationInterpretationPromptContext>
                    interpretationPromptContext,

            Optional<ConsultationNarrativePlan>
                    narrativePlan,

            Optional<ConsultationStoryGeneration>
                    consultationStoryGeneration,

            int targetYear,

            ZonedDateTime targetDateTime

    ) {

        List<ConsultationSectionResponse>
                consultationResponses =
                createConsultationResponses(
                        interpretationPromptContext,
                        narrativePlan,
                        consultationStoryGeneration
                );

        ConsultationPersonalizationResponse personalizationResponse =
                createConsultationPersonalizationResponse(
                        narrativePlan
                );

        String fullNarrationText =
                createFullNarrationText(
                        consultationResponses
                );

        return new SajuAnalysisResponse(
                birthData.calendarType(),

                birthData
                        .birthDate()
                        .isoFormat(),

                birthData.solarBirthDate(),

                birthData.birthTime(),

                birthData.birthTimeUnknown(),

                birthData.gender(),

                birthData.leapMonth(),

                targetYear,

                toPillarResponse(
                        fourPillars.yearPillar()
                ),

                toPillarResponse(
                        fourPillars.monthPillar()
                ),

                toPillarResponse(
                        fourPillars.dayPillar()
                ),

                fourPillars.hasHourPillar()
                        ? toPillarResponse(
                        fourPillars.hourPillar()
                )
                        : null,

                fourPillars
                        .dayMaster()
                        .getKoreanName(),

                fourPillars
                        .dayMaster()
                        .getChineseCharacter(),

                advancedAnalysis
                        .dayMasterStrengthAnalysis()
                        .levelKorean(),

                advancedAnalysis
                        .structureAnalysis()
                        .primaryNameKorean(),

                toElementNames(
                        advancedAnalysis
                                .usefulGodAnalysis()
                                .relationAnalysis()
                                .usefulGodElements()
                ),

                toElementNames(
                        advancedAnalysis
                                .usefulGodAnalysis()
                                .relationAnalysis()
                                .favorableGodElements()
                ),

                toElementNames(
                        advancedAnalysis
                                .usefulGodAnalysis()
                                .relationAnalysis()
                                .unfavorableGodElements()
                ),

                toElementNames(
                        selectedClimateElements(
                                advancedAnalysis
                        )
                ),

                toElementNames(
                        selectedPassageElements(
                                advancedAnalysis
                        )
                ),

                toDecadeLuckResponse(
                        decadeLuckAnalysis,
                        targetDateTime
                ),

                toAnnualLuckResponse(
                        annualLuckAnalysis
                ),

                personalizationResponse,

                consultationResponses,

                fullNarrationText
        );
    }

    /*
     * =================================================
     * Deterministic Consultation API Response
     * =================================================
     *
     * 모든 ConsultationCategory의
     * API Section 구조는 유지한다.
     *
     * 그러나 상담 의미는
     * ConsultationStoryGeneration의 storyText만
     * 사용한다.
     *
     * Legacy ConsultationSection은 사용하지 않는다.
     *
     * 121부터는 각 Section의 상태를
     * storyText 문자열로 추측하지 않고
     * ConsultationSectionStatus로 Java가 직접 판정한다.
     */

    private List<ConsultationSectionResponse>
    createConsultationResponses(

            Optional<ConsultationInterpretationPromptContext>
                    interpretationPromptContext,

            Optional<ConsultationNarrativePlan>
                    narrativePlan,

            Optional<ConsultationStoryGeneration>
                    consultationStoryGeneration

    ) {

        Objects.requireNonNull(
                interpretationPromptContext,
                "Consultation API Response에는 ConsultationInterpretationPromptContext Optional이 필요합니다."
        );

        Objects.requireNonNull(
                narrativePlan,
                "Consultation API Response에는 ConsultationNarrativePlan Optional이 필요합니다."
        );

        Objects.requireNonNull(
                consultationStoryGeneration,
                "Consultation API Response에는 ConsultationStoryGeneration Optional이 필요합니다."
        );

        List<ConsultationCategory> orderedCategories;

        if (
                narrativePlan.isPresent()
        ) {

            List<ConsultationCategory> focusedOrder =
                    narrativePlan
                            .orElseThrow()
                            .sections()
                            .stream()
                            .map(
                                    section ->
                                            section.category()
                            )
                            .toList();

            List<ConsultationCategory> remaining =
                    Arrays
                            .stream(
                                    ConsultationCategory.values()
                            )
                            .filter(
                                    category ->
                                            !focusedOrder.contains(
                                                    category
                                            )
                            )
                            .sorted(
                                    Comparator.comparingInt(
                                            ConsultationCategory::getOrder
                                    )
                            )
                            .toList();

            List<ConsultationCategory> merged =
                    new ArrayList<>(
                            focusedOrder
                    );

            merged.addAll(
                    remaining
            );

            orderedCategories =
                    List.copyOf(
                            merged
                    );

        } else {

            orderedCategories =
                    Arrays
                            .stream(
                                    ConsultationCategory.values()
                            )
                            .sorted(
                                    Comparator.comparingInt(
                                            ConsultationCategory::getOrder
                                    )
                            )
                            .toList();
        }

        List<ConsultationCategory> focusedCategories =
                narrativePlan
                        .map(
                                ConsultationNarrativePlan::focusedCategories
                        )
                        .orElse(
                                List.of()
                        );

        List<ConsultationSectionResponse> responses =
                new ArrayList<>();

        int displayOrder =
                1;

        for (
                ConsultationCategory category
                : orderedCategories
        ) {

            ConsultationSectionStatus status =
                    resolveConsultationSectionStatus(
                            category,
                            interpretationPromptContext,
                            consultationStoryGeneration
                    );

            responses.add(
                    new ConsultationSectionResponse(
                            category.getOrder(),

                            displayOrder++,

                            focusedCategories.contains(
                                    category
                            ),

                            category,

                            category.getKoreanName(),

                            status,

                            resolveStoryText(
                                    category,
                                    status,
                                    consultationStoryGeneration
                            ),

                            resolveStoryEmphasis(
                                    category,
                                    status,
                                    consultationStoryGeneration
                            )
                    )
            );
        }

        return List.copyOf(
                responses
        );
    }

    /*
     * =================================================
     * Consultation Section Status
     * =================================================
     *
     * 프론트가 storyText 내용을 해석해서
     * 상태를 추측하지 않도록
     * Java가 공식 상태를 결정한다.
     */

    private ConsultationSectionStatus
    resolveConsultationSectionStatus(

            ConsultationCategory category,

            Optional<ConsultationInterpretationPromptContext>
                    interpretationPromptContext,

            Optional<ConsultationStoryGeneration>
                    consultationStoryGeneration

    ) {

        Objects.requireNonNull(
                category,
                "Consultation Section Status 판정에는 ConsultationCategory가 필요합니다."
        );

        Objects.requireNonNull(
                interpretationPromptContext,
                "Consultation Section Status 판정에는 ConsultationInterpretationPromptContext Optional이 필요합니다."
        );

        Objects.requireNonNull(
                consultationStoryGeneration,
                "Consultation Section Status 판정에는 ConsultationStoryGeneration Optional이 필요합니다."
        );

        /*
         * 상담용 deterministic Prompt Context 자체가 없다.
         */
        if (
                interpretationPromptContext.isEmpty()
        ) {

            return ConsultationSectionStatus
                    .ANALYSIS_UNAVAILABLE;
        }

        ConsultationInterpretationPromptContext promptContext =
                interpretationPromptContext
                        .orElseThrow();

        Optional<ConsultationInterpretationPromptSection> promptSection =
                promptContext
                        .findSection(
                                category
                        );

        /*
         * 전체 11개 API 구조에는 존재하지만
         * 이번 요청의 Prompt Section이 아니다.
         */
        if (
                promptSection.isEmpty()
        ) {

            return ConsultationSectionStatus
                    .NOT_REQUESTED;
        }

        /*
         * 요청됐지만 deterministic Meaning 근거상
         * 독립적인 사용자 해석을 확정하지 않는다.
         */
        if (
                promptSection
                        .orElseThrow()
                        .unavailable()
        ) {

            return ConsultationSectionStatus
                    .UNAVAILABLE;
        }

        /*
         * Prompt Section은 available이지만
         * Story Generation 자체가 없다.
         */
        if (
                consultationStoryGeneration.isEmpty()
        ) {

            return ConsultationSectionStatus
                    .GENERATION_UNAVAILABLE;
        }

        ConsultationStoryGeneration generation =
                consultationStoryGeneration
                        .orElseThrow();

        /*
         * 실제 검증된 Story가 존재할 때만 READY.
         */
        return generation
                .storyTextOf(
                        category
                )
                .filter(
                        story ->
                                !story.isBlank()
                )
                .isPresent()
                ? ConsultationSectionStatus.READY
                : ConsultationSectionStatus.GENERATION_UNAVAILABLE;
    }

    private ConsultationPersonalizationResponse
    createConsultationPersonalizationResponse(

            Optional<ConsultationNarrativePlan> narrativePlan

    ) {

        Objects.requireNonNull(
                narrativePlan,
                "ConsultationPersonalizationResponse에는 Narrative Plan Optional이 필요합니다."
        );

        if (
                narrativePlan.isEmpty()
        ) {

            return new ConsultationPersonalizationResponse(
                    true,
                    "",
                    com.dajeom.type.ConsultationRelationshipTarget.UNSPECIFIED,
                    com.dajeom.type.ConsultationRelationshipStatus.UNSPECIFIED,
                    com.dajeom.type.ConsultationWorkStatus.UNSPECIFIED,
                    "",
                    List.of()
            );
        }

        ConsultationNarrativePlan plan =
                narrativePlan
                        .orElseThrow();

        ConsultationUserContext context =
                plan.userContext();

        return new ConsultationPersonalizationResponse(
                true,
                plan.planVersion(),
                context.relationshipTarget(),
                context.relationshipStatus(),
                context.workStatus(),
                context
                        .consultationQuestion()
                        .orElse(
                                ""
                        ),
                plan.focusedCategories()
        );
    }

    /*
     * =================================================
     * Story Text
     * =================================================
     *
     * 절대 Legacy narrationText로 fallback하지 않는다.
     *
     * 121부터는 status를 먼저 확정하고
     * 그 상태에 맞는 Text만 반환한다.
     */

    private String resolveStoryText(

            ConsultationCategory category,

            ConsultationSectionStatus status,

            Optional<ConsultationStoryGeneration>
                    consultationStoryGeneration

    ) {

        Objects.requireNonNull(
                category,
                "Story Text 조회에는 ConsultationCategory가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "Story Text 조회에는 ConsultationSectionStatus가 필요합니다."
        );

        Objects.requireNonNull(
                consultationStoryGeneration,
                "Story Text 조회에는 ConsultationStoryGeneration Optional이 필요합니다."
        );

        return switch (
                status
                ) {

            /*
             * 분석 자체가 없거나
             * 이번 요청 대상이 아닌 Category는
             * 사용자 Story를 만들지 않는다.
             */
            case ANALYSIS_UNAVAILABLE,
                 NOT_REQUESTED ->
                    "";

            /*
             * deterministic 근거상
             * 독립적인 해석을 확정하지 않은 상태.
             */
            case UNAVAILABLE ->
                    CATEGORY_UNAVAILABLE_MESSAGE;

            /*
             * Java Meaning은 존재하지만
             * 최종 Story Generation 결과가 없다.
             */
            case GENERATION_UNAVAILABLE ->
                    STORY_GENERATION_UNAVAILABLE_MESSAGE;

            /*
             * READY는 반드시 실제 생성 Story를 가져야 한다.
             */
            case READY -> {

                ConsultationStoryGeneration generation =
                        consultationStoryGeneration
                                .orElseThrow(
                                        () ->
                                                new IllegalStateException(
                                                        "READY 상태인데 ConsultationStoryGeneration이 없습니다. category="
                                                                + category
                                                )
                                );

                String storyText =
                        generation
                                .storyTextOf(
                                        category
                                )
                                .filter(
                                        story ->
                                                !story.isBlank()
                                )
                                .map(
                                        String::trim
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalStateException(
                                                        "READY 상태인데 실제 Story Text가 없습니다. category="
                                                                + category
                                                )
                                );

                yield storyText;
            }
        };
    }

    /*
     * =================================================
     * Story Emphasis
     * =================================================
     *
     * READY 상태의 실제 Story에 대해서만
     * emphasis를 노출한다.
     */

    private List<String> resolveStoryEmphasis(

            ConsultationCategory category,

            ConsultationSectionStatus status,

            Optional<ConsultationStoryGeneration>
                    consultationStoryGeneration

    ) {

        Objects.requireNonNull(
                category,
                "Story Emphasis 조회에는 ConsultationCategory가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "Story Emphasis 조회에는 ConsultationSectionStatus가 필요합니다."
        );

        Objects.requireNonNull(
                consultationStoryGeneration,
                "Story Emphasis 조회에는 ConsultationStoryGeneration Optional이 필요합니다."
        );

        if (
                status
                        != ConsultationSectionStatus.READY
        ) {

            return List.of();
        }

        ConsultationStoryGeneration generation =
                consultationStoryGeneration
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "READY 상태인데 ConsultationStoryGeneration이 없습니다. category="
                                                        + category
                                        )
                        );

        if (
                !generation.hasStory(
                        category
                )
        ) {

            throw new IllegalStateException(
                    "READY 상태인데 Story가 존재하지 않습니다. category="
                            + category
            );
        }

        return generation.storyEmphasisOf(
                category
        );
    }

    /*
     * =================================================
     * Full Narration
     * =================================================
     *
     * Legacy ConsultationAnalysis.fullNarrationText()
     * 를 사용하지 않는다.
     *
     * 실제 API storyText만
     * Category 순서대로 연결한다.
     */

    private String createFullNarrationText(

            List<ConsultationSectionResponse>
                    consultationResponses

    ) {

        Objects.requireNonNull(
                consultationResponses,
                "Full Narration 생성에는 ConsultationSectionResponse 목록이 필요합니다."
        );

        return String.join(
                " ",
                consultationResponses
                        .stream()
                        .map(
                                ConsultationSectionResponse::storyText
                        )
                        .filter(
                                Objects::nonNull
                        )
                        .map(
                                String::trim
                        )
                        .filter(
                                text ->
                                        !text.isBlank()
                        )
                        .toList()
        );
    }

    private List<FiveElement> selectedClimateElements(
            NatalChartAdvancedAnalysis advancedAnalysis
    ) {

        return Arrays
                .stream(
                        FiveElement.values()
                )
                .filter(
                        element ->
                                advancedAnalysis
                                        .usefulGodAnalysis()
                                        .selectionAnalysis()
                                        .climateAnalysis()
                                        .candidateOf(
                                                element
                                        )
                                        .map(
                                                candidate ->
                                                        candidate.selected()
                                        )
                                        .orElse(
                                                false
                                        )
                )
                .toList();
    }

    private List<FiveElement> selectedPassageElements(
            NatalChartAdvancedAnalysis advancedAnalysis
    ) {

        return Arrays
                .stream(
                        FiveElement.values()
                )
                .filter(
                        element ->
                                advancedAnalysis
                                        .usefulGodAnalysis()
                                        .selectionAnalysis()
                                        .passageAnalysis()
                                        .candidateOf(
                                                element
                                        )
                                        .map(
                                                candidate ->
                                                        candidate.selected()
                                        )
                                        .orElse(
                                                false
                                        )
                )
                .toList();
    }

    private PillarResponse toPillarResponse(
            Pillar pillar
    ) {

        Objects.requireNonNull(
                pillar,
                "간지는 필수입니다."
        );

        return new PillarResponse(
                pillar.koreanName(),

                pillar.chineseName(),

                pillar
                        .stem()
                        .getKoreanName(),

                pillar
                        .stem()
                        .getChineseCharacter(),

                pillar
                        .stem()
                        .getElement(),

                pillar
                        .stem()
                        .getYinYang(),

                pillar
                        .branch()
                        .getKoreanName(),

                pillar
                        .branch()
                        .getChineseCharacter(),

                pillar
                        .branch()
                        .getElement(),

                pillar
                        .branch()
                        .getYinYang()
        );
    }

    private PillarResponse toPillarResponse(
            SexagenaryPillar pillar
    ) {

        Objects.requireNonNull(
                pillar,
                "육십갑자 간지는 필수입니다."
        );

        return toPillarResponse(
                new Pillar(
                        pillar.stem(),
                        pillar.branch()
                )
        );
    }

    private DecadeLuckResponse toDecadeLuckResponse(

            DecadeLuckAnalysis analysis,

            ZonedDateTime referenceDateTime

    ) {

        Objects.requireNonNull(
                analysis,
                "대운 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                referenceDateTime,
                "대운 조회 기준시각이 필요합니다."
        );

        List<DecadeLuckPeriodResponse> periodResponses =
                analysis
                        .periods()
                        .stream()
                        .map(
                                this::toDecadeLuckPeriodResponse
                        )
                        .toList();

        DecadeLuckPeriodResponse exactCurrentPeriod =
                analysis
                        .exactPeriodAt(
                                referenceDateTime
                        )
                        .map(
                                this::toDecadeLuckPeriodResponse
                        )
                        .orElse(
                                null
                        );

        DecadeLuckPeriodResponse definiteCurrentPeriod =
                analysis
                        .definitePeriodAt(
                                referenceDateTime
                        )
                        .map(
                                this::toDecadeLuckPeriodResponse
                        )
                        .orElse(
                                null
                        );

        List<DecadeLuckPeriodResponse> possibleCurrentPeriods =
                analysis
                        .possiblePeriodsAt(
                                referenceDateTime
                        )
                        .stream()
                        .map(
                                this::toDecadeLuckPeriodResponse
                        )
                        .toList();

        DecadeLuckPeriod firstPeriod =
                analysis
                        .periods()
                        .isEmpty()
                        ? null
                        : analysis
                        .periods()
                        .get(
                                0
                        );

        boolean definitelyBeforeFirst =
                firstPeriod != null
                        &&
                        referenceDateTime
                                .isBefore(
                                        firstPeriod
                                                .earliestStartDateTime()
                                );

        boolean possiblyBeforeFirst =
                firstPeriod != null
                        &&
                        referenceDateTime
                                .isBefore(
                                        firstPeriod
                                                .latestStartDateTime()
                                );

        return new DecadeLuckResponse(
                analysis
                        .direction()
                        .koreanName(),

                analysis.sequenceAvailable(),

                analysis.analysisAvailable(),

                analysis.exactTiming(),

                analysis.birthTimeUnknown(),

                analysis
                        .minimumStartAge()
                        .displayName(),

                analysis
                        .maximumStartAge()
                        .displayName(),

                analysis.startAgeRange(),

                analysis.solarTermBoundaryUncertain(),

                periodResponses,

                exactCurrentPeriod,

                definiteCurrentPeriod,

                possibleCurrentPeriods,

                definitelyBeforeFirst,

                possiblyBeforeFirst,

                analysis
                        .unavailableReason()
                        .orElse(
                                null
                        )
        );
    }

    private DecadeLuckPeriodResponse
    toDecadeLuckPeriodResponse(
            DecadeLuckPeriod period
    ) {

        Objects.requireNonNull(
                period,
                "대운 Period가 필요합니다."
        );

        return new DecadeLuckPeriodResponse(
                period.sequenceNumber(),

                toPillarResponse(
                        period.pillar()
                ),

                period
                        .minimumStartAge()
                        .displayName(),

                period
                        .maximumStartAge()
                        .displayName(),

                period.earliestStartDateTime(),

                period.latestStartDateTime(),

                period.earliestEndDateTimeExclusive(),

                period.latestEndDateTimeExclusive(),

                period.exactTiming()
        );
    }

    private AnnualLuckResponse toAnnualLuckResponse(
            AnnualLuckAnalysis analysis
    ) {

        Objects.requireNonNull(
                analysis,
                "세운 분석 결과가 필요합니다."
        );

        AnnualLuckTenGodEvidence visibleEvidence =
                analysis
                        .visibleTenGodEvidence()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "세운 천간 십성 Evidence가 없습니다."
                                        )
                        );

        AnnualLuckTenGodEvidence mainHiddenEvidence =
                analysis
                        .branchMainTenGodEvidence()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "세운 지지 본기 십성 Evidence가 없습니다."
                                        )
                        );

        return new AnnualLuckResponse(
                analysis.targetYear(),

                toPillarResponse(
                        analysis.annualPillar()
                ),

                analysis.startDateTimeInclusive(),

                analysis.endDateTimeExclusive(),

                visibleEvidence
                        .tenGodType()
                        .koreanName(),

                mainHiddenEvidence
                        .sourceStem()
                        .getKoreanName(),

                mainHiddenEvidence
                        .sourceStem()
                        .getChineseCharacter(),

                mainHiddenEvidence
                        .tenGodType()
                        .koreanName()
        );
    }

    private List<String> toElementNames(
            Collection<FiveElement> elements
    ) {

        if (
                elements == null
                        ||
                        elements.isEmpty()
        ) {

            return List.of();
        }

        return elements
                .stream()
                .map(
                        FiveElement::getKoreanName
                )
                .toList();
    }
}