package com.dajeom.calculator;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 천간과 일간 사이의 정확한 십성을 계산한다.
 *
 * 정밀화 5차부터:
 *
 * - 월령 격 후보
 * - 성격
 * - 파격
 *
 * 분석에서 정확한 음양 십성 판정이
 * 반복적으로 필요하기 때문에
 * 공통 Calculator로 분리한다.
 *
 * 입력:
 *
 * dayMaster
 * target
 *
 * 출력:
 *
 * 비견
 * 겁재
 * 식신
 * 상관
 * 편재
 * 정재
 * 편관
 * 정관
 * 편인
 * 정인
 */
@Component
public class StructureTenGodCalculator {

    public StructureTenGodType calculate(

            HeavenlyStem dayMaster,

            HeavenlyStem target

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                target,
                "대상 천간이 필요합니다."
        );

        int dayIndex =
                stemIndexOf(
                        dayMaster
                );

        int targetIndex =
                stemIndexOf(
                        target
                );

        FiveElement dayElement =
                elementOf(
                        dayMaster
                );

        FiveElement targetElement =
                elementOf(
                        target
                );

        boolean samePolarity =
                dayIndex % 2
                        == targetIndex % 2;

        /*
         * 비겁
         */
        if (
                dayElement == targetElement
        ) {

            return samePolarity
                    ? StructureTenGodType.COMPANION
                    : StructureTenGodType.ROB_WEALTH;
        }

        /*
         * 식상
         *
         * 일간이 target을 생함.
         */
        if (
                produces(
                        dayElement,
                        targetElement
                )
        ) {

            return samePolarity
                    ? StructureTenGodType.EATING_GOD
                    : StructureTenGodType.HURTING_OFFICER;
        }

        /*
         * 재성
         *
         * 일간이 target을 극함.
         */
        if (
                controls(
                        dayElement,
                        targetElement
                )
        ) {

            return samePolarity
                    ? StructureTenGodType.INDIRECT_WEALTH
                    : StructureTenGodType.DIRECT_WEALTH;
        }

        /*
         * 관살
         *
         * target이 일간을 극함.
         */
        if (
                controls(
                        targetElement,
                        dayElement
                )
        ) {

            return samePolarity
                    ? StructureTenGodType.SEVEN_KILLINGS
                    : StructureTenGodType.DIRECT_OFFICER;
        }

        /*
         * 인성
         *
         * target이 일간을 생함.
         */
        if (
                produces(
                        targetElement,
                        dayElement
                )
        ) {

            return samePolarity
                    ? StructureTenGodType.INDIRECT_RESOURCE
                    : StructureTenGodType.DIRECT_RESOURCE;
        }

        throw new IllegalStateException(
                "십성을 계산할 수 없습니다: "
                        + dayMaster
                        + " / "
                        + target
        );
    }

    public FiveElement elementOf(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        int index =
                stemIndexOf(
                        stem
                );

        return switch (
                index / 2
                ) {

            case 0 ->
                    FiveElement.WOOD;

            case 1 ->
                    FiveElement.FIRE;

            case 2 ->
                    FiveElement.EARTH;

            case 3 ->
                    FiveElement.METAL;

            case 4 ->
                    FiveElement.WATER;

            default ->
                    throw new IllegalStateException(
                            "천간 오행을 계산할 수 없습니다: "
                                    + stem
                    );
        };
    }

    public boolean samePolarity(

            HeavenlyStem first,

            HeavenlyStem second

    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 천간이 필요합니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 천간이 필요합니다."
        );

        return stemIndexOf(
                first
        ) % 2
                == stemIndexOf(
                second
        ) % 2;
    }

    private int stemIndexOf(
            HeavenlyStem stem
    ) {

        for (
                int index = 0;
                index < 10;
                index++
        ) {

            if (
                    HeavenlyStem.fromIndex(
                            index
                    ) == stem
            ) {

                return index;
            }
        }

        throw new IllegalArgumentException(
                "알 수 없는 천간입니다: "
                        + stem
        );
    }

    private boolean produces(

            FiveElement source,

            FiveElement target

    ) {

        return switch (
                source
                ) {

            case WOOD ->
                    target == FiveElement.FIRE;

            case FIRE ->
                    target == FiveElement.EARTH;

            case EARTH ->
                    target == FiveElement.METAL;

            case METAL ->
                    target == FiveElement.WATER;

            case WATER ->
                    target == FiveElement.WOOD;
        };
    }

    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return switch (
                source
                ) {

            case WOOD ->
                    target == FiveElement.EARTH;

            case FIRE ->
                    target == FiveElement.METAL;

            case EARTH ->
                    target == FiveElement.WATER;

            case METAL ->
                    target == FiveElement.WOOD;

            case WATER ->
                    target == FiveElement.FIRE;
        };
    }
}