package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 카테고리 전용 deterministic Rule의
 * 하나의 계산 결과.
 *
 * =================================================
 * 기존 LifeDomainRuleMatch와의 차이
 * =================================================
 *
 * LifeDomainRuleMatch:
 *
 * WEALTH
 * CAREER
 * RELATIONSHIP
 * LEARNING_GROWTH
 * HEALTH_LIFESTYLE
 *
 * 같은 기초 생활영역의 deterministic 결과.
 *
 * =================================================
 *
 * ConsultationCategoryDeterministicMatch:
 *
 * PERSONALITY
 * SOCIAL_RELATIONSHIP
 * LOVE_STYLE
 * PARTNER_LUCK
 * WEALTH
 * CAREER
 * LEGAL_RISK
 * HEALTH_TENDENCY
 * ...
 *
 * 실제 상담 카테고리 기준으로
 * 다시 계산된 deterministic 결과.
 *
 * =================================================
 * Source 보존
 * =================================================
 *
 * sourceMatches에는
 * 이 결과를 만들 때 직접 사용한
 * 실제 LifeDomainRuleMatch만 존재한다.
 *
 * =================================================
 * Dependency 보존
 * =================================================
 *
 * dependencies에는
 * 상위 Synthesis Rule이 참조한
 * 기존 ConsultationCategoryDeterministicMatch의
 * immutable dependency reference가 존재한다.
 *
 * =================================================
 *
 * 예:
 *
 * 2028 TURNING_POINT
 *
 * sourceMatches
 * ->
 * 2028의 실제 LifeDomainRuleMatch
 *
 * dependencies
 * ->
 * 2027 BIG_FLOW
 * 2027 PRIMARY_ACTIVE_DOMAIN
 * ...
 * 2028 BIG_FLOW
 * ...
 * 2029 BIG_FLOW
 * ...
 *
 * =================================================
 *
 * 따라서:
 *
 * raw provenance
 *
 * 와
 *
 * deterministic dependency provenance
 *
 * 를 서로 섞지 않는다.
 *
 * =================================================
 * Timeline
 * =================================================
 *
 * NATAL:
 *
 * sajuYear == null
 * monthIndex == null
 * monthIndices == empty
 *
 * CURRENT_TIMELINE:
 *
 * sajuYear != null
 * monthIndex != null
 * monthIndices == empty
 *
 * TIMELINE_YEAR:
 *
 * sajuYear != null
 * monthIndex == null
 * monthIndices는 실제 계산값 그대로 보존.
 */
