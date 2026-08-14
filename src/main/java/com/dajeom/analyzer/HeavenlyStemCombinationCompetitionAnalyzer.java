package com.dajeom.analyzer;

import com.dajeom.model.HeavenlyStemCombinationCandidate;
import com.dajeom.model.HeavenlyStemCombinationCandidateAnalysis;
import com.dajeom.model.HeavenlyStemCombinationCompetitionAnalysis;
import com.dajeom.model.HeavenlyStemCombinationCompetitionEvidence;
import com.dajeom.model.HeavenlyStemControlAnalysis;
import com.dajeom.model.HeavenlyStemControlEvidence;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.rule.HeavenlyStemCombinationCompetitionRule;
import com.dajeom.type.HeavenlyStemCombinationCompetitionReasonType;
import com.dajeom.type.HeavenlyStemCombinationCompetitionState;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-2.
 *
 * 9-1의 모든 천간합 후보를 대상으로:
 *
 * - 동일 천간 중복 참여
 * - 쟁합
 * - 다중합
 * - 인접 경쟁
 * - 적용도 경쟁
 * - 외부 천간충
 * - 외부 직접 피극
 *
 * 을 분석한다.
 *
 * =================================================
 *
 * Candidate를 제거하지 않는다.
 *
 * 모든 9-1 Candidate는
 * 정확히 하나의 9-2 Evidence를 가진다.
 *
 * =================================================
 *
 * 대표 예:
 *
 * YEAR 己
 * DAY 甲
 * HOUR 己
 *
 * ->
 *
 * YEAR己-DAY甲
 * DAY甲-HOUR己
 *
 * 두 Candidate가 DAY 甲을 공유.
 *
 * 어느 쪽이 실제 합화하는지는
 * 9-3에서 결정한다.
 */
@Component
public class HeavenlyStemCombinationCompetitionAnalyzer {

    private final HeavenlyStemCombinationCompetitionRule
            heavenlyStemCombinationCompetitionRule;

