package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchSixCombinationType;
import com.dajeom.type.FiveElement;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 8차-3.
 *
 * 원국 전체 지지 육합 분석.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 하나의 지지가 같은 종류의 상대 지지와
 * 복수 관계를 만들 수 있다.
 *
 * 예:
 *
 * 年支 丑
 * 日支 子
 * 時支 丑
 *
 * 라면:
 *
 * 年丑 - 日子
 * 日子 - 時丑
 *
 * 두 개의 子丑六合 Evidence를 모두 보존한다.
 *
 * =================================================
 *
 * 이 단계에서:
 *
 * - 어느 육합이 실제 우세한지
 * - 하나만 결합하는지
 * - 합화가 성립했는지
 * - 충 때문에 깨지는지
 *
 * 를 결정하지 않는다.
 *
 * 8차는 관계 탐지,
 * 9차는 합화 및 상호작용 검증이다.
 */
public record EarthlyBranchSixCombinationAnalysis(

        EarthlyBranch dayBranch,

        List<EarthlyBranchSixCombinationEvidence> evidences,

        int knownBranchCount,

        boolean completeChart

) {

    public EarthlyBranchSixCombinationAnalysis {

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
         * 동일 위치 Pair가 중복 생성되는 것을 방지한다.
         *
         * 동일 육합 유형이 원국에서 여러 번 존재하는 것은
         * 정상적으로 허용한다.
         */
        Set<String> positionPairs =
                new HashSet<>();

        for (
                EarthlyBranchSixCombinationEvidence evidence
                : evidences
        ) {

            String key =
                    evidence.firstPosition().name()
                            + ":"
                            + evidence.secondPosition().name();

            if (
                    !positionPairs.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 지지 위치 Pair의 육합 Evidence가 중복되었습니다: "
                                + key
                );
            }

            if (
                    evidence.firstPosition()
                            == EarthlyBranchPosition.DAY

                            && evidence.firstBranch()
                            != dayBranch
            ) {

                throw new IllegalArgumentException(
                        "DAY 위치 지지가 실제 일지와 일치하지 않습니다."
                );
            }

            if (
                    evidence.secondPosition()
                            == EarthlyBranchPosition.DAY

                            && evidence.secondBranch()
                            != dayBranch
            ) {

                throw new IllegalArgumentException(
                        "DAY 위치 지지가 실제 일지와 일치하지 않습니다."
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

    public List<EarthlyBranchSixCombinationEvidence>
    dayBranchCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public List<EarthlyBranchSixCombinationEvidence>
    adjacentCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationEvidence
                                ::adjacent
                )
                .toList();
    }

    public List<EarthlyBranchSixCombinationEvidence>
    adjacentDayBranchCombinations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchSixCombinationEvidence
                                ::adjacentToDayBranch
                )
                .toList();
    }

    public List<EarthlyBranchSixCombinationEvidence>
    combinationsOf(
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

    public List<EarthlyBranchSixCombinationEvidence>
    combinationsInvolving(
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

    public List<EarthlyBranchSixCombinationEvidence>
    combinationsInvolving(
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
     * 해당 명목상 합화 오행을 지향하는
     * 육합 Evidence.
     *
     * 실제 합화 여부를 의미하지 않는다.
     */
    public List<EarthlyBranchSixCombinationEvidence>
    combinationsTargeting(
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

    public boolean dayBranchCombined() {

        return !dayBranchCombinations()
                .isEmpty();
    }

    /**
     * 일지가 둘 이상의 다른 위치와
     * 육합 관계 Evidence를 가지는가.
     *
     * 이후 경합 관계 분석에 사용할 수 있다.
     */
    public boolean dayBranchHasMultipleCombinationRelations() {

        return dayBranchCombinations()
                .size() >= 2;
    }

    public boolean hasAdjacentCombination() {

        return !adjacentCombinations()
                .isEmpty();
    }

    public boolean hasCombinationType(
            EarthlyBranchSixCombinationType combinationType
    ) {

        if (
                combinationType == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.combinationType()
                                        == combinationType
                );
    }

    /**
     * 두 원국 위치 사이의 육합 관계.
     */
    public Optional<EarthlyBranchSixCombinationEvidence>
    combinationBetween(

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