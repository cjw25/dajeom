package com.dajeom.model;

import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.ExtremeStrengthStructureType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.FollowStructureUsefulGodCandidateRole;
import com.dajeom.type.FollowStructureUsefulGodReasonType;
import com.dajeom.type.FollowWeakDirection;
import com.dajeom.type.UsefulGodMethodApplicability;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-7.
 *
 * 하나의 五行 종격용신 후보.
 *
 * =================================================
 *
 * followStructureType
 *
 * FOLLOW_WEAK_CANDIDATE
 * 또는
 * FOLLOW_STRONG_CANDIDATE.
 *
 * =================================================
 *
 * followWeakDirection
 *
 * 종약 구조일 때:
 *
 * OUTPUT
 * WEALTH
 * OFFICER
 * MIXED
 *
 * 중 어느 흐름으로 종하는지를 나타낸다.
 *
 * 종강 구조에서는 NONE.
 *
 * =================================================
 *
 * directFollowDirection
 *
 * 현재 종격의 핵심 방향 자체에
 * 직접 해당하는 오행인가.
 *
 * =================================================
 *
 * flowSupport
 *
 * 핵심 방향 자체는 아니지만
 * 그 방향을 생조하여 흐름을 이어주는가.
 *
 * =================================================
 *
 * dominantFlowElement
 *
 * 동일한 종격 방향 후보 가운데
 * 현재 실제 세력이 우세한 오행인가.
 *
 * 이것은 "많으면 무조건 좋다"는 뜻이 아니다.
 *
 * 종격이라는 특수 조건 아래에서
 * 이미 형성된 흐름의 중심을 확인하기 위한 Evidence다.
 *
 * =================================================
 *
 * breaksFollowFlow
 *
 * 해당 오행이 종하는 흐름을
 * 되돌리거나 직접 깨뜨릴 가능성이 있는가.
 *
 * =================================================
 *
 * 종격에서는 일반 오행 과다 개념을 그대로
 * 불리하게 사용하지 않는다.
 *
 * 즉 saturated라고 해서 자동 강등하지 않는다.
 */
public record FollowStructureUsefulGodCandidate(

        FiveElement element,

        DayMasterElementRole dayMasterRole,

        FollowStructureUsefulGodCandidateRole role,

        UsefulGodMethodApplicability methodApplicability,

        ExtremeStrengthStructureType followStructureType,

        FollowWeakDirection followWeakDirection,

        ClimateElementPresenceLevel presenceLevel,

        int existingShareBasisPoints,

        boolean directFollowDirection,

        boolean flowSupport,

        boolean dominantFlowElement,

        boolean breaksFollowFlow,

        ClimateHealthCrossValidationState strengthState,

        ClimateHealthCrossValidationState structureState,

        List<FollowStructureUsefulGodReasonType> reasons

) {

    public FollowStructureUsefulGodCandidate {

        Objects.requireNonNull(
                element,
                "종격용신 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "종격용신 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                methodApplicability,
                "종격용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                followStructureType,
                "극단구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                followWeakDirection,
                "종약 방향이 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 세력 수준이 필요합니다."
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
                existingShareBasisPoints < 0
                        || existingShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "현재 오행 비중은 0~10000bp 사이여야 합니다."
            );
        }

        ClimateElementPresenceLevel expectedPresence =
                ClimateElementPresenceLevel
                        .fromShareBasisPoints(
                                existingShareBasisPoints
                        );

        if (
                expectedPresence != presenceLevel
        ) {

            throw new IllegalArgumentException(
                    "현재 오행 비중과 PresenceLevel이 일치하지 않습니다."
            );
        }

        boolean followWeak =
                followStructureType
                        == ExtremeStrengthStructureType
                        .FOLLOW_WEAK_CANDIDATE;

        boolean followStrong =
                followStructureType
                        == ExtremeStrengthStructureType
                        .FOLLOW_STRONG_CANDIDATE;

        /*
         * 실제 종격용신법이 활성이라면
         * 반드시 종약 또는 종강 구조여야 한다.
         */
        if (
                methodApplicability.applicable()
                        && !followWeak
                        && !followStrong
        ) {

            throw new IllegalArgumentException(
                    "활성 종격용신법에는 종약 또는 종강 구조가 필요합니다."
            );
        }

        /*
         * 종약이면 NONE이 될 수 없다.
         */
        if (
                followWeak
                        && followWeakDirection
                        == FollowWeakDirection.NONE
        ) {

            throw new IllegalArgumentException(
                    "종약 구조에는 실제 종약 방향이 필요합니다."
            );
        }

        /*
         * 종강에서는 FollowWeakDirection을 사용하지 않는다.
         */
        if (
                followStrong
                        && followWeakDirection
                        != FollowWeakDirection.NONE
        ) {

            throw new IllegalArgumentException(
                    "종강 구조에서는 FollowWeakDirection이 NONE이어야 합니다."
            );
        }

        if (
                directFollowDirection
                        && breaksFollowFlow
        ) {

            throw new IllegalArgumentException(
                    "종격 핵심 방향과 직접 일치하면서 동시에 흐름을 깨는 오행일 수 없습니다."
            );
        }

        if (
                flowSupport
                        && breaksFollowFlow
        ) {

            throw new IllegalArgumentException(
                    "종격 흐름을 보조하면서 동시에 흐름을 깨는 오행일 수 없습니다."
            );
        }

        if (
                breaksFollowFlow
                        && role.selected()
        ) {

            throw new IllegalArgumentException(
                    "종하는 흐름을 깨는 오행을 종격용신 선택 후보로 지정할 수 없습니다."
            );
        }

        if (
                strengthState.contradictory()
                        && role.selected()
        ) {

            throw new IllegalArgumentException(
                    "종격 강약 방향과 충돌하는 오행을 선택 후보로 지정할 수 없습니다."
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
                    "종격용신 후보 근거가 중복되었습니다."
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

    public boolean followWeak() {

        return followStructureType
                == ExtremeStrengthStructureType
                .FOLLOW_WEAK_CANDIDATE;
    }

    public boolean followStrong() {

        return followStructureType
                == ExtremeStrengthStructureType
                .FOLLOW_STRONG_CANDIDATE;
    }

    public boolean saturated() {

        return presenceLevel.saturated();
    }

    public boolean scarce() {

        return presenceLevel.scarce();
    }

    public boolean crossValidationConflict() {

        return strengthState.contradictory()
                || structureState.contradictory();
    }

    public boolean crossValidationStrained() {

        return strengthState.strained()
                || structureState.strained();
    }

    public boolean cleanFollowCandidate() {

        return candidate()
                && !breaksFollowFlow
                && !crossValidationConflict();
    }

    public double existingSharePercent() {

        return existingShareBasisPoints
                / 100.0;
    }

    public boolean hasReason(
            FollowStructureUsefulGodReasonType reason
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