package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 천을귀인 계산 결과.
 *
 * dayStem
 * → 일간
 *
 * first / second
 * → 해당 일간의 천을귀인 지지
 *
 * hits
 * → 실제 원국 네 지지 중
 *    천을귀인에 해당하는 위치들
 */
public record TianyiNobleman(
        HeavenlyStem dayStem,
        EarthlyBranch first,
        EarthlyBranch second,
        List<Hit> hits
) {

    public TianyiNobleman {

        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                first,
                "첫 번째 천을귀인 지지가 필요합니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 천을귀인 지지가 필요합니다."
        );

        if (first == second) {

            throw new IllegalArgumentException(
                    "천을귀인 두 지지는 서로 달라야 합니다."
            );
        }

        hits =
                hits == null
                        ? List.of()
                        : List.copyOf(
                        hits
                );
    }

    public String hanja() {

        return first.hanja()
                + second.hanja();
    }

    public String korean() {

        return first.korean()
                + second.korean();
    }

    public boolean existsInOriginalChart() {

        return !hits.isEmpty();
    }

    public record Hit(
            PillarPosition position,
            EarthlyBranch branch
    ) {

        public Hit {

            Objects.requireNonNull(
                    position,
                    "천을귀인이 위치한 기둥이 필요합니다."
            );

            Objects.requireNonNull(
                    branch,
                    "천을귀인 지지가 필요합니다."
            );
        }
    }
}