package com.dajeom.analyzer;

import com.dajeom.model.BasePowerAnalysis;
import com.dajeom.model.HiddenElementPowerAnalysis;
import com.dajeom.model.HiddenElementPowerEvidence;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.PillarHiddenStemAnalysis;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 3차-2.
 *
 * 지장간을 오행별 내부 세력으로 변환한다.
 *
 * 설계 원칙:
 *
 * 이 단계에서는 지장간이 원래 가지고 있는
 * raw weight만 사용한다.
 *
 * 예:
 *
 * 申
 *
 * 庚 60 -> 金 60
 * 壬 25 -> 水 25
 * 戊 15 -> 土 15
 *
 * 월지라는 이유로 여기서 다시
 * 가중치를 올리지 않는다.
 *
 * 월령의 계절 영향은 3-3에서
 * 별도로 반영한다.
 *
 * 통근 영향은 3-4,
 * 투간은 3-5에서 별도로 반영한다.
 */
@Component
public class HiddenElementPowerAnalyzer {

    public HiddenElementPowerAnalysis analyze(
            BasePowerAnalysis basePowerAnalysis
    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        List<HiddenElementPowerEvidence> evidences =
                new ArrayList<>();

        Map<FiveElement, Integer>
                elementBaseWeights =
                createZeroElementMap();

        Map<FiveElement, Integer>
                elementEvidenceCounts =
                createZeroElementMap();

        Map<FiveElement, Integer>
                monthBranchElementWeights =
                createZeroElementMap();

        for (
                PillarHiddenStemAnalysis pillarAnalysis
                : basePowerAnalysis
                .pillarHiddenStemAnalyses()
        ) {

            for (
                    HiddenStem hiddenStem
                    : pillarAnalysis.hiddenStems()
            ) {

                FiveElement element =
                        elementOfStem(
                                hiddenStem.stem()
                        );

                HiddenElementPowerEvidence evidence =
                        new HiddenElementPowerEvidence(
                                pillarAnalysis.position(),
                                pillarAnalysis.branch(),
                                hiddenStem.stem(),
                                hiddenStem.role(),
                                element,
                                hiddenStem.weight()
                        );

                evidences.add(
                        evidence
                );

                /*
                 * 전체 지장간 오행 세력.
                 */
                elementBaseWeights.compute(
                        element,
                        (
                                key,
                                current
                        ) ->
                                safeValue(
                                        current
                                )
                                        + hiddenStem.weight()
                );

                /*
                 * 해당 오행의 지장간 등장 수.
                 */
                elementEvidenceCounts.compute(
                        element,
                        (
                                key,
                                current
                        ) ->
                                safeValue(
                                        current
                                )
                                        + 1
                );

                /*
                 * 월지 내부 세력은
                 * 다음 계절 보정을 위해
                 * 별도로도 보존한다.
                 */
                if (
                        pillarAnalysis.monthBranch()
                ) {

                    monthBranchElementWeights.compute(
                            element,
                            (
                                    key,
                                    current
                            ) ->
                                    safeValue(
                                            current
                                    )
                                            + hiddenStem.weight()
                    );
                }
            }
        }

        int totalBaseWeight =
                evidences
                        .stream()
                        .mapToInt(
                                HiddenElementPowerEvidence::baseWeight
                        )
                        .sum();

        return new HiddenElementPowerAnalysis(
                basePowerAnalysis
                        .pillarHiddenStemAnalyses()
                        .size(),
                evidences.size(),
                totalBaseWeight,
                evidences,
                elementBaseWeights,
                elementEvidenceCounts,
                monthBranchElementWeights
        );
    }

    private Map<FiveElement, Integer>
    createZeroElementMap() {

        EnumMap<FiveElement, Integer> result =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            result.put(
                    element,
                    0
            );
        }

        return result;
    }

    private int safeValue(
            Integer value
    ) {

        return value == null
                ? 0
                : value;
    }

    /**
     * 천간 -> 오행.
     *
     * 甲乙 木
     * 丙丁 火
     * 戊己 土
     * 庚辛 金
     * 壬癸 水
     */
    private FiveElement elementOfStem(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        int stemIndex =
                findStemIndex(
                        stem
                );

        return switch (
                stemIndex / 2
                ) {

            case 0 ->
                    FiveElement.WOOD;

            case 1 ->
                    FiveElement.FIRE;

            case 2 ->
                    FiveElement.EARTH;

            case 3 ->
                    FiveElement.METAL;

            case 4 ->
                    FiveElement.WATER;

            default ->
                    throw new IllegalStateException(
                            "천간 오행을 판정할 수 없습니다: "
                                    + stem
                    );
        };
    }

    private int findStemIndex(
            HeavenlyStem stem
    ) {

        for (
                int index = 0;
                index < 10;
                index++
        ) {

            if (
                    HeavenlyStem.fromIndex(
                            index
                    )
                            == stem
            ) {

                return index;
            }
        }

        throw new IllegalArgumentException(
                "알 수 없는 천간입니다: "
                        + stem
        );
    }
}