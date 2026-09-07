package com.dajeom.calculation;

import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimCoverageAnalysis;
import com.dajeom.domain.LifeClaimCoverageEntry;
import com.dajeom.domain.LifeClaimCoverageState;
import com.dajeom.domain.LifeInterpretationCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class LifeClaimCoverageCalculator {

    private final LifeClaimCategoryMapper categoryMapper;

    public LifeClaimCoverageCalculator(
            LifeClaimCategoryMapper categoryMapper
    ) {

        this.categoryMapper =
                Objects.requireNonNull(
                        categoryMapper,
                        "LifeClaimCategoryMapper는 필수입니다."
                );
    }

    public LifeClaimCoverageAnalysis analyze(
            List<LifeClaim> claims
    ) {

        Objects.requireNonNull(
                claims,
                "LifeClaim 목록은 필수입니다."
        );

        Map<
                LifeInterpretationCategory,
                List<LifeClaim>
                > grouped =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            grouped.put(
                    category,
                    new ArrayList<>()
            );
        }

        for (LifeClaim claim : claims) {

            Objects.requireNonNull(
                    claim,
                    "LifeClaim 목록에 null이 포함될 수 없습니다."
            );

            for (
                    LifeInterpretationCategory category
                    : categoryMapper.categoriesOf(claim)
            ) {

                List<LifeClaim> categoryClaims =
                        grouped.get(category);

                if (!categoryClaims.contains(claim)) {

                    categoryClaims.add(
                            claim
                    );
                }
            }
        }

        Map<
                LifeInterpretationCategory,
                LifeClaimCoverageEntry
                > entries =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            List<LifeClaim> categoryClaims =
                    List.copyOf(
                            grouped.get(category)
                    );

            LifeClaimCoverageState state =
                    categoryClaims.isEmpty()
                            ? LifeClaimCoverageState.MISSING
                            : LifeClaimCoverageState.COVERED;

            entries.put(
                    category,
                    new LifeClaimCoverageEntry(
                            category,
                            state,
                            categoryClaims
                    )
            );
        }

        return new LifeClaimCoverageAnalysis(
                entries
        );
    }
}