package com.dajeom.domain;

/**
 * 食神格 성패 판정용 실제 구조 신호.
 *
 * 단순 십성 존재와 실제 작용을 분리한다.
 *
 * 예:
 *
 * 偏印 존재
 * != 梟神奪食
 *
 * 七殺 존재
 * != 食神制殺
 *
 * 財 존재
 * != 食神生財 완성
 *
 * 숫자 / 점수 / 가중치 없음.
 */
public record EatingGodPatternStructuralSignals(

        /**
         * 월령 食神의 기반이 안정되어 있는가.
         */
        boolean eatingGodFoundationStable,

        /**
         * 食神生財에서 財가 실제 根을 가지고
         * 기능할 수 있는가.
         *
         * 財要有根 Context.
         */
        boolean wealthFoundationStable,

        /**
         * 偏印/梟이 실제 食神을 제압하는가.
         *
         * 단순 偏印 존재와 구분.
         */
        boolean indirectResourceActuallySuppressesEatingGod,

        /**
         * 梟奪食 Context에서 財가 印/梟을 제어하여
         * 食神을 보호하는가.
         *
         * 生財護食.
         */
        boolean wealthProtectsEatingGodFromIndirectResource,

        /**
         * 食神이 七殺을 실제로 제하는가.
         *
         * 食神制殺.
         */
        boolean sevenKillingsControlledByEatingGod,

        /**
         * 食神 자체를 중심으로 쓰기보다
         * 七殺-印 구조로 전환되는
         * 棄食就殺而透印 Context가 실제 성립하는가.
         */
        boolean abandoningEatingGodForKillingWithResource,

        /**
         * 食神이 七殺을 합하여 제거하고
         * 財를 보존하는가.
         *
         * 食神合殺存財.
         */
        boolean eatingGodCombinesKillingPreservingWealth,

        /**
         * 食神生財 구조의 財가 오히려
         * 七殺을 생하여 食神格을 깨는가.
         *
         * 生財露殺의 실제 위험 Context.
         */
        boolean wealthStrengthensSevenKillings,

        /**
         * 財와 七殺이 함께 있어도
         * 食神이 그 사이를 실제로 매개하여
         * 財가 곧바로 七殺의 편으로 흐르지 않는가.
         *
         * 財先煞後，食以間之 계열 Context.
         */
        boolean eatingGodSeparatesWealthAndKilling,

        /**
         * 食神帶煞印 구조가 성립했으나
         * 다시 財가 투출하여 印을 깨고
         * 원래 成格 구조에 흠을 만드는가.
         *
         * 食神帶煞印而又逢財
         * → 成中有敗
         */
        boolean wealthDamagesKillingResourceRoute,

        /**
         * 正官과 七殺이 함께 드러났지만
         * 전체 배치상 食神格으로 정리 가능한가.
         *
         * 官煞競出의 추가 판정용.
         */
        boolean officerKillingsMixtureUsable

) {

    public boolean owlSuppressionResolved() {

        return wealthProtectsEatingGodFromIndirectResource
                ||
                abandoningEatingGodForKillingWithResource;
    }

    public boolean killingProblemResolved() {

        return sevenKillingsControlledByEatingGod
                ||
                eatingGodCombinesKillingPreservingWealth
                ||
                eatingGodSeparatesWealthAndKilling;
    }
}