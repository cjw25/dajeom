package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.List;
import java.util.Objects;

/**
 * 기본성향에서 사용하는
 * 십성 Family 하나의 기능 Profile.
 *
 * =================================================
 *
 * 예:
 *
 * PROCESSING
 * → 인성
 *
 * EXPRESSION
 * → 식상
 *
 * RESPONSIBILITY
 * → 관살
 *
 * RESOURCE_REALITY
 * → 재성
 *
 * SELF_ASSERTION
 * → 비겁
 *
 * =================================================
 *
 * 이 객체는 새로운 성격점수를 만들지 않는다.
 *
 * 기존:
 *
 * NatalTenGodFamilyEffect
 * NatalCrossFunctionalPattern
 * NatalConditionalResponsePattern
 *
 * 을 하나의 기능축 기준으로 묶는다.
 *
 * =================================================
 *
 * 목적:
 *
 * 다음 단계에서
 *
 * "이 기능을 얼마나 강하게 말할 수 있는가"
 *
 * 를 판단할 수 있게 한다.
 *
 * =================================================
 *
 * 주의:
 *
 * strong()
 *
 * 은 좋은 성격이라는 뜻이 아니다.
 *
 * 해당 십성 Family의 구조적 활성도가
 * 기존 10차 계산에서 강하게 확인되었다는 뜻이다.
 */
public record NatalPersonalityFunctionProfile(

        NatalTenGodFunctionType functionType,

        NatalTenGodFamilyEffect functionEffect,

        List<NatalCrossFunctionalPattern>
        crossPatterns,

        List<NatalConditionalResponsePattern>
        conditionalPatterns,

        boolean completeChart

) {

    public NatalPersonalityFunctionProfile {

        Objects.requireNonNull(
                functionType,
                "기본성향 기능축은 필수입니다."
        );

        Objects.requireNonNull(
                functionEffect,
                "기능축 원본 Evidence는 필수입니다."
        );

        crossPatterns =
                crossPatterns == null
                        ? List.of()
                        : List.copyOf(
                        crossPatterns
                );

        conditionalPatterns =
                conditionalPatterns == null
                        ? List.of()
                        : List.copyOf(
                        conditionalPatterns
                );

        /*
         * =================================================
         * 기능축 일치
         * =================================================
         */

        if (
                functionEffect.functionType()
                        != functionType
        ) {

            throw new IllegalArgumentException(
                    "Personality 기능축과 "
                            + "원본 TenGod Family 기능축이 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 완전성
         * =================================================
         */

        if (
                functionEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Personality 기능축과 "
                            + "원본 기능축의 "
                            + "원국 완전성 상태가 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Cross Pattern이
         * 실제 이 기능축에 참여하는지 검증
         * =================================================
         */

        for (
                NatalCrossFunctionalPattern pattern
                : crossPatterns
        ) {

            boolean involved =
                    pattern
                            .patternType()
                            .primaryFunction()

                            == functionType

                            || pattern
                            .patternType()
                            .secondaryFunction()
                            .map(
                                    secondary ->
                                            secondary
                                                    == functionType
                            )
                            .orElse(
                                    false
                            );

            if (
                    !involved
            ) {

                throw new IllegalArgumentException(
                        "Personality 기능축과 관계없는 "
                                + "CrossFunctional Pattern이 "
                                + "포함되었습니다: "
                                + pattern.patternType()
                );
            }

            if (
                    pattern.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "CrossFunctional Pattern의 "
                                + "원국 완전성 상태가 "
                                + "일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * Conditional Pattern이
         * 실제 이 기능축에 참여하는지 검증
         * =================================================
         */

        for (
                NatalConditionalResponsePattern pattern
                : conditionalPatterns
        ) {

            if (
                    !pattern
                            .functionalSequence()
                            .contains(
                                    functionType
                            )
            ) {

                throw new IllegalArgumentException(
                        "Personality 기능축과 관계없는 "
                                + "Conditional Response Pattern이 "
                                + "포함되었습니다: "
                                + pattern.patternType()
                );
            }

            if (
                    pattern.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Conditional Response Pattern의 "
                                + "원국 완전성 상태가 "
                                + "일치하지 않습니다."
                );
            }
        }
    }

    /**
     * 해당 Family가 원국에 실제 존재하는가.
     */
    public boolean present() {

        return functionEffect
                .present();
    }

    /**
     * 기존 10차에서
     * 유의미한 Pattern 수준까지 확인되었는가.
     */
    public boolean meaningful() {

        return functionEffect
                .meaningful();
    }

    /**
     * 기존 10차에서
     * 강한 구조적 활성 수준까지 확인되었는가.
     */
    public boolean strong() {

        return functionEffect
                .strong();
    }

    /**
     * 해당 기능축이
     * 원국 주격의 중심인가.
     *
     * 격국을 추가 점수로 사용하지 않고
     * 중심성으로만 사용한다.
     */
    public boolean structuralCenter() {

        return functionEffect
                .primaryStructure();
    }

    /**
     * 실제 MEANINGFUL/STRONG
     * Cross Pattern이 존재하는가.
     */
    public boolean hasEffectiveCrossPattern() {

        return crossPatterns
                .stream()
                .anyMatch(
                        NatalCrossFunctionalPattern
                                ::structurallyEffective
                );
    }

    /**
     * 실제 Cross Pattern이 존재하면서
     * 10-9에서도 SUPPORTED인가.
     */
    public boolean hasSupportedCrossPattern() {

        return crossPatterns
                .stream()
                .anyMatch(
                        pattern ->
                                pattern
                                        .structurallyEffective()

                                        && pattern
                                        .supported()
                );
    }

    /**
     * 실제 Pattern은 있으나:
     *
     * QUALIFIED
     * WEAKENED
     * CONFLICTED
     *
     * 상태가 존재하는가.
     */
    public boolean hasQualifiedCrossPattern() {

        return crossPatterns
                .stream()
                .anyMatch(
                        pattern ->
                                pattern
                                        .structurallyEffective()

                                        && (
                                        pattern.qualified()

                                                || pattern.weakened()

                                                || pattern.conflicted()
                                )
                );
    }

    /**
     * 이 기능축이 참여하는 상황 Pattern 중
     * 10-9 SUPPORTED가 존재하는가.
     */
    public boolean hasDirectConditionalResponse() {

        return conditionalPatterns
                .stream()
                .anyMatch(
                        NatalConditionalResponsePattern
                                ::directlyAssertable
                );
    }

    /**
     * 이 기능축이 참여하는 상황 Pattern 중
     *
     * 10-8 STRONG
     * +
     * 10-9 SUPPORTED
     *
     * 가 동시에 존재하는가.
     */
    public boolean hasStrongConditionalResponse() {

        return conditionalPatterns
                .stream()
                .anyMatch(
                        NatalConditionalResponsePattern
                                ::stronglyGrounded
                );
    }

    /**
     * 이 기능축을 설명할 때
     * 조건·약화·충돌을 함께 설명해야 하는가.
     */
    public boolean requiresConditionalQualification() {

        return conditionalPatterns
                .stream()
                .anyMatch(
                        NatalConditionalResponsePattern
                                ::requiresQualification
                );
    }
}