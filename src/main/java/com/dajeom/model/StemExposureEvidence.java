package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.StemExposurePosition;

import java.util.Objects;

/**
 * 지장간 하나가 실제 천간으로 투출한 개별 근거.
 *
 * 예:
 *
 * 월지 申
 * └─ 庚 정기
 *
 * 월간 庚
 *
 * => 월지 정기 庚이 월간으로 투출
 */
public record StemExposureEvidence(

        HeavenlyStem stem,

        StemExposurePosition sourcePosition,

        EarthlyBranch sourceBranch,

        HiddenStemRole hiddenStemRole,

        int hiddenStemWeight,

        int sourcePositionWeightRate,

        int effectiveSourceWeight,

        StemExposurePosition targetPosition

) {

    public StemExposureEvidence {

        Objects.requireNonNull(
                stem,
                "투간 천간이 필요합니다."
        );

        Objects.requireNonNull(
                sourcePosition,
                "투간 근원 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                sourceBranch,
                "투간 근원 지지가 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStemRole,
                "지장간 역할이 필요합니다."
        );

        Objects.requireNonNull(
                targetPosition,
                "투출된 천간 위치가 필요합니다."
        );

        if (
                hiddenStemWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "지장간 가중치는 0보다 커야 합니다."
            );
        }

        if (
                sourcePositionWeightRate <= 0
        ) {

            throw new IllegalArgumentException(
                    "지지 위치 가중치는 0보다 커야 합니다."
            );
        }

        if (
                effectiveSourceWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "투간 근원 세력은 0보다 커야 합니다."
            );
        }
    }

    /**
     * 월지에서 나온 투간.
     */
    public boolean fromMonthBranch() {

        return sourcePosition.isMonth();
    }

    /**
     * 월간으로 투출.
     */
    public boolean toMonthStem() {

        return targetPosition.isMonth();
    }

    /**
     * 년간으로 투출.
     */
    public boolean toYearStem() {

        return targetPosition.isYear();
    }

    /**
     * 일간으로 투출.
     */
    public boolean toDayStem() {

        return targetPosition.isDay();
    }

    /**
     * 시간으로 투출.
     */
    public boolean toHourStem() {

        return targetPosition.isHour();
    }

    /**
     * 정기 투간.
     */
    public boolean mainQiExposure() {

        return hiddenStemRole.isMain();
    }

    /**
     * 중기 투간.
     */
    public boolean middleQiExposure() {

        return hiddenStemRole.isMiddle();
    }

    /**
     * 여기 투간.
     */
    public boolean residualQiExposure() {

        return hiddenStemRole.isResidual();
    }

    /**
     * 월지의 정기가 투출한 경우.
     *
     * 이후 격국 판단에서 중요한 근거로 사용한다.
     */
    public boolean monthMainQiExposure() {

        return fromMonthBranch()
                && mainQiExposure();
    }

    /**
     * 지장간과 같은 기둥의 천간으로
     * 바로 투출한 경우.
     *
     * 예:
     * 월지 申의 庚
     * +
     * 월간 庚
     */
    public boolean samePillarExposure() {

        return sourcePosition
                == targetPosition;
    }
}