package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchBreakType;
import com.dajeom.type.EarthlyBranchPosition;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 8차-8.
 *
 * 원국 전체 지지 파 분석.
 *
 * =================================================
 *
 * 보존:
 *
 * - 파 유형
 * - 참여 지지
 * - 참여 궁위
 * - 위치 거리
 * - 인접 여부
 * - 일지 관여
 * - 월지 관여
 * - 동일 위치가 복수 파 관계에 참여하는 구조
 *
 * =================================================
 *
 * 다른 관계와 독립적으로 존재한다.
 *
 * 같은 Pair에:
 *
 * 六合 + 破
 *
 * 같은 중첩 관계가 존재해도
 * 이 Analysis에서는 그대로 보존한다.
 */
public record EarthlyBranchBreakAnalysis(

        EarthlyBranch dayBranch,

        EarthlyBranch monthBranch,

        List<EarthlyBranchBreakEvidence> evidences,

        int knownBranchCount,

        boolean completeChart

) {

    public EarthlyBranchBreakAnalysis {

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
         * 같은 두 원국 위치 사이에는
         * 같은 파 Evidence를 중복 생성하지 않는다.
         */
        Set<String> positionPairs =
                new HashSet<>();

        for (
                EarthlyBranchBreakEvidence evidence
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
                        "동일한 지지 위치 Pair의 파 Evidence가 중복되었습니다: "
                                + key
                );
            }

            /*
             * DAY 실제 지지 검증.
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
             * MONTH 실제 지지 검증.
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

    public boolean hasBreak() {

        return !evidences
                .isEmpty();
    }

    public int breakCount() {

        return evidences
                .size();
    }

    /*
     * =================================================
     * 유형
     * =================================================
     */

    public List<EarthlyBranchBreakEvidence>
    breaksOf(
            EarthlyBranchBreakType breakType
    ) {

        if (
                breakType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.breakType()
                                        == breakType
                )
                .toList();
    }

    public boolean hasBreakType(
            EarthlyBranchBreakType breakType
    ) {

        return !breaksOf(
                breakType
        )
                .isEmpty();
    }

    /*
     * =================================================
     * 특정 지지
     * =================================================
     */

    public List<EarthlyBranchBreakEvidence>
    breaksInvolving(
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

    /*
     * =================================================
     * 특정 궁위
     * =================================================
     */

    public List<EarthlyBranchBreakEvidence>
    breaksInvolving(
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

    /*
     * =================================================
     * 인접
     * =================================================
     */

    public List<EarthlyBranchBreakEvidence>
    adjacentBreaks() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchBreakEvidence::adjacent
                )
                .toList();
    }

    public boolean hasAdjacentBreak() {

        return !adjacentBreaks()
                .isEmpty();
    }

    /*
     * =================================================
     * 일지
     * =================================================
     */

    public List<EarthlyBranchBreakEvidence>
    dayBranchBreaks() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchBreakEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public boolean dayBranchBroken() {

        return !dayBranchBreaks()
                .isEmpty();
    }

    public int dayBranchBreakCount() {

        return dayBranchBreaks()
                .size();
    }

    public boolean dayBranchHasMultipleBreaks() {

        return dayBranchBreakCount() >= 2;
    }

    public List<EarthlyBranchBreakEvidence>
    adjacentDayBranchBreaks() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchBreakEvidence
                                ::adjacentToDayBranch
                )
                .toList();
    }

    /*
     * =================================================
     * 월지
     * =================================================
     */

    public List<EarthlyBranchBreakEvidence>
    monthBranchBreaks() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchBreakEvidence
                                ::monthBranchInvolved
                )
                .toList();
    }

    public boolean monthBranchBroken() {

        return !monthBranchBreaks()
                .isEmpty();
    }

    public int monthBranchBreakCount() {

        return monthBranchBreaks()
                .size();
    }

    public boolean monthBranchHasMultipleBreaks() {

        return monthBranchBreakCount() >= 2;
    }

    public List<EarthlyBranchBreakEvidence>
    adjacentMonthBranchBreaks() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchBreakEvidence
                                ::adjacentToMonthBranch
                )
                .toList();
    }

    /*
     * =================================================
     * 위치별 중첩
     * =================================================
     */

    public int breakCountOfPosition(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return 0;
        }

        return breaksInvolving(
                position
        )
                .size();
    }

    public boolean positionHasMultipleBreaks(
            EarthlyBranchPosition position
    ) {

        return breakCountOfPosition(
                position
        ) >= 2;
    }

    /**
     * 원국의 어느 궁위든
     * 둘 이상의 파 Evidence에 동시에 참여하는가.
     */
    public boolean hasMultipleBreakParticipation() {

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
                    positionHasMultipleBreaks(
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
     * 특정 두 궁위
     * =================================================
     */

    public Optional<EarthlyBranchBreakEvidence>
    breakBetween(

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
                                evidence.between(
                                        position1,
                                        position2
                                )
                )
                .findFirst();
    }

    /*
     * =================================================
     * 특정 궁위의 파 상대 지지
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

        return breaksInvolving(
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