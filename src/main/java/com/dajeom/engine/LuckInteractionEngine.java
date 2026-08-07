package com.dajeom.engine;

import com.dajeom.analyzer.LuckInteractionAnalyzer;
import com.dajeom.model.DecadeLuck;
import com.dajeom.model.FourPillars;
import com.dajeom.model.LuckAnalysis;
import com.dajeom.model.LuckInteraction;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.LuckPeriodEvaluation;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.type.LuckEvaluationLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 원국·현재 대운·세운 상호작용을 통합 분석한다.
 */
@Component
public class LuckInteractionEngine {

    public static final String RULE_VERSION =
            "DAJEOM_LUCK_INTERACTION_V1";

    private final LuckInteractionAnalyzer
            luckInteractionAnalyzer;

    public LuckInteractionEngine(
            LuckInteractionAnalyzer
                    luckInteractionAnalyzer
    ) {
        this.luckInteractionAnalyzer =
                luckInteractionAnalyzer;
    }

    public LuckInteractionAnalysis analyze(
            FourPillars fourPillars,
            NatalChartAnalysis natalChartAnalysis,
            LuckAnalysis luckAnalysis
    ) {
        DecadeLuck currentDecade =
                luckAnalysis.currentDecadeLuck();

        LuckPeriodEvaluation decadeEvaluation =
                currentDecade == null
                        ? null
                        : luckInteractionAnalyzer
                        .analyzeDecade(
                                fourPillars,
                                currentDecade,
                                natalChartAnalysis
                                        .strengthAnalysis(),
                                natalChartAnalysis
                                        .usefulGodAnalysis()
                        );

        LuckPeriodEvaluation annualEvaluation =
                luckInteractionAnalyzer
                        .analyzeAnnual(
                                fourPillars,
                                luckAnalysis.annualLuck(),
                                natalChartAnalysis
                                        .strengthAnalysis(),
                                natalChartAnalysis
                                        .usefulGodAnalysis()
                        );

        List<LuckInteraction>
                decadeAnnualInteractions =
                luckInteractionAnalyzer
                        .analyzeDecadeAnnual(
                                currentDecade,
                                luckAnalysis.annualLuck()
                        );

        double decadeAnnualRelationScore =
                luckInteractionAnalyzer
                        .calculateInteractionScore(
                                decadeAnnualInteractions
                        );

        double combinedScore =
                calculateCombinedScore(
                        decadeEvaluation,
                        annualEvaluation,
                        decadeAnnualRelationScore
                );

        LuckEvaluationLevel combinedLevel =
                LuckEvaluationLevel.fromScore(
                        combinedScore
                );

        List<String> summarySignals =
                createSummarySignals(
                        decadeEvaluation,
                        annualEvaluation,
                        decadeAnnualInteractions,
                        combinedScore,
                        combinedLevel
                );

        return new LuckInteractionAnalysis(
                decadeEvaluation,
                annualEvaluation,
                decadeAnnualInteractions,
                decadeAnnualRelationScore,
                combinedScore,
                combinedLevel,
                summarySignals,
                RULE_VERSION
        );
    }

    private double calculateCombinedScore(
            LuckPeriodEvaluation decadeEvaluation,
            LuckPeriodEvaluation annualEvaluation,
            double decadeAnnualRelationScore
    ) {
        double score;

        if (decadeEvaluation == null) {
            score =
                    annualEvaluation.totalScore()
                            + decadeAnnualRelationScore;
        } else {
            /*
             * 대운은 장기 배경이므로 60%,
             * 세운은 해당 연도의 직접 작용이므로 40%를 적용한다.
             */
            score =
                    decadeEvaluation.totalScore()
                            * 0.6
                            + annualEvaluation.totalScore()
                            * 0.4
                            + decadeAnnualRelationScore;
        }

        return round(
                Math.max(
                        0.0,
                        Math.min(100.0, score)
                )
        );
    }

    private List<String> createSummarySignals(
            LuckPeriodEvaluation decadeEvaluation,
            LuckPeriodEvaluation annualEvaluation,
            List<LuckInteraction> interactions,
            double combinedScore,
            LuckEvaluationLevel combinedLevel
    ) {
        List<String> signals =
                new ArrayList<>();

        if (decadeEvaluation != null) {
            signals.add(
                    "현재 대운은 "
                            + decadeEvaluation
                            .pillar()
                            .koreanName()
                            + "이며 "
                            + decadeEvaluation
                            .level()
                            .getKoreanName()
                            + "으로 평가됨"
            );
        }

        signals.add(
                "선택 연도 세운은 "
                        + annualEvaluation
                        .pillar()
                        .koreanName()
                        + "이며 "
                        + annualEvaluation
                        .level()
                        .getKoreanName()
                        + "으로 평가됨"
        );

        long favorableCount =
                interactions.stream()
                        .filter(
                                LuckInteraction::favorable
                        )
                        .count();

        long challengingCount =
                interactions.size()
                        - favorableCount;

        signals.add(
                "대운과 세운 사이의 유리한 관계 "
                        + favorableCount
                        + "개, 부담 관계 "
                        + challengingCount
                        + "개가 확인됨"
        );

        signals.add(
                "종합 운 점수는 "
                        + combinedScore
                        + "점이며 "
                        + combinedLevel
                        .getKoreanName()
                        + "으로 판정됨"
        );

        return List.copyOf(signals);
    }

    private double round(
            double value
    ) {
        return Math.round(value * 100.0)
                / 100.0;
    }
}