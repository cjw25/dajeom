package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationTransformationState;
import com.dajeom.type.HeavenlyStemCombinationType;
import com.dajeom.type.VisibleStemPosition;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 9차-3.
 *
 * 원국 전체 천간합의
 * 합화 성립도 분석.
 *
 * =================================================
 *
 * 9-1 Candidate 하나마다
 * 9-2 CompetitionEvidence 하나,
 * 9-3 TransformationEvidence 하나가 존재한다.
 *
 * =================================================
 *
 * 후보를 삭제하지 않는다.
 *
 * 예:
 *
 * YEAR己-DAY甲
 * DAY甲-HOUR己
 *
 * 쟁합 구조라 해도
 * 두 결과 모두 보존한다.
 *
 * 단:
 *
 * 각각의 state는 서로 다를 수 있다.
 */
public record HeavenlyStemCombinationTransformationAnalysis(

        HeavenlyStem dayMaster,

        HeavenlyStemCombinationCandidateAnalysis
        candidateAnalysis,

        HeavenlyStemCombinationCompetitionAnalysis
        competitionAnalysis,

        List<HeavenlyStemCombinationTransformationEvidence>
        evidences,

        boolean completeChart

) {

    public HeavenlyStemCombinationTransformationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                candidateAnalysis,
                "9-1 천간합 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                competitionAnalysis,
                "9-2 천간합 경쟁 분석이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                candidateAnalysis.dayMaster()
                        != dayMaster

                        || competitionAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "9-1/9-2/9-3의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                candidateAnalysis.completeChart()
                        != completeChart

                        || competitionAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "천간 합화 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        if (
                evidences.size()
                        != candidateAnalysis
                        .candidateCount()

                        || evidences.size()
                        != competitionAnalysis
                        .candidateCount()
        ) {

            throw new IllegalArgumentException(
                    "9-1 후보 수, 9-2 경쟁 Evidence 수, 9-3 합화 Evidence 수가 일치하지 않습니다."
            );
        }

        Set<String> pairKeys =
                new HashSet<>();

        for (
                HeavenlyStemCombinationTransformationEvidence evidence
                : evidences
        ) {

            if (
                    !candidateAnalysis
                            .candidates()
                            .contains(
                                    evidence.candidate()
                            )
            ) {

                throw new IllegalArgumentException(
                        "9-3 Evidence의 후보가 9-1 후보 분석에 존재하지 않습니다."
                );
            }

            if (
                    !competitionAnalysis
                            .evidences()
                            .contains(
                                    evidence
                                            .competitionEvidence()
                            )
            ) {

                throw new IllegalArgumentException(
                        "9-3 CompetitionEvidence가 9-2 분석에 존재하지 않습니다."
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
                        "동일한 천간 궁위 Pair의 9-3 합화 Evidence가 중복되었습니다: "
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
     * 완전 합화
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    transformedCombinations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationTransformationEvidence
                                ::transformed
                )
                .toList();
    }

    public boolean hasTransformedCombination() {

        return !transformedCombinations()
                .isEmpty();
    }

    /*
     * =================================================
     * 부분 합화
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    partiallyTransformedCombinations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationTransformationEvidence
                                ::partiallyTransformed
                )
                .toList();
    }

    public boolean hasPartialTransformation() {

        return !partiallyTransformedCombinations()
                .isEmpty();
    }

    /*
     * =================================================
     * 조건부
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    conditionalCombinations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationTransformationEvidence
                                ::conditional
                )
                .toList();
    }

    /*
     * =================================================
     * 合而不化
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    combinedNotTransformedCombinations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationTransformationEvidence
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
     * 억제
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    suppressedCombinations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationTransformationEvidence
                                ::suppressed
                )
                .toList();
    }

    public boolean hasSuppressedCombination() {

        return !suppressedCombinations()
                .isEmpty();
    }

    /*
     * =================================================
     * 의미 있는 실제 化
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    meaningfulTransformations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationTransformationEvidence
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

    public List<HeavenlyStemCombinationTransformationEvidence>
    transformationsOfState(
            HeavenlyStemCombinationTransformationState state
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
     * 합 종류
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    transformationsOf(
            HeavenlyStemCombinationType combinationType
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

    public List<HeavenlyStemCombinationTransformationEvidence>
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
    establishedTransformationElements() {

        return meaningfulTransformations()
                .stream()
                .map(
                        HeavenlyStemCombinationTransformationEvidence
                                ::targetElement
                )
                .distinct()
                .toList();
    }

    /*
     * =================================================
     * 일간
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    dayMasterCombinations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationTransformationEvidence
                                ::dayMasterInvolved
                )
                .toList();
    }

    public List<HeavenlyStemCombinationTransformationEvidence>
    dayMasterMeaningfulTransformations() {

        return dayMasterCombinations()
                .stream()
                .filter(
                        HeavenlyStemCombinationTransformationEvidence
                                ::meaningfulTransformation
                )
                .toList();
    }

    public boolean dayMasterInTransformation() {

        return !dayMasterMeaningfulTransformations()
                .isEmpty();
    }

    /*
     * =================================================
     * 궁위
     * =================================================
     */

    public List<HeavenlyStemCombinationTransformationEvidence>
    transformationsInvolving(
            VisibleStemPosition position
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

    public Optional<HeavenlyStemCombinationTransformationEvidence>
    transformationBetween(

            VisibleStemPosition position1,

            VisibleStemPosition position2

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