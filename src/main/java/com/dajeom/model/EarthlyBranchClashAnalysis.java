package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchClashType;
import com.dajeom.type.EarthlyBranchPosition;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 8차-6.
 *
 * 원국 전체 지지충 분석.
 *
 * =================================================
 *
 * 보존:
 *
 * - 육충 유형
 * - 두 지지
 * - 두 궁위
 * - 거리
 * - 인접 여부
 * - 일지 포함 여부
 * - 월지 포함 여부
 * - 하나의 위치가 복수 충 관계를 가지는 구조
 *
 * =================================================
 *
 * 예:
 *
 * 年支 午
 * 日支 子
 * 時支 午
 *
 * 라면:
 *
 * 年午 ↔ 日子
 * 日子 ↔ 時午
 *
 * 두 Evidence를 모두 보존한다.
 *
 * 日子를 어느 午와만 충한다고
 * 임의 선택하지 않는다.
 *
 * =================================================
 *
 * 다른 관계:
 *
 * 六合
 * 三合
 * 方合
 *
 * 이 존재하더라도 이 Analysis에서는 삭제하지 않는다.
 */
public record EarthlyBranchClashAnalysis(

        EarthlyBranch dayBranch,

        EarthlyBranch monthBranch,

        List<EarthlyBranchClashEvidence> evidences,

        int knownBranchCount,

        boolean completeChart

) {

    public EarthlyBranchClashAnalysis {

        Objects.requireNonNull(
                dayBranch,
                "일지가 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        int expectedKnownBranchCount =
                completeChart
                        ? 4
                        : 3;

        if (
                knownBranchCount
                        != expectedKnownBranchCount
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성과 알려진 지지 수가 일치하지 않습니다."
            );
        }

        /*
         * 동일 위치 Pair 중복 방지.
         */
        Set<String> positionPairs =
                new HashSet<>();

        for (
                EarthlyBranchClashEvidence evidence
                : evidences
        ) {

            String key =
                    evidence.firstPosition().name()
                            + ":"
                            + evidence.secondPosition().name();

            if (
                    !positionPairs.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 지지 위치 Pair의 충 Evidence가 중복되었습니다: "
                                + key
                );
            }

            /*
             * DAY 위치 실제 지지 검증.
             */
            if (
                    evidence.firstPosition()
                            == EarthlyBranchPosition.DAY

                            && evidence.firstBranch()
                            != dayBranch
            ) {

                throw new IllegalArgumentException(
                        "DAY 위치 지지가 실제 일지와 일치하지 않습니다."
                );
            }

            if (
                    evidence.secondPosition()
                            == EarthlyBranchPosition.DAY

                            && evidence.secondBranch()
                            != dayBranch
            ) {

                throw new IllegalArgumentException(
                        "DAY 위치 지지가 실제 일지와 일치하지 않습니다."
                );
            }

            /*
             * MONTH 위치 실제 지지 검증.
             */
            if (
                    evidence.firstPosition()
                            == EarthlyBranchPosition.MONTH

                            && evidence.firstBranch()
                            != monthBranch
            ) {

                throw new IllegalArgumentException(
                        "MONTH 위치 지지가 실제 월지와 일치하지 않습니다."
                );
            }

            if (
                    evidence.secondPosition()
                            == EarthlyBranchPosition.MONTH

                            && evidence.secondBranch()
                            != monthBranch
            ) {

                throw new IllegalArgumentException(
                        "MONTH 위치 지지가 실제 월지와 일치하지 않습니다."
                );
            }
        }
    }

    public boolean hasClash() {

        return !evidences
                .isEmpty();
    }

    public int clashCount() {

        return evidences
                .size();
    }

    public List<EarthlyBranchClashEvidence>
    clashesOf(
            EarthlyBranchClashType clashType
    ) {

        if (
                clashType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.clashType()
                                        == clashType
                )
                .toList();
    }

    public boolean hasClashType(
            EarthlyBranchClashType clashType
    ) {

        return !clashesOf(
                clashType
        )
                .isEmpty();
    }

    public List<EarthlyBranchClashEvidence>
    clashesInvolving(
            EarthlyBranch branch
    ) {

        if (
                branch == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        branch
                                )
                )
                .toList();
    }

    public List<EarthlyBranchClashEvidence>
    clashesInvolving(
            EarthlyBranchPosition position
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
                                evidence.involves(
                                        position
                                )
                )
                .toList();
    }

    public List<EarthlyBranchClashEvidence>
    adjacentClashes() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchClashEvidence::adjacent
                )
                .toList();
    }

    public boolean hasAdjacentClash() {

        return !adjacentClashes()
                .isEmpty();
    }

    /*
     * =================================================
     * 일지
     * =================================================
     */

    public List<EarthlyBranchClashEvidence>
    dayBranchClashes() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchClashEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public boolean dayBranchClashed() {

        return !dayBranchClashes()
                .isEmpty();
    }

    public int dayBranchClashCount() {

        return dayBranchClashes()
                .size();
    }

    public boolean dayBranchHasMultipleClashes() {

        return dayBranchClashCount() >= 2;
    }

    public List<EarthlyBranchClashEvidence>
    adjacentDayBranchClashes() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchClashEvidence
                                ::adjacentToDayBranch
                )
                .toList();
    }

    /*
     * =================================================
     * 월지
     * =================================================
     */

    public List<EarthlyBranchClashEvidence>
    monthBranchClashes() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchClashEvidence
                                ::monthBranchInvolved
                )
                .toList();
    }

    public boolean monthBranchClashed() {

        return !monthBranchClashes()
                .isEmpty();
    }

    public int monthBranchClashCount() {

        return monthBranchClashes()
                .size();
    }

    public boolean monthBranchHasMultipleClashes() {

        return monthBranchClashCount() >= 2;
    }

    public List<EarthlyBranchClashEvidence>
    adjacentMonthBranchClashes() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchClashEvidence
                                ::adjacentToMonthBranch
                )
                .toList();
    }

    /*
     * =================================================
     * 위치별 중첩
     * =================================================
     */

    public int clashCountOfPosition(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return 0;
        }

        return clashesInvolving(
                position
        )
                .size();
    }

    public boolean positionHasMultipleClashes(
            EarthlyBranchPosition position
    ) {

        return clashCountOfPosition(
                position
        ) >= 2;
    }

    /**
     * 하나 이상의 위치가
     * 복수 충 관계에 동시에 포함되는가.
     */
    public boolean hasMultipleClashParticipation() {

        for (
                EarthlyBranchPosition position
                : EarthlyBranchPosition.values()
        ) {

            if (
                    position == EarthlyBranchPosition.HOUR
                            && !completeChart
            ) {

                continue;
            }

            if (
                    positionHasMultipleClashes(
                            position
                    )
            ) {

                return true;
            }
        }

        return false;
    }

    /*
     * =================================================
     * 두 위치
     * =================================================
     */

    public Optional<EarthlyBranchClashEvidence>
    clashBetween(

            EarthlyBranchPosition position1,

            EarthlyBranchPosition position2

    ) {

        if (
                position1 == null
                        || position2 == null
                        || position1 == position2
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        position1
                                )
                                        && evidence.involves(
                                        position2
                                )
                )
                .findFirst();
    }

    /*
     * =================================================
     * 특정 지지의 충 상대들
     * =================================================
     */

    public List<EarthlyBranch> counterpartsOf(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return clashesInvolving(
                position
        )
                .stream()
                .map(
                        evidence ->
                                evidence.counterpartOf(
                                        position
                                )
                )
                .filter(
                        Objects::nonNull
                )
                .toList();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}