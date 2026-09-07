package com.dajeom.domain;

/**
 * 傷官格 StructuralSignals 중
 * raw 오행 생극 그래프만으로는
 * 최종 확정할 수 없는 실제 구조 Context.
 *
 * =================================================
 *
 * 이후:
 *
 * - 통근
 * - 천간합
 * - 위치 관계
 * - 지지 합충형파해
 * - 조후
 *
 * 등을 읽는 Adapter가 이 객체를 생성한다.
 *
 * =================================================
 *
 * false는
 *
 * "반대 구조가 확정되었다"
 *
 * 가 아니라
 *
 * "현재 단계에서 성립이 확인되지 않았다"
 *
 * 는 뜻이다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
public record HurtingOfficerPatternRelationContext(

        /**
         * 월령 傷官의 기반이 실제 안정적인가.
         */
        boolean hurtingOfficerFoundationStable,

        /**
         * 傷官生財에서 사용되는 財의 기반이
         * 실제 안정적인가.
         */
        boolean wealthFoundationStable,

        /**
         * raw 傷官 → 財 연결이
         * 실제 傷官生財로 사용할 수 있는가.
         */
        boolean wealthGenerationRouteUsable,

        /**
         * 傷官佩印에서 사용되는 印의 기반이
         * 실제 안정적인가.
         */
        boolean resourceFoundationStable,

        /**
         * raw 印 → 傷官 관계가
         * 실제 傷官을 제어하는 구조로
         * 작동하는가.
         */
        boolean resourceControlRouteUsable,

        /**
         * 傷官佩印을 쓰기에는
         * 傷官이 가볍고 身印이 강한 구조인가.
         *
         * 傷輕身旺.
         */
        boolean hurtingOfficerTooLightForResourceRoute,

        /**
         * raw 七殺 → 印 → 身 연결이
         * 실제 煞印 구조로 사용할 수 있는가.
         */
        boolean killingResourceChainUsable,

        /**
         * 財 없이 傷官과 七殺을 함께 쓰는
         * 실제 구조가 가능한가.
         *
         * 傷官帶煞而無財.
         */
        boolean hurtingOfficerKillingRouteUsableWithoutWealth,

        /**
         * raw 傷官 → 正官 관계가
         * 실제 傷官見官의 충돌로 작동하는가.
         */
        boolean directOfficerConflictActive,

        /**
         * raw 傷官 → 財 → 正官 연결이
         * 실제 통관 구조로 작동 가능한가.
         */
        boolean officerConflictWealthBridgeUsable,

        /**
         * 金水傷官에서
         * 正官을 실제 사용할 수 있는
         * 조후·財印 보좌 조건이 확인되었는가.
         */
        boolean metalWaterOfficerRouteUsable,

        /**
         * 傷官生財에서 사용되는 財가
         * 실제 천간합 등에 묶였는가.
         */
        boolean wealthCombined,

        /**
         * 傷官佩印에서 사용되는 印이
         * 실제 손상을 입었는가.
         */
        boolean resourceDamaged,

        /**
         * raw 傷官 → 財 → 七殺 연결이
         * 실제 生財帶煞의 파격 방향으로
         * 작동하는가.
         */
        boolean wealthKillingRouteDamaging,

        /**
         * 財가 생하는 七殺이
         * 실제 합거되었는가.
         */
        boolean sevenKillingsCombinedAway,

        /**
         * 七殺 → 印 → 身 구조의 印을
         * 財가 실제 손상시키는가.
         */
        boolean wealthDamageKillingResourceRouteActive,

        /**
         * 正官과 七殺이 함께 드러났지만
         * 실제 取清이 되었는가.
         */
        boolean officerKillingsMixtureCleared

) {

    public static HurtingOfficerPatternRelationContext unresolved() {

        return new HurtingOfficerPatternRelationContext(
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
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