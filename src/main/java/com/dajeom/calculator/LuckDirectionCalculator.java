package com.dajeom.calculator;

import com.dajeom.model.Pillar;
import com.dajeom.type.Gender;
import com.dajeom.type.LuckDirection;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

/**
 * 성별과 년간의 음양을 기준으로
 * 대운의 순행 / 역행 방향을 계산한다.
 *
 * 기준:
 *
 * 남성 + 양년생 = 순행
 * 남성 + 음년생 = 역행
 *
 * 여성 + 양년생 = 역행
 * 여성 + 음년생 = 순행
 */
@Component
public class LuckDirectionCalculator {

    private static final int FORWARD_STEP =
            1;

    private static final int REVERSE_STEP =
            -1;

    public LuckDirection calculate(
            Gender gender,
            Pillar yearPillar
    ) {
        Objects.requireNonNull(
                gender,
                "성별은 필수입니다."
        );

        Objects.requireNonNull(
                yearPillar,
                "년주는 필수입니다."
        );

        /*
         * 천간 인덱스:
         *
         * 갑 0 = 양
         * 을 1 = 음
         * 병 2 = 양
         * 정 3 = 음
         * 무 4 = 양
         * 기 5 = 음
         * 경 6 = 양
         * 신 7 = 음
         * 임 8 = 양
         * 계 9 = 음
         */
        boolean yangYear =
                Math.floorMod(
                        yearPillar
                                .stem()
                                .getIndex(),
                        2
                ) == 0;

        boolean male =
                "MALE".equals(
                        gender.name()
                );

        boolean forward =
                (male && yangYear)
                        || (!male && !yangYear);

        int requiredStep =
                forward
                        ? FORWARD_STEP
                        : REVERSE_STEP;

        /*
         * LuckDirection의 enum 이름에 직접 의존하지 않고
         * 기존 getStep() 값을 기준으로 찾는다.
         *
         * 따라서 enum 값이
         * FORWARD / REVERSE이든
         * SUNHAENG / YEOKHAENG이든
         * getStep()만 1 / -1이면 정상 동작한다.
         */
        return Arrays.stream(
                        LuckDirection.values()
                )
                .filter(direction ->
                        direction.getStep()
                                == requiredStep
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "LuckDirection에서 "
                                        + requiredStep
                                        + " 방향을 찾을 수 없습니다."
                        )
                );
    }
}