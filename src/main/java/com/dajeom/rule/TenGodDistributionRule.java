package com.dajeom.rule;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TenGodPowerLevel;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 10차-1.
 *
 * 십성 분포·세력의 공통 계산 Rule.
 *
 * =================================================
 *
 * Power Level
 *
 * 해당 십성이 전체 외부 십성 세력에서
 * 차지하는 상대 비율 기준.
 *
 * DOMINANT >= 25%
 * STRONG   >= 15%
 * MODERATE >= 8%
 * WEAK      > 0%
 * ABSENT     0%
 *
 * =================================================
 *
 * 이 수치는 길흉 확률이 아니다.
 *
 * 10-2~10-8 복합구조 판정의
 * 상대적인 세력 Evidence로 사용한다.
 */
@Component
public class TenGodDistributionRule {

    public static final int VISIBLE_STEM_RAW_WEIGHT =
            100;

    public static final int DOMINANT_SHARE_BASIS_POINTS =
            2_500;

    public static final int STRONG_SHARE_BASIS_POINTS =
            1_500;

    public static final int MODERATE_SHARE_BASIS_POINTS =
            800;

    public TenGodPowerLevel determinePowerLevel(

            int occurrenceCount,

            int shareBasisPoints

    ) {

        if (
                occurrenceCount <= 0
        ) {

            return TenGodPowerLevel.ABSENT;
        }

        if (
                shareBasisPoints
                        >= DOMINANT_SHARE_BASIS_POINTS
        ) {

            return TenGodPowerLevel.DOMINANT;
        }

        if (
                shareBasisPoints
                        >= STRONG_SHARE_BASIS_POINTS
        ) {

            return TenGodPowerLevel.STRONG;
        }

        if (
                shareBasisPoints
                        >= MODERATE_SHARE_BASIS_POINTS
        ) {

            return TenGodPowerLevel.MODERATE;
        }

        return TenGodPowerLevel.WEAK;
    }

    public int calculateShareBasisPoints(

            int power,

            int totalPower

    ) {

        if (
                power <= 0
                        || totalPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                10_000,
                (int) Math.round(
                        power
                                * 10_000.0
                                / totalPower
                )
        );
    }

    /**
     * 하나의 오행 최종 세력을
     * 동일 오행 source들의 rawWeight에 따라 배분.
     */
    public int allocateElementPower(

            int elementFinalPower,

            int sourceRawWeight,

            int totalSameElementRawWeight

    ) {

        if (
                elementFinalPower <= 0
                        || sourceRawWeight <= 0
                        || totalSameElementRawWeight <= 0
        ) {

            return 0;
        }

        return Math.max(
                0,
                (int) Math.round(
                        elementFinalPower
                                * sourceRawWeight
                                / (double) totalSameElementRawWeight
                )
        );
    }

    /**
     * HeavenlyStem -> FiveElement.
     *
     * 십성 분석에서 enum의 별도 convenience API에
     * 의존하지 않도록 명시적인 오행 표를 둔다.
     */
    public FiveElement elementOf(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        return switch (
                stem
                ) {

            case JIA,
                 YI ->
                    FiveElement.WOOD;

            case BING,
                 DING ->
                    FiveElement.FIRE;

            case WU,
                 JI ->
                    FiveElement.EARTH;

            case GENG,
                 XIN ->
                    FiveElement.METAL;

            case REN,
                 GUI ->
                    FiveElement.WATER;
        };
    }
}