package com.dajeom.model;

import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StructureStatus;
import com.dajeom.type.StructureUsefulGodCandidateRole;
import com.dajeom.type.StructureUsefulGodReasonType;
import com.dajeom.type.UsefulGodMethodApplicability;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-4.
 *
 * 하나의 五行 격국용신 후보.
 *
 * =================================================
 *
 * formationSupportMatch
 *
 * 5-5에서 확인한 성격(成格) 요소와
 * 해당 오행이 일치하는가.
 *
 * =================================================
 *
 * damageElementMatch
 *
 * 5-6에서 확인한 파격 요소 자체와
 * 해당 오행이 일치하는가.
 *
 * 이 값은 일반적으로 후보에 불리한 Evidence다.
 *
 * =================================================
 *
 * damageRepairMatch
 *
 * 6-5 병약 단계에서
 * REPAIR_STRUCTURE_DAMAGE 작용을 가진 오행인가.
 *
 * 즉:
 *
 * "파격 오행"과
 * "파격을 해결하는 오행"
 *
 * 을 분리한다.
 *
 * =================================================
 *
 * specialStructureReview
 *
 * 현재 주격이 건록·양인 등의
 * 특수격인 경우.
 *
 * 특수격 세부 규칙을 일반 내격 공식으로
 * 강제로 대체하지 않는다.
 *
 * =================================================
 *
 * followStructureSuppressed
 *
 * 종격 주격이라 일반 격국용신법이
 * 7-7 종격용신법에 우선권을 넘긴 상태.
 */
public record StructureUsefulGodCandidate(

        FiveElement element,

        DayMasterElementRole dayMasterRole,

        StructureUsefulGodCandidateRole role,

        UsefulGodMethodApplicability methodApplicability,

        StructureStatus structureStatus,

        ClimateHealthCrossValidationState strengthState,

        ClimateHealthValidatedCandidate sourceValidatedCandidate,

        boolean standardPrimaryStructure,

        boolean specialPrimaryStructure,

        boolean formationSupportMatch,

        boolean damageElementMatch,

        boolean damageRepairMatch,

        boolean specialStructureReview,

        boolean followStructureSuppressed,

        List<StructureUsefulGodReasonType> reasons

) {

    public StructureUsefulGodCandidate {

        Objects.requireNonNull(
                element,
                "격국용신 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "격국용신 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                methodApplicability,
                "격국용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                structureStatus,
                "격국 상태가 필요합니다."
        );

        Objects.requireNonNull(
                strengthState,
                "신강신약 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                sourceValidatedCandidate,
                "6-9 교차검증 후보가 필요합니다."
        );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                sourceValidatedCandidate.element()
                        != element
        ) {

            throw new IllegalArgumentException(
                    "6-9 후보와 7-4 후보의 오행이 일치하지 않습니다."
            );
        }

        if (
                sourceValidatedCandidate.dayMasterRole()
                        != dayMasterRole
        ) {

            throw new IllegalArgumentException(
                    "일간 기준 오행 역할이 6-9 결과와 일치하지 않습니다."
            );
        }

        if (
                sourceValidatedCandidate.structureSupportMatch()
                        != formationSupportMatch
        ) {

            throw new IllegalArgumentException(
                    "성격 요소 일치 상태가 6-9 결과와 일치하지 않습니다."
            );
        }

        if (
                sourceValidatedCandidate.structureDamageMatch()
                        != damageElementMatch
        ) {

            throw new IllegalArgumentException(
                    "파격 요소 일치 상태가 6-9 결과와 일치하지 않습니다."
            );
        }

        if (
                standardPrimaryStructure
                        && specialPrimaryStructure
        ) {

            throw new IllegalArgumentException(
                    "주격을 일반격과 특수격으로 동시에 지정할 수 없습니다."
            );
        }

        if (
                specialStructureReview
                        != specialPrimaryStructure
        ) {

            throw new IllegalArgumentException(
                    "특수격 검토 상태가 실제 주격 유형과 일치하지 않습니다."
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
                    "격국용신 후보 근거가 중복되었습니다."
            );
        }

        if (
                methodApplicability.inactive()
                        && !role.notNeeded()
        ) {

            throw new IllegalArgumentException(
                    "비활성 격국용신법에서 후보를 생성할 수 없습니다."
            );
        }

        if (
                followStructureSuppressed
                        && role.selected()
        ) {

            throw new IllegalArgumentException(
                    "종격으로 억제된 일반 격국 후보를 PRIMARY/SECONDARY로 선택할 수 없습니다."
            );
        }

        if (
                damageElementMatch

                        && !formationSupportMatch

                        && !damageRepairMatch

                        && role.selected()
        ) {

            throw new IllegalArgumentException(
                    "순수 파격 오행을 격국용신 선택 후보로 지정할 수 없습니다."
            );
        }

        if (
                strengthState.contradictory()
                        && role.selected()
        ) {

            throw new IllegalArgumentException(
                    "일간 강약 핵심 방향과 충돌하는 오행을 격국용신 선택 후보로 지정할 수 없습니다."
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

    public boolean hasPositiveStructureEvidence() {

        return formationSupportMatch
                || damageRepairMatch;
    }

    public boolean hasNegativeStructureEvidence() {

        return damageElementMatch;
    }

    public boolean mixedStructureEvidence() {

        return damageElementMatch
                && (
                formationSupportMatch
                        || damageRepairMatch
        );
    }

    public boolean pureDamageElement() {

        return damageElementMatch
                && !formationSupportMatch
                && !damageRepairMatch;
    }

    public boolean strengthSupported() {

        return strengthState.supported();
    }

    public boolean strengthStrained() {

        return strengthState.strained();
    }

    public boolean strengthContradictory() {

        return strengthState.contradictory();
    }

    public int existingShareBasisPoints() {

        return sourceValidatedCandidate
                .existingShareBasisPoints();
    }

    public double existingSharePercent() {

        return sourceValidatedCandidate
                .existingSharePercent();
    }

    public boolean hasReason(
            StructureUsefulGodReasonType reason
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