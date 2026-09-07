package com.dajeom.domain;

/**
 * 食神格 StructuralSignals 중
 * raw 십성 생극 그래프만으로 확정할 수 없는
 * 실제 구조 Context.
 *
 * =================================================
 *
 * 이후:
 *
 * - 통근
 * - 천간합
 * - 위치 관계
 * - 합거
 * - 월령 기반 안정성
 *
 * 등을 읽는 Adapter가 이 객체를 생성한다.
 *
 * =================================================
 *
 * false는
 *
 * "반대 구조가 확정됨"
 *
 * 이 아니라
 *
 * "현재 단계에서 성립이 확인되지 않음"
 *
 * 을 뜻한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
public record EatingGodPatternRelationContext(

        /**
         * 월령 食神의 기반이 실제 안정적인가.
         */
        boolean eatingGodFoundationStable,

        /**
         * 食神生財에서 사용되는 財가
         * 실제 근과 기반을 갖는가.
         */
        boolean wealthFoundationStable,

        /**
         * 偏印 → 食神 raw 극 관계가
         * 실제 梟神奪食으로 작동하는가.
         */
        boolean indirectResourceSuppressionActive,

        /**
         * 財가 梟을 제어하여
         * 食神을 보호할 수 있는 실제 배치인가.
         *
         * 生財護食.
         */
        boolean wealthProtectionRouteUsable,

        /**
         * 七殺 → 印 → 身 raw 연결이 있을 때
         *
         * 食神을 버리고
         * 七殺·印 구조를 사용하는 것이
         * 실제 가능한가.
         *
         * 棄食就殺而透印.
         */
        boolean abandoningEatingGodForKillingWithResourceUsable,

        /**
         * 食神이 七殺과 실제 천간합하여
         * 七殺을 제거하고 財를 보존하는가.
         *
         * 食神合殺存財.
         */
        boolean eatingGodCombinesKillingPreservingWealth,

        /**
         * 財와 七殺이 동시에 투출했지만
         *
         * 위치상 食神이 둘 사이에 작용하여
         * 財가 바로 七殺을 생하는 흐름을
         * 차단/조절할 수 있는가.
         */
        boolean eatingGodSeparatesWealthAndKilling,

        /**
         * 七殺 → 印 → 身 구조에서
         * 財가 같은 印을 실제 손상시켜
         * 煞印 구조를 깨는가.
         */
        boolean wealthDamageKillingResourceRouteActive,

        /**
         * 正官과 七殺이 함께 투출된 경우
         * 실제 배치상 食神格으로 정리할 수 있는가.
         */
        boolean officerKillingsMixtureUsable

) {

    public static EatingGodPatternRelationContext unresolved() {

        return new EatingGodPatternRelationContext(
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        );
    }
}