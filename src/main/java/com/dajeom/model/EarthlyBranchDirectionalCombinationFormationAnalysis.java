package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchDirectionalCombinationFormationState;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 9차-6.
 *
 * 원국 전체 방합·부분방합의
 * 방해·경쟁 검토 전 方局 성립 분석.
 *
 * =================================================
 *
 * 평가 대상:
 *
 * directionalCombinationAnalysis.independentRelations()
 *
 * =================================================
 *
 * 완전 方合 내부에 포함된 Pair Evidence는
 * 8-5에서는 보존하지만
 * 9-6에서는 별도 국 성립 후보로
 * 중복 평가하지 않는다.
 */
public record EarthlyBranchDirectionalCombinationFormationAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        EarthlyBranch dayBranch,

        EarthlyBranchDirectionalCombinationAnalysis
        directionalCombinationAnalysis,

        ElementStrengthAnalysis
        elementStrengthAnalysis,

        List<EarthlyBranchDirectionalCombinationFormationEvidence>
        evidences,

        boolean completeChart

) {

    public EarthlyBranchDirectionalCombinationFormationAnalysis {

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
                directionalCombinationAnalysis,
                "8-5 방합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 오행 세력 분석이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                directionalCombinationAnalysis
                        .dayBranch()
                        != dayBranch
        ) {

            throw new IllegalArgumentException(
                    "8-5 방합 분석의 일지가 9-6 일지와 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "3차 오행 세력 분석의 일간이 9-6 일간과 일치하지 않습니다."
            );
        }

        if (
                directionalCombinationAnalysis
                        .completeChart()
                        != completeChart

                        || elementStrengthAnalysis
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "9-6 입력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        List<EarthlyBranchDirectionalCombinationEvidence>
                independentRelations =
                directionalCombinationAnalysis
                        .independentRelations();

        if (
                evidences.size()
                        != independentRelations.size()
        ) {

            throw new IllegalArgumentException(
                    "8-5 독립 방합 관계 수와 9-6 국 성립 Evidence 수가 일치하지 않습니다."
            );
        }

        Set<String> uniqueKeys =
                new HashSet<>();

        for (
                EarthlyBranchDirectionalCombinationFormationEvidence evidence
                : evidences
        ) {

            if (
                    !independentRelations.contains(
                            evidence.sourceEvidence()
                    )
            ) {

                throw new IllegalArgumentException(
                        "9-6 Evidence의 원본 방합 관계가 8-5 독립 관계에 존재하지 않습니다."
                );
            }

            /*
             * MONTH 위치가 Evidence에 포함된다면
             * 실제 월지와 일치해야 한다.
             */
            if (
                    evidence.involves(
                            EarthlyBranchPosition.MONTH
                    )
                            && !branchMatchesAtPosition(
                            evidence.sourceEvidence(),
                            EarthlyBranchPosition.MONTH,
                            monthBranch
                    )
            ) {

                throw new IllegalArgumentException(
                        "9-6 방합 Evidence의 MONTH 지지가 실제 월지와 일치하지 않습니다."
                );
            }

            String key =
                    evidence.combinationType().name()
                            + ":"
                            + evidence.form().name()
                            + ":"
                            + evidence.positions();

            if (
                    !uniqueKeys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 9-6 방합 국 성립 Evidence가 중복되었습니다: "
                                + key
                );
            }
        }
    }

    private static boolean branchMatchesAtPosition(

            EarthlyBranchDirectionalCombinationEvidence evidence,

            EarthlyBranchPosition position,

            EarthlyBranch expectedBranch

    ) {

        if (
                evidence.firstPosition() == position
        ) {

            return evidence.firstBranch()
                    == expectedBranch;
        }

        if (
                evidence.secondPosition() == position
        ) {

            return evidence.secondBranch()
                    == expectedBranch;
        }

        if (
                evidence.thirdPosition() == position
        ) {

            return evidence.thirdBranch()
                    == expectedBranch;
        }

        return true;
    }

    public boolean hasRelation() {

        return !evidences
                .isEmpty();
    }

    public int formationEvidenceCount() {

        return evidences
                .size();
    }

    /*
     * =================================================
     * 잠정 方局 성립
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    provisionallyEstablishedFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::provisionallyEstablished
                )
                .toList();
    }

    public boolean hasProvisionallyEstablishedFormation() {

        return !provisionallyEstablishedFormations()
                .isEmpty();
    }

    /*
     * =================================================
     * 부분 方局
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    provisionallyPartialFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::provisionallyPartial
                )
                .toList();
    }

    public boolean hasProvisionallyPartialFormation() {

        return !provisionallyPartialFormations()
                .isEmpty();
    }

    /*
     * =================================================
     * 조건부
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    conditionalFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::conditional
                )
                .toList();
    }

    /*
     * =================================================
     * 관계만 존재
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    relationOnlyFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::relationOnly
                )
                .toList();
    }

    /*
     * =================================================
     * 의미 있는 方局 작용
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    meaningfulFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::meaningfulFormation
                )
                .toList();
    }

    public boolean hasMeaningfulFormation() {

        return !meaningfulFormations()
                .isEmpty();
    }

    /*
     * =================================================
     * 형태별
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    completeCombinationFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::completeCombination
                )
                .toList();
    }

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    partialWithCardinalFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::partialWithCardinal
                )
                .toList();
    }

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    partialWithoutCardinalFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::partialWithoutCardinal
                )
                .toList();
    }

    /*
     * =================================================
     * 계절 방향 일치
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    seasonAlignedFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::seasonDirectionAligned
                )
                .toList();
    }

    public boolean hasSeasonAlignedFormation() {

        return !seasonAlignedFormations()
                .isEmpty();
    }

    /*
     * =================================================
     * 상태별
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    formationsOfState(
            EarthlyBranchDirectionalCombinationFormationState state
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

    /*
     * =================================================
     * 방합 종류
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    formationsOf(
            EarthlyBranchDirectionalCombinationType combinationType
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

    /*
     * =================================================
     * 목표 오행
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    formationsTargeting(
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

    public List<FiveElement>
    provisionalFormationElements() {

        return meaningfulFormations()
                .stream()
                .map(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::targetElement
                )
                .distinct()
                .toList();
    }

    /*
     * =================================================
     * 일지
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    dayBranchFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    dayBranchMeaningfulFormations() {

        return dayBranchFormations()
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::meaningfulFormation
                )
                .toList();
    }

    public boolean dayBranchInMeaningfulFormation() {

        return !dayBranchMeaningfulFormations()
                .isEmpty();
    }

    /*
     * =================================================
     * 월지
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    monthBranchFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchDirectionalCombinationFormationEvidence
                                ::monthBranchInvolved
                )
                .toList();
    }

    public boolean monthBranchInFormation() {

        return !monthBranchFormations()
                .isEmpty();
    }

    /*
     * =================================================
     * 궁위
     * =================================================
     */

    public List<EarthlyBranchDirectionalCombinationFormationEvidence>
    formationsInvolving(
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

    public boolean hourUnknown() {

        return !completeChart;
    }
}