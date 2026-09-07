package com.dajeom.calculation;

import com.dajeom.domain.DaewoonDirection;
import com.dajeom.domain.DaewoonPillar;
import com.dajeom.domain.Ganji;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.SexagenaryCycle;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 대운 간지 배열 계산.
 *
 * 기준:
 *
 * 순행
 * → 월주 다음 간지부터 시작
 *
 * 역행
 * → 월주 이전 간지부터 시작
 *
 * 이후 한 대운마다 60갑자를 한 칸씩 이동한다.
 */
@Component
public class DaewoonPillarCalculator {

    private static final int DEFAULT_COUNT = 10;

    public List<DaewoonPillar> calculate(
            Pillar monthPillar,
            DaewoonDirection direction
    ) {

        return calculate(
                monthPillar,
                direction,
                DEFAULT_COUNT
        );
    }

    public List<DaewoonPillar> calculate(
            Pillar monthPillar,
            DaewoonDirection direction,
            int count
    ) {

        Objects.requireNonNull(
                monthPillar,
                "대운 계산에는 월주가 필요합니다."
        );

        Objects.requireNonNull(
                direction,
                "대운 계산에는 순행/역행 방향이 필요합니다."
        );

        if (count < 1) {

            throw new IllegalArgumentException(
                    "생성할 대운 개수는 1 이상이어야 합니다."
            );
        }

        int step =
                direction.isForward()
                        ? 1
                        : -1;

        List<DaewoonPillar> result =
                new ArrayList<>();

        /*
         * 월주 자체는 대운 1번이 아니다.
         *
         * 순행이면 +1
         * 역행이면 -1
         *
         * 한 간지부터 첫 대운이 시작된다.
         */
        for (int order = 1;
             order <= count;
             order++) {

            int cycleIndex =
                    monthPillar.cycleIndex()
                            + (step * order);

            Ganji ganji =
                    SexagenaryCycle.ofIndex(
                            cycleIndex
                    );

            result.add(
                    new DaewoonPillar(
                            order,
                            ganji
                    )
            );
        }

        return List.copyOf(
                result
        );
    }
}