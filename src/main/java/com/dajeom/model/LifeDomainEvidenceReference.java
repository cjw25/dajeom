package com.dajeom.model;

import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainEvidenceTier;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 18차-1.
 *
 * 기존 deterministic 분석 객체 하나를
 * 생활영역 Evidence Source로 연결한다.
 *
 * =================================================
 *
 * T source
 *
 * 실제 upstream 분석 객체.
 *
 * 예:
 *
 * DayMasterStrengthAnalysis
 * TenGodCompositeAnalysis
 * PalaceAnalysis
 * AnnualLuckContextAnalysis
 * ShinsalEvidence
 *
 * =================================================
 *
 * sourceKey
 *
 * Semantic 식별자.
 *
 * 디버깅 / AI Grounding / 중복검증에서 사용.
 *
 * =================================================
 *
 * upstream 객체를 복사하거나
 * String으로만 변환하지 않고 source에 그대로 유지한다.
 */
public record LifeDomainEvidenceReference<T>(

        LifeDomainEvidenceSourceType sourceType,

        LifeDomainEvidenceTier tier,

        String sourceKey,

        T source,

        String summary,

        List<String> reasons

) {

    public LifeDomainEvidenceReference {

        Objects.requireNonNull(
                sourceType,
                "LifeDomainEvidenceReference에는 SourceType이 필요합니다."
        );

        Objects.requireNonNull(
                tier,
                "LifeDomainEvidenceReference에는 Tier가 필요합니다."
        );

        if (
                tier != sourceType.tier()
        ) {

            throw new IllegalArgumentException(
                    "Evidence Reference의 Tier와 SourceType Tier가 일치하지 않습니다."
            );
        }

        if (
                sourceKey == null
                        || sourceKey.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Evidence Reference에는 sourceKey가 필요합니다."
            );
        }

        sourceKey =
                sourceKey.trim();

        Objects.requireNonNull(
                source,
                "Evidence Reference에는 실제 upstream source 객체가 필요합니다."
        );

        if (
                summary == null
                        || summary.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Evidence Reference에는 summary가 필요합니다."
            );
        }

        summary =
                summary.trim();

        reasons =
                reasons == null
                        ? List.of()
                        : reasons
                        .stream()
                        .filter(
                                Objects::nonNull
                        )
                        .map(
                                String::trim
                        )
                        .filter(
                                reason ->
                                        !reason.isBlank()
                        )
                        .toList();

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Evidence Reference에는 최소 하나의 reason이 필요합니다."
            );
        }
    }

    /**
     * Source를 안전하게 특정 타입으로 가져온다.
     */
    public <R> R requireSource(
            Class<R> expectedType
    ) {

        Objects.requireNonNull(
                expectedType,
                "expectedType이 필요합니다."
        );

        if (
                !expectedType.isInstance(
                        source
                )
        ) {

            throw new IllegalStateException(
                    "LifeDomainEvidenceReference Source 타입이 예상과 다릅니다. "
                            + "expected="
                            + expectedType.getName()
                            + ", actual="
                            + source.getClass().getName()
            );
        }

        return expectedType.cast(
                source
        );
    }

    public boolean auxiliary() {
        return tier.auxiliary();
    }
}