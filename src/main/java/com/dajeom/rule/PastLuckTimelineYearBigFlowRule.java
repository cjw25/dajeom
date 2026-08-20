package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PAST_LUCK 명리연도 BIG_FLOW Rule.
 *
 * =================================================
 *
 * 실제 Domain -> Year 종합 공식은
 * AbstractTimelineYearBigFlowRule에서
 * FUTURE_LUCK과 공유한다.
 *
 * =================================================
 *
 * PAST_LUCK에서 추가되는 의미:
 *
 * 이미 계산된 과거 Timeline의
 * deterministic 구조를 요약할 뿐이다.
 *
 * 실제 과거 사건을:
 *
 * - 만들어내지 않는다.
 * - 추측하지 않는다.
 * - 사주 구조에 끼워 맞추지 않는다.
 */
@Component
public class PastLuckTimelineYearBigFlowRule
        extends AbstractTimelineYearBigFlowRule {

    private static final String RULE_CODE =
            "PAST_LUCK_TIMELINE_YEAR_BIG_FLOW";


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
                "이 BIG_FLOW는 실제 과거 사건의 발생 여부를 역추정하거나 당시의 성공·실패를 사후적으로 끼워 맞추지 않고 해당 명리연도의 deterministic 생활영역 구조만 종합합니다."
        );
    }
}