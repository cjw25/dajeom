package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningExpressionMode;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;

import java.util.Objects;

/**
 * AI가 하나의 Claim Render에서 되돌려주는
 * 공식 Claim Boundary Echo.
 *
 * =================================================
 * AI가 그대로 되돌려야 하는 값
 * =================================================
 *
 * - origin
 * - authority
 * - specificity
 * - strongExpressionAllowed
 * - qualificationRequired
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 위 값은 AI 판단값이 아니다.
 *
 * Java 공식 Claim 경계를
 * 그대로 복사하는 검증용 Echo다.
 *
 * =================================================
 * 현재 검증 단계
 * =================================================
 *
 * 1.
 * Origin ↔ Authority
 *
 * 2.
 * Origin ↔ Specificity
 *
 * 3.
 * Origin / Specificity ↔ Expression Mode
 *
 * 4.
 * 실제 Java 원본 Claim과의 완전 동일성은
 * ConsultationClaimCoverageAnalyzer가 검증한다.
 */
public record ConsultationClaimOriginEcho(

        ConsultationMeaningOrigin origin,

        ConsultationMeaningAuthority authority,

        ConsultationMeaningSpecificity specificity,

        boolean strongExpressionAllowed,

        boolean qualificationRequired

) {

    public ConsultationClaimOriginEcho {

        Objects.requireNonNull(
                origin,
                "Claim Origin Echo origin은 필수입니다."
        );

        Objects.requireNonNull(
                authority,
                "Claim Origin Echo authority는 필수입니다."
        );

        Objects.requireNonNull(
                specificity,
                "Claim Origin Echo specificity는 필수입니다."
        );

        /*
         * =================================================
         * Origin ↔ Authority
         * =================================================
         */

        ConsultationMeaningAuthority expectedAuthority =
                expectedAuthority(
                        origin
                );

        if (
                authority
                        != expectedAuthority
        ) {

            throw new IllegalArgumentException(
                    "Claim Origin Echo의 Origin과 Authority가 일치하지 않습니다. "
                            + "origin="
                            + origin
                            + ", authority="
                            + authority
                            + ", expected="
                            + expectedAuthority
            );
        }

        /*
         * =================================================
         * 전체 Expression Boundary
         * =================================================
         *
         * 여기서:
         *
         * - Origin ↔ Specificity
         * - NATAL_CONTEXT strong 금지
         *
         * 까지 동시에 검증한다.
         *
         * 따라서 AI가:
         *
         * NATAL_CONTEXT
         * strongExpressionAllowed=true
         *
         * 로 반환하면
         * Coverage까지 가기 전
         * Echo 생성 자체가 실패한다.
         */

        ConsultationMeaningExpressionBoundaryContract.of(

                origin,

                specificity,

                strongExpressionAllowed,

                qualificationRequired
        );
    }

    /**
     * Java 공식 Claim에서
     * 정확한 Echo 생성.
     */
    public static ConsultationClaimOriginEcho from(

            ConsultationMeaningClaim claim

    ) {

        Objects.requireNonNull(
                claim,
                "Claim Origin Echo 생성에는 Meaning Claim이 필요합니다."
        );

        return new ConsultationClaimOriginEcho(

                claim.origin(),

                claim.authority(),

                claim.specificity(),

                claim.strongExpressionAllowed(),

                claim.qualificationRequired()
        );
    }

    /**
     * AI Echo가 Java 원본 Claim의
     * 전체 메타데이터 경계를 정확히 보존했는지.
     */
    public boolean matches(

            ConsultationMeaningClaim claim

    ) {

        if (
                claim == null
        ) {

            return false;
        }

        return origin
                == claim.origin()

                && authority
                == claim.authority()

                && specificity
                == claim.specificity()

                && strongExpressionAllowed
                == claim.strongExpressionAllowed()

                && qualificationRequired
                == claim.qualificationRequired();
    }

    public ConsultationMeaningExpressionBoundaryContract
    expressionBoundaryContract() {

        return ConsultationMeaningExpressionBoundaryContract.of(

                origin,

                specificity,

                strongExpressionAllowed,

                qualificationRequired
        );
    }

    public ConsultationMeaningExpressionMode expressionMode() {

        return expressionBoundaryContract()
                .expressionMode();
    }

    private static ConsultationMeaningAuthority expectedAuthority(

            ConsultationMeaningOrigin origin

    ) {

        return switch (
                origin
                ) {

            case EXACT_TEN_GOD ->
                    ConsultationMeaningAuthority
                            .EXACT_TEN_GOD_FACT;

            case FORMAL_PATTERN ->
                    ConsultationMeaningAuthority
                            .FORMAL_PATTERN_FACT;

            case DETERMINISTIC_RULE ->
                    ConsultationMeaningAuthority
                            .DETERMINISTIC_RULE_RESULT;

            case TIMELINE_CONTRACT ->
                    ConsultationMeaningAuthority
                            .TIMELINE_CONTRACT_RESULT;

            case NATAL_CONTEXT ->
                    ConsultationMeaningAuthority
                            .NATAL_CONTEXT_FACT;
        };
    }
}