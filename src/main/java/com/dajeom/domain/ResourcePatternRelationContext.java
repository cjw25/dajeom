package com.dajeom.domain;

/**
 * 印格 StructuralSignals 중
 * raw 오행 생극 그래프만으로 확정할 수 없는
 * 실제 배치/작용 Context.
 *
 * =================================================
 *
 * 이후 실제:
 *
 * - 통근
 * - 천간합
 * - 지지 합충형파해
 * - 위치
 * - 월령 기반
 *
 * 등을 읽는 Adapter가 생성한다.
 *
 * =================================================
 *
 * false는
 *
 * "반대 구조가 확정됐다"
 *
 * 가 아니라
 *
 * "현재 단계에서 성립이 확인되지 않았다"
 *
 * 는 뜻으로 사용한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
public record ResourcePatternRelationContext(

        /**
         * 월령 印의 기반이 실제로 유지되는가.
         */
        boolean resourceFoundationStable,

        /**
         * raw 官 → 印 → 身 연결이
         * 실제 위치/기반상 사용할 수 있는가.
         */
        boolean officerResourceChainUsable,

        /**
         * raw 七殺 → 印 → 身 연결이
         * 실제 殺印相生으로 사용할 수 있는가.
         */
        boolean killingResourceChainUsable,

        /**
         * 身强 + 印重의 실제 과다 구조가
         * 확인되었는가.
         *
         * 단순 strongSide만으로 만들지 않는다.
         */
        boolean excessiveResourceContext,

        /**
         * 官殺混雜 상태에서
         * 七殺이 실제 합거되었는가.
         */
        boolean mixedSevenKillingsClearedByCombination,

        /**
         * 官殺混雜 상태가
         * 制 등의 구조로 실제 정리되었는가.
         */
        boolean mixedOfficerKillingsControlled,

        /**
         * 財 → 印 raw 극 관계가
         * 실제 財破印으로 작동하는가.
         */
        boolean wealthResourceConflictActive,

        /**
         * 財가 印을 깨는 것이 아니라
         * 과다한 印을 적절히 조절하는 用으로
         * 실제 작동하는가.
         */
        boolean wealthModeratesExcessResource,

        /**
         * 財가 합거되어
         * 印이 실제 보존되는가.
         *
         * 合財存印.
         */
        boolean wealthCombinedAwayPreservingResource,

        /**
         * 偏印 → 食神 raw 극 관계가
         * 실제 梟奪食으로 작동하는가.
         */
        boolean indirectResourceSuppressionActive,

        /**
         * 신약 구조에서 食傷의 설기가
         * 실제로 과도하게 작동하는가.
         */
        boolean outputDrainWeakStructureActive

) {

    public static ResourcePatternRelationContext unresolved() {

        return new ResourcePatternRelationContext(
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