package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;

import java.util.Objects;

/**
 * AI가 하나의 Meaning Claim을
 * 어떻게 문장화했는지 보존하는 출력 단위.
 *
 * =================================================
 * 공식 Anchor
 * =================================================
 *
 * sourceClaimText
 *
 * +
 *
 * originEcho
 *
 * 는 모두 Java가 전달한
 * 공식 Claim 정보를 AI가 그대로 되돌려주는
 * 검증용 Anchor다.
 *
 * =================================================
 * AI가 변경할 수 없는 값
 * =================================================
 *
 * - claimId
 * - sourceClaimText
 * - origin
 * - authority
 * - specificity
 * - strongExpressionAllowed
 * - qualificationRequired
 *
 * =================================================
 * AI가 작성할 수 있는 값
 * =================================================
 *
 * text
 *
 * 단,
 * sourceClaimText의 Meaning 경계를
 * 벗어날 수 없다.
 */
public record ConsultationClaimRender(

        String claimId,

        String sourceClaimText,

        ConsultationClaimOriginEcho originEcho,

        String text

) {

    public ConsultationClaimRender {

        claimId =
                requireText(
                        claimId,
                        "Claim Render claimId는 필수입니다."
                );

        sourceClaimText =
                requireText(
                        sourceClaimText,
                        "Claim Render sourceClaimText는 필수입니다."
                );

        Objects.requireNonNull(
                originEcho,
                "Claim Render originEcho는 필수입니다."
        );

        text =
                requireText(
                        text,
                        "Claim Render text는 필수입니다."
                );
    }

    public ConsultationMeaningOrigin origin() {

        return originEcho
                .origin();
    }

    public ConsultationMeaningAuthority authority() {

        return originEcho
                .authority();
    }

    public ConsultationMeaningSpecificity specificity() {

        return originEcho
                .specificity();
    }

    public boolean strongExpressionAllowed() {

        return originEcho
                .strongExpressionAllowed();
    }

    public boolean qualificationRequired() {

        return originEcho
                .qualificationRequired();
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