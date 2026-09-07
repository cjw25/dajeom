package com.dajeom.domain;

/**
 * 七殺格 StructuralSignals 중
 * raw 십성 생극 그래프만으로 최종 확정할 수 없는
 * 실제 구조 Context.
 *
 * =================================================
 *
 * 이후:
 *
 * - 통근
 * - 천간합
 * - 지지 합충형파해
 * - 위치 관계
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
 * 는 의미다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
public record SevenKillingsPatternRelationContext(

        /**
         * 월령 MAIN 七殺의 기반이
         * 실제 안정적인가.
         */
        boolean sevenKillingsFoundationStable,

        /**
         * 食神 자체가 실제 근과 기반을
         * 가지고 있는가.
         */
        boolean eatingGodFoundationStable,

        /**
         * raw 食神 → 七殺 관계가
         * 실제 制殺로 작동 가능한가.
         */
        boolean eatingGodControlRouteUsable,

        /**
         * 殺印相生에서 사용하는 印의
         * 실제 기반이 안정적인가.
         */
        boolean resourceFoundationStable,

        /**
         * raw 七殺 → 印 → 身 연결이
         * 실제 殺印相生으로 사용할 수 있는가.
         */
        boolean killingResourceChainUsable,

        /**
         * 印 → 食神 raw 극 관계가
         * 실제 食神制殺을 손상시키는
         * 印奪食으로 작동하는가.
         */
        boolean resourceSuppressionActive,

        /**
         * 財가 위 印을 제어하여
         * 食神을 실제 보존하는 구조가
         * 가능한가.
         *
         * 財去印存食.
         */
        boolean wealthRemovalRouteUsable,

        /**
         * 財 → 七殺 → 印 → 身의
         * raw chain이 실제 순환 구조로
         * 사용할 수 있는가.
         */
        boolean wealthKillingResourceChainUsable,

        /**
         * 財 → 七殺 raw 생 관계가
         * 실제 財滋殺로 작동하는가.
         */
        boolean wealthStrengtheningRouteActive,

        /**
         * 殺印相生에 사용되는 印을
         * 財가 실제 손상시키는가.
         */
        boolean wealthDamageResourceRouteActive,

        /**
         * 官殺並見 상태에서
         * 실제 官殺取清이 되었는가.
         */
        boolean officerKillingsMixtureCleared,

        /**
         * 월령 七殺이 투간했거나
         * 관련 七殺 천간이 실제 合을 만나
         * 본래 기능이 묶였는가.
         */
        boolean sevenKillingsStemCombined,

        /**
         * 월령 지지가 실제
         * 沖/刑/破/害 등으로 흔들리는가.
         */
        boolean monthCommandDisrupted,

        /**
         * 위 월령 교란이
         * 회합 등으로 실제 해소되는가.
         */
        boolean monthCommandDisruptionResolved

) {

    public static SevenKillingsPatternRelationContext unresolved() {

        return new SevenKillingsPatternRelationContext(
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