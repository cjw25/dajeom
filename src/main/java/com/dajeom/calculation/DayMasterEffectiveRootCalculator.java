package com.dajeom.calculation;

import com.dajeom.domain.DayMasterEffectiveRootAnalysis;
import com.dajeom.domain.DayMasterEffectiveRootCondition;
import com.dajeom.domain.DayMasterEffectiveRootContext;
import com.dajeom.domain.DayMasterRelationType;
import com.dajeom.domain.DayMasterRootInfluenceAssessment;
import com.dajeom.domain.DayMasterRootTransformationAnalysis;
import com.dajeom.domain.DayMasterRootTransformationContext;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.StemGroundHit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 일간의 통근에:
 *
 * - 합충형파해 Context
 * - ACCEPTED 합화 작용
 *
 * 을 결합한다.
 *
 * =================================================
 *
 * 이 계산기의 결과도
 * 최종적인 근의 생사 판정이 아니다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 근 삭제
 * - 근 점수
 * - 득지 true/false
 * - 신강/신약
 */
@Component
public class DayMasterEffectiveRootCalculator {

    public DayMasterEffectiveRootAnalysis analyze(

            HeavenlyStem dayMaster,

            List<DayMasterRootInfluenceAssessment>
                    rootInfluenceAssessments,

            DayMasterRootTransformationAnalysis
                    transformationAnalysis

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                rootInfluenceAssessments,
                "통근 관계 영향 목록은 필수입니다."
        );

        Objects.requireNonNull(
                transformationAnalysis,
                "통근 합화 분석은 필수입니다."
        );

        if (
                transformationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "통근 합화 분석의 일간이 일치하지 않습니다."
            );
        }

        Map<StemGroundHit, DayMasterRootInfluenceAssessment>
                influenceByRoot =
                indexInfluences(
                        dayMaster,
                        rootInfluenceAssessments
                );

        List<DayMasterEffectiveRootContext>
                exactRoots =
                buildContexts(
                        dayMaster,
                        transformationAnalysis
                                .exactStemRootContexts(),
                        influenceByRoot
                );

        List<DayMasterEffectiveRootContext>
                sameElementRoots =
                buildContexts(
                        dayMaster,
                        transformationAnalysis
                                .sameElementRootContexts(),
                        influenceByRoot
                );

        /*
         * Influence에는 있는데
         * Transformation 분석에는 없는 근이 있으면
         * 파이프라인 데이터가 서로 어긋난 것이다.
         */
        int transformationRootCount =
                exactRoots.size()
                        +
                        sameElementRoots.size();

        if (
                influenceByRoot.size()
                        != transformationRootCount
        ) {

            throw new IllegalStateException(
                    "통근 관계 분석과 통근 합화 분석의 근 개수가 일치하지 않습니다."
            );
        }

        return new DayMasterEffectiveRootAnalysis(
                dayMaster,
                exactRoots,
                sameElementRoots
        );
    }

    private List<DayMasterEffectiveRootContext> buildContexts(

            HeavenlyStem dayMaster,

            List<DayMasterRootTransformationContext>
                    transformationContexts,

            Map<StemGroundHit, DayMasterRootInfluenceAssessment>
                    influenceByRoot

    ) {

        List<DayMasterEffectiveRootContext> result =
                new ArrayList<>();

        for (
                DayMasterRootTransformationContext transformation
                : transformationContexts
        ) {

            Objects.requireNonNull(
                    transformation,
                    "통근 합화 Context에 null이 포함될 수 없습니다."
            );

            if (
                    transformation.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "통근 합화 Context의 일간이 일치하지 않습니다."
                );
            }

            StemGroundHit root =
                    transformation.root();

            DayMasterRootInfluenceAssessment influence =
                    influenceByRoot.get(
                            root
                    );

            if (
                    influence == null
            ) {

                throw new IllegalStateException(
                        "통근 지점에 대응하는 관계 영향 분석이 없습니다: "
                                +
                                root
                );
            }

            DayMasterEffectiveRootCondition condition =
                    resolveCondition(
                            transformation
                    );

            result.add(
                    new DayMasterEffectiveRootContext(
                            root,
                            influence.influenceState(),
                            transformation
                                    .transformationElement(),
                            transformation
                                    .transformationRelation(),
                            condition
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    private Map<StemGroundHit, DayMasterRootInfluenceAssessment>
    indexInfluences(

            HeavenlyStem dayMaster,

            List<DayMasterRootInfluenceAssessment> assessments

    ) {

        Map<StemGroundHit, DayMasterRootInfluenceAssessment>
                result =
                new HashMap<>();

        for (
                DayMasterRootInfluenceAssessment assessment
                : assessments
        ) {

            Objects.requireNonNull(
                    assessment,
                    "통근 관계 영향 목록에 null이 포함될 수 없습니다."
            );

            if (
                    assessment.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "통근 관계 영향 분석의 일간이 일치하지 않습니다."
                );
            }

            DayMasterRootInfluenceAssessment previous =
                    result.put(
                            assessment.root(),
                            assessment
                    );

            if (
                    previous != null
            ) {

                throw new IllegalStateException(
                        "동일 통근 지점에 관계 영향 분석이 중복되어 있습니다: "
                                +
                                assessment.root()
                );
            }
        }

        return Map.copyOf(
                result
        );
    }

    private DayMasterEffectiveRootCondition resolveCondition(

            DayMasterRootTransformationContext context

    ) {

        if (
                !context
                        .hasTransformationEffect()
        ) {

            return DayMasterEffectiveRootCondition
                    .ORIGINAL_ROOT_CONTEXT;
        }

        DayMasterRelationType relation =
                context
                        .transformationRelation()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "합화 작용은 존재하지만 일간 관계가 없습니다."
                                        )
                        );

        FiveElement transformationElement =
                context
                        .transformationElement()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "합화 작용은 존재하지만 합화 오행이 없습니다."
                                        )
                        );

        /*
         * transformationElement는
         * Context 무결성 확인용으로 읽는다.
         *
         * 실제 분류는 relation을 기준으로 한다.
         */
        Objects.requireNonNull(
                transformationElement,
                "합화 오행은 필수입니다."
        );

        return switch (relation) {

            case GENERATES_DAY_MASTER ->
                    DayMasterEffectiveRootCondition
                            .TRANSFORMATION_GENERATES_DAY_MASTER;

            case SAME_ELEMENT ->
                    DayMasterEffectiveRootCondition
                            .TRANSFORMATION_SAME_ELEMENT;

            case GENERATED_BY_DAY_MASTER ->
                    DayMasterEffectiveRootCondition
                            .TRANSFORMATION_GENERATED_BY_DAY_MASTER;

            case CONTROLS_DAY_MASTER ->
                    DayMasterEffectiveRootCondition
                            .TRANSFORMATION_CONTROLS_DAY_MASTER;

            case CONTROLLED_BY_DAY_MASTER ->
                    DayMasterEffectiveRootCondition
                            .TRANSFORMATION_CONTROLLED_BY_DAY_MASTER;
        };
    }
}