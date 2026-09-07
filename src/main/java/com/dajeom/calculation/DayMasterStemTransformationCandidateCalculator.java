package com.dajeom.calculation;

import com.dajeom.domain.DayMasterForceStatusAnalysis;
import com.dajeom.domain.DayMasterGroundStatus;
import com.dajeom.domain.DayMasterGroundStatusAnalysis;
import com.dajeom.domain.DayMasterStemCombinationOccurrence;
import com.dajeom.domain.DayMasterStemTransformationCandidate;
import com.dajeom.domain.DayMasterStemTransformationCandidateAnalysis;
import com.dajeom.domain.DayMasterStemTransformationCandidateType;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.FiveElementSeasonState;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 일간이 직접 참여하는 천간오합을 찾아
 * 화기격 검토 후보를 만든다.
 *
 * =================================================
 *
 * 오합:
 *
 * 甲己 → 土
 * 乙庚 → 金
 * 丙辛 → 水
 * 丁壬 → 木
 * 戊癸 → 火
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 천간오합 존재
 * != 합화
 *
 * 합화 가능 Context
 * != 真化
 *
 * 이 계산기는 최종 화기격을 판정하지 않는다.
 */
@Component
public class DayMasterStemTransformationCandidateCalculator {

    private final DayMasterSeasonCalculator
            dayMasterSeasonCalculator;

    public DayMasterStemTransformationCandidateCalculator(

            DayMasterSeasonCalculator
                    dayMasterSeasonCalculator

    ) {

        this.dayMasterSeasonCalculator =
                Objects.requireNonNull(
                        dayMasterSeasonCalculator,
                        "DayMasterSeasonCalculator는 필수입니다."
                );
    }

    public DayMasterStemTransformationCandidateAnalysis analyze(

            HeavenlyStem dayMaster,

            HeavenlyStem yearStem,

            HeavenlyStem monthStem,

            Optional<HeavenlyStem> hourStem,

            EarthlyBranch monthBranch,

            DayMasterGroundStatusAnalysis ground,

            DayMasterForceStatusAnalysis force

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
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                ground,
                "득지 구조 분석은 필수입니다."
        );

        Objects.requireNonNull(
                force,
                "득세 구조 분석은 필수입니다."
        );

