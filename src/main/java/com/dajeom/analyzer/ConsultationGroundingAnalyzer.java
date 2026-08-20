package com.dajeom.analyzer;

import com.dajeom.engine.ConsultationCategoryDeterministicRuleEngine;
import com.dajeom.model.ConsultationCategoryDeterministicDependencyReference;
import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.ConsultationCategoryTimelineYearEvidence;
import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingDeterministicConclusion;
import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingSection;
import com.dajeom.model.ConsultationGroundingSource;
import com.dajeom.model.ConsultationGroundingTimelineScenario;
import com.dajeom.model.ConsultationGroundingTimelineYear;
import com.dajeom.model.ConsultationGroundingTimelineYearDeterministicContract;
import com.dajeom.model.ConsultationRequestEvidenceAnalysis;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.NatalPersonalityStatementContract;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차 / 21차 Range / 22차 Prominence
 * +
 * Personality Direct-Natal Grounding
 * +
 * PAST/FUTURE Category Deterministic Contract 확장.
 *
 * =================================================
 * 기존 일반 Category
 * =================================================
 *
 * LifeDomain
 * ↓
 * Rule Match
 * ↓
 * Grounding Evidence
 *
 * =================================================
 * PERSONALITY
 * =================================================
 *
 * NatalPersonalityStatementContract
 *
 * 를 공식 deterministic 의미 Source로 사용한다.
 *
 * =================================================
 * PAST_LUCK / FUTURE_LUCK
 * =================================================
 *
 * 기존:
 *
 * Timeline Year
 * ↓
 * raw LifeDomainRuleMatch
 * ↓
 * Grounding Evidence
 *
 * =================================================
 *
 * 변경:
 *
 * Timeline Year Evidence
 * ↓
 * ConsultationCategoryDeterministicRuleEngine
 * ↓
 *
 * BIG_FLOW
 * PRIMARY_ACTIVE_DOMAIN
 * PRIMARY_PRESSURE_DOMAIN
 * CROSS_DOMAIN_PATTERN
 * TURNING_POINT
 * OVERALL_NARRATIVE
 *
 * ↓
 *
 * Grounding Timeline Year Deterministic Contract
 *
 * +
 *
 * raw Grounding Evidence
 *
 * =================================================
 * 핵심 우선순위
 * =================================================
 *
 * Deterministic Contract:
 *
 * 최종 연도 의미의 공식 상위 계약.
 *
 * =================================================
 *
 * raw Grounding Evidence:
 *
 * 그 계약을 설명하기 위한 세부 근거.
 *
 * =================================================
 *
 * raw Evidence를 다시 합산해서
 * 연도 전체 방향을 계산하지 않는다.
 *
 * =================================================
 * LLM Boundary
 * =================================================
 *
 * ConsultationCategoryDeterministicMatch 전체를
 * 직접 LLM 경계로 넘기지 않는다.
 *
 * 내부 LifeDomainRuleMatch를 제거하고:
 *
 * - ruleCode
 * - status
 * - summary
 * - source Domain / Status
 * - reasons
 * - dependency provenance
 *
 * 만 Grounding Conclusion으로 변환한다.
 */
@Component
public class ConsultationGroundingAnalyzer {

    private static final String
            PERSONALITY_CONTRACT_UNAVAILABLE_REASON =
            "원국 직접 기본성향 분석에서 "
                    + "사용자에게 직접 설명할 수 있는 수준의 "
                    + "독립적인 성향 근거가 충분히 성립하지 않았습니다.";

    private final ConsultationGroundingProminenceAnalyzer
            prominenceAnalyzer;

    private final ConsultationCategoryDeterministicRuleEngine
            deterministicRuleEngine;


    /*
     * =================================================
     * 기존 호출 호환
     * =================================================
     *
     * 직접 new 하는 기존 코드에서는
     * Category Deterministic Rule이 등록되지 않은
     * 빈 Engine을 사용한다.
     *
     * 실제 Spring Application에서는
     * @Autowired Constructor를 사용한다.
     */

