package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.RootSourcePosition;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 4차-3.
 *
 * 일간의 득지/지지 기반 분석 결과.
 *
 * 이 단계에서 확인하는 것:
 *
 * - 일간이 실제 통근했는가
 * - 직접근인가
 * - 보조근인가
 * - 년지/월지/일지/시지 중 어디인가
 * - 정기/중기/여기 중 어디인가
 * - 몇 개 지지에 기반을 확보했는가
 *
 * 아직 이것만으로 신강/신약을
 * 최종 판정하지 않는다.
 */
public record DayMasterGroundAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        int knownBranchCount,

        int groundedBranchCount,

        int directGroundedBranchCount,

        int directRootWeight,

        int supportingRootWeight,

        int totalRootWeight,

        List<DayMasterGroundEvidence> evidences

) {

    public DayMasterGroundAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                knownBranchCount < 3
                        || knownBranchCount > 4
        ) {

            throw new IllegalArgumentException(
                    "확정된 지지 개수는 3~4개여야 합니다."
            );
        }

        if (
                groundedBranchCount < 0
                        || groundedBranchCount
                        > knownBranchCount
        ) {

            throw new IllegalArgumentException(
                    "통근한 지지 개수가 올바르지 않습니다."
            );
        }

        if (
                directGroundedBranchCount < 0
                        || directGroundedBranchCount
                        > groundedBranchCount
        ) {

            throw new IllegalArgumentException(
                    "직접 통근한 지지 개수가 올바르지 않습니다."
            );
        }

        if (
                directRootWeight < 0
                        || supportingRootWeight < 0
                        || totalRootWeight < 0
        ) {

            throw new IllegalArgumentException(
                    "통근 세력은 음수가 될 수 없습니다."
            );
        }

        if (
                directRootWeight
                        + supportingRootWeight
                        != totalRootWeight
        ) {

            throw new IllegalArgumentException(
                    "직접근과 보조근의 합계가 전체 통근 세력과 일치하지 않습니다."
            );
        }

        int evidenceTotal =
                evidences
                        .stream()
                        .mapToInt(
                                DayMasterGroundEvidence
                                        ::effectiveRootWeight
                        )
                        .sum();

        if (
                evidenceTotal
                        != totalRootWeight
        ) {

            throw new IllegalArgumentException(
                    "통근 Evidence 합계가 전체 통근 세력과 일치하지 않습니다."
            );
        }

        int calculatedGroundedBranchCount =
                countGroundedPositions(
                        evidences,
                        false
                );

        if (
                calculatedGroundedBranchCount
                        != groundedBranchCount
        ) {

            throw new IllegalArgumentException(
                    "통근 지지 개수와 Evidence가 일치하지 않습니다."
            );
        }

        int calculatedDirectGroundedBranchCount =
                countGroundedPositions(
                        evidences,
                        true
                );

        if (
                calculatedDirectGroundedBranchCount
                        != directGroundedBranchCount
        ) {

            throw new IllegalArgumentException(
                    "직접 통근 지지 개수와 Evidence가 일치하지 않습니다."
            );
        }
    }

    private static int countGroundedPositions(

            List<DayMasterGroundEvidence> evidences,

            boolean directOnly

    ) {

        EnumSet<RootSourcePosition> positions =
                EnumSet.noneOf(
                        RootSourcePosition.class
                );

        for (
                DayMasterGroundEvidence evidence
                : evidences
        ) {

            if (
                    directOnly
                            && !evidence.directRoot()
            ) {

                continue;
            }

            positions.add(
                    evidence.sourcePosition()
            );
        }

        return positions.size();
    }

    /**
     * 어떤 형태든 지지에
     * 뿌리를 가지고 있는가.
     */
    public boolean grounded() {

        return groundedBranchCount > 0;
    }

    /**
     * 일간과 같은 천간으로
     * 직접 통근한 곳이 있는가.
     */
    public boolean directlyGrounded() {

        return directGroundedBranchCount > 0;
    }

    /**
     * 직접근은 없고
     * 같은 오행의 보조근만 존재하는가.
     */
    public boolean supportingGroundOnly() {

        return grounded()
                && !directlyGrounded();
    }

    /**
     * 지지 어디에도
     * 일간의 뿌리가 없는 상태.
     */
    public boolean rootless() {

        return !grounded();
    }

    public boolean groundedAt(
            RootSourcePosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.sourcePosition()
                                        == position
                );
    }

    public boolean directlyGroundedAt(
            RootSourcePosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.sourcePosition()
                                        == position
                                        && evidence.directRoot()
                );
    }

    public boolean mainQiGroundedAt(
            RootSourcePosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.sourcePosition()
                                        == position
                                        && evidence.mainQi()
                );
    }

    public boolean mainQiDirectlyGroundedAt(
            RootSourcePosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.sourcePosition()
                                        == position
                                        && evidence.mainQi()
                                        && evidence.directRoot()
                );
    }

    public int rootWeightAt(
            RootSourcePosition position
    ) {

        if (
                position == null
        ) {

            return 0;
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.sourcePosition()
                                        == position
                )
                .mapToInt(
                        DayMasterGroundEvidence
                                ::effectiveRootWeight
                )
                .sum();
    }

    public List<DayMasterGroundEvidence>
    evidencesAt(
            RootSourcePosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.sourcePosition()
                                        == position
                )
                .toList();
    }

    public boolean yearGrounded() {

        return groundedAt(
                RootSourcePosition.YEAR
        );
    }

    public boolean monthGrounded() {

        return groundedAt(
                RootSourcePosition.MONTH
        );
    }

    public boolean dayGrounded() {

        return groundedAt(
                RootSourcePosition.DAY
        );
    }

    public boolean hourGrounded() {

        return groundedAt(
                RootSourcePosition.HOUR
        );
    }

    public boolean monthDirectlyGrounded() {

        return directlyGroundedAt(
                RootSourcePosition.MONTH
        );
    }

    public boolean dayDirectlyGrounded() {

        return directlyGroundedAt(
                RootSourcePosition.DAY
        );
    }

    public boolean monthMainDirectRoot() {

        return mainQiDirectlyGroundedAt(
                RootSourcePosition.MONTH
        );
    }

    public boolean dayMainDirectRoot() {

        return mainQiDirectlyGroundedAt(
                RootSourcePosition.DAY
        );
    }

    /**
     * 월지 또는 일지에
     * 실제 뿌리가 있는가.
     *
     * 년지/시지만 있는 경우와
     * 중심 위치에 근이 있는 경우를
     * 구분하기 위한 값이다.
     */
    public boolean centralGrounded() {

        return monthGrounded()
                || dayGrounded();
    }

    /**
     * 월지 또는 일지 정기에
     * 직접근을 확보한 경우.
     *
     * 이것 자체가 신강 확정은 아니지만
     * 상당히 명확한 지지 기반 Evidence다.
     */
    public boolean strongCentralDirectRoot() {

        return monthMainDirectRoot()
                || dayMainDirectRoot();
    }

    /**
     * 시주 미상 여부.
     */
    public boolean hourUnknown() {

        return knownBranchCount == 3;
    }

    public boolean completeChart() {

        return knownBranchCount == 4;
    }
}