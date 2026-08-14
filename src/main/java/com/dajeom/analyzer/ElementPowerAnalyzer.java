package com.dajeom.analyzer;

import com.dajeom.model.BasePowerAnalysis;
import com.dajeom.model.ElementInteractionAnalysis;
import com.dajeom.model.ElementPowerAnalysis;
import com.dajeom.model.ElementPowerEvidence;
import com.dajeom.model.HiddenElementPowerAnalysis;
import com.dajeom.model.RootElementPowerAnalysis;
import com.dajeom.model.SeasonalElementPowerAnalysis;
import com.dajeom.model.StemExposureElementPowerAnalysis;
import com.dajeom.model.VisibleStemPowerAnalysis;
import com.dajeom.rule.ElementPowerRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.SeasonalPowerState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 3차-7.
 *
 * 지금까지 계산한 모든 오행 세력 근거를
 * 최종 상대 세력으로 통합한다.
 *
 * 처리 순서:
 *
 * 3-1 천간 표면 세력
 *        +
 * 3-2 지장간 내부 세력
 *        ↓
 * 원시 세력
 *        ↓
 * 3-3 월령 계절 보정
 *        ↓
 * 3-4 통근 활성
 *        ↓
 * 3-5 투간 활성
 *        ↓
 * 3-6 생조·설기·극제
 *        ↓
 * 최종 오행 세력
 *
 * 중요:
 *
 * 통근과 투간은 지장간 자체의 세력을
 * 다시 추가하는 방식이 아니다.
 *
 * 이미 존재하는 세력이 얼마나
 * 실제 작동하고 있는지를 제한적으로
 * 보정하는 방식으로 사용한다.
 */
@Component
public class ElementPowerAnalyzer {

    private final VisibleStemPowerAnalyzer
            visibleStemPowerAnalyzer;

    private final HiddenElementPowerAnalyzer
            hiddenElementPowerAnalyzer;

    private final SeasonalElementPowerAnalyzer
            seasonalElementPowerAnalyzer;

    private final RootElementPowerAnalyzer
            rootElementPowerAnalyzer;

    private final StemExposureElementPowerAnalyzer
            stemExposureElementPowerAnalyzer;

    private final ElementInteractionAnalyzer
            elementInteractionAnalyzer;

    private final ElementPowerRule
            elementPowerRule;

    public ElementPowerAnalyzer(

            VisibleStemPowerAnalyzer visibleStemPowerAnalyzer,

            HiddenElementPowerAnalyzer hiddenElementPowerAnalyzer,

            SeasonalElementPowerAnalyzer seasonalElementPowerAnalyzer,

            RootElementPowerAnalyzer rootElementPowerAnalyzer,

            StemExposureElementPowerAnalyzer stemExposureElementPowerAnalyzer,

            ElementInteractionAnalyzer elementInteractionAnalyzer,

            ElementPowerRule elementPowerRule

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

        this.rootElementPowerAnalyzer =
                Objects.requireNonNull(
                        rootElementPowerAnalyzer,
                        "RootElementPowerAnalyzer가 필요합니다."
                );

        this.stemExposureElementPowerAnalyzer =
                Objects.requireNonNull(
                        stemExposureElementPowerAnalyzer,
                        "StemExposureElementPowerAnalyzer가 필요합니다."
                );

        this.elementInteractionAnalyzer =
                Objects.requireNonNull(
                        elementInteractionAnalyzer,
                        "ElementInteractionAnalyzer가 필요합니다."
                );

        this.elementPowerRule =
                Objects.requireNonNull(
                        elementPowerRule,
                        "ElementPowerRule이 필요합니다."
                );
    }

    /**
     * 2차 기초 분석에서
     * 전체 3차 오행 세력을 계산한다.
     */
    public ElementPowerAnalysis analyze(
            BasePowerAnalysis basePowerAnalysis
    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        /*
         * 3-1
         */
        VisibleStemPowerAnalysis visiblePower =
                visibleStemPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        /*
         * 3-2
         */
        HiddenElementPowerAnalysis hiddenPower =
                hiddenElementPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        /*
         * 3-3
         */
        SeasonalElementPowerAnalysis seasonalPower =
                seasonalElementPowerAnalyzer.analyze(
                        basePowerAnalysis,
                        hiddenPower
                );

        /*
         * 3-4
         */
        RootElementPowerAnalysis rootPower =
                rootElementPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        /*
         * 3-5
         */
        StemExposureElementPowerAnalysis exposurePower =
                stemExposureElementPowerAnalyzer.analyze(
                        basePowerAnalysis,
                        hiddenPower
                );

        /*
         * 3-6
         */
        ElementInteractionAnalysis interaction =
                elementInteractionAnalyzer.analyze(
                        visiblePower,
                        hiddenPower,
                        seasonalPower
                );

        return analyze(
                visiblePower,
                hiddenPower,
                seasonalPower,
                rootPower,
                exposurePower,
                interaction
        );
    }

