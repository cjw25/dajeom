package com.dajeom.model;

import com.dajeom.type.EarthlyBranchCombinationCandidateKind;
import com.dajeom.type.EarthlyBranchCombinationIntegrationRole;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;
import com.dajeom.type.VisibleStemPosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-10.
 *
 * 합화 성립 판정 전체 최종 통합 결과.
 *
 * =================================================
 *
 * 포함 범위
 *
 * 9-1
 * 천간합 후보 적용 조건
 *
 * 9-2
 * 천간합 쟁합·다중합·방해 구조
 *
 * 9-3
 * 천간 합화 성립도
 *
 * 9-4
 * 지지 육합 합화
 *
 * 9-5
 * 지지 삼합·반합 국 성립
 *
 * 9-6
 * 지지 방합 국 성립
 *
 * 9-7
 * 충·형·파·해 결합 방해
 *
 * 9-8
 * 육합·삼합·방합 수렴 및 경쟁
 *
 * 9-9
 * 원래 오행 기능 보존 및
 * 목표 오행 활성 상태
 *
 * =================================================
 *
 * 중요:
 *
 * 이 객체는 합화 결과를 표현하는
 * Evidence Layer다.
 *
 * 기존:
 *
 * ElementStrengthAnalysis
 *
 * 의 수치를 직접 수정하지 않는다.
 *
 * 또한 천간·지지·지장간 객체를
 * 목표 오행으로 물리적으로 교체하지 않는다.
 */
