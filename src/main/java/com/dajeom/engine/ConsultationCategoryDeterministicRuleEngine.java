package com.dajeom.engine;

import com.dajeom.model.ConsultationCategoryDeterministicDependencyReference;
import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.ConsultationCategoryTimelineYearEvidence;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.rule.ConsultationCategoryDeterministicRule;
import com.dajeom.rule.ConsultationCategoryDeterministicSynthesisRule;
import com.dajeom.type.ConsultationCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * 카테고리별 deterministic Rule 실행 Engine.
 *
 * =================================================
 * 실행 단계
 * =================================================
 *
 * 1.
 * ConsultationCategoryDeterministicRule
 *
 * 2.
 * ConsultationCategoryDeterministicSynthesisRule
 *    phase 오름차순
 *
 * =================================================
 * 일반 Rule
 * =================================================
 *
 * ConsultationCategoryEvidenceView
 * ->
 * LifeDomainRuleMatch
 * ->
 * ConsultationCategoryDeterministicMatch
 *
 * =================================================
 * Synthesis Rule
 * =================================================
 *
 * 이전 검증 완료
 * ConsultationCategoryDeterministicMatch
 *
 * +
 *
 * 현재 Evidence View
 *
 * ->
 *
 * 상위 deterministic Match
 *
 * =================================================
 * Source Provenance
 * =================================================
 *
 * sourceMatches는 기존과 동일하게
 * 해당 결과의 실제 시간층에 존재하는
 * LifeDomainRuleMatch만 사용할 수 있다.
 *
 * =================================================
 * Dependency Provenance
 * =================================================
 *
 * dependencies는:
 *
 * 현재 Synthesis Phase보다 앞에서
 * 이미 검증 완료된
 * ConsultationCategoryDeterministicMatch만
 * 참조할 수 있다.
 *
 * =================================================
 *
 * 따라서 TURNING_POINT가:
 *
 * 2027 BIG_FLOW
 * 2028 BIG_FLOW
 * 2029 BIG_FLOW
 *
 * 를 사용했다고 임의로 주장하려면
 *
 * 실제 priorSnapshot에
 * 그 Match들이 존재해야 한다.
 *
 * =================================================
 * 같은 Phase
 * =================================================
 *
 * 같은 phase Rule은
 * phase 시작 직전 snapshot만 받는다.
 *
 * 따라서 실행 순서에 따라
 * 같은 Phase 결과를 몰래 참조할 수 없다.
 */
@Component
public class ConsultationCategoryDeterministicRuleEngine {

    private final List<ConsultationCategoryDeterministicRule>
            rules;

    private final List<ConsultationCategoryDeterministicSynthesisRule>
            synthesisRules;


    /*
     * =================================================
     * Backward-Compatible Constructor
     * =================================================
     */

    public ConsultationCategoryDeterministicRuleEngine(

            List<ConsultationCategoryDeterministicRule> rules

    ) {

        this(
                rules,
                List.of()
        );
    }


    /*
     * =================================================
     * Spring Constructor
     * =================================================
     */

    @Autowired
    public ConsultationCategoryDeterministicRuleEngine(

            List<ConsultationCategoryDeterministicRule> rules,

            List<ConsultationCategoryDeterministicSynthesisRule>
                    synthesisRules

    ) {

        this.rules =
                normalizeRules(
                        rules
                );

        this.synthesisRules =
                normalizeSynthesisRules(
                        synthesisRules
                );

        validateDuplicateRuleCodes(
                this.rules,
                this.synthesisRules
        );
    }


    /*
     * =================================================
     * Evaluate
     * =================================================
     */

