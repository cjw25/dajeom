package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TwelveLifeStage;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 11차-1.
 *
 * 십간 기준 12운성 정적 기준표.
 *
 * =================================================
 *
 * 양간 순행:
 *
 * 甲
 * 丙
 * 戊
 * 庚
 * 壬
 *
 * =================================================
 *
 * 음간 역행:
 *
 * 乙
 * 丁
 * 己
 * 辛
 * 癸
 *
 * =================================================
 *
 * 장생 시작점:
 *
 * 甲 -> 亥
 * 乙 -> 午
 *
 * 丙 -> 寅
 * 丁 -> 酉
 *
 * 戊 -> 寅
 * 己 -> 酉
 *
 * 庚 -> 巳
 * 辛 -> 子
 *
 * 壬 -> 申
 * 癸 -> 卯
 *
 * =================================================
 *
 * 중요 정책:
 *
 * 이 Table은 12운성의 정적 Domain Truth만 가진다.
 *
 * 출생시간 미상 처리,
 * 년·월·일·시 위치,
 * 원국 종합해석 등은
 * 이 클래스의 책임이 아니다.
 */
public final class TwelveLifeStageTable {

    private static final
    Map<HeavenlyStem, Map<EarthlyBranch, TwelveLifeStage>>
            TABLE =
            buildTable();

    private TwelveLifeStageTable() {
    }

    /**
     * 일간 + 지지에 해당하는 12운성을 반환한다.
     */
    public static TwelveLifeStage get(

            HeavenlyStem dayMaster,

            EarthlyBranch branch

    ) {

        Objects.requireNonNull(
                dayMaster,
                "12운성 계산에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "12운성 계산에는 지지가 필요합니다."
        );

        Map<EarthlyBranch, TwelveLifeStage> row =
                TABLE.get(
                        dayMaster
                );

        if (
                row == null
        ) {

            throw new IllegalStateException(
                    "12운성 기준표에 일간이 없습니다: "
                            + dayMaster
            );
        }

        TwelveLifeStage stage =
                row.get(
                        branch
                );

        if (
                stage == null
        ) {

            throw new IllegalStateException(
                    "12운성 기준표에 지지가 없습니다. dayMaster="
                            + dayMaster
                            + ", branch="
                            + branch
            );
        }

        return stage;
    }

    /**
     * 특정 천간의 12지 전체 기준표.
     *
     * 반환 Map은 수정할 수 없다.
     */
    public static Map<EarthlyBranch, TwelveLifeStage>
    stagesFor(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        Map<EarthlyBranch, TwelveLifeStage> result =
                TABLE.get(
                        stem
                );

        if (
                result == null
        ) {

            throw new IllegalStateException(
                    "12운성 기준표에 천간이 없습니다: "
                            + stem
            );
        }

        return result;
    }

