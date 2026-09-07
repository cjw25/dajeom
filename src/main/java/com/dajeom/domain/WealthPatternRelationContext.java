package com.dajeom.domain;

/**
 * 財格 StructuralSignals 중
 * 순수 오행 생극 그래프만으로는 확정할 수 없는
 * 관계 Context.
 *
 * =================================================
 *
 * 이 객체의 값은 이후:
 *
 * - StemRelation
 * - BranchRelation
 * - 실제 천간합 종류
 * - 위치 관계
 *
 * 를 읽는 Adapter에서 만들어진다.
 *
 * =================================================
 *
 * false의 의미:
 *
 * "반드시 반대 구조다"가 아니라
 * "현재 관계 계층에서 성립이 확인되지 않았다"
 * 로 사용한다.
 *
 * 숫자 / 점수 / 확률 없음.
 */
public record WealthPatternRelationContext(

        /**
         * 財와 印이 함께 존재하지만
         * 위치상 서로 직접 방해하지 않는 것이
         * 확인되었는가.
         *
         * 財印不相礙.
         */
        boolean wealthAndResourceMutuallyNonObstructing,

        /**
         * 財格에 드러난 七殺이
         * 실제 천간합 등에 의해 제거되고
         * 財는 보존되는 구조인가.
         *
         * 存財而合殺.
         */
        boolean sevenKillingsCombinedAwayPreservingWealth,

        /**
         * 財가 생하는 正官이
         * 실제 천간합을 만나
         * 官의 기능에 흠이 생겼는가.
         *
         * 財旺生官而官逢合 Context.
         */
        boolean generatedOfficerCombined

) {

    /**
     * 아직 실제 합/위치 Adapter가
     * 연결되지 않은 상태.
     */
    public static WealthPatternRelationContext unresolved() {

        return new WealthPatternRelationContext(
                false,
                false,
                false
        );
    }
}