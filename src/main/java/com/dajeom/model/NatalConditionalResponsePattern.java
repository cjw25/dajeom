package com.dajeom.model;

import com.dajeom.model.NatalCrossFunctionalPattern;
import com.dajeom.type.NatalConditionalResponsePatternType;
import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.TenGodCorePatternLevel;
import com.dajeom.type.TenGodCrossValidationStatus;

import java.util.List;
import java.util.Objects;

/**
 * 원국에서 실제 성립한
 * 조건부 기능 작동 Pattern 하나.
 *
 * =================================================
 *
 * 이 객체의 목적:
 *
 * 전통 십성 복합 Pattern을
 *
 * 상황
 * ↓
 * 기능 A
 * ↓
 * 기능 B
 * ↓
 * 교차검증 상태
 *
 * 형태로 보존한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 아직 여기에서는:
 *
 * - "참는다"
 * - "폭발한다"
 * - "고집이 세다"
 * - "말이 직설적이다"
 * - "상사와 싸운다"
 *
 * 같은 심리·행동 문장을 만들지 않는다.
 *
 * =================================================
 *
 * 다음 Personality 단계에서
 * 여러 독립 Pattern과
 * CoreSelf / StructuralCenter가
 * 같은 방향으로 확인될 때만
 * 실제 행동문장으로 승격한다.
 */
public record NatalConditionalResponsePattern(

        NatalConditionalResponsePatternType patternType,

        NatalCrossFunctionalPattern sourcePattern,

        List<NatalTenGodFunctionType> functionalSequence,

        boolean coreSelfIncluded,

        boolean primaryStructuralCenter,

        boolean secondaryStructuralCenter,

        boolean completeChart

) {

    public NatalConditionalResponsePattern {

        Objects.requireNonNull(
                patternType,
                "조건부 반응 Pattern 종류는 필수입니다."
        );

        Objects.requireNonNull(
                sourcePattern,
                "원본 CrossFunctional Pattern은 필수입니다."
        );

        functionalSequence =
                functionalSequence == null
                        ? List.of()
                        : List.copyOf(
                        functionalSequence
                );

        /*
         * =================================================
         * 원본 Pattern 1:1 검증
         * =================================================
         */

        if (
                sourcePattern.patternType()
                        != patternType.sourcePatternType()
        ) {

            throw new IllegalArgumentException(
                    "Conditional Response Pattern과 "
                            + "원본 CrossFunctional Pattern이 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 기능 작동순서 검증
         * =================================================
         */

        if (
                !functionalSequence.equals(
                        patternType.functionalSequence()
                )
        ) {

            throw new IllegalArgumentException(
                    "조건부 반응의 기능 작동순서가 "
                            + "Pattern 정의와 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * CoreSelf 연결 검증
         * =================================================
         */

        if (
                coreSelfIncluded
                        != patternType.coreSelfIncluded()
        ) {

            throw new IllegalArgumentException(
                    "조건부 반응의 CoreSelf 연결 상태가 "
                            + "Pattern 정의와 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 완전성 검증
         * =================================================
         */

        if (
                sourcePattern.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원본 CrossFunctional Pattern과 "
                            + "조건부 반응 Pattern의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }

    /**
     * 이 Pattern이 다루는 상황.
     */
    public NatalResponseSituationType situationType() {

        return patternType
                .situationType();
    }

    /**
     * 10-8 원본 Pattern의
     * 구조적 성립 수준.
     */
    public TenGodCorePatternLevel sourceLevel() {

        return sourcePattern
                .sourceLevel();
    }

    /**
     * 10-9 교차검증 상태.
     */
    public TenGodCrossValidationStatus validationStatus() {

        return sourcePattern
                .validationStatus();
    }

    /**
     * 10-8 STRONG
     *
     * +
     *
     * 10-9 SUPPORTED
     */
    public boolean stronglyGrounded() {

        return sourcePattern
                .strongSourcePattern()

                && sourcePattern
                .supported();
    }

    /**
     * 10-9에서 SUPPORTED.
     */
    public boolean directlyAssertable() {

        return sourcePattern
                .supported();
    }

    /**
     * 조건·약화·충돌 설명이 필요한지.
     */
    public boolean requiresQualification() {

        return sourcePattern
                .qualified()

                || sourcePattern
                .weakened()

                || sourcePattern
                .conflicted();
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
}