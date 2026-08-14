package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationCompetitionState;
import com.dajeom.type.VisibleStemPosition;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 9차-2.
 *
 * 원국 전체 천간합 후보의
 * 쟁합·다중합·외부 방해 분석.
 *
 * =================================================
 *
 * 하나의 9-1 Candidate마다
 * 하나의 CompetitionEvidence가 존재한다.
 *
 * Candidate를 삭제하지 않는다.
 *
 * =================================================
 *
 * 예:
 *
 * YEAR 己
 * DAY 甲
 * HOUR 己
 *
 * 이라면:
 *
 * YEAR己-DAY甲
 * DAY甲-HOUR己
 *
 * 두 Candidate가 모두 존재하며,
 * 서로 DAY를 공유하는 경쟁 후보로 남는다.
 *
 * =================================================
 *
 * 실제 합화 후보 하나를 최종 채택하거나
 * 탈락시키는 것은 9-3 책임이다.
 */
public record HeavenlyStemCombinationCompetitionAnalysis(

        HeavenlyStem dayMaster,

        HeavenlyStemCombinationCandidateAnalysis
        candidateAnalysis,

        HeavenlyStemControlAnalysis
        controlAnalysis,

        List<HeavenlyStemCombinationCompetitionEvidence>
        evidences,

        boolean completeChart

) {

    public HeavenlyStemCombinationCompetitionAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                candidateAnalysis,
                "9-1 천간합 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                controlAnalysis,
                "8-2 천간 충·극 분석이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                candidateAnalysis.dayMaster()
                        != dayMaster

                        || controlAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "9-1 후보 분석과 8-2 충·극 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                candidateAnalysis.completeChart()
                        != completeChart

                        || controlAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "9-2 입력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        if (
                evidences.size()
                        != candidateAnalysis
                        .candidates()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "9-1 후보 수와 9-2 CompetitionEvidence 수가 일치하지 않습니다."
            );
        }

        for (
                HeavenlyStemCombinationCompetitionEvidence evidence
                : evidences
        ) {

            if (
                    !candidateAnalysis
                            .candidates()
                            .contains(
                                    evidence.candidate()
                            )
            ) {

                throw new IllegalArgumentException(
                        "9-2 Evidence의 후보가 9-1 후보 분석에 존재하지 않습니다."
                );
            }

            for (
                    HeavenlyStemCombinationCandidate competitor
                    : evidence.competingCandidates()
            ) {

                if (
                        !candidateAnalysis
                                .candidates()
                                .contains(
                                        competitor
                                )
                ) {

                    throw new IllegalArgumentException(
                            "9-2 경쟁 후보가 9-1 후보 분석에 존재하지 않습니다."
                    );
                }
            }

            for (
                    HeavenlyStemControlEvidence obstruction
                    : evidence.externalObstructions()
            ) {

                if (
                        !controlAnalysis
                                .evidences()
                                .contains(
                                        obstruction
                                )
                ) {

                    throw new IllegalArgumentException(
                            "9-2 외부 방해 Evidence가 8-2 충·극 분석에 존재하지 않습니다."
                    );
                }
            }
        }
    }

    public boolean hasCandidate() {

        return !evidences
                .isEmpty();
    }

    public int candidateCount() {

        return evidences
                .size();
    }

    public List<HeavenlyStemCombinationCompetitionEvidence>
    uncontestedCandidates() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationCompetitionEvidence
                                ::uncontested
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCompetitionEvidence>
    competingCandidates() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationCompetitionEvidence
                                ::competing
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCompetitionEvidence>
    contestedCandidates() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationCompetitionEvidence
                                ::contested
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCompetitionEvidence>
    obstructedCandidates() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationCompetitionEvidence
                                ::obstructed
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCompetitionEvidence>
    evidencesOfState(
            HeavenlyStemCombinationCompetitionState state
    ) {

        if (
                state == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.state()
                                        == state
                )
                .toList();
    }

    public boolean hasCompetition() {

        return evidences
                .stream()
                .anyMatch(
                        HeavenlyStemCombinationCompetitionEvidence
                                ::hasCompetition
                );
    }

    public boolean hasContestedCandidate() {

        return !contestedCandidates()
                .isEmpty();
    }

    public boolean hasObstructedCandidate() {

        return !obstructedCandidates()
                .isEmpty();
    }

    public boolean hasExternalObstruction() {

        return evidences
                .stream()
                .anyMatch(
                        HeavenlyStemCombinationCompetitionEvidence
                                ::hasExternalObstruction
                );
    }

    public List<HeavenlyStemCombinationCompetitionEvidence>
    dayMasterCandidates() {

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence
                                        .candidate()
                                        .dayMasterInvolved()
                )
                .toList();
    }

    public boolean dayMasterInMultipleCombinationStructure() {

        return dayMasterCandidates()
                .stream()
                .anyMatch(
                        HeavenlyStemCombinationCompetitionEvidence
                                ::dayMasterMultipleCombination
                );
    }

    public List<HeavenlyStemCombinationCompetitionEvidence>
    candidatesInvolving(
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
                                evidence
                                        .candidate()
                                        .involves(
                                                position
                                        )
                )
                .toList();
    }

    public Optional<HeavenlyStemCombinationCompetitionEvidence>
    candidateBetween(

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
                                evidence
                                        .candidate()
                                        .involves(
                                                position1
                                        )
                                        && evidence
                                        .candidate()
                                        .involves(
                                                position2
                                        )
                )
                .findFirst();
    }

    /**
     * 9-3에서 우선적으로 검토해야 할
     * 경합/방해 후보.
     */
    public List<HeavenlyStemCombinationCompetitionEvidence>
    reviewRequiredCandidates() {

        return evidences
                .stream()
                .filter(
                        HeavenlyStemCombinationCompetitionEvidence
                                ::requiresStrongReview
                )
                .toList();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}