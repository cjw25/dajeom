package com.dajeom.calculation;

import com.dajeom.domain.BranchRelation;
import com.dajeom.domain.DayMasterHiddenStemRelation;
import com.dajeom.domain.DayMasterRelationAnalysis;
import com.dajeom.domain.DayMasterVisibleStemRelation;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.MonthCommandPatternSelectionAnalysis;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternInteractionHiddenStem;
import com.dajeom.domain.PatternInteractionVisibleStem;
import com.dajeom.domain.StemRelation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 일반 격국 성패 판정을 위해
 * 원국 전체 십성 배치를 Context화한다.
 *
 * =================================================
 *
 * 입력:
 *
 * - 월령 격국 선택 결과
 * - 이미 계산된 일간 생극관계
 * - 기존 천간 관계
 * - 기존 지지 관계
 *
 * =================================================
 *
 * 출력:
 *
 * 천간:
 * 십성 + 위치 + 천간 관계
 *
 * 지장간:
 * 십성 + 위치 + EARLY/MIDDLE/MAIN + 지지 관계
 *
 * =================================================
 *
 * 아직:
 *
 * - 成格
 * - 破格
 * - 救應
 *
 * 을 판단하지 않는다.
 */
@Component
public class PatternInteractionCalculator {

    private final MonthCommandPatternCandidateCalculator
            patternCandidateCalculator;

    public PatternInteractionCalculator(

            MonthCommandPatternCandidateCalculator
                    patternCandidateCalculator

    ) {

        this.patternCandidateCalculator =
                Objects.requireNonNull(
                        patternCandidateCalculator,
                        "MonthCommandPatternCandidateCalculator는 필수입니다."
                );
    }

    public PatternInteractionAnalysis analyze(

            MonthCommandPatternSelectionAnalysis patternSelection,

            DayMasterRelationAnalysis dayMasterRelations,

            List<StemRelation> stemRelations,

            List<BranchRelation> branchRelations

    ) {

        Objects.requireNonNull(
                patternSelection,
                "월령 격국 선택 결과는 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterRelations,
                "일간 생극관계 분석은 필수입니다."
        );

        HeavenlyStem dayMaster =
                patternSelection
                        .dayMaster();

        if (
                dayMasterRelations.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "격국 선택 결과와 생극관계 분석의 일간이 일치하지 않습니다."
            );
        }

        List<StemRelation> safeStemRelations =
                stemRelations == null
                        ? List.of()
                        : List.copyOf(
                        stemRelations
                );

        List<BranchRelation> safeBranchRelations =
                branchRelations == null
                        ? List.of()
                        : List.copyOf(
                        branchRelations
                );

        List<PatternInteractionVisibleStem>
                visibleInteractions =
                new ArrayList<>();

        for (
                DayMasterVisibleStemRelation relation
                : dayMasterRelations
                .visibleStemRelations()
        ) {

            Objects.requireNonNull(
                    relation,
                    "천간 생극관계 목록에 null이 포함될 수 없습니다."
            );

            MonthCommandPatternCandidateType tenGodType =
                    patternCandidateCalculator
                            .candidateTypeOf(
                                    dayMaster,
                                    relation.targetStem(),
                                    relation.relationType()
                            );

            List<StemRelation> touchingStemRelations =
                    safeStemRelations
                            .stream()
                            .filter(
                                    Objects::nonNull
                            )
                            .filter(
                                    stemRelation ->
                                            stemRelation.leftPosition()
                                                    == relation.targetPosition()
                                                    ||
                                                    stemRelation.rightPosition()
                                                            == relation.targetPosition()
                            )
                            .toList();

            visibleInteractions.add(
                    new PatternInteractionVisibleStem(
                            dayMaster,
                            relation.targetPosition(),
                            relation.targetStem(),
                            relation.relationType(),
                            tenGodType,
                            touchingStemRelations
                    )
            );
        }

        List<PatternInteractionHiddenStem>
                hiddenInteractions =
                new ArrayList<>();

        for (
                DayMasterHiddenStemRelation relation
                : dayMasterRelations
                .hiddenStemRelations()
        ) {

            Objects.requireNonNull(
                    relation,
                    "지장간 생극관계 목록에 null이 포함될 수 없습니다."
            );

            MonthCommandPatternCandidateType tenGodType =
                    patternCandidateCalculator
                            .candidateTypeOf(
                                    dayMaster,
                                    relation.hiddenStem(),
                                    relation.relationType()
                            );

            List<BranchRelation> touchingBranchRelations =
                    safeBranchRelations
                            .stream()
                            .filter(
                                    Objects::nonNull
                            )
                            .filter(
                                    branchRelation ->
                                            branchRelation.leftPosition()
                                                    == relation.branchPosition()
                                                    ||
                                                    branchRelation.rightPosition()
                                                            == relation.branchPosition()
                            )
                            .toList();

            hiddenInteractions.add(
                    new PatternInteractionHiddenStem(
                            dayMaster,
                            relation.branchPosition(),
                            relation.branch(),
                            relation.hiddenStem(),
                            relation.hiddenStemPhase(),
                            relation.relationType(),
                            tenGodType,
                            touchingBranchRelations
                    )
            );
        }

        return new PatternInteractionAnalysis(
                dayMaster,
                patternSelection,
                visibleInteractions,
                hiddenInteractions
        );
    }
}