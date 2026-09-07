package com.dajeom.domain;

/**
 * 七殺格 성패 판정용 실제 구조 신호.
 *
 * =================================================
 *
 * 단순 존재와 실제 작용을 분리한다.
 *
 * 食神 존재
 * != 食神制殺
 *
 * 印 존재
 * != 殺印相生
 *
 * 財 존재
 * != 財滋殺
 *
 * 正官 존재
 * != 官殺混雜 확정
 *
 * =================================================
 *
 * 이 객체 자체는 관계를 계산하지 않는다.
 *
 * 실제:
 *
 * - StemRelation
 * - BranchRelation
 * - 통근
 * - 투출
 * - 합거
 * - 위치 배치
 *
 * 를 읽는 Adapter가 이후 생성한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
public record SevenKillingsPatternStructuralSignals(

        /**
         * 월령 七殺의 기반이 실제로 유지되는가.
         */
        boolean sevenKillingsFoundationStable,

        /**
         * 食神이 실제 七殺을 제어하는가.
         *
         * 食神制殺.
         */
        boolean eatingGodActuallyControlsSevenKillings,

        /**
         * 食神 자체가 근과 기반을 갖고
         * 制殺 기능을 수행할 수 있는가.
         */
        boolean eatingGodFoundationStable,

        /**
         * 印이 실제 七殺의 기운을 받아
         * 일간을 생하는 흐름으로 전환하는가.
         *
         * 殺 → 印 → 身
         */
        boolean killingGeneratesResourceToSupportDayMaster,

        /**
         * 殺印相生에서 사용되는 印의 기반이
         * 실제로 안정적인가.
         */
        boolean resourceFoundationStable,

        /**
         * 食神制殺 구조에 印이 개입해
         * 食神을 실제로 억누르는가.
         *
         * 食制又逢印 / 印奪食 Context.
         */
        boolean resourceActuallySuppressesEatingGod,

        /**
         * 위 印奪食 Context에서
         * 財가 실제 印을 제어하여
         * 食神을 다시 살리는가.
         *
         * 財去印存食.
         */
        boolean wealthRemovesResourcePreservingEatingGod,

        /**
         * 財가 실제 七殺을 생하여
         * 七殺의 압력을 더 키우는가.
         *
         * 단순 財 존재와 구분.
         */
        boolean wealthActuallyStrengthensSevenKillings,

        /**
         * 財가 七殺을 생하지만
         * 그 七殺이 다시 印을 생하고
         * 印이 일간을 돕는 흐름으로
         * 실제 연결되는가.
         *
         * 財 → 殺 → 印 → 身
         *
         * 단순 財滋殺 파격과 구분.
         */
        boolean wealthKillingResourceChainUsable,

        /**
         * 財가 殺印相生에서 필요한 印을
         * 실제로 손상시키는가.
         */
        boolean wealthDamagesResourceRoute,

        /**
         * 正官과 七殺이 함께 투출했을 때
         * 합거/제어/위치 배치 등으로
         * 실제 官殺取清이 되었는가.
         */
        boolean officerKillingsMixtureCleared,

        /**
         * 七殺 자체가 천간합 등에 의해
         * 본래 기능을 잃거나 묶이는가.
         *
         * 七殺格 본체의 결함 Context.
         */
        boolean sevenKillingsStemCombined,

        /**
         * 월령 七殺의 지지가
         * 충·형·파·해 등으로 실제 흔들리는가.
         */
        boolean monthCommandDisrupted,

        /**
         * 위 월령 교란이 회합 등으로
         * 실제 해소되는가.
         */
        boolean monthCommandDisruptionResolved

) {

    public boolean foodControlRouteStable() {

        return eatingGodActuallyControlsSevenKillings
                &&
                eatingGodFoundationStable;
    }

    public boolean killingResourceRouteStable() {

        return killingGeneratesResourceToSupportDayMaster
                &&
                resourceFoundationStable;
    }

    public boolean foodControlDamagedByResource() {

        return foodControlRouteStable()
                &&
                resourceActuallySuppressesEatingGod;
    }

    public boolean foodControlRescuedByWealth() {

        return foodControlDamagedByResource()
                &&
                wealthRemovesResourcePreservingEatingGod;
    }

    public boolean unresolvedMonthCommandDisruption() {

        return monthCommandDisrupted
                &&
                !monthCommandDisruptionResolved;
    }
}