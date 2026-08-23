package com.dajeom.resolver;

import com.dajeom.constant.LifeDomainEvidencePolicyRegistry;
import com.dajeom.model.AnnualFunctionalBaselineAnalysis;
import com.dajeom.model.AnnualFunctionalLifeDomainContext;
import com.dajeom.model.AnnualLuckContextAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainEvidenceResolution;
import com.dajeom.model.LifeDomainSegmentEvidence;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckIntegratedSegmentAnalysis;
import com.dajeom.model.MonthlyFunctionalActivationAnalysis;
import com.dajeom.model.MonthlyFunctionalLifeDomainContext;
import com.dajeom.model.MonthlyLuckContextAnalysis;
import com.dajeom.model.ShinsalAnalysis;
import com.dajeom.model.ShinsalEvidence;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainEvidenceTier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 18차-2.
 *
 * 기존 deterministic 결과를
 * 생활영역 Evidence Source로 연결한다.
 *
 * =================================================
 *
 * 직접 연결:
 *
 * 12차 PalaceAnalysis
 * 14차 AnnualLuckContextAnalysis
 * Annual Functional Baseline
 * 15차 MonthlyLuckContextAnalysis
 * Monthly Functional Activation
 * 17차 ShinsalEvidence
 *
 * =================================================
 *
 * Boundary 연결:
 *
 * 3~11차 원국 분석
 * 13차 DecadeLuckContext
 *
 * =================================================
 *
 * 중요한 정책:
 *
 * 이 Resolver에서는
 * 기존 명리 분석을 다시 계산하지 않는다.
 *
 * upstream 분석 객체를
 * LifeDomainEvidenceReference.source에
 * typed source로 연결한다.
 *
 * =================================================
 *
 * 기존 ANNUAL_CONTEXT:
 *
 * AnnualLuckContextAnalysis를 그대로 유지한다.
 *
 * =================================================
 *
 * ANNUAL_FUNCTIONAL_CONTEXT:
 *
 * AnnualFunctionalBaselineAnalysis를
 * 현재 LuckIntegratedSegmentAnalysis와 함께
 * AnnualFunctionalLifeDomainContext로 연결한다.
 *
 * =================================================
 *
 * 기존 MONTHLY_CONTEXT:
 *
 * MonthlyLuckContextAnalysis를 그대로 유지한다.
 *
 * =================================================
 *
 * MONTHLY_FUNCTIONAL_CONTEXT:
 *
 * MonthlyFunctionalActivationAnalysis를
 * 현재 LuckIntegratedSegmentAnalysis와 함께
 * MonthlyFunctionalLifeDomainContext로 연결한다.
 *
 * =================================================
 *
 * 기존 Context와 Functional Context는
 * 서로 대체하지 않는다.
 *
 * 단 Functional Context는 derived temporal source이므로
 * 이후 Prominence / Independence 판정에서
 * 원본 Context와 독립 근거로 단순 합산하지 않는다.
 */
@Component
public class LifeDomainEvidenceResolver {

    private static final Set<LifeDomainEvidenceSourceType>
            ALLOWED_NATAL_ADAPTER_TYPES =
            Set.copyOf(
                    EnumSet.of(
                            LifeDomainEvidenceSourceType.ELEMENT_STRENGTH,
                            LifeDomainEvidenceSourceType.DAY_MASTER_STRENGTH,
                            LifeDomainEvidenceSourceType.STRUCTURE,
                            LifeDomainEvidenceSourceType.CLIMATE_DISEASE_FLOW,
                            LifeDomainEvidenceSourceType.USEFUL_GOD,
                            LifeDomainEvidenceSourceType.TEN_GOD_COMPOSITE,
                            LifeDomainEvidenceSourceType.TWELVE_LIFE_STAGE,
                            LifeDomainEvidenceSourceType.NATAL_RELATION,
                            LifeDomainEvidenceSourceType.TRANSFORMATION
                    )
            );

    private final LifeDomainEvidencePolicyRegistry
            policyRegistry;

