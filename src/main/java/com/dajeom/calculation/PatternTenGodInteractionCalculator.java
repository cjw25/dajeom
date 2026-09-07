package com.dajeom.calculation;

import com.dajeom.domain.FiveElement;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternInteractionHiddenStem;
import com.dajeom.domain.PatternInteractionVisibleStem;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLink;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 원국의 십성 배치를
 * 방향성 있는 생극 그래프로 변환한다.
 *
 * =================================================
 *
 * 노드:
 *
 * - 日主
 * - 年/月/時 천간
 * - 모든 지장간
 *
 * =================================================
 *
 * Link:
 *
 * source 生 target
 *
 * source 剋 target
 *
 * =================================================
 *
 * 예:
 *
 * 傷官(土) → 財(金)
 * = GENERATES
 *
 * 財(金) → 官殺(水)
 * = GENERATES
 *
 * 食神(土) → 七殺(水)
 * = CONTROLS
 *
 * 七殺(水) → 印(木)
 * = GENERATES
 *
 * 印(木) → 日主(火)
 * = GENERATES
 *
 * =================================================
 *
 * 주의:
 *
 * 여기서는 오행상 생극 연결만 만든다.
 *
 * 실제:
 *
 * - 食神制殺 성립
 * - 財生官 성립
 * - 殺印相生成立
 * - 比劫奪財 성립
 *
 * 은 다음 StructuralSignals Adapter에서
 * 통근/합거/위치/기반 등을 함께 보고 결정한다.
 *
 * =================================================
 *
 * 점수 / 가중치 없음.
 */
@Component
public class PatternTenGodInteractionCalculator {

    public PatternTenGodInteractionAnalysis analyze(
            PatternInteractionAnalysis interaction
    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        HeavenlyStem dayMaster =
                interaction.dayMaster();

        List<PatternTenGodNode> nodes =
                new ArrayList<>();

        /*
         * =================================================
         * 1. 日主
         * =================================================
         */

        nodes.add(
                PatternTenGodNode.dayMaster(
                        dayMaster
                )
        );

        /*
         * =================================================
         * 2. 투출 천간
         * =================================================
         */

        for (
                PatternInteractionVisibleStem visible
                : interaction.visibleInteractions()
        ) {

            Objects.requireNonNull(
                    visible,
                    "천간 Interaction에 null이 포함될 수 없습니다."
            );

            nodes.add(
                    PatternTenGodNode.visibleStem(
                            visible.position(),
                            visible.stem(),
                            visible.tenGodType()
                    )
            );
        }

        /*
         * =================================================
         * 3. 지장간
         * =================================================
         */

        for (
                PatternInteractionHiddenStem hidden
                : interaction.hiddenInteractions()
        ) {

            Objects.requireNonNull(
                    hidden,
                    "지장간 Interaction에 null이 포함될 수 없습니다."
            );

            nodes.add(
                    PatternTenGodNode.hiddenStem(
                            hidden.branchPosition(),
                            hidden.branch(),
                            hidden.hiddenStem(),
                            hidden.hiddenStemPhase(),
                            hidden.tenGodType()
                    )
            );
        }

        /*
         * =================================================
         * 4. 방향성 생극 Link 생성
         * =================================================
         */

        List<PatternTenGodLink> links =
                new ArrayList<>();

        for (
                PatternTenGodNode source
                : nodes
        ) {

            for (
                    PatternTenGodNode target
                    : nodes
            ) {

                if (
                        source.equals(
                                target
                        )
                ) {

                    continue;
                }

                FiveElement sourceElement =
                        elementOf(
                                source.stem()
                        );

                FiveElement targetElement =
                        elementOf(
                                target.stem()
                        );

                if (
                        generates(
                                sourceElement,
                                targetElement
                        )
                ) {

                    links.add(
                            new PatternTenGodLink(
                                    source,
                                    target,
                                    PatternTenGodLinkType
                                            .GENERATES
                            )
                    );

                    continue;
                }

                if (
                        controls(
                                sourceElement,
                                targetElement
                        )
                ) {

                    links.add(
                            new PatternTenGodLink(
                                    source,
                                    target,
                                    PatternTenGodLinkType
                                            .CONTROLS
                            )
                    );
                }
            }
        }

        return new PatternTenGodInteractionAnalysis(
                dayMaster,
                nodes,
                links
        );
    }

    private FiveElement elementOf(
            HeavenlyStem stem
    ) {

        return switch (stem) {

            case JIA,
                 YI ->
                    FiveElement.WOOD;

            case BING,
                 DING ->
                    FiveElement.FIRE;

            case WU,
                 JI ->
                    FiveElement.EARTH;

            case GENG,
                 XIN ->
                    FiveElement.METAL;

            case REN,
                 GUI ->
                    FiveElement.WATER;
        };
    }

    /**
     * 生:
     *
     * 木生火
     * 火生土
     * 土生金
     * 金生水
     * 水生木
     */
    private boolean generates(

            FiveElement source,

            FiveElement target

    ) {

        return switch (source) {

            case WOOD ->
                    target
                            == FiveElement.FIRE;

            case FIRE ->
                    target
                            == FiveElement.EARTH;

            case EARTH ->
                    target
                            == FiveElement.METAL;

            case METAL ->
                    target
                            == FiveElement.WATER;

            case WATER ->
                    target
                            == FiveElement.WOOD;
        };
    }

    /**
     * 剋:
     *
     * 木剋土
     * 土剋水
     * 水剋火
     * 火剋金
     * 金剋木
     */
    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return switch (source) {

            case WOOD ->
                    target
                            == FiveElement.EARTH;

            case EARTH ->
                    target
                            == FiveElement.WATER;

            case WATER ->
                    target
                            == FiveElement.FIRE;

            case FIRE ->
                    target
                            == FiveElement.METAL;

            case METAL ->
                    target
                            == FiveElement.WOOD;
        };
    }
}