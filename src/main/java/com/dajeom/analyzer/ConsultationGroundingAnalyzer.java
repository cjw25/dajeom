package com.dajeom.analyzer;

import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.ConsultationCategoryTimelineYearEvidence;
import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingSection;
import com.dajeom.model.ConsultationGroundingSource;
import com.dajeom.model.ConsultationGroundingTimelineScenario;
import com.dajeom.model.ConsultationGroundingTimelineYear;
import com.dajeom.model.ConsultationRequestEvidenceAnalysis;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 20차 / 21차 Range 확장.
 *
 * 19차 ConsultationRequestEvidenceAnalysis를
 * LLM Interpretation용 Grounding으로 변환한다.
 *
 * =================================================
 *
 * 기존:
 *
 * Natal Match
 * Current Timeline Segment
 *
 * =================================================
 *
 * 추가:
 *
 * PAST_LUCK / FUTURE_LUCK용
 * Timeline Year Range
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 새로운 명리 계산
 * - 점수 계산
 * - polarity 계산
 * - 상태 합산
 * - Timeline 대표 연도 선택
 * - 사용자 문장 생성
 */
@Component
public class ConsultationGroundingAnalyzer {

    public ConsultationGroundingAnalysis analyze(

            ConsultationRequestEvidenceAnalysis
                    requestEvidenceAnalysis

    ) {

        Objects.requireNonNull(
                requestEvidenceAnalysis,
                "ConsultationGroundingAnalyzer에는 ConsultationRequestEvidenceAnalysis가 필요합니다."
        );

        List<ConsultationGroundingSection> sections =
                new ArrayList<>();

        for (
                ConsultationCategoryEvidenceView view
                : requestEvidenceAnalysis.selectedViews()
        ) {

            sections.add(
                    toSection(
                            view
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

            ConsultationCategoryEvidenceView view

    ) {

        Objects.requireNonNull(
                view,
                "ConsultationCategoryEvidenceView는 필수입니다."
        );

        /*
         * =================================================
         * unavailable
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
                    view.unavailableReason()
            );
        }

        /*
         * =================================================
         * Natal
         * =================================================
         */

        List<ConsultationGroundingEvidence>
                natalEvidences =
                view
                        .natalMatches()
                        .stream()
                        .map(
                                this::toEvidence
                        )
                        .toList();

        /*
         * =================================================
         * Current Timeline Scenario
         * =================================================
         */

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
                    segmentEvidence
                            .matches()
                            .stream()
                            .map(
                                    this::toEvidence
                            )
                            .toList();

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

        /*
         * =================================================
         * Timeline Range Year
         * =================================================
         */

        List<ConsultationGroundingTimelineYear>
                timelineYears =
                toTimelineYears(
                        view
                                .timelineYearEvidences()
                );

        return new ConsultationGroundingSection(
                view.category(),
                natalEvidences,
                List.copyOf(
                        scenarios
                ),
                timelineYears,
                view.unavailableReason()
        );
    }

    /*
     * =================================================
     * Timeline Range
     * =================================================
     */

    private List<ConsultationGroundingTimelineYear>
    toTimelineYears(

            List<ConsultationCategoryTimelineYearEvidence>
                    yearEvidences

    ) {

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

        int yearOrder =
                1;

        for (
                ConsultationCategoryTimelineYearEvidence
                        yearEvidence
                : ordered
        ) {

            List<ConsultationGroundingEvidence>
                    evidences =
                    yearEvidence
                            .matches()
                            .stream()
                            .map(
                                    this::toEvidence
                            )
                            .toList();

            result.add(
                    new ConsultationGroundingTimelineYear(
                            yearOrder,
                            yearEvidence.sajuYear(),
                            yearEvidence.monthIndices(),
                            yearEvidence.timelineUncertaintyExists(),
                            evidences
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
     * Rule Match
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

        /*
         * 원본 reference.source()는
         * 외부 Grounding으로 넘기지 않는다.
         */

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