package com.dajeom.dto;

import com.dajeom.domain.DaewoonDirection;
import com.dajeom.domain.Gender;
import com.dajeom.domain.HeavenlyStem;

import java.util.Objects;

public record DaewoonDirectionResponse(
        String direction,
        String directionKorean,

        String yearStem,
        String yearStemKorean,

        String yearStemYinYang,
        String yearStemYinYangKorean,

        String gender,

        String rule
) {

    public static DaewoonDirectionResponse of(
            DaewoonDirection direction,
            HeavenlyStem yearStem,
            Gender gender
    ) {

        Objects.requireNonNull(
                direction,
                "대운 방향이 필요합니다."
        );

        Objects.requireNonNull(
                yearStem,
                "년간이 필요합니다."
        );

        Objects.requireNonNull(
                gender,
                "성별이 필요합니다."
        );

        return new DaewoonDirectionResponse(
                direction.name(),
                direction.korean(),

                yearStem.hanja(),
                yearStem.korean(),

                yearStem.yinYang().name(),
                yearStem.yinYang().korean(),

                gender.name(),

                "양남음녀 순행 / 음남양녀 역행"
        );
    }
}