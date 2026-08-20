package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PAST_LUCK
 * TIMELINE_YEAR OVERALL_NARRATIVE.
 *
 * =================================================
 *
 * 계산된 과거 명리연도의:
 *
 * BIG_FLOW
 * PRIMARY_ACTIVE_DOMAIN
 * PRIMARY_PRESSURE_DOMAIN
 * CROSS_DOMAIN_PATTERN
 * TURNING_POINT
 *
 * 을 하나의 최종 deterministic 계약으로 묶는다.
 *
 * =================================================
 *
 * 과거 실제 사건을 알고 있다고 가정하지 않는다.
 *
 * 따라서 최종 narrative에서도:
 *
 * - 실제 이직했다
 * - 연애가 끝났다
 * - 돈을 잃었다
 * - 건강 문제가 있었다
 *
 * 같은 사건을 사후적으로 생성하지 않는다.
 */
@Component
public class PastLuckTimelineYearOverallNarrativeRule
        extends AbstractTimelineYearOverallNarrativeRule {

    private static final String RULE_CODE =
            "PAST_LUCK_TIMELINE_YEAR_OVERALL_NARRATIVE";


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
                "PAST_LUCK의 OVERALL_NARRATIVE는 계산된 과거 운 구조를 최종 정리할 뿐 실제 과거 사건이나 사용자의 경험을 역추정하지 않습니다."
        );
    }
}