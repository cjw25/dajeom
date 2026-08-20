package com.dajeom.engine;

import com.dajeom.model.DecadeFunctionEffect;
import com.dajeom.model.DecadeFunctionalAnalysis;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckContextSignal;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.DecadeLuckPeriodNatalRelationAnalysis;
import com.dajeom.model.DecadeLuckPeriodTenGodAnalysis;
import com.dajeom.model.DecadeLuckTenGodEvidence;
import com.dajeom.model.DecadePeriodFunctionalAnalysis;
import com.dajeom.model.NatalCrossFunctionalAnalysis;
import com.dajeom.model.NatalCrossFunctionalPattern;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 대운을 원국 5대 기능축으로
 * 투영하는 첫 번째 Functional Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * NatalCrossFunctionalAnalysis
 *
 * +
 *
 * DecadeLuckAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * 각 대운 Period마다:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * 5개 Effect를 정확히 하나씩 생성한다.
 *
 * =================================================
 *
 * 이 Engine이 하는 것:
 *
 * 1.
 * 대운 십성을 Function Family로 분류
 *
 * 2.
 * Function을 명시한
 * 13-6 Context Signal 연결
 *
 * 3.
 * 해당 Function과 관련된
 * 원국 Cross Pattern 보존
 *
 * 4.
 * 특정 Function에 귀속할 수 없는
 * Context는 Global Context로 보존
 *
 * =================================================
 *
 * 이 Engine이 하지 않는 것:
 *
 * - 대운 길흉 판정
 * - 신강신약 재계산
 * - 용신 재선정
 * - 격국 재판정
 * - Context Status 점수화
 * - Relation 자체 재계산
 * - 합화 자체 재계산
 * - Cross Pattern 자동 활성화 확정
 *
 * =================================================
 *
 * 특히:
 *
 * SUPPORTED
 * =
 * Function 강화
 *
 * CONFLICTED
 * =
 * Function 약화
 *
 * 로 단순 변환하지 않는다.
 */
@Component
public class DecadeFunctionalAnalysisEngine {

    public DecadeFunctionalAnalysis analyze(

            NatalCrossFunctionalAnalysis
                    natalCrossFunctionalAnalysis,

            DecadeLuckAnalysis
                    decadeLuckAnalysis

    ) {

        Objects.requireNonNull(
                natalCrossFunctionalAnalysis,
                "DecadeFunctionalAnalysis에는 NatalCrossFunctionalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "DecadeFunctionalAnalysis에는 DecadeLuckAnalysis가 필요합니다."
        );

        boolean completeChart =
                decadeLuckAnalysis
                        .natalAnalysis()
                        .completeChart();

        /*
         * =================================================
         * 13차 분석 자체가 불가능한 경우.
         *
         * 억지로 대운 기능효과를 만들지 않는다.
         * =================================================
         */

        if (
                !decadeLuckAnalysis
                        .analysisAvailable()
        ) {

            return new DecadeFunctionalAnalysis(
                    natalCrossFunctionalAnalysis,
                    decadeLuckAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<DecadePeriodFunctionalAnalysis> periodAnalyses =
                new ArrayList<>();

        for (
                DecadeLuckPeriod period
                : decadeLuckAnalysis.periods()
        ) {

            periodAnalyses.add(
                    analyzePeriod(
                            natalCrossFunctionalAnalysis,
                            decadeLuckAnalysis,
                            period,
                            completeChart
                    )
            );
        }

        return new DecadeFunctionalAnalysis(
                natalCrossFunctionalAnalysis,
                decadeLuckAnalysis,
                List.copyOf(
                        periodAnalyses
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    private DecadePeriodFunctionalAnalysis analyzePeriod(

            NatalCrossFunctionalAnalysis
                    natalCrossFunctionalAnalysis,

            DecadeLuckAnalysis
                    decadeLuckAnalysis,

            DecadeLuckPeriod period,

            boolean completeChart

    ) {

        int sequenceNumber =
                period.sequenceNumber();

        DecadeLuckPeriodTenGodAnalysis tenGodAnalysis =
                decadeLuckAnalysis
                        .tenGodAnalysisOf(
                                sequenceNumber
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "대운 13-4 십성 분석을 찾을 수 없습니다: "
                                                        + sequenceNumber
                                        )
                        );

        DecadeLuckPeriodNatalRelationAnalysis
                natalRelationAnalysis =
                decadeLuckAnalysis
                        .natalRelationAnalysisOf(
                                sequenceNumber
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "대운 13-5 원국 관계 분석을 찾을 수 없습니다: "
                                                        + sequenceNumber
                                        )
                        );

        DecadeLuckPeriodContextAnalysis contextAnalysis =
                decadeLuckAnalysis
                        .contextOf(
                                sequenceNumber
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "대운 13-6 Context 분석을 찾을 수 없습니다: "
                                                        + sequenceNumber
                                        )
                        );

        /*
         * =================================================
         * 5개 Function Effect
         * =================================================
         */

        List<DecadeFunctionEffect> functionEffects =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            functionEffects.add(
                    createFunctionEffect(
                            natalCrossFunctionalAnalysis,
                            period,
                            tenGodAnalysis,
                            contextAnalysis,
                            functionType,
                            completeChart
                    )
            );
        }

        /*
         * =================================================
         * Function에 직접 귀속할 수 없는 Context
         * =================================================
         *
         * tenGodTypes가 비어 있다고 해서
         * 불필요한 Signal이 아니다.
         *
         * USEFUL_GOD
         * STRUCTURE
         * RELATION
         * TRANSFORMATION
         *
         * 같은 전역 구조 Context일 수 있으므로
         * 별도 보존한다.
         */

        List<DecadeLuckContextSignal> globalContextSignals =
                contextAnalysis
                        .signals()
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .tenGodTypes()
                                                .isEmpty()
                        )
                        .toList();

        return new DecadePeriodFunctionalAnalysis(
                period,
                tenGodAnalysis,
                natalRelationAnalysis,
                contextAnalysis,
                List.copyOf(
                        functionEffects
                ),
                globalContextSignals,
                completeChart
        );
    }

