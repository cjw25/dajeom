package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchBreakType;
import com.dajeom.type.EarthlyBranchPosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 8차-8.
 *
 * 원국에서 실제 발견된 하나의 지지 파 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * MONTH 巳
 * DAY 申
 *
 * ->
 *
 * 巳申破
 *
 * firstPosition
 * = MONTH
 *
 * secondPosition
 * = DAY
 *
 * positionDistance
 * = 1
 *
 * adjacent
 * = true
 *
 * dayBranchInvolved
 * = true
 *
 * monthBranchInvolved
 * = true
 *
 * =================================================
 *
 * Evidence 저장 순서는 항상:
 *
 * YEAR
 * MONTH
 * DAY
 * HOUR
 *
 * 순이다.
 *
 * first/second는 작용 방향을 의미하지 않는다.
 *
 * 破는 이번 단계에서 대칭적인 Pair 관계로 보존한다.
 */
public record EarthlyBranchBreakEvidence(

        EarthlyBranchBreakType breakType,

        EarthlyBranchPosition firstPosition,

        EarthlyBranch firstBranch,

        EarthlyBranchPosition secondPosition,

        EarthlyBranch secondBranch,

        int positionDistance,

        boolean adjacent,

        boolean dayBranchInvolved,

        boolean monthBranchInvolved

) {

    public EarthlyBranchBreakEvidence {

        Objects.requireNonNull(
                breakType,
                "지지 파 유형이 필요합니다."
        );

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                firstBranch,
                "첫 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                secondBranch,
                "두 번째 지지가 필요합니다."
        );

        if (
                firstPosition == secondPosition
        ) {

            throw new IllegalArgumentException(
                    "동일 위치의 지지끼리는 파 Evidence를 만들 수 없습니다."
            );
        }

        /*
         * Evidence는 원국 위치 순서대로 저장한다.
         */
        if (
                firstPosition.ordinal()
                        >= secondPosition.ordinal()
        ) {

            throw new IllegalArgumentException(
                    "지지 파 Evidence는 앞 기둥 위치부터 저장해야 합니다."
            );
        }

        if (
                !breakType.matches(
                        firstBranch,
                        secondBranch
                )
        ) {

            throw new IllegalArgumentException(
                    "지지 Pair와 파 유형이 일치하지 않습니다."
            );
        }

        int expectedDistance =
                secondPosition.ordinal()
                        - firstPosition.ordinal();

        if (
                positionDistance
                        != expectedDistance
        ) {

            throw new IllegalArgumentException(
                    "지지 파 위치 거리가 실제 위치와 일치하지 않습니다."
            );
        }

        if (
                adjacent
                        != (
                        positionDistance == 1
                )
        ) {

            throw new IllegalArgumentException(
                    "지지 파 인접 여부가 위치 거리와 일치하지 않습니다."
            );
        }

        boolean expectedDayBranchInvolved =
                firstPosition
                        == EarthlyBranchPosition.DAY

                        || secondPosition
                        == EarthlyBranchPosition.DAY;

        if (
                dayBranchInvolved
                        != expectedDayBranchInvolved
        ) {

            throw new IllegalArgumentException(
                    "일지 포함 상태가 실제 위치와 일치하지 않습니다."
            );
        }

        boolean expectedMonthBranchInvolved =
                firstPosition
                        == EarthlyBranchPosition.MONTH

                        || secondPosition
                        == EarthlyBranchPosition.MONTH;

        if (
                monthBranchInvolved
                        != expectedMonthBranchInvolved
        ) {

            throw new IllegalArgumentException(
                    "월지 포함 상태가 실제 위치와 일치하지 않습니다."
            );
        }
    }

    public List<EarthlyBranchPosition> positions() {

        return List.of(
                firstPosition,
                secondPosition
        );
    }

    public List<EarthlyBranch> branches() {

        return List.of(
                firstBranch,
                secondBranch
        );
    }

    public boolean involves(
            EarthlyBranch branch
    ) {

        if (
                branch == null
        ) {

            return false;
        }

        return firstBranch == branch
                || secondBranch == branch;
    }

    public boolean involves(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return firstPosition == position
                || secondPosition == position;
    }

    public boolean yearBranchInvolved() {

        return involves(
                EarthlyBranchPosition.YEAR
        );
    }

    public boolean hourBranchInvolved() {

        return involves(
                EarthlyBranchPosition.HOUR
        );
    }

    /**
     * 일지와 바로 인접한 파.
     *
     * MONTH-DAY
     * DAY-HOUR
     */
    public boolean adjacentToDayBranch() {

        return dayBranchInvolved
                && adjacent;
    }

    /**
     * 월지와 바로 인접한 파.
     *
     * YEAR-MONTH
     * MONTH-DAY
     */
    public boolean adjacentToMonthBranch() {

        return monthBranchInvolved
                && adjacent;
    }

    /**
     * 특정 궁위 반대편의 지지.
     */
    public EarthlyBranch counterpartOf(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return null;
        }

        if (
                firstPosition == position
        ) {

            return secondBranch;
        }

        if (
                secondPosition == position
        ) {

            return firstBranch;
        }

        return null;
    }

    /**
     * 특정 지지의 파 상대.
     */
    public EarthlyBranch counterpartOf(
            EarthlyBranch branch
    ) {

        if (
                branch == null
        ) {

            return null;
        }

        if (
                firstBranch == branch
        ) {

            return secondBranch;
        }

        if (
                secondBranch == branch
        ) {

            return firstBranch;
        }

        return null;
    }

    public boolean between(
            EarthlyBranchPosition position1,
            EarthlyBranchPosition position2
    ) {

        if (
                position1 == null
                        || position2 == null
                        || position1 == position2
        ) {

            return false;
        }

        return involves(
                position1
        )
                && involves(
                position2
        );
    }
}