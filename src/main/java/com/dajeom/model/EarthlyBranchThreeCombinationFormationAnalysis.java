package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchThreeCombinationFormationState;
import com.dajeom.type.EarthlyBranchThreeCombinationType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 9차-5.
 *
 * 원국 전체 삼합·반합·부분조합의
 * 방해 검토 전 국 성립 분석.
 *
 * =================================================
 *
 * 9-5는 8-4의 모든 raw Evidence를
 * 그대로 평가하지 않는다.
 *
 * 입력 대상:
 *
 * threeCombinationAnalysis.independentRelations()
 *
 * =================================================
 *
 * 이유:
 *
 * 완전 삼합이 존재하면 8-4는:
 *
 * COMPLETE
 * +
 * 내부 Pair 3개
 *
 * 를 모두 Evidence로 보존한다.
 *
 * 하지만 9-5에서 이것들을 모두 별개의
 * 국 성립 후보로 계산하면 중복이 발생한다.
 *
 * 따라서 완전 삼합 내부 Pair는 제외하고
 * 독립 관계만 국 성립 대상으로 평가한다.
 */
public record EarthlyBranchThreeCombinationFormationAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        EarthlyBranch dayBranch,

        EarthlyBranchThreeCombinationAnalysis
        threeCombinationAnalysis,

        ElementStrengthAnalysis
        elementStrengthAnalysis,

        List<EarthlyBranchThreeCombinationFormationEvidence>
        evidences,

        boolean completeChart

) {

    public EarthlyBranchThreeCombinationFormationAnalysis {

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
                threeCombinationAnalysis,
                "8-4 삼합 분석이 필요합니다."
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
                threeCombinationAnalysis
                        .dayBranch()
                        != dayBranch
        ) {

            throw new IllegalArgumentException(
                    "8-4 삼합 분석의 일지가 9-5 일지와 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "3차 오행 세력 분석의 일간이 9-5 일간과 일치하지 않습니다."
            );
        }

        if (
                threeCombinationAnalysis.completeChart()
                        != completeChart

                        || elementStrengthAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "9-5 입력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        List<EarthlyBranchThreeCombinationEvidence>
                independentRelations =
                threeCombinationAnalysis
                        .independentRelations();

        if (
                evidences.size()
                        != independentRelations.size()
        ) {

            throw new IllegalArgumentException(
                    "8-4 독립 삼합 관계 수와 9-5 국 성립 Evidence 수가 일치하지 않습니다."
            );
        }

        Set<String> uniqueKeys =
                new HashSet<>();

        for (
                EarthlyBranchThreeCombinationFormationEvidence evidence
                : evidences
        ) {

            if (
                    !independentRelations
                            .contains(
                                    evidence.sourceEvidence()
                            )
            ) {

                throw new IllegalArgumentException(
                        "9-5 Evidence의 원본 삼합 관계가 8-4 독립 관계에 존재하지 않습니다."
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
                        "동일한 9-5 삼합 국 성립 Evidence가 중복되었습니다: "
                                + key
                );
            }
        }
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
     * 잠정 국 성립
     * =================================================
     */

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    provisionallyEstablishedFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
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
     * 부분 국
     * =================================================
     */

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    provisionallyPartialFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
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

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    conditionalFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
                                ::conditional
                )
                .toList();
    }

    /*
     * =================================================
     * 관계만 존재
     * =================================================
     */

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    relationOnlyFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
                                ::relationOnly
                )
                .toList();
    }

    /*
     * =================================================
     * 의미 있는 국 작용
     * =================================================
     */

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    meaningfulFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
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

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    completeCombinationFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
                                ::completeCombination
                )
                .toList();
    }

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    halfCombinationFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
                                ::halfCombination
                )
                .toList();
    }

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    partialWithoutCardinalFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
                                ::partialWithoutCardinal
                )
                .toList();
    }

    /*
     * =================================================
     * 상태별
     * =================================================
     */

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    formationsOfState(
            EarthlyBranchThreeCombinationFormationState state
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
     * 삼합 종류
     * =================================================
     */

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    formationsOf(
            EarthlyBranchThreeCombinationType combinationType
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

    public List<EarthlyBranchThreeCombinationFormationEvidence>
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
                        EarthlyBranchThreeCombinationFormationEvidence
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

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    dayBranchFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    dayBranchMeaningfulFormations() {

        return dayBranchFormations()
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
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

    public List<EarthlyBranchThreeCombinationFormationEvidence>
    monthBranchFormations() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchThreeCombinationFormationEvidence
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

    public List<EarthlyBranchThreeCombinationFormationEvidence>
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