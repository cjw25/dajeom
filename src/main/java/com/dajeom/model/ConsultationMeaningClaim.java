package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningExpressionMode;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;

import java.util.Objects;

/**
 * AI가 표현할 수 있는 최소 의미 단위.
 *
 * =================================================
 * 공식 Meaning
 * =================================================
 *
 * text는 Java deterministic 판단에서
 * 직접 내려온 확정 의미다.
 *
 * AI는 Claim의 의미를
 * 변경하거나 확장할 수 없다.
 *
 * =================================================
 * Origin / Authority
 * =================================================
 *
 * Claim 생성 순간:
 *
 * - origin
 * - authority
 *
 * 를 고정한다.
 *
 * =================================================
 * Specificity
 * =================================================
 *
 * Origin마다 허용되는:
 *
 * - STRUCTURE
 * - TENDENCY
 * - DOMAIN
 * - TIMING
 *
 * 경계를 검증한다.
 *
 * =================================================
 * Expression Boundary
 * =================================================
 *
 * 기존:
 *
 * strongExpressionAllowed
 * qualificationRequired
 *
 * 두 값을 이제 공식:
 *
 * ConsultationMeaningExpressionBoundaryContract
 *
 * 로 다시 검증한다.
 *
 * 따라서 Claim 하나는 반드시:
 *
 * DIRECT
 * QUALIFIED
 * STRONG_DIRECT
 * STRONG_QUALIFIED
 *
 * 중 하나의 표현모드를 갖는다.
 *
 * =================================================
 * NATAL_CONTEXT
 * =================================================
 *
 * NATAL_CONTEXT는
 * 강한 확정 Meaning Source가 아니므로:
 *
 * NATAL_CONTEXT
 * +
 * STRONG_DIRECT
 *
 * 또는:
 *
 * NATAL_CONTEXT
 * +
 * STRONG_QUALIFIED
 *
 * Claim은 생성 단계에서 실패한다.
 */
public record ConsultationMeaningClaim(

        String claimId,

        String sourceCode,

        String text,

        ConsultationMeaningSpecificity specificity,

        boolean strongExpressionAllowed,

        boolean qualificationRequired,

        ConsultationMeaningOriginContract originContract

) {

    /**
     * =================================================
     * 기존 생성자 호환
     * =================================================
     *
     * 기존 Narrative Plan 코드는
     * 그대로 사용할 수 있다.
     */
    public ConsultationMeaningClaim(

            String claimId,

            String sourceCode,

            String text,

            ConsultationMeaningSpecificity specificity,

            boolean strongExpressionAllowed,

            boolean qualificationRequired

    ) {

        this(

                claimId,

                sourceCode,

                text,

                specificity,

                strongExpressionAllowed,

                qualificationRequired,

                ConsultationMeaningOriginContract.resolve(

                        sourceCode,

                        specificity
                )
        );
    }

    public ConsultationMeaningClaim {

        claimId =
                requireText(
                        claimId,
                        "Meaning Claim ID는 필수입니다."
                );

        sourceCode =
                requireText(
                        sourceCode,
                        "Meaning Claim Source Code는 필수입니다."
                );

        text =
                requireText(
                        text,
                        "Meaning Claim Text는 필수입니다."
                );

        Objects.requireNonNull(
                specificity,
                "Meaning Claim Specificity는 필수입니다."
        );

        Objects.requireNonNull(
                originContract,
                "Meaning Claim Origin Contract는 필수입니다."
        );

        /*
         * =================================================
         * Source → Origin Contract 위조 차단
         * =================================================
         */

        ConsultationMeaningOriginContract canonicalOriginContract =
                ConsultationMeaningOriginContract.resolve(

                        sourceCode,

                        specificity
                );

        if (
                !originContract.equals(
                        canonicalOriginContract
                )
        ) {

            throw new IllegalArgumentException(
                    "Meaning Claim의 명시 Origin Contract가 "
                            + "공식 Source 분류와 일치하지 않습니다. "
                            + "claimId="
                            + claimId
                            + ", sourceCode="
                            + sourceCode
                            + ", actual="
                            + originContract
                            + ", expected="
                            + canonicalOriginContract
            );
        }

        /*
         * =================================================
         * Origin → Specificity
         * =================================================
         */

        originContract
                .requireSpecificity(
                        specificity
                );

        /*
         * =================================================
         * EVENT 하드 차단
         * =================================================
         */

        if (
                specificity.eventLevel()
        ) {

            throw new IllegalArgumentException(
                    "별도의 deterministic 사건 Contract 없이 "
                            + "EVENT 수준 Meaning Claim을 생성할 수 없습니다. claimId="
                            + claimId
            );
        }

        /*
         * =================================================
         * Expression Boundary
         * =================================================
         *
         * boolean 두 개를 독립값으로 두지 않고
         * 공식 Expression Mode 조합으로 검증한다.
         */

        ConsultationMeaningExpressionBoundaryContract.of(

                originContract.origin(),

                specificity,

                strongExpressionAllowed,

                qualificationRequired
        );
    }

    public ConsultationMeaningOrigin origin() {

        return originContract
                .origin();
    }

    public ConsultationMeaningAuthority authority() {

        return originContract
                .authority();
    }

    public ConsultationMeaningSpecificityContract specificityContract() {

        return originContract
                .specificityContract();
    }

    /**
     * 현재 Claim의 공식 Expression Boundary.
     */
    public ConsultationMeaningExpressionBoundaryContract
    expressionBoundaryContract() {

        return ConsultationMeaningExpressionBoundaryContract.of(

                origin(),

                specificity,

                strongExpressionAllowed,

                qualificationRequired
        );
    }

    public ConsultationMeaningExpressionMode expressionMode() {

        return expressionBoundaryContract()
                .expressionMode();
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