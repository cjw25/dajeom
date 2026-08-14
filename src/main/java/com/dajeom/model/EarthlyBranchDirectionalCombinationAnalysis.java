package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchDirectionalCombinationForm;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 8차-5.
 *
 * 원국 전체 방합 관계 분석.
 *
 * =================================================
 *
 * 포함:
 *
 * COMPLETE
 * 완전 방합
 *
 * PARTIAL_WITH_CARDINAL
 * 왕지 포함 부분 방합
 *
 * PARTIAL_WITHOUT_CARDINAL
 * 왕지 없는 부분 방합
 *
 * =================================================
 *
 * 삼합 분석과 독립적이다.
 *
 * 같은 원국에서:
 *
 * 삼합 Evidence
 * +
 * 방합 Evidence
 *
 * 가 동시에 존재할 수 있다.
 *
 * 어느 쪽이 우세한지는
 * 여기서 결정하지 않는다.
 */
public record EarthlyBranchDirectionalCombinationAnalysis(

        EarthlyBranch dayBranch,

        List<EarthlyBranchDirectionalCombinationEvidence> evidences,

        int knownBranchCount,

        boolean completeChart

) {

    public EarthlyBranchDirectionalCombinationAnalysis {

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

        Set<String> uniqueKeys =
                new HashSet<>();

        for (
                EarthlyBranchDirectionalCombinationEvidence evidence
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
                        "동일한 방합 Evidence가 중복되었습니다: "
                                + key
                );
            }

            if (
                    evidence.involves(
                            EarthlyBranchPosition.DAY
                    )

                            && !matchesDayBranch(
                            evidence,
                            dayBranch
                    )
            ) {

                throw new IllegalArgumentException(
                        "DAY 위치의 지지가 실제 일지와 일치하지 않습니다."
                );
            }
        }
    }

    private static boolean matchesDayBranch(

            EarthlyBranchDirectionalCombinationEvidence evidence,

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

    public List<EarthlyBranchDirectionalCombinationEvidence>
    completeCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationEvidence
                                ::completeCombination
                )
                .toList();
    }

    public List<EarthlyBranchDirectionalCombinationEvidence>
    partialWithCardinalCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationEvidence
                                ::partialWithCardinal
                )
                .toList();
    }

    public List<EarthlyBranchDirectionalCombinationEvidence>
    partialWithoutCardinalCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationEvidence
                                ::partialWithoutCardinal
                )
                .toList();
    }

    public List<EarthlyBranchDirectionalCombinationEvidence>
    standalonePartialRelations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationEvidence
                                ::standalonePartialRelation
                )
                .toList();
    }

    public List<EarthlyBranchDirectionalCombinationEvidence>
    coveredPartialRelations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationEvidence
                                ::subordinateToCompleteCombination
                )
                .toList();
    }

    public boolean hasCompleteCombination() {

        return !completeCombinations()
                .isEmpty();
    }

    public boolean hasPartialWithCardinal() {

        return !partialWithCardinalCombinations()
                .isEmpty();
    }

    public boolean hasStandalonePartialWithCardinal() {

        return standalonePartialRelations()
                .stream()
                .anyMatch(
                        EarthlyBranchDirectionalCombinationEvidence
                                ::partialWithCardinal
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

    public List<EarthlyBranchDirectionalCombinationEvidence>
    dayBranchRelations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public List<EarthlyBranchDirectionalCombinationEvidence>
    dayBranchCompleteCombinations() {

        return completeCombinations()
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public boolean dayBranchInDirectionalRelation() {

        return !dayBranchRelations()
                .isEmpty();
    }

    public boolean dayBranchInCompleteCombination() {

        return !dayBranchCompleteCombinations()
                .isEmpty();
    }

    public List<EarthlyBranchDirectionalCombinationEvidence>
    relationsOf(
            EarthlyBranchDirectionalCombinationType combinationType
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

    public List<EarthlyBranchDirectionalCombinationEvidence>
    relationsOfForm(
            EarthlyBranchDirectionalCombinationForm form
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

    public List<EarthlyBranchDirectionalCombinationEvidence>
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

    public List<EarthlyBranchDirectionalCombinationEvidence>
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

    /**
     * 명목상 해당 오행의 方을 형성하는 Evidence.
     *
     * 실제 합화 완료 여부는 의미하지 않는다.
     */
    public List<EarthlyBranchDirectionalCombinationEvidence>
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
     * 9차에서 독립적으로 평가할 관계.
     *
     * 완전 방합
     * +
     * 완전 방합 내부에 포함되지 않은 부분 관계
     */
    public List<EarthlyBranchDirectionalCombinationEvidence>
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