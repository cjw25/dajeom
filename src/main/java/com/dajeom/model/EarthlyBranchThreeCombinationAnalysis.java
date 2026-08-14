package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchThreeCombinationForm;
import com.dajeom.type.EarthlyBranchThreeCombinationType;
import com.dajeom.type.FiveElement;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 8차-4.
 *
 * 원국 전체 삼합 관계 분석.
 *
 * =================================================
 *
 * 포함:
 *
 * COMPLETE
 * 완전 삼합
 *
 * HALF_WITH_CARDINAL
 * 왕지 포함 반합
 *
 * PARTIAL_WITHOUT_CARDINAL
 * 왕지 없는 부분조합
 *
 * =================================================
 *
 * 완전 삼합이 있는 경우에도
 * 내부 Pair Evidence를 삭제하지 않는다.
 *
 * 대신:
 *
 * coveredByCompleteCombination
 *
 * 을 통해 중복 포함 관계를 표시한다.
 *
 * =================================================
 *
 * 따라서 이후 9차에서는:
 *
 * 완전 삼합을 먼저 보고,
 * 그 안의 반합 Evidence를
 * 별도 독립국처럼 다시 계산하지 않을 수 있다.
 */
public record EarthlyBranchThreeCombinationAnalysis(

        EarthlyBranch dayBranch,

        List<EarthlyBranchThreeCombinationEvidence> evidences,

        int knownBranchCount,

        boolean completeChart

) {

    public EarthlyBranchThreeCombinationAnalysis {

        Objects.requireNonNull(
                dayBranch,
                "일지가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        int expectedKnownBranchCount =
                completeChart
                        ? 4
                        : 3;

        if (
                knownBranchCount
                        != expectedKnownBranchCount
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성과 알려진 지지 수가 일치하지 않습니다."
            );
        }

        /*
         * 동일 형태 + 동일 위치 구성의
         * Evidence 중복 방지.
         */
        Set<String> uniqueKeys =
                new HashSet<>();

        for (
                EarthlyBranchThreeCombinationEvidence evidence
                : evidences
        ) {

            String key =
                    evidence.combinationType().name()
                            + ":"
                            + evidence.form().name()
                            + ":"
                            + evidence.positions();

            if (
                    !uniqueKeys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 삼합 Evidence가 중복되었습니다: "
                                + key
                );
            }

            if (
                    evidence.involves(
                            EarthlyBranchPosition.DAY
                    )
            ) {

                boolean dayMatches =
                        matchesDayBranch(
                                evidence,
                                dayBranch
                        );

                if (
                        !dayMatches
                ) {

                    throw new IllegalArgumentException(
                            "DAY 위치의 지지가 실제 일지와 일치하지 않습니다."
                    );
                }
            }
        }
    }

    private static boolean matchesDayBranch(

            EarthlyBranchThreeCombinationEvidence evidence,

            EarthlyBranch dayBranch

    ) {

        if (
                evidence.firstPosition()
                        == EarthlyBranchPosition.DAY
        ) {

            return evidence.firstBranch()
                    == dayBranch;
        }

        if (
                evidence.secondPosition()
                        == EarthlyBranchPosition.DAY
        ) {

            return evidence.secondBranch()
                    == dayBranch;
        }

        if (
                evidence.thirdPosition()
                        == EarthlyBranchPosition.DAY
        ) {

            return evidence.thirdBranch()
                    == dayBranch;
        }

        return true;
    }

    public boolean hasRelation() {

        return !evidences
                .isEmpty();
    }

    public List<EarthlyBranchThreeCombinationEvidence>
    completeCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationEvidence
                                ::completeCombination
                )
                .toList();
    }

    public List<EarthlyBranchThreeCombinationEvidence>
    halfCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationEvidence
                                ::halfCombination
                )
                .toList();
    }

    public List<EarthlyBranchThreeCombinationEvidence>
    partialWithoutCardinalCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationEvidence
                                ::partialWithoutCardinal
                )
                .toList();
    }

    /**
     * 완전 삼합 안에 포함되지 않은
     * 독립 반합/부분조합.
     */
    public List<EarthlyBranchThreeCombinationEvidence>
    standalonePartialRelations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationEvidence
                                ::standalonePartialRelation
                )
                .toList();
    }

    /**
     * 완전 삼합 내부에 이미 포함된
     * Pair Evidence.
     */
    public List<EarthlyBranchThreeCombinationEvidence>
    coveredPartialRelations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationEvidence
                                ::subordinateToCompleteCombination
                )
                .toList();
    }

    public boolean hasCompleteCombination() {

        return !completeCombinations()
                .isEmpty();
    }

    public boolean hasHalfCombination() {

        return !halfCombinations()
                .isEmpty();
    }

    public boolean hasStandaloneHalfCombination() {

        return standalonePartialRelations()
                .stream()
                .anyMatch(
                        EarthlyBranchThreeCombinationEvidence
                                ::halfCombination
                );
    }

    public boolean hasPartialWithoutCardinal() {

        return !partialWithoutCardinalCombinations()
                .isEmpty();
    }

    public int completeCombinationCount() {

        return completeCombinations()
                .size();
    }

    public List<EarthlyBranchThreeCombinationEvidence>
    dayBranchRelations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public List<EarthlyBranchThreeCombinationEvidence>
    dayBranchCompleteCombinations() {

        return completeCombinations()
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public boolean dayBranchInThreeCombinationRelation() {

        return !dayBranchRelations()
                .isEmpty();
    }

    public boolean dayBranchInCompleteCombination() {

        return !dayBranchCompleteCombinations()
                .isEmpty();
    }

    public List<EarthlyBranchThreeCombinationEvidence>
    relationsOf(
            EarthlyBranchThreeCombinationType combinationType
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

    public List<EarthlyBranchThreeCombinationEvidence>
    relationsOfForm(
            EarthlyBranchThreeCombinationForm form
    ) {

        if (
                form == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.form() == form
                )
                .toList();
    }

    public List<EarthlyBranchThreeCombinationEvidence>
    relationsInvolving(
            EarthlyBranch branch
    ) {

        if (
                branch == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        branch
                                )
                )
                .toList();
    }

    public List<EarthlyBranchThreeCombinationEvidence>
    relationsInvolving(
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

    public List<EarthlyBranchThreeCombinationEvidence>
    relationsTargeting(
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
                                evidence
                                        .nominalTransformationElement()
                                        == element
                )
                .toList();
    }

    /**
     * 실제 9차 합화 판단에서 우선 검토할 Evidence.
     *
     * 완전 삼합
     * +
     * 완전 삼합에 포함되지 않은 독립 Pair
     *
     * 만 반환한다.
     *
     * 완전 삼합 내부 Pair를 중복으로 다시
     * 검토하지 않도록 하기 위한 편의 메서드.
     */
    public List<EarthlyBranchThreeCombinationEvidence>
    independentRelations() {

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.completeCombination()
                                        || evidence
                                        .standalonePartialRelation()
                )
                .toList();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}