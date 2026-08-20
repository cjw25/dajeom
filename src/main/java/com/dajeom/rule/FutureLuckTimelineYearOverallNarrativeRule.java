package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FUTURE_LUCK
 * TIMELINE_YEAR OVERALL_NARRATIVE.
 *
 * =================================================
 *
 * 계산된 미래 명리연도의:
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
 * 이 결과는 미래 사건 예측 목록이 아니다.
 *
 * =================================================
 *
 * 예:
 *
 * BIG_FLOW = MIXED
 *
 * PRIMARY_ACTIVE_DOMAIN = CAREER / PRESSURE
 *
 * ->
 *
 * "직업운이 나빠진다"
 *
 * 가 아니다.
 *
 * =================================================
 *
 * 공식 의미:
 *
 * 전체 연도 방향은 MIXED이고
 * 그 안에서 CAREER의 PRESSURE 작동이
 * 상대적으로 분명한 축이다.
 *
 * =================================================
 *
 * 마찬가지로:
 *
 * TURNING_POINT=true
 *
 * ->
 *
 * 이직 / 결혼 / 손실 / 사고가 예정됨
 *
 * 이 아니다.
 *
 * 전후 명리연도와 비교해
 * deterministic 운 구조의 변화가
 * 확인되었다는 의미다.
 */
@Component
public class FutureLuckTimelineYearOverallNarrativeRule
        extends AbstractTimelineYearOverallNarrativeRule {

    private static final String RULE_CODE =
            "FUTURE_LUCK_TIMELINE_YEAR_OVERALL_NARRATIVE";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.FUTURE_LUCK;
    }


    @Override
    public String code() {

        return RULE_CODE;
    }


    @Override
    protected String flowLabel() {

        return "FUTURE_LUCK";
    }


    @Override
    protected void appendTemporalReason(

            List<String> reasons

    ) {

        reasons.add(
                "FUTURE_LUCK의 OVERALL_NARRATIVE는 미래 사건을 생성하는 것이 아니라 계산된 미래 운의 연도별 Functional 구조를 OpenAI가 임의 재계산하지 못하도록 최종 deterministic 계약으로 정리합니다."
        );
    }
}