package com.dajeom.model;

import com.dajeom.type.EarthlyBranchCombinationCandidateKind;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 9차-7.
 *
 * 지지 결합 후보 전체의
 * 충·형·파·해 방해 분석.
 *
 * =================================================
 *
 * 입력:
 *
 * 9-4 六合 합화 분석
 * 9-5 三合 국 분석
 * 9-6 方合 국 분석
 * 8-10 RelationAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * 각 후보마다:
 *
 * NONE
 * MINOR
 * MEANINGFUL
 * SEVERE
 *
 * 방해 수준을 하나씩 부여한다.
 *
 * =================================================
 *
 * 후보는 삭제하지 않는다.
 *
 * 9-8에서:
 *
 * 기본 성립 상태
 * +
 * 9-7 방해 수준
 * +
 * 다른 결합 후보와의 경쟁
 *
 * 을 종합한다.
 */
public record EarthlyBranchCombinationObstructionAnalysis(

        HeavenlyStem dayMaster,

        RelationAnalysis relationAnalysis,

        EarthlyBranchSixCombinationTransformationAnalysis
        sixCombinationAnalysis,

        EarthlyBranchThreeCombinationFormationAnalysis
        threeCombinationAnalysis,

        EarthlyBranchDirectionalCombinationFormationAnalysis
        directionalCombinationAnalysis,

        List<EarthlyBranchCombinationObstructionEvidence>
        evidences,

        boolean completeChart

) {

    public EarthlyBranchCombinationObstructionAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

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

        /*
         * =================================================
         * Compact constructor 입력 정규화
         * =================================================
         */

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        /*
         * =================================================
         * 입력 분석 일간 정합성
         * =================================================
         */

        if (
                relationAnalysis.dayMaster()
                        != dayMaster

                        || sixCombinationAnalysis.dayMaster()
                        != dayMaster

                        || threeCombinationAnalysis.dayMaster()
                        != dayMaster

                        || directionalCombinationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "9-7 입력 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 완전성 정합성
         * =================================================
         */

        if (
                relationAnalysis.completeChart()
                        != completeChart

                        || sixCombinationAnalysis.completeChart()
                        != completeChart

                        || threeCombinationAnalysis.completeChart()
                        != completeChart

                        || directionalCombinationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "9-7 입력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 후보 수 정합성
         * =================================================
         */

        int expectedEvidenceCount =
                sixCombinationAnalysis
                        .evidences()
                        .size()

                        + threeCombinationAnalysis
                        .evidences()
                        .size()

                        + directionalCombinationAnalysis
                        .evidences()
                        .size();

        if (
                evidences.size()
                        != expectedEvidenceCount
        ) {

            throw new IllegalArgumentException(
                    "9-4/9-5/9-6 후보 수와 9-7 방해 Evidence 수가 일치하지 않습니다."
            );
        }

        Set<String> uniqueKeys =
                new HashSet<>();

        for (
                EarthlyBranchCombinationObstructionEvidence evidence
                : evidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "9-7 결합 방해 Evidence에는 null을 넣을 수 없습니다."
            );

            /*
             * =================================================
             * 중요:
             * compact constructor 안에서는
             * instance validateSource(evidence)를 호출하지 않는다.
             *
             * record field인:
             *
             * this.sixCombinationAnalysis
             * this.threeCombinationAnalysis
             * this.directionalCombinationAnalysis
             *
             * 는 아직 자동 대입 전이다.
             *
             * constructor parameter를 명시적으로 넘기는
             * static validation을 사용한다.
             * =================================================
             */

            validateSource(
                    evidence,
                    sixCombinationAnalysis,
                    threeCombinationAnalysis,
                    directionalCombinationAnalysis
            );

            String key =
                    evidence.candidateKind().name()
                            + ":"
                            + evidence.candidatePositions()
                            + ":"
                            + evidence.targetElement().name();

            if (
                    !uniqueKeys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 9-7 결합 방해 Evidence가 중복되었습니다: "
                                + key
                );
            }
        }
    }

    /*
     * =================================================
     * Source Identity 검증
     * =================================================
     *
     * static helper로 둔다.
     *
     * record compact constructor에서도
     * 아직 초기화되지 않은 this.xxx를 참조하지 않고
     * constructor parameter 객체 자체를 검증한다.
     */

    private static void validateSource(

            EarthlyBranchCombinationObstructionEvidence evidence,

            EarthlyBranchSixCombinationTransformationAnalysis
                    sixCombinationAnalysis,

            EarthlyBranchThreeCombinationFormationAnalysis
                    threeCombinationAnalysis,

            EarthlyBranchDirectionalCombinationFormationAnalysis
                    directionalCombinationAnalysis

    ) {

        Objects.requireNonNull(
                evidence,
                "9-7 결합 방해 Evidence가 필요합니다."
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

        if (
                evidence.candidateKind()
                        .sixCombination()

                        && !sixCombinationAnalysis
                        .evidences()
                        .contains(
                                evidence
                                        .sixCombinationEvidence()
                        )
        ) {

            throw new IllegalArgumentException(
                    "9-7 육합 원본 후보가 9-4 분석에 존재하지 않습니다."
            );
        }

        if (
                evidence.candidateKind()
                        .threeCombination()

                        && !threeCombinationAnalysis
                        .evidences()
                        .contains(
                                evidence
                                        .threeCombinationEvidence()
                        )
        ) {

            throw new IllegalArgumentException(
                    "9-7 삼합 원본 후보가 9-5 분석에 존재하지 않습니다."
            );
        }

        if (
                evidence.candidateKind()
                        .directionalCombination()

                        && !directionalCombinationAnalysis
                        .evidences()
                        .contains(
                                evidence
                                        .directionalCombinationEvidence()
                        )
        ) {

            throw new IllegalArgumentException(
                    "9-7 방합 원본 후보가 9-6 분석에 존재하지 않습니다."
            );
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

    /*
     * =================================================
     * 수준별
     * =================================================
     */

    public List<EarthlyBranchCombinationObstructionEvidence>
    evidencesOfLevel(
            EarthlyBranchCombinationObstructionLevel level
    ) {

        if (
                level == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.level()
                                        == level
                )
                .toList();
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    unobstructedCandidates() {

        return evidencesOfLevel(
                EarthlyBranchCombinationObstructionLevel.NONE
        );
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    minorObstructionCandidates() {

        return evidencesOfLevel(
                EarthlyBranchCombinationObstructionLevel.MINOR
        );
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    meaningfulObstructionCandidates() {

        return evidencesOfLevel(
                EarthlyBranchCombinationObstructionLevel.MEANINGFUL
        );
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    severeObstructionCandidates() {

        return evidencesOfLevel(
                EarthlyBranchCombinationObstructionLevel.SEVERE
        );
    }

    public boolean hasObstruction() {

        return evidences
                .stream()
                .anyMatch(
                        EarthlyBranchCombinationObstructionEvidence
                                ::hasObstruction
                );
    }

    public boolean hasMeaningfulOrSevereObstruction() {

        return evidences
                .stream()
                .anyMatch(
                        EarthlyBranchCombinationObstructionEvidence
                                ::requiresAdjustment
                );
    }

    public boolean hasSevereObstruction() {

        return !severeObstructionCandidates()
                .isEmpty();
    }

    /*
     * =================================================
     * 후보 종류
     * =================================================
     */

    public List<EarthlyBranchCombinationObstructionEvidence>
    candidatesOfKind(
            EarthlyBranchCombinationCandidateKind kind
    ) {

        if (
                kind == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.candidateKind()
                                        == kind
                )
                .toList();
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    sixCombinationCandidates() {

        return candidatesOfKind(
                EarthlyBranchCombinationCandidateKind
                        .SIX_COMBINATION
        );
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    threeCombinationCandidates() {

        return candidatesOfKind(
                EarthlyBranchCombinationCandidateKind
                        .THREE_COMBINATION
        );
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    directionalCombinationCandidates() {

        return candidatesOfKind(
                EarthlyBranchCombinationCandidateKind
                        .DIRECTIONAL_COMBINATION
        );
    }

    /*
     * =================================================
     * 방해 종류
     * =================================================
     */

    public List<EarthlyBranchCombinationObstructionEvidence>
    candidatesWithClash() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchCombinationObstructionEvidence
                                ::hasClash
                )
                .toList();
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    candidatesWithPunishment() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchCombinationObstructionEvidence
                                ::hasPunishment
                )
                .toList();
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    candidatesWithBreak() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchCombinationObstructionEvidence
                                ::hasBreak
                )
                .toList();
    }

    public List<EarthlyBranchCombinationObstructionEvidence>
    candidatesWithHarm() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchCombinationObstructionEvidence
                                ::hasHarm
                )
                .toList();
    }

    /*
     * =================================================
     * 왕지
     * =================================================
     */

    public List<EarthlyBranchCombinationObstructionEvidence>
    cardinalAffectedCandidates() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchCombinationObstructionEvidence
                                ::cardinalBranchAffected
                )
                .toList();
    }

    /*
     * =================================================
     * 위치
     * =================================================
     */

    public List<EarthlyBranchCombinationObstructionEvidence>
    candidatesInvolving(
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

    /*
     * =================================================
     * 목표 오행
     * =================================================
     */

    public List<EarthlyBranchCombinationObstructionEvidence>
    candidatesTargeting(
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
                                evidence.targetElement()
                                        == element
                )
                .toList();
    }

    /*
     * =================================================
     * 조정 필요 후보
     * =================================================
     */

    public List<EarthlyBranchCombinationObstructionEvidence>
    adjustmentRequiredCandidates() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchCombinationObstructionEvidence
                                ::requiresAdjustment
                )
                .toList();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}