    /**
     * 이미 계산된 각 분석 결과를 받아
     * 최종 오행 세력만 합성한다.
     *
     * 이후 3-8 통합 Engine에서
     * 중복 계산을 줄일 때 사용할 수 있다.
     */
    public ElementPowerAnalysis analyze(

            VisibleStemPowerAnalysis visiblePower,

            HiddenElementPowerAnalysis hiddenPower,

            SeasonalElementPowerAnalysis seasonalPower,

            RootElementPowerAnalysis rootPower,

            StemExposureElementPowerAnalysis exposurePower,

            ElementInteractionAnalysis interaction

    ) {

        Objects.requireNonNull(
                visiblePower,
                "천간 표면 세력이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenPower,
                "지장간 내부 세력이 필요합니다."
        );

        Objects.requireNonNull(
                seasonalPower,
                "월령 계절 세력이 필요합니다."
        );

        Objects.requireNonNull(
                rootPower,
                "통근 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                exposurePower,
                "투간 활성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                interaction,
                "생극 분석이 필요합니다."
        );

        List<ElementDraft> drafts =
                new ArrayList<>();

        /*
         * 오행 5개 각각의 최종 세력을 계산.
         */
        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int visibleBase =
                    visiblePower.baseWeightOf(
                            element
                    );

            int hiddenBase =
                    hiddenPower.baseWeightOf(
                            element
                    );

            int rawBase =
                    visibleBase
                            + hiddenBase;

            /*
             * ElementInteractionAnalyzer에서
             * 동일한 원시합계가 사용되었는지 검증.
             */
            int interactionRawBase =
                    interaction.rawBasePowerOf(
                            element
                    );

            if (
                    rawBase
                            != interactionRawBase
            ) {

                throw new IllegalStateException(
                        "오행 원시 세력 계산이 일치하지 않습니다: "
                                + element
                );
            }

            SeasonalPowerState seasonalState =
                    seasonalPower.stateOf(
                            element
                    );

            int seasonalRate =
                    seasonalPower.seasonalRateOf(
                            element
                    );

            int seasonalAdjustedPower =
                    interaction.preInteractionPowerOf(
                            element
                    );

            /*
             * 통근.
             */
            int rootActivationIndex =
                    rootPower.totalRootWeightOf(
                            element
                    );

            int rootActivationRate =
                    elementPowerRule
                            .calculateRootActivationRate(
                                    rootActivationIndex,
                                    hiddenBase
                            );

            int rootAdjustment =
                    elementPowerRule
                            .calculateRootAdjustment(
                                    visibleBase,
                                    rootActivationRate
                            );

            /*
             * 투간.
             */
            int exposureActivationRate =
                    exposurePower.activationRateOf(
                            element
                    );

            int exposureAdjustment =
                    elementPowerRule
                            .calculateExposureAdjustment(
                                    hiddenBase,
                                    exposureActivationRate
                            );

            /*
             * 생조.
             */
            int generationSupportCapacity =
                    interaction.supportReceivedOf(
                            element
                    );

            int generationSupportAdjustment =
                    elementPowerRule
                            .calculateGenerationSupportAdjustment(
                                    generationSupportCapacity
                            );

            /*
             * 설기.
             */
            int generationDrainCapacity =
                    interaction.generationDrainOf(
                            element
                    );

            int generationDrainAdjustment =
                    elementPowerRule
                            .calculateGenerationDrainAdjustment(
                                    generationDrainCapacity
                            );

            /*
             * 피극.
             */
            int controlPressureCapacity =
                    interaction.controlPressureOf(
                            element
                    );

            int controlPressureAdjustment =
                    elementPowerRule
                            .calculateControlPressureAdjustment(
                                    controlPressureCapacity
                            );

            /*
             * 다른 오행을 극하기 위해
             * 사용한 힘.
             */
            int controlExertionCapacity =
                    interaction.controlExertionOf(
                            element
                    );

            int controlExertionAdjustment =
                    elementPowerRule
                            .calculateControlExertionAdjustment(
                                    controlExertionCapacity
                            );

            int netInteractionAdjustment =
                    generationSupportAdjustment

                            - generationDrainAdjustment

                            - controlPressureAdjustment

                            - controlExertionAdjustment;

            boolean elementExists =
                    rawBase > 0;

            int finalPower =
                    elementPowerRule
                            .calculateFinalPower(
                                    seasonalAdjustedPower,
                                    rootAdjustment,
                                    exposureAdjustment,
                                    generationSupportAdjustment,
                                    generationDrainAdjustment,
                                    controlPressureAdjustment,
                                    controlExertionAdjustment,
                                    elementExists
                            );

            drafts.add(
                    new ElementDraft(
                            element,
                            visibleBase,
                            hiddenBase,
                            rawBase,
                            seasonalState,
                            seasonalRate,
                            seasonalAdjustedPower,
                            rootActivationIndex,
                            rootActivationRate,
                            rootAdjustment,
                            exposureActivationRate,
                            exposureAdjustment,
                            generationSupportCapacity,
                            generationSupportAdjustment,
                            generationDrainCapacity,
                            generationDrainAdjustment,
                            controlPressureCapacity,
                            controlPressureAdjustment,
                            controlExertionCapacity,
                            controlExertionAdjustment,
                            netInteractionAdjustment,
                            finalPower
                    )
            );
        }

