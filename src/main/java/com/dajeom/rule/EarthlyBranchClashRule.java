package com.dajeom.rule;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchClashType;
import com.dajeom.type.EarthlyBranchPosition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-6.
 *
 * 지지 육충 탐지 Rule.
 *
 * =================================================
 *
 * 子午
 * 丑未
 * 寅申
 * 卯酉
 * 辰戌
 * 巳亥
 *
 * =================================================
 *
 * 책임:
 *
 * - 두 지지가 육충 Pair인지
 * - 어떤 육충 유형인지
 * - 두 위치의 거리
 * - 인접 여부
 * - 일지 포함 여부
 * - 월지 포함 여부
 *
 * =================================================
 *
 * 여기서 판단하지 않는 것:
 *
 * - 충의 강약
 * - 길흉
 * - 합충 우선순위
 * - 실제 합의 해체
 * - 충에 의한 합화 실패
 * - 지장간 충돌
 *
 * 해당 내용은 이후 단계에서 처리한다.
 */
@Component
public class EarthlyBranchClashRule {

    public Optional<EarthlyBranchClashType>
    findClashType(

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
                EarthlyBranchClashType type
                : EarthlyBranchClashType.values()
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

    public boolean clashes(

            EarthlyBranch firstBranch,

            EarthlyBranch secondBranch

    ) {

        if (
                firstBranch == null
                        || secondBranch == null
        ) {

            return false;
        }

        return findClashType(
                firstBranch,
                secondBranch
        )
                .isPresent();
    }

    /**
     * 특정 지지의 충 상대.
     */
    public EarthlyBranch counterpartOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        for (
                EarthlyBranchClashType type
                : EarthlyBranchClashType.values()
        ) {

            if (
                    type.contains(
                            branch
                    )
            ) {

                return type.counterpartOf(
                        branch
                );
            }
        }

        throw new IllegalStateException(
                "충 상대를 찾을 수 없습니다: "
                        + branch
        );
    }

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

    public boolean monthBranchInvolved(

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
                == EarthlyBranchPosition.MONTH

                || secondPosition
                == EarthlyBranchPosition.MONTH;
    }
}