    public List<ConsultationCategoryDeterministicMatch> evaluate(

            ConsultationCategoryEvidenceView view

    ) {

        Objects.requireNonNull(
                view,
                "Category Deterministic Rule 실행에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        evaluateBaseRules(
                view,
                result
        );

        evaluateSynthesisRules(
                view,
                result
        );

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Base Rules
     * =================================================
     */

    private void evaluateBaseRules(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch> result

    ) {

        for (
                ConsultationCategoryDeterministicRule rule
                : rules
        ) {

            if (
                    rule.category()
                            != view.category()
            ) {

                continue;
            }

            List<ConsultationCategoryDeterministicMatch> resolved =
                    rule.evaluate(
                            view
                    );

            if (
                    resolved == null
            ) {

                throw new IllegalStateException(
                        "ConsultationCategoryDeterministicRule.evaluate()는 null을 반환할 수 없습니다. category="
                                + rule.category()
                                + ", rule="
                                + rule.code()
                );
            }

            for (
                    ConsultationCategoryDeterministicMatch match
                    : resolved
            ) {

                ConsultationCategoryDeterministicMatch validated =
                        Objects.requireNonNull(
                                match,
                                "Category Deterministic Rule 결과는 null일 수 없습니다."
                        );

                validateMatch(
                        view,
                        rule.category(),
                        rule.code(),
                        validated
                );

                /*
                 * 일반 Rule은
                 * 다른 Deterministic Match에
                 * 의존할 수 없다.
                 */

                if (
                        validated.hasDependencies()
                ) {

                    throw new IllegalStateException(
                            "일반 ConsultationCategoryDeterministicRule은 dependency를 생성할 수 없습니다. category="
                                    + rule.category()
                                    + ", rule="
                                    + rule.code()
                    );
                }

                result.add(
                        validated
                );
            }
        }
    }


    /*
     * =================================================
     * Synthesis Rules
     * =================================================
     */

    private void evaluateSynthesisRules(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch> result

    ) {

        List<ConsultationCategoryDeterministicSynthesisRule>
                categoryRules =
                synthesisRulesOf(
                        view.category()
                );

        if (
                categoryRules.isEmpty()
        ) {

            return;
        }

        Map<
                Integer,
                List<ConsultationCategoryDeterministicSynthesisRule>
                > byPhase =
                new TreeMap<>();

        for (
                ConsultationCategoryDeterministicSynthesisRule rule
                : categoryRules
        ) {

            int phase =
                    rule.phase();

            if (
                    phase <= 0
            ) {

                throw new IllegalStateException(
                        "ConsultationCategoryDeterministicSynthesisRule.phase()는 1 이상이어야 합니다. category="
                                + rule.category()
                                + ", rule="
                                + rule.code()
                                + ", phase="
                                + phase
                );
            }

            byPhase
                    .computeIfAbsent(
                            phase,
                            ignored ->
                                    new ArrayList<>()
                    )
                    .add(
                            rule
                    );
        }

        for (
                Map.Entry<
                        Integer,
                        List<ConsultationCategoryDeterministicSynthesisRule>
                        > phaseEntry
                : byPhase.entrySet()
        ) {

            int phase =
                    phaseEntry.getKey();

            List<ConsultationCategoryDeterministicSynthesisRule>
                    phaseRules =
                    phaseEntry.getValue();

            List<ConsultationCategoryDeterministicMatch>
                    priorSnapshot =
                    List.copyOf(
                            result
                    );

            List<ConsultationCategoryDeterministicMatch>
                    phaseResults =
                    new ArrayList<>();

            for (
                    ConsultationCategoryDeterministicSynthesisRule rule
                    : phaseRules
            ) {

                List<ConsultationCategoryDeterministicMatch> resolved =
                        rule.evaluate(
                                view,
                                priorSnapshot
                        );

                if (
                        resolved == null
                ) {

                    throw new IllegalStateException(
                            "ConsultationCategoryDeterministicSynthesisRule.evaluate()는 null을 반환할 수 없습니다. category="
                                    + rule.category()
                                    + ", rule="
                                    + rule.code()
                                    + ", phase="
                                    + phase
                    );
                }

                for (
                        ConsultationCategoryDeterministicMatch match
                        : resolved
                ) {

                    ConsultationCategoryDeterministicMatch validated =
                            Objects.requireNonNull(
                                    match,
                                    "Category Deterministic Synthesis Rule 결과는 null일 수 없습니다."
                            );

                    validateMatch(
                            view,
                            rule.category(),
                            rule.code(),
                            validated
                    );

                    validateDependencies(
                            view,
                            rule,
                            validated,
                            priorSnapshot
                    );

                    phaseResults.add(
                            validated
                    );
                }
            }

            result.addAll(
                    phaseResults
            );
        }
    }


    /*
     * =================================================
     * Dependency 검증
     * =================================================
     */

    private void validateDependencies(

            ConsultationCategoryEvidenceView view,

            ConsultationCategoryDeterministicSynthesisRule rule,

            ConsultationCategoryDeterministicMatch match,

            List<ConsultationCategoryDeterministicMatch>
                    priorSnapshot

    ) {

        for (
                ConsultationCategoryDeterministicDependencyReference dependency
                : match.dependencies()
        ) {

            ConsultationCategoryDeterministicDependencyReference normalized =
                    Objects.requireNonNull(
                            dependency,
                            "Category Deterministic dependency는 null일 수 없습니다."
                    );

            /*
             * 현재 Category Synthesis는
             * 같은 Category 안에서만 수행된다.
             */

            if (
                    normalized.category()
                            != view.category()
            ) {

                throw new IllegalStateException(
                        "Synthesis Rule dependency Category가 현재 View와 일치하지 않습니다. rule="
                                + rule.code()
                                + ", viewCategory="
                                + view.category()
                                + ", dependencyCategory="
                                + normalized.category()
                );
            }

            boolean exists =
                    false;

            for (
                    ConsultationCategoryDeterministicMatch prior
                    : priorSnapshot
            ) {

                if (
                        normalized.matches(
                                prior
                        )
                ) {

                    exists =
                            true;

                    break;
                }
            }

            if (
                    !exists
            ) {

                throw new IllegalStateException(
                        "Synthesis Rule이 priorSnapshot에 존재하지 않는 deterministic dependency를 사용했습니다. category="
                                + rule.category()
                                + ", rule="
                                + rule.code()
                                + ", dependencyRule="
                                + normalized.ruleCode()
                                + ", dependencyYear="
                                + normalized.sajuYear()
                                + ", dependencyLayer="
                                + normalized.layer()
                );
            }
        }
    }


    /*
     * =================================================
     * Base Rule 조회
     * =================================================
     */

    public List<ConsultationCategoryDeterministicRule>
    rulesOf(

            ConsultationCategory category

    ) {

        if (
                category == null
        ) {

            return List.of();
        }

        return rules
                .stream()
                .filter(
                        rule ->
                                rule.category()
                                        == category
                )
                .toList();
    }


    public boolean hasRules(

            ConsultationCategory category

    ) {

        return !rulesOf(
                category
        )
                .isEmpty();
    }


    public int ruleCount() {

        return rules
                .size();
    }


    /*
     * =================================================
     * Synthesis 조회
     * =================================================
     */

    public List<ConsultationCategoryDeterministicSynthesisRule>
    synthesisRulesOf(

            ConsultationCategory category

    ) {

        if (
                category == null
        ) {

            return List.of();
        }

        return synthesisRules
                .stream()
                .filter(
                        rule ->
                                rule.category()
                                        == category
                )
                .toList();
    }


    public boolean hasSynthesisRules(

            ConsultationCategory category

    ) {

        return !synthesisRulesOf(
                category
        )
                .isEmpty();
    }


    public int synthesisRuleCount() {

        return synthesisRules
                .size();
    }


    public int totalRuleCount() {

        return rules.size()
                + synthesisRules.size();
    }


    /*
     * =================================================
     * Match 검증
     * =================================================
     */

    private void validateMatch(

            ConsultationCategoryEvidenceView view,

            ConsultationCategory expectedCategory,

            String expectedRuleCodeSource,

            ConsultationCategoryDeterministicMatch match

    ) {

        Objects.requireNonNull(
                expectedCategory,
                "Category Rule의 category는 null일 수 없습니다."
        );

        String expectedRuleCode =
                requireText(
                        expectedRuleCodeSource,
                        "Category Rule에는 code가 필요합니다."
                );

        if (
                match.category()
                        != expectedCategory
        ) {

            throw new IllegalStateException(
                    "Category Rule과 결과 Category가 일치하지 않습니다. rule="
                            + expectedRuleCode
                            + ", expected="
                            + expectedCategory
                            + ", actual="
                            + match.category()
            );
        }

        if (
                match.category()
                        != view.category()
        ) {

            throw new IllegalStateException(
                    "Category Evidence View와 Category Rule 결과가 일치하지 않습니다. view="
                            + view.category()
                            + ", match="
                            + match.category()
            );
        }

        if (
                !match.ruleCode()
                        .equals(
                                expectedRuleCode
                        )
        ) {

            throw new IllegalStateException(
                    "Category Rule과 결과 ruleCode가 일치하지 않습니다. expected="
                            + expectedRuleCode
                            + ", actual="
                            + match.ruleCode()
            );
        }

        for (
                LifeDomainRuleMatch sourceMatch
                : match.sourceMatches()
        ) {

            if (
                    !sourceExistsInExpectedLayer(
                            view,
                            match,
                            sourceMatch
                    )
            ) {

                throw new IllegalStateException(
                        "Category Rule이 현재 Evidence View의 해당 시간층에 존재하지 않는 "
                                + "LifeDomainRuleMatch를 사용했습니다. category="
                                + match.category()
                                + ", rule="
                                + match.ruleCode()
                                + ", sourceRule="
                                + sourceMatch.ruleCode()
                                + ", sourceDomain="
                                + sourceMatch.domain()
                );
            }
        }
    }


    /*
     * =================================================
     * Source Layer
     * =================================================
     */

    private boolean sourceExistsInExpectedLayer(

            ConsultationCategoryEvidenceView view,

            ConsultationCategoryDeterministicMatch match,

            LifeDomainRuleMatch sourceMatch

    ) {

        return switch (
                match.layer()
                ) {

            case NATAL ->
                    exists(
                            view.natalMatches(),
                            sourceMatch
                    );

            case CURRENT_TIMELINE ->
                    existsInCurrentTimeline(
                            view,
                            match,
                            sourceMatch
                    );

            case TIMELINE_YEAR ->
                    existsInTimelineYear(
                            view,
                            match,
                            sourceMatch
                    );
        };
    }


    /*
     * =================================================
     * Current Timeline
     * =================================================
     */

    private boolean existsInCurrentTimeline(

            ConsultationCategoryEvidenceView view,

            ConsultationCategoryDeterministicMatch match,

            LifeDomainRuleMatch sourceMatch

    ) {

        Integer expectedYear =
                match.sajuYear();

        Integer expectedMonthIndex =
                match.monthIndex();

        if (
                expectedYear == null
                        || expectedMonthIndex == null
        ) {

            return false;
        }

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            if (
                    segment.sajuYear()
                            != expectedYear.intValue()
            ) {

                continue;
            }

            if (
                    segment.monthIndex()
                            != expectedMonthIndex.intValue()
            ) {

                continue;
            }

            if (
                    exists(
                            segment.matches(),
                            sourceMatch
                    )
            ) {

                return true;
            }
        }

        return false;
    }


