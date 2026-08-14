package com.dajeom.model;

import com.dajeom.type.BalancingUsefulGodCandidateRole;
import com.dajeom.type.BalancingUsefulGodReasonType;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodMethodApplicability;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-2.
 *
 * 하나의 五行 억부용신 후보.
 *
 * =================================================
 *
 * dayMasterRole
 *
 * 해당 오행이 일간 기준으로:
 *
 * COMPANION
 * RESOURCE
 * OUTPUT
 * WEALTH
 * OFFICER
 *
 * 중 어느 역할인지.
 *
 * =================================================
 *
 * strengthDirectionMatch
 *
 * 현재 신강·신약 방향에서
 * 해당 역할이 억부 조정 방향과 일치하는가.
 *
 * =================================================
 *
 * directPathologyMatch
 *
 * 단순 강약뿐 아니라 실제 6-4 병 구조도
 * 직접 완화하는 역할인가.
 *
 * 예:
 *
 * 신약 + 관살 과다
 * -> RESOURCE
 *
 * 신약 + 재성 부담
 * -> COMPANION
 *
 * 신강 + 생조 과다
 * -> OUTPUT
 *
 * =================================================
 *
 * preferredRole
 *
 * 억부 방향에 맞는 여러 역할 가운데
 * 현재 명식에서 더 우선적으로
 * 검토하도록 선택된 역할인가.
 *
 * =================================================
 *
 * followStructureSuppressed
 *
 * 종격 주격 때문에 일반 억부 판단을
 * 조건부로 제한한 상태인가.
 */
public record BalancingUsefulGodCandidate(

        FiveElement element,

        DayMasterElementRole dayMasterRole,

        BalancingUsefulGodCandidateRole role,

        UsefulGodMethodApplicability methodApplicability,

        ClimateElementPresenceLevel presenceLevel,

        int existingShareBasisPoints,

        boolean strengthDirectionMatch,

        boolean directPathologyMatch,

        boolean preferredRole,

        boolean followStructureSuppressed,

        List<BalancingUsefulGodReasonType> reasons

) {

    public BalancingUsefulGodCandidate {

        Objects.requireNonNull(
                element,
                "억부 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "억부 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                methodApplicability,
                "억부용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 보유 수준이 필요합니다."
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
                expectedPresence
                        != presenceLevel
        ) {

            throw new IllegalArgumentException(
                    "현재 오행 비중과 PresenceLevel이 일치하지 않습니다."
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
                    "억부 후보 선정 근거가 중복되었습니다."
            );
        }

        if (
                role.notNeeded()
                        && preferredRole
        ) {

            throw new IllegalArgumentException(
                    "NOT_NEEDED 후보를 억부 우선 역할로 지정할 수 없습니다."
            );
        }

        if (
                role.selected()
                        && !strengthDirectionMatch
        ) {

            throw new IllegalArgumentException(
                    "억부 방향과 일치하지 않는 오행을 선택 후보로 지정할 수 없습니다."
            );
        }

        if (
                followStructureSuppressed
                        && role.selected()
        ) {

            throw new IllegalArgumentException(
                    "종격으로 억제된 일반 억부 후보를 PRIMARY/SECONDARY로 선택할 수 없습니다."
            );
        }

        if (
                methodApplicability.inactive()
                        && !role.notNeeded()
        ) {

            throw new IllegalArgumentException(
                    "비활성 억부용신법에서 후보를 생성할 수 없습니다."
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

    public boolean scarce() {

        return presenceLevel
                .scarce();
    }

    public boolean abundant() {

        return presenceLevel
                .abundant();
    }

    public boolean saturated() {

        return presenceLevel
                .saturated();
    }

    public double existingSharePercent() {

        return existingShareBasisPoints
                / 100.0;
    }

    public boolean hasReason(
            BalancingUsefulGodReasonType reason
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