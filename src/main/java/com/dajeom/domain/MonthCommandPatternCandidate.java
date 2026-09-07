package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 월령의 한 지장간에서 나온 격국 후보.
 *
 * =================================================
 *
 * MAIN:
 *
 * 월령 본래 중심 후보.
 *
 * =================================================
 *
 * EARLY / MIDDLE:
 *
 * 천간에 실제 투출했을 경우
 * 用神變化 / 格局變化 검토 후보.
 *
 * =================================================
 *
 * 이 객체가 있다고 해서
 * 해당 격국이 성립한 것은 아니다.
 */
public record MonthCommandPatternCandidate(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        HeavenlyStem hiddenStem,

        HiddenStemPhase phase,

        DayMasterRelationType relationType,

        MonthCommandPatternCandidateType candidateType,

        List<HiddenStemExposureHit> exposureHits

) {

    public MonthCommandPatternCandidate {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "월령 지장간은 필수입니다."
        );

        Objects.requireNonNull(
                phase,
                "월령 지장간 단계는 필수입니다."
        );

        Objects.requireNonNull(
                relationType,
                "일간과 월령 지장간의 관계는 필수입니다."
        );

        Objects.requireNonNull(
                candidateType,
                "격국 후보 종류는 필수입니다."
        );

        exposureHits =
                exposureHits == null
                        ? List.of()
                        : List.copyOf(
                        exposureHits
                );

        for (
                HiddenStemExposureHit hit
                : exposureHits
        ) {

            Objects.requireNonNull(
                    hit,
                    "투출 목록에 null이 포함될 수 없습니다."
            );

            if (
                    !hit.fromMonthCommand()
            ) {

                throw new IllegalArgumentException(
                        "월령 격국 후보에는 월지에서 발생한 투출만 포함할 수 있습니다."
                );
            }

            if (
                    hit.sourceBranch()
                            != monthBranch
            ) {

                throw new IllegalArgumentException(
                        "투출 정보의 월지가 격국 후보의 월지와 일치하지 않습니다."
                );
            }

            if (
                    hit.hiddenStem()
                            != hiddenStem
            ) {

                throw new IllegalArgumentException(
                        "투출 지장간이 격국 후보 지장간과 일치하지 않습니다."
                );
            }

            if (
                    hit.hiddenStemPhase()
                            != phase
            ) {

                throw new IllegalArgumentException(
                        "투출 지장간 단계가 격국 후보와 일치하지 않습니다."
                );
            }
        }
    }

    public boolean main() {

        return phase
                == HiddenStemPhase.MAIN;
    }

    public boolean exposed() {

        return !exposureHits
                .isEmpty();
    }

    public List<PillarPosition> exposedPositions() {

        return exposureHits
                .stream()
                .map(
                        HiddenStemExposureHit
                                ::exposedStemPosition
                )
                .distinct()
                .toList();
    }

    public boolean standardEightPatternCandidate() {

        return candidateType
                .standardEightPatternCandidate();
    }
}