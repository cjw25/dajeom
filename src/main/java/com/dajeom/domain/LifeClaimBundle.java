package com.dajeom.domain;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 최종 7개 카테고리에 배치된 LifeClaim 묶음.
 *
 * =================================================
 *
 * 이후 AI Prompt Builder는
 * NatalMyeongriProfile 전체를 받지 않고
 *
 * LifeClaimBundle
 *
 * 을 받는 방향으로 간다.
 */
public record LifeClaimBundle(

        Map<LifeInterpretationCategory, List<LifeClaim>> claimsByCategory

) {

    public LifeClaimBundle {

        Objects.requireNonNull(
                claimsByCategory,
                "카테고리별 LifeClaim Map은 필수입니다."
        );

        EnumMap<LifeInterpretationCategory, List<LifeClaim>> normalized =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        /*
         * 7개 카테고리를 항상 모두 만든다.
         *
         * claim이 하나도 없는 카테고리도
         * 빈 List로 존재한다.
         */
        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            List<LifeClaim> claims =
                    claimsByCategory.get(
                            category
                    );

            if (claims == null) {

                normalized.put(
                        category,
                        List.of()
                );

                continue;
            }

            List<LifeClaim> copied =
                    List.copyOf(claims);

            for (
                    LifeClaim claim
                    : copied
            ) {

                Objects.requireNonNull(
                        claim,
                        "LifeClaim 목록에 null이 포함될 수 없습니다."
                );
            }

            normalized.put(
                    category,
                    copied
            );
        }

        claimsByCategory =
                Map.copyOf(
                        normalized
                );
    }

    public List<LifeClaim> claimsFor(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "해석 카테고리는 필수입니다."
        );

        return claimsByCategory
                .getOrDefault(
                        category,
                        List.of()
                );
    }

    public boolean hasClaimsFor(
            LifeInterpretationCategory category
    ) {

        return !claimsFor(category)
                .isEmpty();
    }

    public int totalCategoryAssignments() {

        return claimsByCategory
                .values()
                .stream()
                .mapToInt(
                        List::size
                )
                .sum();
    }

    /**
     * 같은 Claim은 여러 카테고리에 들어갈 수 있으므로
     * 중복을 제거한 실제 LifeClaim 목록.
     */
    public List<LifeClaim> uniqueClaims() {

        List<LifeClaim> result =
                new ArrayList<>();

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            for (
                    LifeClaim claim
                    : claimsFor(category)
            ) {

                if (!result.contains(claim)) {

                    result.add(
                            claim
                    );
                }
            }
        }

        return List.copyOf(
                result
        );
    }
}