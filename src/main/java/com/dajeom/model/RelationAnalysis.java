package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchRelationKind;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemRelationKind;
import com.dajeom.type.VisibleStemPosition;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 8차-10.
 *
 * 천간·지지 관계 분석의 최종 통합 모델.
 *
 * =================================================
 *
 * 포함:
 *
 * 8-1
 * HeavenlyStemCombinationAnalysis
 *
 * 8-2
 * HeavenlyStemControlAnalysis
 *
 * 8-3
 * EarthlyBranchSixCombinationAnalysis
 *
 * 8-4
 * EarthlyBranchThreeCombinationAnalysis
 *
 * 8-5
 * EarthlyBranchDirectionalCombinationAnalysis
 *
 * 8-6
 * EarthlyBranchClashAnalysis
 *
 * 8-7
 * EarthlyBranchPunishmentAnalysis
 *
 * 8-8
 * EarthlyBranchBreakAnalysis
 *
 * 8-9
 * EarthlyBranchHarmAnalysis
 *
 * =================================================
 *
 * 추가:
 *
 * heavenlyStemPairRelations
 *
 * earthlyBranchPairRelations
 *
 * 을 통해 각 궁위 Pair에
 * 어떤 관계들이 중첩되어 있는지
 * 빠르게 조회할 수 있다.
 *
 * =================================================
 *
 * 이 클래스에서 하지 않는 것:
 *
 * - 실제 합화
 * - 합충 우선순위 확정
 * - 오행 변환
 * - 관계 점수 합산
 * - 길흉 판단
 *
 * 위 내용은 9차 이후 단계의 책임이다.
 */
