package com.dajeom.model;

import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PassageNeedLevel;
import com.dajeom.type.PassageUsefulGodCandidateRole;
import com.dajeom.type.PassageUsefulGodReasonType;
import com.dajeom.type.UsefulGodMethodApplicability;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-6.
 *
 * 하나의 五行 통관용신 후보.
 *
 * =================================================
 *
 * sourceCandidate
 *
 * 6-7에서 계산한 통관 오행 후보.
 *
 * 7-6은 해당 후보를 그대로 복사하지 않고
 * 용신 후보로 사용할 수 있는지를 재검증한다.
 *
 * =================================================
 *
 * meaningfulPassageEvidenceCount
 *
 * 해당 오행이 bridge로 관여하는
 * 의미 있는 통관 Evidence 수.
 *
 * 점수가 아니다.
 *
 * =================================================
 *
 * highPriorityPassageEvidenceCount
 *
 * HIGH / CRITICAL 통관 병목 가운데
 * 해당 bridge 오행이 실제로 연결하는 수.
 *
 * =================================================
 *
 * bridgeInsufficient
 *
 * 현재 원국에 해당 통관 오행이
 * 실제로 부족한가.
 *
 * =================================================
 *
 * dayMasterDisadvantaged
 *
 * 해당 통관 관계에서 일간이 직접
 * 불리한 쪽에 있는가.
 *
 * =================================================
 *
 * remedyAligned / climateAligned
 *
 * 6-5 병약 후보,
 * 6-3 조후 후보도
 * 같은 오행 방향을 지목하는가.
 *
 * =================================================
 *
 * strengthState / structureState
 *
 * 6-9에서 검증한 신강신약·격국 상태.
 *
 * =================================================
 *
 * 아직 최종 용신이 아니다.
 */
