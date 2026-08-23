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
 * TIMELINE_YEAR PRIMARY_PRESSURE_DOMAIN 공통 deterministic Rule.
 *
 * 핵심 원칙:
 *
 * - 명시적 PRESSURE Match만 사용한다.
 * - Natal-only PRESSURE는 연도 주된 부담축으로 승격하지 않는다.
 * - Match/Rule/Evidence 개수를 점수화하지 않는다.
 * - Annual qualification은 동일 Annual provenance의 정밀도 계층이다.
 * - MONTHLY_CONTEXT와 MONTHLY_FUNCTIONAL_CONTEXT는 같은 월이면
 *   동일 MONTHLY_CONTEXT_FAMILY anchor로 정규화한다.
 * - 같은 월의 raw + functional PRESSURE를 두 달 지속으로 세지 않는다.
 * - 서로 다른 최소 2개 월 family에서 PRESSURE가 있을 때만
 *   multiMonthPressure=true다.
 * - 하나의 후보가 다른 모든 후보를 Pareto 방식으로 지배할 때만
 *   PRIMARY_PRESSURE_DOMAIN을 만든다.
 * - MIXED / CONTEXTUAL / SUPPORTIVE는 이 Rule의 Pressure 후보가 아니다.
 * - 사건 발생, 성공/실패, 실제 과거 사건을 추론하지 않는다.
 */
