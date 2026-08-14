package com.dajeom.constant;

import com.dajeom.model.HiddenStem;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 12지지의 지장간 테이블.
 *
 * 지지 index:
 *
 * 0  子
 * 1  丑
 * 2  寅
 * 3  卯
 * 4  辰
 * 5  巳
 * 6  午
 * 7  未
 * 8  申
 * 9  酉
 * 10 戌
 * 11 亥
 *
 * 천간 index:
 *
 * 0  甲
 * 1  乙
 * 2  丙
 * 3  丁
 * 4  戊
 * 5  己
 * 6  庚
 * 7  辛
 * 8  壬
 * 9  癸
 *
 * 다점 정책
 *
 * 지장간 구성 자체와
 * 상대 세력 가중치를 함께 저장한다.
 *
 * weight는 이후
 *
 * 통근
 * 득지
 * 오행 세력
 * 십성 세력
 *
 * 계산에 사용한다.
 */
public final class HiddenStemTable {

    private static final Map<EarthlyBranch, List<HiddenStem>>
            TABLE =
            createTable();

    private HiddenStemTable() {
    }

    public static List<HiddenStem> get(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        List<HiddenStem> result =
                TABLE.get(
                        branch
                );

        if (
                result == null
        ) {

            throw new IllegalArgumentException(
                    "지장간 테이블에 등록되지 않은 지지입니다: "
                            + branch
            );
        }

        return result;
    }