public record PassageUsefulGodCandidate(

        FiveElement element,

        PassageUsefulGodCandidateRole role,

        UsefulGodMethodApplicability methodApplicability,

        PassageNeedLevel strongestNeed,

        PassageCandidate sourceCandidate,

        ClimateHealthCrossValidationState strengthState,

        ClimateHealthCrossValidationState structureState,

        int meaningfulPassageEvidenceCount,

        int highPriorityPassageEvidenceCount,

        boolean bridgeInsufficient,

        boolean bridgeAlreadyFunctions,

        boolean dayMasterDisadvantaged,

        boolean remedyAligned,

        boolean climateAligned,

        boolean sourceConflict,

        boolean saturated,

        List<PassageUsefulGodReasonType> reasons

) {

    public PassageUsefulGodCandidate {

        Objects.requireNonNull(
                element,
                "통관용신 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "통관용신 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                methodApplicability,
                "통관용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                strongestNeed,
                "통관 필요도가 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "6-7 통관 후보가 필요합니다."
        );

        Objects.requireNonNull(
                strengthState,
                "신강신약 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                structureState,
                "격국 교차검증 상태가 필요합니다."
        );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                sourceCandidate.element()
                        != element
        ) {

            throw new IllegalArgumentException(
                    "6-7 통관 후보와 7-6 후보의 오행이 일치하지 않습니다."
            );
        }

        if (
                sourceCandidate.strongestNeed()
                        != strongestNeed
        ) {

            throw new IllegalArgumentException(
                    "통관 필요도가 6-7 결과와 일치하지 않습니다."
            );
        }

        int expectedMeaningfulCount =
                (int) sourceCandidate
                        .passageEvidences()
                        .stream()
                        .filter(
                                PassageEvidence
                                        ::meaningfulPassageRequired
                        )
                        .count();

        if (
                meaningfulPassageEvidenceCount
                        != expectedMeaningfulCount
        ) {

            throw new IllegalArgumentException(
                    "의미 있는 통관 Evidence 수가 6-7 결과와 일치하지 않습니다."
            );
        }

        int expectedHighPriorityCount =
                (int) sourceCandidate
                        .passageEvidences()
                        .stream()
                        .filter(
                                PassageEvidence
                                        ::highPriority
                        )
                        .count();

        if (
                highPriorityPassageEvidenceCount
                        != expectedHighPriorityCount
        ) {

            throw new IllegalArgumentException(
                    "고우선 통관 Evidence 수가 6-7 결과와 일치하지 않습니다."
            );
        }

        if (
                bridgeInsufficient
                        != sourceCandidate
                        .bridgeInsufficient()
        ) {

            throw new IllegalArgumentException(
                    "통관 오행 부족 상태가 6-7 결과와 일치하지 않습니다."
            );
        }

        if (
                bridgeAlreadyFunctions
                        != sourceCandidate
                        .bridgeAlreadyFunctions()
        ) {

            throw new IllegalArgumentException(
                    "통관 오행 작동 상태가 6-7 결과와 일치하지 않습니다."
            );
        }

        if (
                dayMasterDisadvantaged
                        != sourceCandidate
                        .dayMasterDisadvantaged()
        ) {

            throw new IllegalArgumentException(
                    "일간 불리 상태가 6-7 결과와 일치하지 않습니다."
            );
        }

        if (
                remedyAligned
                        != sourceCandidate.remedyAligned()
        ) {

            throw new IllegalArgumentException(
                    "병약 방향 일치 상태가 6-7 결과와 일치하지 않습니다."
            );
        }

        if (
                climateAligned
                        != sourceCandidate.climateAligned()
        ) {

            throw new IllegalArgumentException(
                    "조후 방향 일치 상태가 6-7 결과와 일치하지 않습니다."
            );
        }

        if (
                sourceConflict
                        != sourceCandidate
                        .crossAnalysisConflict()
        ) {

            throw new IllegalArgumentException(
                    "통관 교차충돌 상태가 6-7 결과와 일치하지 않습니다."
            );
        }

        if (
                saturated
                        != sourceCandidate.saturated()
        ) {

            throw new IllegalArgumentException(
                    "오행 과다 상태가 6-7 결과와 일치하지 않습니다."
            );
        }

        if (
                meaningfulPassageEvidenceCount < 0
                        || highPriorityPassageEvidenceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "통관 Evidence 개수는 음수가 될 수 없습니다."
            );
        }

        if (
                highPriorityPassageEvidenceCount
                        > meaningfulPassageEvidenceCount
        ) {

            throw new IllegalArgumentException(
                    "고우선 통관 Evidence 수가 의미 있는 Evidence 수보다 많을 수 없습니다."
            );
        }

        if (
                reasons
                        .stream()
                        .distinct()
                        .count()
                        != reasons.size()
        ) {

            throw new IllegalArgumentException(
                    "통관용신 후보 선정 근거가 중복되었습니다."
            );
        }

        if (
                methodApplicability.inactive()
                        && !role.notNeeded()
        ) {

            throw new IllegalArgumentException(
                    "비활성 통관용신법에서 후보를 생성할 수 없습니다."
            );
        }

        if (
                !sourceCandidate.candidate()
                        && !role.notNeeded()
        ) {

            throw new IllegalArgumentException(
                    "6-7에서 통관 후보가 아닌 오행을 통관용신 후보로 선택할 수 없습니다."
            );
        }

        if (
                sourceConflict
                        && role.selected()
        ) {

            throw new IllegalArgumentException(
                    "병약·조후와 충돌하는 통관 후보를 선택 상태로 둘 수 없습니다."
            );
        }

        if (
                saturated
                        && role.primary()
        ) {

            throw new IllegalArgumentException(
                    "이미 과도한 오행을 통관용신 PRIMARY로 지정할 수 없습니다."
            );
        }

        if (
                (
                        strengthState.contradictory()
                                || structureState.contradictory()
                )
                        && role.selected()
        ) {

            throw new IllegalArgumentException(
                    "신강신약 또는 격국과 직접 충돌하는 오행을 통관용신 선택 후보로 지정할 수 없습니다."
            );
        }
    }

    public boolean primary() {

        return role.primary();
    }

    public boolean secondary() {

        return role.secondary();
    }

    public boolean conditional() {

        return role.conditional();
    }

    public boolean notNeeded() {

        return role.notNeeded();
    }

    public boolean candidate() {

        return role.candidate();
    }

    public boolean selected() {

        return role.selected();
    }

    public boolean sourcePrimary() {

        return sourceCandidate.primary();
    }

    public boolean sourceSecondary() {

        return sourceCandidate.secondary();
    }

    public boolean sourceConditional() {

        return sourceCandidate.conditional();
    }

    public boolean meaningfulNeed() {

        return strongestNeed.meaningful();
    }

    public boolean highPriorityNeed() {

        return strongestNeed.highPriority();
    }

    public boolean criticalNeed() {

        return strongestNeed.critical();
    }

    public boolean addressesMultipleMeaningfulConflicts() {

        return meaningfulPassageEvidenceCount >= 2;
    }

    public boolean addressesHighPriorityConflict() {

        return highPriorityPassageEvidenceCount > 0;
    }

    public boolean anyCrossMethodAlignment() {

        return remedyAligned
                || climateAligned;
    }

    public boolean dualCrossMethodAlignment() {

        return remedyAligned
                && climateAligned;
    }

    public boolean crossValidationConflict() {

        return strengthState.contradictory()
                || structureState.contradictory();
    }

    public boolean crossValidationStrained() {

        return strengthState.strained()
                || structureState.strained();
    }

    public boolean cleanCandidate() {

        return candidate()
                && !sourceConflict
                && !crossValidationConflict()
                && !saturated;
    }

    public int existingShareBasisPoints() {

        return sourceCandidate
                .existingShareBasisPoints();
    }

    public double existingSharePercent() {

        return sourceCandidate
                .existingShareBasisPoints()
                / 100.0;
    }

    public boolean hasReason(
            PassageUsefulGodReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }
}