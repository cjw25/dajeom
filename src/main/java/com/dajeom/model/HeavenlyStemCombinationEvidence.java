package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationType;
import com.dajeom.type.VisibleStemPosition;

import java.util.Objects;

/**
 * 정밀화 8차-1.
 *
 * 원국에서 실제 발견된 하나의 천간합 관계.
 *
 * =================================================
 *
 * 예:
 *
 * 月干 庚
 * 日干 丁
 *
 * 은 천간합이 아니다.
 *
 * 반면:
 *
 * 日干 丁
 * 時干 壬
 *
 * 이라면:
 *
 * 丁壬合木
 *
 * Evidence 하나를 생성한다.
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
 * 천간이 바로 이웃한 기둥인지.
 *
 * 실제 합화 성립 여부가 아니라
 * 9차에서 사용할 구조 Evidence다.
 *
 * =================================================
 *
 * dayMasterInvolved
 *
 * 일간이 해당 합 관계에 직접 들어가는지.
 */
public record HeavenlyStemCombinationEvidence(

        HeavenlyStemCombinationType combinationType,

        VisibleStemPosition firstPosition,

        HeavenlyStem firstStem,

        VisibleStemPosition secondPosition,

        HeavenlyStem secondStem,

        int positionDistance,

        boolean adjacent,

        boolean dayMasterInvolved

) {

    public HeavenlyStemCombinationEvidence {

        Objects.requireNonNull(
                combinationType,
                "천간합 유형이 필요합니다."
        );

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 천간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                firstStem,
                "첫 번째 천간이 필요합니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 천간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                secondStem,
                "두 번째 천간이 필요합니다."
        );

        if (
                firstPosition == secondPosition
        ) {

            throw new IllegalArgumentException(
                    "동일한 위치의 천간끼리는 합 관계를 만들 수 없습니다."
            );
        }

        /*
         * Evidence 내부에서는
         * 항상 원국 위치 순서대로 저장한다.
         *
         * YEAR
         * MONTH
         * DAY
         * HOUR
         */
        if (
                firstPosition.ordinal()
                        >= secondPosition.ordinal()
        ) {

            throw new IllegalArgumentException(
                    "천간합 Evidence는 앞 기둥 위치부터 저장해야 합니다."
            );
        }

        if (
                !combinationType.matches(
                        firstStem,
                        secondStem
                )
        ) {

            throw new IllegalArgumentException(
                    "천간과 천간합 유형이 일치하지 않습니다."
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
                    "천간 위치 거리가 실제 위치와 일치하지 않습니다."
            );
        }

        if (
                adjacent
                        != (
                        positionDistance == 1
                )
        ) {

            throw new IllegalArgumentException(
                    "천간 인접 상태가 위치 거리와 일치하지 않습니다."
            );
        }

        boolean expectedDayMasterInvolved =
                firstPosition
                        == VisibleStemPosition.DAY

                        || secondPosition
                        == VisibleStemPosition.DAY;

        if (
                dayMasterInvolved
                        != expectedDayMasterInvolved
        ) {

            throw new IllegalArgumentException(
                    "일간 포함 상태가 실제 위치와 일치하지 않습니다."
            );
        }
    }

    public FiveElement nominalTransformationElement() {

        return combinationType
                .nominalTransformationElement();
    }

    public boolean involves(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return false;
        }

        return firstStem == stem
                || secondStem == stem;
    }

    public boolean involves(
            VisibleStemPosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return firstPosition == position
                || secondPosition == position;
    }

    public boolean yearInvolved() {

        return involves(
                VisibleStemPosition.YEAR
        );
    }

    public boolean monthInvolved() {

        return involves(
                VisibleStemPosition.MONTH
        );
    }

    public boolean dayInvolved() {

        return involves(
                VisibleStemPosition.DAY
        );
    }

    public boolean hourInvolved() {

        return involves(
                VisibleStemPosition.HOUR
        );
    }

    /**
     * 일간과 바로 붙어 있는 합.
     *
     * 月-日
     * 또는
     * 日-時
     */
    public boolean adjacentToDayMaster() {

        return dayMasterInvolved
                && adjacent;
    }

    /**
     * 전통적인 합화 목표 오행만 반환한다.
     *
     * 실제 합화 여부는 의미하지 않는다.
     */
    public FiveElement transformationTarget() {

        return nominalTransformationElement();
    }
}