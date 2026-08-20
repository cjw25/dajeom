package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategoryTimelineYearEvidence;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PAST_LUCK / FUTURE_LUCK
 * TIMELINE_YEAR CROSS_DOMAIN_PATTERN
 * 공통 deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 하나의 명리연도 안에서
 *
 * WEALTH
 * CAREER
 * RELATIONSHIP
 * LEARNING_GROWTH
 * HEALTH_LIFESTYLE
 *
 * 같은 서로 다른 LifeDomain의
 * deterministic 방향이
 * 어떤 구조로 함께 작동하는지 분류한다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 Rule은:
 *
 * - PRIMARY Domain을 선택하지 않는다.
 * - Domain 순위를 매기지 않는다.
 * - Match 개수를 세지 않는다.
 * - Rule 개수를 세지 않는다.
 * - Evidence 개수를 세지 않는다.
 * - 가장 많이 나온 Status를 선택하지 않는다.
 *
 * =================================================
 * Temporal Match
 * =================================================
 *
 * 해당 연도 CROSS_DOMAIN_PATTERN에는
 * 실제 연도 작동성이 있는:
 *
 * ANNUAL_CONTEXT
 * ANNUAL_FUNCTIONAL_CONTEXT
 * MONTHLY_CONTEXT
 *
 * 중 하나 이상을 사용하는 Match만 포함한다.
 *
 * Natal-only Match만으로
 * 연도 Cross-Domain Pattern을 만들지 않는다.
 *
 * =================================================
 * Annual Derived Provenance
 * =================================================
 *
 * ANNUAL_CONTEXT
 *
 * +
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * 가 함께 존재하더라도
 * 독립 근거 두 개로 계산하지 않는다.
 *
 * 이 Rule은 Source 수를 세지 않고
 * Domain별 상태 존재성만 사용하므로
 * derived provenance 중복이
 * Cross-Domain 방향을 강화하지 않는다.
 *
 * =================================================
 * Domain State
 * =================================================
 *
 * 하나의 LifeDomain 안에서:
 *
 * 명시적 MIXED 존재
 * ->
 * MIXED
 *
 * SUPPORTIVE + PRESSURE 공존
 * ->
 * MIXED
 *
 * PRESSURE만 방향성 존재
 * ->
 * PRESSURE
 *
 * SUPPORTIVE만 방향성 존재
 * ->
 * SUPPORTIVE
 *
 * CONTEXTUAL만 존재
 * ->
 * CONTEXTUAL
 *
 * =================================================
 * CROSS_DOMAIN_PATTERN
 * =================================================
 *
 * 1.
 * MIXED_PROPAGATION
 *
 * MIXED Domain이 존재하고
 * 다른 별도의 방향성 Domain도 존재.
 *
 * ->
 * MIXED
 *
 * =================================================
 *
 * 2.
 * DIRECTIONAL_CONFLICT
 *
 * 서로 다른 Domain 사이에
 *
 * SUPPORTIVE
 *
 * 와
 *
 * PRESSURE
 *
 * 가 동시에 존재.
 *
 * ->
 * MIXED
 *
 * =================================================
 *
 * 3.
 * PRESSURE_RESONANCE
 *
 * 서로 다른 최소 2개 Domain이
 * PRESSURE.
 *
 * ->
 * PRESSURE
 *
 * =================================================
 *
 * 4.
 * SUPPORTIVE_RESONANCE
 *
 * 서로 다른 최소 2개 Domain이
 * SUPPORTIVE.
 *
 * ->
 * SUPPORTIVE
 *
 * =================================================
 *
 * 5.
 * DIRECTIONAL_WITH_CONTEXT
 *
 * 방향성 Domain은 하나뿐이고
 * 다른 Domain들은 CONTEXTUAL.
 *
 * 또는
 *
 * MIXED Domain 하나와
 * CONTEXTUAL Domain만 공존.
 *
 * ->
 * CONTEXTUAL
 *
 * 이유:
 *
 * 한 Domain의 방향을
 * 다른 CONTEXTUAL Domain까지
 * 같은 방향이라고 승격하지 않는다.
 *
 * =================================================
 *
 * 6.
 * CONTEXTUAL_SPREAD
 *
 * 둘 이상의 Domain이
 * CONTEXTUAL만 가지고 있음.
 *
 * ->
 * CONTEXTUAL
 *
 * =================================================
 * 우선순위
 * =================================================
 *
 * Pattern 분류 우선순위는
 * 강도 순위가 아니다.
 *
 * 더 구체적인 구조를 먼저 식별하기 위한
 * deterministic classification 순서다.
 *
 * MIXED propagation
 * ->
 * directional conflict
 * ->
 * pressure resonance
 * ->
 * supportive resonance
 * ->
 * directional with context
 * ->
 * contextual spread
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * PRESSURE_RESONANCE
 * ->
 * 실제 문제가 여러 개 발생한다
 *
 * DIRECTIONAL_CONFLICT
 * ->
 * 좋은 일과 나쁜 일이 동시에 발생한다
 *
 * SUPPORTIVE_RESONANCE
 * ->
 * 큰 성공이 발생한다
 *
 * MIXED_PROPAGATION
 * ->
 * 상황이 혼란스럽다
 *
 * 같은 사건·심리·체감 추론은 하지 않는다.
 */
