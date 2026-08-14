package com.dajeom.engine;

import com.dajeom.analyzer.CombinationTransformationEffectAnalyzer;
import com.dajeom.analyzer.EarthlyBranchCombinationIntegrationAnalyzer;
import com.dajeom.analyzer.EarthlyBranchCombinationObstructionAnalyzer;
import com.dajeom.analyzer.EarthlyBranchDirectionalCombinationFormationAnalyzer;
import com.dajeom.analyzer.EarthlyBranchSixCombinationTransformationAnalyzer;
import com.dajeom.analyzer.EarthlyBranchThreeCombinationFormationAnalyzer;
import com.dajeom.analyzer.HeavenlyStemCombinationCandidateAnalyzer;
import com.dajeom.analyzer.HeavenlyStemCombinationCompetitionAnalyzer;
import com.dajeom.analyzer.HeavenlyStemCombinationTransformationAnalyzer;
import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.CombinationTransformationEffectAnalysis;
import com.dajeom.model.EarthlyBranchCombinationIntegrationAnalysis;
import com.dajeom.model.EarthlyBranchCombinationObstructionAnalysis;
import com.dajeom.model.EarthlyBranchDirectionalCombinationFormationAnalysis;
import com.dajeom.model.EarthlyBranchSixCombinationTransformationAnalysis;
import com.dajeom.model.EarthlyBranchThreeCombinationFormationAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.HeavenlyStemCombinationCandidateAnalysis;
import com.dajeom.model.HeavenlyStemCombinationCompetitionAnalysis;
import com.dajeom.model.HeavenlyStemCombinationTransformationAnalysis;
import com.dajeom.model.RelationAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 9차-10.
 *
 * 합화 성립 분석 전체 Engine.
 *
 * =================================================
 *
 * 공식 입력:
 *
 * RelationAnalysis
 *
 * +
 *
 * ElementStrengthAnalysis
 *
 * =================================================
 *
 * 실행 순서:
 *
 * 9-1
 * HeavenlyStemCombinationCandidateAnalyzer
 *
 * ↓
 *
 * 9-2
 * HeavenlyStemCombinationCompetitionAnalyzer
 *
 * ↓
 *
 * 9-3
 * HeavenlyStemCombinationTransformationAnalyzer
 *
 * ↓
 *
 * 9-4
 * EarthlyBranchSixCombinationTransformationAnalyzer
 *
 * ↓
 *
 * 9-5
 * EarthlyBranchThreeCombinationFormationAnalyzer
 *
 * ↓
 *
 * 9-6
 * EarthlyBranchDirectionalCombinationFormationAnalyzer
 *
 * ↓
 *
 * 9-7
 * EarthlyBranchCombinationObstructionAnalyzer
 *
 * ↓
 *
 * 9-8
 * EarthlyBranchCombinationIntegrationAnalyzer
 *
 * ↓
 *
 * 9-9
 * CombinationTransformationEffectAnalyzer
 *
 * ↓
 *
 * CombinationTransformationAnalysis
 *
 * =================================================
 *
 * 이 Engine은 orchestration만 담당한다.
 *
 * 합화 판정 규칙을 직접 구현하지 않는다.
 *
 * 각 세부 판정은 기존 9-1~9-9
 * Analyzer / Rule의 책임이다.
 */
@Component
public class CombinationTransformationAnalysisEngine {

    private final HeavenlyStemCombinationCandidateAnalyzer
            heavenlyStemCombinationCandidateAnalyzer;

    private final HeavenlyStemCombinationCompetitionAnalyzer
            heavenlyStemCombinationCompetitionAnalyzer;

    private final HeavenlyStemCombinationTransformationAnalyzer
            heavenlyStemCombinationTransformationAnalyzer;

    private final EarthlyBranchSixCombinationTransformationAnalyzer
            earthlyBranchSixCombinationTransformationAnalyzer;

    private final EarthlyBranchThreeCombinationFormationAnalyzer
            earthlyBranchThreeCombinationFormationAnalyzer;

    private final EarthlyBranchDirectionalCombinationFormationAnalyzer
            earthlyBranchDirectionalCombinationFormationAnalyzer;

    private final EarthlyBranchCombinationObstructionAnalyzer
            earthlyBranchCombinationObstructionAnalyzer;

    private final EarthlyBranchCombinationIntegrationAnalyzer
            earthlyBranchCombinationIntegrationAnalyzer;

    private final CombinationTransformationEffectAnalyzer
            combinationTransformationEffectAnalyzer;

