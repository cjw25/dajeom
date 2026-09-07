package com.dajeom.rule;

import com.dajeom.domain.InterpretationLayer;
import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.MyeongriFact;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 명리 Interpretation Rule 실행기.
 *
 * 수학적 점수나 가중치로 판단하지 않는다.
 *
 * 명리 해석 계층 순서에 따라
 * Java에 정의된 명리 규칙을 적용하고,
 * 성립한 LifeClaim을 보존한다.
 */
@Component
public class MyeongriRuleEngine {

    private final List<InterpretationRule> rules;

    public MyeongriRuleEngine(
            List<InterpretationRule> rules
    ) {

        Objects.requireNonNull(
                rules,
                "InterpretationRule 목록은 필수입니다."
        );

        this.rules =
                rules
                        .stream()
                        .sorted(
                                Comparator
                                        .<InterpretationRule>comparingInt(
                                                rule ->
                                                        rule
                                                                .layer()
                                                                .ordinal()
                                        )
                                        .thenComparing(
                                                InterpretationRule::ruleCode
                                        )
                        )
                        .toList();
    }

    public List<LifeClaim> interpret(
            List<MyeongriFact> facts
    ) {

        Objects.requireNonNull(
                facts,
                "명리 Fact 목록은 필수입니다."
        );

        List<MyeongriFact> safeFacts =
                List.copyOf(
                        facts
                );

        Map<String, LifeClaim> claims =
                new LinkedHashMap<>();

        for (
                InterpretationRule rule
                : rules
        ) {

            rule
                    .apply(
                            safeFacts
                    )
                    .ifPresent(
                            claim -> {

                                String key =
                                        claim
                                                .claimCode()
                                                + "|"
                                                + claim
                                                .periodKey();

                                claims.putIfAbsent(
                                        key,
                                        claim
                                );
                            }
                    );
        }

        return List.copyOf(
                new ArrayList<>(
                        claims.values()
                )
        );
    }

    public List<InterpretationRule> rulesOf(
            InterpretationLayer layer
    ) {

        Objects.requireNonNull(
                layer,
                "InterpretationLayer는 필수입니다."
        );

        return rules
                .stream()
                .filter(
                        rule ->
                                rule.layer()
                                        == layer
                )
                .toList();
    }
}