package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PAST_LUCK
 * TIMELINE_YEAR PRIMARY_PRESSURE_DOMAIN.
 *
 * =================================================
 *
 * 과거에 실제 무슨 사건이 있었는지를
 * 사주 결과에서 역으로 만들어내지 않는다.
 *
 * 현재 Rule은 이미 계산된 과거 Timeline의
 * 명시적 PRESSURE 구조 중
 *
 * 구조적으로 하나의 주된 부담 Domain을
 * 식별할 자격이 있을 때만
 * deterministic Match를 생성한다.
 */
@Component
public class PastLuckTimelineYearPrimaryPressureDomainRule
        extends AbstractTimelineYearPrimaryPressureDomainRule {

    private static final String RULE_CODE =
            "PAST_LUCK_TIMELINE_YEAR_PRIMARY_PRESSURE_DOMAIN";


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
                "PAST_LUCK의 PRIMARY_PRESSURE_DOMAIN은 실제 과거 사건을 역추정하거나 당시 무엇이 가장 힘들었는지를 사실처럼 만들어내는 것이 아니라 계산된 과거 Timeline 안의 주된 deterministic 부담축만 식별합니다."
        );
    }
}