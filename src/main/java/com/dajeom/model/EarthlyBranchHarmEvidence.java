package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchHarmType;
import com.dajeom.type.EarthlyBranchPosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 8차-9.
 *
 * 원국에서 실제 발견된 하나의
 * 지지 해(害) 관계 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * MONTH 卯
 * DAY 辰
 *
 * ->
 *
 * 卯辰害
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
 * first / second는 해의 방향을 의미하지 않는다.
 *
 * Evidence는 단순히:
 *
 * YEAR
 * MONTH
 * DAY
 * HOUR
 *
 * 원국 위치 순서대로 저장한다.
 */
public record EarthlyBranchHarmEvidence(

        EarthlyBranchHarmType harmType,

        EarthlyBranchPosition firstPosition,

        EarthlyBranch firstBranch,

        EarthlyBranchPosition secondPosition,

        EarthlyBranch secondBranch,

        int positionDistance,

        boolean adjacent,

        boolean dayBranchInvolved,

        boolean monthBranchInvolved

) {

    public EarthlyBranchHarmEvidence {

        Objects.requireNonNull(
                harmType,
                "지지 해 유형이 필요합니다."
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
                    "동일 위치의 지지끼리는 해 Evidence를 만들 수 없습니다."
            );
        }

        /*
         * Evidence는 항상 원국 위치 순서대로 저장한다.
         */
        if (
                firstPosition.ordinal()
                        >= secondPosition.ordinal()
        ) {

            throw new IllegalArgumentException(
                    "지지 해 Evidence는 앞 기둥 위치부터 저장해야 합니다."
            );
        }

        if (
                !harmType.matches(
                        firstBranch,
                        secondBranch
                )
        ) {

            throw new IllegalArgumentException(
                    "지지 Pair와 해 유형이 일치하지 않습니다."
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
                    "지지 해 위치 거리가 실제 위치와 일치하지 않습니다."
            );
        }

        if (
                adjacent
                        != (
                        positionDistance == 1
                )
        ) {

            throw new IllegalArgumentException(
                    "지지 해 인접 여부가 위치 거리와 일치하지 않습니다."
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
     * 일지가 포함되고 두 궁위가 인접한 해.
     *
     * MONTH-DAY
     * 또는
     * DAY-HOUR
     */
    public boolean adjacentToDayBranch() {

        return dayBranchInvolved
                && adjacent;
    }

    /**
     * 월지가 포함되고 두 궁위가 인접한 해.
     *
     * YEAR-MONTH
     * 또는
     * MONTH-DAY
     */
    public boolean adjacentToMonthBranch() {

        return monthBranchInvolved
                && adjacent;
    }

    /**
     * 특정 궁위의 반대편 지지.
     *
     * 해당 궁위가 이 Evidence에 포함되지 않으면 null.
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
     * 특정 지지의 해 상대.
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