    public LifeDomainEvidenceResolver(
            LifeDomainEvidencePolicyRegistry policyRegistry
    ) {

        this.policyRegistry =
                Objects.requireNonNull(
                        policyRegistry,
                        "LifeDomainEvidencePolicyRegistry가 필요합니다."
                );
    }

    /*
     * =================================================
     * Annual + Monthly Functional 공식 API
     * =================================================
     */

    public LifeDomainEvidenceResolution resolve(

            LuckIntegratedAnalysis luckIntegratedAnalysis,

            ShinsalAnalysis shinsalAnalysis,

            LifeDomainNatalEvidenceSourceResolver
                    natalEvidenceSourceResolver,

            LifeDomainDecadeContextSourceResolver
                    decadeContextSourceResolver,

            LifeDomainAnnualFunctionalSourceResolver
                    annualFunctionalSourceResolver,

            LifeDomainMonthlyFunctionalSourceResolver
                    monthlyFunctionalSourceResolver

    ) {

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "18-2에는 LuckIntegratedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                shinsalAnalysis,
                "18-2에는 ShinsalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalEvidenceSourceResolver,
                "18-2에는 LifeDomainNatalEvidenceSourceResolver가 필요합니다."
        );

        Objects.requireNonNull(
                decadeContextSourceResolver,
                "18-2에는 LifeDomainDecadeContextSourceResolver가 필요합니다."
        );

        Objects.requireNonNull(
                annualFunctionalSourceResolver,
                "18-2 Annual Functional 연결에는 LifeDomainAnnualFunctionalSourceResolver가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyFunctionalSourceResolver,
                "18-2 Monthly Functional 연결에는 LifeDomainMonthlyFunctionalSourceResolver가 필요합니다."
        );

