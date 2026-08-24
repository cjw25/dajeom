package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningOrigin;

import java.util.Objects;

/**
 * AI가 하나의 Claim Render에서 되돌려주는
 * Meaning Origin / Authority Echo.
 *
 * 이 값은 AI가 새로 판단하는 메타데이터가 아니다.
 * Java가 입력으로 전달한 공식 Origin / Authority를
 * 그대로 복사해 되돌려주는 검증용 Echo다.
 */
public record ConsultationClaimOriginEcho(

        ConsultationMeaningOrigin origin,

        ConsultationMeaningAuthority authority

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

        ConsultationMeaningAuthority expected =
                expectedAuthority(
                        origin
                );

        if (
                authority != expected
        ) {

            throw new IllegalArgumentException(
                    "Claim Origin Echo의 Origin과 Authority가 일치하지 않습니다. origin="
                            + origin
                            + ", authority="
                            + authority
                            + ", expected="
                            + expected
            );
        }
    }

    public static ConsultationClaimOriginEcho from(
            ConsultationMeaningClaim claim
    ) {

        Objects.requireNonNull(
                claim,
                "Claim Origin Echo 생성에는 Meaning Claim이 필요합니다."
        );

        return new ConsultationClaimOriginEcho(
                claim.origin(),
                claim.authority()
        );
    }

    public boolean matches(
            ConsultationMeaningClaim claim
    ) {

        if (
                claim == null
        ) {

            return false;
        }

        return origin == claim.origin()
                && authority == claim.authority();
    }

    private static ConsultationMeaningAuthority expectedAuthority(
            ConsultationMeaningOrigin origin
    ) {

        return switch (origin) {

            case EXACT_TEN_GOD ->
                    ConsultationMeaningAuthority.EXACT_TEN_GOD_FACT;

            case FORMAL_PATTERN ->
                    ConsultationMeaningAuthority.FORMAL_PATTERN_FACT;

            case DETERMINISTIC_RULE ->
                    ConsultationMeaningAuthority.DETERMINISTIC_RULE_RESULT;

            case TIMELINE_CONTRACT ->
                    ConsultationMeaningAuthority.TIMELINE_CONTRACT_RESULT;

            case NATAL_CONTEXT ->
                    ConsultationMeaningAuthority.NATAL_CONTEXT_FACT;
        };
    }
}