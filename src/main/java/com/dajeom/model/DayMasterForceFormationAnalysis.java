package com.dajeom.model;

import com.dajeom.type.DayMasterForceFormationLevel;
import com.dajeom.type.DayMasterForcePosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 4차-4.
 *
 * 일간의 득세 구조 분석 결과.
 *
 * 중요한 점:
 *
 * supportivePositionCount와
 * opposingPositionCount는
 *
 * "몇 개의 다른 위치에서
 * 해당 세력이 나타나는가"
 *
 * 를 의미한다.
 *
 * 같은 위치에 여러 지장간이 있어도
 * 위치 자체는 한 번만 센다.
 *
 * 또한 하나의 지지에는
 * 지원 오행과 반대 오행이 동시에
 * 존재할 수 있으므로:
 *
 * supportivePositionCount
 * +
 * opposingPositionCount
 *
 * 가 전체 위치 수와 같을 필요는 없다.
 *
 * 최종 신강/신약은
 * 이 모델에서 판정하지 않는다.
 */
public record DayMasterForceFormationAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        int knownSurroundingPositionCount,

        int supportivePositionCount,

        int opposingPositionCount,

        int strongSupportPositionCount,

        int supportiveVisibleStemCount,

        int opposingVisibleStemCount,

        int supportiveHiddenSourceCount,

        int opposingHiddenSourceCount,

        int supportiveExposedHiddenSourceCount,

        DayMasterForceFormationLevel level,

        List<DayMasterForceEvidence> evidences

) {

    public DayMasterForceFormationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "득세 구조 수준이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                knownSurroundingPositionCount != 5
                        && knownSurroundingPositionCount != 7
        ) {

            throw new IllegalArgumentException(
                    "일간 주변의 확정 위치 수는 5 또는 7이어야 합니다."
            );
        }

        if (
                supportivePositionCount < 0
                        || supportivePositionCount
                        > knownSurroundingPositionCount
        ) {

            throw new IllegalArgumentException(
                    "지원 위치 개수가 올바르지 않습니다."
            );
        }

        if (
                opposingPositionCount < 0
                        || opposingPositionCount
                        > knownSurroundingPositionCount
        ) {

            throw new IllegalArgumentException(
                    "반대 위치 개수가 올바르지 않습니다."
            );
        }

        if (
                strongSupportPositionCount < 0
                        || strongSupportPositionCount
                        > supportivePositionCount
        ) {

            throw new IllegalArgumentException(
                    "강한 지원 위치 개수가 올바르지 않습니다."
            );
        }

        if (
                supportiveVisibleStemCount < 0
                        || opposingVisibleStemCount < 0
                        || supportiveHiddenSourceCount < 0
                        || opposingHiddenSourceCount < 0
                        || supportiveExposedHiddenSourceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "득세 source 개수는 음수가 될 수 없습니다."
            );
        }

        if (
                supportiveExposedHiddenSourceCount
                        > supportiveHiddenSourceCount
        ) {

            throw new IllegalArgumentException(
                    "투출한 지원 지장간 수가 전체 지원 지장간 수보다 클 수 없습니다."
            );
        }

        int calculatedSupportPositions =
                countPositions(
                        evidences,
                        true,
                        false
                );

        int calculatedOpposingPositions =
                countPositions(
                        evidences,
                        false,
                        false
                );

        int calculatedStrongSupportPositions =
                countPositions(
                        evidences,
                        true,
                        true
                );

        if (
                calculatedSupportPositions
                        != supportivePositionCount
        ) {

            throw new IllegalArgumentException(
                    "지원 위치 수와 Evidence가 일치하지 않습니다."
            );
        }

        if (
                calculatedOpposingPositions
                        != opposingPositionCount
        ) {

            throw new IllegalArgumentException(
                    "반대 위치 수와 Evidence가 일치하지 않습니다."
            );
        }

        if (
                calculatedStrongSupportPositions
                        != strongSupportPositionCount
        ) {

            throw new IllegalArgumentException(
                    "강한 지원 위치 수와 Evidence가 일치하지 않습니다."
            );
        }
    }

    private static int countPositions(

            List<DayMasterForceEvidence> evidences,

            boolean supportive,

            boolean strongOnly

    ) {

        EnumSet<DayMasterForcePosition> positions =
                EnumSet.noneOf(
                        DayMasterForcePosition.class
                );

        for (
                DayMasterForceEvidence evidence
                : evidences
        ) {

            if (
                    supportive
                            != evidence.supportive()
            ) {

                continue;
            }

            if (
                    strongOnly
                            && !evidence
                            .structurallyStrongSource()
            ) {

                continue;
            }

            positions.add(
                    evidence.position()
            );
        }

        return positions.size();
    }

    public boolean hasSupport() {

        return supportivePositionCount > 0;
    }

    public boolean hasOpposition() {

        return opposingPositionCount > 0;
    }

    /**
     * 구조적인 득세 기반이
     * 아예 존재하는지.
     */
    public boolean gainsSomeForce() {

        return level.hasSupportFormation();
    }

    /**
     * 단순한 한 곳의 지원을 넘어
     * 실제 지원망이 형성되었는가.
     */
    public boolean gainsEstablishedForce() {

        return level.established()
                || level.widespread();
    }

    public boolean gainsWidespreadForce() {

        return level.widespread();
    }

    public boolean mixedEnvironment() {

        return hasSupport()
                && hasOpposition();
    }

    public boolean onlyOpposingEnvironment() {

        return !hasSupport()
                && hasOpposition();
    }

    public boolean hasVisibleSupport() {

        return supportiveVisibleStemCount > 0;
    }

    public boolean hasHiddenSupport() {

        return supportiveHiddenSourceCount > 0;
    }

    public boolean hasExposedHiddenSupport() {

        return supportiveExposedHiddenSourceCount > 0;
    }

    public boolean supportiveAt(
            DayMasterForcePosition position
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
                                evidence.position()
                                        == position
                                        && evidence.supportive()
                );
    }

    public boolean opposingAt(
            DayMasterForcePosition position
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
                                evidence.position()
                                        == position
                                        && evidence.opposing()
                );
    }

    public List<DayMasterForceEvidence>
    evidencesAt(
            DayMasterForcePosition position
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
                                evidence.position()
                                        == position
                )
                .toList();
    }

    public List<DayMasterForceEvidence>
    supportiveEvidences() {

        return evidences
                .stream()
                .filter(
                        DayMasterForceEvidence
                                ::supportive
                )
                .toList();
    }

    public List<DayMasterForceEvidence>
    opposingEvidences() {

        return evidences
                .stream()
                .filter(
                        DayMasterForceEvidence
                                ::opposing
                )
                .toList();
    }

    public boolean hourUnknown() {

        return knownSurroundingPositionCount == 5;
    }

    public boolean completeChart() {

        return knownSurroundingPositionCount == 7;
    }
}