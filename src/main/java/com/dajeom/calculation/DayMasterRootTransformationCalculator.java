package com.dajeom.calculation;

import com.dajeom.domain.DayMasterGroundAnalysis;
import com.dajeom.domain.DayMasterRelationType;
import com.dajeom.domain.DayMasterRootTransformationAnalysis;
import com.dajeom.domain.DayMasterRootTransformationContext;
import com.dajeom.domain.EffectiveBranchElementAnalysis;
import com.dajeom.domain.EffectiveBranchElementState;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.StemGroundHit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 일간의 통근과
 * ACCEPTED 지지 합화 작용을 연결한다.
 *
 * =================================================
 *
 * 하는 것:
 *
 * 통근
 * +
 * 해당 지지의 합화 작용
 * +
 * 합화 오행과 일간의 생극관계
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 통근 제거
 * - 통근 무효화
 * - 통근 강화
 * - 통근 약화
 * - 신강/신약 판정
 *
 * =================================================
 *
 * 원래 통근 사실은 항상 보존한다.
 */
@Component
public class DayMasterRootTransformationCalculator {

    public DayMasterRootTransformationAnalysis analyze(

            HeavenlyStem dayMaster,

            DayMasterGroundAnalysis ground,

            EffectiveBranchElementAnalysis
                    effectiveBranchElements

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                ground,
                "일간 뿌리 구조는 필수입니다."
        );

        Objects.requireNonNull(
                effectiveBranchElements,
                "지지 작용 오행 분석은 필수입니다."
        );

        if (
                ground.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "일간과 뿌리 구조의 대상 천간이 일치하지 않습니다."
            );
        }

        List<DayMasterRootTransformationContext>
                exactContexts =
                buildContexts(
                        dayMaster,
                        ground.exactStemRoots(),
                        effectiveBranchElements
                );

        List<DayMasterRootTransformationContext>
                sameElementContexts =
                buildContexts(
                        dayMaster,
                        ground.sameElementOtherStemRoots(),
                        effectiveBranchElements
                );

        return new DayMasterRootTransformationAnalysis(
                dayMaster,
                exactContexts,
                sameElementContexts
        );
    }

    private List<DayMasterRootTransformationContext> buildContexts(

            HeavenlyStem dayMaster,

            List<StemGroundHit> roots,

            EffectiveBranchElementAnalysis
                    effectiveBranchElements

    ) {

        if (
                roots == null
                        ||
                        roots.isEmpty()
        ) {

            return List.of();
        }

        List<DayMasterRootTransformationContext> result =
                new ArrayList<>();

        for (
                StemGroundHit root
                : roots
        ) {

            Objects.requireNonNull(
                    root,
                    "통근 목록에 null이 포함될 수 없습니다."
            );

            EffectiveBranchElementState branchState =
                    effectiveBranchElements
                            .stateAt(
                                    root.branchPosition()
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "통근 위치에 해당하는 지지 작용 상태가 없습니다: "
                                                            +
                                                            root.branchPosition()
                                            )
                            );

            if (
                    branchState.branch()
                            != root.branch()
            ) {

                throw new IllegalStateException(
                        "통근 지지와 지지 작용 분석의 지지가 일치하지 않습니다: "
                                +
                                root.branch()
                                +
                                " / "
                                +
                                branchState.branch()
                );
            }

            Optional<FiveElement>
                    transformationElement =
                    branchState
                            .transformationElement();

            Optional<DayMasterRelationType>
                    transformationRelation =
                    transformationElement.map(
                            element ->
                                    relationOf(
                                            elementOf(
                                                    dayMaster
                                            ),
                                            element
                                    )
                    );

            result.add(
                    new DayMasterRootTransformationContext(
                            root,
                            branchState,
                            transformationElement,
                            transformationRelation
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 일간 오행을 기준으로
     * 합화 작용 오행과의 관계를 판정한다.
     *
     * targetElement는 합화 작용 오행이다.
     */
    private DayMasterRelationType relationOf(

            FiveElement dayMasterElement,

            FiveElement targetElement

    ) {

        if (
                dayMasterElement
                        == targetElement
        ) {

            return DayMasterRelationType
                    .SAME_ELEMENT;
        }

        /*
         * 합화 오행이 일간을 생함.
         */
        if (
                generates(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterRelationType
                    .GENERATES_DAY_MASTER;
        }

        /*
         * 일간이 합화 오행을 생함.
         */
        if (
                generates(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterRelationType
                    .GENERATED_BY_DAY_MASTER;
        }

        /*
         * 합화 오행이 일간을 극함.
         */
        if (
                controls(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterRelationType
                    .CONTROLS_DAY_MASTER;
        }

        /*
         * 일간이 합화 오행을 극함.
         */
        if (
                controls(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterRelationType
                    .CONTROLLED_BY_DAY_MASTER;
        }

        throw new IllegalStateException(
                "일간과 합화 오행의 관계를 판정할 수 없습니다: "
                        +
                        dayMasterElement
                        +
                        " / "
                        +
                        targetElement
        );
    }

    private boolean generates(

            FiveElement source,

            FiveElement target

    ) {

        return switch (source) {

            case WOOD ->
                    target
                            == FiveElement.FIRE;

            case FIRE ->
                    target
                            == FiveElement.EARTH;

            case EARTH ->
                    target
                            == FiveElement.METAL;

            case METAL ->
                    target
                            == FiveElement.WATER;

            case WATER ->
                    target
                            == FiveElement.WOOD;
        };
    }

    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return switch (source) {

            case WOOD ->
                    target
                            == FiveElement.EARTH;

            case EARTH ->
                    target
                            == FiveElement.WATER;

            case WATER ->
                    target
                            == FiveElement.FIRE;

            case FIRE ->
                    target
                            == FiveElement.METAL;

            case METAL ->
                    target
                            == FiveElement.WOOD;
        };
    }

    private FiveElement elementOf(
            HeavenlyStem stem
    ) {

        return switch (stem) {

            case JIA, YI ->
                    FiveElement.WOOD;

            case BING, DING ->
                    FiveElement.FIRE;

            case WU, JI ->
                    FiveElement.EARTH;

            case GENG, XIN ->
                    FiveElement.METAL;

            case REN, GUI ->
                    FiveElement.WATER;
        };
    }
}