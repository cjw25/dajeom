package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 19차.
 *
 * 하나의 ConsultationCategory가
 * 하나의 16차 Timeline Segment에서 사용할
 * 18차 LifeDomainRuleMatch 묶음.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 이 객체는:
 *
 * - 점수를 계산하지 않는다.
 * - 대표 polarity를 선택하지 않는다.
 * - SUPPORTIVE/PRESSURE/MIXED/CONTEXTUAL을 합산하지 않는다.
 *
 * =================================================
 *
 * 같은 조회 시점에:
 *
 * 출생시간
 * 대운 교체 시점
 * 날짜 정밀도
 *
 * 등의 이유로 여러 Timeline Segment가 가능하면
 * Segment마다 별도 객체로 유지한다.
 *
 * =================================================
 *
 * matches:
 *
 * 현재 Segment에서 새롭게 관여하는
 * LifeDomainRuleMatch만 들어간다.
 *
 * 원국 Match는 상위
 * ConsultationCategoryEvidenceView.natalMatches
 * 에서 별도로 보존한다.
 */
public record ConsultationCategorySegmentEvidence(

        LuckIntegratedSegmentAnalysis segmentAnalysis,

        List<LifeDomainRuleMatch> matches

) {

    public ConsultationCategorySegmentEvidence {

        Objects.requireNonNull(
                segmentAnalysis,
                "ConsultationCategorySegmentEvidence에는 LuckIntegratedSegmentAnalysis가 필요합니다."
        );

        matches =
                matches == null
                        ? List.of()
                        : List.copyOf(
                        matches
                );

        for (
                LifeDomainRuleMatch match
                : matches
        ) {

            Objects.requireNonNull(
                    match,
                    "ConsultationCategorySegmentEvidence의 Match는 null일 수 없습니다."
            );
        }
    }

    /*
     * =================================================
     * Evidence
     * =================================================
     */

    public boolean hasMatches() {

        return !matches.isEmpty();
    }

    public int matchCount() {

        return matches.size();
    }

    /*
     * =================================================
     * Timeline
     * =================================================
     */

    public boolean fullyDefinite() {

        return segmentAnalysis
                .fullyDefinite();
    }

    public boolean timelineUncertaintyExists() {

        return segmentAnalysis
                .timelineUncertaintyExists();
    }

    public int sajuYear() {

        return segmentAnalysis
                .annualCandidate()
                .targetYear();
    }

    public int monthIndex() {

        return segmentAnalysis
                .monthlyCandidate()
                .monthIndex();
    }

    /*
     * =================================================
     * 대운 상태
     * =================================================
     */

    public boolean hasActiveDecade() {

        return segmentAnalysis
                .hasActiveDecade();
    }

    public boolean beforeFirstDecadeLuck() {

        return segmentAnalysis
                .beforeFirstDecadeLuck();
    }

    public boolean afterGeneratedDecadeRange() {

        return segmentAnalysis
                .afterGeneratedDecadeRange();
    }

    public boolean decadeUnavailable() {

        return segmentAnalysis
                .decadeUnavailable();
    }
}