package com.dajeom.domain;

import java.util.Objects;

/**
 * 일간이 직접 참여한 천간오합.
 *
 * 예:
 *
 * 丁 일간 + 壬 월간
 * → 丁壬合
 * → 화신 木
 *
 * 이 객체가 존재한다고 해서
 * 화기격이 성립한 것은 아니다.
 */
public record DayMasterStemCombinationOccurrence(

        HeavenlyStem dayMaster,

        HeavenlyStem counterpartStem,

        PillarPosition counterpartPosition,

        FiveElement transformationElement

) {

    public DayMasterStemCombinationOccurrence {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                counterpartStem,
                "합 상대 천간은 필수입니다."
        );

        Objects.requireNonNull(
                counterpartPosition,
                "합 상대 위치는 필수입니다."
        );

        Objects.requireNonNull(
                transformationElement,
                "화신 오행은 필수입니다."
        );

        if (
                counterpartPosition
                        == PillarPosition.DAY
        ) {

            throw new IllegalArgumentException(
                    "일간 자신을 천간오합 상대 위치로 지정할 수 없습니다."
            );
        }
    }

    /**
     * 전통 진화 검토에서 특히 중요하게 보는
     * 월간 또는 시간의 합 상대인지.
     *
     * 이것만으로 真化를 확정하지 않는다.
     */
    public boolean counterpartInMonthOrHour() {

        return counterpartPosition
                == PillarPosition.MONTH

                ||

                counterpartPosition
                        == PillarPosition.HOUR;
    }
}