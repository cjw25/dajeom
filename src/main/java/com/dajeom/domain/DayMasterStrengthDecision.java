package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 일간 왕쇠/특수구조의 최종 라우팅 결과.
 *
 * =================================================
 *
 * TRUE_TRANSFORMATION
 *
 * → 真化가 최종 우선
 * → 일반 왕쇠 없음
 *
 * =================================================
 *
 * FOLLOWING_STRUCTURE_ESTABLISHED
 *
 * → 실제 종격 성립
 * → 일반 왕쇠 없음
 *
 * =================================================
 *
 * GENERAL_STRENGTH_WITH_SPECIAL_CONTEXT
 *
 * → 특수구조 후보/천간합 Context는 있었지만
 *   최종 특수격으로 성립하지 않음
 * → 일반 왕쇠 결과 존재
 *
 * =================================================
 *
 * GENERAL_STRENGTH
 *
 * → 순수 일반 왕쇠
 *
 * =================================================
 */
public record DayMasterStrengthDecision(

        HeavenlyStem dayMaster,

        DayMasterStrengthDecisionPath path,

        Optional<DayMasterStrengthAnalysis> ordinaryStrength,

        Optional<DayMasterFollowingStructureAnalysis>
        followingStructure,

        Set<DayMasterSpecialStructureCandidateType>
        followingCandidates,

        List<DayMasterStemTransformationAssessment>
        stemTransformationAssessments

) {

    public DayMasterStrengthDecision {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                path,
                "판정 경로는 필수입니다."
        );

        ordinaryStrength =
                ordinaryStrength == null
                        ? Optional.empty()
                        : ordinaryStrength;

        followingStructure =
                followingStructure == null
                        ? Optional.empty()
                        : followingStructure;

        followingCandidates =
                followingCandidates == null
                        ? Set.of()
                        : Set.copyOf(
                        followingCandidates
                );

        stemTransformationAssessments =
                stemTransformationAssessments == null
                        ? List.of()
                        : List.copyOf(
                        stemTransformationAssessments
                );

        ordinaryStrength.ifPresent(
                strength -> {

                    if (
                            strength.dayMaster()
                                    != dayMaster
                    ) {

                        throw new IllegalArgumentException(
                                "일반 왕쇠 분석의 일간이 일치하지 않습니다."
                        );
                    }
                }
        );

        followingStructure.ifPresent(
                following -> {

                    if (
                            following.dayMaster()
                                    != dayMaster
                    ) {

                        throw new IllegalArgumentException(
                                "종격 분석의 일간이 일치하지 않습니다."
                        );
                    }
                }
        );

        for (
                DayMasterStemTransformationAssessment assessment
                : stemTransformationAssessments
        ) {

            Objects.requireNonNull(
                    assessment,
                    "천간 화기 판정 목록에 null이 포함될 수 없습니다."
            );

            if (
                    assessment.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "천간 화기 판정의 일간이 일치하지 않습니다."
                );
            }
        }

        validatePath(
                path,
                ordinaryStrength,
                followingStructure,
                stemTransformationAssessments
        );
    }

    public boolean trueTransformationPath() {

        return path
                == DayMasterStrengthDecisionPath
                .TRUE_TRANSFORMATION;
    }

    public boolean followingStructurePath() {

        return path
                == DayMasterStrengthDecisionPath
                .FOLLOWING_STRUCTURE_ESTABLISHED;
    }

    public boolean ordinaryStrengthPath() {

        return path
                == DayMasterStrengthDecisionPath
                .GENERAL_STRENGTH

                ||

                path
                        == DayMasterStrengthDecisionPath
                        .GENERAL_STRENGTH_WITH_SPECIAL_CONTEXT;
    }

    /**
     * 真化 결과.
     *
     * 데이터 무결성상 하나만 존재해야 한다.
     */
    public Optional<DayMasterStemTransformationAssessment>
    trueTransformation() {

        return stemTransformationAssessments
                .stream()
                .filter(
                        DayMasterStemTransformationAssessment
                                ::trueTransformation
                )
                .findFirst();
    }

    /**
     * 실제 성립한 종격.
     */
    public Optional<DayMasterFollowingStructureAnalysis>
    establishedFollowingStructure() {

        return followingStructure
                .filter(
                        DayMasterFollowingStructureAnalysis
                                ::established
                );
    }

    /**
     * 종격 후보였지만
     * 실제로는 성립하지 않은 분석.
     */
    public Optional<DayMasterFollowingStructureAnalysis>
    rejectedFollowingStructure() {

        return followingStructure
                .filter(
                        following ->
                                !following.established()
                );
    }

    public List<DayMasterStemTransformationAssessment>
    fakeTransformations() {

        return stemTransformationAssessments
                .stream()
                .filter(
                        DayMasterStemTransformationAssessment
                                ::fakeTransformation
                )
                .toList();
    }

    public List<DayMasterStemTransformationAssessment>
    contestedTransformations() {

        return stemTransformationAssessments
                .stream()
                .filter(
                        DayMasterStemTransformationAssessment
                                ::contested
                )
                .toList();
    }

    public List<DayMasterStemTransformationAssessment>
    notTransformedCombinations() {

        return stemTransformationAssessments
                .stream()
                .filter(
                        DayMasterStemTransformationAssessment
                                ::notTransformed
                )
                .toList();
    }

    /**
     * 특수구조를 검토했지만
     * 일반 왕쇠로 복귀한 Context가 있는가.
     */
    public boolean hasRejectedSpecialContext() {

        return rejectedFollowingStructure()
                .isPresent()

                ||

                !fakeTransformations()
                        .isEmpty()

                ||

                !contestedTransformations()
                        .isEmpty()

                ||

                !notTransformedCombinations()
                        .isEmpty();
    }

    private static void validatePath(

            DayMasterStrengthDecisionPath path,

            Optional<DayMasterStrengthAnalysis>
                    ordinaryStrength,

            Optional<DayMasterFollowingStructureAnalysis>
                    followingStructure,

            List<DayMasterStemTransformationAssessment>
                    transformations

    ) {

        switch (path) {

            case TRUE_TRANSFORMATION -> {

                if (
                        ordinaryStrength.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "真化 경로에는 일반 왕쇠 결과가 존재할 수 없습니다."
                    );
                }

                if (
                        followingStructure.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "真化 경로에서는 종격을 동시에 최종 판정할 수 없습니다."
                    );
                }

                long trueCount =
                        transformations
                                .stream()
                                .filter(
                                        DayMasterStemTransformationAssessment
                                                ::trueTransformation
                                )
                                .count();

                if (
                        trueCount != 1
                ) {

                    throw new IllegalArgumentException(
                            "真化 경로에는 정확히 하나의 TRUE_TRANSFORMATION이 필요합니다."
                    );
                }
            }

            case FOLLOWING_STRUCTURE_ESTABLISHED -> {

                if (
                        ordinaryStrength.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "종격 성립 경로에는 일반 왕쇠 결과가 존재할 수 없습니다."
                    );
                }

                DayMasterFollowingStructureAnalysis following =
                        followingStructure
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "종격 성립 경로에는 종격 분석 결과가 필요합니다."
                                                )
                                );

                if (
                        !following.established()
                ) {

                    throw new IllegalArgumentException(
                            "종격 성립 경로인데 실제 종격이 성립하지 않았습니다."
                    );
                }

                boolean trueTransformationExists =
                        transformations
                                .stream()
                                .anyMatch(
                                        DayMasterStemTransformationAssessment
                                                ::trueTransformation
                                );

                if (
                        trueTransformationExists
                ) {

                    throw new IllegalArgumentException(
                            "真化와 종격을 동시에 최종 판정할 수 없습니다."
                    );
                }
            }

            case GENERAL_STRENGTH_WITH_SPECIAL_CONTEXT -> {

                if (
                        ordinaryStrength.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "일반 왕쇠 복귀 경로에는 일반 왕쇠 결과가 필요합니다."
                    );
                }

                boolean rejectedFollowing =
                        followingStructure
                                .map(
                                        following ->
                                                !following.established()
                                )
                                .orElse(
                                        false
                                );

                boolean nonTrueStemContext =
                        transformations
                                .stream()
                                .anyMatch(
                                        transformation ->
                                                !transformation
                                                        .trueTransformation()
                                );

                if (
                        !rejectedFollowing
                                &&
                                !nonTrueStemContext
                ) {

                    throw new IllegalArgumentException(
                            "특수 Context 경로인데 불성립 종격이나 천간합 Context가 없습니다."
                    );
                }

                boolean trueTransformationExists =
                        transformations
                                .stream()
                                .anyMatch(
                                        DayMasterStemTransformationAssessment
                                                ::trueTransformation
                                );

                if (
                        trueTransformationExists
                ) {

                    throw new IllegalArgumentException(
                            "일반 왕쇠 경로에 TRUE_TRANSFORMATION이 포함될 수 없습니다."
                    );
                }
            }

            case GENERAL_STRENGTH -> {

                if (
                        ordinaryStrength.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "일반 왕쇠 경로에는 일반 왕쇠 결과가 필요합니다."
                    );
                }

                if (
                        followingStructure.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "순수 일반 왕쇠 경로에는 종격 검토 결과가 포함될 수 없습니다."
                    );
                }

                if (
                        !transformations.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "순수 일반 왕쇠 경로에는 천간 화기 Context가 포함될 수 없습니다."
                    );
                }
            }
        }
    }
}