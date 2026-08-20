package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalCrossFunctionalScope;
import com.dajeom.type.NatalFunctionalActivationLevel;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.TenGodCorePatternLevel;
import com.dajeom.type.TenGodCrossValidationStatus;

import java.util.Objects;
import java.util.Optional;

/**
 * 원국에서 실제 확인된
 * 하나의 기능 교차 Pattern.
 *
 * =================================================
 *
 * 이 객체는 기존:
 *
 * TenGodCrossValidatedPattern
 *
 * 을 대체하지 않는다.
 *
 * 원본 10-9 Pattern 전체를
 * sourcePattern으로 그대로 보존하면서
 *
 * Functional Analysis 관점의:
 *
 * - 주 기능축
 * - 보조 기능축
 * - CoreSelf 연결 여부
 *
 * 만 추가한다.
 *
 * =================================================
 *
 * 따라서:
 *
 * - 새 점수 없음
 * - 새 threshold 없음
 * - summary 문자열 파싱 없음
 * - reason 문자열 파싱 없음
 * - 10-9 상태 임의 변경 없음
 *
 * =================================================
 *
 * REFERENCE Pattern도 삭제하지 않는다.
 *
 * 다만 다음 행동 Pattern 단계에서
 * 강한 행동 근거로 사용할 수 있는지는
 * structurallyEffective()로 구분한다.
 */
