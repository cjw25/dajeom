package com.dajeom.engine;

import com.dajeom.model.NatalConditionalResponseAnalysis;
import com.dajeom.model.NatalCrossFunctionalAnalysis;
import com.dajeom.model.NatalExactTenGodAnalysis;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalPersonalityDeterministicAnalysis;
import com.dajeom.model.NatalPersonalityExactTenGodSelection;
import com.dajeom.model.NatalPersonalityFindingAnalysis;
import com.dajeom.model.NatalPersonalityFindingSynthesisAnalysis;
import com.dajeom.model.NatalPersonalityPatternAnalysis;
import com.dajeom.model.NatalPersonalityStatementBundleAnalysis;
import com.dajeom.model.NatalPersonalityStatementContract;
import com.dajeom.model.NatalPersonalityStatementFactAnalysis;
import com.dajeom.model.NatalPersonalityStatementSelectionAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 원국 기본성향 분석 전체 Pipeline.
 *
 * =================================================
 * 공식 사용자 Meaning 우선선
 * =================================================
 *
 * 10-1 정확한 10십성 분포
 *
 * +
 *
 * 5차 실제 주격 정확한 십성
 *
 * ↓
 *
 * NatalExactTenGodAnalysis
 *
 * ↓
 *
 * NatalPersonalityExactTenGodSelection
 *
 * ↓
 *
 * Exact TenGod Statement Fact
 *
 * ↓
 *
 * Final Personality Contract
 *
 * =================================================
 * 전통 Pattern / 호환선
 * =================================================
 *
 * 기존 5개 기능축 분석은:
 *
 * - 전통 Cross Pattern의 성립 확인
 * - 기존 API 구조 호환
 *
 * 을 위해 유지한다.
 *
 * =================================================
 *
 * 그러나:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * 자체가 정확한 10십성을 대신하는
 * 사용자 Personality Meaning의
 * 공식 기준이 되지는 않는다.
 *
 * =================================================
 *
 * 2차 Finding Synthesis 역시
 * 공식 실행에서는 비활성이다.
 */
@Component
public class NatalPersonalityStatementContractPipelineEngine {

    /*
     * =================================================
     * Exact TenGod
     * =================================================
     */

    private final NatalExactTenGodAnalysisEngine
            natalExactTenGodAnalysisEngine;

    private final NatalPersonalityExactTenGodSelectionEngine
            natalPersonalityExactTenGodSelectionEngine;

    /*
     * =================================================
     * 기존 Pattern / 호환 Pipeline
     * =================================================
     */

    private final NatalFunctionalAnalysisEngine
            natalFunctionalAnalysisEngine;

    private final NatalCrossFunctionalAnalysisEngine
            natalCrossFunctionalAnalysisEngine;

    private final NatalConditionalResponseAnalysisEngine
            natalConditionalResponseAnalysisEngine;

    private final NatalPersonalityPatternAnalysisEngine
            natalPersonalityPatternAnalysisEngine;

    private final NatalPersonalityDeterministicJudgmentEngine
            natalPersonalityDeterministicJudgmentEngine;

    private final NatalPersonalityFindingAnalysisEngine
            natalPersonalityFindingAnalysisEngine;

    private final NatalPersonalityFindingSynthesisEngine
            natalPersonalityFindingSynthesisEngine;

    private final NatalPersonalityStatementFactAnalysisEngine
            natalPersonalityStatementFactAnalysisEngine;

    private final NatalPersonalityStatementSelectionEngine
            natalPersonalityStatementSelectionEngine;

    private final NatalPersonalityStatementBundleEngine
            natalPersonalityStatementBundleEngine;

    private final NatalPersonalityStatementContractEngine
            natalPersonalityStatementContractEngine;

