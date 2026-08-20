package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 원국 Functional Effect의
 * 첫 공식 Aggregate.
 *
 * =================================================
 *
 * 기존 계산:
 *
 * 4차 신강·신약
 * 5차 격국
 * 8차 관계
 * 9차 합화
 * 10차 십성 복합구조
 *
 * 를 다시 계산하지 않고
 * 후속 분석에서 사용할 수 있는
 * 기능축으로 정규화한다.
 *
 * =================================================
 *
 * 구성:
 *
 * CORE_SELF
 *
 * +
 *
 * SELF_ASSERTION
 * 비겁
 *
 * +
 *
 * EXPRESSION
 * 식상
 *
 * +
 *
 * RESOURCE_REALITY
 * 재성
 *
 * +
 *
 * RESPONSIBILITY
 * 관살
 *
 * +
 *
 * PROCESSING
 * 인성
 *
 * +
 *
 * STRUCTURAL_CENTER
 * 격국 중심성
 *
 * +
 *
 * INTERPERSONAL
 * 관계/합화 Context
 *
 * =================================================
 *
 * 주의:
 *
 * 이 객체 자체는 아직
 * 최종 Personality 문장을 만들지 않는다.
 *
 * 다음 단계:
 *
 * NatalFunctionalAnalysis
 *
 * ↓
 *
 * CrossFunctionalPattern
 *
 * ↓
 *
 * ConditionalResponsePattern
 *
 * ↓
 *
 * PERSONALITY
 */
public record NatalFunctionalAnalysis(

        HeavenlyStem dayMaster,

        NatalCoreSelfEffect coreSelf,

        List<NatalTenGodFamilyEffect>
        tenGodFamilyEffects,

        NatalStructuralCenterEffect
        structuralCenter,

        NatalInterpersonalEffect
        interpersonal,

        List<TenGodCrossValidatedPattern>
        crossValidatedPatterns,

        boolean completeChart

) {

    public NatalFunctionalAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                coreSelf,
                "자기 기반 기능축은 필수입니다."
        );

        Objects.requireNonNull(
                tenGodFamilyEffects,
                "십성 기능축은 필수입니다."
        );

        Objects.requireNonNull(
                structuralCenter,
                "격국 중심축은 필수입니다."
        );

        Objects.requireNonNull(
                interpersonal,
                "관계 기능축은 필수입니다."
        );

        tenGodFamilyEffects =
                List.copyOf(
                        tenGodFamilyEffects
                );

        crossValidatedPatterns =
                crossValidatedPatterns == null
                        ? List.of()
                        : List.copyOf(
                        crossValidatedPatterns
                );

        /*
         * 일간 일치.
         */
        if (
                coreSelf.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "NatalFunctionalAnalysis와 "
                            + "CoreSelf의 일간이 일치하지 않습니다."
            );
        }

        /*
         * 십성 5대 Family 기능축이
         * 정확히 한 개씩 있어야 한다.
         */
        if (
                tenGodFamilyEffects.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "원국 Functional Analysis에는 "
                            + "십성 5대 기능축이 모두 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> seen =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                NatalTenGodFamilyEffect effect
                : tenGodFamilyEffects
        ) {

            if (
                    !seen.add(
                            effect.functionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 십성 기능축이 중복되었습니다: "
                                + effect.functionType()
                );
            }
        }

        if (
                seen.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "원국 Functional Analysis의 "
                            + "십성 기능축이 누락되었습니다."
            );
        }

        /*
         * 출생시간 완전성 일치.
         */
        if (
                coreSelf.completeChart()
                        != completeChart

                        || structuralCenter.completeChart()
                        != completeChart

                        || interpersonal.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 Functional Analysis의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        for (
                NatalTenGodFamilyEffect effect
                : tenGodFamilyEffects
        ) {

            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "십성 기능축의 "
                                + "원국 완전성 상태가 일치하지 않습니다."
                );
            }
        }
    }

    public Optional<NatalTenGodFamilyEffect>
    function(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return tenGodFamilyEffects
                .stream()
                .filter(
                        effect ->
                                effect.functionType()
                                        == functionType
                )
                .findFirst();
    }

    public NatalTenGodFamilyEffect
    requireFunction(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "기능축은 필수입니다."
        );

        return function(
                functionType
        )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "원국 기능축을 찾을 수 없습니다: "
                                        + functionType
                        )
                );
    }

    public NatalTenGodFamilyEffect
    selfAssertion() {

        return requireFunction(
                NatalTenGodFunctionType
                        .SELF_ASSERTION
        );
    }

    public NatalTenGodFamilyEffect
    expression() {

        return requireFunction(
                NatalTenGodFunctionType
                        .EXPRESSION
        );
    }

    public NatalTenGodFamilyEffect
    resourceReality() {

        return requireFunction(
                NatalTenGodFunctionType
                        .RESOURCE_REALITY
        );
    }

    public NatalTenGodFamilyEffect
    responsibility() {

        return requireFunction(
                NatalTenGodFunctionType
                        .RESPONSIBILITY
        );
    }

    public NatalTenGodFamilyEffect
    processing() {

        return requireFunction(
                NatalTenGodFunctionType
                        .PROCESSING
        );
    }
}