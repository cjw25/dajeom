package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 격국 판정에서 사용하는 지장간의 십성 배치 Context.
 *
 * 보존:
 *
 * - 어느 지지인가
 * - EARLY / MIDDLE / MAIN
 * - 정확한 지장간
 * - 일간과의 생극관계
 * - 정확한 십성 역할
 * - 해당 지지가 합충형파해 관계에 있는가
 *
 * 아직 지장간의 세력 크기나
 * 成格/破格을 판단하지 않는다.
 */
public record PatternInteractionHiddenStem(

        HeavenlyStem dayMaster,

        PillarPosition branchPosition,

        EarthlyBranch branch,

        HeavenlyStem hiddenStem,

        HiddenStemPhase hiddenStemPhase,

        DayMasterRelationType relationType,

        MonthCommandPatternCandidateType tenGodType,

        List<BranchRelation> branchRelations

) {

    public PatternInteractionHiddenStem {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                branchPosition,
                "지지 위치는 필수입니다."
        );

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "지장간은 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStemPhase,
                "지장간 단계는 필수입니다."
        );

        Objects.requireNonNull(
                relationType,
                "일간과 지장간의 관계는 필수입니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "십성 역할은 필수입니다."
        );

        branchRelations =
                branchRelations == null
                        ? List.of()
                        : List.copyOf(
                        branchRelations
                );

        for (
                BranchRelation relation
                : branchRelations
        ) {

            Objects.requireNonNull(
                    relation,
                    "지지 관계 목록에 null이 포함될 수 없습니다."
            );

            boolean touchesPosition =
                    relation.leftPosition()
                            == branchPosition
                            ||
                            relation.rightPosition()
                                    == branchPosition;

            if (!touchesPosition) {

                throw new IllegalArgumentException(
                        "현재 지지 위치와 관계없는 BranchRelation이 포함되어 있습니다."
                );
            }
        }
    }

    public boolean hasBranchRelation() {

        return !branchRelations
                .isEmpty();
    }

    public boolean fromMonthCommand() {

        return branchPosition
                == PillarPosition.MONTH;
    }

    public boolean mainHiddenStem() {

        return hiddenStemPhase
                == HiddenStemPhase.MAIN;
    }
}