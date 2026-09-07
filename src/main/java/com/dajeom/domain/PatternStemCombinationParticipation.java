package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 하나의 천간 노드가
 * 어떤 天干五合 occurrence들에 참여하는지 나타낸다.
 *
 * 예:
 *
 * 日干 丁
 * +
 * 年干 壬
 * +
 * 時干 壬
 *
 * 이라면 丁 하나가 두 개의 丁壬合 후보에 참여한다.
 *
 * 이 경우:
 *
 * competingCombinationCandidate = true
 *
 * =================================================
 *
 * 주의:
 *
 * 이것은 爭合/妬合 확정이 아니다.
 *
 * 동일 천간 노드가 복수의 五合 관계 후보에
 * 동시에 걸려 있다는 구조적 사실만 나타낸다.
 */
public record PatternStemCombinationParticipation(

        PatternTenGodNode node,

        List<PatternStemCombinationOccurrence> combinations

) {

    public PatternStemCombinationParticipation {

        Objects.requireNonNull(
                node,
                "천간 노드는 필수입니다."
        );

        combinations =
                combinations == null
                        ? List.of()
                        : List.copyOf(combinations);

        for (
                PatternStemCombinationOccurrence combination
                : combinations
        ) {

            Objects.requireNonNull(
                    combination,
                    "천간합 목록에 null이 포함될 수 없습니다."
            );

            boolean participates =
                    combination.left().equals(node)
                            ||
                            combination.right().equals(node);

            if (!participates) {

                throw new IllegalArgumentException(
                        "Participation의 천간 노드가 해당 천간합에 참여하지 않습니다."
                );
            }
        }

        if (combinations.isEmpty()) {

            throw new IllegalArgumentException(
                    "PatternStemCombinationParticipation에는 최소 하나의 천간합이 필요합니다."
            );
        }
    }

    public boolean competingCombinationCandidate() {

        return combinations.size() > 1;
    }

    public boolean dayMaster() {

        return node.dayMaster();
    }

    public boolean visible() {

        return node.visible();
    }

    public boolean hasTenGodType(
            MonthCommandPatternCandidateType type
    ) {

        return node.hasTenGodType(type);
    }

    /**
     * 이 노드가 참여하는 합 중
     * 일간과의 합이 있는가.
     */
    public boolean combinedWithDayMaster() {

        if (node.dayMaster()) {

            return false;
        }

        return combinations
                .stream()
                .anyMatch(
                        combination ->
                                combination.involvesDayMaster()
                );
    }

    /**
     * 이 노드와 합하는 상대 노드들.
     */
    public List<PatternTenGodNode> counterparts() {

        return combinations
                .stream()
                .map(
                        combination -> {

                            if (
                                    combination.left()
                                            .equals(node)
                            ) {

                                return combination.right();
                            }

                            return combination.left();
                        }
                )
                .distinct()
                .toList();
    }
}