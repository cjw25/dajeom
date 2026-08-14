package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchRelationKind;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 8차-10.
 *
 * 하나의 지지 궁위 Pair에 존재하는
 * 관계 종류 전체를 묶는다.
 *
 * =================================================
 *
 * 예:
 *
 * MONTH 巳
 * DAY 申
 *
 * 이라면 구조에 따라:
 *
 * SIX_COMBINATION
 * PUNISHMENT
 * BREAK
 *
 * 가 한 Pair에 동시에 존재할 수 있다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 이 클래스는 세부 Evidence를
 * 대체하지 않는다.
 *
 * 예를 들어 THREE_COMBINATION이 있어도:
 *
 * - COMPLETE인지
 * - HALF_WITH_CARDINAL인지
 * - PARTIAL_WITHOUT_CARDINAL인지
 *
 * 는 EarthlyBranchThreeCombinationAnalysis에서
 * 확인해야 한다.
 *
 * =================================================
 *
 * RelationPair는:
 *
 * "이 두 궁위 사이에 어떤 관계 종류들이
 * 겹쳐 있는가"
 *
 * 를 빠르게 조회하기 위한 관계망이다.
 */
public record EarthlyBranchPairRelation(

        EarthlyBranchPosition firstPosition,

        EarthlyBranch firstBranch,

        EarthlyBranchPosition secondPosition,

        EarthlyBranch secondBranch,

        Set<EarthlyBranchRelationKind> relationKinds

) {

    public EarthlyBranchPairRelation {

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                firstBranch,
                "첫 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                secondBranch,
                "두 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                relationKinds,
                "지지 관계 종류가 필요합니다."
        );

        if (
                firstPosition.ordinal()
                        >= secondPosition.ordinal()
        ) {

            throw new IllegalArgumentException(
                    "지지 Pair는 원국 위치 순서대로 저장해야 합니다."
            );
        }

        if (
                relationKinds.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "지지 PairRelation에는 하나 이상의 관계가 필요합니다."
            );
        }

        relationKinds =
                Set.copyOf(
                        EnumSet.copyOf(
                                relationKinds
                        )
                );
    }

    public int positionDistance() {

        return secondPosition.ordinal()
                - firstPosition.ordinal();
    }

    public boolean adjacent() {

        return positionDistance() == 1;
    }

    public boolean dayBranchInvolved() {

        return firstPosition
                == EarthlyBranchPosition.DAY

                || secondPosition
                == EarthlyBranchPosition.DAY;
    }

    public boolean monthBranchInvolved() {

        return firstPosition
                == EarthlyBranchPosition.MONTH

                || secondPosition
                == EarthlyBranchPosition.MONTH;
    }

    public boolean involves(
            EarthlyBranchPosition position
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
            EarthlyBranch branch
    ) {

        if (
                branch == null
        ) {

            return false;
        }

        return firstBranch == branch
                || secondBranch == branch;
    }

    public boolean hasRelation(
            EarthlyBranchRelationKind relationKind
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

    public boolean hasSixCombination() {

        return hasRelation(
                EarthlyBranchRelationKind
                        .SIX_COMBINATION
        );
    }

    public boolean hasThreeCombination() {

        return hasRelation(
                EarthlyBranchRelationKind
                        .THREE_COMBINATION
        );
    }

    public boolean hasDirectionalCombination() {

        return hasRelation(
                EarthlyBranchRelationKind
                        .DIRECTIONAL_COMBINATION
        );
    }

    public boolean hasClash() {

        return hasRelation(
                EarthlyBranchRelationKind
                        .CLASH
        );
    }

    public boolean hasPunishment() {

        return hasRelation(
                EarthlyBranchRelationKind
                        .PUNISHMENT
        );
    }

    public boolean hasBreak() {

        return hasRelation(
                EarthlyBranchRelationKind
                        .BREAK
        );
    }

    public boolean hasHarm() {

        return hasRelation(
                EarthlyBranchRelationKind
                        .HARM
        );
    }

    /**
     * 합 계열 관계가 하나 이상 존재하는가.
     */
    public boolean hasCombinationFamilyRelation() {

        return relationKinds
                .stream()
                .anyMatch(
                        EarthlyBranchRelationKind
                                ::combinationFamily
                );
    }

    /**
     * 충·형·파·해 계열 관계가
     * 하나 이상 존재하는가.
     *
     * 이것 자체가 길흉을 뜻하지 않는다.
     */
    public boolean hasNonCombinationFamilyRelation() {

        return relationKinds
                .stream()
                .anyMatch(
                        EarthlyBranchRelationKind
                                ::nonCombinationFamily
                );
    }

    /**
     * 합 계열과 비합 계열 관계가
     * 동일 Pair에서 동시에 존재하는가.
     *
     * 예:
     *
     * 巳申
     *
     * 六合
     * +
     * 刑
     * +
     * 破
     */
    public boolean mixedRelationFamilies() {

        return hasCombinationFamilyRelation()
                && hasNonCombinationFamilyRelation();
    }

    /**
     * 둘 이상의 관계 종류가
     * 동일 궁위 Pair에 겹쳐 있는가.
     */
    public boolean overlappingRelations() {

        return relationKinds.size() >= 2;
    }

    public int relationCount() {

        return relationKinds.size();
    }

    public int combinationFamilyRelationCount() {

        return (int) relationKinds
                .stream()
                .filter(
                        EarthlyBranchRelationKind
                                ::combinationFamily
                )
                .count();
    }

    public int nonCombinationFamilyRelationCount() {

        return (int) relationKinds
                .stream()
                .filter(
                        EarthlyBranchRelationKind
                                ::nonCombinationFamily
                )
                .count();
    }

    public EarthlyBranch counterpartOf(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return null;
        }

        if (
                firstPosition == position
        ) {

            return secondBranch;
        }

        if (
                secondPosition == position
        ) {

            return firstBranch;
        }

        return null;
    }
}