    public ConsultationGroundingAnalyzer() {

        this(
                new ConsultationGroundingProminenceAnalyzer(),
                new ConsultationCategoryDeterministicRuleEngine(
                        List.of()
                )
        );
    }


    /*
     * =================================================
     * 기존 Prominence 직접 주입 호출 호환
     * =================================================
     */

    public ConsultationGroundingAnalyzer(

            ConsultationGroundingProminenceAnalyzer
                    prominenceAnalyzer

    ) {

        this(
                prominenceAnalyzer,
                new ConsultationCategoryDeterministicRuleEngine(
                        List.of()
                )
        );
    }


    /*
     * =================================================
     * Spring 공식 Constructor
     * =================================================
     */

    @Autowired
    public ConsultationGroundingAnalyzer(

            ConsultationGroundingProminenceAnalyzer
                    prominenceAnalyzer,

            ConsultationCategoryDeterministicRuleEngine
                    deterministicRuleEngine

    ) {

        this.prominenceAnalyzer =
                Objects.requireNonNull(
                        prominenceAnalyzer,
                        "ConsultationGroundingProminenceAnalyzer가 필요합니다."
                );

        this.deterministicRuleEngine =
                Objects.requireNonNull(
                        deterministicRuleEngine,
                        "ConsultationCategoryDeterministicRuleEngine이 필요합니다."
                );
    }


    /*
     * =================================================
     * 기존 호출 호환
     * =================================================
     */

    public ConsultationGroundingAnalysis analyze(

            ConsultationRequestEvidenceAnalysis
                    requestEvidenceAnalysis

    ) {

        return analyzeInternal(
                requestEvidenceAnalysis,
                Optional.empty()
        );
    }


    /*
     * =================================================
     * 현재 공식 Application 호출
     * =================================================
     */

    public ConsultationGroundingAnalysis analyze(

            ConsultationRequestEvidenceAnalysis
                    requestEvidenceAnalysis,

            NatalPersonalityStatementContract
                    personalityStatementContract

    ) {

        Objects.requireNonNull(
                personalityStatementContract,
                "Personality Statement Contract는 필수입니다."
        );

        return analyzeInternal(
                requestEvidenceAnalysis,
                Optional.of(
                        personalityStatementContract
                )
        );
    }


    private ConsultationGroundingAnalysis analyzeInternal(

            ConsultationRequestEvidenceAnalysis
                    requestEvidenceAnalysis,

            Optional<NatalPersonalityStatementContract>
                    personalityStatementContract

    ) {

        Objects.requireNonNull(
                requestEvidenceAnalysis,
                "ConsultationGroundingAnalyzer에는 ConsultationRequestEvidenceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                personalityStatementContract,
                "Personality Statement Contract Optional은 필수입니다."
        );

        List<ConsultationGroundingSection> sections =
                new ArrayList<>();

        for (
                ConsultationCategoryEvidenceView view
                : requestEvidenceAnalysis.selectedViews()
        ) {

            sections.add(
                    toSection(
                            view,
                            personalityStatementContract
                    )
            );
        }

        return new ConsultationGroundingAnalysis(

                requestEvidenceAnalysis
                        .questionRoutingAnalysis()
                        .status(),

                requestEvidenceAnalysis
                        .consultationQuestion(),

                List.copyOf(
                        sections
                ),

                requestEvidenceAnalysis
                        .completeChart(),

                requestEvidenceAnalysis
                        .uncertaintyExists()
        );
    }


    /*
     * =================================================
     * Category
     * =================================================
     */