public record RelationAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        EarthlyBranch dayBranch,

        HeavenlyStemCombinationAnalysis
        heavenlyStemCombinationAnalysis,

        HeavenlyStemControlAnalysis
        heavenlyStemControlAnalysis,

        EarthlyBranchSixCombinationAnalysis
        sixCombinationAnalysis,

        EarthlyBranchThreeCombinationAnalysis
        threeCombinationAnalysis,

        EarthlyBranchDirectionalCombinationAnalysis
        directionalCombinationAnalysis,

        EarthlyBranchClashAnalysis
        clashAnalysis,

        EarthlyBranchPunishmentAnalysis
        punishmentAnalysis,

        EarthlyBranchBreakAnalysis
        breakAnalysis,

        EarthlyBranchHarmAnalysis
        harmAnalysis,

        List<HeavenlyStemPairRelation>
        heavenlyStemPairRelations,

        List<EarthlyBranchPairRelation>
        earthlyBranchPairRelations,

        boolean completeChart

) {

    public RelationAnalysis {

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
                heavenlyStemCombinationAnalysis,
                "8-1 천간합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                heavenlyStemControlAnalysis,
                "8-2 천간 충·극 분석이 필요합니다."
        );

        Objects.requireNonNull(
                sixCombinationAnalysis,
                "8-3 지지 육합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                threeCombinationAnalysis,
                "8-4 지지 삼합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                directionalCombinationAnalysis,
                "8-5 지지 방합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                clashAnalysis,
                "8-6 지지 충 분석이 필요합니다."
        );

        Objects.requireNonNull(
                punishmentAnalysis,
                "8-7 지지 형 분석이 필요합니다."
        );

        Objects.requireNonNull(
                breakAnalysis,
                "8-8 지지 파 분석이 필요합니다."
        );

        Objects.requireNonNull(
                harmAnalysis,
                "8-9 지지 해 분석이 필요합니다."
        );

        heavenlyStemPairRelations =
                heavenlyStemPairRelations == null
                        ? List.of()
                        : List.copyOf(
                        heavenlyStemPairRelations
                );

        earthlyBranchPairRelations =
                earthlyBranchPairRelations == null
                        ? List.of()
                        : List.copyOf(
                        earthlyBranchPairRelations
                );

        /*
         * =================================================
         * 기본 동일성 검증
         * =================================================
         */

        if (
                heavenlyStemCombinationAnalysis
                        .dayMaster()
                        != dayMaster

                        || heavenlyStemControlAnalysis
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "천간 관계 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                sixCombinationAnalysis
                        .dayBranch()
                        != dayBranch

                        || threeCombinationAnalysis
                        .dayBranch()
                        != dayBranch

                        || directionalCombinationAnalysis
                        .dayBranch()
                        != dayBranch

                        || clashAnalysis
                        .dayBranch()
                        != dayBranch

                        || punishmentAnalysis
                        .dayBranch()
                        != dayBranch

                        || breakAnalysis
                        .dayBranch()
                        != dayBranch

                        || harmAnalysis
                        .dayBranch()
                        != dayBranch
        ) {

            throw new IllegalArgumentException(
                    "지지 관계 분석의 일지가 서로 일치하지 않습니다."
            );
        }

        if (
                clashAnalysis
                        .monthBranch()
                        != monthBranch

                        || punishmentAnalysis
                        .monthBranch()
                        != monthBranch

                        || breakAnalysis
                        .monthBranch()
                        != monthBranch

                        || harmAnalysis
                        .monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "지지 관계 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * completeChart 검증
         * =================================================
         */

        if (
                heavenlyStemCombinationAnalysis
                        .completeChart()
                        != completeChart

                        || heavenlyStemControlAnalysis
                        .completeChart()
                        != completeChart

                        || sixCombinationAnalysis
                        .completeChart()
                        != completeChart

                        || threeCombinationAnalysis
                        .completeChart()
                        != completeChart

                        || directionalCombinationAnalysis
                        .completeChart()
                        != completeChart

                        || clashAnalysis
                        .completeChart()
                        != completeChart

                        || punishmentAnalysis
                        .completeChart()
                        != completeChart

                        || breakAnalysis
                        .completeChart()
                        != completeChart

                        || harmAnalysis
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "8차 관계 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 알려진 천간·지지 개수 검증
         * =================================================
         */

        int expectedKnownCount =
                completeChart
                        ? 4
                        : 3;

        if (
                heavenlyStemCombinationAnalysis
                        .knownStemCount()
                        != expectedKnownCount

                        || heavenlyStemControlAnalysis
                        .knownStemCount()
                        != expectedKnownCount
        ) {

            throw new IllegalArgumentException(
                    "천간 관계 분석의 알려진 천간 수가 일치하지 않습니다."
            );
        }

        if (
                sixCombinationAnalysis
                        .knownBranchCount()
                        != expectedKnownCount

                        || threeCombinationAnalysis
                        .knownBranchCount()
                        != expectedKnownCount

                        || directionalCombinationAnalysis
                        .knownBranchCount()
                        != expectedKnownCount

                        || clashAnalysis
                        .knownBranchCount()
                        != expectedKnownCount

                        || punishmentAnalysis
                        .knownBranchCount()
                        != expectedKnownCount

                        || breakAnalysis
                        .knownBranchCount()
                        != expectedKnownCount

                        || harmAnalysis
                        .knownBranchCount()
                        != expectedKnownCount
        ) {

            throw new IllegalArgumentException(
                    "지지 관계 분석의 알려진 지지 수가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 천간 Pair 중복 검증
         * =================================================
         */

        Set<String> stemPairKeys =
                new HashSet<>();

        for (
                HeavenlyStemPairRelation relation
                : heavenlyStemPairRelations
        ) {

            String key =
                    relation.firstPosition().name()
                            + ":"
                            + relation.secondPosition().name();

            if (
                    !stemPairKeys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 천간 궁위 PairRelation이 중복되었습니다: "
                                + key
                );
            }

            if (
                    !completeChart
                            && relation.involves(
                            VisibleStemPosition.HOUR
                    )
            ) {

                throw new IllegalArgumentException(
                        "출생시간 미상 원국에 HOUR 천간 관계가 존재할 수 없습니다."
                );
            }
        }

        /*
         * =================================================
         * 지지 Pair 중복 검증
         * =================================================
         */

        Set<String> branchPairKeys =
                new HashSet<>();

        for (
                EarthlyBranchPairRelation relation
                : earthlyBranchPairRelations
        ) {

            String key =
                    relation.firstPosition().name()
                            + ":"
                            + relation.secondPosition().name();

            if (
                    !branchPairKeys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 지지 궁위 PairRelation이 중복되었습니다: "
                                + key
                );
            }

            if (
                    !completeChart
                            && relation.involves(
                            EarthlyBranchPosition.HOUR
                    )
            ) {

                throw new IllegalArgumentException(
                        "출생시간 미상 원국에 HOUR 지지 관계가 존재할 수 없습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 천간 Pair 관계
     * =================================================
     */

    public Optional<HeavenlyStemPairRelation>
    heavenlyStemRelationBetween(

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

        return heavenlyStemPairRelations
                .stream()
                .filter(
                        relation ->
                                relation.involves(
                                        position1
                                )
                                        && relation.involves(
                                        position2
                                )
                )
                .findFirst();
    }

    public List<HeavenlyStemPairRelation>
    heavenlyStemRelationsInvolving(
            VisibleStemPosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return heavenlyStemPairRelations
                .stream()
                .filter(
                        relation ->
                                relation.involves(
                                        position
                                )
                )
                .toList();
    }

    public List<HeavenlyStemPairRelation>
    heavenlyStemRelationsOfKind(
            HeavenlyStemRelationKind relationKind
    ) {

        if (
                relationKind == null
        ) {

            return List.of();
        }

        return heavenlyStemPairRelations
                .stream()
                .filter(
                        relation ->
                                relation.hasRelation(
                                        relationKind
                                )
                )
                .toList();
    }

    public List<HeavenlyStemPairRelation>
    overlappingHeavenlyStemRelations() {

        return heavenlyStemPairRelations
                .stream()
                .filter(
                        HeavenlyStemPairRelation
                                ::overlappingRelations
                )
                .toList();
    }

    public boolean hasOverlappingHeavenlyStemRelation() {

        return !overlappingHeavenlyStemRelations()
                .isEmpty();
    }

    public boolean dayMasterInStemRelation() {

        return !heavenlyStemRelationsInvolving(
                VisibleStemPosition.DAY
        )
                .isEmpty();
    }

    /*
     * =================================================
     * 지지 Pair 관계
     * =================================================
     */

    public Optional<EarthlyBranchPairRelation>
    earthlyBranchRelationBetween(

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

        return earthlyBranchPairRelations
                .stream()
                .filter(
                        relation ->
                                relation.involves(
                                        position1
                                )
                                        && relation.involves(
                                        position2
                                )
                )
                .findFirst();
    }

    public List<EarthlyBranchPairRelation>
    earthlyBranchRelationsInvolving(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return earthlyBranchPairRelations
                .stream()
                .filter(
                        relation ->
                                relation.involves(
                                        position
                                )
                )
                .toList();
    }

    public List<EarthlyBranchPairRelation>
    earthlyBranchRelationsOfKind(
            EarthlyBranchRelationKind relationKind
    ) {

        if (
                relationKind == null
        ) {

            return List.of();
        }

        return earthlyBranchPairRelations
                .stream()
                .filter(
                        relation ->
                                relation.hasRelation(
                                        relationKind
                                )
                )
                .toList();
    }

    /**
     * 둘 이상의 관계가
     * 동일 Pair에 겹치는 경우.
     */
    public List<EarthlyBranchPairRelation>
    overlappingEarthlyBranchRelations() {

        return earthlyBranchPairRelations
                .stream()
                .filter(
                        EarthlyBranchPairRelation
                                ::overlappingRelations
                )
                .toList();
    }

    public boolean hasOverlappingEarthlyBranchRelation() {

        return !overlappingEarthlyBranchRelations()
                .isEmpty();
    }

    /**
     * 동일 Pair에:
     *
     * 합 계열
     * +
     * 충·형·파·해 계열
     *
     * 관계가 동시에 존재하는 경우.
     */
    public List<EarthlyBranchPairRelation>
    mixedEarthlyBranchRelationPairs() {

        return earthlyBranchPairRelations
                .stream()
                .filter(
                        EarthlyBranchPairRelation
                                ::mixedRelationFamilies
                )
                .toList();
    }

    public boolean hasMixedEarthlyBranchRelationPair() {

        return !mixedEarthlyBranchRelationPairs()
                .isEmpty();
    }

    /*
     * =================================================
     * 일지 관계망
     * =================================================
     */

    public List<EarthlyBranchPairRelation>
    dayBranchPairRelations() {

        return earthlyBranchRelationsInvolving(
                EarthlyBranchPosition.DAY
        );
    }

    public boolean dayBranchInRelation() {

        return !dayBranchPairRelations()
                .isEmpty();
    }

    public int dayBranchRelationPairCount() {

        return dayBranchPairRelations()
                .size();
    }

    /*
     * =================================================
     * 월지 관계망
     * =================================================
     */

    public List<EarthlyBranchPairRelation>
    monthBranchPairRelations() {

        return earthlyBranchRelationsInvolving(
                EarthlyBranchPosition.MONTH
        );
    }

    public boolean monthBranchInRelation() {

        return !monthBranchPairRelations()
                .isEmpty();
    }

    public int monthBranchRelationPairCount() {

        return monthBranchPairRelations()
                .size();
    }

    /*
     * =================================================
     * 원국 전체
     * =================================================
     */

    public boolean hasAnyHeavenlyStemRelation() {

        return !heavenlyStemPairRelations
                .isEmpty();
    }

    public boolean hasAnyEarthlyBranchRelation() {

        return !earthlyBranchPairRelations
                .isEmpty();
    }

    public boolean hasAnyRelation() {

        return hasAnyHeavenlyStemRelation()
                || hasAnyEarthlyBranchRelation();
    }

    /**
     * 관계망에 기록된 궁위 Pair 수.
     *
     * 관계 Evidence 개수와는 다르다.
     *
     * 한 Pair에 관계 3개가 있어도
     * PairRelation은 1개다.
     */
    public int heavenlyStemRelationPairCount() {

        return heavenlyStemPairRelations
                .size();
    }

    public int earthlyBranchRelationPairCount() {

        return earthlyBranchPairRelations
                .size();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}