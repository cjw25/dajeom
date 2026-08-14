package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchHarmType;
import com.dajeom.type.EarthlyBranchPosition;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 8차-9.
 *
 * 원국 전체 지지 해 분석.
 *
 * =================================================
 *
 * 보존:
 *
 * - 육해 유형
 * - 참여 지지
 * - 참여 궁위
 * - 위치 거리
 * - 인접 여부
 * - 일지 포함
 * - 월지 포함
 * - 동일 궁위의 복수 해 관계
 *
 * =================================================
 *
 * 다른 관계와 상호 배타적이지 않다.
 *
 * 동일 Pair 또는 동일 궁위에:
 *
 * 六合
 * 三合
 * 方合
 * 沖
 * 刑
 * 破
 * 害
 *
 * 가 함께 존재할 수 있다.
 */
public record EarthlyBranchHarmAnalysis(

        EarthlyBranch dayBranch,

        EarthlyBranch monthBranch,

        List<EarthlyBranchHarmEvidence> evidences,

        int knownBranchCount,

        boolean completeChart

) {

    public EarthlyBranchHarmAnalysis {

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
         * 동일한 두 궁위 사이에
         * 같은 해 Evidence가 중복 생성되는 것을 방지한다.
         */
        Set<String> positionPairs =
                new HashSet<>();

        for (
                EarthlyBranchHarmEvidence evidence
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
                        "동일한 지지 위치 Pair의 해 Evidence가 중복되었습니다: "
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

    public boolean hasHarm() {

        return !evidences
                .isEmpty();
    }

    public int harmCount() {

        return evidences
                .size();
    }

    /*
     * =================================================
     * 유형
     * =================================================
     */

    public List<EarthlyBranchHarmEvidence>
    harmsOf(
            EarthlyBranchHarmType harmType
    ) {

        if (
                harmType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.harmType()
                                        == harmType
                )
                .toList();
    }

    public boolean hasHarmType(
            EarthlyBranchHarmType harmType
    ) {

        return !harmsOf(
                harmType
        )
                .isEmpty();
    }

    /*
     * =================================================
     * 특정 지지
     * =================================================
     */

    public List<EarthlyBranchHarmEvidence>
    harmsInvolving(
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

    public List<EarthlyBranchHarmEvidence>
    harmsInvolving(
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

    public List<EarthlyBranchHarmEvidence>
    adjacentHarms() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchHarmEvidence::adjacent
                )
                .toList();
    }

    public boolean hasAdjacentHarm() {

        return !adjacentHarms()
                .isEmpty();
    }

    /*
     * =================================================
     * 일지
     * =================================================
     */

    public List<EarthlyBranchHarmEvidence>
    dayBranchHarms() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchHarmEvidence
                                ::dayBranchInvolved
                )
                .toList();
    }

    public boolean dayBranchHarmed() {

        return !dayBranchHarms()
                .isEmpty();
    }

    public int dayBranchHarmCount() {

        return dayBranchHarms()
                .size();
    }

    public boolean dayBranchHasMultipleHarms() {

        return dayBranchHarmCount() >= 2;
    }

    public List<EarthlyBranchHarmEvidence>
    adjacentDayBranchHarms() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchHarmEvidence
                                ::adjacentToDayBranch
                )
                .toList();
    }

    /*
     * =================================================
     * 월지
     * =================================================
     */

    public List<EarthlyBranchHarmEvidence>
    monthBranchHarms() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchHarmEvidence
                                ::monthBranchInvolved
                )
                .toList();
    }

    public boolean monthBranchHarmed() {

        return !monthBranchHarms()
                .isEmpty();
    }

    public int monthBranchHarmCount() {

        return monthBranchHarms()
                .size();
    }

    public boolean monthBranchHasMultipleHarms() {

        return monthBranchHarmCount() >= 2;
    }

    public List<EarthlyBranchHarmEvidence>
    adjacentMonthBranchHarms() {

        return evidences
                .stream()
                .filter(
                        EarthlyBranchHarmEvidence
                                ::adjacentToMonthBranch
                )
                .toList();
    }

    /*
     * =================================================
     * 궁위별 참여 횟수
     * =================================================
     */

    public int harmCountOfPosition(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return 0;
        }

        return harmsInvolving(
                position
        )
                .size();
    }

    public boolean positionHasMultipleHarms(
            EarthlyBranchPosition position
    ) {

        return harmCountOfPosition(
                position
        ) >= 2;
    }

    /**
     * 원국의 어느 궁위든
     * 둘 이상의 해 관계에 참여하는가.
     */
    public boolean hasMultipleHarmParticipation() {

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
                    positionHasMultipleHarms(
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

    public Optional<EarthlyBranchHarmEvidence>
    harmBetween(

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
     * 특정 궁위의 해 상대
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

        return harmsInvolving(
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