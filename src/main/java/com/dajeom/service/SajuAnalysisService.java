package com.dajeom.service;

import com.dajeom.adapter.ConsultationStoryGenerator;
import com.dajeom.analyzer.ConsultationCategoryEvidenceAnalyzer;
import com.dajeom.analyzer.ConsultationGroundingAnalyzer;
import com.dajeom.analyzer.ConsultationGroundingPolicyAnalyzer;
import com.dajeom.analyzer.ConsultationInterpretationPromptContextAnalyzer;
import com.dajeom.analyzer.ConsultationRequestEvidenceAnalyzer;
import com.dajeom.analyzer.ConsultationRequestScopeAnalyzer;
import com.dajeom.analyzer.ConsultationTimelineRangeAnalyzer;
import com.dajeom.calculator.KoreanLunarCalendarCalculator;
import com.dajeom.constant.SajuConstants;
import com.dajeom.dto.AnnualLuckResponse;
import com.dajeom.dto.ConsultationSectionResponse;
import com.dajeom.dto.DecadeLuckPeriodResponse;
import com.dajeom.dto.DecadeLuckResponse;
import com.dajeom.dto.PillarResponse;
import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.engine.AnnualLuckAnalysisEngine;
import com.dajeom.engine.ConsultationAnalysisEngine;
import com.dajeom.engine.DecadeLuckAnalysisEngine;
import com.dajeom.engine.FourPillarsCalculationEngine;
import com.dajeom.engine.InterpretationSignalAnalysisEngine;
import com.dajeom.engine.LifeDomainAnalysisEngine;
import com.dajeom.engine.LuckIntegratedAnalysisEngine;
import com.dajeom.engine.LuckInteractionAnalysisEngine;
import com.dajeom.engine.NatalChartAdvancedAnalysisEngine;
import com.dajeom.engine.NatalChartAnalysisEngine;
import com.dajeom.engine.PalaceAnalysisEngine;
import com.dajeom.engine.ShinsalAnalysisEngine;
import com.dajeom.engine.TenGodCompositeAnalysisEngine;
import com.dajeom.engine.TwelveLifeStageAnalysisEngine;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckTenGodEvidence;
import com.dajeom.model.BirthDateValue;
import com.dajeom.model.BirthTimeContext;
import com.dajeom.model.ConsultationAnalysis;
import com.dajeom.model.ConsultationCategoryEvidenceAnalysis;
import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingPolicyAnalysis;
import com.dajeom.model.ConsultationInterpretationPromptContext;
import com.dajeom.model.ConsultationInterpretationPromptSection;
import com.dajeom.model.ConsultationRequestEvidenceAnalysis;
import com.dajeom.model.ConsultationRequestScopeAnalysis;
import com.dajeom.model.ConsultationSection;
import com.dajeom.model.ConsultationStoryGeneration;
import com.dajeom.model.ConsultationTimelineRangeAnalysis;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.FourPillars;
import com.dajeom.model.InterpretationSignalAnalysis;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainEvidenceResolution;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalChartAnalysis;
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
import com.dajeom.resolver.LifeDomainDecadeContextSourceResolver;
import com.dajeom.resolver.LifeDomainEvidenceResolver;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 사주 전체 분석 Application Service.
 *
 * =================================================
 *
 * deterministic 실행선:
 *
 * 1 ~ 12차 원국
 *
 * ↓
 *
 * 13차 대운
 *
 * ↓
 *
 * luckReferenceDate 존재 시
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
 * 20차
 * AI Grounding
 * +
 * Grounding Policy
 * +
 * Interpretation Prompt Context
 *
 * =================================================
 *
 * ConsultationInterpretationPromptContext가
 * 외부 LLM Adapter의 공식 deterministic 입력 Anchor다.
 *
 * =================================================
 *
 * 외부 LLM 결과는:
 *
 * - storyText
 * - storyEmphasis
 *
 * 에만 연결한다.
 *
 * storyEmphasis는 새로운 사주 계산값이 아니라
 * storyText 중 화면에서 진하게 보여줄 위치 정보다.
 *
 * 외부 LLM 실패가 deterministic 분석 전체를
 * 실패시키지 않도록 한다.
 */
