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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 원국 + 대운 + 세운의
 * 종합 상호작용 분석 Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * NatalChartAdvancedAnalysis
 * +
 * DecadeLuckAnalysis
 * +
 * AnnualLuckAnalysis
 *
 * =================================================
 *
 * 처리:
 *
 * 1. 분석 대상 세운 시작 시점의 대운 결정
 * 2. 세운 기간 중 대운 전환 여부 확인
 * 3. 원국 / 대운 / 세운 천간 관계
 * 4. 원국 / 대운 / 세운 지지 관계
 * 5. 대운 / 세운 십성
 * 6. 용신 / 희신 / 기신 / 조후 / 통관 오행 유입
 *
 * =================================================
 *
 * 주의:
 *
 * 현재 13차에서는
 * DecadeLuckAnalysis 내부에
 * "현재 조회시각"이나 "현재 대운"을 저장하지 않는다.
 *
 * 필요한 날짜를 명시적으로 넘겨:
 *
 * exactPeriodAt()
 * definitePeriodAt()
 *
 * 등으로 조회한다.
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
            UsefulElementInteractionAnalyzer usefulElementInteractionAnalyzer
    ) {

        this.luckRelationAnalyzer =
                Objects.requireNonNull(
                        luckRelationAnalyzer,
                        "LuckRelationAnalyzer가 필요합니다."
                );

        this.luckTenGodAnalyzer =
                Objects.requireNonNull(
                        luckTenGodAnalyzer,
                        "LuckTenGodAnalyzer가 필요합니다."
                );

        this.usefulElementInteractionAnalyzer =
                Objects.requireNonNull(
                        usefulElementInteractionAnalyzer,
                        "UsefulElementInteractionAnalyzer가 필요합니다."
                );
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
         * =================================================
         * 1.
         * 해당 세운을 분석할 때 사용할
         * 대운 구간 결정
         * =================================================
         */
        DecadeLuckPeriod decadeLuckPeriod =
                resolveDecadeLuckPeriod(
                        decadeLuckAnalysis,
                        annualLuckAnalysis
                );

        /*
         * =================================================
         * 2.
         * 세운 기간 안에서
         * 대운 교체가 발생하는지 확인
         * =================================================
         */
        boolean decadeTransition =
                hasDecadeTransitionDuringAnnualLuck(
                        decadeLuckAnalysis,
                        annualLuckAnalysis
                );

        /*
         * =================================================
         * 3.
         * 원국 / 대운 / 세운 천간 관계
         * =================================================
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
         * =================================================
         * 4.
         * 원국 / 대운 / 세운 지지 관계
         * =================================================
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
         * =================================================
         * 5.
         * 대운 / 세운 십성
         * =================================================
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
         * =================================================
         * 6.
         * 대운 / 세운에서 들어오는 오행과
         *
         * 용신
         * 희신
         * 기신
         * 조후
         * 통관
         *
         * 후보의 일치 여부
         * =================================================
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

                /*
                 * 이 Engine은 대운 Context가 아니라
                 * 실제 대운 Period Sequence를 사용한다.
                 *
                 * 따라서 예전 calculationAvailable()의
                 * 현재 대응값은 sequenceAvailable()이다.
                 */
                decadeLuckAnalysis
                        .sequenceAvailable(),

                decadeTransition,

                LuckInteractionRule.RULE_VERSION
        );
    }

    /**
     * 분석 대상 세운의 시작 시점에
     * 사용할 수 있는 대운 하나를 결정한다.
     *
     * =================================================
     *
     * 현재 13차 정책:
     *
     * 특정 기준시점을 Aggregate 내부에 저장하지 않는다.
     *
     * 따라서:
     *
     * annualStart
     *
     * 를 명시적으로 넘겨
     * DecadeLuckAnalysis에서 조회한다.
     *
     * =================================================
     *
     * 우선순위:
     *
     * 1. definitePeriodAt()
     *
     * 시간 미상 등 불확실성이 있어도
     * 어떤 가능한 출생시간을 적용해도
     * 동일 대운으로 확정되는 경우.
     *
     * 2. exactPeriodAt()
     *
     * 정확한 교운시점 구조에서
     * 해당 시점의 대운을 찾을 수 있는 경우.
     *
     * =================================================
     *
     * POSSIBLE 대운이 여러 개뿐인 경우에는
     * 임의로 하나를 선택하지 않는다.
     *
     * 이 V1 Engine은 단일 DecadeLuckPeriod만
     * 받을 수 있으므로 null로 남긴다.
     */
    private DecadeLuckPeriod resolveDecadeLuckPeriod(
            DecadeLuckAnalysis decadeLuckAnalysis,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        if (
                !decadeLuckAnalysis
                        .sequenceAvailable()
        ) {

            return null;
        }

        ZonedDateTime annualStart =
                annualLuckAnalysis
                        .period()
                        .startDateTimeInclusive();

        /*
         * 시간 미상 범위가 있더라도
         * 확정적으로 활성화되는 대운이 있으면
         * 그것을 우선 사용한다.
         */
        DecadeLuckPeriod definite =
                decadeLuckAnalysis
                        .definitePeriodAt(
                                annualStart
                        )
                        .orElse(
                                null
                        );

        if (
                definite != null
        ) {

            return definite;
        }

        /*
         * 정확한 교운 구조라면
         * exactPeriodAt()으로 조회한다.
         */
        return decadeLuckAnalysis
                .exactPeriodAt(
                        annualStart
                )
                .orElse(
                        null
                );
    }

    /**
     * 해당 세운 기간 안에서
     * 새로운 대운 시작이 발생하는지 확인한다.
     *
     * =================================================
     *
     * true:
     *
     * 하나의 세운 안에서
     * 대운 배경이 중간에 변경된다.
     *
     * =================================================
     *
     * 여기에서도 대운 Sequence 자체가 없으면
     * 전환 여부를 계산할 수 없으므로 false.
     */
    private boolean
    hasDecadeTransitionDuringAnnualLuck(
            DecadeLuckAnalysis decadeLuckAnalysis,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        if (
                !decadeLuckAnalysis
                        .sequenceAvailable()
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
                                                        .period()
                                        )
                );
    }
}