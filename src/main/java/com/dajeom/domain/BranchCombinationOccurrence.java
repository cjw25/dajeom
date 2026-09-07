package com.dajeom.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 원국에서 실제 확인된 지지 합 관계.
 *
 * 중요한 점:
 *
 * 이 객체가 존재한다고 해서
 * 합화가 성립했다는 뜻이 아니다.
 *
 * 예:
 *
 * 子 + 丑
 * → 육합 관계 존재
 *
 * 와
 *
 * 子丑이 실제 土로 합화
 *
 * 는 별도의 판단이다.
 */
public record BranchCombinationOccurrence(

        BranchCombinationKind kind,

        List<PillarPosition> positions,

        List<EarthlyBranch> branches,

        FiveElement transformationElement

) {

    public BranchCombinationOccurrence {

        Objects.requireNonNull(
                kind,
                "합 종류는 필수입니다."
        );

        Objects.requireNonNull(
                positions,
                "합 참여 위치는 필수입니다."
        );

        Objects.requireNonNull(
                branches,
                "합 참여 지지는 필수입니다."
        );

        Objects.requireNonNull(
                transformationElement,
                "합화 방향 오행은 필수입니다."
        );

        positions =
                List.copyOf(
                        positions
                );

        branches =
                List.copyOf(
                        branches
                );

        if (
                positions.size()
                        != branches.size()
        ) {

            throw new IllegalArgumentException(
                    "합 참여 위치와 지지 개수가 일치하지 않습니다."
            );
        }

        int expectedSize =
                switch (kind) {

                    case SIX_HARMONY ->
                            2;

                    case THREE_HARMONY,
                         DIRECTIONAL_HARMONY ->
                            3;
                };

        if (
                positions.size()
                        != expectedSize
        ) {

            throw new IllegalArgumentException(
                    kind
                            + "의 참여 지지 수가 올바르지 않습니다."
            );
        }

        Set<PillarPosition> uniquePositions =
                new HashSet<>(
                        positions
                );

        if (
                uniquePositions.size()
                        != positions.size()
        ) {

            throw new IllegalArgumentException(
                    "하나의 기둥 위치가 합 관계에 중복될 수 없습니다."
            );
        }

        Set<EarthlyBranch> uniqueBranches =
                new HashSet<>(
                        branches
                );

        if (
                uniqueBranches.size()
                        != branches.size()
        ) {

            throw new IllegalArgumentException(
                    "완성 합 구조에는 동일 지지가 중복될 수 없습니다."
            );
        }
    }

    public boolean involves(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return positions.contains(
                position
        );
    }

    public boolean involves(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        return branches.contains(
                branch
        );
    }
}