package com.dajeom.rule;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemComponent;
import com.dajeom.domain.HiddenStemPhase;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 통근 / 투간 분석에서 사용하는 지장간 규칙 공급자.
 *
 * 수학적 점수나 가중치를 사용하지 않는다.
 *
 * 현재 프로젝트에서 확정한 지장간표를
 * EARLY / MIDDLE / MAIN 구조로 반환한다.
 *
 * 2개의 지장간을 가진 지지는:
 *
 * EARLY / MAIN
 *
 * 으로 보존한다.
 *
 * 3개의 지장간을 가진 지지는:
 *
 * EARLY / MIDDLE / MAIN
 *
 * 으로 보존한다.
 */
@Component
public class HiddenStemSource {

    public List<HiddenStemComponent> hiddenStemsOf(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        return switch (branch) {

            /*
             * 子
             * 壬 / 癸
             */
            case ZI -> List.of(

                    early(
                            HeavenlyStem.REN
                    ),

                    main(
                            HeavenlyStem.GUI
                    )
            );

            /*
             * 丑
             * 癸 / 辛 / 己
             */
            case CHOU -> List.of(

                    early(
                            HeavenlyStem.GUI
                    ),

                    middle(
                            HeavenlyStem.XIN
                    ),

                    main(
                            HeavenlyStem.JI
                    )
            );

            /*
             * 寅
             * 戊 / 丙 / 甲
             */
            case YIN -> List.of(

                    early(
                            HeavenlyStem.WU
                    ),

                    middle(
                            HeavenlyStem.BING
                    ),

                    main(
                            HeavenlyStem.JIA
                    )
            );

            /*
             * 卯
             * 甲 / 乙
             */
            case MAO -> List.of(

                    early(
                            HeavenlyStem.JIA
                    ),

                    main(
                            HeavenlyStem.YI
                    )
            );

            /*
             * 辰
             * 乙 / 癸 / 戊
             */
            case CHEN -> List.of(

                    early(
                            HeavenlyStem.YI
                    ),

                    middle(
                            HeavenlyStem.GUI
                    ),

                    main(
                            HeavenlyStem.WU
                    )
            );

            /*
             * 巳
             * 戊 / 庚 / 丙
             */
            case SI -> List.of(

                    early(
                            HeavenlyStem.WU
                    ),

                    middle(
                            HeavenlyStem.GENG
                    ),

                    main(
                            HeavenlyStem.BING
                    )
            );

            /*
             * 午
             * 丙 / 己 / 丁
             */
            case WU -> List.of(

                    early(
                            HeavenlyStem.BING
                    ),

                    middle(
                            HeavenlyStem.JI
                    ),

                    main(
                            HeavenlyStem.DING
                    )
            );

            /*
             * 未
             * 丁 / 乙 / 己
             */
            case WEI -> List.of(

                    early(
                            HeavenlyStem.DING
                    ),

                    middle(
                            HeavenlyStem.YI
                    ),

                    main(
                            HeavenlyStem.JI
                    )
            );

            /*
             * 申
             * 戊 / 壬 / 庚
             */
            case SHEN -> List.of(

                    early(
                            HeavenlyStem.WU
                    ),

                    middle(
                            HeavenlyStem.REN
                    ),

                    main(
                            HeavenlyStem.GENG
                    )
            );

            /*
             * 酉
             * 庚 / 辛
             */
            case YOU -> List.of(

                    early(
                            HeavenlyStem.GENG
                    ),

                    main(
                            HeavenlyStem.XIN
                    )
            );

            /*
             * 戌
             * 辛 / 丁 / 戊
             */
            case XU -> List.of(

                    early(
                            HeavenlyStem.XIN
                    ),

                    middle(
                            HeavenlyStem.DING
                    ),

                    main(
                            HeavenlyStem.WU
                    )
            );

            /*
             * 亥
             * 戊 / 甲 / 壬
             */
            case HAI -> List.of(

                    early(
                            HeavenlyStem.WU
                    ),

                    middle(
                            HeavenlyStem.JIA
                    ),

                    main(
                            HeavenlyStem.REN
                    )
            );
        };
    }

    private HiddenStemComponent early(
            HeavenlyStem stem
    ) {

        return new HiddenStemComponent(
                stem,
                HiddenStemPhase.EARLY
        );
    }

    private HiddenStemComponent middle(
            HeavenlyStem stem
    ) {

        return new HiddenStemComponent(
                stem,
                HiddenStemPhase.MIDDLE
        );
    }

    private HiddenStemComponent main(
            HeavenlyStem stem
    ) {

        return new HiddenStemComponent(
                stem,
                HiddenStemPhase.MAIN
        );
    }
}