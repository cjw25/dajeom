package com.dajeom.calculation;

import com.dajeom.domain.DaewoonDirection;
import com.dajeom.domain.Gender;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.YinYang;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 대운 순행 / 역행 판정.
 *
 * 채택 규칙:
 *
 * 양남음녀 → 순행
 * 음남양녀 → 역행
 *
 * 여기서 음양은 사주 년주의 천간을 기준으로 한다.
 */
@Component
public class DaewoonDirectionCalculator {

    public DaewoonDirection calculate(
            HeavenlyStem yearStem,
            Gender gender
    ) {

        Objects.requireNonNull(
                yearStem,
                "대운 방향 계산에는 년간이 필요합니다."
        );

        Objects.requireNonNull(
                gender,
                "대운 방향 계산에는 성별이 필요합니다."
        );

        YinYang yinYang =
                yearStem.yinYang();

        /*
         * 양남
         */
        if (yinYang == YinYang.YANG
                && gender == Gender.MALE) {

            return DaewoonDirection.FORWARD;
        }

        /*
         * 음녀
         */
        if (yinYang == YinYang.YIN
                && gender == Gender.FEMALE) {

            return DaewoonDirection.FORWARD;
        }

        /*
         * 나머지는
         *
         * 음남
         * 양녀
         *
         * → 역행
         */
        return DaewoonDirection.REVERSE;
    }
}