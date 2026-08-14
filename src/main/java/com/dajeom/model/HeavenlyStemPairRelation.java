package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemRelationKind;
import com.dajeom.type.VisibleStemPosition;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 8차-10.
 *
 * 하나의 천간 궁위 Pair에 존재하는
 * 모든 관계를 묶은 관계망 노드.
 *
 * =================================================
 *
 * 예:
 *
 * MONTH 丙
 * DAY 辛
 *
 * 이라면:
 *
 * 丙辛合水
 * +
 * 丙火剋辛金
 *
 * 이므로:
 *
 * relationKinds
 *
 * COMBINATION
 * CONTROL_ONLY
 *
 * 두 개가 동시에 존재한다.
 *
 * =================================================
 *
 * first / second는 관계 방향이 아니다.
 *
 * 항상:
 *
 * YEAR
 * MONTH
 * DAY
 * HOUR
 *
 * 원국 위치 순서로 저장한다.
 *
 * 극제 방향이 필요하면
 * HeavenlyStemControlAnalysis의
 * 원본 Evidence를 사용한다.
 */
public record HeavenlyStemPairRelation(

        VisibleStemPosition firstPosition,

        HeavenlyStem firstStem,

        VisibleStemPosition secondPosition,

        HeavenlyStem secondStem,

        Set<HeavenlyStemRelationKind> relationKinds

) {

    public HeavenlyStemPairRelation {

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 천간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                firstStem,
                "첫 번째 천간이 필요합니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 천간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                secondStem,
                "두 번째 천간이 필요합니다."
        );

        Objects.requireNonNull(
                relationKinds,
                "천간 관계 종류가 필요합니다."
        );

        if (
                firstPosition.ordinal()
                        >= secondPosition.ordinal()
        ) {

            throw new IllegalArgumentException(
                    "천간 Pair는 원국 위치 순서대로 저장해야 합니다."
            );
        }

        if (
                relationKinds.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "천간 PairRelation에는 하나 이상의 관계가 필요합니다."
            );
        }

        relationKinds =
                Set.copyOf(
                        EnumSet.copyOf(
                                relationKinds
                        )
                );

        /*
         * CLASH와 CONTROL_ONLY는
         * 8-2의 동일 분류축에서 서로 배타적이다.
         */
        if (
                relationKinds.contains(
                        HeavenlyStemRelationKind.CLASH
                )
                        && relationKinds.contains(
                        HeavenlyStemRelationKind.CONTROL_ONLY
                )
        ) {

            throw new IllegalArgumentException(
                    "하나의 천간 Pair를 CLASH와 CONTROL_ONLY로 동시에 분류할 수 없습니다."
            );
        }
    }

    public int positionDistance() {

        return secondPosition.ordinal()
                - firstPosition.ordinal();
    }

    public boolean adjacent() {

        return positionDistance() == 1;
    }

    public boolean dayMasterInvolved() {

        return firstPosition
                == VisibleStemPosition.DAY

                || secondPosition
                == VisibleStemPosition.DAY;
    }

    public boolean monthStemInvolved() {

        return firstPosition
                == VisibleStemPosition.MONTH

                || secondPosition
                == VisibleStemPosition.MONTH;
    }

    public boolean involves(
            VisibleStemPosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return firstPosition == position
                || secondPosition == position;
    }

    public boolean involves(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return false;
        }

        return firstStem == stem
                || secondStem == stem;
    }

    public boolean hasRelation(
            HeavenlyStemRelationKind relationKind
    ) {

        if (
                relationKind == null
        ) {

            return false;
        }

        return relationKinds.contains(
                relationKind
        );
    }

    public boolean hasCombination() {

        return hasRelation(
                HeavenlyStemRelationKind
                        .COMBINATION
        );
    }

    public boolean hasClash() {

        return hasRelation(
                HeavenlyStemRelationKind
                        .CLASH
        );
    }

    public boolean hasControlOnly() {

        return hasRelation(
                HeavenlyStemRelationKind
                        .CONTROL_ONLY
        );
    }

    public boolean hasControlRelation() {

        return hasClash()
                || hasControlOnly();
    }

    /**
     * 동일 Pair에 둘 이상의
     * 독립 관계 종류가 존재하는가.
     *
     * 예:
     *
     * 천간합 + 일반 극제
     */
    public boolean overlappingRelations() {

        return relationKinds.size() >= 2;
    }

    public int relationCount() {

        return relationKinds.size();
    }

    public HeavenlyStem counterpartOf(
            VisibleStemPosition position
    ) {

        if (
                position == null
        ) {

            return null;
        }

        if (
                firstPosition == position
        ) {

            return secondStem;
        }

        if (
                secondPosition == position
        ) {

            return firstStem;
        }

        return null;
    }
}