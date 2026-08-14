package com.dajeom.model;

import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainEvidenceTier;
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 정밀화 18차-3.
 *
 * 생활영역 Rule이 생성하는
 * 최소 deterministic 해석 Signal.
 *
 * =================================================
 *
 * LifeDomainEvidenceReference를
 * 문자열로 복사하지 않고
 * 실제 Reference 객체 그대로 보존한다.
 *
 * =================================================
 *
 * 이 객체는:
 *
 * 돈을 번다
 * 취업한다
 * 결혼한다
 * 합격한다
 * 병이 생긴다
 *
 * 같은 사건 결과를 나타내지 않는다.
 */
public record LifeDomainSignal(

        LifeDomain domain,

        LifeDomainSignalStatus status,

        String code,

        String summary,

        List<LifeDomainEvidenceReference<?>> evidences,

        List<String> reasons

) {

    public LifeDomainSignal {

        Objects.requireNonNull(
                domain,
                "LifeDomainSignal에는 LifeDomain이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "LifeDomainSignal에는 상태가 필요합니다."
        );

        if (
                code == null
                        || code.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainSignal에는 code가 필요합니다."
            );
        }

        code =
                code.trim();

        if (
                summary == null
                        || summary.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainSignal에는 summary가 필요합니다."
            );
        }

        summary =
                summary.trim();

        evidences =
                evidences == null
                        ? List.of()
                        : evidences
                        .stream()
                        .peek(
                                evidence ->
                                        Objects.requireNonNull(
                                                evidence,
                                                "LifeDomainSignal Evidence는 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator
                                        .comparingInt(
                                                (
                                                        LifeDomainEvidenceReference<?> evidence
                                                ) ->
                                                        evidence
                                                                .tier()
                                                                .ordinal()
                                        )
                                        .thenComparingInt(
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

        if (
                evidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainSignal에는 최소 하나의 Evidence가 필요합니다."
            );
        }

        Set<String> evidenceKeys =
                new HashSet<>();

        for (
                LifeDomainEvidenceReference<?> evidence
                : evidences
        ) {

            String key =
                    evidence
                            .sourceType()
                            .name()
                            + "|"
                            + evidence
                            .sourceKey();

            if (
                    !evidenceKeys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "LifeDomainSignal에 동일 Evidence가 중복되었습니다: "
                                + key
                );
            }
        }

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
                    "LifeDomainSignal에는 최소 하나의 reason이 필요합니다."
            );
        }
    }

    public int evidenceCount() {

        return evidences
                .size();
    }

    public Set<LifeDomainEvidenceSourceType>
    sourceTypes() {

        return evidences
                .stream()
                .map(
                        LifeDomainEvidenceReference
                                ::sourceType
                )
                .collect(
                        Collectors
                                .toUnmodifiableSet()
                );
    }

    public int distinctSourceTypeCount() {

        return sourceTypes()
                .size();
    }

    public boolean hasSource(
            LifeDomainEvidenceSourceType sourceType
    ) {

        return sourceType != null
                && evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence
                                        .sourceType()
                                        == sourceType
                );
    }

    public boolean hasTier(
            LifeDomainEvidenceTier tier
    ) {

        return tier != null
                && evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence
                                        .tier()
                                        == tier
                );
    }

    public boolean hasCoreStructureEvidence() {

        return hasTier(
                LifeDomainEvidenceTier
                        .CORE_STRUCTURE
        );
    }

    public boolean hasStructuralContextEvidence() {

        return hasTier(
                LifeDomainEvidenceTier
                        .STRUCTURAL_CONTEXT
        );
    }

    public boolean hasTemporalContextEvidence() {

        return hasTier(
                LifeDomainEvidenceTier
                        .TEMPORAL_CONTEXT
        );
    }

    public boolean hasAuxiliaryEvidence() {

        return hasTier(
                LifeDomainEvidenceTier
                        .AUXILIARY
        );
    }

    public boolean auxiliaryOnly() {

        return evidences
                .stream()
                .allMatch(
                        evidence ->
                                evidence
                                        .tier()
                                        .auxiliary()
                );
    }
}