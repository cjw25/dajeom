package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.StemRelationType;

import java.util.Objects;

/**
 * 원국 / 대운 / 세운 사이에서
 * 발견된 천간 관계.
 */
public record LuckStemInteraction(

        StemRelationType type,

        LuckPillarSource firstSource,

        LuckPillarSource secondSource,

        /**
         * 천간합의 합화 오행 후보.
         *
         * 실제 합화 성립 여부와는 별개다.
         */
        FiveElement combinationElement

) {

    public LuckStemInteraction {

        Objects.requireNonNull(
                type,
                "천간 관계 종류는 필수입니다."
        );

        Objects.requireNonNull(
                firstSource,
                "첫 번째 출처는 필수입니다."
        );

        Objects.requireNonNull(
                secondSource,
                "두 번째 출처는 필수입니다."
        );

        Objects.requireNonNull(
                combinationElement,
                "합화 후보 오행은 필수입니다."
        );
    }
}