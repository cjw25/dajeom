package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.HiddenStemTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.AnnualLuck;
import com.dajeom.model.DecadeLuck;
import com.dajeom.model.FourPillars;
import com.dajeom.model.LuckInteraction;
import com.dajeom.model.LuckPeriodEvaluation;
import com.dajeom.model.Pillar;
import com.dajeom.model.StrengthAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.model.UsefulGodCandidate;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.LuckEvaluationLevel;
import com.dajeom.type.LuckInteractionSource;
import com.dajeom.type.RelationType;
import com.dajeom.type.StrengthLevel;
import com.dajeom.type.TenGod;
import com.dajeom.type.UsefulGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 대운·세운의 오행, 십성, 합충형파해를
 * 원국과 결합해 평가한다.
 */
@Component
public class LuckInteractionAnalyzer {

    private static final double BASE_SCORE =
            50.0;

    public LuckPeriodEvaluation analyzeDecade(
            FourPillars fourPillars,
            DecadeLuck decadeLuck,
            StrengthAnalysis strengthAnalysis,
            UsefulGodAnalysis usefulGodAnalysis
    ) {
        List<LuckInteraction> interactions =
                analyzePillarAgainstNatal(
                        fourPillars,
                        decadeLuck.pillar(),
                        LuckInteractionSource.NATAL_DECADE,
                        "대운"
                );

        return evaluatePeriod(
                "DECADE",
                decadeLuck.pillar(),
                decadeLuck.stemTenGod(),
                decadeLuck.branchMainTenGod(),
                strengthAnalysis,
                usefulGodAnalysis,
                interactions
        );
    }

    public LuckPeriodEvaluation analyzeAnnual(
            FourPillars fourPillars,
            AnnualLuck annualLuck,
            StrengthAnalysis strengthAnalysis,
            UsefulGodAnalysis usefulGodAnalysis
    ) {
        List<LuckInteraction> interactions =
                analyzePillarAgainstNatal(
                        fourPillars,
                        annualLuck.pillar(),
                        LuckInteractionSource.NATAL_ANNUAL,
                        "세운"
                );

        return evaluatePeriod(
                "ANNUAL",
                annualLuck.pillar(),
                annualLuck.stemTenGod(),
                annualLuck.branchMainTenGod(),
                strengthAnalysis,
                usefulGodAnalysis,
                interactions
        );
    }

    public List<LuckInteraction>
    analyzeDecadeAnnual(
            DecadeLuck decadeLuck,
            AnnualLuck annualLuck
    ) {
        if (decadeLuck == null) {
            return List.of();
        }

        List<LuckInteraction> result =
                new ArrayList<>();

        analyzeStemPair(
                result,
                decadeLuck.pillar().stem(),
                "대운 천간",
                annualLuck.pillar().stem(),
                "세운 천간",
                LuckInteractionSource.DECADE_ANNUAL
        );

        analyzeBranchPair(
                result,
                decadeLuck.pillar().branch(),
                "대운 지지",
                annualLuck.pillar().branch(),
                "세운 지지",
                LuckInteractionSource.DECADE_ANNUAL
        );

        return List.copyOf(result);
    }

    public double calculateInteractionScore(
            List<LuckInteraction> interactions
    ) {
        return round(
                interactions.stream()
                        .mapToDouble(
                                LuckInteraction::scoreChange
                        )
                        .sum()
        );
    }

    private LuckPeriodEvaluation evaluatePeriod(
            String periodType,
            Pillar pillar,
            TenGod stemTenGod,
            TenGod branchTenGod,
            StrengthAnalysis strengthAnalysis,
            UsefulGodAnalysis usefulGodAnalysis,
            List<LuckInteraction> interactions
    ) {
        FiveElement stemElement =
                pillar.stem().getElement();

        FiveElement branchElement =
                pillar.branch().getElement();

        UsefulGodType stemUsefulGodType =
                findUsefulGodType(
                        usefulGodAnalysis,
                        stemElement
                );

        UsefulGodType branchUsefulGodType =
                findUsefulGodType(
                        usefulGodAnalysis,
                        branchElement
                );

        double elementCompatibilityScore =
                calculateElementCompatibility(
                        stemUsefulGodType,
                        branchUsefulGodType
                );

        double tenGodScore =
                calculateTenGodScore(
                        strengthAnalysis.level(),
                        stemTenGod,
                        branchTenGod
                );

        double relationScore =
                calculateInteractionScore(
                        interactions
                );

        double totalScore =
                clampAndRound(
                        BASE_SCORE
                                + elementCompatibilityScore
                                + tenGodScore
                                + relationScore
                );

        LuckEvaluationLevel level =
                LuckEvaluationLevel.fromScore(
                        totalScore
                );

        List<String> evidence =
                createEvidence(
                        pillar,
                        stemUsefulGodType,
                        branchUsefulGodType,
                        stemTenGod,
                        branchTenGod,
                        elementCompatibilityScore,
                        tenGodScore,
                        relationScore
                );

        return new LuckPeriodEvaluation(
                periodType,
                pillar,
                stemElement,
                branchElement,
                stemTenGod,
                branchTenGod,
                stemUsefulGodType,
                branchUsefulGodType,
                elementCompatibilityScore,
                tenGodScore,
                relationScore,
                totalScore,
                level,
                interactions,
                evidence
        );
    }

