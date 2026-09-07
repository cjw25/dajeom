package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 격국 판정에서 사용하는 천간의 십성 배치 Context.
 *
 * 예:
 *
 * 丁 일간
 *
 * 년간 癸
 * → 七殺/正官 계열 중 정확한 십성 타입
 *
 * 월간 庚
 * → 財 계열
 *
 * 시간 甲
 * → 印 계열
 *
 * 또한 해당 천간에
 * 합/충 등의 StemRelation이 걸려 있는지도
 * 함께 보존한다.
 *
 * 아직 길흉이나 成格/破格을 판정하지 않는다.
 */
public record PatternInteractionVisibleStem(

        HeavenlyStem dayMaster,

        PillarPosition position,

        HeavenlyStem stem,

        DayMasterRelationType relationType,

        MonthCommandPatternCandidateType tenGodType,

        List<StemRelation> stemRelations

) {

    public PatternInteractionVisibleStem {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                position,
                "천간 위치는 필수입니다."
        );

        Objects.requireNonNull(
                stem,
                "천간은 필수입니다."
        );

        Objects.requireNonNull(
                relationType,
                "일간과 천간의 생극관계는 필수입니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "십성 역할은 필수입니다."
        );

        stemRelations =
                stemRelations == null
                        ? List.of()
                        : List.copyOf(
                        stemRelations
                );

        if (
                position
                        == PillarPosition.DAY
        ) {

            throw new IllegalArgumentException(
                    "일간 자신은 격국 Interaction의 외부 천간 대상이 아닙니다."
            );
        }

        for (
                StemRelation relation
                : stemRelations
        ) {

            Objects.requireNonNull(
                    relation,
                    "천간 관계 목록에 null이 포함될 수 없습니다."
            );

            boolean touchesPosition =
                    relation.leftPosition()
                            == position
                            ||
                            relation.rightPosition()
                                    == position;

            if (!touchesPosition) {

                throw new IllegalArgumentException(
                        "현재 천간 위치와 관계없는 StemRelation이 포함되어 있습니다."
                );
            }
        }
    }

    public boolean hasStemRelation() {

        return !stemRelations
                .isEmpty();
    }
}