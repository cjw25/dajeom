package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 지지에서 발견된 하나의 회합 occurrence.
 *
 * =================================================
 *
 * 六合:
 *
 * positions.size() = 2
 * branches.size()  = 2
 * groupElement     = empty
 *
 * =================================================
 *
 * 三合 / 三會:
 *
 * positions.size() = 3
 * branches.size()  = 3
 * groupElement     = 해당 局 오행
 *
 * =================================================
 *
 * 중요:
 *
 * occurrence 존재
 * !=
 * 실제 合化 성립
 *
 * 이 객체는 회합의 구성요건이
 * 원국에 존재한다는 사실만 나타낸다.
 */
public record PatternBranchCombinationOccurrence(

        PatternBranchCombinationKind kind,

        List<PillarPosition> positions,

        List<EarthlyBranch> branches,

        Optional<FiveElement> groupElement

) {

    public PatternBranchCombinationOccurrence {

        Objects.requireNonNull(
                kind,
                "지지 회합 종류는 필수입니다."
        );

        positions =
                positions == null
                        ? List.of()
                        : List.copyOf(positions);

        branches =
                branches == null
                        ? List.of()
                        : List.copyOf(branches);

        groupElement =
                groupElement == null
                        ? Optional.empty()
                        : groupElement;

        for (PillarPosition position : positions) {

            Objects.requireNonNull(
                    position,
                    "회합 위치 목록에 null이 포함될 수 없습니다."
            );
        }

        for (EarthlyBranch branch : branches) {

            Objects.requireNonNull(
                    branch,
                    "회합 지지 목록에 null이 포함될 수 없습니다."
            );
        }

        if (
                positions.size()
                        != branches.size()
        ) {

            throw new IllegalArgumentException(
                    "회합 위치 수와 지지 수가 일치해야 합니다."
            );
        }

        long distinctPositionCount =
                positions
                        .stream()
                        .distinct()
                        .count();

        if (
                distinctPositionCount
                        != positions.size()
        ) {

            throw new IllegalArgumentException(
                    "하나의 회합 occurrence에 동일 기둥 위치가 중복될 수 없습니다."
            );
        }

        switch (kind) {

            case SIX_HARMONY -> {

                if (positions.size() != 2) {

                    throw new IllegalArgumentException(
                            "六合 occurrence에는 정확히 두 지지가 필요합니다."
                    );
                }

                /*
                 * 六合의 化氣 오행은
                 * 학파별 해석 차이가 있으므로
                 * 이 공통 계층에서는 넣지 않는다.
                 */
                if (groupElement.isPresent()) {

                    throw new IllegalArgumentException(
                            "六合 occurrence에는 groupElement를 저장하지 않습니다."
                    );
                }
            }

            case COMPLETE_THREE_HARMONY,
                 COMPLETE_THREE_MEETING -> {

                if (positions.size() != 3) {

                    throw new IllegalArgumentException(
                            "三合/三會 occurrence에는 정확히 세 지지가 필요합니다."
                    );
                }

                if (groupElement.isEmpty()) {

                    throw new IllegalArgumentException(
                            "완전한 三合/三會에는 局 오행이 필요합니다."
                    );
                }
            }
        }
    }

    public boolean involvesMonthCommand() {

        return positions.contains(
                PillarPosition.MONTH
        );
    }

    public boolean involvesPosition(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "위치는 필수입니다."
        );

        return positions.contains(
                position
        );
    }

    public boolean involvesBranch(
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

    public boolean sixHarmony() {

        return kind
                == PatternBranchCombinationKind
                .SIX_HARMONY;
    }

    public boolean completeThreeHarmony() {

        return kind
                == PatternBranchCombinationKind
                .COMPLETE_THREE_HARMONY;
    }

    public boolean completeThreeMeeting() {

        return kind
                == PatternBranchCombinationKind
                .COMPLETE_THREE_MEETING;
    }

    /**
     * 월령과 함께 회합에 참여하는
     * 나머지 기둥 위치들.
     */
    public List<PillarPosition> monthCommandPartnerPositions() {

        if (!involvesMonthCommand()) {

            return List.of();
        }

        return positions
                .stream()
                .filter(
                        position ->
                                position
                                        != PillarPosition.MONTH
                )
                .toList();
    }

    /**
     * 월령과 함께 회합에 참여하는
     * 나머지 지지들.
     */
    public List<EarthlyBranch> monthCommandPartnerBranches() {

        if (!involvesMonthCommand()) {

            return List.of();
        }

        return positions
                .stream()
                .filter(
                        position ->
                                position
                                        != PillarPosition.MONTH
                )
                .map(
                        this::branchAt
                )
                .toList();
    }

    public EarthlyBranch branchAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "위치는 필수입니다."
        );

        for (
                int i = 0;
                i < positions.size();
                i++
        ) {

            if (
                    positions.get(i)
                            == position
            ) {

                return branches.get(i);
            }
        }

        throw new IllegalArgumentException(
                "해당 위치는 이 회합 occurrence에 포함되어 있지 않습니다: "
                        +
                        position
        );
    }
}