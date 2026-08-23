package com.dajeom.model;

import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 18차-1.
 *
 * 하나의 LifeDomainRule이
 * Java에서 명시적으로 계산하여 확정한
 * 생활영역 deterministic Match.
 *
 * =================================================
 *
 * 이 객체 자체는 Evidence를 다시 계산하거나
 * 서로 다른 Evidence 사이의 새로운 의미를 만들지 않는다.
 *
 * 다음과 같은 의미는
 * 해당 LifeDomainRule이 Java 조건으로
 * 실제 계산한 경우에만 Match에 포함될 수 있다.
 *
 * - 관계
 * - 인과
 * - 순서
 * - 강화
 * - 약화
 * - 상쇄
 * - Evidence 간 종합 의미
 *
 * 단순히 여러 Evidence가 함께 존재한다는 이유만으로
 * 새로운 관계나 결론을 생성해서는 안 된다.
 *
 * =================================================
 *
 * evidences
 *
 * 해당 Rule이 deterministic 판단에
 * 실제 사용한 Evidence Reference.
 *
 * 여러 Evidence가 포함될 수 있지만,
 * Evidence 개수 자체가 점수나
 * 독립 확증 횟수를 의미하지 않는다.
 *
 * =================================================
 *
 * status / summary / reasons
 *
 * 모두 LifeDomainRule이
 * Java에서 확정한 결과를 보존한다.
 *
 * 이 모델에서 다시 재해석하거나
 * 별도의 대표 상태를 계산하지 않는다.
 *
 * =================================================
 *
 * 또한 이 Match는:
 *
 * 돈을 번다
 * 돈을 잃는다
 * 취업한다
 * 결혼한다
 * 합격한다
 * 병이 생긴다
 *
 * 같은 실제 사건을 임의로 확정하는 객체가 아니다.
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
     * AUXILIARY Evidence만으로 생성된 Match인가.
     *
     * LifeDomainRuleEngine에서
     * 이런 Match의 반환을 최종 차단한다.
     */
    public boolean auxiliaryOnly() {

        return evidences
                .stream()
                .allMatch(
                        LifeDomainEvidenceReference::auxiliary
                );
    }
}