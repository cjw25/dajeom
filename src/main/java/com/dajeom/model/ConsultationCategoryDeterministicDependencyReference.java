package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Category Deterministic Synthesis 결과가
 * 이전에 계산된 다른
 * ConsultationCategoryDeterministicMatch를
 * 참조했다는 사실을 보존한다.
 *
 * =================================================
 *
 * 필요한 이유
 * =================================================
 *
 * 예:
 *
 * 2028 TURNING_POINT
 *
 * 는 단순히 2028 LifeDomain Match만 보고
 * 만들어지는 결과가 아니다.
 *
 * 실제로는:
 *
 * 2027 구조
 * ->
 * 2028 구조
 * ->
 * 2029 구조
 *
 * 를 비교해야 한다.
 *
 * =================================================
 *
 * 하지만 기존 sourceMatches에는
 * 여전히 현재 결과 Layer에 실제 존재하는
 * LifeDomainRuleMatch만 들어가야 한다.
 *
 * 따라서:
 *
 * sourceMatches
 *
 * =
 * 현재 결과의 원시 LifeDomain provenance
 *
 * dependencies
 *
 * =
 * 이 Synthesis 결과가 참조한
 * 기존 Category Deterministic 결과 provenance
 *
 * 로 역할을 분리한다.
 *
 * =================================================
 *
 * 재귀 객체 참조 금지
 * =================================================
 *
 * ConsultationCategoryDeterministicMatch 전체를
 * 직접 보관하지 않는다.
 *
 * 대신:
 *
 * - category
 * - layer
 * - ruleCode
 * - status
 * - sajuYear
 * - monthIndex
 * - source Domain / Status 구조
 *
 * 만 immutable reference로 보존한다.
 *
 * 따라서:
 *
 * - 순환 참조
 * - 무한 JSON 중첩
 * - 전체 결과 객체 복제
 *
 * 를 방지한다.
 *
 * =================================================
 *
 * sourceDomainStatuses
 * =================================================
 *
 * 단순 Source Match 개수는 보존하지 않는다.
 *
 * 각 LifeDomain에 어떤
 * LifeDomainSignalStatus가 실제 Source로
 * 사용되었는지만 구조적으로 보존한다.
 *
 * 따라서:
 *
 * 같은 연도의 같은 Rule 결과인지
 * Engine에서 검증할 수 있고,
 *
 * TURNING_POINT 역시 문자열 summary를
 * 파싱하지 않고 구조를 비교할 수 있다.
 */
