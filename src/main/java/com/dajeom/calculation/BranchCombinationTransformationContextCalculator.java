package com.dajeom.calculation;

import com.dajeom.domain.BranchCombinationOccurrence;
import com.dajeom.domain.BranchCombinationTransformationContext;
import com.dajeom.domain.BranchTransformationHiddenStemHit;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.FiveElementSeasonState;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemComponent;
import com.dajeom.domain.PillarPosition;
import com.dajeom.rule.HiddenStemSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 지지 합화 판단에 필요한 Context 계산기.
 *
 * =================================================
 *
 * 하는 것:
 *
 * - 합화 목표 오행의 월령 상태
 * - 목표 오행의 천간 노출
 * - 목표 오행의 지장간 기반
 * - 합 참여 지지에 걸린 충 Context
 * - 합 경쟁 Context
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 합화 성공
 * - 합화 실패
 * - 합거
 * - 합반
 * - 원래 지지 오행 삭제
 *
 * =================================================
 *
 * 따라서 이 계산 결과만으로
 * 지지의 원래 오행을 변경하면 안 된다.
 */
@Component
public class BranchCombinationTransformationContextCalculator {

    private final HiddenStemSource
            hiddenStemSource;

    private final DayMasterSeasonCalculator
            dayMasterSeasonCalculator;

    public BranchCombinationTransformationContextCalculator(

            HiddenStemSource hiddenStemSource,

            DayMasterSeasonCalculator
                    dayMasterSeasonCalculator

    ) {

        this.hiddenStemSource =
                Objects.requireNonNull(
                        hiddenStemSource,
                        "HiddenStemSource는 필수입니다."
                );

        this.dayMasterSeasonCalculator =
                Objects.requireNonNull(
                        dayMasterSeasonCalculator,
                        "DayMasterSeasonCalculator는 필수입니다."
                );
    }

    public BranchCombinationTransformationContext analyze(

            BranchCombinationOccurrence occurrence,

            HeavenlyStem yearStem,

            HeavenlyStem monthStem,

            HeavenlyStem dayStem,

            Optional<HeavenlyStem> hourStem,

            EarthlyBranch yearBranch,

            EarthlyBranch monthBranch,

            EarthlyBranch dayBranch,

            Optional<EarthlyBranch> hourBranch,

            Set<PillarPosition> clashedPositions,

            Set<PillarPosition> competingCombinationPositions

    ) {

        Objects.requireNonNull(
                occurrence,
                "지지 합 관계는 필수입니다."
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
                dayStem,
                "일간은 필수입니다."
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

        Set<PillarPosition> safeClashedPositions =
                clashedPositions == null
                        ? Set.of()
                        : Set.copyOf(
                        clashedPositions
                );

        Set<PillarPosition> safeCompetingPositions =
                competingCombinationPositions == null
                        ? Set.of()
                        : Set.copyOf(
                        competingCombinationPositions
                );

        FiveElement transformationElement =
                occurrence
                        .transformationElement();

        FiveElement monthElement =
                dayMasterSeasonCalculator
                        .monthCommandElementOf(
                                monthBranch
                        );

        FiveElementSeasonState
                transformationSeasonState =
                dayMasterSeasonCalculator
                        .seasonStateOf(
                                transformationElement,
                                monthElement
                        );

        List<PillarPosition>
                visibleTransformationPositions =
                new ArrayList<>();

        appendVisibleTarget(
                visibleTransformationPositions,
                transformationElement,
                PillarPosition.YEAR,
                yearStem
        );

        appendVisibleTarget(
                visibleTransformationPositions,
                transformationElement,
                PillarPosition.MONTH,
                monthStem
        );

        appendVisibleTarget(
                visibleTransformationPositions,
                transformationElement,
                PillarPosition.DAY,
                dayStem
        );

        hourStem.ifPresent(
                stem ->
                        appendVisibleTarget(
                                visibleTransformationPositions,
                                transformationElement,
                                PillarPosition.HOUR,
                                stem
                        )
        );

        List<BranchTransformationHiddenStemHit>
                hiddenTransformationHits =
                new ArrayList<>();

        appendHiddenTargets(
                hiddenTransformationHits,
                transformationElement,
                PillarPosition.YEAR,
                yearBranch
        );

        appendHiddenTargets(
                hiddenTransformationHits,
                transformationElement,
                PillarPosition.MONTH,
                monthBranch
        );

        appendHiddenTargets(
                hiddenTransformationHits,
                transformationElement,
                PillarPosition.DAY,
                dayBranch
        );

        hourBranch.ifPresent(
                branch ->
                        appendHiddenTargets(
                                hiddenTransformationHits,
                                transformationElement,
                                PillarPosition.HOUR,
                                branch
                        )
        );

        Set<PillarPosition>
                participantClashes =
                safeClashedPositions
                        .stream()
                        .filter(
                                occurrence::involves
                        )
                        .collect(
                                java.util.stream.Collectors
                                        .toUnmodifiableSet()
                        );

        Set<PillarPosition>
                participantCompetition =
                safeCompetingPositions
                        .stream()
                        .filter(
                                occurrence::involves
                        )
                        .collect(
                                java.util.stream.Collectors
                                        .toUnmodifiableSet()
                        );

        return new BranchCombinationTransformationContext(
                occurrence,
                transformationSeasonState,
                visibleTransformationPositions,
                hiddenTransformationHits,
                participantClashes,
                participantCompetition
        );
    }

    private void appendVisibleTarget(

            List<PillarPosition> result,

            FiveElement targetElement,

            PillarPosition position,

            HeavenlyStem stem

    ) {

        if (
                elementOf(
                        stem
                )
                        == targetElement
        ) {

            result.add(
                    position
            );
        }
    }

    private void appendHiddenTargets(

            List<BranchTransformationHiddenStemHit> result,

            FiveElement targetElement,

            PillarPosition position,

            EarthlyBranch branch

    ) {

        List<HiddenStemComponent> hiddenStems =
                hiddenStemSource
                        .hiddenStemsOf(
                                branch
                        );

        if (
                hiddenStems == null
                        ||
                        hiddenStems.isEmpty()
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

            if (
                    elementOf(
                            hiddenStem
                    )
                            != targetElement
            ) {

                continue;
            }

            result.add(
                    new BranchTransformationHiddenStemHit(
                            position,
                            branch,
                            hiddenStem,
                            component.phase()
                    )
            );
        }
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