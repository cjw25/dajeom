package com.dajeom.model;

import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodSourceType;
import com.dajeom.type.VisibleStemPosition;

import java.util.Objects;

/**
 * 정밀화 10차-1.
 *
 * 하나의 천간 또는 지장간이
 * 일간 기준으로 어떤 십성이며,
 * 얼마만큼의 원국 세력을 배분받는지 나타낸다.
 *
 * =================================================
 *
 * VISIBLE_STEM
 *
 * visibleStemPosition != null
 * branchPosition == null
 * hiddenStemRole == null
 *
 * =================================================
 *
 * HIDDEN_STEM
 *
 * visibleStemPosition == null
 * branchPosition != null
 * hiddenStemRole != null
 *
 * =================================================
 *
 * rawWeight
 *
 * 천간:
 * 기본 100
 *
 * 지장간:
 * 기존 HiddenStemTable의 비중
 *
 * 예:
 *
 * 申
 * 庚 60
 * 壬 25
 * 戊 15
 *
 * =================================================
 *
 * allocatedElementPower
 *
 * 3차 ElementStrengthAnalysis에서 확정된
 * 해당 오행의 final power를
 * 동일 오행 source들의 rawWeight 비율에 따라
 * 배분한 값.
 *
 * 새로운 오행 세력을 계산하는 것이 아니다.
 */
public record TenGodDistributionEvidence(

        StructureTenGodType tenGodType,

        HeavenlyStem sourceStem,

        FiveElement sourceElement,

        TenGodSourceType sourceType,

        VisibleStemPosition visibleStemPosition,

        EarthlyBranchPosition branchPosition,

        HiddenStemRole hiddenStemRole,

        int rawWeight,

        int allocatedElementPower

) {

    public TenGodDistributionEvidence {

        Objects.requireNonNull(
                tenGodType,
                "십성 종류가 필요합니다."
        );

        Objects.requireNonNull(
                sourceStem,
                "십성 source 천간이 필요합니다."
        );

        Objects.requireNonNull(
                sourceElement,
                "십성 source 오행이 필요합니다."
        );

        Objects.requireNonNull(
                sourceType,
                "십성 source 종류가 필요합니다."
        );

        if (
                rawWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "십성 source rawWeight는 0보다 커야 합니다."
            );
        }

        if (
                allocatedElementPower < 0
        ) {

            throw new IllegalArgumentException(
                    "배분된 오행 세력은 음수일 수 없습니다."
            );
        }

        if (
                sourceType.visibleStem()
        ) {

            Objects.requireNonNull(
                    visibleStemPosition,
                    "천간 source에는 천간 위치가 필요합니다."
            );

            if (
                    branchPosition != null
                            || hiddenStemRole != null
            ) {

                throw new IllegalArgumentException(
                        "천간 source에는 지지 위치 또는 지장간 역할이 존재할 수 없습니다."
                );
            }
        }

        if (
                sourceType.hiddenStem()
        ) {

            Objects.requireNonNull(
                    branchPosition,
                    "지장간 source에는 지지 위치가 필요합니다."
            );

            Objects.requireNonNull(
                    hiddenStemRole,
                    "지장간 source에는 지장간 역할이 필요합니다."
            );

            if (
                    visibleStemPosition != null
            ) {

                throw new IllegalArgumentException(
                        "지장간 source에는 천간 위치가 존재할 수 없습니다."
                );
            }
        }
    }

    public boolean visible() {

        return sourceType.visibleStem();
    }

    public boolean hidden() {

        return sourceType.hiddenStem();
    }

    public boolean yearSource() {

        return visibleStemPosition
                == VisibleStemPosition.YEAR

                || branchPosition
                == EarthlyBranchPosition.YEAR;
    }

    public boolean monthSource() {

        return visibleStemPosition
                == VisibleStemPosition.MONTH

                || branchPosition
                == EarthlyBranchPosition.MONTH;
    }

    public boolean daySource() {

        return visibleStemPosition
                == VisibleStemPosition.DAY

                || branchPosition
                == EarthlyBranchPosition.DAY;
    }

    public boolean hourSource() {

        return visibleStemPosition
                == VisibleStemPosition.HOUR

                || branchPosition
                == EarthlyBranchPosition.HOUR;
    }

    public boolean monthBranchHiddenStem() {

        return hidden()
                && branchPosition
                == EarthlyBranchPosition.MONTH;
    }

    public boolean mainHiddenStem() {

        return hidden()
                && hiddenStemRole
                == HiddenStemRole.MAIN;
    }

    public boolean middleHiddenStem() {

        return hidden()
                && hiddenStemRole
                == HiddenStemRole.MIDDLE;
    }

    public boolean residualHiddenStem() {

        return hidden()
                && hiddenStemRole
                == HiddenStemRole.RESIDUAL;
    }
}