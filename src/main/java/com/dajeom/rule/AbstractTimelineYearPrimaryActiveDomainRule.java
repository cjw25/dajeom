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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * PAST_LUCK / FUTURE_LUCK의
 * TIMELINE_YEAR PRIMARY_ACTIVE_DOMAIN
 * 공통 deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 한 명리연도에서:
 *
 * WEALTH
 * CAREER
 * RELATIONSHIP
 * LEARNING_GROWTH
 * HEALTH_LIFESTYLE
 *
 * 중 어떤 생활영역이
 * 가장 분명하게 "작동축"으로 식별되는지를
 * deterministic하게 판단한다.
 *
 * =================================================
 * PRIMARY_PRESSURE_DOMAIN과의 차이
 * =================================================
 *
 * PRIMARY_PRESSURE_DOMAIN:
 *
 * 명시적인 PRESSURE만 후보.
 *
 * =================================================
 *
 * PRIMARY_ACTIVE_DOMAIN:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * 모두 후보가 될 수 있다.
 *
 * 따라서 이 Rule은:
 *
 * "좋은 영역"
 *
 * 또는
 *
 * "나쁜 영역"
 *
 * 을 찾는 Rule이 아니다.
 *
 * =================================================
 * 중요한 원칙 1.
 *
 * Match 개수를 사용하지 않는다.
 *
 * RELATIONSHIP 20개
 * CAREER 3개
 *
 * 라고 해서
 * RELATIONSHIP이 더 Active하다는 뜻이 아니다.
 *
 * =================================================
 * 중요한 원칙 2.
 *
 * Rule 개수를 사용하지 않는다.
 *
 * Domain마다 등록된 Rule 수가 다르므로
 * Rule 수는 Domain 활동 강도가 아니다.
 *
 * =================================================
 * 중요한 원칙 3.
 *
 * Evidence 수를 더하지 않는다.
 *
 * 특히:
 *
 * ANNUAL_CONTEXT
 * +
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * 는 동일 Annual provenance의
 * 서로 다른 분석 단계다.
 *
 * 독립 근거 두 개로 더하지 않는다.
 *
 * =================================================
 * Temporal Candidate
 * =================================================
 *
 * PRIMARY_ACTIVE_DOMAIN 후보가 되려면
 * 해당 Domain에 최소 하나의:
 *
 * - ANNUAL_CONTEXT
 * - ANNUAL_FUNCTIONAL_CONTEXT
 * - MONTHLY_CONTEXT
 *
 * 를 사용하는 Match가 있어야 한다.
 *
 * Natal-only Match 반복으로
 * 해당 연도의 Active Domain을 만들지 않는다.
 *
 * =================================================
 * Domain 방향
 * =================================================
 *
 * 시간적 Match들 안에서:
 *
 * MIXED 존재
 * ->
 * MIXED
 *
 * SUPPORTIVE + PRESSURE 공존
 * ->
 * MIXED
 *
 * PRESSURE 존재
 * ->
 * PRESSURE
 *
 * SUPPORTIVE 존재
 * ->
 * SUPPORTIVE
 *
 * CONTEXTUAL만 존재
 * ->
 * CONTEXTUAL
 *
 * =================================================
 * Annual Qualification
 * =================================================
 *
 * NONE
 * <
 * ANNUAL_CONTEXT
 * <
 * ANNUAL_FUNCTIONAL
 *
 * =================================================
 *
 * 이것은 Evidence 가중치가 아니다.
 *
 * ANNUAL_FUNCTIONAL_CONTEXT가
 * 동일 Annual provenance에 대해:
 *
 * Natal Functional
 * Decade Functional
 * Annual Functional
 * CrossFunctional
 * Context Qualification
 *
 * 까지 연결된 더 정밀한 구조라는
 * qualification 계층이다.
 *
 * =================================================
 * Monthly Persistence
 * =================================================
 *
 * 서로 다른 2개 이상의
 * MONTHLY_CONTEXT sourceKey가 존재하면:
 *
 * multiMonthActivity = true
 *
 * =================================================
 *
 * 이것은:
 *
 * Match 2개니까 강도 2배
 *
 * 라는 뜻이 아니다.
 *
 * 하나의 월 Anchor에만 국한되지 않은
 * 시간적 지속 조건이다.
 *
 * =================================================
 * Directional Activity
 * =================================================
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 *
 * 는 방향성이 존재하므로:
 *
 * directionalActivity = true
 *
 * CONTEXTUAL만 존재하면:
 *
 * directionalActivity = false
 *
 * =================================================
 *
 * 단 SUPPORTIVE가 PRESSURE보다
 * 우위라는 의미는 아니다.
 *
 * SUPPORTIVE / PRESSURE / MIXED는
 * PRIMARY_ACTIVE 비교에서 같은
 * "방향성 존재" 계층이다.
 *
 * =================================================
 * Primary Selection
 * =================================================
 *
 * Candidate A가 B보다:
 *
 * 1.
 * Annual qualification이 낮지 않고
 *
 * 2.
 * Monthly persistence가 낮지 않고
 *
 * 3.
 * Directional activity가 낮지 않으며
 *
 * 이 세 축 중 최소 하나가
 * 엄격하게 높을 때:
 *
 * A dominates B.
 *
 * =================================================
 *
 * 하나의 Candidate가
 * 다른 모든 Candidate를 지배할 때만
 * PRIMARY_ACTIVE_DOMAIN을 생성한다.
 *
 * =================================================
 * 예
 * =================================================
 *
 * WEALTH:
 * ANNUAL_FUNCTIONAL
 * multiMonth=true
 * PRESSURE
 *
 * CAREER:
 * ANNUAL_CONTEXT
 * multiMonth=true
 * SUPPORTIVE
 *
 * ->
 *
 * WEALTH가 Annual qualification에서 높고
 * 나머지 자격은 낮지 않음.
 *
 * ->
 * WEALTH Primary Active
 *
 * =================================================
 * 예
 * =================================================
 *
 * WEALTH:
 * ANNUAL_FUNCTIONAL
 * multiMonth=false
 * PRESSURE
 *
 * CAREER:
 * ANNUAL_CONTEXT
 * multiMonth=true
 * SUPPORTIVE
 *
 * ->
 *
 * 서로 다른 자격축에서 우위.
 *
 * ->
 * incomparable
 *
 * ->
 * Primary 생성 안 함.
 *
 * =================================================
 * 예
 * =================================================
 *
 * WEALTH:
 * ANNUAL_FUNCTIONAL
 * multiMonth=true
 * PRESSURE
 *
 * CAREER:
 * ANNUAL_FUNCTIONAL
 * multiMonth=true
 * SUPPORTIVE
 *
 * ->
 *
 * 자격 동률.
 *
 * SUPPORTIVE/PRESSURE를 우열 비교하지 않음.
 *
 * ->
 * Primary 생성 안 함.
 *
 * =================================================
 * Timeline uncertainty
 * =================================================
 *
 * uncertainty만으로 이미 계산된
 * Domain 활동을 삭제하지 않는다.
 *
 * 다만 reason에 반드시 보존한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Match 수 랭킹
 * - Rule 수 랭킹
 * - Source 수 랭킹
 * - Domain 고정 우선순위
 * - SUPPORTIVE 우대
 * - PRESSURE 우대
 * - 대표 월 선택
 * - 사건 예측
 * - 실제 과거 사건 역추정
 */