public record ConsultationCategoryDeterministicDependencyReference(

        ConsultationCategory category,

        ConsultationCategoryDeterministicLayer layer,

        String ruleCode,

        ConsultationCategoryDeterministicStatus status,

        Integer sajuYear,

        Integer monthIndex,

        Map<
                LifeDomain,
                Set<LifeDomainSignalStatus>
                > sourceDomainStatuses

) {

    public ConsultationCategoryDeterministicDependencyReference {

        Objects.requireNonNull(
                category,
                "Deterministic Dependency에는 category가 필요합니다."
        );

        Objects.requireNonNull(
                layer,
                "Deterministic Dependency에는 layer가 필요합니다."
        );

        ruleCode =
                requireText(
                        ruleCode,
                        "Deterministic Dependency에는 ruleCode가 필요합니다."
                );

        Objects.requireNonNull(
                status,
                "Deterministic Dependency에는 status가 필요합니다."
        );

        sourceDomainStatuses =
                normalizeSourceDomainStatuses(
                        sourceDomainStatuses
                );

        validateTimeline(
                layer,
                sajuYear,
                monthIndex
        );
    }


    /*
     * =================================================
     * Match -> Dependency
     * =================================================
     */

    public static ConsultationCategoryDeterministicDependencyReference
    of(

            ConsultationCategoryDeterministicMatch match

    ) {

        Objects.requireNonNull(
                match,
                "Dependency Reference 생성에는 ConsultationCategoryDeterministicMatch가 필요합니다."
        );

        return new ConsultationCategoryDeterministicDependencyReference(
                match.category(),
                match.layer(),
                match.ruleCode(),
                match.status(),
                match.sajuYear(),
                match.monthIndex(),
                sourceDomainStatusesOf(
                        match
                )
        );
    }


    /*
     * =================================================
     * Dependency -> Match 검증
     * =================================================
     */

    public boolean matches(

            ConsultationCategoryDeterministicMatch candidate

    ) {

        if (
                candidate == null
        ) {

            return false;
        }

        if (
                category
                        != candidate.category()
        ) {

            return false;
        }

        if (
                layer
                        != candidate.layer()
        ) {

            return false;
        }

        if (
                !ruleCode.equals(
                        candidate.ruleCode()
                )
        ) {

            return false;
        }

        if (
                status
                        != candidate.status()
        ) {

            return false;
        }

        if (
                !Objects.equals(
                        sajuYear,
                        candidate.sajuYear()
                )
        ) {

            return false;
        }

        if (
                !Objects.equals(
                        monthIndex,
                        candidate.monthIndex()
                )
        ) {

            return false;
        }

        return sourceDomainStatuses.equals(
                sourceDomainStatusesOf(
                        candidate
                )
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


    /*
     * =================================================
     * Source 구조 생성
     * =================================================
     */

    private static Map<
            LifeDomain,
            Set<LifeDomainSignalStatus>
            > sourceDomainStatusesOf(

            ConsultationCategoryDeterministicMatch match

    ) {

        EnumMap<
                LifeDomain,
                LinkedHashSet<LifeDomainSignalStatus>
                > mutable =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomainRuleMatch sourceMatch
                : match.sourceMatches()
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            sourceMatch,
                            "Deterministic Match sourceMatch는 null일 수 없습니다."
                    );

            mutable
                    .computeIfAbsent(
                            normalized.domain(),
                            ignored ->
                                    new LinkedHashSet<>()
                    )
                    .add(
                            normalized.status()
                    );
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
                        LinkedHashSet<LifeDomainSignalStatus>
                        > entry
                : mutable.entrySet()
        ) {

            result.put(
                    entry.getKey(),
                    Set.copyOf(
                            entry.getValue()
                    )
            );
        }

        return Collections.unmodifiableMap(
                result
        );
    }


    /*
     * =================================================
     * Normalize
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
                            "Dependency Source Domain은 null일 수 없습니다."
                    );

            Set<LifeDomainSignalStatus> statuses =
                    Objects.requireNonNull(
                            entry.getValue(),
                            "Dependency Source Status Set은 null일 수 없습니다."
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
                                "Dependency Source Status는 null일 수 없습니다."
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
                    Set.copyOf(
                            normalizedStatuses
                    )
            );
        }

        return Collections.unmodifiableMap(
                result
        );
    }


    /*
     * =================================================
     * Timeline Validation
     * =================================================
     */

    private static void validateTimeline(

            ConsultationCategoryDeterministicLayer layer,

            Integer sajuYear,

            Integer monthIndex

    ) {

        switch (
                layer
        ) {

            case NATAL -> {

                if (
                        sajuYear != null
                                || monthIndex != null
                ) {

                    throw new IllegalArgumentException(
                            "NATAL Dependency에는 Timeline 값이 존재할 수 없습니다."
                    );
                }
            }

            case CURRENT_TIMELINE -> {

                if (
                        sajuYear == null
                                || monthIndex == null
                ) {

                    throw new IllegalArgumentException(
                            "CURRENT_TIMELINE Dependency에는 sajuYear와 monthIndex가 필요합니다."
                    );
                }
            }

            case TIMELINE_YEAR -> {

                if (
                        sajuYear == null
                ) {

                    throw new IllegalArgumentException(
                            "TIMELINE_YEAR Dependency에는 sajuYear가 필요합니다."
                    );
                }

                if (
                        monthIndex != null
                ) {

                    throw new IllegalArgumentException(
                            "TIMELINE_YEAR Dependency에는 단일 monthIndex를 사용할 수 없습니다."
                    );
                }
            }
        }
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