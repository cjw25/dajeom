package com.dajeom.calculation;

import com.dajeom.domain.FiveElement;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.PatternStemCombinationAnalysis;
import com.dajeom.domain.PatternStemCombinationOccurrence;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국에 실제로 드러난 천간을 대상으로
 * 天干五合을 계산한다.
 *
 * =================================================
 *
 * 五合:
 *
 * 甲己合土
 * 乙庚合金
 * 丙辛合水
 * 丁壬合木
 * 戊癸合火
 *
 * =================================================
 *
 * 분석 대상:
 *
 * - 日干
 * - 年干
 * - 月干
 * - 時干
 *
 * 지장간은 天干五合 occurrence에
 * 직접 넣지 않는다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Calculator는 "合 존재"만 계산한다.
 *
 * 合化 여부는 계산하지 않는다.
 */
@Component
public class PatternStemCombinationCalculator {

    public PatternStemCombinationAnalysis analyze(
            PatternTenGodInteractionAnalysis tenGodInteractions
    ) {

        Objects.requireNonNull(
                tenGodInteractions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        List<PatternTenGodNode> visibleStems =
                tenGodInteractions
                        .nodes()
                        .stream()
                        .filter(
                                node ->
                                        node.dayMaster()
                                                ||
                                                node.visible()
                        )
                        .toList();

        List<PatternStemCombinationOccurrence> combinations =
                new ArrayList<>();

        for (
                int i = 0;
                i < visibleStems.size();
                i++
        ) {

            PatternTenGodNode left =
                    visibleStems.get(i);

            for (
                    int j = i + 1;
                    j < visibleStems.size();
                    j++
            ) {

                PatternTenGodNode right =
                        visibleStems.get(j);

                Optional<FiveElement> combinationElement =
                        combinationElementOf(
                                left.stem(),
                                right.stem()
                        );

                combinationElement.ifPresent(
                        element ->
                                combinations.add(
                                        new PatternStemCombinationOccurrence(
                                                left,
                                                right,
                                                element
                                        )
                                )
                );
            }
        }

        return new PatternStemCombinationAnalysis(
                tenGodInteractions.dayMaster(),
                combinations
        );
    }

    /**
     * 天干五合의 고전적 化氣 오행을 반환한다.
     *
     * 주의:
     *
     * 반환값이 존재한다는 것은
     * "五合 pair가 존재한다"는 뜻이지
     * 실제 化가 완성되었다는 뜻이 아니다.
     */
    private Optional<FiveElement> combinationElementOf(

            HeavenlyStem first,

            HeavenlyStem second

    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 천간은 필수입니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 천간은 필수입니다."
        );

        /*
         * 甲己合土
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.JIA,
                        HeavenlyStem.JI
                )
        ) {

            return Optional.of(
                    FiveElement.EARTH
            );
        }

        /*
         * 乙庚合金
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.YI,
                        HeavenlyStem.GENG
                )
        ) {

            return Optional.of(
                    FiveElement.METAL
            );
        }

        /*
         * 丙辛合水
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.BING,
                        HeavenlyStem.XIN
                )
        ) {

            return Optional.of(
                    FiveElement.WATER
            );
        }

        /*
         * 丁壬合木
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.DING,
                        HeavenlyStem.REN
                )
        ) {

            return Optional.of(
                    FiveElement.WOOD
            );
        }

        /*
         * 戊癸合火
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.WU,
                        HeavenlyStem.GUI
                )
        ) {

            return Optional.of(
                    FiveElement.FIRE
            );
        }

        return Optional.empty();
    }

    private boolean pair(

            HeavenlyStem first,

            HeavenlyStem second,

            HeavenlyStem expectedA,

            HeavenlyStem expectedB

    ) {

        return (
                first == expectedA
                        &&
                        second == expectedB
        )
                ||
                (
                        first == expectedB
                                &&
                                second == expectedA
                );
    }
}