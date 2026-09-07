package com.dajeom.calculation;

import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimBundle;
import com.dajeom.domain.LifeInterpretationCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 생성 완료된 LifeClaim들을
 * 최종 7개 장문 해석 카테고리에 배치한다.
 */
@Component
public class LifeClaimBundleCalculator {

    private final LifeClaimCategoryMapper categoryMapper;

    public LifeClaimBundleCalculator(
            LifeClaimCategoryMapper categoryMapper
    ) {

        this.categoryMapper =
                Objects.requireNonNull(
                        categoryMapper,
                        "LifeClaimCategoryMapper는 필수입니다."
                );
    }

    public LifeClaimBundle build(
            List<LifeClaim> claims
    ) {

        Objects.requireNonNull(
                claims,
                "LifeClaim 목록은 필수입니다."
        );

        Map<LifeInterpretationCategory, List<LifeClaim>> grouped =
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

        for (
                LifeClaim claim
                : claims
        ) {

            Objects.requireNonNull(
                    claim,
                    "LifeClaim 목록에 null이 포함될 수 없습니다."
            );

            Set<LifeInterpretationCategory> categories =
                    categoryMapper
                            .categoriesOf(
                                    claim
                            );

            if (categories.isEmpty()) {

                throw new IllegalStateException(
                        "LifeClaim에 해석 카테고리가 배정되지 않았습니다: "
                                + claim.code()
                );
            }

            for (
                    LifeInterpretationCategory category
                    : categories
            ) {

                List<LifeClaim> categoryClaims =
                        grouped.get(category);

                /*
                 * 동일 Claim이 동일 카테고리에
                 * 중복으로 들어가는 것을 방지.
                 */
                if (!categoryClaims.contains(claim)) {

                    categoryClaims.add(
                            claim
                    );
                }
            }
        }

        return new LifeClaimBundle(
                grouped
        );
    }
}