    public NatalPersonalityStatementContractPipelineEngine(

            NatalExactTenGodAnalysisEngine
                    natalExactTenGodAnalysisEngine,

            NatalPersonalityExactTenGodSelectionEngine
                    natalPersonalityExactTenGodSelectionEngine,

            NatalFunctionalAnalysisEngine
                    natalFunctionalAnalysisEngine,

            NatalCrossFunctionalAnalysisEngine
                    natalCrossFunctionalAnalysisEngine,

            NatalConditionalResponseAnalysisEngine
                    natalConditionalResponseAnalysisEngine,

            NatalPersonalityPatternAnalysisEngine
                    natalPersonalityPatternAnalysisEngine,

            NatalPersonalityDeterministicJudgmentEngine
                    natalPersonalityDeterministicJudgmentEngine,

            NatalPersonalityFindingAnalysisEngine
                    natalPersonalityFindingAnalysisEngine,

            NatalPersonalityFindingSynthesisEngine
                    natalPersonalityFindingSynthesisEngine,

            NatalPersonalityStatementFactAnalysisEngine
                    natalPersonalityStatementFactAnalysisEngine,

            NatalPersonalityStatementSelectionEngine
                    natalPersonalityStatementSelectionEngine,

            NatalPersonalityStatementBundleEngine
                    natalPersonalityStatementBundleEngine,

            NatalPersonalityStatementContractEngine
                    natalPersonalityStatementContractEngine

    ) {

        this.natalExactTenGodAnalysisEngine =
                Objects.requireNonNull(
                        natalExactTenGodAnalysisEngine
                );

        this.natalPersonalityExactTenGodSelectionEngine =
                Objects.requireNonNull(
                        natalPersonalityExactTenGodSelectionEngine
                );

        this.natalFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        natalFunctionalAnalysisEngine
                );

        this.natalCrossFunctionalAnalysisEngine =
                Objects.requireNonNull(
                        natalCrossFunctionalAnalysisEngine
                );

        this.natalConditionalResponseAnalysisEngine =
                Objects.requireNonNull(
                        natalConditionalResponseAnalysisEngine
                );

        this.natalPersonalityPatternAnalysisEngine =
                Objects.requireNonNull(
                        natalPersonalityPatternAnalysisEngine
                );

        this.natalPersonalityDeterministicJudgmentEngine =
                Objects.requireNonNull(
                        natalPersonalityDeterministicJudgmentEngine
                );

        this.natalPersonalityFindingAnalysisEngine =
                Objects.requireNonNull(
                        natalPersonalityFindingAnalysisEngine
                );

        this.natalPersonalityFindingSynthesisEngine =
                Objects.requireNonNull(
                        natalPersonalityFindingSynthesisEngine
                );

        this.natalPersonalityStatementFactAnalysisEngine =
                Objects.requireNonNull(
                        natalPersonalityStatementFactAnalysisEngine
                );

        this.natalPersonalityStatementSelectionEngine =
                Objects.requireNonNull(
                        natalPersonalityStatementSelectionEngine
                );

        this.natalPersonalityStatementBundleEngine =
                Objects.requireNonNull(
                        natalPersonalityStatementBundleEngine
                );