    /*
     * =================================================
     * Function Effect
     * =================================================
     */

    private DecadeFunctionEffect createFunctionEffect(

            NatalCrossFunctionalAnalysis
                    natalCrossFunctionalAnalysis,

            DecadeLuckPeriod period,

            DecadeLuckPeriodTenGodAnalysis
                    tenGodAnalysis,

            DecadeLuckPeriodContextAnalysis
                    contextAnalysis,

            NatalTenGodFunctionType
                    functionType,

            boolean completeChart

    ) {

        /*
         * =================================================
         * 1.
         * 대운에서 직접 들어온 십성
         * =================================================
         */

        List<DecadeLuckTenGodEvidence>
                directTenGodEvidences =
                tenGodAnalysis
                        .evidences()
                        .stream()
                        .filter(
                                evidence ->
                                        functionType
                                                .contains(
                                                        evidence
                                                                .tenGodType()
                                                )
                        )
                        .toList();

        /*
         * =================================================
         * 2.
         * 해당 기능축을 명시하는 13-6 Signal
         * =================================================
         *
         * Signal Axis가 무엇이든
         * tenGodTypes에 해당 Function Family가
         * 실제 명시되어 있을 때만 연결한다.
         *
         * 예:
         *
         * TEN_GOD
         * TRANSFORMATION
         * STRUCTURE
         *
         * 등에서 tenGodTypes가 있다면
         * Function Context가 될 수 있다.
         *
         * 반대로 tenGodTypes가 없으면
         * 특정 Function에 억지 귀속하지 않는다.
         */

        List<DecadeLuckContextSignal>
                functionContextSignals =
                contextAnalysis
                        .signals()
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .tenGodTypes()
                                                .stream()
                                                .anyMatch(
                                                        functionType
                                                                ::contains
                                                )
                        )
                        .toList();

        /*
         * =================================================
         * 3.
         * 원국 Cross Pattern
         * =================================================
         *
         * REFERENCE까지 삭제하지 않는다.
         *
         * 다만 현재 단계에서
         * 대운 때문에 활성화됐다고 확정하지 않는다.
         */

        List<NatalCrossFunctionalPattern>
                natalCrossPatterns =
                natalCrossFunctionalAnalysis
                        .patternsInvolving(
                                functionType
                        );

        return new DecadeFunctionEffect(
                period,
                functionType,
                directTenGodEvidences,
                functionContextSignals,
                natalCrossPatterns,
                completeChart
        );
    }
}