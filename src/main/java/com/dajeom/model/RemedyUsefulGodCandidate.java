package com.dajeom.model;

import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.RemedyUsefulGodCandidateRole;
import com.dajeom.type.RemedyUsefulGodReasonType;
import com.dajeom.type.UsefulGodMethodApplicability;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-5.
 *
 * 하나의 五行 병약용신 후보.
 *
 * =================================================
 *
 * sourceCandidate
 *
 * 6-5에서 계산한 약(藥) 후보.
 *
 * 7-5는 6-5의 계산을 다시 만드는 단계가 아니라
 * 용신 후보로 사용할 수 있는지를 재검증하는 단계다.
 *
 * =================================================
 *
 * meaningfulAddressedPathologyCount
 *
 * 해당 오행이 의미 있게 완화하는
 * 병 구조의 수.
 *
 * 이것은 점수가 아니다.
 *
 * =================================================
 *
 * addressesSeverePathology
 *
 * HIGH / CRITICAL 수준의 병 구조를
 * 직접 다루는 Evidence가 있는가.
 *
 * =================================================
 *
 * unresolvedMeaningfulPathologyPresent
 *
 * 6-5 이후에도 아직 해결 방향을
 * 만들지 못한 의미 있는 병 구조가 존재하는가.
 *
 * 특정 후보가 잘못됐다는 뜻은 아니며
 * 전체 병약 판단에 남아 있는 불확실성을 의미한다.
 *
 * =================================================
 *
 * strengthState / structureState
 *
 * 6-9에서 이미 검증한:
 *
 * - 신강신약
 * - 격국
 *
 * 과 해당 오행의 관계.
 *
 * =================================================
 *
 * 아직 최종 용신이 아니다.
 */
public record RemedyUsefulGodCandidate(

        FiveElement element,

        DayMasterElementRole dayMasterRole,

        RemedyUsefulGodCandidateRole role,

        UsefulGodMethodApplicability methodApplicability,

        PathologySeverity overallPathologySeverity,

        RemedyCandidate sourceCandidate,

        ClimateHealthCrossValidationState strengthState,

        ClimateHealthCrossValidationState structureState,

        int meaningfulAddressedPathologyCount,

        boolean addressesSeverePathology,

        boolean unresolvedMeaningfulPathologyPresent,

        boolean sourceConflict,

        boolean saturated,

        List<RemedyUsefulGodReasonType> reasons

) {

    public RemedyUsefulGodCandidate {

        Objects.requireNonNull(
                element,
                "병약용신 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "병약용신 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                methodApplicability,
                "병약용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                overallPathologySeverity,
                "전체 병 구조 심각도가 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "6-5 약 후보가 필요합니다."
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
                    "6-5 약 후보와 7-5 후보의 오행이 일치하지 않습니다."
            );
        }

        if (
                sourceCandidate.dayMasterRole()
                        != dayMasterRole
        ) {

            throw new IllegalArgumentException(
                    "일간 기준 오행 역할이 6-5 결과와 일치하지 않습니다."
            );
        }

        if (
                meaningfulAddressedPathologyCount
                        != sourceCandidate
                        .meaningfulAddressedPathologyCount()
        ) {

            throw new IllegalArgumentException(
                    "해결하는 의미 있는 병 구조 수가 6-5 결과와 일치하지 않습니다."
            );
        }

        if (
                addressesSeverePathology
                        != sourceCandidate
                        .addressesSeverePathology()
        ) {

            throw new IllegalArgumentException(
                    "강한 병 구조 대응 상태가 6-5 결과와 일치하지 않습니다."
            );
        }

        if (
                sourceConflict
                        != sourceCandidate.hasConflict()
        ) {

            throw new IllegalArgumentException(
                    "병약 충돌 상태가 6-5 결과와 일치하지 않습니다."
            );
        }

        if (
                saturated
                        != sourceCandidate.saturated()
        ) {

            throw new IllegalArgumentException(
                    "오행 과다 상태가 6-5 결과와 일치하지 않습니다."
            );
        }

        if (
                meaningfulAddressedPathologyCount < 0
        ) {

            throw new IllegalArgumentException(
                    "해결 병 구조 수는 음수가 될 수 없습니다."
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
                    "병약용신 후보 선정 근거가 중복되었습니다."
            );
        }

        if (
                methodApplicability.inactive()
                        && !role.notNeeded()
        ) {

            throw new IllegalArgumentException(
                    "비활성 병약용신법에서 후보를 생성할 수 없습니다."
            );
        }

        if (
                !sourceCandidate.candidate()
                        && !role.notNeeded()
        ) {

            throw new IllegalArgumentException(
                    "6-5에서 약 후보가 아닌 오행을 병약용신 후보로 선택할 수 없습니다."
            );
        }

        if (
                sourceConflict
                        && role.selected()
        ) {

            throw new IllegalArgumentException(
                    "다른 병 구조와 충돌하는 오행을 병약용신 선택 후보로 지정할 수 없습니다."
            );
        }

        if (
                saturated
                        && role.primary()
        ) {

            throw new IllegalArgumentException(
                    "이미 과도한 오행을 병약용신 PRIMARY로 지정할 수 없습니다."
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
                    "신강신약 또는 격국과 직접 충돌하는 오행을 병약용신 선택 후보로 지정할 수 없습니다."
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

    public boolean addressesMultipleMeaningfulPathologies() {

        return meaningfulAddressedPathologyCount >= 2;
    }

    public boolean addressesMeaningfulPathology() {

        return meaningfulAddressedPathologyCount > 0;
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

        return existingShareBasisPoints()
                / 100.0;
    }

    public boolean hasReason(
            RemedyUsefulGodReasonType reason
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