package com.dajeom.analyzer;

import com.dajeom.constant.LifeDomainEvidencePolicyRegistry;
import com.dajeom.model.LifeDomainRuleCoverageAnalysis;
import com.dajeom.rule.LifeDomainRule;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * LifeDomain Rule의 Evidence Source Coverage 분석기.
 *
 * =================================================
 *
 * 비교 대상:
 *
 * 1.
 * LifeDomainEvidencePolicyRegistry
 *
 * Domain별로 허용된 Evidence Source.
 *
 * =================================================
 *
 * 2.
 * 등록된 LifeDomainRule.supportedSources()
 *
 * 실제 Rule들이 직접 소비한다고 선언한 Source.
 *
 * =================================================
 *
 * 결과:
 *
 * allowed
 * declared
 * unconsumedAllowed
 * ruleCodes
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * unconsumedAllowed Source가 있다고 해서
 * 애플리케이션 시작을 실패시키지 않는다.
 *
 * 이 Analyzer는 Validator가 아니다.
 *
 * =================================================
 *
 * 이유:
 *
 * upstream Source가 다른 복합분석 안에서
 * 이미 교차검증에 사용된 경우가 있기 때문이다.
 *
 * 예:
 *
 * 10차 TenGod 교차검증 내부에서:
 *
 * STRUCTURE
 * DAY_MASTER_STRENGTH
 * USEFUL_GOD
 * TRANSFORMATION
 *
 * 등이 함께 사용될 수 있다.
 *
 * 따라서 18차에서 각각을
 * 반드시 별도 Rule로 다시 해석하면
 * 오히려 중복 Evidence가 될 수 있다.
 *
 * =================================================
 *
 * 이 결과를 보고:
 *
 * - 정말 독립 생활 의미가 필요한가
 * - 기존 Rule에 이미 포함됐는가
 * - 구조적 Context만 추가해야 하는가
 * - 아예 직접 Rule을 만들지 않아야 하는가
 *
 * 를 결정한다.
 */
@Component
public class LifeDomainRuleCoverageAnalyzer {

    private final List<LifeDomainRule> rules;

    private final LifeDomainEvidencePolicyRegistry
            policyRegistry;

    public LifeDomainRuleCoverageAnalyzer(

            List<LifeDomainRule> rules,

            LifeDomainEvidencePolicyRegistry policyRegistry

    ) {

        this.policyRegistry =
                Objects.requireNonNull(
                        policyRegistry,
                        "LifeDomainEvidencePolicyRegistry가 필요합니다."
                );

        List<LifeDomainRule> normalized =
                rules == null
                        ? List.of()
                        : rules
                        .stream()
                        .peek(
                                rule ->
                                        Objects.requireNonNull(
                                                rule,
                                                "LifeDomainRule은 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator.comparing(
                                        LifeDomainRule::code
                                )
                        )
                        .toList();

        /*
         * =================================================
         * 최소 Metadata 정합성
         * =================================================
         *
         * 실제 실행 강제 검증은
         * LifeDomainRuleEngine 책임이다.
         *
         * Coverage Analyzer에서도
         * 의미 없는 선언은 받아들이지 않는다.
         */

        for (
                LifeDomainRule rule
                : normalized
        ) {

            Objects.requireNonNull(
                    rule.domain(),
                    "LifeDomainRule.domain()은 null일 수 없습니다."
            );

            if (
                    rule.code() == null
                            || rule.code().isBlank()
            ) {

                throw new IllegalArgumentException(
                        "LifeDomainRule.code()는 비어 있을 수 없습니다."
                );
            }

            Set<LifeDomainEvidenceSourceType> supportedSources =
                    rule.supportedSources();

            if (
                    supportedSources == null
                            || supportedSources.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "LifeDomainRule에는 최소 하나의 supportedSource가 필요합니다: "
                                + rule.code()
                );
            }

            for (
                    LifeDomainEvidenceSourceType sourceType
                    : supportedSources
            ) {

                Objects.requireNonNull(
                        sourceType,
                        "LifeDomainRule.supportedSources()에는 null이 들어갈 수 없습니다."
                );

                if (
                        !policyRegistry.supports(
                                rule.domain(),
                                sourceType
                        )
                ) {

                    throw new IllegalArgumentException(
                            "Policy에서 허용되지 않은 Source를 선언한 LifeDomainRule입니다. "
                                    + "rule="
                                    + rule.code()
                                    + ", domain="
                                    + rule.domain()
                                    + ", source="
                                    + sourceType
                    );
                }
            }
        }

