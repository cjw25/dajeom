package com.dajeom.model;

import com.dajeom.type.DecadeContextTenGodMatchLevel;
import com.dajeom.type.DecadeLuckContextStatus;
import com.dajeom.type.NatalCrossFunctionalPatternType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 대운 CrossFunctional Effect와
 * 13-6 Context Signal을 교차 연결한
 * 최종 Synthesis.
 *
 * =================================================
 *
 * 입력:
 *
 * DecadeCrossFunctionalEffect
 *
 * +
 *
 * 관련 DecadeLuckContextSignal
 *
 * =================================================
 *
 * 이 객체는 다음을 함께 보존한다.
 *
 * 1.
 * 원국 Pattern 수준
 *
 * 2.
 * 대운 Participant Contact
 *
 * 3.
 * 원기능 감소 / Transformation
 *
 * 4.
 * Natal Capacity Qualification
 *
 * 5.
 * 13-6 Context의 십성 구조 Match
 *
 * 6.
 * Context Status
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * EXACT TenGod Match가 있다고 해서
 * 원국 Pattern을 승격하지 않는다.
 *
 * SUPPORTED Context가 있다고 해서
 * Function을 강화하지 않는다.
 *
 * CONFLICTED Context가 있다고 해서
 * Function을 삭제하지 않는다.
 *
 * Context는 최종 해석의
 * 지지/제한/충돌 조건으로만 보존한다.
 */
