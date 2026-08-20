package com.dajeom.model;

import com.dajeom.type.NatalConditionalResponsePatternType;
import com.dajeom.type.NatalResponseSituationType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 원국 상황별 기능 작동 분석 Aggregate.
 *
 * =================================================
 *
 * 흐름:
 *
 * NatalFunctionalAnalysis
 *
 * ↓
 *
 * NatalCrossFunctionalAnalysis
 *
 * ↓
 *
 * NatalConditionalResponseAnalysis
 *
 * =================================================
 *
 * 포함:
 *
 * - 압력·책임 상황
 * - 기준·규범과 표현 상황
 * - 수용·처리와 표현 상황
 * - 현실·성과와 책임 상황
 * - 현실 요구와 처리 기능 상황
 * - 경쟁과 자원 상황
 * - 재성 과중과 CoreSelf 상황
 * - 관살 내부 혼재 상황
 *
 * =================================================
 *
 * 주의:
 *
 * 평상시 기본성향은
 * 여기에서 억지로 만들지 않는다.
 *
 * 평상시 판단은 다음 단계에서:
 *
 * CoreSelf
 * +
 * StructuralCenter
 * +
 * 5대 Functional Effect
 *
 * 를 기준으로 만든다.
 *
 * =================================================
 *
 * 대인관계 Context도
 * 여기에서 행동방향으로 임의 변환하지 않는다.
 *
 * 기존:
 *
 * NatalInterpersonalEffect
 *
 * 를 그대로 제공한다.
 *
 * 합 = 사교적
 * 충 = 싸움을 잘함
 *
 * 같은 추론은 금지한다.
 */
public record NatalConditionalResponseAnalysis(

        NatalCrossFunctionalAnalysis
        crossFunctionalAnalysis,

        List<NatalConditionalResponsePattern>
        patterns,

        boolean completeChart

) {

    public NatalConditionalResponseAnalysis {

        Objects.requireNonNull(
                crossFunctionalAnalysis,
                "CrossFunctional Analysis는 필수입니다."
        );

        patterns =
                patterns == null
                        ? List.of()
                        : List.copyOf(
                        patterns
                );

        /*
         * =================================================
         * 원국 완전성 검증
         * =================================================
         */

        if (
                crossFunctionalAnalysis
                        .completeChart()

                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctionalAnalysis와 "
                            + "ConditionalResponseAnalysis의 "
                            + "원국 완전성 상태가 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 동일 Pattern 중복 금지
         * =================================================
         */

        Set<NatalConditionalResponsePatternType>
                seen =
                EnumSet.noneOf(
                        NatalConditionalResponsePatternType.class
                );

        for (
                NatalConditionalResponsePattern pattern
                : patterns
        ) {

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

            if (
                    !seen.add(
                            pattern.patternType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Conditional Response Pattern이 "
                                + "중복되었습니다: "
                                + pattern.patternType()
                );
            }
        }
    }

    /**
     * 특정 상황에 해당하는
     * 모든 조건부 작동 Pattern.
     */
    public List<NatalConditionalResponsePattern>
    patternsOf(

            NatalResponseSituationType
                    situationType

    ) {

        if (
                situationType == null
        ) {

            return List.of();
        }

        return patterns
                .stream()
                .filter(
                        pattern ->
                                pattern.situationType()
                                        == situationType
                )
                .toList();
    }

    /**
     * 특정 Pattern 직접 조회.
     */
    public Optional<NatalConditionalResponsePattern>
    patternOf(

            NatalConditionalResponsePatternType
                    patternType

    ) {

        if (
                patternType == null
        ) {

            return Optional.empty();
        }

        return patterns
                .stream()
                .filter(
                        pattern ->
                                pattern.patternType()
                                        == patternType
                )
                .findFirst();
    }

    /**
     * 10-9에서 SUPPORTED인 Pattern.
     *
     * 후속 Personality에서
     * 비교적 직접적인 행동설명의
     * 근거 후보가 된다.
     */
    public List<NatalConditionalResponsePattern>
    directlyAssertablePatterns() {

        return patterns
                .stream()
                .filter(
                        NatalConditionalResponsePattern
                                ::directlyAssertable
                )
                .toList();
    }

    /**
     * 10-8 STRONG
     *
     * +
     *
     * 10-9 SUPPORTED.
     *
     * 가장 강하게 구조가 교차검증된
     * 조건부 Pattern.
     */
    public List<NatalConditionalResponsePattern>
    stronglyGroundedPatterns() {

        return patterns
                .stream()
                .filter(
                        NatalConditionalResponsePattern
                                ::stronglyGrounded
                )
                .toList();
    }

    /**
     * 조건부 설명이 반드시 필요한 Pattern.
     *
     * QUALIFIED
     * WEAKENED
     * CONFLICTED
     *
     * 를 삭제하지 않고 보존한다.
     */
    public List<NatalConditionalResponsePattern>
    qualifiedPatterns() {

        return patterns
                .stream()
                .filter(
                        NatalConditionalResponsePattern
                                ::requiresQualification
                )
                .toList();
    }

    /**
     * 기존 원국 대인관계 Context.
     *
     * 대인 상황을 억지로
     * 심리행동으로 변환하지 않고
     * 구조적 Evidence 그대로 제공한다.
     */
    public NatalInterpersonalEffect
    interpersonalContext() {

        return crossFunctionalAnalysis
                .natalFunctionalAnalysis()
                .interpersonal();
    }

    public NatalFunctionalAnalysis
    natalFunctionalAnalysis() {

        return crossFunctionalAnalysis
                .natalFunctionalAnalysis();
    }
}