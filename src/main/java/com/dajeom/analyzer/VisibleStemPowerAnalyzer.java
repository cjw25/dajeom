package com.dajeom.analyzer;

import com.dajeom.model.BasePowerAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.Pillar;
import com.dajeom.model.VisibleStemPower;
import com.dajeom.model.VisibleStemPowerAnalysis;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 3차-1.
 *
 * 원국 천간의 기본 표면 세력을 계산한다.
 *
 * 중요한 설계 원칙:
 *
 * 여기서는 천간이 "존재한다"는
 * 원시 세력만 계산한다.
 *
 * 월령
 * 통근
 * 투간
 * 지장간
 * 합화
 * 생극
 *
 * 때문에 같은 천간의 세력을
 * 미리 올리거나 내리지 않는다.
 *
 * 그 요소들은 이후 단계에서
 * 독립적인 Evidence로 반영한다.
 */
@Component
public class VisibleStemPowerAnalyzer {

    /**
     * 표면 천간 하나가 가지는
     * 원시 기본 세력.
     *
     * 이 숫자는 확률이나 운세 점수가 아니다.
     *
     * 이후의 모든 조정값을 계산하기 위한
     * 내부 상대 기준점이다.
     */
    private static final int
            BASE_VISIBLE_STEM_WEIGHT =
            100;

    /**
     * FourPillars에서 직접 분석.
     */
    public VisibleStemPowerAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        List<VisibleStemPower> visibleStems =
                new ArrayList<>();

        addVisibleStem(
                visibleStems,
                fourPillars.yearPillar(),
                VisibleStemPosition.YEAR
        );

        addVisibleStem(
                visibleStems,
                fourPillars.monthPillar(),
                VisibleStemPosition.MONTH
        );

        addVisibleStem(
                visibleStems,
                fourPillars.dayPillar(),
                VisibleStemPosition.DAY
        );

        fourPillars
                .hourPillarOptional()
                .ifPresent(
                        hourPillar ->
                                addVisibleStem(
                                        visibleStems,
                                        hourPillar,
                                        VisibleStemPosition.HOUR
                                )
                );

        HeavenlyStem dayMaster =
                fourPillars
                        .dayPillar()
                        .stem();

        FiveElement dayMasterElement =
                elementOfStem(
                        dayMaster
                );

        Map<FiveElement, Integer>
                elementBaseWeights =
                createZeroElementMap();

        Map<FiveElement, Integer>
                elementVisibleCounts =
                createZeroElementMap();

        for (
                VisibleStemPower visibleStem
                : visibleStems
        ) {

            elementBaseWeights.compute(
                    visibleStem.element(),
                    (
                            element,
                            current
                    ) ->
                            safeValue(
                                    current
                            )
                                    + visibleStem.baseWeight()
            );

            elementVisibleCounts.compute(
                    visibleStem.element(),
                    (
                            element,
                            current
                    ) ->
                            safeValue(
                                    current
                            )
                                    + 1
            );
        }

        int totalBaseWeight =
                visibleStems
                        .stream()
                        .mapToInt(
                                VisibleStemPower::baseWeight
                        )
                        .sum();

        return new VisibleStemPowerAnalysis(
                dayMaster,
                dayMasterElement,
                visibleStems.size(),
                totalBaseWeight,
                visibleStems,
                elementBaseWeights,
                elementVisibleCounts
        );
    }

    /**
     * 2차에서 생성한 BasePowerAnalysis를
     * 그대로 받아서 분석할 수도 있다.
     *
     * 앞으로 3차 운영 흐름에서는
     * 이 메서드를 주로 사용한다.
     */
    public VisibleStemPowerAnalysis analyze(
            BasePowerAnalysis basePowerAnalysis
    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        return analyze(
                basePowerAnalysis.fourPillars()
        );
    }

    private void addVisibleStem(

            List<VisibleStemPower> result,

            Pillar pillar,

            VisibleStemPosition position

    ) {

        Objects.requireNonNull(
                result,
                "결과 목록이 필요합니다."
        );

        Objects.requireNonNull(
                pillar,
                "기둥이 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "천간 위치가 필요합니다."
        );

        HeavenlyStem stem =
                pillar.stem();

        FiveElement element =
                elementOfStem(
                        stem
                );

        boolean dayMaster =
                position.isDayMasterPosition();

        result.add(
                new VisibleStemPower(
                        position,
                        stem,
                        element,
                        dayMaster,
                        BASE_VISIBLE_STEM_WEIGHT
                )
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
     * 천간 → 오행.
     *
     * 표준 천간 순서:
     *
     * 甲 0
     * 乙 1
     * 丙 2
     * 丁 3
     * 戊 4
     * 己 5
     * 庚 6
     * 辛 7
     * 壬 8
     * 癸 9
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
                            "천간의 오행을 판정할 수 없습니다: "
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