public record NatalCrossFunctionalPattern(

        NatalCrossFunctionalPatternType
        patternType,

        TenGodCrossValidatedPattern
        sourcePattern,

        NatalTenGodFamilyEffect
        primaryFunctionEffect,

        Optional<NatalTenGodFamilyEffect>
        secondaryFunctionEffect,

        Optional<NatalCoreSelfEffect>
        coreSelfEffect,

        boolean completeChart

) {

    public NatalCrossFunctionalPattern {

        Objects.requireNonNull(
                patternType,
                "교차 기능 Pattern 종류는 필수입니다."
        );

        Objects.requireNonNull(
                sourcePattern,
                "10-9 원본 Pattern은 필수입니다."
        );

        Objects.requireNonNull(
                primaryFunctionEffect,
                "주 기능축 Evidence는 필수입니다."
        );

        secondaryFunctionEffect =
                secondaryFunctionEffect == null
                        ? Optional.empty()
                        : secondaryFunctionEffect;

        coreSelfEffect =
                coreSelfEffect == null
                        ? Optional.empty()
                        : coreSelfEffect;

        /*
         * =================================================
         * 원본 Pattern 종류 검증
         * =================================================
         */

        if (
                sourcePattern.patternType()
                        != patternType.sourcePatternType()
        ) {

            throw new IllegalArgumentException(
                    "Functional Pattern과 "
                            + "10-9 원본 Pattern 종류가 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 주 기능축 검증
         * =================================================
         */

        if (
                primaryFunctionEffect.functionType()
                        != patternType.primaryFunction()
        ) {

            throw new IllegalArgumentException(
                    "Functional Pattern의 주 기능축이 "
                            + "정의와 일치하지 않습니다."
            );
        }

        NatalCrossFunctionalScope scope =
                patternType.scope();

        /*
         * =================================================
         * 서로 다른 기능축 사이 Pattern
         * =================================================
         */

        if (
                scope
                        == NatalCrossFunctionalScope
                        .BETWEEN_FUNCTIONS
        ) {

            NatalTenGodFunctionType
                    expectedSecondary =
                    patternType
                            .secondaryFunction()
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "기능 간 Pattern에는 "
                                                    + "보조 기능축 정의가 필요합니다."
                                    )
                            );

            NatalTenGodFamilyEffect secondary =
                    secondaryFunctionEffect
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "기능 간 Pattern에는 "
                                                    + "보조 기능축 Evidence가 필요합니다."
                                    )
                            );

            if (
                    secondary.functionType()
                            != expectedSecondary
            ) {

                throw new IllegalArgumentException(
                        "Functional Pattern의 보조 기능축이 "
                                + "정의와 일치하지 않습니다."
                );
            }

            if (
                    coreSelfEffect.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "기능 간 Pattern에는 "
                                + "CoreSelf Evidence를 직접 넣지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * 기능축 ↔ CoreSelf
         * =================================================
         */

        if (
                scope
                        == NatalCrossFunctionalScope
                        .FUNCTION_TO_CORE_SELF
        ) {

            if (
                    secondaryFunctionEffect.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "CoreSelf 연결 Pattern에는 "
                                + "보조 기능축을 넣지 않습니다."
                );
            }

            coreSelfEffect
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "CoreSelf 연결 Pattern에는 "
                                            + "CoreSelf Evidence가 필요합니다."
                            )
                    );
        }

        /*
         * =================================================
         * 동일 Family 내부 Pattern
         * =================================================
         */

        if (
                scope
                        == NatalCrossFunctionalScope
                        .WITHIN_FUNCTION
        ) {

            if (
                    secondaryFunctionEffect.isPresent()

                            || coreSelfEffect.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "기능 내부 Pattern에는 "
                                + "보조 기능축이나 CoreSelf Evidence를 "
                                + "넣지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * 원국 완전성 검증
         * =================================================
         */

        if (
                primaryFunctionEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "주 기능축과 CrossFunctional Pattern의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        secondaryFunctionEffect
                .ifPresent(
                        effect -> {

                            if (
                                    effect.completeChart()
                                            != completeChart
                            ) {

                                throw new IllegalArgumentException(
                                        "보조 기능축과 "
                                                + "CrossFunctional Pattern의 "
                                                + "원국 완전성 상태가 "
                                                + "일치하지 않습니다."
                                );
                            }
                        }
                );

        coreSelfEffect
                .ifPresent(
                        effect -> {

                            if (
                                    effect.completeChart()
                                            != completeChart
                            ) {

                                throw new IllegalArgumentException(
                                        "CoreSelf와 "
                                                + "CrossFunctional Pattern의 "
                                                + "원국 완전성 상태가 "
                                                + "일치하지 않습니다."
                                );
                            }
                        }
                );
    }

    /**
     * 기존 10-8의 Pattern 성립 수준.
     */
    public TenGodCorePatternLevel sourceLevel() {

        return sourcePattern
                .sourceSignal()
                .level();
    }

    /**
     * 기존 10-9 최종 교차검증 상태.
     *
     * 새 상태를 만들지 않는다.
     */
    public TenGodCrossValidationStatus
    validationStatus() {

        return sourcePattern
                .status();
    }

    /**
     * 10-8에서 실제 Pattern으로
     * 다룰 수 있는 수준인지.
     *
     * REFERENCE는 구조적으로 보존하지만
     * 이후 행동 Pattern의 강한 근거로는
     * 사용하지 않는다.
     */
    public boolean structurallyEffective() {

        return sourcePattern
                .sourceSignal()
                .meaningful();
    }

    public boolean strongSourcePattern() {

        return sourcePattern
                .sourceSignal()
                .strong();
    }

    public boolean supported() {

        return sourcePattern
                .supported();
    }

    public boolean qualified() {

        return sourcePattern
                .qualified();
    }

    public boolean weakened() {

        return sourcePattern
                .weakened();
    }

    public boolean conflicted() {

        return sourcePattern
                .conflicted();
    }

    public NatalFunctionalActivationLevel
    primaryActivationLevel() {

        return primaryFunctionEffect
                .activationLevel();
    }

    public Optional<NatalFunctionalActivationLevel>
    secondaryActivationLevel() {

        return secondaryFunctionEffect
                .map(
                        NatalTenGodFamilyEffect
                                ::activationLevel
                );
    }

    /**
     * 이 Pattern의 주 기능축이
     * 원국 주격 중심인지.
     */
    public boolean primaryStructuralCenter() {

        return primaryFunctionEffect
                .primaryStructure();
    }

    /**
     * 이 Pattern의 보조 기능축이
     * 원국 주격 중심인지.
     */
    public boolean secondaryStructuralCenter() {

        return secondaryFunctionEffect
                .map(
                        NatalTenGodFamilyEffect
                                ::primaryStructure
                )
                .orElse(
                        false
                );
    }
}