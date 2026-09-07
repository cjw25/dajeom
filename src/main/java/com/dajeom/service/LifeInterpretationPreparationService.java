package com.dajeom.service;

import com.dajeom.calculation.LifeClaimBundleCalculator;
import com.dajeom.calculation.LifeClaimNarrativeBundleCalculator;
import com.dajeom.calculation.LifeInterpretationPromptBuilder;
import com.dajeom.calculation.NatalLifeClaimPipelineCalculator;
import com.dajeom.calculation.PastFlowAnalysisCalculator;
import com.dajeom.calculation.PastFlowInterpretationPromptBuilder;
import com.dajeom.calculation.PastFlowNarrativeBundleCalculator;
import com.dajeom.domain.LifeClaimBundle;
import com.dajeom.domain.LifeClaimNarrativeBundle;
import com.dajeom.domain.LifeInterpretationCategory;
import com.dajeom.domain.LifeInterpretationPreparationResult;
import com.dajeom.domain.LifeInterpretationPrompt;
import com.dajeom.domain.LifeInterpretationPromptBundle;
import com.dajeom.domain.NatalLifeClaimPipelineResult;
import com.dajeom.domain.NatalMyeongriProfile;
import com.dajeom.domain.PastFlowAnalysis;
import com.dajeom.domain.PastFlowNarrativeBundle;
import com.dajeom.domain.PatternOfficerKillingsFinalPurificationAnalysis;
import com.dajeom.domain.PeriodMyeongriFacts;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 사주 7개 장문 풀이의
 * AI 호출 직전 준비 Service.
 *
 * =================================================
 *
 * 원국 6개 영역
 *
 * +
 *
 * 실제 기간 운 기반 PAST_FLOW
 *
 * 를 하나의 PromptBundle로 합친다.
 *
 * =================================================
 *
 * 官殺 최종 판정
 * =================================================
 *
 * node-level 최종 purification 결과가 존재하면
 *
 * prepare(
 *     profile,
 *     periodFacts,
 *     finalPurification,
 *     asOf
 * )
 *
 * 를 사용한다.
 */
@Service
public class LifeInterpretationPreparationService {

    private final NatalLifeClaimPipelineCalculator
            natalLifeClaimPipelineCalculator;

    private final LifeClaimBundleCalculator
            lifeClaimBundleCalculator;

    private final LifeClaimNarrativeBundleCalculator
            lifeClaimNarrativeBundleCalculator;

    private final LifeInterpretationPromptBuilder
            lifeInterpretationPromptBuilder;

    private final PastFlowAnalysisCalculator
            pastFlowAnalysisCalculator;

    private final PastFlowNarrativeBundleCalculator
            pastFlowNarrativeBundleCalculator;

    private final PastFlowInterpretationPromptBuilder
            pastFlowInterpretationPromptBuilder;

    public LifeInterpretationPreparationService(

            NatalLifeClaimPipelineCalculator
                    natalLifeClaimPipelineCalculator,

            LifeClaimBundleCalculator
                    lifeClaimBundleCalculator,

            LifeClaimNarrativeBundleCalculator
                    lifeClaimNarrativeBundleCalculator,

            LifeInterpretationPromptBuilder
                    lifeInterpretationPromptBuilder,

            PastFlowAnalysisCalculator
                    pastFlowAnalysisCalculator,

            PastFlowNarrativeBundleCalculator
                    pastFlowNarrativeBundleCalculator,

            PastFlowInterpretationPromptBuilder
                    pastFlowInterpretationPromptBuilder

    ) {

        this.natalLifeClaimPipelineCalculator =
                Objects.requireNonNull(
                        natalLifeClaimPipelineCalculator,
                        "NatalLifeClaimPipelineCalculator는 필수입니다."
                );

        this.lifeClaimBundleCalculator =
                Objects.requireNonNull(
                        lifeClaimBundleCalculator,
                        "LifeClaimBundleCalculator는 필수입니다."
                );

        this.lifeClaimNarrativeBundleCalculator =
                Objects.requireNonNull(
                        lifeClaimNarrativeBundleCalculator,
                        "LifeClaimNarrativeBundleCalculator는 필수입니다."
                );

        this.lifeInterpretationPromptBuilder =
                Objects.requireNonNull(
                        lifeInterpretationPromptBuilder,
                        "LifeInterpretationPromptBuilder는 필수입니다."
                );

        this.pastFlowAnalysisCalculator =
                Objects.requireNonNull(
                        pastFlowAnalysisCalculator,
                        "PastFlowAnalysisCalculator는 필수입니다."
                );

        this.pastFlowNarrativeBundleCalculator =
                Objects.requireNonNull(
                        pastFlowNarrativeBundleCalculator,
                        "PastFlowNarrativeBundleCalculator는 필수입니다."
                );

        this.pastFlowInterpretationPromptBuilder =
                Objects.requireNonNull(
                        pastFlowInterpretationPromptBuilder,
                        "PastFlowInterpretationPromptBuilder는 필수입니다."
                );
    }

