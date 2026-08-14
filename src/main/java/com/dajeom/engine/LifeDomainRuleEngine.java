package com.dajeom.engine;

import com.dajeom.constant.LifeDomainEvidencePolicyRegistry;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.rule.LifeDomainRule;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차-1.
 *
 * 등록된 생활영역 Rule들을 deterministic하게 실행한다.
 *
 * =================================================
 *
 * 중요한 강제 정책:
 *
 * 1.
 * Rule은 자신의 Domain Bundle만 평가.
 *
 * 2.
 * Rule이 선언한 Source만 사용.
 *
 * 3.
 * 해당 Domain Policy에서 허용된 Source만 사용.
 *
 * 4.
 * 신살 AUXILIARY Evidence만으로
 * LifeDomainRuleMatch를 생성할 수 없음.
 *
 * =================================================
 *
 * 따라서:
 *
 * 도화 하나
 * -> 관계 좋음
 *
 * 역마 하나
 * -> 이직
 *
 * 같은 Rule은 Engine 수준에서 차단된다.
 */
@Component
public class LifeDomainRuleEngine {

    private final List<LifeDomainRule> rules;

    private final LifeDomainEvidencePolicyRegistry
            policyRegistry;

    public LifeDomainRuleEngine(

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
         * Rule Metadata + Code 중복 검증.
         */
        for (
                int first = 0;
                first < normalized.size();
                first++
        ) {

            LifeDomainRule firstRule =
                    normalized.get(first);

            validateRuleMetadata(
                    firstRule
            );

            for (
                    int second = first + 1;
                    second < normalized.size();
                    second++
            ) {

                if (
                        firstRule.code()
                                .equals(
                                        normalized
                                                .get(second)
                                                .code()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "동일 LifeDomainRule Code가 중복 등록되었습니다: "
                                    + firstRule.code()
                    );
                }
            }
        }

        this.rules =
                List.copyOf(
                        normalized
                );
    }

    /**
     * =================================================
     * 공식 18-1 Rule 실행 API
     * =================================================
     */
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "LifeDomainRuleEngine에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        bundle.validatePolicy(
                policyRegistry
        );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        for (
                LifeDomainRule rule
                : rules
        ) {

            if (
                    rule.domain()
                            != bundle.domain()
            ) {

                continue;
            }

            List<LifeDomainRuleMatch> resolved =
                    rule.evaluate(
                            bundle
                    );

            if (
                    resolved == null
            ) {

                throw new IllegalStateException(
                        "LifeDomainRule.evaluate()는 null을 반환할 수 없습니다: "
                                + rule.code()
                );
            }

            for (
                    LifeDomainRuleMatch match
                    : resolved
            ) {

                validateMatch(
                        rule,
                        bundle,
                        Objects.requireNonNull(
                                match,
                                "LifeDomainRuleMatch는 null일 수 없습니다."
                        )
                );

                matches.add(
                        match
                );
            }
        }

        return List.copyOf(
                matches
        );
    }

    public List<LifeDomainRule>
    rulesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return List.of();
        }

        return rules
                .stream()
                .filter(
                        rule ->
                                rule.domain()
                                        == domain
                )
                .toList();
    }

    public boolean hasRules(
            LifeDomain domain
    ) {

        return !rulesOf(
                domain
        )
                .isEmpty();
    }

    /*
     * =================================================
     * Rule Metadata
     * =================================================
     */

    private void validateRuleMetadata(
            LifeDomainRule rule
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
                    "LifeDomainRule Code가 필요합니다."
            );
        }

        Set<LifeDomainEvidenceSourceType> sources =
                rule.supportedSources();

        if (
                sources == null
                        || sources.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainRule에는 최소 하나의 supportedSource가 필요합니다: "
                            + rule.code()
            );
        }

        /*
         * Rule 선언 Source 자체가
         * Domain Policy와 맞는지 시작 시 검증.
         */
        for (
                LifeDomainEvidenceSourceType sourceType
                : sources
        ) {

            Objects.requireNonNull(
                    sourceType,
                    "supportedSource는 null일 수 없습니다."
            );

            if (
                    !policyRegistry.supports(
                            rule.domain(),
                            sourceType
                    )
            ) {

                throw new IllegalArgumentException(
                        "Rule이 해당 생활영역에서 허용되지 않은 Source를 선언했습니다. "
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

    /*
     * =================================================
     * Match 검증
     * =================================================
     */

    private void validateMatch(

            LifeDomainRule rule,

            LifeDomainEvidenceBundle bundle,

            LifeDomainRuleMatch match

    ) {

        if (
                match.domain()
                        != rule.domain()
        ) {

            throw new IllegalStateException(
                    "LifeDomainRule과 Match의 Domain이 일치하지 않습니다: "
                            + rule.code()
            );
        }

        if (
                match.domain()
                        != bundle.domain()
        ) {

            throw new IllegalStateException(
                    "LifeDomainRuleMatch가 현재 EvidenceBundle과 다른 Domain입니다."
            );
        }

        if (
                !match.ruleCode()
                        .equals(
                                rule.code()
                        )
        ) {

            throw new IllegalStateException(
                    "LifeDomainRule과 Match의 ruleCode가 일치하지 않습니다: "
                            + rule.code()
            );
        }

        /*
         * =================================================
         * Match Evidence는 Bundle에 실제 존재해야 한다.
         * Object Identity 기준.
         * =================================================
         */

        for (
                LifeDomainEvidenceReference<?> evidence
                : match.evidences()
        ) {

            boolean exists =
                    bundle
                            .evidences()
                            .stream()
                            .anyMatch(
                                    source ->
                                            source == evidence
                            );

            if (
                    !exists
            ) {

                throw new IllegalStateException(
                        "LifeDomainRule이 EvidenceBundle에 존재하지 않는 Evidence를 반환했습니다: "
                                + rule.code()
                                + " / "
                                + evidence.sourceKey()
                );
            }

            if (
                    !rule
                            .supportedSources()
                            .contains(
                                    evidence.sourceType()
                            )
            ) {

                throw new IllegalStateException(
                        "LifeDomainRule이 선언하지 않은 Source를 Match에 사용했습니다: "
                                + rule.code()
                                + " / "
                                + evidence.sourceType()
                );
            }
        }

        /*
         * =================================================
         * 핵심 정책:
         *
         * 신살만으로 생활영역 Match 생성 금지.
         * =================================================
         */

        if (
                match.auxiliaryOnly()
        ) {

            throw new IllegalStateException(
                    "신살 등 AUXILIARY Evidence만으로 생활영역 결론을 생성할 수 없습니다: "
                            + rule.code()
            );
        }
    }
}