public record DecadeCrossFunctionalSynthesis(

        DecadeCrossFunctionalEffect sourceEffect,

        List<DecadeCrossContextMatch> contextMatches,

        boolean completeChart

) {

    public DecadeCrossFunctionalSynthesis {

        Objects.requireNonNull(
                sourceEffect,
                "CrossFunctional Synthesis에는 Source Effect가 필요합니다."
        );

        contextMatches =
                contextMatches == null
                        ? List.of()
                        : List.copyOf(
                        contextMatches
                );

        if (
                sourceEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctional Synthesis와 Source Effect의 completeChart가 일치하지 않습니다."
            );
        }

        Set<DecadeLuckContextSignal> seenSignals =
                new HashSet<>();

        for (
                DecadeCrossContextMatch match
                : contextMatches
        ) {

            Objects.requireNonNull(
                    match,
                    "DecadeCrossContextMatch는 null일 수 없습니다."
            );

            if (
                    match.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Cross Context Match의 completeChart가 일치하지 않습니다."
                );
            }

            if (
                    match.sourceEffect()
                            != sourceEffect
            ) {

                throw new IllegalArgumentException(
                        "Cross Context Match가 현재 Source Effect를 참조하지 않습니다."
                );
            }

            if (
                    !seenSignals.add(
                            match.contextSignal()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Context Signal이 CrossFunctional Synthesis에 중복되었습니다: "
                                + match.contextSignal().code()
                );
            }
        }
    }

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    public NatalCrossFunctionalPatternType patternType() {

        return sourceEffect
                .patternType();
    }

    /*
     * =================================================
     * Contact
     * =================================================
     */

    public boolean temporalContactExists() {

        return sourceEffect
                .temporalContactExists();
    }

    public boolean partialPatternContact() {

        return sourceEffect
                .partialPatternContact();
    }

    public boolean fullPatternContact() {

        return sourceEffect
                .fullPatternContact();
    }

    public boolean effectiveFullPatternContact() {

        return sourceEffect
                .effectiveFullPatternContact();
    }

    public boolean referencePatternContact() {

        return sourceEffect
                .referencePatternContact();
    }

    /*
     * =================================================
     * Context Match
     * =================================================
     */

    public List<DecadeCrossContextMatch>
    exactTenGodMatches() {

        return contextMatches
                .stream()
                .filter(
                        DecadeCrossContextMatch
                                ::exactTenGodMatch
                )
                .toList();
    }

    public List<DecadeCrossContextMatch>
    overlappingTenGodMatches() {

        return contextMatches
                .stream()
                .filter(
                        DecadeCrossContextMatch
                                ::overlappingTenGodMatch
                )
                .toList();
    }

    public boolean exactTenGodContextMatchExists() {

        return !exactTenGodMatches()
                .isEmpty();
    }

    public boolean overlappingTenGodContextMatchExists() {

        return !overlappingTenGodMatches()
                .isEmpty();
    }

    /**
     * 원국에서 structurallyEffective한 Pattern이
     * 이번 대운에서 full participant contact를 갖고,
     *
     * 동시에 13-6 Context에서도
     * 같은 involvedTenGodTypes Set이
     * 정확히 확인된 경우.
     *
     * =================================================
     *
     * 이것은 "독립 Evidence 2개"를 뜻하지 않는다.
     *
     * 동일 구조가 서로 다른 분석 Layer에서
     * 구조적으로 다시 나타난다는 뜻만 가진다.
     */
    public boolean effectiveFullContactWithExactContext() {

        return effectiveFullPatternContact()
                && exactTenGodContextMatchExists();
    }

    /*
     * =================================================
     * Context Status
     * =================================================
     */

    public List<DecadeCrossContextMatch>
    matchesOfStatus(

            DecadeLuckContextStatus status

    ) {

        if (
                status == null
        ) {

            return List.of();
        }

        return contextMatches
                .stream()
                .filter(
                        match ->
                                match.status()
                                        == status
                )
                .toList();
    }

    public boolean supportedContextExists() {

        return contextMatches
                .stream()
                .anyMatch(
                        DecadeCrossContextMatch
                                ::supported
                );
    }

    public boolean qualifiedContextExists() {

        return contextMatches
                .stream()
                .anyMatch(
                        DecadeCrossContextMatch
                                ::qualified
                );
    }

    public boolean weakenedContextExists() {

        return contextMatches
                .stream()
                .anyMatch(
                        DecadeCrossContextMatch
                                ::weakened
                );
    }

    public boolean conflictedContextExists() {

        return contextMatches
                .stream()
                .anyMatch(
                        DecadeCrossContextMatch
                                ::conflicted
                );
    }

    public boolean mixedContextStatus() {

        Set<DecadeLuckContextStatus> statuses =
                new HashSet<>();

        for (
                DecadeCrossContextMatch match
                : contextMatches
        ) {

            statuses.add(
                    match.status()
            );
        }

        return statuses.size()
                >= 2;
    }

    /*
     * =================================================
     * Exact Context Status
     * =================================================
     */

    public boolean exactSupportedContextExists() {

        return exactTenGodMatches()
                .stream()
                .anyMatch(
                        DecadeCrossContextMatch
                                ::supported
                );
    }

    public boolean exactQualifiedContextExists() {

        return exactTenGodMatches()
                .stream()
                .anyMatch(
                        DecadeCrossContextMatch
                                ::qualified
                );
    }

    public boolean exactWeakenedContextExists() {

        return exactTenGodMatches()
                .stream()
                .anyMatch(
                        DecadeCrossContextMatch
                                ::weakened
                );
    }

    public boolean exactConflictedContextExists() {

        return exactTenGodMatches()
                .stream()
                .anyMatch(
                        DecadeCrossContextMatch
                                ::conflicted
                );
    }

    /*
     * =================================================
     * Source Function 상태
     * =================================================
     */

    public boolean transformedTargetParticipantContact() {

        return sourceEffect
                .transformedTargetParticipantContact();
    }

    public boolean originalFunctionReductionExists() {

        return sourceEffect
                .activeParticipantOriginalInputReductionExists();
    }

    public boolean capacityReviewRequired() {

        return sourceEffect
                .capacityReviewRequired();
    }

    /*
     * =================================================
     * 최종 Qualification
     * =================================================
     */

    /**
     * 이 Synthesis를 단순한 한 방향 문장으로
     * 강하게 표현하면 안 되는가.
     */
    public boolean qualificationRequired() {

        if (
                !temporalContactExists()
        ) {

            return false;
        }

        return sourceEffect
                .qualificationRequired()

                || qualifiedContextExists()

                || weakenedContextExists()

                || conflictedContextExists()

                || mixedContextStatus();
    }

    /**
     * 원국 REFERENCE Pattern은
     * 어떤 Context Match가 추가돼도
     * 강한 직접근거로 승격하지 않는다.
     */
    public boolean referenceOnly() {

        return referencePatternContact();
    }

    /**
     * Annual Layer가
     * "대운에서 실제 구조적 Pattern 접촉이 있었다"
     * 고 사용할 수 있는 기준.
     *
     * Context SUPPORTED 여부와 무관하게,
     * 원국 structurallyEffective + full contact가 핵심이다.
     */
    public boolean usableAsEffectiveDecadePatternContact() {

        return effectiveFullPatternContact();
    }

    /**
     * Exact Match 자체를 별도 점수로
     * 더하면 안 된다는 의미의 Helper.
     */
    public boolean exactMatchIsContextOnly() {

        return exactTenGodContextMatchExists();
    }
}