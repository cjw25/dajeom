package com.dajeom.model;

import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 18차-1.
 *
 * 하나의 생활영역 Rule이
 * 기존 Evidence 조합에서 찾아낸 구조.
 *
 * =================================================
 *
 * 예:
 *
 * 재성 발달
 * +
 * 신약
 *
 * ->
 * 재물 기회가 존재하더라도
 * 감당 부담 Context가 함께 존재.
 *
 * =================================================
 *
 * 이는:
 *
 * 돈을 잃는다
 * 돈을 번다
 *
 * 를 단정하는 것이 아니다.
 */
public record LifeDomainRuleMatch(

        LifeDomain domain,

        String ruleCode,

        LifeDomainSignalStatus status,

        String summary,

        List<LifeDomainEvidenceReference<?>> evidences,

        List<String> reasons

) {

    public LifeDomainRuleMatch {

        Objects.requireNonNull(
                domain,
                "LifeDomainRuleMatch에는 Domain이 필요합니다."
        );

        if (
                ruleCode == null
                        || ruleCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainRuleMatch에는 ruleCode가 필요합니다."
            );
        }

        ruleCode =
                ruleCode.trim();

        Objects.requireNonNull(
                status,
                "LifeDomainRuleMatch에는 SignalStatus가 필요합니다."
        );

        if (
                summary == null
                        || summary.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainRuleMatch에는 summary가 필요합니다."
            );
        }

        summary =
                summary.trim();

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                evidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainRuleMatch에는 최소 하나의 Evidence가 필요합니다."
            );
        }

        for (
                LifeDomainEvidenceReference<?> evidence
                : evidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "LifeDomainRuleMatch Evidence는 null일 수 없습니다."
            );
        }

        reasons =
                reasons == null
                        ? List.of()
                        : reasons
                        .stream()
                        .filter(
                                Objects::nonNull
                        )
                        .map(
                                String::trim
                        )
                        .filter(
                                reason ->
                                        !reason.isBlank()
                        )
                        .toList();

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainRuleMatch에는 최소 하나의 reason이 필요합니다."
            );
        }
    }

    /**
     * 신살만으로 생성된 Match인가.
     *
     * 18차에서는 이런 Match를 허용하지 않는다.
     * RuleEngine에서 최종 검증한다.
     */
    public boolean auxiliaryOnly() {

        return evidences
                .stream()
                .allMatch(
                        LifeDomainEvidenceReference::auxiliary
                );
    }
}