package com.dajeom.domain;

/**
 * 印格 성패 판정에 필요한 실제 구조 신호.
 *
 * =================================================
 *
 * 단순 십성 존재와 실제 작용을 분리한다.
 *
 * 예:
 *
 * 財 존재
 * != 財破印
 *
 * 七殺 존재
 * != 印格破
 *
 * 偏印 존재
 * != 梟神奪食
 *
 * =================================================
 *
 * 이 객체 자체는 관계를 계산하지 않는다.
 *
 * 이후 실제:
 *
 * - StemRelation
 * - BranchRelation
 * - 통근
 * - 투출
 * - 합거
 * - 위치 관계
 *
 * 를 연결하는 Adapter가 생성한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
public record ResourcePatternStructuralSignals(

        /**
         * 월령 印의 기반이 실제로 유지되고 있는가.
         *
         * 충/합화 등으로 원래 印의 기반이
         * 크게 흔들리지 않은 상태.
         */
        boolean resourceFoundationStable,

        /**
         * 官 → 印 흐름이 실제로 유지되는가.
         *
         * 官이 합거되거나 다른 구조로
         * 기능을 잃지 않은 官印相生 Context.
         */
        boolean officerResourceChainIntact,

        /**
         * 七殺이 印을 생하는 방향으로
         * 적절하게 사용되는 구조인가.
         *
         * 단순 七殺 존재가 아니다.
         */
        boolean sevenKillingsAppropriatelySupportsResource,

        /**
         * 身印이 이미 강한 구조에서
         * 七殺이 추가로 印을 생하여
         * 실제 과다 Context를 만드는가.
         *
         * 身強印重而透煞 검토용.
         */
        boolean strongResourceWithExcessKilling,

        /**
         * 七殺과 食傷이 함께 있을 때
         * 食傷이 실제 七殺을 조절하고 있는가.
         */
        boolean sevenKillingsControlledByOutput,

        /**
         * 官殺混雜에서 七殺이 합거되어
         * 官印 구조가 맑아졌는가.
         */
        boolean mixedSevenKillingsClearedByCombination,

        /**
         * 官殺混雜이 制 등의 실제 구조로
         * 정리되었는가.
         */
        boolean mixedOfficerKillingsControlled,

        /**
         * 身強印重 상태에서 財가
         * 印의 과다를 적절히 제어하는가.
         *
         * 印多用財 Context.
         */
        boolean wealthModeratesExcessResource,

        /**
         * 財가 실제 印을 손상시키는가.
         *
         * 印輕逢財 / 貪財破印 Context.
         *
         * 단순 財 투출과 구분한다.
         */
        boolean wealthActuallyDamagesResource,

        /**
         * 財破印 Context에서 劫財가
         * 실제 財를 제어하여 印을 구하는가.
         *
         * 劫財救印.
         */
        boolean wealthDamageResolvedByRobWealth,

        /**
         * 財가 합거되어 印이 보존되는가.
         *
         * 合財存印.
         */
        boolean wealthCombinedAwayPreservingResource,

        /**
         * 食神이 투출된 구조에서
         * 偏印/梟이 실제 食神을 제압하는가.
         *
         * 단순 偏印 존재와 구분한다.
         */
        boolean indirectResourceActuallySuppressesEatingGod,

        /**
         * 身이나 印이 충분치 않은 상태에서
         * 食傷이 실제로 과도하게 설기시키는가.
         */
        boolean outputExcessivelyDrainsWeakStructure

) {

    public boolean wealthDamageResolved() {

        return wealthDamageResolvedByRobWealth
                ||
                wealthCombinedAwayPreservingResource;
    }

    public boolean mixedOfficerKillingsResolved() {

        return mixedSevenKillingsClearedByCombination
                ||
                mixedOfficerKillingsControlled;
    }
}