package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningExpressionMode;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;

import java.util.Objects;

/**
 * 최종 사용자 Story까지 내려간 Claim의
 * Java 공식 provenance snapshot.
 *
 * 이 객체는 AI Echo에서 만들지 않는다.
 * 반드시 ConsultationMeaningClaim 원본에서 생성한다.
 */
public record ConsultationClaimRuntimeTrace(

        String claimId,

        String sourceCode,

        String sourceClaimText,

        ConsultationMeaningOrigin origin,

        ConsultationMeaningAuthority authority,

        ConsultationMeaningSpecificity specificity,

        ConsultationMeaningExpressionMode expressionMode,

        boolean strongExpressionAllowed,

        boolean qualificationRequired

) {

    public ConsultationClaimRuntimeTrace {

        claimId =
                requireText(
                        claimId,
                        "Claim Runtime Trace claimId는 필수입니다."
                );

        sourceCode =
                requireText(
                        sourceCode,
                        "Claim Runtime Trace sourceCode는 필수입니다."
                );

        sourceClaimText =
                requireText(
                        sourceClaimText,
                        "Claim Runtime Trace sourceClaimText는 필수입니다."
                );

        Objects.requireNonNull(
                origin,
                "Claim Runtime Trace origin은 필수입니다."
        );

        Objects.requireNonNull(
                authority,
                "Claim Runtime Trace authority는 필수입니다."
        );

        Objects.requireNonNull(
                specificity,
                "Claim Runtime Trace specificity는 필수입니다."
        );

        Objects.requireNonNull(
                expressionMode,
                "Claim Runtime Trace expressionMode는 필수입니다."
        );

        ConsultationMeaningOriginContract canonicalOriginContract =
                ConsultationMeaningOriginContract.resolve(
                        sourceCode,
                        specificity
                );

        if (
                canonicalOriginContract.origin() != origin
                        || canonicalOriginContract.authority() != authority
        ) {

            throw new IllegalArgumentException(
                    "Claim Runtime Trace의 Source provenance가 공식 Claim Contract와 일치하지 않습니다. "
                            + "claimId="
                            + claimId
                            + ", sourceCode="
                            + sourceCode
            );
        }

        ConsultationMeaningExpressionMode canonicalExpressionMode =
                ConsultationMeaningExpressionBoundaryContract
                        .of(
                                origin,
                                specificity,
                                strongExpressionAllowed,
                                qualificationRequired
                        )
                        .expressionMode();

        if (
                canonicalExpressionMode != expressionMode
        ) {

            throw new IllegalArgumentException(
                    "Claim Runtime Trace의 expressionMode가 공식 Expression Boundary와 일치하지 않습니다. "
                            + "claimId="
                            + claimId
                            + ", actual="
                            + expressionMode
                            + ", expected="
                            + canonicalExpressionMode
            );
        }
    }

    /**
     * AI Echo가 아니라 Java 공식 MeaningClaim에서 생성한다.
     */
    public static ConsultationClaimRuntimeTrace from(
            ConsultationMeaningClaim claim
    ) {

        Objects.requireNonNull(
                claim,
                "Claim Runtime Trace 생성에는 Meaning Claim이 필요합니다."
        );

        return new ConsultationClaimRuntimeTrace(
                claim.claimId(),
                claim.sourceCode(),
                claim.text(),
                claim.origin(),
                claim.authority(),
                claim.specificity(),
                claim.expressionMode(),
                claim.strongExpressionAllowed(),
                claim.qualificationRequired()
        );
    }

    private static String requireText(
            String value,
            String message
    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}