    /*
     * =================================================
     * 기존 호환 경로
     * =================================================
     *
     * 官殺 node-level 최종 분석이 아직 연결되지 않은 경우.
     *
     * 이 경로에서는 Pipeline이
     * 보수적인 官殺 판정을 사용한다.
     */

    public LifeInterpretationPreparationResult prepare(

            NatalMyeongriProfile natalProfile,

            List<PeriodMyeongriFacts> periodFacts,

            LocalDateTime asOf

    ) {

        Objects.requireNonNull(
                natalProfile,
                "NatalMyeongriProfile은 필수입니다."
        );

        Objects.requireNonNull(
                periodFacts,
                "PeriodMyeongriFacts 목록은 필수입니다."
        );

        Objects.requireNonNull(
                asOf,
                "해석 기준 시각은 필수입니다."
        );

        NatalLifeClaimPipelineResult natalClaimResult =
                natalLifeClaimPipelineCalculator
                        .calculate(
                                natalProfile
                        );

        return prepareAfterNatalClaims(
                natalProfile,
                periodFacts,
                asOf,
                natalClaimResult
        );
    }

    /*
     * =================================================
     * 정식 경로
     * =================================================
     *
     * 官殺 node-level 최종 판정까지
     * 완료된 경우 사용.
     */

    public LifeInterpretationPreparationResult prepare(

            NatalMyeongriProfile natalProfile,

            List<PeriodMyeongriFacts> periodFacts,

            PatternOfficerKillingsFinalPurificationAnalysis
                    finalPurification,

            LocalDateTime asOf

    ) {

        Objects.requireNonNull(
                natalProfile,
                "NatalMyeongriProfile은 필수입니다."
        );

        Objects.requireNonNull(
                periodFacts,
                "PeriodMyeongriFacts 목록은 필수입니다."
        );

        Objects.requireNonNull(
                finalPurification,
                "官殺 최종 purification 분석은 필수입니다."
        );

        Objects.requireNonNull(
                asOf,
                "해석 기준 시각은 필수입니다."
        );

        NatalLifeClaimPipelineResult natalClaimResult =
                natalLifeClaimPipelineCalculator
                        .calculate(
                                natalProfile,
                                finalPurification
                        );

        return prepareAfterNatalClaims(
                natalProfile,
                periodFacts,
                asOf,
                natalClaimResult
        );
    }

    /*
     * =================================================
     * 원국만 준비 - 기존 호환
     * =================================================
     */

    public LifeInterpretationPreparationResult prepareNatalOnly(

            NatalMyeongriProfile natalProfile,

            LocalDateTime asOf

    ) {

        return prepare(
                natalProfile,
                List.of(),
                asOf
        );
    }

    /*
     * =================================================
     * 원국만 준비 - 최종 官殺 판정 포함
     * =================================================
     */

