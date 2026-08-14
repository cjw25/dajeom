package com.dajeom.rule;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchBreakType;
import com.dajeom.type.EarthlyBranchPosition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-8.
 *
 * 지지 파 탐지 Rule.
 *
 * =================================================
 *
 * 子酉
 * 丑辰
 * 寅亥
 * 卯午
 * 巳申
 * 未戌
 *
 * =================================================
 *
 * 책임
 *
 * - 두 지지가 파 관계인지
 * - 어떤 파 유형인지
 * - 궁위 거리
 * - 인접 여부
 * - 일지 포함 여부
 * - 월지 포함 여부
 *
 * =================================================
 *
 * 이 Rule에서는 판단하지 않는다.
 *
 * - 길흉
 * - 관계 파괴의 실제 강도
 * - 六合와 破 중 무엇이 우세한가
 * - 三合/方合이 깨지는가
 * - 지장간이 실제 손상되는가
 * - 용신·희신·기신이 손상되는가
 *
 * 위 내용은 상위 관계 통합 단계에서 처리한다.
 */
@Component
public class EarthlyBranchBreakRule {

    public Optional<EarthlyBranchBreakType>
    findBreakType(

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
                EarthlyBranchBreakType breakType
                : EarthlyBranchBreakType.values()
        ) {

            if (
                    breakType.matches(
                            firstBranch,
                            secondBranch
                    )
            ) {

                return Optional.of(
                        breakType
                );
            }
        }

        return Optional.empty();
    }

    public boolean breaks(

            EarthlyBranch firstBranch,

            EarthlyBranch secondBranch

    ) {

        if (
                firstBranch == null
                        || secondBranch == null
        ) {

            return false;
        }

        return findBreakType(
                firstBranch,
                secondBranch
        )
                .isPresent();
    }

    /**
     * 특정 지지의 파 상대.
     */
    public EarthlyBranch counterpartOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        for (
                EarthlyBranchBreakType breakType
                : EarthlyBranchBreakType.values()
        ) {

            if (
                    breakType.contains(
                            branch
                    )
            ) {

                return breakType
                        .counterpartOf(
                                branch
                        );
            }
        }

        throw new IllegalStateException(
                "파 상대 지지를 찾을 수 없습니다: "
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