public abstract class AbstractTimelineYearPrimaryPressureDomainRule
        implements ConsultationCategoryDeterministicRule {

    @Override
    public abstract ConsultationCategory category();

    @Override
    public abstract String code();

    protected abstract String flowLabel();

    protected abstract void appendTemporalReason(List<String> reasons);

    @Override
    public final List<ConsultationCategoryDeterministicMatch> evaluate(
            ConsultationCategoryEvidenceView view
    ) {
        Objects.requireNonNull(
                view,
                flowLabel() + " PRIMARY_PRESSURE_DOMAIN에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (view.category() != category()) {
            throw new IllegalArgumentException(
                    flowLabel()
                            + " PRIMARY_PRESSURE_DOMAIN Rule은 "
                            + category()
                            + " View만 평가할 수 있습니다. actual="
                            + view.category()
            );
        }

        if (view.unavailable() || !view.hasTimelineYearEvidences()) {
            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (ConsultationCategoryTimelineYearEvidence yearEvidence
                : view.timelineYearEvidences()) {

            ConsultationCategoryDeterministicMatch match =
                    evaluateYear(yearEvidence);

            if (match != null) {
                result.add(match);
            }
        }

        return List.copyOf(result);
    }

    private ConsultationCategoryDeterministicMatch evaluateYear(
            ConsultationCategoryTimelineYearEvidence yearEvidence
    ) {
        Objects.requireNonNull(
                yearEvidence,
                flowLabel() + " PRIMARY_PRESSURE_DOMAIN에는 Timeline Year Evidence가 필요합니다."
        );

        List<LifeDomainRuleMatch> yearMatches =
                yearEvidence.matches();

        if (yearMatches == null || yearMatches.isEmpty()) {
            return null;
        }

        Map<LifeDomain, List<LifeDomainRuleMatch>> pressureMatchesByDomain =
                groupPressureMatches(yearMatches);

        if (pressureMatchesByDomain.isEmpty()) {
            return null;
        }

        List<PressureDomainCandidate> candidates =
                new ArrayList<>();

        for (LifeDomain domain : LifeDomain.values()) {
            List<LifeDomainRuleMatch> pressureMatches =
                    pressureMatchesByDomain.getOrDefault(
                            domain,
                            List.of()
                    );

            if (!pressureMatches.isEmpty()) {
                candidates.add(
                        buildCandidate(
                                domain,
                                pressureMatches
                        )
                );
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        PressureDomainCandidate selected =
                candidates.size() == 1
                        ? candidates.get(0)
                        : selectUniqueDominantCandidate(candidates);

        if (selected == null) {
            return null;
        }

        return new ConsultationCategoryDeterministicMatch(
                category(),
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                code(),
                ConsultationCategoryDeterministicStatus.PRESSURE,
                yearEvidence.sajuYear(),
                null,
                yearEvidence.monthIndices(),
                primarySummary(selected.domain()),
                selected.pressureMatches(),
                buildReasons(
                        yearEvidence,
                        candidates,
                        selected
                )
        );
    }

    private Map<LifeDomain, List<LifeDomainRuleMatch>> groupPressureMatches(
            List<LifeDomainRuleMatch> matches
    ) {
        Map<LifeDomain, List<LifeDomainRuleMatch>> mutable =
                new EnumMap<>(LifeDomain.class);

        for (LifeDomainRuleMatch match : matches) {
            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            flowLabel() + " Timeline Year Match는 null일 수 없습니다."
                    );

            if (normalized.status() != LifeDomainSignalStatus.PRESSURE) {
                continue;
            }

            /*
             * Timeline Year의 주된 부담축은
             * 실제 시간축 Source를 가진 PRESSURE만 사용한다.
             */
            if (!pressureTemporalActivityExists(normalized)) {
                continue;
            }

            mutable
                    .computeIfAbsent(
                            normalized.domain(),
                            ignored -> new ArrayList<>()
                    )
                    .add(normalized);
        }

        Map<LifeDomain, List<LifeDomainRuleMatch>> result =
                new EnumMap<>(LifeDomain.class);

        for (Map.Entry<LifeDomain, List<LifeDomainRuleMatch>> entry
                : mutable.entrySet()) {

            result.put(
                    entry.getKey(),
                    List.copyOf(entry.getValue())
            );
        }

        return result;
    }

    private boolean pressureTemporalActivityExists(
            LifeDomainRuleMatch match
    ) {
        Objects.requireNonNull(
                match,
                "Pressure Temporal Activity에는 LifeDomainRuleMatch가 필요합니다."
        );

        for (LifeDomainEvidenceReference<?> reference
                : match.evidences()) {

            Objects.requireNonNull(
                    reference,
                    "Pressure Temporal Activity Evidence는 null일 수 없습니다."
            );

            LifeDomainEvidenceSourceType sourceType =
                    reference.sourceType();

            if (sourceType == LifeDomainEvidenceSourceType.ANNUAL_CONTEXT
                    || sourceType == LifeDomainEvidenceSourceType.ANNUAL_FUNCTIONAL_CONTEXT
                    || sourceType == LifeDomainEvidenceSourceType.MONTHLY_CONTEXT
                    || sourceType == LifeDomainEvidenceSourceType.MONTHLY_FUNCTIONAL_CONTEXT) {

                return true;
            }
        }

        return false;
    }

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

        if (pressureMatches.isEmpty()) {
            throw new IllegalArgumentException(
                    "Pressure Domain Candidate에는 최소 하나의 PRESSURE Match가 필요합니다. domain="
                            + domain
            );
        }

        AnnualPressureQualification annualQualification =
                AnnualPressureQualification.NONE;

        Set<String> monthlyPressureAnchors =
                new LinkedHashSet<>();

        for (LifeDomainRuleMatch match : pressureMatches) {
            if (match.domain() != domain) {
                throw new IllegalArgumentException(
                        "Pressure Candidate의 Domain과 Source Match Domain이 일치하지 않습니다. expected="
                                + domain
                                + ", actual="
                                + match.domain()
                );
            }

            if (match.status() != LifeDomainSignalStatus.PRESSURE) {
                throw new IllegalArgumentException(
                        "Pressure Candidate에는 PRESSURE Match만 사용할 수 있습니다. domain="
                                + domain
                                + ", status="
                                + match.status()
                );
            }

            for (LifeDomainEvidenceReference<?> reference
                    : match.evidences()) {

                Objects.requireNonNull(
                        reference,
                        "Pressure Candidate Evidence는 null일 수 없습니다."
                );

                LifeDomainEvidenceSourceType sourceType =
                        reference.sourceType();

                /*
                 * 동일 Annual provenance의
                 * 가장 높은 qualification만 보존한다.
                 */
                if (sourceType == LifeDomainEvidenceSourceType.ANNUAL_FUNCTIONAL_CONTEXT) {
                    annualQualification =
                            AnnualPressureQualification.ANNUAL_FUNCTIONAL;

                    continue;
                }

                if (sourceType == LifeDomainEvidenceSourceType.ANNUAL_CONTEXT) {
                    if (annualQualification == AnnualPressureQualification.NONE) {
                        annualQualification =
                                AnnualPressureQualification.ANNUAL_CONTEXT;
                    }

                    continue;
                }

                /*
                 * MONTHLY_CONTEXT
                 * MONTHLY_FUNCTIONAL_CONTEXT
                 *
                 * 같은 월이면 같은 provenance family다.
                 */
                if (sourceType == LifeDomainEvidenceSourceType.MONTHLY_CONTEXT
                        || sourceType == LifeDomainEvidenceSourceType.MONTHLY_FUNCTIONAL_CONTEXT) {

                    monthlyPressureAnchors.add(
                            monthlyIndependentAnchor(reference)
                    );
                }
            }
        }

        return new PressureDomainCandidate(
                domain,
                annualQualification,
                monthlyPressureAnchors.size() >= 2,
                pressureMatches,
                monthlyPressureAnchors
        );
    }

    /**
     * MONTHLY:2026:7
     * MONTHLY_FUNCTIONAL:2026:7
     *
     * ->
     *
     * MONTHLY_CONTEXT_FAMILY:2026:7
     *
     * 예상하지 못한 key 형식은
     * 같은 provenance라고 추측하지 않는다.
     */
    private String monthlyIndependentAnchor(
            LifeDomainEvidenceReference<?> reference
    ) {
        Objects.requireNonNull(
                reference,
                "Monthly Independent Anchor 계산에는 Evidence Reference가 필요합니다."
        );

        LifeDomainEvidenceSourceType sourceType =
                reference.sourceType();

        String sourceKey =
                Objects.requireNonNull(
                        reference.sourceKey(),
                        "Monthly Pressure Evidence sourceKey는 null일 수 없습니다."
                );

        String expectedPrefix;

        if (sourceType == LifeDomainEvidenceSourceType.MONTHLY_CONTEXT) {
            expectedPrefix =
                    "MONTHLY:";

        } else if (sourceType == LifeDomainEvidenceSourceType.MONTHLY_FUNCTIONAL_CONTEXT) {
            expectedPrefix =
                    "MONTHLY_FUNCTIONAL:";

        } else {
            throw new IllegalArgumentException(
                    "Monthly Independent Anchor에는 MONTHLY_CONTEXT 또는 MONTHLY_FUNCTIONAL_CONTEXT만 사용할 수 있습니다. actual="
                            + sourceType
            );
        }

        if (sourceKey.startsWith(expectedPrefix)) {
            String anchor =
                    sourceKey.substring(
                            expectedPrefix.length()
                    );

            if (!anchor.isBlank()) {
                return "MONTHLY_CONTEXT_FAMILY:"
                        + anchor;
            }
        }

        /*
         * 알 수 없는 SourceKey는
         * 다른 provenance와 강제로 합치지 않는다.
         */
        return "UNNORMALIZED_MONTHLY:"
                + sourceType.name()
                + ":"
                + sourceKey;
    }

    private PressureDomainCandidate selectUniqueDominantCandidate(
            List<PressureDomainCandidate> candidates
    ) {
        List<PressureDomainCandidate> dominantCandidates =
                new ArrayList<>();

        for (PressureDomainCandidate candidate : candidates) {
            boolean dominatesAllOthers =
                    true;

            for (PressureDomainCandidate other : candidates) {
                if (candidate == other) {
                    continue;
                }

                if (!dominates(candidate, other)) {
                    dominatesAllOthers =
                            false;

                    break;
                }
            }

            if (dominatesAllOthers) {
                dominantCandidates.add(candidate);
            }
        }

        return dominantCandidates.size() == 1
                ? dominantCandidates.get(0)
                : null;
    }

    /**
     * 점수 합산이 아니다.
     *
     * 모든 자격축에서 낮지 않고,
     * 최소 하나의 축에서 엄격하게 높을 때만
     * 다른 Candidate를 지배한다.
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

        int firstMonthlyLevel =
                booleanLevel(
                        first.multiMonthPressure()
                );

        int secondMonthlyLevel =
                booleanLevel(
                        second.multiMonthPressure()
                );

        boolean notLower =
                firstAnnualLevel >= secondAnnualLevel
                        && firstMonthlyLevel >= secondMonthlyLevel;

        if (!notLower) {
            return false;
        }

        return firstAnnualLevel > secondAnnualLevel
                || firstMonthlyLevel > secondMonthlyLevel;
    }

    private int booleanLevel(
            boolean value
    ) {
        return value
                ? 1
                : 0;
    }

    private List<String> buildReasons(
            ConsultationCategoryTimelineYearEvidence yearEvidence,
            List<PressureDomainCandidate> candidates,
            PressureDomainCandidate selected
    ) {
        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                flowLabel()
                        + " PRIMARY_PRESSURE_DOMAIN은 LifeDomain별 Match 수나 Rule 수를 비교하지 않고 명시적 PRESSURE의 Annual qualification과 multi-month 지속 조건만 구조적으로 비교합니다."
        );

        reasons.add(
                "Natal-only PRESSURE는 해당 명리연도의 주된 부담축 후보로 승격하지 않고 ANNUAL_CONTEXT, ANNUAL_FUNCTIONAL_CONTEXT, MONTHLY_CONTEXT 또는 MONTHLY_FUNCTIONAL_CONTEXT가 실제 존재하는 PRESSURE만 사용합니다."
        );

        reasons.add(
                "ANNUAL_CONTEXT와 ANNUAL_FUNCTIONAL_CONTEXT는 독립 근거 두 개로 합산하지 않고 동일 Annual provenance의 qualification level로 사용합니다."
        );

        reasons.add(
                "ANNUAL_FUNCTIONAL_CONTEXT는 동일 세운 provenance의 더 정밀한 Functional/CrossFunctional 해석이라는 이유로 ANNUAL_CONTEXT보다 높은 qualification level로 분류하며 Evidence 개수 가중치는 부여하지 않습니다."
        );

        reasons.add(
                "MONTHLY_CONTEXT와 MONTHLY_FUNCTIONAL_CONTEXT는 같은 사주연도·같은 월이면 하나의 MONTHLY_CONTEXT_FAMILY Anchor로 정규화하며 서로 다른 최소 두 월 Anchor에서 PRESSURE가 존재하는 경우에만 multi-month pressure 조건을 인정합니다."
        );

        for (PressureDomainCandidate candidate : candidates) {
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

        if (candidates.size() == 1) {
            reasons.add(
                    selected.domain()
                            + "은 해당 명리연도에 명시적 Temporal PRESSURE를 가진 유일한 LifeDomain이므로 PRIMARY_PRESSURE_DOMAIN으로 식별됩니다."
            );

        } else {
            reasons.add(
                    selected.domain()
                            + "은 다른 모든 PRESSURE 후보보다 Annual qualification과 multi-month 조건에서 낮지 않고 최소 하나의 자격축에서 더 높아 해당 연도의 PRIMARY_PRESSURE_DOMAIN으로 선택되었습니다."
            );
        }

        reasons.add(
                "선택된 Domain의 명시적 PRESSURE Match만 sourceMatches로 보존하며 CONTEXTUAL/MIXED/SUPPORTIVE Match를 Primary Pressure 근거로 승격하지 않습니다."
        );

        if (selected.multiMonthPressure()) {
            reasons.add(
                    "선택된 Domain의 PRESSURE는 raw/functional 중복을 제거한 둘 이상의 서로 다른 MONTHLY_CONTEXT_FAMILY Anchor에서 확인되어 하나의 월에만 국한되지 않는 시간적 지속 조건이 존재합니다."
            );

        } else {
            reasons.add(
                    "선택된 Domain에는 multi-month PRESSURE 자격이 없으며 Primary 선택은 다른 Domain보다 높은 Annual qualification 또는 유일한 Temporal PRESSURE Domain이라는 구조에 의해 이루어졌습니다."
            );
        }

        reasons.add(
                "해당 명리연도의 실제 Timeline monthIndex 목록 "
                        + yearEvidence.monthIndices()
                        + "을 그대로 보존하며 월 번호를 새로 보정하지 않습니다."
        );

        if (yearEvidence.timelineUncertaintyExists()) {
            reasons.add(
                    "해당 명리연도에는 Timeline uncertainty가 존재합니다. 이 불확실성을 삭제하지 않으며 PRIMARY_PRESSURE_DOMAIN을 사건 발생 확정이나 단일 미래/과거 시나리오로 해석하지 않습니다."
            );
        }

        appendTemporalReason(reasons);

        return List.copyOf(reasons);
    }

    private String primarySummary(
            LifeDomain domain
    ) {
        return "해당 명리연도의 명시적 부담 방향 가운데 "
                + domain
                + "이 다른 PRESSURE Domain보다 구조적으로 우세한 자격을 가져 주된 부담축으로 식별됩니다.";
    }

    /**
     * 점수가 아니다.
     *
     * 동일 Annual provenance 안의
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

            if (pressureMatches.isEmpty()) {
                throw new IllegalArgumentException(
                        "Pressure Domain Candidate에는 최소 하나의 PRESSURE Match가 필요합니다. domain="
                                + domain
                );
            }

            for (LifeDomainRuleMatch match : pressureMatches) {
                Objects.requireNonNull(
                        match,
                        "Pressure Domain Candidate의 sourceMatch는 null일 수 없습니다."
                );

                if (match.domain() != domain) {
                    throw new IllegalArgumentException(
                            "Pressure Domain Candidate의 sourceMatch Domain이 일치하지 않습니다. expected="
                                    + domain
                                    + ", actual="
                                    + match.domain()
                    );
                }

                if (match.status() != LifeDomainSignalStatus.PRESSURE) {
                    throw new IllegalArgumentException(
                            "Pressure Domain Candidate의 sourceMatch는 PRESSURE여야 합니다. domain="
                                    + domain
                                    + ", actual="
                                    + match.status()
                    );
                }
            }
        }
    }
}