        if (
                shinsalAnalysis
                        .luckIntegratedAnalysis()
                        != luckIntegratedAnalysis
        ) {

            throw new IllegalArgumentException(
                    "LuckIntegratedAnalysis와 ShinsalAnalysis의 Anchor가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Temporal Reference Canonicalization
         * =================================================
         *
         * 기존 Annual / Monthly / Decade Context는
         * 같은 upstream 객체가 여러 Timeline Segment에
         * 재사용되면 Reference도 재사용한다.
         *
         * AnnualFunctionalLifeDomainContext와
         * MonthlyFunctionalLifeDomainContext는
         * Timeline Segment 자체를 포함하므로
         * Segment별 객체를 생성한다.
         */

        Map<
                Object,
                LifeDomainEvidenceReference<?>
                > temporalReferenceCache =
                new IdentityHashMap<>();

        /*
         * =================================================
         * Shinsal Reference Canonicalization
         * =================================================
         */

        Map<
                ShinsalEvidence,
                LifeDomainEvidenceReference<ShinsalEvidence>
                > shinsalReferenceCache =
                new IdentityHashMap<>();

        /*
         * =================================================
         * 1. NATAL Evidence
         * =================================================
         */

        List<LifeDomainEvidenceReference<?>> natalEvidences =
                buildNatalEvidences(
                        luckIntegratedAnalysis,
                        shinsalAnalysis,
                        natalEvidenceSourceResolver,
                        shinsalReferenceCache
                );

        /*
         * =================================================
         * 2. Segment Evidence
         * =================================================
         */

        List<LifeDomainSegmentEvidence> segmentEvidences =
                new ArrayList<>(
                        luckIntegratedAnalysis
                                .segmentAnalyses()
                                .size()
                );

        for (
                LuckIntegratedSegmentAnalysis segmentAnalysis
                : luckIntegratedAnalysis.segmentAnalyses()
        ) {

            segmentEvidences.add(
                    buildSegmentEvidence(
                            segmentAnalysis,
                            shinsalAnalysis,
                            decadeContextSourceResolver,
                            annualFunctionalSourceResolver,
                            monthlyFunctionalSourceResolver,
                            temporalReferenceCache,
                            shinsalReferenceCache
                    )
            );
        }

        return new LifeDomainEvidenceResolution(
                luckIntegratedAnalysis,
                shinsalAnalysis,
                natalEvidences,
                segmentEvidences
        );
    }

    /*
     * =================================================
     * NATAL
     * =================================================
     */

    private List<LifeDomainEvidenceReference<?>>
    buildNatalEvidences(

            LuckIntegratedAnalysis luckIntegratedAnalysis,

            ShinsalAnalysis shinsalAnalysis,

            LifeDomainNatalEvidenceSourceResolver
                    natalEvidenceSourceResolver,

            Map<
                    ShinsalEvidence,
                    LifeDomainEvidenceReference<ShinsalEvidence>
                    > shinsalReferenceCache

    ) {

        List<LifeDomainEvidenceReference<?>> result =
                new ArrayList<>();

        /*
         * =================================================
         * 3~11차
         * =================================================
         */

        List<LifeDomainEvidenceReference<?>> resolved =
                natalEvidenceSourceResolver
                        .resolveNatalEvidences(
                                luckIntegratedAnalysis
                        );

        if (
                resolved == null
        ) {

            throw new IllegalStateException(
                    "LifeDomainNatalEvidenceSourceResolver는 null을 반환할 수 없습니다."
            );
        }

        for (
                LifeDomainEvidenceReference<?> evidence
                : resolved
        ) {

            Objects.requireNonNull(
                    evidence,
                    "원국 Evidence Reference는 null일 수 없습니다."
            );

            if (
                    !ALLOWED_NATAL_ADAPTER_TYPES
                            .contains(
                                    evidence.sourceType()
                            )
            ) {

                throw new IllegalArgumentException(
                        "Natal Evidence Adapter가 허용되지 않은 SourceType을 반환했습니다: "
                                + evidence.sourceType()
                );
            }

            result.add(
                    evidence
            );
        }

        /*
         * =================================================
         * 12차 Palace
         * =================================================
         */

        result.add(
                new LifeDomainEvidenceReference<>(
                        LifeDomainEvidenceSourceType.PALACE,
                        LifeDomainEvidenceTier.STRUCTURAL_CONTEXT,
                        "NATAL:PALACE",
                        luckIntegratedAnalysis.natalAnalysis(),
                        "원국 궁위 분석",
                        List.of(
                                "12차 PalaceAnalysis 원본 객체를 생활영역 구조적 Context로 연결합니다.",
                                "년·월·일·시의 위치 의미를 기존 궁위 분석 그대로 사용합니다."
                        )
                )
        );

        /*
         * =================================================
         * 17차 원국 신살
         * =================================================
         */

        for (
                ShinsalEvidence shinsalEvidence
                : shinsalAnalysis.natalEvidences()
        ) {

            LifeDomainEvidenceReference<ShinsalEvidence> reference =
                    shinsalReferenceCache
                            .computeIfAbsent(
                                    shinsalEvidence,
                                    this::createShinsalReference
                            );

            result.add(
                    reference
            );
        }

        validateNoDuplicateSourceKeys(
                result,
                "원국"
        );

        return sortReferences(
                result
        );
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    private LifeDomainSegmentEvidence
    buildSegmentEvidence(

            LuckIntegratedSegmentAnalysis segmentAnalysis,

            ShinsalAnalysis shinsalAnalysis,

            LifeDomainDecadeContextSourceResolver
                    decadeContextSourceResolver,

            LifeDomainAnnualFunctionalSourceResolver
                    annualFunctionalSourceResolver,

            LifeDomainMonthlyFunctionalSourceResolver
                    monthlyFunctionalSourceResolver,

            Map<
                    Object,
                    LifeDomainEvidenceReference<?>
                    > temporalReferenceCache,

            Map<
                    ShinsalEvidence,
                    LifeDomainEvidenceReference<ShinsalEvidence>
                    > shinsalReferenceCache

    ) {

        List<LifeDomainEvidenceReference<?>> temporalEvidences =
                new ArrayList<>();

        /*
         * =================================================
         * 13차 Decade Context
         * =================================================
         */

        Optional<LifeDomainEvidenceReference<?>> decadeReference =
                decadeContextSourceResolver
                        .resolveDecadeContext(
                                segmentAnalysis
                        );

        if (
                decadeReference == null
        ) {

            throw new IllegalStateException(
                    "LifeDomainDecadeContextSourceResolver는 null Optional을 반환할 수 없습니다."
            );
        }

        if (
                !segmentAnalysis.hasActiveDecade()
                        && decadeReference.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "ACTIVE가 아닌 Segment에서 DECADE_CONTEXT Evidence를 반환할 수 없습니다."
            );
        }

        decadeReference
                .ifPresent(
                        reference -> {

                            validateDecadeReference(
                                    reference
                            );

                            temporalEvidences.add(
                                    canonicalize(
                                            temporalReferenceCache,
                                            reference
                                    )
                            );
                        }
                );

        /*
         * =================================================
         * 기존 14차 Annual Context
         * =================================================
         */

        AnnualLuckContextAnalysis annualContextAnalysis =
                segmentAnalysis
                        .annualContextAnalysis();

        LifeDomainEvidenceReference<
                AnnualLuckContextAnalysis
                > annualReference =
                new LifeDomainEvidenceReference<>(
                        LifeDomainEvidenceSourceType.ANNUAL_CONTEXT,
                        LifeDomainEvidenceTier.TEMPORAL_CONTEXT,
                        annualSourceKey(
                                segmentAnalysis
                        ),
                        annualContextAnalysis,
                        "현재 세운 Context",
                        List.of(
                                "14차 AnnualLuckContextAnalysis 원본 객체를 그대로 연결합니다.",
                                "현재 Timeline Segment가 소유한 실제 AnnualLuckPeriod의 Context입니다."
                        )
                );

        temporalEvidences.add(
                canonicalize(
                        temporalReferenceCache,
                        annualReference
                )
        );

        /*
         * =================================================
         * Annual Functional Context
         * =================================================
         */

        Optional<AnnualFunctionalBaselineAnalysis>
                annualFunctionalOptional =
                annualFunctionalSourceResolver
                        .resolveAnnualFunctionalBaseline(
                                segmentAnalysis
                        );

        if (
                annualFunctionalOptional == null
        ) {

            throw new IllegalStateException(
                    "LifeDomainAnnualFunctionalSourceResolver는 null Optional을 반환할 수 없습니다."
            );
        }

        AnnualFunctionalBaselineAnalysis baseline =
                annualFunctionalOptional
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "현재 Timeline Segment에는 ANNUAL_FUNCTIONAL_CONTEXT Evidence가 정확히 하나 필요합니다."
                                        )
                        );

