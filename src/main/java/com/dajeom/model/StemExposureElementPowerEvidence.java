package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.StemExposurePosition;

import java.util.List;
import java.util.Objects;

/**
 * 오행 세력 계산에 사용하는 하나의 투간 활성 근거.
 *
 * 예:
 *
 * 월지 申
 * └─ 庚 정기 60
 *
 * 월간 庚
 *
 * 위 구조라면:
 *
 * source = 월지 申 속 庚
 * target = 월간 庚
 *
 * 로 투간이 성립한다.
 *
 * 중요:
 *
 * hiddenStemWeight를 최종 오행 세력에
 * 다시 그대로 더하지 않는다.
 *
 * 지장간 자체 세력은 이미 3-2에서 계산됐다.
 *
 * 이 Evidence는:
 *
 * "해당 지장간이 천간으로 드러나
 * 실제 활성된 상태인가"
 *
 * 를 나타낸다.
 */
public record StemExposureElementPowerEvidence(

        FiveElement element,

        HeavenlyStem hiddenStem,

        StemExposurePosition sourcePosition,

        EarthlyBranch sourceBranch,

        HiddenStemRole hiddenStemRole,

        int hiddenStemWeight,

        int effectiveSourceWeight,

        List<StemExposurePosition> targetPositions

) {

    public StemExposureElementPowerEvidence {

        Objects.requireNonNull(
                element,
                "투간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "투간 지장간이 필요합니다."
        );

        Objects.requireNonNull(
                sourcePosition,
                "투간 근원 위치가 필요합니다."
        );

        Objects.requireNonNull(
                sourceBranch,
                "투간 근원 지지가 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStemRole,
                "지장간 역할이 필요합니다."
        );

        targetPositions =
                targetPositions == null
                        ? List.of()
                        : targetPositions
                        .stream()
                        .distinct()
                        .toList();

        if (
                hiddenStemWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "지장간 세력은 0보다 커야 합니다."
            );
        }

        if (
                effectiveSourceWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "투간 근원 유효 세력은 0보다 커야 합니다."
            );
        }

        if (
                targetPositions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "투출된 천간 위치가 최소 하나 이상 필요합니다."
            );
        }
    }

    /**
     * 실제 몇 군데 천간에
     * 동일한 천간이 드러났는지.
     */
    public int visibleStemCount() {

        return targetPositions.size();
    }

    /**
     * 동일 천간이 두 군데 이상
     * 겉으로 드러났는지.
     */
    public boolean repeatedExposure() {

        return visibleStemCount() >= 2;
    }

    public boolean fromYearBranch() {

        return sourcePosition.isYear();
    }

    public boolean fromMonthBranch() {

        return sourcePosition.isMonth();
    }

    public boolean fromDayBranch() {

        return sourcePosition.isDay();
    }

    public boolean fromHourBranch() {

        return sourcePosition.isHour();
    }

    public boolean mainQiExposure() {

        return hiddenStemRole.isMain();
    }

    public boolean middleQiExposure() {

        return hiddenStemRole.isMiddle();
    }

    public boolean residualQiExposure() {

        return hiddenStemRole.isResidual();
    }

    /**
     * 월지 정기가 투출한 상태.
     *
     * 이후 격국에서 매우 중요한
     * 별도 근거로 사용할 수 있다.
     */
    public boolean monthMainQiExposure() {

        return fromMonthBranch()
                && mainQiExposure();
    }

    /**
     * 같은 기둥의 천간으로
     * 직접 투출했는지.
     *
     * 예:
     *
     * 월지 申의 庚
     * ↓
     * 월간 庚
     */
    public boolean samePillarExposure() {

        return targetPositions
                .contains(
                        sourcePosition
                );
    }

    public boolean exposedToYearStem() {

        return targetPositions
                .contains(
                        StemExposurePosition.YEAR
                );
    }

    public boolean exposedToMonthStem() {

        return targetPositions
                .contains(
                        StemExposurePosition.MONTH
                );
    }

    public boolean exposedToDayStem() {

        return targetPositions
                .contains(
                        StemExposurePosition.DAY
                );
    }

    public boolean exposedToHourStem() {

        return targetPositions
                .contains(
                        StemExposurePosition.HOUR
                );
    }
}