    private ConsultationGroundingSection toSection(

            ConsultationCategoryEvidenceView view,

            Optional<NatalPersonalityStatementContract>
                    personalityStatementContract

    ) {

        Objects.requireNonNull(
                view,
                "ConsultationCategoryEvidenceView는 필수입니다."
        );

        Objects.requireNonNull(
                personalityStatementContract,
                "Personality Statement Contract Optional은 필수입니다."
        );

        /*
         * =================================================
         * PERSONALITY
         * =================================================
         */

        if (
                view.category()
                        == ConsultationCategory.PERSONALITY

                        && personalityStatementContract.isPresent()
        ) {

            return toPersonalitySection(
                    view,
                    personalityStatementContract
                            .orElseThrow()
            );
        }

        /*
         * =================================================
         * Unavailable
         * =================================================
         */

        if (
                view.unavailable()
        ) {

            return new ConsultationGroundingSection(
                    view.category(),
                    List.of(),
                    List.of(),
                    List.of(),
                    Optional.empty(),
                    view.unavailableReason()
            );
        }

        /*
         * =================================================
         * Category Deterministic
         * =================================================
         *
         * PAST_LUCK / FUTURE_LUCK에서만
         * 현재 연도 종합 Contract를 연결한다.
         *
         * 다른 Category의 기존 Grounding 동작은
         * 이번 단계에서 변경하지 않는다.
         */

        List<ConsultationCategoryDeterministicMatch>
                deterministicMatches =
                createTimelineRangeDeterministicMatches(
                        view
                );

        /*
         * =================================================
         * Existing Grounding
         * =================================================
         */

        List<ConsultationGroundingEvidence>
                natalEvidences =
                toEvidences(
                        view.natalMatches()
                );

        List<ConsultationGroundingTimelineScenario>
                scenarios =
                createTimelineScenarios(
                        view
                );

        List<ConsultationGroundingTimelineYear>
                timelineYears =
                toTimelineYears(
                        view,
                        deterministicMatches
                );

        return new ConsultationGroundingSection(

                view.category(),

                natalEvidences,

                scenarios,

                timelineYears,

                Optional.empty(),

                view.unavailableReason()
        );
    }


    /*
     * =================================================
     * Category Deterministic Range
     * =================================================
     */

    private List<ConsultationCategoryDeterministicMatch>
    createTimelineRangeDeterministicMatches(

            ConsultationCategoryEvidenceView view

    ) {

        if (
                !timelineRangeCategory(
                        view.category()
                )
        ) {

            return List.of();
        }

        if (
                !view.hasTimelineYearEvidences()
        ) {

            return List.of();
        }

        return deterministicRuleEngine
                .evaluate(
                        view
                );
    }


    private boolean timelineRangeCategory(

            ConsultationCategory category

    ) {

        return category
                == ConsultationCategory.PAST_LUCK

                || category
                == ConsultationCategory.FUTURE_LUCK;
    }


    /*
     * =================================================
     * PERSONALITY
     * =================================================
     */

    private ConsultationGroundingSection
    toPersonalitySection(

            ConsultationCategoryEvidenceView view,

            NatalPersonalityStatementContract contract

    ) {

        Objects.requireNonNull(
                contract,
                "PERSONALITY Grounding에는 Personality Statement Contract가 필요합니다."
        );

        boolean outputAvailable =
                hasPersonalityOutput(
                        contract
                );

        if (
                !outputAvailable
        ) {

            return new ConsultationGroundingSection(

                    ConsultationCategory.PERSONALITY,

                    List.of(),

                    List.of(),

                    List.of(),

                    Optional.of(
                            contract
                    ),

                    Optional.of(
                            PERSONALITY_CONTRACT_UNAVAILABLE_REASON
                    )
            );
        }

        List<ConsultationGroundingEvidence>
                legacyNatalEvidences =
                toEvidences(
                        view.natalMatches()
                );

        return new ConsultationGroundingSection(

                ConsultationCategory.PERSONALITY,

                legacyNatalEvidences,

                List.of(),

                List.of(),

                Optional.of(
                        contract
                ),

                Optional.empty()
        );
    }


