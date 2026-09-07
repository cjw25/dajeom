package com.dajeom.domain;

/**
 * 傷官格 성패 판정용 실제 구조 신호.
 *
 * =================================================
 *
 * 단순 존재와 실제 작용을 분리한다.
 *
 * 正官 존재
 * != 傷官見官의 실제 충돌
 *
 * 偏正印 존재
 * != 傷官佩印 성립
 *
 * 七殺 존재
 * != 傷官制殺 / 煞印 구조 성립
 *
 * 財 존재
 * != 傷官生財 완성
 *
 * =================================================
 *
 * 이 객체는 관계를 직접 계산하지 않는다.
 *
 * 이후:
 *
 * - StemRelation
 * - BranchRelation
 * - 통근
 * - 투출
 * - 합거
 * - 위치
 * - 월령
 *
 * 등을 읽는 Adapter가 실제 신호를 만든다.
 *
 * =================================================
 *
 * 점수 / 가중치 / 퍼센트 없음.
 */
public record HurtingOfficerPatternStructuralSignals(

        /**
         * 월령 傷官의 기반이 실제로 안정적인가.
         */
        boolean hurtingOfficerFoundationStable,

        /**
         * 財가 실제 根을 가지고 기능할 수 있는가.
         *
         * 傷官生財 검토용.
         */
        boolean wealthFoundationStable,

        /**
         * 傷官 → 財 흐름이
         * 실제 배치상 막히지 않고 이어지는가.
         */
        boolean hurtingOfficerGeneratesWealthIntact,

        /**
         * 印이 실제 根을 가지고 기능하는가.
         *
         * 傷官佩印 검토용.
         */
        boolean resourceFoundationStable,

        /**
         * 印이 실제 傷官을 적절히 제어하는가.
         *
         * 단순 印 존재와 구분한다.
         */
        boolean resourceActuallyControlsHurtingOfficer,

        /**
         * 傷官佩印을 쓰려 하지만
         *
         * 傷官이 가볍고
         * 身·印이 오히려 강하여
         *
         * 傷輕身旺의 구조가 실제 형성되는가.
         */
        boolean hurtingOfficerTooLightForResourceRoute,

        /**
         * 身弱 傷旺 구조에서
         * 七殺 → 印 → 身의 흐름이 실제 성립하는가.
         *
         * 傷官旺、身主弱而透煞印.
         */
        boolean killingGeneratesResourceToSupportDayMaster,

        /**
         * 財 없이 傷官과 七殺을 함께 쓰는 구조가
         * 실제로 정리되어 있는가.
         *
         * 傷官帶煞而無財.
         */
        boolean hurtingOfficerAndKillingUsableWithoutWealth,

        /**
         * 正官과 傷官이 실제로 충돌하는가.
         *
         * 단순 正官 존재와 구분한다.
         */
        boolean directOfficerActuallyConflictsWithHurtingOfficer,

        /**
         * 傷官見官이지만
         *
         * 傷官 → 財 → 官
         *
         * 흐름으로 실제 충돌을 해소하는가.
         */
        boolean officerConflictResolvedByWealthBridge,

        /**
         * 金水傷官에서 官을 실제로 사용할 수 있는
         * 조후·財印 보좌 구조가 갖춰졌는가.
         *
         * 단순히 金日主라는 이유로 true가 아니다.
         */
        boolean metalWaterOfficerRouteUsable,

        /**
         * 傷官生財의 財가 다른 천간과 합하여
         * 財의 기능에 흠이 생겼는가.
         *
         * 傷官生財而財又逢合.
         */
        boolean wealthCombined,

        /**
         * 傷官佩印의 印이 실제 손상을 입는가.
         *
         * 佩印而印又遭傷.
         */
        boolean resourceDamaged,

        /**
         * 傷官生財 구조에서 七殺이 투출하여
         * 財가 七殺을 돕는 실제 파격 Context가
         * 형성되는가.
         */
        boolean sevenKillingsDamagesWealthRoute,

        /**
         * 傷官生財에 개입한 七殺이
         * 합거되어 財가 보존되는가.
         *
         * 傷官生財透煞而煞逢合.
         */
        boolean sevenKillingsCombinedAway,

        /**
         * 煞印을 이용하는 傷官格에 財가 개입하여
         * 印을 손상시키는가.
         */
        boolean wealthDamagesKillingResourceRoute,

        /**
         * 正官과 七殺이 함께 투출했지만
         * 干頭取清 등으로 실제 정리가 되었는가.
         */
        boolean officerKillingsMixtureCleared

) {

    public boolean wealthRouteStable() {

        return wealthFoundationStable
                &&
                hurtingOfficerGeneratesWealthIntact;
    }

    public boolean resourceRouteStable() {

        return resourceFoundationStable
                &&
                resourceActuallyControlsHurtingOfficer;
    }

    public boolean officerConflictResolved() {

        return officerConflictResolvedByWealthBridge
                ||
                metalWaterOfficerRouteUsable;
    }
}