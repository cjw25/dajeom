package com.dajeom.model;

import com.dajeom.type.DecadeLuckContextAxis;
import com.dajeom.type.DecadeLuckContextStatus;
import com.dajeom.type.DecadeLuckDirection;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 13차-7.
 *
 * 대운 분석 최종 Aggregate.
 *
 * =================================================
 *
 * 포함 단계:
 *
 * 13-1 대운 순·역행
 * 13-2 대운 기산
 * 13-3 대운 간지 Sequence
 * 13-4 대운 십성
 * 13-5 원국 × 대운 관계
 * 13-6 용신·격국·십성·궁위 Context
 *
 * =================================================
 *
 * 이후:
 *
 * 14차 세운
 * 15차 월운
 * 16차 운 통합
 * 18차 생활영역
 * 19차 고민분석
 * 20차 AI Grounding
 *
 * 에서는 대운 관련 정보가 필요하면
 * 이 Aggregate를 기준으로 접근한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 원국 분석 객체를 변경하지 않는다.
 *
 * 대운은 원국에 들어오는
 * 별도의 시간축 Evidence다.
 */
public record DecadeLuckAnalysis(

        HeavenlyStem dayMaster,

        PalaceAnalysis natalAnalysis,

        DecadeLuckDirectionAnalysis directionAnalysis,

        DecadeLuckStartAnalysis startAnalysis,

        DecadeLuckSequenceAnalysis sequenceAnalysis,

        DecadeLuckTenGodAnalysis tenGodAnalysis,

        DecadeLuckNatalRelationAnalysis natalRelationAnalysis,

        DecadeLuckContextAnalysis contextAnalysis

) {

    public DecadeLuckAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "대운 최종 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "대운 최종 분석에는 원국 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                directionAnalysis,
                "13-1 DecadeLuckDirectionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                startAnalysis,
                "13-2 DecadeLuckStartAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                sequenceAnalysis,
                "13-3 DecadeLuckSequenceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodAnalysis,
                "13-4 DecadeLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "13-5 DecadeLuckNatalRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                contextAnalysis,
                "13-6 DecadeLuckContextAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 일간 정합성
         * =================================================
         */

        if (
                natalAnalysis.dayMaster()
                        != dayMaster

                        || tenGodAnalysis.dayMaster()
                        != dayMaster

                        || natalRelationAnalysis.dayMaster()
                        != dayMaster

                        || contextAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "13차 대운 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 13-1 ↔ 13-2
         * =================================================
         */

        if (
                startAnalysis.directionAnalysis()
                        != directionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-2 대운 기산 분석이 현재 13-1 방향 분석을 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 13-2 ↔ 13-3
         * =================================================
         */

        if (
                sequenceAnalysis.startAnalysis()
                        != startAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-3 대운 Sequence가 현재 13-2 기산 분석을 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 13-3 ↔ 13-4
         * =================================================
         */

        if (
                tenGodAnalysis.sequenceAnalysis()
                        != sequenceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-4 대운 십성 분석이 현재 13-3 Sequence를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 13-3 ↔ 13-5
         * =================================================
         */

        if (
                natalRelationAnalysis.sequenceAnalysis()
                        != sequenceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-5 대운 관계 분석이 현재 13-3 Sequence를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 12차 ↔ 13-5
         * =================================================
         */

        if (
                natalRelationAnalysis.natalAnalysis()
                        != natalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-5 대운 관계 분석이 현재 원국 PalaceAnalysis를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 13-4 / 13-5 ↔ 13-6
         * =================================================
         */

        if (
                contextAnalysis.tenGodAnalysis()
                        != tenGodAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-6 Context가 현재 13-4 대운 십성 분석을 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.natalRelationAnalysis()
                        != natalRelationAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-6 Context가 현재 13-5 대운 관계 분석을 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 기준 월주 정합성
         * =================================================
         */

        if (
                !sequenceAnalysis
                        .referenceMonthPillar()
                        .samePillar(
                                natalAnalysis
                                        .monthStemEvidence()
                                        .stem(),

                                natalAnalysis
                                        .monthBranchEvidence()
                                        .branch()
                        )
        ) {

            throw new IllegalArgumentException(
                    "13차 대운 Sequence의 기준 월주와 원국 월주가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 분석 가능 상태 정합성
         * =================================================
         *
         * Sequence가 확정되지 않으면
         * 13-4 / 13-5 / 13-6 역시
         * 확정 분석이 불가능해야 한다.
         */

        if (
                sequenceAnalysis.sequenceAvailable()
                        != tenGodAnalysis.analysisAvailable()
        ) {

            throw new IllegalArgumentException(
                    "13-3과 13-4의 분석 가능 상태가 일치하지 않습니다."
            );
        }

        if (
                sequenceAnalysis.sequenceAvailable()
                        != natalRelationAnalysis.analysisAvailable()
        ) {

            throw new IllegalArgumentException(
                    "13-3과 13-5의 분석 가능 상태가 일치하지 않습니다."
            );
        }

        if (
                sequenceAnalysis.sequenceAvailable()
                        != contextAnalysis.analysisAvailable()
        ) {

            throw new IllegalArgumentException(
                    "13-3과 13-6의 분석 가능 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 출생시간 상태 정합성
         * =================================================
         *
         * 시간 확인:
         *
         * PalaceAnalysis = 4주
         * LuckStart = EXACT
         *
         * 시간 미상:
         *
         * PalaceAnalysis = 3주
         * LuckStart = Range
         */

        if (
                startAnalysis.exact()
                        != natalAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "출생시간 상태와 원국 PalaceAnalysis의 completeChart 상태가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 방향
     * =================================================
     */

    public DecadeLuckDirection direction() {

        return directionAnalysis
                .direction();
    }

    public boolean forward() {

        return direction()
                .forward();
    }

    public boolean reverse() {

        return direction()
                .reverse();
    }

    /*
     * =================================================
     * 출생시간 / 기산
     * =================================================
     */

    public boolean exactTiming() {

        return startAnalysis
                .exact();
    }

    public boolean birthTimeUnknown() {

        return startAnalysis
                .birthTimeUnknown();
    }

    public LuckStartAge minimumStartAge() {

        return startAnalysis
                .minimumStartAge();
    }

    public LuckStartAge maximumStartAge() {

        return startAnalysis
                .maximumStartAge();
    }

    public boolean startAgeRange() {

        return startAnalysis
                .hasRange();
    }

    public boolean solarTermBoundaryUncertain() {

        return startAnalysis
                .solarTermBoundaryUncertain();
    }

    /*
     * =================================================
     * Sequence 상태
     * =================================================
     */

    public boolean sequenceAvailable() {

        return sequenceAnalysis
                .sequenceAvailable();
    }

    public boolean analysisAvailable() {

        return contextAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        if (
                analysisAvailable()
        ) {

            return Optional.empty();
        }

        String reason =
                contextAnalysis
                        .unavailableReason();

        if (
                reason == null
                        || reason.isBlank()
        ) {

            reason =
                    sequenceAnalysis
                            .unavailableReason();
        }

        return reason == null
                || reason.isBlank()

                ? Optional.empty()

                : Optional.of(
                reason
        );
    }

    /*
     * =================================================
     * 대운 Period
     * =================================================
     */

    public List<DecadeLuckPeriod> periods() {

        return sequenceAnalysis
                .periods();
    }

    public int periodCount() {

        return sequenceAnalysis
                .periodCount();
    }

    public Optional<DecadeLuckPeriod> period(
            int sequenceNumber
    ) {

        return sequenceAnalysis
                .period(
                        sequenceNumber
                );
    }

    /*
     * =================================================
     * 13-4 십성
     * =================================================
     */

    public Optional<DecadeLuckPeriodTenGodAnalysis>
    tenGodAnalysisOf(
            int sequenceNumber
    ) {

        return tenGodAnalysis
                .periodAnalysis(
                        sequenceNumber
                );
    }

    public List<DecadeLuckTenGodEvidence>
    tenGodEvidencesOf(
            StructureTenGodType tenGodType
    ) {

        return tenGodAnalysis
                .evidencesOf(
                        tenGodType
                );
    }

    /*
     * =================================================
     * 13-5 원국 관계
     * =================================================
     */

    public Optional<DecadeLuckPeriodNatalRelationAnalysis>
    natalRelationAnalysisOf(
            int sequenceNumber
    ) {

        return natalRelationAnalysis
                .periodAnalysis(
                        sequenceNumber
                );
    }

    public List<DecadeLuckPeriodNatalRelationAnalysis>
    periodsInvolving(
            PalacePosition position
    ) {

        return natalRelationAnalysis
                .periodsInvolving(
                        position
                );
    }

    /*
     * =================================================
     * 13-6 Context
     * =================================================
     */

    public Optional<DecadeLuckPeriodContextAnalysis>
    contextOf(
            int sequenceNumber
    ) {

        return contextAnalysis
                .periodAnalysis(
                        sequenceNumber
                );
    }

    public List<DecadeLuckContextSignal> contextSignals() {

        return contextAnalysis
                .signals();
    }

    public List<DecadeLuckContextSignal> contextSignalsOf(
            DecadeLuckContextAxis axis
    ) {

        return contextAnalysis
                .signalsOf(
                        axis
                );
    }

    public List<DecadeLuckContextSignal> contextSignalsOf(
            DecadeLuckContextStatus status
    ) {

        return contextAnalysis
                .signalsOf(
                        status
                );
    }

    public List<DecadeLuckPeriodContextAnalysis>
    mixedPeriods() {

        return contextAnalysis
                .mixedPeriods();
    }

    /*
     * =================================================
     * 날짜 기준 조회
     * =================================================
     *
     * LocalDate.now()를 내부에서 사용하지 않는다.
     *
     * 호출자가 원하는 기준시점을
     * 명시적으로 전달한다.
     */

    public Optional<DecadeLuckPeriod> exactPeriodAt(
            ZonedDateTime dateTime
    ) {

        return sequenceAnalysis
                .exactPeriodAt(
                        dateTime
                );
    }

    public List<DecadeLuckPeriod> possiblePeriodsAt(
            ZonedDateTime dateTime
    ) {

        return sequenceAnalysis
                .possiblePeriodsAt(
                        dateTime
                );
    }

    public Optional<DecadeLuckPeriod> definitePeriodAt(
            ZonedDateTime dateTime
    ) {

        return sequenceAnalysis
                .definitePeriodAt(
                        dateTime
                );
    }

    /*
     * =================================================
     * 특정 날짜의 Context
     * =================================================
     */

    public Optional<DecadeLuckPeriodContextAnalysis>
    exactContextAt(
            ZonedDateTime dateTime
    ) {

        return exactPeriodAt(
                dateTime
        )
                .flatMap(
                        period ->
                                contextOf(
                                        period.sequenceNumber()
                                )
                );
    }

    /**
     * 출생시간 미상일 경우 교운 경계에서는
     * 2개의 Context가 반환될 수 있다.
     */
    public List<DecadeLuckPeriodContextAnalysis>
    possibleContextsAt(
            ZonedDateTime dateTime
    ) {

        return possiblePeriodsAt(
                dateTime
        )
                .stream()
                .map(
                        period ->
                                contextOf(
                                        period.sequenceNumber()
                                )
                                        .orElseThrow()
                )
                .toList();
    }

    public Optional<DecadeLuckPeriodContextAnalysis>
    definiteContextAt(
            ZonedDateTime dateTime
    ) {

        return definitePeriodAt(
                dateTime
        )
                .flatMap(
                        period ->
                                contextOf(
                                        period.sequenceNumber()
                                )
                );
    }
}