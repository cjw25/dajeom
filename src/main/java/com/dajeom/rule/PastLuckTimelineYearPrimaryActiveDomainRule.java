package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PAST_LUCK
 * TIMELINE_YEAR PRIMARY_ACTIVE_DOMAIN.
 *
 * =================================================
 *
 * 과거의 실제 사건을 역추정하지 않는다.
 *
 * 이미 계산된 과거 Timeline에서
 * 시간적으로 가장 분명한 생활영역의
 * 작동축을 deterministic하게 식별한다.
 *
 * =================================================
 *
 * 선택된 Domain이:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * 중 어떤 방향인지는 그대로 보존한다.
 *
 * 실제 과거의:
 *
 * 성공
 * 실패
 * 사건
 * 체감 강도
 *
 * 를 생성하지 않는다.
 */
@Component
public class PastLuckTimelineYearPrimaryActiveDomainRule
        extends AbstractTimelineYearPrimaryActiveDomainRule {

    private static final String RULE_CODE =
            "PAST_LUCK_TIMELINE_YEAR_PRIMARY_ACTIVE_DOMAIN";


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
                "PAST_LUCK의 PRIMARY_ACTIVE_DOMAIN은 실제 과거에 무엇이 가장 중요했는지를 사후적으로 추측하는 것이 아니라 계산된 과거 Timeline에서 시간적 작동 자격이 가장 분명한 생활영역만 식별합니다."
        );
    }
}