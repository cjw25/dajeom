package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 4차-6.
 *
 * 일간에 작용하는 약화 방향의 세력을:
 *
 * - 식상 설기
 * - 재성 소모
 * - 관살 극제
 *
 * 로 분리한 결과.
 *
 * 이 모델에서는 세 종류를
 * 하나의 의미로 뭉개지 않는다.
 *
 * totalPressurePower는
 * 4-8에서 지원 세력과 비교하기 위한
 * 총 상대 압력 지수다.
 *
 * 단,
 *
 * totalSupportPower < totalPressurePower
 *
 * 만으로 바로 신약을 판정하지 않는다.
 *
 * 득령·득지·득세 및
 * 극단 구조 검토가 함께 필요하다.
 */
public record DayMasterPressurePowerAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        DayMasterPressureEvidence outputEvidence,

        DayMasterPressureEvidence wealthEvidence,

        DayMasterPressureEvidence officerEvidence,

        int outputDrainPower,

        int wealthExertionPower,

        int officerControlPower,

        int totalPressurePower,

        int pressureSourceCount,

        int visiblePressureSourceCount,

        int hiddenPressureSourceCount,

        int exposedHiddenPressureSourceCount

) {

    public DayMasterPressurePowerAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                outputEvidence,
                "식상 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                wealthEvidence,
                "재성 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                officerEvidence,
                "관살 Evidence가 필요합니다."
        );

        if (
                !outputEvidence.output()
        ) {

            throw new IllegalArgumentException(
                    "outputEvidence는 식상이어야 합니다."
            );
        }

        if (
                !wealthEvidence.wealth()
        ) {

            throw new IllegalArgumentException(
                    "wealthEvidence는 재성이어야 합니다."
            );
        }

        if (
                !officerEvidence.officer()
        ) {

            throw new IllegalArgumentException(
                    "officerEvidence는 관살이어야 합니다."
            );
        }

        if (
                outputDrainPower < 0
                        || wealthExertionPower < 0
                        || officerControlPower < 0
                        || totalPressurePower < 0
                        || pressureSourceCount < 0
                        || visiblePressureSourceCount < 0
                        || hiddenPressureSourceCount < 0
                        || exposedHiddenPressureSourceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "압력 분석값은 음수가 될 수 없습니다."
            );
        }

        if (
                outputDrainPower
                        != outputEvidence
                        .effectivePressurePower()
        ) {

            throw new IllegalArgumentException(
                    "식상 설기 세력이 Evidence와 일치하지 않습니다."
            );
        }

        if (
                wealthExertionPower
                        != wealthEvidence
                        .effectivePressurePower()
        ) {

            throw new IllegalArgumentException(
                    "재성 소모 세력이 Evidence와 일치하지 않습니다."
            );
        }

        if (
                officerControlPower
                        != officerEvidence
                        .effectivePressurePower()
        ) {

            throw new IllegalArgumentException(
                    "관살 극제 세력이 Evidence와 일치하지 않습니다."
            );
        }

        if (
                totalPressurePower
                        != outputDrainPower
                        + wealthExertionPower
                        + officerControlPower
        ) {

            throw new IllegalArgumentException(
                    "전체 압력 세력이 식상+재성+관살 합계와 일치하지 않습니다."
            );
        }

        int calculatedVisibleSourceCount =
                outputEvidence.visibleSourceCount()
                        + wealthEvidence.visibleSourceCount()
                        + officerEvidence.visibleSourceCount();

        int calculatedHiddenSourceCount =
                outputEvidence.hiddenSourceCount()
                        + wealthEvidence.hiddenSourceCount()
                        + officerEvidence.hiddenSourceCount();

        int calculatedExposedHiddenSourceCount =
                outputEvidence.exposedHiddenSourceCount()
                        + wealthEvidence.exposedHiddenSourceCount()
                        + officerEvidence.exposedHiddenSourceCount();

        int calculatedSourceCount =
                calculatedVisibleSourceCount
                        + calculatedHiddenSourceCount;

        if (
                visiblePressureSourceCount
                        != calculatedVisibleSourceCount
        ) {

            throw new IllegalArgumentException(
                    "표면 압력 source 수가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                hiddenPressureSourceCount
                        != calculatedHiddenSourceCount
        ) {

            throw new IllegalArgumentException(
                    "지장간 압력 source 수가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                exposedHiddenPressureSourceCount
                        != calculatedExposedHiddenSourceCount
        ) {

            throw new IllegalArgumentException(
                    "투출 압력 source 수가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                pressureSourceCount
                        != calculatedSourceCount
        ) {

            throw new IllegalArgumentException(
                    "전체 압력 source 수가 Evidence와 일치하지 않습니다."
            );
        }
    }

    public FiveElement outputElement() {

        return outputEvidence.element();
    }

    public FiveElement wealthElement() {

        return wealthEvidence.element();
    }

    public FiveElement officerElement() {

        return officerEvidence.element();
    }

    public boolean hasOutputDrain() {

        return outputDrainPower > 0;
    }

    public boolean hasWealthExertion() {

        return wealthExertionPower > 0;
    }

    public boolean hasOfficerControl() {

        return officerControlPower > 0;
    }

    public boolean hasAnyPressure() {

        return totalPressurePower > 0;
    }

    /**
     * 세 종류의 약화 작용이
     * 전부 실제로 존재하는 구조.
     */
    public boolean triplePressure() {

        return hasOutputDrain()
                && hasWealthExertion()
                && hasOfficerControl();
    }

    /**
     * 관살 극제가 전체 압력 중
     * 가장 큰 요소인가.
     */
    public boolean officerDominant() {

        return officerControlPower
                >= outputDrainPower

                && officerControlPower
                >= wealthExertionPower;
    }

    /**
     * 식상 설기가 가장 큰 요소인가.
     */
    public boolean outputDominant() {

        return outputDrainPower
                >= wealthExertionPower

                && outputDrainPower
                >= officerControlPower;
    }

    /**
     * 재성 소모가 가장 큰 요소인가.
     */
    public boolean wealthDominant() {

        return wealthExertionPower
                >= outputDrainPower

                && wealthExertionPower
                >= officerControlPower;
    }

    public List<DayMasterPressureEvidence> evidences() {

        return List.of(
                outputEvidence,
                wealthEvidence,
                officerEvidence
        );
    }
}