    private List<LuckInteraction>
    analyzePillarAgainstNatal(
            FourPillars fourPillars,
            Pillar luckPillar,
            LuckInteractionSource source,
            String luckLabel
    ) {
        List<LuckInteraction> result =
                new ArrayList<>();

        List<NatalPillarItem> natalPillars =
                List.of(
                        new NatalPillarItem(
                                "년주",
                                fourPillars.yearPillar()
                        ),
                        new NatalPillarItem(
                                "월주",
                                fourPillars.monthPillar()
                        ),
                        new NatalPillarItem(
                                "일주",
                                fourPillars.dayPillar()
                        ),
                        new NatalPillarItem(
                                "시주",
                                fourPillars.hourPillar()
                        )
                );

        for (NatalPillarItem natal
                : natalPillars) {

            analyzeStemPair(
                    result,
                    natal.pillar().stem(),
                    natal.label() + " 천간",
                    luckPillar.stem(),
                    luckLabel + " 천간",
                    source
            );

            analyzeBranchPair(
                    result,
                    natal.pillar().branch(),
                    natal.label() + " 지지",
                    luckPillar.branch(),
                    luckLabel + " 지지",
                    source
            );
        }

        addThreeHarmonyInteractions(
                result,
                fourPillars,
                luckPillar.branch(),
                luckLabel + " 지지",
                source
        );

        return List.copyOf(result);
    }

    private void analyzeStemPair(
            List<LuckInteraction> destination,
            HeavenlyStem firstStem,
            String firstLabel,
            HeavenlyStem secondStem,
            String secondLabel,
            LuckInteractionSource source
    ) {
        StemRelationTable.findCombination(
                        firstStem,
                        secondStem
                )
                .ifPresent(combination ->
                        destination.add(
                                new LuckInteraction(
                                        source,
                                        RelationType
                                                .STEM_COMBINATION,
                                        firstLabel,
                                        firstStem
                                                .getChineseCharacter(),
                                        secondLabel,
                                        secondStem
                                                .getChineseCharacter(),
                                        3.0,
                                        true,
                                        firstStem
                                                .getChineseCharacter()
                                                + secondStem
                                                .getChineseCharacter()
                                                + " 천간합 후보가 형성됨"
                                )
                        )
                );

        if (firstStem
                .getElement()
                .controls(
                        secondStem.getElement()
                )) {

            destination.add(
                    new LuckInteraction(
                            source,
                            RelationType.STEM_CONTROL,
                            firstLabel,
                            firstStem.getChineseCharacter(),
                            secondLabel,
                            secondStem.getChineseCharacter(),
                            -3.0,
                            false,
                            firstStem.getChineseCharacter()
                                    + "이(가) "
                                    + secondStem
                                    .getChineseCharacter()
                                    + "을(를) 극함"
                    )
            );

        } else if (secondStem
                .getElement()
                .controls(
                        firstStem.getElement()
                )) {

            destination.add(
                    new LuckInteraction(
                            source,
                            RelationType.STEM_CONTROL,
                            secondLabel,
                            secondStem.getChineseCharacter(),
                            firstLabel,
                            firstStem.getChineseCharacter(),
                            -3.0,
                            false,
                            secondStem.getChineseCharacter()
                                    + "이(가) "
                                    + firstStem
                                    .getChineseCharacter()
                                    + "을(를) 극함"
                    )
            );
        }
    }

