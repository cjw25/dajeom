package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.Ganji;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.SexagenaryCycle;
import com.dajeom.domain.WolwoonPeriod;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 월운 계산기.
 *
 * 담당:
 *
 * - 세운 연간 기준 오호둔
 * - 월운 천간 계산
 * - 월운 지지 계산
 * - 월운 간지 생성
 * - 절입 ~ 다음 절입 기간 생성
 *
 * 담당하지 않음:
 *
 * - 절기 자체 계산
 * - 입춘 계산
 * - 세운 계산
 * - 사용자 UI 생성
 * - 월운 해석
 *
 * 절입시각은 SolarTermRepository에서
 * 이미 계산된 정밀값을 전달받아 사용한다.
 */
@Component
public class WolwoonCalculator {

    private static final int STEM_COUNT = 10;

    /**
     * 특정 월운을 생성한다.
     *
     * @param sajuYear               입춘 기준 명리연도
     * @param annualStem             해당 세운의 연간
     * @param monthNumber            1=寅 ... 12=丑
     * @param startDateTimeInclusive 해당 절입시각
     * @param endDateTimeExclusive   다음 절입시각
     */
    public WolwoonPeriod calculate(

            int sajuYear,

            HeavenlyStem annualStem,

            int monthNumber,

            LocalDateTime startDateTimeInclusive,

            LocalDateTime endDateTimeExclusive

    ) {

        Objects.requireNonNull(
                annualStem,
                "세운 연간은 필수입니다."
        );

        Objects.requireNonNull(
                startDateTimeInclusive,
                "월운 시작 절입시각은 필수입니다."
        );

        Objects.requireNonNull(
                endDateTimeExclusive,
                "월운 다음 절입시각은 필수입니다."
        );

        validateMonthNumber(
                monthNumber
        );

        if (
                !endDateTimeExclusive.isAfter(
                        startDateTimeInclusive
                )
        ) {

            throw new IllegalArgumentException(
                    "다음 절입시각은 현재 절입시각보다 뒤여야 합니다."
            );
        }

        HeavenlyStem monthStem =
                calculateMonthStem(
                        annualStem,
                        monthNumber
                );

        EarthlyBranch monthBranch =
                branchOf(
                        monthNumber
                );

        Ganji ganji =
                findGanji(
                        monthStem,
                        monthBranch
                );

        return new WolwoonPeriod(
                sajuYear,
                monthNumber,
                ganji,
                startDateTimeInclusive,
                endDateTimeExclusive
        );
    }

    /**
     * 오호둔으로 월간 계산.
     *
     * 寅월 시작:
     *
     * 甲己 → 丙
     * 乙庚 → 戊
     * 丙辛 → 庚
     * 丁壬 → 壬
     * 戊癸 → 甲
     *
     * 이후 매 월 천간이 한 칸씩 진행한다.
     */
    public HeavenlyStem calculateMonthStem(

            HeavenlyStem annualStem,

            int monthNumber

    ) {

        Objects.requireNonNull(
                annualStem,
                "세운 연간은 필수입니다."
        );

        validateMonthNumber(
                monthNumber
        );

        HeavenlyStem yinMonthStem =
                yinMonthStartingStem(
                        annualStem
                );

        int offset =
                monthNumber - 1;

        int stemIndex =
                Math.floorMod(
                        yinMonthStem.ordinal()
                                + offset,
                        STEM_COUNT
                );

        return HeavenlyStem.values()[
                stemIndex
                ];
    }

    /**
     * 세운 연간으로 寅월 시작 천간 결정.
     */
    public HeavenlyStem yinMonthStartingStem(
            HeavenlyStem annualStem
    ) {

        Objects.requireNonNull(
                annualStem,
                "세운 연간은 필수입니다."
        );

        return switch (annualStem) {

            case JIA, JI ->
                    HeavenlyStem.BING;

            case YI, GENG ->
                    HeavenlyStem.WU;

            case BING, XIN ->
                    HeavenlyStem.GENG;

            case DING, REN ->
                    HeavenlyStem.REN;

            case WU, GUI ->
                    HeavenlyStem.JIA;
        };
    }

    /**
     * 월번호 → 월지.
     */
    public EarthlyBranch branchOf(
            int monthNumber
    ) {

        validateMonthNumber(
                monthNumber
        );

        return switch (monthNumber) {

            case 1 ->
                    EarthlyBranch.YIN;

            case 2 ->
                    EarthlyBranch.MAO;

            case 3 ->
                    EarthlyBranch.CHEN;

            case 4 ->
                    EarthlyBranch.SI;

            case 5 ->
                    EarthlyBranch.WU;

            case 6 ->
                    EarthlyBranch.WEI;

            case 7 ->
                    EarthlyBranch.SHEN;

            case 8 ->
                    EarthlyBranch.YOU;

            case 9 ->
                    EarthlyBranch.XU;

            case 10 ->
                    EarthlyBranch.HAI;

            case 11 ->
                    EarthlyBranch.ZI;

            case 12 ->
                    EarthlyBranch.CHOU;

            default ->
                    throw new IllegalArgumentException(
                            "월운 월번호는 1~12여야 합니다."
                    );
        };
    }

    /**
     * 월지 → 월번호.
     *
     * SolarTermRepository에서 찾은 최근 절입을
     * 기존 월주 계산 로직으로 월지까지 결정했다면
     * 역으로 월번호를 구할 때 사용할 수 있다.
     */
    public int monthNumberOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "월운 지지는 필수입니다."
        );

        return switch (branch) {

            case YIN ->
                    1;

            case MAO ->
                    2;

            case CHEN ->
                    3;

            case SI ->
                    4;

            case WU ->
                    5;

            case WEI ->
                    6;

            case SHEN ->
                    7;

            case YOU ->
                    8;

            case XU ->
                    9;

            case HAI ->
                    10;

            case ZI ->
                    11;

            case CHOU ->
                    12;
        };
    }

    /**
     * 현재 WolwoonPeriod에 targetDateTime이
     * 포함되는지 확인한다.
     */
    public boolean isActiveAt(

            WolwoonPeriod period,

            LocalDateTime targetDateTime

    ) {

        Objects.requireNonNull(
                period,
                "월운 기간은 필수입니다."
        );

        Objects.requireNonNull(
                targetDateTime,
                "월운 조회 기준시각은 필수입니다."
        );

        return period.contains(
                targetDateTime
        );
    }

    /**
     * 천간 + 지지에 해당하는
     * 60갑자 객체 검색.
     *
     * SexagenaryCycle.of(...)의 실제 시그니처에
     * 의존하지 않도록 기존 ofIndex만 사용한다.
     */
    private Ganji findGanji(

            HeavenlyStem stem,

            EarthlyBranch branch

    ) {

        for (
                int cycleIndex = 0;
                cycleIndex < 60;
                cycleIndex++
        ) {

            if (
                    cycleIndex % 10
                            == stem.ordinal()

                            &&

                            cycleIndex % 12
                                    == branch.ordinal()
            ) {

                return SexagenaryCycle.ofIndex(
                        cycleIndex
                );
            }
        }

        throw new IllegalArgumentException(
                "유효하지 않은 천간·지지 조합입니다: "
                        + stem
                        + " / "
                        + branch
        );
    }

    private void validateMonthNumber(
            int monthNumber
    ) {

        if (
                monthNumber < 1
                        || monthNumber > 12
        ) {

            throw new IllegalArgumentException(
                    "월운 월번호는 1~12여야 합니다."
            );
        }
    }
}