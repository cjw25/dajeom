package com.dajeom.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-7.
 *
 * MonthlyLuckContextResolver가 반환하는
 * 정규화된 Context 결과.
 *
 * =================================================
 *
 * monthlyOnly
 *
 * 대운에 관계없이 현재 월운에서
 * 확정 가능한 Context.
 *
 * 포함 가능:
 *
 * - 월운 십성
 * - 원국 × 월운
 * - 세운 × 월운
 * - 용신
 * - 격국
 * - 궁위
 * - 원국 + 세운 + 월운 3층 관계
 *
 * =================================================
 *
 * withDecade
 *
 * 특정 대운이 해당 월운과 겹치는
 * 시간구간에서만 성립하는 Context.
 *
 * 포함 가능:
 *
 * - 대운 × 월운
 * - 원국 + 대운 + 월운
 * - 대운 + 세운 + 월운
 * - 원국 + 대운 + 세운 + 월운
 */
public record MonthlyLuckContextResolution(

        MonthlyLuckPeriod monthlyPeriod,

        Optional<DecadeLuckPeriod> decadePeriod,

        List<MonthlyLuckContextSignal> signals

) {

    public MonthlyLuckContextResolution {

        Objects.requireNonNull(
                monthlyPeriod,
                "월운 Context Resolution에는 MonthlyLuckPeriod가 필요합니다."
        );

        decadePeriod =
                decadePeriod == null
                        ? Optional.empty()
                        : decadePeriod;

        signals =
                signals == null
                        ? List.of()
                        : List.copyOf(
                        signals
                );

        for (
                MonthlyLuckContextSignal signal
                : signals
        ) {

            Objects.requireNonNull(
                    signal,
                    "월운 Context Signal은 null일 수 없습니다."
            );
        }

        /*
         * 동일 Axis + Code 중복 방지.
         */
        for (
                int first = 0;
                first < signals.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < signals.size();
                    second++
            ) {

                MonthlyLuckContextSignal a =
                        signals.get(
                                first
                        );

                MonthlyLuckContextSignal b =
                        signals.get(
                                second
                        );

                if (
                        a.axis()
                                == b.axis()

                                && a.code()
                                .equals(
                                        b.code()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "동일 월운 Context Signal이 중복되었습니다: "
                                    + a.axis()
                                    + " / "
                                    + a.code()
                    );
                }
            }
        }
    }

    public static MonthlyLuckContextResolution
    monthlyOnly(

            MonthlyLuckPeriod monthlyPeriod,

            List<MonthlyLuckContextSignal> signals

    ) {

        return new MonthlyLuckContextResolution(
                monthlyPeriod,
                Optional.empty(),
                signals
        );
    }

    public static MonthlyLuckContextResolution
    withDecade(

            MonthlyLuckPeriod monthlyPeriod,

            DecadeLuckPeriod decadePeriod,

            List<MonthlyLuckContextSignal> signals

    ) {

        Objects.requireNonNull(
                decadePeriod,
                "대운 Segment 월운 Context에는 DecadeLuckPeriod가 필요합니다."
        );

        return new MonthlyLuckContextResolution(
                monthlyPeriod,
                Optional.of(
                        decadePeriod
                ),
                signals
        );
    }

    public boolean monthlyOnly() {

        return decadePeriod
                .isEmpty();
    }

    public boolean hasDecade() {

        return decadePeriod
                .isPresent();
    }
}