    private void analyzeBranchPair(
            List<LuckInteraction> destination,
            EarthlyBranch firstBranch,
            String firstLabel,
            EarthlyBranch secondBranch,
            String secondLabel,
            LuckInteractionSource source
    ) {
        addPairRelation(
                destination,
                RelationType.SIX_HARMONY,
                firstBranch,
                firstLabel,
                secondBranch,
                secondLabel,
                source,
                4.0,
                true
        );

        addPairRelation(
                destination,
                RelationType.CLASH,
                firstBranch,
                firstLabel,
                secondBranch,
                secondLabel,
                source,
                -8.0,
                false
        );

        addPairRelation(
                destination,
                RelationType.PUNISHMENT,
                firstBranch,
                firstLabel,
                secondBranch,
                secondLabel,
                source,
                -6.0,
                false
        );

        addPairRelation(
                destination,
                RelationType.BREAK,
                firstBranch,
                firstLabel,
                secondBranch,
                secondLabel,
                source,
                -4.0,
                false
        );

        addPairRelation(
                destination,
                RelationType.HARM,
                firstBranch,
                firstLabel,
                secondBranch,
                secondLabel,
                source,
                -4.0,
                false
        );

        BranchRelationTable
                .findHarmonyGroupContaining(
                        firstBranch,
                        secondBranch
                )
                .ifPresent(group ->
                        destination.add(
                                new LuckInteraction(
                                        source,
                                        RelationType.HALF_HARMONY,
                                        firstLabel,
                                        firstBranch
                                                .getChineseCharacter(),
                                        secondLabel,
                                        secondBranch
                                                .getChineseCharacter(),
                                        3.0,
                                        true,
                                        firstBranch
                                                .getChineseCharacter()
                                                + secondBranch
                                                .getChineseCharacter()
                                                + " 반합 후보가 형성됨"
                                )
                        )
                );
    }

    private void addPairRelation(
            List<LuckInteraction> destination,
            RelationType relationType,
            EarthlyBranch firstBranch,
            String firstLabel,
            EarthlyBranch secondBranch,
            String secondLabel,
            LuckInteractionSource source,
            double scoreChange,
            boolean favorable
    ) {
        BranchRelationTable.findPairRelation(
                        relationType,
                        firstBranch,
                        secondBranch
                )
                .ifPresent(relation ->
                        destination.add(
                                new LuckInteraction(
                                        source,
                                        relationType,
                                        firstLabel,
                                        firstBranch
                                                .getChineseCharacter(),
                                        secondLabel,
                                        secondBranch
                                                .getChineseCharacter(),
                                        scoreChange,
                                        favorable,
                                        firstBranch
                                                .getChineseCharacter()
                                                + secondBranch
                                                .getChineseCharacter()
                                                + " "
                                                + relationType
                                                .getKoreanName()
                                                + " 관계가 형성됨"
                                )
                        )
                );
    }

    private void addThreeHarmonyInteractions(
            List<LuckInteraction> destination,
            FourPillars fourPillars,
            EarthlyBranch luckBranch,
            String luckLabel,
            LuckInteractionSource source
    ) {
        List<EarthlyBranch> natalBranches =
                List.of(
                        fourPillars.yearPillar().branch(),
                        fourPillars.monthPillar().branch(),
                        fourPillars.dayPillar().branch(),
                        fourPillars.hourPillar().branch()
                );

        for (BranchRelationTable.BranchTripleRelation group
                : BranchRelationTable.getThreeHarmonies()) {

            if (!group.contains(luckBranch)) {
                continue;
            }

            long natalMatchCount =
                    natalBranches.stream()
                            .distinct()
                            .filter(group::contains)
                            .count();

            if (natalMatchCount < 2) {
                continue;
            }

            destination.add(
                    new LuckInteraction(
                            source,
                            RelationType.THREE_HARMONY,
                            "원국 지지",
                            natalBranches.stream()
                                    .filter(group::contains)
                                    .map(
                                            EarthlyBranch::
                                                    getChineseCharacter
                                    )
                                    .distinct()
                                    .reduce(
                                            "",
                                            String::concat
                                    ),
                            luckLabel,
                            luckBranch.getChineseCharacter(),
                            8.0,
                            true,
                            "운의 지지가 원국의 두 지지와 결합해 "
                                    + group
                                    .transformationElement()
                                    .getKoreanName()
                                    + " 삼합 후보를 완성함"
                    )
            );
        }
    }

    private UsefulGodType findUsefulGodType(
            UsefulGodAnalysis analysis,
            FiveElement element
    ) {
        return analysis.allCandidates()
                .stream()
                .filter(candidate ->
                        candidate.element() == element
                )
                .map(UsefulGodCandidate::type)
                .findFirst()
                .orElse(
                        UsefulGodType.NEUTRAL_GOD
                );
    }

    private double calculateElementCompatibility(
            UsefulGodType stemType,
            UsefulGodType branchType
    ) {
        return round(
                usefulGodScore(stemType)
                        + usefulGodScore(branchType)
        );
    }

