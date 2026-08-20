package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FUTURE_LUCK
 * TIMELINE_YEAR PRIMARY_PRESSURE_DOMAIN.
 *
 * =================================================
 *
 * 미래에 특정 사건이 발생한다고
 * 예측하지 않는다.
 *
 * 현재 Rule은 이미 계산된 미래 Timeline에서
 * 명시적인 PRESSURE 구조가 존재하고,
 *
 * 다른 PRESSURE Domain과 비교했을 때
 * 하나의 Domain이 deterministic qualification
 * 기준으로 명확하게 우세할 때만
 *
 * 주된 부담축을 식별한다.
 */
@Component
public class FutureLuckTimelineYearPrimaryPressureDomainRule
        extends AbstractTimelineYearPrimaryPressureDomainRule {

    private static final String RULE_CODE =
            "FUTURE_LUCK_TIMELINE_YEAR_PRIMARY_PRESSURE_DOMAIN";


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
                "FUTURE_LUCK의 PRIMARY_PRESSURE_DOMAIN은 미래의 실패·손실·갈등 사건을 예측하는 것이 아니라 계산된 미래 Timeline에서 상대적으로 분명한 주된 부담 Functional Domain을 식별합니다."
        );
    }
}