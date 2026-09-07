package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 월령 내부 구조 분석 결과.
 *
 * 숫자 점수나 강약 점수를 계산하지 않는다.
 *
 * 보존하는 사실:
 *
 * - 월지
 * - 월지의 지장간 EARLY / MIDDLE / MAIN
 * - 그 지장간 중 실제 천간에 투출된 것
 * - 어느 천간 위치에 투출됐는지
 *
 * 예:
 *
 * 월지 申
 * 지장간 戊 / 壬 / 庚
 *
 * 庚이 월간에 존재한다면:
 *
 * MAIN 庚
 * → MONTH 천간에 투출
 */
public record MonthCommandAnalysis(

        EarthlyBranch monthBranch,

        List<HiddenStemComponent> hiddenStems,

        List<HiddenStemExposureHit> exposureHits

) {

    public MonthCommandAnalysis {

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStems,
                "월령 지장간 목록은 필수입니다."
        );

        Objects.requireNonNull(
                exposureHits,
                "월령 투출 목록은 필수입니다."
        );

        hiddenStems =
                List.copyOf(
                        hiddenStems
                );

        exposureHits =
                List.copyOf(
                        exposureHits
                );

        for (
                HiddenStemComponent hiddenStem
                : hiddenStems
        ) {

            Objects.requireNonNull(
                    hiddenStem,
                    "월령 지장간 목록에 null이 포함될 수 없습니다."
            );
        }

        for (
                HiddenStemExposureHit hit
                : exposureHits
        ) {

            Objects.requireNonNull(
                    hit,
                    "월령 투출 목록에 null이 포함될 수 없습니다."
            );

            if (
                    hit.sourceBranchPosition()
                            != PillarPosition.MONTH
            ) {

                throw new IllegalArgumentException(
                        "MonthCommandAnalysis에는 월지에서 발생한 투출만 포함할 수 있습니다."
                );
            }

            if (
                    hit.sourceBranch()
                            != monthBranch
            ) {

                throw new IllegalArgumentException(
                        "월령 투출의 지지가 monthBranch와 일치하지 않습니다."
                );
            }
        }
    }

    /**
     * 여기(EARLY).
     */
    public Optional<HeavenlyStem> earlyStem() {

        return stemOf(
                HiddenStemPhase.EARLY
        );
    }

    /**
     * 중기(MIDDLE).
     *
     * 중기가 없는 지지도 있으므로 Optional.
     */
    public Optional<HeavenlyStem> middleStem() {

        return stemOf(
                HiddenStemPhase.MIDDLE
        );
    }

    /**
     * 정기/본기(MAIN).
     */
    public Optional<HeavenlyStem> mainStem() {

        return stemOf(
                HiddenStemPhase.MAIN
        );
    }

    /**
     * 특정 월령 지장간 글자가
     * 천간에 실제로 투출되어 있는지.
     *
     * 어느 위치에 투출되었는지는
     * exposureHitsOf()로 별도 확인한다.
     */
    public boolean isExposed(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "확인할 천간은 필수입니다."
        );

        return exposureHits
                .stream()
                .anyMatch(
                        hit ->
                                hit.hiddenStem()
                                        == stem
                );
    }

    /**
     * 특정 월령 지장간이
     * 어느 천간 위치에 투출되었는지 모두 반환.
     *
     * 예:
     *
     * 庚
     * → YEAR
     * → MONTH
     *
     * 두 곳 모두 있다면 둘 다 보존한다.
     */
    public List<HiddenStemExposureHit> exposureHitsOf(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "확인할 천간은 필수입니다."
        );

        return exposureHits
                .stream()
                .filter(
                        hit ->
                                hit.hiddenStem()
                                        == stem
                )
                .toList();
    }

    /**
     * 월령 MAIN 지장간의 투출 결과.
     *
     * MAIN이 천간에 나타나지 않았다면 빈 목록.
     */
    public List<HiddenStemExposureHit> mainStemExposureHits() {

        Optional<HeavenlyStem> main =
                mainStem();

        if (main.isEmpty()) {

            return List.of();
        }

        return exposureHitsOf(
                main.get()
        );
    }

    /**
     * 월령 MIDDLE 지장간의 투출 결과.
     */
    public List<HiddenStemExposureHit> middleStemExposureHits() {

        Optional<HeavenlyStem> middle =
                middleStem();

        if (middle.isEmpty()) {

            return List.of();
        }

        return exposureHitsOf(
                middle.get()
        );
    }

    /**
     * 월령 EARLY 지장간의 투출 결과.
     */
    public List<HiddenStemExposureHit> earlyStemExposureHits() {

        Optional<HeavenlyStem> early =
                earlyStem();

        if (early.isEmpty()) {

            return List.of();
        }

        return exposureHitsOf(
                early.get()
        );
    }

    public boolean mainStemExposed() {

        return !mainStemExposureHits()
                .isEmpty();
    }

    public boolean middleStemExposed() {

        return !middleStemExposureHits()
                .isEmpty();
    }

    public boolean earlyStemExposed() {

        return !earlyStemExposureHits()
                .isEmpty();
    }

    private Optional<HeavenlyStem> stemOf(
            HiddenStemPhase phase
    ) {

        return hiddenStems
                .stream()
                .filter(
                        component ->
                                component.phase()
                                        == phase
                )
                .map(
                        HiddenStemComponent::stem
                )
                .findFirst();
    }
}