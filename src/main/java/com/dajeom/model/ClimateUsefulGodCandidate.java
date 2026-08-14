package com.dajeom.model;

import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.ClimateUsefulGodCandidateRole;
import com.dajeom.type.ClimateUsefulGodReasonType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodMethodApplicability;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-3.
 *
 * 하나의 五行 조후용신 후보.
 *
 * =================================================
 *
 * sourceCandidate
 *
 * 6-3에서 계산한 조후 오행 후보.
 *
 * 7-3은 이 값을 다시 처음부터 계산하지 않고
 * 7차의 용신법 관점에서 재검증한다.
 *
 * =================================================
 *
 * strengthState
 *
 * 6-9에서 확인한
 * 일간 강약과 해당 오행의 관계.
 *
 * =================================================
 *
 * structureState
 *
 * 6-9에서 확인한
 * 격국과 해당 오행의 관계.
 *
 * =================================================
 *
 * competingClimate
 *
 * 寒燥 / 熱濕처럼
 * 한난과 조습의 보정 방향이 경쟁하는지.
 *
 * =================================================
 *
 * bridgeCandidate
 *
 * 경쟁하는 두 조후축을
 * 동시에 완화하거나 중재할 가능성이 있는
 * 6-3 조후 연결 후보인지.
 *
 * =================================================
 *
 * 아직 최종 용신이 아니다.
 */
public record ClimateUsefulGodCandidate(

        FiveElement element,

        ClimateUsefulGodCandidateRole role,

        UsefulGodMethodApplicability methodApplicability,

        ClimateElementCandidate sourceCandidate,

        ClimateHealthCrossValidationState strengthState,

        ClimateHealthCrossValidationState structureState,

        boolean competingClimate,

        boolean bridgeCandidate,

        List<ClimateUsefulGodReasonType> reasons

) {

    public ClimateUsefulGodCandidate {

        Objects.requireNonNull(
                element,
                "조후용신 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "조후용신 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                methodApplicability,
                "조후용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "6-3 조후 오행 후보가 필요합니다."
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
                    "6-3 조후 후보와 7-3 후보의 오행이 일치하지 않습니다."
            );
        }

        if (
                bridgeCandidate
                        != sourceCandidate.bridgeCandidate()
        ) {

            throw new IllegalArgumentException(
                    "조후 중재 후보 상태가 6-3 분석과 일치하지 않습니다."
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
                    "조후용신 후보 선정 근거가 중복되었습니다."
            );
        }

        if (
                methodApplicability.inactive()
                        && !role.notNeeded()
        ) {

            throw new IllegalArgumentException(
                    "비활성 조후용신법에서 후보를 생성할 수 없습니다."
            );
        }

        if (
                !sourceCandidate.needed()
                        && !role.notNeeded()
        ) {

            throw new IllegalArgumentException(
                    "6-3에서 조후 필요가 없는 오행을 7-3 후보로 선택할 수 없습니다."
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
                    "신강신약 또는 격국과 직접 충돌하는 조후 후보를 선택 상태로 둘 수 없습니다."
            );
        }

        if (
                sourceCandidate.saturated()
                        && role.primary()
        ) {

            throw new IllegalArgumentException(
                    "이미 과도한 오행을 조후 PRIMARY로 확정할 수 없습니다."
            );
        }
    }

    public boolean primary() {

        return role
                .primary();
    }

    public boolean secondary() {

        return role
                .secondary();
    }

    public boolean conditional() {

        return role
                .conditional();
    }

    public boolean notNeeded() {

        return role
                .notNeeded();
    }

    public boolean candidate() {

        return role
                .candidate();
    }

    public boolean selected() {

        return role
                .selected();
    }

    public boolean sourcePrimary() {

        return sourceCandidate
                .primary();
    }

    public boolean sourceSecondary() {

        return sourceCandidate
                .secondary();
    }

    public boolean sourceConditional() {

        return sourceCandidate
                .conditional();
    }

    public boolean sourceConflict() {

        return sourceCandidate
                .hasConflict();
    }

    public boolean saturated() {

        return sourceCandidate
                .saturated();
    }

    public boolean scarce() {

        return sourceCandidate
                .presenceLevel()
                .scarce();
    }

    public ClimateElementPresenceLevel presenceLevel() {

        return sourceCandidate
                .presenceLevel();
    }

    public int existingShareBasisPoints() {

        return sourceCandidate
                .existingShareBasisPoints();
    }

    public double existingSharePercent() {

        return existingShareBasisPoints()
                / 100.0;
    }

    public boolean crossValidationSupported() {

        return !strengthState
                .requiresCaution()

                && !structureState
                .requiresCaution();
    }

    public boolean crossValidationConflict() {

        return strengthState
                .contradictory()

                || structureState
                .contradictory();
    }

    public boolean crossValidationStrained() {

        return strengthState
                .strained()

                || structureState
                .strained();
    }

    public boolean hasReason(
            ClimateUsefulGodReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons
                .contains(
                        reason
                );
    }
}