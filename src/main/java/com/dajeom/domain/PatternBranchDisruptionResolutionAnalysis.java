package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국 전체의
 * 지지 교란 + 회합 해소 후보 분석.
 *
 * =================================================
 *
 * 특히 격국에서는
 * 月令 沖刑破害가 중요하므로
 * 월령 전용 helper를 제공한다.
 *
 * =================================================
 *
 * 여기서는 최종 해소 확정까지 하지 않는다.
 */
public record PatternBranchDisruptionResolutionAnalysis(

        List<PatternBranchDisruptionResolutionOccurrence> resolutions

) {

    public PatternBranchDisruptionResolutionAnalysis {

        resolutions =
                resolutions == null
                        ? List.of()
                        : List.copyOf(resolutions);

        for (
                PatternBranchDisruptionResolutionOccurrence resolution
                : resolutions
        ) {

            Objects.requireNonNull(
                    resolution,
                    "지지 교란 해소 분석에 null이 포함될 수 없습니다."
            );
        }
    }

    public List<PatternBranchDisruptionResolutionOccurrence>
    monthCommandResolutions() {

        return resolutions
                .stream()
                .filter(
                        PatternBranchDisruptionResolutionOccurrence
                                ::involvesMonthCommand
                )
                .toList();
    }

    public List<PatternBranchDisruptionResolutionOccurrence>
    monthCommandResolutionsOf(
            PatternBranchDisruptionType type
    ) {

        Objects.requireNonNull(
                type,
                "교란 타입은 필수입니다."
        );

        return monthCommandResolutions()
                .stream()
                .filter(
                        resolution ->
                                resolution
                                        .disruption()
                                        .type()
                                        == type
                )
                .toList();
    }

    public boolean monthCommandHasAnyResolutionCandidate() {

        return monthCommandResolutions()
                .stream()
                .anyMatch(
                        PatternBranchDisruptionResolutionOccurrence
                                ::hasResolutionCandidate
                );
    }

    public boolean monthCommandHasUncontestedResolutionCandidate() {

        return monthCommandResolutions()
                .stream()
                .anyMatch(
                        PatternBranchDisruptionResolutionOccurrence
                                ::uncontestedCandidate
                );
    }

    public boolean monthCommandHasContestedResolutionCandidate() {

        return monthCommandResolutions()
                .stream()
                .anyMatch(
                        PatternBranchDisruptionResolutionOccurrence
                                ::contested
                );
    }

    /**
     * 월령에 걸린 모든 교란이
     * 각각 경쟁 없는 회합 해소 후보를
     * 하나 이상 갖고 있는가.
     *
     * =================================================
     *
     * 중요:
     *
     * true여도
     *
     * "실제 최종 해소 확정"
     *
     * 은 아니다.
     *
     * 다음 Rule이 사용할 수 있는
     * 강한 resolution candidate라는 뜻이다.
     *
     * =================================================
     *
     * 예:
     *
     * 子月
     * 卯 A
     * 卯 B
     * 戌
     *
     * 에서 戌이 卯 A 하나만 합한다면
     * 卯 B의 子卯刑이 남으므로 false가 된다.
     */
    public boolean
    allMonthCommandDisruptionsHaveUncontestedResolutionCandidate() {

        List<PatternBranchDisruptionResolutionOccurrence> month =
                monthCommandResolutions();

        if (month.isEmpty()) {

            return false;
        }

        return month
                .stream()
                .allMatch(
                        PatternBranchDisruptionResolutionOccurrence
                                ::uncontestedCandidate
                );
    }

    public boolean
    allMonthCommandClashesHaveUncontestedResolutionCandidate() {

        return allOfTypeHaveUncontestedCandidate(
                PatternBranchDisruptionType.CLASH
        );
    }

    public boolean
    allMonthCommandPunishmentsHaveUncontestedResolutionCandidate() {

        return allOfTypeHaveUncontestedCandidate(
                PatternBranchDisruptionType.PUNISHMENT
        );
    }

    private boolean allOfTypeHaveUncontestedCandidate(
            PatternBranchDisruptionType type
    ) {

        List<PatternBranchDisruptionResolutionOccurrence> selected =
                monthCommandResolutionsOf(type);

        if (selected.isEmpty()) {

            return false;
        }

        return selected
                .stream()
                .allMatch(
                        PatternBranchDisruptionResolutionOccurrence
                                ::uncontestedCandidate
                );
    }
}