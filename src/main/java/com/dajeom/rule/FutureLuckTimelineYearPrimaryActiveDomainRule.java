package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FUTURE_LUCK
 * TIMELINE_YEAR PRIMARY_ACTIVE_DOMAIN.
 *
 * =================================================
 *
 * 계산된 미래 Timeline에서
 * 어떤 생활영역이 시간적으로
 * 가장 분명한 작동축인지 식별한다.
 *
 * =================================================
 *
 * 이 Rule은:
 *
 * PRIMARY_ACTIVE_DOMAIN
 *
 * 을:
 *
 * "가장 좋은 분야"
 *
 * 또는
 *
 * "가장 나쁜 분야"
 *
 * 로 해석하지 않는다.
 *
 * =================================================
 *
 * 예:
 *
 * PRIMARY_ACTIVE_DOMAIN = CAREER
 * status = PRESSURE
 *
 * ->
 *
 * 직업 관련 부담 기능이
 * 상대적으로 분명한 작동축.
 *
 * 승진 실패 / 퇴사 예정 아님.
 *
 * =================================================
 *
 * PRIMARY_ACTIVE_DOMAIN = WEALTH
 * status = SUPPORTIVE
 *
 * ->
 *
 * 재물 영역의 지지 Functional 구조가
 * 상대적으로 분명한 작동축.
 *
 * 수익 확정 아님.
 */
@Component
public class FutureLuckTimelineYearPrimaryActiveDomainRule
        extends AbstractTimelineYearPrimaryActiveDomainRule {

    private static final String RULE_CODE =
            "FUTURE_LUCK_TIMELINE_YEAR_PRIMARY_ACTIVE_DOMAIN";


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
                "FUTURE_LUCK의 PRIMARY_ACTIVE_DOMAIN은 미래에 특정 사건이 발생할 영역을 예언하는 것이 아니라 계산된 미래 Timeline에서 시간적 Functional 작동이 상대적으로 가장 분명한 생활영역을 식별합니다."
        );
    }
}