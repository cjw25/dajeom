package com.dajeom.model;

import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 20차 / 22차 Prominence 확장.
 *
 * 하나의 LifeDomainRuleMatch를
 * LLM Grounding용 형태로 변환한 Evidence.
 *
 * =================================================
 *
 * 보존:
 *
 * - LifeDomain
 * - Rule Code
 * - SUPPORTIVE / PRESSURE / MIXED / CONTEXTUAL
 * - Rule Summary
 * - 실제 사용 Evidence Source
 * - Reasons
 * - deterministic 서술 우선순위 provenance
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 점수 합산
 * - 대표 길흉 선택
 * - 성공 확률 계산
 * - 사건 발생 확정
 *
 * =================================================
 *
 * 특히:
 *
 * SUPPORTIVE + PRESSURE
 *
 * 가 동시에 존재하면
 * 두 Evidence를 각각 그대로 유지한다.
 *
 * prominence는 어느 쪽이 더 길하거나 좋은지를
 * 결정하지 않는다.
 *
 * 단지 상담문에서 어떤 Evidence를
 * 먼저 깊게 설명할지 결정하는 내부 정보다.
 */
public record ConsultationGroundingEvidence(

        LifeDomain domain,

        String ruleCode,

        LifeDomainSignalStatus status,

        String summary,

        List<ConsultationGroundingSource> sources,

        List<String> reasons,

        ConsultationGroundingProminence prominence

) {

    /*
     * =================================================
     * 기존 20차 호출 호환 Constructor
     * =================================================
     *
     * 기존 호출부가 prominence를 전달하지 않아도
     * 컴파일이 깨지지 않게 유지한다.
     *
     * 이 경우 prominence는
     * narrativePriority == 0 상태로 생성되고
     *
     * ConsultationGroundingProminenceAnalyzer가
     * 전체 Slice 안에서 실제 순위를 다시 계산한다.
     */

    public ConsultationGroundingEvidence(

            LifeDomain domain,

            String ruleCode,

            LifeDomainSignalStatus status,

            String summary,

            List<ConsultationGroundingSource> sources,

            List<String> reasons

    ) {

        this(
                domain,
                ruleCode,
                status,
                summary,
                sources,
                reasons,
                null
        );
    }

    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

    public ConsultationGroundingEvidence {

        Objects.requireNonNull(
                domain,
                "Grounding Evidence의 LifeDomain은 필수입니다."
        );

        ruleCode =
                requireText(
                        ruleCode,
                        "Grounding Evidence Rule Code는 필수입니다."
                );

        Objects.requireNonNull(
                status,
                "Grounding Evidence Status는 필수입니다."
        );

        summary =
                requireText(
                        summary,
                        "Grounding Evidence Summary는 필수입니다."
                );

        sources =
                sources == null
                        ?
                        List.of()
                        :
                        List.copyOf(
                                sources
                        );

        if (
                sources.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Grounding Evidence에는 최소 하나의 Source가 필요합니다: "
                            +
                            ruleCode
            );
        }

        for (
                ConsultationGroundingSource source
                : sources
        ) {

            Objects.requireNonNull(
                    source,
                    "Grounding Evidence Source는 null일 수 없습니다."
            );
        }

        /*
         * =================================================
         * AUXILIARY-only 금지
         * =================================================
         */

        boolean auxiliaryOnly =
                sources
                        .stream()
                        .allMatch(
                                ConsultationGroundingSource
                                        ::auxiliary
                        );

        if (
                auxiliaryOnly
        ) {

            throw new IllegalArgumentException(
                    "AUXILIARY Source만으로 Grounding Evidence를 만들 수 없습니다: "
                            +
                            ruleCode
            );
        }

        reasons =
                normalizeReasons(
                        reasons
                );

        /*
         * =================================================
         * Prominence
         * =================================================
         */

        if (
                prominence == null
        ) {

            prominence =
                    ConsultationGroundingProminence
                            .unranked(
                                    sources
                            );
        }

        validateProminenceAgainstSources(
                sources,
                prominence,
                ruleCode
        );
    }

    public boolean supportive() {

        return status
                ==
                LifeDomainSignalStatus.SUPPORTIVE;
    }

    public boolean pressure() {

        return status
                ==
                LifeDomainSignalStatus.PRESSURE;
    }

    public boolean mixed() {

        return status
                ==
                LifeDomainSignalStatus.MIXED;
    }

    public boolean contextual() {

        return status
                ==
                LifeDomainSignalStatus.CONTEXTUAL;
    }

    public boolean hasAuxiliarySource() {

        return sources
                .stream()
                .anyMatch(
                        ConsultationGroundingSource
                                ::auxiliary
                );
    }

    public boolean prominenceRanked() {

        return prominence.ranked();
    }

    /*
     * =================================================
     * Prominence Source 정합성
     * =================================================
     *
     * prominence에 들어 있는 직접 Source 수를
     * 임의로 조작할 수 없게 실제 Source와 비교한다.
     */

    private static void validateProminenceAgainstSources(

            List<ConsultationGroundingSource> sources,

            ConsultationGroundingProminence prominence,

            String ruleCode

    ) {

        Set<String> directKeys =
                new LinkedHashSet<>();

        Set<String> directTypes =
                new LinkedHashSet<>();

        int auxiliaryCount =
                0;

        for (
                ConsultationGroundingSource source
                : sources
        ) {

            if (
                    source.auxiliary()
            ) {

                auxiliaryCount++;

                continue;
            }

            directKeys.add(
                    source.sourceKey()
            );

            directTypes.add(
                    String.valueOf(
                            source.sourceType()
                    )
            );
        }

        if (
                prominence.nonAuxiliarySourceCount()
                        !=
                        directKeys.size()
        ) {

            throw new IllegalArgumentException(
                    "Grounding Prominence의 비보조 Source 수가 실제 Source와 일치하지 않습니다: "
                            +
                            ruleCode
            );
        }

        if (
                prominence.nonAuxiliarySourceTypeCount()
                        !=
                        directTypes.size()
        ) {

            throw new IllegalArgumentException(
                    "Grounding Prominence의 비보조 Source Type 수가 실제 Source와 일치하지 않습니다: "
                            +
                            ruleCode
            );
        }

        if (
                prominence.auxiliarySourceCount()
                        !=
                        auxiliaryCount
        ) {

            throw new IllegalArgumentException(
                    "Grounding Prominence의 AUXILIARY Source 수가 실제 Source와 일치하지 않습니다: "
                            +
                            ruleCode
            );
        }
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

    private static List<String> normalizeReasons(

            List<String> source

    ) {

        if (
                source == null
                        ||
                        source.isEmpty()
        ) {

            return List.of();
        }

        return source
                .stream()
                .map(
                        reason ->
                                requireText(
                                        reason,
                                        "Grounding Evidence Reason은 비어 있을 수 없습니다."
                                )
                )
                .toList();
    }
}