package com.dajeom.calculation;

import com.dajeom.domain.LifeCategoryNarrativeDirective;
import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimBundle;
import com.dajeom.domain.LifeClaimNarrativeBundle;
import com.dajeom.domain.LifeClaimNarrativeDirective;
import com.dajeom.domain.LifeClaimNarrativeInstruction;
import com.dajeom.domain.LifeInterpretationCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * LifeClaimBundle을
 * AI Prompt Builder 직전의 NarrativeBundle로 변환한다.
 */
@Component
public class LifeClaimNarrativeBundleCalculator {

    private final LifeClaimNarrativeDirectiveCatalog
            claimDirectiveCatalog;

    private final LifeCategoryNarrativeDirectiveCatalog
            categoryDirectiveCatalog;

    public LifeClaimNarrativeBundleCalculator(

            LifeClaimNarrativeDirectiveCatalog claimDirectiveCatalog,

            LifeCategoryNarrativeDirectiveCatalog categoryDirectiveCatalog

    ) {

        this.claimDirectiveCatalog =
                Objects.requireNonNull(
                        claimDirectiveCatalog,
                        "LifeClaimNarrativeDirectiveCatalog는 필수입니다."
                );

        this.categoryDirectiveCatalog =
                Objects.requireNonNull(
                        categoryDirectiveCatalog,
                        "LifeCategoryNarrativeDirectiveCatalog는 필수입니다."
                );
    }

    public LifeClaimNarrativeBundle build(
            LifeClaimBundle claimBundle
    ) {

        Objects.requireNonNull(
                claimBundle,
                "LifeClaimBundle은 필수입니다."
        );

        Map<
                LifeInterpretationCategory,
                List<LifeClaimNarrativeInstruction>
                > grouped =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            LifeCategoryNarrativeDirective categoryDirective =
                    categoryDirectiveCatalog
                            .directiveOf(
                                    category
                            );

            List<LifeClaimNarrativeInstruction> instructions =
                    new ArrayList<>();

            for (
                    LifeClaim claim
                    : claimBundle.claimsFor(category)
            ) {

                LifeClaimNarrativeDirective claimDirective =
                        claimDirectiveCatalog
                                .directiveOf(
                                        claim.code()
                                );

                instructions.add(
                        new LifeClaimNarrativeInstruction(

                                category,

                                claim,

                                claimDirective,

                                categoryDirective
                        )
                );
            }

            grouped.put(
                    category,
                    List.copyOf(
                            instructions
                    )
            );
        }

        return new LifeClaimNarrativeBundle(
                grouped
        );
    }
}