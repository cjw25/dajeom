package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;

import java.util.Objects;

/**
 * AI가 표현할 수 있는 최소 의미 단위.
 *
 * text는 Java deterministic 판단에서 직접 내려온 확정 의미다.
 * AI는 claimId가 가리키는 text의 의미를 바꾸거나 확장할 수 없다.
 *
 * =================================================
 * Origin Contract
 * =================================================
 *
 * Claim 생성 순간 다음을 함께 고정한다.
 *
 * - 어디서 내려온 의미인지
 * - 어떤 공식 Authority인지
 *
 * 서로 다른 Origin의 Claim을 같은 의미처럼 섞는 것은
 * Packet 단계에서 금지한다.
 *
 * =================================================
 * Specificity Contract
 * =================================================
 *
 * Origin이 허용하지 않는 현실 구체성은
 * AI에 도달하기 전에 Claim 생성 자체를 실패시킨다.
 *
 * 예:
 *
 * EXACT_TEN_GOD + TIMING
 * -> 금지
 *
 * NATAL_CONTEXT + DOMAIN
 * -> 금지
 *
 * NATAL_CONTEXT + TIMING
 * -> 금지
 *
 * TIMELINE_CONTRACT + TENDENCY
 * -> 금지
 *
 * 현재 모델에는 EVENT Specificity 자체가 없으므로
 * 별도의 deterministic 사건 계산 없이
 * 현실 사건 수준으로 승격할 수 없다.
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
     * 기존 Narrative Plan 생성 코드 호환 생성자.
     *
     * sourceCode + specificity에서
     * 공식 Origin Contract를 즉시 결정한다.
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
         * 명시적으로 Origin Contract를 넣은 경우에도
         * 현재 sourceCode / specificity와 충돌하면 허용하지 않는다.
         * =================================================
         */

        ConsultationMeaningOriginContract canonical =
                ConsultationMeaningOriginContract.resolve(
                        sourceCode,
                        specificity
                );

        if (
                !originContract.equals(
                        canonical
                )
        ) {

            throw new IllegalArgumentException(
                    "Meaning Claim의 명시 Origin Contract가 공식 Source 분류와 일치하지 않습니다. "
                            + "claimId="
                            + claimId
                            + ", sourceCode="
                            + sourceCode
                            + ", actual="
                            + originContract
                            + ", expected="
                            + canonical
            );
        }

        /*
         * =================================================
         * Origin → Specificity 하드 경계
         * =================================================
         *
         * 이 검증은 AI 이전에 수행된다.
         */

        originContract.requireSpecificity(
                specificity
        );

        /*
         * =================================================
         * EVENT 수준은 현재 공식 모델에 존재하지 않는다.
         * =================================================
         */

        if (
                specificity.eventLevel()
        ) {

            throw new IllegalArgumentException(
                    "별도의 deterministic 사건 Contract 없이 EVENT 수준 Meaning Claim을 생성할 수 없습니다. claimId="
                            + claimId
            );
        }
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