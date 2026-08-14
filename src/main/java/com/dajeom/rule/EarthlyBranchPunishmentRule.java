package com.dajeom.rule;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchPunishmentType;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-7.
 *
 * 지지 형 관계 탐지 Rule.
 *
 * =================================================
 *
 * 삼형:
 *
 * 寅巳申
 * 丑戌未
 *
 * =================================================
 *
 * 상형:
 *
 * 子卯
 *
 * =================================================
 *
 * 자형:
 *
 * 辰辰
 * 午午
 * 酉酉
 * 亥亥
 *
 * =================================================
 *
 * 중요한 정책
 *
 * 하나의 辰만 존재한다고
 * 辰辰 자형을 만들지 않는다.
 *
 * 서로 다른 원국 위치에
 * 辰이 적어도 두 개 있어야 한다.
 *
 * 같은 방식으로:
 *
 * 午午
 * 酉酉
 * 亥亥
 *
 * 를 처리한다.
 */
@Component
public class EarthlyBranchPunishmentRule {

    public Optional<EarthlyBranchPunishmentType>
    findCompleteThreePunishmentType(

            EarthlyBranch branch1,

            EarthlyBranch branch2,

            EarthlyBranch branch3

    ) {

        Objects.requireNonNull(
                branch1,
                "첫 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                branch2,
                "두 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                branch3,
                "세 번째 지지가 필요합니다."
        );

        for (
                EarthlyBranchPunishmentType type
                : EarthlyBranchPunishmentType.values()
        ) {

            if (
                    type.threePunishment()
                            && type
                            .matchesCompleteThree(
                                    branch1,
                                    branch2,
                                    branch3
                            )
            ) {

                return Optional.of(
                        type
                );
            }
        }

        return Optional.empty();
    }

    public Optional<EarthlyBranchPunishmentType>
    findPartialThreePunishmentType(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        Objects.requireNonNull(
                branch1,
                "첫 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                branch2,
                "두 번째 지지가 필요합니다."
        );

        if (
                branch1 == branch2
        ) {

            return Optional.empty();
        }

        for (
                EarthlyBranchPunishmentType type
                : EarthlyBranchPunishmentType.values()
        ) {

            if (
                    type.threePunishment()
                            && type
                            .matchesPartialThree(
                                    branch1,
                                    branch2
                            )
            ) {

                return Optional.of(
                        type
                );
            }
        }

        return Optional.empty();
    }

    public Optional<EarthlyBranchPunishmentType>
    findMutualPunishmentType(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        Objects.requireNonNull(
                branch1,
                "첫 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                branch2,
                "두 번째 지지가 필요합니다."
        );

        if (
                EarthlyBranchPunishmentType
                        .ZI_MAO
                        .matchesMutual(
                                branch1,
                                branch2
                        )
        ) {

            return Optional.of(
                    EarthlyBranchPunishmentType
                            .ZI_MAO
            );
        }

        return Optional.empty();
    }

    public Optional<EarthlyBranchPunishmentType>
    findSelfPunishmentType(

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        Objects.requireNonNull(
                branch1,
                "첫 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                branch2,
                "두 번째 지지가 필요합니다."
        );

        if (
                branch1 != branch2
        ) {

            return Optional.empty();
        }

        for (
                EarthlyBranchPunishmentType type
                : EarthlyBranchPunishmentType.values()
        ) {

            if (
                    type.selfPunishment()
                            && type.matchesSelf(
                            branch1,
                            branch2
                    )
            ) {

                return Optional.of(
                        type
                );
            }
        }

        return Optional.empty();
    }

    public int positionDistance(

            EarthlyBranchPosition position1,

            EarthlyBranchPosition position2

    ) {

        Objects.requireNonNull(
                position1,
                "첫 번째 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                position2,
                "두 번째 지지 위치가 필요합니다."
        );

        return Math.abs(
                position1.ordinal()
                        - position2.ordinal()
        );
    }

    public boolean adjacent(

            EarthlyBranchPosition position1,

            EarthlyBranchPosition position2

    ) {

        return positionDistance(
                position1,
                position2
        ) == 1;
    }
}