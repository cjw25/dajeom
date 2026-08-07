package com.dajeom.analyzer;

import com.dajeom.model.ClimateAnalysis;
import com.dajeom.model.ElementStrength;
import com.dajeom.model.FourPillars;
import com.dajeom.model.StrengthAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.model.UsefulGodCandidate;
import com.dajeom.type.ConfidenceLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodMethod;
import com.dajeom.type.UsefulGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 조후·억부·통관·병약 결과를 통합해
 * 용신·희신·기신 후보를 산출한다.
 */
@Component
public class UsefulGodAnalyzer {

    public static final String RULE_VERSION =
            "DAJEOM_USEFUL_GOD_V1";

    private final ClimateAnalyzer
            climateAnalyzer;

    private final BalancingUsefulGodAnalyzer
            balancingUsefulGodAnalyzer;

    private final MediationUsefulGodAnalyzer
            mediationUsefulGodAnalyzer;

    private final DiseaseRemedyAnalyzer
            diseaseRemedyAnalyzer;

    public UsefulGodAnalyzer(
            ClimateAnalyzer climateAnalyzer,
            BalancingUsefulGodAnalyzer
                    balancingUsefulGodAnalyzer,
            MediationUsefulGodAnalyzer
                    mediationUsefulGodAnalyzer,
            DiseaseRemedyAnalyzer
                    diseaseRemedyAnalyzer
    ) {
        this.climateAnalyzer =
                climateAnalyzer;

        this.balancingUsefulGodAnalyzer =
                balancingUsefulGodAnalyzer;

        this.mediationUsefulGodAnalyzer =
                mediationUsefulGodAnalyzer;

        this.diseaseRemedyAnalyzer =
                diseaseRemedyAnalyzer;
    }

    public UsefulGodAnalysis analyze(
            FourPillars fourPillars,
            ElementStrength elementStrength,
            StrengthAnalysis strengthAnalysis
    ) {
        ClimateAnalysis climateAnalysis =
                climateAnalyzer.analyze(
                        fourPillars,
                        elementStrength
                );

        Map<FiveElement, Double>
                balancingScores =
                balancingUsefulGodAnalyzer.analyze(
                        strengthAnalysis
                );

        Map<FiveElement, Double>
                mediationScores =
                mediationUsefulGodAnalyzer.analyze(
                        elementStrength
                );

        Map<FiveElement, Double>
                diseaseRemedyScores =
                diseaseRemedyAnalyzer.analyze(
                        elementStrength
                );

        EnumMap<FiveElement, CandidateBuilder>
                builders =
                createBuilders();

        applyClimateScores(
                climateAnalysis,
                builders
        );

        applyMethodScores(
                balancingScores,
                UsefulGodMethod.BALANCING,
                "BALANCING_SCORE",
                "신강·신약 조절 관점에서 산출된 점수",
                builders
        );

        applyMethodScores(
                mediationScores,
                UsefulGodMethod.MEDIATION,
                "MEDIATION_SCORE",
                "강한 오행 충돌을 연결하는 통관 점수",
                builders
        );

        applyMethodScores(
                diseaseRemedyScores,
                UsefulGodMethod.DISEASE_REMEDY,
                "DISEASE_REMEDY_SCORE",
                "과다 오행을 조절하는 병약 점수",
                builders
        );

        List<UsefulGodCandidate> candidates =
                builders.values()
                        .stream()
                        .map(CandidateBuilder::build)
                        .sorted(
                                Comparator.comparingDouble(
                                        UsefulGodCandidate::score
                                ).reversed()
                        )
                        .toList();

        List<UsefulGodCandidate> usefulCandidates =
                candidates.stream()
                        .filter(candidate ->
                                candidate.type()
                                        == UsefulGodType.USEFUL_GOD
                                        || candidate.type()
                                        == UsefulGodType.FAVORABLE_GOD
                        )
                        .toList();

        List<UsefulGodCandidate>
                unfavorableCandidates =
                candidates.stream()
                        .filter(candidate ->
                                candidate.type()
                                        == UsefulGodType.UNFAVORABLE_GOD
                                        || candidate.type()
                                        == UsefulGodType.ENEMY_GOD
                        )
                        .toList();

        UsefulGodCandidate primary =
                usefulCandidates.stream()
                        .findFirst()
                        .orElseGet(() ->
                                candidates.get(0)
                        );

        UsefulGodCandidate secondary =
                usefulCandidates.stream()
                        .skip(1)
                        .findFirst()
                        .orElse(null);

        return new UsefulGodAnalysis(
                climateAnalysis,
                primary,
                secondary,
                usefulCandidates,
                unfavorableCandidates,
                candidates,
                RULE_VERSION
        );
    }