    private boolean hasPersonalityOutput(

            NatalPersonalityStatementContract contract

    ) {

        return contract
                .bundleContracts()
                .stream()
                .anyMatch(
                        bundleContract ->
                                !bundleContract
                                        .mandatorySelections()
                                        .isEmpty()

                                        || !bundleContract
                                        .supportingSelections()
                                        .isEmpty()
                );
    }


    /*
     * =================================================
     * Current Timeline Scenario
     * =================================================
     */

    private List<ConsultationGroundingTimelineScenario>
    createTimelineScenarios(

            ConsultationCategoryEvidenceView view

    ) {

        List<ConsultationGroundingTimelineScenario>
                scenarios =
                new ArrayList<>();

        int scenarioOrder =
                1;

        for (
                ConsultationCategorySegmentEvidence
                        segmentEvidence
                : view.segmentEvidences()
        ) {

            List<ConsultationGroundingEvidence>
                    evidences =
                    toEvidences(
                            segmentEvidence.matches()
                    );

            scenarios.add(
                    new ConsultationGroundingTimelineScenario(
                            scenarioOrder,
                            segmentEvidence.sajuYear(),
                            segmentEvidence.monthIndex(),
                            segmentEvidence.fullyDefinite(),
                            segmentEvidence.timelineUncertaintyExists(),
                            segmentEvidence.hasActiveDecade(),
                            segmentEvidence.beforeFirstDecadeLuck(),
                            segmentEvidence.afterGeneratedDecadeRange(),
                            segmentEvidence.decadeUnavailable(),
                            evidences
                    )
            );

            scenarioOrder++;
        }

        return List.copyOf(
                scenarios
        );
    }


    /*
     * =================================================
     * Evidence Slice
     * =================================================
     */

