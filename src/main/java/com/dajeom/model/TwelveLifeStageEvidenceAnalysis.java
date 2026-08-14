package com.dajeom.model;

import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TwelveLifeStage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 11차-3.
 *
 * 년·월·일·시 지지에 적용한
 * 12운성 Evidence 묶음.
 *
 * =================================================
 *
 * completeChart = true
 *
 * YEAR
 * MONTH
 * DAY
 * HOUR
 *
 * 총 4개.
 *
 * =================================================
 *
 * completeChart = false
 *
 * YEAR
 * MONTH
 * DAY
 *
 * 총 3개.
 *
 * =================================================
 *
 * 시간 미상이라고 해서:
 *
 * HOUR = null Evidence
 *
 * 또는
 *
 * 임의의 子/午 시주
 *
 * 를 생성하지 않는다.
 */
public record TwelveLifeStageEvidenceAnalysis(

        HeavenlyStem dayMaster,

        List<TwelveLifeStageEvidence> evidences,

        int knownPillarCount,

        boolean completeChart

) {

    public TwelveLifeStageEvidenceAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "12운성 Evidence 분석에는 일간이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                knownPillarCount != 3
                        && knownPillarCount != 4
        ) {

            throw new IllegalArgumentException(
                    "12운성 knownPillarCount는 3 또는 4여야 합니다."
            );
        }

        if (
                evidences.size()
                        != knownPillarCount
        ) {

            throw new IllegalArgumentException(
                    "12운성 Evidence 개수와 knownPillarCount가 일치하지 않습니다."
            );
        }

        if (
                completeChart
                        != (
                        knownPillarCount == 4
                )
        ) {

            throw new IllegalArgumentException(
                    "12운성 completeChart 상태와 knownPillarCount가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 일간 동일성 검증
         * =================================================
         */

        for (
                TwelveLifeStageEvidence evidence
                : evidences
        ) {

            if (
                    evidence.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "12운성 Evidence의 일간이 서로 일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * 위치 중복 방지
         * =================================================
         */

        for (
                int first = 0;
                first < evidences.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < evidences.size();
                    second++
            ) {

                if (
                        evidences
                                .get(first)
                                .position()

                                == evidences
                                .get(second)
                                .position()
                ) {

                    throw new IllegalArgumentException(
                            "동일 지지 위치의 12운성 Evidence가 중복되었습니다: "
                                    + evidences
                                    .get(first)
                                    .position()
                    );
                }
            }
        }

        /*
         * =================================================
         * 필수 YEAR / MONTH / DAY 검증
         * =================================================
         */

        if (
                !containsPosition(
                        evidences,
                        EarthlyBranchPosition.YEAR
                )

                        || !containsPosition(
                        evidences,
                        EarthlyBranchPosition.MONTH
                )

                        || !containsPosition(
                        evidences,
                        EarthlyBranchPosition.DAY
                )
        ) {

            throw new IllegalArgumentException(
                    "12운성 Evidence에는 YEAR, MONTH, DAY가 반드시 존재해야 합니다."
            );
        }

        /*
         * =================================================
         * HOUR 검증
         * =================================================
         */

        boolean hasHour =
                containsPosition(
                        evidences,
                        EarthlyBranchPosition.HOUR
                );

        if (
                completeChart != hasHour
        ) {

            throw new IllegalArgumentException(
                    "12운성 completeChart 상태와 HOUR Evidence 존재 여부가 일치하지 않습니다."
            );
        }
    }

    public Optional<TwelveLifeStageEvidence> evidenceOf(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.position()
                                        == position
                )
                .findFirst();
    }

    public Optional<TwelveLifeStageEvidence> yearEvidence() {

        return evidenceOf(
                EarthlyBranchPosition.YEAR
        );
    }

    public Optional<TwelveLifeStageEvidence> monthEvidence() {

        return evidenceOf(
                EarthlyBranchPosition.MONTH
        );
    }

    public Optional<TwelveLifeStageEvidence> dayEvidence() {

        return evidenceOf(
                EarthlyBranchPosition.DAY
        );
    }

    public Optional<TwelveLifeStageEvidence> hourEvidence() {

        return evidenceOf(
                EarthlyBranchPosition.HOUR
        );
    }

    public TwelveLifeStage yearStage() {

        return yearEvidence()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "YEAR 12운성 Evidence가 없습니다."
                                )
                )
                .stage();
    }

    public TwelveLifeStage monthStage() {

        return monthEvidence()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "MONTH 12운성 Evidence가 없습니다."
                                )
                )
                .stage();
    }

    public TwelveLifeStage dayStage() {

        return dayEvidence()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "DAY 12운성 Evidence가 없습니다."
                                )
                )
                .stage();
    }

    public Optional<TwelveLifeStage> hourStage() {

        return hourEvidence()
                .map(
                        TwelveLifeStageEvidence
                                ::stage
                );
    }

    public List<TwelveLifeStageEvidence> evidencesOf(
            TwelveLifeStage stage
    ) {

        if (
                stage == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.stage()
                                        == stage
                )
                .toList();
    }

    public boolean contains(
            TwelveLifeStage stage
    ) {

        return !evidencesOf(
                stage
        )
                .isEmpty();
    }

    public long count(
            TwelveLifeStage stage
    ) {

        return evidencesOf(
                stage
        )
                .size();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }

    private static boolean containsPosition(

            List<TwelveLifeStageEvidence> evidences,

            EarthlyBranchPosition position

    ) {

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.position()
                                        == position
                );
    }
}