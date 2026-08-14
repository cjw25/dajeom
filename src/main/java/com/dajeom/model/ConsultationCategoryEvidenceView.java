package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 19차.
 *
 * 하나의 ConsultationCategory에
 * 18차 LifeDomain Evidence를 선별한 결과.
 *
 * =================================================
 *
 * 아직 사용자 상담문이 아니다.
 *
 * 이 객체는:
 *
 * 18차 deterministic Match
 * ->
 * 11개 사용자 상담 View
 *
 * 사이의 경계다.
 *
 * =================================================
 *
 * natalMatches:
 *
 * 해당 Category가 사용하는
 * 원국 LifeDomainRuleMatch.
 *
 * =================================================
 *
 * segmentEvidences:
 *
 * 현재 조회 기준시점에서 가능한
 * Timeline Segment별 변화 Match.
 *
 * =================================================
 *
 * unavailableReason:
 *
 * 현재 LifeDomainAnalysis만으로
 * 해당 Category를 만들면 안 되는 경우 사용.
 *
 * 대표:
 *
 * PAST_LUCK
 * -> 실제 과거 Timeline Range 필요
 *
 * FUTURE_LUCK
 * -> 실제 미래 Timeline Range 필요
 *
 * LEGAL_RISK
 * -> 전용 교차 Evidence Rule 필요
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * Evidence 없음
 * !=
 * 불리함
 *
 * unavailable
 * !=
 * 나쁨
 *
 * POSSIBLE
 * !=
 * DEFINITE
 *
 * 서로 다른 Match 상태를
 * 하나의 polarity로 합치지 않는다.
 */
public record ConsultationCategoryEvidenceView(

        ConsultationCategory category,

        List<LifeDomainRuleMatch> natalMatches,

        List<ConsultationCategorySegmentEvidence>
        segmentEvidences,

        Optional<String> unavailableReason

) {

    public ConsultationCategoryEvidenceView {

        Objects.requireNonNull(
                category,
                "ConsultationCategoryEvidenceView에는 ConsultationCategory가 필요합니다."
        );

        natalMatches =
                natalMatches == null
                        ? List.of()
                        : List.copyOf(
                        natalMatches
                );

        for (
                LifeDomainRuleMatch match
                : natalMatches
        ) {

            Objects.requireNonNull(
                    match,
                    "ConsultationCategoryEvidenceView의 Natal Match는 null일 수 없습니다."
            );
        }

        segmentEvidences =
                segmentEvidences == null
                        ? List.of()
                        : List.copyOf(
                        segmentEvidences
                );

        validateSegmentEvidences(
                segmentEvidences
        );

        unavailableReason =
                normalizeUnavailableReason(
                        unavailableReason
                );

        /*
         * =================================================
         * unavailable View에는
         * 가짜 Evidence를 넣지 않는다.
         * =================================================
         */

        if (
                unavailableReason.isPresent()
                        && (
                        !natalMatches.isEmpty()
                                || !segmentEvidences.isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "unavailable ConsultationCategory View에는 Evidence를 함께 넣을 수 없습니다: "
                            + category
            );
        }
    }

    /*
     * =================================================
     * 상태
     * =================================================
     */

    public boolean available() {

        return unavailableReason
                .isEmpty();
    }

    public boolean unavailable() {

        return !available();
    }

    /*
     * =================================================
     * 원국
     * =================================================
     */

    public boolean hasNatalMatches() {

        return !natalMatches.isEmpty();
    }

    public int natalMatchCount() {

        return natalMatches.size();
    }

    /*
     * =================================================
     * Timeline
     * =================================================
     */

    public boolean hasSegmentEvidences() {

        return !segmentEvidences.isEmpty();
    }

    public boolean hasSegmentMatches() {

        return segmentEvidences
                .stream()
                .anyMatch(
                        ConsultationCategorySegmentEvidence
                                ::hasMatches
                );
    }

    public int segmentCount() {

        return segmentEvidences.size();
    }

    public boolean timelineUncertaintyExists() {

        return segmentEvidences
                .stream()
                .anyMatch(
                        ConsultationCategorySegmentEvidence
                                ::timelineUncertaintyExists
                );
    }

    /*
     * =================================================
     * 전체 Match
     * =================================================
     *
     * 원국
     * +
     * 모든 가능한 현재 Segment 변화분.
     *
     * 상태는 합산하지 않는다.
     */

    public List<LifeDomainRuleMatch> allMatches() {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        result.addAll(
                natalMatches
        );

        for (
                ConsultationCategorySegmentEvidence segmentEvidence
                : segmentEvidences
        ) {

            result.addAll(
                    segmentEvidence.matches()
            );
        }

        return List.copyOf(
                result
        );
    }

    public int matchCount() {

        return allMatches()
                .size();
    }

    public boolean hasAnyMatch() {

        return matchCount() > 0;
    }

    /*
     * =================================================
     * Segment Identity 검증
     * =================================================
     *
     * 동일 Timeline Segment를
     * 같은 Category에 중복 삽입하지 않는다.
     */

    private static void validateSegmentEvidences(
            List<ConsultationCategorySegmentEvidence> evidences
    ) {

        Set<LuckIntegratedSegmentAnalysis> identities =
                java.util.Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        for (
                ConsultationCategorySegmentEvidence evidence
                : evidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "ConsultationCategorySegmentEvidence는 null일 수 없습니다."
            );

            if (
                    !identities.add(
                            evidence.segmentAnalysis()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Timeline Segment가 ConsultationCategory View에 중복 연결되었습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * unavailableReason 정규화
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

            throw new IllegalArgumentException(
                    "unavailableReason은 빈 문자열일 수 없습니다."
            );
        }

        return Optional.of(
                value
        );
    }
}