        this.rules =
                List.copyOf(
                        normalized
                );
    }

    /*
     * =================================================
     * 공식 Coverage 분석
     * =================================================
     */

    public LifeDomainRuleCoverageAnalysis analyze() {

        EnumMap<
                LifeDomain,
                Set<LifeDomainEvidenceSourceType>
                > allowedSourcesByDomain =
                new EnumMap<>(
                        LifeDomain.class
                );

        EnumMap<
                LifeDomain,
                Set<LifeDomainEvidenceSourceType>
                > declaredSourcesByDomain =
                new EnumMap<>(
                        LifeDomain.class
                );

        EnumMap<
                LifeDomain,
                Set<LifeDomainEvidenceSourceType>
                > unconsumedAllowedSourcesByDomain =
                new EnumMap<>(
                        LifeDomain.class
                );

        EnumMap<
                LifeDomain,
                List<String>
                > ruleCodesByDomain =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            /*
             * =================================================
             * 1.
             * Policy 허용 Source
             * =================================================
             */

            EnumSet<LifeDomainEvidenceSourceType> allowed =
                    EnumSet.noneOf(
                            LifeDomainEvidenceSourceType.class
                    );

            for (
                    LifeDomainEvidenceSourceType sourceType
                    : LifeDomainEvidenceSourceType.values()
            ) {

                if (
                        policyRegistry.supports(
                                domain,
                                sourceType
                        )
                ) {

                    allowed.add(
                            sourceType
                    );
                }
            }

            /*
             * =================================================
             * 2.
             * 등록 Rule
             * =================================================
             */

            List<LifeDomainRule> domainRules =
                    rules
                            .stream()
                            .filter(
                                    rule ->
                                            rule.domain()
                                                    == domain
                            )
                            .toList();

            EnumSet<LifeDomainEvidenceSourceType> declared =
                    EnumSet.noneOf(
                            LifeDomainEvidenceSourceType.class
                    );

            List<String> ruleCodes =
                    new ArrayList<>();

            for (
                    LifeDomainRule rule
                    : domainRules
            ) {

                declared.addAll(
                        rule.supportedSources()
                );

                ruleCodes.add(
                        rule.code()
                                .trim()
                );
            }

            ruleCodes.sort(
                    String::compareTo
            );

            /*
             * =================================================
             * 3.
             * Policy 허용 - 실제 Rule 선언
             * =================================================
             */

            EnumSet<LifeDomainEvidenceSourceType> unconsumed =
                    EnumSet.noneOf(
                            LifeDomainEvidenceSourceType.class
                    );

            unconsumed.addAll(
                    allowed
            );

            unconsumed.removeAll(
                    declared
            );

            /*
             * =================================================
             * 결과
             * =================================================
             */

            allowedSourcesByDomain.put(
                    domain,
                    allowed
            );

            declaredSourcesByDomain.put(
                    domain,
                    declared
            );

            unconsumedAllowedSourcesByDomain.put(
                    domain,
                    unconsumed
            );

            ruleCodesByDomain.put(
                    domain,
                    List.copyOf(
                            ruleCodes
                    )
            );
        }

        return new LifeDomainRuleCoverageAnalysis(
                allowedSourcesByDomain,
                declaredSourcesByDomain,
                unconsumedAllowedSourcesByDomain,
                ruleCodesByDomain
        );
    }
}