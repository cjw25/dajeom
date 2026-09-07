package com.dajeom.calculation;

import com.dajeom.domain.DayMasterHiddenStemRelation;
import com.dajeom.domain.DayMasterRelationAnalysis;
import com.dajeom.domain.DayMasterRelationType;
import com.dajeom.domain.DayMasterVisibleStemRelation;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemComponent;
import com.dajeom.domain.PillarPosition;
import com.dajeom.rule.HiddenStemSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 일간 기준 원국 생극 관계 분석기.
 *
 * 계산하는 것:
 *
 * - 년간과 일간의 관계
 * - 월간과 일간의 관계
 * - 시간과 일간의 관계
 * - 네 지지의 모든 지장간과 일간의 관계
 *
 * 계산하지 않는 것:
 *
 * - 신강/신약
 * - 격국
 * - 용신
 * - 점수
 * - 가중치
 * - 근거 개수 비교
 */
@Component
public class DayMasterRelationCalculator {

    private final HiddenStemSource hiddenStemSource;

    public DayMasterRelationCalculator(
            HiddenStemSource hiddenStemSource
    ) {

        this.hiddenStemSource =
                Objects.requireNonNull(
                        hiddenStemSource,
                        "HiddenStemSource는 필수입니다."
                );
    }

    public DayMasterRelationAnalysis analyze(

            HeavenlyStem dayMaster,

            HeavenlyStem yearStem,

            HeavenlyStem monthStem,

            Optional<HeavenlyStem> hourStem,

            EarthlyBranch yearBranch,

            EarthlyBranch monthBranch,

            EarthlyBranch dayBranch,

            Optional<EarthlyBranch> hourBranch

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                yearStem,
                "년간은 필수입니다."
        );

        Objects.requireNonNull(
                monthStem,
                "월간은 필수입니다."
        );

        Objects.requireNonNull(
                hourStem,
                "시간 Optional은 필수입니다."
        );

        Objects.requireNonNull(
                yearBranch,
                "년지는 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                dayBranch,
                "일지는 필수입니다."
        );

        Objects.requireNonNull(
                hourBranch,
                "시지 Optional은 필수입니다."
        );

        List<DayMasterVisibleStemRelation>
                visibleRelations =
                new ArrayList<>();

        appendVisibleRelation(
                visibleRelations,
                dayMaster,
                PillarPosition.YEAR,
                yearStem
        );

        appendVisibleRelation(
                visibleRelations,
                dayMaster,
                PillarPosition.MONTH,
                monthStem
        );

        hourStem.ifPresent(
                stem ->
                        appendVisibleRelation(
                                visibleRelations,
                                dayMaster,
                                PillarPosition.HOUR,
                                stem
                        )
        );

        List<DayMasterHiddenStemRelation>
                hiddenRelations =
                new ArrayList<>();

        appendHiddenRelations(
                hiddenRelations,
                dayMaster,
                PillarPosition.YEAR,
                yearBranch
        );

        appendHiddenRelations(
                hiddenRelations,
                dayMaster,
                PillarPosition.MONTH,
                monthBranch
        );

        appendHiddenRelations(
                hiddenRelations,
                dayMaster,
                PillarPosition.DAY,
                dayBranch
        );

        hourBranch.ifPresent(
                branch ->
                        appendHiddenRelations(
                                hiddenRelations,
                                dayMaster,
                                PillarPosition.HOUR,
                                branch
                        )
        );

        return new DayMasterRelationAnalysis(
                dayMaster,
                visibleRelations,
                hiddenRelations
        );
    }

    private void appendVisibleRelation(

            List<DayMasterVisibleStemRelation> result,

            HeavenlyStem dayMaster,

            PillarPosition position,

            HeavenlyStem targetStem

    ) {

        result.add(
                new DayMasterVisibleStemRelation(
                        dayMaster,
                        position,
                        targetStem,
                        relationOf(
                                dayMaster,
                                targetStem
                        )
                )
        );
    }

    private void appendHiddenRelations(

            List<DayMasterHiddenStemRelation> result,

            HeavenlyStem dayMaster,

            PillarPosition branchPosition,

            EarthlyBranch branch

    ) {

        List<HiddenStemComponent> hiddenStems =
                hiddenStemSource.hiddenStemsOf(
                        branch
                );

        if (
                hiddenStems == null
                        || hiddenStems.isEmpty()
        ) {

            return;
        }

        for (
                HiddenStemComponent component
                : hiddenStems
        ) {

            Objects.requireNonNull(
                    component,
                    "지장간 목록에 null이 포함될 수 없습니다."
            );

            HeavenlyStem hiddenStem =
                    component.stem();

            result.add(
                    new DayMasterHiddenStemRelation(
                            dayMaster,
                            branchPosition,
                            branch,
                            hiddenStem,
                            component.phase(),
                            relationOf(
                                    dayMaster,
                                    hiddenStem
                            )
                    )
            );
        }
    }

    public DayMasterRelationType relationOf(

            HeavenlyStem dayMaster,

            HeavenlyStem targetStem

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                targetStem,
                "대상 천간은 필수입니다."
        );

        FiveElement dayMasterElement =
                elementOf(
                        dayMaster
                );

        FiveElement targetElement =
                elementOf(
                        targetStem
                );

        if (
                dayMasterElement
                        == targetElement
        ) {

            return DayMasterRelationType.SAME_ELEMENT;
        }

        if (
                generates(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterRelationType.GENERATES_DAY_MASTER;
        }

        if (
                generates(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterRelationType.GENERATED_BY_DAY_MASTER;
        }

        if (
                controls(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterRelationType.CONTROLS_DAY_MASTER;
        }

        if (
                controls(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterRelationType.CONTROLLED_BY_DAY_MASTER;
        }

        throw new IllegalStateException(
                "오행 관계를 판정할 수 없습니다: "
                        + dayMaster
                        + " / "
                        + targetStem
        );
    }

    /**
     * 오행 상생.
     *
     * 木生火
     * 火生土
     * 土生金
     * 金生水
     * 水生木
     */
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

    /**
     * 오행 상극.
     *
     * 木克土
     * 土克水
     * 水克火
     * 火克金
     * 金克木
     */
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