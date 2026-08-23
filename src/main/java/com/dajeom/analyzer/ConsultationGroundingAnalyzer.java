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
import com.dajeom.model.ConsultationGroundingProminence;
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
 * Category Evidence View를
 * 공식 LLM Grounding 구조로 변환한다.
 *
 * =================================================
 * 의미 경계
 * =================================================
 *
 * raw LifeDomain Evidence
 *
 * =
 * 세부 계산 근거와 provenance.
 *
 * -------------------------------------------------
 *
 * Category Deterministic Conclusion
 *
 * =
 * Java가 해당 ConsultationCategory에서
 * 확정한 공식 Meaning Boundary.
 *
 * =================================================
 * Layer Routing
 * =================================================
 *
 * NATAL
 *
 * ->
 * ConsultationGroundingSection
 * .natalDeterministicConclusions
 *
 * CURRENT_TIMELINE
 *
 * ->
 * ConsultationGroundingTimelineScenario
 * .deterministicConclusions
 *
 * TIMELINE_YEAR
 *
 * ->
 * PAST_LUCK / FUTURE_LUCK
 * ConsultationGroundingTimelineYear
 * .deterministicContract
 *
 * =================================================
 * Evidence Prominence
 * =================================================
 *
 * LifeDomainRuleMatch를
 * ConsultationGroundingEvidence로 처음 변환할 때도
 * prominence를 생략하지 않는다.
 *
 * 최초 변환에서는:
 *
 * ConsultationGroundingProminence.unranked(sources)
 *
 * 를 명시적으로 넣는다.
 *
 * 이후 같은 Slice 안에서:
 *
 * ConsultationGroundingProminenceAnalyzer
 *
 * 가 실제 narrativePriority를 계산한다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 독립 계산 결과는 독립적으로 유지한다.
 *
 * 여러 Evidence 또는 Conclusion이
 * 함께 존재한다는 이유만으로:
 *
 * - 관계
 * - 인과
 * - 순서
 * - 강화/약화
 * - 새로운 종합 Status
 *
 * 를 생성하지 않는다.
 *
 * 관계 또는 합성 의미는
 * 별도의 Java Deterministic Rule이
 * 실제로 계산한 경우에만 보존한다.
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


    public ConsultationGroundingAnalysis analyze(

            ConsultationRequestEvidenceAnalysis
                    requestEvidenceAnalysis,

            NatalPersonalityStatementContract
                    personalityStatementContract

    ) {

        Objects.requireNonNull(
                requestEvidenceAnalysis,
                "ConsultationGroundingAnalyzer에는 ConsultationRequestEvidenceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                personalityStatementContract,
                "Personality Statement Contract는 필수입니다."
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
     * Section
     * =================================================
     */

    private ConsultationGroundingSection toSection(

            ConsultationCategoryEvidenceView view,

            NatalPersonalityStatementContract
                    personalityStatementContract

    ) {

        Objects.requireNonNull(
                view,
                "ConsultationCategoryEvidenceView는 필수입니다."
        );

        Objects.requireNonNull(
                personalityStatementContract,
                "Personality Statement Contract는 필수입니다."
        );

        if (
                view.category()
                        == ConsultationCategory.PERSONALITY
        ) {

            return toPersonalitySection(
                    view,
                    personalityStatementContract
            );
        }

        if (
                view.unavailable()
        ) {

            return new ConsultationGroundingSection(
                    view.category(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    Optional.empty(),
                    view.unavailableReason()
            );
        }

        List<ConsultationCategoryDeterministicMatch>
                deterministicMatches =
                createCategoryDeterministicMatches(
                        view
                );

        List<ConsultationGroundingEvidence>
                natalEvidences =
                toEvidences(
                        view.natalMatches()
                );

        List<ConsultationGroundingDeterministicConclusion>
                natalDeterministicConclusions =
                createNatalDeterministicConclusions(
                        view,
                        deterministicMatches
                );

        List<ConsultationGroundingTimelineScenario>
                scenarios =
                createTimelineScenarios(
                        view,
                        deterministicMatches
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

                natalDeterministicConclusions,

                scenarios,

                timelineYears,

                Optional.empty(),

                view.unavailableReason()
        );
    }


    /*
     * =================================================
     * Category Deterministic
     * =================================================
     */

    private List<ConsultationCategoryDeterministicMatch>
    createCategoryDeterministicMatches(

            ConsultationCategoryEvidenceView view

    ) {

        Objects.requireNonNull(
                view,
                "Category Deterministic Rule 실행에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> matches =
                deterministicRuleEngine
                        .evaluate(
                                view
                        );

        if (
                matches == null
        ) {

            throw new IllegalStateException(
                    "ConsultationCategoryDeterministicRuleEngine.evaluate()는 null을 반환할 수 없습니다. category="
                            + view.category()
            );
        }

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
                            != view.category()
            ) {

                throw new IllegalStateException(
                        "Category Deterministic Match의 Category가 현재 Evidence View와 일치하지 않습니다. viewCategory="
                                + view.category()
                                + ", matchCategory="
                                + normalized.category()
                                + ", rule="
                                + normalized.ruleCode()
                );
            }
        }

        return List.copyOf(
                matches
        );
    }


    /*
     * =================================================
     * NATAL
     * =================================================
     */

    private List<ConsultationGroundingDeterministicConclusion>
    createNatalDeterministicConclusions(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch>
                    deterministicMatches

    ) {

        Objects.requireNonNull(
                view,
                "NATAL Deterministic Grounding에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                deterministicMatches == null
                        || deterministicMatches.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingDeterministicConclusion>
                result =
                new ArrayList<>();

        for (
                ConsultationCategoryDeterministicMatch match
                : deterministicMatches
        ) {

            ConsultationCategoryDeterministicMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "NATAL Deterministic Match는 null일 수 없습니다."
                    );

            if (
                    normalized.category()
                            != view.category()
            ) {

                throw new IllegalStateException(
                        "NATAL Deterministic Match의 Category가 View와 일치하지 않습니다. viewCategory="
                                + view.category()
                                + ", matchCategory="
                                + normalized.category()
                                + ", rule="
                                + normalized.ruleCode()
                );
            }

            if (
                    normalized.layer()
                            != ConsultationCategoryDeterministicLayer.NATAL
            ) {

                continue;
            }

            result.add(
                    toDeterministicConclusion(
                            normalized
                    )
            );
        }

        return List.copyOf(
                result
        );
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
                view,
                "PERSONALITY Grounding에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        Objects.requireNonNull(
                contract,
                "PERSONALITY Grounding에는 Personality Statement Contract가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.PERSONALITY
        ) {

            throw new IllegalArgumentException(
                    "toPersonalitySection은 PERSONALITY View만 처리할 수 있습니다: "
                            + view.category()
            );
        }

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
                natalEvidences =
                toEvidences(
                        view.natalMatches()
                );

        return new ConsultationGroundingSection(

                ConsultationCategory.PERSONALITY,

                natalEvidences,

                List.of(),

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
     * Current Timeline
     * =================================================
     */

    private List<ConsultationGroundingTimelineScenario>
    createTimelineScenarios(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch>
                    deterministicMatches

    ) {

        Objects.requireNonNull(
                view,
                "Current Timeline Scenario 생성에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

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

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segmentEvidence,
                            "Current Timeline Segment Evidence는 null일 수 없습니다."
                    );

            List<ConsultationGroundingEvidence>
                    evidences =
                    toEvidences(
                            normalizedSegment.matches()
                    );

            List<ConsultationGroundingDeterministicConclusion>
                    deterministicConclusions =
                    createCurrentTimelineDeterministicConclusions(
                            view,
                            normalizedSegment,
                            deterministicMatches
                    );

            scenarios.add(
                    new ConsultationGroundingTimelineScenario(
                            scenarioOrder,
                            normalizedSegment.sajuYear(),
                            normalizedSegment.monthIndex(),
                            normalizedSegment.fullyDefinite(),
                            normalizedSegment.timelineUncertaintyExists(),
                            normalizedSegment.hasActiveDecade(),
                            normalizedSegment.beforeFirstDecadeLuck(),
                            normalizedSegment.afterGeneratedDecadeRange(),
                            normalizedSegment.decadeUnavailable(),
                            evidences,
                            deterministicConclusions
                    )
            );

            scenarioOrder++;
        }

        return List.copyOf(
                scenarios
        );
    }


    private List<ConsultationGroundingDeterministicConclusion>
    createCurrentTimelineDeterministicConclusions(

            ConsultationCategoryEvidenceView view,

            ConsultationCategorySegmentEvidence segmentEvidence,

            List<ConsultationCategoryDeterministicMatch>
                    deterministicMatches

    ) {

        Objects.requireNonNull(
                view,
                "CURRENT_TIMELINE Deterministic Grounding에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        Objects.requireNonNull(
                segmentEvidence,
                "CURRENT_TIMELINE Deterministic Grounding에는 Segment Evidence가 필요합니다."
        );

        if (
                deterministicMatches == null
                        || deterministicMatches.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingDeterministicConclusion>
                result =
                new ArrayList<>();

        for (
                ConsultationCategoryDeterministicMatch match
                : deterministicMatches
        ) {

            ConsultationCategoryDeterministicMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "CURRENT_TIMELINE Deterministic Match는 null일 수 없습니다."
                    );

            if (
                    normalized.category()
                            != view.category()
            ) {

                throw new IllegalStateException(
                        "CURRENT_TIMELINE Deterministic Match의 Category가 View와 일치하지 않습니다. viewCategory="
                                + view.category()
                                + ", matchCategory="
                                + normalized.category()
                                + ", rule="
                                + normalized.ruleCode()
                );
            }

            if (
                    normalized.layer()
                            != ConsultationCategoryDeterministicLayer
                            .CURRENT_TIMELINE
            ) {

                continue;
            }

            Integer matchYear =
                    normalized.sajuYear();

            Integer matchMonthIndex =
                    normalized.monthIndex();

            if (
                    matchYear == null
                            || matchMonthIndex == null
            ) {

                throw new IllegalStateException(
                        "CURRENT_TIMELINE Deterministic Match에는 sajuYear와 monthIndex가 필요합니다. category="
                                + normalized.category()
                                + ", rule="
                                + normalized.ruleCode()
                );
            }

            if (
                    matchYear.intValue()
                            != segmentEvidence.sajuYear()
            ) {

                continue;
            }

            if (
                    matchMonthIndex.intValue()
                            != segmentEvidence.monthIndex()
            ) {

                continue;
            }

            if (
                    !currentTimelineMatchBelongsToSegment(
                            view,
                            segmentEvidence,
                            normalized
                    )
            ) {

                continue;
            }

            result.add(
                    toDeterministicConclusion(
                            normalized
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    private boolean currentTimelineMatchBelongsToSegment(

            ConsultationCategoryEvidenceView view,

            ConsultationCategorySegmentEvidence segmentEvidence,

            ConsultationCategoryDeterministicMatch match

    ) {

        Objects.requireNonNull(
                view,
                "CURRENT_TIMELINE Segment 소속 판정에는 Evidence View가 필요합니다."
        );

        Objects.requireNonNull(
                segmentEvidence,
                "CURRENT_TIMELINE Segment 소속 판정에는 Segment Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                match,
                "CURRENT_TIMELINE Segment 소속 판정에는 Deterministic Match가 필요합니다."
        );

        if (
                !match.sourceMatches()
                        .isEmpty()
        ) {

            for (
                    LifeDomainRuleMatch sourceMatch
                    : match.sourceMatches()
            ) {

                LifeDomainRuleMatch normalizedSource =
                        Objects.requireNonNull(
                                sourceMatch,
                                "CURRENT_TIMELINE Deterministic sourceMatch는 null일 수 없습니다."
                        );

                if (
                        !containsLifeDomainMatch(
                                segmentEvidence.matches(),
                                normalizedSource
                        )
                ) {

                    return false;
                }
            }

            return true;
        }

        int sameCoordinateScenarioCount =
                countCurrentTimelineSegmentsAtCoordinate(
                        view,
                        segmentEvidence.sajuYear(),
                        segmentEvidence.monthIndex()
                );

        if (
                sameCoordinateScenarioCount > 1
        ) {

            throw new IllegalStateException(
                    "sourceMatches가 없는 CURRENT_TIMELINE Deterministic Match를 동일 좌표의 복수 Scenario에 안전하게 배정할 수 없습니다. category="
                            + match.category()
                            + ", rule="
                            + match.ruleCode()
                            + ", sajuYear="
                            + segmentEvidence.sajuYear()
                            + ", monthIndex="
                            + segmentEvidence.monthIndex()
                            + ", scenarioCount="
                            + sameCoordinateScenarioCount
            );
        }

        return sameCoordinateScenarioCount == 1;
    }


    private int countCurrentTimelineSegmentsAtCoordinate(

            ConsultationCategoryEvidenceView view,

            int sajuYear,

            int monthIndex

    ) {

        int count =
                0;

        for (
                ConsultationCategorySegmentEvidence candidate
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalized =
                    Objects.requireNonNull(
                            candidate,
                            "Timeline Segment Evidence는 null일 수 없습니다."
                    );

            if (
                    normalized.sajuYear()
                            != sajuYear
            ) {

                continue;
            }

            if (
                    normalized.monthIndex()
                            != monthIndex
            ) {

                continue;
            }

            count++;
        }

        return count;
    }


    private boolean containsLifeDomainMatch(

            List<LifeDomainRuleMatch> candidates,

            LifeDomainRuleMatch expected

    ) {

        if (
                candidates == null
                        || candidates.isEmpty()
        ) {

            return false;
        }

        Objects.requireNonNull(
                expected,
                "확인할 LifeDomainRuleMatch는 null일 수 없습니다."
        );

        for (
                LifeDomainRuleMatch candidate
                : candidates
        ) {

            if (
                    candidate == null
            ) {

                continue;
            }

            if (
                    candidate == expected
                            || candidate.equals(
                            expected
                    )
            ) {

                return true;
            }
        }

        return false;
    }


    /*
     * =================================================
     * Evidence
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
     * Timeline Year Range
     * =================================================
     */

    private List<ConsultationGroundingTimelineYear>
    toTimelineYears(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch>
                    deterministicMatches

    ) {

        Objects.requireNonNull(
                view,
                "Timeline Range Grounding에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        List<ConsultationCategoryTimelineYearEvidence>
                yearEvidences =
                view.timelineYearEvidences();

        if (
                yearEvidences == null
                        || yearEvidences.isEmpty()
        ) {

            return List.of();
        }

        if (
                !timelineRangeCategory(
                        view.category()
                )
        ) {

            throw new IllegalStateException(
                    "Timeline Year Grounding은 PAST_LUCK / FUTURE_LUCK에만 사용할 수 있습니다. category="
                            + view.category()
            );
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

        int yearOrder =
                1;

        for (
                ConsultationCategoryTimelineYearEvidence
                        yearEvidence
                : ordered
        ) {

            ConsultationCategoryTimelineYearEvidence
                    normalizedYearEvidence =
                    Objects.requireNonNull(
                            yearEvidence,
                            "Timeline Year Evidence는 null일 수 없습니다."
                    );

            List<ConsultationGroundingEvidence>
                    evidences =
                    toEvidences(
                            normalizedYearEvidence.matches()
                    );

            ConsultationGroundingTimelineYearDeterministicContract
                    deterministicContract =
                    createTimelineYearDeterministicContract(
                            view.category(),
                            normalizedYearEvidence.sajuYear(),
                            deterministicMatches
                    );

            result.add(
                    new ConsultationGroundingTimelineYear(
                            yearOrder,
                            normalizedYearEvidence.sajuYear(),
                            normalizedYearEvidence.monthIndices(),
                            normalizedYearEvidence.timelineUncertaintyExists(),
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


    private boolean timelineRangeCategory(

            ConsultationCategory category

    ) {

        return category
                == ConsultationCategory.PAST_LUCK

                || category
                == ConsultationCategory.FUTURE_LUCK;
    }


    private ConsultationGroundingTimelineYearDeterministicContract
    createTimelineYearDeterministicContract(

            ConsultationCategory category,

            int sajuYear,

            List<ConsultationCategoryDeterministicMatch>
                    deterministicMatches

    ) {

        if (
                !timelineRangeCategory(
                        category
                )
        ) {

            throw new IllegalStateException(
                    "Timeline Year Deterministic Contract는 PAST_LUCK / FUTURE_LUCK에만 생성할 수 있습니다. category="
                            + category
                            + ", sajuYear="
                            + sajuYear
            );
        }

        if (
                deterministicMatches == null
                        || deterministicMatches.isEmpty()
        ) {

            throw new IllegalStateException(
                    "PAST/FUTURE Timeline Year에 Category Deterministic Match가 없습니다. category="
                            + category
                            + ", sajuYear="
                            + sajuYear
            );
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
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "PAST/FUTURE Timeline Year의 OVERALL_NARRATIVE가 없습니다. category="
                                                        + category
                                                        + ", sajuYear="
                                                        + sajuYear
                                        )
                        );

        ConsultationCategoryDeterministicMatch bigFlow =
                findUniqueTimelineYearMatch(
                        deterministicMatches,
                        category,
                        sajuYear,
                        prefix
                                + "BIG_FLOW"
                )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "PAST/FUTURE Timeline Year의 BIG_FLOW가 없습니다. category="
                                                        + category
                                                        + ", sajuYear="
                                                        + sajuYear
                                        )
                        );

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

        return new ConsultationGroundingTimelineYearDeterministicContract(

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
        );
    }


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
                            != ConsultationCategoryDeterministicLayer
                            .TIMELINE_YEAR
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

                match.dependencies(),

                structuralReference
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
     * Raw Evidence
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

        ConsultationGroundingProminence unrankedProminence =
                ConsultationGroundingProminence
                        .unranked(
                                sources
                        );

        return new ConsultationGroundingEvidence(
                match.domain(),
                match.ruleCode(),
                match.status(),
                match.summary(),
                sources,
                match.reasons(),
                unrankedProminence
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