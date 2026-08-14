package com.dajeom.model;

import com.dajeom.type.LifeDomainEvidenceSourceType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차-2.
 *
 * 하나의 LuckIntegratedSegmentAnalysis에
 * 연결되는 생활영역 Evidence.
 *
 * =================================================
 *
 * temporalEvidences
 *
 * DECADE_CONTEXT
 * ANNUAL_CONTEXT
 * MONTHLY_CONTEXT
 *
 * =================================================
 *
 * shinsalEvidences
 *
 * 현재 Segment에 실제 적용되는
 * 운 ShinsalEvidence.
 *
 * =================================================
 *
 * 원국 Evidence는 여기 넣지 않는다.
 *
 * LifeDomainEvidenceResolution에서
 * 한 번만 보존한 뒤 Segment Bundle 생성 시
 * 합쳐서 사용한다.
 */
public record LifeDomainSegmentEvidence(

        LuckIntegratedSegmentAnalysis segmentAnalysis,

        List<LifeDomainEvidenceReference<?>> temporalEvidences,

        List<LifeDomainEvidenceReference<ShinsalEvidence>>
        shinsalEvidences

) {

    private static final Set<LifeDomainEvidenceSourceType>
            ALLOWED_TEMPORAL_TYPES =
            Set.of(
                    LifeDomainEvidenceSourceType.DECADE_CONTEXT,
                    LifeDomainEvidenceSourceType.ANNUAL_CONTEXT,
                    LifeDomainEvidenceSourceType.MONTHLY_CONTEXT
            );

    public LifeDomainSegmentEvidence {

        Objects.requireNonNull(
                segmentAnalysis,
                "Segment Evidence에는 LuckIntegratedSegmentAnalysis가 필요합니다."
        );

        temporalEvidences =
                temporalEvidences == null
                        ? List.of()
                        : List.copyOf(
                        temporalEvidences
                );

        shinsalEvidences =
                shinsalEvidences == null
                        ? List.of()
                        : List.copyOf(
                        shinsalEvidences
                );

        /*
         * =================================================
         * Temporal Source
         * =================================================
         */

        for (
                LifeDomainEvidenceReference<?> evidence
                : temporalEvidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "Temporal Evidence는 null일 수 없습니다."
            );

            if (
                    !ALLOWED_TEMPORAL_TYPES
                            .contains(
                                    evidence.sourceType()
                            )
            ) {

                throw new IllegalArgumentException(
                        "Segment temporalEvidences에 허용되지 않은 SourceType이 들어왔습니다: "
                                + evidence.sourceType()
                );
            }
        }

        /*
         * =================================================
         * ANNUAL / MONTHLY는 반드시 하나.
         * =================================================
         */

        long annualCount =
                temporalEvidences
                        .stream()
                        .filter(
                                evidence ->
                                        evidence.sourceType()
                                                == LifeDomainEvidenceSourceType
                                                .ANNUAL_CONTEXT
                        )
                        .count();

        if (
                annualCount != 1
        ) {

            throw new IllegalArgumentException(
                    "하나의 Timeline Segment에는 ANNUAL_CONTEXT Evidence가 정확히 하나 필요합니다."
            );
        }

        long monthlyCount =
                temporalEvidences
                        .stream()
                        .filter(
                                evidence ->
                                        evidence.sourceType()
                                                == LifeDomainEvidenceSourceType
                                                .MONTHLY_CONTEXT
                        )
                        .count();

        if (
                monthlyCount != 1
        ) {

            throw new IllegalArgumentException(
                    "하나의 Timeline Segment에는 MONTHLY_CONTEXT Evidence가 정확히 하나 필요합니다."
            );
        }

        /*
         * =================================================
         * DECADE는 최대 하나.
         * =================================================
         */

        long decadeCount =
                temporalEvidences
                        .stream()
                        .filter(
                                evidence ->
                                        evidence.sourceType()
                                                == LifeDomainEvidenceSourceType
                                                .DECADE_CONTEXT
                        )
                        .count();

        if (
                decadeCount > 1
        ) {

            throw new IllegalArgumentException(
                    "하나의 Timeline Segment에는 DECADE_CONTEXT Evidence가 최대 하나만 존재할 수 있습니다."
            );
        }

        if (
                !segmentAnalysis.hasActiveDecade()
                        && decadeCount > 0
        ) {

            throw new IllegalArgumentException(
                    "ACTIVE가 아닌 Timeline Segment에 DECADE_CONTEXT Evidence를 연결할 수 없습니다."
            );
        }

        /*
         * =================================================
         * Shinsal
         * =================================================
         */

        for (
                LifeDomainEvidenceReference<ShinsalEvidence> reference
                : shinsalEvidences
        ) {

            Objects.requireNonNull(
                    reference,
                    "Shinsal Evidence Reference는 null일 수 없습니다."
            );

            if (
                    reference.sourceType()
                            != LifeDomainEvidenceSourceType.SHINSAL
            ) {

                throw new IllegalArgumentException(
                        "shinsalEvidences에는 SHINSAL SourceType만 들어갈 수 있습니다."
                );
            }

            ShinsalEvidence evidence =
                    reference.source();

            if (
                    !evidence.luckEvidence()
            ) {

                throw new IllegalArgumentException(
                        "Segment shinsalEvidences에는 운 ShinsalEvidence만 연결할 수 있습니다."
                );
            }

            if (
                    !evidence.appliesTo(
                            segmentAnalysis
                    )
            ) {

                throw new IllegalArgumentException(
                        "현재 Timeline Segment에 적용되지 않는 ShinsalEvidence가 연결되었습니다: "
                                + evidence.identityKey()
                );
            }
        }

        /*
         * =================================================
         * Source Key 중복 금지
         * =================================================
         */

        Set<String> keys =
                new HashSet<>();

        for (
                LifeDomainEvidenceReference<?> evidence
                : temporalEvidences
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
                        "Segment Temporal Evidence가 중복되었습니다: "
                                + key
                );
            }
        }

        for (
                LifeDomainEvidenceReference<ShinsalEvidence> evidence
                : shinsalEvidences
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
                        "Segment Evidence가 중복되었습니다: "
                                + key
                );
            }
        }
    }

    /*
     * =================================================
     * Temporal
     * =================================================
     */

    public List<LifeDomainEvidenceReference<?>>
    decadeEvidences() {

        return temporalOf(
                LifeDomainEvidenceSourceType.DECADE_CONTEXT
        );
    }

    public LifeDomainEvidenceReference<?>
    annualEvidence() {

        return temporalOf(
                LifeDomainEvidenceSourceType.ANNUAL_CONTEXT
        )
                .get(0);
    }

    public LifeDomainEvidenceReference<?>
    monthlyEvidence() {

        return temporalOf(
                LifeDomainEvidenceSourceType.MONTHLY_CONTEXT
        )
                .get(0);
    }

    public List<LifeDomainEvidenceReference<?>>
    temporalOf(
            LifeDomainEvidenceSourceType sourceType
    ) {

        if (
                sourceType == null
                        || !ALLOWED_TEMPORAL_TYPES.contains(
                        sourceType
                )
        ) {

            return List.of();
        }

        return temporalEvidences
                .stream()
                .filter(
                        evidence ->
                                evidence.sourceType()
                                        == sourceType
                )
                .toList();
    }

    /*
     * =================================================
     * Shinsal
     * =================================================
     */

    public boolean hasShinsalEvidences() {

        return !shinsalEvidences
                .isEmpty();
    }
}