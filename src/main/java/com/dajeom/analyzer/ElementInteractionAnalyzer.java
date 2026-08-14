package com.dajeom.analyzer;

import com.dajeom.model.BasePowerAnalysis;
import com.dajeom.model.ElementInteractionAnalysis;
import com.dajeom.model.ElementInteractionEvidence;
import com.dajeom.model.HiddenElementPowerAnalysis;
import com.dajeom.model.SeasonalElementPowerAnalysis;
import com.dajeom.model.VisibleStemPowerAnalysis;
import com.dajeom.type.ElementInteractionType;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 3차-6.
 *
 * 오행 사이의:
 *
 * - 생조
 * - 설기
 * - 극제
 * - 피극
 *
 * 작용을 계산한다.
 *
 * 중요한 설계 원칙:
 *
 * 이 단계의 생극 계산 기준 세력은:
 *
 * 3-1 천간 표면
 * +
 * 3-2 지장간 내부
 * +
 * 3-3 계절 상태
 *
 * 까지만 사용한다.
 *
 * 3-4 통근
 * 3-5 투간
 *
 * 은 여기서 다시 넣지 않는다.
 *
 * 통근과 투간은 3-7에서
 * 독립적인 근거로 최종 합성한다.
 *
 * 이렇게 해야 동일한 통근/투간 효과가
 * 생극 계산과 최종 세력 계산에
 * 이중 반영되지 않는다.
 */
@Component
public class ElementInteractionAnalyzer {

    private final VisibleStemPowerAnalyzer
            visibleStemPowerAnalyzer;

    private final HiddenElementPowerAnalyzer
            hiddenElementPowerAnalyzer;

    private final SeasonalElementPowerAnalyzer
            seasonalElementPowerAnalyzer;

    public ElementInteractionAnalyzer(

            VisibleStemPowerAnalyzer visibleStemPowerAnalyzer,

            HiddenElementPowerAnalyzer hiddenElementPowerAnalyzer,

            SeasonalElementPowerAnalyzer seasonalElementPowerAnalyzer

    ) {

        this.visibleStemPowerAnalyzer =
                Objects.requireNonNull(
                        visibleStemPowerAnalyzer,
                        "VisibleStemPowerAnalyzer가 필요합니다."
                );

        this.hiddenElementPowerAnalyzer =
                Objects.requireNonNull(
                        hiddenElementPowerAnalyzer,
                        "HiddenElementPowerAnalyzer가 필요합니다."
                );

        this.seasonalElementPowerAnalyzer =
                Objects.requireNonNull(
                        seasonalElementPowerAnalyzer,
                        "SeasonalElementPowerAnalyzer가 필요합니다."
                );
    }

    /**
     * 필요한 3-1 ~ 3-3 결과를
     * 내부에서 계산한다.
     */
    public ElementInteractionAnalysis analyze(
            BasePowerAnalysis basePowerAnalysis
    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        VisibleStemPowerAnalysis visiblePower =
                visibleStemPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        HiddenElementPowerAnalysis hiddenPower =
                hiddenElementPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        SeasonalElementPowerAnalysis seasonalPower =
                seasonalElementPowerAnalyzer.analyze(
                        basePowerAnalysis,
                        hiddenPower
                );

        return analyze(
                visiblePower,
                hiddenPower,
                seasonalPower
        );
    }

    /**
     * 이미 계산된 3-1 ~ 3-3 결과를 사용한다.
     *
     * 3-7 통합 엔진에서는
     * 이 오버로드를 사용하면
     * 중복 계산을 줄일 수 있다.
     */
    public ElementInteractionAnalysis analyze(

            VisibleStemPowerAnalysis visiblePower,

            HiddenElementPowerAnalysis hiddenPower,

            SeasonalElementPowerAnalysis seasonalPower

    ) {

        Objects.requireNonNull(
                visiblePower,
                "천간 표면 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenPower,
                "지장간 내부 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                seasonalPower,
                "월령 계절 세력 분석이 필요합니다."
        );

        Map<FiveElement, Integer> rawBasePowers =
                createZeroElementMap();

        Map<FiveElement, Integer> preInteractionPowers =
                createZeroElementMap();

        /*
         * 먼저 각 오행의:
         *
         * 표면
         * +
         * 지장간
         *
         * 원시 세력을 만든다.
         */
        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int rawBasePower =
                    visiblePower.baseWeightOf(
                            element
                    )
                            + hiddenPower.baseWeightOf(
                            element
                    );

            rawBasePowers.put(
                    element,
                    rawBasePower
            );

            /*
             * 여기에 3-3 계절 보정률만 적용.
             *
             * 아직 통근/투간은 적용하지 않는다.
             */
            int seasonalRate =
                    seasonalPower.seasonalRateOf(
                            element
                    );

            int preInteractionPower =
                    applyRate(
                            rawBasePower,
                            seasonalRate
                    );

            preInteractionPowers.put(
                    element,
                    preInteractionPower
            );
        }

        List<ElementInteractionEvidence> evidences =
                new ArrayList<>();

        Map<FiveElement, Integer>
                generationSupportReceived =
                createZeroElementMap();

        Map<FiveElement, Integer>
                generationDrainGiven =
                createZeroElementMap();

        Map<FiveElement, Integer>
                controlPressureReceived =
                createZeroElementMap();

        Map<FiveElement, Integer>
                controlExertionGiven =
                createZeroElementMap();

        /*
         * 상생 5개.
         */
        addGeneratingRelation(
                FiveElement.WOOD,
                FiveElement.FIRE,
                preInteractionPowers,
                evidences,
                generationSupportReceived,
                generationDrainGiven
        );

