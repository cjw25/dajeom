package com.dajeom.model;

import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchRelationKind;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 9차-7.
 *
 * 하나의 결합 후보를 실제로 건드리는
 * 궁위 Pair 방해 관계.
 *
 * =================================================
 *
 * sourceRelation
 *
 * 8-10 RelationAnalysis에서 생성된
 * EarthlyBranchPairRelation.
 *
 * =================================================
 *
 * obstructionKinds
 *
 * sourceRelation 안에서:
 *
 * CLASH
 * PUNISHMENT
 * BREAK
 * HARM
 *
 * 만 추출한 집합.
 *
 * =================================================
 *
 * affectedCandidatePositions
 *
 * 이 방해 Pair에 직접 참여하는
 * 현재 결합 후보의 구성원 위치.
 *
 * 예:
 *
 * 후보:
 *
 * YEAR 酉
 * DAY 丑
 *
 * 외부:
 *
 * DAY 丑 - HOUR 辰
 * 丑辰破
 *
 * 이라면:
 *
 * affectedCandidatePositions = [DAY]
 *
 * =================================================
 *
 * internalToCandidate
 *
 * sourceRelation의 두 위치 모두가
 * 현재 결합 후보 내부 구성원인가.
 *
 * 예:
 *
 * 巳申六合 후보에서
 * 巳申破가 동일 Pair에 존재하면 true.
 */
public record EarthlyBranchCombinationObstructionRelation(

        EarthlyBranchPairRelation sourceRelation,

        Set<EarthlyBranchRelationKind> obstructionKinds,

        List<EarthlyBranchPosition> affectedCandidatePositions,

        boolean internalToCandidate,

        boolean cardinalBranchAffected

) {

    public EarthlyBranchCombinationObstructionRelation {

        Objects.requireNonNull(
                sourceRelation,
                "8-10 지지 PairRelation이 필요합니다."
        );

        Objects.requireNonNull(
                obstructionKinds,
                "방해 관계 종류가 필요합니다."
        );

        obstructionKinds =
                Set.copyOf(
                        obstructionKinds
                );

        if (
                obstructionKinds.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "하나 이상의 충·형·파·해 관계가 필요합니다."
            );
        }

        for (
                EarthlyBranchRelationKind kind
                : obstructionKinds
        ) {

            if (
                    !isObstructionKind(
                            kind
                    )
            ) {

                throw new IllegalArgumentException(
                        "9-7 방해 관계에는 충·형·파·해만 포함할 수 있습니다: "
                                + kind
                );
            }

            if (
                    !sourceRelation
                            .hasRelation(
                                    kind
                            )
            ) {

                throw new IllegalArgumentException(
                        "방해 관계 종류가 원본 PairRelation에 존재하지 않습니다: "
                                + kind
                );
            }
        }

        affectedCandidatePositions =
                affectedCandidatePositions == null
                        ? List.of()
                        : affectedCandidatePositions
                        .stream()
                        .distinct()
                        .toList();

        if (
                affectedCandidatePositions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "방해 관계는 결합 후보의 구성원 하나 이상에 영향을 주어야 합니다."
            );
        }

        for (
                EarthlyBranchPosition position
                : affectedCandidatePositions
        ) {

            if (
                    !sourceRelation
                            .involves(
                                    position
                            )
            ) {

                throw new IllegalArgumentException(
                        "영향받는 후보 위치가 원본 PairRelation에 포함되지 않습니다: "
                                + position
                );
            }
        }

        boolean expectedInternal =
                affectedCandidatePositions.size()
                        == 2;

        if (
                internalToCandidate
                        != expectedInternal
        ) {

            throw new IllegalArgumentException(
                    "결합 내부 방해 상태가 영향을 받는 후보 위치 수와 일치하지 않습니다."
            );
        }

        if (
                cardinalBranchAffected
                        && affectedCandidatePositions
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "왕지 영향 상태에는 영향을 받는 후보 구성원이 필요합니다."
            );
        }
    }

    private static boolean isObstructionKind(
            EarthlyBranchRelationKind kind
    ) {

        return kind
                == EarthlyBranchRelationKind.CLASH

                || kind
                == EarthlyBranchRelationKind.PUNISHMENT

                || kind
                == EarthlyBranchRelationKind.BREAK

                || kind
                == EarthlyBranchRelationKind.HARM;
    }

    public EarthlyBranchPosition firstPosition() {

        return sourceRelation
                .firstPosition();
    }

    public EarthlyBranchPosition secondPosition() {

        return sourceRelation
                .secondPosition();
    }

    public boolean adjacent() {

        return sourceRelation
                .adjacent();
    }

    public int positionDistance() {

        return sourceRelation
                .positionDistance();
    }

    public boolean externalToCandidate() {

        return !internalToCandidate;
    }

    public int affectedMemberCount() {

        return affectedCandidatePositions
                .size();
    }

    public boolean hasClash() {

        return obstructionKinds.contains(
                EarthlyBranchRelationKind.CLASH
        );
    }

    public boolean hasPunishment() {

        return obstructionKinds.contains(
                EarthlyBranchRelationKind.PUNISHMENT
        );
    }

    public boolean hasBreak() {

        return obstructionKinds.contains(
                EarthlyBranchRelationKind.BREAK
        );
    }

    public boolean hasHarm() {

        return obstructionKinds.contains(
                EarthlyBranchRelationKind.HARM
        );
    }

    public boolean multipleObstructionKinds() {

        return obstructionKinds.size() >= 2;
    }
}