        AnnualFunctionalLifeDomainContext functionalContext =
                new AnnualFunctionalLifeDomainContext(
                        segmentAnalysis,
                        baseline
                );

        LifeDomainEvidenceReference<
                AnnualFunctionalLifeDomainContext
                > functionalReference =
                new LifeDomainEvidenceReference<>(
                        LifeDomainEvidenceSourceType
                                .ANNUAL_FUNCTIONAL_CONTEXT,
                        LifeDomainEvidenceTier
                                .TEMPORAL_CONTEXT,
                        annualFunctionalSourceKey(
                                segmentAnalysis
                        ),
                        functionalContext,
                        "현재 세운 Functional 기준선",
                        List.of(
                                "AnnualFunctionalBaselineAnalysis의 계산 결과를 현재 16차 Timeline Segment에 typed context로 연결합니다.",
                                "기존 14-6 ANNUAL_CONTEXT를 대체하거나 복제하지 않습니다.",
                                "Function 활성·CrossFunctional Pattern·Transformation·Natal Capacity·Context Qualification의 provenance를 그대로 보존합니다.",
                                "이 Evidence 하나만으로 생활영역의 길흉이나 실제 사건을 확정하지 않습니다."
                        )
                );

        temporalEvidences.add(
                functionalReference
        );

        /*
         * =================================================
         * 기존 15차 Monthly Context
         * =================================================
         */

        MonthlyLuckContextAnalysis monthlyContextAnalysis =
                segmentAnalysis
                        .monthlyContextAnalysis();

        LifeDomainEvidenceReference<
                MonthlyLuckContextAnalysis
                > monthlyReference =
                new LifeDomainEvidenceReference<>(
                        LifeDomainEvidenceSourceType.MONTHLY_CONTEXT,
                        LifeDomainEvidenceTier.TEMPORAL_CONTEXT,
                        monthlySourceKey(
                                segmentAnalysis
                        ),
                        monthlyContextAnalysis,
                        "현재 월운 Context",
                        List.of(
                                "15차 MonthlyLuckContextAnalysis 원본 객체를 그대로 연결합니다.",
                                "현재 Timeline Segment가 소유한 실제 MonthlyLuckPeriod의 Context입니다."
                        )
                );

