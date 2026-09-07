package com.dajeom.domain;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record LifeClaimCoverageAnalysis(

        Map<
                LifeInterpretationCategory,
                LifeClaimCoverageEntry
                > entries

) {

    public LifeClaimCoverageAnalysis {

        Objects.requireNonNull(
                entries,
                "Coverage entries는 필수입니다."
        );

        EnumMap<
                LifeInterpretationCategory,
                LifeClaimCoverageEntry
                > normalized =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            LifeClaimCoverageEntry entry =
                    entries.get(category);

            if (entry == null) {

                throw new IllegalArgumentException(
                        "Coverage 항목이 누락되었습니다: "
                                + category
                );
            }

            if (
                    entry.category()
                            != category
            ) {

                throw new IllegalArgumentException(
                        "Coverage Map의 category와 entry가 일치하지 않습니다."
                );
            }

            normalized.put(
                    category,
                    entry
            );
        }

        entries =
                Map.copyOf(
                        normalized
                );
    }

    public LifeClaimCoverageEntry entryOf(
            LifeInterpretationCategory category
    ) {

        return entries.get(category);
    }

    public boolean covered(
            LifeInterpretationCategory category
    ) {

        return entryOf(category)
                .covered();
    }

    public boolean missing(
            LifeInterpretationCategory category
    ) {

        return entryOf(category)
                .missing();
    }

    public List<LifeInterpretationCategory> missingCategories() {

        return entries
                .values()
                .stream()
                .filter(
                        LifeClaimCoverageEntry
                                ::missing
                )
                .map(
                        LifeClaimCoverageEntry
                                ::category
                )
                .sorted(
                        java.util.Comparator.comparingInt(
                                LifeInterpretationCategory
                                        ::order
                        )
                )
                .toList();
    }

    public boolean complete() {

        return missingCategories()
                .isEmpty();
    }
}