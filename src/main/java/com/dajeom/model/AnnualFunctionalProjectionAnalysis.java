package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 세운 AnnualLuckAnalysis를
 * Functional 5축으로 투영한 결과.
 *
 * =================================================
 *
 * 입력:
 *
 * NatalFunctionalAnalysis
 *
 * +
 *
 * AnnualLuckAnalysis
 *
 * =================================================
 *
 * 사용 Source:
 *
 * 14-3 Annual TenGod
 *
 * +
 *
 * 14-4 Natal Relation / Transformation
 *
 * =================================================
 *
 * 아직 사용하지 않는 것:
 *
 * 14-5 Decade × Annual Interaction
 *
 * 14-6 Context 최종 판단
 *
 * DecadeFunctionalSynthesisAnalysis
 *
 * =================================================
 *
 * 정확히 5개 Function을 항상 보존한다.
 *
 * 세운에서 특정 Function Input이 없어도
 * 빈 AnnualFunctionEffect를 유지한다.
 *
 * 이를 통해 후속 Layer에서:
 *
 * "없음"
 *
 * 과
 *
 * "분석 누락"
 *
 * 을 구분한다.
 */
public record AnnualFunctionalProjectionAnalysis(

        NatalFunctionalAnalysis natalFunctionalAnalysis,

        AnnualLuckAnalysis annualLuckAnalysis,

        List<AnnualFunctionEffect> functionEffects,

        boolean completeChart

) {

    public AnnualFunctionalProjectionAnalysis {

        Objects.requireNonNull(
                natalFunctionalAnalysis,
                "Annual Functional Projection에는 NatalFunctionalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "Annual Functional Projection에는 AnnualLuckAnalysis가 필요합니다."
        );

        functionEffects =
                functionEffects == null
                        ? List.of()
                        : List.copyOf(
                        functionEffects
                );

        /*
         * =================================================
         * 동일 일간
         * =================================================
         */

        if (
                natalFunctionalAnalysis.dayMaster()
                        != annualLuckAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "Natal Functional Analysis와 AnnualLuckAnalysis의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * completeChart
         * =================================================
         */

        if (
                natalFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Natal Functional Analysis와 Annual Functional Projection의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                annualLuckAnalysis
                        .natalAnalysis()
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "AnnualLuckAnalysis 원국과 Annual Functional Projection의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 정확히 5 Function
         * =================================================
         */

        if (
                functionEffects.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Projection에는 정확히 "
                            + NatalTenGodFunctionType.values().length
                            + "개의 Function Effect가 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> seen =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                AnnualFunctionEffect effect
                : functionEffects
        ) {

            Objects.requireNonNull(
                    effect,
                    "AnnualFunctionEffect는 null일 수 없습니다."
            );

            if (
                    effect.annualLuckAnalysis()
                            != annualLuckAnalysis
            ) {

                throw new IllegalArgumentException(
                        "Annual Function Effect가 현재 AnnualLuckAnalysis Identity를 보존하지 않았습니다."
                );
            }

            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual Function Effect의 completeChart가 일치하지 않습니다."
                );
            }

            NatalTenGodFamilyEffect expectedNatalFunction =
                    natalFunctionalAnalysis
                            .requireFunction(
                                    effect.functionType()
                            );

            if (
                    effect.natalFunctionEffect()
                            != expectedNatalFunction
            ) {

                throw new IllegalArgumentException(
                        "Annual Function Effect가 원국 Function Identity를 보존하지 않았습니다. function="
                                + effect.functionType()
                );
            }

            if (
                    !seen.add(
                            effect.functionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Annual Function Effect가 중복되었습니다: "
                                + effect.functionType()
                );
            }
        }

        Set<NatalTenGodFunctionType> expected =
                EnumSet.allOf(
                        NatalTenGodFunctionType.class
                );

        if (
                !seen.equals(
                        expected
                )
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Projection의 5개 Function Coverage가 완전하지 않습니다."
            );
        }

        /*
         * =================================================
         * 14-3 Evidence Coverage
         * =================================================
         *
         * 모든 Annual TenGod Evidence는
         * 정확히 하나의 Function에 들어가야 한다.
         */

        long projectedEvidenceCount =
                functionEffects
                        .stream()
                        .flatMap(
                                effect ->
                                        effect
                                                .directTenGodEvidences()
                                                .stream()
                        )
                        .distinct()
                        .count();

        if (
                projectedEvidenceCount
                        != annualLuckAnalysis
                        .tenGodAnalysis()
                        .evidences()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "14-3 Annual TenGod Evidence 일부가 "
                            + "Functional Projection에서 누락되거나 중복되었습니다. "
                            + "source="
                            + annualLuckAnalysis.tenGodAnalysis().evidences().size()
                            + ", projected="
                            + projectedEvidenceCount
            );
        }
    }

    public int targetYear() {

        return annualLuckAnalysis
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return annualLuckAnalysis
                .period();
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    public Optional<AnnualFunctionEffect>
    effectOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return functionEffects
                .stream()
                .filter(
                        effect ->
                                effect.functionType()
                                        == functionType
                )
                .findFirst();
    }

    public AnnualFunctionEffect requireEffect(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "Annual Function 조회에는 Function Type이 필요합니다."
        );

        return effectOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual Function Effect를 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }

    /*
     * =================================================
     * Active
     * =================================================
     */

    public List<AnnualFunctionEffect>
    directlyEnteredEffects() {

        return functionEffects
                .stream()
                .filter(
                        AnnualFunctionEffect
                                ::directlyEntered
                )
                .toList();
    }

    public List<AnnualFunctionEffect>
    effectsWithTransformationContext() {

        return functionEffects
                .stream()
                .filter(
                        AnnualFunctionEffect
                                ::transformationContextExists
                )
                .toList();
    }

    public List<AnnualFunctionEffect>
    effectsWithActiveTransformation() {

        return functionEffects
                .stream()
                .filter(
                        AnnualFunctionEffect
                                ::activeTransformationExists
                )
                .toList();
    }

    public List<AnnualFunctionEffect>
    effectsWithAnnualOriginalFunctionReduction() {

        return functionEffects
                .stream()
                .filter(
                        AnnualFunctionEffect
                                ::annualOriginalFunctionReductionExists
                )
                .toList();
    }

    /*
     * =================================================
     * 14-4 Distinct Transformation
     * =================================================
     *
     * 동일 지지 Transformation이 여러 Function의
     * 지장간 Input에 연결될 수 있으므로
     * 전체 Aggregate에서도 중복 제거한다.
     */

    public List<AnnualLuckNatalTransformationReference>
    distinctTransformationContexts() {

        Set<AnnualLuckNatalTransformationReference> result =
                new java.util.LinkedHashSet<>();

        for (
                AnnualFunctionEffect effect
                : functionEffects
        ) {

            result.addAll(
                    effect
                            .distinctTransformationContexts()
            );
        }

        return List.copyOf(
                result
        );
    }
}