        addGeneratingRelation(
                FiveElement.FIRE,
                FiveElement.EARTH,
                preInteractionPowers,
                evidences,
                generationSupportReceived,
                generationDrainGiven
        );

        addGeneratingRelation(
                FiveElement.EARTH,
                FiveElement.METAL,
                preInteractionPowers,
                evidences,
                generationSupportReceived,
                generationDrainGiven
        );

        addGeneratingRelation(
                FiveElement.METAL,
                FiveElement.WATER,
                preInteractionPowers,
                evidences,
                generationSupportReceived,
                generationDrainGiven
        );

        addGeneratingRelation(
                FiveElement.WATER,
                FiveElement.WOOD,
                preInteractionPowers,
                evidences,
                generationSupportReceived,
                generationDrainGiven
        );

        /*
         * 상극 5개.
         */
        addControllingRelation(
                FiveElement.WOOD,
                FiveElement.EARTH,
                preInteractionPowers,
                evidences,
                controlPressureReceived,
                controlExertionGiven
        );

        addControllingRelation(
                FiveElement.EARTH,
                FiveElement.WATER,
                preInteractionPowers,
                evidences,
                controlPressureReceived,
                controlExertionGiven
        );

        addControllingRelation(
                FiveElement.WATER,
                FiveElement.FIRE,
                preInteractionPowers,
                evidences,
                controlPressureReceived,
                controlExertionGiven
        );

        addControllingRelation(
                FiveElement.FIRE,
                FiveElement.METAL,
                preInteractionPowers,
                evidences,
                controlPressureReceived,
                controlExertionGiven
        );

        addControllingRelation(
                FiveElement.METAL,
                FiveElement.WOOD,
                preInteractionPowers,
                evidences,
                controlPressureReceived,
                controlExertionGiven
        );

        return new ElementInteractionAnalysis(
                rawBasePowers,
                preInteractionPowers,
                generationSupportReceived,
                generationDrainGiven,
                controlPressureReceived,
                controlExertionGiven,
                evidences
        );
    }

    private void addGeneratingRelation(

            FiveElement source,

            FiveElement target,

            Map<FiveElement, Integer> powers,

            List<ElementInteractionEvidence> evidences,

            Map<FiveElement, Integer> supportReceived,

            Map<FiveElement, Integer> drainGiven

    ) {

        ElementInteractionEvidence evidence =
                createEvidence(
                        ElementInteractionType.GENERATING,
                        source,
                        target,
                        powers
                );

        evidences.add(
                evidence
        );

        /*
         * source는 target을 생하므로
         * 설기 방향.
         */
        drainGiven.compute(
                source,
                (
                        key,
                        current
                ) ->
                        safeValue(
                                current
                        )
                                + evidence.relationCapacity()
        );

        /*
         * target은 source로부터
         * 생조를 받는다.
         */
        supportReceived.compute(
                target,
                (
                        key,
                        current
                ) ->
                        safeValue(
                                current
                        )
                                + evidence.relationCapacity()
        );
    }

    private void addControllingRelation(

            FiveElement source,

            FiveElement target,

            Map<FiveElement, Integer> powers,

            List<ElementInteractionEvidence> evidences,

            Map<FiveElement, Integer> pressureReceived,

            Map<FiveElement, Integer> exertionGiven

    ) {

        ElementInteractionEvidence evidence =
                createEvidence(
                        ElementInteractionType.CONTROLLING,
                        source,
                        target,
                        powers
                );

        evidences.add(
                evidence
        );

        /*
         * source는 target을 극하기 위해
         * 힘을 사용한다.
         */
        exertionGiven.compute(
                source,
                (
                        key,
                        current
                ) ->
                        safeValue(
                                current
                        )
                                + evidence.relationCapacity()
        );

        /*
         * target은 source에게
         * 극제 압력을 받는다.
         */
        pressureReceived.compute(
                target,
                (
                        key,
                        current
                ) ->
                        safeValue(
                                current
                        )
                                + evidence.relationCapacity()
        );
    }

    private ElementInteractionEvidence createEvidence(

            ElementInteractionType interactionType,

            FiveElement source,

            FiveElement target,

            Map<FiveElement, Integer> powers

    ) {

        int sourcePower =
                powers.getOrDefault(
                        source,
                        0
                );

        int targetPower =
                powers.getOrDefault(
                        target,
                        0
                );

        /*
         * 관계가 실제로 작동할 수 있는 범위는
         * 둘 중 더 약한 쪽의 세력을
         * 넘을 수 없다고 본다.
         *
         * 이것을 "최종 감소량"으로 사용하지 않고
         * 관계 capacity로만 저장한다.
         */
        int relationCapacity =
                Math.min(
                        sourcePower,
                        targetPower
                );

        int total =
                sourcePower
                        + targetPower;

        int sourceShareRate;

        int targetShareRate;

        if (
                total <= 0
        ) {

            sourceShareRate =
                    0;

            targetShareRate =
                    0;

        } else {

            sourceShareRate =
                    (int) Math.round(
                            (
                                    sourcePower
                                            * 100.0
                            )
                                    / total
                    );

            /*
             * 반올림 오차를 없애기 위해
             * target은 100 - source로 계산.
             */
            targetShareRate =
                    100
                            - sourceShareRate;
        }

        return new ElementInteractionEvidence(
                interactionType,
                source,
                target,
                sourcePower,
                targetPower,
                relationCapacity,
                sourceShareRate,
                targetShareRate
        );
    }

    private int applyRate(

            int power,

            int rate

    ) {

        if (
                power <= 0
        ) {

            return 0;
        }

        return Math.max(
                0,
                (int) Math.round(
                        power
                                * (
                                rate / 100.0
                        )
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
}