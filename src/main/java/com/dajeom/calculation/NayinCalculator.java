package com.dajeom.calculation;

import com.dajeom.domain.Ganji;
import com.dajeom.domain.Nayin;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NayinCalculator {

    public Nayin calculate(
            Ganji ganji
    ) {

        Objects.requireNonNull(
                ganji,
                "납음 계산 기준 간지가 필요합니다."
        );

        return Nayin.of(
                ganji
        );
    }
}