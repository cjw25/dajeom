package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PAST_LUCK
 * TIMELINE_YEAR CROSS_DOMAIN_PATTERN.
 *
 * =================================================
 *
 * 계산된 과거 Timeline에서
 * 서로 다른 LifeDomain의 방향이
 * 어떤 구조로 함께 나타났는지만 보존한다.
 *
 * =================================================
 *
 * 실제 과거에:
 *
 * - 무슨 일이 있었는지
 * - 어떤 분야가 실제로 더 힘들었는지
 * - 어떤 사건들이 동시에 발생했는지
 *
 * 를 역추정하지 않는다.
 */
@Component
public class PastLuckTimelineYearCrossDomainPatternRule
        extends AbstractTimelineYearCrossDomainPatternRule {

    private static final String RULE_CODE =
            "PAST_LUCK_TIMELINE_YEAR_CROSS_DOMAIN_PATTERN";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.PAST_LUCK;
    }


    @Override
    public String code() {

        return RULE_CODE;
    }


    @Override
    protected String flowLabel() {

        return "PAST_LUCK";
    }


    @Override
    protected void appendTemporalReason(

            List<String> reasons

    ) {

        reasons.add(
                "PAST_LUCK의 CROSS_DOMAIN_PATTERN은 실제 과거 사건들을 사주 구조에 사후적으로 연결하는 것이 아니라 계산된 과거 Timeline에서 서로 다른 생활영역의 deterministic 방향이 어떻게 공존했는지만 보존합니다."
        );
    }
}