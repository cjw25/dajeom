package com.dajeom.calculator;

import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.DecadeLuckSequenceAnalysis;
import com.dajeom.model.DecadeLuckStartAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.LuckStartAge;
import com.dajeom.model.SexagenaryPillar;
import com.dajeom.type.DecadeLuckDirection;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 13차-3.
 *
 * 대운 간지 Sequence Calculator.
 *
 * =================================================
 *
 * 기준:
 *
 * 월주
 *
 * =================================================
 *
 * 순행:
 *
 * 월주 +1
 * 월주 +2
 * 월주 +3
 * ...
 *
 * =================================================
 *
 * 역행:
 *
 * 월주 -1
 * 월주 -2
 * 월주 -3
 * ...
 *
 * =================================================
 *
 * 첫 대운 기간:
 *
 * 13-2 첫 교운시점
 * ~
 * 첫 교운시점 + 10년
 *
 * 이후 각 대운 역시 정확히 10년씩 진행한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 현재 대운을 여기서 LocalDate.now()로
 * 자동 결정하지 않는다.
 *
 * "현재"는 실행 날짜에 따라 변하는 값이므로
 * 필요할 때 기준 DateTime을 명시적으로 넘겨
 * 조회하도록 한다.
 */
@Component
public class DecadeLuckSequenceCalculator {

    /**
     * 기본적으로 첫 교운 이후
     * 10개 대운 = 100년 구간을 생성한다.
     *
     * 계산 규칙이 아니라
     * 기본 출력 개수 정책이다.
     */
    private static final int
            DEFAULT_PERIOD_COUNT = 10;

    private static final int
            MAX_PERIOD_COUNT = 20;

    private final SexagenaryCycleCalculator
            sexagenaryCycleCalculator;

