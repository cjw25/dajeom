package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 19차 / 21차 확장.
 *
 * 하나의 ConsultationCategory가
 * 실제로 사용할 deterministic Evidence View.
 *
 * =================================================
 *
 * 구성:
 *
 * 1.
 * natalMatches
 *
 * 원국 LifeDomain Match.
 *
 * =================================================
 *
 * 2.
 * segmentEvidences
 *
 * 현재 조회시점의 실제 Timeline Segment Evidence.
 *
 * =================================================
 *
 * 3.
 * timelineYearEvidences
 *
 * PAST_LUCK / FUTURE_LUCK 전용
 * 다중 월 Timeline Range의 연도별 Evidence.
 *
 * =================================================
 *
 * 4.
 * unavailableReason
 *
 * 해당 Category를 현재 deterministic 계산만으로
 * 독립적으로 만들 수 없는 경우의 사유.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 현재 Segment와
 * 과거/미래 Timeline Range를
 * 같은 의미로 취급하지 않는다.
 *
 * =================================================
 *
 * unavailable 상태에서는
 * 어떤 Evidence도 함께 존재할 수 없다.
 */
public record ConsultationCategoryEvidenceView(

        ConsultationCategory category,

        List<LifeDomainRuleMatch> natalMatches,

        List<ConsultationCategorySegmentEvidence>
        segmentEvidences,

        List<ConsultationCategoryTimelineYearEvidence>
        timelineYearEvidences,

        Optional<String> unavailableReason

) {

    public ConsultationCategoryEvidenceView {

        Objects.requireNonNull(
                category,
                "ConsultationCategoryEvidenceView에는 ConsultationCategory가 필요합니다."
        );

        natalMatches =
                normalizeNatalMatches(
                        natalMatches
                );

        segmentEvidences =
                normalizeSegmentEvidences(
                        segmentEvidences
                );

        timelineYearEvidences =
                normalizeTimelineYearEvidences(
                        timelineYearEvidences
                );

        unavailableReason =
                normalizeUnavailableReason(
                        unavailableReason
                );

        validateUnavailableState(
                natalMatches,
                segmentEvidences,
                timelineYearEvidences,
                unavailableReason
        );

        validateSegmentIdentityUniqueness(
                segmentEvidences
        );

        validateTimelineYearUniqueness(
                timelineYearEvidences
        );
    }

    /*
     * =================================================
     * Availability
     * =================================================
     */

    public boolean available() {

        return unavailableReason
                .isEmpty();
    }

    public boolean unavailable() {

        return unavailableReason
                .isPresent();
    }

    public String requireUnavailableReason() {

        if (
                available()
        ) {

            throw new IllegalStateException(
                    "사용 가능한 ConsultationCategoryEvidenceView에는 unavailableReason이 없습니다."
            );
        }

        return unavailableReason
                .orElseThrow();
    }

    /*
     * =================================================
     * Natal
     * =================================================
     */

    public boolean hasNatalMatches() {

        return !natalMatches
                .isEmpty();
    }

    public int natalMatchCount() {

        return natalMatches
                .size();
    }

    /*
     * =================================================
     * Current Timeline Segment
     * =================================================
     */

    public boolean hasSegmentEvidences() {

        return !segmentEvidences
                .isEmpty();
    }

    public int segmentEvidenceCount() {

        return segmentEvidences
                .size();
    }

    /*
     * =================================================
     * Timeline Year Range
     * =================================================
     */

    public boolean hasTimelineYearEvidences() {

        return !timelineYearEvidences
                .isEmpty();
    }

    public int timelineYearEvidenceCount() {

        return timelineYearEvidences
                .size();
    }

    public Optional<ConsultationCategoryTimelineYearEvidence>
    findTimelineYearEvidence(
            int sajuYear
    ) {

        return timelineYearEvidences
                .stream()
                .filter(
                        evidence ->
                                evidence
                                        .sajuYear()
                                        == sajuYear
                )
                .findFirst();
    }

    /*
     * =================================================
     * 전체 Match
     * =================================================
     *
     * 단순 조회 편의를 위한 연결이다.
     *
     * 여기서:
     *
     * - 점수
     * - polarity
     * - 대표 상태
     *
     * 를 계산하지 않는다.
     */

    public List<LifeDomainRuleMatch>
    allMatches() {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        result.addAll(
                natalMatches
        );

        for (
                ConsultationCategorySegmentEvidence evidence
                : segmentEvidences
        ) {

            result.addAll(
                    evidence.matches()
            );
        }

        for (
                ConsultationCategoryTimelineYearEvidence evidence
                : timelineYearEvidences
        ) {

            result.addAll(
                    evidence.matches()
            );
        }

        return List.copyOf(
                result
        );
    }

    public boolean hasMatches() {

        return !allMatches()
                .isEmpty();
    }

    public int matchCount() {

        return allMatches()
                .size();
    }

    /*
     * =================================================
     * Timeline Uncertainty
     * =================================================
     */

    public boolean timelineUncertaintyExists() {

        boolean currentSegmentUncertainty =
                segmentEvidences
                        .stream()
                        .anyMatch(
                                ConsultationCategorySegmentEvidence
                                        ::timelineUncertaintyExists
                        );

        if (
                currentSegmentUncertainty
        ) {

            return true;
        }

        return timelineYearEvidences
                .stream()
                .anyMatch(
                        ConsultationCategoryTimelineYearEvidence
                                ::timelineUncertaintyExists
                );
    }

    /*
     * =================================================
     * Normalize Natal
     * =================================================
     */

    private static List<LifeDomainRuleMatch>
    normalizeNatalMatches(
            List<LifeDomainRuleMatch> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<LifeDomainRuleMatch> normalized =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : source
        ) {

            normalized.add(
                    Objects.requireNonNull(
                            match,
                            "ConsultationCategoryEvidenceView의 Natal Match는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                normalized
        );
    }

    /*
     * =================================================
     * Normalize Segment
     * =================================================
     */

    private static List<ConsultationCategorySegmentEvidence>
    normalizeSegmentEvidences(

            List<ConsultationCategorySegmentEvidence>
                    source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationCategorySegmentEvidence>
                normalized =
                new ArrayList<>();

        for (
                ConsultationCategorySegmentEvidence evidence
                : source
        ) {

            normalized.add(
                    Objects.requireNonNull(
                            evidence,
                            "ConsultationCategory Segment Evidence는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                normalized
        );
    }

    /*
     * =================================================
     * Normalize Timeline Year
     * =================================================
     */

    private static List<ConsultationCategoryTimelineYearEvidence>
    normalizeTimelineYearEvidences(

            List<ConsultationCategoryTimelineYearEvidence>
                    source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationCategoryTimelineYearEvidence>
                normalized =
                new ArrayList<>();

        for (
                ConsultationCategoryTimelineYearEvidence evidence
                : source
        ) {

            normalized.add(
                    Objects.requireNonNull(
                            evidence,
                            "ConsultationCategory Timeline Year Evidence는 null일 수 없습니다."
                    )
            );
        }

        normalized.sort(
                java.util.Comparator.comparingInt(
                        ConsultationCategoryTimelineYearEvidence
                                ::sajuYear
                )
        );

        return List.copyOf(
                normalized
        );
    }

    /*
     * =================================================
     * Normalize Unavailable
     * =================================================
     */

    private static Optional<String>
    normalizeUnavailableReason(
            Optional<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Optional.empty();
        }

        String value =
                source
                        .orElseThrow()
                        .trim();

        if (
                value.isBlank()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                value
        );
    }

    /*
     * =================================================
     * Unavailable 검증
     * =================================================
     */

    private static void validateUnavailableState(

            List<LifeDomainRuleMatch> natalMatches,

            List<ConsultationCategorySegmentEvidence>
                    segmentEvidences,

            List<ConsultationCategoryTimelineYearEvidence>
                    timelineYearEvidences,

            Optional<String> unavailableReason

    ) {

        if (
                unavailableReason.isEmpty()
        ) {

            return;
        }

        if (
                !natalMatches.isEmpty()
                        || !segmentEvidences.isEmpty()
                        || !timelineYearEvidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "사용 불가능한 ConsultationCategoryEvidenceView에는 Evidence를 함께 넣을 수 없습니다."
            );
        }
    }

    /*
     * =================================================
     * Segment Identity 중복 검증
     * =================================================
     */

    private static void
    validateSegmentIdentityUniqueness(

            List<ConsultationCategorySegmentEvidence>
                    segmentEvidences

    ) {

        Map<
                LuckIntegratedSegmentAnalysis,
                Boolean
                > identities =
                new IdentityHashMap<>();

        for (
                ConsultationCategorySegmentEvidence evidence
                : segmentEvidences
        ) {

            LuckIntegratedSegmentAnalysis segment =
                    evidence
                            .segmentAnalysis();

            Boolean previous =
                    identities.put(
                            segment,
                            Boolean.TRUE
                    );

            if (
                    previous != null
            ) {

                throw new IllegalArgumentException(
                        "동일 LuckIntegratedSegmentAnalysis가 ConsultationCategoryEvidenceView에 중복 포함되었습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * Timeline Year 중복 검증
     * =================================================
     */

    private static void
    validateTimelineYearUniqueness(

            List<ConsultationCategoryTimelineYearEvidence>
                    timelineYearEvidences

    ) {

        List<Integer> years =
                new ArrayList<>();

        for (
                ConsultationCategoryTimelineYearEvidence evidence
                : timelineYearEvidences
        ) {

            if (
                    years.contains(
                            evidence.sajuYear()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 명리연도가 Timeline Year Evidence에 중복 포함되었습니다: "
                                + evidence.sajuYear()
                );
            }

            years.add(
                    evidence.sajuYear()
            );
        }
    }
}