    /**
     * 특정 천간에서 특정 12운성이
     * 어느 지지에 해당하는지 역조회한다.
     *
     * 예:
     *
     * 甲 + 長生 -> 亥
     * 丁 + 帝旺 -> 巳
     */
    public static EarthlyBranch branchOf(

            HeavenlyStem stem,

            TwelveLifeStage stage

    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        Objects.requireNonNull(
                stage,
                "12운성이 필요합니다."
        );

        return stagesFor(
                stem
        )
                .entrySet()
                .stream()
                .filter(
                        entry ->
                                entry.getValue()
                                        == stage
                )
                .map(
                        Map.Entry
                                ::getKey
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "12운성 역조회에 실패했습니다. stem="
                                                + stem
                                                + ", stage="
                                                + stage
                                )
                );
    }

    /**
     * 해당 천간의 장생지를 반환한다.
     */
    public static EarthlyBranch
    changShengBranchOf(
            HeavenlyStem stem
    ) {

        return branchOf(
                stem,
                TwelveLifeStage.CHANG_SHENG
        );
    }

    /*
     * =================================================
     * 기준표 생성
     * =================================================
     */

    private static
    Map<HeavenlyStem, Map<EarthlyBranch, TwelveLifeStage>>
    buildTable() {

        EnumMap<
                HeavenlyStem,
                Map<EarthlyBranch, TwelveLifeStage>
                > table =
                new EnumMap<>(
                        HeavenlyStem.class
                );

        /*
         * =================================================
         * 甲
         *
         * 長生 亥
         * 순행
         * =================================================
         */

        table.put(
                HeavenlyStem.JIA,
                row(
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 乙
         *
         * 長生 午
         * 역행
         * =================================================
         */

        table.put(
                HeavenlyStem.YI,
                row(
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 丙
         *
         * 長生 寅
         * 순행
         * =================================================
         */

        table.put(
                HeavenlyStem.BING,
                row(
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 丁
         *
         * 長生 酉
         * 역행
         * =================================================
         */

        table.put(
                HeavenlyStem.DING,
                row(
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 戊
         *
         * 丙과 동일.
         * 長生 寅
         * 순행
         * =================================================
         */

        table.put(
                HeavenlyStem.WU,
                row(
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 己
         *
         * 丁과 동일.
         * 長生 酉
         * 역행
         * =================================================
         */

        table.put(
                HeavenlyStem.JI,
                row(
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 庚
         *
         * 長生 巳
         * 순행
         * =================================================
         */

        table.put(
                HeavenlyStem.GENG,
                row(
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 辛
         *
         * 長生 子
         * 역행
         * =================================================
         */

        table.put(
                HeavenlyStem.XIN,
                row(
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 壬
         *
         * 長生 申
         * 순행
         * =================================================
         */

        table.put(
                HeavenlyStem.REN,
                row(
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 癸
         *
         * 長生 卯
         * 역행
         * =================================================
         */

        table.put(
                HeavenlyStem.GUI,
                row(
                        entry(
                                EarthlyBranch.MAO,
                                TwelveLifeStage.CHANG_SHENG
                        ),
                        entry(
                                EarthlyBranch.YIN,
                                TwelveLifeStage.MU_YU
                        ),
                        entry(
                                EarthlyBranch.CHOU,
                                TwelveLifeStage.GUAN_DAI
                        ),
                        entry(
                                EarthlyBranch.ZI,
                                TwelveLifeStage.LIN_GUAN
                        ),
                        entry(
                                EarthlyBranch.HAI,
                                TwelveLifeStage.DI_WANG
                        ),
                        entry(
                                EarthlyBranch.XU,
                                TwelveLifeStage.SHUAI
                        ),
                        entry(
                                EarthlyBranch.YOU,
                                TwelveLifeStage.BING
                        ),
                        entry(
                                EarthlyBranch.SHEN,
                                TwelveLifeStage.SI
                        ),
                        entry(
                                EarthlyBranch.WEI,
                                TwelveLifeStage.MU
                        ),
                        entry(
                                EarthlyBranch.WU,
                                TwelveLifeStage.JUE
                        ),
                        entry(
                                EarthlyBranch.SI,
                                TwelveLifeStage.TAI
                        ),
                        entry(
                                EarthlyBranch.CHEN,
                                TwelveLifeStage.YANG
                        )
                )
        );

        /*
         * =================================================
         * 정적 기준표 자체 검증
         * =================================================
         */

        validateTable(
                table
        );

        return Collections.unmodifiableMap(
                table
        );
    }

    /*
     * =================================================
     * Row
     * =================================================
     */

    @SafeVarargs
    private static Map<EarthlyBranch, TwelveLifeStage> row(
            Map.Entry<EarthlyBranch, TwelveLifeStage>... entries
    ) {

        EnumMap<EarthlyBranch, TwelveLifeStage> row =
                new EnumMap<>(
                        EarthlyBranch.class
                );

        for (
                Map.Entry<EarthlyBranch, TwelveLifeStage> entry
                : entries
        ) {

            Objects.requireNonNull(
                    entry,
                    "12운성 기준표 Entry는 null일 수 없습니다."
            );

            TwelveLifeStage previous =
                    row.put(
                            Objects.requireNonNull(
                                    entry.getKey(),
                                    "12운성 지지가 필요합니다."
                            ),
                            Objects.requireNonNull(
                                    entry.getValue(),
                                    "12운성 상태가 필요합니다."
                            )
                    );

            if (
                    previous != null
            ) {

                throw new IllegalStateException(
                        "12운성 기준표 Row에 동일 지지가 중복되었습니다: "
                                + entry.getKey()
                );
            }
        }

        return Collections.unmodifiableMap(
                row
        );
    }

    private static
    Map.Entry<EarthlyBranch, TwelveLifeStage> entry(

            EarthlyBranch branch,

            TwelveLifeStage stage

    ) {

        return Map.entry(
                branch,
                stage
        );
    }

    /*
     * =================================================
     * Table Validation
     * =================================================
     *
     * 각 천간은 반드시:
     *
     * - 12개 지지를 모두 포함
     * - 12개 운성을 정확히 한 번씩 포함
     *
     * 해야 한다.
     */

    private static void validateTable(
            Map<
                    HeavenlyStem,
                    Map<EarthlyBranch, TwelveLifeStage>
                    > table
    ) {

        if (
                table.size()
                        != HeavenlyStem.values().length
        ) {

            throw new IllegalStateException(
                    "12운성 기준표에 10천간 전체가 등록되어 있지 않습니다."
            );
        }

        for (
                HeavenlyStem stem
                : HeavenlyStem.values()
        ) {

            Map<EarthlyBranch, TwelveLifeStage> row =
                    table.get(
                            stem
                    );

            if (
                    row == null
            ) {

                throw new IllegalStateException(
                        "12운성 기준표에 천간이 누락되었습니다: "
                                + stem
                );
            }

            if (
                    row.size()
                            != EarthlyBranch.values().length
            ) {

                throw new IllegalStateException(
                        "12운성 기준표의 지지 개수가 올바르지 않습니다. stem="
                                + stem
                                + ", size="
                                + row.size()
                );
            }

            EnumSet<EarthlyBranch> branches =
                    EnumSet.copyOf(
                            row.keySet()
                    );

            EnumSet<EarthlyBranch> expectedBranches =
                    EnumSet.allOf(
                            EarthlyBranch.class
                    );

            if (
                    !branches.equals(
                            expectedBranches
                    )
            ) {

                throw new IllegalStateException(
                        "12운성 기준표에 누락되거나 잘못된 지지가 있습니다: "
                                + stem
                );
            }

            EnumSet<TwelveLifeStage> stages =
                    EnumSet.copyOf(
                            row.values()
                    );

            EnumSet<TwelveLifeStage> expectedStages =
                    EnumSet.allOf(
                            TwelveLifeStage.class
                    );

            if (
                    !stages.equals(
                            expectedStages
                    )
            ) {

                throw new IllegalStateException(
                        "12운성 기준표에서 12운성이 정확히 한 번씩 배치되지 않았습니다: "
                                + stem
                );
            }
        }
    }
}