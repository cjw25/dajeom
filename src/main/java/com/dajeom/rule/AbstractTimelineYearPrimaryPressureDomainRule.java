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
 * TIMELINE_YEAR PRIMARY_PRESSURE_DOMAIN
 * 공통 deterministic Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * 한 명리연도에 여러 LifeDomain의
 * PRESSURE가 동시에 존재할 수 있다.
 *
 * 이때:
 *
 * - Match 개수
 * - Rule 개수
 * - Evidence 개수
 *
 * 로 가장 큰 Domain을 선택하지 않는다.
 *
 * =================================================
 *
 * 예:
 *
 * RELATIONSHIP PRESSURE Match 20개
 * CAREER PRESSURE Match 3개
 *
 * ->
 *
 * RELATIONSHIP이 Primary라는 뜻이 아니다.
 *
 * Domain마다 Rule 구조와 Match 발생 방식이
 * 서로 다르기 때문이다.
 *
 * =================================================
 *
 * 이 Rule은 다음 자격만 본다.
 *
 * 1.
 * 명시적 PRESSURE가 존재하는가
 *
 * 2.
 * 그 PRESSURE에
 *    ANNUAL_CONTEXT가 존재하는가
 *
 * 3.
 * 그 PRESSURE에
 *    ANNUAL_FUNCTIONAL_CONTEXT가 존재하는가
 *
 * 4.
 * 서로 다른 둘 이상의
 *    MONTHLY_CONTEXT Anchor에서
 *    PRESSURE가 확인되는가
 *
 * =================================================
 *
 * Annual Provenance
 * =================================================
 *
 * ANNUAL_CONTEXT
 *
 * 와
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * 를 독립 근거 2개로 더하지 않는다.
 *
 * Annual qualification은 하나의 계층으로 본다.
 *
 * NONE
 * <
 * ANNUAL_CONTEXT
 * <
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * =================================================
 *
 * ANNUAL_FUNCTIONAL_CONTEXT가 더 높은 이유:
 *
 * 독립 Evidence가 하나 더 있다는 뜻이 아니다.
 *
 * 동일 세운 provenance를
 * Natal / Decade / Annual Functional /
 * CrossFunctional / Qualification까지
 * 더 구조적으로 해석한 결과라는 뜻이다.
 *
 * 즉:
 *
 * "근거 수 증가"
 *
 * 가 아니라
 *
 * "같은 provenance의 해석 정밀도 증가"
 *
 * 다.
 *
 * =================================================
 *
 * Monthly Persistence
 * =================================================
 *
 * 같은 PRESSURE가
 * 서로 다른 최소 2개의
 * MONTHLY_CONTEXT sourceKey에서 존재하면
 *
 * multiMonthPressure=true.
 *
 * 이것은:
 *
 * Match 2개니까 두 배 강하다
 *
 * 라는 뜻이 아니다.
 *
 * 단지:
 *
 * "하나의 월 Anchor에만 국한되지 않았다"
 *
 * 는 시간적 지속 조건이다.
 *
 * =================================================
 *
 * Primary 선택
 * =================================================
 *
 * 점수를 더하지 않는다.
 *
 * Candidate A가 Candidate B보다:
 *
 * - Annual qualification이 같거나 높고
 * - Multi-month qualification도 같거나 높고
 * - 그중 하나 이상이 엄격하게 높으면
 *
 * A가 B를 dominates 한다.
 *
 * =================================================
 *
 * 하나의 Candidate가
 * 다른 모든 Candidate를 지배할 때만
 *
 * PRIMARY_PRESSURE_DOMAIN을 생성한다.
 *
 * =================================================
 *
 * 예:
 *
 * A:
 * ANNUAL_FUNCTIONAL
 * MULTI_MONTH
 *
 * B:
 * ANNUAL_CONTEXT
 * ONE_MONTH
 *
 * ->
 * A selected
 *
 * =================================================
 *
 * 예:
 *
 * A:
 * ANNUAL_FUNCTIONAL
 * ONE_MONTH
 *
 * B:
 * ANNUAL_CONTEXT
 * MULTI_MONTH
 *
 * ->
 * 서로 incomparable
 * ->
 * Primary 생성 안 함
 *
 * =================================================
 *
 * 예:
 *
 * A:
 * ANNUAL_FUNCTIONAL
 * MULTI_MONTH
 *
 * B:
 * ANNUAL_FUNCTIONAL
 * MULTI_MONTH
 *
 * ->
 * 동률
 * ->
 * Primary 생성 안 함
 *
 * =================================================
 *
 * MIXED
 * =================================================
 *
 * MIXED 자체만으로는
 * PRIMARY_PRESSURE_DOMAIN 후보로 올리지 않는다.
 *
 * 이유:
 *
 * MIXED에는 실제 Pressure 방향이 포함될 수 있지만
 * 현재 LifeDomainRuleMatch API만으로
 * 그 MIXED 안의 어느 source가
 * Pressure 축을 만들었는지
 * 구조적으로 분리할 수 없는 경우가 있기 때문이다.
 *
 * 따라서 이 Rule은
 * 명시적 PRESSURE Match만 사용한다.
 *
 * =================================================
 *
 * CONTEXTUAL
 *
 * 역시 Primary Pressure 후보가 아니다.
 *
 * =================================================
 *
 * SUPPORTIVE
 *
 * 역시 Primary Pressure 후보가 아니다.
 *
 * =================================================
 *
 * Timeline uncertainty
 * =================================================
 *
 * 연도 전체에 uncertainty가 있다는 이유만으로
 * Primary 선택을 강제로 삭제하지 않는다.
 *
 * 단 uncertainty 자체는 reasons에 보존한다.
 *
 * 실제 후보들이 서로 다른 자격을 보여
 * 하나를 확정할 수 없으면
 * 애초에 Primary가 생성되지 않는다.
 *
 * =================================================
 *
 * 이 Rule이 하지 않는 것
 * =================================================
 *
 * - 가장 많은 Match 선택
 * - 가장 많은 월 선택
 * - Domain 우선순위 하드코딩
 * - WEALTH > CAREER 같은 고정 순위
 * - PAST/FUTURE 다른 계산식 사용
 * - 사건 발생 예측
 * - 성공/실패 판단
 * - 실제 과거 사건 역추정
 */
