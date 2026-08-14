package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 13차-6.
 *
 * 하나의 대운 Period에 대해
 *
 * 5차 격국
 * 7차 용신
 * 10차 십성 복합구조
 * 12차 궁위
 *
 * 를 교차한 Resolver 결과.
 */
public record DecadeLuckContextResolution(

        DecadeLuckPeriod period,

        List<DecadeLuckContextSignal> signals

) {

    public DecadeLuckContextResolution {

        Objects.requireNonNull(
                period,
                "대운 Context Resolution에는 Period가 필요합니다."
        );

        signals =
                signals == null
                        ? List.of()
                        : List.copyOf(
                        signals
                );

        for (
                DecadeLuckContextSignal signal
                : signals
        ) {

            Objects.requireNonNull(
                    signal,
                    "대운 Context Signal은 null일 수 없습니다."
            );
        }

        /*
         * 동일 Axis + Code는 하나의 의미여야 한다.
         *
         * 같은 Code를 SUPPORTED와 CONFLICTED로
         * 동시에 생성해야 한다면 Code 자체를
         * 더 구체적으로 나누는 것이 맞다.
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

                DecadeLuckContextSignal a =
                        signals.get(
                                first
                        );

                DecadeLuckContextSignal b =
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
                            "동일한 대운 Context Signal이 중복되었습니다: "
                                    + a.axis()
                                    + " / "
                                    + a.code()
                    );
                }
            }
        }
    }

    public boolean empty() {

        return signals.isEmpty();
    }
}