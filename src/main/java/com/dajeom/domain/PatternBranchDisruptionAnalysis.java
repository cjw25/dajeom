package com.dajeom.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 원국 네 지지의 沖 / 刑 / 破 / 害 분석.
 *
 * =================================================
 *
 * 모든 기둥 관계를 계산하지만
 * 격국에서는 특히 月支가 핵심이므로
 * 월령 전용 helper를 제공한다.
 *
 * =================================================
 *
 * 여기서 판정:
 *
 * - 관계 존재
 *
 * =================================================
 *
 * 여기서 판정하지 않음:
 *
 * - 충이 실제 格을 깨는 정도
 * - 三刑 완성 여부에 따른 강약
 * - 합으로 충이 해소되는가
 * - 合沖 병존의 최종 작용
 * - 운에서 다시 引動되는가
 *
 * 숫자 / 점수 / 확률 없음.
 */
public record PatternBranchDisruptionAnalysis(

        Map<PillarPosition, EarthlyBranch> branches,

        List<PatternBranchDisruptionOccurrence> occurrences

) {

    public PatternBranchDisruptionAnalysis {

        branches =
                branches == null
                        ? Map.of()
                        : Map.copyOf(
                        branches
                );

        occurrences =
                occurrences == null
                        ? List.of()
                        : List.copyOf(
                        occurrences
                );

        for (
                PillarPosition position
                : PillarPosition.values()
        ) {

            if (!branches.containsKey(position)) {

                throw new IllegalArgumentException(
                        "지지 분석에는 YEAR/MONTH/DAY/HOUR 네 위치가 모두 필요합니다."
                );
            }

            Objects.requireNonNull(
                    branches.get(position),
                    "지지는 null일 수 없습니다."
            );
        }

        for (
                PatternBranchDisruptionOccurrence occurrence
                : occurrences
        ) {

            Objects.requireNonNull(
                    occurrence,
                    "지지 관계 목록에 null이 포함될 수 없습니다."
            );

            if (
                    branches.get(
                            occurrence.leftPosition()
                    )
                            != occurrence.leftBranch()
            ) {

                throw new IllegalArgumentException(
                        "지지 관계의 왼쪽 지지가 원국 위치 정보와 일치하지 않습니다."
                );
            }

            if (
                    branches.get(
                            occurrence.rightPosition()
                    )
                            != occurrence.rightBranch()
            ) {

                throw new IllegalArgumentException(
                        "지지 관계의 오른쪽 지지가 원국 위치 정보와 일치하지 않습니다."
                );
            }
        }
    }

    public EarthlyBranch monthBranch() {

        return branches.get(
                PillarPosition.MONTH
        );
    }

    public List<PatternBranchDisruptionOccurrence>
    monthCommandDisruptions() {

        return occurrences
                .stream()
                .filter(
                        PatternBranchDisruptionOccurrence
                                ::involvesMonthCommand
                )
                .toList();
    }

    public List<PatternBranchDisruptionOccurrence>
    monthCommandDisruptionsOf(
            PatternBranchDisruptionType type
    ) {

        Objects.requireNonNull(
                type,
                "관계 타입은 필수입니다."
        );

        return monthCommandDisruptions()
                .stream()
                .filter(
                        occurrence ->
                                occurrence.type()
                                        == type
                )
                .toList();
    }

    public boolean monthCommandClashed() {

        return hasMonthCommandDisruption(
                PatternBranchDisruptionType.CLASH
        );
    }

    public boolean monthCommandPunished() {

        return hasMonthCommandDisruption(
                PatternBranchDisruptionType.PUNISHMENT
        );
    }

    public boolean monthCommandBroken() {

        return hasMonthCommandDisruption(
                PatternBranchDisruptionType.BREAK
        );
    }

    public boolean monthCommandHarmed() {

        return hasMonthCommandDisruption(
                PatternBranchDisruptionType.HARM
        );
    }

    public boolean monthCommandDisrupted() {

        return monthCommandClashed()
                ||
                monthCommandPunished()
                ||
                monthCommandBroken()
                ||
                monthCommandHarmed();
    }

    private boolean hasMonthCommandDisruption(
            PatternBranchDisruptionType type
    ) {

        return occurrences
                .stream()
                .anyMatch(
                        occurrence ->
                                occurrence.involvesMonthCommand()

                                        &&

                                        occurrence.type()
                                                == type
                );
    }
}