    /**
     * 정기만 가져온다.
     */
    public static HiddenStem main(
            EarthlyBranch branch
    ) {

        return get(
                branch
        )
                .stream()
                .filter(
                        HiddenStem::main
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "정기가 없는 지지입니다: "
                                        + branch
                        )
                );
    }

    /**
     * 중기가 존재할 경우 반환.
     */
    public static Optional<HiddenStem> middle(
            EarthlyBranch branch
    ) {

        return get(
                branch
        )
                .stream()
                .filter(
                        HiddenStem::middle
                )
                .findFirst();
    }

    /**
     * 여기가 존재할 경우 반환.
     */
    public static Optional<HiddenStem> residual(
            EarthlyBranch branch
    ) {

        return get(
                branch
        )
                .stream()
                .filter(
                        HiddenStem::residual
                )
                .findFirst();
    }

    /**
     * 특정 천간이 해당 지지 내부에
     * 뿌리를 두는지 확인.
     *
     * 다음 통근 계산에서 사용한다.
     */
    public static boolean contains(
            EarthlyBranch branch,
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        return get(
                branch
        )
                .stream()
                .anyMatch(hiddenStem ->
                        hiddenStem.stem()
                                == stem
                );
    }

    /**
     * 특정 천간의 지장간 정보를 반환한다.
     */
    public static Optional<HiddenStem> find(
            EarthlyBranch branch,
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        return get(
                branch
        )
                .stream()
                .filter(hiddenStem ->
                        hiddenStem.stem()
                                == stem
                )
                .findFirst();
    }

    public static Map<EarthlyBranch, List<HiddenStem>>
    all() {

        return TABLE;
    }

    private static Map<EarthlyBranch, List<HiddenStem>>
    createTable() {

        Map<EarthlyBranch, List<HiddenStem>> table =
                new LinkedHashMap<>();

        /*
         * 子
         *
         * 癸
         */
        table.put(
                branch(0),
                List.of(
                        hiddenStem(
                                9,
                                HiddenStemRole.MAIN,
                                100
                        )
                )
        );

        /*
         * 丑
         *
         * 己 정기
         * 癸 중기
         * 辛 여기
         */
        table.put(
                branch(1),
                List.of(
                        hiddenStem(
                                5,
                                HiddenStemRole.MAIN,
                                60
                        ),
                        hiddenStem(
                                9,
                                HiddenStemRole.MIDDLE,
                                30
                        ),
                        hiddenStem(
                                7,
                                HiddenStemRole.RESIDUAL,
                                10
                        )
                )
        );

        /*
         * 寅
         *
         * 甲 정기
         * 丙 중기
         * 戊 여기
         */
        table.put(
                branch(2),
                List.of(
                        hiddenStem(
                                0,
                                HiddenStemRole.MAIN,
                                60
                        ),
                        hiddenStem(
                                2,
                                HiddenStemRole.MIDDLE,
                                25
                        ),
                        hiddenStem(
                                4,
                                HiddenStemRole.RESIDUAL,
                                15
                        )
                )
        );

        /*
         * 卯
         *
         * 乙
         */
        table.put(
                branch(3),
                List.of(
                        hiddenStem(
                                1,
                                HiddenStemRole.MAIN,
                                100
                        )
                )
        );

        /*
         * 辰
         *
         * 戊 정기
         * 乙 중기
         * 癸 여기
         */
        table.put(
                branch(4),
                List.of(
                        hiddenStem(
                                4,
                                HiddenStemRole.MAIN,
                                60
                        ),
                        hiddenStem(
                                1,
                                HiddenStemRole.MIDDLE,
                                30
                        ),
                        hiddenStem(
                                9,
                                HiddenStemRole.RESIDUAL,
                                10
                        )
                )
        );

        /*
         * 巳
         *
         * 丙 정기
         * 庚 중기
         * 戊 여기
         */
        table.put(
                branch(5),
                List.of(
                        hiddenStem(
                                2,
                                HiddenStemRole.MAIN,
                                60
                        ),
                        hiddenStem(
                                6,
                                HiddenStemRole.MIDDLE,
                                25
                        ),
                        hiddenStem(
                                4,
                                HiddenStemRole.RESIDUAL,
                                15
                        )
                )
        );

        /*
         * 午
         *
         * 丁 정기
         * 己 중기
         */
        table.put(
                branch(6),
                List.of(
                        hiddenStem(
                                3,
                                HiddenStemRole.MAIN,
                                70
                        ),
                        hiddenStem(
                                5,
                                HiddenStemRole.MIDDLE,
                                30
                        )
                )
        );

        /*
         * 未
         *
         * 己 정기
         * 丁 중기
         * 乙 여기
         */
        table.put(
                branch(7),
                List.of(
                        hiddenStem(
                                5,
                                HiddenStemRole.MAIN,
                                60
                        ),
                        hiddenStem(
                                3,
                                HiddenStemRole.MIDDLE,
                                30
                        ),
                        hiddenStem(
                                1,
                                HiddenStemRole.RESIDUAL,
                                10
                        )
                )
        );

        /*
         * 申
         *
         * 庚 정기
         * 壬 중기
         * 戊 여기
         */
        table.put(
                branch(8),
                List.of(
                        hiddenStem(
                                6,
                                HiddenStemRole.MAIN,
                                60
                        ),
                        hiddenStem(
                                8,
                                HiddenStemRole.MIDDLE,
                                25
                        ),
                        hiddenStem(
                                4,
                                HiddenStemRole.RESIDUAL,
                                15
                        )
                )
        );

        /*
         * 酉
         *
         * 辛
         */
        table.put(
                branch(9),
                List.of(
                        hiddenStem(
                                7,
                                HiddenStemRole.MAIN,
                                100
                        )
                )
        );

        /*
         * 戌
         *
         * 戊 정기
         * 辛 중기
         * 丁 여기
         */
        table.put(
                branch(10),
                List.of(
                        hiddenStem(
                                4,
                                HiddenStemRole.MAIN,
                                60
                        ),
                        hiddenStem(
                                7,
                                HiddenStemRole.MIDDLE,
                                30
                        ),
                        hiddenStem(
                                3,
                                HiddenStemRole.RESIDUAL,
                                10
                        )
                )
        );

        /*
         * 亥
         *
         * 壬 정기
         * 甲 중기
         */
        table.put(
                branch(11),
                List.of(
                        hiddenStem(
                                8,
                                HiddenStemRole.MAIN,
                                70
                        ),
                        hiddenStem(
                                0,
                                HiddenStemRole.MIDDLE,
                                30
                        )
                )
        );

        return Map.copyOf(
                table
        );
    }

    private static EarthlyBranch branch(
            int index
    ) {

        return EarthlyBranch.fromIndex(
                index
        );
    }

    private static HiddenStem hiddenStem(
            int stemIndex,
            HiddenStemRole role,
            int weight
    ) {

        return new HiddenStem(
                HeavenlyStem.fromIndex(
                        stemIndex
                ),
                role,
                weight
        );
    }
}