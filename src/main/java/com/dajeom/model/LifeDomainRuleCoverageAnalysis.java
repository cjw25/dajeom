package com.dajeom.model;

import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * LifeDomainRule의 Evidence Source 선언 현황 분석.
 *
 * =================================================
 *
 * 목적:
 *
 * Domain Policy가 허용한 Source와
 * 실제 등록 Rule이 supportedSources()로 선언한 Source를
 * 비교한다.
 *
 * =================================================
 *
 * allowedSourcesByDomain
 *
 * 해당 Domain Policy에서 사용 가능한 전체 Source.
 *
 * =================================================
 *
 * declaredSourcesByDomain
 *
 * 현재 등록된 LifeDomainRule들이
 * supportedSources()로 실제 선언한 Source의 합집합.
 *
 * =================================================
 *
 * unconsumedAllowedSourcesByDomain
 *
 * Policy상 허용되어 있지만
 * 현재 어떤 Rule도 직접 supportedSources()로
 * 선언하지 않은 Source.
 *
 * =================================================
 *
 * 중요:
 *
 * unconsumedAllowedSource
 * != 구현 오류
 *
 * 어떤 upstream Source는
 * 다른 복합 분석 내부에서 이미 교차검증에
 * 사용되었을 수도 있다.
 *
 * 예:
 *
 * STRUCTURE / USEFUL_GOD / TRANSFORMATION
 *
 * 은 10차 십성 교차검증 내부에도
 * 이미 관여할 수 있다.
 *
 * 따라서 이 Analysis는:
 *
 * - 누락 후보 발견
 * - 중복 해석 방지
 * - Rule 확장 판단
 *
 * 을 위한 점검 자료다.
 *
 * 모든 허용 Source에 반드시
 * 독립 Rule을 만들어야 한다는 의미가 아니다.
 */
