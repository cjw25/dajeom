package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchCombinationObstructionAnalysis;
import com.dajeom.model.EarthlyBranchCombinationObstructionEvidence;
import com.dajeom.model.EarthlyBranchCombinationObstructionRelation;
import com.dajeom.model.EarthlyBranchDirectionalCombinationFormationAnalysis;
import com.dajeom.model.EarthlyBranchDirectionalCombinationFormationEvidence;
import com.dajeom.model.EarthlyBranchPairRelation;
import com.dajeom.model.EarthlyBranchSixCombinationTransformationAnalysis;
import com.dajeom.model.EarthlyBranchSixCombinationTransformationEvidence;
import com.dajeom.model.EarthlyBranchThreeCombinationFormationAnalysis;
import com.dajeom.model.EarthlyBranchThreeCombinationFormationEvidence;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.rule.EarthlyBranchCombinationObstructionRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchCombinationCandidateKind;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import com.dajeom.type.EarthlyBranchCombinationObstructionReasonType;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchRelationKind;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 9차-7.
 *
 * 9-4 / 9-5 / 9-6의 모든 지지 결합 후보에
 * 8-10 궁위 Pair 관계망을 연결하여
 * 충·형·파·해 방해 구조를 분석한다.
 *
 * =================================================
 *
 * 핵심 설계:
 *
 * 8-6 ClashAnalysis
 * 8-7 PunishmentAnalysis
 * 8-8 BreakAnalysis
 * 8-9 HarmAnalysis
 *
 * 을 각각 다시 탐색하지 않는다.
 *
 * 이미 8-10 RelationAnalysis에서:
 *
 * EarthlyBranchPairRelation
 *
 * 으로 통합했기 때문에
 * 해당 관계망을 9-7의 공식 입력으로 사용한다.
 *
 * =================================================
 *
 * 이 방식의 장점:
 *
 * - 같은 Pair의 복수 관계 중첩을 그대로 사용
 * - 六合+刑+破 같은 구조를 한 번에 확인
 * - 충/형/파/해 Analyzer를 재실행하지 않음
 * - 9차가 8차 세부 구현에 과도하게 결합되지 않음
 *
 * =================================================
 *
 * 9-7에서는 후보를 삭제하지 않는다.
 *
 * 최종 결합 상태 조정은 9-8 책임이다.
 */
@Component
public class EarthlyBranchCombinationObstructionAnalyzer {

    private final EarthlyBranchCombinationObstructionRule
            earthlyBranchCombinationObstructionRule;

    public EarthlyBranchCombinationObstructionAnalyzer(
            EarthlyBranchCombinationObstructionRule
                    earthlyBranchCombinationObstructionRule
    ) {

        this.earthlyBranchCombinationObstructionRule =
                Objects.requireNonNull(
                        earthlyBranchCombinationObstructionRule,
                        "EarthlyBranchCombinationObstructionRule이 필요합니다."
                );
    }

