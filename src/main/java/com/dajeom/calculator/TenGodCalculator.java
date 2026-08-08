package com.dajeom.calculator;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TenGod;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 십성 계산기.
 *
 * 십성은 일간(Day Master)을 기준으로
 * 상대 천간과의 오행 생극 관계 및
 * 음양 관계를 조합해서 결정한다.
 */
@Component
public class TenGodCalculator {

    /**
     * 일간을 기준으로
     * 상대 천간의 십성을 계산한다.
     */
    public TenGod calculate(
            HeavenlyStem dayMaster,
            HeavenlyStem targetStem
    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                targetStem,
                "대상 천간은 필수입니다."
        );

        FiveElement dayElement =
                dayMaster.getElement();

        FiveElement targetElement =
                targetStem.getElement();

        boolean samePolarity =
                dayMaster.getYinYang()
                        == targetStem.getYinYang();

        /*
         * 같은 오행
         *
         * 같은 음양 → 비견
         * 다른 음양 → 겁재
         */
        if (
                dayElement
                        == targetElement
        ) {

            return samePolarity
                    ? TenGod.BI_GYEON
                    : TenGod.GEOP_JAE;
        }

        /*
         * 일간이 상대 오행을 생함.
         *
         * 같은 음양 → 식신
         * 다른 음양 → 상관
         */
        if (
                produces(
                        dayElement,
                        targetElement
                )
        ) {

            return samePolarity
                    ? TenGod.SIK_SIN
                    : TenGod.SANG_GWAN;
        }

        /*
         * 일간이 상대 오행을 극함.
         *
         * 같은 음양 → 편재
         * 다른 음양 → 정재
         */
        if (
                controls(
                        dayElement,
                        targetElement
                )
        ) {

            return samePolarity
                    ? TenGod.PYEON_JAE
                    : TenGod.JEONG_JAE;
        }

        /*
         * 상대 오행이 일간을 극함.
         *
         * 같은 음양 → 편관
         * 다른 음양 → 정관
         */
        if (
                controls(
                        targetElement,
                        dayElement
                )
        ) {

            return samePolarity
                    ? TenGod.PYEON_GWAN
                    : TenGod.JEONG_GWAN;
        }

        /*
         * 상대 오행이 일간을 생함.
         *
         * 같은 음양 → 편인
         * 다른 음양 → 정인
         */
        if (
                produces(
                        targetElement,
                        dayElement
                )
        ) {

            return samePolarity
                    ? TenGod.PYEON_IN
                    : TenGod.JEONG_IN;
        }

        throw new IllegalStateException(
                "십성 관계를 계산할 수 없습니다: "
                        + dayMaster
                        + " / "
                        + targetStem
        );
    }

    /**
     * 오행 상생 관계.
     *
     * 목 → 화
     * 화 → 토
     * 토 → 금
     * 금 → 수
     * 수 → 목
     */
    private boolean produces(
            FiveElement source,
            FiveElement target
    ) {

        return switch (source) {

            case WOOD ->
                    target
                            == FiveElement.FIRE;

            case FIRE ->
                    target
                            == FiveElement.EARTH;

            case EARTH ->
                    target
                            == FiveElement.METAL;

            case METAL ->
                    target
                            == FiveElement.WATER;

            case WATER ->
                    target
                            == FiveElement.WOOD;
        };
    }

    /**
     * 오행 상극 관계.
     *
     * 목 → 토
     * 화 → 금
     * 토 → 수
     * 금 → 목
     * 수 → 화
     */
    private boolean controls(
            FiveElement source,
            FiveElement target
    ) {

        return switch (source) {

            case WOOD ->
                    target
                            == FiveElement.EARTH;

            case FIRE ->
                    target
                            == FiveElement.METAL;

            case EARTH ->
                    target
                            == FiveElement.WATER;

            case METAL ->
                    target
                            == FiveElement.WOOD;

            case WATER ->
                    target
                            == FiveElement.FIRE;
        };
    }
}