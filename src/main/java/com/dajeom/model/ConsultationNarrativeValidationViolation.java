package com.dajeom.model;

import com.dajeom.type.ConsultationNarrativeViolationType;

import java.util.Objects;

/**
 * 하나의 Narrative Packet 검증 위반.
 */
public record ConsultationNarrativeValidationViolation(

        ConsultationNarrativeViolationType type,

        String packetId,

        String detail

) {

    public ConsultationNarrativeValidationViolation {

        Objects.requireNonNull(
                type,
                "Narrative Violation Type은 필수입니다."
        );

        packetId =
                requireText(
                        packetId,
                        "Narrative Violation packetId는 필수입니다."
                );

        detail =
                requireText(
                        detail,
                        "Narrative Violation detail은 필수입니다."
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