package com.dajeom.constant;

import com.dajeom.model.HiddenStem;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 지장간 기본 테이블.
 *
 * V1에서는 한국 명리에서 널리 사용되는
 * 여기 / 중기 / 정기 구성을 기준으로 한다.
 *
 * 지장간의 실제 강도나 점수는
 * 이 테이블에서 결정하지 않는다.
 *
 * 강도 평가는 추후 Analyzer / Rule에서
 * 별도로 수행한다.
 */
public final class HiddenStemTable {

    private static final Map<
            EarthlyBranch,
            List<HiddenStem>
            > TABLE =
            Map.ofEntries(

                    Map.entry(
                            EarthlyBranch.ZI,
                            List.of(
                                    hidden(
                                            HeavenlyStem.REN,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.GUI,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.CHOU,
                            List.of(
                                    hidden(
                                            HeavenlyStem.GUI,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.XIN,
                                            HiddenStemRole.MIDDLE
                                    ),
                                    hidden(
                                            HeavenlyStem.JI,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.YIN,
                            List.of(
                                    hidden(
                                            HeavenlyStem.WU,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.BING,
                                            HiddenStemRole.MIDDLE
                                    ),
                                    hidden(
                                            HeavenlyStem.JIA,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.MAO,
                            List.of(
                                    hidden(
                                            HeavenlyStem.JIA,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.YI,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.CHEN,
                            List.of(
                                    hidden(
                                            HeavenlyStem.YI,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.GUI,
                                            HiddenStemRole.MIDDLE
                                    ),
                                    hidden(
                                            HeavenlyStem.WU,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.SI,
                            List.of(
                                    hidden(
                                            HeavenlyStem.WU,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.GENG,
                                            HiddenStemRole.MIDDLE
                                    ),
                                    hidden(
                                            HeavenlyStem.BING,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.WU,
                            List.of(
                                    hidden(
                                            HeavenlyStem.BING,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.JI,
                                            HiddenStemRole.MIDDLE
                                    ),
                                    hidden(
                                            HeavenlyStem.DING,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.WEI,
                            List.of(
                                    hidden(
                                            HeavenlyStem.DING,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.YI,
                                            HiddenStemRole.MIDDLE
                                    ),
                                    hidden(
                                            HeavenlyStem.JI,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.SHEN,
                            List.of(
                                    hidden(
                                            HeavenlyStem.WU,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.REN,
                                            HiddenStemRole.MIDDLE
                                    ),
                                    hidden(
                                            HeavenlyStem.GENG,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.YOU,
                            List.of(
                                    hidden(
                                            HeavenlyStem.GENG,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.XIN,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.XU,
                            List.of(
                                    hidden(
                                            HeavenlyStem.XIN,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.DING,
                                            HiddenStemRole.MIDDLE
                                    ),
                                    hidden(
                                            HeavenlyStem.WU,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    ),

                    Map.entry(
                            EarthlyBranch.HAI,
                            List.of(
                                    hidden(
                                            HeavenlyStem.WU,
                                            HiddenStemRole.RESIDUAL
                                    ),
                                    hidden(
                                            HeavenlyStem.JIA,
                                            HiddenStemRole.MIDDLE
                                    ),
                                    hidden(
                                            HeavenlyStem.REN,
                                            HiddenStemRole.MAIN
                                    )
                            )
                    )
            );

    private HiddenStemTable() {

        throw new IllegalStateException(
                "HiddenStemTable은 생성할 수 없습니다."
        );
    }

    /**
     * 특정 지지의 모든 지장간.
     */
    public static List<HiddenStem> get(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        List<HiddenStem> hiddenStems =
                TABLE.get(branch);

        if (hiddenStems == null) {

            throw new IllegalArgumentException(
                    "지장간 정보가 없습니다: "
                            + branch
            );
        }

        return hiddenStems;
    }

    /**
     * 특정 지지의 정기(본기)를 반환한다.
     */
    public static HeavenlyStem getMainStem(
            EarthlyBranch branch
    ) {

        return get(branch)
                .stream()
                .filter(
                        hiddenStem ->
                                hiddenStem.role()
                                        == HiddenStemRole.MAIN
                )
                .map(
                        HiddenStem::stem
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "정기 지장간이 없습니다: "
                                                + branch
                                )
                );
    }

    private static HiddenStem hidden(
            HeavenlyStem stem,
            HiddenStemRole role
    ) {

        return new HiddenStem(
                stem,
                role
        );
    }
}