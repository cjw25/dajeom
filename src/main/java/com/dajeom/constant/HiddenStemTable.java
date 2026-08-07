package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;

import java.util.List;
import java.util.Map;

/**
 * 십이지지별 지장간 고정표다.
 *
 * 표 안에서는 본기, 중기, 여기 순서로 관리한다.
 */
public final class HiddenStemTable {

    private static final Map<
            EarthlyBranch,
            List<HiddenStemDefinition>
            > TABLE = Map.ofEntries(

            Map.entry(
                    EarthlyBranch.JA,
                    List.of(
                            main(HeavenlyStem.GYE)
                    )
            ),

            Map.entry(
                    EarthlyBranch.CHUK,
                    List.of(
                            main(HeavenlyStem.GI),
                            middle(HeavenlyStem.SIN),
                            residual(HeavenlyStem.GYE)
                    )
            ),

            Map.entry(
                    EarthlyBranch.IN,
                    List.of(
                            main(HeavenlyStem.GAP),
                            middle(HeavenlyStem.BYEONG),
                            residual(HeavenlyStem.MU)
                    )
            ),

            Map.entry(
                    EarthlyBranch.MYO,
                    List.of(
                            main(HeavenlyStem.EUL)
                    )
            ),

            Map.entry(
                    EarthlyBranch.JIN,
                    List.of(
                            main(HeavenlyStem.MU),
                            middle(HeavenlyStem.GYE),
                            residual(HeavenlyStem.EUL)
                    )
            ),

            Map.entry(
                    EarthlyBranch.SA,
                    List.of(
                            main(HeavenlyStem.BYEONG),
                            middle(HeavenlyStem.GYEONG),
                            residual(HeavenlyStem.MU)
                    )
            ),

            Map.entry(
                    EarthlyBranch.O,
                    List.of(
                            main(HeavenlyStem.JEONG),
                            residual(HeavenlyStem.GI)
                    )
            ),

            Map.entry(
                    EarthlyBranch.MI,
                    List.of(
                            main(HeavenlyStem.GI),
                            middle(HeavenlyStem.EUL),
                            residual(HeavenlyStem.JEONG)
                    )
            ),

            Map.entry(
                    EarthlyBranch.SIN,
                    List.of(
                            main(HeavenlyStem.GYEONG),
                            middle(HeavenlyStem.IM),
                            residual(HeavenlyStem.MU)
                    )
            ),

            Map.entry(
                    EarthlyBranch.YU,
                    List.of(
                            main(HeavenlyStem.SIN)
                    )
            ),

            Map.entry(
                    EarthlyBranch.SUL,
                    List.of(
                            main(HeavenlyStem.MU),
                            middle(HeavenlyStem.JEONG),
                            residual(HeavenlyStem.SIN)
                    )
            ),

            Map.entry(
                    EarthlyBranch.HAE,
                    List.of(
                            main(HeavenlyStem.IM),
                            middle(HeavenlyStem.GAP)
                    )
            )
    );

    private HiddenStemTable() {
    }

    public static List<HiddenStemDefinition> get(
            EarthlyBranch branch
    ) {
        List<HiddenStemDefinition> definitions =
                TABLE.get(branch);

        if (definitions == null) {
            throw new IllegalArgumentException(
                    "등록되지 않은 지지입니다: "
                            + branch
            );
        }

        return definitions;
    }

    private static HiddenStemDefinition main(
            HeavenlyStem stem
    ) {
        return new HiddenStemDefinition(
                stem,
                HiddenStemRole.MAIN
        );
    }

    private static HiddenStemDefinition middle(
            HeavenlyStem stem
    ) {
        return new HiddenStemDefinition(
                stem,
                HiddenStemRole.MIDDLE
        );
    }

    private static HiddenStemDefinition residual(
            HeavenlyStem stem
    ) {
        return new HiddenStemDefinition(
                stem,
                HiddenStemRole.RESIDUAL
        );
    }

    public record HiddenStemDefinition(
            HeavenlyStem stem,
            HiddenStemRole role
    ) {
    }
}