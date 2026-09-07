package com.dajeom.service;

import com.dajeom.calculation.*;
import com.dajeom.domain.*;
import com.dajeom.dto.*;
import com.dajeom.lunar.LunarSolarConverter;
import com.dajeom.rule.*;
import com.dajeom.validation.SajuValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class SajuService {

    private final SajuValidator sajuValidator;
    private final LunarSolarConverter lunarSolarConverter;
    private final FourPillarsCalculator fourPillarsCalculator;
    private final TenGodCalculator tenGodCalculator;
    private final HiddenStemCalculator hiddenStemCalculator;
    private final TwelveStageCalculator twelveStageCalculator;
    private final ElementCountCalculator elementCountCalculator;
    private final YinYangCountCalculator yinYangCountCalculator;
    private final StemRelationCalculator stemRelationCalculator;
    private final BranchRelationCalculator branchRelationCalculator;
    private final BranchGroupRelationCalculator branchGroupRelationCalculator;
    private final VoidBranchCalculator voidBranchCalculator;
    private final NayinCalculator nayinCalculator;
    private final TianyiNoblemanCalculator tianyiNoblemanCalculator;
    private final TwelveShinsalCalculator twelveShinsalCalculator;
    private final SpecialStarCalculator specialStarCalculator;
    private final DaewoonDirectionCalculator daewoonDirectionCalculator;
    private final DaewoonPillarCalculator daewoonPillarCalculator;
    private final DaewoonStartCalculator daewoonStartCalculator;

    private final NatalStructureCalculator natalStructureCalculator;
    private final MonthCommandPatternCandidateCalculator monthCommandPatternCandidateCalculator;
    private final MonthCommandPatternSelectionRule monthCommandPatternSelectionRule;
    private final PatternInteractionCalculator patternInteractionCalculator;
    private final PatternTenGodInteractionCalculator patternTenGodInteractionCalculator;
    private final PatternTenGodFoundationCalculator patternTenGodFoundationCalculator;
    private final PatternTenGodRouteCalculator patternTenGodRouteCalculator;

    private final PatternStemCombinationCalculator patternStemCombinationCalculator;
    private final PatternStemCombinationContextCalculator patternStemCombinationContextCalculator;
    private final PatternStemCombinationEffectRule patternStemCombinationEffectRule;
    private final PatternStemCombinationActivationRule patternStemCombinationActivationRule;
    private final PatternStemCombinationPreservationRule patternStemCombinationPreservationRule;

    private final PatternBranchDisruptionCalculator patternBranchDisruptionCalculator;
    private final PatternBranchCombinationCalculator patternBranchCombinationCalculator;
    private final PatternBranchDisruptionResolutionCalculator patternBranchDisruptionResolutionCalculator;
    private final PatternMonthCommandDisruptionResolutionRule patternMonthCommandDisruptionResolutionRule;

    private final PatternWealthResourceInteractionCalculator patternWealthResourceInteractionCalculator;
    private final PatternOfficerKillingsPurificationRule patternOfficerKillingsPurificationRule;
    private final PatternYangBladeCalculator patternYangBladeCalculator;
    private final PatternYangBladeKillingCombinationCalculator patternYangBladeKillingCombinationCalculator;

    private final DayMasterRootContextCalculator dayMasterRootContextCalculator;
    private final DayMasterRootInfluenceRule dayMasterRootInfluenceRule;
    private final BranchCombinationTransformationContextCalculator branchCombinationTransformationContextCalculator;
    private final BranchCombinationTransformationRule branchCombinationTransformationRule;
    private final EffectiveBranchElementCalculator effectiveBranchElementCalculator;
    private final DayMasterRootTransformationCalculator dayMasterRootTransformationCalculator;
    private final DayMasterEffectiveRootCalculator dayMasterEffectiveRootCalculator;
    private final DayMasterGroundStatusCalculator dayMasterGroundStatusCalculator;
    private final DayMasterForceStatusCalculator dayMasterForceStatusCalculator;
    private final DayMasterSpecialStructureCandidateCalculator dayMasterSpecialStructureCandidateCalculator;
    private final DayMasterStemTransformationCandidateCalculator dayMasterStemTransformationCandidateCalculator;
    private final DayMasterStemTransformationRule dayMasterStemTransformationRule;
    private final DayMasterStrengthDecisionCalculator dayMasterStrengthDecisionCalculator;

    private final PatternStrengthSensitiveContextCalculator patternStrengthSensitiveContextCalculator;
    private final PatternTenGodFoundationRelationContextCalculator patternTenGodFoundationRelationContextCalculator;
    private final PatternTenGodRouteRelationContextCalculator patternTenGodRouteRelationContextCalculator;
    private final PatternRelationContextCalculator patternRelationContextCalculator;
    private final PatternWealthResourceRelationContextCalculator patternWealthResourceRelationContextCalculator;
    private final PatternStrengthSensitiveRelationContextCalculator patternStrengthSensitiveRelationContextCalculator;
    private final PatternOfficerKillingsPurificationRelationContextCalculator patternOfficerKillingsPurificationRelationContextCalculator;

    private final DirectOfficerPatternStructuralSignalsCalculator directOfficerPatternStructuralSignalsCalculator;
    private final WealthPatternStructuralSignalsCalculator wealthPatternStructuralSignalsCalculator;
    private final ResourcePatternStructuralSignalsCalculator resourcePatternStructuralSignalsCalculator;
    private final EatingGodPatternStructuralSignalsCalculator eatingGodPatternStructuralSignalsCalculator;
    private final HurtingOfficerPatternStructuralSignalsCalculator hurtingOfficerPatternStructuralSignalsCalculator;
    private final SevenKillingsPatternStructuralSignalsCalculator sevenKillingsPatternStructuralSignalsCalculator;

    private final StandardPatternAssessmentCalculator standardPatternAssessmentCalculator;
    private final CompanionMonthPatternRule companionMonthPatternRule;
    private final NatalPatternDecisionCalculator natalPatternDecisionCalculator;
    private final NatalMyeongriProfileCalculator natalMyeongriProfileCalculator;
    private final DaewoonPeriodMyeongriFactsCalculator daewoonPeriodMyeongriFactsCalculator;
    private final LifeInterpretationPreparationService lifeInterpretationPreparationService;
    private final LifeInterpretationGenerationService lifeInterpretationGenerationService;

    public SajuService(
            SajuValidator sajuValidator,
            LunarSolarConverter lunarSolarConverter,
            FourPillarsCalculator fourPillarsCalculator,
            TenGodCalculator tenGodCalculator,
            HiddenStemCalculator hiddenStemCalculator,
            TwelveStageCalculator twelveStageCalculator,
            ElementCountCalculator elementCountCalculator,
            YinYangCountCalculator yinYangCountCalculator,
            StemRelationCalculator stemRelationCalculator,
            BranchRelationCalculator branchRelationCalculator,
            BranchGroupRelationCalculator branchGroupRelationCalculator,
            VoidBranchCalculator voidBranchCalculator,
            NayinCalculator nayinCalculator,
            TianyiNoblemanCalculator tianyiNoblemanCalculator,
            TwelveShinsalCalculator twelveShinsalCalculator,
            SpecialStarCalculator specialStarCalculator,
            DaewoonDirectionCalculator daewoonDirectionCalculator,
            DaewoonPillarCalculator daewoonPillarCalculator,
            DaewoonStartCalculator daewoonStartCalculator,
            NatalStructureCalculator natalStructureCalculator,
            MonthCommandPatternCandidateCalculator monthCommandPatternCandidateCalculator,
            MonthCommandPatternSelectionRule monthCommandPatternSelectionRule,
            PatternInteractionCalculator patternInteractionCalculator,
            PatternTenGodInteractionCalculator patternTenGodInteractionCalculator,
            PatternTenGodFoundationCalculator patternTenGodFoundationCalculator,
            PatternTenGodRouteCalculator patternTenGodRouteCalculator,
            PatternStemCombinationCalculator patternStemCombinationCalculator,
            PatternStemCombinationContextCalculator patternStemCombinationContextCalculator,
            PatternStemCombinationEffectRule patternStemCombinationEffectRule,
            PatternStemCombinationActivationRule patternStemCombinationActivationRule,
            PatternStemCombinationPreservationRule patternStemCombinationPreservationRule,
            PatternBranchDisruptionCalculator patternBranchDisruptionCalculator,
            PatternBranchCombinationCalculator patternBranchCombinationCalculator,
            PatternBranchDisruptionResolutionCalculator patternBranchDisruptionResolutionCalculator,
            PatternMonthCommandDisruptionResolutionRule patternMonthCommandDisruptionResolutionRule,
            PatternWealthResourceInteractionCalculator patternWealthResourceInteractionCalculator,
            PatternOfficerKillingsPurificationRule patternOfficerKillingsPurificationRule,
            PatternYangBladeCalculator patternYangBladeCalculator,
            PatternYangBladeKillingCombinationCalculator patternYangBladeKillingCombinationCalculator,
            DayMasterRootContextCalculator dayMasterRootContextCalculator,
            DayMasterRootInfluenceRule dayMasterRootInfluenceRule,
            BranchCombinationTransformationContextCalculator branchCombinationTransformationContextCalculator,
            BranchCombinationTransformationRule branchCombinationTransformationRule,
            EffectiveBranchElementCalculator effectiveBranchElementCalculator,
            DayMasterRootTransformationCalculator dayMasterRootTransformationCalculator,
            DayMasterEffectiveRootCalculator dayMasterEffectiveRootCalculator,
            DayMasterGroundStatusCalculator dayMasterGroundStatusCalculator,
            DayMasterForceStatusCalculator dayMasterForceStatusCalculator,
            DayMasterSpecialStructureCandidateCalculator dayMasterSpecialStructureCandidateCalculator,
            DayMasterStemTransformationCandidateCalculator dayMasterStemTransformationCandidateCalculator,
            DayMasterStemTransformationRule dayMasterStemTransformationRule,
            DayMasterStrengthDecisionCalculator dayMasterStrengthDecisionCalculator,
            PatternStrengthSensitiveContextCalculator patternStrengthSensitiveContextCalculator,
            PatternTenGodFoundationRelationContextCalculator patternTenGodFoundationRelationContextCalculator,
            PatternTenGodRouteRelationContextCalculator patternTenGodRouteRelationContextCalculator,
            PatternRelationContextCalculator patternRelationContextCalculator,
            PatternWealthResourceRelationContextCalculator patternWealthResourceRelationContextCalculator,
            PatternStrengthSensitiveRelationContextCalculator patternStrengthSensitiveRelationContextCalculator,
            PatternOfficerKillingsPurificationRelationContextCalculator patternOfficerKillingsPurificationRelationContextCalculator,
            DirectOfficerPatternStructuralSignalsCalculator directOfficerPatternStructuralSignalsCalculator,
            WealthPatternStructuralSignalsCalculator wealthPatternStructuralSignalsCalculator,
            ResourcePatternStructuralSignalsCalculator resourcePatternStructuralSignalsCalculator,
            EatingGodPatternStructuralSignalsCalculator eatingGodPatternStructuralSignalsCalculator,
            HurtingOfficerPatternStructuralSignalsCalculator hurtingOfficerPatternStructuralSignalsCalculator,
            SevenKillingsPatternStructuralSignalsCalculator sevenKillingsPatternStructuralSignalsCalculator,
            StandardPatternAssessmentCalculator standardPatternAssessmentCalculator,
            CompanionMonthPatternRule companionMonthPatternRule,
            NatalPatternDecisionCalculator natalPatternDecisionCalculator,
            NatalMyeongriProfileCalculator natalMyeongriProfileCalculator,
            DaewoonPeriodMyeongriFactsCalculator daewoonPeriodMyeongriFactsCalculator,
            LifeInterpretationPreparationService lifeInterpretationPreparationService,
            LifeInterpretationGenerationService lifeInterpretationGenerationService
    ) {
        this.sajuValidator = Objects.requireNonNull(sajuValidator, "SajuValidator가 필요합니다.");
        this.lunarSolarConverter = Objects.requireNonNull(lunarSolarConverter, "LunarSolarConverter가 필요합니다.");
        this.fourPillarsCalculator = Objects.requireNonNull(fourPillarsCalculator, "FourPillarsCalculator가 필요합니다.");
        this.tenGodCalculator = Objects.requireNonNull(tenGodCalculator, "TenGodCalculator가 필요합니다.");
        this.hiddenStemCalculator = Objects.requireNonNull(hiddenStemCalculator, "HiddenStemCalculator가 필요합니다.");
        this.twelveStageCalculator = Objects.requireNonNull(twelveStageCalculator, "TwelveStageCalculator가 필요합니다.");
        this.elementCountCalculator = Objects.requireNonNull(elementCountCalculator, "ElementCountCalculator가 필요합니다.");
        this.yinYangCountCalculator = Objects.requireNonNull(yinYangCountCalculator, "YinYangCountCalculator가 필요합니다.");
        this.stemRelationCalculator = Objects.requireNonNull(stemRelationCalculator, "StemRelationCalculator가 필요합니다.");
        this.branchRelationCalculator = Objects.requireNonNull(branchRelationCalculator, "BranchRelationCalculator가 필요합니다.");
        this.branchGroupRelationCalculator = Objects.requireNonNull(branchGroupRelationCalculator, "BranchGroupRelationCalculator가 필요합니다.");
        this.voidBranchCalculator = Objects.requireNonNull(voidBranchCalculator, "VoidBranchCalculator가 필요합니다.");
        this.nayinCalculator = Objects.requireNonNull(nayinCalculator, "NayinCalculator가 필요합니다.");
        this.tianyiNoblemanCalculator = Objects.requireNonNull(tianyiNoblemanCalculator, "TianyiNoblemanCalculator가 필요합니다.");
        this.twelveShinsalCalculator = Objects.requireNonNull(twelveShinsalCalculator, "TwelveShinsalCalculator가 필요합니다.");
        this.specialStarCalculator = Objects.requireNonNull(specialStarCalculator, "SpecialStarCalculator가 필요합니다.");
        this.daewoonDirectionCalculator = Objects.requireNonNull(daewoonDirectionCalculator, "DaewoonDirectionCalculator가 필요합니다.");
        this.daewoonPillarCalculator = Objects.requireNonNull(daewoonPillarCalculator, "DaewoonPillarCalculator가 필요합니다.");
        this.daewoonStartCalculator = Objects.requireNonNull(daewoonStartCalculator, "DaewoonStartCalculator가 필요합니다.");
        this.natalStructureCalculator = Objects.requireNonNull(natalStructureCalculator, "NatalStructureCalculator가 필요합니다.");
        this.monthCommandPatternCandidateCalculator = Objects.requireNonNull(monthCommandPatternCandidateCalculator, "MonthCommandPatternCandidateCalculator가 필요합니다.");
        this.monthCommandPatternSelectionRule = Objects.requireNonNull(monthCommandPatternSelectionRule, "MonthCommandPatternSelectionRule이 필요합니다.");
        this.patternInteractionCalculator = Objects.requireNonNull(patternInteractionCalculator, "PatternInteractionCalculator가 필요합니다.");
        this.patternTenGodInteractionCalculator = Objects.requireNonNull(patternTenGodInteractionCalculator, "PatternTenGodInteractionCalculator가 필요합니다.");
        this.patternTenGodFoundationCalculator = Objects.requireNonNull(patternTenGodFoundationCalculator, "PatternTenGodFoundationCalculator가 필요합니다.");
        this.patternTenGodRouteCalculator = Objects.requireNonNull(patternTenGodRouteCalculator, "PatternTenGodRouteCalculator가 필요합니다.");
        this.patternStemCombinationCalculator = Objects.requireNonNull(patternStemCombinationCalculator, "PatternStemCombinationCalculator가 필요합니다.");
        this.patternStemCombinationContextCalculator = Objects.requireNonNull(patternStemCombinationContextCalculator, "PatternStemCombinationContextCalculator가 필요합니다.");
        this.patternStemCombinationEffectRule = Objects.requireNonNull(patternStemCombinationEffectRule, "PatternStemCombinationEffectRule이 필요합니다.");
        this.patternStemCombinationActivationRule = Objects.requireNonNull(patternStemCombinationActivationRule, "PatternStemCombinationActivationRule이 필요합니다.");
        this.patternStemCombinationPreservationRule = Objects.requireNonNull(patternStemCombinationPreservationRule, "PatternStemCombinationPreservationRule이 필요합니다.");
        this.patternBranchDisruptionCalculator = Objects.requireNonNull(patternBranchDisruptionCalculator, "PatternBranchDisruptionCalculator가 필요합니다.");
        this.patternBranchCombinationCalculator = Objects.requireNonNull(patternBranchCombinationCalculator, "PatternBranchCombinationCalculator가 필요합니다.");
        this.patternBranchDisruptionResolutionCalculator = Objects.requireNonNull(patternBranchDisruptionResolutionCalculator, "PatternBranchDisruptionResolutionCalculator가 필요합니다.");
        this.patternMonthCommandDisruptionResolutionRule = Objects.requireNonNull(patternMonthCommandDisruptionResolutionRule, "PatternMonthCommandDisruptionResolutionRule이 필요합니다.");
        this.patternWealthResourceInteractionCalculator = Objects.requireNonNull(patternWealthResourceInteractionCalculator, "PatternWealthResourceInteractionCalculator가 필요합니다.");
        this.patternOfficerKillingsPurificationRule = Objects.requireNonNull(patternOfficerKillingsPurificationRule, "PatternOfficerKillingsPurificationRule이 필요합니다.");
        this.patternYangBladeCalculator = Objects.requireNonNull(patternYangBladeCalculator, "PatternYangBladeCalculator가 필요합니다.");
        this.patternYangBladeKillingCombinationCalculator = Objects.requireNonNull(patternYangBladeKillingCombinationCalculator, "PatternYangBladeKillingCombinationCalculator가 필요합니다.");
        this.dayMasterRootContextCalculator = Objects.requireNonNull(dayMasterRootContextCalculator, "DayMasterRootContextCalculator가 필요합니다.");
        this.dayMasterRootInfluenceRule = Objects.requireNonNull(dayMasterRootInfluenceRule, "DayMasterRootInfluenceRule이 필요합니다.");
        this.branchCombinationTransformationContextCalculator = Objects.requireNonNull(branchCombinationTransformationContextCalculator, "BranchCombinationTransformationContextCalculator가 필요합니다.");
        this.branchCombinationTransformationRule = Objects.requireNonNull(branchCombinationTransformationRule, "BranchCombinationTransformationRule이 필요합니다.");
        this.effectiveBranchElementCalculator = Objects.requireNonNull(effectiveBranchElementCalculator, "EffectiveBranchElementCalculator가 필요합니다.");
        this.dayMasterRootTransformationCalculator = Objects.requireNonNull(dayMasterRootTransformationCalculator, "DayMasterRootTransformationCalculator가 필요합니다.");
        this.dayMasterEffectiveRootCalculator = Objects.requireNonNull(dayMasterEffectiveRootCalculator, "DayMasterEffectiveRootCalculator가 필요합니다.");
        this.dayMasterGroundStatusCalculator = Objects.requireNonNull(dayMasterGroundStatusCalculator, "DayMasterGroundStatusCalculator가 필요합니다.");
        this.dayMasterForceStatusCalculator = Objects.requireNonNull(dayMasterForceStatusCalculator, "DayMasterForceStatusCalculator가 필요합니다.");
        this.dayMasterSpecialStructureCandidateCalculator = Objects.requireNonNull(dayMasterSpecialStructureCandidateCalculator, "DayMasterSpecialStructureCandidateCalculator가 필요합니다.");
        this.dayMasterStemTransformationCandidateCalculator = Objects.requireNonNull(dayMasterStemTransformationCandidateCalculator, "DayMasterStemTransformationCandidateCalculator가 필요합니다.");
        this.dayMasterStemTransformationRule = Objects.requireNonNull(dayMasterStemTransformationRule, "DayMasterStemTransformationRule이 필요합니다.");
        this.dayMasterStrengthDecisionCalculator = Objects.requireNonNull(dayMasterStrengthDecisionCalculator, "DayMasterStrengthDecisionCalculator가 필요합니다.");
        this.patternStrengthSensitiveContextCalculator = Objects.requireNonNull(patternStrengthSensitiveContextCalculator, "PatternStrengthSensitiveContextCalculator가 필요합니다.");
        this.patternTenGodFoundationRelationContextCalculator = Objects.requireNonNull(patternTenGodFoundationRelationContextCalculator, "PatternTenGodFoundationRelationContextCalculator가 필요합니다.");
        this.patternTenGodRouteRelationContextCalculator = Objects.requireNonNull(patternTenGodRouteRelationContextCalculator, "PatternTenGodRouteRelationContextCalculator가 필요합니다.");
        this.patternRelationContextCalculator = Objects.requireNonNull(patternRelationContextCalculator, "PatternRelationContextCalculator가 필요합니다.");
        this.patternWealthResourceRelationContextCalculator = Objects.requireNonNull(patternWealthResourceRelationContextCalculator, "PatternWealthResourceRelationContextCalculator가 필요합니다.");
        this.patternStrengthSensitiveRelationContextCalculator = Objects.requireNonNull(patternStrengthSensitiveRelationContextCalculator, "PatternStrengthSensitiveRelationContextCalculator가 필요합니다.");
        this.patternOfficerKillingsPurificationRelationContextCalculator = Objects.requireNonNull(patternOfficerKillingsPurificationRelationContextCalculator, "PatternOfficerKillingsPurificationRelationContextCalculator가 필요합니다.");
        this.directOfficerPatternStructuralSignalsCalculator = Objects.requireNonNull(directOfficerPatternStructuralSignalsCalculator, "DirectOfficerPatternStructuralSignalsCalculator가 필요합니다.");
        this.wealthPatternStructuralSignalsCalculator = Objects.requireNonNull(wealthPatternStructuralSignalsCalculator, "WealthPatternStructuralSignalsCalculator가 필요합니다.");
        this.resourcePatternStructuralSignalsCalculator = Objects.requireNonNull(resourcePatternStructuralSignalsCalculator, "ResourcePatternStructuralSignalsCalculator가 필요합니다.");
        this.eatingGodPatternStructuralSignalsCalculator = Objects.requireNonNull(eatingGodPatternStructuralSignalsCalculator, "EatingGodPatternStructuralSignalsCalculator가 필요합니다.");
        this.hurtingOfficerPatternStructuralSignalsCalculator = Objects.requireNonNull(hurtingOfficerPatternStructuralSignalsCalculator, "HurtingOfficerPatternStructuralSignalsCalculator가 필요합니다.");
        this.sevenKillingsPatternStructuralSignalsCalculator = Objects.requireNonNull(sevenKillingsPatternStructuralSignalsCalculator, "SevenKillingsPatternStructuralSignalsCalculator가 필요합니다.");
        this.standardPatternAssessmentCalculator = Objects.requireNonNull(standardPatternAssessmentCalculator, "StandardPatternAssessmentCalculator가 필요합니다.");
        this.companionMonthPatternRule = Objects.requireNonNull(companionMonthPatternRule, "CompanionMonthPatternRule이 필요합니다.");
        this.natalPatternDecisionCalculator = Objects.requireNonNull(natalPatternDecisionCalculator, "NatalPatternDecisionCalculator가 필요합니다.");
        this.natalMyeongriProfileCalculator = Objects.requireNonNull(natalMyeongriProfileCalculator, "NatalMyeongriProfileCalculator가 필요합니다.");
        this.daewoonPeriodMyeongriFactsCalculator = Objects.requireNonNull(
                daewoonPeriodMyeongriFactsCalculator,
                "DaewoonPeriodMyeongriFactsCalculator가 필요합니다."
        );
        this.lifeInterpretationPreparationService = Objects.requireNonNull(
                lifeInterpretationPreparationService,
                "LifeInterpretationPreparationService가 필요합니다."
        );
        this.lifeInterpretationGenerationService = Objects.requireNonNull(
                lifeInterpretationGenerationService,
                "LifeInterpretationGenerationService가 필요합니다."
        );
    }

    public SajuResponse calculate(SajuRequest request) {
        sajuValidator.validate(request);

        BirthDateTime solarBirth = normalizeBirth(request);
        FourPillars fourPillars = fourPillarsCalculator.calculate(solarBirth);
        HeavenlyStem dayStem = fourPillars.dayStem();

        Optional<HeavenlyStem> hourStem = fourPillars.hasKnownHour()
                ? Optional.of(fourPillars.hour().stem())
                : Optional.empty();

        Optional<EarthlyBranch> hourBranch = fourPillars.hasKnownHour()
                ? Optional.of(fourPillars.hour().branch())
                : Optional.empty();

        PillarAnalysisResponse yearAnalysis = analyzePillar(dayStem, fourPillars.year(), false);
        PillarAnalysisResponse monthAnalysis = analyzePillar(dayStem, fourPillars.month(), false);
        PillarAnalysisResponse dayAnalysis = analyzePillar(dayStem, fourPillars.day(), true);
        PillarAnalysisResponse hourAnalysis = fourPillars.hasKnownHour()
                ? analyzePillar(dayStem, fourPillars.hour(), false)
                : null;

        ElementCount elementCount = elementCountCalculator.calculate(fourPillars);
        YinYangCount yinYangCount = yinYangCountCalculator.calculate(fourPillars);

        List<StemRelation> natalStemRelations = stemRelationCalculator.calculate(fourPillars);
        List<StemRelationResponse> stemRelations = natalStemRelations.stream()
                .map(StemRelationResponse::from)
                .toList();

        List<BranchRelation> natalBranchRelations = branchRelationCalculator.calculate(fourPillars);
        List<BranchRelationResponse> branchRelations = natalBranchRelations.stream()
                .map(BranchRelationResponse::from)
                .toList();

        List<BranchGroupRelation> natalBranchGroupRelations = branchGroupRelationCalculator.calculate(fourPillars);
        List<BranchGroupRelationResponse> branchGroupRelations = natalBranchGroupRelations.stream()
                .map(BranchGroupRelationResponse::from)
                .toList();

        NatalStructureAnalysis natalStructure = natalStructureCalculator.analyze(
                fourPillars.year().stem(),
                fourPillars.month().stem(),
                dayStem,
                hourStem,
                fourPillars.year().branch(),
                fourPillars.month().branch(),
                fourPillars.day().branch(),
                hourBranch,
                natalStemRelations,
                natalBranchRelations,
                natalBranchGroupRelations
        );

        MonthCommandPatternCandidateAnalysis patternCandidates =
                monthCommandPatternCandidateCalculator.analyze(natalStructure.monthCommandDayMaster());

        MonthCommandPatternSelectionAnalysis patternSelection =
                monthCommandPatternSelectionRule.assess(patternCandidates);

        PatternInteractionAnalysis patternInteraction = patternInteractionCalculator.analyze(
                patternSelection,
                natalStructure.dayMasterRelations(),
                natalStemRelations,
                natalBranchRelations
        );

        PatternTenGodInteractionAnalysis tenGodInteractions =
                patternTenGodInteractionCalculator.analyze(patternInteraction);

        PatternTenGodFoundationProfile foundations =
                patternTenGodFoundationCalculator.analyze(patternInteraction, tenGodInteractions);

        PatternTenGodRouteAnalysis routes =
                patternTenGodRouteCalculator.analyze(patternInteraction, tenGodInteractions);

        PatternStemCombinationAnalysis stemCombinations =
                patternStemCombinationCalculator.analyze(tenGodInteractions);

        PatternStemCombinationContext stemCombinationContext =
                patternStemCombinationContextCalculator.analyze(stemCombinations);

        PatternStemCombinationEffectAnalysis stemCombinationEffect =
                patternStemCombinationEffectRule.assess(stemCombinationContext);

        PatternStemCombinationActivationAnalysis stemCombinationActivation =
                patternStemCombinationActivationRule.assess(stemCombinationEffect, tenGodInteractions);

        PatternStemCombinationPreservationAnalysis stemCombinationPreservation =
                patternStemCombinationPreservationRule.assess(tenGodInteractions, stemCombinationActivation);

        PatternBranchDisruptionAnalysis branchDisruptions =
                patternBranchDisruptionCalculator.analyze(patternInteraction);

        PatternBranchCombinationAnalysis branchCombinations =
                patternBranchCombinationCalculator.analyze(branchDisruptions);

        PatternBranchDisruptionResolutionAnalysis branchDisruptionResolutions =
                patternBranchDisruptionResolutionCalculator.analyze(branchDisruptions, branchCombinations);

        PatternMonthCommandDisruptionResolutionAnalysis monthCommandDisruptionResolution =
                patternMonthCommandDisruptionResolutionRule.assess(
                        branchDisruptions,
                        branchDisruptionResolutions
                );

        PatternWealthResourceInteractionAnalysis wealthResourceInteraction =
                patternWealthResourceInteractionCalculator.analyze(patternInteraction, tenGodInteractions);

        PatternOfficerKillingsPurificationAnalysis officerKillingsPurification =
                patternOfficerKillingsPurificationRule.assess(
                        tenGodInteractions,
                        foundations,
                        stemCombinationActivation
                );

        PatternYangBladeAnalysis yangBlade = patternYangBladeCalculator.analyze(tenGodInteractions);

        PatternYangBladeKillingCombinationAnalysis yangBladeKillingCombination =
                patternYangBladeKillingCombinationCalculator.analyze(
                        yangBlade,
                        tenGodInteractions,
                        branchDisruptions
                );

        DayMasterRootContextAnalysis rootContext = dayMasterRootContextCalculator.analyze(
                dayStem,
                natalStructure.dayMasterGround(),
                natalBranchRelations
        );

        List<DayMasterRootInfluenceAssessment> rootInfluences =
                buildRootInfluenceAssessments(rootContext, natalStructure.dayMasterSeason());

        List<BranchCombinationTransformationAssessment> branchTransformationAssessments =
                buildBranchTransformationAssessments(
                        branchCombinations,
                        fourPillars,
                        hourStem,
                        hourBranch,
                        natalBranchRelations
                );

        EffectiveBranchElementAnalysis effectiveBranchElements = effectiveBranchElementCalculator.analyze(
                fourPillars.year().branch(),
                fourPillars.month().branch(),
                fourPillars.day().branch(),
                hourBranch,
                branchTransformationAssessments
        );

        DayMasterRootTransformationAnalysis rootTransformation =
                dayMasterRootTransformationCalculator.analyze(
                        dayStem,
                        natalStructure.dayMasterGround(),
                        effectiveBranchElements
                );

        DayMasterEffectiveRootAnalysis effectiveRoots =
                dayMasterEffectiveRootCalculator.analyze(dayStem, rootInfluences, rootTransformation);

        DayMasterGroundStatusAnalysis groundStatus =
                dayMasterGroundStatusCalculator.analyze(dayStem, effectiveRoots);

        DayMasterForceStatusAnalysis forceStatus =
                dayMasterForceStatusCalculator.analyze(
                        dayStem,
                        natalStructure.dayMasterForceFormation()
                );

        DayMasterSpecialStructureCandidateAnalysis specialStructureCandidates =
                dayMasterSpecialStructureCandidateCalculator.analyze(
                        dayStem,
                        natalStructure.dayMasterSeason(),
                        groundStatus,
                        forceStatus
                );

        DayMasterStemTransformationCandidateAnalysis stemTransformationCandidates =
                dayMasterStemTransformationCandidateCalculator.analyze(
                        dayStem,
                        fourPillars.year().stem(),
                        fourPillars.month().stem(),
                        hourStem,
                        fourPillars.month().branch(),
                        groundStatus,
                        forceStatus
                );

        List<DayMasterStemTransformationAssessment> stemTransformations =
                dayMasterStemTransformationRule.assess(dayStem, stemTransformationCandidates);

        DayMasterStrengthDecision strengthDecision = dayMasterStrengthDecisionCalculator.analyze(
                dayStem,
                natalStructure.dayMasterSeason(),
                natalStructure.monthCommandDayMaster(),
                groundStatus,
                forceStatus,
                specialStructureCandidates,
                stemTransformations
        );

        if (strengthDecision.dayMaster() != dayStem) {
            throw new IllegalStateException(
                    "신강·신약 최종 판정의 일간이 원국 일간과 일치하지 않습니다."
            );
        }

        Optional<DayMasterStrengthAnalysis> ordinaryStrength = strengthDecision.ordinaryStrength();

        PatternStrengthSensitiveContextAnalysis strengthSensitiveContext;

        if (ordinaryStrength.isPresent()) {
            DayMasterStrengthAnalysis strength = ordinaryStrength.get();
            strengthSensitiveContext = patternStrengthSensitiveContextCalculator.analyze(
                    strength.status(),
                    foundations,
                    routes,
                    wealthResourceInteraction
            );
        } else {
            strengthSensitiveContext = PatternStrengthSensitiveContextAnalysis.notApplicable(dayStem);
        }

        PatternRelationContextBundle relationContext = PatternRelationContextBundle.unresolved();

        relationContext = patternTenGodFoundationRelationContextCalculator.apply(
                foundations,
                relationContext
        );

        relationContext = patternTenGodRouteRelationContextCalculator.apply(
                routes,
                foundations,
                relationContext
        );

        relationContext = patternRelationContextCalculator.applyStemCombinations(
                patternInteraction,
                stemCombinationContext,
                relationContext
        );

        relationContext = patternRelationContextCalculator.applyBranchDisruptions(
                patternInteraction,
                branchDisruptions,
                relationContext
        );

        relationContext = patternRelationContextCalculator.applyMonthCommandDisruptionResolution(
                patternInteraction,
                monthCommandDisruptionResolution,
                relationContext
        );

        relationContext = patternRelationContextCalculator.applyStemCombinationPreservation(
                patternInteraction,
                stemCombinationPreservation,
                relationContext
        );

        relationContext = patternWealthResourceRelationContextCalculator.apply(
                wealthResourceInteraction,
                relationContext
        );

        relationContext = patternStrengthSensitiveRelationContextCalculator.apply(
                strengthSensitiveContext,
                relationContext
        );

        relationContext = patternOfficerKillingsPurificationRelationContextCalculator.apply(
                officerKillingsPurification,
                relationContext
        );

        MonthCommandPatternSelectionPath selectionPath = patternSelection.path();

        NatalPatternDecision patternDecision = switch (selectionPath) {
            case STANDARD_PRIMARY_REVIEW -> {
                DayMasterStrengthAnalysis resolvedOrdinaryStrength = ordinaryStrength.orElseThrow(
                        () -> new IllegalStateException(
                                "STANDARD_PRIMARY_REVIEW 경로에는 일반 신강·신약 분석이 필요합니다."
                        )
                );

                MonthCommandPatternCandidate selectedPattern = patternSelection
                        .provisionalStandardCandidate()
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "STANDARD_PRIMARY_REVIEW 경로인데 표준격 후보가 없습니다."
                                )
                        );

                StandardPatternStructuralSignals structuralSignals =
                        buildStandardPatternStructuralSignals(
                                selectedPattern,
                                patternInteraction,
                                tenGodInteractions,
                                resolvedOrdinaryStrength,
                                relationContext
                        );

                StandardPatternAssessmentResult standardPatternResult =
                        standardPatternAssessmentCalculator.assess(
                                patternSelection,
                                patternInteraction,
                                resolvedOrdinaryStrength,
                                structuralSignals
                        );

                yield natalPatternDecisionCalculator.decide(
                        patternInteraction,
                        standardPatternResult.assessment(),
                        Optional.empty()
                );
            }

            case STANDARD_CHANGE_REVIEW,
                 STANDARD_MULTIPLE_CHANGE_REVIEW ->
                    natalPatternDecisionCalculator.decide(
                            patternInteraction,
                            Optional.empty(),
                            Optional.empty()
                    );

            case JIANLU_REVIEW,
                 YUEJIE_REVIEW,
                 YANG_BLADE_REVIEW -> {
                CompanionMonthPatternAnalysis companionAnalysis = companionMonthPatternRule.assess(
                        patternInteraction,
                        tenGodInteractions,
                        foundations,
                        yangBlade
                );

                yield natalPatternDecisionCalculator.decide(
                        patternInteraction,
                        Optional.empty(),
                        Optional.of(companionAnalysis)
                );
            }
        };

        NatalPatternResolution patternResolution = NatalPatternResolution.decided(patternDecision);

        NatalMyeongriProfile natalProfile = natalMyeongriProfileCalculator.build(
                patternInteraction,
                strengthDecision,
                patternResolution,
                foundations,
                routes,
                stemCombinationActivation,
                stemCombinationPreservation,
                branchDisruptions,
                branchCombinations,
                branchDisruptionResolutions,
                monthCommandDisruptionResolution,
                wealthResourceInteraction,
                strengthSensitiveContext,
                officerKillingsPurification,
                yangBlade,
                yangBladeKillingCombination
        );

        if (natalProfile.dayMaster() != dayStem) {
            throw new IllegalStateException(
                    "최종 NatalMyeongriProfile의 일간이 원국 일간과 일치하지 않습니다."
            );
        }

        VoidBranches yearVoid = voidBranchCalculator.calculate(fourPillars.year().ganji());
        VoidBranches dayVoid = voidBranchCalculator.calculate(fourPillars.day().ganji());

        Nayin yearNayin = nayinCalculator.calculate(fourPillars.year().ganji());
        Nayin monthNayin = nayinCalculator.calculate(fourPillars.month().ganji());
        Nayin dayNayin = nayinCalculator.calculate(fourPillars.day().ganji());

        NayinResponse hourNayinResponse = null;
        if (fourPillars.hasKnownHour()) {
            Nayin hourNayin = nayinCalculator.calculate(fourPillars.hour().ganji());
            hourNayinResponse = NayinResponse.of(fourPillars.hour().ganji(), hourNayin);
        }

        TianyiNobleman tianyiNobleman = tianyiNoblemanCalculator.calculate(fourPillars);

        List<TwelveShinsalResponse> twelveShinsal = twelveShinsalCalculator.calculate(fourPillars)
                .stream()
                .map(TwelveShinsalResponse::from)
                .toList();

        List<SpecialStarResponse> specialStars = specialStarCalculator.calculate(fourPillars)
                .stream()
                .map(SpecialStarResponse::from)
                .toList();

        DaewoonDirection daewoonDirection = daewoonDirectionCalculator.calculate(
                fourPillars.year().stem(),
                solarBirth.gender()
        );

        List<DaewoonPillar> daewoonDomainPillars =
                daewoonPillarCalculator.calculate(
                        fourPillars.month(),
                        daewoonDirection
                );

        List<DaewoonPillarResponse> daewoonPillars =
                daewoonDomainPillars
                        .stream()
                        .map(DaewoonPillarResponse::from)
                        .toList();

        DaewoonStartResponse daewoonStartResponse = null;

        /*
         * =================================================
         * 대운 기간 Key
         *
         * PeriodMyeongriFacts의 boolean을 여기서 임의 생성하지 않는다.
         *
         * 현재 확정 가능한 것은:
         *
         * - 대운 간지
         * - 첫 대운 실제 시작시각
         * - 각 대운의 10년 기간 경계
         *
         * 뿐이므로 먼저 LifePeriodKey만 만든다.
         * =================================================
         */

        List<LifePeriodKey> daewoonPeriodKeys =
                List.of();

        if (solarBirth.hasKnownTime()) {

            DaewoonStart daewoonStart =
                    daewoonStartCalculator.calculate(
                            solarBirth,
                            daewoonDirection
                    );

            daewoonStartResponse =
                    DaewoonStartResponse.from(
                            daewoonStart
                    );

            daewoonPeriodKeys =
                    buildDaewoonPeriodKeys(
                            daewoonDomainPillars,
                            daewoonStart
                    );
        }

        Objects.requireNonNull(
                daewoonPeriodKeys,
                "대운 LifePeriodKey 생성 결과가 필요합니다."
        );

        /*
         * =================================================
         * 대운 -> PeriodMyeongriFacts
         *
         * 현재 단계에서는 5대 십성 Family 활성화만
         * 실제 기간 Fact로 만든다.
         *
         * 구조 훼손/해소, active binding,
         * 원국 방향 강화/도전은
         * raw 관계만으로 true로 만들지 않는다.
         * =================================================
         */

        List<PeriodMyeongriFacts> daewoonPeriodFacts =
                daewoonPeriodKeys.isEmpty()
                        ? List.of()
                        : daewoonPeriodMyeongriFactsCalculator
                        .calculate(
                                natalProfile,
                                daewoonDomainPillars,
                                daewoonPeriodKeys
                        );

        if (
                daewoonPeriodFacts.size()
                        != daewoonPeriodKeys.size()
        ) {
            throw new IllegalStateException(
                    "대운 PeriodMyeongriFacts 개수가 대운 기간 Key 개수와 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 Claim + PAST_FLOW + 7개 Prompt 준비
         *
         * prepareNatalOnly()를 사용하지 않는다.
         * 실제 대운 PeriodMyeongriFacts를 넘겨서
         * PAST_FLOW까지 동일 실행선에서 준비한다.
         * =================================================
         */

        LifeInterpretationPreparationResult interpretationPreparation =
                lifeInterpretationPreparationService
                        .prepare(
                                natalProfile,
                                daewoonPeriodFacts,
                                LocalDateTime.now(
                                        com.dajeom.solar.SolarTermInstant.KOREA_ZONE
                                )
                        );

        Objects.requireNonNull(
                interpretationPreparation,
                "LifeInterpretationPreparationResult가 필요합니다."
        );

        /*
         * =================================================
         * 최종 7개 AI 장문 생성
         *
         * generationAllowed=false 카테고리는
         * LifeInterpretationGenerationService 내부에서
         * AI를 호출하지 않는다.
         *
         * 허용된 카테고리만 실제 OpenAI 생성선으로 보낸다.
         * =================================================
         */

        LifeInterpretationGenerationResult interpretationGeneration =
                lifeInterpretationGenerationService
                        .generate(
                                interpretationPreparation
                        );

        Objects.requireNonNull(
                interpretationGeneration,
                "LifeInterpretationGenerationResult가 필요합니다."
        );

        /*
         * 차단 카테고리가 하나도 없다면
         * 7개 본문이 전부 생성되어 있어야 한다.
         *
         * 빈 문자열 fallback이나 임의 기본 문장은 사용하지 않는다.
         */
        if (
                interpretationGeneration
                        .blockedCategories()
                        .isEmpty()
                        &&
                        !interpretationGeneration.complete()
        ) {
            throw new IllegalStateException(
                    "생성 차단 카테고리가 없는데 7개 사주 해석 본문이 완성되지 않았습니다. missing="
                            + interpretationGeneration.missingCategories()
            );
        }

        /*
         * =================================================
         * 최종 공개 응답
         *
         * 외부에는 다음만 공개한다.
         *
         * - 사주 4기둥
         * - 오행 분포
         * - 7개 장문 해석
         *
         * 내부 명리 판정값과 중간 계산 결과는
         * public API 응답에 노출하지 않는다.
         * =================================================
         */

        LifeInterpretationTextBundle interpretationTexts =
                interpretationGeneration
                        .requireCompleteBundle();

        return SajuResponse.of(
                fourPillars,
                ElementCountResponse.from(
                        elementCount
                ),
                interpretationTexts
        );
    }

    /*
     * =================================================
     * 대운 간지 + 첫 대운 시작시각
     * -> LifePeriodKey
     *
     * 새 Adapter를 만들지 않는다.
     *
     * 대운 1개는 첫 대운 시작시각부터
     * 다음 대운 시작시각 직전까지의 10년 구간이다.
     * =================================================
     */

    private List<LifePeriodKey> buildDaewoonPeriodKeys(

            List<DaewoonPillar> daewoonPillars,

            DaewoonStart daewoonStart

    ) {

        Objects.requireNonNull(
                daewoonPillars,
                "대운 간지 목록이 필요합니다."
        );

        Objects.requireNonNull(
                daewoonStart,
                "대운 시작 분석이 필요합니다."
        );

        if (daewoonPillars.isEmpty()) {

            return List.of();
        }

        LocalDateTime firstStart =
                daewoonStart
                        .firstDaewoonKoreaDateTime();

        List<LifePeriodKey> result =
                new ArrayList<>();

        int expectedOrder =
                1;

        for (
                DaewoonPillar pillar
                : daewoonPillars
        ) {

            Objects.requireNonNull(
                    pillar,
                    "대운 간지 목록에는 null이 들어갈 수 없습니다."
            );

            if (
                    pillar.order()
                            != expectedOrder
            ) {

                throw new IllegalStateException(
                        "대운 순번이 연속적이지 않습니다. expected="
                                + expectedOrder
                                + ", actual="
                                + pillar.order()
                );
            }

            long startOffsetYears =
                    Math.multiplyExact(
                            (long) pillar.order() - 1L,
                            10L
                    );

            LocalDateTime startInclusive =
                    firstStart
                            .plusYears(
                                    startOffsetYears
                            );

            LocalDateTime endExclusive =
                    startInclusive
                            .plusYears(
                                    10L
                            );

            result.add(
                    new LifePeriodKey(

                            LifePeriodType.DAEWOON,

                            "DAEWOON-"
                                    + pillar.order(),

                            "대운 "
                                    + pillar.order()
                                    + " "
                                    + pillar.hanja(),

                            startInclusive,

                            endExclusive
                    )
            );

            expectedOrder++;
        }

        return List.copyOf(
                result
        );
    }


    private StandardPatternStructuralSignals buildStandardPatternStructuralSignals(
            MonthCommandPatternCandidate selectedPattern,
            PatternInteractionAnalysis patternInteraction,
            PatternTenGodInteractionAnalysis tenGodInteractions,
            DayMasterStrengthAnalysis strength,
            PatternRelationContextBundle relationContext
    ) {
        Objects.requireNonNull(selectedPattern, "선택된 표준격 후보가 필요합니다.");
        Objects.requireNonNull(patternInteraction, "PatternInteractionAnalysis가 필요합니다.");
        Objects.requireNonNull(tenGodInteractions, "PatternTenGodInteractionAnalysis가 필요합니다.");
        Objects.requireNonNull(strength, "일반 신강·신약 분석이 필요합니다.");
        Objects.requireNonNull(relationContext, "PatternRelationContextBundle이 필요합니다.");

        return switch (selectedPattern.candidateType()) {
            case DIRECT_OFFICER -> new StandardPatternStructuralSignals(
                    Optional.of(
                            directOfficerPatternStructuralSignalsCalculator.analyze(
                                    patternInteraction,
                                    tenGodInteractions,
                                    relationContext.directOfficer()
                            )
                    ),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );

            case DIRECT_WEALTH,
                 INDIRECT_WEALTH -> new StandardPatternStructuralSignals(
                    Optional.empty(),
                    Optional.of(
                            wealthPatternStructuralSignalsCalculator.analyze(
                                    patternInteraction,
                                    tenGodInteractions,
                                    relationContext.wealth()
                            )
                    ),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );

            case DIRECT_RESOURCE,
                 INDIRECT_RESOURCE -> new StandardPatternStructuralSignals(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(
                            resourcePatternStructuralSignalsCalculator.analyze(
                                    patternInteraction,
                                    tenGodInteractions,
                                    strength,
                                    relationContext.resource()
                            )
                    ),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );

            case EATING_GOD -> new StandardPatternStructuralSignals(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(
                            eatingGodPatternStructuralSignalsCalculator.analyze(
                                    patternInteraction,
                                    tenGodInteractions,
                                    relationContext.eatingGod()
                            )
                    ),
                    Optional.empty(),
                    Optional.empty()
            );

            case HURTING_OFFICER -> new StandardPatternStructuralSignals(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(
                            hurtingOfficerPatternStructuralSignalsCalculator.analyze(
                                    patternInteraction,
                                    tenGodInteractions,
                                    relationContext.hurtingOfficer()
                            )
                    ),
                    Optional.empty()
            );

            case SEVEN_KILLINGS -> new StandardPatternStructuralSignals(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(
                            sevenKillingsPatternStructuralSignalsCalculator.analyze(
                                    patternInteraction,
                                    tenGodInteractions,
                                    relationContext.sevenKillings()
                            )
                    )
            );

            case MONTH_COMPANION,
                 MONTH_ROB_WEALTH -> throw new IllegalStateException(
                    "STANDARD_PRIMARY_REVIEW에 비겁 월령 후보가 전달됐습니다: "
                            + selectedPattern.candidateType()
            );
        };
    }

    private List<DayMasterRootInfluenceAssessment> buildRootInfluenceAssessments(
            DayMasterRootContextAnalysis rootContext,
            DayMasterSeasonAnalysis season
    ) {
        Objects.requireNonNull(rootContext, "DayMasterRootContextAnalysis가 필요합니다.");
        Objects.requireNonNull(season, "DayMasterSeasonAnalysis가 필요합니다.");

        List<DayMasterRootInfluenceAssessment> result = new ArrayList<>();

        for (DayMasterRootRelationContext context : rootContext.exactStemRootContexts()) {
            result.add(
                    dayMasterRootInfluenceRule.assess(
                            context.root(),
                            toRootRelationSignals(context),
                            season
                    )
            );
        }

        for (DayMasterRootRelationContext context : rootContext.sameElementRootContexts()) {
            result.add(
                    dayMasterRootInfluenceRule.assess(
                            context.root(),
                            toRootRelationSignals(context),
                            season
                    )
            );
        }

        return List.copyOf(result);
    }

    private DayMasterRootRelationSignals toRootRelationSignals(
            DayMasterRootRelationContext context
    ) {
        Objects.requireNonNull(context, "DayMasterRootRelationContext가 필요합니다.");

        boolean clash = false;
        boolean combination = false;
        boolean punishment = false;
        boolean selfPunishment = false;
        boolean breakRelation = false;
        boolean harm = false;

        for (BranchRelation relation : context.branchRelations()) {
            if (relation == null) {
                continue;
            }

            switch (relation.type()) {
                case CLASH -> clash = true;
                case SIX_COMBINATION,
                     THREE_HARMONY,
                     DIRECTIONAL_HARMONY -> combination = true;
                case PUNISHMENT -> punishment = true;
                case SELF_PUNISHMENT -> selfPunishment = true;
                case BREAK -> breakRelation = true;
                case HARM -> harm = true;
            }
        }

        return new DayMasterRootRelationSignals(
                clash,
                combination,
                punishment,
                selfPunishment,
                breakRelation,
                harm
        );
    }

    private List<BranchCombinationTransformationAssessment> buildBranchTransformationAssessments(
            PatternBranchCombinationAnalysis branchCombinations,
            FourPillars fourPillars,
            Optional<HeavenlyStem> hourStem,
            Optional<EarthlyBranch> hourBranch,
            List<BranchRelation> branchRelations
    ) {
        Objects.requireNonNull(branchCombinations, "PatternBranchCombinationAnalysis가 필요합니다.");
        Objects.requireNonNull(fourPillars, "FourPillars가 필요합니다.");
        Objects.requireNonNull(hourStem, "시간 Optional이 필요합니다.");
        Objects.requireNonNull(hourBranch, "시지 Optional이 필요합니다.");
        Objects.requireNonNull(branchRelations, "BranchRelation 목록이 필요합니다.");

        List<PatternBranchCombinationOccurrence> occurrences = branchCombinations.combinations();
        Set<PillarPosition> clashedPositions = collectClashedPositions(branchRelations);
        List<BranchCombinationTransformationAssessment> result = new ArrayList<>();

        for (PatternBranchCombinationOccurrence occurrence : occurrences) {
            Optional<BranchCombinationOccurrence> transformationOccurrence =
                    toTransformationOccurrence(occurrence);

            if (transformationOccurrence.isEmpty()) {
                continue;
            }

            Set<PillarPosition> competingCombinationPositions =
                    collectCompetingCombinationPositions(occurrence, occurrences);

            BranchCombinationTransformationContext context =
                    branchCombinationTransformationContextCalculator.analyze(
                            transformationOccurrence.get(),
                            fourPillars.year().stem(),
                            fourPillars.month().stem(),
                            fourPillars.day().stem(),
                            hourStem,
                            fourPillars.year().branch(),
                            fourPillars.month().branch(),
                            fourPillars.day().branch(),
                            hourBranch,
                            clashedPositions,
                            competingCombinationPositions
                    );

            result.add(branchCombinationTransformationRule.assess(context));
        }

        return List.copyOf(result);
    }

    private Optional<BranchCombinationOccurrence> toTransformationOccurrence(
            PatternBranchCombinationOccurrence occurrence
    ) {
        Objects.requireNonNull(occurrence, "PatternBranchCombinationOccurrence가 필요합니다.");

        if (occurrence.kind() == PatternBranchCombinationKind.SIX_HARMONY) {
            return Optional.empty();
        }

        BranchCombinationKind kind = switch (occurrence.kind()) {
            case COMPLETE_THREE_HARMONY -> BranchCombinationKind.THREE_HARMONY;
            case COMPLETE_THREE_MEETING -> BranchCombinationKind.DIRECTIONAL_HARMONY;
            case SIX_HARMONY -> throw new IllegalStateException(
                    "육합은 이 합화 변환 경로에서 처리하지 않습니다."
            );
        };

        FiveElement transformationElement = occurrence.groupElement().orElseThrow(
                () -> new IllegalStateException(
                        "완전 삼합/삼회의 합화 목표 오행이 없습니다."
                )
        );

        return Optional.of(
                new BranchCombinationOccurrence(
                        kind,
                        occurrence.positions(),
                        occurrence.branches(),
                        transformationElement
                )
        );
    }

    private Set<PillarPosition> collectClashedPositions(
            List<BranchRelation> branchRelations
    ) {
        EnumSet<PillarPosition> result = EnumSet.noneOf(PillarPosition.class);

        for (BranchRelation relation : branchRelations) {
            if (relation == null || relation.type() != BranchRelationType.CLASH) {
                continue;
            }

            result.add(relation.leftPosition());
            result.add(relation.rightPosition());
        }

        return Set.copyOf(result);
    }

    private Set<PillarPosition> collectCompetingCombinationPositions(
            PatternBranchCombinationOccurrence current,
            List<PatternBranchCombinationOccurrence> all
    ) {
        Objects.requireNonNull(current, "현재 Pattern 지지 합 occurrence가 필요합니다.");
        Objects.requireNonNull(all, "전체 Pattern 지지 합 목록이 필요합니다.");

        EnumSet<PillarPosition> result = EnumSet.noneOf(PillarPosition.class);

        for (PatternBranchCombinationOccurrence other : all) {
            if (other == null || other == current || other.equals(current)) {
                continue;
            }

            for (PillarPosition position : current.positions()) {
                if (other.positions().contains(position)) {
                    result.add(position);
                }
            }
        }

        return Set.copyOf(result);
    }

    private BirthDateTime normalizeBirth(SajuRequest request) {
        if (request.calendarType().isSolar()) {
            return lunarSolarConverter.normalizeSolar(
                    request.birthDate(),
                    request.birthTime(),
                    request.gender()
            );
        }

        return lunarSolarConverter.normalizeLunar(
                request.birthDate(),
                request.birthTime(),
                request.gender(),
                request.leapMonth()
        );
    }

    private PillarAnalysisResponse analyzePillar(
            HeavenlyStem dayStem,
            Pillar pillar,
            boolean isDayMaster
    ) {
        Objects.requireNonNull(dayStem, "일간이 필요합니다.");
        Objects.requireNonNull(pillar, "분석할 기둥이 필요합니다.");

        TenGod stemTenGod = tenGodCalculator.calculate(dayStem, pillar.stem());
        TenGod branchTenGod = hiddenStemCalculator.calculateMainTenGod(dayStem, pillar.branch());
        var hiddenStems = hiddenStemCalculator.calculate(dayStem, pillar.branch());
        TwelveStage dayMasterTwelveStage = twelveStageCalculator.calculate(dayStem, pillar.branch());
        TwelveStage pillarStemTwelveStage = twelveStageCalculator.calculate(pillar.stem(), pillar.branch());

        return PillarAnalysisResponse.of(
                stemTenGod,
                isDayMaster,
                branchTenGod,
                dayMasterTwelveStage,
                pillarStemTwelveStage,
                hiddenStems
        );
    }
}
