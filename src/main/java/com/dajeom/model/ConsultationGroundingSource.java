package com.dajeom.model;

import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainEvidenceTier;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 20차.
 *
 * LLM Grounding에 전달할
 * 하나의 deterministic Evidence Source.
 *
 * =================================================
 *
 * 중요한 점:
 *
 * LifeDomainEvidenceReference의 source 원본 객체는
 * Grounding에 직접 전달하지 않는다.
 *
 * 대신:
 *
 * - Source Type
 * - Tier
 * - Source Key
 * - Auxiliary 여부
 * - Summary
 * - Reasons
 *
 * 만 전달한다.
 *
 * =================================================
 *
 * 이렇게 해야:
 *
 * LLM이 3~17차 계산 객체를 직접 읽고
 * 새로운 사주 계산을 시도하는 것을 막으면서도
 * 근거 추적성은 유지할 수 있다.
 */
public record ConsultationGroundingSource(

        LifeDomainEvidenceSourceType sourceType,

        LifeDomainEvidenceTier tier,

        String sourceKey,

        boolean auxiliary,

        String summary,

        List<String> reasons

) {

    public ConsultationGroundingSource {

        Objects.requireNonNull(
                sourceType,
                "Grounding Source Type은 필수입니다."
        );

        Objects.requireNonNull(
                tier,
                "Grounding Evidence Tier는 필수입니다."
        );

        sourceKey =
                requireText(
                        sourceKey,
                        "Grounding Source Key는 필수입니다."
                );

        summary =
                requireText(
                        summary,
                        "Grounding Source Summary는 필수입니다."
                );

        reasons =
                normalizeReasons(
                        reasons
                );
    }

    public boolean coreStructure() {

        return tier
                == LifeDomainEvidenceTier.CORE_STRUCTURE;
    }

    public boolean structuralContext() {

        return tier
                == LifeDomainEvidenceTier.STRUCTURAL_CONTEXT;
    }

    public boolean temporalContext() {

        return tier
                == LifeDomainEvidenceTier.TEMPORAL_CONTEXT;
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
                                        "Grounding Source Reason은 비어 있을 수 없습니다."
                                )
                )
                .toList();
    }
}