package com.dajeom.domain;

import java.util.Objects;

/**
 * 특정 카테고리에서
 * 하나의 LifeClaim을 설명하기 위한 완성된 AI 입력 단위.
 */
public record LifeClaimNarrativeInstruction(

        LifeInterpretationCategory category,

        LifeClaim claim,

        LifeClaimNarrativeDirective claimDirective,

        LifeCategoryNarrativeDirective categoryDirective

) {

    public LifeClaimNarrativeInstruction {

        Objects.requireNonNull(
                category,
                "카테고리는 필수입니다."
        );

        Objects.requireNonNull(
                claim,
                "LifeClaim은 필수입니다."
        );

        Objects.requireNonNull(
                claimDirective,
                "LifeClaimNarrativeDirective는 필수입니다."
        );

        Objects.requireNonNull(
                categoryDirective,
                "LifeCategoryNarrativeDirective는 필수입니다."
        );

        if (
                claimDirective.claimCode()
                        != claim.code()
        ) {

            throw new IllegalArgumentException(
                    "LifeClaim과 ClaimDirective의 code가 일치하지 않습니다."
            );
        }

        if (
                categoryDirective.category()
                        != category
        ) {

            throw new IllegalArgumentException(
                    "category와 CategoryDirective가 일치하지 않습니다."
            );
        }
    }
}