    public CombinationTransformationAnalysisEngine(

            HeavenlyStemCombinationCandidateAnalyzer
                    heavenlyStemCombinationCandidateAnalyzer,

            HeavenlyStemCombinationCompetitionAnalyzer
                    heavenlyStemCombinationCompetitionAnalyzer,

            HeavenlyStemCombinationTransformationAnalyzer
                    heavenlyStemCombinationTransformationAnalyzer,

            EarthlyBranchSixCombinationTransformationAnalyzer
                    earthlyBranchSixCombinationTransformationAnalyzer,

            EarthlyBranchThreeCombinationFormationAnalyzer
                    earthlyBranchThreeCombinationFormationAnalyzer,

            EarthlyBranchDirectionalCombinationFormationAnalyzer
                    earthlyBranchDirectionalCombinationFormationAnalyzer,

            EarthlyBranchCombinationObstructionAnalyzer
                    earthlyBranchCombinationObstructionAnalyzer,

            EarthlyBranchCombinationIntegrationAnalyzer
                    earthlyBranchCombinationIntegrationAnalyzer,

            CombinationTransformationEffectAnalyzer
                    combinationTransformationEffectAnalyzer

    ) {

        this.heavenlyStemCombinationCandidateAnalyzer =
                Objects.requireNonNull(
                        heavenlyStemCombinationCandidateAnalyzer,
                        "HeavenlyStemCombinationCandidateAnalyzer가 필요합니다."
                );

        this.heavenlyStemCombinationCompetitionAnalyzer =
                Objects.requireNonNull(
                        heavenlyStemCombinationCompetitionAnalyzer,
                        "HeavenlyStemCombinationCompetitionAnalyzer가 필요합니다."
                );

        this.heavenlyStemCombinationTransformationAnalyzer =
                Objects.requireNonNull(
                        heavenlyStemCombinationTransformationAnalyzer,
                        "HeavenlyStemCombinationTransformationAnalyzer가 필요합니다."
                );

        this.earthlyBranchSixCombinationTransformationAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchSixCombinationTransformationAnalyzer,
                        "EarthlyBranchSixCombinationTransformationAnalyzer가 필요합니다."
                );

        this.earthlyBranchThreeCombinationFormationAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchThreeCombinationFormationAnalyzer,
                        "EarthlyBranchThreeCombinationFormationAnalyzer가 필요합니다."
                );

        this.earthlyBranchDirectionalCombinationFormationAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchDirectionalCombinationFormationAnalyzer,
                        "EarthlyBranchDirectionalCombinationFormationAnalyzer가 필요합니다."
                );

        this.earthlyBranchCombinationObstructionAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchCombinationObstructionAnalyzer,
                        "EarthlyBranchCombinationObstructionAnalyzer가 필요합니다."
                );

        this.earthlyBranchCombinationIntegrationAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchCombinationIntegrationAnalyzer,
                        "EarthlyBranchCombinationIntegrationAnalyzer가 필요합니다."
                );

        this.combinationTransformationEffectAnalyzer =
                Objects.requireNonNull(
                        combinationTransformationEffectAnalyzer,
                        "CombinationTransformationEffectAnalyzer가 필요합니다."
                );
    }

    /**
     * 정밀화 9차 전체 공식 진입점.
     *
     * =================================================
     *
     * RelationAnalysis는
     * 정밀화 8차 관계 분석 결과.
     *
     * ElementStrengthAnalysis는
     * 정밀화 3차 오행 세력 분석 결과.
     *
     * =================================================
     *
     * 현재 Engine에서는 두 분석을
     * 재계산하지 않는다.
     *
     * 이미 계산된 upstream 결과를 받아
     * 합화 분석만 수행한다.
     */
    public CombinationTransformationAnalysis analyze(

            RelationAnalysis relationAnalysis,

            ElementStrengthAnalysis elementStrengthAnalysis

    ) {

        Objects.requireNonNull(
                relationAnalysis,
                "8차 RelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 ElementStrengthAnalysis가 필요합니다."
        );

        validateInputIdentity(
                relationAnalysis,
                elementStrengthAnalysis
        );

        /*
         * =================================================
         * 9-1
         *
         * 천간합 기본 후보
         * =================================================
         */
        HeavenlyStemCombinationCandidateAnalysis
                heavenlyStemCandidateAnalysis =
                heavenlyStemCombinationCandidateAnalyzer
                        .analyze(
                                relationAnalysis,
                                elementStrengthAnalysis
                        );

        /*
         * =================================================
         * 9-2
         *
         * 천간 쟁합·다중합·외부 방해
         * =================================================
         */
        HeavenlyStemCombinationCompetitionAnalysis
                heavenlyStemCompetitionAnalysis =
                heavenlyStemCombinationCompetitionAnalyzer
                        .analyze(
                                heavenlyStemCandidateAnalysis,
                                relationAnalysis
                        );

        /*
         * =================================================
         * 9-3
         *
         * 천간 합화 성립도
         * =================================================
         */
        HeavenlyStemCombinationTransformationAnalysis
                heavenlyStemTransformationAnalysis =
                heavenlyStemCombinationTransformationAnalyzer
                        .analyze(
                                heavenlyStemCompetitionAnalysis
                        );

        /*
         * =================================================
         * 9-4
         *
         * 지지 六合
         * =================================================
         */
        EarthlyBranchSixCombinationTransformationAnalysis
                sixCombinationTransformationAnalysis =
                earthlyBranchSixCombinationTransformationAnalyzer
                        .analyze(
                                relationAnalysis,
                                elementStrengthAnalysis
                        );

        /*
         * =================================================
         * 9-5
         *
         * 지지 三合·半合
         * =================================================
         */
        EarthlyBranchThreeCombinationFormationAnalysis
                threeCombinationFormationAnalysis =
                earthlyBranchThreeCombinationFormationAnalyzer
                        .analyze(
                                relationAnalysis,
                                elementStrengthAnalysis
                        );

        /*
         * =================================================
         * 9-6
         *
         * 지지 方合
         * =================================================
         */
        EarthlyBranchDirectionalCombinationFormationAnalysis
                directionalCombinationFormationAnalysis =
                earthlyBranchDirectionalCombinationFormationAnalyzer
                        .analyze(
                                relationAnalysis,
                                elementStrengthAnalysis
                        );

        /*
         * =================================================
         * 9-7
         *
         * 충·형·파·해 방해
         * =================================================
         */
        EarthlyBranchCombinationObstructionAnalysis
                earthlyBranchObstructionAnalysis =
                earthlyBranchCombinationObstructionAnalyzer
                        .analyze(
                                relationAnalysis,
                                sixCombinationTransformationAnalysis,
                                threeCombinationFormationAnalysis,
                                directionalCombinationFormationAnalysis
                        );

        /*
         * =================================================
         * 9-8
         *
         * 六合·三合·方合
         * 중첩·수렴·경쟁 조정
         * =================================================
         */
        EarthlyBranchCombinationIntegrationAnalysis
                earthlyBranchIntegrationAnalysis =
                earthlyBranchCombinationIntegrationAnalyzer
                        .analyze(
                                earthlyBranchObstructionAnalysis
                        );

        /*
         * =================================================
         * 9-9
         *
         * 원래 기능 보존
         * +
         * 목표 오행 활성
         * =================================================
         */
        CombinationTransformationEffectAnalysis
                effectAnalysis =
                combinationTransformationEffectAnalyzer
                        .analyze(
                                heavenlyStemTransformationAnalysis,
                                earthlyBranchIntegrationAnalysis
                        );

        /*
         * =================================================
         * 9-10
         *
         * 최종 통합
         * =================================================
         */
        return new CombinationTransformationAnalysis(
                relationAnalysis.dayMaster(),
                relationAnalysis,
                elementStrengthAnalysis,
                heavenlyStemCandidateAnalysis,
                heavenlyStemCompetitionAnalysis,
                heavenlyStemTransformationAnalysis,
                sixCombinationTransformationAnalysis,
                threeCombinationFormationAnalysis,
                directionalCombinationFormationAnalysis,
                earthlyBranchObstructionAnalysis,
                earthlyBranchIntegrationAnalysis,
                effectAnalysis,
                relationAnalysis.completeChart()
        );
    }

    /**
     * 8차와 3차 분석이
     * 동일 원국의 결과인지 최소 조건을 검증한다.
     *
     * =================================================
     *
     * 여기서는 새로운 명리 Rule을 적용하지 않는다.
     */
    private void validateInputIdentity(

            RelationAnalysis relationAnalysis,

            ElementStrengthAnalysis elementStrengthAnalysis

    ) {

        if (
                relationAnalysis.dayMaster()
                        != elementStrengthAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "RelationAnalysis와 ElementStrengthAnalysis의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                relationAnalysis.completeChart()
                        != elementStrengthAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "RelationAnalysis와 ElementStrengthAnalysis의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }
}