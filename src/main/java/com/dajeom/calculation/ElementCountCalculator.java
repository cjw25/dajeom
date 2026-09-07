package com.dajeom.calculation;

import com.dajeom.domain.ElementCount;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.FourPillars;
import com.dajeom.domain.Pillar;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 원국에 드러난 4주의 오행 개수를 계산한다.
 *
 * 중요:
 *
 * 여기서는 지장간을 세지 않는다.
 *
 * 오직:
 *
 * 년간 + 년지
 * 월간 + 월지
 * 일간 + 일지
 * 시간 + 시지
 *
 * 를 센다.
 */
@Component
public class ElementCountCalculator {

    public ElementCount calculate(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 네 기둥이 필요합니다."
        );

        EnumMap<FiveElement, Integer> counts =
                new EnumMap<>(
                        FiveElement.class
                );

        /*
         * 모든 오행을 0으로 초기화.
         */
        for (FiveElement element : FiveElement.values()) {

            counts.put(
                    element,
                    0
            );
        }

        /*
         * 년주
         */
        addPillar(
                counts,
                fourPillars.year()
        );

        /*
         * 월주
         */
        addPillar(
                counts,
                fourPillars.month()
        );

        /*
         * 일주
         */
        addPillar(
                counts,
                fourPillars.day()
        );

        /*
         * 시주는 출생시간을 아는 경우만 존재한다.
         */
        if (fourPillars.hasKnownHour()) {

            addPillar(
                    counts,
                    fourPillars.hour()
            );
        }

        return new ElementCount(
                counts.get(FiveElement.WOOD),
                counts.get(FiveElement.FIRE),
                counts.get(FiveElement.EARTH),
                counts.get(FiveElement.METAL),
                counts.get(FiveElement.WATER)
        );
    }

    private void addPillar(
            Map<FiveElement, Integer> counts,
            Pillar pillar
    ) {

        Objects.requireNonNull(
                counts,
                "오행 개수 저장소가 필요합니다."
        );

        Objects.requireNonNull(
                pillar,
                "기둥이 필요합니다."
        );

        /*
         * 천간 1개.
         */
        add(
                counts,
                pillar.stem().element()
        );

        /*
         * 지지 1개.
         */
        add(
                counts,
                pillar.branch().element()
        );
    }

    private void add(
            Map<FiveElement, Integer> counts,
            FiveElement element
    ) {

        counts.compute(
                element,
                (key, current) ->
                        current == null
                                ? 1
                                : current + 1
        );
    }
}