package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 3차 전체 오행 세력 분석 결과.
 *
 * 다음 분석 결과를 하나로 묶는다.
 *
 * 3-1 천간 표면 세력
 * 3-2 지장간 내부 세력
 * 3-3 월령 계절 세력
 * 3-4 통근 세력
 * 3-5 투간 활성도
 * 3-6 생조·설기·극제
 * 3-7 최종 오행 세력
 *
 * 중요:
 *
 * 이 모델의 Strength는
 * "일간 신강/신약"을 뜻하지 않는다.
 *
 * 木火土金水 각각의 상대적인
 * 오행 세력 분석을 의미한다.
 *
 * 일간의 신강/신약 최종 판단은
 * 정밀화 4차에서 별도로 수행한다.
 */
public record ElementStrengthAnalysis(

        BasePowerAnalysis basePowerAnalysis,

        VisibleStemPowerAnalysis visibleStemPowerAnalysis,

        HiddenElementPowerAnalysis hiddenElementPowerAnalysis,

        SeasonalElementPowerAnalysis seasonalElementPowerAnalysis,

        RootElementPowerAnalysis rootElementPowerAnalysis,

        StemExposureElementPowerAnalysis
        stemExposureElementPowerAnalysis,

        ElementInteractionAnalysis elementInteractionAnalysis,

        ElementPowerAnalysis elementPowerAnalysis

) {

    public ElementStrengthAnalysis {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                visibleStemPowerAnalysis,
                "천간 표면 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenElementPowerAnalysis,
                "지장간 내부 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                seasonalElementPowerAnalysis,
                "월령 계절 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                rootElementPowerAnalysis,
                "통근 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                stemExposureElementPowerAnalysis,
                "투간 활성도 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementInteractionAnalysis,
                "오행 생극 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementPowerAnalysis,
                "최종 오행 세력 분석이 필요합니다."
        );

        validateDayMasterConsistency(
                basePowerAnalysis,
                visibleStemPowerAnalysis,
                rootElementPowerAnalysis
        );

        validateChartCompleteness(
                basePowerAnalysis,
                visibleStemPowerAnalysis,
                hiddenElementPowerAnalysis
        );

        validateMonthCommandConsistency(
                basePowerAnalysis,
                seasonalElementPowerAnalysis
        );

        validateRawPowerConsistency(
                visibleStemPowerAnalysis,
                hiddenElementPowerAnalysis,
                elementInteractionAnalysis
        );
    }

    /**
     * 일간.
     */
    public HeavenlyStem dayMaster() {

        return basePowerAnalysis
                .dayMaster();
    }

    /**
     * 일간 오행.
     */
    public FiveElement dayMasterElement() {

        return visibleStemPowerAnalysis
                .dayMasterElement();
    }

    /**
     * 월령 중심 오행.
     */
    public FiveElement monthCommandElement() {

        return seasonalElementPowerAnalysis
                .monthCommandElement();
    }

    /**
     * 특정 오행의 최종 세력.
     */
    public int powerOf(
            FiveElement element
    ) {

        return elementPowerAnalysis
                .powerOf(
                        element
                );
    }

    /**
     * 특정 오행의 최종 상대 비중.
     *
     * 예:
     *
     * 25.34
     *
     * -> 전체 오행 세력 가운데
     *    25.34%의 상대 비중.
     *
     * 확률이 아니다.
     */
    public double sharePercentOf(
            FiveElement element
    ) {

        return elementPowerAnalysis
                .sharePercentOf(
                        element
                );
    }

    /**
     * 특정 오행의 상세 계산 근거.
     */
    public Optional<ElementPowerEvidence>
    evidenceOf(
            FiveElement element
    ) {

        return elementPowerAnalysis
                .evidenceOf(
                        element
                );
    }

    /**
     * 최종적으로 가장 강한 오행.
     */
    public Optional<FiveElement>
    strongestElement() {

        return elementPowerAnalysis
                .strongestElement();
    }

    /**
     * 실제 원국에 존재하는 오행 가운데
     * 최종적으로 가장 약한 오행.
     */
    public Optional<FiveElement>
    weakestExistingElement() {

        return elementPowerAnalysis
                .weakestExistingElement();
    }

    /**
     * 최종 오행 세력을
     * 강한 순서대로 반환.
     */
    public List<ElementPowerEvidence>
    orderedByPowerDescending() {

        return elementPowerAnalysis
                .orderedByPowerDescending();
    }

    /**
     * 일간이 통근했는지.
     */
    public boolean dayMasterRooted() {

        return rootElementPowerAnalysis
                .dayMasterRooted();
    }

    /**
     * 일간이 같은 천간으로
     * 직접 통근했는지.
     */
    public boolean dayMasterDirectRooted() {

        return rootElementPowerAnalysis
                .dayMasterDirectRooted();
    }

    /**
     * 일간이 월지에 뿌리를
     * 확보했는지.
     */
    public boolean dayMasterMonthRooted() {

        return rootElementPowerAnalysis
                .dayMasterMonthRooted();
    }

    /**
     * 일간이 정기에
     * 뿌리를 확보했는지.
     */
    public boolean dayMasterMainQiRooted() {

        return rootElementPowerAnalysis
                .dayMasterMainQiRooted();
    }

    /**
     * 일간이 월지 정기에
     * 직접 통근했는지.
     */
    public boolean dayMasterStrongMonthMainRoot() {

        return rootElementPowerAnalysis
                .dayMasterStrongMonthMainRoot();
    }

    /**
     * 일간의 통근 활성 지수.
     */
    public int dayMasterRootWeight() {

        return rootElementPowerAnalysis
                .dayMasterRootWeight();
    }

    /**
     * 일간이 월령으로부터
     * 계절적 지원을 받는지.
     */
    public boolean dayMasterSeasonallySupported() {

        return basePowerAnalysis
                .dayMasterSeasonallySupported();
    }

    /**
     * 일간이 계절적으로
     * 설기·소모되는 관계인지.
     */
    public boolean dayMasterSeasonallyDrained() {

        return basePowerAnalysis
                .dayMasterSeasonallyDrained();
    }

    /**
     * 일간이 월령으로부터
     * 직접 극을 받는 관계인지.
     */
    public boolean dayMasterSeasonallyControlled() {

        return basePowerAnalysis
                .dayMasterSeasonallyControlled();
    }

    /**
     * 월지 정기가 천간으로
     * 투출했는지.
     *
     * 격국 분석에서도 사용한다.
     */
    public boolean monthMainQiExposed() {

        return basePowerAnalysis
                .hasMonthMainQiExposure();
    }

    /**
     * 출생시간까지 모두 확정된
     * 4주 원국인지.
     */
    public boolean completeChart() {

        return basePowerAnalysis
                .completeChart();
    }

    /**
     * 출생시간 미상으로
     * 시주가 없는 원국인지.
     */
    public boolean hourUnknown() {

        return basePowerAnalysis
                .hourUnknown();
    }

    /**
     * 전체 최종 오행 세력.
     *
     * 상대 비중 계산의 denominator.
     */
    public int totalFinalPower() {

        return elementPowerAnalysis
                .totalFinalPower();
    }

    /**
     * 각 단계에서 일간이 동일하게
     * 유지되고 있는지 확인.
     */
    private static void validateDayMasterConsistency(

            BasePowerAnalysis basePowerAnalysis,

            VisibleStemPowerAnalysis visibleStemPowerAnalysis,

            RootElementPowerAnalysis rootElementPowerAnalysis

    ) {

        HeavenlyStem baseDayMaster =
                basePowerAnalysis
                        .dayMaster();

        HeavenlyStem visibleDayMaster =
                visibleStemPowerAnalysis
                        .dayMaster();

        HeavenlyStem rootDayMaster =
                rootElementPowerAnalysis
                        .dayMaster();

        if (
                baseDayMaster
                        != visibleDayMaster
                        || baseDayMaster
                        != rootDayMaster
        ) {

            throw new IllegalArgumentException(
                    "오행 세력 분석 단계의 일간이 서로 일치하지 않습니다."
            );
        }

        FiveElement visibleDayMasterElement =
                visibleStemPowerAnalysis
                        .dayMasterElement();

        FiveElement rootDayMasterElement =
                rootElementPowerAnalysis
                        .dayMasterElement();

        if (
                visibleDayMasterElement
                        != rootDayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "오행 세력 분석 단계의 일간 오행이 서로 일치하지 않습니다."
            );
        }
    }

    /**
     * 출생시간 미상 여부가 각 분석 단계에서
     * 동일하게 유지되는지 확인.
     */
    private static void validateChartCompleteness(

            BasePowerAnalysis basePowerAnalysis,

            VisibleStemPowerAnalysis visibleStemPowerAnalysis,

            HiddenElementPowerAnalysis hiddenElementPowerAnalysis

    ) {

        boolean baseComplete =
                basePowerAnalysis
                        .completeChart();

        boolean visibleComplete =
                visibleStemPowerAnalysis
                        .completeChart();

        boolean hiddenComplete =
                hiddenElementPowerAnalysis
                        .completeChart();

        if (
                baseComplete
                        != visibleComplete
                        || baseComplete
                        != hiddenComplete
        ) {

            throw new IllegalArgumentException(
                    "출생시간 확정 여부가 오행 분석 단계에서 서로 일치하지 않습니다."
            );
        }
    }

    /**
     * 월령이 각 단계에서 동일한지 확인.
     */
    private static void validateMonthCommandConsistency(

            BasePowerAnalysis basePowerAnalysis,

            SeasonalElementPowerAnalysis
                    seasonalElementPowerAnalysis

    ) {

        if (
                basePowerAnalysis
                        .monthCommandAnalysis()
                        .monthBranch()
                        != seasonalElementPowerAnalysis
                        .monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "월지가 월령 계절 분석과 일치하지 않습니다."
            );
        }

        if (
                basePowerAnalysis
                        .monthCommandAnalysis()
                        .monthCommandElement()
                        != seasonalElementPowerAnalysis
                        .monthCommandElement()
        ) {

            throw new IllegalArgumentException(
                    "월령 중심 오행이 계절 분석과 일치하지 않습니다."
            );
        }
    }

    /**
     * 3-1 + 3-2 원시 합계가
     * 3-6 생극 분석에서 사용한 값과
     * 동일한지 확인한다.
     */
    private static void validateRawPowerConsistency(

            VisibleStemPowerAnalysis visibleStemPowerAnalysis,

            HiddenElementPowerAnalysis hiddenElementPowerAnalysis,

            ElementInteractionAnalysis elementInteractionAnalysis

    ) {

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int expected =
                    visibleStemPowerAnalysis
                            .baseWeightOf(
                                    element
                            )
                            + hiddenElementPowerAnalysis
                            .baseWeightOf(
                                    element
                            );

            int actual =
                    elementInteractionAnalysis
                            .rawBasePowerOf(
                                    element
                            );

            if (
                    expected != actual
            ) {

                throw new IllegalArgumentException(
                        "오행 원시 세력이 분석 단계 간 일치하지 않습니다: "
                                + element
                );
            }
        }
    }
}