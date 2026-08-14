package com.dajeom.rule;

import com.dajeom.model.ShinsalRuleContext;
import com.dajeom.model.ShinsalRuleMatch;
import com.dajeom.type.ShinsalRuleBasis;
import com.dajeom.type.ShinsalType;

import java.util.List;
import java.util.Set;

/**
 * 정밀화 17차-1.
 *
 * 모든 deterministic 신살 Rule의
 * 공통 Interface.
 *
 * =================================================
 *
 * Rule 구현체 책임:
 *
 * 자신의 신살 표만 계산한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 길흉 점수
 * - 용신 변경
 * - 격국 변경
 * - 신강신약 변경
 * - 십성 구조 변경
 * - 다른 신살 Rule 호출
 * - LLM 해석
 */
public interface ShinsalRule {

    /**
     * 신살 종류.
     */
    ShinsalType type();

    /**
     * deterministic Rule 식별자.
     *
     * 예:
     *
     * TIAN_YI_DAY_STEM_BRANCH_V1
     */
    String code();

    /**
     * 해당 Rule에서 사용하는 기준점.
     */
    Set<ShinsalRuleBasis> bases();

    /**
     * 적용한 유파/표/규칙 버전 식별자.
     *
     * 신살은 기준 차이가 존재할 수 있으므로
     * Evidence에 반드시 남긴다.
     *
     * 예:
     *
     * DAJEOM_TIAN_YI_DAY_STEM_V1
     */
    String conventionId();

    /**
     * 실제 deterministic 판정.
     */
    List<ShinsalRuleMatch> evaluate(
            ShinsalRuleContext context
    );
}