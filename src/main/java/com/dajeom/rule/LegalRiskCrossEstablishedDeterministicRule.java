package com.dajeom.rule;

import com.dajeom.analyzer.ConsultationLegalRiskCrossEvaluator;
import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LEGAL_RISK 전용 Cross 성립 Deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * ConsultationLegalRiskAnalyzer가 선별한
 * LEGAL_RISK Evidence View에서:
 *
 * LEGAL_RISK_CROSS_ESTABLISHED
 *
 * 라는 공식 Category Deterministic Match를 만든다.
 *
 * =================================================
 * Cross 계산 Source
 * =================================================
 *
 * 이 Rule은:
 *
 * - Domain 개수
 * - independent provenance 개수
 * - Annual provenance family
 * - Monthly provenance family
 *
 * 계산 공식을 자체적으로 가지지 않는다.
 *
 * 모든 LEGAL_RISK Cross 자격 계산은:
 *
 * ConsultationLegalRiskCrossEvaluator
 *
 * 에 위임한다.
 *
 * =================================================
 * 중요한 구조
 * =================================================
 *
 * 따라서:
 *
 * ConsultationLegalRiskAnalyzer
 *
 * 와
 *
 * LegalRiskCrossEstablishedDeterministicRule
 *
 * 사이에 서로 다른 Cross 공식이 존재할 수 없다.
 *
 * 둘 다 동일한
 * ConsultationLegalRiskCrossEvaluator
 * 계약만 사용한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * LEGAL_RISK_CROSS_ESTABLISHED의
 * Category Deterministic Status는
 * PRESSURE로 고정한다.
 *
 * 이것은 Source Status의:
 *
 * - 합산
 * - 다수결
 * - 점수
 *
 * 결과가 아니다.
 *
 * 이 Rule 자체가:
 *
 * "LEGAL_RISK 전용 주의 Cross 자격이 성립했다"
 *
 * 는 의미이기 때문이다.
 *
 * =================================================
 * NATAL
 * =================================================
 *
 * view.natalMatches() 자체가
 * 공식 Legal Cross 조건을 만족할 때만
 * NATAL Conclusion을 만든다.
 *
 * =================================================
 * CURRENT_TIMELINE
 * =================================================
 *
 * 각 Segment에서는:
 *
 * view.natalMatches()
 * +
 * segment.matches()
 *
 * 를 Cross Evaluator에 전달한다.
 *
 * 단:
 *
 * Segment-local Match가 없는 경우
 * Natal Cross만으로 모든 Timeline Scenario에
 * 같은 CURRENT_TIMELINE Conclusion을 반복하지 않는다.
 *
 * =================================================
 * sourceMatches
 * =================================================
 *
 * NATAL:
 *
 * NATAL Match만 보존한다.
 *
 * -------------------------------------------------
 *
 * CURRENT_TIMELINE:
 *
 * 현재 Segment-local Match만 보존한다.
 *
 * 이는 Category Deterministic Engine의
 * Timeline Layer provenance 계약을 지키기 위한 것이다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * LEGAL_RISK_CROSS_ESTABLISHED는:
 *
 * "여러 독립 근거가 Legal 전용 자격 조건을 만족한다"
 *
 * 는 Java 계산 결과다.
 *
 * 이것으로:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A가 B를 강화한다
 * - 먼저 A 이후 B
 *
 * 같은 관계형 의미를 만들지 않는다.
 *
 * =================================================
 * 사건 예측 금지
 * =================================================
 *
 * 이 결과는:
 *
 * - 소송
 * - 고소
 * - 고발
 * - 처벌
 * - 승소
 * - 패소
 * - 계약 분쟁
 *
 * 같은 실제 법률 사건의 발생을 의미하지 않는다.
 */
