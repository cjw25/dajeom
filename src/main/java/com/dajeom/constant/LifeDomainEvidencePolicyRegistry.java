package com.dajeom.constant;

import com.dajeom.model.LifeDomainEvidencePolicy;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차-1.
 *
 * 생활영역 분석에서 사용할 수 있는
 * Evidence Source 정책의 Single Source of Truth.
 *
 * =================================================
 *
 * 신살:
 *
 * 모든 Domain에서 보조 Evidence로 사용할 수 있으나
 * Tier 자체가 AUXILIARY이므로
 * 구조 Evidence보다 먼저 판정에 사용하지 않는다.
 *
 * =================================================
 *
 * Annual Functional Context:
 *
 * 신규 세운 Functional Chain의 최종 기준선.
 *
 * 모든 Domain에서 참조할 수 있지만,
 * Function 하나를 Domain 결과 하나로
 * 기계적으로 변환하지 않는다.
 *
 * standaloneOutcomeAllowed=false를 유지한다.
 *
 * =================================================
 *
 * standaloneOutcomeAllowed:
 *
 * 현재 모든 Source = false.
 *
 * 생활영역의 사건 결과를
 * 단일 명리 Evidence 하나로 확정하지 않는다.
 */
@Component
public class LifeDomainEvidencePolicyRegistry {

    private final Map<
            LifeDomainEvidenceSourceType,
            LifeDomainEvidencePolicy
            > policies;

    public LifeDomainEvidencePolicyRegistry() {

        EnumMap<
                LifeDomainEvidenceSourceType,
                LifeDomainEvidencePolicy
                > map =
                new EnumMap<>(
                        LifeDomainEvidenceSourceType.class
                );

        register(
                map,
                LifeDomainEvidenceSourceType.ELEMENT_STRENGTH,
                allDomains()
        );

        register(
                map,
                LifeDomainEvidenceSourceType.DAY_MASTER_STRENGTH,
                allDomains()
        );

        register(
                map,
                LifeDomainEvidenceSourceType.STRUCTURE,
                EnumSet.of(
                        LifeDomain.WEALTH,
                        LifeDomain.CAREER,
                        LifeDomain.RELATIONSHIP,
                        LifeDomain.LEARNING_GROWTH
                )
        );

        register(
                map,
                LifeDomainEvidenceSourceType.CLIMATE_DISEASE_FLOW,
                EnumSet.of(
                        LifeDomain.HEALTH_LIFESTYLE,
                        LifeDomain.LEARNING_GROWTH,
                        LifeDomain.CAREER
                )
        );

        register(
                map,
                LifeDomainEvidenceSourceType.USEFUL_GOD,
                allDomains()
        );

        register(
                map,
                LifeDomainEvidenceSourceType.TEN_GOD_COMPOSITE,
                EnumSet.of(
                        LifeDomain.WEALTH,
                        LifeDomain.CAREER,
                        LifeDomain.RELATIONSHIP,
                        LifeDomain.LEARNING_GROWTH
                )
        );

        register(
                map,
                LifeDomainEvidenceSourceType.TWELVE_LIFE_STAGE,
                allDomains()
        );

        register(
                map,
                LifeDomainEvidenceSourceType.PALACE,
                allDomains()
        );

        register(
                map,
                LifeDomainEvidenceSourceType.NATAL_RELATION,
                allDomains()
        );

        register(
                map,
                LifeDomainEvidenceSourceType.TRANSFORMATION,
                allDomains()
        );

        register(
                map,
                LifeDomainEvidenceSourceType.DECADE_CONTEXT,
                allDomains()
        );

        register(
                map,
                LifeDomainEvidenceSourceType.ANNUAL_CONTEXT,
                allDomains()
        );

        /*
         * =================================================
         * Annual Functional
         * =================================================
         *
         * 모든 Domain이 Functional 구조를
         * 참조할 수 있다.
         *
         * 실제 Domain별 판정은
         * 전용 LifeDomain Rule이 담당한다.
         */
        register(
                map,
                LifeDomainEvidenceSourceType
                        .ANNUAL_FUNCTIONAL_CONTEXT,
                allDomains()
        );

        register(
                map,
                LifeDomainEvidenceSourceType.MONTHLY_CONTEXT,
                allDomains()
        );

        /*
         * 신살은 모든 Domain에서 참조 가능.
         *
         * 그러나 SourceType 자체의 Tier가 AUXILIARY이므로
         * 핵심 근거가 될 수 없다.
         */
        register(
                map,
                LifeDomainEvidenceSourceType.SHINSAL,
                allDomains()
        );

        /*
         * 모든 SourceType 등록 검증.
         */
        if (
                map.size()
                        != LifeDomainEvidenceSourceType
                        .values()
                        .length
        ) {

            throw new IllegalStateException(
                    "모든 LifeDomainEvidenceSourceType이 Policy Registry에 등록되어야 합니다."
            );
        }

        this.policies =
                Map.copyOf(
                        map
                );
    }

    public LifeDomainEvidencePolicy policyOf(
            LifeDomainEvidenceSourceType sourceType
    ) {

        Objects.requireNonNull(
                sourceType,
                "LifeDomainEvidenceSourceType이 필요합니다."
        );

        LifeDomainEvidencePolicy policy =
                policies.get(
                        sourceType
                );

        if (
                policy == null
        ) {

            throw new IllegalStateException(
                    "등록되지 않은 생활영역 Evidence Source입니다: "
                            + sourceType
            );
        }

        return policy;
    }

    public boolean supports(

            LifeDomain domain,

            LifeDomainEvidenceSourceType sourceType

    ) {

        if (
                domain == null
                        || sourceType == null
        ) {

            return false;
        }

        return policyOf(
                sourceType
        )
                .supports(
                        domain
                );
    }

    public List<LifeDomainEvidencePolicy>
    policiesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return List.of();
        }

        return policies
                .values()
                .stream()
                .filter(
                        policy ->
                                policy.supports(
                                        domain
                                )
                )
                .sorted(
                        (
                                first,
                                second
                        ) ->
                                Integer.compare(
                                        first.tier().ordinal(),
                                        second.tier().ordinal()
                                )
                )
                .toList();
    }

    public Set<LifeDomainEvidenceSourceType>
    sourceTypesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return Set.of();
        }

        EnumSet<LifeDomainEvidenceSourceType> result =
                EnumSet.noneOf(
                        LifeDomainEvidenceSourceType.class
                );

        for (
                LifeDomainEvidencePolicy policy
                : policiesOf(
                domain
        )
        ) {

            result.add(
                    policy.sourceType()
            );
        }

        return Set.copyOf(
                result
        );
    }

    private static void register(

            Map<
                    LifeDomainEvidenceSourceType,
                    LifeDomainEvidencePolicy
                    > map,

            LifeDomainEvidenceSourceType sourceType,

            Set<LifeDomain> domains

    ) {

        LifeDomainEvidencePolicy previous =
                map.put(
                        sourceType,
                        new LifeDomainEvidencePolicy(
                                sourceType,
                                sourceType.tier(),
                                domains,
                                false
                        )
                );

        if (
                previous != null
        ) {

            throw new IllegalStateException(
                    "LifeDomainEvidenceSourceType이 중복 등록되었습니다: "
                            + sourceType
            );
        }
    }

    private static Set<LifeDomain> allDomains() {

        return Set.copyOf(
                EnumSet.allOf(
                        LifeDomain.class
                )
        );
    }
}