public record CombinationTransformationAnalysis(

        HeavenlyStem dayMaster,

        RelationAnalysis relationAnalysis,

        ElementStrengthAnalysis elementStrengthAnalysis,

        HeavenlyStemCombinationCandidateAnalysis
        heavenlyStemCandidateAnalysis,

        HeavenlyStemCombinationCompetitionAnalysis
        heavenlyStemCompetitionAnalysis,

        HeavenlyStemCombinationTransformationAnalysis
        heavenlyStemTransformationAnalysis,

        EarthlyBranchSixCombinationTransformationAnalysis
        sixCombinationTransformationAnalysis,

        EarthlyBranchThreeCombinationFormationAnalysis
        threeCombinationFormationAnalysis,

        EarthlyBranchDirectionalCombinationFormationAnalysis
        directionalCombinationFormationAnalysis,

        EarthlyBranchCombinationObstructionAnalysis
        earthlyBranchObstructionAnalysis,

        EarthlyBranchCombinationIntegrationAnalysis
        earthlyBranchIntegrationAnalysis,

        CombinationTransformationEffectAnalysis
        effectAnalysis,

        boolean completeChart

) {

    public CombinationTransformationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                relationAnalysis,
                "8차 관계 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 오행 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                heavenlyStemCandidateAnalysis,
                "9-1 천간합 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                heavenlyStemCompetitionAnalysis,
                "9-2 천간합 경쟁 분석이 필요합니다."
        );

        Objects.requireNonNull(
                heavenlyStemTransformationAnalysis,
                "9-3 천간 합화 분석이 필요합니다."
        );

        Objects.requireNonNull(
                sixCombinationTransformationAnalysis,
                "9-4 지지 육합 합화 분석이 필요합니다."
        );

        Objects.requireNonNull(
                threeCombinationFormationAnalysis,
                "9-5 지지 삼합 국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                directionalCombinationFormationAnalysis,
                "9-6 지지 방합 국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                earthlyBranchObstructionAnalysis,
                "9-7 지지 결합 방해 분석이 필요합니다."
        );

        Objects.requireNonNull(
                earthlyBranchIntegrationAnalysis,
                "9-8 지지 결합 통합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                effectAnalysis,
                "9-9 합화 기능 변화 분석이 필요합니다."
        );

        /*
         * =================================================
         * 일간 동일성
         * =================================================
         */

        if (
                relationAnalysis.dayMaster()
                        != dayMaster

                        || elementStrengthAnalysis.dayMaster()
                        != dayMaster

                        || heavenlyStemCandidateAnalysis.dayMaster()
                        != dayMaster

                        || heavenlyStemCompetitionAnalysis.dayMaster()
                        != dayMaster

                        || heavenlyStemTransformationAnalysis.dayMaster()
                        != dayMaster

                        || sixCombinationTransformationAnalysis.dayMaster()
                        != dayMaster

                        || threeCombinationFormationAnalysis.dayMaster()
                        != dayMaster

                        || directionalCombinationFormationAnalysis.dayMaster()
                        != dayMaster

                        || earthlyBranchObstructionAnalysis.dayMaster()
                        != dayMaster

                        || earthlyBranchIntegrationAnalysis.dayMaster()
                        != dayMaster

                        || effectAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "합화 분석 전체 단계의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 완전성 동일성
         * =================================================
         */

        if (
                relationAnalysis.completeChart()
                        != completeChart

                        || elementStrengthAnalysis.completeChart()
                        != completeChart

                        || heavenlyStemCandidateAnalysis.completeChart()
                        != completeChart

                        || heavenlyStemCompetitionAnalysis.completeChart()
                        != completeChart

                        || heavenlyStemTransformationAnalysis.completeChart()
                        != completeChart

                        || sixCombinationTransformationAnalysis.completeChart()
                        != completeChart

                        || threeCombinationFormationAnalysis.completeChart()
                        != completeChart

                        || directionalCombinationFormationAnalysis.completeChart()
                        != completeChart

                        || earthlyBranchObstructionAnalysis.completeChart()
                        != completeChart

                        || earthlyBranchIntegrationAnalysis.completeChart()
                        != completeChart

                        || effectAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "합화 분석 전체 단계의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 단계 연결 검증
         * =================================================
         */

        if (
                heavenlyStemCompetitionAnalysis
                        .candidateAnalysis()
                        != heavenlyStemCandidateAnalysis
        ) {

            throw new IllegalArgumentException(
                    "9-2가 참조하는 9-1 천간합 후보 분석이 최종 통합 입력과 일치하지 않습니다."
            );
        }

        if (
                heavenlyStemTransformationAnalysis
                        .competitionAnalysis()
                        != heavenlyStemCompetitionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "9-3이 참조하는 9-2 천간합 경쟁 분석이 최종 통합 입력과 일치하지 않습니다."
            );
        }

        if (
                earthlyBranchObstructionAnalysis
                        .sixCombinationAnalysis()
                        != sixCombinationTransformationAnalysis

                        || earthlyBranchObstructionAnalysis
                        .threeCombinationAnalysis()
                        != threeCombinationFormationAnalysis

                        || earthlyBranchObstructionAnalysis
                        .directionalCombinationAnalysis()
                        != directionalCombinationFormationAnalysis
        ) {

            throw new IllegalArgumentException(
                    "9-7이 참조하는 9-4/9-5/9-6 분석이 최종 통합 입력과 일치하지 않습니다."
            );
        }

        if (
                earthlyBranchIntegrationAnalysis
                        .obstructionAnalysis()
                        != earthlyBranchObstructionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "9-8이 참조하는 9-7 방해 분석이 최종 통합 입력과 일치하지 않습니다."
            );
        }

        if (
                effectAnalysis
                        .heavenlyStemTransformationAnalysis()
                        != heavenlyStemTransformationAnalysis

                        || effectAnalysis
                        .earthlyBranchIntegrationAnalysis()
                        != earthlyBranchIntegrationAnalysis
        ) {

            throw new IllegalArgumentException(
                    "9-9가 참조하는 9-3/9-8 분석이 최종 통합 입력과 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 전체 관계 존재
     * =================================================
     */

    public boolean hasAnyCombinationRelation() {

        return hasHeavenlyStemCombination()
                || hasEarthlyBranchCombination();
    }

    public boolean hasHeavenlyStemCombination() {

        return heavenlyStemTransformationAnalysis
                .hasCombination();
    }

    public boolean hasEarthlyBranchCombination() {

        return earthlyBranchIntegrationAnalysis
                .hasCandidate();
    }

    /*
     * =================================================
     * 실제 합화 활성
     * =================================================
     */

    public boolean hasActiveTransformation() {

        return effectAnalysis
                .hasActiveTransformation();
    }

    public boolean hasStrongTransformation() {

        return !stronglyActivatedElements()
                .isEmpty();
    }

    /**
     * PARTIAL 또는 STRONG 상태로
     * 실제 활성된 합화 목표 오행.
     */
    public List<FiveElement> activeTransformationElements() {

        return effectAnalysis
                .activelyTransformedElements();
    }

    /**
     * STRONG 상태의 목표 오행.
     */
    public List<FiveElement> stronglyActivatedElements() {

        return effectAnalysis
                .stronglyActivatedTargetElements();
    }

    /*
     * =================================================
     * 원래 기능 변화
     * =================================================
     */

    public boolean hasOriginalFunctionReduction() {

        return effectAnalysis
                .hasOriginalFunctionReduction();
    }

    public boolean hasStrongOriginalFunctionReduction() {

        return effectAnalysis
                .hasStrongOriginalFunctionReduction();
    }

    /*
     * =================================================
     * 천간 합화
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    transformedHeavenlyStemCombinations() {

        return heavenlyStemTransformationAnalysis
                .transformedCombinations();
    }

    public List<HeavenlyStemCombinationTransformationEvidence>
    partiallyTransformedHeavenlyStemCombinations() {

        return heavenlyStemTransformationAnalysis
                .partiallyTransformedCombinations();
    }

    public List<HeavenlyStemCombinationTransformationEvidence>
    conditionalHeavenlyStemCombinations() {

        return heavenlyStemTransformationAnalysis
                .conditionalCombinations();
    }

    public List<HeavenlyStemCombinationTransformationEvidence>
    combinedNotTransformedHeavenlyStemCombinations() {

        return heavenlyStemTransformationAnalysis
                .combinedNotTransformedCombinations();
    }

    public List<HeavenlyStemCombinationTransformationEvidence>
    suppressedHeavenlyStemCombinations() {

        return heavenlyStemTransformationAnalysis
                .suppressedCombinations();
    }

    /*
     * =================================================
     * 지지 결합 최종 역할
     * =================================================
     */

    public List<EarthlyBranchCombinationIntegratedCandidate>
    primaryEarthlyBranchCombinations() {

        return earthlyBranchIntegrationAnalysis
                .primaryCandidates();
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    supportingEarthlyBranchCombinations() {

        return earthlyBranchIntegrationAnalysis
                .supportingCandidates();
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    competingEarthlyBranchCombinations() {

        return earthlyBranchIntegrationAnalysis
                .competingCandidates();
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    deferredEarthlyBranchCombinations() {

        return earthlyBranchIntegrationAnalysis
                .deferredCandidates();
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    suppressedEarthlyBranchCombinations() {

        return earthlyBranchIntegrationAnalysis
                .suppressedCandidates();
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    effectiveEarthlyBranchCombinations() {

        return earthlyBranchIntegrationAnalysis
                .effectiveCandidates();
    }

    /*
     * =================================================
     * 지지 후보 종류별
     * =================================================
     */

    public List<EarthlyBranchCombinationIntegratedCandidate>
    sixCombinationCandidates() {

        return earthlyBranchIntegrationAnalysis
                .candidatesOfKind(
                        EarthlyBranchCombinationCandidateKind
                                .SIX_COMBINATION
                );
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    threeCombinationCandidates() {

        return earthlyBranchIntegrationAnalysis
                .candidatesOfKind(
                        EarthlyBranchCombinationCandidateKind
                                .THREE_COMBINATION
                );
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    directionalCombinationCandidates() {

        return earthlyBranchIntegrationAnalysis
                .candidatesOfKind(
                        EarthlyBranchCombinationCandidateKind
                                .DIRECTIONAL_COMBINATION
                );
    }

    /*
     * =================================================
     * 지지 경쟁/수렴
     * =================================================
     */

    public boolean hasEarthlyBranchConvergence() {

        return earthlyBranchIntegrationAnalysis
                .hasConvergence();
    }

    public boolean hasCrossElementCompetition() {

        return earthlyBranchIntegrationAnalysis
                .hasCrossElementCompetition();
    }

    public boolean hasUnresolvedEarthlyBranchCompetition() {

        return earthlyBranchIntegrationAnalysis
                .hasUnresolvedCompetition();
    }

    public List<FiveElement>
    primaryEarthlyBranchElements() {

        return earthlyBranchIntegrationAnalysis
                .primaryElements();
    }

    public List<FiveElement>
    effectiveEarthlyBranchElements() {

        return earthlyBranchIntegrationAnalysis
                .effectiveElements();
    }

    /*
     * =================================================
     * 지지 방해
     * =================================================
     */

    public boolean hasEarthlyBranchObstruction() {

        return earthlyBranchObstructionAnalysis
                .hasObstruction();
    }

    public boolean hasMeaningfulEarthlyBranchObstruction() {

        return earthlyBranchObstructionAnalysis
                .hasMeaningfulOrSevereObstruction();
    }

    public boolean hasSevereEarthlyBranchObstruction() {

        return earthlyBranchObstructionAnalysis
                .hasSevereObstruction();
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    severeObstructionCandidates() {

        return earthlyBranchObstructionAnalysis
                .severeObstructionCandidates();
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    candidatesWithObstructionLevel(
            EarthlyBranchCombinationObstructionLevel level
    ) {

        return earthlyBranchObstructionAnalysis
                .evidencesOfLevel(
                        level
                );
    }

    /*
     * =================================================
     * YEAR / MONTH / DAY / HOUR 천간 Effect
     * =================================================
     */

    public List<HeavenlyStemTransformationMemberEffect>
    heavenlyStemEffectsAt(
            VisibleStemPosition position
    ) {

        return effectAnalysis
                .heavenlyStemEffectsAt(
                        position
                );
    }

    public List<HeavenlyStemTransformationMemberEffect>
    yearStemEffects() {

        return heavenlyStemEffectsAt(
                VisibleStemPosition.YEAR
        );
    }

    public List<HeavenlyStemTransformationMemberEffect>
    monthStemEffects() {

        return heavenlyStemEffectsAt(
                VisibleStemPosition.MONTH
        );
    }

    public List<HeavenlyStemTransformationMemberEffect>
    dayStemEffects() {

        return heavenlyStemEffectsAt(
                VisibleStemPosition.DAY
        );
    }

    public List<HeavenlyStemTransformationMemberEffect>
    hourStemEffects() {

        if (
                hourUnknown()
        ) {

            return List.of();
        }

        return heavenlyStemEffectsAt(
                VisibleStemPosition.HOUR
        );
    }

    /*
     * =================================================
     * YEAR / MONTH / DAY / HOUR 지지 Effect
     * =================================================
     */

    public List<EarthlyBranchTransformationMemberEffect>
    earthlyBranchEffectsAt(
            EarthlyBranchPosition position
    ) {

        return effectAnalysis
                .earthlyBranchEffectsAt(
                        position
                );
    }

    public List<EarthlyBranchTransformationMemberEffect>
    yearBranchEffects() {

        return earthlyBranchEffectsAt(
                EarthlyBranchPosition.YEAR
        );
    }

    public List<EarthlyBranchTransformationMemberEffect>
    monthBranchEffects() {

        return earthlyBranchEffectsAt(
                EarthlyBranchPosition.MONTH
        );
    }

    public List<EarthlyBranchTransformationMemberEffect>
    dayBranchEffects() {

        return earthlyBranchEffectsAt(
                EarthlyBranchPosition.DAY
        );
    }

    public List<EarthlyBranchTransformationMemberEffect>
    hourBranchEffects() {

        if (
                hourUnknown()
        ) {

            return List.of();
        }

        return earthlyBranchEffectsAt(
                EarthlyBranchPosition.HOUR
        );
    }

    /*
     * =================================================
     * 일간 / 일지
     * =================================================
     */

    public boolean dayMasterInCombination() {

        return !dayStemEffects()
                .isEmpty();
    }

    public boolean dayMasterFunctionReduced() {

        return effectAnalysis
                .dayMasterOriginalFunctionReduced();
    }

    public boolean dayBranchInCombination() {

        return !dayBranchEffects()
                .isEmpty();
    }

    /*
     * =================================================
     * 특정 목표 오행 Effect
     * =================================================
     */

    public List<HeavenlyStemTransformationMemberEffect>
    heavenlyStemEffectsTargeting(
            FiveElement element
    ) {

        return effectAnalysis
                .heavenlyStemEffectsTargeting(
                        element
                );
    }

    public List<EarthlyBranchTransformationMemberEffect>
    earthlyBranchEffectsTargeting(
            FiveElement element
    ) {

        return effectAnalysis
                .earthlyBranchEffectsTargeting(
                        element
                );
    }

    public boolean hasActiveTransformationTargeting(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return false;
        }

        return heavenlyStemEffectsTargeting(
                element
        )
                .stream()
                .anyMatch(
                        effect ->
                                effect
                                        .targetActivationState()
                                        .active()
                )

                || earthlyBranchEffectsTargeting(
                element
        )
                .stream()
                .anyMatch(
                        effect ->
                                effect
                                        .targetActivationState()
                                        .active()
                );
    }

    public boolean hasStrongTransformationTargeting(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return false;
        }

        return heavenlyStemEffectsTargeting(
                element
        )
                .stream()
                .anyMatch(
                        effect ->
                                effect
                                        .targetActivationState()
                                        == TransformationTargetActivationState
                                        .STRONG
                )

                || earthlyBranchEffectsTargeting(
                element
        )
                .stream()
                .anyMatch(
                        effect ->
                                effect
                                        .targetActivationState()
                                        == TransformationTargetActivationState
                                        .STRONG
                );
    }

    /*
     * =================================================
     * 특정 원래 기능 감소
     * =================================================
     */

    public boolean hasStronglyReducedHeavenlyStem() {

        return effectAnalysis
                .heavenlyStemEffects()
                .stream()
                .anyMatch(
                        effect ->
                                effect
                                        .originalFunctionState()
                                        == TransformationOriginalFunctionState
                                        .STRONGLY_REDUCED
                );
    }

    public boolean hasReducedEarthlyBranch() {

        return effectAnalysis
                .earthlyBranchEffects()
                .stream()
                .anyMatch(
                        effect ->
                                effect
                                        .originalFunctionState()
                                        .reduced()
                );
    }

    /*
     * =================================================
     * 최종 해석 상태
     * =================================================
     */

    /**
     * 합 관계는 존재하지만
     * 현재 실제 활성화된 化 방향이 없는가.
     */
    public boolean combinationExistsButNoActiveTransformation() {

        return hasAnyCombinationRelation()
                && !hasActiveTransformation();
    }

    /**
     * 실제 활성화된 목표 오행이
     * 둘 이상 존재하는가.
     *
     * 반드시 충돌이라는 뜻은 아니다.
     *
     * 서로 독립적인 궁위에서
     * 복수 오행 합화가 성립할 수도 있다.
     */
    public boolean multipleActiveTransformationElements() {

        return activeTransformationElements()
                .size() >= 2;
    }

    /**
     * 지지 영역에서 실제 해결되지 않은
     * 다른 목표 오행 경쟁이 존재하는가.
     */
    public boolean requiresEarthlyCompetitionReview() {

        return hasUnresolvedEarthlyBranchCompetition();
    }

    /**
     * 출생시간 미상 여부.
     */
    public boolean hourUnknown() {

        return !completeChart;
    }
}