public abstract class AbstractTimelineYearCrossDomainPatternRule
        implements ConsultationCategoryDeterministicRule {

    /*
     * =================================================
     * Subclass Contract
     * =================================================
     */

    @Override
    public abstract ConsultationCategory category();

    @Override
    public abstract String code();

    protected abstract String flowLabel();

    protected abstract void appendTemporalReason(
            List<String> reasons
    );


    /*
     * =================================================
     * Evaluate
     * =================================================
     */

    @Override
    public final List<ConsultationCategoryDeterministicMatch> evaluate(

            ConsultationCategoryEvidenceView view

    ) {

        Objects.requireNonNull(
                view,
                flowLabel()
                        + " CROSS_DOMAIN_PATTERN에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != category()
        ) {

            throw new IllegalArgumentException(
                    flowLabel()
                            + " CROSS_DOMAIN_PATTERN Rule은 "
                            + category()
                            + " View만 평가할 수 있습니다. actual="
                            + view.category()
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        if (
                !view.hasTimelineYearEvidences()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                ConsultationCategoryTimelineYearEvidence yearEvidence
                : view.timelineYearEvidences()
        ) {

            ConsultationCategoryDeterministicMatch match =
                    evaluateYear(
                            yearEvidence
                    );

            if (
                    match != null
            ) {

                result.add(
                        match
                );
            }
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * One Year
     * =================================================
     */

    private ConsultationCategoryDeterministicMatch evaluateYear(

            ConsultationCategoryTimelineYearEvidence yearEvidence

    ) {

        Objects.requireNonNull(
                yearEvidence,
                flowLabel()
                        + " CROSS_DOMAIN_PATTERN에는 Timeline Year Evidence가 필요합니다."
        );

        List<LifeDomainRuleMatch> yearMatches =
                yearEvidence.matches();

        if (
                yearMatches == null
                        || yearMatches.isEmpty()
        ) {

            return null;
        }

        /*
         * =================================================
         * Temporal Match만 사용
         * =================================================
         */

        Map<
                LifeDomain,
                List<LifeDomainRuleMatch>
                > temporalByDomain =
                groupTemporalMatches(
                        yearMatches
                );

        /*
         * Cross-Domain은 최소 두 Domain이 필요하다.
         */

        if (
                temporalByDomain.size()
                        < 2
        ) {

            return null;
        }

        Map<
                LifeDomain,
                DomainState
                > domainStates =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            List<LifeDomainRuleMatch> matches =
                    temporalByDomain
                            .getOrDefault(
                                    domain,
                                    List.of()
                            );

            if (
                    matches.isEmpty()
            ) {

                continue;
            }

            domainStates.put(
                    domain,
                    resolveDomainState(
                            matches
                    )
            );
        }

        if (
                domainStates.size()
                        < 2
        ) {

            return null;
        }

        CrossDomainPattern pattern =
                resolveCrossDomainPattern(
                        domainStates
                );

        if (
                pattern == null
        ) {

            return null;
        }

        List<LifeDomainRuleMatch> sourceMatches =
                selectSourceMatches(
                        temporalByDomain,
                        domainStates,
                        pattern
                );

        if (
                sourceMatches.isEmpty()
        ) {

            throw new IllegalStateException(
                    flowLabel()
                            + " CROSS_DOMAIN_PATTERN이 생성되었지만 sourceMatches가 없습니다. sajuYear="
                            + yearEvidence.sajuYear()
                            + ", pattern="
                            + pattern
            );
        }

        List<String> reasons =
                buildReasons(
                        yearEvidence,
                        domainStates,
                        pattern
                );

        return new ConsultationCategoryDeterministicMatch(
                category(),
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                code(),
                pattern.status(),
                yearEvidence.sajuYear(),
                null,
                yearEvidence.monthIndices(),
                summaryOf(
                        pattern,
                        domainStates
                ),
                sourceMatches,
                reasons
        );
    }


    /*
     * =================================================
     * Temporal Group
     * =================================================
     */

    private Map<
            LifeDomain,
            List<LifeDomainRuleMatch>
            > groupTemporalMatches(

            List<LifeDomainRuleMatch> matches

    ) {

        Map<
                LifeDomain,
                List<LifeDomainRuleMatch>
                > mutable =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomainRuleMatch match
                : matches
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            flowLabel()
                                    + " CROSS_DOMAIN_PATTERN의 LifeDomainRuleMatch는 null일 수 없습니다."
                    );

            if (
                    !temporalActivityExists(
                            normalized
                    )
            ) {

                continue;
            }

            mutable
                    .computeIfAbsent(
                            normalized.domain(),
                            ignored ->
                                    new ArrayList<>()
                    )
                    .add(
                            normalized
                    );
        }

        Map<
                LifeDomain,
                List<LifeDomainRuleMatch>
                > result =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                Map.Entry<
                        LifeDomain,
                        List<LifeDomainRuleMatch>
                        > entry
                : mutable.entrySet()
        ) {

            result.put(
                    entry.getKey(),
                    List.copyOf(
                            entry.getValue()
                    )
            );
        }

        return result;
    }


    /*
     * =================================================
     * Temporal Activity
     * =================================================
     */

    private boolean temporalActivityExists(

            LifeDomainRuleMatch match

    ) {

        for (
                LifeDomainEvidenceReference<?> reference
                : match.evidences()
        ) {

            Objects.requireNonNull(
                    reference,
                    "CROSS_DOMAIN_PATTERN Evidence는 null일 수 없습니다."
            );

            LifeDomainEvidenceSourceType sourceType =
                    reference.sourceType();

            if (
                    sourceType
                            == LifeDomainEvidenceSourceType
                            .ANNUAL_CONTEXT

                            || sourceType
                            == LifeDomainEvidenceSourceType
                            .ANNUAL_FUNCTIONAL_CONTEXT

                            || sourceType
                            == LifeDomainEvidenceSourceType
                            .MONTHLY_CONTEXT
            ) {

                return true;
            }
        }

        return false;
    }


    /*
     * =================================================
     * Domain State
     * =================================================
     */

    private DomainState resolveDomainState(

            List<LifeDomainRuleMatch> matches

    ) {

        boolean supportiveExists =
                false;

        boolean pressureExists =
                false;

        boolean mixedExists =
                false;

        boolean contextualExists =
                false;

        for (
                LifeDomainRuleMatch match
                : matches
        ) {

            LifeDomainSignalStatus status =
                    Objects.requireNonNull(
                            match.status(),
                            "CROSS_DOMAIN_PATTERN source status는 null일 수 없습니다."
                    );

            switch (
                    status
            ) {

                case SUPPORTIVE ->
                        supportiveExists =
                                true;

                case PRESSURE ->
                        pressureExists =
                                true;

                case MIXED ->
                        mixedExists =
                                true;

                case CONTEXTUAL ->
                        contextualExists =
                                true;
            }
        }

        if (
                mixedExists
        ) {

            return DomainState.MIXED;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return DomainState.MIXED;
        }

        if (
                pressureExists
        ) {

            return DomainState.PRESSURE;
        }

        if (
                supportiveExists
        ) {

            return DomainState.SUPPORTIVE;
        }

        if (
                contextualExists
        ) {

            return DomainState.CONTEXTUAL;
        }

        throw new IllegalStateException(
                flowLabel()
                        + " CROSS_DOMAIN_PATTERN Domain에 Match가 존재하지만 상태를 결정할 수 없습니다."
        );
    }


    /*
     * =================================================
     * Cross-Domain Classification
     * =================================================
     */

    private CrossDomainPattern resolveCrossDomainPattern(

            Map<
                    LifeDomain,
                    DomainState
                    > domainStates

    ) {

        List<LifeDomain> supportiveDomains =
                domainsOfState(
                        domainStates,
                        DomainState.SUPPORTIVE
                );

        List<LifeDomain> pressureDomains =
                domainsOfState(
                        domainStates,
                        DomainState.PRESSURE
                );

        List<LifeDomain> mixedDomains =
                domainsOfState(
                        domainStates,
                        DomainState.MIXED
                );

        List<LifeDomain> contextualDomains =
                domainsOfState(
                        domainStates,
                        DomainState.CONTEXTUAL
                );

        int directionalDomainCount =
                supportiveDomains.size()
                        + pressureDomains.size()
                        + mixedDomains.size();

        /*
         * =================================================
         * 1. MIXED_PROPAGATION
         * =================================================
         *
         * MIXED Domain 하나가 존재하고
         * 그 외에 별도의 방향성 Domain이 하나 이상.
         */

        if (
                !mixedDomains.isEmpty()
                        && directionalDomainCount >= 2
        ) {

            return CrossDomainPattern
                    .MIXED_PROPAGATION;
        }

        /*
         * =================================================
         * 2. DIRECTIONAL_CONFLICT
         * =================================================
         *
         * 서로 다른 Domain에
         * SUPPORTIVE와 PRESSURE가 공존.
         */

        if (
                !supportiveDomains.isEmpty()
                        && !pressureDomains.isEmpty()
        ) {

            return CrossDomainPattern
                    .DIRECTIONAL_CONFLICT;
        }

        /*
         * =================================================
         * 3. PRESSURE_RESONANCE
         * =================================================
         */

        if (
                pressureDomains.size()
                        >= 2
        ) {

            return CrossDomainPattern
                    .PRESSURE_RESONANCE;
        }

        /*
         * =================================================
         * 4. SUPPORTIVE_RESONANCE
         * =================================================
         */

        if (
                supportiveDomains.size()
                        >= 2
        ) {

            return CrossDomainPattern
                    .SUPPORTIVE_RESONANCE;
        }

        /*
         * =================================================
         * 5. DIRECTIONAL_WITH_CONTEXT
         * =================================================
         *
         * 방향성 Domain은 하나뿐이고
         * 다른 Domain들은 Contextual.
         *
         * 또는 MIXED 하나 + Contextual.
         *
         * Contextual을 같은 방향으로 승격하지 않는다.
         */

        if (
                directionalDomainCount == 1
                        && !contextualDomains.isEmpty()
        ) {

            return CrossDomainPattern
                    .DIRECTIONAL_WITH_CONTEXT;
        }

        /*
         * =================================================
         * 6. CONTEXTUAL_SPREAD
         * =================================================
         */

        if (
                directionalDomainCount == 0
                        && contextualDomains.size() >= 2
        ) {

            return CrossDomainPattern
                    .CONTEXTUAL_SPREAD;
        }

        /*
         * 구조적으로 Cross-Domain Pattern을
         * 하나로 정의할 수 없음.
         */

        return null;
    }


    /*
     * =================================================
     * Domains Of State
     * =================================================
     */

    private List<LifeDomain> domainsOfState(

            Map<
                    LifeDomain,
                    DomainState
                    > domainStates,

            DomainState expectedState

    ) {

        List<LifeDomain> result =
                new ArrayList<>();

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            DomainState actual =
                    domainStates.get(
                            domain
                    );

            if (
                    actual
                            == expectedState
            ) {

                result.add(
                        domain
                );
            }
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Source Selection
     * =================================================
     *
     * Pattern에 실제 기여한 Domain/Status만
     * sourceMatches로 보존한다.
     */

    private List<LifeDomainRuleMatch>
    selectSourceMatches(

            Map<
                    LifeDomain,
                    List<LifeDomainRuleMatch>
                    > temporalByDomain,

            Map<
                    LifeDomain,
                    DomainState
                    > domainStates,

            CrossDomainPattern pattern

    ) {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            DomainState domainState =
                    domainStates
                            .get(
                                    domain
                            );

            if (
                    domainState == null
            ) {

                continue;
            }

            if (
                    !domainContributes(
                            domainState,
                            pattern
                    )
            ) {

                continue;
            }

            List<LifeDomainRuleMatch> domainMatches =
                    temporalByDomain
                            .getOrDefault(
                                    domain,
                                    List.of()
                            );

            for (
                    LifeDomainRuleMatch match
                    : domainMatches
            ) {

                if (
                        matchContributes(
                                match.status(),
                                domainState,
                                pattern
                        )
                ) {

                    result.add(
                            match
                    );
                }
            }
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Domain Contribution
     * =================================================
     */

    private boolean domainContributes(

            DomainState state,

            CrossDomainPattern pattern

    ) {

        return switch (
                pattern
                ) {

            case MIXED_PROPAGATION ->
                    state
                            == DomainState.MIXED

                            || state
                            == DomainState.SUPPORTIVE

                            || state
                            == DomainState.PRESSURE;

            case DIRECTIONAL_CONFLICT ->
                    state
                            == DomainState.SUPPORTIVE

                            || state
                            == DomainState.PRESSURE;

            case PRESSURE_RESONANCE ->
                    state
                            == DomainState.PRESSURE;

            case SUPPORTIVE_RESONANCE ->
                    state
                            == DomainState.SUPPORTIVE;

            case DIRECTIONAL_WITH_CONTEXT ->
                    state
                            == DomainState.MIXED

                            || state
                            == DomainState.SUPPORTIVE

                            || state
                            == DomainState.PRESSURE

                            || state
                            == DomainState.CONTEXTUAL;

            case CONTEXTUAL_SPREAD ->
                    state
                            == DomainState.CONTEXTUAL;
        };
    }


    /*
     * =================================================
     * Match Contribution
     * =================================================
     */

    private boolean matchContributes(

            LifeDomainSignalStatus matchStatus,

            DomainState domainState,

            CrossDomainPattern pattern

    ) {

        Objects.requireNonNull(
                matchStatus,
                "CROSS_DOMAIN_PATTERN source Match status가 필요합니다."
        );

        /*
         * =================================================
         * Domain State를 만든 실제 방향만 사용.
         * =================================================
         */

        return switch (
                domainState
                ) {

            case MIXED ->
                    matchStatus
                            == LifeDomainSignalStatus.MIXED

                            || matchStatus
                            == LifeDomainSignalStatus.SUPPORTIVE

                            || matchStatus
                            == LifeDomainSignalStatus.PRESSURE;

            case PRESSURE ->
                    matchStatus
                            == LifeDomainSignalStatus.PRESSURE;

            case SUPPORTIVE ->
                    matchStatus
                            == LifeDomainSignalStatus.SUPPORTIVE;

            case CONTEXTUAL ->
                    pattern
                            == CrossDomainPattern.DIRECTIONAL_WITH_CONTEXT

                            || pattern
                            == CrossDomainPattern.CONTEXTUAL_SPREAD

                            ? matchStatus
                            == LifeDomainSignalStatus.CONTEXTUAL

                            : false;
        };
    }


    /*
     * =================================================
     * Reasons
     * =================================================
     */

    private List<String> buildReasons(

            ConsultationCategoryTimelineYearEvidence yearEvidence,

            Map<
                    LifeDomain,
                    DomainState
                    > domainStates,

            CrossDomainPattern pattern

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                flowLabel()
                        + " CROSS_DOMAIN_PATTERN은 Match 수, Rule 수, Evidence 수를 비교하지 않고 서로 다른 LifeDomain에 존재하는 deterministic 방향의 구조적 조합만 사용합니다."
        );

        reasons.add(
                "ANNUAL_CONTEXT와 ANNUAL_FUNCTIONAL_CONTEXT가 동시에 존재하더라도 동일 세운 provenance의 파생 구조를 독립 확증 두 개처럼 가중하지 않습니다."
        );

        reasons.add(
                "Natal-only Match는 해당 명리연도의 Cross-Domain 작동으로 승격하지 않고 ANNUAL_CONTEXT, ANNUAL_FUNCTIONAL_CONTEXT 또는 MONTHLY_CONTEXT를 사용하는 Match만 사용합니다."
        );

        /*
         * =================================================
         * Domain State
         * =================================================
         */

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            DomainState state =
                    domainStates
                            .get(
                                    domain
                            );

            if (
                    state == null
            ) {

                continue;
            }

            reasons.add(
                    "해당 명리연도의 "
                            + domain
                            + " temporal deterministic 방향은 "
                            + state
                            + "입니다."
            );
        }

        /*
         * =================================================
         * Pattern
         * =================================================
         */

        switch (
                pattern
        ) {

            case MIXED_PROPAGATION ->
                    reasons.add(
                            "하나 이상의 LifeDomain에 MIXED 구조가 존재하고 별도의 다른 방향성 LifeDomain도 함께 존재하여 CROSS_DOMAIN_PATTERN을 MIXED_PROPAGATION으로 분류합니다."
                    );

            case DIRECTIONAL_CONFLICT ->
                    reasons.add(
                            "서로 다른 LifeDomain 사이에 SUPPORTIVE와 PRESSURE 방향이 동시에 존재하여 CROSS_DOMAIN_PATTERN을 DIRECTIONAL_CONFLICT로 분류합니다. 어느 방향도 삭제하거나 상쇄하지 않습니다."
                    );

            case PRESSURE_RESONANCE ->
                    reasons.add(
                            "서로 다른 최소 두 LifeDomain에 명시적 PRESSURE 방향이 존재하여 CROSS_DOMAIN_PATTERN을 PRESSURE_RESONANCE로 분류합니다. Match 반복 횟수는 사용하지 않습니다."
                    );

            case SUPPORTIVE_RESONANCE ->
                    reasons.add(
                            "서로 다른 최소 두 LifeDomain에 SUPPORTIVE 방향이 존재하여 CROSS_DOMAIN_PATTERN을 SUPPORTIVE_RESONANCE로 분류합니다. 이것을 사건 성공이나 결과 확정으로 변환하지 않습니다."
                    );

            case DIRECTIONAL_WITH_CONTEXT ->
                    reasons.add(
                            "하나의 방향성 LifeDomain과 하나 이상의 CONTEXTUAL LifeDomain이 함께 존재하지만 CONTEXTUAL Domain을 해당 방향으로 승격할 수 없어 CROSS_DOMAIN_PATTERN을 DIRECTIONAL_WITH_CONTEXT로 보존합니다."
                    );

            case CONTEXTUAL_SPREAD ->
                    reasons.add(
                            "서로 다른 최소 두 LifeDomain에 CONTEXTUAL 구조만 존재하여 CROSS_DOMAIN_PATTERN을 CONTEXTUAL_SPREAD로 분류합니다."
                    );
        }

        /*
         * =================================================
         * Timeline
         * =================================================
         */

        reasons.add(
                "해당 명리연도의 Timeline Range가 실제 계산한 monthIndex 목록 "
                        + yearEvidence.monthIndices()
                        + "을 그대로 보존하며 특정 대표월 하나를 선택하지 않습니다."
        );

        if (
                yearEvidence
                        .timelineUncertaintyExists()
        ) {

            reasons.add(
                    "해당 명리연도에는 Timeline uncertainty가 존재하며 Cross-Domain Pattern을 확정 사건이나 단일 시간 시나리오로 변환하지 않습니다."
            );
        }

        appendTemporalReason(
                reasons
        );

        return List.copyOf(
                reasons
        );
    }


    /*
     * =================================================
     * Summary
     * =================================================
     */

    private String summaryOf(

            CrossDomainPattern pattern,

            Map<
                    LifeDomain,
                    DomainState
                    > domainStates

    ) {

        List<String> participants =
                new ArrayList<>();

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            DomainState state =
                    domainStates
                            .get(
                                    domain
                            );

            if (
                    state == null
            ) {

                continue;
            }

            if (
                    domainContributes(
                            state,
                            pattern
                    )
            ) {

                participants.add(
                        domain
                                + "="
                                + state
                );
            }
        }

        return switch (
                pattern
                ) {

            case MIXED_PROPAGATION ->
                    "해당 명리연도에는 MIXED LifeDomain과 다른 방향성 LifeDomain이 함께 작동하는 Cross-Domain 혼재 구조가 존재합니다. participants="
                            + participants;

            case DIRECTIONAL_CONFLICT ->
                    "해당 명리연도에는 서로 다른 LifeDomain 사이에 SUPPORTIVE와 PRESSURE 방향이 함께 존재하는 Cross-Domain 방향 충돌 구조가 있습니다. participants="
                            + participants;

            case PRESSURE_RESONANCE ->
                    "해당 명리연도에는 서로 다른 LifeDomain에 PRESSURE 방향이 함께 존재하는 Cross-Domain 부담 공진 구조가 있습니다. participants="
                            + participants;

            case SUPPORTIVE_RESONANCE ->
                    "해당 명리연도에는 서로 다른 LifeDomain에 SUPPORTIVE 방향이 함께 존재하는 Cross-Domain 지지 공진 구조가 있습니다. participants="
                            + participants;

            case DIRECTIONAL_WITH_CONTEXT ->
                    "해당 명리연도에는 하나의 방향성 LifeDomain과 방향을 확정하지 않는 CONTEXTUAL LifeDomain이 함께 존재합니다. participants="
                            + participants;

            case CONTEXTUAL_SPREAD ->
                    "해당 명리연도에는 여러 LifeDomain에 방향을 단독 확정하지 않는 CONTEXTUAL 구조가 함께 존재합니다. participants="
                            + participants;
        };
    }


    /*
     * =================================================
     * Internal Domain State
     * =================================================
     */

    private enum DomainState {

        SUPPORTIVE,

        PRESSURE,

        MIXED,

        CONTEXTUAL
    }


    /*
     * =================================================
     * Internal Cross-Domain Pattern
     * =================================================
     *
     * 외부 공식 Enum을 새로 추가하지 않는다.
     *
     * 현재 CategoryDeterministicMatch의
     * 공식 Status 체계를 유지하면서
     * 이 Rule 내부에서 Cross-Domain 구조를
     * deterministic하게 분류한다.
     */
    private enum CrossDomainPattern {

        MIXED_PROPAGATION(
                ConsultationCategoryDeterministicStatus.MIXED
        ),

        DIRECTIONAL_CONFLICT(
                ConsultationCategoryDeterministicStatus.MIXED
        ),

        PRESSURE_RESONANCE(
                ConsultationCategoryDeterministicStatus.PRESSURE
        ),

        SUPPORTIVE_RESONANCE(
                ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ),

        DIRECTIONAL_WITH_CONTEXT(
                ConsultationCategoryDeterministicStatus.CONTEXTUAL
        ),

        CONTEXTUAL_SPREAD(
                ConsultationCategoryDeterministicStatus.CONTEXTUAL
        );

        private final ConsultationCategoryDeterministicStatus
                status;

        CrossDomainPattern(
                ConsultationCategoryDeterministicStatus status
        ) {

            this.status =
                    Objects.requireNonNull(
                            status
                    );
        }

        private ConsultationCategoryDeterministicStatus status() {

            return status;
        }
    }
}