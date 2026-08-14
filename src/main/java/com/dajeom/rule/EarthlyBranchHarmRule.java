package com.dajeom.rule;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchHarmType;
import com.dajeom.type.EarthlyBranchPosition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-9.
 *
 * 지지 육해 탐지 Rule.
 *
 * =================================================
 *
 * 子未
 * 丑午
 * 寅巳
 * 卯辰
 * 申亥
 * 酉戌
 *
 * =================================================
 *
 * 책임:
 *
 * - 두 지지가 해 관계인지
 * - 어떤 해 유형인지
 * - 궁위 거리
 * - 인접 여부
 * - 일지 포함 여부
 * - 월지 포함 여부
 *
 * =================================================
 *
 * 여기서 판단하지 않는 것:
 *
 * - 해의 실제 강약
 * - 현실 사건
 * - 해가 합을 깨는가
 * - 충/형/파와 어느 쪽이 우세한가
 * - 용신 손상 여부
 * - 배우자궁/재물/직업 등 생활영역 해석
 *
 * 이러한 해석은 이후 단계의 책임이다.
 */
@Component
public class EarthlyBranchHarmRule {

    public Optional<EarthlyBranchHarmType>
    findHarmType(

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
                EarthlyBranchHarmType harmType
                : EarthlyBranchHarmType.values()
        ) {

            if (
                    harmType.matches(
                            firstBranch,
                            secondBranch
                    )
            ) {

                return Optional.of(
                        harmType
                );
            }
        }

        return Optional.empty();
    }

    public boolean harms(

            EarthlyBranch firstBranch,

            EarthlyBranch secondBranch

    ) {

        if (
                firstBranch == null
                        || secondBranch == null
        ) {

            return false;
        }

        return findHarmType(
                firstBranch,
                secondBranch
        )
                .isPresent();
    }

    /**
     * 특정 지지의 해 상대.
     */
    public EarthlyBranch counterpartOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        for (
                EarthlyBranchHarmType harmType
                : EarthlyBranchHarmType.values()
        ) {

            if (
                    harmType.contains(
                            branch
                    )
            ) {

                return harmType
                        .counterpartOf(
                                branch
                        );
            }
        }

        throw new IllegalStateException(
                "해 상대 지지를 찾을 수 없습니다: "
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