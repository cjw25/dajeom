package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 지지 합화 여부를 판단하기 직전의
 * 전체 Context.
 *
 * =================================================
 *
 * 합 관계 존재
 * != 합화 성립
 *
 * =================================================
 *
 * 보존:
 *
 * 1. 어떤 합인가
 * 2. 어느 오행으로 향하는 합인가
 * 3. 그 화신이 월령에서 旺相休囚死 중 어디인가
 * 4. 화신 오행이 천간에 실제 드러났는가
 * 5. 화신 오행이 지장간에 실제 근기를 가지는가
 * 6. 합 참여 지지가 충을 받고 있는가
 * 7. 다른 합 구조와 경쟁하고 있는가
 *
 * =================================================
 *
 * 의도적으로 만들지 않는 메서드:
 *
 * transformed()
 * transformationSucceeded()
 * isTrueTransformation()
 *
 * 실제 합화 성립 여부는
 * 다음 Rule 단계에서 결정한다.
 */
public record BranchCombinationTransformationContext(

        BranchCombinationOccurrence occurrence,

        FiveElementSeasonState transformationElementSeasonState,

        List<PillarPosition>
        visibleTransformationElementPositions,

        List<BranchTransformationHiddenStemHit>
        hiddenTransformationElementHits,

        Set<PillarPosition>
        clashedParticipantPositions,

        Set<PillarPosition>
        competingCombinationPositions

) {

    public BranchCombinationTransformationContext {

        Objects.requireNonNull(
                occurrence,
                "지지 합 관계는 필수입니다."
        );

        Objects.requireNonNull(
                transformationElementSeasonState,
                "화신의 월령 상태는 필수입니다."
        );

        visibleTransformationElementPositions =
                visibleTransformationElementPositions == null
                        ? List.of()
                        : List.copyOf(
                        visibleTransformationElementPositions
                );

        hiddenTransformationElementHits =
                hiddenTransformationElementHits == null
                        ? List.of()
                        : List.copyOf(
                        hiddenTransformationElementHits
                );

        clashedParticipantPositions =
                clashedParticipantPositions == null
                        ? Set.of()
                        : Set.copyOf(
                        clashedParticipantPositions
                );

        competingCombinationPositions =
                competingCombinationPositions == null
                        ? Set.of()
                        : Set.copyOf(
                        competingCombinationPositions
                );

        for (
                PillarPosition position
                : clashedParticipantPositions
        ) {

            if (
                    !occurrence.involves(
                            position
                    )
            ) {

                throw new IllegalArgumentException(
                        "충 위치가 현재 합 참여 지지가 아닙니다."
                );
            }
        }

        for (
                PillarPosition position
                : competingCombinationPositions
        ) {

            if (
                    !occurrence.involves(
                            position
                    )
            ) {

                throw new IllegalArgumentException(
                        "합 경쟁 위치가 현재 합 참여 지지가 아닙니다."
                );
            }
        }
    }

    public FiveElement transformationElement() {

        return occurrence
                .transformationElement();
    }

    /*
     * =================================================
     * 월령 Context
     * =================================================
     */

    public boolean transformationElementWang() {

        return transformationElementSeasonState
                == FiveElementSeasonState.WANG;
    }

    public boolean transformationElementXiang() {

        return transformationElementSeasonState
                == FiveElementSeasonState.XIANG;
    }

    public boolean transformationElementWangOrXiang() {

        return transformationElementWang()
                ||
                transformationElementXiang();
    }

    /*
     * =================================================
     * 화신의 천간 드러남
     * =================================================
     */

    public boolean hasVisibleTransformationElement() {

        return !visibleTransformationElementPositions
                .isEmpty();
    }

    /*
     * =================================================
     * 화신의 지장간 기반
     * =================================================
     */

    public boolean hasHiddenTransformationElement() {

        return !hiddenTransformationElementHits
                .isEmpty();
    }

    /*
     * =================================================
     * 방해 Context
     * =================================================
     */

    public boolean hasClashedParticipant() {

        return !clashedParticipantPositions
                .isEmpty();
    }

    public boolean hasCompetingCombination() {

        return !competingCombinationPositions
                .isEmpty();
    }

    public boolean hasTransformationDisturbance() {

        return hasClashedParticipant()
                ||
                hasCompetingCombination();
    }
}