public abstract class AbstractTimelineYearPrimaryPressureDomainRule
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
                        + " PRIMARY_PRESSURE_DOMAIN에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != category()
        ) {

            throw new IllegalArgumentException(
                    flowLabel()
                            + " PRIMARY_PRESSURE_DOMAIN Rule은 "
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
                        + " PRIMARY_PRESSURE_DOMAIN에는 Timeline Year Evidence가 필요합니다."
        );

        List<LifeDomainRuleMatch> yearMatches =
                yearEvidence.matches();

        if (
                yearMatches == null
                        || yearMatches.isEmpty()
        ) {

            return null;
        }

        Map<LifeDomain, List<LifeDomainRuleMatch>>
                pressureMatchesByDomain =
                groupPressureMatches(
                        yearMatches
                );

        if (
                pressureMatchesByDomain.isEmpty()
        ) {

            /*
             * 명시적 Pressure가 없는 연도.
             *
             * PRIMARY_PRESSURE_DOMAIN 자체가 없다.
             */
            return null;
        }

        List<PressureDomainCandidate> candidates =
                new ArrayList<>();

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            List<LifeDomainRuleMatch> pressureMatches =
                    pressureMatchesByDomain
                            .getOrDefault(
                                    domain,
                                    List.of()
                            );

            if (
                    pressureMatches.isEmpty()
            ) {

                continue;
            }

            candidates.add(
                    buildCandidate(
                            domain,
                            pressureMatches
                    )
            );
        }

        if (
                candidates.isEmpty()
        ) {

            return null;
        }

        /*
         * =================================================
         * 단 하나의 Pressure Domain
         * =================================================
         *
         * 비교 대상이 없으므로
         * 그 Domain이 자연스럽게 Primary다.
         */

        PressureDomainCandidate selected;

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
             * =================================================
             * 명확한 우위 없음
             * =================================================
             *
             * tie-break를 만들지 않는다.
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
                ConsultationCategoryDeterministicStatus.PRESSURE,
                yearEvidence.sajuYear(),
                null,
                yearEvidence.monthIndices(),
                primarySummary(
                        selected.domain()
                ),
                selected.pressureMatches(),
                reasons
        );
    }


    /*
     * =================================================
     * Pressure Group
     * =================================================
     */

    private Map<
            LifeDomain,
            List<LifeDomainRuleMatch>
            > groupPressureMatches(

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
                    normalized.status()
                            != LifeDomainSignalStatus.PRESSURE
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
     * Candidate
     * =================================================
     */

    private PressureDomainCandidate buildCandidate(

            LifeDomain domain,

            List<LifeDomainRuleMatch> pressureMatches

    ) {

        Objects.requireNonNull(
                domain,
                "Pressure Domain Candidate에는 LifeDomain이 필요합니다."
        );

        Objects.requireNonNull(
                pressureMatches,
                "Pressure Domain Candidate에는 Pressure Match 목록이 필요합니다."
        );

        if (
                pressureMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Pressure Domain Candidate에는 최소 하나의 PRESSURE Match가 필요합니다. domain="
                            + domain
            );
        }

        AnnualPressureQualification annualQualification =
                AnnualPressureQualification.NONE;

        Set<String> monthlyPressureAnchors =
                new LinkedHashSet<>();

        for (
                LifeDomainRuleMatch match
                : pressureMatches
        ) {

            if (
                    match.domain()
                            != domain
            ) {

                throw new IllegalArgumentException(
                        "Pressure Candidate의 Domain과 Source Match Domain이 일치하지 않습니다. expected="
                                + domain
                                + ", actual="
                                + match.domain()
                );
            }

            if (
                    match.status()
                            != LifeDomainSignalStatus.PRESSURE
            ) {

                throw new IllegalArgumentException(
                        "Pressure Candidate에는 PRESSURE Match만 사용할 수 있습니다. domain="
                                + domain
                                + ", status="
                                + match.status()
                );
            }

            for (
                    LifeDomainEvidenceReference<?> reference
                    : match.evidences()
            ) {

                Objects.requireNonNull(
                        reference,
                        "Pressure Candidate Evidence는 null일 수 없습니다."
                );

                /*
                 * =========================================
                 * Annual Functional
                 * =========================================
                 *
                 * 독립 근거 +1이 아니라
                 * 동일 Annual provenance의
                 * 더 정밀한 qualification level.
                 */

                if (
                        reference.sourceType()
                                == LifeDomainEvidenceSourceType
                                .ANNUAL_FUNCTIONAL_CONTEXT
                ) {

                    annualQualification =
                            AnnualPressureQualification
                                    .ANNUAL_FUNCTIONAL;

                    continue;
                }

                /*
                 * =========================================
                 * Annual Context
                 * =========================================
                 */

                if (
                        reference.sourceType()
                                == LifeDomainEvidenceSourceType
                                .ANNUAL_CONTEXT
                ) {

                    if (
                            annualQualification
                                    == AnnualPressureQualification.NONE
                    ) {

                        annualQualification =
                                AnnualPressureQualification
                                        .ANNUAL_CONTEXT;
                    }

                    continue;
                }

                /*
                 * =========================================
                 * Monthly Context
                 * =========================================
                 *
                 * 동일 sourceKey 반복은 Set으로 제거.
                 */

                if (
                        reference.sourceType()
                                == LifeDomainEvidenceSourceType
                                .MONTHLY_CONTEXT
                ) {

                    monthlyPressureAnchors.add(
                            reference.sourceKey()
                    );
                }
            }
        }

        boolean multiMonthPressure =
                monthlyPressureAnchors.size()
                        >= 2;

        return new PressureDomainCandidate(
                domain,
                annualQualification,
                multiMonthPressure,
                List.copyOf(
                        pressureMatches
                ),
                Set.copyOf(
                        monthlyPressureAnchors
                )
        );
    }


    /*
     * =================================================
     * Dominance Selection
     * =================================================
     *
     * 점수 합산 없음.
     *
     * 하나의 후보가
     * 다른 모든 후보를 구조적으로 지배해야 한다.
     */

    private PressureDomainCandidate
    selectUniqueDominantCandidate(

            List<PressureDomainCandidate> candidates

    ) {

        List<PressureDomainCandidate> dominantCandidates =
                new ArrayList<>();

        for (
                PressureDomainCandidate candidate
                : candidates
        ) {

            boolean dominatesAllOthers =
                    true;

            for (
                    PressureDomainCandidate other
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

        /*
         * =================================================
         * 정확히 하나일 때만 선택
         * =================================================
         */

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
     * A >= B on every qualification axis
     * +
     * A > B on at least one axis.
     */

    private boolean dominates(

            PressureDomainCandidate first,

            PressureDomainCandidate second

    ) {

        int firstAnnualLevel =
                first
                        .annualQualification()
                        .level();

        int secondAnnualLevel =
                second
                        .annualQualification()
                        .level();

        boolean annualNotLower =
                firstAnnualLevel
                        >= secondAnnualLevel;

        boolean monthlyNotLower =
                booleanLevel(
                        first.multiMonthPressure()
                )
                        >=
                        booleanLevel(
                                second.multiMonthPressure()
                        );

        if (
                !annualNotLower
                        || !monthlyNotLower
        ) {

            return false;
        }

        boolean annualStrictlyHigher =
                firstAnnualLevel
                        > secondAnnualLevel;

        boolean monthlyStrictlyHigher =
                booleanLevel(
                        first.multiMonthPressure()
                )
                        >
                        booleanLevel(
                                second.multiMonthPressure()
                        );

        return annualStrictlyHigher
                || monthlyStrictlyHigher;
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

            List<PressureDomainCandidate> candidates,

            PressureDomainCandidate selected

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                flowLabel()
                        + " PRIMARY_PRESSURE_DOMAIN은 LifeDomain별 Match 개수나 Rule 개수를 비교하지 않고 명시적 PRESSURE가 가진 Annual qualification과 multi-month 지속 조건만 구조적으로 비교합니다."
        );

        reasons.add(
                "ANNUAL_CONTEXT와 ANNUAL_FUNCTIONAL_CONTEXT는 독립 근거 두 개로 합산하지 않고 동일 Annual provenance의 qualification level로 사용합니다."
        );

        reasons.add(
                "ANNUAL_FUNCTIONAL_CONTEXT는 동일 세운 provenance의 더 정밀한 Functional/CrossFunctional 해석이라는 이유로 ANNUAL_CONTEXT보다 높은 qualification level로 분류하며 Evidence 개수 가중치는 부여하지 않습니다."
        );

        reasons.add(
                "서로 다른 최소 두 MONTHLY_CONTEXT sourceKey에서 PRESSURE가 존재하는 경우에만 multi-month pressure 조건을 인정하며 반복 Match 개수 자체는 사용하지 않습니다."
        );

        /*
         * =================================================
         * Candidate 설명
         * =================================================
         */

        for (
                PressureDomainCandidate candidate
                : candidates
        ) {

            reasons.add(
                    "Pressure 후보 "
                            + candidate.domain()
                            + "의 Annual qualification="
                            + candidate.annualQualification()
                            + ", multiMonthPressure="
                            + candidate.multiMonthPressure()
                            + "입니다."
            );
        }

        /*
         * =================================================
         * Selected
         * =================================================
         */

        reasons.add(
                selected.domain()
                        + "은 다른 모든 PRESSURE 후보보다 Annual qualification과 multi-month 조건에서 낮지 않고 최소 하나의 자격축에서 더 높아 해당 연도의 PRIMARY_PRESSURE_DOMAIN으로 선택되었습니다."
        );

        reasons.add(
                "선택된 Domain의 명시적 PRESSURE Match만 sourceMatches로 보존하며 CONTEXTUAL/MIXED/SUPPORTIVE Match를 Primary Pressure 근거로 승격하지 않습니다."
        );

        /*
         * =================================================
         * Monthly
         * =================================================
         */

        if (
                selected.multiMonthPressure()
        ) {

            reasons.add(
                    "선택된 Domain의 PRESSURE는 둘 이상의 서로 다른 MONTHLY_CONTEXT Anchor에서 확인되어 하나의 월에만 국한되지 않는 시간적 지속 조건이 존재합니다."
            );

        } else {

            reasons.add(
                    "선택된 Domain에는 multi-month PRESSURE 자격이 없으며 Primary 선택은 다른 Domain보다 높은 Annual qualification 또는 유일한 PRESSURE Domain이라는 구조에 의해 이루어졌습니다."
            );
        }

        /*
         * =================================================
         * Timeline Coverage
         * =================================================
         */

        reasons.add(
                "해당 명리연도의 실제 Timeline monthIndex 목록 "
                        + yearEvidence.monthIndices()
                        + "을 그대로 보존하며 월 번호를 새로 보정하지 않습니다."
        );

        if (
                yearEvidence.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "해당 명리연도에는 Timeline uncertainty가 존재합니다. 이 불확실성을 삭제하지 않으며 PRIMARY_PRESSURE_DOMAIN을 사건 발생 확정이나 단일 미래/과거 시나리오로 해석하지 않습니다."
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

    private String primarySummary(
            LifeDomain domain
    ) {

        return "해당 명리연도의 명시적 부담 방향 가운데 "
                + domain
                + "이 다른 PRESSURE Domain보다 구조적으로 우세한 자격을 가져 주된 부담축으로 식별됩니다.";
    }


    /*
     * =================================================
     * Internal Annual Qualification
     * =================================================
     *
     * 점수가 아니다.
     *
     * 동일 Annual provenance의
     * 해석 정밀도 계층이다.
     */

    private enum AnnualPressureQualification {

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

        AnnualPressureQualification(
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

    private record PressureDomainCandidate(

            LifeDomain domain,

            AnnualPressureQualification annualQualification,

            boolean multiMonthPressure,

            List<LifeDomainRuleMatch> pressureMatches,

            Set<String> monthlyPressureAnchors

    ) {

        private PressureDomainCandidate {

            Objects.requireNonNull(
                    domain,
                    "Pressure Domain Candidate에는 domain이 필요합니다."
            );

            Objects.requireNonNull(
                    annualQualification,
                    "Pressure Domain Candidate에는 Annual qualification이 필요합니다."
            );

            pressureMatches =
                    pressureMatches == null
                            ? List.of()
                            : List.copyOf(
                            pressureMatches
                    );

            monthlyPressureAnchors =
                    monthlyPressureAnchors == null
                            ? Set.of()
                            : Set.copyOf(
                            monthlyPressureAnchors
                    );

            if (
                    pressureMatches.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Pressure Domain Candidate에는 최소 하나의 PRESSURE Match가 필요합니다. domain="
                                + domain
                );
            }

            for (
                    LifeDomainRuleMatch match
                    : pressureMatches
            ) {

                Objects.requireNonNull(
                        match,
                        "Pressure Domain Candidate의 sourceMatch는 null일 수 없습니다."
                );

                if (
                        match.domain()
                                != domain
                ) {

                    throw new IllegalArgumentException(
                            "Pressure Domain Candidate의 sourceMatch Domain이 일치하지 않습니다. expected="
                                    + domain
                                    + ", actual="
                                    + match.domain()
                    );
                }

                if (
                        match.status()
                                != LifeDomainSignalStatus.PRESSURE
                ) {

                    throw new IllegalArgumentException(
                            "Pressure Domain Candidate에는 PRESSURE Match만 포함할 수 있습니다. domain="
                                    + domain
                                    + ", status="
                                    + match.status()
                    );
                }
            }
        }
    }
}