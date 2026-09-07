package com.dajeom.domain;

/**
 * 正官格 성패 판정에 필요한
 * 구조 관계 신호.
 *
 * =================================================
 *
 * 이 객체 자체는 관계를 계산하지 않는다.
 *
 * 기존:
 *
 * - StemRelation
 * - BranchRelation
 * - BranchGroupRelation
 *
 * 등의 실제 관계 타입이 확인된 뒤
 * 별도 Adapter가 이 신호를 만든다.
 *
 * =================================================
 *
 * 숫자 없음.
 */
public record DirectOfficerStructuralSignals(

        /**
         * 천간에 드러난 正官 자체가
         * 다른 천간과 합되어 있는가.
         *
         * 子平真詮:
         * 透官而又逢合
         * → 成中有敗 Context
         */
        boolean officerStemCombined,

        /**
         * 월령/正官의 핵심 지지가 충을 받는가.
         */
        boolean monthCommandClashed,

        /**
         * 월령/正官의 핵심 지지가 형을 받는가.
         */
        boolean monthCommandPunished,

        /**
         * 월령/正官의 핵심 지지가 파를 받는가.
         */
        boolean monthCommandBroken,

        /**
         * 월령/正官의 핵심 지지가 해를 받는가.
         */
        boolean monthCommandHarmed,

        /**
         * 傷官이 존재하지만
         * 印의 작용으로 실제 해소되는 구조인가.
         *
         * 단순 印 존재가 아니라
         * 실제 배치까지 확인된 결과를 넣는다.
         */
        boolean hurtingOfficerResolvedByResource,

        /**
         * 官殺混雜 상태에서
         * 七殺이 합거되어 正官이 맑아졌는가.
         *
         * 合殺留官 Context.
         */
        boolean mixedSevenKillingsClearedByCombination,

        /**
         * 월령의 형충파해가
         * 회합 등의 구조로 실제 해소되었는가.
         */
        boolean monthCommandDisruptionResolvedByCombination,

        /**
         * 財와 印이 동시에 투출했을 때
         * 서로 직접 방해하지 않는 배치인가.
         *
         * 단순히 둘 다 있다는 이유로
         * 자동 成格하지 않기 위한 신호.
         */
        boolean wealthAndResourceMutuallyNonObstructing

) {

    public boolean hasMonthCommandDisruption() {

        return monthCommandClashed
                ||
                monthCommandPunished
                ||
                monthCommandBroken
                ||
                monthCommandHarmed;
    }
}