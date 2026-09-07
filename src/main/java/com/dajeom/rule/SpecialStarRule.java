package com.dajeom.rule;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.Ganji;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.SexagenaryCycle;
import com.dajeom.domain.SpecialStarReferenceType;
import com.dajeom.domain.SpecialStarType;

import java.util.List;
import java.util.Objects;

public final class SpecialStarRule {

    private static final List<Ganji> BAEKHO_GANJI =
            List.of(
                    SexagenaryCycle.of(
                            HeavenlyStem.JIA,
                            EarthlyBranch.CHEN
                    ),

                    SexagenaryCycle.of(
                            HeavenlyStem.YI,
                            EarthlyBranch.WEI
                    ),

                    SexagenaryCycle.of(
                            HeavenlyStem.BING,
                            EarthlyBranch.XU
                    ),

                    SexagenaryCycle.of(
                            HeavenlyStem.DING,
                            EarthlyBranch.CHOU
                    ),

                    SexagenaryCycle.of(
                            HeavenlyStem.WU,
                            EarthlyBranch.CHEN
                    ),

                    SexagenaryCycle.of(
                            HeavenlyStem.REN,
                            EarthlyBranch.XU
                    ),

                    SexagenaryCycle.of(
                            HeavenlyStem.GUI,
                            EarthlyBranch.CHOU
                    )
            );

    private SpecialStarRule() {
    }

    // =========================================================
    // 신살 기준 종류
    // =========================================================

    public static SpecialStarReferenceType referenceType(
            SpecialStarType type
    ) {

        Objects.requireNonNull(
                type,
                "신살 종류가 필요합니다."
        );

        return switch (type) {

            case HYEONCHIM ->
                    SpecialStarReferenceType.PILLAR_CHARACTER;

            case BAEKHO ->
                    SpecialStarReferenceType.PILLAR_GANJI;

            case CHEONDEOK_NOBLEMAN,
                 WOLDEOK_NOBLEMAN ->
                    SpecialStarReferenceType.MONTH_BRANCH_CHARACTER;

            default ->
                    SpecialStarReferenceType.DAY_STEM_BRANCH;
        };
    }

    // =========================================================
    // 일간 → 지지 방식
    // =========================================================

    public static List<EarthlyBranch> targetBranches(
            SpecialStarType type,
            HeavenlyStem dayStem
    ) {

        Objects.requireNonNull(
                type,
                "신살 종류가 필요합니다."
        );

        Objects.requireNonNull(
                dayStem,
                "신살 계산 기준 일간이 필요합니다."
        );

        if (referenceType(type)
                != SpecialStarReferenceType.DAY_STEM_BRANCH) {

            throw new IllegalArgumentException(
                    type.korean()
                            + "은 일간→지지 방식의 신살이 아닙니다."
            );
        }

        return switch (type) {

            case MUNCHANG_NOBLEMAN ->
                    List.of(
                            munchang(dayStem)
                    );

            case MUNGOK_NOBLEMAN ->
                    List.of(
                            mungok(dayStem)
                    );

            case HAKDANG_NOBLEMAN ->
                    List.of(
                            hakdang(dayStem)
                    );

            case CHEONJU_NOBLEMAN ->
                    List.of(
                            cheonju(dayStem)
                    );

            case AMROK ->
                    List.of(
                            amrok(dayStem)
                    );

            case TAEGEUK_NOBLEMAN ->
                    taegeuk(dayStem);

            case BOKSEONG_NOBLEMAN ->
                    List.of(
                            bokseong(dayStem)
                    );

            case GWANGWI_HAKGWAN ->
                    List.of(
                            gwangwiHakgwan(dayStem)
                    );

            case GEUMYEO ->
                    List.of(
                            geumyeo(dayStem)
                    );

            case BIIN ->
                    List.of(
                            biin(dayStem)
                    );

            case HYEONCHIM,
                 BAEKHO,
                 CHEONDEOK_NOBLEMAN,
                 WOLDEOK_NOBLEMAN ->
                    throw new IllegalArgumentException(
                            type.korean()
                                    + "은 일간→지지 방식이 아닙니다."
                    );
        };
    }

    // =========================================================
    // 문창귀인
    // =========================================================

