package com.dajeom.calculator;

import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.MonthlyLuckBoundaryAnalysis;
import com.dajeom.model.SexagenaryPillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 15차-2.
 *
 * 월운 간지 Calculator.
 *
 * =================================================
 *
 * 월지:
 *
 * monthIndex 0  -> 寅
 * monthIndex 1  -> 卯
 * monthIndex 2  -> 辰
 * monthIndex 3  -> 巳
 * monthIndex 4  -> 午
 * monthIndex 5  -> 未
 * monthIndex 6  -> 申
 * monthIndex 7  -> 酉
 * monthIndex 8  -> 戌
 * monthIndex 9  -> 亥
 * monthIndex 10 -> 子
 * monthIndex 11 -> 丑
 *
 * =================================================
 *
 * 월간:
 *
 * 해당 명리연도의 연간을 기준으로
 * 五虎遁을 적용한다.
 *
 * 甲己年 -> 丙寅起
 * 乙庚年 -> 戊寅起
 * 丙辛年 -> 庚寅起
 * 丁壬年 -> 壬寅起
 * 戊癸年 -> 甲寅起
 *
 * =================================================
 *
 * 예:
 *
 * 2026 = 丙午年
 *
 * 丙年이므로
 *
 * 寅월 = 庚寅
 *
 * 이후:
 *
 * 辛卯
 * 壬辰
 * 癸巳
 * ...
 *
 * 순서로 진행한다.
 *
 * =================================================
 *
 * 육십갑자 생성 자체는
 * 기존 SexagenaryCycleCalculator를
 * canonical source로 사용한다.
 */
@Component
public class MonthlyLuckPillarCalculator {

    private static final int
            MONTH_COUNT = 12;

    private final SexagenaryCycleCalculator
            sexagenaryCycleCalculator;

    public MonthlyLuckPillarCalculator(
            SexagenaryCycleCalculator
                    sexagenaryCycleCalculator
    ) {

        this.sexagenaryCycleCalculator =
                Objects.requireNonNull(
                        sexagenaryCycleCalculator,
                        "SexagenaryCycleCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15-2 API
     * =================================================
     */
    public SexagenaryPillar calculate(

            AnnualLuckPeriod annualPeriod,

            MonthlyLuckBoundaryAnalysis boundaryAnalysis

    ) {

        Objects.requireNonNull(
                annualPeriod,
                "월운 간지 계산에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                boundaryAnalysis,
                "월운 간지 계산에는 MonthlyLuckBoundaryAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 동일 명리연도 검증
         * =================================================
         */

        if (
                annualPeriod.targetYear()
                        != boundaryAnalysis.sajuYear()
        ) {

            throw new IllegalArgumentException(
                    "세운 targetYear와 월운 sajuYear가 일치하지 않습니다."
            );
        }

        if (
                !annualPeriod.zoneId()
                        .equals(
                                boundaryAnalysis.zoneId()
                        )
        ) {

            throw new IllegalArgumentException(
                    "세운과 월운 경계의 ZoneId가 일치하지 않습니다."
            );
        }

        return calculate(
                annualPeriod.stem(),
                boundaryAnalysis.monthIndex()
        );
    }

    /**
     * =================================================
     * 연간 + monthIndex 직접 계산
     * =================================================
     */
    public SexagenaryPillar calculate(

            HeavenlyStem annualStem,

            int monthIndex

    ) {

        Objects.requireNonNull(
                annualStem,
                "월운 계산에는 세운 연간이 필요합니다."
        );

        validateMonthIndex(
                monthIndex
        );

        /*
         * 五虎遁으로 寅월의 시작 천간 결정.
         */

        HeavenlyStem firstMonthStem =
                calculateTigerMonthStem(
                        annualStem
                );

        /*
         * 寅월의 완전한 육십갑자 Pillar.
         */

        SexagenaryPillar firstMonthPillar =
                sexagenaryCycleCalculator
                        .pillarOf(
                                firstMonthStem,
                                EarthlyBranch.YIN
                        );

        /*
         * 이후 월은 육십갑자에서
         * monthIndex만큼 순행.
         *
         * 0 -> 寅
         * 1 -> 卯
         * ...
         * 11 -> 丑
         */

        return sexagenaryCycleCalculator
                .shift(
                        firstMonthPillar,
                        monthIndex
                );
    }

    /**
     * =================================================
     * 五虎遁
     * =================================================
     *
     * 해당 연간에서 寅월의 월간을 결정한다.
     */
    public HeavenlyStem calculateTigerMonthStem(
            HeavenlyStem annualStem
    ) {

        Objects.requireNonNull(
                annualStem,
                "五虎遁 계산에는 연간이 필요합니다."
        );

        return switch (
                annualStem
                ) {

            /*
             * 甲己年 丙作首
             */
            case JIA, JI ->
                    HeavenlyStem.BING;

            /*
             * 乙庚年 戊為頭
             */
            case YI, GENG ->
                    HeavenlyStem.WU;

            /*
             * 丙辛年 庚寅起
             */
            case BING, XIN ->
                    HeavenlyStem.GENG;

            /*
             * 丁壬年 壬寅順
             */
            case DING, REN ->
                    HeavenlyStem.REN;

            /*
             * 戊癸年 甲寅求
             */
            case WU, GUI ->
                    HeavenlyStem.JIA;
        };
    }

    /**
     * =================================================
     * monthIndex -> 월지
     * =================================================
     *
     * 계산 검증이나 후속 단계에서 사용할 수 있다.
     */
    public EarthlyBranch calculateMonthBranch(
            int monthIndex
    ) {

        validateMonthIndex(
                monthIndex
        );

        return switch (
                monthIndex
                ) {

            case 0 ->
                    EarthlyBranch.YIN;

            case 1 ->
                    EarthlyBranch.MAO;

            case 2 ->
                    EarthlyBranch.CHEN;

            case 3 ->
                    EarthlyBranch.SI;

            case 4 ->
                    EarthlyBranch.WU;

            case 5 ->
                    EarthlyBranch.WEI;

            case 6 ->
                    EarthlyBranch.SHEN;

            case 7 ->
                    EarthlyBranch.YOU;

            case 8 ->
                    EarthlyBranch.XU;

            case 9 ->
                    EarthlyBranch.HAI;

            case 10 ->
                    EarthlyBranch.ZI;

            case 11 ->
                    EarthlyBranch.CHOU;

            default ->
                    throw new IllegalStateException(
                            "월운 월지를 계산할 수 없습니다."
                    );
        };
    }

    /**
     * 계산 결과와 monthIndex의 월지가
     * 정확히 일치하는지 검증.
     */
    public boolean matchesMonthBranch(

            SexagenaryPillar pillar,

            int monthIndex

    ) {

        if (
                pillar == null
        ) {

            return false;
        }

        return pillar.branch()
                == calculateMonthBranch(
                monthIndex
        );
    }

    private void validateMonthIndex(
            int monthIndex
    ) {

        if (
                monthIndex < 0
                        || monthIndex >= MONTH_COUNT
        ) {

            throw new IllegalArgumentException(
                    "월운 monthIndex는 0~11이어야 합니다: "
                            + monthIndex
            );
        }
    }
}