    public LifeInterpretationPreparationResult prepareNatalOnly(

            NatalMyeongriProfile natalProfile,

            PatternOfficerKillingsFinalPurificationAnalysis
                    finalPurification,

            LocalDateTime asOf

    ) {

        return prepare(
                natalProfile,
                List.of(),
                finalPurification,
                asOf
        );
    }

    /*
     * =================================================
     * 공통 준비 과정
     * =================================================
     */

    private LifeInterpretationPreparationResult prepareAfterNatalClaims(

            NatalMyeongriProfile natalProfile,

            List<PeriodMyeongriFacts> periodFacts,

            LocalDateTime asOf,

            NatalLifeClaimPipelineResult natalClaimResult

    ) {

        /*
         * =================================================
         * 1. 원국 Claim → Bundle
         * =================================================
         */

        LifeClaimBundle claimBundle =
                lifeClaimBundleCalculator
                        .build(
                                natalClaimResult.claims()
                        );

        /*
         * =================================================
         * 2. Narrative Bundle
         * =================================================
         */

        LifeClaimNarrativeBundle narrativeBundle =
                lifeClaimNarrativeBundleCalculator
                        .build(
                                claimBundle
                        );

        /*
         * =================================================
         * 3. 원국 Prompt
         * =================================================
         */

        LifeInterpretationPromptBundle natalPromptBundle =
                lifeInterpretationPromptBuilder
                        .build(
                                narrativeBundle
                        );

        /*
         * =================================================
         * 4. 지나온 흐름 계산
         * =================================================
         */

        PastFlowAnalysis pastFlowAnalysis =
                pastFlowAnalysisCalculator
                        .calculate(
                                natalProfile,
                                periodFacts
                        );

        /*
         * =================================================
         * 5. 지나온 흐름 Narrative
         * =================================================
         */

        PastFlowNarrativeBundle pastFlowNarrativeBundle =
                pastFlowNarrativeBundleCalculator
                        .build(
                                pastFlowAnalysis
                        );

        /*
         * =================================================
         * 6. PAST_FLOW Prompt
         * =================================================
         */

        LifeInterpretationPrompt pastFlowPrompt =
                pastFlowInterpretationPromptBuilder
                        .build(
                                pastFlowNarrativeBundle,
                                asOf
                        );

        /*
         * =================================================
         * 7. 최종 7개 Prompt
         * =================================================
         */

        LifeInterpretationPromptBundle finalPrompts =
                mergeFinalPrompts(
                        natalPromptBundle,
                        pastFlowPrompt
                );

        return new LifeInterpretationPreparationResult(
                natalClaimResult,
                pastFlowAnalysis,
                finalPrompts
        );
    }

    /*
     * =================================================
     * 원국 Prompt + 기간 PAST_FLOW 병합
     * =================================================
     */

    private LifeInterpretationPromptBundle mergeFinalPrompts(

            LifeInterpretationPromptBundle natalPromptBundle,

            LifeInterpretationPrompt pastFlowPrompt

    ) {

        Objects.requireNonNull(
                natalPromptBundle,
                "원국 PromptBundle은 필수입니다."
        );

        Objects.requireNonNull(
                pastFlowPrompt,
                "지나온 흐름 Prompt는 필수입니다."
        );

        if (
                pastFlowPrompt.category()
                        != LifeInterpretationCategory.PAST_FLOW
        ) {

            throw new IllegalArgumentException(
                    "교체할 Prompt는 반드시 PAST_FLOW여야 합니다."
            );
        }

        Map<
                LifeInterpretationCategory,
                LifeInterpretationPrompt
                > merged =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            if (
                    category
                            == LifeInterpretationCategory.PAST_FLOW
            ) {

                merged.put(
                        category,
                        pastFlowPrompt
                );

                continue;
            }

            merged.put(
                    category,
                    natalPromptBundle
                            .promptFor(
                                    category
                            )
            );
        }

        return new LifeInterpretationPromptBundle(
                merged
        );
    }
}