package com.dajeom.domain;

/**
 * 正官格 판정 중
 * 순수 오행 생극 그래프만으로 확정할 수 없는
 * 실제 관계 Context.
 *
 * =================================================
 *
 * 이후 실제:
 *
 * - StemRelation
 * - BranchRelation
 * - BranchGroupRelation
 * - 천간합
 * - 지지 충/형/파/해
 * - 위치 관계
 *
 * 를 읽는 Adapter가 이 객체를 만든다.
 *
 * =================================================
 *
 * false는
 *
 * "반대 구조가 확정됐다"
 *
 * 가 아니라
 *
 * "현재 관계 계층에서 성립이 확인되지 않았다"
 *
 * 로 사용한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 확률 없음.
 */
public record DirectOfficerPatternRelationContext(

        /**
         * 투출한 正官 자체가
         * 다른 천간과 실제 合되어 있는가.
         *
         * 透官而又逢合.
         */
        boolean officerStemCombined,

        /**
         * 월령 지지가 실제 沖을 받는가.
         */
        boolean monthCommandClashed,

        /**
         * 월령 지지가 실제 刑을 받는가.
         */
        boolean monthCommandPunished,

        /**
         * 월령 지지가 실제 破를 받는가.
         */
        boolean monthCommandBroken,

        /**
         * 월령 지지가 실제 害를 받는가.
         */
        boolean monthCommandHarmed,

        /**
         * 印이 투출되어 傷官을 제어할 수 있는
         * 위치 배치가 실제 성립하는가.
         *
         * raw 오행상
         *
         * 印 → 傷官
         *
         * 이 있다고 해서 자동 true가 아니다.
         */
        boolean resourcePlacementSupportsHurtingOfficerResolution,

        /**
         * 官殺混雜 상태에서
         * 七殺이 실제 合去되어
         * 正官이 남는가.
         *
         * 合殺留官.
         */
        boolean mixedSevenKillingsClearedByCombination,

        /**
         * 월령의 충/형/파/해가
         * 회합 등의 구조로 실제 해소되는가.
         */
        boolean monthCommandDisruptionResolvedByCombination,

        /**
         * 財와 印이 함께 존재할 때
         * 위치상 서로 직접 방해하지 않는가.
         *
         * 財印不相礙.
         */
        boolean wealthAndResourceMutuallyNonObstructing

) {

    /**
     * 실제 StemRelation / BranchRelation Adapter가
     * 아직 연결되지 않은 상태.
     */
    public static DirectOfficerPatternRelationContext unresolved() {

        return new DirectOfficerPatternRelationContext(
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