package com.dajeom.model;

import com.dajeom.type.DayMasterForceFormationLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 4차-5.
 *
 * 일간을 돕는 실제 세력 분석.
 *
 * 지원 방향을:
 *
 * - 비겁
 * - 인성
 *
 * 으로 분리한다.
 *
 * 중요한 점:
 *
 * 이 결과만으로 신강을 판정하지 않는다.
 *
 * 다음 단계의:
 *
 * 식상
 * 재성
 * 관살
 *
 * 압력과 비교해야 하며,
 *
 * 4-2 득령
 * 4-3 득지
 * 4-4 득세
 *
 * 구조도 최종 StrengthRule에서
 * 함께 사용한다.
 */
public record DayMasterSupportPowerAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        DayMasterSupportEvidence companionEvidence,

        DayMasterSupportEvidence resourceEvidence,

        int companionSupportPower,

        int resourceSupportPower,

        int totalSupportPower,

        int supportSourceCount,

        int visibleSupportSourceCount,

        int hiddenSupportSourceCount,

        int exposedHiddenSupportSourceCount,

        DayMasterForceFormationLevel forceFormationLevel

) {

    public DayMasterSupportPowerAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                companionEvidence,
                "비겁 지원 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                resourceEvidence,
                "인성 지원 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                forceFormationLevel,
                "득세 구조 수준이 필요합니다."
        );

        if (
                !companionEvidence.companion()
        ) {

            throw new IllegalArgumentException(
                    "companionEvidence는 비겁이어야 합니다."
            );
        }

        if (
                !resourceEvidence.resource()
        ) {

            throw new IllegalArgumentException(
                    "resourceEvidence는 인성이어야 합니다."
            );
        }

        if (
                companionEvidence.element()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "비겁 오행은 일간 오행과 같아야 합니다."
            );
        }

        if (
                companionSupportPower < 0
                        || resourceSupportPower < 0
                        || totalSupportPower < 0
                        || supportSourceCount < 0
                        || visibleSupportSourceCount < 0
                        || hiddenSupportSourceCount < 0
                        || exposedHiddenSupportSourceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "생조 세력 값은 음수가 될 수 없습니다."
            );
        }

        if (
                companionSupportPower
                        != companionEvidence
                        .effectiveSupportPower()
        ) {

            throw new IllegalArgumentException(
                    "비겁 지원력이 Evidence와 일치하지 않습니다."
            );
        }

        if (
                resourceSupportPower
                        != resourceEvidence
                        .effectiveSupportPower()
        ) {

            throw new IllegalArgumentException(
                    "인성 지원력이 Evidence와 일치하지 않습니다."
            );
        }

        if (
                totalSupportPower
                        != companionSupportPower
                        + resourceSupportPower
        ) {

            throw new IllegalArgumentException(
                    "전체 생조 세력이 비겁+인성 합계와 일치하지 않습니다."
            );
        }

        int calculatedVisibleCount =
                companionEvidence.visibleSourceCount()
                        + resourceEvidence.visibleSourceCount();

        int calculatedHiddenCount =
                companionEvidence.hiddenSourceCount()
                        + resourceEvidence.hiddenSourceCount();

        int calculatedExposedHiddenCount =
                companionEvidence.exposedHiddenSourceCount()
                        + resourceEvidence.exposedHiddenSourceCount();

        int calculatedSourceCount =
                calculatedVisibleCount
                        + calculatedHiddenCount;

        if (
                visibleSupportSourceCount
                        != calculatedVisibleCount
        ) {

            throw new IllegalArgumentException(
                    "표면 지원 source 수가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                hiddenSupportSourceCount
                        != calculatedHiddenCount
        ) {

            throw new IllegalArgumentException(
                    "지장간 지원 source 수가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                exposedHiddenSupportSourceCount
                        != calculatedExposedHiddenCount
        ) {

            throw new IllegalArgumentException(
                    "투출 지원 source 수가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                supportSourceCount
                        != calculatedSourceCount
        ) {

            throw new IllegalArgumentException(
                    "전체 지원 source 수가 Evidence와 일치하지 않습니다."
            );
        }
    }

    public FiveElement companionElement() {

        return companionEvidence.element();
    }

    public FiveElement resourceElement() {

        return resourceEvidence.element();
    }

    public boolean hasCompanionSupport() {

        return companionSupportPower > 0;
    }

    public boolean hasResourceSupport() {

        return resourceSupportPower > 0;
    }

    public boolean hasAnySupport() {

        return totalSupportPower > 0;
    }

    /**
     * 인성과 비겁 모두 실제 지원력을
     * 가지고 있는 구조.
     */
    public boolean dualSupport() {

        return hasCompanionSupport()
                && hasResourceSupport();
    }

    /**
     * 비겁 없이 인성만으로
     * 지원받는 구조.
     */
    public boolean resourceOnlySupport() {

        return !hasCompanionSupport()
                && hasResourceSupport();
    }

    /**
     * 인성 없이 비겁만으로
     * 지원받는 구조.
     */
    public boolean companionOnlySupport() {

        return hasCompanionSupport()
                && !hasResourceSupport();
    }

    /**
     * 4-4에서 지원망이 구조적으로
     * 형성됐는지.
     *
     * 수치와 구조를 일부러 분리해 둔다.
     */
    public boolean hasEstablishedSupportFormation() {

        return forceFormationLevel
                == DayMasterForceFormationLevel.ESTABLISHED

                || forceFormationLevel
                == DayMasterForceFormationLevel.WIDESPREAD;
    }

    public List<DayMasterSupportEvidence> evidences() {

        return List.of(
                companionEvidence,
                resourceEvidence
        );
    }
}