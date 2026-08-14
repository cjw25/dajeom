package com.dajeom.analyzer;

import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.MonthCommandAnalysis;
import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthCommandRelationType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 월령(月令)을 분석한다.
 *
 * 핵심 흐름:
 *
 * 월지
 * ↓
 * 지장간
 * ↓
 * 정기
 * ↓
 * 월령 중심 오행
 * ↓
 * 일간 오행과의 생극관계
 *
 * 이 단계에서는 아직
 *
 * - 신강/신약 최종 판정
 * - 용신 최종 판정
 * - 격국 최종 판정
 *
 * 을 하지 않는다.
 *
 * 이 클래스는 그 판단에 필요한
 * 월령 근거를 구조화해서 제공한다.
 */
@Component
public class MonthCommandAnalyzer {

    private final HiddenStemCalculator
            hiddenStemCalculator;

    public MonthCommandAnalyzer(
            HiddenStemCalculator hiddenStemCalculator
    ) {

        this.hiddenStemCalculator =
                Objects.requireNonNull(
                        hiddenStemCalculator,
                        "HiddenStemCalculator가 필요합니다."
                );
    }

    /**
     * 원국의 월령을 분석한다.
     */
    public MonthCommandAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        Pillar dayPillar =
                fourPillars.dayPillar();

        Pillar monthPillar =
                fourPillars.monthPillar();

        HeavenlyStem dayMaster =
                dayPillar.stem();

        EarthlyBranch monthBranch =
                monthPillar.branch();

        FiveElement dayMasterElement =
                elementOfStem(
                        dayMaster
                );

        List<HiddenStem> hiddenStems =
                hiddenStemCalculator.calculate(
                        monthBranch
                );

        HiddenStem mainHiddenStem =
                hiddenStemCalculator
                        .getMainHiddenStem(
                                monthBranch
                        );

        FiveElement monthCommandElement =
                elementOfStem(
                        mainHiddenStem.stem()
                );

        MonthCommandRelationType relationType =
                determineRelation(
                        dayMasterElement,
                        monthCommandElement
                );

        Map<FiveElement, Integer>
                hiddenStemElementWeights =
                calculateHiddenStemElementWeights(
                        hiddenStems
                );

        boolean sameElementSupport =
                relationType.sameElement();

        boolean resourceSupport =
                relationType.resourceSupport();

        boolean seasonallySupported =
                relationType.supportive();

        boolean draining =
                relationType.draining();

        boolean controlled =
                relationType.controlling();

