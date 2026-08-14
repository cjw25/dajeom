package com.dajeom.engine;

import com.dajeom.model.ShinsalRuleContext;
import com.dajeom.model.ShinsalRuleEvaluation;
import com.dajeom.model.ShinsalRuleMatch;
import com.dajeom.rule.ShinsalRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 17차-1.
 *
 * 등록된 모든 ShinsalRule을
 * deterministic하게 실행한다.
 *
 * =================================================
 *
 * Spring:
 *
 * List<ShinsalRule>
 *
 * 형태로 모든 @Component Rule을
 * 자동 주입받는다.
 *
 * =================================================
 *
 * 이 Engine은:
 *
 * 신살 계산 순서에 따라 결과가 달라지지 않도록
 * Rule Code 기준으로 정렬해서 실행한다.
 *
 * =================================================
 *
 * Rule끼리 상태를 공유하지 않는다.
 */
@Component
public class ShinsalRuleEngine {

    private final List<ShinsalRule> rules;

    public ShinsalRuleEngine(
            List<ShinsalRule> rules
    ) {

        List<ShinsalRule> normalized =
                rules == null
                        ? List.of()
                        : rules
                        .stream()
                        .peek(
                                rule ->
                                        Objects.requireNonNull(
                                                rule,
                                                "ShinsalRule은 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator.comparing(
                                        ShinsalRule::code
                                )
                        )
                        .toList();

        /*
         * =================================================
         * Rule Code 중복 금지
         * =================================================
         */

        for (
                int first = 0;
                first < normalized.size();
                first++
        ) {

            ShinsalRule firstRule =
                    normalized.get(first);

            validateRuleMetadata(
                    firstRule
            );

            for (
                    int second = first + 1;
                    second < normalized.size();
                    second++
            ) {

                ShinsalRule secondRule =
                        normalized.get(second);

                if (
                        firstRule
                                .code()
                                .equals(
                                        secondRule.code()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "동일 ShinsalRule Code가 중복 등록되었습니다: "
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
     * 공식 17-1 API
     * =================================================
     */
    public ShinsalRuleEvaluation evaluate(
            ShinsalRuleContext context
    ) {

        Objects.requireNonNull(
                context,
                "신살 Rule 평가에는 ShinsalRuleContext가 필요합니다."
        );

        List<ShinsalRuleMatch> matches =
                new ArrayList<>();

        for (
                ShinsalRule rule
                : rules
        ) {

            List<ShinsalRuleMatch> resolved =
                    rule.evaluate(
                            context
                    );

            if (
                    resolved == null
            ) {

                throw new IllegalStateException(
                        "ShinsalRule.evaluate()는 null을 반환할 수 없습니다: "
                                + rule.code()
                );
            }

            for (
                    ShinsalRuleMatch match
                    : resolved
            ) {

                validateMatch(
                        rule,
                        Objects.requireNonNull(
                                match,
                                "ShinsalRuleMatch는 null일 수 없습니다."
                        )
                );

                matches.add(
                        match
                );
            }
        }

        matches.sort(
                Comparator
                        .comparing(
                                (
                                        ShinsalRuleMatch match
                                ) ->
                                        match.type().ordinal()
                        )
                        .thenComparing(
                                ShinsalRuleMatch::ruleCode
                        )
                        .thenComparing(
                                match ->
                                        match
                                                .matchedPoints()
                                                .get(0)
                                                .locationKey()
                        )
        );

        return new ShinsalRuleEvaluation(
                context,
                matches
        );
    }

    public List<ShinsalRule> rules() {

        return rules;
    }

    public boolean hasRegisteredRules() {

        return !rules
                .isEmpty();
    }

    /*
     * =================================================
     * Rule Metadata
     * =================================================
     */

    private static void validateRuleMetadata(
            ShinsalRule rule
    ) {

        Objects.requireNonNull(
                rule.type(),
                "ShinsalRule.type()은 null일 수 없습니다."
        );

        if (
                rule.code() == null
                        || rule.code().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalRule Code가 필요합니다."
            );
        }

        if (
                rule.bases() == null
                        || rule.bases().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalRule에는 최소 하나의 RuleBasis가 필요합니다: "
                            + rule.code()
            );
        }

        if (
                rule.conventionId() == null
                        || rule.conventionId().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalRule에는 conventionId가 필요합니다: "
                            + rule.code()
            );
        }
    }

    /*
     * =================================================
     * Rule ↔ Match 정합성
     * =================================================
     */

    private static void validateMatch(

            ShinsalRule rule,

            ShinsalRuleMatch match

    ) {

        if (
                match.type()
                        != rule.type()
        ) {

            throw new IllegalStateException(
                    "ShinsalRule과 Match의 ShinsalType이 일치하지 않습니다: "
                            + rule.code()
            );
        }

        if (
                !match.ruleCode()
                        .equals(
                                rule.code()
                        )
        ) {

            throw new IllegalStateException(
                    "ShinsalRule과 Match의 ruleCode가 일치하지 않습니다: "
                            + rule.code()
            );
        }

        if (
                !match.conventionId()
                        .equals(
                                rule.conventionId()
                        )
        ) {

            throw new IllegalStateException(
                    "ShinsalRule과 Match의 conventionId가 일치하지 않습니다: "
                            + rule.code()
            );
        }

        if (
                !rule
                        .bases()
                        .contains(
                                match.basis()
                        )
        ) {

            throw new IllegalStateException(
                    "ShinsalRule이 선언하지 않은 RuleBasis를 Match가 사용했습니다: "
                            + rule.code()
                            + " / "
                            + match.basis()
            );
        }
    }
}