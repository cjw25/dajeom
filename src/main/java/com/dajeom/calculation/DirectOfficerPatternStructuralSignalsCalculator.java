package com.dajeom.calculation;

import com.dajeom.domain.DirectOfficerPatternRelationContext;
import com.dajeom.domain.DirectOfficerStructuralSignals;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLink;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 正官格용 DirectOfficerStructuralSignals를
 * 자동 생성한다.
 *
 * =================================================
 *
 * raw 십성 생극 그래프에서 자동 확인:
 *
 * 傷官 → 正官
 *
 * 印 → 傷官
 *
 * 을 연결하여
 *
 * 官逢傷
 * +
 * 透印以解
 *
 * 의 기본 구조를 찾는다.
 *
 * =================================================
 *
 * 그래프만으로 확정하지 않는 것:
 *
 * - 官星逢合
 * - 月令沖
 * - 月令刑
 * - 月令破
 * - 月令害
 * - 合殺留官
 * - 財印不相礙
 *
 * 위 항목은
 * DirectOfficerPatternRelationContext에서 받는다.
 *
 * =================================================
 *
 * 중요:
 *
 * 印과 傷官이 오행상 상극한다고 해서
 * 자동 救應으로 처리하지 않는다.
 *
 * 실제로:
 *
 * 1. 동일한 傷官이 正官을 해치고 있고
 * 2. 투출한 印이 그 傷官을 제하며
 * 3. 위치상 그 제어가 작동할 수 있음
 *
 * 을 모두 확인해야 한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
@Component
public class DirectOfficerPatternStructuralSignalsCalculator {

    public DirectOfficerStructuralSignals analyze(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions,

            DirectOfficerPatternRelationContext relationContext

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
                "DirectOfficerPatternRelationContext는 필수입니다."
        );

        validateDayMaster(
                interaction,
                tenGodInteractions
        );

        /*
         * =================================================
         * 1. 官逢傷而透印以解
         * =================================================
         *
         * 동일한 傷官 노드에 대해:
         *
         * 傷官
         *   ↓ 剋
         * 正官
         *
         * 그리고
         *
         * 투출 印
         *   ↓ 剋
         * 傷官
         *
         * 이 동시에 연결되어야 한다.
         *
         * 마지막으로 위치상 실제 제어가 가능한지는
         * RelationContext에서 확인한다.
         */

        boolean rawHurtingOfficerResolvedByResource =
                hasResourceResolvingOfficerDamagingHurtingOfficer(
                        tenGodInteractions
                );

        boolean hurtingOfficerResolvedByResource =
                rawHurtingOfficerResolvedByResource
                        &&
                        relationContext
                                .resourcePlacementSupportsHurtingOfficerResolution();

        /*
         * =================================================
         * 2. 나머지 관계 신호
         * =================================================
         */

        return new DirectOfficerStructuralSignals(

                relationContext
                        .officerStemCombined(),

                relationContext
                        .monthCommandClashed(),

                relationContext
                        .monthCommandPunished(),

                relationContext
                        .monthCommandBroken(),

                relationContext
                        .monthCommandHarmed(),

                hurtingOfficerResolvedByResource,

                relationContext
                        .mixedSevenKillingsClearedByCombination(),

                relationContext
                        .monthCommandDisruptionResolvedByCombination(),

                relationContext
                        .wealthAndResourceMutuallyNonObstructing()
        );
    }

    /**
     * 다음과 같은 동일 傷官 노드를 찾는다.
     *
     * 투출 印
     *   ↓ 剋
     * 傷官
     *   ↓ 剋
     * 正官
     *
     * =================================================
     *
     * 印은 "透印以解"이므로
     * source가 visible이어야 한다.
     *
     * 傷官은 천간/지장간 모두 허용한다.
     *
     * 正官도 월령 MAIN으로 암장되어 있을 수 있으므로
     * visible만 요구하지 않는다.
     */
    private boolean hasResourceResolvingOfficerDamagingHurtingOfficer(
            PatternTenGodInteractionAnalysis analysis
    ) {

        for (
                PatternTenGodNode hurtingOfficer
                : analysis.nodesOf(
                MonthCommandPatternCandidateType
                        .HURTING_OFFICER
        )
        ) {

            boolean damagesOfficer =
                    hasControlLink(
                            analysis,
                            hurtingOfficer,
                            MonthCommandPatternCandidateType
                                    .DIRECT_OFFICER
                    );

            if (!damagesOfficer) {

                continue;
            }

            boolean controlledByVisibleResource =
                    hasVisibleResourceControlTarget(
                            analysis,
                            hurtingOfficer
                    );

            if (controlledByVisibleResource) {

                return true;
            }
        }

        return false;
    }

    /**
     * 특정 source 노드가
     * target 십성 타입을 剋하는 Link가 있는가.
     */
    private boolean hasControlLink(

            PatternTenGodInteractionAnalysis analysis,

            PatternTenGodNode source,

            MonthCommandPatternCandidateType targetType

    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source()
                                                .equals(
                                                        source
                                                )

                                        &&

                                        link.target()
                                                .hasTenGodType(
                                                        targetType
                                                )
                );
    }

    /**
     * 투출한 正印/偏印이
     * 특정 傷官 노드를 剋하는가.
     */
    private boolean hasVisibleResourceControlTarget(

            PatternTenGodInteractionAnalysis analysis,

            PatternTenGodNode target

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

                                        isResource(
                                                link.source()
                                        )

                                        &&

                                        link.target()
                                                .equals(
                                                        target
                                                )
                );
    }

    private boolean isResource(
            PatternTenGodNode node
    ) {

        return node.hasTenGodType(
                MonthCommandPatternCandidateType
                        .DIRECT_RESOURCE
        )
                ||
                node.hasTenGodType(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
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