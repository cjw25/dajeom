package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.VisibleStemPosition;

import java.util.Objects;

/**
 * 원국에 실제로 드러나 있는 천간 하나의
 * 기본 표면 세력.
 *
 * 예:
 *
 * 癸酉
 * 庚申
 * 丁丑
 * 甲辰
 *
 * 에서는:
 *
 * 년간 癸
 * 월간 庚
 * 일간 丁
 * 시간 甲
 *
 * 네 개의 VisibleStemPower가 생성된다.
 *
 * baseWeight는 이 단계에서
 * 모든 천간에 동일하게 부여한다.
 *
 * 통근, 월령, 투간, 생극 등의 영향은
 * 이후 단계에서 별도로 계산한다.
 */
public record VisibleStemPower(

        VisibleStemPosition position,

        HeavenlyStem stem,

        FiveElement element,

        boolean dayMaster,

        int baseWeight

) {

    public VisibleStemPower {

        Objects.requireNonNull(
                position,
                "천간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        Objects.requireNonNull(
                element,
                "천간 오행이 필요합니다."
        );

        if (
                baseWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "천간 기본 세력은 0보다 커야 합니다."
            );
        }

        if (
                dayMaster
                        != position.isDayMasterPosition()
        ) {

            throw new IllegalArgumentException(
                    "일간 여부와 천간 위치가 일치하지 않습니다."
            );
        }
    }

    public boolean yearStem() {

        return position.isYear();
    }

    public boolean monthStem() {

        return position.isMonth();
    }

    public boolean dayStem() {

        return position.isDay();
    }

    public boolean hourStem() {

        return position.isHour();
    }
}