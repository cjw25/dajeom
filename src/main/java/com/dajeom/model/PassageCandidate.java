package com.dajeom.model;

import com.dajeom.type.ClimateElementCandidateRole;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PassageCandidateReasonType;
import com.dajeom.type.PassageCandidateRole;
import com.dajeom.type.PassageNeedLevel;
import com.dajeom.type.RemedyCandidateRole;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-7.
 *
 * 하나의 五行 통관 후보.
 *
 * passageEvidences:
 *
 * 해당 오행을 bridge로 사용하는
 * 6-6 통관 필요 Evidence.
 *
 * 현재 기본 五行 통관 규칙에서는
 * 각 bridge 오행이 하나의 정규 상극 관계에
 * 대응하지만 List 구조로 보존해
 * 후속 Rule 확장 가능성을 유지한다.
 *
 * =================================================
 *
 * remedyRole
 *
 * 동일 오행이 6-5 병약 분석에서는
 * 어떤 후보 역할을 가지는지.
 *
 * =================================================
 *
 * climateRole
 *
 * 동일 오행이 6-3 조후 분석에서는
 * 어떤 후보 역할을 가지는지.
 *
 * =================================================
 *
 * PRIMARY는 최종 용신이 아니다.
 */
public record PassageCandidate(

        FiveElement element,

        PassageCandidateRole role,

        PassageNeedLevel strongestNeed,

        ClimateElementPresenceLevel presenceLevel,

        int existingShareBasisPoints,

        RemedyCandidateRole remedyRole,

        ClimateElementCandidateRole climateRole,

        List<PassageEvidence> passageEvidences,

        List<PassageCandidateReasonType> reasons,

        boolean dayMasterDisadvantaged,

        boolean remedyConflict,

        boolean climateConflict

) {

    public PassageCandidate {

        Objects.requireNonNull(
                element,
                "통관 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "통관 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                strongestNeed,
                "통관 필요도가 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "통관 오행 보유 수준이 필요합니다."
        );

        Objects.requireNonNull(
                remedyRole,
                "병약 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                climateRole,
                "조후 후보 역할이 필요합니다."
        );

        passageEvidences =
                passageEvidences == null
                        ? List.of()
                        : List.copyOf(
                        passageEvidences
                );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                passageEvidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "통관 후보에는 해당 bridge 오행을 사용하는 PassageEvidence가 필요합니다."
            );
        }

        if (
                existingShareBasisPoints < 0
                        || existingShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "현재 통관 오행 비중은 0~10000bp 사이여야 합니다."
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
                    "현재 통관 오행 비중과 PresenceLevel이 일치하지 않습니다."
            );
        }

        for (
                PassageEvidence evidence
                : passageEvidences
        ) {

            if (
                    evidence.bridgeElement()
                            != element
            ) {

                throw new IllegalArgumentException(
                        "PassageEvidence의 통관 오행이 후보 오행과 일치하지 않습니다."
                );
            }

            if (
                    evidence.bridgeShareBasisPoints()
                            != existingShareBasisPoints
            ) {

                throw new IllegalArgumentException(
                        "PassageEvidence의 통관 오행 비중이 후보 비중과 일치하지 않습니다."
                );
            }
        }

        PassageNeedLevel expectedStrongest =
                passageEvidences
                        .stream()
                        .map(
                                PassageEvidence
                                        ::needLevel
                        )
                        .max(
                                Comparator.comparingInt(
                                        PassageNeedLevel
                                                ::rank
                                )
                        )
                        .orElse(
                                PassageNeedLevel.NONE
                        );

        if (
                strongestNeed
                        != expectedStrongest
        ) {

            throw new IllegalArgumentException(
                    "후보의 최강 통관 필요도가 PassageEvidence와 일치하지 않습니다."
            );
        }

        boolean expectedDayMasterDisadvantaged =
                passageEvidences
                        .stream()
                        .anyMatch(
                                PassageEvidence
                                        ::dayMasterDisadvantaged
                        );

        if (
                dayMasterDisadvantaged
                        != expectedDayMasterDisadvantaged
        ) {

            throw new IllegalArgumentException(
                    "일간 불리 상태가 PassageEvidence와 일치하지 않습니다."
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
                    "통관 후보 선택 근거가 중복되었습니다."
            );
        }

        /*
         * 통관 필요가 전혀 없다면
         * 후보 역할을 부여할 수 없다.
         */
        if (
                strongestNeed.none()
                        && !role.notNeeded()
        ) {

            throw new IllegalArgumentException(
                    "통관 필요도가 NONE인 오행을 통관 후보로 선택할 수 없습니다."
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

    public boolean meaningfulNeed() {

        return strongestNeed
                .meaningful();
    }

    public boolean highPriorityNeed() {

        return strongestNeed
                .highPriority();
    }

    public boolean criticalNeed() {

        return strongestNeed
                .critical();
    }

    public boolean remedyAligned() {

        return remedyRole
                .selected();
    }

    public boolean climateAligned() {

        return climateRole
                .selected();
    }

    public boolean anyCrossAnalysisAlignment() {

        return remedyAligned()
                || climateAligned();
    }

    public boolean bothAnalysesAligned() {

        return remedyAligned()
                && climateAligned();
    }

    public boolean crossAnalysisConflict() {

        return remedyConflict
                || climateConflict;
    }

    public boolean saturated() {

        return presenceLevel
                .saturated();
    }

    public boolean scarce() {

        return presenceLevel
                .scarce();
    }

    public boolean bridgeInsufficient() {

        return passageEvidences
                .stream()
                .filter(
                        PassageEvidence
                                ::passageRequired
                )
                .anyMatch(
                        PassageEvidence
                                ::bridgeInsufficient
                );
    }

    public boolean bridgeAlreadyFunctions() {

        List<PassageEvidence> required =
                passageEvidences
                        .stream()
                        .filter(
                                PassageEvidence
                                        ::passageRequired
                        )
                        .toList();

        return !required.isEmpty()
                && required
                .stream()
                .allMatch(
                        PassageEvidence
                                ::bridgeAlreadyFunctions
                );
    }

    public boolean hasReason(
            PassageCandidateReasonType reason
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

    public double existingSharePercent() {

        return existingShareBasisPoints
                / 100.0;
    }
}