    /*
     * =================================================
     * Timeline Year
     * =================================================
     */

    private boolean existsInTimelineYear(

            ConsultationCategoryEvidenceView view,

            ConsultationCategoryDeterministicMatch match,

            LifeDomainRuleMatch sourceMatch

    ) {

        Integer expectedYear =
                match.sajuYear();

        if (
                expectedYear == null
        ) {

            return false;
        }

        for (
                ConsultationCategoryTimelineYearEvidence yearEvidence
                : view.timelineYearEvidences()
        ) {

            if (
                    yearEvidence.sajuYear()
                            != expectedYear.intValue()
            ) {

                continue;
            }

            if (
                    exists(
                            yearEvidence.matches(),
                            sourceMatch
                    )
            ) {

                return true;
            }
        }

        return false;
    }


    /*
     * =================================================
     * Match 존재
     * =================================================
     */

    private boolean exists(

            List<LifeDomainRuleMatch> candidates,

            LifeDomainRuleMatch expected

    ) {

        if (
                candidates == null
                        || candidates.isEmpty()
        ) {

            return false;
        }

        Objects.requireNonNull(
                expected,
                "검증할 LifeDomainRuleMatch는 null일 수 없습니다."
        );

        for (
                LifeDomainRuleMatch candidate
                : candidates
        ) {

            if (
                    candidate == null
            ) {

                continue;
            }

            if (
                    candidate == expected
                            || candidate.equals(
                            expected
                    )
            ) {

                return true;
            }
        }

        return false;
    }


