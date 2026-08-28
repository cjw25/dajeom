package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningExpressionMode;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;
import com.dajeom.type.ConsultationNarrativePriority;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * AI Renderer에 전달하는
 * 하나의 독립 서술 단위.
 *
 * =================================================
 * Packet Isolation
 * =================================================
 *
 * 서로 다른 Packet은 서로의:
 *
 * - 원인
 * - 결과
 * - 순서
 * - 강화
 * - 약화
 *
 * 가 아니다.
 *
 * =================================================
 * 135-1 Timeline Year
 * =================================================
 *
 * timelineYear
 *
 * =
 * Java Timeline provenance가 직접 전달한
 * 공식 명리연도.
 *
 * =================================================
 * 135-4 Timeline Stage
 * =================================================
 *
 * timelineStage
 *
 * =
 * ConsultationTimelineUserMeaningPolicy가
 * 이미 결정한 공식 TimelineStage.
 *
 * 문자열:
 *
 * - ruleCode
 * - titleHint
 * - userMeaning
 *
 * 을 다시 파싱하여 Stage를 추론하지 않는다.
 *
 * timelineStage는
 * CURRENT_TIMELINE의 표시 계층에만 사용한다.
 *
 * PAST/FUTURE의 TIMELINE_YEAR Packet은
 * timelineYear는 존재하지만
 * timelineStage는 null일 수 있다.
 *
 * 동일 Stage는 표시 Group일 뿐이며
 * Packet 간 의미 합성을 허용하지 않는다.
 */
public record ConsultationMeaningPacket(

        String packetId,

        ConsultationCategory category,

        ConsultationNarrativePriority priority,

        String titleHint,

        Integer timelineYear,

        ConsultationTimelineUserMeaningPolicy.TimelineStage timelineStage,

        List<ConsultationMeaningClaim> claims,

        List<String> renderingContexts,

        List<String> forbiddenMeanings,

        int targetMinChars,

        int targetMaxChars

) {

    /**
     * 기존 일반 Packet 생성자.
     */
    public ConsultationMeaningPacket(

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

        this(
                packetId,
                category,
                priority,
                titleHint,
                null,
                null,
                claims,
                renderingContexts,
                forbiddenMeanings,
                targetMinChars,
                targetMaxChars
        );
    }

    /**
     * 135-1 호환 생성자.
     *
     * 연도만 존재하고 Stage는 없는
     * Timeline Year Packet 등에 사용 가능하다.
     */
    public ConsultationMeaningPacket(

            String packetId,

            ConsultationCategory category,

            ConsultationNarrativePriority priority,

            String titleHint,

            Integer timelineYear,

            List<ConsultationMeaningClaim> claims,

            List<String> renderingContexts,

            List<String> forbiddenMeanings,

            int targetMinChars,

            int targetMaxChars

    ) {

        this(
                packetId,
                category,
                priority,
                titleHint,
                timelineYear,
                null,
                claims,
                renderingContexts,
                forbiddenMeanings,
                targetMinChars,
                targetMaxChars
        );
    }

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

        if (
                timelineYear != null
                        &&
                        (
                                timelineYear < 1
                                        ||
                                        timelineYear > 9_999
                        )
        ) {

            throw new IllegalArgumentException(
                    "Meaning Packet timelineYear 범위가 올바르지 않습니다: "
                            + timelineYear
            );
        }

        /*
         * Stage만 단독으로 존재할 수는 없다.
         */
        if (
                timelineStage != null
                        &&
                        timelineYear == null
        ) {

            throw new IllegalArgumentException(
                    "Meaning Packet timelineStage가 존재하려면 timelineYear도 필요합니다. packetId="
                            + packetId
                            + ", timelineStage="
                            + timelineStage
            );
        }

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

        validateSingleExpressionBoundary(
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

                        ||

                        targetMaxChars < 1

                        ||

                        targetMaxChars < targetMinChars
        ) {

            throw new IllegalArgumentException(
                    "Meaning Packet 글자수 범위가 올바르지 않습니다. min="
                            + targetMinChars
                            + ", max="
                            + targetMaxChars
            );
        }
    }

    public boolean hasTimelineYear() {

        return timelineYear != null;
    }

    public boolean hasTimelineStage() {

        return timelineStage != null;
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
                &&
                allowedClaimIds()
                        .contains(
                                claimId
                        );
    }

    public ConsultationMeaningOrigin origin() {

        return claims
                .get(0)
                .origin();
    }

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

    public ConsultationMeaningExpressionBoundaryContract
    expressionBoundaryContract() {

        return claims
                .get(0)
                .expressionBoundaryContract();
    }

    public ConsultationMeaningExpressionMode expressionMode() {

        return expressionBoundaryContract()
                .expressionMode();
    }

    public boolean strongExpressionAllowed() {

        return expressionBoundaryContract()
                .strongExpressionAllowed();
    }

    public boolean qualificationRequired() {

        return expressionBoundaryContract()
                .qualificationRequired();
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
                        "하나의 Meaning Packet에는 서로 다른 Origin/Authority Claim을 섞을 수 없습니다. packetId="
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
                        "하나의 Meaning Packet에는 서로 다른 Specificity Claim을 섞을 수 없습니다. packetId="
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

    private static void validateSingleExpressionBoundary(

            String packetId,

            List<ConsultationMeaningClaim> claims

    ) {

        ConsultationMeaningExpressionBoundaryContract canonical =
                claims
                        .get(0)
                        .expressionBoundaryContract();

        for (
                ConsultationMeaningClaim claim
                : claims
        ) {

            ConsultationMeaningExpressionBoundaryContract current =
                    claim
                            .expressionBoundaryContract();

            if (
                    !canonical.equals(
                            current
                    )
            ) {

                throw new IllegalArgumentException(
                        "하나의 Meaning Packet에는 서로 다른 Expression Boundary Claim을 섞을 수 없습니다. packetId="
                                + packetId
                                + ", firstClaim="
                                + claims.get(0).claimId()
                                + ", firstMode="
                                + canonical.expressionMode()
                                + ", mixedClaim="
                                + claim.claimId()
                                + ", mixedMode="
                                + current.expressionMode()
                );
            }
        }
    }

    private static List<String> normalizeTexts(
            List<String> source
    ) {

        if (
                source == null
                        ||
                        source.isEmpty()
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