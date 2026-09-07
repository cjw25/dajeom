package com.dajeom.dto;

import com.dajeom.calculation.HiddenStemCalculator;
import com.dajeom.domain.HiddenStem;

import java.util.Objects;

public record HiddenStemResponse(
        String position,
        String positionKorean,
        String stem,
        String stemKorean,
        String tenGod,
        String tenGodHanja
) {

    public static HiddenStemResponse from(
            HiddenStemCalculator.HiddenStemTenGod value
    ) {

        Objects.requireNonNull(
                value,
                "지장간 십성 결과가 필요합니다."
        );

        HiddenStem hiddenStem =
                value.hiddenStem();

        return new HiddenStemResponse(
                hiddenStem.position().name(),
                hiddenStem.position().korean(),
                hiddenStem.stem().hanja(),
                hiddenStem.stem().korean(),
                value.tenGod().korean(),
                value.tenGod().hanja()
        );
    }
}