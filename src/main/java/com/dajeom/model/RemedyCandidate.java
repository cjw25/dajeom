package com.dajeom.model;

import com.dajeom.type.ClimateElementCandidateRole;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PathologyPatternType;
import com.dajeom.type.RemedyCandidateRole;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-5.
 *
 * 하나의 五行 약(藥) 후보.
 *
 * 한 오행이 여러 병 구조를 동시에
 * 완화할 수 있으므로 remedyLinks를
 * List로 보존한다.
 *
 * 동시에 다른 병을 악화할 수 있으므로
 * aggravatedPathologies도 별도로 보존한다.
 *
 * 예:
 *
 * 木
 *
 * - 약한 丁火 보강
 * - 식상 과다 제어
 *
 * 를 동시에 할 수 있지만
 *
 * 木 자체가 이미 과도하다면
 * 조건부 후보가 될 수 있다.
 */
public record RemedyCandidate(

        FiveElement element,

        DayMasterElementRole dayMasterRole,

        RemedyCandidateRole role,

        ClimateElementCandidateRole climateRole,

        ClimateElementPresenceLevel presenceLevel,

        int existingShareBasisPoints,

        List<RemedyPathologyLink> remedyLinks,

        List<PathologyPatternType> aggravatedPathologies,

        boolean strengthDirectionConflict,

        boolean climateConflict

) {

    public RemedyCandidate {

        Objects.requireNonNull(
                element,
                "약 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "약 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                climateRole,
                "조후 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 보유 수준이 필요합니다."
        );

        remedyLinks =
                remedyLinks == null
                        ? List.of()
                        : List.copyOf(
                        remedyLinks
                );

        aggravatedPathologies =
                aggravatedPathologies == null
                        ? List.of()
                        : List.copyOf(
                        aggravatedPathologies
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
                aggravatedPathologies
                        .stream()
                        .distinct()
                        .count()
                        != aggravatedPathologies.size()
        ) {

            throw new IllegalArgumentException(
                    "악화 가능 병 구조가 중복되었습니다."
            );
        }

        if (
                role.rejected()
                        && !remedyLinks.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "REJECTED 후보에 약 작용 Evidence를 보존할 수 없습니다."
            );
        }

        if (
                !role.rejected()
                        && remedyLinks.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "약 후보에는 하나 이상의 병 완화 Evidence가 필요합니다."
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

    public boolean rejected() {

        return role
                .rejected();
    }

    public boolean candidate() {

        return role
                .candidate();
    }

    public boolean selected() {

        return role
                .selected();
    }

    public boolean hasConflict() {

        return strengthDirectionConflict
                || climateConflict
                || !aggravatedPathologies
                .isEmpty();
    }

    public boolean aggravatesAnotherPathology() {

        return !aggravatedPathologies
                .isEmpty();
    }

    public int addressedPathologyCount() {

        return remedyLinks
                .stream()
                .map(
                        RemedyPathologyLink
                                ::pathologyPatternType
                )
                .distinct()
                .toList()
                .size();
    }

    public int meaningfulAddressedPathologyCount() {

        return (int) remedyLinks
                .stream()
                .filter(
                        RemedyPathologyLink
                                ::meaningfulPathology
                )
                .map(
                        RemedyPathologyLink
                                ::pathologyPatternType
                )
                .distinct()
                .count();
    }

    public boolean addressesSeverePathology() {

        return remedyLinks
                .stream()
                .anyMatch(
                        RemedyPathologyLink
                                ::severePathology
                );
    }

    public boolean addressesCriticalPathology() {

        return remedyLinks
                .stream()
                .anyMatch(
                        RemedyPathologyLink
                                ::criticalPathology
                );
    }

    public boolean hasDirectRemedyAction() {

        return remedyLinks
                .stream()
                .anyMatch(
                        RemedyPathologyLink
                                ::direct
                );
    }

    public boolean climateSupported() {

        return climateRole
                .candidate();
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
}