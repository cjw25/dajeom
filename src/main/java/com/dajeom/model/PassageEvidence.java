package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.PassageBalanceState;
import com.dajeom.type.PassageBridgeState;
import com.dajeom.type.PassageConflictType;
import com.dajeom.type.PassageNeedLevel;
import com.dajeom.type.RemedyCandidateRole;

import java.util.Objects;

/**
 * 정밀화 6차-6.
 *
 * 하나의 상극 관계에 대한
 * 통관 필요성 Evidence.
 *
 * 예:
 *
 * METAL_CONTROLS_WOOD
 *
 * Metal 3200bp
 * Wood 2800bp
 * Water 500bp
 *
 * ->
 * controller/controlled 모두 강함
 * ->
 * 서로 대립
 * ->
 * bridge Water 부족
 * ->
 * 통관 필요 가능
 *
 * =================================================
 *
 * conflictFloorBasisPoints
 *
 * 양쪽 세력 중 더 약한 쪽.
 *
 * 둘 중 하나가 너무 약하면
 * "팽팽한 대립"이라고 보기 어렵기 때문에
 * 중요한 Evidence다.
 *
 * =================================================
 *
 * sideGapBasisPoints
 *
 * 양쪽 세력 차이의 절대값.
 *
 * =================================================
 *
 * bridgeRemedyRole
 *
 * 6-5 약 분석에서도
 * 동일한 통관 오행이 이미:
 *
 * PRIMARY
 * SECONDARY
 * CONDITIONAL
 * REJECTED
 *
 * 중 어떤 상태인지 보존한다.
 *
 * 이것은 실제 통관이 이미 완료됐다는 뜻이 아니다.
 * 6-7 후보 우선순위를 조정하기 위한 Evidence다.
 */
public record PassageEvidence(

        PassageConflictType conflictType,

        int controllerShareBasisPoints,

        int controlledShareBasisPoints,

        int bridgeShareBasisPoints,

        int conflictFloorBasisPoints,

        int sideGapBasisPoints,

        PassageBalanceState balanceState,

        PassageBridgeState bridgeState,

        PassageNeedLevel needLevel,

        boolean dayMasterInConflict,

        boolean dayMasterOnControllerSide,

        boolean dayMasterOnControlledSide,

        boolean dayMasterDisadvantaged,

        RemedyCandidateRole bridgeRemedyRole

) {

    public PassageEvidence {

        Objects.requireNonNull(
                conflictType,
                "상극 관계 유형이 필요합니다."
        );

        Objects.requireNonNull(
                balanceState,
                "상극 균형 상태가 필요합니다."
        );

        Objects.requireNonNull(
                bridgeState,
                "통관 오행 상태가 필요합니다."
        );

        Objects.requireNonNull(
                needLevel,
                "통관 필요도가 필요합니다."
        );

        Objects.requireNonNull(
                bridgeRemedyRole,
                "통관 오행의 6-5 약 후보 역할이 필요합니다."
        );

        validateShare(
                controllerShareBasisPoints,
                "극하는 오행 비중"
        );

        validateShare(
                controlledShareBasisPoints,
                "극받는 오행 비중"
        );

        validateShare(
                bridgeShareBasisPoints,
                "통관 오행 비중"
        );

        validateShare(
                conflictFloorBasisPoints,
                "대립 최소 세력"
        );

        validateShare(
                sideGapBasisPoints,
                "상극 양쪽 세력 차이"
        );

        int expectedFloor =
                Math.min(
                        controllerShareBasisPoints,
                        controlledShareBasisPoints
                );

        if (
                conflictFloorBasisPoints
                        != expectedFloor
        ) {

            throw new IllegalArgumentException(
                    "대립 최소 세력이 실제 양쪽 오행 비중과 일치하지 않습니다."
            );
        }

        int expectedGap =
                Math.abs(
                        controllerShareBasisPoints
                                - controlledShareBasisPoints
                );

        if (
                sideGapBasisPoints
                        != expectedGap
        ) {

            throw new IllegalArgumentException(
                    "상극 양쪽 세력 차이가 실제 오행 비중과 일치하지 않습니다."
            );
        }

        boolean expectedInConflict =
                dayMasterOnControllerSide
                        || dayMasterOnControlledSide;

        if (
                dayMasterInConflict
                        != expectedInConflict
        ) {

            throw new IllegalArgumentException(
                    "일간의 상극 관계 포함 상태가 일치하지 않습니다."
            );
        }

        if (
                dayMasterOnControllerSide
                        && dayMasterOnControlledSide
        ) {

            throw new IllegalArgumentException(
                    "일간이 상극 관계 양쪽에 동시에 위치할 수 없습니다."
            );
        }

        if (
                dayMasterDisadvantaged
                        && !dayMasterInConflict
        ) {

            throw new IllegalArgumentException(
                    "상극 관계에 포함되지 않은 일간을 불리한 상태로 지정할 수 없습니다."
            );
        }
    }

    private static void validateShare(
            int value,
            String name
    ) {

        if (
                value < 0
                        || value > 10_000
        ) {

            throw new IllegalArgumentException(
                    name
                            + "은 0~10000bp 사이여야 합니다."
            );
        }
    }

    public FiveElement controllerElement() {

        return conflictType
                .controllerElement();
    }

    public FiveElement controlledElement() {

        return conflictType
                .controlledElement();
    }

    public FiveElement bridgeElement() {

        return conflictType
                .bridgeElement();
    }

    public boolean passageRequired() {

        return needLevel
                .required();
    }

    public boolean meaningfulPassageRequired() {

        return needLevel
                .meaningful();
    }

    public boolean highPriority() {

        return needLevel
                .highPriority();
    }

    public boolean stronglyOpposed() {

        return balanceState
                .stronglyOpposed();
    }

    public boolean bridgeInsufficient() {

        return bridgeState
                .insufficient();
    }

    public boolean bridgeAlreadyFunctions() {

        return bridgeState
                .alreadyFunctions();
    }

    public boolean remedyBridgeSelected() {

        return bridgeRemedyRole
                .selected();
    }

    public boolean remedyBridgeConditional() {

        return bridgeRemedyRole
                .conditional();
    }

    public boolean remedyBridgeRejected() {

        return bridgeRemedyRole
                .rejected();
    }

    /**
     * 병약 분석과 통관 방향이
     * 동일한 오행을 지목하고 있는가.
     *
     * 실제 원국에 충분하다는 뜻은 아니다.
     */
    public boolean remedyDirectionAligned() {

        return bridgeRemedyRole
                .primary()

                || bridgeRemedyRole
                .secondary();
    }

    public double controllerSharePercent() {

        return controllerShareBasisPoints
                / 100.0;
    }

    public double controlledSharePercent() {

        return controlledShareBasisPoints
                / 100.0;
    }

    public double bridgeSharePercent() {

        return bridgeShareBasisPoints
                / 100.0;
    }
}