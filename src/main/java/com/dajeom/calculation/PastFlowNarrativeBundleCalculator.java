package com.dajeom.calculation;

import com.dajeom.domain.PastFlowAnalysis;
import com.dajeom.domain.PastFlowNarrativeBundle;
import com.dajeom.domain.PeriodLifeClaim;
import com.dajeom.domain.PeriodLifeClaimNarrativeDirective;
import com.dajeom.domain.PeriodLifeClaimNarrativeInstruction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PastFlowAnalysis를
 * AI Prompt 직전의 PastFlowNarrativeBundle로 변환한다.
 */
@Component
public class PastFlowNarrativeBundleCalculator {

    private final PeriodLifeClaimNarrativeDirectiveCatalog
            directiveCatalog;

    public PastFlowNarrativeBundleCalculator(
            PeriodLifeClaimNarrativeDirectiveCatalog directiveCatalog
    ) {

        this.directiveCatalog =
                Objects.requireNonNull(
                        directiveCatalog,
                        "PeriodLifeClaimNarrativeDirectiveCatalog는 필수입니다."
                );
    }

    public PastFlowNarrativeBundle build(
            PastFlowAnalysis analysis
    ) {

        Objects.requireNonNull(
                analysis,
                "PastFlowAnalysis는 필수입니다."
        );

        if (
                !analysis.hasClaims()
        ) {

            return new PastFlowNarrativeBundle(
                    List.of()
            );
        }

        List<PeriodLifeClaimNarrativeInstruction> instructions =
                new ArrayList<>();

        for (
                PeriodLifeClaim claim
                : analysis.orderedClaims()
        ) {

            PeriodLifeClaimNarrativeDirective directive =
                    directiveCatalog
                            .directiveOf(
                                    claim.code()
                            );

            instructions.add(
                    new PeriodLifeClaimNarrativeInstruction(
                            claim.period(),
                            claim,
                            directive
                    )
            );
        }

        return new PastFlowNarrativeBundle(
                instructions
        );
    }
}