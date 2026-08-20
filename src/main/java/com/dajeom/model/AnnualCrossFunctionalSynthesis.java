package com.dajeom.model;

import com.dajeom.type.AnnualCrossContextScope;
import com.dajeom.type.AnnualLuckContextStatus;
import com.dajeom.type.NatalCrossFunctionalPatternType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 Annual CrossFunctional Pattern Effect와
 * 14-6 Context Signal을 구조적으로 교차한
 * 최종 Annual CrossFunctional Synthesis.
 *
 * =================================================
 *
 * 입력:
 *
 * AnnualCrossFunctionalPatternEffect
 *
 * +
 *
 * AnnualCrossContextMatch
 *
 * =================================================
 *
 * 보존:
 *
 * 1.
 * 원국 Pattern 수준
 *
 * 2.
 * 대운 Contact
 *
 * 3.
 * 세운 Segment Contact
 *
 * 4.
 * 새 Completion / Continuation
 *
 * 5.
 * Transformation Target 참여
 *
 * 6.
 * Original Function Reduction
 *
 * 7.
 * Natal Fixed Capacity
 *
 * 8.
 * 14-6 Context TenGod Match
 *
 * 9.
 * Context Status
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * EXACT Match는 Pattern 승격 근거가 아니다.
 *
 * SUPPORTED Context도 Function을 강화하지 않는다.
 *
 * WEAKENED / CONFLICTED도
 * Function이나 Pattern을 삭제하지 않는다.
 *
 * Context는 지지/제한/충돌 조건으로만 보존한다.
 */