        if (
                ground.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "득지 분석의 일간이 대상 일간과 일치하지 않습니다."
            );
        }

        if (
                force.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "득세 분석의 일간이 대상 일간과 일치하지 않습니다."
            );
        }

        List<DayMasterStemCombinationOccurrence> occurrences =
                new ArrayList<>();

        appendOccurrenceIfCombined(
                occurrences,
                dayMaster,
                PillarPosition.YEAR,
                yearStem
        );

        appendOccurrenceIfCombined(
                occurrences,
                dayMaster,
                PillarPosition.MONTH,
                monthStem
        );

        hourStem.ifPresent(
                stem ->
                        appendOccurrenceIfCombined(
                                occurrences,
                                dayMaster,
                                PillarPosition.HOUR,
                                stem
                        )
        );

        /*
         * 일간과 합할 수 있는 상대 천간이
         * 원국에 둘 이상 존재하면
         * 독합이 아닌 경쟁 Context로 보존.
         *
         * 점수나 근거 개수 계산이 아니라
         * 실제 동일 합 상대의 복수 존재를
         * 판별하는 명리 구조다.
         */
        boolean competingCounterpart =
                occurrences.size() > 1;

        boolean rootPresent =
                ground.status()
                        != DayMasterGroundStatus.NO_ROOT;

        boolean generatingSupport =
                force
                        .hasAnyGeneratingSupport();

        boolean sameElementSupport =
                force
                        .hasAnySameElementSupport();

        List<DayMasterStemTransformationCandidate> candidates =
                occurrences
                        .stream()
                        .map(
                                occurrence ->
                                        createCandidate(
                                                occurrence,
                                                monthBranch,
                                                rootPresent,
                                                generatingSupport,
                                                sameElementSupport,
                                                competingCounterpart
                                        )
                        )
                        .toList();

        return new DayMasterStemTransformationCandidateAnalysis(
                dayMaster,
                candidates
        );
    }

    private DayMasterStemTransformationCandidate createCandidate(

            DayMasterStemCombinationOccurrence occurrence,

            EarthlyBranch monthBranch,

            boolean rootPresent,

            boolean generatingSupport,

            boolean sameElementSupport,

            boolean competingCounterpart

    ) {

        FiveElement monthElement =
                dayMasterSeasonCalculator
                        .monthCommandElementOf(
                                monthBranch
                        );

        FiveElementSeasonState transformationSeasonState =
                dayMasterSeasonCalculator
                        .seasonStateOf(
                                occurrence
                                        .transformationElement(),
                                monthElement
                        );

        DayMasterStemTransformationCandidateType type =
                determineCandidateType(
                        occurrence,
                        transformationSeasonState,
                        rootPresent,
                        generatingSupport,
                        sameElementSupport,
                        competingCounterpart
                );

        return new DayMasterStemTransformationCandidate(
                occurrence,
                transformationSeasonState,
                rootPresent,
                generatingSupport,
                sameElementSupport,
                competingCounterpart,
                type
        );
    }

    private DayMasterStemTransformationCandidateType determineCandidateType(

            DayMasterStemCombinationOccurrence occurrence,

            FiveElementSeasonState transformationSeasonState,

            boolean rootPresent,

            boolean generatingSupport,

            boolean sameElementSupport,

            boolean competingCounterpart

    ) {

        /*
         * 독합이 아니면
         * 먼저 경쟁/쟁합 Context로 보존.
         */
        if (
                competingCounterpart
        ) {

            return DayMasterStemTransformationCandidateType
                    .CONTESTED_TRANSFORMATION_REVIEW;
        }

        boolean transformationElementWang =
                transformationSeasonState
                        == FiveElementSeasonState.WANG;

        boolean originalSupportPresent =
                generatingSupport
                        ||
                        sameElementSupport;

        /*
         * 현재 엔진의 엄격한 真化 검토 문턱.
         *
         * - 일간이 실제 오합함
         * - 화신이 월령에서 당령
         * - 일간 본래 근 없음
         * - 일간 생부 없음
         * - 독합
         * - 합 상대가 월간 또는 시간
         *
         * 이것을 통과해도 아직 真化 확정은 아니다.
         */
        if (
                transformationElementWang
                        &&
                        !rootPresent
                        &&
                        !originalSupportPresent
                        &&
                        occurrence
                                .counterpartInMonthOrHour()
        ) {

            return DayMasterStemTransformationCandidateType
                    .TRUE_TRANSFORMATION_REVIEW;
        }

        /*
         * 오합은 존재하지만
         * 진화의 엄격한 문턱에는 미달.
         *
         * 이후 假化 / 不化를 따로 검토.
         */
        return DayMasterStemTransformationCandidateType
                .FAKE_OR_INCOMPLETE_TRANSFORMATION_REVIEW;
    }

    private void appendOccurrenceIfCombined(

            List<DayMasterStemCombinationOccurrence> result,

            HeavenlyStem dayMaster,

            PillarPosition counterpartPosition,

            HeavenlyStem counterpartStem

    ) {

        Optional<FiveElement> transformationElement =
                transformationElementOf(
                        dayMaster,
                        counterpartStem
                );

        transformationElement.ifPresent(
                element ->
                        result.add(
                                new DayMasterStemCombinationOccurrence(
                                        dayMaster,
                                        counterpartStem,
                                        counterpartPosition,
                                        element
                                )
                        )
        );
    }

    /**
     * 일간과 상대 천간이
     * 천간오합 관계일 때 화신 반환.
     */
    public Optional<FiveElement> transformationElementOf(

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

        /*
         * 甲己化土
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.JIA,
                        HeavenlyStem.JI
                )
        ) {

            return Optional.of(
                    FiveElement.EARTH
            );
        }

        /*
         * 乙庚化金
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.YI,
                        HeavenlyStem.GENG
                )
        ) {

            return Optional.of(
                    FiveElement.METAL
            );
        }

        /*
         * 丙辛化水
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.BING,
                        HeavenlyStem.XIN
                )
        ) {

            return Optional.of(
                    FiveElement.WATER
            );
        }

        /*
         * 丁壬化木
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.DING,
                        HeavenlyStem.REN
                )
        ) {

            return Optional.of(
                    FiveElement.WOOD
            );
        }

        /*
         * 戊癸化火
         */
        if (
                pair(
                        first,
                        second,
                        HeavenlyStem.WU,
                        HeavenlyStem.GUI
                )
        ) {

            return Optional.of(
                    FiveElement.FIRE
            );
        }

        return Optional.empty();
    }

    private boolean pair(

            HeavenlyStem first,

            HeavenlyStem second,

            HeavenlyStem a,

            HeavenlyStem b

    ) {

        return (
                first == a
                        &&
                        second == b
        )
                ||
                (
                        first == b
                                &&
                                second == a
                );
    }
}