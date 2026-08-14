package com.dajeom.analyzer;

import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.ConsultationGroundingAnalysis;
import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingSection;
import com.dajeom.model.ConsultationGroundingSource;
import com.dajeom.model.ConsultationGroundingTimelineScenario;
import com.dajeom.model.ConsultationRequestEvidenceAnalysis;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 20차.
 *
 * 19차 ConsultationRequestEvidenceAnalysis를
 * LLM Interpretation용 Grounding으로 변환한다.
 *
 * =================================================
 *
 * 핵심:
 *
 * 원본 명리 계산 객체를
 * LLM 입력 모델에 직접 노출하지 않는다.
 *
 * LifeDomainRuleMatch가 실제 사용한:
 *
 * - 상태
 * - Summary
 * - Reasons
 * - Source Type
 * - Tier
 * - Source Key
 * - Auxiliary 여부
 *
 * 만 추출한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 새로운 명리 계산
 * - 점수 계산
 * - polarity/intensity 계산
 * - 여러 Match 합산
 * - POSSIBLE Scenario 제거
 * - 사용자 문장 생성
 */
@Component
public class ConsultationGroundingAnalyzer {

    public ConsultationGroundingAnalysis analyze(
            ConsultationRequestEvidenceAnalysis requestEvidenceAnalysis
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
         * 현재 deterministic 범위에서
         * 만들 수 없는 Category.
         *
         * 가짜 Evidence를 생성하지 않는다.
         */
        if (
                view.unavailable()
        ) {

            return new ConsultationGroundingSection(
                    view.category(),
                    List.of(),
                    List.of(),
                    view.unavailableReason()
            );
        }

        List<ConsultationGroundingEvidence>
                natalEvidences =
                view
                        .natalMatches()
                        .stream()
                        .map(
                                this::toEvidence
                        )
                        .toList();

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

        return new ConsultationGroundingSection(
                view.category(),
                natalEvidences,
                List.copyOf(
                        scenarios
                ),
                view.unavailableReason()
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
         * reference.source()
         *
         * 는 의도적으로 Grounding에 전달하지 않는다.
         *
         * 3~17차 계산 객체는 Java 내부에 남긴다.
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