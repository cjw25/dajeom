package com.dajeom.rule;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchSixCombinationType;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-3.
 *
 * 지지 육합 탐지 Rule.
 *
 * =================================================
 *
 * 책임
 *
 * 두 지지 사이에:
 *
 * 子丑
 * 寅亥
 * 卯戌
 * 辰酉
 * 巳申
 * 午未
 *
 * 육합 관계가 존재하는지만 판정한다.
 *
 * =================================================
 *
 * 여기서 판단하지 않는 것:
 *
 * - 실제 합화
 * - 합화 실패
 * - 합이 충보다 우세한지
 * - 충으로 인해 합이 깨지는지
 * - 쟁합
 * - 다중 합 우선순위
 * - 원래 지지 기능 상실
 *
 * 위 내용은 이후 통합 및 9차에서 처리한다.
 */
@Component
public class EarthlyBranchSixCombinationRule {

    public Optional<EarthlyBranchSixCombinationType>
    findCombinationType(

            EarthlyBranch firstBranch,

            EarthlyBranch secondBranch

    ) {

        Objects.requireNonNull(
                firstBranch,
                "첫 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                secondBranch,
                "두 번째 지지가 필요합니다."
        );

        if (
                firstBranch == secondBranch
        ) {

            return Optional.empty();
        }

        for (
                EarthlyBranchSixCombinationType type
                : EarthlyBranchSixCombinationType.values()
        ) {

            if (
                    type.matches(
                            firstBranch,
                            secondBranch
                    )
            ) {

                return Optional.of(
                        type
                );
            }
        }

        return Optional.empty();
    }

    public boolean combines(

            EarthlyBranch firstBranch,

            EarthlyBranch secondBranch

    ) {

        if (
                firstBranch == null
                        || secondBranch == null
        ) {

            return false;
        }

        return findCombinationType(
                firstBranch,
                secondBranch
        )
                .isPresent();
    }

    /**
     * 원국 위치 거리.
     *
     * YEAR  = 0
     * MONTH = 1
     * DAY   = 2
     * HOUR  = 3
     */
    public int positionDistance(

            EarthlyBranchPosition firstPosition,

            EarthlyBranchPosition secondPosition

    ) {

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 지지 위치가 필요합니다."
        );

        if (
                firstPosition == secondPosition
        ) {

            return 0;
        }

        return Math.abs(
                firstPosition.ordinal()
                        - secondPosition.ordinal()
        );
    }

    public boolean adjacent(

            EarthlyBranchPosition firstPosition,

            EarthlyBranchPosition secondPosition

    ) {

        return positionDistance(
                firstPosition,
                secondPosition
        ) == 1;
    }

    public boolean dayBranchInvolved(

            EarthlyBranchPosition firstPosition,

            EarthlyBranchPosition secondPosition

    ) {

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 지지 위치가 필요합니다."
        );

        return firstPosition
                == EarthlyBranchPosition.DAY

                || secondPosition
                == EarthlyBranchPosition.DAY;
    }
}