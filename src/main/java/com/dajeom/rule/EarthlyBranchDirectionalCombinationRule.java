package com.dajeom.rule;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchDirectionalCombinationForm;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchPosition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-5.
 *
 * 지지 방합 관계 탐지 Rule.
 *
 * =================================================
 *
 * 책임
 *
 * 1.
 * 세 지지가 완전 방합인지.
 *
 * 2.
 * 두 지지가 동일 方의 구성원인지.
 *
 * 3.
 * 두 지지 부분 관계가 왕지를 포함하는지.
 *
 * 4.
 * 왕지가 빠진 시작지+마무리지 관계인지.
 *
 * =================================================
 *
 * 여기서 판단하지 않는 것:
 *
 * - 실제 方局 성립 강도
 * - 실제 합화
 * - 삼합과 방합 중 무엇이 우세한가
 * - 육합과 어느 관계가 우세한가
 * - 충으로 방합이 깨지는가
 * - 계절 월령으로 인해 실제 강화되는가
 *
 * 위 내용은 이후 8-10 / 9차에서 처리한다.
 */
@Component
public class EarthlyBranchDirectionalCombinationRule {

    public Optional<EarthlyBranchDirectionalCombinationType>
    findCompleteCombinationType(

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
                EarthlyBranchDirectionalCombinationType type
                : EarthlyBranchDirectionalCombinationType.values()
        ) {

            if (
                    type.matchesComplete(
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

    public Optional<EarthlyBranchDirectionalCombinationType>
    findPartialCombinationType(

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
                EarthlyBranchDirectionalCombinationType type
                : EarthlyBranchDirectionalCombinationType.values()
        ) {

            if (
                    type.matchesPair(
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

    public EarthlyBranchDirectionalCombinationForm
    determinePairForm(

            EarthlyBranchDirectionalCombinationType combinationType,

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        Objects.requireNonNull(
                combinationType,
                "방합 유형이 필요합니다."
        );

        Objects.requireNonNull(
                branch1,
                "첫 번째 지지가 필요합니다."
        );

        Objects.requireNonNull(
                branch2,
                "두 번째 지지가 필요합니다."
        );

        if (
                !combinationType.matchesPair(
                        branch1,
                        branch2
                )
        ) {

            throw new IllegalArgumentException(
                    "해당 Pair는 지정된 방합의 구성원이 아닙니다."
            );
        }

        if (
                combinationType
                        .pairContainsCardinal(
                                branch1,
                                branch2
                        )
        ) {

            return EarthlyBranchDirectionalCombinationForm
                    .PARTIAL_WITH_CARDINAL;
        }

        if (
                combinationType
                        .openingClosingPair(
                                branch1,
                                branch2
                        )
        ) {

            return EarthlyBranchDirectionalCombinationForm
                    .PARTIAL_WITHOUT_CARDINAL;
        }

        throw new IllegalStateException(
                "부분 방합 형태를 결정할 수 없습니다."
        );
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