        this.natalPersonalityStatementContractEngine =
                Objects.requireNonNull(
                        natalPersonalityStatementContractEngine
                );
    }

    public NatalPersonalityStatementContract analyze(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "Personality Pipeline에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "Personality Pipeline에는 TenGodCompositeAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 1.
         * 정확한 10십성 공식 계층
         * =================================================
         */

        NatalExactTenGodAnalysis exactTenGodAnalysis =
                natalExactTenGodAnalysisEngine
                        .analyze(

                                advancedAnalysis,

                                tenGodCompositeAnalysis
                        );

        NatalPersonalityExactTenGodSelection exactTenGodSelection =
                natalPersonalityExactTenGodSelectionEngine
                        .analyze(
                                exactTenGodAnalysis
                        );

        /*
         * =================================================
         * 2.
         * 기존 기능축 분석
         *
         * 전통 Pattern/호환 목적.
         * =================================================
         */

        NatalFunctionalAnalysis functionalAnalysis =
                natalFunctionalAnalysisEngine
                        .analyze(

                                advancedAnalysis,

                                tenGodCompositeAnalysis
                        );

        /*
         * =================================================
         * 3.
         * 기존 Cross 분석
         * =================================================
         */

        NatalCrossFunctionalAnalysis crossFunctionalAnalysis =
                natalCrossFunctionalAnalysisEngine
                        .analyze(
                                functionalAnalysis
                        );

        /*
         * =================================================
         * 4.
         * 조건부 Pattern
         * =================================================
         */

        NatalConditionalResponseAnalysis conditionalResponseAnalysis =
                natalConditionalResponseAnalysisEngine
                        .analyze(
                                crossFunctionalAnalysis
                        );

        /*
         * =================================================
         * 5.
         * Personality Pattern
         * =================================================
         */

        NatalPersonalityPatternAnalysis personalityPatternAnalysis =
                natalPersonalityPatternAnalysisEngine
                        .analyze(
                                conditionalResponseAnalysis
                        );

        /*
         * =================================================
         * 6.
         * Deterministic Judgment
         * =================================================
         */

        NatalPersonalityDeterministicAnalysis deterministicAnalysis =
                natalPersonalityDeterministicJudgmentEngine
                        .analyze(
                                personalityPatternAnalysis
                        );

        /*
         * =================================================
         * 7.
         * Finding
         * =================================================
         */

        NatalPersonalityFindingAnalysis findingAnalysis =
                natalPersonalityFindingAnalysisEngine
                        .analyze(
                                deterministicAnalysis
                        );

        /*
         * =================================================
         * 8.
         * Legacy Synthesis 호환 경계
         * =================================================
         */

        NatalPersonalityFindingSynthesisAnalysis synthesisAnalysis =
                natalPersonalityFindingSynthesisEngine
                        .analyze(
                                findingAnalysis
                        );

        if (
                !synthesisAnalysis
                        .syntheses()
                        .isEmpty()
        ) {

            throw new IllegalStateException(
                    "공식 Personality Pipeline에서는 "
                            + "2차 Finding Synthesis를 생성할 수 없습니다."
            );
        }

        /*
         * =================================================
         * 9.
         * Statement Fact
         * =================================================
         *
         * 정확한 10십성을
         * 여기서 공식 Fact로 먼저 보존한다.
         */

        NatalPersonalityStatementFactAnalysis statementFactAnalysis =
                natalPersonalityStatementFactAnalysisEngine
                        .analyze(

                                synthesisAnalysis,

                                exactTenGodSelection
                        );

        /*
         * =================================================
         * Exact TenGod Identity 확인
         * =================================================
         */

        if (
                statementFactAnalysis
                        .exactTenGodSelection()
                        .orElseThrow()
                        != exactTenGodSelection
        ) {

            throw new IllegalStateException(
                    "Statement Fact Analysis가 "
                            + "공식 Exact TenGod Selection Identity를 "
                            + "보존하지 않았습니다."
            );
        }

        /*
         * =================================================
         * 10.
         * 기존 Pattern Selection
         * =================================================
         *
         * Selection Engine은 기존 Finding Fact를
         * 계속 분류한다.
         *
         * exactTenGodFacts는 별도 공식선으로
         * Contract까지 직접 보존된다.
         */

        NatalPersonalityStatementSelectionAnalysis selectionAnalysis =
                natalPersonalityStatementSelectionEngine
                        .analyze(
                                statementFactAnalysis
                        );

        /*
         * =================================================
         * 11.
         * 기존 Pattern Bundle
         * =================================================
         */

        NatalPersonalityStatementBundleAnalysis bundleAnalysis =
                natalPersonalityStatementBundleEngine
                        .analyze(
                                selectionAnalysis
                        );

        /*
         * =================================================
         * 12.
         * 최종 Personality Contract
         * =================================================
         *
         * Contract의 호환 생성자가
         *
         * bundleAnalysis
         * ↓
         * statementFactAnalysis
         *
         * 에 보존된:
         *
         * exactTenGodSelection
         * exactTenGodFacts
         *
         * 를 자동 승계한다.
         */

        NatalPersonalityStatementContract contract =
                Objects.requireNonNull(

                        natalPersonalityStatementContractEngine
                                .analyze(
                                        bundleAnalysis
                                ),

                        "Personality Statement Contract Engine이 null을 반환했습니다."
                );

        /*
         * =================================================
         * 최종 Contract Identity 검증
         * =================================================
         */

        if (
                contract
                        .exactTenGodSelection()
                        .orElseThrow()
                        != exactTenGodSelection
        ) {

            throw new IllegalStateException(
                    "최종 Personality Contract가 "
                            + "공식 Exact TenGod Selection Identity를 "
                            + "보존하지 않았습니다."
            );
        }

        return contract;
    }
}