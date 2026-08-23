package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategoryTimelineYearEvidence;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PAST_LUCK / FUTURE_LUCK의
 * TIMELINE_YEAR BIG_FLOW 공통 계산기.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * PAST_LUCK과 FUTURE_LUCK이
 * 같은 Timeline Year Evidence를 사용하면서
 * 서로 다른 종합 공식을 갖는 것을 방지한다.
 *
 * 과거/미래의 차이는:
 *
 * - Evidence Range의 방향
 * - 최종 ConsultationCategory
 * - Rule Code
 * - 서술 문맥
 *
 * 이다.
 *
 * LifeDomain 상태를 연도 BIG_FLOW로
 * 계산하는 deterministic 공식 자체는 동일하다.
 *
 * =================================================
 *
 * Input
 * =================================================
 *
 * ConsultationCategoryEvidenceView
 *
 * ->
 *
 * ConsultationCategoryTimelineYearEvidence
 *
 * ->
 *
 * 실제 LifeDomainRuleMatch
 *
 * =================================================
 *
 * 계산 원칙
 * =================================================
 *
 * 1.
 * Match 개수를 강도로 사용하지 않는다.
 *
 * 2.
 * Rule 개수를 강도로 사용하지 않는다.
 *
 * 3.
 * Source 개수를 강도로 사용하지 않는다.
 *
 * 4.
 * 동일 Status 반복 횟수를
 *    확신도처럼 사용하지 않는다.
 *
 * 5.
 * Domain마다 먼저 상태를 결정한다.
 *
 * 6.
 * 그 다음 서로 다른 Domain 상태를
 *    연도 BIG_FLOW로 계산한다.
 *
 * =================================================
 *
 * Domain 상태
 * =================================================
 *
 * MIXED 존재
 * -> MIXED
 *
 * SUPPORTIVE + PRESSURE 공존
 * -> MIXED
 *
 * PRESSURE만 방향성으로 존재
 * -> PRESSURE
 *
 * SUPPORTIVE만 방향성으로 존재
 * -> SUPPORTIVE
 *
 * CONTEXTUAL만 존재
 * -> CONTEXTUAL
 *
 * =================================================
 *
 * Year BIG_FLOW
 * =================================================
 *
 * 하나 이상의 Domain이 MIXED
 * -> MIXED
 *
 * 서로 다른 Domain에
 * SUPPORTIVE + PRESSURE 공존
 * -> MIXED
 *
 * PRESSURE 방향만 존재
 * -> PRESSURE
 *
 * SUPPORTIVE 방향만 존재
 * -> SUPPORTIVE
 *
 * CONTEXTUAL만 존재
 * -> CONTEXTUAL
 *
 * =================================================
 *
 * 중요한 제한
 * =================================================
 *
 * ANNUAL_CONTEXT
 * +
 * ANNUAL_FUNCTIONAL
 *
 * 이 같이 존재한다는 이유로
 * 독립 확증 두 개로 세지 않는다.
 *
 * 애초에 이 계산기는
 * Evidence 개수나 Match 개수를
 * 점수화하지 않는다.
 *
 * =================================================
 *
 * 하지 않는 것
 * =================================================
 *
 * - 다수결
 * - 평균
 * - 점수 합산
 * - 대표 월 선택
 * - 대표 Domain 선택
 * - 사건 예측
 * - 성공/실패 판단
 * - 과거 사건 역추정
 * - 미래 사건 생성
 */
