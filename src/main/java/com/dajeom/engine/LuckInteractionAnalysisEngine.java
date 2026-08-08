package com.dajeom.engine;

import com.dajeom.analyzer.LuckRelationAnalyzer;
import com.dajeom.analyzer.LuckTenGodAnalyzer;
import com.dajeom.analyzer.UsefulElementInteractionAnalyzer;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.ElementCandidateMatch;
import com.dajeom.model.LuckBranchInteraction;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.LuckStemInteraction;
import com.dajeom.model.LuckTenGodInfluence;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.rule.LuckInteractionRule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 원국 + 대운 + 세운의
 * 종합 상호작용 분석 Engine.
 *
 * 처리:
 *
 * NatalChartAdvancedAnalysis
 *      +
 * DecadeLuckAnalysis
 *      +
 * AnnualLuckAnalysis
 *
 *          ↓
 *
 * 대상 세운 시점의 대운 결정
 *
 *          ↓
 *
 * 원국 ↔ 대운
 * 원국 ↔ 세운
 * 대운 ↔ 세운
 *
 *          ↓
 *
 * 천간 관계
 * 지지 관계
 * 십성
 * 용신 후보 오행 유입
 *
 *          ↓
 *
 * LuckInteractionAnalysis
 */
@Component
public class LuckInteractionAnalysisEngine {

    private final LuckRelationAnalyzer
            luckRelationAnalyzer;

    private final LuckTenGodAnalyzer
            luckTenGodAnalyzer;

    private final UsefulElementInteractionAnalyzer
            usefulElementInteractionAnalyzer;

    public LuckInteractionAnalysisEngine(
            LuckRelationAnalyzer luckRelationAnalyzer,
            LuckTenGodAnalyzer luckTenGodAnalyzer,
            UsefulElementInteractionAnalyzer
                    usefulElementInteractionAnalyzer
    ) {

        this.luckRelationAnalyzer =
                luckRelationAnalyzer;

        this.luckTenGodAnalyzer =
                luckTenGodAnalyzer;

        this.usefulElementInteractionAnalyzer =
                usefulElementInteractionAnalyzer;
    }

    public LuckInteractionAnalysis analyze(
            NatalChartAdvancedAnalysis natalChart,
            DecadeLuckAnalysis decadeLuckAnalysis,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        Objects.requireNonNull(
                natalChart,
                "고차 원국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "대운 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "세운 분석 결과는 필수입니다."
        );

        /*
         * 1.
         * 해당 세운을 분석할 때 사용할
         * 대운 구간을 결정한다.
         */
        DecadeLuckPeriod decadeLuckPeriod =
                resolveDecadeLuckPeriod(
                        decadeLuckAnalysis,
                        annualLuckAnalysis
                );

        /*
         * 2.
         * 세운 기간 안에서
         * 대운 교체가 발생하는지 확인한다.
         */
        boolean decadeTransition =
                hasDecadeTransitionDuringAnnualLuck(
                        decadeLuckAnalysis,
                        annualLuckAnalysis
                );

        /*
         * 3.
         * 원국/대운/세운 천간 관계.
         */
        List<LuckStemInteraction>
                stemInteractions =
                luckRelationAnalyzer
                        .analyzeStemInteractions(
                                natalChart
                                        .fourPillars(),
                                decadeLuckPeriod,
                                annualLuckAnalysis
                        );

        /*
         * 4.
         * 원국/대운/세운 지지 관계.
         */
        List<LuckBranchInteraction>
                branchInteractions =
                luckRelationAnalyzer
                        .analyzeBranchInteractions(
                                natalChart
                                        .fourPillars(),
                                decadeLuckPeriod,
                                annualLuckAnalysis
                        );

        /*
         * 5.
         * 대운/세운의 십성.
         */
        List<LuckTenGodInfluence>
                tenGodInfluences =
                luckTenGodAnalyzer
                        .analyze(
                                natalChart
                                        .coreAnalysis()
                                        .dayMaster(),
                                decadeLuckPeriod,
                                annualLuckAnalysis
                        );

        /*
         * 6.
         * 대운/세운에서 들어오는 오행이
         * 용신/희신/기신/조후/통관 후보와
         * 일치하는지 분석.
         */
        List<ElementCandidateMatch>
                elementCandidateMatches =
                usefulElementInteractionAnalyzer
                        .analyze(
                                natalChart
                                        .usefulGodAnalysis(),
                                decadeLuckPeriod,
                                annualLuckAnalysis
                        );

        return new LuckInteractionAnalysis(
                decadeLuckPeriod,
                annualLuckAnalysis,
                stemInteractions,
                branchInteractions,
                tenGodInfluences,
                elementCandidateMatches,
                decadeLuckAnalysis
                        .calculationAvailable(),
                decadeTransition,
                LuckInteractionRule.RULE_VERSION
        );
    }

    /**
     * 분석 대상 세운에 사용할 대운을 찾는다.
     *
     * 1.
     * decadeLuckAnalysis의 기준 시각이
     * 해당 세운 안에 들어 있다면
     * 현재 대운을 우선 사용한다.
     *
     * 2.
     * 미래 또는 과거 세운을 분석한다면
     * 해당 세운 시작 시점에 작동 중인
     * 대운을 찾는다.
     */
    private DecadeLuckPeriod resolveDecadeLuckPeriod(
            DecadeLuckAnalysis decadeLuckAnalysis,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        if (
                !decadeLuckAnalysis
                        .calculationAvailable()
        ) {

            return null;
        }

        /*
         * 현재 조회시점과
         * 분석 대상 세운이 같은 경우.
         */
        if (
                annualLuckAnalysis
                        .isActiveAt(
                                decadeLuckAnalysis
                                        .targetDateTime()
                        )
        ) {

            DecadeLuckPeriod current =
                    decadeLuckAnalysis
                            .currentPeriod();

            if (current != null) {

                return current;
            }
        }

        /*
         * 미래/과거 특정 세운 분석.
         *
         * 해당 세운 시작 시각에
         * 어떤 대운이 작동하는지 찾는다.
         */
        return decadeLuckAnalysis
                .periods()
                .stream()
                .filter(
                        period ->
                                period.contains(
                                        annualLuckAnalysis
                                                .startDateTime()
                                )
                )
                .findFirst()
                .orElse(null);
    }

    /**
     * 해당 세운 기간 안에서
     * 새로운 대운이 시작되는지 확인한다.
     *
     * 이 값이 true라면:
     *
     * 같은 세운 안에서도
     * 대운 배경이 중간에 바뀐다.
     *
     * 이후 해석 단계에서
     * 중요한 주의 정보로 사용할 수 있다.
     */
    private boolean
    hasDecadeTransitionDuringAnnualLuck(
            DecadeLuckAnalysis decadeLuckAnalysis,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        if (
                !decadeLuckAnalysis
                        .calculationAvailable()
        ) {

            return false;
        }

        return decadeLuckAnalysis
                .periods()
                .stream()
                .anyMatch(
                        period ->
                                LuckInteractionRule
                                        .startsInsideAnnualLuck(
                                                period,
                                                annualLuckAnalysis
                                                        .annualLuck()
                                        )
                );
    }
}