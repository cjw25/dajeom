package com.dajeom.model;

import com.dajeom.type.FiveElement;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 원국에서 각 오행이 가지는
 * 가중 세력을 나타낸다.
 *
 * ElementDistribution:
 * 단순 표면 개수
 *
 * ElementStrength:
 * 지장간, 월령 등의 가중치를 반영한
 * 분석용 세력 점수
 */
public record ElementStrength(

        Map<FiveElement, Double> scores

) {

    public ElementStrength {

        Objects.requireNonNull(
                scores,
                "오행 세력 점수는 필수입니다."
        );

        EnumMap<
                FiveElement,
                Double
                > normalized =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            normalized.put(
                    element,
                    0.0
            );
        }

        scores.forEach(
                (element, score) -> {

                    Objects.requireNonNull(
                            element,
                            "오행은 null일 수 없습니다."
                    );

                    Objects.requireNonNull(
                            score,
                            "오행 점수는 null일 수 없습니다."
                    );

                    if (
                            Double.isNaN(score)
                                    || Double.isInfinite(score)
                                    || score < 0.0
                    ) {

                        throw new IllegalArgumentException(
                                "오행 점수는 0 이상의 유한한 값이어야 합니다."
                        );
                    }

                    normalized.put(
                            element,
                            score
                    );
                }
        );

        scores =
                Collections.unmodifiableMap(
                        normalized
                );
    }

    /**
     * 특정 오행의 세력 점수.
     */
    public double scoreOf(
            FiveElement element
    ) {

        Objects.requireNonNull(
                element,
                "오행은 필수입니다."
        );

        return scores.getOrDefault(
                element,
                0.0
        );
    }

    /**
     * 전체 세력 점수.
     */
    public double totalScore() {

        return scores
                .values()
                .stream()
                .mapToDouble(
                        Double::doubleValue
                )
                .sum();
    }

    /**
     * 전체 점수 중 특정 오행이
     * 차지하는 비율.
     */
    public double percentageOf(
            FiveElement element
    ) {

        double total =
                totalScore();

        if (total <= 0.0) {

            return 0.0;
        }

        return (
                scoreOf(element)
                        / total
        )
                * 100.0;
    }

    /**
     * 가장 강한 오행.
     *
     * 동점이면 enum 선언 순서상
     * 먼저 등장하는 오행을 반환한다.
     */
    public FiveElement strongestElement() {

        FiveElement strongest =
                FiveElement.values()[0];

        double highestScore =
                scoreOf(
                        strongest
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            double score =
                    scoreOf(
                            element
                    );

            if (
                    score > highestScore
            ) {

                strongest =
                        element;

                highestScore =
                        score;
            }
        }

        return strongest;
    }

    /**
     * 가장 약한 오행.
     */
    public FiveElement weakestElement() {

        FiveElement weakest =
                FiveElement.values()[0];

        double lowestScore =
                scoreOf(
                        weakest
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            double score =
                    scoreOf(
                            element
                    );

            if (
                    score < lowestScore
            ) {

                weakest =
                        element;

                lowestScore =
                        score;
            }
        }

        return weakest;
    }
}