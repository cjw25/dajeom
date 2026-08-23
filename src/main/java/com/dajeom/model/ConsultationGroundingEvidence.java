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
 * 보존
 * =================================================
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
 * prominence
 * =================================================
 *
 * prominence는 선택값이 아니다.
 *
 * 모든 Grounding Evidence는
 * 생성 시점부터 자기 Source에 대응하는
 * ConsultationGroundingProminence를
 * 반드시 가져야 한다.
 *
 * 아직 전체 Slice 안의 최종 순위가
 * 계산되지 않은 최초 Evidence는:
 *
 * ConsultationGroundingProminence.unranked(sources)
 *
 * 를 명시적으로 전달한다.
 *
 * 이후:
 *
 * ConsultationGroundingProminenceAnalyzer
 *
 * 가 같은 Slice 안의 Evidence들을 비교하여
 * 실제 narrativePriority를 계산한
 * 새로운 Grounding Evidence를 생성한다.
 *
 * =================================================
 * 중요한 경계
 * =================================================
 *
 * prominence가 없다는 상태와
 * prominence가 아직 순위 계산 전이라는 상태를
 * 같은 null 값으로 표현하지 않는다.
 *
 * 순위 계산 전 상태는:
 *
 * narrativePriority == 0
 *
 * 인 명시적인 unranked Prominence로 표현한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 점수 합산
 * - 대표 길흉 선택
 * - 성공 확률 계산
 * - 사건 발생 확정
 * - prominence null fallback
 * - prominence 없는 우회 생성
 *
 * =================================================
 * 독립 방향 유지
 * =================================================
 *
 * SUPPORTIVE + PRESSURE
 *
 * 가 동시에 존재하면
 * 두 Evidence를 각각 그대로 유지한다.
 *
 * prominence는 어느 쪽이 더 좋거나
 * 더 실제적이라는 의미가 아니다.
 *
 * 단지 상담문에서 어떤 Evidence를
 * 먼저 깊게 설명할지 결정하기 위한
 * deterministic provenance 메타데이터다.
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
         * Prominence 필수
         * =================================================
         *
         * 최초 변환 단계에서도 null을 사용하지 않는다.
         *
         * 아직 Slice 순위가 계산되지 않았다면
         * 호출부가 반드시:
         *
         * ConsultationGroundingProminence.unranked(sources)
         *
         * 를 명시적으로 전달한다.
         */

        prominence =
                Objects.requireNonNull(
                        prominence,
                        "Grounding Evidence에는 ConsultationGroundingProminence가 반드시 필요합니다: "
                                + ruleCode
                );

        validateProminenceAgainstSources(
                sources,
                prominence,
                ruleCode
        );
    }


    /*
     * =================================================
     * Status Helper
     * =================================================
     */

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


    /*
     * =================================================
     * Source Helper
     * =================================================
     */

    public boolean hasAuxiliarySource() {

        return sources
                .stream()
                .anyMatch(
                        ConsultationGroundingSource
                                ::auxiliary
                );
    }


    /*
     * =================================================
     * Prominence Helper
     * =================================================
     */

    public boolean prominenceRanked() {

        return prominence.ranked();
    }


    /*
     * =================================================
     * Prominence Source 정합성
     * =================================================
     *
     * prominence에 들어 있는 직접 Source 수를
     * 임의로 조작할 수 없게
     * 실제 Grounding Source와 비교한다.
     *
     * 여기서는:
     *
     * - narrativePriority의 높고 낮음
     * - 같은 방향 반복의 의미
     * - Status의 우열
     *
     * 을 계산하지 않는다.
     *
     * Source 기반 기본 정합성만 검증한다.
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


    /*
     * =================================================
     * Text
     * =================================================
     */

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


    /*
     * =================================================
     * Reason Normalize
     * =================================================
     */

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