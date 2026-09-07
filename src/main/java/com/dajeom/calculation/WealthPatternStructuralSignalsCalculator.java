package com.dajeom.calculation;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLink;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.WealthPatternRelationContext;
import com.dajeom.domain.WealthPatternStructuralSignals;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 財格에 필요한 StructuralSignals를
 * 십성 생극 그래프에서 자동 생성한다.
 *
 * =================================================
 *
 * 자동 계산 가능한 것:
 *
 * 1. 比劫 → 財
 *    → 財를 다투는 raw 구조
 *
 * 2. 比劫 → 食神 → 財
 *    → 食神化劫生財
 *
 * 3. 正官 → 比劫
 *    → 官制比劫
 *
 * 4. 食神 → 七殺
 *    → 食神制殺의 raw 구조
 *
 * 5. 傷官 → 正官
 *    → 財生官 구조의 官 손상 가능성
 *
 * =================================================
 *
 * 그래프만으로 확정하지 않는 것:
 *
 * 1. 財印不相礙
 *
 * 2. 合殺存財
 *
 * 3. 官星逢合
 *
 * 위 세 가지는 WealthPatternRelationContext에서 받는다.
 *
 * =================================================
 *
 * 중요:
 *
 * 여기서의 "raw 생극 연결"은
 * 격국 최종 판정 그 자체가 아니다.
 *
 * 하지만 기존처럼 호출부가 임의로
 * boolean을 작성할 필요는 없어지게 된다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 / 다수결 없음.
 */
@Component
public class WealthPatternStructuralSignalsCalculator {

    public WealthPatternStructuralSignals analyze(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            WealthPatternRelationContext relationContext

    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                tenGodInteractions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                relationContext,
                "WealthPatternRelationContext는 필수입니다."
        );

        validateDayMaster(
                interaction,
                tenGodInteractions
        );

        /*
         * =================================================
         * 1. 比劫奪財 raw Context
         * =================================================
         *
         * 투출 比肩/劫財가
         * 財를 직접 剋하는 생극 Link가 있는가.
         *
         * 財는 월령 지장간일 수도 있으므로
         * target은 visible로 제한하지 않는다.
         */
        boolean wealthContestedByCompanionOrRobWealth =
                hasVisibleCompanionOrRobControlWealth(
                        tenGodInteractions
                );

        /*
         * =================================================
         * 2. 財逢劫而透食以化
         * =================================================
         *
         * 比劫
         *   ↓ 生
         * 食神(투출)
         *   ↓ 生
         * 財
         *
         * 가 실제 그래프에 존재해야 한다.
         */
        boolean robberyResolvedByEatingGod =
                wealthContestedByCompanionOrRobWealth
                        &&
                        hasVisibleCompanionOrRobGenerateEatingGod(
                                tenGodInteractions
                        )
                        &&
                        hasVisibleEatingGodGenerateWealth(
                                tenGodInteractions
                        );

        /*
         * =================================================
         * 3. 生官以制劫
         * =================================================
         *
         * 正官
         *   ↓ 剋
         * 比肩/劫財
         */
        boolean robberyControlledByOfficer =
                wealthContestedByCompanionOrRobWealth
                        &&
                        hasVisibleDirectOfficerControlCompanionOrRob(
                                tenGodInteractions
                        );

        /*
         * =================================================
         * 4. 財逢殺而食神制殺
         * =================================================
         *
         * 食神과 七殺이 모두 천간에 드러나고
         *
         * 食神
         *   ↓ 剋
         * 七殺
         *
         * 관계가 있는가.
         */
        boolean sevenKillingsControlledByEatingGod =
                tenGodInteractions
                        .hasVisibleEatingGodControlSevenKillings();

        /*
         * =================================================
         * 5. 財旺生官而又逢傷
         * =================================================
         *
         * 傷官이 천간에 드러나
         * 正官을 직접 剋하는가.
         *
         * 여기서는 傷官 → 官의 생극 사실을
         * 자동 계산한다.
         */
        boolean generatedOfficerDamagedByHurtingOfficer =
                hasVisibleHurtingOfficerControlDirectOfficer(
                        tenGodInteractions
                );

        /*
         * =================================================
         * 6. 실제 합/배치 Context
         * =================================================
         *
         * 현재 raw 오행 그래프만으로는
         * 아래를 만들지 않는다.
         */