        return new MonthCommandAnalysis(
                dayMaster,
                dayMasterElement,
                monthBranch,
                mainHiddenStem.stem(),
                monthCommandElement,
                relationType,
                seasonallySupported,
                sameElementSupport,
                resourceSupport,
                draining,
                controlled,
                mainHiddenStem.weight(),
                hiddenStems.size(),
                hiddenStems,
                hiddenStemElementWeights
        );
    }

    /**
     * 일간이 월령의 직접적인
     * 계절 지원을 받는지 확인.
     *
     * 동기 또는 생조인 경우 true.
     */
    public boolean isSeasonallySupported(
            FourPillars fourPillars
    ) {

        return analyze(
                fourPillars
        ).seasonallySupported();
    }

    /**
     * 일간과 월령 중심 오행이
     * 정확히 같은 오행인지.
     */
    public boolean isSameElementSeason(
            FourPillars fourPillars
    ) {

        return analyze(
                fourPillars
        ).sameElementSupport();
    }

    /**
     * 월령이 일간을 생하는 관계인지.
     */
    public boolean isResourceSeason(
            FourPillars fourPillars
    ) {

        return analyze(
                fourPillars
        ).resourceSupport();
    }

    /**
     * 월령 중심 오행을 가져온다.
     */
    public FiveElement determineMonthCommandElement(
            FourPillars fourPillars
    ) {

        return analyze(
                fourPillars
        ).monthCommandElement();
    }

    /**
     * 월지 지장간을 오행별로 합산한다.
     *
     * 예:
     *
     * 申
     *
     * 庚 60 -> 金 60
     * 壬 25 -> 水 25
     * 戊 15 -> 土 15
     */
    public Map<FiveElement, Integer>
    calculateHiddenStemElementWeights(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        return calculateHiddenStemElementWeights(
                hiddenStemCalculator.calculate(
                        fourPillars
                                .monthPillar()
                                .branch()
                )
        );
    }

    private Map<FiveElement, Integer>
    calculateHiddenStemElementWeights(
            List<HiddenStem> hiddenStems
    ) {

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

        for (
                HiddenStem hiddenStem
                : hiddenStems
        ) {

            FiveElement element =
                    elementOfStem(
                            hiddenStem.stem()
                    );

            result.compute(
                    element,
                    (
                            key,
                            current
                    ) ->
                            (
                                    current == null
                                            ? 0
                                            : current
                            )
                                    + hiddenStem.weight()
            );
        }

        return Collections.unmodifiableMap(
                result
        );
    }

    /**
     * 월령 오행과 일간 오행의 관계를
     * 일간 기준으로 판정한다.
     */
    private MonthCommandRelationType determineRelation(

            FiveElement dayMasterElement,

            FiveElement monthElement

    ) {

        /*
         * 같은 오행.
         */
        if (
                dayMasterElement
                        == monthElement
        ) {

            return MonthCommandRelationType
                    .COMPANION;
        }

        /*
         * 월령이 일간을 생함.
         *
         * 예:
         *
         * 木 -> 火
         *
         * 일간 火
         * 월령 木
         */
        if (
                produces(
                        monthElement,
                        dayMasterElement
                )
        ) {

            return MonthCommandRelationType
                    .RESOURCE;
        }

        /*
         * 일간이 월령을 생함.
         *
         * 설기.
         */
        if (
                produces(
                        dayMasterElement,
                        monthElement
                )
        ) {

            return MonthCommandRelationType
                    .OUTPUT;
        }

        /*
         * 일간이 월령을 극함.
         *
         * 재성 방향.
         */
        if (
                controls(
                        dayMasterElement,
                        monthElement
                )
        ) {

            return MonthCommandRelationType
                    .WEALTH;
        }

        /*
         * 월령이 일간을 극함.
         *
         * 관살 방향.
         */
        if (
                controls(
                        monthElement,
                        dayMasterElement
                )
        ) {

            return MonthCommandRelationType
                    .OFFICER;
        }

        throw new IllegalStateException(
                "판정할 수 없는 오행 관계입니다: "
                        + dayMasterElement
                        + " / "
                        + monthElement
        );
    }

    /**
     * 오행 상생.
     *
     * 木 -> 火
     * 火 -> 土
     * 土 -> 金
     * 金 -> 水
     * 水 -> 木
     */
    private boolean produces(

            FiveElement source,

            FiveElement target

    ) {

        return switch (
                source
                ) {

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
     * 오행 상극.
     *
     * 木 -> 土
     * 土 -> 水
     * 水 -> 火
     * 火 -> 金
     * 金 -> 木
     */
    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return switch (
                source
                ) {

            case WOOD ->
                    target
                            == FiveElement.EARTH;

            case FIRE ->
                    target
                            == FiveElement.METAL;

            case EARTH ->
                    target
                            == FiveElement.WATER;

            case METAL ->
                    target
                            == FiveElement.WOOD;

            case WATER ->
                    target
                            == FiveElement.FIRE;
        };
    }

    /**
     * 천간의 오행.
     *
     * 10천간 표준 순서:
     *
     * 0 甲 -> 木
     * 1 乙 -> 木
     *
     * 2 丙 -> 火
     * 3 丁 -> 火
     *
     * 4 戊 -> 土
     * 5 己 -> 土
     *
     * 6 庚 -> 金
     * 7 辛 -> 金
     *
     * 8 壬 -> 水
     * 9 癸 -> 水
     *
     * HeavenlyStem 내부 필드명에
     * 직접 의존하지 않도록 한다.
     */
    private FiveElement elementOfStem(
            HeavenlyStem stem
    ) {

        int index =
                findStemIndex(
                        stem
                );

        return switch (
                index / 2
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

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

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