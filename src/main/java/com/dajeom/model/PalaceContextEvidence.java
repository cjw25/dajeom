package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;
import com.dajeom.type.TwelveLifeStage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 12차-4.
 *
 * Palace 하나의 통합 Context.
 *
 * =================================================
 *
 * 포함:
 *
 * Palace 기본 위치
 * Palace 천간
 * Palace 지지
 * Palace 십성
 * 12운성
 * 천간 관계
 * 지지 관계
 * 합화
 *
 * =================================================
 *
 * 여기서는 기존 분석 결과를
 * 다시 계산하지 않는다.
 */
public record PalaceContextEvidence(

        PalacePosition position,

        PalacePositionContextType positionContext,

        PalaceStemEvidence stemEvidence,

        PalaceBranchEvidence branchEvidence,

        List<PalaceTenGodEvidence> tenGodEvidences,

        TwelveLifeStageContextEvidence twelveLifeStageContext,

        List<PalaceRelationReference> stemRelations,

        List<PalaceRelationReference> branchRelations,

        List<PalaceTransformationReference> transformations

) {

    public PalaceContextEvidence {

        Objects.requireNonNull(
                position,
                "Palace Context에는 위치가 필요합니다."
        );

        Objects.requireNonNull(
                positionContext,
                "Palace Context에는 위치 Context가 필요합니다."
        );

        Objects.requireNonNull(
                stemEvidence,
                "Palace Context에는 천간 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                branchEvidence,
                "Palace Context에는 지지 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                twelveLifeStageContext,
                "Palace Context에는 12운성 Context가 필요합니다."
        );

        tenGodEvidences =
                tenGodEvidences == null
                        ? List.of()
                        : List.copyOf(
                        tenGodEvidences
                );

        stemRelations =
                stemRelations == null
                        ? List.of()
                        : List.copyOf(
                        stemRelations
                );

        branchRelations =
                branchRelations == null
                        ? List.of()
                        : List.copyOf(
                        branchRelations
                );

        transformations =
                transformations == null
                        ? List.of()
                        : List.copyOf(
                        transformations
                );

        /*
         * =================================================
         * 위치 정합성
         * =================================================
         */

        if (
                stemEvidence.position()
                        != position

                        || branchEvidence.position()
                        != position
        ) {

            throw new IllegalArgumentException(
                    "Palace의 천간/지지 Evidence 위치가 서로 일치하지 않습니다."
            );
        }

        if (
                stemEvidence.positionContext()
                        != positionContext

                        || branchEvidence.positionContext()
                        != positionContext
        ) {

            throw new IllegalArgumentException(
                    "Palace 위치 Context가 서로 일치하지 않습니다."
            );
        }

        for (
                PalaceTenGodEvidence evidence
                : tenGodEvidences
        ) {

            if (
                    evidence.position()
                            != position
            ) {

                throw new IllegalArgumentException(
                        "다른 Palace의 십성 Evidence가 연결되었습니다."
                );
            }
        }

        /*
         * 11차 위치 정합성.
         */

        PalacePosition lifeStagePosition =
                switch (
                        twelveLifeStageContext.position()
                        ) {

                    case YEAR ->
                            PalacePosition.YEAR;

                    case MONTH ->
                            PalacePosition.MONTH;

                    case DAY ->
                            PalacePosition.DAY;

                    case HOUR ->
                            PalacePosition.HOUR;
                };

        if (
                lifeStagePosition
                        != position
        ) {

            throw new IllegalArgumentException(
                    "12운성 Context의 위치가 현재 Palace와 일치하지 않습니다."
            );
        }

        for (
                PalaceRelationReference relation
                : stemRelations
        ) {

            if (
                    !relation.stemRelation()
                            || !relation.involves(
                            position
                    )
            ) {

                throw new IllegalArgumentException(
                        "잘못된 천간 관계가 Palace에 연결되었습니다."
                );
            }
        }

        for (
                PalaceRelationReference relation
                : branchRelations
        ) {

            if (
                    !relation.branchRelation()
                            || !relation.involves(
                            position
                    )
            ) {

                throw new IllegalArgumentException(
                        "잘못된 지지 관계가 Palace에 연결되었습니다."
                );
            }
        }

        for (
                PalaceTransformationReference transformation
                : transformations
        ) {

            if (
                    !transformation.involves(
                            position
                    )
            ) {

                throw new IllegalArgumentException(
                        "현재 Palace와 무관한 합화 Context가 연결되었습니다."
                );
            }
        }
    }

    public HeavenlyStem dayMaster() {

        return stemEvidence
                .dayMaster();
    }

    public HeavenlyStem stem() {

        return stemEvidence
                .stem();
    }

    public EarthlyBranch branch() {

        return branchEvidence
                .branch();
    }

    public TwelveLifeStage twelveLifeStage() {

        return twelveLifeStageContext
                .stage();
    }

    public boolean dayMasterAnchor() {

        return stemEvidence
                .dayMasterAnchor();
    }

    public boolean hasStemRelations() {

        return !stemRelations
                .isEmpty();
    }

    public boolean hasBranchRelations() {

        return !branchRelations
                .isEmpty();
    }

    public boolean hasTransformations() {

        return !transformations
                .isEmpty();
    }

    public boolean hasActivatedTransformation() {

        return transformations
                .stream()
                .anyMatch(
                        PalaceTransformationReference
                                ::activated
                );
    }

    public boolean hasReducedOriginalFunction() {

        return transformations
                .stream()
                .anyMatch(
                        PalaceTransformationReference
                                ::originalFunctionReduced
                );
    }

    public Optional<PalaceTenGodEvidence> visibleTenGod() {

        return tenGodEvidences
                .stream()
                .filter(
                        PalaceTenGodEvidence
                                ::visible
                )
                .findFirst();
    }

    public List<PalaceTenGodEvidence> hiddenTenGods() {

        return tenGodEvidences
                .stream()
                .filter(
                        PalaceTenGodEvidence
                                ::hidden
                )
                .toList();
    }
}