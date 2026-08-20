package com.dajeom.model;

import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 기본성향 Pattern Analysis의
 * 공식 Aggregate.
 *
 * =================================================
 *
 * 구성:
 *
 * NatalFunctionalAnalysis
 *
 * +
 *
 * NatalCrossFunctionalAnalysis
 *
 * +
 *
 * NatalConditionalResponseAnalysis
 *
 * +
 *
 * 기능별 Personality Profile
 *
 * +
 *
 * 상황별 Personality Profile
 *
 * =================================================
 *
 * 이 객체의 목적:
 *
 * 다음 단계에서 기본성향을
 * 똑부러지게 표현하되
 *
 * 근거가 없는 성격 단정을
 * 만들지 않도록 하는 것이다.
 *
 * =================================================
 *
 * 아직 이 단계에서도:
 *
 * - 사용자 문장
 * - 심리 진단
 * - 사건 예측
 *
 * 은 생성하지 않는다.
 */
public record NatalPersonalityPatternAnalysis(

        NatalFunctionalAnalysis
        natalFunctionalAnalysis,

        NatalCrossFunctionalAnalysis
        crossFunctionalAnalysis,

        NatalConditionalResponseAnalysis
        conditionalResponseAnalysis,

        List<NatalPersonalityFunctionProfile>
        functionProfiles,

        List<NatalPersonalitySituationProfile>
        situationProfiles,

        boolean completeChart

) {

    public NatalPersonalityPatternAnalysis {

        Objects.requireNonNull(
                natalFunctionalAnalysis,
                "NatalFunctionalAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                crossFunctionalAnalysis,
                "NatalCrossFunctionalAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                conditionalResponseAnalysis,
                "NatalConditionalResponseAnalysis는 필수입니다."
        );

        functionProfiles =
                functionProfiles == null
                        ? List.of()
                        : List.copyOf(
                        functionProfiles
                );

        situationProfiles =
                situationProfiles == null
                        ? List.of()
                        : List.copyOf(
                        situationProfiles
                );

        /*
         * =================================================
         * 원국 완전성 검증
         * =================================================
         */

        if (
                natalFunctionalAnalysis.completeChart()
                        != completeChart

                        || crossFunctionalAnalysis.completeChart()
                        != completeChart

                        || conditionalResponseAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Personality Pattern Analysis의 "
                            + "원국 완전성 상태가 "
                            + "upstream 분석과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 동일 분석 체인 검증
         * =================================================
         */

        if (
                crossFunctionalAnalysis
                        .natalFunctionalAnalysis()

                        != natalFunctionalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctionalAnalysis가 "
                            + "다른 NatalFunctionalAnalysis를 "
                            + "참조하고 있습니다."
            );
        }

        if (
                conditionalResponseAnalysis
                        .crossFunctionalAnalysis()

                        != crossFunctionalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "ConditionalResponseAnalysis가 "
                            + "다른 CrossFunctionalAnalysis를 "
                            + "참조하고 있습니다."
            );
        }

        /*
         * =================================================
         * 십성 5대 기능축
         * 정확히 한 개씩
         * =================================================
         */

        if (
                functionProfiles.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "Personality Pattern Analysis에는 "
                            + "십성 5대 기능 Profile이 "
                            + "모두 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> functions =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                NatalPersonalityFunctionProfile profile
                : functionProfiles
        ) {

            if (
                    !functions.add(
                            profile.functionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Personality 기능 Profile이 "
                                + "중복되었습니다: "
                                + profile.functionType()
                );
            }

            if (
                    profile.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Personality 기능 Profile의 "
                                + "원국 완전성 상태가 "
                                + "일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * 상황 Profile도
         * enum 전체를 정확히 한 개씩 보존
         *
         * Pattern이 없는 상황도
         * 빈 Profile로 유지한다.
         *
         * 이유:
         *
         * "근거 없음"
         *
         * 과
         *
         * "분석에서 누락"
         *
         * 을 구분하기 위해서다.
         * =================================================
         */

        if (
                situationProfiles.size()
                        != NatalResponseSituationType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "Personality Pattern Analysis에는 "
                            + "모든 상황 Profile이 필요합니다."
            );
        }

        Set<NatalResponseSituationType> situations =
                EnumSet.noneOf(
                        NatalResponseSituationType.class
                );

        for (
                NatalPersonalitySituationProfile profile
                : situationProfiles
        ) {

            if (
                    !situations.add(
                            profile.situationType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Personality Situation Profile이 "
                                + "중복되었습니다: "
                                + profile.situationType()
                );
            }

            if (
                    profile.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Personality Situation Profile의 "
                                + "원국 완전성 상태가 "
                                + "일치하지 않습니다."
                );
            }
        }
    }

    /**
     * 특정 기능 Profile 조회.
     */
    public Optional<NatalPersonalityFunctionProfile>
    function(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return functionProfiles
                .stream()
                .filter(
                        profile ->
                                profile.functionType()
                                        == functionType
                )
                .findFirst();
    }

    public NatalPersonalityFunctionProfile
    requireFunction(

            NatalTenGodFunctionType functionType

    ) {

        Objects.requireNonNull(
                functionType,
                "Personality 기능축은 필수입니다."
        );

        return function(
                functionType
        )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Personality 기능 Profile을 "
                                        + "찾을 수 없습니다: "
                                        + functionType
                        )
                );
    }

    /**
     * 특정 상황 Profile 조회.
     */
    public Optional<NatalPersonalitySituationProfile>
    situation(

            NatalResponseSituationType situationType

    ) {

        if (
                situationType == null
        ) {

            return Optional.empty();
        }

        return situationProfiles
                .stream()
                .filter(
                        profile ->
                                profile.situationType()
                                        == situationType
                )
                .findFirst();
    }

    /**
     * 원국 주격과 직접 연결된
     * Personality 기능축.
     *
     * 특수격의 경우
     * 일반 십성 Family 중심축이
     * 없을 수도 있다.
     */
    public List<NatalPersonalityFunctionProfile>
    structuralCenterFunctions() {

        return functionProfiles
                .stream()
                .filter(
                        NatalPersonalityFunctionProfile
                                ::structuralCenter
                )
                .toList();
    }

    /**
     * 명리 Pattern 근거상
     * 비교적 직접적으로 설명할 수 있는 상황.
     */
    public List<NatalPersonalitySituationProfile>
    directlyAssertableSituations() {

        return situationProfiles
                .stream()
                .filter(
                        NatalPersonalitySituationProfile
                                ::directlyAssertable
                )
                .toList();
    }

    /**
     * 조건 설명이 반드시 필요한 상황.
     */
    public List<NatalPersonalitySituationProfile>
    qualifiedSituations() {

        return situationProfiles
                .stream()
                .filter(
                        NatalPersonalitySituationProfile
                                ::requiresQualification
                )
                .toList();
    }

    /**
     * 원국 자기기반.
     */
    public NatalCoreSelfEffect coreSelf() {

        return natalFunctionalAnalysis
                .coreSelf();
    }

    /**
     * 격국 중심구조.
     */
    public NatalStructuralCenterEffect
    structuralCenter() {

        return natalFunctionalAnalysis
                .structuralCenter();
    }

    /**
     * 원국 관계 Context.
     *
     * 관계 자체를 성격으로 단정하지 않고
     * 별도 Evidence로 유지한다.
     */
    public NatalInterpersonalEffect
    interpersonal() {

        return natalFunctionalAnalysis
                .interpersonal();
    }
}