package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FUTURE_LUCK
 * TIMELINE_YEAR TURNING_POINT.
 *
 * =================================================
 *
 * 미래 명리연도들의:
 *
 * BIG_FLOW
 * PRIMARY_ACTIVE_DOMAIN
 * PRIMARY_PRESSURE_DOMAIN
 * CROSS_DOMAIN_PATTERN
 *
 * 구조적 변화를 비교한다.
 *
 * =================================================
 *
 * TURNING_POINT가 존재한다고 해서:
 *
 * - 직업 변경 예정
 * - 결혼 예정
 * - 큰돈 발생 예정
 * - 사고 예정
 *
 * 같은 사건을 생성하지 않는다.
 *
 * =================================================
 *
 * 의미:
 *
 * 기존 운 구조와 다른
 * deterministic 패턴 구간으로
 * 진입하거나,
 *
 * 전후 연도와 구별되는
 * 일시적인 구조적 Pivot이
 * 확인된다는 뜻이다.
 */
@Component
public class FutureLuckTimelineYearTurningPointRule
        extends AbstractTimelineYearTurningPointRule {

    private static final String RULE_CODE =
            "FUTURE_LUCK_TIMELINE_YEAR_TURNING_POINT";


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
                "FUTURE_LUCK의 TURNING_POINT는 특정 미래 사건을 예언하는 것이 아니라 계산된 미래 명리연도들 사이에서 운의 구조적 작동 방식이 바뀌는 구간만 식별합니다."
        );
    }
}