public record AnnualCrossFunctionalSynthesis(

        AnnualCrossFunctionalPatternEffect
        sourceEffect,

        List<AnnualCrossContextMatch>
        contextMatches,

        boolean completeChart

) {

    public AnnualCrossFunctionalSynthesis {

        Objects.requireNonNull(
                sourceEffect,
                "Annual CrossFunctional Synthesis에는 Source Effect가 필요합니다."
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
                    "Annual CrossFunctional Synthesis와 Source Effect의 completeChart가 일치하지 않습니다."
            );
        }

        Set<ContextMatchKey> seen =
                new HashSet<>();

        for (
                AnnualCrossContextMatch match
                : contextMatches
        ) {

            Objects.requireNonNull(
                    match,
                    "AnnualCrossContextMatch는 null일 수 없습니다."
            );

            if (
                    match.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual Cross Context Match의 completeChart가 일치하지 않습니다."
                );
            }

            if (
                    match.sourceEffect()
                            != sourceEffect
            ) {

                throw new IllegalArgumentException(
                        "Annual Cross Context Match가 현재 Source Effect를 참조하지 않습니다."
                );
            }

            ContextMatchKey key =
                    new ContextMatchKey(
                            match.contextScope(),
                            match.contextSignal()
                    );

            if (
                    !seen.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Scope의 Context Signal이 Annual Cross Synthesis에 중복되었습니다. "
                                + "scope="
                                + match.contextScope()
                                + ", code="
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
     * Temporal Contact
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
     * Completion / Continuation
     * =================================================
     */

    public boolean decadeFullContinuation() {

        return sourceEffect
                .decadeFullContinuation();
    }

    public boolean newlyCompleted() {

        return sourceEffect
                .newlyCompleted();
    }

    public boolean effectiveNewCompletionCandidate() {

        return sourceEffect
                .effectiveNewCompletionCandidate();
    }

    public boolean annualCompletion() {

        return sourceEffect
                .annualCompletion();
    }

    public boolean crossLayerCompletion() {

        return sourceEffect
                .crossLayerCompletion();
    }

    public boolean transformationCompletion() {

        return sourceEffect
                .transformationCompletion();
    }

    /*
     * =================================================
     * Context Match
     * =================================================
     */

    public List<AnnualCrossContextMatch>
    exactTenGodMatches() {

        return contextMatches
                .stream()
                .filter(
                        AnnualCrossContextMatch
                                ::exactTenGodMatch
                )
                .toList();
    }

    public List<AnnualCrossContextMatch>
    overlappingTenGodMatches() {

        return contextMatches
                .stream()
                .filter(
                        AnnualCrossContextMatch
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

    /*
     * =================================================
     * Context Scope
     * =================================================
     */

    public List<AnnualCrossContextMatch>
    annualCommonMatches() {

        return contextMatches
                .stream()
                .filter(
                        AnnualCrossContextMatch
                                ::annualCommonContext
                )
                .toList();
    }

    public List<AnnualCrossContextMatch>
    decadeSegmentMatches() {

        return contextMatches
                .stream()
                .filter(
                        AnnualCrossContextMatch
                                ::decadeSegmentContext
                )
                .toList();
    }

    public boolean annualCommonContextMatchExists() {

        return !annualCommonMatches()
                .isEmpty();
    }

    public boolean decadeSegmentContextMatchExists() {

        return !decadeSegmentMatches()
                .isEmpty();
    }

    public List<AnnualCrossContextMatch>
    matchesOfScope(
            AnnualCrossContextScope scope
    ) {

        if (
                scope == null
        ) {

            return List.of();
        }

        return contextMatches
                .stream()
                .filter(
                        match ->
                                match.contextScope()
                                        == scope
                )
                .toList();
    }

    /*
     * =================================================
     * Context Status
     * =================================================
     */

    public List<AnnualCrossContextMatch>
    matchesOfStatus(
            AnnualLuckContextStatus status
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
                        AnnualCrossContextMatch
                                ::supported
                );
    }

    public boolean qualifiedContextExists() {

        return contextMatches
                .stream()
                .anyMatch(
                        AnnualCrossContextMatch
                                ::qualified
                );
    }

    public boolean weakenedContextExists() {

        return contextMatches
                .stream()
                .anyMatch(
                        AnnualCrossContextMatch
                                ::weakened
                );
    }

    public boolean conflictedContextExists() {

        return contextMatches
                .stream()
                .anyMatch(
                        AnnualCrossContextMatch
                                ::conflicted
                );
    }

    public boolean mixedContextStatus() {

        Set<AnnualLuckContextStatus> statuses =
                new HashSet<>();

        for (
                AnnualCrossContextMatch match
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
     * Exact Match Status
     * =================================================
     */

    public boolean exactSupportedContextExists() {

        return exactTenGodMatches()
                .stream()
                .anyMatch(
                        AnnualCrossContextMatch
                                ::supported
                );
    }

    public boolean exactQualifiedContextExists() {

        return exactTenGodMatches()
                .stream()
                .anyMatch(
                        AnnualCrossContextMatch
                                ::qualified
                );
    }

    public boolean exactWeakenedContextExists() {

        return exactTenGodMatches()
                .stream()
                .anyMatch(
                        AnnualCrossContextMatch
                                ::weakened
                );
    }

    public boolean exactConflictedContextExists() {

        return exactTenGodMatches()
                .stream()
                .anyMatch(
                        AnnualCrossContextMatch
                                ::conflicted
                );
    }

    /*
     * =================================================
     * Effective + Context
     * =================================================
     *
     * 아래 Helper들은 "Evidence 추가"가 아니라
     * 동일 구조의 Context 재확인 여부다.
     */

    public boolean effectiveFullContactWithExactContext() {

        return effectiveFullPatternContact()
                && exactTenGodContextMatchExists();
    }

    public boolean effectiveNewCompletionWithExactContext() {

        return effectiveNewCompletionCandidate()
                && exactTenGodContextMatchExists();
    }

    /*
     * =================================================
     * Source Conditions
     * =================================================
     */

    public boolean transformationTargetParticipantContact() {

        return sourceEffect
                .crossLuckTargetParticipantContact();
    }

    public boolean originalFunctionReductionExists() {

        return sourceEffect
                .decadeOriginalInputReductionExists()

                || sourceEffect
                .annualOriginalInputReductionExists();
    }

    public boolean capacityReviewRequired() {

        return sourceEffect
                .capacityReviewRequired();
    }

    public boolean timingUncertain() {

        return sourceEffect
                .timingUncertain();
    }

    /*
     * =================================================
     * Qualification
     * =================================================
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
     * Annual Full Contact + Exact Supported가
     * 존재해도 승격하지 않는다.
     */
    public boolean referenceOnly() {

        return sourceEffect
                .referenceOnly();
    }

    /**
     * 이후 계층이 사용할 수 있는
     * 구조적으로 유효한 Annual Pattern Contact.
     *
     * Context Status와 무관하게
     *
     * natal structurallyEffective
     * +
     * Annual Segment full contact
     *
     * 가 기준이다.
     */
    public boolean usableAsEffectiveAnnualPatternContact() {

        return sourceEffect
                .usableAsEffectiveAnnualPatternContact();
    }

    /**
     * Exact Match를 별도 독립근거로
     * 점수화하지 말아야 함을 명시한다.
     */
    public boolean exactMatchIsContextOnly() {

        return exactTenGodContextMatchExists();
    }

    /*
     * =================================================
     * Internal Key
     * =================================================
     */

    private record ContextMatchKey(

            AnnualCrossContextScope scope,

            AnnualLuckContextSignal signal

    ) {

        private ContextMatchKey {

            Objects.requireNonNull(
                    scope
            );

            Objects.requireNonNull(
                    signal
            );
        }
    }
}