    private List<ConsultationGroundingEvidence> toEvidences(

            List<LifeDomainRuleMatch> matches

    ) {

        if (
                matches == null
                        || matches.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingEvidence> raw =
                matches
                        .stream()
                        .map(
                                this::toEvidence
                        )
                        .toList();

        return prominenceAnalyzer
                .analyze(
                        raw
                );
    }


    /*
     * =================================================
     * Timeline Range
     * =================================================
     */

    private List<ConsultationGroundingTimelineYear>
    toTimelineYears(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch>
                    deterministicMatches

    ) {

        List<ConsultationCategoryTimelineYearEvidence>
                yearEvidences =
                view.timelineYearEvidences();

        if (
                yearEvidences == null
                        || yearEvidences.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationCategoryTimelineYearEvidence>
                ordered =
                yearEvidences
                        .stream()
                        .sorted(
                                Comparator.comparingInt(
                                        ConsultationCategoryTimelineYearEvidence
                                                ::sajuYear
                                )
                        )
                        .toList();

        List<ConsultationGroundingTimelineYear> result =
                new ArrayList<>(
                        ordered.size()
                );

        boolean strictDeterministicContractExpected =
                timelineRangeCategory(
                        view.category()
                )

                        && deterministicRuleEngine
                        .hasRules(
                                view.category()
                        )

                        && deterministicRuleEngine
                        .hasSynthesisRules(
                                view.category()
                        );

        int yearOrder =
                1;

        for (
                ConsultationCategoryTimelineYearEvidence
                        yearEvidence
                : ordered
        ) {

            List<ConsultationGroundingEvidence>
                    evidences =
                    toEvidences(
                            yearEvidence.matches()
                    );

            Optional<
                    ConsultationGroundingTimelineYearDeterministicContract
                    > deterministicContract =
                    createTimelineYearDeterministicContract(
                            view.category(),
                            yearEvidence.sajuYear(),
                            deterministicMatches,
                            strictDeterministicContractExpected
                    );

            result.add(
                    new ConsultationGroundingTimelineYear(
                            yearOrder,
                            yearEvidence.sajuYear(),
                            yearEvidence.monthIndices(),
                            yearEvidence.timelineUncertaintyExists(),
                            evidences,
                            deterministicContract
                    )
            );

            yearOrder++;
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Timeline Year Contract
     * =================================================
     */

    private Optional<
            ConsultationGroundingTimelineYearDeterministicContract
            > createTimelineYearDeterministicContract(

            ConsultationCategory category,

            int sajuYear,

            List<ConsultationCategoryDeterministicMatch>
                    deterministicMatches,

            boolean strictExpected

    ) {

        if (
                !timelineRangeCategory(
                        category
                )
        ) {

            return Optional.empty();
        }

        if (
                deterministicMatches == null
                        || deterministicMatches.isEmpty()
        ) {

            if (
                    strictExpected
            ) {

                throw new IllegalStateException(
                        "PAST/FUTURE Timeline Year에 Category Deterministic Rule이 등록되어 있지만 결과가 없습니다. category="
                                + category
                                + ", sajuYear="
                                + sajuYear
                );
            }

            return Optional.empty();
        }

        String prefix =
                category.name()
                        + "_TIMELINE_YEAR_";

        ConsultationCategoryDeterministicMatch overall =
                findUniqueTimelineYearMatch(
                        deterministicMatches,
                        category,
                        sajuYear,
                        prefix
                                + "OVERALL_NARRATIVE"
                )
                        .orElse(
                                null
                        );

        ConsultationCategoryDeterministicMatch bigFlow =
                findUniqueTimelineYearMatch(
                        deterministicMatches,
                        category,
                        sajuYear,
                        prefix
                                + "BIG_FLOW"
                )
                        .orElse(
                                null
                        );

        /*
         * =================================================
         * 공식 Contract가 활성화된 Application에서
         * Overall / BigFlow 누락은 조용히 fallback하지 않는다.
         * =================================================
         */

        if (
                strictExpected

                        && (
                        overall == null
                                || bigFlow == null
                )
        ) {

            throw new IllegalStateException(
                    "PAST/FUTURE Timeline Year의 공식 Deterministic Contract가 완성되지 않았습니다. category="
                            + category
                            + ", sajuYear="
                            + sajuYear
                            + ", overallExists="
                            + (overall != null)
                            + ", bigFlowExists="
                            + (bigFlow != null)
            );
        }

        /*
         * 기존 직접 생성 호환 경로에서
         * Rule Engine이 비어 있으면 Contract 없음.
         */

        if (
                overall == null
                        || bigFlow == null
        ) {

            return Optional.empty();
        }

        ConsultationCategoryDeterministicMatch primaryActive =
                findUniqueTimelineYearMatch(
                        deterministicMatches,
                        category,
                        sajuYear,
                        prefix
                                + "PRIMARY_ACTIVE_DOMAIN"
                )
                        .orElse(
                                null
                        );

        ConsultationCategoryDeterministicMatch primaryPressure =
                findUniqueTimelineYearMatch(
                        deterministicMatches,
                        category,
                        sajuYear,
                        prefix
                                + "PRIMARY_PRESSURE_DOMAIN"
                )
                        .orElse(
                                null
                        );

        ConsultationCategoryDeterministicMatch crossDomain =
                findUniqueTimelineYearMatch(
                        deterministicMatches,
                        category,
                        sajuYear,
                        prefix
                                + "CROSS_DOMAIN_PATTERN"
                )
                        .orElse(
                                null
                        );

        ConsultationCategoryDeterministicMatch turningPoint =
                findUniqueTimelineYearMatch(
                        deterministicMatches,
                        category,
                        sajuYear,
                        prefix
                                + "TURNING_POINT"
                )
                        .orElse(
                                null
                        );

        return Optional.of(
                new ConsultationGroundingTimelineYearDeterministicContract(

                        toDeterministicConclusion(
                                overall
                        ),

                        toDeterministicConclusion(
                                bigFlow
                        ),

                        optionalConclusion(
                                primaryActive
                        ),

                        optionalConclusion(
                                primaryPressure
                        ),

                        optionalConclusion(
                                crossDomain
                        ),

                        optionalConclusion(
                                turningPoint
                        )
                )
        );
    }


    /*
     * =================================================
     * Find Deterministic Match
     * =================================================
     */

    private Optional<ConsultationCategoryDeterministicMatch>
    findUniqueTimelineYearMatch(

            List<ConsultationCategoryDeterministicMatch>
                    matches,

            ConsultationCategory category,

            int sajuYear,

            String ruleCode

    ) {

        ConsultationCategoryDeterministicMatch found =
                null;

        for (
                ConsultationCategoryDeterministicMatch match
                : matches
        ) {

            ConsultationCategoryDeterministicMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "Category Deterministic Match는 null일 수 없습니다."
                    );

            if (
                    normalized.category()
                            != category
            ) {

                continue;
            }

            if (
                    normalized.layer()
                            != ConsultationCategoryDeterministicLayer.TIMELINE_YEAR
            ) {

                continue;
            }

            if (
                    normalized.sajuYear() == null
                            || normalized.sajuYear()
                            != sajuYear
            ) {

                continue;
            }

            if (
                    !normalized.ruleCode()
                            .equals(
                                    ruleCode
                            )
            ) {

                continue;
            }

            if (
                    found != null
            ) {

                throw new IllegalStateException(
                        "동일 Category/Year/Rule의 Deterministic Match가 중복되었습니다. category="
                                + category
                                + ", sajuYear="
                                + sajuYear
                                + ", rule="
                                + ruleCode
                );
            }

            found =
                    normalized;
        }

        return Optional.ofNullable(
                found
        );
    }


    /*
     * =================================================
     * Deterministic -> Grounding
     * =================================================
     */

    private ConsultationGroundingDeterministicConclusion
    toDeterministicConclusion(

            ConsultationCategoryDeterministicMatch match

    ) {

        Objects.requireNonNull(
                match,
                "Grounding Deterministic Conclusion 변환에는 Match가 필요합니다."
        );

        ConsultationCategoryDeterministicDependencyReference
                structuralReference =
                ConsultationCategoryDeterministicDependencyReference
                        .of(
                                match
                        );

        return new ConsultationGroundingDeterministicConclusion(

                match.ruleCode(),

                match.status(),

                match.summary(),

                structuralReference
                        .sourceDomainStatuses(),

                match.reasons(),

                match.dependencies()
        );
    }


    private Optional<ConsultationGroundingDeterministicConclusion>
    optionalConclusion(

            ConsultationCategoryDeterministicMatch match

    ) {

        if (
                match == null
        ) {

            return Optional.empty();
        }

        return Optional.of(
                toDeterministicConclusion(
                        match
                )
        );
    }


    /*
     * =================================================
     * Rule Match -> Grounding Evidence
     * =================================================
     */

    private ConsultationGroundingEvidence toEvidence(

            LifeDomainRuleMatch match

    ) {

        Objects.requireNonNull(
                match,
                "LifeDomainRuleMatch는 필수입니다."
        );

        List<ConsultationGroundingSource> sources =
                match
                        .evidences()
                        .stream()
                        .map(
                                this::toSource
                        )
                        .toList();

        return new ConsultationGroundingEvidence(
                match.domain(),
                match.ruleCode(),
                match.status(),
                match.summary(),
                sources,
                match.reasons()
        );
    }


    /*
     * =================================================
     * Evidence Source
     * =================================================
     */

    private ConsultationGroundingSource toSource(

            LifeDomainEvidenceReference<?> reference

    ) {

        Objects.requireNonNull(
                reference,
                "LifeDomainEvidenceReference는 필수입니다."
        );

        return new ConsultationGroundingSource(
                reference.sourceType(),
                reference.tier(),
                reference.sourceKey(),
                reference.auxiliary(),
                reference.summary(),
                reference.reasons()
        );
    }
}