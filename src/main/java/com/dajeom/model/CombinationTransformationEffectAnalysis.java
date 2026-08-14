package com.dajeom.model;

import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;
import com.dajeom.type.VisibleStemPosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-9.
 *
 * 천간·지지 합화 이후의
 * 원래 기능 보존 / 목표 오행 활성 분석.
 *
 * =================================================
 *
 * 입력:
 *
 * 9-3 HeavenlyStemCombinationTransformationAnalysis
 *
 * +
 *
 * 9-8 EarthlyBranchCombinationIntegrationAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * HeavenlyStemTransformationMemberEffect
 *
 * EarthlyBranchTransformationMemberEffect
 *
 * =================================================
 *
 * 이 분석도 기존 ElementStrengthAnalysis를
 * 직접 수정하지 않는다.
 *
 * 9-10에서 전체 합화 분석을 통합한 뒤
 * 후속 단계가 필요할 경우
 * 이 Effect Layer를 참고하게 된다.
 */
public record CombinationTransformationEffectAnalysis(

        HeavenlyStem dayMaster,

        HeavenlyStemCombinationTransformationAnalysis
        heavenlyStemTransformationAnalysis,

        EarthlyBranchCombinationIntegrationAnalysis
        earthlyBranchIntegrationAnalysis,

        List<HeavenlyStemTransformationMemberEffect>
        heavenlyStemEffects,

        List<EarthlyBranchTransformationMemberEffect>
        earthlyBranchEffects,

        boolean completeChart

) {

    public CombinationTransformationEffectAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                heavenlyStemTransformationAnalysis,
                "9-3 천간 합화 분석이 필요합니다."
        );

        Objects.requireNonNull(
                earthlyBranchIntegrationAnalysis,
                "9-8 지지 결합 통합 분석이 필요합니다."
        );

        heavenlyStemEffects =
                heavenlyStemEffects == null
                        ? List.of()
                        : List.copyOf(
                        heavenlyStemEffects
                );

        earthlyBranchEffects =
                earthlyBranchEffects == null
                        ? List.of()
                        : List.copyOf(
                        earthlyBranchEffects
                );

        if (
                heavenlyStemTransformationAnalysis
                        .dayMaster()
                        != dayMaster

                        || earthlyBranchIntegrationAnalysis
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "9-3/9-8/9-9 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                heavenlyStemTransformationAnalysis
                        .completeChart()
                        != completeChart

                        || earthlyBranchIntegrationAnalysis
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "9-3/9-8/9-9 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        /*
         * 천간 합화 Evidence 하나는
         * 정확히 두 개의 천간 구성원을 가진다.
         */
        int expectedStemEffectCount =
                heavenlyStemTransformationAnalysis
                        .evidences()
                        .size()
                        * 2;

        if (
                heavenlyStemEffects.size()
                        != expectedStemEffectCount
        ) {

            throw new IllegalArgumentException(
                    "9-3 천간 합화 Evidence와 9-9 천간 MemberEffect 수가 일치하지 않습니다."
            );
        }

        /*
         * 지지 후보는 2개 또는 3개의 구성원을 가진다.
         */
        int expectedBranchEffectCount =
                earthlyBranchIntegrationAnalysis
                        .candidates()
                        .stream()
                        .mapToInt(
                                candidate ->
                                        candidate
                                                .candidatePositions()
                                                .size()
                        )
                        .sum();

        if (
                earthlyBranchEffects.size()
                        != expectedBranchEffectCount
        ) {

            throw new IllegalArgumentException(
                    "9-8 지지 결합 후보 구성원 수와 9-9 지지 MemberEffect 수가 일치하지 않습니다."
            );
        }

        for (
                HeavenlyStemTransformationMemberEffect effect
                : heavenlyStemEffects
        ) {

            if (
                    !heavenlyStemTransformationAnalysis
                            .evidences()
                            .contains(
                                    effect.sourceEvidence()
                            )
            ) {

                throw new IllegalArgumentException(
                        "9-9 천간 Effect의 원본 Evidence가 9-3 분석에 존재하지 않습니다."
                );
            }

            if (
                    !completeChart
                            && effect.position()
                            == VisibleStemPosition.HOUR
            ) {

                throw new IllegalArgumentException(
                        "출생시간 미상 원국에 HOUR 천간 Effect가 존재할 수 없습니다."
                );
            }
        }

        for (
                EarthlyBranchTransformationMemberEffect effect
                : earthlyBranchEffects
        ) {

            if (
                    !earthlyBranchIntegrationAnalysis
                            .candidates()
                            .contains(
                                    effect.sourceCandidate()
                            )
            ) {

                throw new IllegalArgumentException(
                        "9-9 지지 Effect의 원본 후보가 9-8 분석에 존재하지 않습니다."
                );
            }

            if (
                    !completeChart
                            && effect.position()
                            == EarthlyBranchPosition.HOUR
            ) {

                throw new IllegalArgumentException(
                        "출생시간 미상 원국에 HOUR 지지 Effect가 존재할 수 없습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 천간
     * =================================================
     */

    public List<HeavenlyStemTransformationMemberEffect>
    heavenlyStemEffectsAt(
            VisibleStemPosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return heavenlyStemEffects
                .stream()
                .filter(
                        effect ->
                                effect.position()
                                        == position
                )
                .toList();
    }

    public List<HeavenlyStemTransformationMemberEffect>
    dayMasterEffects() {

        return heavenlyStemEffectsAt(
                VisibleStemPosition.DAY
        );
    }

    public boolean dayMasterOriginalFunctionReduced() {

        return dayMasterEffects()
                .stream()
                .anyMatch(
                        HeavenlyStemTransformationMemberEffect
                                ::originalFunctionReduced
                );
    }

    /*
     * =================================================
     * 지지
     * =================================================
     */

    public List<EarthlyBranchTransformationMemberEffect>
    earthlyBranchEffectsAt(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return earthlyBranchEffects
                .stream()
                .filter(
                        effect ->
                                effect.position()
                                        == position
                )
                .toList();
    }

    public List<EarthlyBranchTransformationMemberEffect>
    dayBranchEffects() {

        return earthlyBranchEffectsAt(
                EarthlyBranchPosition.DAY
        );
    }

    public List<EarthlyBranchTransformationMemberEffect>
    monthBranchEffects() {

        return earthlyBranchEffectsAt(
                EarthlyBranchPosition.MONTH
        );
    }

    /*
     * =================================================
     * 목표 오행 활성
     * =================================================
     */

    public List<HeavenlyStemTransformationMemberEffect>
    heavenlyStemEffectsTargeting(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return List.of();
        }

        return heavenlyStemEffects
                .stream()
                .filter(
                        effect ->
                                effect.targetElement()
                                        == element
                )
                .toList();
    }

    public List<EarthlyBranchTransformationMemberEffect>
    earthlyBranchEffectsTargeting(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return List.of();
        }

        return earthlyBranchEffects
                .stream()
                .filter(
                        effect ->
                                effect.targetElement()
                                        == element
                )
                .toList();
    }

    public List<FiveElement>
    stronglyActivatedTargetElements() {

        return java.util.stream.Stream
                .concat(
                        heavenlyStemEffects
                                .stream()
                                .filter(
                                        effect ->
                                                effect.targetActivationState()
                                                        == TransformationTargetActivationState.STRONG
                                )
                                .map(
                                        HeavenlyStemTransformationMemberEffect
                                                ::targetElement
                                ),

                        earthlyBranchEffects
                                .stream()
                                .filter(
                                        effect ->
                                                effect.targetActivationState()
                                                        == TransformationTargetActivationState.STRONG
                                )
                                .map(
                                        EarthlyBranchTransformationMemberEffect
                                                ::targetElement
                                )
                )
                .distinct()
                .toList();
    }

    public List<FiveElement>
    activelyTransformedElements() {

        return java.util.stream.Stream
                .concat(
                        heavenlyStemEffects
                                .stream()
                                .filter(
                                        effect ->
                                                effect.targetActivationState()
                                                        .active()
                                )
                                .map(
                                        HeavenlyStemTransformationMemberEffect
                                                ::targetElement
                                ),

                        earthlyBranchEffects
                                .stream()
                                .filter(
                                        effect ->
                                                effect.targetActivationState()
                                                        .active()
                                )
                                .map(
                                        EarthlyBranchTransformationMemberEffect
                                                ::targetElement
                                )
                )
                .distinct()
                .toList();
    }

    /*
     * =================================================
     * 원래 기능 감소
     * =================================================
     */

    public List<HeavenlyStemTransformationMemberEffect>
    reducedHeavenlyStemEffects() {

        return heavenlyStemEffects
                .stream()
                .filter(
                        HeavenlyStemTransformationMemberEffect
                                ::originalFunctionReduced
                )
                .toList();
    }

    public List<EarthlyBranchTransformationMemberEffect>
    reducedEarthlyBranchEffects() {

        return earthlyBranchEffects
                .stream()
                .filter(
                        EarthlyBranchTransformationMemberEffect
                                ::originalFunctionReduced
                )
                .toList();
    }

    public boolean hasOriginalFunctionReduction() {

        return !reducedHeavenlyStemEffects()
                .isEmpty()

                || !reducedEarthlyBranchEffects()
                .isEmpty();
    }

    public boolean hasStrongOriginalFunctionReduction() {

        return heavenlyStemEffects
                .stream()
                .anyMatch(
                        effect ->
                                effect.originalFunctionState()
                                        == TransformationOriginalFunctionState
                                        .STRONGLY_REDUCED
                );
    }

    public boolean hasActiveTransformation() {

        return !activelyTransformedElements()
                .isEmpty();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}