        temporalEvidences.add(
                canonicalize(
                        temporalReferenceCache,
                        monthlyReference
                )
        );

        /*
         * =================================================
         * Monthly Functional Context
         * =================================================
         *
         * 공식 Monthly Functional Pipeline이
         * 제공한 결과를 정확히 하나 연결한다.
         *
         * 기존 MONTHLY_CONTEXT는 그대로 유지한다.
         */

        Optional<MonthlyFunctionalActivationAnalysis>
                monthlyFunctionalOptional =
                monthlyFunctionalSourceResolver
                        .resolveMonthlyFunctionalActivation(
                                segmentAnalysis
                        );

        if (
                monthlyFunctionalOptional == null
        ) {

            throw new IllegalStateException(
                    "LifeDomainMonthlyFunctionalSourceResolver는 null Optional을 반환할 수 없습니다."
            );
        }

        MonthlyFunctionalActivationAnalysis activationAnalysis =
                monthlyFunctionalOptional
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "현재 Timeline Segment에는 MONTHLY_FUNCTIONAL_CONTEXT Evidence가 정확히 하나 필요합니다."
                                        )
                        );

        /*
         * Timeline의 실제 MonthlyLuckAnalysis와
         * 반드시 Identity가 같아야 한다.
         *
         * 연도/월 숫자로만 연결하지 않는다.
         */
        if (
                activationAnalysis
                        .monthlyLuckAnalysis()
                        != segmentAnalysis
                        .monthlyLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Source Resolver가 현재 Timeline Segment와 "
                            + "다른 MonthlyLuckAnalysis Identity를 반환했습니다. "
                            + "sajuYear="
                            + segmentAnalysis
                            .monthlyLuckAnalysis()
                            .sajuYear()
                            + ", monthIndex="
                            + segmentAnalysis
                            .monthlyLuckAnalysis()
                            .monthIndex()
            );
        }

        MonthlyFunctionalLifeDomainContext monthlyFunctionalContext =
                new MonthlyFunctionalLifeDomainContext(
                        segmentAnalysis,
                        activationAnalysis
                );

        LifeDomainEvidenceReference<
                MonthlyFunctionalLifeDomainContext
                > monthlyFunctionalReference =
                new LifeDomainEvidenceReference<>(
                        LifeDomainEvidenceSourceType
                                .MONTHLY_FUNCTIONAL_CONTEXT,
                        LifeDomainEvidenceTier
                                .TEMPORAL_CONTEXT,
                        monthlyFunctionalSourceKey(
                                segmentAnalysis
                        ),
                        monthlyFunctionalContext,
                        "현재 월운 Functional 활성",
                        List.of(
                                "MonthlyFunctionalActivationAnalysis의 계산 결과를 현재 16차 Timeline Segment에 typed context로 연결합니다.",
                                "공식 Annual Functional Baseline은 변경하지 않고 월운의 Function 접촉 상태만 보존합니다.",
                                "기존 15-7 MONTHLY_CONTEXT를 대체하지 않습니다.",
                                "같은 월의 MONTHLY_CONTEXT와 MONTHLY_FUNCTIONAL_CONTEXT를 서로 독립된 명리근거 개수로 단순 합산해서는 안 됩니다.",
                                "Function Contact 하나만으로 생활영역의 길흉이나 실제 사건을 확정하지 않습니다."
                        )
                );

        temporalEvidences.add(
                monthlyFunctionalReference
        );

        /*
         * =================================================
         * 17차 운 신살
         * =================================================
         */

        List<
                LifeDomainEvidenceReference<ShinsalEvidence>
                > shinsalEvidences =
                new ArrayList<>();

        for (
                ShinsalEvidence shinsalEvidence
                : shinsalAnalysis.luckEvidences()
        ) {

            if (
                    !shinsalEvidence
                            .appliesTo(
                                    segmentAnalysis
                            )
            ) {

                continue;
            }

            shinsalEvidences.add(
                    shinsalReferenceCache
                            .computeIfAbsent(
                                    shinsalEvidence,
                                    this::createShinsalReference
                            )
            );
        }

        return new LifeDomainSegmentEvidence(
                segmentAnalysis,
                sortReferences(
                        temporalEvidences
                ),
                List.copyOf(
                        shinsalEvidences
                )
        );
    }

    /*
     * =================================================
     * Domain Bundle
     * =================================================
     */

    public LifeDomainEvidenceBundle resolveNatalBundle(

            LifeDomainEvidenceResolution resolution,

            LifeDomain domain

    ) {

        Objects.requireNonNull(
                resolution,
                "LifeDomainEvidenceResolution이 필요합니다."
        );

        Objects.requireNonNull(
                domain,
                "LifeDomain이 필요합니다."
        );

        return buildBundle(
                domain,
                resolution.natalEvidences()
        );
    }

    public LifeDomainEvidenceBundle resolveSegmentBundle(

            LifeDomainEvidenceResolution resolution,

            LifeDomain domain,

            LuckIntegratedSegmentAnalysis segmentAnalysis

    ) {

        Objects.requireNonNull(
                resolution,
                "LifeDomainEvidenceResolution이 필요합니다."
        );

        Objects.requireNonNull(
                domain,
                "LifeDomain이 필요합니다."
        );

        Objects.requireNonNull(
                segmentAnalysis,
                "LuckIntegratedSegmentAnalysis가 필요합니다."
        );

        List<LifeDomainEvidenceReference<?>> evidences =
                resolution
                        .evidencesForSegment(
                                segmentAnalysis
                        );

        return buildBundle(
                domain,
                evidences
        );
    }

    public List<LifeDomainEvidenceBundle>
    resolveSegmentBundles(

            LifeDomainEvidenceResolution resolution,

            LifeDomain domain

    ) {

        Objects.requireNonNull(
                resolution
        );

        Objects.requireNonNull(
                domain
        );

        List<LifeDomainEvidenceBundle> result =
                new ArrayList<>(
                        resolution.segmentCount()
                );

        for (
                LifeDomainSegmentEvidence segmentEvidence
                : resolution.segmentEvidences()
        ) {

            result.add(
                    resolveSegmentBundle(
                            resolution,
                            domain,
                            segmentEvidence.segmentAnalysis()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Bundle Filtering
     * =================================================
     */

    private LifeDomainEvidenceBundle buildBundle(

            LifeDomain domain,

            List<LifeDomainEvidenceReference<?>> evidences

    ) {

        List<LifeDomainEvidenceReference<?>> supported =
                evidences
                        .stream()
                        .filter(
                                evidence ->
                                        policyRegistry.supports(
                                                domain,
                                                evidence.sourceType()
                                        )
                        )
                        .toList();

        LifeDomainEvidenceBundle bundle =
                new LifeDomainEvidenceBundle(
                        domain,
                        supported
                );

        bundle.validatePolicy(
                policyRegistry
        );

        return bundle;
    }

    /*
     * =================================================
     * Shinsal Reference
     * =================================================
     */

    private LifeDomainEvidenceReference<ShinsalEvidence>
    createShinsalReference(
            ShinsalEvidence evidence
    ) {

        return new LifeDomainEvidenceReference<>(
                LifeDomainEvidenceSourceType.SHINSAL,
                LifeDomainEvidenceTier.AUXILIARY,
                "SHINSAL:"
                        + evidence.identityKey(),
                evidence,
                evidence.type().koreanName()
                        + " 신살 Evidence - "
                        + evidence.certainty().koreanName(),
                evidence.reasons()
        );
    }

    /*
     * =================================================
     * DECADE Adapter 검증
     * =================================================
     */

    private void validateDecadeReference(
            LifeDomainEvidenceReference<?> reference
    ) {

        Objects.requireNonNull(
                reference
        );

        if (
                reference.sourceType()
                        != LifeDomainEvidenceSourceType.DECADE_CONTEXT
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainDecadeContextSourceResolver는 DECADE_CONTEXT Source만 반환해야 합니다."
            );
        }

        if (
                reference.tier()
                        != LifeDomainEvidenceTier.TEMPORAL_CONTEXT
        ) {

            throw new IllegalArgumentException(
                    "DECADE_CONTEXT Evidence의 Tier는 TEMPORAL_CONTEXT여야 합니다."
            );
        }
    }

    /*
     * =================================================
     * Canonical Reference
     * =================================================
     */

    private LifeDomainEvidenceReference<?>
    canonicalize(

            Map<
                    Object,
                    LifeDomainEvidenceReference<?>
                    > cache,

            LifeDomainEvidenceReference<?> candidate

    ) {

        Objects.requireNonNull(
                candidate
        );

        Object source =
                candidate.source();

        LifeDomainEvidenceReference<?> existing =
                cache.get(
                        source
                );

        if (
                existing == null
        ) {

            cache.put(
                    source,
                    candidate
            );

            return candidate;
        }

        if (
                existing.sourceType()
                        != candidate.sourceType()
        ) {

            throw new IllegalStateException(
                    "동일 upstream 객체가 서로 다른 LifeDomainEvidenceSourceType으로 연결되었습니다."
            );
        }

        if (
                !existing
                        .sourceKey()
                        .equals(
                                candidate.sourceKey()
                        )
        ) {

            throw new IllegalStateException(
                    "동일 upstream 객체의 LifeDomainEvidence sourceKey가 일치하지 않습니다."
            );
        }

        return existing;
    }

    /*
     * =================================================
     * Source Key
     * =================================================
     */

    private String annualSourceKey(
            LuckIntegratedSegmentAnalysis segment
    ) {

        return "ANNUAL:"
                + segment
                .annualCandidate()
                .targetYear();
    }

    private String annualFunctionalSourceKey(
            LuckIntegratedSegmentAnalysis segment
    ) {

        return "ANNUAL_FUNCTIONAL:"
                + segment
                .annualCandidate()
                .targetYear();
    }

    private String monthlySourceKey(
            LuckIntegratedSegmentAnalysis segment
    ) {

        return "MONTHLY:"
                + segment
                .monthlyCandidate()
                .sajuYear()
                + ":"
                + segment
                .monthlyCandidate()
                .monthIndex();
    }

    private String monthlyFunctionalSourceKey(
            LuckIntegratedSegmentAnalysis segment
    ) {

        return "MONTHLY_FUNCTIONAL:"
                + segment
                .monthlyCandidate()
                .sajuYear()
                + ":"
                + segment
                .monthlyCandidate()
                .monthIndex();
    }

    /*
     * =================================================
     * Sort
     * =================================================
     */

    private List<LifeDomainEvidenceReference<?>>
    sortReferences(
            List<? extends LifeDomainEvidenceReference<?>> references
    ) {

        Objects.requireNonNull(
                references,
                "정렬할 LifeDomainEvidenceReference 목록이 필요합니다."
        );

        Comparator<LifeDomainEvidenceReference<?>>
                comparator =
                Comparator
                        .comparingInt(
                                (
                                        LifeDomainEvidenceReference<?> reference
                                ) ->
                                        reference
                                                .tier()
                                                .ordinal()
                        )
                        .thenComparingInt(
                                (
                                        LifeDomainEvidenceReference<?> reference
                                ) ->
                                        reference
                                                .sourceType()
                                                .ordinal()
                        )
                        .thenComparing(
                                (
                                        LifeDomainEvidenceReference<?> reference
                                ) ->
                                        reference
                                                .sourceKey()
                        );

        List<LifeDomainEvidenceReference<?>> result =
                new ArrayList<>(
                        references.size()
                );

        for (
                LifeDomainEvidenceReference<?> reference
                : references
        ) {

            result.add(
                    reference
            );
        }

        result.sort(
                comparator
        );

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Duplicate
     * =================================================
     */

    private void validateNoDuplicateSourceKeys(

            List<LifeDomainEvidenceReference<?>> evidences,

            String label

    ) {

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
                        label
                                + " LifeDomain Evidence Source가 중복되었습니다: "
                                + key
                );
            }
        }
    }
}