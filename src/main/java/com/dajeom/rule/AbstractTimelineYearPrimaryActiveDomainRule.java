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
 * PAST_LUCK / FUTURE_LUCK의 TIMELINE_YEAR PRIMARY_ACTIVE_DOMAIN 공통 Rule.
 *
 * 핵심 원칙:
 * - Match/Rule/Evidence 개수를 점수화하지 않는다.
 * - Annual qualification, multi-month persistence, directional activity만 비교한다.
 * - ANNUAL_CONTEXT와 ANNUAL_FUNCTIONAL_CONTEXT는 동일 Annual provenance의
 *   qualification 계층으로만 사용한다.
 * - MONTHLY_CONTEXT와 MONTHLY_FUNCTIONAL_CONTEXT는 같은 사주연도/월이면
 *   동일 MONTHLY_CONTEXT_FAMILY anchor로 정규화한다.
 * - SUPPORTIVE/PRESSURE/MIXED 사이에 우열을 두지 않는다.
 * - Natal-only Match로 연도 Active Domain을 만들지 않는다.
 */
public abstract class AbstractTimelineYearPrimaryActiveDomainRule
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
                flowLabel() + " PRIMARY_ACTIVE_DOMAIN에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (view.category() != category()) {
            throw new IllegalArgumentException(
                    flowLabel()
                            + " PRIMARY_ACTIVE_DOMAIN Rule은 "
                            + category()
                            + " View만 평가할 수 있습니다. actual="
                            + view.category()
            );
        }

        if (view.unavailable() || !view.hasTimelineYearEvidences()) {
            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result = new ArrayList<>();

        for (ConsultationCategoryTimelineYearEvidence yearEvidence : view.timelineYearEvidences()) {
            ConsultationCategoryDeterministicMatch match = evaluateYear(yearEvidence);

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
                flowLabel() + " PRIMARY_ACTIVE_DOMAIN에는 Timeline Year Evidence가 필요합니다."
        );

        List<LifeDomainRuleMatch> yearMatches = yearEvidence.matches();

        if (yearMatches == null || yearMatches.isEmpty()) {
            return null;
        }

        Map<LifeDomain, List<LifeDomainRuleMatch>> temporalMatchesByDomain =
                groupTemporalMatches(yearMatches);

        if (temporalMatchesByDomain.isEmpty()) {
            return null;
        }

        List<ActiveDomainCandidate> candidates = new ArrayList<>();

        for (LifeDomain domain : LifeDomain.values()) {
            List<LifeDomainRuleMatch> temporalMatches =
                    temporalMatchesByDomain.getOrDefault(
                            domain,
                            List.of()
                    );

            if (!temporalMatches.isEmpty()) {
                candidates.add(
                        buildCandidate(
                                domain,
                                temporalMatches
                        )
                );
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        ActiveDomainCandidate selected =
                candidates.size() == 1
                        ? candidates.get(0)
                        : selectUniqueDominantCandidate(
                        candidates
                );

        if (selected == null) {
            return null;
        }

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
                buildReasons(
                        yearEvidence,
                        candidates,
                        selected
                )
        );
    }

    private Map<LifeDomain, List<LifeDomainRuleMatch>> groupTemporalMatches(
            List<LifeDomainRuleMatch> matches
    ) {
        Map<LifeDomain, List<LifeDomainRuleMatch>> mutable =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (LifeDomainRuleMatch match : matches) {
            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            flowLabel()
                                    + " Timeline Year Match는 null일 수 없습니다."
                    );

            if (!temporalActivityExists(normalized)) {
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

        Map<LifeDomain, List<LifeDomainRuleMatch>> result =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (Map.Entry<LifeDomain, List<LifeDomainRuleMatch>> entry
                : mutable.entrySet()) {

            result.put(
                    entry.getKey(),
                    List.copyOf(
                            entry.getValue()
                    )
            );
        }

        return result;
    }

    private boolean temporalActivityExists(
            LifeDomainRuleMatch match
    ) {
        for (LifeDomainEvidenceReference<?> reference
                : match.evidences()) {

            Objects.requireNonNull(
                    reference,
                    "PRIMARY_ACTIVE_DOMAIN Evidence는 null일 수 없습니다."
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

        if (temporalMatches.isEmpty()) {
            throw new IllegalArgumentException(
                    "Active Domain Candidate에는 최소 하나의 Temporal Match가 필요합니다. domain="
                            + domain
            );
        }

        AnnualActivityQualification annualQualification =
                AnnualActivityQualification.NONE;

        Set<String> monthlyAnchors =
                new LinkedHashSet<>();

        boolean supportiveExists = false;
        boolean pressureExists = false;
        boolean mixedExists = false;
        boolean contextualExists = false;

        for (LifeDomainRuleMatch match
                : temporalMatches) {

            if (match.domain() != domain) {
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

            switch (status) {
                case SUPPORTIVE ->
                        supportiveExists = true;

                case PRESSURE ->
                        pressureExists = true;

                case MIXED ->
                        mixedExists = true;

                case CONTEXTUAL ->
                        contextualExists = true;
            }

            for (LifeDomainEvidenceReference<?> reference
                    : match.evidences()) {

                Objects.requireNonNull(
                        reference,
                        "Active Domain Candidate Evidence는 null일 수 없습니다."
                );

                LifeDomainEvidenceSourceType sourceType =
                        reference.sourceType();

                /*
                 * Annual Functional은
                 * 동일 Annual provenance의
                 * 더 정밀한 qualification.
                 */
                if (sourceType
                        == LifeDomainEvidenceSourceType.ANNUAL_FUNCTIONAL_CONTEXT) {

                    annualQualification =
                            AnnualActivityQualification.ANNUAL_FUNCTIONAL;

                    continue;
                }

                /*
                 * Annual Context.
                 *
                 * 이미 ANNUAL_FUNCTIONAL이면
                 * 낮은 단계로 되돌리지 않는다.
                 */
                if (sourceType
                        == LifeDomainEvidenceSourceType.ANNUAL_CONTEXT) {

                    if (annualQualification
                            == AnnualActivityQualification.NONE) {

                        annualQualification =
                                AnnualActivityQualification.ANNUAL_CONTEXT;
                    }

                    continue;
                }

                /*
                 * Monthly raw / derived.
                 *
                 * 같은 월은 같은 provenance family로
                 * 정규화한다.
                 */
                if (sourceType
                        == LifeDomainEvidenceSourceType.MONTHLY_CONTEXT

                        || sourceType
                        == LifeDomainEvidenceSourceType.MONTHLY_FUNCTIONAL_CONTEXT) {

                    monthlyAnchors.add(
                            monthlyIndependentAnchor(
                                    reference
                            )
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
                temporalMatches,
                monthlyAnchors
        );
    }

    /**
     * MONTHLY:2026:7
     * +
     * MONTHLY_FUNCTIONAL:2026:7
     *
     * ->
     *
     * MONTHLY_CONTEXT_FAMILY:2026:7
     *
     * 예상하지 못한 key 형식은
     * 서로 같은 provenance라고 추측하지 않는다.
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
                        "Monthly Evidence sourceKey는 null일 수 없습니다."
                );

        String expectedPrefix;

        if (sourceType
                == LifeDomainEvidenceSourceType.MONTHLY_CONTEXT) {

            expectedPrefix =
                    "MONTHLY:";

        } else if (sourceType
                == LifeDomainEvidenceSourceType.MONTHLY_FUNCTIONAL_CONTEXT) {

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
         * 모르는 provenance는
         * raw / functional끼리 억지로 합치지 않는다.
         */
        return "UNNORMALIZED_MONTHLY:"
                + sourceType.name()
                + ":"
                + sourceKey;
    }

    private LifeDomainSignalStatus resolveDomainStatus(
            boolean supportiveExists,
            boolean pressureExists,
            boolean mixedExists,
            boolean contextualExists
    ) {
        if (mixedExists) {
            return LifeDomainSignalStatus.MIXED;
        }

        if (supportiveExists
                && pressureExists) {

            return LifeDomainSignalStatus.MIXED;
        }

        if (pressureExists) {
            return LifeDomainSignalStatus.PRESSURE;
        }

        if (supportiveExists) {
            return LifeDomainSignalStatus.SUPPORTIVE;
        }

        if (contextualExists) {
            return LifeDomainSignalStatus.CONTEXTUAL;
        }

        throw new IllegalStateException(
                flowLabel()
                        + " Active Domain에 Temporal Match가 존재하지만 Domain Status를 결정할 수 없습니다."
        );
    }

    private ActiveDomainCandidate selectUniqueDominantCandidate(
            List<ActiveDomainCandidate> candidates
    ) {
        List<ActiveDomainCandidate> dominantCandidates =
                new ArrayList<>();

        for (ActiveDomainCandidate candidate
                : candidates) {

            boolean dominatesAllOthers =
                    true;

            for (ActiveDomainCandidate other
                    : candidates) {

                if (candidate == other) {
                    continue;
                }

                if (!dominates(
                        candidate,
                        other
                )) {

                    dominatesAllOthers =
                            false;

                    break;
                }
            }

            if (dominatesAllOthers) {
                dominantCandidates.add(
                        candidate
                );
            }
        }

        return dominantCandidates.size() == 1
                ? dominantCandidates.get(0)
                : null;
    }

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

        if (!annualNotLower
                || !monthlyNotLower
                || !directionalNotLower) {

            return false;
        }

        return firstAnnualLevel
                > secondAnnualLevel

                || firstMonthlyLevel
                > secondMonthlyLevel

                || firstDirectionalLevel
                > secondDirectionalLevel;
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
                "Natal-only Match는 해당 명리연도의 Active Domain 후보로 사용하지 않고 ANNUAL_CONTEXT, ANNUAL_FUNCTIONAL_CONTEXT, MONTHLY_CONTEXT 또는 MONTHLY_FUNCTIONAL_CONTEXT가 실제 존재하는 Domain만 후보로 사용합니다."
        );

        reasons.add(
                "ANNUAL_CONTEXT와 ANNUAL_FUNCTIONAL_CONTEXT는 독립 Evidence 두 개로 합산하지 않으며 동일 Annual provenance의 qualification 계층으로 사용합니다."
        );

        reasons.add(
                "MONTHLY_CONTEXT와 MONTHLY_FUNCTIONAL_CONTEXT는 같은 명리연도·같은 monthIndex라면 하나의 MONTHLY_CONTEXT_FAMILY Anchor로 정규화하고, 서로 다른 최소 두 월 Anchor가 있을 때만 multi-month activity를 인정합니다."
        );

        reasons.add(
                "SUPPORTIVE, PRESSURE, MIXED는 PRIMARY_ACTIVE 비교에서 모두 방향성이 존재하는 상태이며 서로 우열을 두지 않습니다. CONTEXTUAL만 방향 미확정 상태로 구분합니다."
        );

        for (ActiveDomainCandidate candidate
                : candidates) {

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

        if (candidates.size() == 1) {

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

        switch (selected.domainStatus()) {

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

        if (selected.multiMonthActivity()) {

            reasons.add(
                    "선택된 Domain은 raw/functional 중복을 제거한 둘 이상의 서로 다른 MONTHLY_CONTEXT_FAMILY Anchor에서 실제 Timeline Activity가 확인되어 하나의 대표 월에만 국한되지 않습니다."
            );

        } else {

            reasons.add(
                    "선택된 Domain에는 multi-month activity 자격이 없으며 Primary 식별은 다른 자격축의 구조적 우위 또는 유일한 Temporal Domain이라는 조건에 의해 이루어졌습니다."
            );
        }

        reasons.add(
                "해당 명리연도의 실제 Timeline monthIndex 목록 "
                        + yearEvidence.monthIndices()
                        + "을 그대로 보존하며 대표 월을 임의 선택하지 않습니다."
        );

        if (yearEvidence.timelineUncertaintyExists()) {

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

    private String summaryOf(
            ActiveDomainCandidate selected
    ) {
        return "해당 명리연도에는 "
                + selected.domain()
                + "이 다른 생활영역보다 시간적 작동 자격이 구조적으로 분명한 PRIMARY_ACTIVE_DOMAIN이며, 그 방향은 "
                + selected.domainStatus()
                + "입니다.";
    }

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

            if (temporalMatches.isEmpty()) {
                throw new IllegalArgumentException(
                        "Active Domain Candidate에는 최소 하나의 Temporal Match가 필요합니다. domain="
                                + domain
                );
            }

            for (LifeDomainRuleMatch match
                    : temporalMatches) {

                Objects.requireNonNull(
                        match,
                        "Active Domain Candidate의 sourceMatch는 null일 수 없습니다."
                );

                if (match.domain() != domain) {
                    throw new IllegalArgumentException(
                            "Active Domain Candidate의 sourceMatch Domain이 일치하지 않습니다. expected="
                                    + domain
                                    + ", actual="
                                    + match.domain()
                    );
                }
            }

            if (directionalActivity
                    != (domainStatus
                    != LifeDomainSignalStatus.CONTEXTUAL)) {

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