public record ConsultationCategoryDeterministicMatch(

        ConsultationCategory category,

        ConsultationCategoryDeterministicLayer layer,

        String ruleCode,

        ConsultationCategoryDeterministicStatus status,

        Integer sajuYear,

        Integer monthIndex,

        List<Integer> monthIndices,

        String summary,

        List<LifeDomainRuleMatch> sourceMatches,

        List<String> reasons,

        List<ConsultationCategoryDeterministicDependencyReference>
        dependencies

) {

    /*
     * =================================================
     * 기존 생성자 호환
     * =================================================
     *
     * 기존 모든 일반 Deterministic Rule은
     * 수정 없이 그대로 컴파일된다.
     */

    public ConsultationCategoryDeterministicMatch(

            ConsultationCategory category,

            ConsultationCategoryDeterministicLayer layer,

            String ruleCode,

            ConsultationCategoryDeterministicStatus status,

            Integer sajuYear,

            Integer monthIndex,

            List<Integer> monthIndices,

            String summary,

            List<LifeDomainRuleMatch> sourceMatches,

            List<String> reasons

    ) {

        this(
                category,
                layer,
                ruleCode,
                status,
                sajuYear,
                monthIndex,
                monthIndices,
                summary,
                sourceMatches,
                reasons,
                List.of()
        );
    }


    /*
     * =================================================
     * Canonical
     * =================================================
     */

    public ConsultationCategoryDeterministicMatch {

        Objects.requireNonNull(
                category,
                "Category Deterministic Match에는 ConsultationCategory가 필요합니다."
        );

        Objects.requireNonNull(
                layer,
                "Category Deterministic Match에는 Layer가 필요합니다."
        );

        ruleCode =
                requireText(
                        ruleCode,
                        "Category Deterministic Match에는 ruleCode가 필요합니다."
                );

        Objects.requireNonNull(
                status,
                "Category Deterministic Match에는 status가 필요합니다."
        );

        summary =
                requireText(
                        summary,
                        "Category Deterministic Match에는 summary가 필요합니다."
                );

        monthIndices =
                normalizeMonthIndices(
                        monthIndices
                );

        sourceMatches =
                normalizeSourceMatches(
                        sourceMatches
                );

        reasons =
                normalizeReasons(
                        reasons
                );

        dependencies =
                normalizeDependencies(
                        dependencies
                );

        validateTimeline(
                layer,
                sajuYear,
                monthIndex,
                monthIndices
        );
    }


    /*
     * =================================================
     * Source
     * =================================================
     */

    public int sourceMatchCount() {

        return sourceMatches
                .size();
    }


    public boolean multipleSourceMatches() {

        return sourceMatches
                .size()
                >= 2;
    }


    public Set<LifeDomain> sourceDomains() {

        LinkedHashSet<LifeDomain> result =
                new LinkedHashSet<>();

        for (
                LifeDomainRuleMatch match
                : sourceMatches
        ) {

            result.add(
                    match.domain()
            );
        }

        return Set.copyOf(
                result
        );
    }


    public int sourceDomainCount() {

        return sourceDomains()
                .size();
    }


    public boolean crossDomain() {

        return sourceDomainCount()
                >= 2;
    }


    /*
     * =================================================
     * Dependency
     * =================================================
     */

    public boolean hasDependencies() {

        return !dependencies
                .isEmpty();
    }


    public int dependencyCount() {

        return dependencies
                .size();
    }


    /*
     * =================================================
     * Timeline Helper
     * =================================================
     */

    public boolean natal() {

        return layer
                ==
                ConsultationCategoryDeterministicLayer.NATAL;
    }


    public boolean currentTimeline() {

        return layer
                ==
                ConsultationCategoryDeterministicLayer.CURRENT_TIMELINE;
    }


    public boolean timelineYear() {

        return layer
                ==
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR;
    }


    public boolean hasSajuYear() {

        return sajuYear
                != null;
    }


    public boolean hasMonthIndex() {

        return monthIndex
                != null;
    }


    public boolean hasMonthIndices() {

        return !monthIndices
                .isEmpty();
    }


    /*
     * =================================================
     * Timeline Validation
     * =================================================
     */

    private static void validateTimeline(

            ConsultationCategoryDeterministicLayer layer,

            Integer sajuYear,

            Integer monthIndex,

            List<Integer> monthIndices

    ) {

        switch (
                layer
        ) {

            case NATAL -> {

                if (
                        sajuYear != null
                                || monthIndex != null
                                || !monthIndices.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "NATAL Category Match에는 Timeline 값이 존재할 수 없습니다."
                    );
                }
            }

            case CURRENT_TIMELINE -> {

                if (
                        sajuYear == null
                ) {

                    throw new IllegalArgumentException(
                            "CURRENT_TIMELINE Category Match에는 sajuYear가 필요합니다."
                    );
                }

                if (
                        monthIndex == null
                ) {

                    throw new IllegalArgumentException(
                            "CURRENT_TIMELINE Category Match에는 monthIndex가 필요합니다."
                    );
                }

                if (
                        !monthIndices.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "CURRENT_TIMELINE Category Match에는 monthIndices 목록을 사용할 수 없습니다."
                    );
                }
            }

            case TIMELINE_YEAR -> {

                if (
                        sajuYear == null
                ) {

                    throw new IllegalArgumentException(
                            "TIMELINE_YEAR Category Match에는 sajuYear가 필요합니다."
                    );
                }

                if (
                        monthIndex != null
                ) {

                    throw new IllegalArgumentException(
                            "TIMELINE_YEAR Category Match에는 단일 monthIndex를 사용할 수 없습니다."
                    );
                }
            }
        }
    }


    /*
     * =================================================
     * Normalize Source
     * =================================================
     */

    private static List<LifeDomainRuleMatch>
    normalizeSourceMatches(

            List<LifeDomainRuleMatch> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Category Deterministic Match에는 최소 하나의 source LifeDomainRuleMatch가 필요합니다."
            );
        }

        List<LifeDomainRuleMatch> normalized =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : source
        ) {

            Objects.requireNonNull(
                    match,
                    "Category Deterministic sourceMatch는 null일 수 없습니다."
            );

            if (
                    !normalized.contains(
                            match
                    )
            ) {

                normalized.add(
                        match
                );
            }
        }

        return List.copyOf(
                normalized
        );
    }


    /*
     * =================================================
     * Normalize Dependency
     * =================================================
     */

    private static List<
            ConsultationCategoryDeterministicDependencyReference
            > normalizeDependencies(

            List<
                    ConsultationCategoryDeterministicDependencyReference
                    > source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<
                ConsultationCategoryDeterministicDependencyReference
                > normalized =
                new ArrayList<>();

        for (
                ConsultationCategoryDeterministicDependencyReference dependency
                : source
        ) {

            ConsultationCategoryDeterministicDependencyReference value =
                    Objects.requireNonNull(
                            dependency,
                            "Category Deterministic dependency는 null일 수 없습니다."
                    );

            if (
                    !normalized.contains(
                            value
                    )
            ) {

                normalized.add(
                        value
                );
            }
        }

        return List.copyOf(
                normalized
        );
    }


    /*
     * =================================================
     * Normalize Month
     * =================================================
     */

    private static List<Integer>
    normalizeMonthIndices(

            List<Integer> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<Integer> normalized =
                new ArrayList<>();

        for (
                Integer monthIndex
                : source
        ) {

            Objects.requireNonNull(
                    monthIndex,
                    "Category Deterministic monthIndex는 null일 수 없습니다."
            );

            if (
                    !normalized.contains(
                            monthIndex
                    )
            ) {

                normalized.add(
                        monthIndex
                );
            }
        }

        normalized.sort(
                Integer::compareTo
        );

        return List.copyOf(
                normalized
        );
    }


    /*
     * =================================================
     * Normalize Reason
     * =================================================
     */

    private static List<String>
    normalizeReasons(

            List<String> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        LinkedHashSet<String> normalized =
                new LinkedHashSet<>();

        for (
                String reason
                : source
        ) {

            if (
                    reason == null
            ) {

                continue;
            }

            String value =
                    reason.trim();

            if (
                    value.isBlank()
            ) {

                continue;
            }

            normalized.add(
                    value
            );
        }

        return List.copyOf(
                normalized
        );
    }


    /*
     * =================================================
     * Text
     * =================================================
     */

    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}