    private static EarthlyBranch munchang(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA ->
                    EarthlyBranch.SI;

            case YI ->
                    EarthlyBranch.WU;

            case BING, WU ->
                    EarthlyBranch.SHEN;

            case DING, JI ->
                    EarthlyBranch.YOU;

            case GENG ->
                    EarthlyBranch.HAI;

            case XIN ->
                    EarthlyBranch.ZI;

            case REN ->
                    EarthlyBranch.YIN;

            case GUI ->
                    EarthlyBranch.MAO;
        };
    }

    // =========================================================
    // 문곡귀인
    // =========================================================

    private static EarthlyBranch mungok(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA ->
                    EarthlyBranch.HAI;

            case YI ->
                    EarthlyBranch.ZI;

            case BING, WU ->
                    EarthlyBranch.YIN;

            case DING, JI ->
                    EarthlyBranch.MAO;

            case GENG ->
                    EarthlyBranch.SI;

            case XIN ->
                    EarthlyBranch.WU;

            case REN ->
                    EarthlyBranch.SHEN;

            case GUI ->
                    EarthlyBranch.YOU;
        };
    }

    // =========================================================
    // 학당귀인
    // =========================================================

    private static EarthlyBranch hakdang(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA ->
                    EarthlyBranch.HAI;

            case YI ->
                    EarthlyBranch.WU;

            case BING, WU ->
                    EarthlyBranch.YIN;

            case DING, JI ->
                    EarthlyBranch.YOU;

            case GENG ->
                    EarthlyBranch.SI;

            case XIN ->
                    EarthlyBranch.ZI;

            case REN ->
                    EarthlyBranch.SHEN;

            case GUI ->
                    EarthlyBranch.MAO;
        };
    }

    // =========================================================
    // 천주귀인
    // =========================================================

    private static EarthlyBranch cheonju(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA ->
                    EarthlyBranch.SI;

            case YI ->
                    EarthlyBranch.WU;

            case BING ->
                    EarthlyBranch.SI;

            case DING ->
                    EarthlyBranch.WU;

            case WU ->
                    EarthlyBranch.SHEN;

            case JI ->
                    EarthlyBranch.YOU;

            case GENG ->
                    EarthlyBranch.HAI;

            case XIN ->
                    EarthlyBranch.ZI;

            case REN ->
                    EarthlyBranch.YIN;

            case GUI ->
                    EarthlyBranch.MAO;
        };
    }

    // =========================================================
    // 암록
    // =========================================================

    private static EarthlyBranch amrok(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA ->
                    EarthlyBranch.HAI;

            case YI ->
                    EarthlyBranch.XU;

            case BING, WU ->
                    EarthlyBranch.SHEN;

            case DING, JI ->
                    EarthlyBranch.WEI;

            case GENG ->
                    EarthlyBranch.SI;

            case XIN ->
                    EarthlyBranch.CHEN;

            case REN ->
                    EarthlyBranch.YIN;

            case GUI ->
                    EarthlyBranch.CHOU;
        };
    }

    // =========================================================
    // 태극귀인
    // =========================================================

    private static List<EarthlyBranch> taegeuk(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA, YI ->
                    List.of(
                            EarthlyBranch.ZI,
                            EarthlyBranch.WU
                    );

            case BING, DING ->
                    List.of(
                            EarthlyBranch.MAO,
                            EarthlyBranch.YOU
                    );

            case WU, JI ->
                    List.of(
                            EarthlyBranch.CHEN,
                            EarthlyBranch.XU,
                            EarthlyBranch.CHOU,
                            EarthlyBranch.WEI
                    );

            case GENG, XIN ->
                    List.of(
                            EarthlyBranch.YIN,
                            EarthlyBranch.HAI
                    );

            case REN, GUI ->
                    List.of(
                            EarthlyBranch.SI,
                            EarthlyBranch.SHEN
                    );
        };
    }

    // =========================================================
    // 복성귀인
    // =========================================================

    private static EarthlyBranch bokseong(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA ->
                    EarthlyBranch.YIN;

            case YI ->
                    EarthlyBranch.CHOU;

            case BING ->
                    EarthlyBranch.ZI;

            case DING ->
                    EarthlyBranch.YOU;

            case WU ->
                    EarthlyBranch.SHEN;

            case JI ->
                    EarthlyBranch.WEI;

            case GENG ->
                    EarthlyBranch.WU;

            case XIN ->
                    EarthlyBranch.SI;

            case REN ->
                    EarthlyBranch.CHEN;

            case GUI ->
                    EarthlyBranch.MAO;
        };
    }

    // =========================================================
    // 관귀학관
    // =========================================================

    private static EarthlyBranch gwangwiHakgwan(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA, YI ->
                    EarthlyBranch.SI;

            case BING, DING ->
                    EarthlyBranch.SHEN;

            case WU, JI ->
                    EarthlyBranch.HAI;

            case GENG, XIN ->
                    EarthlyBranch.YIN;

            case REN, GUI ->
                    EarthlyBranch.SHEN;
        };
    }

    // =========================================================
    // 금여
    // =========================================================

    private static EarthlyBranch geumyeo(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA ->
                    EarthlyBranch.CHEN;

            case YI ->
                    EarthlyBranch.SI;

            case BING, WU ->
                    EarthlyBranch.WEI;

            case DING, JI ->
                    EarthlyBranch.SHEN;

            case GENG ->
                    EarthlyBranch.XU;

            case XIN ->
                    EarthlyBranch.HAI;

            case REN ->
                    EarthlyBranch.CHOU;

            case GUI ->
                    EarthlyBranch.YIN;
        };
    }

    // =========================================================
    // 비인살
    // =========================================================

    private static EarthlyBranch biin(
            HeavenlyStem dayStem
    ) {

        return switch (dayStem) {

            case JIA ->
                    EarthlyBranch.YOU;

            case YI ->
                    EarthlyBranch.XU;

            case BING, WU ->
                    EarthlyBranch.ZI;

            case DING, JI ->
                    EarthlyBranch.CHOU;

            case GENG ->
                    EarthlyBranch.MAO;

            case XIN ->
                    EarthlyBranch.CHEN;

            case REN ->
                    EarthlyBranch.WU;

            case GUI ->
                    EarthlyBranch.WEI;
        };
    }

    // =========================================================
    // 현침살
    // =========================================================

    public static boolean isHyeonchim(
            Pillar pillar
    ) {

        Objects.requireNonNull(
                pillar,
                "현침살 계산 기둥이 필요합니다."
        );

        return isHyeonchimStem(
                pillar.stem()
        )
                || isHyeonchimBranch(
                pillar.branch()
        );
    }

    public static boolean isHyeonchimStem(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(stem);

        return stem == HeavenlyStem.JIA
                || stem == HeavenlyStem.XIN;
    }

    public static boolean isHyeonchimBranch(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(branch);

        return branch == EarthlyBranch.MAO
                || branch == EarthlyBranch.WU
                || branch == EarthlyBranch.SHEN;
    }

    public static String hyeonchimEvidence(
            Pillar pillar
    ) {

        Objects.requireNonNull(pillar);

        boolean stemMatched =
                isHyeonchimStem(
                        pillar.stem()
                );

        boolean branchMatched =
                isHyeonchimBranch(
                        pillar.branch()
                );

        if (stemMatched
                && branchMatched) {

            return "STEM="
                    + pillar.stem().hanja()
                    + ", BRANCH="
                    + pillar.branch().hanja();
        }

        if (stemMatched) {

            return "STEM="
                    + pillar.stem().hanja();
        }

        if (branchMatched) {

            return "BRANCH="
                    + pillar.branch().hanja();
        }

        throw new IllegalArgumentException(
                pillar.ganji().hanja()
                        + "은 현침살 대상이 아닙니다."
        );
    }

    // =========================================================
    // 백호살
    // =========================================================

    public static boolean isBaekho(
            Ganji ganji
    ) {

        Objects.requireNonNull(
                ganji,
                "백호살 계산 간지가 필요합니다."
        );

        return BAEKHO_GANJI.contains(
                ganji
        );
    }

    public static List<Ganji> baekhoGanjis() {
        return BAEKHO_GANJI;
    }

    // =========================================================
    // 천덕귀인 / 월덕귀인
    // =========================================================

    public static MonthBranchCharacterTarget monthBranchTarget(
            SpecialStarType type,
            EarthlyBranch monthBranch
    ) {

        Objects.requireNonNull(
                type,
                "신살 종류가 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "기준 월지가 필요합니다."
        );

        return switch (type) {

            case CHEONDEOK_NOBLEMAN ->
                    cheondeok(
                            monthBranch
                    );

            case WOLDEOK_NOBLEMAN ->
                    woldeok(
                            monthBranch
                    );

            default ->
                    throw new IllegalArgumentException(
                            type.korean()
                                    + "은 월지 기준 귀인이 아닙니다."
                    );
        };
    }

    /**
     * 천덕귀인.
     *
     * 월지 → 목표 글자
     *
     * 寅 → 丁
     * 卯 → 申
     * 辰 → 壬
     * 巳 → 辛
     * 午 → 亥
     * 未 → 甲
     * 申 → 癸
     * 酉 → 寅
     * 戌 → 丙
     * 亥 → 乙
     * 子 → 巳
     * 丑 → 庚
     *
     * 일부 월은 천간,
     * 일부 월은 지지를 찾는다.
     */
    private static MonthBranchCharacterTarget cheondeok(
            EarthlyBranch monthBranch
    ) {

        return switch (monthBranch) {

            case YIN ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.DING
                    );

            case MAO ->
                    MonthBranchCharacterTarget.branch(
                            EarthlyBranch.SHEN
                    );

            case CHEN ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.REN
                    );

            case SI ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.XIN
                    );

            case WU ->
                    MonthBranchCharacterTarget.branch(
                            EarthlyBranch.HAI
                    );

            case WEI ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.JIA
                    );

            case SHEN ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.GUI
                    );

            case YOU ->
                    MonthBranchCharacterTarget.branch(
                            EarthlyBranch.YIN
                    );

            case XU ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.BING
                    );

            case HAI ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.YI
                    );

            case ZI ->
                    MonthBranchCharacterTarget.branch(
                            EarthlyBranch.SI
                    );

            case CHOU ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.GENG
                    );
        };
    }

    /**
     * 월덕귀인.
     *
     * 寅午戌 → 丙
     * 亥卯未 → 甲
     * 申子辰 → 壬
     * 巳酉丑 → 庚
     */
    private static MonthBranchCharacterTarget woldeok(
            EarthlyBranch monthBranch
    ) {

        return switch (monthBranch) {

            case YIN, WU, XU ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.BING
                    );

            case HAI, MAO, WEI ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.JIA
                    );

            case SHEN, ZI, CHEN ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.REN
                    );

            case SI, YOU, CHOU ->
                    MonthBranchCharacterTarget.stem(
                            HeavenlyStem.GENG
                    );
        };
    }

    /**
     * 월지 기준 귀인의 목표 글자.
     *
     * 천간 또는 지지 중 정확히 하나만 가진다.
     */
    public record MonthBranchCharacterTarget(
            HeavenlyStem stem,
            EarthlyBranch branch
    ) {

        public MonthBranchCharacterTarget {

            boolean hasStem =
                    stem != null;

            boolean hasBranch =
                    branch != null;

            if (hasStem
                    == hasBranch) {

                throw new IllegalArgumentException(
                        "목표 천간 또는 목표 지지 중 정확히 하나만 필요합니다."
                );
            }
        }

        public static MonthBranchCharacterTarget stem(
                HeavenlyStem stem
        ) {

            return new MonthBranchCharacterTarget(
                    Objects.requireNonNull(stem),
                    null
            );
        }

        public static MonthBranchCharacterTarget branch(
                EarthlyBranch branch
        ) {

            return new MonthBranchCharacterTarget(
                    null,
                    Objects.requireNonNull(branch)
            );
        }

        public boolean isStemTarget() {
            return stem != null;
        }

        public boolean isBranchTarget() {
            return branch != null;
        }

        public boolean matches(
                Pillar pillar
        ) {

            Objects.requireNonNull(pillar);

            if (isStemTarget()) {

                return pillar.stem()
                        == stem;
            }

            return pillar.branch()
                    == branch;
        }
    }
}