public abstract class AbstractTimelineYearBigFlowRule
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

    /**
     * reason/exception 문구에 사용할
     * 방향 표현.
     *
     * 예:
     *
     * PAST_LUCK
     * FUTURE_LUCK
     */
    protected abstract String flowLabel();


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
                        + " Timeline Year BIG_FLOW에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != category()
        ) {

            throw new IllegalArgumentException(
                    flowLabel()
                            + " Timeline Year BIG_FLOW Rule은 "
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
                        + " 연도 BIG_FLOW에는 ConsultationCategoryTimelineYearEvidence가 필요합니다."
        );

        List<LifeDomainRuleMatch> yearMatches =
                yearEvidence
                        .matches();

        if (
                yearMatches == null
                        || yearMatches.isEmpty()
        ) {

            return null;
        }

        Map<
                LifeDomain,
                List<LifeDomainRuleMatch>
                > matchesByDomain =
                groupByDomain(
                        yearMatches
                );

        Map<
                LifeDomain,
                DomainFlowState
                > domainStates =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            List<LifeDomainRuleMatch> domainMatches =
                    matchesByDomain
                            .getOrDefault(
                                    domain,
                                    List.of()
                            );

            if (
                    domainMatches.isEmpty()
            ) {

                continue;
            }

            domainStates.put(
                    domain,
                    resolveDomainState(
                            domainMatches
                    )
            );
        }

        if (
                domainStates.isEmpty()
        ) {

            return null;
        }

        ConsultationCategoryDeterministicStatus yearStatus =
                resolveYearStatus(
                        domainStates
                );

        List<LifeDomainRuleMatch> sourceMatches =
                selectSourceMatches(
                        yearMatches,
                        domainStates,
                        yearStatus
                );

        if (
                sourceMatches.isEmpty()
        ) {

            throw new IllegalStateException(
                    flowLabel()
                            + " BIG_FLOW 상태는 생성되었지만 Source Match가 없습니다. sajuYear="
                            + yearEvidence.sajuYear()
            );
        }

        return new ConsultationCategoryDeterministicMatch(
                category(),
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                code(),
                yearStatus,
                yearEvidence.sajuYear(),
                null,
                yearEvidence.monthIndices(),
                summaryOf(
                        yearStatus
                ),
                sourceMatches,
                buildReasons(
                        yearEvidence,
                        domainStates,
                        yearStatus
                )
        );
    }


    /*
     * =================================================
     * Group
     * =================================================
     */

    private Map<
            LifeDomain,
            List<LifeDomainRuleMatch>
            > groupByDomain(

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
                                    + " Timeline Year의 LifeDomainRuleMatch는 null일 수 없습니다."
                    );

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
     * Domain State
     * =================================================
     */

    private DomainFlowState resolveDomainState(

            List<LifeDomainRuleMatch> domainMatches

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
                : domainMatches
        ) {

            LifeDomainSignalStatus status =
                    Objects.requireNonNull(
                            match.status(),
                            "LifeDomainRuleMatch status는 null일 수 없습니다."
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

            return DomainFlowState.MIXED;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return DomainFlowState.MIXED;
        }

        if (
                pressureExists
        ) {

            return DomainFlowState.PRESSURE;
        }

        if (
                supportiveExists
        ) {

            return DomainFlowState.SUPPORTIVE;
        }

        if (
                contextualExists
        ) {

            return DomainFlowState.CONTEXTUAL;
        }

        throw new IllegalStateException(
                flowLabel()
                        + " Domain에 Match가 존재하지만 상태를 결정할 수 없습니다."
        );
    }


    /*
     * =================================================
     * Year State
     * =================================================
     */

    private ConsultationCategoryDeterministicStatus
    resolveYearStatus(

            Map<
                    LifeDomain,
                    DomainFlowState
                    > domainStates

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
                DomainFlowState state
                : domainStates.values()
        ) {

            switch (
                    state
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

            return ConsultationCategoryDeterministicStatus
                    .MIXED;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .MIXED;
        }

        if (
                pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .PRESSURE;
        }

        if (
                supportiveExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .SUPPORTIVE;
        }

        if (
                contextualExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .CONTEXTUAL;
        }

        throw new IllegalStateException(
                flowLabel()
                        + " Timeline Year의 BIG_FLOW를 결정할 수 없습니다."
        );
    }


    /*
     * =================================================
     * Source Selection
     * =================================================
     */

    private List<LifeDomainRuleMatch>
    selectSourceMatches(

            List<LifeDomainRuleMatch> yearMatches,

            Map<
                    LifeDomain,
                    DomainFlowState
                    > domainStates,

            ConsultationCategoryDeterministicStatus yearStatus

    ) {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : yearMatches
        ) {

            DomainFlowState domainState =
                    domainStates
                            .get(
                                    match.domain()
                            );

            if (
                    domainState == null
            ) {

                continue;
            }

            if (
                    !domainContributesToYear(
                            domainState,
                            yearStatus
                    )
            ) {

                continue;
            }

            if (
                    !matchContributesToDomain(
                            match.status(),
                            domainState
                    )
            ) {

                continue;
            }

            result.add(
                    match
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Domain -> Year
     * =================================================
     */

    private boolean domainContributesToYear(

            DomainFlowState domainState,

            ConsultationCategoryDeterministicStatus yearStatus

    ) {

        return switch (
                yearStatus
                ) {

            case MIXED ->
                    domainState
                            == DomainFlowState.MIXED

                            || domainState
                            == DomainFlowState.SUPPORTIVE

                            || domainState
                            == DomainFlowState.PRESSURE;

            case PRESSURE ->
                    domainState
                            == DomainFlowState.PRESSURE;

            case SUPPORTIVE ->
                    domainState
                            == DomainFlowState.SUPPORTIVE;

            case CONTEXTUAL ->
                    domainState
                            == DomainFlowState.CONTEXTUAL;
        };
    }


    /*
     * =================================================
     * Match -> Domain
     * =================================================
     */

    private boolean matchContributesToDomain(

            LifeDomainSignalStatus matchStatus,

            DomainFlowState domainState

    ) {

        Objects.requireNonNull(
                matchStatus,
                "Source Match Status가 필요합니다."
        );

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
                    matchStatus
                            == LifeDomainSignalStatus.CONTEXTUAL;
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
                    DomainFlowState
                    > domainStates,

            ConsultationCategoryDeterministicStatus yearStatus

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                flowLabel()
                        + "의 연도 BIG_FLOW는 LifeDomainRuleMatch 개수나 반복 횟수를 점수화하지 않고 각 LifeDomain에 어떤 deterministic 방향이 존재하는지만 사용합니다."
        );

        reasons.add(
                "같은 세운 또는 같은 Rule 결과가 여러 월 구간에서 반복되었다는 이유만으로 방향을 강화하지 않습니다."
        );

        reasons.add(
                "ANNUAL_CONTEXT와 ANNUAL_FUNCTIONAL이 함께 존재해도 같은 세운 provenance에서 파생된 근거를 독립 확증 두 개처럼 가중하지 않습니다."
        );

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            DomainFlowState state =
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
                            + " deterministic 방향은 "
                            + state
                            + "입니다."
            );
        }

        switch (
                yearStatus
        ) {

            case MIXED ->
                    reasons.add(
                            "하나 이상의 LifeDomain 자체에 MIXED 구조가 존재하거나 서로 다른 LifeDomain 사이에 SUPPORTIVE와 PRESSURE 방향이 함께 존재하여 연도 BIG_FLOW를 MIXED로 보존합니다."
                    );

            case PRESSURE ->
                    reasons.add(
                            "방향성이 확인된 LifeDomain에서 PRESSURE가 존재하고 SUPPORTIVE 방향과의 동시 공존은 확인되지 않아 연도 BIG_FLOW를 PRESSURE로 보존합니다."
                    );

            case SUPPORTIVE ->
                    reasons.add(
                            "방향성이 확인된 LifeDomain에서 SUPPORTIVE가 존재하고 PRESSURE 방향과의 동시 공존은 확인되지 않아 연도 BIG_FLOW를 SUPPORTIVE로 보존합니다."
                    );

            case CONTEXTUAL ->
                    reasons.add(
                            "해당 명리연도에는 SUPPORTIVE/PRESSURE/MIXED 방향을 독립적으로 확정할 LifeDomain 상태 없이 CONTEXTUAL 구조만 존재하여 연도 BIG_FLOW를 CONTEXTUAL로 보존합니다."
                    );
        }

        reasons.add(
                "이 연도 판단은 Timeline Range가 실제 계산한 monthIndex 목록 "
                        + yearEvidence.monthIndices()
                        + "을 그대로 보존하며 특정 대표월 하나를 선택하지 않습니다."
        );

        if (
                yearEvidence
                        .timelineUncertaintyExists()
        ) {

            reasons.add(
                    "해당 명리연도에는 Timeline uncertainty가 존재하며 이미 계산된 방향을 삭제하거나 임의로 CONTEXTUAL로 바꾸지 않고 불확실성 자체를 별도로 보존합니다."
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
     * Temporal-specific Reason
     * =================================================
     */

    protected abstract void appendTemporalReason(
            List<String> reasons
    );


    /*
     * =================================================
     * Summary
     * =================================================
     */

    private String summaryOf(

            ConsultationCategoryDeterministicStatus status

    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "해당 명리연도에는 방향성이 확인된 생활영역에서 지지 방향이 나타나며 별도의 부담 방향과의 혼재는 확인되지 않습니다.";

            case PRESSURE ->
                    "해당 명리연도에는 방향성이 확인된 생활영역에서 부담 방향이 나타나며 별도의 지지 방향과의 혼재는 확인되지 않습니다.";

            case MIXED ->
                    "해당 명리연도에는 생활영역 내부 또는 서로 다른 생활영역 사이에 지지와 부담의 서로 다른 방향이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "해당 명리연도에는 생활영역의 구조적 Context는 존재하지만 연도 전체의 지지·부담 방향을 단독으로 확정하지 않습니다.";
        };
    }


    /*
     * =================================================
     * Internal State
     * =================================================
     */

    private enum DomainFlowState {

        SUPPORTIVE,

        PRESSURE,

        MIXED,

        CONTEXTUAL
    }
}