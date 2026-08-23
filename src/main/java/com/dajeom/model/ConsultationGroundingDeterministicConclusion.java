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
 * 보존 구조
 * =================================================
 *
 * 다음 구조만 보존한다.
 *
 * - ruleCode
 * - deterministic status
 * - deterministic summary
 * - source LifeDomain / Status 구조
 * - deterministic reasons
 * - 상위 Synthesis dependency provenance
 * - 자기 자신의 deterministic structural provenance
 *
 * =================================================
 * structuralReference
 * =================================================
 *
 * ConsultationCategoryDeterministicMatch 전체를
 * Grounding 경계에 그대로 넣지 않으면서도:
 *
 * - category
 * - layer
 * - ruleCode
 * - status
 * - sajuYear
 * - monthIndex
 * - sourceDomainStatuses
 *
 * 를 보존하기 위한 자기 자신의
 * immutable structural reference다.
 *
 * 실제 타입은:
 *
 * ConsultationCategoryDeterministicDependencyReference
 *
 * 를 재사용한다.
 *
 * =================================================
 * 공식 생성 계약
 * =================================================
 *
 * Grounding Deterministic Conclusion은
 * 반드시 structuralReference를 가진다.
 *
 * structuralReference가 없는
 * Deterministic Conclusion은
 * 공식 Grounding 경계에 존재할 수 없다.
 *
 * 호출부는 반드시:
 *
 * ConsultationCategoryDeterministicMatch
 *
 * 로부터 자기 자신의 Structural Reference를
 * 생성해서 전달해야 한다.
 *
 * =================================================
 * 필요한 이유
 * =================================================
 *
 * structuralReference가 없으면:
 *
 * - category
 * - layer
 * - sajuYear
 * - monthIndex
 *
 * 정보를 잃게 된다.
 *
 * 그러면 Grounding Policy 단계에서:
 *
 * NATAL Conclusion이
 * 실제 NATAL Layer에서 왔는지,
 *
 * CURRENT_TIMELINE Conclusion이
 * 실제 현재 Scenario 좌표에 해당하는지,
 *
 * 해당 Conclusion이
 * 현재 ConsultationCategory에서 생성된 것인지
 *
 * 구조적으로 재검증할 수 없다.
 *
 * =================================================
 * RuleCode 문자열 추측 금지
 * =================================================
 *
 * 다음처럼 검사하지 않는다.
 *
 * ruleCode가 WEALTH_로 시작하니까
 * WEALTH Category일 것이다.
 *
 * ruleCode에 MONTHLY가 있으니까
 * CURRENT_TIMELINE일 것이다.
 *
 * 이런 방식은 공식 구조 검증이 아니다.
 *
 * structuralReference의
 * 실제 enum / timeline 좌표를 사용한다.
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
 * 반복 횟수 경계
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
 *
 * =================================================
 * structuralReference와 dependencies 차이
 * =================================================
 *
 * structuralReference
 *
 * =
 * 현재 Conclusion 자기 자신의 구조.
 *
 * -------------------------------------------------
 *
 * dependencies
 *
 * =
 * 현재 Conclusion이 Synthesis 과정에서 참조한
 * 이전 Deterministic Match들의 구조.
 *
 * 두 개념을 합치지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * structuralReference 또는 dependencies가
 * 여러 개 존재한다고 해서:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * 같은 의미를 LLM이 새로 만들 수 있다는
 * 뜻이 아니다.
 *
 * dependencies는 Java Synthesis Rule이
 * 실제 참조한 provenance일 뿐이다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - sourceDomainStatuses 다수결
 * - dependency 개수 점수화
 * - sourceDomain 개수 점수화
 * - deterministic status 재계산
 * - 관계 생성
 * - 인과 생성
 * - 순서 생성
 * - structuralReference 없는 우회 생성
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
        dependencies,

        ConsultationCategoryDeterministicDependencyReference
        structuralReference

) {

    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

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

        structuralReference =
                Objects.requireNonNull(
                        structuralReference,
                        "Grounding Deterministic Conclusion에는 structuralReference가 반드시 필요합니다. rule="
                                + ruleCode
                );

        /*
         * =================================================
         * 자기 Structural Reference 일치 검증
         * =================================================
         *
         * - ruleCode
         * - status
         * - sourceDomainStatuses
         *
         * 는 현재 Grounding Conclusion과
         * 반드시 일치해야 한다.
         *
         * category / layer / timeline 좌표는
         * Grounding Conclusion에 별도 중복 저장하지 않고
         * structuralReference를 공식 Source로 사용한다.
         */

        if (
                !structuralReference
                        .ruleCode()
                        .equals(
                                ruleCode
                        )
        ) {

            throw new IllegalArgumentException(
                    "Grounding Deterministic Conclusion의 ruleCode와 "
                            + "structuralReference.ruleCode가 일치하지 않습니다. "
                            + "conclusionRule="
                            + ruleCode
                            + ", referenceRule="
                            + structuralReference.ruleCode()
            );
        }

        if (
                structuralReference.status()
                        != status
        ) {

            throw new IllegalArgumentException(
                    "Grounding Deterministic Conclusion의 status와 "
                            + "structuralReference.status가 일치하지 않습니다. "
                            + "rule="
                            + ruleCode
                            + ", conclusionStatus="
                            + status
                            + ", referenceStatus="
                            + structuralReference.status()
            );
        }

        if (
                !structuralReference
                        .sourceDomainStatuses()
                        .equals(
                                sourceDomainStatuses
                        )
        ) {

            throw new IllegalArgumentException(
                    "Grounding Deterministic Conclusion의 sourceDomainStatuses와 "
                            + "structuralReference.sourceDomainStatuses가 일치하지 않습니다. "
                            + "rule="
                            + ruleCode
            );
        }
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
                    LifeDomainSignalStatus sourceStatus
                    : statuses
            ) {

                normalizedStatuses.add(
                        Objects.requireNonNull(
                                sourceStatus,
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