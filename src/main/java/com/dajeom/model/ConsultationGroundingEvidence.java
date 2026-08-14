package com.dajeom.model;

import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 20차.
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
 */
public record ConsultationGroundingEvidence(

        LifeDomain domain,

        String ruleCode,

        LifeDomainSignalStatus status,

        String summary,

        List<ConsultationGroundingSource> sources,

        List<String> reasons

) {

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
                        ? List.of()
                        : List.copyOf(
                        sources
                );

        if (
                sources.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Grounding Evidence에는 최소 하나의 Source가 필요합니다: "
                            + ruleCode
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
         * 18차 RuleEngine의 핵심 불변식 유지.
         *
         * 신살 등 AUXILIARY Source만으로
         * 독립 생활영역 판단을 만들 수 없다.
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
                            + ruleCode
            );
        }

        reasons =
                normalizeReasons(
                        reasons
                );
    }

    public boolean supportive() {

        return status
                == LifeDomainSignalStatus.SUPPORTIVE;
    }

    public boolean pressure() {

        return status
                == LifeDomainSignalStatus.PRESSURE;
    }

    public boolean mixed() {

        return status
                == LifeDomainSignalStatus.MIXED;
    }

    public boolean contextual() {

        return status
                == LifeDomainSignalStatus.CONTEXTUAL;
    }

    public boolean hasAuxiliarySource() {

        return sources
                .stream()
                .anyMatch(
                        ConsultationGroundingSource
                                ::auxiliary
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

    private static List<String> normalizeReasons(
            List<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
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