package com.dajeom.engine;

import com.dajeom.model.NatalConditionalResponseAnalysis;
import com.dajeom.model.NatalCrossFunctionalAnalysis;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalPersonalityDeterministicAnalysis;
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
 *
 * 이 Engine은 새로운 명리 계산을 하지 않는다.
 *
 * 지금까지 분리해 둔 Personality 분석 계층을
 * 정확한 순서로 실행하는 orchestration layer다.
 *
 * =================================================
 *
 * 흐름:
 *
 * NatalChartAdvancedAnalysis
 * +
 * TenGodCompositeAnalysis
 *
 * ↓
 *
 * NatalFunctionalAnalysis
 *
 * ↓
 *
 * NatalCrossFunctionalAnalysis
 *
 * ↓
 *
 * NatalConditionalResponseAnalysis
 *
 * ↓
 *
 * NatalPersonalityPatternAnalysis
 *
 * ↓
 *
 * NatalPersonalityDeterministicAnalysis
 *
 * ↓
 *
 * NatalPersonalityFindingAnalysis
 *
 * ↓
 *
 * NatalPersonalityFindingSynthesisAnalysis
 *
 * ↓
 *
 * NatalPersonalityStatementFactAnalysis
 *
 * ↓
 *
 * NatalPersonalityStatementSelectionAnalysis
 *
 * ↓
 *
 * NatalPersonalityStatementBundleAnalysis
 *
 * ↓
 *
 * NatalPersonalityStatementContract
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 이 Pipeline에서는:
 *
 * - 새 점수
 * - 새 Threshold
 * - 새 십성 판정
 * - 새 심리판정
 *
 * 을 만들지 않는다.
 *
 * 각 하위 Engine의 결과를
 * 다음 계층으로 전달하기만 한다.
 */
@Component
public class NatalPersonalityStatementContractPipelineEngine {

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
         * 원국 기능축
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
         * 2.
         * 기능축 교차작동
         * =================================================
         */

        NatalCrossFunctionalAnalysis crossFunctionalAnalysis =
                natalCrossFunctionalAnalysisEngine
                        .analyze(
                                functionalAnalysis
                        );

        /*
         * =================================================
         * 3.
         * 조건부 작동 Pattern
         * =================================================
         */

        NatalConditionalResponseAnalysis
                conditionalResponseAnalysis =
                natalConditionalResponseAnalysisEngine
                        .analyze(
                                crossFunctionalAnalysis
                        );

        /*
         * =================================================
         * 4.
         * Personality Pattern
         * =================================================
         */

        NatalPersonalityPatternAnalysis
                personalityPatternAnalysis =
                natalPersonalityPatternAnalysisEngine
                        .analyze(
                                conditionalResponseAnalysis
                        );

        /*
         * =================================================
         * 5.
         * Deterministic Judgment
         * =================================================
         */

        NatalPersonalityDeterministicAnalysis
                deterministicAnalysis =
                natalPersonalityDeterministicJudgmentEngine
                        .analyze(
                                personalityPatternAnalysis
                        );

        /*
         * =================================================
         * 6.
         * Finding
         * =================================================
         */

        NatalPersonalityFindingAnalysis
                findingAnalysis =
                natalPersonalityFindingAnalysisEngine
                        .analyze(
                                deterministicAnalysis
                        );

        /*
         * =================================================
         * 7.
         * Finding Synthesis
         * =================================================
         */

        NatalPersonalityFindingSynthesisAnalysis
                synthesisAnalysis =
                natalPersonalityFindingSynthesisEngine
                        .analyze(
                                findingAnalysis
                        );

        /*
         * =================================================
         * 8.
         * Statement Fact
         * =================================================
         */

        NatalPersonalityStatementFactAnalysis
                statementFactAnalysis =
                natalPersonalityStatementFactAnalysisEngine
                        .analyze(
                                synthesisAnalysis
                        );

        /*
         * =================================================
         * 9.
         * Statement Selection
         * =================================================
         */

        NatalPersonalityStatementSelectionAnalysis
                selectionAnalysis =
                natalPersonalityStatementSelectionEngine
                        .analyze(
                                statementFactAnalysis
                        );

        /*
         * =================================================
         * 10.
         * Statement Bundle
         * =================================================
         */

        NatalPersonalityStatementBundleAnalysis
                bundleAnalysis =
                natalPersonalityStatementBundleEngine
                        .analyze(
                                selectionAnalysis
                        );

        /*
         * =================================================
         * 11.
         * 최종 Personality Statement Contract
         * =================================================
         */

        return Objects.requireNonNull(
                natalPersonalityStatementContractEngine
                        .analyze(
                                bundleAnalysis
                        ),
                "Personality Statement Contract Engine이 null을 반환했습니다."
        );
    }
}