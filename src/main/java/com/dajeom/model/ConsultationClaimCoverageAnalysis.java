package com.dajeom.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 하나의 Packet에서
 * Java가 요구한 Claim이
 * AI 출력에 정확히 대응되었는지 검증한 결과.
 *
 * =================================================
 * 현재 검증 Anchor
 * =================================================
 *
 * - packetId
 * - claimId
 * - sourceClaimText
 * - origin
 * - authority
 * - specificity
 * - strongExpressionAllowed
 * - qualificationRequired
 *
 * =================================================
 *
 * 즉 AI가 Meaning 자체뿐 아니라
 * 표현 강도 경계까지 바꿔치기할 수 없다.
 */
public record ConsultationClaimCoverageAnalysis(

        String packetId,

        boolean packetIdMatched,

        int expectedClaimCount,

        int renderedClaimCount,

        List<String> missingClaimIds,

        List<String> unexpectedClaimIds,

        List<String> duplicateClaimIds,

        List<String> sourceMismatchClaimIds,

        List<String> originMismatchClaimIds,

        List<String> authorityMismatchClaimIds,

        List<String> specificityMismatchClaimIds,

        List<String> strongExpressionMismatchClaimIds,

        List<String> qualificationMismatchClaimIds,

        List<String> repeatedTextClaimIds

) {

    public ConsultationClaimCoverageAnalysis {

        packetId =
                requireText(
                        packetId,
                        "Claim Coverage packetId는 필수입니다."
                );

        if (
                expectedClaimCount < 1
        ) {

            throw new IllegalArgumentException(
                    "Claim Coverage expectedClaimCount는 1 이상이어야 합니다."
            );
        }

        if (
                renderedClaimCount < 0
        ) {

            throw new IllegalArgumentException(
                    "Claim Coverage renderedClaimCount는 0 이상이어야 합니다."
            );
        }

        missingClaimIds =
                copy(
                        missingClaimIds
                );

        unexpectedClaimIds =
                copy(
                        unexpectedClaimIds
                );

        duplicateClaimIds =
                copy(
                        duplicateClaimIds
                );

        sourceMismatchClaimIds =
                copy(
                        sourceMismatchClaimIds
                );

        originMismatchClaimIds =
                copy(
                        originMismatchClaimIds
                );

        authorityMismatchClaimIds =
                copy(
                        authorityMismatchClaimIds
                );

        specificityMismatchClaimIds =
                copy(
                        specificityMismatchClaimIds
                );

        strongExpressionMismatchClaimIds =
                copy(
                        strongExpressionMismatchClaimIds
                );

        qualificationMismatchClaimIds =
                copy(
                        qualificationMismatchClaimIds
                );

        repeatedTextClaimIds =
                copy(
                        repeatedTextClaimIds
                );
    }

    public boolean valid() {

        return packetIdMatched

                && expectedClaimCount
                == renderedClaimCount

                && missingClaimIds
                .isEmpty()

                && unexpectedClaimIds
                .isEmpty()

                && duplicateClaimIds
                .isEmpty()

                && sourceMismatchClaimIds
                .isEmpty()

                && originMismatchClaimIds
                .isEmpty()

                && authorityMismatchClaimIds
                .isEmpty()

                && specificityMismatchClaimIds
                .isEmpty()

                && strongExpressionMismatchClaimIds
                .isEmpty()

                && qualificationMismatchClaimIds
                .isEmpty()

                && repeatedTextClaimIds
                .isEmpty();
    }

    public String summary() {

        if (
                valid()
        ) {

            return "";
        }

        List<String> details =
                new java.util.ArrayList<>();

        if (
                !packetIdMatched
        ) {

            details.add(
                    "packetId 불일치"
            );
        }

        if (
                expectedClaimCount
                        != renderedClaimCount
        ) {

            details.add(
                    "Claim 수 불일치 expected="
                            + expectedClaimCount
                            + ", actual="
                            + renderedClaimCount
            );
        }

        addDetail(
                details,
                "누락 Claim",
                missingClaimIds
        );

        addDetail(
                details,
                "허용되지 않은 Claim",
                unexpectedClaimIds
        );

        addDetail(
                details,
                "중복 Claim",
                duplicateClaimIds
        );

        addDetail(
                details,
                "Source 원문 불일치",
                sourceMismatchClaimIds
        );

        addDetail(
                details,
                "Meaning Origin 불일치",
                originMismatchClaimIds
        );

        addDetail(
                details,
                "Meaning Authority 불일치",
                authorityMismatchClaimIds
        );

        addDetail(
                details,
                "Meaning Specificity 불일치",
                specificityMismatchClaimIds
        );

        addDetail(
                details,
                "strongExpressionAllowed 불일치",
                strongExpressionMismatchClaimIds
        );

        addDetail(
                details,
                "qualificationRequired 불일치",
                qualificationMismatchClaimIds
        );

        addDetail(
                details,
                "서로 다른 Claim의 동일 문장 반복",
                repeatedTextClaimIds
        );

        return details
                .stream()
                .collect(
                        Collectors.joining(
                                ", "
                        )
                );
    }

    private static void addDetail(

            List<String> details,

            String label,

            List<String> values

    ) {

        if (
                !values.isEmpty()
        ) {

            details.add(
                    label
                            + "="
                            + String.join(
                            "|",
                            values
                    )
            );
        }
    }

    private static List<String> copy(
            List<String> source
    ) {

        return source == null
                ? List.of()
                : List.copyOf(
                source
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