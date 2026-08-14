package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationType;
import com.dajeom.type.VisibleStemPosition;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 8차-1.
 *
 * 원국 전체 천간합 탐지 결과.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 하나의 천간이 여러 동일 상대 천간과
 * 합 관계를 만들 수 있다.
 *
 * 예:
 *
 * 年干 己
 * 日干 甲
 * 時干 己
 *
 * 라면:
 *
 * 年己 - 日甲
 * 日甲 - 時己
 *
 * 두 개의 관계 Evidence를 모두 보존한다.
 *
 * 이 단계에서:
 *
 * "甲이 어느 己와 실제로 합하는가"
 *
 * 를 임의로 하나만 선택하지 않는다.
 *
 * 실제 결합 우선도와 합화 가능성은
 * 이후 단계에서 판단한다.
 */
public record HeavenlyStemCombinationAnalysis(

        HeavenlyStem dayMaster,

        List<HeavenlyStemCombinationEvidence> evidences,

        int knownStemCount,

        boolean completeChart

) {

    public HeavenlyStemCombinationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        int expectedKnownStemCount =
                completeChart
                        ? 4
                        : 3;

        if (
                knownStemCount
                        != expectedKnownStemCount
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성과 알려진 천간 수가 일치하지 않습니다."
            );
        }

        /*
         * 동일 위치 Pair 중복 방지.
         *
         * 동일 합 유형이 여러 번 존재하는 것은 허용한다.
         */
        Set<String> positionPairs =
                new HashSet<>();

        for (
                HeavenlyStemCombinationEvidence evidence
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
                        "동일한 천간 위치 Pair가 중복되었습니다: "
                                + key
                );
            }

            /*
             * DAY 위치가 포함된 Evidence의 실제 천간은
             * 반드시 dayMaster와 같아야 한다.
             */
            if (
                    evidence.firstPosition()
                            == VisibleStemPosition.DAY

                            && evidence.firstStem()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "DAY 위치 천간이 일간과 일치하지 않습니다."
                );
            }

            if (
                    evidence.secondPosition()
                            == VisibleStemPosition.DAY

                            && evidence.secondStem()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "DAY 위치 천간이 일간과 일치하지 않습니다."
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

    public List<HeavenlyStemCombinationEvidence>
    dayMasterCombinations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationEvidence
                                ::dayMasterInvolved
                )
                .toList();
    }

    public List<HeavenlyStemCombinationEvidence>
    adjacentCombinations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationEvidence
                                ::adjacent
                )
                .toList();
    }

    public List<HeavenlyStemCombinationEvidence>
    adjacentDayMasterCombinations() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationEvidence
                                ::adjacentToDayMaster
                )
                .toList();
    }

    public List<HeavenlyStemCombinationEvidence>
    combinationsOf(
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

    public List<HeavenlyStemCombinationEvidence>
    combinationsInvolving(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        stem
                                )
                )
                .toList();
    }

    public List<HeavenlyStemCombinationEvidence>
    combinationsInvolving(
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

    /**
     * 해당 명목상 합화 오행으로 향하는
     * 천간합 Evidence.
     *
     * 실제 합화 여부를 의미하지 않는다.
     */
    public List<HeavenlyStemCombinationEvidence>
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

    public boolean dayMasterCombined() {

        return !dayMasterCombinations()
                .isEmpty();
    }

    public boolean dayMasterHasMultipleCombinationRelations() {

        return dayMasterCombinations()
                .size() >= 2;
    }

    public boolean hasAdjacentCombination() {

        return !adjacentCombinations()
                .isEmpty();
    }

    public boolean hasCombinationType(
            HeavenlyStemCombinationType type
    ) {

        if (
                type == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.combinationType()
                                        == type
                );
    }

    public Optional<HeavenlyStemCombinationEvidence>
    combinationBetween(

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