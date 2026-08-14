package com.dajeom.analyzer;

import com.dajeom.model.DayMasterElementRelationAnalysis;
import com.dajeom.model.DayMasterForceEvidence;
import com.dajeom.model.DayMasterForceFormationAnalysis;
import com.dajeom.model.DayMasterPressureEvidence;
import com.dajeom.model.DayMasterPressurePowerAnalysis;
import com.dajeom.model.ElementPowerEvidence;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.rule.DayMasterPressurePowerRule;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 4차-6.
 *
 * 식상·재성·관살의 실제 약화 압력을 계산한다.
 *
 * 역할:
 *
 * OUTPUT
 * -> 일간의 설기
 *
 * WEALTH
 * -> 일간이 재성을 제어하면서 사용하는 힘
 *
 * OFFICER
 * -> 일간이 직접 극을 받는 힘
 *
 * 중요한 설계 원칙:
 *
 * 1.
 * 3차 최종 오행 세력을 재사용한다.
 *
 * 2.
 * 계절/통근/투간을 여기서
 * 또 가산하지 않는다.
 *
 * 이미 ElementPowerAnalysis에
 * 반영되어 있기 때문이다.
 *
 * 3.
 * 득령/득지/득세 역시
 * 여기서 추가 점수로 넣지 않는다.
 *
 * 4-8 StrengthRule에서
 * 별도의 구조 Evidence로 함께 사용한다.
 */
@Component
public class DayMasterPressurePowerAnalyzer {

    private final DayMasterElementRelationAnalyzer
            dayMasterElementRelationAnalyzer;

    private final DayMasterForceFormationAnalyzer
            dayMasterForceFormationAnalyzer;

    private final DayMasterPressurePowerRule
            dayMasterPressurePowerRule;

    public DayMasterPressurePowerAnalyzer(

            DayMasterElementRelationAnalyzer
                    dayMasterElementRelationAnalyzer,

            DayMasterForceFormationAnalyzer
                    dayMasterForceFormationAnalyzer,

            DayMasterPressurePowerRule
                    dayMasterPressurePowerRule

    ) {

        this.dayMasterElementRelationAnalyzer =
                Objects.requireNonNull(
                        dayMasterElementRelationAnalyzer,
                        "DayMasterElementRelationAnalyzer가 필요합니다."
                );

        this.dayMasterForceFormationAnalyzer =
                Objects.requireNonNull(
                        dayMasterForceFormationAnalyzer,
                        "DayMasterForceFormationAnalyzer가 필요합니다."
                );

        this.dayMasterPressurePowerRule =
                Objects.requireNonNull(
                        dayMasterPressurePowerRule,
                        "DayMasterPressurePowerRule이 필요합니다."
                );
    }

    /**
     * 필요한 4-1 / 4-4 결과를
     * 내부에서 계산한다.
     */
    public DayMasterPressurePowerAnalysis analyze(
            ElementStrengthAnalysis elementStrength
    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        DayMasterElementRelationAnalysis relations =
                dayMasterElementRelationAnalyzer
                        .analyze(
                                elementStrength
                        );

        DayMasterForceFormationAnalysis formation =
                dayMasterForceFormationAnalyzer
                        .analyze(
                                elementStrength,
                                relations
                        );

        return analyze(
                elementStrength,
                relations,
                formation
        );
    }

    /**
     * 4차 통합 Engine에서
     * 기존 계산 결과를 재사용하기 위한 오버로드.
     */
    public DayMasterPressurePowerAnalysis analyze(

            ElementStrengthAnalysis elementStrength,

            DayMasterElementRelationAnalysis relations,

            DayMasterForceFormationAnalysis formation

    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                relations,
                "일간 기준 오행 관계 분석이 필요합니다."
        );

        Objects.requireNonNull(
                formation,
                "득세 구조 분석이 필요합니다."
        );

