package com.dajeom.calculator;

import com.dajeom.model.ElementDistribution;
import com.dajeom.model.FourPillars;
import com.dajeom.model.Pillar;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 원국의 기본 오행 분포 계산기.
 *
 * 현재 단계에서는:
 *
 * 천간의 오행
 * +
 * 지지의 대표 오행
 *
 * 만 집계한다.
 *
 * 지장간 가중치,
 * 월령,
 * 통근,
 * 생조극설,
 * 계절 세력 등은 포함하지 않는다.
 *
 * 따라서 이 결과를 그대로
 * 신강·신약 판정에 사용하면 안 된다.
 */
@Component
public class ElementDistributionCalculator {

    public ElementDistribution calculate(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        EnumMap<
                FiveElement,
                Integer
                > counts =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            counts.put(
                    element,
                    0
            );
        }

        countPillar(
                counts,
                fourPillars.yearPillar()
        );

        countPillar(
                counts,
                fourPillars.monthPillar()
        );

        countPillar(
                counts,
                fourPillars.dayPillar()
        );

        if (
                fourPillars.hasHourPillar()
        ) {

            countPillar(
                    counts,
                    fourPillars.hourPillar()
            );
        }

        return new ElementDistribution(
                counts
        );
    }

    /**
     * 하나의 기둥에서:
     *
     * 천간 1개
     * 지지 1개
     *
     * 두 오행을 집계한다.
     */
    private void countPillar(
            Map<FiveElement, Integer> counts,
            Pillar pillar
    ) {

        increment(
                counts,
                pillar
                        .stem()
                        .getElement()
        );

        increment(
                counts,
                pillar
                        .branch()
                        .getElement()
        );
    }

    private void increment(
            Map<FiveElement, Integer> counts,
            FiveElement element
    ) {

        counts.merge(
                element,
                1,
                Integer::sum
        );
    }
}