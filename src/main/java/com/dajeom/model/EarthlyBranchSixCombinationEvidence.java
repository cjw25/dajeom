package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchSixCombinationType;
import com.dajeom.type.FiveElement;

import java.util.Objects;

/**
 * 정밀화 8차-3.
 *
 * 원국에서 실제 발견된 하나의
 * 지지 육합 관계 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 月支 子
 * 日支 丑
 *
 * ->
 *
 * 子丑六合
 *
 * combinationType
 * = ZI_CHOU_EARTH
 *
 * nominalTransformationElement
 * = EARTH
 *
 * =================================================
 *
 * positionDistance
 *
 * 年 - 月 = 1
 * 月 - 日 = 1
 * 日 - 時 = 1
 *
 * 年 - 日 = 2
 * 月 - 時 = 2
 * 年 - 時 = 3
 *
 * =================================================
 *
 * adjacent
 *
 * 바로 이웃한 두 궁위인지.
 *
 * =================================================
 *
 * dayBranchInvolved
 *
 * 일지가 육합 관계에 직접 들어가는지.
 *
 * 일지는 이후:
 *
 * - 배우자궁
 * - 일상생활
 * - 개인적 관계
 *
 * 등 궁위 해석과 연결될 수 있으므로
 * Evidence를 별도로 보존한다.
 *
 * =================================================
 *
 * 주의:
 *
 * 이 클래스는 육합 존재만 나타낸다.
 *
 * 실제 合化 여부는 9차 책임이다.
 */
public record EarthlyBranchSixCombinationEvidence(

        EarthlyBranchSixCombinationType combinationType,

        EarthlyBranchPosition firstPosition,

        EarthlyBranch firstBranch,

        EarthlyBranchPosition secondPosition,

        EarthlyBranch secondBranch,

        int positionDistance,

        boolean adjacent,

        boolean dayBranchInvolved

) {

    public EarthlyBranchSixCombinationEvidence {

        Objects.requireNonNull(
                combinationType,
                "지지 육합 유형이 필요합니다."
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
                    "동일한 위치의 지지끼리는 육합 Evidence를 만들 수 없습니다."
            );
        }

        /*
         * Evidence는 항상:
         *
         * YEAR
         * MONTH
         * DAY
         * HOUR
         *
         * 순서로 저장한다.
         */
        if (
                firstPosition.ordinal()
                        >= secondPosition.ordinal()
        ) {

            throw new IllegalArgumentException(
                    "육합 Evidence는 앞 기둥 위치부터 저장해야 합니다."
            );
        }

        if (
                !combinationType.matches(
                        firstBranch,
                        secondBranch
                )
        ) {

            throw new IllegalArgumentException(
                    "지지 Pair와 육합 유형이 일치하지 않습니다."
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
                    "지지 위치 거리가 실제 위치와 일치하지 않습니다."
            );
        }

        if (
                adjacent
                        != (
                        positionDistance == 1
                )
        ) {

            throw new IllegalArgumentException(
                    "지지 인접 여부가 위치 거리와 일치하지 않습니다."
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
    }

    public FiveElement nominalTransformationElement() {

        return combinationType
                .nominalTransformationElement();
    }

    public FiveElement transformationTarget() {

        return nominalTransformationElement();
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

    public boolean monthBranchInvolved() {

        return involves(
                EarthlyBranchPosition.MONTH
        );
    }

    public boolean dayBranchInvolvedDirectly() {

        return involves(
                EarthlyBranchPosition.DAY
        );
    }

    public boolean hourBranchInvolved() {

        return involves(
                EarthlyBranchPosition.HOUR
        );
    }

    /**
     * 일지와 바로 인접한 육합.
     *
     * 月-日
     * 또는
     * 日-時
     */
    public boolean adjacentToDayBranch() {

        return dayBranchInvolved
                && adjacent;
    }
}