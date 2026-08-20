package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PAST_LUCK
 * TIMELINE_YEAR TURNING_POINT.
 *
 * =================================================
 *
 * 과거 실제 사건을 알고 있다고 가정하지 않는다.
 *
 * 이전 / 현재 / 다음 명리연도의
 * deterministic 운 구조 변화만 비교한다.
 *
 * =================================================
 *
 * TURNING_POINT가 검출되어도:
 *
 * - 실제 이직
 * - 결혼
 * - 이별
 * - 손실
 * - 건강 사건
 *
 * 등이 발생했다고 역추정하지 않는다.
 */
@Component
public class PastLuckTimelineYearTurningPointRule
        extends AbstractTimelineYearTurningPointRule {

    private static final String RULE_CODE =
            "PAST_LUCK_TIMELINE_YEAR_TURNING_POINT";


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
                "PAST_LUCK의 TURNING_POINT는 실제 과거 사건을 사후적으로 끼워 맞추는 것이 아니라 계산된 과거 명리연도들 사이의 deterministic 구조 변화만 식별합니다."
        );
    }
}