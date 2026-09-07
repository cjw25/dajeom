package com.dajeom.domain;

import java.util.Objects;

/**
 * 원국 두 지지 사이에서 발견된
 * 沖 / 刑 / 破 / 害 한 건.
 *
 * 하나의 지지쌍이 두 종류 이상의 관계를
 * 동시에 가질 수도 있다.
 *
 * 예:
 *
 * 寅 - 巳
 * → 刑
 * → 害
 *
 * 따라서 같은 위치쌍에 여러 occurrence가
 * 존재하는 것을 허용한다.
 */
public record PatternBranchDisruptionOccurrence(

        PillarPosition leftPosition,

        EarthlyBranch leftBranch,

        PillarPosition rightPosition,

        EarthlyBranch rightBranch,

        PatternBranchDisruptionType type

) {

    public PatternBranchDisruptionOccurrence {

        Objects.requireNonNull(
                leftPosition,
                "왼쪽 위치는 필수입니다."
        );

        Objects.requireNonNull(
                leftBranch,
                "왼쪽 지지는 필수입니다."
        );

        Objects.requireNonNull(
                rightPosition,
                "오른쪽 위치는 필수입니다."
        );

        Objects.requireNonNull(
                rightBranch,
                "오른쪽 지지는 필수입니다."
        );

        Objects.requireNonNull(
                type,
                "지지 관계 타입은 필수입니다."
        );

        if (
                leftPosition
                        == rightPosition
        ) {

            throw new IllegalArgumentException(
                    "동일 기둥 자신과 지지 관계를 만들 수 없습니다."
            );
        }
    }

    public boolean involvesMonthCommand() {

        return leftPosition
                == PillarPosition.MONTH

                ||

                rightPosition
                        == PillarPosition.MONTH;
    }

    public boolean involvesPosition(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "위치는 필수입니다."
        );

        return leftPosition == position
                ||
                rightPosition == position;
    }

    /**
     * 월지와 관계를 이루는 상대 지지.
     */
    public EarthlyBranch counterpartOfMonthCommand() {

        if (!involvesMonthCommand()) {

            throw new IllegalStateException(
                    "월령이 포함되지 않은 관계입니다."
            );
        }

        if (
                leftPosition
                        == PillarPosition.MONTH
        ) {

            return rightBranch;
        }

        return leftBranch;
    }

    /**
     * 월령과 관계를 이루는 상대 위치.
     */
    public PillarPosition counterpartPositionOfMonthCommand() {

        if (!involvesMonthCommand()) {

            throw new IllegalStateException(
                    "월령이 포함되지 않은 관계입니다."
            );
        }

        if (
                leftPosition
                        == PillarPosition.MONTH
        ) {

            return rightPosition;
        }

        return leftPosition;
    }
}