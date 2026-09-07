package com.dajeom.domain;

/**
 * 지지 합 구조 자체의 성립 상태.
 *
 * 합화 여부와는 완전히 별개다.
 */
public enum BranchCombinationFormationState {

    /**
     * 六合 두 지지가 실제 존재.
     */
    SIX_HARMONY_RELATION,

    /**
     * 三合 세 지지가 모두 갖춰짐.
     *
     * 成局했다는 뜻이지
     * 곧바로 오행이 합화했다는 뜻은 아니다.
     */
    THREE_HARMONY_COMPLETE,

    /**
     * 方合 세 지지가 모두 갖춰짐.
     *
     * 역시 합화와 별개다.
     */
    DIRECTIONAL_HARMONY_COMPLETE
}