    private double usefulGodScore(
            UsefulGodType type
    ) {
        return switch (type) {
            case USEFUL_GOD -> 12.0;
            case FAVORABLE_GOD -> 7.0;
            case NEUTRAL_GOD -> 0.0;
            case UNFAVORABLE_GOD -> -7.0;
            case ENEMY_GOD -> -12.0;
        };
    }

    private double calculateTenGodScore(
            StrengthLevel strengthLevel,
            TenGod stemTenGod,
            TenGod branchTenGod
    ) {
        return round(
                tenGodScore(
                        strengthLevel,
                        stemTenGod
                )
                        + tenGodScore(
                        strengthLevel,
                        branchTenGod
                )
        );
    }

    private double tenGodScore(
            StrengthLevel strengthLevel,
            TenGod tenGod
    ) {
        if (isWeak(strengthLevel)) {
            return switch (tenGod) {
                case FRIEND, ROB_WEALTH -> 5.0;
                case INDIRECT_RESOURCE,
                     DIRECT_RESOURCE -> 6.0;
                case EATING_GOD,
                     HURTING_OFFICER -> -3.0;
                case INDIRECT_WEALTH,
                     DIRECT_WEALTH -> -5.0;
                case SEVEN_KILLINGS,
                     DIRECT_OFFICER -> -6.0;
            };
        }

        if (isStrong(strengthLevel)) {
            return switch (tenGod) {
                case FRIEND, ROB_WEALTH -> -5.0;
                case INDIRECT_RESOURCE,
                     DIRECT_RESOURCE -> -5.0;
                case EATING_GOD,
                     HURTING_OFFICER -> 5.0;
                case INDIRECT_WEALTH,
                     DIRECT_WEALTH -> 4.0;
                case SEVEN_KILLINGS,
                     DIRECT_OFFICER -> 4.0;
            };
        }

        return switch (tenGod) {
            case FRIEND, ROB_WEALTH -> 0.0;
            case INDIRECT_RESOURCE,
                 DIRECT_RESOURCE -> 1.0;
            case EATING_GOD,
                 HURTING_OFFICER -> 2.0;
            case INDIRECT_WEALTH,
                 DIRECT_WEALTH -> 2.0;
            case SEVEN_KILLINGS,
                 DIRECT_OFFICER -> 1.0;
        };
    }

    private boolean isWeak(
            StrengthLevel level
    ) {
        return level
                == StrengthLevel.EXTREMELY_WEAK
                || level == StrengthLevel.WEAK
                || level == StrengthLevel.SLIGHTLY_WEAK;
    }

    private boolean isStrong(
            StrengthLevel level
    ) {
        return level
                == StrengthLevel.EXTREMELY_STRONG
                || level == StrengthLevel.STRONG
                || level == StrengthLevel.SLIGHTLY_STRONG;
    }

    private List<String> createEvidence(
            Pillar pillar,
            UsefulGodType stemUsefulGodType,
            UsefulGodType branchUsefulGodType,
            TenGod stemTenGod,
            TenGod branchTenGod,
            double elementScore,
            double tenGodScore,
            double relationScore
    ) {
        List<String> evidence =
                new ArrayList<>();

        evidence.add(
                "운의 간지는 "
                        + pillar.chineseName()
                        + "임"
        );

        evidence.add(
                "천간 오행은 "
                        + pillar.stem()
                        .getElement()
                        .getKoreanName()
                        + "이며 "
                        + stemUsefulGodType
                        .getKoreanName()
                        + "으로 분류됨"
        );

        evidence.add(
                "지지 대표 오행은 "
                        + pillar.branch()
                        .getElement()
                        .getKoreanName()
                        + "이며 "
                        + branchUsefulGodType
                        .getKoreanName()
                        + "으로 분류됨"
        );

        evidence.add(
                "천간 십성은 "
                        + stemTenGod.getKoreanName()
                        + ", 지지 본기 십성은 "
                        + branchTenGod.getKoreanName()
                        + "임"
        );

        evidence.add(
                "용희기신 적합도 점수 변화: "
                        + elementScore
        );

        evidence.add(
                "십성 적합도 점수 변화: "
                        + tenGodScore
        );

        evidence.add(
                "합충형파해 점수 변화: "
                        + relationScore
        );

        return List.copyOf(evidence);
    }

    private double clampAndRound(
            double value
    ) {
        return round(
                Math.max(
                        0.0,
                        Math.min(100.0, value)
                )
        );
    }

    private double round(
            double value
    ) {
        return Math.round(value * 100.0)
                / 100.0;
    }

    private record NatalPillarItem(
            String label,
            Pillar pillar
    ) {
    }
}