        if (
                elementStrength.dayMaster()
                        != relations.dayMaster()

                        || elementStrength.dayMaster()
                        != formation.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "4차 분석 단계의 일간이 서로 일치하지 않습니다."
            );
        }

        FiveElement dayMasterElement =
                elementStrength
                        .dayMasterElement();

        FiveElement outputElement =
                relations
                        .outputElement();

        FiveElement wealthElement =
                relations
                        .wealthElement();

        FiveElement officerElement =
                relations
                        .officerElement();

        /*
         * 일간 오행 자체의 최종 세력.
         *
         * 이 값은:
         *
         * - 일간 자체
         * - 외부 비겁
         * - 계절
         * - 통근
         * - 투간
         * - 생극
         *
         * 등을 반영한 3차 결과다.
         */
        int dayMasterElementFinalPower =
                requireElementPowerEvidence(
                        elementStrength,
                        dayMasterElement
                )
                        .finalPower();

        /*
         * =================================================
         * 식상
         * =================================================
         */

        ElementPowerEvidence outputPowerEvidence =
                requireElementPowerEvidence(
                        elementStrength,
                        outputElement
                );

        List<DayMasterForceEvidence> outputSources =
                formation
                        .evidences()
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::output
                        )
                        .toList();

        validateRawPowerConsistency(
                outputElement,
                outputPowerEvidence,
                outputSources
        );

        int outputCapacity =
                dayMasterPressurePowerRule
                        .calculateOutputDrainCapacity(
                                dayMasterElementFinalPower,
                                outputPowerEvidence.finalPower()
                        );

        int outputDrainPower =
                dayMasterPressurePowerRule
                        .calculateOutputDrainPower(
                                outputCapacity
                        );

        DayMasterPressureEvidence outputEvidence =
                new DayMasterPressureEvidence(
                        DayMasterElementRole.OUTPUT,
                        outputElement,
                        outputPowerEvidence.rawBasePower(),
                        outputPowerEvidence.finalPower(),
                        outputCapacity,
                        dayMasterPressurePowerRule
                                .outputDrainRate(),
                        outputDrainPower,
                        countVisibleSources(
                                outputSources
                        ),
                        countHiddenSources(
                                outputSources
                        ),
                        countExposedHiddenSources(
                                outputSources
                        )
                );

        /*
         * =================================================
         * 재성
         * =================================================
         */

        ElementPowerEvidence wealthPowerEvidence =
                requireElementPowerEvidence(
                        elementStrength,
                        wealthElement
                );

        List<DayMasterForceEvidence> wealthSources =
                formation
                        .evidences()
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::wealth
                        )
                        .toList();

        validateRawPowerConsistency(
                wealthElement,
                wealthPowerEvidence,
                wealthSources
        );

        int wealthCapacity =
                dayMasterPressurePowerRule
                        .calculateWealthExertionCapacity(
                                dayMasterElementFinalPower,
                                wealthPowerEvidence.finalPower()
                        );

        int wealthExertionPower =
                dayMasterPressurePowerRule
                        .calculateWealthExertionPower(
                                wealthCapacity
                        );

        DayMasterPressureEvidence wealthEvidence =
                new DayMasterPressureEvidence(
                        DayMasterElementRole.WEALTH,
                        wealthElement,
                        wealthPowerEvidence.rawBasePower(),
                        wealthPowerEvidence.finalPower(),
                        wealthCapacity,
                        dayMasterPressurePowerRule
                                .wealthExertionRate(),
                        wealthExertionPower,
                        countVisibleSources(
                                wealthSources
                        ),
                        countHiddenSources(
                                wealthSources
                        ),
                        countExposedHiddenSources(
                                wealthSources
                        )
                );

        /*
         * =================================================
         * 관살
         * =================================================
         */

        ElementPowerEvidence officerPowerEvidence =
                requireElementPowerEvidence(
                        elementStrength,
                        officerElement
                );

        List<DayMasterForceEvidence> officerSources =
                formation
                        .evidences()
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::officer
                        )
                        .toList();

        validateRawPowerConsistency(
                officerElement,
                officerPowerEvidence,
                officerSources
        );

        int officerCapacity =
                dayMasterPressurePowerRule
                        .calculateOfficerControlCapacity(
                                officerPowerEvidence.finalPower(),
                                dayMasterElementFinalPower
                        );

        int officerControlPower =
                dayMasterPressurePowerRule
                        .calculateOfficerControlPower(
                                officerCapacity
                        );

        DayMasterPressureEvidence officerEvidence =
                new DayMasterPressureEvidence(
                        DayMasterElementRole.OFFICER,
                        officerElement,
                        officerPowerEvidence.rawBasePower(),
                        officerPowerEvidence.finalPower(),
                        officerCapacity,
                        dayMasterPressurePowerRule
                                .officerControlRate(),
                        officerControlPower,
                        countVisibleSources(
                                officerSources
                        ),
                        countHiddenSources(
                                officerSources
                        ),
                        countExposedHiddenSources(
                                officerSources
                        )
                );

        /*
         * 세 작용은 의미가 다르지만
         * 4-8에서 지원과 전체 압력을
         * 비교할 수 있도록 총합도 보존한다.
         */
        int totalPressurePower =
                outputDrainPower
                        + wealthExertionPower
                        + officerControlPower;

        int visiblePressureSourceCount =
                outputEvidence.visibleSourceCount()

                        + wealthEvidence.visibleSourceCount()

                        + officerEvidence.visibleSourceCount();

        int hiddenPressureSourceCount =
                outputEvidence.hiddenSourceCount()

                        + wealthEvidence.hiddenSourceCount()

                        + officerEvidence.hiddenSourceCount();

        int exposedHiddenPressureSourceCount =
                outputEvidence.exposedHiddenSourceCount()

                        + wealthEvidence.exposedHiddenSourceCount()

                        + officerEvidence.exposedHiddenSourceCount();

        int pressureSourceCount =
                visiblePressureSourceCount
                        + hiddenPressureSourceCount;

        return new DayMasterPressurePowerAnalysis(
                elementStrength.dayMaster(),
                dayMasterElement,
                outputEvidence,
                wealthEvidence,
                officerEvidence,
                outputDrainPower,
                wealthExertionPower,
                officerControlPower,
                totalPressurePower,
                pressureSourceCount,
                visiblePressureSourceCount,
                hiddenPressureSourceCount,
                exposedHiddenPressureSourceCount
        );
    }

    /**
     * 3차 최종 오행 Evidence 조회.
     */
    private ElementPowerEvidence
    requireElementPowerEvidence(

            ElementStrengthAnalysis elementStrength,

            FiveElement element

    ) {

        return elementStrength
                .evidenceOf(
                        element
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "오행 최종 세력 Evidence를 찾을 수 없습니다: "
                                                + element
                                )
                );
    }

    /**
     * 식상/재성/관살은
     * 일간 오행 자체와 다른 오행이므로
     * 득세 분석에 존재하는 모든 해당 source의
     * raw 합계가 3차 raw 오행 세력과
     * 일치해야 한다.
     */
    private void validateRawPowerConsistency(

            FiveElement element,

            ElementPowerEvidence powerEvidence,

            List<DayMasterForceEvidence> sources

    ) {

        int sourceRawPower =
                sources
                        .stream()
                        .mapToInt(
                                DayMasterForceEvidence
                                        ::rawWeight
                        )
                        .sum();

        if (
                sourceRawPower
                        != powerEvidence.rawBasePower()
        ) {

            throw new IllegalStateException(
                    "압력 오행의 source raw 세력이 "
                            + "3차 오행 raw 세력과 일치하지 않습니다. "
                            + "element="
                            + element
                            + ", sourceRaw="
                            + sourceRawPower
                            + ", elementRaw="
                            + powerEvidence.rawBasePower()
            );
        }
    }

    private int countVisibleSources(
            List<DayMasterForceEvidence> sources
    ) {

        return (int) sources
                .stream()
                .filter(
                        DayMasterForceEvidence
                                ::visibleStemSource
                )
                .count();
    }

    private int countHiddenSources(
            List<DayMasterForceEvidence> sources
    ) {

        return (int) sources
                .stream()
                .filter(
                        DayMasterForceEvidence
                                ::hiddenStemSource
                )
                .count();
    }

    private int countExposedHiddenSources(
            List<DayMasterForceEvidence> sources
    ) {

        return (int) sources
                .stream()
                .filter(
                        DayMasterForceEvidence
                                ::exposedHiddenStem
                )
                .count();
    }
}