package com.dajeom.model;

import com.dajeom.type.ConsultationCategoryDeterministicStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * PAST_LUCK / FUTURE_LUCK의
 * 한 명리연도에 대한
 * 최종 deterministic Grounding Contract.
 *
 * =================================================
 * 공식 구조
 * =================================================
 *
 * 1.
 * overallNarrative
 *
 * 최종 연도 방향의 공식 계약.
 *
 * 반드시 존재한다.
 *
 * =================================================
 *
 * 2.
 * bigFlow
 *
 * 연도 전체 방향 Anchor.
 *
 * 반드시 존재한다.
 *
 * =================================================
 *
 * 3.
 * primaryActiveDomain
 *
 * 주된 작동축을
 * deterministic하게 하나로 식별할 수 있을 때만 존재.
 *
 * =================================================
 *
 * 4.
 * primaryPressureDomain
 *
 * 주된 부담축을
 * deterministic하게 하나로 식별할 수 있을 때만 존재.
 *
 * =================================================
 *
 * 5.
 * crossDomainPattern
 *
 * 여러 LifeDomain의 방향 공존 구조가
 * 성립할 때만 존재.
 *
 * =================================================
 *
 * 6.
 * turningPoint
 *
 * 전후 명리연도 구조 비교에서
 * 전환점 조건이 성립할 때만 존재.
 *
 * =================================================
 * 최상위 계약
 * =================================================
 *
 * overallNarrative.status
 *
 * =
 *
 * bigFlow.status
 *
 * 이어야 한다.
 *
 * =================================================
 *
 * PRIMARY_ACTIVE_DOMAIN
 * PRIMARY_PRESSURE_DOMAIN
 * CROSS_DOMAIN_PATTERN
 * TURNING_POINT
 *
 * 의 status가
 * overallNarrative를 덮어쓸 수 없다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 Contract는 LLM이
 * raw LifeDomain Grounding Evidence를 보고
 * 연도 전체 방향을 다시 계산하지 못하도록
 * 최종 의미 경계를 제공한다.
 */
