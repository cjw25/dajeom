package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.StemRelationType;

import java.util.Objects;

/**
 * 원국 천간 사이에서 발견된 관계.
 *
 * combinationElement는
 * 전통적인 천간합의 합화 오행 후보를 의미한다.
 *
 * 실제 합화가 성립했다는 의미는 아니다.
 */
public record StemRelation(

        StemRelationType type,

        PillarPosition firstPosition,

        PillarPosition secondPosition,

        HeavenlyStem firstStem,

        HeavenlyStem secondStem,

        FiveElement combinationElement

) {

    public StemRelation {

        Objects.requireNonNull(
                type,
                "천간 관계 종류는 필수입니다."
        );

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 기둥 위치는 필수입니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 기둥 위치는 필수입니다."
        );

        Objects.requireNonNull(
                firstStem,
                "첫 번째 천간은 필수입니다."
        );

        Objects.requireNonNull(
                secondStem,
                "두 번째 천간은 필수입니다."
        );

        Objects.requireNonNull(
                combinationElement,
                "천간합 오행 후보는 필수입니다."
        );

        if (
                firstPosition == secondPosition
        ) {

            throw new IllegalArgumentException(
                    "천간 관계의 두 위치는 같을 수 없습니다."
            );
        }
    }

    public String firstStemName() {

        return firstStem
                .getChineseCharacter();
    }

    public String secondStemName() {

        return secondStem
                .getChineseCharacter();
    }
}