package com.dajeom.constant;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 천간 오합 기본 관계표.
 *
 * 甲己合土
 * 乙庚合金
 * 丙辛合水
 * 丁壬合木
 * 戊癸合火
 *
 * 이 테이블은 합 관계와
 * 전통적인 합화 오행 후보만 제공한다.
 *
 * 실제 합화 성립 여부는 판단하지 않는다.
 */
public final class StemRelationTable {

    private static final Map<
            StemPair,
            FiveElement
            > COMBINATIONS =
            Map.of(

                    pair(
                            HeavenlyStem.JIA,
                            HeavenlyStem.JI
                    ),
                    FiveElement.EARTH,

                    pair(
                            HeavenlyStem.YI,
                            HeavenlyStem.GENG
                    ),
                    FiveElement.METAL,

                    pair(
                            HeavenlyStem.BING,
                            HeavenlyStem.XIN
                    ),
                    FiveElement.WATER,

                    pair(
                            HeavenlyStem.DING,
                            HeavenlyStem.REN
                    ),
                    FiveElement.WOOD,

                    pair(
                            HeavenlyStem.WU,
                            HeavenlyStem.GUI
                    ),
                    FiveElement.FIRE
            );

    private StemRelationTable() {

        throw new IllegalStateException(
                "StemRelationTable은 생성할 수 없습니다."
        );
    }

    /**
     * 두 천간이 오합 관계라면
     * 합화 오행 후보를 반환한다.
     */
    public static Optional<FiveElement>
    findCombinationElement(
            HeavenlyStem first,
            HeavenlyStem second
    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 천간은 필수입니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 천간은 필수입니다."
        );

        return Optional.ofNullable(
                COMBINATIONS.get(
                        pair(
                                first,
                                second
                        )
                )
        );
    }

    public static boolean isCombination(
            HeavenlyStem first,
            HeavenlyStem second
    ) {

        return findCombinationElement(
                first,
                second
        ).isPresent();
    }

    private static StemPair pair(
            HeavenlyStem first,
            HeavenlyStem second
    ) {

        return new StemPair(
                first,
                second
        );
    }

    /**
     * 순서가 바뀌어도 동일한 관계가 되도록
     * index 순서로 정규화한다.
     */
    private record StemPair(

            HeavenlyStem first,

            HeavenlyStem second

    ) {

        private StemPair {

            Objects.requireNonNull(
                    first
            );

            Objects.requireNonNull(
                    second
            );

            if (
                    first.getIndex()
                            > second.getIndex()
            ) {

                HeavenlyStem temp =
                        first;

                first =
                        second;

                second =
                        temp;
            }
        }
    }
}