@Service
public class SajuAnalysisService {

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

    private final ConsultationStoryGenerator
            consultationStoryGenerator;

    private final LuckInteractionAnalysisEngine
            luckInteractionAnalysisEngine;

    private final InterpretationSignalAnalysisEngine
            interpretationSignalAnalysisEngine;

    private final ConsultationAnalysisEngine
            consultationAnalysisEngine;

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

            ConsultationStoryGenerator
                    consultationStoryGenerator,

            LuckInteractionAnalysisEngine
                    luckInteractionAnalysisEngine,

            InterpretationSignalAnalysisEngine
                    interpretationSignalAnalysisEngine,

            ConsultationAnalysisEngine
                    consultationAnalysisEngine,

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

        this.consultationStoryGenerator =
                Objects.requireNonNull(
                        consultationStoryGenerator
                );

        this.luckInteractionAnalysisEngine =
                Objects.requireNonNull(
                        luckInteractionAnalysisEngine
                );

        this.interpretationSignalAnalysisEngine =
                Objects.requireNonNull(
                        interpretationSignalAnalysisEngine
                );

        this.consultationAnalysisEngine =
                Objects.requireNonNull(
                        consultationAnalysisEngine
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

        Objects.requireNonNull(
                request,
                "사주 분석 요청은 필수입니다."
        );

        NormalizedBirthData birthData =
                normalizeBirthData(
                        request
                );

        BirthTimeContext birthTimeContext =
                createBirthTimeContext(
                        birthData
                );

        FourPillars fourPillars =
                fourPillarsCalculationEngine
                        .calculate(
                                birthTimeContext
                        );

        NatalChartAnalysis natalChart =
                natalChartAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        NatalChartAdvancedAnalysis advancedAnalysis =
                natalChartAdvancedAnalysisEngine
                        .analyze(
                                natalChart
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

        TwelveLifeStageAnalysis twelveLifeStageAnalysis =
                twelveLifeStageAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        PalaceCrossContext palaceCrossContext =
                palaceCrossContextAdapter
                        .resolve(
                                advancedAnalysis
                                        .relationAnalysis(),

                                advancedAnalysis
                                        .combinationTransformationAnalysis()
                        );

        PalaceAnalysis palaceAnalysis =
                palaceAnalysisEngine
                        .analyze(
                                fourPillars,
                                twelveLifeStageAnalysis,
                                palaceTenGodSourceResolver,
                                palaceCrossContext
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

        DecadeLuckAnalysis decadeLuckAnalysis;

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

        Optional<ShinsalAnalysis>
                shinsalAnalysis =
                analyzeShinsalIfIntegrated(
                        luckIntegratedAnalysis
                );

        if (
                luckIntegratedAnalysis.isPresent()
                        != shinsalAnalysis.isPresent()
        ) {

            throw new IllegalStateException(
                    "16차 LuckIntegratedAnalysis와 17차 ShinsalAnalysis 생성 상태가 일치하지 않습니다."
            );
        }

        Optional<LifeDomainAnalysis>
                lifeDomainAnalysis =
                analyzeLifeDomainIfIntegrated(
                        luckIntegratedAnalysis,
                        shinsalAnalysis,
                        advancedAnalysis,
                        tenGodCompositeAnalysis,
                        palaceAnalysis
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

        Optional<ConsultationRequestScopeAnalysis>
                consultationRequestScopeAnalysis =
                analyzeConsultationRequestScopeIfAvailable(
                        request,
                        lifeDomainAnalysis
                );

        if (
                lifeDomainAnalysis.isPresent()
                        != consultationRequestScopeAnalysis.isPresent()
        ) {

            throw new IllegalStateException(
                    "18차 LifeDomainAnalysis와 19차 ConsultationRequestScopeAnalysis 생성 상태가 일치하지 않습니다."
            );
        }

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

        Optional<ConsultationRequestEvidenceAnalysis>
                consultationRequestEvidenceAnalysis =
                analyzeConsultationRequestEvidenceIfAvailable(
                        consultationRequestScopeAnalysis,
                        lifeDomainAnalysis,
                        consultationTimelineRangeAnalysis
                );

        if (
                lifeDomainAnalysis.isPresent()
                        != consultationRequestEvidenceAnalysis.isPresent()
        ) {

            throw new IllegalStateException(
                    "18차 LifeDomainAnalysis와 19차 ConsultationRequestEvidenceAnalysis 생성 상태가 일치하지 않습니다."
            );
        }

        Optional<ConsultationInterpretationPromptContext>
                interpretationPromptContext =
                analyzeInterpretationPromptContextIfAvailable(
                        consultationRequestEvidenceAnalysis
                );

        if (
                consultationRequestEvidenceAnalysis.isPresent()
                        != interpretationPromptContext.isPresent()
        ) {

            throw new IllegalStateException(
                    "19차 ConsultationRequestEvidenceAnalysis와 20차 ConsultationInterpretationPromptContext 생성 상태가 일치하지 않습니다."
            );
        }

        Optional<ConsultationStoryGeneration>
                consultationStoryGeneration =
                generateConsultationStoriesIfAvailable(
                        interpretationPromptContext
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

        ZonedDateTime targetDateTime =
                resolveLegacyDecadeReferenceDateTime(
                        birthData,
                        luckIntegratedAnalysis
                );

        LuckInteractionAnalysis luckInteractionAnalysis =
                luckInteractionAnalysisEngine
                        .analyze(
                                advancedAnalysis,
                                decadeLuckAnalysis,
                                annualLuckAnalysis
                        );

        InterpretationSignalAnalysis signalAnalysis =
                interpretationSignalAnalysisEngine
                        .analyze(
                                advancedAnalysis,
                                luckInteractionAnalysis
                        );

        ConsultationAnalysis consultationAnalysis =
                consultationAnalysisEngine
                        .analyze(
                                signalAnalysis,
                                luckInteractionAnalysis
                        );

        return createResponse(
                birthData,
                fourPillars,
                advancedAnalysis,
                decadeLuckAnalysis,
                annualLuckAnalysis,
                consultationAnalysis,
                interpretationPromptContext,
                consultationStoryGeneration,
                request.targetYear(),
                targetDateTime
        );
    }

    private Optional<ConsultationStoryGeneration>
    generateConsultationStoriesIfAvailable(

            Optional<ConsultationInterpretationPromptContext>
                    interpretationPromptContext

    ) {

        Objects.requireNonNull(
                interpretationPromptContext,
                "외부 LLM 문장화에는 ConsultationInterpretationPromptContext Optional이 필요합니다."
        );

        if (
                interpretationPromptContext.isEmpty()
        ) {

            return Optional.empty();
        }

        ConsultationInterpretationPromptContext promptContext =
                interpretationPromptContext
                        .orElseThrow();

        if (
                promptContext.categoryResolutionRequired()
                        || !promptContext.hasSections()
        ) {

            return Optional.empty();
        }

        Optional<ConsultationStoryGeneration> generated =
                consultationStoryGenerator
                        .generate(
                                promptContext
                        );

        if (
                generated.isPresent()
                        && generated
                        .orElseThrow()
                        .promptContext()
                        != promptContext
        ) {

            throw new IllegalStateException(
                    "외부 LLM Story Generation이 현재 ConsultationInterpretationPromptContext Identity를 보존하지 않았습니다."
            );
        }

        return generated;
    }

    private Optional<ConsultationInterpretationPromptContext>
    analyzeInterpretationPromptContextIfAvailable(

            Optional<ConsultationRequestEvidenceAnalysis>
                    requestEvidenceAnalysis

    ) {

        Objects.requireNonNull(
                requestEvidenceAnalysis,
                "20차에는 ConsultationRequestEvidenceAnalysis Optional이 필요합니다."
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

        ConsultationGroundingAnalysis
                groundingAnalysis =
                Objects.requireNonNull(
                        consultationGroundingAnalyzer
                                .analyze(
                                        resolvedRequestEvidence
                                ),
                        "ConsultationGroundingAnalyzer가 null을 반환했습니다."
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

        if (
                policyAnalysis
                        .groundingAnalysis()
                        != groundingAnalysis
        ) {

            throw new IllegalStateException(
                    "20차 ConsultationGroundingPolicyAnalysis가 현재 ConsultationGroundingAnalysis Identity를 보존하지 않았습니다."
            );
        }

        ConsultationInterpretationPromptContext
                promptContext =
                Objects.requireNonNull(
                        consultationInterpretationPromptContextAnalyzer
                                .analyze(
                                        policyAnalysis
                                ),
                        "ConsultationInterpretationPromptContextAnalyzer가 null을 반환했습니다."
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
                        && requestScopeAnalysis.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                lifeDomainAnalysis.isEmpty()
                        || requestScopeAnalysis.isEmpty()
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
                        && !futureRequested
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

        if (
                pastRequested
                        && !analysis.hasPastRange()
        ) {

            throw new IllegalStateException(
                    "PAST_LUCK이 요청되었지만 과거 Timeline Range가 생성되지 않았습니다."
            );
        }

        if (
                futureRequested
                        && !analysis.hasFutureRange()
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
                        && lifeDomainAnalysis.isEmpty()
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
                        || lifeDomainAnalysis.isEmpty()
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
                        && shinsalAnalysis.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                luckIntegratedAnalysis.isEmpty()
                        || shinsalAnalysis.isEmpty()
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

        LifeDomainEvidenceResolution evidenceResolution =
                lifeDomainEvidenceResolver
                        .resolve(
                                integratedAnalysis,
                                resolvedShinsalAnalysis,
                                natalEvidenceSourceResolver,
                                lifeDomainDecadeContextSourceResolver
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

        LifeDomainAnalysis analysis =
                Objects.requireNonNull(
                        lifeDomainAnalysisEngine
                                .analyze(
                                        evidenceResolution
                                ),
                        "LifeDomainAnalysisEngine이 null을 반환했습니다."
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

        LuckIntegratedAnalysis analysis =
                luckIntegratedAnalysisEngine
                        .analyze(
                                palaceAnalysis,
                                decadeLuckAnalysis,
                                referenceContext,
                                layerAnalysisResolver
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

    private LuckReferenceContext
    createLuckReferenceContext(

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

    private ZonedDateTime
    resolveLegacyDecadeReferenceDateTime(

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
                        && request.birthTime() != null
        ) {

            throw new IllegalArgumentException(
                    "출생시간 미상인 경우 birthTime은 비워야 합니다."
            );
        }

        if (
                !request.birthTimeUnknown()
                        && request.birthTime() == null
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

            ConsultationAnalysis consultationAnalysis,

            Optional<ConsultationInterpretationPromptContext>
                    interpretationPromptContext,

            Optional<ConsultationStoryGeneration>
                    consultationStoryGeneration,

            int targetYear,

            ZonedDateTime targetDateTime

    ) {

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
                        .coreAnalysis()
                        .strengthAnalysis()
                        .level()
                        .getKoreanName(),

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

                consultationAnalysis
                        .sections()
                        .stream()
                        .map(
                                section ->
                                        toConsultationResponse(
                                                section,
                                                interpretationPromptContext,
                                                consultationStoryGeneration
                                        )
                        )
                        .toList(),

                consultationAnalysis
                        .fullNarrationText()
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
                        && referenceDateTime
                        .isBefore(
                                firstPeriod
                                        .earliestStartDateTime()
                        );

        boolean possiblyBeforeFirst =
                firstPeriod != null
                        && referenceDateTime
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

    private ConsultationSectionResponse
    toConsultationResponse(

            ConsultationSection section,

            Optional<ConsultationInterpretationPromptContext>
                    interpretationPromptContext,

            Optional<ConsultationStoryGeneration>
                    consultationStoryGeneration

    ) {

        Objects.requireNonNull(
                section,
                "ConsultationSection은 필수입니다."
        );

        Objects.requireNonNull(
                interpretationPromptContext,
                "ConsultationInterpretationPromptContext Optional은 필수입니다."
        );

        Objects.requireNonNull(
                consultationStoryGeneration,
                "ConsultationStoryGeneration Optional은 필수입니다."
        );

        String storyText =
                resolveStoryText(
                        section,
                        interpretationPromptContext,
                        consultationStoryGeneration
                );

        List<String> storyEmphasis =
                resolveStoryEmphasis(
                        section,
                        interpretationPromptContext,
                        consultationStoryGeneration
                );

        return new ConsultationSectionResponse(
                section.order(),
                section.category(),
                section.title(),
                section.polarity(),
                section.intensity(),
                section.confidence(),
                section.openingMessage(),
                section.summaryMessage(),
                section.guidanceMessage(),
                section.narrationText(),
                storyText,
                storyEmphasis,
                section.basisLabels(),
                section.evidenceCount()
        );
    }

    private String resolveStoryText(

            ConsultationSection section,

            Optional<ConsultationInterpretationPromptContext>
                    interpretationPromptContext,

            Optional<ConsultationStoryGeneration>
                    consultationStoryGeneration

    ) {

        ConsultationCategory category =
                section.category();

        if (
                interpretationPromptContext.isEmpty()
        ) {

            return section.narrationText();
        }

        ConsultationInterpretationPromptContext promptContext =
                interpretationPromptContext
                        .orElseThrow();

        Optional<ConsultationInterpretationPromptSection> promptSection =
                promptContext
                        .findSection(
                                category
                        );

        if (
                promptSection.isEmpty()
        ) {

            return "";
        }

        if (
                promptSection
                        .orElseThrow()
                        .unavailable()
        ) {

            return "현재 계산 근거만으로는 이 분야의 독립적인 해석을 확정하지 않습니다. "
                    + "근거가 없는 내용을 추정해서 채우지 않았습니다.";
        }

        Optional<String> generatedStory =
                consultationStoryGeneration
                        .flatMap(
                                generation ->
                                        generation.storyTextOf(
                                                category
                                        )
                        );

        return generatedStory
                .orElse(
                        section.narrationText()
                );
    }

    private List<String> resolveStoryEmphasis(

            ConsultationSection section,

            Optional<ConsultationInterpretationPromptContext>
                    interpretationPromptContext,

            Optional<ConsultationStoryGeneration>
                    consultationStoryGeneration

    ) {

        Objects.requireNonNull(
                section,
                "ConsultationSection은 필수입니다."
        );

        Objects.requireNonNull(
                interpretationPromptContext,
                "ConsultationInterpretationPromptContext Optional은 필수입니다."
        );

        Objects.requireNonNull(
                consultationStoryGeneration,
                "ConsultationStoryGeneration Optional은 필수입니다."
        );

        if (
                interpretationPromptContext.isEmpty()
        ) {

            return List.of();
        }

        ConsultationCategory category =
                section.category();

        ConsultationInterpretationPromptContext promptContext =
                interpretationPromptContext
                        .orElseThrow();

        Optional<ConsultationInterpretationPromptSection> promptSection =
                promptContext
                        .findSection(
                                category
                        );

        if (
                promptSection.isEmpty()
        ) {

            return List.of();
        }

        if (
                promptSection
                        .orElseThrow()
                        .unavailable()
        ) {

            return List.of();
        }

        if (
                consultationStoryGeneration.isEmpty()
        ) {

            return List.of();
        }

        ConsultationStoryGeneration generation =
                consultationStoryGeneration
                        .orElseThrow();

        /*
         * 실제 생성 Story가 없는 경우에는
         * resolveStoryText()가 legacy narrationText를 사용하므로
         * LLM emphasis를 붙이지 않는다.
         */
        if (
                !generation.hasStory(
                        category
                )
        ) {

            return List.of();
        }

        return generation.storyEmphasisOf(
                category
        );
    }

    private List<String> toElementNames(
            Collection<FiveElement> elements
    ) {

        if (
                elements == null
                        || elements.isEmpty()
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