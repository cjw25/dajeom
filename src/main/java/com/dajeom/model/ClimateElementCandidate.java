package com.dajeom.model;

import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.ClimateCorrectionTarget;
import com.dajeom.type.ClimateElementCandidateRole;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-3.
 *
 * 조후 관점의 하나의 오행 후보.
 *
 * 예:
 *
 * COLD_WET
 *
 * FIRE
 * ->
 * WARMING
 * DRYING
 *
 * 두 조후 축을 동시에 보정할 수 있다.
 *
 * 반대로:
 *
 * COLD_DRY
 *
 * FIRE
 * ->
 * WARMING에는 도움이 되지만
 * DRY를 더 강화할 수 있다.
 *
 * 따라서 CONDITIONAL이 될 수도 있다.
 *
 * 주의:
 *
 * 이 객체는 용신 확정 결과가 아니다.
 */
public record ClimateElementCandidate(

        FiveElement element,

        ClimateElementCandidateRole role,

        List<ClimateCorrectionTarget> targets,

        ClimateAdjustmentNeedLevel addressedNeedLevel,

        ClimateElementPresenceLevel presenceLevel,

        int existingShareBasisPoints,

        boolean directTemperatureCorrection,

        boolean directMoistureCorrection,

        boolean temperatureConflict,

        boolean moistureConflict,

        boolean bridgeCandidate

) {

    public ClimateElementCandidate {

        Objects.requireNonNull(
                element,
                "조후 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "조후 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                addressedNeedLevel,
                "조후 후보가 대응하는 필요도가 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 보유 수준이 필요합니다."
        );

        targets =
                targets == null
                        ? List.of()
                        : List.copyOf(
                        targets
                );

        if (
                targets
                        .stream()
                        .distinct()
                        .count()
                        != targets.size()
        ) {

            throw new IllegalArgumentException(
                    "조후 보정 목적이 중복되었습니다."
            );
        }

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

        boolean hasTemperatureTarget =
                targets
                        .stream()
                        .anyMatch(
                                ClimateCorrectionTarget
                                        ::temperatureTarget
                        );

        boolean hasMoistureTarget =
                targets
                        .stream()
                        .anyMatch(
                                ClimateCorrectionTarget
                                        ::moistureTarget
                        );

        boolean hasBridgeTarget =
                targets
                        .contains(
                                ClimateCorrectionTarget.BRIDGING
                        );

        if (
                directTemperatureCorrection
                        != hasTemperatureTarget
        ) {

            throw new IllegalArgumentException(
                    "한난 직접 보정 상태와 Target이 일치하지 않습니다."
            );
        }

        if (
                directMoistureCorrection
                        != hasMoistureTarget
        ) {

            throw new IllegalArgumentException(
                    "조습 직접 보정 상태와 Target이 일치하지 않습니다."
            );
        }

        if (
                bridgeCandidate
                        != hasBridgeTarget
        ) {

            throw new IllegalArgumentException(
                    "조후 연결 후보 상태와 Target이 일치하지 않습니다."
            );
        }

        if (
                role.notNeeded()
        ) {

            if (
                    !targets.isEmpty()
                            || directTemperatureCorrection
                            || directMoistureCorrection
                            || bridgeCandidate
            ) {

                throw new IllegalArgumentException(
                        "NOT_NEEDED 후보에는 조후 보정 기능을 지정할 수 없습니다."
                );
            }

            if (
                    !addressedNeedLevel.none()
            ) {

                throw new IllegalArgumentException(
                        "NOT_NEEDED 후보의 대응 필요도는 NONE이어야 합니다."
                );
            }

        } else {

            if (
                    targets.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "조후 후보에는 하나 이상의 보정 목적이 필요합니다."
                );
            }

            if (
                    addressedNeedLevel.none()
            ) {

                throw new IllegalArgumentException(
                        "조후 후보에는 실제 조후 필요도가 있어야 합니다."
                );
            }
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

    public boolean needed() {

        return role.candidate();
    }

    public boolean selected() {

        return role.selected();
    }

    public boolean directCandidate() {

        return directTemperatureCorrection
                || directMoistureCorrection;
    }

    public boolean hasConflict() {

        return temperatureConflict
                || moistureConflict;
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

    public int targetCount() {

        return targets.size();
    }

    public boolean addresses(
            ClimateCorrectionTarget target
    ) {

        if (
                target == null
        ) {

            return false;
        }

        return targets.contains(
                target
        );
    }

    public double existingSharePercent() {

        return existingShareBasisPoints
                / 100.0;
    }
}