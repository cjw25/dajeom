package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningOrigin;

import java.util.Objects;

/**
 * AI가 하나의 Meaning Claim을 어떻게 문장화했는지 보존하는 출력 단위.
 *
 * sourceClaimText와 originEcho는 모두
 * Java가 전달한 공식 Claim 정보를 AI가 그대로 되돌려주는
 * 검증용 Anchor다.
 *
 * AI는:
 * - claimId
 * - sourceClaimText
 * - origin
 * - authority
 *
 * 를 변경하거나 새로 판단할 수 없다.
 */
public record ConsultationClaimRender(

        String claimId,

        String sourceClaimText,

        ConsultationClaimOriginEcho originEcho,

        String text

) {

    public ConsultationClaimRender {

        claimId = requireText(
                claimId,
                "Claim Render claimId는 필수입니다."
        );

        sourceClaimText = requireText(
                sourceClaimText,
                "Claim Render sourceClaimText는 필수입니다."
        );

        Objects.requireNonNull(
                originEcho,
                "Claim Render originEcho는 필수입니다."
        );

        text = requireText(
                text,
                "Claim Render text는 필수입니다."
        );
    }

    public ConsultationMeaningOrigin origin() {

        return originEcho.origin();
    }

    public ConsultationMeaningAuthority authority() {

        return originEcho.authority();
    }

    private static String requireText(
            String value,
            String message
    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized = value.trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return normalized;
    }
}