    public EarthlyBranchCombinationObstructionAnalysis analyze(

            RelationAnalysis relationAnalysis,

            EarthlyBranchSixCombinationTransformationAnalysis
                    sixCombinationAnalysis,

            EarthlyBranchThreeCombinationFormationAnalysis
                    threeCombinationAnalysis,

            EarthlyBranchDirectionalCombinationFormationAnalysis
                    directionalCombinationAnalysis

    ) {

        Objects.requireNonNull(
                relationAnalysis,
                "8-10 관계 분석이 필요합니다."
        );

        Objects.requireNonNull(
                sixCombinationAnalysis,
                "9-4 육합 합화 분석이 필요합니다."
        );

        Objects.requireNonNull(
                threeCombinationAnalysis,
                "9-5 삼합 국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                directionalCombinationAnalysis,
                "9-6 방합 국 분석이 필요합니다."
        );

        validateIdentity(
                relationAnalysis,
                sixCombinationAnalysis,
                threeCombinationAnalysis,
                directionalCombinationAnalysis
        );

        List<EarthlyBranchCombinationObstructionEvidence>
                results =
                new ArrayList<>();

        /*
         * =================================================
         * 9-4 六合
         * =================================================
         */
        for (
                EarthlyBranchSixCombinationTransformationEvidence evidence
                : sixCombinationAnalysis.evidences()
        ) {

            List<EarthlyBranchPosition> candidatePositions =
                    List.of(
                            evidence.firstPosition(),
                            evidence.secondPosition()
                    );

            List<EarthlyBranchCombinationObstructionRelation>
                    obstructionRelations =
                    findObstructionRelations(
                            relationAnalysis,
                            candidatePositions,
                            null
                    );

            EarthlyBranchCombinationObstructionLevel level =
                    earthlyBranchCombinationObstructionRule
                            .determineLevel(
                                    obstructionRelations
                            );

            List<EarthlyBranchCombinationObstructionReasonType>
                    reasons =
                    earthlyBranchCombinationObstructionRule
                            .buildReasons(
                                    level,
                                    obstructionRelations
                            );

            results.add(
                    new EarthlyBranchCombinationObstructionEvidence(
                            EarthlyBranchCombinationCandidateKind
                                    .SIX_COMBINATION,
                            evidence,
                            null,
                            null,
                            level,
                            obstructionRelations,
                            affectedMemberCount(
                                    obstructionRelations
                            ),
                            false,
                            hasInternalObstruction(
                                    obstructionRelations
                            ),
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 9-5 三合
         * =================================================
         */
        for (
                EarthlyBranchThreeCombinationFormationEvidence evidence
                : threeCombinationAnalysis.evidences()
        ) {

            EarthlyBranchPosition cardinalPosition =
                    findThreeCombinationCardinalPosition(
                            evidence
                    );

            List<EarthlyBranchCombinationObstructionRelation>
                    obstructionRelations =
                    findObstructionRelations(
                            relationAnalysis,
                            evidence.positions(),
                            cardinalPosition
                    );

            EarthlyBranchCombinationObstructionLevel level =
                    earthlyBranchCombinationObstructionRule
                            .determineLevel(
                                    obstructionRelations
                            );

            List<EarthlyBranchCombinationObstructionReasonType>
                    reasons =
                    earthlyBranchCombinationObstructionRule
                            .buildReasons(
                                    level,
                                    obstructionRelations
                            );

            results.add(
                    new EarthlyBranchCombinationObstructionEvidence(
                            EarthlyBranchCombinationCandidateKind
                                    .THREE_COMBINATION,
                            null,
                            evidence,
                            null,
                            level,
                            obstructionRelations,
                            affectedMemberCount(
                                    obstructionRelations
                            ),
                            cardinalPosition != null
                                    && isPositionAffected(
                                    obstructionRelations,
                                    cardinalPosition
                            ),
                            hasInternalObstruction(
                                    obstructionRelations
                            ),
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 9-6 方合
         * =================================================
         */
        for (
                EarthlyBranchDirectionalCombinationFormationEvidence evidence
                : directionalCombinationAnalysis.evidences()
        ) {

            EarthlyBranchPosition cardinalPosition =
                    findDirectionalCombinationCardinalPosition(
                            evidence
                    );

            List<EarthlyBranchCombinationObstructionRelation>
                    obstructionRelations =
                    findObstructionRelations(
                            relationAnalysis,
                            evidence.positions(),
                            cardinalPosition
                    );

            EarthlyBranchCombinationObstructionLevel level =
                    earthlyBranchCombinationObstructionRule
                            .determineLevel(
                                    obstructionRelations
                            );

            List<EarthlyBranchCombinationObstructionReasonType>
                    reasons =
                    earthlyBranchCombinationObstructionRule
                            .buildReasons(
                                    level,
                                    obstructionRelations
                            );

            results.add(
                    new EarthlyBranchCombinationObstructionEvidence(
                            EarthlyBranchCombinationCandidateKind
                                    .DIRECTIONAL_COMBINATION,
                            null,
                            null,
                            evidence,
                            level,
                            obstructionRelations,
                            affectedMemberCount(
                                    obstructionRelations
                            ),
                            cardinalPosition != null
                                    && isPositionAffected(
                                    obstructionRelations,
                                    cardinalPosition
                            ),
                            hasInternalObstruction(
                                    obstructionRelations
                            ),
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 1. 방해 수준
         * 2. 기존 9-4/5/6 상태
         * 3. 후보 종류
         * 4. 첫 궁위
         *
         * 길흉 순위가 아니다.
         */
        results.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchCombinationObstructionEvidence evidence
                                ) ->
                                        evidence
                                                .level()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator.comparingInt(
                                                (
                                                        EarthlyBranchCombinationObstructionEvidence evidence
                                                ) ->
                                                        evidence
                                                                .baseStateRank()
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .candidateKind()
                                                .ordinal()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .candidatePositions()
                                                .get(0)
                                                .ordinal()
                        )
        );

        return new EarthlyBranchCombinationObstructionAnalysis(
                relationAnalysis.dayMaster(),
                relationAnalysis,
                sixCombinationAnalysis,
                threeCombinationAnalysis,
                directionalCombinationAnalysis,
                results,
                relationAnalysis.completeChart()
        );
    }

    /*
     * =================================================
     * 입력 동일성
     * =================================================
     */

    private void validateIdentity(

            RelationAnalysis relationAnalysis,

            EarthlyBranchSixCombinationTransformationAnalysis
                    sixCombinationAnalysis,

            EarthlyBranchThreeCombinationFormationAnalysis
                    threeCombinationAnalysis,

            EarthlyBranchDirectionalCombinationFormationAnalysis
                    directionalCombinationAnalysis

    ) {

        if (
                relationAnalysis.dayMaster()
                        != sixCombinationAnalysis.dayMaster()

                        || relationAnalysis.dayMaster()
                        != threeCombinationAnalysis.dayMaster()

                        || relationAnalysis.dayMaster()
                        != directionalCombinationAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "8-10과 9-4/9-5/9-6 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                relationAnalysis.monthBranch()
                        != sixCombinationAnalysis.monthBranch()

                        || relationAnalysis.monthBranch()
                        != threeCombinationAnalysis.monthBranch()

                        || relationAnalysis.monthBranch()
                        != directionalCombinationAnalysis.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "8-10과 9-4/9-5/9-6 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                relationAnalysis.dayBranch()
                        != sixCombinationAnalysis.dayBranch()

                        || relationAnalysis.dayBranch()
                        != threeCombinationAnalysis.dayBranch()

                        || relationAnalysis.dayBranch()
                        != directionalCombinationAnalysis.dayBranch()
        ) {

            throw new IllegalArgumentException(
                    "8-10과 9-4/9-5/9-6 분석의 일지가 서로 일치하지 않습니다."
            );
        }

        if (
                relationAnalysis.completeChart()
                        != sixCombinationAnalysis.completeChart()

                        || relationAnalysis.completeChart()
                        != threeCombinationAnalysis.completeChart()

                        || relationAnalysis.completeChart()
                        != directionalCombinationAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "8-10과 9-4/9-5/9-6 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 후보에 걸리는 방해 관계 탐색
     * =================================================
     */

    private List<EarthlyBranchCombinationObstructionRelation>
    findObstructionRelations(

            RelationAnalysis relationAnalysis,

            List<EarthlyBranchPosition> candidatePositions,

            EarthlyBranchPosition cardinalPosition

    ) {

        List<EarthlyBranchCombinationObstructionRelation> results =
                new ArrayList<>();

        for (
                EarthlyBranchPairRelation pairRelation
                : relationAnalysis.earthlyBranchPairRelations()
        ) {

            Set<EarthlyBranchRelationKind> obstructionKinds =
                    extractObstructionKinds(
                            pairRelation
                    );

            if (
                    obstructionKinds.isEmpty()
            ) {

                continue;
            }

            List<EarthlyBranchPosition> affectedCandidatePositions =
                    candidatePositions
                            .stream()
                            .filter(
                                    pairRelation::involves
                            )
                            .distinct()
                            .toList();

            /*
             * 현재 결합 후보 구성원을
             * 하나도 건드리지 않는 Pair는 제외.
             */
            if (
                    affectedCandidatePositions
                            .isEmpty()
            ) {

                continue;
            }

            boolean internalToCandidate =
                    candidatePositions.contains(
                            pairRelation.firstPosition()
                    )
                            && candidatePositions.contains(
                            pairRelation.secondPosition()
                    );

            boolean cardinalBranchAffected =
                    cardinalPosition != null
                            && pairRelation.involves(
                            cardinalPosition
                    );

            results.add(
                    new EarthlyBranchCombinationObstructionRelation(
                            pairRelation,
                            obstructionKinds,
                            affectedCandidatePositions,
                            internalToCandidate,
                            cardinalBranchAffected
                    )
            );
        }

        results.sort(
                Comparator
                        .comparing(
                                EarthlyBranchCombinationObstructionRelation
                                        ::internalToCandidate
                        )
                        .reversed()

                        .thenComparing(
                                EarthlyBranchCombinationObstructionRelation
                                        ::hasClash,
                                Comparator.reverseOrder()
                        )

                        .thenComparingInt(
                                relation ->
                                        relation
                                                .firstPosition()
                                                .ordinal()
                        )

                        .thenComparingInt(
                                relation ->
                                        relation
                                                .secondPosition()
                                                .ordinal()
                        )
        );

        return List.copyOf(
                results
        );
    }

    private Set<EarthlyBranchRelationKind> extractObstructionKinds(
            EarthlyBranchPairRelation pairRelation
    ) {

        EnumSet<EarthlyBranchRelationKind> kinds =
                EnumSet.noneOf(
                        EarthlyBranchRelationKind.class
                );

        if (
                pairRelation.hasClash()
        ) {

            kinds.add(
                    EarthlyBranchRelationKind.CLASH
            );
        }

        if (
                pairRelation.hasPunishment()
        ) {

            kinds.add(
                    EarthlyBranchRelationKind.PUNISHMENT
            );
        }

        if (
                pairRelation.hasBreak()
        ) {

            kinds.add(
                    EarthlyBranchRelationKind.BREAK
            );
        }

        if (
                pairRelation.hasHarm()
        ) {

            kinds.add(
                    EarthlyBranchRelationKind.HARM
            );
        }

        return Set.copyOf(
                kinds
        );
    }

    /*
     * =================================================
     * 삼합 왕지 위치
     * =================================================
     */

    private EarthlyBranchPosition
    findThreeCombinationCardinalPosition(
            EarthlyBranchThreeCombinationFormationEvidence evidence
    ) {

        if (
                !evidence.cardinalBranchIncluded()
        ) {

            return null;
        }

        EarthlyBranch cardinalBranch =
                evidence
                        .combinationType()
                        .cardinalBranch();

        List<EarthlyBranchPosition> positions =
                evidence.positions();

        List<EarthlyBranch> branches =
                evidence.branches();

        for (
                int index = 0;
                index < branches.size();
                index++
        ) {

            if (
                    branches.get(
                            index
                    )
                            == cardinalBranch
            ) {

                return positions.get(
                        index
                );
            }
        }

        throw new IllegalStateException(
                "삼합 Evidence가 왕지를 포함한다고 표시되었지만 실제 왕지 위치를 찾을 수 없습니다."
        );
    }

    /*
     * =================================================
     * 방합 왕지 위치
     * =================================================
     */

    private EarthlyBranchPosition
    findDirectionalCombinationCardinalPosition(
            EarthlyBranchDirectionalCombinationFormationEvidence evidence
    ) {

        if (
                !evidence.cardinalBranchIncluded()
        ) {

            return null;
        }

        EarthlyBranch cardinalBranch =
                evidence
                        .combinationType()
                        .cardinalBranch();

        List<EarthlyBranchPosition> positions =
                evidence.positions();

        List<EarthlyBranch> branches =
                evidence.branches();

        for (
                int index = 0;
                index < branches.size();
                index++
        ) {

            if (
                    branches.get(
                            index
                    )
                            == cardinalBranch
            ) {

                return positions.get(
                        index
                );
            }
        }

        throw new IllegalStateException(
                "방합 Evidence가 왕지를 포함한다고 표시되었지만 실제 왕지 위치를 찾을 수 없습니다."
        );
    }

    /*
     * =================================================
     * 공통 집계
     * =================================================
     */

    private int affectedMemberCount(
            List<EarthlyBranchCombinationObstructionRelation> relations
    ) {

        return relations
                .stream()
                .flatMap(
                        relation ->
                                relation
                                        .affectedCandidatePositions()
                                        .stream()
                )
                .distinct()
                .toList()
                .size();
    }

    private boolean hasInternalObstruction(
            List<EarthlyBranchCombinationObstructionRelation> relations
    ) {

        return relations
                .stream()
                .anyMatch(
                        EarthlyBranchCombinationObstructionRelation
                                ::internalToCandidate
                );
    }

    private boolean isPositionAffected(

            List<EarthlyBranchCombinationObstructionRelation> relations,

            EarthlyBranchPosition position

    ) {

        return relations
                .stream()
                .anyMatch(
                        relation ->
                                relation
                                        .affectedCandidatePositions()
                                        .contains(
                                                position
                                        )
                );
    }
}