    /*
     * =================================================
     * Base Normalize
     * =================================================
     */

    private List<ConsultationCategoryDeterministicRule>
    normalizeRules(

            List<ConsultationCategoryDeterministicRule> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        return source
                .stream()
                .map(
                        rule ->
                                Objects.requireNonNull(
                                        rule,
                                        "ConsultationCategoryDeterministicRule은 null일 수 없습니다."
                                )
                )
                .sorted(
                        Comparator
                                .<ConsultationCategoryDeterministicRule>
                                        comparingInt(
                                        rule ->
                                                Objects.requireNonNull(
                                                                rule.category(),
                                                                "Category Rule의 category는 null일 수 없습니다."
                                                        )
                                                        .getOrder()
                                )
                                .thenComparing(
                                        rule ->
                                                requireText(
                                                        rule.code(),
                                                        "Category Rule에는 code가 필요합니다."
                                                )
                                )
                )
                .toList();
    }


    /*
     * =================================================
     * Synthesis Normalize
     * =================================================
     */

    private List<ConsultationCategoryDeterministicSynthesisRule>
    normalizeSynthesisRules(

            List<ConsultationCategoryDeterministicSynthesisRule> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        return source
                .stream()
                .map(
                        rule ->
                                Objects.requireNonNull(
                                        rule,
                                        "ConsultationCategoryDeterministicSynthesisRule은 null일 수 없습니다."
                                )
                )
                .peek(
                        rule -> {

                            Objects.requireNonNull(
                                    rule.category(),
                                    "Category Synthesis Rule의 category는 null일 수 없습니다."
                            );

                            requireText(
                                    rule.code(),
                                    "Category Synthesis Rule에는 code가 필요합니다."
                            );

                            if (
                                    rule.phase()
                                            <= 0
                            ) {

                                throw new IllegalArgumentException(
                                        "Category Synthesis Rule의 phase는 1 이상이어야 합니다. category="
                                                + rule.category()
                                                + ", code="
                                                + rule.code()
                                                + ", phase="
                                                + rule.phase()
                                );
                            }
                        }
                )
                .sorted(
                        Comparator
                                .<ConsultationCategoryDeterministicSynthesisRule>
                                        comparingInt(
                                        rule ->
                                                rule.category()
                                                        .getOrder()
                                )
                                .thenComparingInt(
                                        ConsultationCategoryDeterministicSynthesisRule
                                                ::phase
                                )
                                .thenComparing(
                                        rule ->
                                                requireText(
                                                        rule.code(),
                                                        "Category Synthesis Rule에는 code가 필요합니다."
                                                )
                                )
                )
                .toList();
    }