public record ConsultationGroundingTimelineYearDeterministicContract(

        ConsultationGroundingDeterministicConclusion
        overallNarrative,

        ConsultationGroundingDeterministicConclusion
        bigFlow,

        Optional<ConsultationGroundingDeterministicConclusion>
        primaryActiveDomain,

        Optional<ConsultationGroundingDeterministicConclusion>
        primaryPressureDomain,

        Optional<ConsultationGroundingDeterministicConclusion>
        crossDomainPattern,

        Optional<ConsultationGroundingDeterministicConclusion>
        turningPoint

) {

    public ConsultationGroundingTimelineYearDeterministicContract {

        Objects.requireNonNull(
                overallNarrative,
                "Timeline Year Deterministic Contract에는 overallNarrative가 필요합니다."
        );

        Objects.requireNonNull(
                bigFlow,
                "Timeline Year Deterministic Contract에는 bigFlow가 필요합니다."
        );

        primaryActiveDomain =
                normalizeOptional(
                        primaryActiveDomain
                );

        primaryPressureDomain =
                normalizeOptional(
                        primaryPressureDomain
                );

        crossDomainPattern =
                normalizeOptional(
                        crossDomainPattern
                );

        turningPoint =
                normalizeOptional(
                        turningPoint
                );

        /*
         * =================================================
         * Rule 역할 검증
         * =================================================
         */

        requireRuleSuffix(
                overallNarrative,
                "_TIMELINE_YEAR_OVERALL_NARRATIVE"
        );

        requireRuleSuffix(
                bigFlow,
                "_TIMELINE_YEAR_BIG_FLOW"
        );

        primaryActiveDomain.ifPresent(
                conclusion ->
                        requireRuleSuffix(
                                conclusion,
                                "_TIMELINE_YEAR_PRIMARY_ACTIVE_DOMAIN"
                        )
        );

        primaryPressureDomain.ifPresent(
                conclusion ->
                        requireRuleSuffix(
                                conclusion,
                                "_TIMELINE_YEAR_PRIMARY_PRESSURE_DOMAIN"
                        )
        );

        crossDomainPattern.ifPresent(
                conclusion ->
                        requireRuleSuffix(
                                conclusion,
                                "_TIMELINE_YEAR_CROSS_DOMAIN_PATTERN"
                        )
        );

        turningPoint.ifPresent(
                conclusion ->
                        requireRuleSuffix(
                                conclusion,
                                "_TIMELINE_YEAR_TURNING_POINT"
                        )
        );

        /*
         * =================================================
         * Overall = BigFlow
         * =================================================
         */

        if (
                overallNarrative.status()
                        != bigFlow.status()
        ) {

            throw new IllegalArgumentException(
                    "Timeline Year OVERALL_NARRATIVE status는 BIG_FLOW status와 동일해야 합니다. overall="
                            + overallNarrative.status()
                            + ", bigFlow="
                            + bigFlow.status()
            );
        }

        /*
         * =================================================
         * Primary Pressure
         * =================================================
         */

        if (
                primaryPressureDomain.isPresent()

                        && primaryPressureDomain
                        .orElseThrow()
                        .status()
                        != ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            throw new IllegalArgumentException(
                    "PRIMARY_PRESSURE_DOMAIN Grounding Conclusion의 status는 PRESSURE여야 합니다."
            );
        }

        /*
         * =================================================
         * Turning Point
         * =================================================
         */

        if (
                turningPoint.isPresent()

                        && turningPoint
                        .orElseThrow()
                        .status()
                        != bigFlow.status()
        ) {

            throw new IllegalArgumentException(
                    "TURNING_POINT status는 해당 명리연도의 BIG_FLOW status와 동일해야 합니다."
            );
        }
    }


    /*
     * =================================================
     * Helper
     * =================================================
     */

    public boolean hasPrimaryActiveDomain() {

        return primaryActiveDomain
                .isPresent();
    }


    public boolean hasPrimaryPressureDomain() {

        return primaryPressureDomain
                .isPresent();
    }


    public boolean hasCrossDomainPattern() {

        return crossDomainPattern
                .isPresent();
    }


    public boolean hasTurningPoint() {

        return turningPoint
                .isPresent();
    }


    /**
     * 실제 사용자 설명에 사용할
     * deterministic component.
     *
     * 순서:
     *
     * Overall
     * BigFlow
     * PrimaryActive
     * PrimaryPressure
     * CrossDomain
     * TurningPoint
     */
    public List<ConsultationGroundingDeterministicConclusion>
    allConclusions() {

        List<ConsultationGroundingDeterministicConclusion> result =
                new ArrayList<>();

        result.add(
                overallNarrative
        );

        result.add(
                bigFlow
        );

        primaryActiveDomain.ifPresent(
                result::add
        );

        primaryPressureDomain.ifPresent(
                result::add
        );

        crossDomainPattern.ifPresent(
                result::add
        );

        turningPoint.ifPresent(
                result::add
        );

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Optional
     * =================================================
     */

    private static Optional<ConsultationGroundingDeterministicConclusion>
    normalizeOptional(

            Optional<ConsultationGroundingDeterministicConclusion>
                    source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                Objects.requireNonNull(
                        source.orElseThrow(),
                        "Timeline Year Deterministic Conclusion은 null일 수 없습니다."
                )
        );
    }


    /*
     * =================================================
     * Rule Suffix
     * =================================================
     */

    private static void requireRuleSuffix(

            ConsultationGroundingDeterministicConclusion conclusion,

            String suffix

    ) {

        Objects.requireNonNull(
                conclusion,
                "Timeline Year Deterministic Conclusion은 null일 수 없습니다."
        );

        if (
                !conclusion
                        .ruleCode()
                        .endsWith(
                                suffix
                        )
        ) {

            throw new IllegalArgumentException(
                    "Timeline Year Deterministic Conclusion의 Rule 역할이 일치하지 않습니다. rule="
                            + conclusion.ruleCode()
                            + ", expectedSuffix="
                            + suffix
            );
        }
    }
}