package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CAREER current annual context deterministic Rule.
 *
 * 각 Timeline Segment의 공식 Source Match를 1:1로 Category Match에 투영한다.
 * 같은 Segment에 여러 독립 Source Match가 있어도 하나의 대표 status로 합치지 않는다.
 * upstream Java가 이미 계산한 각 Match의 status와 의미 경계를 그대로 보존한다.
 */
@Component
public class CareerCurrentAnnualContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE = "CAREER_CURRENT_ANNUAL_CONTEXT";
    private static final String SOURCE_RULE_CODE = "CAREER_ANNUAL_CONTEXT";

    @Override
    public ConsultationCategory category() {
        return ConsultationCategory.CAREER;
    }

    @Override
    public String code() {
        return RULE_CODE;
    }

    @Override
    public List<ConsultationCategoryDeterministicMatch> evaluate(
            ConsultationCategoryEvidenceView view
    ) {
        Objects.requireNonNull(view, "CareerCurrentAnnualContextRule에는 ConsultationCategoryEvidenceView가 필요합니다.");

        if (view.category() != ConsultationCategory.CAREER) {
            throw new IllegalArgumentException("CareerCurrentAnnualContextRule은 CAREER View만 평가할 수 있습니다.");
        }

        if (view.unavailable() || !view.hasSegmentEvidences()) {
            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result = new ArrayList<>();

        for (ConsultationCategorySegmentEvidence segment : view.segmentEvidences()) {
            ConsultationCategorySegmentEvidence normalizedSegment = Objects.requireNonNull(
                    segment,
                    "CareerCurrentAnnualContextRule의 Segment Evidence는 null일 수 없습니다."
            );

            for (LifeDomainRuleMatch sourceMatch : normalizedSegment.matches()) {
                LifeDomainRuleMatch source = Objects.requireNonNull(
                        sourceMatch,
                        "CareerCurrentAnnualContextRule의 Segment Source Match는 null일 수 없습니다."
                );

                if (!SOURCE_RULE_CODE.equals(source.ruleCode())) {
                    continue;
                }

                if (source.domain() != LifeDomain.CAREER) {
                    throw new IllegalStateException(
                            "CareerCurrentAnnualContextRule Source Domain이 일치하지 않습니다. expected=CAREER, actual=" + source.domain()
                    );
                }

                ConsultationCategoryDeterministicStatus status = mapStatus(source);
                List<String> reasons = new ArrayList<>();
                reasons.add("현재 Segment의 공식 Source Match [" + SOURCE_RULE_CODE + "] 하나를 1:1로 보존했습니다.");
                reasons.add("upstream LifeDomain status=" + source.status().name() + "을 Category status=" + status.name() + "으로 직접 변환했습니다.");
                reasons.add("같은 Segment의 다른 독립 Source Match와 status·개수·summary를 합산하지 않았습니다.");
                reasons.add("다른 시간층 또는 Source Family와 새로운 인과·순서·강화·약화·상쇄·복합 의미를 생성하지 않았습니다.");
                reasons.add("upstream summary: " + source.summary());

                if (normalizedSegment.timelineUncertaintyExists()) {
                    reasons.add("현재 Timeline에 불확실성이 있으므로 가능한 Segment를 임의로 하나만 선택하지 않습니다.");
                }

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.CAREER,
                                ConsultationCategoryDeterministicLayer.CURRENT_TIMELINE,
                                RULE_CODE,
                                status,
                                normalizedSegment.sajuYear(),
                                normalizedSegment.monthIndex(),
                                List.of(),
                                "[" + SOURCE_RULE_CODE + "] 독립 Match를 보존합니다: " + source.summary(),
                                List.of(source),
                                List.copyOf(reasons)
                        )
                );
            }
        }

        return List.copyOf(result);
    }

    private ConsultationCategoryDeterministicStatus mapStatus(
            LifeDomainRuleMatch source
    ) {
        return switch (source.status()) {
            case SUPPORTIVE -> ConsultationCategoryDeterministicStatus.SUPPORTIVE;
            case PRESSURE -> ConsultationCategoryDeterministicStatus.PRESSURE;
            case MIXED -> ConsultationCategoryDeterministicStatus.MIXED;
            case CONTEXTUAL -> ConsultationCategoryDeterministicStatus.CONTEXTUAL;
        };
    }
}