package com.dajeom.model;

import com.dajeom.type.FiveElement;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 원국의 오행 원시 분포를 나타낸다.
 *
 * visibleStemCounts:
 * 년간·월간·일간·시간의 오행 분포
 *
 * branchMainCounts:
 * 년지·월지·일지·시지 자체의 대표 오행 분포
 *
 * eightCharacterCounts:
 * 표면에 나타난 팔자 8글자의 오행 분포
 *
 * hiddenStemCounts:
 * 모든 지장간의 오행 분포
 *
 * 현재 단계에서는 계절과 월령 가중치를 적용하지 않는다.
 * 신강·신약 계산용 가중치는 다음 단계에서 별도로 처리한다.
 */
public record ElementDistribution(
        Map<FiveElement, Integer> visibleStemCounts,
        Map<FiveElement, Integer> branchMainCounts,
        Map<FiveElement, Integer> eightCharacterCounts,
        Map<FiveElement, Integer> hiddenStemCounts
) {

    public ElementDistribution {
        Objects.requireNonNull(
                visibleStemCounts,
                "천간 오행 분포는 필수입니다."
        );

        Objects.requireNonNull(
                branchMainCounts,
                "지지 대표 오행 분포는 필수입니다."
        );

        Objects.requireNonNull(
                eightCharacterCounts,
                "팔자 오행 분포는 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStemCounts,
                "지장간 오행 분포는 필수입니다."
        );

        visibleStemCounts =
                immutableCopy(visibleStemCounts);

        branchMainCounts =
                immutableCopy(branchMainCounts);

        eightCharacterCounts =
                immutableCopy(eightCharacterCounts);

        hiddenStemCounts =
                immutableCopy(hiddenStemCounts);
    }

    private static Map<FiveElement, Integer> immutableCopy(
            Map<FiveElement, Integer> source
    ) {
        EnumMap<FiveElement, Integer> copy =
                new EnumMap<>(FiveElement.class);

        for (FiveElement element : FiveElement.values()) {
            copy.put(
                    element,
                    source.getOrDefault(element, 0)
            );
        }

        return Collections.unmodifiableMap(copy);
    }
}