public record LifeDomainRuleCoverageAnalysis(

        Map<
                LifeDomain,
                Set<LifeDomainEvidenceSourceType>
                > allowedSourcesByDomain,

        Map<
                LifeDomain,
                Set<LifeDomainEvidenceSourceType>
                > declaredSourcesByDomain,

        Map<
                LifeDomain,
                Set<LifeDomainEvidenceSourceType>
                > unconsumedAllowedSourcesByDomain,

        Map<
                LifeDomain,
                List<String>
                > ruleCodesByDomain

) {

    public LifeDomainRuleCoverageAnalysis {

        allowedSourcesByDomain =
                normalizeSourceMap(
                        allowedSourcesByDomain,
                        "allowedSourcesByDomain"
                );

        declaredSourcesByDomain =
                normalizeSourceMap(
                        declaredSourcesByDomain,
                        "declaredSourcesByDomain"
                );

        unconsumedAllowedSourcesByDomain =
                normalizeSourceMap(
                        unconsumedAllowedSourcesByDomain,
                        "unconsumedAllowedSourcesByDomain"
                );

        ruleCodesByDomain =
                normalizeRuleCodeMap(
                        ruleCodesByDomain
                );

        /*
         * =================================================
         * 선언 Source는 반드시 Policy 허용 Source의 부분집합.
         * =================================================
         */

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            Set<LifeDomainEvidenceSourceType> allowed =
                    allowedSourcesByDomain.get(
                            domain
                    );

            Set<LifeDomainEvidenceSourceType> declared =
                    declaredSourcesByDomain.get(
                            domain
                    );

            if (
                    !allowed.containsAll(
                            declared
                    )
            ) {

                EnumSet<LifeDomainEvidenceSourceType> invalid =
                        EnumSet.noneOf(
                                LifeDomainEvidenceSourceType.class
                        );

                invalid.addAll(
                        declared
                );

                invalid.removeAll(
                        allowed
                );

                throw new IllegalArgumentException(
                        "Policy에서 허용되지 않은 LifeDomain Rule Source가 선언되어 있습니다. "
                                + "domain="
                                + domain
                                + ", invalid="
                                + invalid
                );
            }

            /*
             * =================================================
             * unconsumed는 정확히 allowed - declared여야 한다.
             * =================================================
             */

            EnumSet<LifeDomainEvidenceSourceType> expected =
                    EnumSet.noneOf(
                            LifeDomainEvidenceSourceType.class
                    );

            expected.addAll(
                    allowed
            );

            expected.removeAll(
                    declared
            );

            if (
                    !expected.equals(
                            unconsumedAllowedSourcesByDomain.get(
                                    domain
                            )
                    )
            ) {

                throw new IllegalArgumentException(
                        "unconsumedAllowedSources 계산 결과가 일치하지 않습니다. "
                                + "domain="
                                + domain
                );
            }
        }
    }

    /*
     * =================================================
     * Domain별 조회
     * =================================================
     */

    public Set<LifeDomainEvidenceSourceType>
    allowedSourcesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return Set.of();
        }

        return allowedSourcesByDomain
                .getOrDefault(
                        domain,
                        Set.of()
                );
    }

    public Set<LifeDomainEvidenceSourceType>
    declaredSourcesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return Set.of();
        }

        return declaredSourcesByDomain
                .getOrDefault(
                        domain,
                        Set.of()
                );
    }

    public Set<LifeDomainEvidenceSourceType>
    unconsumedAllowedSourcesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return Set.of();
        }

        return unconsumedAllowedSourcesByDomain
                .getOrDefault(
                        domain,
                        Set.of()
                );
    }

    public List<String> ruleCodesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return List.of();
        }

        return ruleCodesByDomain
                .getOrDefault(
                        domain,
                        List.of()
                );
    }

    /*
     * =================================================
     * Coverage 상태
     * =================================================
     *
     * fullyDirectlyCovered는
     *
     * Policy 허용 Source마다
     * 최소 하나의 Rule 선언이 존재한다는 뜻.
     *
     * 명리학적으로 분석이 완전하다는 뜻은 아니다.
     */

    public boolean fullyDirectlyCovered(
            LifeDomain domain
    ) {

        return domain != null
                && unconsumedAllowedSourcesOf(
                domain
        )
                .isEmpty();
    }

    public boolean hasUnconsumedAllowedSources(
            LifeDomain domain
    ) {

        return !unconsumedAllowedSourcesOf(
                domain
        )
                .isEmpty();
    }

    public boolean allDomainsFullyDirectlyCovered() {

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            if (
                    !fullyDirectlyCovered(
                            domain
                    )
            ) {

                return false;
            }
        }

        return true;
    }

    public int ruleCount(
            LifeDomain domain
    ) {

        return ruleCodesOf(
                domain
        )
                .size();
    }

    /*
     * =================================================
     * 전체 Unconsumed 조회
     * =================================================
     */

    public Map<
            LifeDomain,
            Set<LifeDomainEvidenceSourceType>
            > domainsWithUnconsumedAllowedSources() {

        EnumMap<
                LifeDomain,
                Set<LifeDomainEvidenceSourceType>
                > result =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            Set<LifeDomainEvidenceSourceType> sources =
                    unconsumedAllowedSourcesOf(
                            domain
                    );

            if (
                    !sources.isEmpty()
            ) {

                result.put(
                        domain,
                        sources
                );
            }
        }

        return Collections.unmodifiableMap(
                result
        );
    }

    /*
     * =================================================
     * Normalize Source Map
     * =================================================
     */

    private static Map<
            LifeDomain,
            Set<LifeDomainEvidenceSourceType>
            > normalizeSourceMap(

            Map<
                    LifeDomain,
                    Set<LifeDomainEvidenceSourceType>
                    > source,

            String mapName

    ) {

        EnumMap<
                LifeDomain,
                Set<LifeDomainEvidenceSourceType>
                > normalized =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            Set<LifeDomainEvidenceSourceType> values =
                    source == null
                            ? Set.of()
                            : source.get(
                            domain
                    );

            EnumSet<LifeDomainEvidenceSourceType> copied =
                    EnumSet.noneOf(
                            LifeDomainEvidenceSourceType.class
                    );

            if (
                    values != null
            ) {

                for (
                        LifeDomainEvidenceSourceType value
                        : values
                ) {

                    copied.add(
                            Objects.requireNonNull(
                                    value,
                                    mapName
                                            + "에는 null Source가 들어갈 수 없습니다. domain="
                                            + domain
                            )
                    );
                }
            }

            normalized.put(
                    domain,
                    Collections.unmodifiableSet(
                            copied
                    )
            );
        }

        if (
                source != null
        ) {

            for (
                    LifeDomain domain
                    : source.keySet()
            ) {

                Objects.requireNonNull(
                        domain,
                        mapName
                                + "의 LifeDomain Key는 null일 수 없습니다."
                );
            }
        }

        return Collections.unmodifiableMap(
                normalized
        );
    }

    /*
     * =================================================
     * Normalize Rule Code Map
     * =================================================
     */

    private static Map<
            LifeDomain,
            List<String>
            > normalizeRuleCodeMap(

            Map<
                    LifeDomain,
                    List<String>
                    > source

    ) {

        EnumMap<
                LifeDomain,
                List<String>
                > normalized =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            List<String> codes =
                    source == null
                            ? List.of()
                            : source.get(
                            domain
                    );

            List<String> normalizedCodes =
                    new ArrayList<>();

            if (
                    codes != null
            ) {

                for (
                        String code
                        : codes
                ) {

                    if (
                            code == null
                                    || code.isBlank()
                    ) {

                        throw new IllegalArgumentException(
                                "LifeDomain Rule Code는 비어 있을 수 없습니다. domain="
                                        + domain
                        );
                    }

                    normalizedCodes.add(
                            code.trim()
                    );
                }
            }

            /*
             * 동일 Domain에서 Rule Code 중복 금지.
             */

            Set<String> unique =
                    new HashSet<>(
                            normalizedCodes
                    );

            if (
                    unique.size()
                            != normalizedCodes.size()
            ) {

                throw new IllegalArgumentException(
                        "동일 Domain의 LifeDomain Rule Code가 중복되었습니다: "
                                + domain
                );
            }

            normalizedCodes.sort(
                    String::compareTo
            );

            normalized.put(
                    domain,
                    List.copyOf(
                            normalizedCodes
                    )
            );
        }

        return Collections.unmodifiableMap(
                normalized
        );
    }
}