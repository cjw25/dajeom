package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;

import java.util.Objects;

/**
 * 정밀화 12차-2.
 *
 * 특정 궁위의 천간 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 癸酉
 * 庚申
 * 丁丑
 * 甲辰
 *
 * 이라면:
 *
 * YEAR  -> 癸
 * MONTH -> 庚
 * DAY   -> 丁
 * HOUR  -> 甲
 *
 * =================================================
 *
 * dayMasterAnchor
 *
 * 단순히 stem이 일간과 같은 글자인지를
 * 의미하지 않는다.
 *
 * DAY Palace의 천간인지 여부를 뜻한다.
 *
 * 예:
 *
 * 일간이 丁인데 년간에도 丁이 있다고 해서
 * YEAR 천간을 dayMasterAnchor=true로 만들지 않는다.
 *
 * 오직 DAY Palace Stem만 true다.
 */
public record PalaceStemEvidence(

        HeavenlyStem dayMaster,

        PalacePosition position,

        PalacePositionContextType positionContext,

        HeavenlyStem stem,

        boolean dayMasterAnchor

) {

    public PalaceStemEvidence {

        Objects.requireNonNull(
                dayMaster,
                "궁위 천간 Evidence에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "궁위 천간 Evidence에는 궁위 위치가 필요합니다."
        );

        Objects.requireNonNull(
                positionContext,
                "궁위 천간 Evidence에는 위치 Context가 필요합니다."
        );

        Objects.requireNonNull(
                stem,
                "궁위 천간 Evidence에는 천간이 필요합니다."
        );

        boolean expectedDayMasterAnchor =
                position == PalacePosition.DAY;

        if (
                dayMasterAnchor
                        != expectedDayMasterAnchor
        ) {

            throw new IllegalArgumentException(
                    "dayMasterAnchor는 DAY Palace에서만 true여야 합니다."
            );
        }

        if (
                position == PalacePosition.DAY
                        && stem != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "DAY Palace의 천간은 반드시 일간과 일치해야 합니다."
            );
        }
    }

    public boolean yearPosition() {

        return position == PalacePosition.YEAR;
    }

    public boolean monthPosition() {

        return position == PalacePosition.MONTH;
    }

    public boolean dayPosition() {

        return position == PalacePosition.DAY;
    }

    public boolean hourPosition() {

        return position == PalacePosition.HOUR;
    }

    public boolean dayMasterAnchorPosition() {

        return dayMasterAnchor;
    }

    /**
     * 현재 천간이 일간과 같은 글자인지 확인한다.
     *
     * dayMasterAnchor와는 의미가 다르다.
     *
     * YEAR 丁 / DAY 丁인 경우
     * YEAR도 sameStemAsDayMaster()는 true일 수 있지만,
     * dayMasterAnchor는 false다.
     */
    public boolean sameStemAsDayMaster() {

        return stem == dayMaster;
    }
}