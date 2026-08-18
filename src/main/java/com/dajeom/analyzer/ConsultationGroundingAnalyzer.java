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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 20차 / 21차 Range / 22차 Prominence 확장.
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
 * Timeline Year Range
 *
 * =================================================
 *
 * 22차 추가:
 *
 * 각 Grounding Slice 안에서
 * 실제 Source provenance를 기준으로
 * deterministic 서술 우선순위를 계산한다.
 *
 * =================================================
 *
 * Slice:
 *
 * Natal
 *
 * Current Scenario 각각
 *
 * Timeline Year 각각
 *
 * 은 서로 섞지 않는다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 새로운 명리 계산
 * - 길흉 점수 계산
 * - polarity 계산
 * - 상태 합산
 * - Timeline 대표 연도 선택
 * - 사용자 문장 생성
 */
@Component
public class ConsultationGroundingAnalyzer {

    private final ConsultationGroundingProminenceAnalyzer
            prominenceAnalyzer;

    /*
     * 기존 단위테스트 등에서
     * new ConsultationGroundingAnalyzer()
     * 를 사용하던 호출 호환.
     */
    public ConsultationGroundingAnalyzer() {

        this(
                new ConsultationGroundingProminenceAnalyzer()
        );
    }

    @Autowired
    public ConsultationGroundingAnalyzer(

            ConsultationGroundingProminenceAnalyzer
                    prominenceAnalyzer

    ) {

        this.prominenceAnalyzer =
                Objects.requireNonNull(
                        prominenceAnalyzer,
                        "ConsultationGroundingProminenceAnalyzer가 필요합니다."
                );
    }

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
         *
         * Natal 전체가 하나의 prominence Slice.
         */

        List<ConsultationGroundingEvidence>
                natalEvidences =
                toEvidences(
                        view.natalMatches()
                );

        /*
         * =================================================
         * Current Timeline Scenario
         * =================================================
         *
         * 각 Scenario마다 독립적으로 prominence를 계산한다.
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

        /*
         * =================================================
         * Timeline Range Year
         * =================================================
         */

        List<ConsultationGroundingTimelineYear>
                timelineYears =
                toTimelineYears(
                        view.timelineYearEvidences()
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
     * Evidence Slice
     * =================================================
     */

    private List<ConsultationGroundingEvidence> toEvidences(

            List<LifeDomainRuleMatch> matches

    ) {

        if (
                matches == null
                        ||
                        matches.isEmpty()
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

            List<ConsultationCategoryTimelineYearEvidence>
                    yearEvidences

    ) {

        if (
                yearEvidences == null
                        ||
                        yearEvidences.isEmpty()
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

            /*
             * 각 Year가 독립 prominence Slice다.
             */
            List<ConsultationGroundingEvidence>
                    evidences =
                    toEvidences(
                            yearEvidence.matches()
                    );

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

        /*
         * 여기서는 아직 prominence 순위를 모른다.
         *
         * ConsultationGroundingEvidence의
         * 호환 Constructor를 사용하여
         * narrativePriority == 0 상태로 만든 뒤,
         *
         * toEvidences()에서 전체 Slice를 보고
         * 실제 순위를 확정한다.
         */
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