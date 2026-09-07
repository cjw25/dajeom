package com.dajeom.domain;

/**
 * 財格의 성패 판정에 필요한
 * 실제 구조 관계 신호.
 *
 * =================================================
 *
 * 이 객체 자체는 관계를 계산하지 않는다.
 *
 * 기존:
 *
 * - StemRelation
 * - BranchRelation
 * - TenGod 구조
 * - 위치 관계
 *
 * 등을 읽는 Adapter가 이후 이 신호를 만든다.
 *
 * =================================================
 *
 * 단순 존재와 실제 작용을 구분하기 위한 구조다.
 *
 * 예:
 *
 * 比劫이 존재
 * != 財를 실제로 奪함
 *
 * 印이 존재
 * != 財와 印이 서로 방해함
 *
 * 食神이 존재
 * != 七殺을 실제로 制함
 *
 * =================================================
 *
 * 숫자 / 점수 없음.
 */
public record WealthPatternStructuralSignals(

        /**
         * 財와 印이 동시에 투출했지만
         * 위치상 서로 직접 방해하지 않는가.
         *
         * 財格佩印 성립 검토용.
         */
        boolean wealthAndResourceMutuallyNonObstructing,

        /**
         * 比肩/劫財가 실제 財를 다투거나
         * 극하는 배치가 형성되어 있는가.
         *
         * 단순 比劫 존재와 구분한다.
         */
        boolean wealthContestedByCompanionOrRobWealth,

        /**
         * 財를 다투는 比劫이 있으나
         * 食神이 투출하여 그 기세를 설하고
         * 財로 이어주는 구조인가.
         *
         * 財逢劫而透食以化
         */
        boolean robberyResolvedByEatingGod,

        /**
         * 財를 다투는 比劫이 있으나
         * 官이 작용하여 比劫을 제하는 구조인가.
         *
         * 生官以制之
         */
        boolean robberyControlledByOfficer,

        /**
         * 財格에 七殺이 투출했으나
         * 食神이 실제로 七殺을 제하고
         * 財를 살리는 구조인가.
         *
         * 食神制殺以生財
         */
        boolean sevenKillingsControlledByEatingGod,

        /**
         * 七殺이 합거되고
         * 財는 보존되는 구조인가.
         *
         * 存財而合殺
         */
        boolean sevenKillingsCombinedAwayPreservingWealth,

        /**
         * 財生官 구조에서
         * 傷官이 실제 官을 손상시키는가.
         */
        boolean generatedOfficerDamagedByHurtingOfficer,

        /**
         * 財生官 구조에서
         * 생성된 官이 다른 천간과 합되어
         * 官의 기능이 흔들리는가.
         */
        boolean generatedOfficerCombined

) {

    public boolean robberyResolved() {

        return robberyResolvedByEatingGod
                ||
                robberyControlledByOfficer;
    }

    public boolean sevenKillingsResolved() {

        return sevenKillingsControlledByEatingGod
                ||
                sevenKillingsCombinedAwayPreservingWealth;
    }

    public boolean generatedOfficerHasDefect() {

        return generatedOfficerDamagedByHurtingOfficer
                ||
                generatedOfficerCombined;
    }
}