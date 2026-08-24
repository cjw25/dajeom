package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;
import com.dajeom.type.ConsultationNarrativePriority;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * AI Renderer에 전달하는 하나의 독립 서술 단위.
 *
 * 서로 다른 Packet은 서로의 원인/결과/순서/강화/약화가 아니다.
 * Packet 내부에도 Java가 이미 확정한 Claim만 존재한다.
 *
 * =================================================
 * Origin Isolation
 * =================================================
 *
 * 하나의 Packet에는 정확히 하나의 Meaning Origin / Authority만
 * 존재할 수 있다.
 *
 * =================================================
 * Specificity Isolation
 * =================================================
 *
 * 하나의 Packet에는 정확히 하나의 Specificity만 존재할 수 있다.
 *
 * 같은 Origin이라도:
 *
 * STRUCTURE Claim
 * +
 * TENDENCY Claim
 *
 * 을 한 Packet에 섞지 않는다.
 *
 * 서로 다른 현실 구체성을 한 번에 AI에 넣으면
 * AI가 구조 사실을 성향으로 확대하거나
 * 성향을 생활영역/시기 판단으로 올릴 여지가 생기기 때문이다.
 *
 * =================================================
 * 예
 * =================================================
 *
 * EXACT_TEN_GOD / TENDENCY Packet
 * -> 허용
 *
 * FORMAL_PATTERN / STRUCTURE Packet
 * -> 허용
 *
 * DETERMINISTIC_RULE / DOMAIN Packet
 * -> 허용
 *
 * TIMELINE_CONTRACT / TIMING Packet
 * -> 허용
 *
 * EXACT_TEN_GOD / TIMING
 * -> Claim 생성 단계에서 이미 금지
 */
public record ConsultationMeaningPacket(

        String packetId,

        ConsultationCategory category,

        ConsultationNarrativePriority priority,

        String titleHint,

        List<ConsultationMeaningClaim> claims,

        List<String> renderingContexts,

        List<String> forbiddenMeanings,

        int targetMinChars,

        int targetMaxChars

) {

    public ConsultationMeaningPacket {

        packetId =
                requireText(
                        packetId,
                        "Meaning Packet ID는 필수입니다."
                );

        Objects.requireNonNull(
                category,
                "Meaning Packet Category는 필수입니다."
        );

        Objects.requireNonNull(
                priority,
                "Meaning Packet Priority는 필수입니다."
        );

        titleHint =
                requireText(
                        titleHint,
                        "Meaning Packet titleHint는 필수입니다."
                );

        claims =
                claims == null
                        ? List.of()
                        : claims
                        .stream()
                        .map(
                                claim ->
                                        Objects.requireNonNull(
                                                claim,
                                                "Meaning Claim은 null일 수 없습니다."
                                        )
                        )
                        .toList();

        if (
                claims.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Meaning Packet에는 최소 하나의 Claim이 필요합니다."
            );
        }

        LinkedHashSet<String> claimIds =
                new LinkedHashSet<>();

        for (
                ConsultationMeaningClaim claim
                : claims
        ) {

            if (
                    !claimIds.add(
                            claim.claimId()
                    )
            ) {

                throw new IllegalArgumentException(
                        "Meaning Packet Claim ID가 중복되었습니다: "
                                + claim.claimId()
                );
            }
        }

        validateSingleOrigin(
                packetId,
                claims
        );

        validateSingleSpecificity(
                packetId,
                claims
        );

        renderingContexts =
                normalizeTexts(
                        renderingContexts
                );

        forbiddenMeanings =
                normalizeTexts(
                        forbiddenMeanings
                );

        if (
                targetMinChars < 0
                        || targetMaxChars < 1
                        || targetMaxChars < targetMinChars
        ) {

            throw new IllegalArgumentException(
                    "Meaning Packet 글자수 범위가 올바르지 않습니다. min="
                            + targetMinChars
                            + ", max="
                            + targetMaxChars
            );
        }
    }

    public List<String> allowedClaimIds() {

        return claims
                .stream()
                .map(
                        ConsultationMeaningClaim::claimId
                )
                .toList();
    }

    public boolean allowsClaim(
            String claimId
    ) {

        return claimId != null
                && allowedClaimIds()
                .contains(
                        claimId
                );
    }

    /**
     * Packet은 단일 Origin만 허용하므로
     * 첫 Claim의 Origin이 공식 Packet Origin이다.
     */
    public ConsultationMeaningOrigin origin() {

        return claims
                .get(0)
                .origin();
    }

    /**
     * Packet은 단일 Authority만 허용하므로
     * 첫 Claim의 Authority가 공식 Packet Authority다.
     */
    public ConsultationMeaningAuthority authority() {

        return claims
                .get(0)
                .authority();
    }

    public ConsultationMeaningOriginContract originContract() {

        return claims
                .get(0)
                .originContract();
    }

    /**
     * Packet은 단일 Specificity만 허용하므로
     * 첫 Claim의 Specificity가 공식 Packet Specificity다.
     */
    public ConsultationMeaningSpecificity specificity() {

        return claims
                .get(0)
                .specificity();
    }

    public ConsultationMeaningSpecificityContract specificityContract() {

        return claims
                .get(0)
                .specificityContract();
    }

    private static void validateSingleOrigin(

            String packetId,

            List<ConsultationMeaningClaim> claims

    ) {

        ConsultationMeaningOriginContract canonical =
                claims
                        .get(0)
                        .originContract();

        for (
                ConsultationMeaningClaim claim
                : claims
        ) {

            if (
                    !canonical.equals(
                            claim.originContract()
                    )
            ) {

                throw new IllegalArgumentException(
                        "하나의 Meaning Packet에는 서로 다른 Origin/Authority Claim을 섞을 수 없습니다. "
                                + "packetId="
                                + packetId
                                + ", first="
                                + canonical
                                + ", mixedClaim="
                                + claim.claimId()
                                + ", mixed="
                                + claim.originContract()
                );
            }
        }
    }

    private static void validateSingleSpecificity(

            String packetId,

            List<ConsultationMeaningClaim> claims

    ) {

        ConsultationMeaningSpecificity canonical =
                claims
                        .get(0)
                        .specificity();

        for (
                ConsultationMeaningClaim claim
                : claims
        ) {

            if (
                    claim.specificity()
                            != canonical
            ) {

                throw new IllegalArgumentException(
                        "하나의 Meaning Packet에는 서로 다른 Specificity Claim을 섞을 수 없습니다. "
                                + "packetId="
                                + packetId
                                + ", first="
                                + canonical
                                + ", mixedClaim="
                                + claim.claimId()
                                + ", mixed="
                                + claim.specificity()
                );
            }
        }
    }

    private static List<String> normalizeTexts(
            List<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        for (
                String raw
                : source
        ) {

            if (
                    raw == null
            ) {

                continue;
            }

            String value =
                    raw.trim();

            if (
                    !value.isBlank()
            ) {

                result.add(
                        value
                );
            }
        }

        return List.copyOf(
                result
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