    /*
     * =================================================
     * Rule Code 중복
     * =================================================
     */

    private void validateDuplicateRuleCodes(

            List<ConsultationCategoryDeterministicRule>
                    baseRules,

            List<ConsultationCategoryDeterministicSynthesisRule>
                    synthesisRules

    ) {

        Set<String> identities =
                new LinkedHashSet<>();

        for (
                ConsultationCategoryDeterministicRule rule
                : baseRules
        ) {

            String identity =
                    ruleIdentity(
                            rule.category(),
                            rule.code()
                    );

            if (
                    !identities.add(
                            identity
                    )
            ) {

                throw new IllegalArgumentException(
                        "같은 ConsultationCategory 안에서 Deterministic Rule Code가 중복되었습니다. category="
                                + rule.category()
                                + ", code="
                                + rule.code()
                );
            }
        }

        for (
                ConsultationCategoryDeterministicSynthesisRule rule
                : synthesisRules
        ) {

            String identity =
                    ruleIdentity(
                            rule.category(),
                            rule.code()
                    );

            if (
                    !identities.add(
                            identity
                    )
            ) {

                throw new IllegalArgumentException(
                        "일반/Synthesis Rule 전체에서 같은 ConsultationCategory의 Rule Code가 중복되었습니다. category="
                                + rule.category()
                                + ", code="
                                + rule.code()
                );
            }
        }
    }


    /*
     * =================================================
     * Identity
     * =================================================
     */

    private String ruleIdentity(

            ConsultationCategory category,

            String code

    ) {

        Objects.requireNonNull(
                category,
                "Rule Identity에는 ConsultationCategory가 필요합니다."
        );

        return category.name()
                + "\u001F"
                + requireText(
                code,
                "Rule Identity에는 code가 필요합니다."
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