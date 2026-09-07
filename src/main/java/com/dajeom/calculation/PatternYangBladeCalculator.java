package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PatternYangBladeAnalysis;
import com.dajeom.domain.PatternYangBladeOccurrence;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국에서 陽刃을 자동 식별한다.
 *
 * =================================================
 *
 * 固定:
 *
 * 甲 → 卯 / 卯中乙
 * 丙 → 午 / 午中丁
 * 戊 → 午 / 午中己
 * 庚 → 酉 / 酉中辛
 * 壬 → 子 / 子中癸
 *
 * =================================================
 *
 * 단순 branch만 찾지 않는다.
 *
 * 반드시 해당 刃支 안의
 * 일간 기준 劫財 지장간 노드까지 찾는다.
 */
@Component
public class PatternYangBladeCalculator {

    public PatternYangBladeAnalysis analyze(
            PatternTenGodInteractionAnalysis interactions
    ) {

        Objects.requireNonNull(
                interactions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        HeavenlyStem dayMaster =
                interactions.dayMaster();

        Optional<YangBladeDefinition> definitionOptional =
                definitionOf(
                        dayMaster
                );

        /*
         * 五陰干
         */
        if (definitionOptional.isEmpty()) {

            return new PatternYangBladeAnalysis(

                    dayMaster,

                    Optional.empty(),

                    List.of()
            );
        }

        YangBladeDefinition definition =
                definitionOptional.get();

        /*
         * 원국 중 해당 刃支가 있는 위치들.
         *
         * hidden node들의 branch 정보를 이용하므로
         * 기존 BranchRelation API를 추측하지 않는다.
         */
        EnumSet<PillarPosition> bladePositions =
                EnumSet.noneOf(
                        PillarPosition.class
                );

        for (
                PatternTenGodNode node
                : interactions.nodes()
        ) {

            if (!node.hidden()) {

                continue;
            }

            EarthlyBranch branch =
                    node
                            .branch()
                            .orElse(null);

            if (
                    branch
                            == definition.branch()
            ) {

                bladePositions.add(
                        node.position()
                );
            }
        }

        List<PatternYangBladeOccurrence> occurrences =
                new ArrayList<>();

        for (
                PillarPosition position
                : bladePositions
        ) {

            PatternTenGodNode robWealthHiddenStem =
                    interactions
                            .nodes()
                            .stream()
                            .filter(
                                    PatternTenGodNode
                                            ::hidden
                            )
                            .filter(
                                    node ->
                                            node.position()
                                                    == position
                            )
                            .filter(
                                    node ->
                                            node.branch()
                                                    .orElse(null)
                                                    == definition.branch()
                            )
                            .filter(
                                    node ->
                                            node.stem()
                                                    == definition.robWealthStem()
                            )
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    position
                                                            +
                                                            "의 陽刃 지지에서 劫財藏干 "
                                                            +
                                                            definition.robWealthStem()
                                                            +
                                                            "을 찾을 수 없습니다."
                                            )
                            );

            occurrences.add(
                    new PatternYangBladeOccurrence(

                            position,

                            definition.branch(),

                            robWealthHiddenStem
                    )
            );
        }

        return new PatternYangBladeAnalysis(

                dayMaster,

                Optional.of(
                        definition.branch()
                ),

                occurrences
        );
    }

    private Optional<YangBladeDefinition> definitionOf(
            HeavenlyStem dayMaster
    ) {

        return switch (dayMaster) {

            case JIA ->
                    Optional.of(
                            new YangBladeDefinition(
                                    EarthlyBranch.MAO,
                                    HeavenlyStem.YI
                            )
                    );

            case BING ->
                    Optional.of(
                            new YangBladeDefinition(
                                    EarthlyBranch.WU,
                                    HeavenlyStem.DING
                            )
                    );

            case WU ->
                    Optional.of(
                            new YangBladeDefinition(
                                    EarthlyBranch.WU,
                                    HeavenlyStem.JI
                            )
                    );

            case GENG ->
                    Optional.of(
                            new YangBladeDefinition(
                                    EarthlyBranch.YOU,
                                    HeavenlyStem.XIN
                            )
                    );

            case REN ->
                    Optional.of(
                            new YangBladeDefinition(
                                    EarthlyBranch.ZI,
                                    HeavenlyStem.GUI
                            )
                    );

            case YI,
                 DING,
                 JI,
                 XIN,
                 GUI -> Optional.empty();
        };
    }

    private record YangBladeDefinition(

            EarthlyBranch branch,

            HeavenlyStem robWealthStem

    ) {
    }
}