    private void applyClimateScores(
            ClimateAnalysis climateAnalysis,
            Map<FiveElement, CandidateBuilder> builders
    ) {
        for (FiveElement element
                : climateAnalysis.requiredElements()) {

            CandidateBuilder builder =
                    builders.get(element);

            builder.addScore(35.0);

            builder.addMethod(
                    UsefulGodMethod.CLIMATE
            );

            builder.addEvidence(
                    "CLIMATE_REQUIRED_"
                            + element.name(),

                    "한난조습 조절에 "
                            + element.getKoreanName()
                            + " 오행이 필요함"
            );
        }
    }

    private void applyMethodScores(
            Map<FiveElement, Double> scores,
            UsefulGodMethod method,
            String codePrefix,
            String description,
            Map<FiveElement, CandidateBuilder> builders
    ) {
        for (Map.Entry<FiveElement, Double>
                entry : scores.entrySet()) {

            double score =
                    entry.getValue();

            if (score == 0.0) {
                continue;
            }

            CandidateBuilder builder =
                    builders.get(entry.getKey());

            builder.addScore(score);

            builder.addMethod(method);

            builder.addEvidence(
                    codePrefix
                            + "_"
                            + entry.getKey().name(),

                    description
                            + ": "
                            + score
            );
        }
    }

    private EnumMap<FiveElement, CandidateBuilder>
    createBuilders() {
        EnumMap<FiveElement, CandidateBuilder>
                builders =
                new EnumMap<>(FiveElement.class);

        for (FiveElement element
                : FiveElement.values()) {

            builders.put(
                    element,
                    new CandidateBuilder(element)
            );
        }

        return builders;
    }

    private static class CandidateBuilder {

        private final FiveElement element;

        private double rawScore = 50.0;

        private final List<UsefulGodMethod>
                methods =
                new ArrayList<>();

        private final List<String>
                evidenceCodes =
                new ArrayList<>();

        private final List<String>
                evidenceDescriptions =
                new ArrayList<>();

        private CandidateBuilder(
                FiveElement element
        ) {
            this.element = element;
        }

        private void addScore(double score) {
            rawScore += score;
        }

        private void addMethod(
                UsefulGodMethod method
        ) {
            if (!methods.contains(method)) {
                methods.add(method);
            }
        }

        private void addEvidence(
                String code,
                String description
        ) {
            evidenceCodes.add(code);
            evidenceDescriptions.add(description);
        }

        private UsefulGodCandidate build() {
            double normalized =
                    normalize(rawScore);

            UsefulGodType type =
                    determineType(normalized);

            return new UsefulGodCandidate(
                    element,
                    type,
                    normalized,
                    ConfidenceLevel.fromScore(
                            normalized
                    ),
                    methods.isEmpty()
                            ? List.of(
                            UsefulGodMethod.INTEGRATED
                    )
                            : methods,
                    evidenceCodes,
                    evidenceDescriptions
            );
        }

        private UsefulGodType determineType(
                double score
        ) {
            if (score >= 80.0) {
                return UsefulGodType.USEFUL_GOD;
            }

            if (score >= 60.0) {
                return UsefulGodType.FAVORABLE_GOD;
            }

            if (score >= 40.0) {
                return UsefulGodType.NEUTRAL_GOD;
            }

            if (score >= 20.0) {
                return UsefulGodType.UNFAVORABLE_GOD;
            }

            return UsefulGodType.ENEMY_GOD;
        }

        private double normalize(
                double value
        ) {
            double clamped =
                    Math.max(
                            0.0,
                            Math.min(100.0, value)
                    );

            return Math.round(clamped * 100.0)
                    / 100.0;
        }
    }
}