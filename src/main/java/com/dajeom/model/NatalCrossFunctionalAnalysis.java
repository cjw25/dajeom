package com.dajeom.model;

import com.dajeom.model.NatalCrossFunctionalPattern;
import com.dajeom.type.NatalCrossFunctionalInteractionType;
import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.TenGodCorePatternType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 원국 기능축 사이의
 * 교차작동 Aggregate.
 *
 * =================================================
 *
 * 기반:
 *
 * NatalFunctionalAnalysis
 *
 * +
 *
 * 10-9 TenGodCrossValidatedPattern
 *
 * =================================================
 *
 * 이 단계에서는:
 *
 * 식신제살
 * 상관견관
 * 상관패인
 * 편인도식
 * 재생관
 * 관인상생
 * 살인상생
 * 재극인
 * 비겁쟁재
 * 재다신약
 * 관살혼재
 *
 * 등의 기존 전통 Pattern을
 *
 * Functional 기능축 사이의
 * 구조적 상호작용으로 변환한다.
 *
 * =================================================
 *
 * 아직 여기에서는:
 *
 * - 성격 문장
 * - 심리 단정
 * - 사건 예측
 * - 좋다/나쁘다
 *
 * 를 만들지 않는다.
 *
 * 다음 단계인
 * ConditionalResponsePattern에서
 * 실제 상황별 반응 구조의 Evidence로 사용한다.
 */
public record NatalCrossFunctionalAnalysis(

        NatalFunctionalAnalysis natalFunctionalAnalysis,

        List<NatalCrossFunctionalPattern> patterns,

        boolean completeChart

) {

    public NatalCrossFunctionalAnalysis {

        Objects.requireNonNull(
                natalFunctionalAnalysis,
                "원국 Functional Analysis는 필수입니다."
        );

        patterns =
                patterns == null
                        ? List.of()
                        : List.copyOf(
                        patterns
                );

        /*
         * =================================================
         * 원국 완전성
         * =================================================
         */

        if (
                natalFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "NatalFunctionalAnalysis와 "
                            + "CrossFunctionalAnalysis의 "
                            + "원국 완전성 상태가 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 동일 10차 Pattern 중복 변환 금지
         * =================================================
         */

        Set<TenGodCorePatternType> sourceTypes =
                new HashSet<>();

        for (
                NatalCrossFunctionalPattern pattern
                : patterns
        ) {

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

            TenGodCorePatternType sourceType =
                    pattern
                            .patternType()
                            .sourcePatternType();

            if (
                    !sourceTypes.add(
                            sourceType
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 원본 십성 Pattern이 "
                                + "중복 변환되었습니다: "
                                + sourceType
                );
            }
        }
    }

    /**
     * 정확한 Functional Pattern 조회.
     */
    public Optional<NatalCrossFunctionalPattern> patternOf(

            NatalCrossFunctionalPatternType patternType

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
     * 10-8에서 MEANINGFUL 또는 STRONG으로
     * 실제 Pattern으로 취급할 수 있는 것만 조회.
     *
     * REFERENCE Pattern은 삭제되지 않고
     * 전체 patterns에는 그대로 남는다.
     */
    public List<NatalCrossFunctionalPattern> effectivePatterns() {

        return patterns
                .stream()
                .filter(
                        NatalCrossFunctionalPattern
                                ::structurallyEffective
                )
                .toList();
    }

    /**
     * 구조적으로 실제 Pattern이며
     * 10-9 교차검증에서도 SUPPORTED인 것.
     */
    public List<NatalCrossFunctionalPattern> supportedPatterns() {

        return effectivePatterns()
                .stream()
                .filter(
                        NatalCrossFunctionalPattern
                                ::supported
                )
                .toList();
    }

    /**
     * 특정 상호작용 종류 조회.
     */
    public List<NatalCrossFunctionalPattern> patternsOfInteraction(

            NatalCrossFunctionalInteractionType interactionType

    ) {

        if (
                interactionType == null
        ) {

            return List.of();
        }

        return patterns
                .stream()
                .filter(
                        pattern ->
                                pattern
                                        .patternType()
                                        .interactionType()
                                        == interactionType
                )
                .toList();
    }

    /**
     * 특정 기능축이 참여하는
     * 모든 교차 Pattern 조회.
     */
    public List<NatalCrossFunctionalPattern> patternsInvolving(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return patterns
                .stream()
                .filter(
                        pattern -> {

                            NatalCrossFunctionalPatternType type =
                                    pattern.patternType();

                            return type.primaryFunction()
                                    == functionType

                                    || type.secondaryFunction()
                                    .map(
                                            secondary ->
                                                    secondary
                                                            == functionType
                                    )
                                    .orElse(
                                            false
                                    );
                        }
                )
                .toList();
    }
}