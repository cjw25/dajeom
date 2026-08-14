package com.dajeom.rule;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchThreeCombinationForm;
import com.dajeom.type.EarthlyBranchThreeCombinationType;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-4.
 *
 * 지지 삼합 관계 탐지 Rule.
 *
 * =================================================
 *
 * 책임
 *
 * 1.
 * 세 지지가 완전 삼합국인지.
 *
 * 2.
 * 두 지지가 동일 삼합국의 부분 구성인지.
 *
 * 3.
 * Pair가 왕지를 포함한 반합인지.
 *
 * 4.
 * 왕지가 빠진 생지+고지 부분조합인지.
 *
 * =================================================
 *
 * 여기서 판단하지 않는 것:
 *
 * - 실제 三合化局 성립
 * - 반합의 실제 화기 성립
 * - 충에 의해 깨지는가
 * - 육합과 어느 관계가 우세한가
 * - 방합과 어느 관계가 우세한가
 * - 월령을 얻었는가
 * - 투간했는가
 *
 * 위 내용은 9차 책임이다.
 */
@Component
public class EarthlyBranchThreeCombinationRule {

    public Optional<EarthlyBranchThreeCombinationType>
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
                EarthlyBranchThreeCombinationType type
                : EarthlyBranchThreeCombinationType.values()
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

    public Optional<EarthlyBranchThreeCombinationType>
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
                EarthlyBranchThreeCombinationType type
                : EarthlyBranchThreeCombinationType.values()
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

    public EarthlyBranchThreeCombinationForm
    determinePairForm(

            EarthlyBranchThreeCombinationType combinationType,

            EarthlyBranch branch1,

            EarthlyBranch branch2

    ) {

        Objects.requireNonNull(
                combinationType,
                "삼합 유형이 필요합니다."
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
                    "해당 Pair는 지정된 삼합국의 구성원이 아닙니다."
            );
        }

        if (
                combinationType
                        .pairContainsCardinal(
                                branch1,
                                branch2
                        )
        ) {

            return EarthlyBranchThreeCombinationForm
                    .HALF_WITH_CARDINAL;
        }

        if (
                combinationType
                        .birthStoragePair(
                                branch1,
                                branch2
                        )
        ) {

            return EarthlyBranchThreeCombinationForm
                    .PARTIAL_WITHOUT_CARDINAL;
        }

        throw new IllegalStateException(
                "삼합 Pair 형태를 결정할 수 없습니다."
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