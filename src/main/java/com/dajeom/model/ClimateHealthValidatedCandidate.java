package com.dajeom.model;

import com.dajeom.type.ClimateHealthCandidateReasonType;
import com.dajeom.type.ClimateHealthCandidateRole;
import com.dajeom.type.ClimateHealthCrossValidationReasonType;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-9.
 *
 * 6-8 ClimateHealthCandidate를
 * 신강·신약 및 격국과 다시 교차검증한 후보.
 *
 * =================================================
 *
 * strengthState
 *
 * 해당 오행이 현재 일간 강약 방향에
 * 얼마나 적합한지.
 *
 * =================================================
 *
 * structureState
 *
 * 해당 오행이 현재 주격을:
 *
 * - 살리는지
 * - 중립인지
 * - 부담시키는지
 * - 파격을 강화하는지
 *
 * 검토한 결과.
 *
 * =================================================
 *
 * overallState
 *
 * strengthState와 structureState를
 * 합산 점수 없이 구조적으로 결합한 결과.
 *
 * =================================================
 *
 * validatedRole
 *
 * 6-8 role을 기준으로 하되
 * 교차검증 결과가 불리하면 하향할 수 있다.
 *
 * 여기서는 6-8보다 후보를 승격시키지 않는다.
 */
public record ClimateHealthValidatedCandidate(

        FiveElement element,

        DayMasterElementRole dayMasterRole,

        ClimateHealthCandidate integrationCandidate,

        ClimateHealthCrossValidationState strengthState,

        ClimateHealthCrossValidationState structureState,

        ClimateHealthCrossValidationState overallState,

        ClimateHealthCandidateRole validatedRole,

        boolean structureSupportMatch,

        boolean structureDamageMatch,

        boolean followStructureReview,

        List<ClimateHealthCrossValidationReasonType> reasons

) {

    public ClimateHealthValidatedCandidate {

        Objects.requireNonNull(
                element,
                "교차검증 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                integrationCandidate,
                "6-8 통합 후보가 필요합니다."
        );

        Objects.requireNonNull(
                strengthState,
                "신강신약 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                structureState,
                "격국 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                overallState,
                "전체 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                validatedRole,
                "교차검증 이후 후보 역할이 필요합니다."
        );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                integrationCandidate.element()
                        != element
        ) {

            throw new IllegalArgumentException(
                    "6-8 후보 오행과 교차검증 오행이 일치하지 않습니다."
            );
        }

        if (
                integrationCandidate
                        .remedyCandidate()
                        .dayMasterRole()
                        != dayMasterRole
        ) {

            throw new IllegalArgumentException(
                    "일간 기준 오행 역할이 병약 분석 결과와 일치하지 않습니다."
            );
        }

        ClimateHealthCrossValidationState expectedOverall =
                combineStates(
                        strengthState,
                        structureState
                );

        if (
                expectedOverall
                        != overallState
        ) {

            throw new IllegalArgumentException(
                    "전체 교차검증 상태가 강약·격국 상태와 일치하지 않습니다."
            );
        }

        /*
         * 6-9는 검증 단계이므로
         * 6-8보다 후보를 승격시키지 않는다.
         */
        if (
                validatedRole.rank()
                        > integrationCandidate
                        .role()
                        .rank()
        ) {

            throw new IllegalArgumentException(
                    "6-9에서는 6-8 후보보다 역할을 상향할 수 없습니다."
            );
        }

        if (
                integrationCandidate.rejected()
                        && !validatedRole.rejected()
        ) {

            throw new IllegalArgumentException(
                    "6-8 REJECTED 후보를 6-9에서 다시 활성화할 수 없습니다."
            );
        }

        if (
                integrationCandidate.deferred()
                        && !validatedRole.deferred()
        ) {

            throw new IllegalArgumentException(
                    "6-8 DEFERRED 후보는 6-9에서도 DEFERRED를 유지해야 합니다."
            );
        }

        if (
                overallState.contradictory()
                        && integrationCandidate.selected()
                        && !validatedRole.deferred()
        ) {

            throw new IllegalArgumentException(
                    "핵심 구조와 충돌하는 선택 후보는 DEFERRED여야 합니다."
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
                    "교차검증 근거가 중복되었습니다."
            );
        }
    }

    private static ClimateHealthCrossValidationState combineStates(

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState

    ) {

        if (
                strengthState.contradictory()
                        || structureState.contradictory()
        ) {

            return ClimateHealthCrossValidationState
                    .CONTRADICTORY;
        }

        if (
                strengthState.strained()
                        || structureState.strained()
        ) {

            return ClimateHealthCrossValidationState
                    .STRAINED;
        }

        if (
                strengthState.supported()
                        || structureState.supported()
        ) {

            return ClimateHealthCrossValidationState
                    .SUPPORTED;
        }

        return ClimateHealthCrossValidationState
                .NEUTRAL;
    }

    public ClimateHealthCandidateRole baseRole() {

        return integrationCandidate
                .role();
    }

    public boolean primary() {

        return validatedRole
                .primary();
    }

    public boolean secondary() {

        return validatedRole
                .secondary();
    }

    public boolean deferred() {

        return validatedRole
                .deferred();
    }

    public boolean rejected() {

        return validatedRole
                .rejected();
    }

    public boolean selected() {

        return validatedRole
                .selected();
    }

    public boolean downgraded() {

        return validatedRole
                .rank()
                < baseRole()
                .rank();
    }

    public boolean strengthSupported() {

        return strengthState
                .supported();
    }

    public boolean strengthStrained() {

        return strengthState
                .strained();
    }

    public boolean structureSupported() {

        return structureState
                .supported();
    }

    public boolean structureContradictory() {

        return structureState
                .contradictory();
    }

    public boolean cleanlyValidated() {

        return overallState
                .acceptable()

                && selected();
    }

    public int existingShareBasisPoints() {

        return integrationCandidate
                .existingShareBasisPoints();
    }

    public double existingSharePercent() {

        return integrationCandidate
                .existingSharePercent();
    }

    public List<ClimateHealthCandidateReasonType>
    integrationReasons() {

        return integrationCandidate
                .reasons();
    }

    public boolean hasReason(
            ClimateHealthCrossValidationReasonType reason
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