package com.dajeom.domain;

import java.util.Objects;

/**
 * 특정 대운 / 세운 / 월운의 Claim 하나를
 * AI에게 전달하기 위한 완성된 내부 서술 단위.
 *
 * =================================================
 *
 * period
 *
 * 실제 기간.
 *
 * =================================================
 *
 * claim
 *
 * Java가 확정한 기간 LifeClaim.
 *
 * =================================================
 *
 * directive
 *
 * AI가 해당 Claim을 설명할 수 있는 의미 범위.
 */
public record PeriodLifeClaimNarrativeInstruction(

        LifePeriodKey period,

        PeriodLifeClaim claim,

        PeriodLifeClaimNarrativeDirective directive

) {

    public PeriodLifeClaimNarrativeInstruction {

        Objects.requireNonNull(
                period,
                "LifePeriodKey는 필수입니다."
        );

        Objects.requireNonNull(
                claim,
                "PeriodLifeClaim은 필수입니다."
        );

        Objects.requireNonNull(
                directive,
                "PeriodLifeClaimNarrativeDirective는 필수입니다."
        );

        if (
                !claim
                        .period()
                        .equals(period)
        ) {

            throw new IllegalArgumentException(
                    "Instruction의 period와 PeriodLifeClaim의 period가 일치하지 않습니다."
            );
        }

        if (
                directive.claimCode()
                        != claim.code()
        ) {

            throw new IllegalArgumentException(
                    "PeriodLifeClaim과 NarrativeDirective의 code가 일치하지 않습니다."
            );
        }
    }

    public LifePeriodType periodType() {

        return period.type();
    }
}