package com.dajeom.model;

import com.dajeom.constant.LifeDomainEvidencePolicyRegistry;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainEvidenceTier;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차-1.
 *
 * 하나의 생활영역 Rule들이 사용할
 * 정규화 Evidence Bundle.
 *
 * =================================================
 *
 * 이 객체 자체는:
 *
 * 재물 좋음
 * 직업 나쁨
 *
 * 등의 결론을 만들지 않는다.
 *
 * 기존 Evidence Source를
 * 정책에 맞게 묶어 주는 역할이다.
 */
public record LifeDomainEvidenceBundle(

        LifeDomain domain,

        List<LifeDomainEvidenceReference<?>> evidences

) {

    public LifeDomainEvidenceBundle {

        Objects.requireNonNull(
                domain,
                "LifeDomainEvidenceBundle에는 LifeDomain이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : evidences
                        .stream()
                        .peek(
                                evidence ->
                                        Objects.requireNonNull(
                                                evidence,
                                                "LifeDomainEvidenceReference는 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator
                                        .comparing(
                                                (
                                                        LifeDomainEvidenceReference<?> evidence
                                                ) ->
                                                        evidence
                                                                .tier()
                                                                .ordinal()
                                        )
                                        .thenComparing(
                                                evidence ->
                                                        evidence
                                                                .sourceType()
                                                                .ordinal()
                                        )
                                        .thenComparing(
                                                LifeDomainEvidenceReference
                                                        ::sourceKey
                                        )
                        )
                        .toList();

        /*
         * 같은 Source Key 중복 금지.
         */
        Set<String> keys =
                new HashSet<>();

        for (
                LifeDomainEvidenceReference<?> evidence
                : evidences
        ) {

            String key =
                    evidence.sourceType().name()
                            + "|"
                            + evidence.sourceKey();

            if (
                    !keys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 생활영역 Evidence가 중복되었습니다: "
                                + key
                );
            }
        }
    }

    /*
     * =================================================
     * Policy 검증
     * =================================================
     */

    public void validatePolicy(
            LifeDomainEvidencePolicyRegistry registry
    ) {

        Objects.requireNonNull(
                registry,
                "LifeDomainEvidencePolicyRegistry가 필요합니다."
        );

        for (
                LifeDomainEvidenceReference<?> evidence
                : evidences
        ) {

            if (
                    !registry.supports(
                            domain,
                            evidence.sourceType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "현재 생활영역에서 사용할 수 없는 Evidence Source입니다. "
                                + "domain="
                                + domain
                                + ", source="
                                + evidence.sourceType()
                );
            }
        }
    }

    /*
     * =================================================
     * 조회
     * =================================================
     */

    public List<LifeDomainEvidenceReference<?>>
    evidencesOf(
            LifeDomainEvidenceTier tier
    ) {

        if (
                tier == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.tier()
                                        == tier
                )
                .toList();
    }

    public List<LifeDomainEvidenceReference<?>>
    evidencesOf(
            LifeDomainEvidenceSourceType sourceType
    ) {

        if (
                sourceType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.sourceType()
                                        == sourceType
                )
                .toList();
    }

    public boolean has(
            LifeDomainEvidenceSourceType sourceType
    ) {

        return !evidencesOf(
                sourceType
        )
                .isEmpty();
    }

    public boolean hasCoreStructure() {

        return !evidencesOf(
                LifeDomainEvidenceTier.CORE_STRUCTURE
        )
                .isEmpty();
    }

    public boolean hasTemporalContext() {

        return !evidencesOf(
                LifeDomainEvidenceTier.TEMPORAL_CONTEXT
        )
                .isEmpty();
    }

    public boolean hasAuxiliaryEvidence() {

        return !evidencesOf(
                LifeDomainEvidenceTier.AUXILIARY
        )
                .isEmpty();
    }

    public int evidenceCount() {
        return evidences.size();
    }
}