public abstract class AbstractTimelineYearPrimaryActiveDomainRule
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
                        + " PRIMARY_ACTIVE_DOMAIN에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != category()
        ) {

            throw new IllegalArgumentException(
                    flowLabel()
                            + " PRIMARY_ACTIVE_DOMAIN Rule은 "
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
                        + " PRIMARY_ACTIVE_DOMAIN에는 Timeline Year Evidence가 필요합니다."
        );

        List<LifeDomainRuleMatch> yearMatches =
                yearEvidence.matches();

        if (
                yearMatches == null
                        || yearMatches.isEmpty()
        ) {

            return null;
        }

        Map<
                LifeDomain,
                List<LifeDomainRuleMatch>
                > temporalMatchesByDomain =
                groupTemporalMatches(
                        yearMatches
                );

        if (
                temporalMatchesByDomain.isEmpty()
        ) {

            /*
             * Natal-only 구조만으로
             * 해당 연도의 Active Domain을 만들지 않는다.
             */
            return null;
        }

        List<ActiveDomainCandidate> candidates =
                new ArrayList<>();

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            List<LifeDomainRuleMatch> temporalMatches =
                    temporalMatchesByDomain
                            .getOrDefault(
                                    domain,
                                    List.of()
                            );

            if (
                    temporalMatches.isEmpty()
            ) {

                continue;
            }

            candidates.add(
                    buildCandidate(
                            domain,
                            temporalMatches
                    )
            );
        }

        if (
                candidates.isEmpty()
        ) {

            return null;
        }

        ActiveDomainCandidate selected;

        /*
         * =================================================
         * 하나의 Temporal Domain만 존재
         * =================================================
         */

        if (
                candidates.size()
                        == 1
        ) {

            selected =
                    candidates.get(
                            0
                    );

        } else {

            selected =
                    selectUniqueDominantCandidate(
                            candidates
                    );

            /*
             * 명확한 지배 후보가 없다면
             * 억지 Primary를 만들지 않는다.
             */

            if (
                    selected == null
            ) {

                return null;
            }
        }

        List<String> reasons =
                buildReasons(
                        yearEvidence,
                        candidates,
                        selected
                );

        return new ConsultationCategoryDeterministicMatch(
                category(),
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                code(),
                ConsultationCategoryDeterministicStatus.from(
                        selected.domainStatus()
                ),
                yearEvidence.sajuYear(),
                null,
                yearEvidence.monthIndices(),
                summaryOf(
                        selected
                ),
                selected.temporalMatches(),
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
                                    + " Timeline Year Match는 null일 수 없습니다."
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
     *
     * 현재 공식적으로 확실히 연결된
     * Timeline Year Source만 사용한다.
     *
     * Natal-only Match를 Active Domain으로
     * 승격하지 않는다.
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
                    "PRIMARY_ACTIVE_DOMAIN Evidence는 null일 수 없습니다."
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
     * Candidate
     * =================================================
     */

    private ActiveDomainCandidate buildCandidate(

            LifeDomain domain,

            List<LifeDomainRuleMatch> temporalMatches

    ) {

        Objects.requireNonNull(
                domain,
                "Active Domain Candidate에는 LifeDomain이 필요합니다."
        );

        Objects.requireNonNull(
                temporalMatches,
                "Active Domain Candidate에는 Temporal Match가 필요합니다."
        );

        if (
                temporalMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Active Domain Candidate에는 최소 하나의 Temporal Match가 필요합니다. domain="
                            + domain
            );
        }

        AnnualActivityQualification annualQualification =
                AnnualActivityQualification.NONE;

        Set<String> monthlyAnchors =
                new LinkedHashSet<>();

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
                : temporalMatches
        ) {

            if (
                    match.domain()
                            != domain
            ) {

                throw new IllegalArgumentException(
                        "Active Domain Candidate의 Domain이 Source Match와 일치하지 않습니다. expected="
                                + domain
                                + ", actual="
                                + match.domain()
                );
            }

            LifeDomainSignalStatus status =
                    Objects.requireNonNull(
                            match.status(),
                            "Active Domain Candidate의 Match status는 null일 수 없습니다."
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

            for (
                    LifeDomainEvidenceReference<?> reference
                    : match.evidences()
            ) {

                Objects.requireNonNull(
                        reference,
                        "Active Domain Candidate Evidence는 null일 수 없습니다."
                );

                LifeDomainEvidenceSourceType sourceType =
                        reference.sourceType();

                /*
                 * =========================================
                 * Annual Functional
                 * =========================================
                 */

                if (
                        sourceType
                                == LifeDomainEvidenceSourceType
                                .ANNUAL_FUNCTIONAL_CONTEXT
                ) {

                    annualQualification =
                            AnnualActivityQualification
                                    .ANNUAL_FUNCTIONAL;

                    continue;
                }

                /*
                 * =========================================
                 * Annual Context
                 * =========================================
                 */

                if (
                        sourceType
                                == LifeDomainEvidenceSourceType
                                .ANNUAL_CONTEXT
                ) {

                    if (
                            annualQualification
                                    == AnnualActivityQualification.NONE
                    ) {

                        annualQualification =
                                AnnualActivityQualification
                                        .ANNUAL_CONTEXT;
                    }

                    continue;
                }

                /*
                 * =========================================
                 * Monthly
                 * =========================================
                 */

                if (
                        sourceType
                                == LifeDomainEvidenceSourceType
                                .MONTHLY_CONTEXT
                ) {

                    monthlyAnchors.add(
                            reference.sourceKey()
                    );
                }
            }
        }

        LifeDomainSignalStatus domainStatus =
                resolveDomainStatus(
                        supportiveExists,
                        pressureExists,
                        mixedExists,
                        contextualExists
                );

        boolean multiMonthActivity =
                monthlyAnchors.size()
                        >= 2;

        boolean directionalActivity =
                domainStatus
                        != LifeDomainSignalStatus.CONTEXTUAL;

        return new ActiveDomainCandidate(
                domain,
                domainStatus,
                annualQualification,
                multiMonthActivity,
                directionalActivity,
                List.copyOf(
                        temporalMatches
                ),
                Set.copyOf(
                        monthlyAnchors
                )
        );
    }


    /*
     * =================================================
     * Domain Status
     * =================================================
     */

    private LifeDomainSignalStatus resolveDomainStatus(

            boolean supportiveExists,

            boolean pressureExists,

            boolean mixedExists,

            boolean contextualExists

    ) {

        if (
                mixedExists
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                pressureExists
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        if (
                supportiveExists
        ) {

            return LifeDomainSignalStatus
                    .SUPPORTIVE;
        }

        if (
                contextualExists
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        throw new IllegalStateException(
                flowLabel()
                        + " Active Domain에 Temporal Match가 존재하지만 Domain Status를 결정할 수 없습니다."
        );
    }


    /*
     * =================================================
     * Dominance Selection
     * =================================================
     */

    private ActiveDomainCandidate
    selectUniqueDominantCandidate(

            List<ActiveDomainCandidate> candidates

    ) {

        List<ActiveDomainCandidate> dominantCandidates =
                new ArrayList<>();

        for (
                ActiveDomainCandidate candidate
                : candidates
        ) {

            boolean dominatesAllOthers =
                    true;

            for (
                    ActiveDomainCandidate other
                    : candidates
            ) {

                if (
                        candidate == other
                ) {

                    continue;
                }

                if (
                        !dominates(
                                candidate,
                                other
                        )
                ) {

                    dominatesAllOthers =
                            false;

                    break;
                }
            }

            if (
                    dominatesAllOthers
            ) {

                dominantCandidates.add(
                        candidate
                );
            }
        }

        if (
                dominantCandidates.size()
                        != 1
        ) {

            return null;
        }

        return dominantCandidates.get(
                0
        );
    }


    /*
     * =================================================
     * Dominates
     * =================================================
     *
     * 점수를 더하지 않는다.
     *
     * 모든 자격축에서 낮지 않고
     * 최소 한 축에서 엄격히 높아야 한다.
     */

    private boolean dominates(

            ActiveDomainCandidate first,

            ActiveDomainCandidate second

    ) {

        int firstAnnualLevel =
                first
                        .annualQualification()
                        .level();

        int secondAnnualLevel =
                second
                        .annualQualification()
                        .level();

        int firstMonthlyLevel =
                booleanLevel(
                        first.multiMonthActivity()
                );

        int secondMonthlyLevel =
                booleanLevel(
                        second.multiMonthActivity()
                );

        int firstDirectionalLevel =
                booleanLevel(
                        first.directionalActivity()
                );

        int secondDirectionalLevel =
                booleanLevel(
                        second.directionalActivity()
                );

        boolean annualNotLower =
                firstAnnualLevel
                        >= secondAnnualLevel;

        boolean monthlyNotLower =
                firstMonthlyLevel
                        >= secondMonthlyLevel;

        boolean directionalNotLower =
                firstDirectionalLevel
                        >= secondDirectionalLevel;

        if (
                !annualNotLower
                        || !monthlyNotLower
                        || !directionalNotLower
        ) {

            return false;
        }

        boolean annualStrictlyHigher =
                firstAnnualLevel
                        > secondAnnualLevel;

        boolean monthlyStrictlyHigher =
                firstMonthlyLevel
                        > secondMonthlyLevel;

        boolean directionalStrictlyHigher =
                firstDirectionalLevel
                        > secondDirectionalLevel;

        return annualStrictlyHigher
                || monthlyStrictlyHigher
                || directionalStrictlyHigher;
    }


    private int booleanLevel(
            boolean value
    ) {

        return value
                ? 1
                : 0;
    }


    /*
     * =================================================
     * Reasons
     * =================================================
     */

    private List<String> buildReasons(

            ConsultationCategoryTimelineYearEvidence yearEvidence,

            List<ActiveDomainCandidate> candidates,

            ActiveDomainCandidate selected

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                flowLabel()
                        + " PRIMARY_ACTIVE_DOMAIN은 Match 수, Rule 수, Source 수를 비교하지 않고 Annual qualification, multi-month persistence, directional activity의 구조적 자격만 비교합니다."
        );

        reasons.add(
                "Natal-only Match는 해당 명리연도의 Active Domain 후보로 사용하지 않고 ANNUAL_CONTEXT, ANNUAL_FUNCTIONAL_CONTEXT 또는 MONTHLY_CONTEXT가 실제 존재하는 Domain만 후보로 사용합니다."
        );

        reasons.add(
                "ANNUAL_CONTEXT와 ANNUAL_FUNCTIONAL_CONTEXT는 독립 Evidence 두 개로 합산하지 않으며 동일 Annual provenance의 qualification 계층으로 사용합니다."
        );

        reasons.add(
                "SUPPORTIVE, PRESSURE, MIXED는 PRIMARY_ACTIVE 비교에서 모두 방향성이 존재하는 상태이며 서로 우열을 두지 않습니다. CONTEXTUAL만 방향 미확정 상태로 구분합니다."
        );

        reasons.add(
                "서로 다른 최소 두 MONTHLY_CONTEXT sourceKey가 존재할 때만 multi-month activity를 인정하며 반복 Match 개수 자체는 강도로 사용하지 않습니다."
        );

        /*
         * =================================================
         * Candidate
         * =================================================
         */

        for (
                ActiveDomainCandidate candidate
                : candidates
        ) {

            reasons.add(
                    "Active 후보 "
                            + candidate.domain()
                            + "의 domainStatus="
                            + candidate.domainStatus()
                            + ", annualQualification="
                            + candidate.annualQualification()
                            + ", multiMonthActivity="
                            + candidate.multiMonthActivity()
                            + ", directionalActivity="
                            + candidate.directionalActivity()
                            + "입니다."
            );
        }

        /*
         * =================================================
         * Selected
         * =================================================
         */

        if (
                candidates.size()
                        == 1
        ) {

            reasons.add(
                    selected.domain()
                            + "은 해당 명리연도에 Temporal Activity 자격을 가진 유일한 LifeDomain이므로 PRIMARY_ACTIVE_DOMAIN으로 식별됩니다."
            );

        } else {

            reasons.add(
                    selected.domain()
                            + "은 다른 모든 Active 후보보다 Annual qualification, multi-month persistence, directional activity에서 낮지 않고 최소 하나의 자격축에서 더 높아 PRIMARY_ACTIVE_DOMAIN으로 식별됩니다."
            );
        }

        /*
         * =================================================
         * Selected Direction
         * =================================================
         */

        switch (
                selected.domainStatus()
        ) {

            case SUPPORTIVE ->
                    reasons.add(
                            "선택된 PRIMARY_ACTIVE_DOMAIN의 deterministic 방향은 SUPPORTIVE이며 이는 해당 Domain이 가장 많이 발생했다는 뜻이 아니라 Temporal qualification상 주된 작동축으로 식별되었다는 뜻입니다."
                    );

            case PRESSURE ->
                    reasons.add(
                            "선택된 PRIMARY_ACTIVE_DOMAIN의 deterministic 방향은 PRESSURE이며 이는 사건 실패를 의미하지 않고 해당 Domain이 Temporal qualification상 주된 부담성 작동축으로 식별되었다는 뜻입니다."
                    );

            case MIXED ->
                    reasons.add(
                            "선택된 PRIMARY_ACTIVE_DOMAIN에는 SUPPORTIVE/PRESSURE 공존 또는 명시적 MIXED 구조가 있어 서로 다른 작동 방향을 함께 보존합니다."
                    );

            case CONTEXTUAL ->
                    reasons.add(
                            "선택된 PRIMARY_ACTIVE_DOMAIN은 시간적 작동 자격은 가장 분명하지만 방향 자체는 CONTEXTUAL이므로 유리·불리를 확정하지 않습니다."
                    );
        }

        /*
         * =================================================
         * Monthly
         * =================================================
         */

        if (
                selected.multiMonthActivity()
        ) {

            reasons.add(
                    "선택된 Domain은 둘 이상의 서로 다른 MONTHLY_CONTEXT Anchor에서 실제 Timeline Activity가 확인되어 하나의 대표 월에만 국한되지 않습니다."
            );

        } else {

            reasons.add(
                    "선택된 Domain에는 multi-month activity 자격이 없으며 Primary 식별은 다른 자격축의 구조적 우위 또는 유일한 Temporal Domain이라는 조건에 의해 이루어졌습니다."
            );
        }

        /*
         * =================================================
         * Timeline
         * =================================================
         */

        reasons.add(
                "해당 명리연도의 실제 Timeline monthIndex 목록 "
                        + yearEvidence.monthIndices()
                        + "을 그대로 보존하며 대표 월을 임의 선택하지 않습니다."
        );

        if (
                yearEvidence
                        .timelineUncertaintyExists()
        ) {

            reasons.add(
                    "해당 명리연도에는 Timeline uncertainty가 존재하며 PRIMARY_ACTIVE_DOMAIN을 단일 사건이나 확정된 시간 결과로 변환하지 않습니다."
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
            ActiveDomainCandidate selected
    ) {

        return "해당 명리연도에는 "
                + selected.domain()
                + "이 다른 생활영역보다 시간적 작동 자격이 구조적으로 분명한 PRIMARY_ACTIVE_DOMAIN이며, 그 방향은 "
                + selected.domainStatus()
                + "입니다.";
    }


    /*
     * =================================================
     * Annual Qualification
     * =================================================
     */

    private enum AnnualActivityQualification {

        NONE(
                0
        ),

        ANNUAL_CONTEXT(
                1
        ),

        ANNUAL_FUNCTIONAL(
                2
        );

        private final int level;

        AnnualActivityQualification(
                int level
        ) {

            this.level =
                    level;
        }

        private int level() {

            return level;
        }
    }


    /*
     * =================================================
     * Candidate
     * =================================================
     */

    private record ActiveDomainCandidate(

            LifeDomain domain,

            LifeDomainSignalStatus domainStatus,

            AnnualActivityQualification annualQualification,

            boolean multiMonthActivity,

            boolean directionalActivity,

            List<LifeDomainRuleMatch> temporalMatches,

            Set<String> monthlyAnchors

    ) {

        private ActiveDomainCandidate {

            Objects.requireNonNull(
                    domain,
                    "Active Domain Candidate에는 domain이 필요합니다."
            );

            Objects.requireNonNull(
                    domainStatus,
                    "Active Domain Candidate에는 domainStatus가 필요합니다."
            );

            Objects.requireNonNull(
                    annualQualification,
                    "Active Domain Candidate에는 annualQualification이 필요합니다."
            );

            temporalMatches =
                    temporalMatches == null
                            ? List.of()
                            : List.copyOf(
                            temporalMatches
                    );

            monthlyAnchors =
                    monthlyAnchors == null
                            ? Set.of()
                            : Set.copyOf(
                            monthlyAnchors
                    );

            if (
                    temporalMatches.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Active Domain Candidate에는 최소 하나의 Temporal Match가 필요합니다. domain="
                                + domain
                );
            }

            for (
                    LifeDomainRuleMatch match
                    : temporalMatches
            ) {

                Objects.requireNonNull(
                        match,
                        "Active Domain Candidate의 sourceMatch는 null일 수 없습니다."
                );

                if (
                        match.domain()
                                != domain
                ) {

                    throw new IllegalArgumentException(
                            "Active Domain Candidate의 sourceMatch Domain이 일치하지 않습니다. expected="
                                    + domain
                                    + ", actual="
                                    + match.domain()
                    );
                }
            }

            if (
                    directionalActivity
                            !=
                            (
                                    domainStatus
                                            != LifeDomainSignalStatus.CONTEXTUAL
                            )
            ) {

                throw new IllegalArgumentException(
                        "Active Domain Candidate의 directionalActivity와 domainStatus가 일치하지 않습니다. domain="
                                + domain
                                + ", status="
                                + domainStatus
                );
            }
        }
    }
}