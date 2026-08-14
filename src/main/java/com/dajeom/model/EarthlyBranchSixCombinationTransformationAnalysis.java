package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchSixCombinationTransformationState;
import com.dajeom.type.EarthlyBranchSixCombinationType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 9차-4.
 *
 * 원국 전체 지지 육합의
 * 방해 검토 전 합화 성립 분석.
 *
 * =================================================
 *
 * 8-3 六合 Evidence 하나마다
 * 9-4 TransformationEvidence 하나가 존재한다.
 *
 * =================================================
 *
 * 아직:
 *
 * 沖
 * 刑
 * 破
 * 害
 *
 * 에 의한 최종 다운그레이드는
 * 적용하지 않는다.
 *
 * =================================================
 *
 * 따라서 PROVISIONALLY_TRANSFORMED는
 * 9-7/9-8 이전의 잠정 성립 상태다.
 */
public record EarthlyBranchSixCombinationTransformationAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        EarthlyBranch dayBranch,

        EarthlyBranchSixCombinationAnalysis
        sixCombinationAnalysis,

        ElementStrengthAnalysis
        elementStrengthAnalysis,

        List<EarthlyBranchSixCombinationTransformationEvidence>
        evidences,

        boolean completeChart

) {

    public EarthlyBranchSixCombinationTransformationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                dayBranch,
                "일지가 필요합니다."
        );

        Objects.requireNonNull(
                sixCombinationAnalysis,
                "8-3 지지 육합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 오행 세력 분석이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                sixCombinationAnalysis
                        .dayBranch()
                        != dayBranch
        ) {

            throw new IllegalArgumentException(
                    "8-3 육합 분석의 일지가 9-4 일지와 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "3차 오행 세력 분석의 일간이 9-4 일간과 일치하지 않습니다."
            );
        }

        if (
                sixCombinationAnalysis.completeChart()
                        != completeChart

                        || elementStrengthAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "9-4 입력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        if (
                evidences.size()
                        != sixCombinationAnalysis
                        .evidences()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "8-3 육합 Evidence 수와 9-4 합화 Evidence 수가 일치하지 않습니다."
            );
        }

        Set<String> pairKeys =
                new HashSet<>();

        for (
                EarthlyBranchSixCombinationTransformationEvidence evidence
                : evidences
        ) {

            if (
                    !sixCombinationAnalysis
                            .evidences()
                            .contains(
                                    evidence.sourceEvidence()
                            )
            ) {

                throw new IllegalArgumentException(
                        "9-4 Evidence의 원본 육합이 8-3 분석에 존재하지 않습니다."
                );
            }

            String key =
                    evidence.firstPosition().name()
                            + ":"
                            + evidence.secondPosition().name();

            if (
                    !pairKeys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 지지 궁위 Pair의 9-4 육합 Evidence가 중복되었습니다: "
                                + key
                );
            }
        }
    }

    public boolean hasCombination() {

        return !evidences
                .isEmpty();
    }

    public int combinationCount() {

        return evidences
                .size();
    }

    /*
     * =================================================
     * 잠정 완전 합화
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    provisionallyTransformedCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationTransformationEvidence
                                ::provisionallyTransformed
                )
                .toList();
    }

    public boolean hasProvisionallyTransformedCombination() {

        return !provisionallyTransformedCombinations()
                .isEmpty();
    }

    /*
     * =================================================
     * 잠정 부분 합화
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    provisionallyPartialCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationTransformationEvidence
                                ::provisionallyPartial
                )
                .toList();
    }

    /*
     * =================================================
     * 조건부
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    conditionalCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationTransformationEvidence
                                ::conditional
                )
                .toList();
    }

    /*
     * =================================================
     * 合而不化
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    combinedNotTransformedCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationTransformationEvidence
                                ::combinedNotTransformed
                )
                .toList();
    }

    public boolean hasCombinedNotTransformed() {

        return !combinedNotTransformedCombinations()
                .isEmpty();
    }

    /*
     * =================================================
     * 현재 단계에서 의미 있는 化 방향
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    meaningfulTransformations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationTransformationEvidence
                                ::meaningfulTransformation
                )
                .toList();
    }

    public boolean hasMeaningfulTransformation() {

        return !meaningfulTransformations()
                .isEmpty();
    }

    /*
     * =================================================
     * 상태별
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    transformationsOfState(
            EarthlyBranchSixCombinationTransformationState state
    ) {

        if (
                state == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.state()
                                        == state
                )
                .toList();
    }

    /*
     * =================================================
     * 육합 종류
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    transformationsOf(
            EarthlyBranchSixCombinationType combinationType
    ) {

        if (
                combinationType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.combinationType()
                                        == combinationType
                )
                .toList();
    }

    /*
     * =================================================
     * 목표 오행
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    transformationsTargeting(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.targetElement()
                                        == element
                )
                .toList();
    }

    public List<FiveElement>
    provisionalTransformationElements() {

        return meaningfulTransformations()
                .stream()
                .map(
                        EarthlyBranchSixCombinationTransformationEvidence
                                ::targetElement
                )
                .distinct()
                .toList();
    }

    /*
     * =================================================
     * 일지
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    dayBranchCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationTransformationEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    dayBranchMeaningfulTransformations() {

        return dayBranchCombinations()
                .stream()
                .filter(
                        EarthlyBranchSixCombinationTransformationEvidence
                                ::meaningfulTransformation
                )
                .toList();
    }

    public boolean dayBranchInTransformation() {

        return !dayBranchMeaningfulTransformations()
                .isEmpty();
    }

    /*
     * =================================================
     * 월지
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    monthBranchCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationTransformationEvidence
                                ::monthBranchInvolved
                )
                .toList();
    }

    /*
     * =================================================
     * 궁위
     * =================================================
     */

    public List<EarthlyBranchSixCombinationTransformationEvidence>
    transformationsInvolving(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        position
                                )
                )
                .toList();
    }

    public Optional<EarthlyBranchSixCombinationTransformationEvidence>
    transformationBetween(

            EarthlyBranchPosition position1,

            EarthlyBranchPosition position2

    ) {

        if (
                position1 == null
                        || position2 == null
                        || position1 == position2
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        position1
                                )
                                        && evidence.involves(
                                        position2
                                )
                )
                .findFirst();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}