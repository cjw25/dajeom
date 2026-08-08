package com.dajeom.calculator;

import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.Pillar;
import com.dajeom.type.Gender;
import com.dajeom.type.LuckDirection;
import com.dajeom.type.YinYang;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 대운 순행/역행 계산기.
 *
 * V1 기준:
 *
 * 양년 남자 -> 순행
 * 음년 남자 -> 역행
 *
 * 양년 여자 -> 역행
 * 음년 여자 -> 순행
 */
@Component
public class LuckDirectionCalculator {

    public LuckDirection calculate(
            NormalizedBirthData birthData,
            Pillar yearPillar
    ) {

        Objects.requireNonNull(
                birthData,
                "출생정보는 필수입니다."
        );

        Objects.requireNonNull(
                yearPillar,
                "년주는 필수입니다."
        );

        Gender gender =
                birthData.gender();

        YinYang yearStemYinYang =
                yearPillar
                        .stem()
                        .getYinYang();

        boolean forward;

        if (
                gender
                        == Gender.MALE
        ) {

            forward =
                    yearStemYinYang
                            == YinYang.YANG;

        } else {

            forward =
                    yearStemYinYang
                            == YinYang.YIN;
        }

        return forward
                ? LuckDirection.FORWARD
                : LuckDirection.REVERSE;
    }
}