        int totalFinalPower =
                drafts
                        .stream()
                        .mapToInt(
                                ElementDraft::finalPower
                        )
                        .sum();

        Map<FiveElement, Integer> shareBasisPoints =
                calculateShareBasisPoints(
                        drafts,
                        totalFinalPower
                );

        Map<FiveElement, Integer> finalPowers =
                new EnumMap<>(
                        FiveElement.class
                );

        List<ElementPowerEvidence> evidences =
                new ArrayList<>();

        for (
                ElementDraft draft
                : drafts
        ) {

            int share =
                    shareBasisPoints
                            .getOrDefault(
                                    draft.element(),
                                    0
                            );

            finalPowers.put(
                    draft.element(),
                    draft.finalPower()
            );

            evidences.add(
                    new ElementPowerEvidence(
                            draft.element(),
                            draft.visibleBasePower(),
                            draft.hiddenBasePower(),
                            draft.rawBasePower(),
                            draft.seasonalState(),
                            draft.seasonalRate(),
                            draft.seasonalAdjustedPower(),
                            draft.rootActivationIndex(),
                            draft.rootActivationRate(),
                            draft.rootAdjustment(),
                            draft.exposureActivationRate(),
                            draft.exposureAdjustment(),
                            draft.generationSupportCapacity(),
                            draft.generationSupportAdjustment(),
                            draft.generationDrainCapacity(),
                            draft.generationDrainAdjustment(),
                            draft.controlPressureCapacity(),
                            draft.controlPressureAdjustment(),
                            draft.controlExertionCapacity(),
                            draft.controlExertionAdjustment(),
                            draft.netInteractionAdjustment(),
                            draft.finalPower(),
                            share
                    )
            );
        }

        return new ElementPowerAnalysis(
                totalFinalPower,
                evidences,
                finalPowers,
                shareBasisPoints
        );
    }

    /**
     * 5개 오행의 최종 세력을
     * 10000 basis points로 정규화한다.
     *
     * 예:
     *
     * 2534bp
     * =
     * 25.34%
     *
     * Math.round 때문에 총합이
     * 9999 또는 10001이 될 수 있으므로
     * 마지막 오차는 가장 강한 오행에
     * 보정해서 정확히 10000으로 맞춘다.
     */
    private Map<FiveElement, Integer>
    calculateShareBasisPoints(

            List<ElementDraft> drafts,

            int totalFinalPower

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

        if (
                totalFinalPower <= 0
        ) {

            return result;
        }

        int calculatedTotal =
                0;

        for (
                ElementDraft draft
                : drafts
        ) {

            int basisPoints =
                    (int) Math.round(
                            draft.finalPower()
                                    * 10_000.0
                                    / totalFinalPower
                    );

            basisPoints =
                    Math.max(
                            0,
                            Math.min(
                                    10_000,
                                    basisPoints
                            )
                    );

            result.put(
                    draft.element(),
                    basisPoints
            );

            calculatedTotal +=
                    basisPoints;
        }

        int difference =
                10_000
                        - calculatedTotal;

        if (
                difference != 0
        ) {

            FiveElement strongestElement =
                    drafts
                            .stream()
                            .max(
                                    Comparator.comparingInt(
                                            ElementDraft::finalPower
                                    )
                            )
                            .map(
                                    ElementDraft::element
                            )
                            .orElseThrow();

            int corrected =
                    result.get(
                            strongestElement
                    )
                            + difference;

            result.put(
                    strongestElement,
                    Math.max(
                            0,
                            Math.min(
                                    10_000,
                                    corrected
                            )
                    )
            );
        }

        return result;
    }

    /**
     * 최종 Evidence를 만들기 전의
     * 내부 계산용 immutable 데이터.
     */
    private record ElementDraft(

            FiveElement element,

            int visibleBasePower,

            int hiddenBasePower,

            int rawBasePower,

            SeasonalPowerState seasonalState,

            int seasonalRate,

            int seasonalAdjustedPower,

            int rootActivationIndex,

            int rootActivationRate,

            int rootAdjustment,

            int exposureActivationRate,

            int exposureAdjustment,

            int generationSupportCapacity,

            int generationSupportAdjustment,

            int generationDrainCapacity,

            int generationDrainAdjustment,

            int controlPressureCapacity,

            int controlPressureAdjustment,

            int controlExertionCapacity,

            int controlExertionAdjustment,

            int netInteractionAdjustment,

            int finalPower

    ) {

        private ElementDraft {

            Objects.requireNonNull(
                    element
            );

            Objects.requireNonNull(
                    seasonalState
            );
        }
    }
}