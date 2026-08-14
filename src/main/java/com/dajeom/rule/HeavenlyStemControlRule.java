package com.dajeom.rule;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemClashType;
import com.dajeom.type.HeavenlyStemControlRelationType;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 8차-2.
 *
 * 천간 충·극 관계 Rule.
 *
 * =================================================
 *
 * 책임
 *
 * 1.
 * 두 천간 사이에 상극 관계가 있는지.
 *
 * 2.
 * 어느 천간이 극하고
 *    어느 천간이 극받는지.
 *
 * 3.
 * 천간 사충에 해당하는지.
 *
 * 4.
 * 일반 극제인지.
 *
 * =================================================
 *
 * 오행 극제
 *
 * 木剋土
 * 火剋金
 * 土剋水
 * 金剋木
 * 水剋火
 *
 * =================================================
 *
 * 천간 사충
 *
 * 甲庚
 * 乙辛
 * 丙壬
 * 丁癸
 *
 * =================================================
 *
 * 주의:
 *
 * "상극 관계 존재"
 * 와
 * "실제 영향이 강하다"
 * 는 다른 문제다.
 *
 * 인접 여부, 합, 세력, 용신 관계 등은
 * 상위 단계에서 함께 판단한다.
 */
@Component
public class HeavenlyStemControlRule {

    /**
     * source 천간이 target 천간을 극하는가.
     */
    public boolean controls(

            HeavenlyStem source,

            HeavenlyStem target

    ) {

        if (
                source == null
                        || target == null
                        || source == target
        ) {

            return false;
        }

        /*
         * =================================================
         * 金剋木
         * =================================================
         */
        if (
                (
                        source == HeavenlyStem.GENG
                                || source == HeavenlyStem.XIN
                )
                        && (
                        target == HeavenlyStem.JIA
                                || target == HeavenlyStem.YI
                )
        ) {

            return true;
        }

        /*
         * =================================================
         * 水剋火
         * =================================================
         */
        if (
                (
                        source == HeavenlyStem.REN
                                || source == HeavenlyStem.GUI
                )
                        && (
                        target == HeavenlyStem.BING
                                || target == HeavenlyStem.DING
                )
        ) {

            return true;
        }

        /*
         * =================================================
         * 木剋土
         * =================================================
         */
        if (
                (
                        source == HeavenlyStem.JIA
                                || source == HeavenlyStem.YI
                )
                        && (
                        target == HeavenlyStem.WU
                                || target == HeavenlyStem.JI
                )
        ) {

            return true;
        }

        /*
         * =================================================
         * 火剋金
         * =================================================
         */
        if (
                (
                        source == HeavenlyStem.BING
                                || source == HeavenlyStem.DING
                )
                        && (
                        target == HeavenlyStem.GENG
                                || target == HeavenlyStem.XIN
                )
        ) {

            return true;
        }

        /*
         * =================================================
         * 土剋水
         * =================================================
         */
        return (
                source == HeavenlyStem.WU
                        || source == HeavenlyStem.JI
        )
                && (
                target == HeavenlyStem.REN
                        || target == HeavenlyStem.GUI
        );
    }

    /**
     * 두 천간 사이에 어느 방향이든
     * 극제가 존재하는가.
     */
    public boolean hasControlRelation(

            HeavenlyStem stem1,

            HeavenlyStem stem2

    ) {

        if (
                stem1 == null
                        || stem2 == null
        ) {

            return false;
        }

        return controls(
                stem1,
                stem2
        )
                || controls(
                stem2,
                stem1
        );
    }

    /**
     * Pair의 극하는 천간.
     *
     * 극제 관계가 없으면 Optional.empty().
     */
    public Optional<HeavenlyStem> findController(

            HeavenlyStem stem1,

            HeavenlyStem stem2

    ) {

        Objects.requireNonNull(
                stem1,
                "첫 번째 천간이 필요합니다."
        );

        Objects.requireNonNull(
                stem2,
                "두 번째 천간이 필요합니다."
        );

        if (
                controls(
                        stem1,
                        stem2
                )
        ) {

            return Optional.of(
                    stem1
            );
        }

        if (
                controls(
                        stem2,
                        stem1
                )
        ) {

            return Optional.of(
                    stem2
            );
        }

        return Optional.empty();
    }

    /**
     * Pair의 극받는 천간.
     */
    public Optional<HeavenlyStem> findControlled(

            HeavenlyStem stem1,

            HeavenlyStem stem2

    ) {

        Objects.requireNonNull(
                stem1,
                "첫 번째 천간이 필요합니다."
        );

        Objects.requireNonNull(
                stem2,
                "두 번째 천간이 필요합니다."
        );

        if (
                controls(
                        stem1,
                        stem2
                )
        ) {

            return Optional.of(
                    stem2
            );
        }

        if (
                controls(
                        stem2,
                        stem1
                )
        ) {

            return Optional.of(
                    stem1
            );
        }

        return Optional.empty();
    }

    /**
     * 천간 사충 여부.
     */
    public Optional<HeavenlyStemClashType> findClashType(

            HeavenlyStem stem1,

            HeavenlyStem stem2

    ) {

        Objects.requireNonNull(
                stem1,
                "첫 번째 천간이 필요합니다."
        );

        Objects.requireNonNull(
                stem2,
                "두 번째 천간이 필요합니다."
        );

        for (
                HeavenlyStemClashType clashType
                : HeavenlyStemClashType.values()
        ) {

            if (
                    clashType.matches(
                            stem1,
                            stem2
                    )
            ) {

                return Optional.of(
                        clashType
                );
            }
        }

        return Optional.empty();
    }

    /**
     * 두 천간 사이의 최종 8-2 관계 분류.
     */
    public Optional<HeavenlyStemControlRelationType>
    findRelationType(

            HeavenlyStem stem1,

            HeavenlyStem stem2

    ) {

        Objects.requireNonNull(
                stem1,
                "첫 번째 천간이 필요합니다."
        );

        Objects.requireNonNull(
                stem2,
                "두 번째 천간이 필요합니다."
        );

        if (
                !hasControlRelation(
                        stem1,
                        stem2
                )
        ) {

            return Optional.empty();
        }

        if (
                findClashType(
                        stem1,
                        stem2
                )
                        .isPresent()
        ) {

            return Optional.of(
                    HeavenlyStemControlRelationType
                            .CLASH
            );
        }

        return Optional.of(
                HeavenlyStemControlRelationType
                        .CONTROL_ONLY
        );
    }

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

    public int positionDistance(

            VisibleStemPosition position1,

            VisibleStemPosition position2

    ) {

        Objects.requireNonNull(
                position1,
                "첫 번째 위치가 필요합니다."
        );

        Objects.requireNonNull(
                position2,
                "두 번째 위치가 필요합니다."
        );

        return Math.abs(
                position1.ordinal()
                        - position2.ordinal()
        );
    }

    public boolean adjacent(

            VisibleStemPosition position1,

            VisibleStemPosition position2

    ) {

        return positionDistance(
                position1,
                position2
        ) == 1;
    }
}