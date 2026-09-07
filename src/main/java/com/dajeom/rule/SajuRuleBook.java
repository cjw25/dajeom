package com.dajeom.rule;

import com.dajeom.domain.BranchRelationType;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.Ganji;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStem;
import com.dajeom.domain.SexagenaryCycle;
import com.dajeom.domain.StemRelationType;
import com.dajeom.domain.YinYang;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SajuRuleBook {

    private static final int MIN_SAJU_MONTH = 1;
    private static final int MAX_SAJU_MONTH = 12;

    private SajuRuleBook() {
    }

    // =========================================================
    // 년주
    // =========================================================

    public static int yearCycleIndex(
            int effectiveSajuYear
    ) {

        return Math.floorMod(
                effectiveSajuYear - 4,
                SexagenaryCycle.CYCLE_SIZE
        );
    }

    public static Ganji yearGanji(
            int effectiveSajuYear
    ) {

        return SexagenaryCycle.ofIndex(
                yearCycleIndex(
                        effectiveSajuYear
                )
        );
    }

    // =========================================================
    // 월주
    // =========================================================

    public static EarthlyBranch monthBranch(
            int sajuMonthNumber
    ) {

        validateSajuMonthNumber(
                sajuMonthNumber
        );

        return EarthlyBranch.ofIndex(
                Math.floorMod(
                        sajuMonthNumber + 1,
                        12
                )
        );
    }

    public static HeavenlyStem firstMonthStem(
            HeavenlyStem yearStem
    ) {

        Objects.requireNonNull(
                yearStem,
                "년간이 필요합니다."
        );

        int stemIndex =
                Math.floorMod(
                        (yearStem.index() % 5) * 2 + 2,
                        10
                );

        return HeavenlyStem.ofIndex(
                stemIndex
        );
    }

    public static HeavenlyStem monthStem(
            HeavenlyStem yearStem,
            int sajuMonthNumber
    ) {

        Objects.requireNonNull(
                yearStem,
                "년간이 필요합니다."
        );

        validateSajuMonthNumber(
                sajuMonthNumber
        );

        HeavenlyStem first =
                firstMonthStem(
                        yearStem
                );

        return HeavenlyStem.ofIndex(
                first.index()
                        + sajuMonthNumber
                        - 1
        );
    }

    public static Ganji monthGanji(
            HeavenlyStem yearStem,
            int sajuMonthNumber
    ) {

        return SexagenaryCycle.of(
                monthStem(
                        yearStem,
                        sajuMonthNumber
                ),
                monthBranch(
                        sajuMonthNumber
                )
        );
    }

    // =========================================================
    // 시주
    // =========================================================

    public static HeavenlyStem firstHourStem(
            HeavenlyStem dayStem
    ) {

        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        return HeavenlyStem.ofIndex(
                Math.floorMod(
                        (dayStem.index() % 5) * 2,
                        10
                )
        );
    }

    public static HeavenlyStem hourStem(
            HeavenlyStem dayStem,
            EarthlyBranch hourBranch
    ) {

        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                hourBranch,
                "시지가 필요합니다."
        );

        HeavenlyStem first =
                firstHourStem(
                        dayStem
                );

        return HeavenlyStem.ofIndex(
                first.index()
                        + hourBranch.index()
        );
    }

    public static Ganji hourGanji(
            HeavenlyStem dayStem,
            EarthlyBranch hourBranch
    ) {

        return SexagenaryCycle.of(
                hourStem(
                        dayStem,
                        hourBranch
                ),
                hourBranch
        );
    }

    // =========================================================
    // 오행
    // =========================================================

    public static FiveElement generatedElement(
            FiveElement source
    ) {

        Objects.requireNonNull(
                source,
                "기준 오행이 필요합니다."
        );

        return switch (source) {

            case WOOD -> FiveElement.FIRE;
            case FIRE -> FiveElement.EARTH;
            case EARTH -> FiveElement.METAL;
            case METAL -> FiveElement.WATER;
            case WATER -> FiveElement.WOOD;
        };
    }

    public static boolean generates(
            FiveElement source,
            FiveElement target
    ) {

        Objects.requireNonNull(source);
        Objects.requireNonNull(target);

        return generatedElement(source)
                == target;
    }

    public static FiveElement controlledElement(
            FiveElement source
    ) {

        Objects.requireNonNull(
                source,
                "기준 오행이 필요합니다."
        );

        return switch (source) {

            case WOOD -> FiveElement.EARTH;
            case FIRE -> FiveElement.METAL;
            case EARTH -> FiveElement.WATER;
            case METAL -> FiveElement.WOOD;
            case WATER -> FiveElement.FIRE;
        };
    }

    public static boolean controls(
            FiveElement source,
            FiveElement target
    ) {

        Objects.requireNonNull(source);
        Objects.requireNonNull(target);

        return controlledElement(source)
                == target;
    }

    // =========================================================
    // 지장간
    // =========================================================

    public static List<HiddenStem> hiddenStems(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        return switch (branch) {

            case ZI ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.REN
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.GUI
                            )
                    );

            case CHOU ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.GUI
                            ),
                            hidden(
                                    HiddenStem.Position.MIDDLE,
                                    HeavenlyStem.XIN
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.JI
                            )
                    );

            case YIN ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.WU
                            ),
                            hidden(
                                    HiddenStem.Position.MIDDLE,
                                    HeavenlyStem.BING
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.JIA
                            )
                    );

            case MAO ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.JIA
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.YI
                            )
                    );

            case CHEN ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.YI
                            ),
                            hidden(
                                    HiddenStem.Position.MIDDLE,
                                    HeavenlyStem.GUI
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.WU
                            )
                    );

            case SI ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.WU
                            ),
                            hidden(
                                    HiddenStem.Position.MIDDLE,
                                    HeavenlyStem.GENG
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.BING
                            )
                    );

            case WU ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.BING
                            ),
                            hidden(
                                    HiddenStem.Position.MIDDLE,
                                    HeavenlyStem.JI
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.DING
                            )
                    );

            case WEI ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.DING
                            ),
                            hidden(
                                    HiddenStem.Position.MIDDLE,
                                    HeavenlyStem.YI
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.JI
                            )
                    );

            case SHEN ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.WU
                            ),
                            hidden(
                                    HiddenStem.Position.MIDDLE,
                                    HeavenlyStem.REN
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.GENG
                            )
                    );

            case YOU ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.GENG
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.XIN
                            )
                    );

            case XU ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.XIN
                            ),
                            hidden(
                                    HiddenStem.Position.MIDDLE,
                                    HeavenlyStem.DING
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.WU
                            )
                    );

            case HAI ->
                    List.of(
                            hidden(
                                    HiddenStem.Position.EARLY,
                                    HeavenlyStem.WU
                            ),
                            hidden(
                                    HiddenStem.Position.MIDDLE,
                                    HeavenlyStem.JIA
                            ),
                            hidden(
                                    HiddenStem.Position.MAIN,
                                    HeavenlyStem.REN
                            )
                    );
        };
    }

    public static HiddenStem mainHiddenStem(
            EarthlyBranch branch
    ) {

        return hiddenStems(branch)
                .stream()
                .filter(value ->
                        value.position()
                                == HiddenStem.Position.MAIN
                )
                .findFirst()
                .orElseThrow();
    }

    private static HiddenStem hidden(
            HiddenStem.Position position,
            HeavenlyStem stem
    ) {

        return new HiddenStem(
                position,
                stem
        );
    }

    // =========================================================
    // 12운성
    // =========================================================

    public static EarthlyBranch twelveStageStartBranch(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(stem);

        return switch (stem) {

            case JIA -> EarthlyBranch.HAI;
            case YI -> EarthlyBranch.WU;
            case BING -> EarthlyBranch.YIN;
            case DING -> EarthlyBranch.YOU;
            case WU -> EarthlyBranch.YIN;
            case JI -> EarthlyBranch.YOU;
            case GENG -> EarthlyBranch.SI;
            case XIN -> EarthlyBranch.ZI;
            case REN -> EarthlyBranch.SHEN;
            case GUI -> EarthlyBranch.MAO;
        };
    }

    public static boolean isTwelveStageForward(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(stem);

        return stem.yinYang()
                == YinYang.YANG;
    }

    // =========================================================
    // 천간 관계
    // =========================================================

    public static List<StemRelationType> stemRelations(
            HeavenlyStem a,
            HeavenlyStem b
    ) {

        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        if (a == b) {
            return List.of();
        }

        List<StemRelationType> result =
                new ArrayList<>();

        if (isStemCombination(a, b)) {

            result.add(
                    StemRelationType.COMBINATION
            );
        }

        if (isStemClash(a, b)) {

            result.add(
                    StemRelationType.CLASH
            );
        }

        return List.copyOf(result);
    }

    /**
     * 천간 오합.
     *
     * 甲己
     * 乙庚
     * 丙辛
     * 丁壬
     * 戊癸
     */
    public static boolean isStemCombination(
            HeavenlyStem a,
            HeavenlyStem b
    ) {

        return pair(
                a, b,
                HeavenlyStem.JIA,
                HeavenlyStem.JI
        )
                || pair(
                a, b,
                HeavenlyStem.YI,
                HeavenlyStem.GENG
        )
                || pair(
                a, b,
                HeavenlyStem.BING,
                HeavenlyStem.XIN
        )
                || pair(
                a, b,
                HeavenlyStem.DING,
                HeavenlyStem.REN
        )
                || pair(
                a, b,
                HeavenlyStem.WU,
                HeavenlyStem.GUI
        );
    }

    /**
     * 천간충.
     *
     * 甲庚
     * 乙辛
     * 丙壬
     * 丁癸
     */
    public static boolean isStemClash(
            HeavenlyStem a,
            HeavenlyStem b
    ) {

        return pair(
                a, b,
                HeavenlyStem.JIA,
                HeavenlyStem.GENG
        )
                || pair(
                a, b,
                HeavenlyStem.YI,
                HeavenlyStem.XIN
        )
                || pair(
                a, b,
                HeavenlyStem.BING,
                HeavenlyStem.REN
        )
                || pair(
                a, b,
                HeavenlyStem.DING,
                HeavenlyStem.GUI
        );
    }

    // =========================================================
    // 지지 관계
    // =========================================================

    public static List<BranchRelationType> branchRelations(
            EarthlyBranch a,
            EarthlyBranch b
    ) {

        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        List<BranchRelationType> result =
                new ArrayList<>();

        /*
         * 같은 지지끼리는 일반 pair 관계 대신
         * 자형 여부만 본다.
         */
        if (a == b) {

            if (isSelfPunishment(a)) {

                result.add(
                        BranchRelationType.SELF_PUNISHMENT
                );
            }

            return List.copyOf(result);
        }

        if (isSixCombination(a, b)) {
            result.add(
                    BranchRelationType.SIX_COMBINATION
            );
        }

        if (isBranchClash(a, b)) {
            result.add(
                    BranchRelationType.CLASH
            );
        }

        if (isPunishment(a, b)) {
            result.add(
                    BranchRelationType.PUNISHMENT
            );
        }

        if (isBreak(a, b)) {
            result.add(
                    BranchRelationType.BREAK
            );
        }

        if (isHarm(a, b)) {
            result.add(
                    BranchRelationType.HARM
            );
        }

        if (isThreeHarmonyPair(a, b)) {
            result.add(
                    BranchRelationType.THREE_HARMONY
            );
        }

        if (isDirectionalHarmonyPair(a, b)) {
            result.add(
                    BranchRelationType.DIRECTIONAL_HARMONY
            );
        }

        return List.copyOf(result);
    }

    /**
     * 육합.
     *
     * 子丑
     * 寅亥
     * 卯戌
     * 辰酉
     * 巳申
     * 午未
     */
    public static boolean isSixCombination(
            EarthlyBranch a,
            EarthlyBranch b
    ) {

        return pair(
                a, b,
                EarthlyBranch.ZI,
                EarthlyBranch.CHOU
        )
                || pair(
                a, b,
                EarthlyBranch.YIN,
                EarthlyBranch.HAI
        )
                || pair(
                a, b,
                EarthlyBranch.MAO,
                EarthlyBranch.XU
        )
                || pair(
                a, b,
                EarthlyBranch.CHEN,
                EarthlyBranch.YOU
        )
                || pair(
                a, b,
                EarthlyBranch.SI,
                EarthlyBranch.SHEN
        )
                || pair(
                a, b,
                EarthlyBranch.WU,
                EarthlyBranch.WEI
        );
    }

    /**
     * 육충.
     */
    public static boolean isBranchClash(
            EarthlyBranch a,
            EarthlyBranch b
    ) {

        return pair(
                a, b,
                EarthlyBranch.ZI,
                EarthlyBranch.WU
        )
                || pair(
                a, b,
                EarthlyBranch.CHOU,
                EarthlyBranch.WEI
        )
                || pair(
                a, b,
                EarthlyBranch.YIN,
                EarthlyBranch.SHEN
        )
                || pair(
                a, b,
                EarthlyBranch.MAO,
                EarthlyBranch.YOU
        )
                || pair(
                a, b,
                EarthlyBranch.CHEN,
                EarthlyBranch.XU
        )
                || pair(
                a, b,
                EarthlyBranch.SI,
                EarthlyBranch.HAI
        );
    }

    /**
     * 형.
     *
     * 子卯
     *
     * 寅巳申
     * → 寅巳 / 巳申 / 寅申
     *
     * 丑戌未
     * → 丑戌 / 戌未 / 丑未
     */
    public static boolean isPunishment(
            EarthlyBranch a,
            EarthlyBranch b
    ) {

        return pair(
                a, b,
                EarthlyBranch.ZI,
                EarthlyBranch.MAO
        )
                || inSameGroup(
                a, b,
                EarthlyBranch.YIN,
                EarthlyBranch.SI,
                EarthlyBranch.SHEN
        )
                || inSameGroup(
                a, b,
                EarthlyBranch.CHOU,
                EarthlyBranch.XU,
                EarthlyBranch.WEI
        );
    }

    /**
     * 자형.
     *
     * 辰辰
     * 午午
     * 酉酉
     * 亥亥
     */
    public static boolean isSelfPunishment(
            EarthlyBranch branch
    ) {

        return branch
                == EarthlyBranch.CHEN
                || branch
                == EarthlyBranch.WU
                || branch
                == EarthlyBranch.YOU
                || branch
                == EarthlyBranch.HAI;
    }

    /**
     * 파.
     *
     * 子酉
     * 丑辰
     * 寅亥
     * 卯午
     * 巳申
     * 未戌
     */
    public static boolean isBreak(
            EarthlyBranch a,
            EarthlyBranch b
    ) {

        return pair(
                a, b,
                EarthlyBranch.ZI,
                EarthlyBranch.YOU
        )
                || pair(
                a, b,
                EarthlyBranch.CHOU,
                EarthlyBranch.CHEN
        )
                || pair(
                a, b,
                EarthlyBranch.YIN,
                EarthlyBranch.HAI
        )
                || pair(
                a, b,
                EarthlyBranch.MAO,
                EarthlyBranch.WU
        )
                || pair(
                a, b,
                EarthlyBranch.SI,
                EarthlyBranch.SHEN
        )
                || pair(
                a, b,
                EarthlyBranch.WEI,
                EarthlyBranch.XU
        );
    }

    /**
     * 해.
     *
     * 子未
     * 丑午
     * 寅巳
     * 卯辰
     * 申亥
     * 酉戌
     */
    public static boolean isHarm(
            EarthlyBranch a,
            EarthlyBranch b
    ) {

        return pair(
                a, b,
                EarthlyBranch.ZI,
                EarthlyBranch.WEI
        )
                || pair(
                a, b,
                EarthlyBranch.CHOU,
                EarthlyBranch.WU
        )
                || pair(
                a, b,
                EarthlyBranch.YIN,
                EarthlyBranch.SI
        )
                || pair(
                a, b,
                EarthlyBranch.MAO,
                EarthlyBranch.CHEN
        )
                || pair(
                a, b,
                EarthlyBranch.SHEN,
                EarthlyBranch.HAI
        )
                || pair(
                a, b,
                EarthlyBranch.YOU,
                EarthlyBranch.XU
        );
    }

    /**
     * 삼합 그룹 내 두 글자인지 판정.
     *
     * 申子辰
     * 亥卯未
     * 寅午戌
     * 巳酉丑
     *
     * 여기서는 천을귀인 화면처럼
     * 두 글자만 존재해도 삼합 관계 근거를 남긴다.
     *
     * 나중에 3글자가 모두 존재하는 경우는
     * 완전 삼합 여부를 별도 계산한다.
     */
    public static boolean isThreeHarmonyPair(
            EarthlyBranch a,
            EarthlyBranch b
    ) {

        return inSameGroup(
                a, b,
                EarthlyBranch.SHEN,
                EarthlyBranch.ZI,
                EarthlyBranch.CHEN
        )
                || inSameGroup(
                a, b,
                EarthlyBranch.HAI,
                EarthlyBranch.MAO,
                EarthlyBranch.WEI
        )
                || inSameGroup(
                a, b,
                EarthlyBranch.YIN,
                EarthlyBranch.WU,
                EarthlyBranch.XU
        )
                || inSameGroup(
                a, b,
                EarthlyBranch.SI,
                EarthlyBranch.YOU,
                EarthlyBranch.CHOU
        );
    }

    /**
     * 방합 그룹 내 두 글자인지 판정.
     *
     * 寅卯辰
     * 巳午未
     * 申酉戌
     * 亥子丑
     */
    public static boolean isDirectionalHarmonyPair(
            EarthlyBranch a,
            EarthlyBranch b
    ) {

        return inSameGroup(
                a, b,
                EarthlyBranch.YIN,
                EarthlyBranch.MAO,
                EarthlyBranch.CHEN
        )
                || inSameGroup(
                a, b,
                EarthlyBranch.SI,
                EarthlyBranch.WU,
                EarthlyBranch.WEI
        )
                || inSameGroup(
                a, b,
                EarthlyBranch.SHEN,
                EarthlyBranch.YOU,
                EarthlyBranch.XU
        )
                || inSameGroup(
                a, b,
                EarthlyBranch.HAI,
                EarthlyBranch.ZI,
                EarthlyBranch.CHOU
        );
    }

    // =========================================================
    // 공통 helper
    // =========================================================

    private static boolean pair(
            HeavenlyStem a,
            HeavenlyStem b,
            HeavenlyStem x,
            HeavenlyStem y
    ) {

        return (a == x && b == y)
                || (a == y && b == x);
    }

    private static boolean pair(
            EarthlyBranch a,
            EarthlyBranch b,
            EarthlyBranch x,
            EarthlyBranch y
    ) {

        return (a == x && b == y)
                || (a == y && b == x);
    }

    private static boolean inSameGroup(
            EarthlyBranch a,
            EarthlyBranch b,
            EarthlyBranch x,
            EarthlyBranch y,
            EarthlyBranch z
    ) {

        if (a == b) {
            return false;
        }

        boolean aIn =
                a == x
                        || a == y
                        || a == z;

        boolean bIn =
                b == x
                        || b == y
                        || b == z;

        return aIn && bIn;
    }

    private static void validateSajuMonthNumber(
            int sajuMonthNumber
    ) {

        if (sajuMonthNumber < MIN_SAJU_MONTH
                || sajuMonthNumber > MAX_SAJU_MONTH) {

            throw new IllegalArgumentException(
                    "사주 월 번호는 1~12 사이여야 합니다. sajuMonthNumber="
                            + sajuMonthNumber
            );
        }
    }
}