    public HeavenlyStemCombinationCompetitionAnalyzer(
            HeavenlyStemCombinationCompetitionRule
                    heavenlyStemCombinationCompetitionRule
    ) {

        this.heavenlyStemCombinationCompetitionRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationCompetitionRule,
                        "HeavenlyStemCombinationCompetitionRule이 필요합니다."
                );
    }

    /**
     * RelationAnalysis를 함께 사용하는 정식 경로.
     */
    public HeavenlyStemCombinationCompetitionAnalysis analyze(

            HeavenlyStemCombinationCandidateAnalysis candidateAnalysis,

            RelationAnalysis relationAnalysis

    ) {

        Objects.requireNonNull(
                candidateAnalysis,
                "9-1 천간합 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                relationAnalysis,
                "8차 관계 분석이 필요합니다."
        );

        if (
                candidateAnalysis.dayMaster()
                        != relationAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "9-1 후보 분석과 8차 관계 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                candidateAnalysis.completeChart()
                        != relationAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "9-1 후보 분석과 8차 관계 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        return analyze(
                candidateAnalysis,
                relationAnalysis
                        .heavenlyStemControlAnalysis()
        );
    }

    /**
     * 8-2 천간 충·극 분석을 직접 전달하는 경로.
     */
    public HeavenlyStemCombinationCompetitionAnalysis analyze(

            HeavenlyStemCombinationCandidateAnalysis candidateAnalysis,

            HeavenlyStemControlAnalysis controlAnalysis

    ) {

        Objects.requireNonNull(
                candidateAnalysis,
                "9-1 천간합 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                controlAnalysis,
                "8-2 천간 충·극 분석이 필요합니다."
        );

        if (
                candidateAnalysis.dayMaster()
                        != controlAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "9-1 후보 분석과 8-2 충·극 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                candidateAnalysis.completeChart()
                        != controlAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "9-1 후보 분석과 8-2 충·극 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        List<HeavenlyStemCombinationCompetitionEvidence> evidences =
                new ArrayList<>();

        for (
                HeavenlyStemCombinationCandidate candidate
                : candidateAnalysis.candidates()
        ) {

            /*
             * =================================================
             * 1. 동일 천간을 공유하는 경쟁 후보
             * =================================================
             */
            List<HeavenlyStemCombinationCandidate> competitors =
                    candidateAnalysis
                            .candidates()
                            .stream()
                            .filter(
                                    other ->
                                            other != candidate
                            )
                            .filter(
                                    other ->
                                            heavenlyStemCombinationCompetitionRule
                                                    .sharesMember(
                                                            candidate,
                                                            other
                                                    )
                            )
                            .toList();

            /*
             * =================================================
             * 2. 동급 또는 상위 경쟁 후보
             * =================================================
             */
            List<HeavenlyStemCombinationCandidate>
                    equalOrStrongerCompetitors =
                    competitors
                            .stream()
                            .filter(
                                    competitor ->
                                            heavenlyStemCombinationCompetitionRule
                                                    .equalOrStronger(
                                                            candidate,
                                                            competitor
                                                    )
                            )
                            .toList();

            /*
             * =================================================
             * 3. 외부 천간충·극 방해
             * =================================================
             *
             * Candidate 내부 Pair 자체의
             * CONTROL Evidence는 제외한다.
             */
            List<HeavenlyStemControlEvidence>
                    externalObstructions =
                    controlAnalysis
                            .evidences()
                            .stream()
                            .filter(
                                    controlEvidence ->
                                            heavenlyStemCombinationCompetitionRule
                                                    .externalObstruction(
                                                            candidate,
                                                            controlEvidence
                                                    )
                            )
                            .toList();

            boolean adjacentCompetitorExists =
                    competitors
                            .stream()
                            .anyMatch(
                                    HeavenlyStemCombinationCandidate
                                            ::adjacent
                            );

            boolean adjacentExternalObstructionExists =
                    externalObstructions
                            .stream()
                            .anyMatch(
                                    HeavenlyStemControlEvidence
                                            ::adjacent
                            );

            /*
             * =================================================
             * 일간 다중합
             * =================================================
             *
             * 현재 후보가 일간에 참여하고,
             *
             * 일간 위치를 공유하는 다른 Candidate가
             * 하나 이상 존재하면 true.
             */
            boolean dayMasterMultipleCombination =
                    candidate.dayMasterInvolved()

                            && competitors
                            .stream()
                            .anyMatch(
                                    competitor ->
                                            competitor.involves(
                                                    VisibleStemPosition.DAY
                                            )
                            );

            /*
             * =================================================
             * 대표 상태
             * =================================================
             */
            HeavenlyStemCombinationCompetitionState state =
                    heavenlyStemCombinationCompetitionRule
                            .determineState(
                                    competitors,
                                    equalOrStrongerCompetitors,
                                    externalObstructions
                            );

            List<HeavenlyStemCombinationCompetitionReasonType> reasons =
                    heavenlyStemCombinationCompetitionRule
                            .buildReasons(
                                    candidate,
                                    state,
                                    competitors,
                                    equalOrStrongerCompetitors,
                                    externalObstructions,
                                    dayMasterMultipleCombination
                            );

            evidences.add(
                    new HeavenlyStemCombinationCompetitionEvidence(
                            candidate,
                            state,
                            competitors,
                            equalOrStrongerCompetitors,
                            externalObstructions,
                            adjacentCompetitorExists,
                            adjacentExternalObstructionExists,
                            dayMasterMultipleCombination,
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 대표 경쟁 상태
         * ↓
         * 9-1 기본 적용도
         * ↓
         * 원국 위치
         *
         * 순으로 정렬한다.
         *
         * 이 순서는 실제 합화 성공 순위가 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        HeavenlyStemCombinationCompetitionEvidence evidence
                                ) ->
                                        evidence
                                                .state()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator.comparingInt(
                                                (
                                                        HeavenlyStemCombinationCompetitionEvidence evidence
                                                ) ->
                                                        evidence
                                                                .candidate()
                                                                .applicability()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .candidate()
                                                .firstPosition()
                                                .ordinal()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .candidate()
                                                .secondPosition()
                                                .ordinal()
                        )
        );

        return new HeavenlyStemCombinationCompetitionAnalysis(
                candidateAnalysis.dayMaster(),
                candidateAnalysis,
                controlAnalysis,
                evidences,
                candidateAnalysis.completeChart()
        );
    }
}