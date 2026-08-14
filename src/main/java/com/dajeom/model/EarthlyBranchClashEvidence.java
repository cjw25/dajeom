package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchClashType;
import com.dajeom.type.EarthlyBranchPosition;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 8차-6.
 *
 * 원국에서 실제 발견된 하나의 지지충 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 月支 子
 * 日支 午
 *
 * ->
 *
 * 子午沖
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
 * 저장 순서는 항상:
 *
 * YEAR
 * MONTH
 * DAY
 * HOUR
 *
 * 기준이다.
 *
 * 지지충 자체는 대칭 관계이므로
 * first/second가 공격자/피공격자를 의미하지 않는다.
 *
 * =================================================
 *
 * 이 단계에서는:
 *
 * "일지가 충을 당했다"
 *
 * 라는 표현 대신:
 *
 * "일지가 충 관계에 직접 포함되어 있다"
 *
 * 로 구조적으로 저장한다.
 */
public record EarthlyBranchClashEvidence(

        EarthlyBranchClashType clashType,

        EarthlyBranchPosition firstPosition,

        EarthlyBranch firstBranch,

        EarthlyBranchPosition secondPosition,

        EarthlyBranch secondBranch,

        int positionDistance,

        boolean adjacent,

        boolean dayBranchInvolved,

        boolean monthBranchInvolved

) {

    public EarthlyBranchClashEvidence {

        Objects.requireNonNull(
                clashType,
                "지지충 유형이 필요합니다."
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
                    "동일 위치의 지지끼리는 충 Evidence를 만들 수 없습니다."
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
                    "지지충 Evidence는 앞 기둥 위치부터 저장해야 합니다."
            );
        }

        if (
                !clashType.matches(
                        firstBranch,
                        secondBranch
                )
        ) {

            throw new IllegalArgumentException(
                    "지지 Pair와 지지충 유형이 일치하지 않습니다."
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
                    "지지충 위치 거리가 실제 위치와 일치하지 않습니다."
            );
        }

        if (
                adjacent
                        != (
                        positionDistance == 1
                )
        ) {

            throw new IllegalArgumentException(
                    "지지충 인접 여부가 위치 거리와 일치하지 않습니다."
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
     * 일지와 바로 붙어 있는 충.
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
     * 월지와 바로 붙어 있는 충.
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
     * 특정 위치의 충 상대 지지를 반환한다.
     *
     * Evidence에 해당 위치가 포함되지 않으면 null.
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
     * 특정 지지의 충 상대.
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
}