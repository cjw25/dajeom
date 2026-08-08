package com.dajeom.model;

import com.dajeom.type.FiveElement;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 사주 원국의 기본 오행 분포.
 *
 * 이 모델은 단순 오행 개수와 비율을 나타낸다.
 *
 * 신강·신약 판단이나 월령 가중치,
 * 지장간 가중치 등은 포함하지 않는다.
 *
 * 해당 평가는 추후 Analyzer에서 수행한다.
 */
public record ElementDistribution(

        Map<FiveElement, Integer> counts

) {

    public ElementDistribution {

        Objects.requireNonNull(
                counts,
                "오행 분포는 필수입니다."
        );

        EnumMap<
                FiveElement,
                Integer
                > normalized =
                new EnumMap<>(
                        FiveElement.class
                );

        /*
         * 모든 오행을 기본 0으로 초기화한다.
         */
        for (
                FiveElement element
                : FiveElement.values()
        ) {

            normalized.put(
                    element,
                    0
            );
        }

        /*
         * 전달받은 값을 복사한다.
         */
        counts.forEach(
                (element, count) -> {

                    Objects.requireNonNull(
                            element,
                            "오행은 null일 수 없습니다."
                    );

                    Objects.requireNonNull(
                            count,
                            "오행 개수는 null일 수 없습니다."
                    );

                    if (count < 0) {

                        throw new IllegalArgumentException(
                                "오행 개수는 음수일 수 없습니다."
                        );
                    }

                    normalized.put(
                            element,
                            count
                    );
                }
        );

        counts =
                Collections.unmodifiableMap(
                        normalized
                );
    }

    /**
     * 특정 오행의 개수.
     */
    public int countOf(
            FiveElement element
    ) {

        Objects.requireNonNull(
                element,
                "오행은 필수입니다."
        );

        return counts.getOrDefault(
                element,
                0
        );
    }

    /**
     * 전체 집계 개수.
     *
     * 출생시간을 알고 있다면
     * 기본적으로 8.
     *
     * 출생시간을 모르면
     * 기본적으로 6.
     */
    public int total() {

        return counts
                .values()
                .stream()
                .mapToInt(
                        Integer::intValue
                )
                .sum();
    }

    /**
     * 특정 오행의 비율.
     *
     * 0 ~ 100.
     */
    public double percentageOf(
            FiveElement element
    ) {

        int total =
                total();

        if (total == 0) {

            return 0.0;
        }

        return (
                countOf(element)
                        * 100.0
        )
                / total;
    }

    /**
     * 기본 개수 기준으로
     * 가장 많은 오행.
     *
     * 동률이면 enum 선언 순서상
     * 먼저 등장한 오행을 반환한다.
     */
    public FiveElement strongestElement() {

        FiveElement strongest =
                FiveElement.values()[0];

        int highest =
                countOf(
                        strongest
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int count =
                    countOf(
                            element
                    );

            if (count > highest) {

                strongest =
                        element;

                highest =
                        count;
            }
        }

        return strongest;
    }

    /**
     * 기본 개수 기준으로
     * 가장 적은 오행.
     *
     * 동률이면 enum 선언 순서상
     * 먼저 등장한 오행을 반환한다.
     */
    public FiveElement weakestElement() {

        FiveElement weakest =
                FiveElement.values()[0];

        int lowest =
                countOf(
                        weakest
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int count =
                    countOf(
                            element
                    );

            if (count < lowest) {

                weakest =
                        element;

                lowest =
                        count;
            }
        }

        return weakest;
    }
}