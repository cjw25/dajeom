package com.dajeom.model;

import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Category Deterministic Match를
 * LLM Grounding 경계로 변환한 결과.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * ConsultationCategoryDeterministicMatch는
 * 내부 계산 provenance 보존을 위해
 * 실제 LifeDomainRuleMatch를 sourceMatches로 가진다.
 *
 * 하지만 LLM Grounding 경계에서는
 * 그 내부 계산 객체를 직접 노출하지 않는다.
 *
 * =================================================
 *
 * 따라서 다음 구조만 보존한다.
 *
 * - ruleCode
 * - deterministic status
 * - deterministic summary
 * - source LifeDomain / Status 구조
 * - deterministic reasons
 * - 상위 Synthesis dependency provenance
 *
 * =================================================
 * sourceDomainStatuses
 * =================================================
 *
 * Match 개수를 보존하는 것이 아니다.
 *
 * 예:
 *
 * WEALTH:
 * PRESSURE
 * PRESSURE
 * PRESSURE
 *
 * 라고 하더라도:
 *
 * WEALTH -> {PRESSURE}
 *
 * 로 보존된다.
 *
 * =================================================
 *
 * 따라서:
 *
 * - Match 반복 횟수
 * - Rule 반복 횟수
 *
 * 를 LLM이 강도로 오해하는 것을 방지한다.
 *
 * =================================================
 * dependencies
 * =================================================
 *
 * TURNING_POINT / OVERALL_NARRATIVE 같은
 * Synthesis 결과가 어떤 이전 deterministic 결과를
 * 참조했는지 추적하기 위한 provenance다.
 *
 * 이것 자체를:
 *
 * - 확률
 * - 점수
 * - 독립 Evidence 개수
 *
 * 로 해석하지 않는다.
 */
public record ConsultationGroundingDeterministicConclusion(

        String ruleCode,

        ConsultationCategoryDeterministicStatus status,

        String summary,

        Map<
                LifeDomain,
                Set<LifeDomainSignalStatus>
                > sourceDomainStatuses,

        List<String> reasons,

        List<ConsultationCategoryDeterministicDependencyReference>
        dependencies

) {

    public ConsultationGroundingDeterministicConclusion {

        ruleCode =
                requireText(
                        ruleCode,
                        "Grounding Deterministic Conclusion에는 ruleCode가 필요합니다."
                );

        Objects.requireNonNull(
                status,
                "Grounding Deterministic Conclusion에는 status가 필요합니다."
        );

        summary =
                requireText(
                        summary,
                        "Grounding Deterministic Conclusion에는 summary가 필요합니다."
                );

        sourceDomainStatuses =
                normalizeSourceDomainStatuses(
                        sourceDomainStatuses
                );

        reasons =
                normalizeReasons(
                        reasons
                );

        dependencies =
                normalizeDependencies(
                        dependencies
                );
    }


    /*
     * =================================================
     * Source Domain
     * =================================================
     */

    public Set<LifeDomain> sourceDomains() {

        return Set.copyOf(
                sourceDomainStatuses
                        .keySet()
        );
    }


    public int sourceDomainCount() {

        return sourceDomainStatuses
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
     * Normalize Source Domain Status
     * =================================================
     */

    private static Map<
            LifeDomain,
            Set<LifeDomainSignalStatus>
            > normalizeSourceDomainStatuses(

            Map<
                    LifeDomain,
                    Set<LifeDomainSignalStatus>
                    > source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Map.of();
        }

        EnumMap<
                LifeDomain,
                Set<LifeDomainSignalStatus>
                > result =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                Map.Entry<
                        LifeDomain,
                        Set<LifeDomainSignalStatus>
                        > entry
                : source.entrySet()
        ) {

            LifeDomain domain =
                    Objects.requireNonNull(
                            entry.getKey(),
                            "Grounding Deterministic sourceDomain은 null일 수 없습니다."
                    );

            Set<LifeDomainSignalStatus> statuses =
                    Objects.requireNonNull(
                            entry.getValue(),
                            "Grounding Deterministic source status Set은 null일 수 없습니다."
                    );

            LinkedHashSet<LifeDomainSignalStatus>
                    normalizedStatuses =
                    new LinkedHashSet<>();

            for (
                    LifeDomainSignalStatus status
                    : statuses
            ) {

                normalizedStatuses.add(
                        Objects.requireNonNull(
                                status,
                                "Grounding Deterministic source status는 null일 수 없습니다."
                        )
                );
            }

            if (
                    normalizedStatuses.isEmpty()
            ) {

                continue;
            }

            result.put(
                    domain,
                    Collections.unmodifiableSet(
                            new LinkedHashSet<>(
                                    normalizedStatuses
                            )
                    )
            );
        }

        return Collections.unmodifiableMap(
                result
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
                > result =
                new ArrayList<>();

        for (
                ConsultationCategoryDeterministicDependencyReference dependency
                : source
        ) {

            ConsultationCategoryDeterministicDependencyReference normalized =
                    Objects.requireNonNull(
                            dependency,
                            "Grounding Deterministic dependency는 null일 수 없습니다."
                    );

            if (
                    !result.contains(
                            normalized
                    )
            ) {

                result.add(
                        normalized
                );
            }
        }

        return List.copyOf(
                result
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