package com.dajeom.analyzer;

import com.dajeom.model.BranchRelation;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.model.RelationImpactResult;
import com.dajeom.model.StrengthEvidence;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.RelationType;
import com.dajeom.type.StrengthFactorType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 지지 관계가 일간의 통근에 미치는 영향을 분석한다.
 *
 * 4차 V1에서는 합화에 따른 오행 변환은 반영하지 않는다.
 *
 * 충·형·파·해가 일간의 실제 뿌리가 있는 지지를 건드릴 때만
 * 신강·신약 점수를 보수적으로 감점한다.
 */
@Component
public class RelationImpactAnalyzer {

    public RelationImpactResult analyze(
            FiveElement dayMasterElement,
            List<HiddenStem> hiddenStems,
            RelationAnalysis relationAnalysis
    ) {
        List<PillarPosition> rootPositions =
                findRootPositions(
                        dayMasterElement,
                        hiddenStems
                );

        if (rootPositions.isEmpty()) {
            return RelationImpactResult.empty();
        }

        double totalScoreChange = 0.0;

        List<StrengthEvidence> evidence =
                new ArrayList<>();

        for (BranchRelation relation
                : relationAnalysis.branchRelations()) {

            if (!affectsRoot(
                    relation,
                    rootPositions
            )) {
                continue;
            }

            double scoreChange =
                    scoreChangeOf(
                            relation.relationType()
                    );

            if (scoreChange == 0.0) {
                continue;
            }

            totalScoreChange += scoreChange;

            evidence.add(
                    new StrengthEvidence(
                            createEvidenceCode(relation),

                            StrengthFactorType
                                    .RELATION_IMPACT,

                            scoreChange,

                            relation.description()
                                    + ". 일간의 통근 위치와 겹쳐 "
                                    + "뿌리의 안정성을 낮추는 것으로 반영함"
                    )
            );
        }

        return new RelationImpactResult(
                round(totalScoreChange),
                evidence
        );
    }

    private List<PillarPosition> findRootPositions(
            FiveElement dayMasterElement,
            List<HiddenStem> hiddenStems
    ) {
        return hiddenStems.stream()
                .filter(hiddenStem ->
                        hiddenStem
                                .stem()
                                .getElement()
                                == dayMasterElement
                )
                .map(HiddenStem::branchPosition)
                .distinct()
                .toList();
    }

    private boolean affectsRoot(
            BranchRelation relation,
            List<PillarPosition> rootPositions
    ) {
        return relation.positions()
                .stream()
                .anyMatch(rootPositions::contains);
    }

    private double scoreChangeOf(
            RelationType relationType
    ) {
        return switch (relationType) {
            case CLASH -> -4.0;
            case PUNISHMENT -> -3.0;
            case BREAK -> -2.0;
            case HARM -> -2.0;

            /*
             * 합은 성립 조건을 판정하기 전까지
             * 강약 점수에 직접 반영하지 않는다.
             */
            case SIX_HARMONY,
                 THREE_HARMONY,
                 HALF_HARMONY,
                 STEM_COMBINATION,
                 STEM_CONTROL -> 0.0;
        };
    }

    private String createEvidenceCode(
            BranchRelation relation
    ) {
        String positions =
                relation.positions()
                        .stream()
                        .map(Enum::name)
                        .reduce(
                                "",
                                (first, second) ->
                                        first.isBlank()
                                                ? second
                                                : first + "_" + second
                        );

        return relation.relationType().name()
                + "_ROOT_IMPACT_"
                + positions;
    }

    private double round(
            double value
    ) {
        return Math.round(value * 100.0)
                / 100.0;
    }
}