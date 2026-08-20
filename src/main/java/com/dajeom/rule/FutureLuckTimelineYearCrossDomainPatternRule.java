package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FUTURE_LUCK
 * TIMELINE_YEAR CROSS_DOMAIN_PATTERN.
 *
 * =================================================
 *
 * 계산된 미래 Timeline에서
 * 서로 다른 LifeDomain의 deterministic 방향이
 * 어떤 구조로 함께 나타나는지 분류한다.
 *
 * =================================================
 *
 * 예:
 *
 * WEALTH = PRESSURE
 * CAREER = PRESSURE
 *
 * ->
 *
 * PRESSURE_RESONANCE
 *
 * =================================================
 *
 * 이것은:
 *
 * 돈 문제와 직업 문제가
 * 실제로 동시에 발생한다
 *
 * 는 사건 예측이 아니다.
 *
 * =================================================
 *
 * 마찬가지로:
 *
 * WEALTH = SUPPORTIVE
 * CAREER = PRESSURE
 *
 * ->
 *
 * DIRECTIONAL_CONFLICT
 *
 * 는:
 *
 * 돈은 좋아지고 직장은 나빠진다
 *
 * 같은 사건 확정이 아니다.
 *
 * 서로 다른 LifeDomain의
 * deterministic 방향이
 * 서로 다르다는 구조만 보존한다.
 */
@Component
public class FutureLuckTimelineYearCrossDomainPatternRule
        extends AbstractTimelineYearCrossDomainPatternRule {

    private static final String RULE_CODE =
            "FUTURE_LUCK_TIMELINE_YEAR_CROSS_DOMAIN_PATTERN";


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
                "FUTURE_LUCK의 CROSS_DOMAIN_PATTERN은 미래 사건들의 동시 발생을 예측하는 것이 아니라 계산된 미래 Timeline에서 서로 다른 생활영역의 deterministic 방향이 어떤 구조로 함께 존재하는지만 보존합니다."
        );
    }
}