        boolean wealthAndResourceMutuallyNonObstructing =
                relationContext
                        .wealthAndResourceMutuallyNonObstructing();

        boolean sevenKillingsCombinedAwayPreservingWealth =
                relationContext
                        .sevenKillingsCombinedAwayPreservingWealth();

        boolean generatedOfficerCombined =
                relationContext
                        .generatedOfficerCombined();

        return new WealthPatternStructuralSignals(

                wealthAndResourceMutuallyNonObstructing,

                wealthContestedByCompanionOrRobWealth,

                robberyResolvedByEatingGod,

                robberyControlledByOfficer,

                sevenKillingsControlledByEatingGod,

                sevenKillingsCombinedAwayPreservingWealth,

                generatedOfficerDamagedByHurtingOfficer,

                generatedOfficerCombined
        );
    }

    /**
     * 比肩/劫財 → 財.
     *
     * source는 실제 투출 천간이어야 한다.
     *
     * target 財는 투출 또는 지장간 모두 허용.
     */
    private boolean hasVisibleCompanionOrRobControlWealth(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source().visible()

                                        &&

                                        isCompanionOrRob(
                                                link.source()
                                        )

                                        &&

                                        isWealth(
                                                link.target()
                                        )
                );
    }

    /**
     * 比肩/劫財 → 食神.
     *
     * 財逢劫而透食의 "透食"을 보존하기 위해
     * 양쪽 모두 visible이어야 한다.
     */
    private boolean hasVisibleCompanionOrRobGenerateEatingGod(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.GENERATES

                                        &&

                                        link.source().visible()

                                        &&

                                        link.target().visible()

                                        &&

                                        isCompanionOrRob(
                                                link.source()
                                        )

                                        &&

                                        link.target()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .EATING_GOD
                                                )
                );
    }

    /**
     * 食神 → 財.
     *
     * 食神은 실제 투출을 요구한다.
     *
     * 財는 월령 자체가 財일 수 있으므로
     * 지장간 target도 허용한다.
     */
    private boolean hasVisibleEatingGodGenerateWealth(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.GENERATES

                                        &&

                                        link.source().visible()

                                        &&

                                        link.source()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .EATING_GOD
                                                )

                                        &&

                                        isWealth(
                                                link.target()
                                        )
                );
    }

    /**
     * 正官 → 比肩/劫財.
     *
     * 官制比劫.
     *
     * 실제 보호 역할을 하려면
     * 正官과 比劫이 모두 투출된 raw 구조를
     * 우선 확인한다.
     */
    private boolean hasVisibleDirectOfficerControlCompanionOrRob(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source().visible()

                                        &&

                                        link.target().visible()

                                        &&

                                        link.source()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .DIRECT_OFFICER
                                                )

                                        &&

                                        isCompanionOrRob(
                                                link.target()
                                        )
                );
    }

    /**
     * 傷官 → 正官.
     *
     * 財旺生官 구조에 傷官이 개입해
     * 官을 직접 剋하는 raw Context.
     */
    private boolean hasVisibleHurtingOfficerControlDirectOfficer(
            PatternTenGodInteractionAnalysis analysis
    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source().visible()

                                        &&

                                        link.target().visible()

                                        &&

                                        link.source()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .HURTING_OFFICER
                                                )

                                        &&

                                        link.target()
                                                .hasTenGodType(
                                                        MonthCommandPatternCandidateType
                                                                .DIRECT_OFFICER
                                                )
                );
    }

    private boolean isWealth(
            PatternTenGodNode node
    ) {

        return node.hasTenGodType(
                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        )
                ||
                node.hasTenGodType(
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
                );
    }

    private boolean isCompanionOrRob(
            PatternTenGodNode node
    ) {

        return node.hasTenGodType(
                MonthCommandPatternCandidateType
                        .MONTH_COMPANION
        )
                ||
                node.hasTenGodType(
                        MonthCommandPatternCandidateType
                                .MONTH_ROB_WEALTH
                );
    }

    private void validateDayMaster(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions

    ) {

        HeavenlyStem dayMaster =
                interaction.dayMaster();

        if (
                tenGodInteractions.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 "
                            +
                            "PatternTenGodInteractionAnalysis의 일간이 일치하지 않습니다."
            );
        }
    }
}