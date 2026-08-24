package com.dajeom.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 하나의 Narrative Packet에 대한
 * Java 의미 경계 검증 결과.
 */
public record ConsultationNarrativeValidationResult(

        String packetId,

        List<ConsultationNarrativeValidationViolation> violations

) {

    public ConsultationNarrativeValidationResult {

        packetId =
                requireText(
                        packetId,
                        "Narrative Validation packetId는 필수입니다."
                );

        violations =
                violations == null
                        ? List.of()
                        : List.copyOf(
                        violations
                );

        for (
                ConsultationNarrativeValidationViolation violation
                : violations
        ) {

            Objects.requireNonNull(
                    violation,
                    "Narrative Validation Violation은 null일 수 없습니다."
            );

            if (
                    !packetId.equals(
                            violation.packetId()
                    )
            ) {

                throw new IllegalArgumentException(
                        "Narrative Validation Result와 Violation의 packetId가 일치하지 않습니다."
                );
            }
        }
    }

    public boolean valid() {

        return violations.isEmpty();
    }

    public String summary() {

        if (
                valid()
        ) {

            return "";
        }

        return violations
                .stream()
                .map(
                        violation ->
                                violation
                                        .type()
                                        .name()
                                        + ": "
                                        + violation
                                        .detail()
                )
                .collect(
                        Collectors.joining(
                                ", "
                        )
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