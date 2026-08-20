package com.dajeom.model;

import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 특정 상황에서 확인되는
 * 원국 기능작동 Profile.
 *
 * =================================================
 *
 * 예:
 *
 * RULES_AND_EXPRESSION
 *
 * PROCESSING_AND_EXPRESSION
 *
 * COMPETITION_AND_RESOURCE
 *
 * PRESSURE_AND_RESPONSIBILITY
 *
 * =================================================
 *
 * 이 객체는 아직:
 *
 * "상사와 싸운다"
 * "참다가 폭발한다"
 *
 * 같은 행동문장을 만들지 않는다.
 *
 * =================================================
 *
 * 대신:
 *
 * - 어떤 Pattern이 실제 존재하는지
 * - 어떤 기능들이 참여하는지
 * - 직접 말할 수 있는 근거인지
 * - 강하게 교차검증됐는지
 * - 조건 설명이 필요한지
 * - 충돌 상태가 있는지
 *
 * 를 구조화한다.
 */
public record NatalPersonalitySituationProfile(

        NatalResponseSituationType situationType,

        List<NatalConditionalResponsePattern>
        patterns,

        boolean completeChart

) {

    public NatalPersonalitySituationProfile {

        Objects.requireNonNull(
                situationType,
                "상황 종류는 필수입니다."
        );

        patterns =
                patterns == null
                        ? List.of()
                        : List.copyOf(
                        patterns
                );

        for (
                NatalConditionalResponsePattern pattern
                : patterns
        ) {

            if (
                    pattern.situationType()
                            != situationType
            ) {

                throw new IllegalArgumentException(
                        "Situation Profile과 "
                                + "Conditional Pattern의 "
                                + "상황 종류가 일치하지 않습니다."
                );
            }

            if (
                    pattern.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Situation Profile과 "
                                + "Conditional Pattern의 "
                                + "원국 완전성 상태가 "
                                + "일치하지 않습니다."
                );
            }
        }
    }

    /**
     * 이 상황을 설명할 수 있는
     * 명리 Pattern이 실제 존재하는가.
     */
    public boolean evidenced() {

        return !patterns
                .isEmpty();
    }

    /**
     * 최소 하나의 Pattern이:
     *
     * 10-8 STRONG
     * +
     * 10-9 SUPPORTED
     *
     * 인가.
     */
    public boolean stronglyGrounded() {

        return patterns
                .stream()
                .anyMatch(
                        NatalConditionalResponsePattern
                                ::stronglyGrounded
                );
    }

    /**
     * 최소 하나의 Pattern이
     * 10-9 SUPPORTED인가.
     */
    public boolean directlyAssertable() {

        return patterns
                .stream()
                .anyMatch(
                        NatalConditionalResponsePattern
                                ::directlyAssertable
                );
    }

    /**
     * QUALIFIED / WEAKENED / CONFLICTED
     * Pattern이 존재하여
     * 조건 설명이 필요한가.
     */
    public boolean requiresQualification() {

        return patterns
                .stream()
                .anyMatch(
                        NatalConditionalResponsePattern
                                ::requiresQualification
                );
    }

    /**
     * 10-9 CONFLICTED가
     * 실제 존재하는가.
     */
    public boolean conflicted() {

        return patterns
                .stream()
                .anyMatch(
                        NatalConditionalResponsePattern
                                ::conflicted
                );
    }

    /**
     * 이 상황에 실제 참여하는
     * 기능축을 작동순서 기준으로
     * 중복 제거하여 반환한다.
     */
    public List<NatalTenGodFunctionType>
    participatingFunctions() {

        Set<NatalTenGodFunctionType> result =
                new LinkedHashSet<>();

        for (
                NatalConditionalResponsePattern pattern
                : patterns
        ) {

            result.addAll(
                    pattern
                            .functionalSequence()
            );
        }

        return List.copyOf(
                result
        );
    }
}