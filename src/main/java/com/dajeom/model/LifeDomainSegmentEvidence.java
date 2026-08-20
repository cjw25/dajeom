package com.dajeom.model;

import com.dajeom.type.LifeDomainEvidenceSourceType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * ANNUAL_FUNCTIONAL_CONTEXT
 * MONTHLY_CONTEXT
 *
 * =================================================
 *
 * ANNUAL_CONTEXT:
 *
 * 기존 14-6 AnnualLuckContextAnalysis.
 *
 * 정확히 하나.
 *
 * =================================================
 *
 * ANNUAL_FUNCTIONAL_CONTEXT:
 *
 * 신규 Annual Functional 최종 기준선.
 *
 * 아직 신규 Functional Chain을 연결하지 않은
 * 기존 호출 경로도 유지해야 하므로
 * 0개 또는 1개를 허용한다.
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
                    LifeDomainEvidenceSourceType
                            .ANNUAL_FUNCTIONAL_CONTEXT,
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
         * 기존 ANNUAL은 반드시 하나.
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

        /*
         * =================================================
         * Annual Functional은 최대 하나.
         * =================================================
         *
         * 기존 18차 호출 경로 호환성을 위해
         * 아직 0개도 허용한다.
         */

        long annualFunctionalCount =
                temporalEvidences
                        .stream()
                        .filter(
                                evidence ->
                                        evidence.sourceType()
                                                == LifeDomainEvidenceSourceType
                                                .ANNUAL_FUNCTIONAL_CONTEXT
                        )
                        .count();

        if (
                annualFunctionalCount > 1
        ) {

            throw new IllegalArgumentException(
                    "하나의 Timeline Segment에는 ANNUAL_FUNCTIONAL_CONTEXT Evidence가 최대 하나만 존재할 수 있습니다."
            );
        }

        /*
         * =================================================
         * MONTHLY는 반드시 하나.
         * =================================================
         */

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
         * Annual Functional Source Type 검증
         * =================================================
         */

        for (
                LifeDomainEvidenceReference<?> evidence
                : temporalEvidences
        ) {

            if (
                    evidence.sourceType()
                            != LifeDomainEvidenceSourceType
                            .ANNUAL_FUNCTIONAL_CONTEXT
            ) {

                continue;
            }

            if (
                    !(evidence.source()
                            instanceof AnnualFunctionalLifeDomainContext)
            ) {

                throw new IllegalArgumentException(
                        "ANNUAL_FUNCTIONAL_CONTEXT Source는 AnnualFunctionalLifeDomainContext여야 합니다."
                );
            }
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

    /**
     * 신규 Annual Functional Evidence.
     *
     * 기존 호출 경로에서는 없을 수 있다.
     */
    public Optional<LifeDomainEvidenceReference<?>>
    annualFunctionalEvidence() {

        List<LifeDomainEvidenceReference<?>> evidences =
                temporalOf(
                        LifeDomainEvidenceSourceType
                                .ANNUAL_FUNCTIONAL_CONTEXT
                );

        if (
                evidences.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                evidences.size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "ANNUAL_FUNCTIONAL_CONTEXT Evidence가 2개 이상입니다."
            );
        }

        return Optional.of(
                evidences.get(0)
        );
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