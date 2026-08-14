package com.dajeom.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 14차-6.
 *
 * AnnualLuckContextResolver의
 * 정규화 결과.
 *
 * =================================================
 *
 * 두 종류가 존재한다.
 *
 * 1.
 * annualOnly
 *
 * 세운 자체와 원국을 기준으로 만든
 * 공통 Context.
 *
 * 2.
 * withDecade
 *
 * 특정 대운과 세운이 겹치는 구간에서
 * 추가로 발생하는 Context.
 *
 * =================================================
 *
 * 같은 AnnualLuckPeriod를 기준으로 하되
 * decadePeriod 존재 여부로 구분한다.
 */
public record AnnualLuckContextResolution(

        AnnualLuckPeriod annualPeriod,

        Optional<DecadeLuckPeriod> decadePeriod,

        List<AnnualLuckContextSignal> signals

) {

    public AnnualLuckContextResolution {

        Objects.requireNonNull(
                annualPeriod,
                "세운 Context Resolution에는 AnnualLuckPeriod가 필요합니다."
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
                AnnualLuckContextSignal signal
                : signals
        ) {

            Objects.requireNonNull(
                    signal,
                    "세운 Context Signal은 null일 수 없습니다."
            );
        }

        /*
         * =================================================
         * 동일 Axis + Code 중복 방지
         * =================================================
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

                AnnualLuckContextSignal a =
                        signals.get(
                                first
                        );

                AnnualLuckContextSignal b =
                        signals.get(
                                second
                        );

                if (
                        a.axis() == b.axis()

                                && a.code()
                                .equals(
                                        b.code()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "동일한 세운 Context Signal이 중복되었습니다: "
                                    + a.axis()
                                    + " / "
                                    + a.code()
                    );
                }
            }
        }
    }

    public static AnnualLuckContextResolution
    annualOnly(

            AnnualLuckPeriod annualPeriod,

            List<AnnualLuckContextSignal> signals

    ) {

        return new AnnualLuckContextResolution(
                annualPeriod,
                Optional.empty(),
                signals
        );
    }

    public static AnnualLuckContextResolution
    withDecade(

            AnnualLuckPeriod annualPeriod,

            DecadeLuckPeriod decadePeriod,

            List<AnnualLuckContextSignal> signals

    ) {

        Objects.requireNonNull(
                decadePeriod,
                "대운 포함 Context에는 DecadeLuckPeriod가 필요합니다."
        );

        return new AnnualLuckContextResolution(
                annualPeriod,
                Optional.of(
                        decadePeriod
                ),
                signals
        );
    }

    public boolean annualOnly() {

        return decadePeriod
                .isEmpty();
    }

    public boolean hasDecade() {

        return decadePeriod
                .isPresent();
    }
}