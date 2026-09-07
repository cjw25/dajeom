package com.dajeom.dto;

import com.dajeom.domain.Ganji;
import com.dajeom.domain.Nayin;

import java.util.Objects;

public record NayinResponse(
        String sourceGanji,

        String name,
        String nameKorean,

        String element,
        String elementKorean,
        String elementHanja
) {

    public static NayinResponse of(
            Ganji ganji,
            Nayin nayin
    ) {

        Objects.requireNonNull(
                ganji,
                "납음 기준 간지가 필요합니다."
        );

        Objects.requireNonNull(
                nayin,
                "납음 결과가 필요합니다."
        );

        return new NayinResponse(
                ganji.hanja(),

                nayin.hanja(),
                nayin.korean(),

                nayin.element().name(),
                nayin.element().korean(),
                nayin.element().hanja()
        );
    }
}