@Component
public class LegalRiskCrossEstablishedDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "LEGAL_RISK_CROSS_ESTABLISHED";

    private final ConsultationLegalRiskCrossEvaluator
            crossEvaluator;


    /*
     * =================================================
     * Constructor
     * =================================================
     */

    public LegalRiskCrossEstablishedDeterministicRule(

            ConsultationLegalRiskCrossEvaluator
                    crossEvaluator

    ) {

        this.crossEvaluator =
                Objects.requireNonNull(
                        crossEvaluator,
                        "ConsultationLegalRiskCrossEvaluator가 필요합니다."
                );
    }


    /*
     * =================================================
     * Category
     * =================================================
     */

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.LEGAL_RISK;
    }


    /*
     * =================================================
     * Code
     * =================================================
     */

    @Override
    public String code() {

        return RULE_CODE;
    }


    /*
     * =================================================
     * Evaluate
     * =================================================
     */

    @Override
    public List<ConsultationCategoryDeterministicMatch>
    evaluate(

            ConsultationCategoryEvidenceView view

    ) {

        Objects.requireNonNull(
                view,
                "LegalRiskCrossEstablishedDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.LEGAL_RISK
        ) {

            throw new IllegalArgumentException(
                    "LegalRiskCrossEstablishedDeterministicRule은 LEGAL_RISK View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();


        /*
         * =================================================
         * NATAL
         * =================================================
         */

        boolean natalCrossEstablished =
                crossEvaluator
                        .established(
                                view.natalMatches()
                        );

        if (
                natalCrossEstablished
        ) {

            result.add(
                    createNatalMatch(
                            view.natalMatches()
                    )
            );
        }


        /*
         * =================================================
         * CURRENT_TIMELINE
         * =================================================
         */

        boolean anyCurrentTimelineCrossEstablished =
                false;

        for (
                ConsultationCategorySegmentEvidence segmentEvidence
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segmentEvidence,
                            "LEGAL_RISK Segment Evidence는 null일 수 없습니다."
                    );

            List<LifeDomainRuleMatch> segmentMatches =
                    normalizedSegment.matches();

            /*
             * ConsultationLegalRiskAnalyzer가
             * Cross가 성립하지 않은 Segment에는
             * Segment-local Legal Match를 넣지 않는다.
             */

            if (
                    segmentMatches.isEmpty()
            ) {

                continue;
            }

            List<LifeDomainRuleMatch> effectiveMatches =
                    new ArrayList<>(
                            view.natalMatches()
                                    .size()

                                    + segmentMatches
                                    .size()
                    );

            effectiveMatches.addAll(
                    view.natalMatches()
            );

            effectiveMatches.addAll(
                    segmentMatches
            );

            boolean currentTimelineCrossEstablished =
                    crossEvaluator
                            .established(
                                    effectiveMatches
                            );

            /*
             * Analyzer가 non-empty Segment를
             * Legal Cross 결과로 보존했는데
             * 공식 Cross Evaluator 결과가 false라면
             * 구조 계약 위반이다.
             */

            if (
                    !currentTimelineCrossEstablished
            ) {

                throw new IllegalStateException(
                        "사용 가능한 LEGAL_RISK View의 non-empty Segment가 "
                                + "공식 Legal Cross 조건을 충족하지 않습니다. "
                                + "sajuYear="
                                + normalizedSegment.sajuYear()
                                + ", monthIndex="
                                + normalizedSegment.monthIndex()
                );
            }

            anyCurrentTimelineCrossEstablished =
                    true;

            result.add(
                    createCurrentTimelineMatch(
                            normalizedSegment
                    )
            );
        }


        /*
         * =================================================
         * Available View Contract
         * =================================================
         */

        if (
                !natalCrossEstablished
                        && !anyCurrentTimelineCrossEstablished
        ) {

            throw new IllegalStateException(
                    "사용 가능한 LEGAL_RISK View인데 "
                            + "LEGAL_RISK_CROSS_ESTABLISHED "
                            + "Deterministic Match를 만들 수 없습니다."
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * NATAL Match
     * =================================================
     */

    private ConsultationCategoryDeterministicMatch
    createNatalMatch(

            List<LifeDomainRuleMatch> natalMatches

    ) {

        Objects.requireNonNull(
                natalMatches,
                "LEGAL_RISK NATAL sourceMatches가 필요합니다."
        );

        if (
                natalMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "LEGAL_RISK NATAL Cross Match에는 sourceMatches가 필요합니다."
            );
        }

        return new ConsultationCategoryDeterministicMatch(

                ConsultationCategory.LEGAL_RISK,

                ConsultationCategoryDeterministicLayer.NATAL,

                RULE_CODE,

                ConsultationCategoryDeterministicStatus.PRESSURE,

                null,

                null,

                List.of(),

                "원국의 재물·직업·관계 관련 주의 근거가 "
                        + "LEGAL_RISK 전용 복수 생활영역·독립 provenance "
                        + "Cross 자격 조건을 충족합니다.",

                List.copyOf(
                        natalMatches
                ),

                List.of(
                        "원국의 LEGAL_RISK 후보가 공식 Legal Cross Evaluator의 "
                                + "복수 LifeDomain·독립 non-AUXILIARY provenance 조건을 충족했습니다.",

                        "LEGAL_RISK_CROSS_ESTABLISHED의 PRESSURE는 "
                                + "Source Status 다수결이나 점수가 아니라 "
                                + "Legal 전용 주의 자격이 성립했다는 Category Rule 의미입니다.",

                        "이 결과는 실제 소송·처벌·분쟁·승소·패소 같은 "
                                + "법률 사건의 발생을 의미하지 않습니다.",

                        "서로 다른 Evidence 사이의 원인·결과·시간순서·강화 관계를 만들지 않습니다."
                )
        );
    }


    /*
     * =================================================
     * CURRENT_TIMELINE Match
     * =================================================
     */

    private ConsultationCategoryDeterministicMatch
    createCurrentTimelineMatch(

            ConsultationCategorySegmentEvidence segmentEvidence

    ) {

        Objects.requireNonNull(
                segmentEvidence,
                "LEGAL_RISK CURRENT_TIMELINE Segment Evidence가 필요합니다."
        );

        if (
                segmentEvidence.matches()
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "LEGAL_RISK CURRENT_TIMELINE Cross Match에는 "
                            + "Segment-local sourceMatches가 필요합니다."
            );
        }

        return new ConsultationCategoryDeterministicMatch(

                ConsultationCategory.LEGAL_RISK,

                ConsultationCategoryDeterministicLayer
                        .CURRENT_TIMELINE,

                RULE_CODE,

                ConsultationCategoryDeterministicStatus.PRESSURE,

                segmentEvidence.sajuYear(),

                segmentEvidence.monthIndex(),

                List.of(),

                "현재 명리 시점에서 원국과 해당 시점의 "
                        + "재물·직업·관계 관련 주의 근거가 "
                        + "LEGAL_RISK 전용 복수 생활영역·독립 provenance "
                        + "Cross 자격 조건을 충족합니다.",

                List.copyOf(
                        segmentEvidence.matches()
                ),

                List.of(
                        "원국 후보와 현재 Segment 후보가 공식 Legal Cross Evaluator의 "
                                + "복수 LifeDomain·독립 non-AUXILIARY provenance 조건을 충족했습니다.",

                        "CURRENT_TIMELINE sourceMatches에는 "
                                + "Category Deterministic Layer provenance 계약에 따라 "
                                + "현재 Segment-local Match만 보존합니다.",

                        "LEGAL_RISK_CROSS_ESTABLISHED의 PRESSURE는 "
                                + "Source Status 다수결이나 점수가 아니라 "
                                + "Legal 전용 주의 자격이 성립했다는 Category Rule 의미입니다.",

                        "이 결과는 현재 시점에 실제 법률 사건이 발생한다는 뜻이 아닙니다.",

                        "원국과 현재 시점 Evidence 사이의 "
                                + "사건 인과·시간순서·강화 관계를 새로 만들지 않습니다."
                )
        );
    }
}