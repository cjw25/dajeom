package com.dajeom.rule;

import com.dajeom.type.ConsultationCategory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FUTURE_LUCK 명리연도 BIG_FLOW Rule.
 *
 * =================================================
 *
 * PAST_LUCK과 동일한
 * Domain -> Year deterministic 합성 공식을 사용한다.
 *
 * 미래라는 이유로:
 *
 * - 더 강한 상태
 * - 더 약한 상태
 * - 확률
 * - 사건 발생 여부
 *
 * 를 새로 계산하지 않는다.
 *
 * =================================================
 *
 * FUTURE_LUCK에서 추가되는 의미:
 *
 * 계산된 미래 Timeline을
 * 가능성 사건 목록으로 변환하는 것이 아니라
 *
 * 각 연도의:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * 구조적 방향만 보존한다.
 *
 * =================================================
 *
 * 금지:
 *
 * PRESSURE
 * -> 실패 예정
 *
 * SUPPORTIVE
 * -> 성공 예정
 *
 * RELATIONSHIP PRESSURE
 * -> 이별 예정
 *
 * CAREER SUPPORTIVE
 * -> 승진 예정
 *
 * WEALTH PRESSURE
 * -> 손실 예정
 *
 * 같은 사건 예측.
 */
@Component
public class FutureLuckTimelineYearBigFlowRule
        extends AbstractTimelineYearBigFlowRule {

    private static final String RULE_CODE =
            "FUTURE_LUCK_TIMELINE_YEAR_BIG_FLOW";


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
                "이 BIG_FLOW는 미래 사건의 발생 여부나 성공·실패를 예측하지 않고 해당 명리연도에 계산된 deterministic 생활영역의 구조적 방향만 종합합니다."
        );
    }
}