    public DecadeLuckSequenceCalculator(
            SexagenaryCycleCalculator
                    sexagenaryCycleCalculator
    ) {

        this.sexagenaryCycleCalculator =
                Objects.requireNonNull(
                        sexagenaryCycleCalculator,
                        "SexagenaryCycleCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 기본 API
     * =================================================
     */
    public DecadeLuckSequenceAnalysis analyze(

            FourPillars fourPillars,

            DecadeLuckStartAnalysis startAnalysis

    ) {

        return analyze(
                fourPillars,
                startAnalysis,
                DEFAULT_PERIOD_COUNT
        );
    }

    /**
     * =================================================
     * 대운 개수 지정 API
     * =================================================
     */
    public DecadeLuckSequenceAnalysis analyze(

            FourPillars fourPillars,

            DecadeLuckStartAnalysis startAnalysis,

            int periodCount

    ) {

        Objects.requireNonNull(
                fourPillars,
                "대운 Sequence 계산에는 FourPillars가 필요합니다."
        );

        Objects.requireNonNull(
                startAnalysis,
                "13-2 DecadeLuckStartAnalysis가 필요합니다."
        );

        if (
                periodCount < 1
                        || periodCount > MAX_PERIOD_COUNT
        ) {

            throw new IllegalArgumentException(
                    "대운 생성 개수는 1~"
                            + MAX_PERIOD_COUNT
                            + " 범위여야 합니다."
            );
        }

        Objects.requireNonNull(
                fourPillars.monthPillar(),
                "대운 Sequence 계산에는 월주가 필요합니다."
        );

        HeavenlyStem monthStem =
                Objects.requireNonNull(
                        fourPillars
                                .monthPillar()
                                .stem(),
                        "월간을 찾을 수 없습니다."
                );

        EarthlyBranch monthBranch =
                Objects.requireNonNull(
                        fourPillars
                                .monthPillar()
                                .branch(),
                        "월지를 찾을 수 없습니다."
                );

        SexagenaryPillar monthPillar =
                sexagenaryCycleCalculator
                        .pillarOf(
                                monthStem,
                                monthBranch
                        );

        /*
         * =================================================
         * 절입일 + 시간 미상
         * =================================================
         *
         * 이 경우 출생시각에 따라
         * 월주 자체가 달라질 가능성이 있다.
         *
         * 하나의 대운 Sequence를
         * 임의 확정하지 않는다.
         */

        if (
                startAnalysis
                        .requiresSequenceBoundaryReview()
        ) {

            return new DecadeLuckSequenceAnalysis(
                    startAnalysis,
                    monthPillar,
                    List.of(),
                    false,
                    "출생시간 미상 상태에서 출생일에 절입 경계가 존재하여 "
                            + "월주와 대운 간지 Sequence를 하나로 확정할 수 없습니다."
            );
        }

        DecadeLuckDirection direction =
                startAnalysis
                        .directionAnalysis()
                        .direction();

        /*
         * =================================================
         * 첫 교운 시점 범위
         * =================================================
         */

        ZonedDateTime minimumCandidateStart =
                startAnalysis
                        .minimumCandidate()
                        .firstLuckStartDateTime();

        ZonedDateTime maximumCandidateStart =
                startAnalysis
                        .maximumCandidate()
                        .firstLuckStartDateTime();

        ZonedDateTime earliestFirstStart =
                minimumCandidateStart
                        .isBefore(
                                maximumCandidateStart
                        )
                        ? minimumCandidateStart
                        : maximumCandidateStart;

        ZonedDateTime latestFirstStart =
                minimumCandidateStart
                        .isAfter(
                                maximumCandidateStart
                        )
                        ? minimumCandidateStart
                        : maximumCandidateStart;

        boolean exactTiming =
                startAnalysis.exact();

        List<DecadeLuckPeriod> periods =
                new ArrayList<>();

        for (
                int sequenceNumber = 1;
                sequenceNumber <= periodCount;
                sequenceNumber++
        ) {

            /*
             * =================================================
             * 간지 이동
             * =================================================
             *
             * 첫 대운부터 월주에서 한 칸 이동.
             */

            int cycleStep =
                    direction
                            .cycleStep()
                            * sequenceNumber;

            SexagenaryPillar decadePillar =
                    sexagenaryCycleCalculator
                            .shift(
                                    monthPillar,
                                    cycleStep
                            );

            /*
             * =================================================
             * 시작 나이
             * =================================================
             *
             * 1대운:
             * 기산 나이
             *
             * 2대운:
             * 기산 나이 + 10년
             *
             * 3대운:
             * 기산 나이 + 20년
             */

            int additionalYears =
                    (
                            sequenceNumber - 1
                    ) * 10;

            LuckStartAge minimumStartAge =
                    addYears(
                            startAnalysis
                                    .minimumStartAge(),
                            additionalYears
                    );

            LuckStartAge maximumStartAge =
                    addYears(
                            startAnalysis
                                    .maximumStartAge(),
                            additionalYears
                    );

            /*
             * =================================================
             * 실제 DateTime 범위
             * =================================================
             */

            ZonedDateTime earliestStart =
                    earliestFirstStart
                            .plusYears(
                                    additionalYears
                            );

            ZonedDateTime latestStart =
                    latestFirstStart
                            .plusYears(
                                    additionalYears
                            );

            ZonedDateTime earliestEndExclusive =
                    earliestStart
                            .plusYears(
                                    10
                            );

            ZonedDateTime latestEndExclusive =
                    latestStart
                            .plusYears(
                                    10
                            );

            periods.add(
                    new DecadeLuckPeriod(
                            sequenceNumber,
                            decadePillar,
                            minimumStartAge,
                            maximumStartAge,
                            earliestStart,
                            latestStart,
                            earliestEndExclusive,
                            latestEndExclusive,
                            exactTiming
                    )
            );
        }

        return new DecadeLuckSequenceAnalysis(
                startAnalysis,
                monthPillar,
                periods,
                true,
                ""
        );
    }

    private LuckStartAge addYears(

            LuckStartAge age,

            int additionalYears

    ) {

        Objects.requireNonNull(
                age,
                "대운 시작 나이가 필요합니다."
        );

        if (
                additionalYears < 0
        ) {

            throw new IllegalArgumentException(
                    "추가 연수는 음수일 수 없습니다."
            );
        }

        return new LuckStartAge(
                age.years()
                        + additionalYears,
                age.months(),
                age.days()
        );
    }
}