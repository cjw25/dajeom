package com.dajeom.model;

import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainEvidenceTier;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 20차.
 * +
 * Grounding Provenance 정합성 강화.
 *
 * LLM Grounding에 전달할
 * 하나의 deterministic Evidence Source.
 *
 * =================================================
 * 중요한 점
 * =================================================
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
 * Source Type / Tier 계약
 * =================================================
 *
 * LifeDomainEvidenceSourceType은
 * 자기 자신의 공식 Tier를 이미 가지고 있다.
 *
 * 예:
 *
 * ELEMENT_STRENGTH
 * ->
 * CORE_STRUCTURE
 *
 * NATAL_RELATION
 * ->
 * STRUCTURAL_CONTEXT
 *
 * ANNUAL_CONTEXT
 * ->
 * TEMPORAL_CONTEXT
 *
 * SHINSAL
 * ->
 * AUXILIARY
 *
 * 따라서 Grounding Source 생성 시:
 *
 * tier
 *
 * 는 반드시:
 *
 * sourceType.tier()
 *
 * 와 일치해야 한다.
 *
 * =================================================
 * Auxiliary 계약
 * =================================================
 *
 * auxiliary 여부는
 * 별도의 독립 의미 값이 아니다.
 *
 * 공식 Tier가:
 *
 * AUXILIARY
 *
 * 인 경우에만 true다.
 *
 * 따라서:
 *
 * auxiliary
 *
 * 는 반드시:
 *
 * tier.auxiliary()
 *
 * 와 일치해야 한다.
 *
 * =================================================
 * 필요한 이유
 * =================================================
 *
 * 다음과 같은 잘못된 provenance를
 * Grounding 경계에서 허용하지 않는다.
 *
 * 예:
 *
 * ANNUAL_CONTEXT
 * +
 * CORE_STRUCTURE
 *
 * 또는:
 *
 * SHINSAL
 * +
 * auxiliary = false
 *
 * 이런 조합이 허용되면:
 *
 * - Evidence Priority
 * - AUXILIARY-only 차단
 * - Provenance 독립성 판단
 * - Legal Cross 검증
 *
 * 에 잘못된 정보가 전달될 수 있다.
 *
 * =================================================
 * upstream 계약과 일치
 * =================================================
 *
 * LifeDomainEvidenceReference는 이미:
 *
 * tier == sourceType.tier()
 *
 * 를 강제한다.
 *
 * Grounding 변환 이후에도
 * 같은 provenance 계약을 유지한다.
 *
 * =================================================
 * LLM 경계
 * =================================================
 *
 * 이렇게 해야:
 *
 * LLM이 3~17차 계산 객체를 직접 읽고
 * 새로운 사주 계산을 시도하는 것을 막으면서도
 * 근거 추적성은 유지할 수 있다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Tier 재계산
 * - Source Type 재분류
 * - Auxiliary 임의 승격/강등
 * - Source 간 점수화
 * - Source 개수로 의미 강화
 * - 관계 생성
 * - 인과 생성
 * - 순서 생성
 */
public record ConsultationGroundingSource(

        LifeDomainEvidenceSourceType sourceType,

        LifeDomainEvidenceTier tier,

        String sourceKey,

        boolean auxiliary,

        String summary,

        List<String> reasons

) {

    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

    public ConsultationGroundingSource {

        Objects.requireNonNull(
                sourceType,
                "Grounding Source Type은 필수입니다."
        );

        Objects.requireNonNull(
                tier,
                "Grounding Evidence Tier는 필수입니다."
        );

        /*
         * =================================================
         * Source Type / Tier 정합성
         * =================================================
         *
         * Grounding 단계에서 Tier를
         * 별도로 다시 선택할 수 없게 한다.
         *
         * Source Type이 가진 공식 Tier와
         * 전달된 Tier는 반드시 동일해야 한다.
         */

        if (
                tier
                        != sourceType.tier()
        ) {

            throw new IllegalArgumentException(
                    "Grounding Source의 Tier와 SourceType의 공식 Tier가 일치하지 않습니다. "
                            + "sourceType="
                            + sourceType
                            + ", expectedTier="
                            + sourceType.tier()
                            + ", actualTier="
                            + tier
            );
        }

        /*
         * =================================================
         * Auxiliary 정합성
         * =================================================
         *
         * auxiliary는 별도의 임의 상태가 아니다.
         *
         * Tier가 AUXILIARY이면 true,
         * 그 외 Tier이면 false여야 한다.
         */

        boolean expectedAuxiliary =
                tier.auxiliary();

        if (
                auxiliary
                        != expectedAuxiliary
        ) {

            throw new IllegalArgumentException(
                    "Grounding Source의 auxiliary 상태가 Evidence Tier와 일치하지 않습니다. "
                            + "sourceType="
                            + sourceType
                            + ", tier="
                            + tier
                            + ", expectedAuxiliary="
                            + expectedAuxiliary
                            + ", actualAuxiliary="
                            + auxiliary
            );
        }

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


    /*
     * =================================================
     * Tier Helper
     * =================================================
     */

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
                        || source.isEmpty()
        ) {

            return List.of();
        }

        LinkedHashSet<String> normalized =
                new LinkedHashSet<>();

        for (
                String reason
                : source
        ) {

            if (
                    reason == null
            ) {

                continue;
            }

            String value =
                    reason.trim();

            if (
                    value.isBlank()
            ) {

                continue;
            }

            normalized.add(
                    value
            );
        }

        return List.copyOf(
                normalized
        );
    }
}