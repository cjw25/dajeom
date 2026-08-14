package com.dajeom.analyzer;

import com.dajeom.model.DecadeLuckCandidate;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.DecadeLuckSelectionAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.model.LuckTimelineAnalysis;
import com.dajeom.model.LuckTimelineSegment;
import com.dajeom.model.LuckUncertaintyAnalysis;
import com.dajeom.model.LuckUncertaintyEvidence;
import com.dajeom.model.MonthlyLuckSelectionAnalysis;
import com.dajeom.model.AnnualLuckSelectionAnalysis;
import com.dajeom.type.LuckTimelineDecadeState;
import com.dajeom.type.LuckUncertaintyCause;
import com.dajeom.type.LuckUncertaintyScope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 16차-6.
 *
 * 16-5 LuckTimelineAnalysis에 이미 존재하는
 * 불확실성 / 입력 제한 / 데이터 범위 제한을
 * 원인별 Evidence로 정규화한다.
 *
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 대운을 다시 계산하지 않는다.
 * - 입춘을 다시 계산하지 않는다.
 * - 절입을 다시 계산하지 않는다.
 * - Timeline을 다시 생성하지 않는다.
 * - 확률값을 만들지 않는다.
 *
 * =================================================
 *
 * 기존 deterministic 결과를 읽어서
 * "왜 단정할 수 없는가"만 구조화한다.
 */
@Component
public class LuckUncertaintyAnalyzer {

    /**
     * =================================================
     * 공식 16-6 API
     * =================================================
     */
    public LuckUncertaintyAnalysis analyze(
            LuckTimelineAnalysis timelineAnalysis
    ) {

        Objects.requireNonNull(
                timelineAnalysis,
                "16-6에는 LuckTimelineAnalysis가 필요합니다."
        );

        LuckReferenceContext referenceContext =
                timelineAnalysis
                        .referenceContext();

        DecadeLuckSelectionAnalysis decadeSelection =
                timelineAnalysis
                        .decadeSelectionAnalysis();

        AnnualLuckSelectionAnalysis annualSelection =
                timelineAnalysis
                        .annualSelectionAnalysis();

        MonthlyLuckSelectionAnalysis monthlySelection =
                timelineAnalysis
                        .monthlySelectionAnalysis();

        List<LuckUncertaintyEvidence> evidences =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 조회시간 미입력
         * =================================================
         */

        if (
                referenceContext.dateOnly()
        ) {

            addReferenceTimeEvidence(
                    timelineAnalysis,
                    decadeSelection,
                    annualSelection,
                    monthlySelection,
                    evidences
            );
        }

        /*
         * =================================================
         * 2.
         * 출생시간 미상
         * =================================================
         */

        if (
                !timelineAnalysis
                        .natalAnalysis()
                        .completeChart()
        ) {

            addNatalTimeUnknownEvidence(
                    timelineAnalysis,
                    evidences
            );
        }

        /*
         * =================================================
         * 3.
         * 대운 자체의 교운시점 불확실성
         * =================================================
         */

        if (
                hasRelevantDecadeBoundaryUncertainty(
                        timelineAnalysis
                )
        ) {

            evidences.add(
                    new LuckUncertaintyEvidence(
                            LuckUncertaintyCause
                                    .DECADE_BOUNDARY_UNCERTAIN,
                            Set.of(
                                    LuckUncertaintyScope.DECADE,
                                    LuckUncertaintyScope.TIMELINE
                            ),
                            true,
                            "현재 조회구간에서 대운 교운시점을 하나의 시각으로 확정할 수 없습니다.",
                            List.of(
                                    "13차 대운 Period의 earliest/latest 경계가 서로 다릅니다.",
                                    "16-5 Timeline에서 해당 경계의 possible 범위와 definite 범위가 일치하지 않습니다.",
                                    "이전 대운과 다음 대운 중 하나를 임의로 선택하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 4.
         * 입춘 당일 DATE_ONLY
         * =================================================
         */

        if (
                annualSelection
                        .multipleCandidates()
        ) {

            evidences.add(
                    new LuckUncertaintyEvidence(
                            LuckUncertaintyCause
                                    .ANNUAL_BOUNDARY_WITHOUT_REFERENCE_TIME,
                            Set.of(
                                    LuckUncertaintyScope.REFERENCE,
                                    LuckUncertaintyScope.ANNUAL,
                                    LuckUncertaintyScope.TIMELINE
                            ),
                            true,
                            "조회 날짜 안에 입춘 경계가 있어 세운을 하나로 확정할 수 없습니다.",
                            List.of(
                                    "16-3에서 두 개의 AnnualLuckCandidate가 반환되었습니다.",
                                    "조회시각이 없으므로 입춘 전인지 입춘 후인지 확정하지 않습니다.",
                                    "이전 세운과 새 세운을 모두 POSSIBLE로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 5.
         * 월 절입 당일 DATE_ONLY
         * =================================================
         */

        if (
                monthlySelection
                        .multipleCandidates()
        ) {

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "16-4에서 두 개의 MonthlyLuckCandidate가 반환되었습니다."
            );

            if (
                    monthlySelection
                            .crossesSajuYearBoundary()
            ) {

                reasons.add(
                        "이번 월운 경계는 입춘이며 이전 명리연도 丑월과 새 명리연도 寅월을 동시에 가릅니다."
                );

            } else if (
                    monthlySelection
                            .crossesMonthlyBoundaryWithinSameSajuYear()
            ) {

                reasons.add(
                        "같은 명리연도 안에서 실제 절입시각을 기준으로 이전 월운과 다음 월운이 갈립니다."
                );
            }

            reasons.add(
                    "조회시각이 없으므로 두 월운 중 하나를 임의로 선택하지 않습니다."
            );

            evidences.add(
                    new LuckUncertaintyEvidence(
                            LuckUncertaintyCause
                                    .MONTHLY_BOUNDARY_WITHOUT_REFERENCE_TIME,
                            Set.of(
                                    LuckUncertaintyScope.REFERENCE,
                                    LuckUncertaintyScope.MONTHLY,
                                    LuckUncertaintyScope.TIMELINE
                            ),
                            true,
                            "조회 날짜 안에 월 절입 경계가 있어 월운을 하나로 확정할 수 없습니다.",
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 6.
         * 대운 분석 자체가 사용 불가
         * =================================================
         */

        if (
                !decadeSelection
                        .selectionAvailable()
        ) {

            String reason =
                    decadeSelection
                            .unavailableReasonOptional()
                            .orElse(
                                    "대운 Sequence를 현재 분석에 사용할 수 없습니다."
                            );

            evidences.add(
                    new LuckUncertaintyEvidence(
                            LuckUncertaintyCause
                                    .DECADE_ANALYSIS_UNAVAILABLE,
                            Set.of(
                                    LuckUncertaintyScope.DECADE,
                                    LuckUncertaintyScope.TIMELINE
                            ),
                            true,
                            "대운 계층을 현재 통합 운 분석에 연결할 수 없습니다.",
                            List.of(
                                    reason,
                                    "세운과 월운 선택 결과는 그대로 유지합니다.",
                                    "대운이 없다고 가정하지 않고 UNAVAILABLE 상태로 보존합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 7.
         * 생성 대운 범위 초과
         * =================================================
         */

        if (
                timelineAnalysis
                        .hasAfterGeneratedRangeSegment()
        ) {

            boolean definiteRangeExhaustion =
                    timelineAnalysis
                            .segmentsOf(
                                    LuckTimelineDecadeState
                                            .AFTER_GENERATED_RANGE
                            )
                            .stream()
                            .anyMatch(
                                    LuckTimelineSegment
                                            ::fullyDefinite
                            );

            String rangeReason =
                    definiteRangeExhaustion
                            ? "조회구간의 일부 또는 전체가 확정적으로 현재 생성한 마지막 대운 이후에 있습니다."
                            : "조회구간이 마지막 생성 대운의 종료 불확실 범위와 겹쳐 생성 범위 이후일 가능성이 있습니다.";

            evidences.add(
                    new LuckUncertaintyEvidence(
                            LuckUncertaintyCause
                                    .DECADE_GENERATED_RANGE_EXHAUSTED,
                            Set.of(
                                    LuckUncertaintyScope.DECADE,
                                    LuckUncertaintyScope.TIMELINE
                            ),
                            true,
                            "현재 생성한 대운 Sequence만으로는 조회 시점의 대운을 끝까지 연결할 수 없습니다.",
                            List.of(
                                    rangeReason,
                                    "AFTER_GENERATED_RANGE는 실제로 대운이 존재하지 않는다는 의미가 아닙니다.",
                                    "필요하면 13차 대운 Sequence 생성 개수를 늘려야 합니다."
                            )
                    )
            );
        }

        return new LuckUncertaintyAnalysis(
                timelineAnalysis,
                evidences
        );
    }

    /*
     * =================================================
     * 조회시간 미입력 Evidence
     * =================================================
     */

    private void addReferenceTimeEvidence(

            LuckTimelineAnalysis timelineAnalysis,

            DecadeLuckSelectionAnalysis decadeSelection,

            AnnualLuckSelectionAnalysis annualSelection,

            MonthlyLuckSelectionAnalysis monthlySelection,

            List<LuckUncertaintyEvidence> evidences

    ) {

        EnumSet<LuckUncertaintyScope> scopes =
                EnumSet.of(
                        LuckUncertaintyScope.REFERENCE
                );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "사용자가 정확한 조회시각을 제공하지 않아 civil-day 전체를 조회범위로 유지합니다."
        );

        boolean affectsCurrentResult =
                false;

        /*
         * =================================================
         * 세운
         * =================================================
         */

        if (
                annualSelection
                        .uncertaintyExists()
        ) {

            scopes.add(
                    LuckUncertaintyScope.ANNUAL
            );

            scopes.add(
                    LuckUncertaintyScope.TIMELINE
            );

            affectsCurrentResult =
                    true;

            reasons.add(
                    "조회 날짜 안에 입춘 경계가 있어 조회시각에 따라 세운이 달라집니다."
            );
        }

        /*
         * =================================================
         * 월운
         * =================================================
         */

        if (
                monthlySelection
                        .uncertaintyExists()
        ) {

            scopes.add(
                    LuckUncertaintyScope.MONTHLY
            );

            scopes.add(
                    LuckUncertaintyScope.TIMELINE
            );

            affectsCurrentResult =
                    true;

            reasons.add(
                    "조회 날짜 안에 절입 경계가 있어 조회시각에 따라 월운이 달라집니다."
            );
        }

        /*
         * =================================================
         * 대운의 정확한 경계가 날짜 안에 존재하는 경우
         * =================================================
         *
         * 대운 earliest/latest가 서로 다른 경우는
         * 출생정보/교운 정밀도 문제이므로
         * DECADE_BOUNDARY_UNCERTAIN에서 따로 처리한다.
         *
         * 여기서는 "정확히 알려진 대운 경계"가
         * DATE_ONLY 하루 안에 존재하는 경우만
         * 조회시간 미입력의 직접 영향으로 본다.
         */

        if (
                decadeSelection.selectionAvailable()
                        && crossesExactDecadeBoundary(
                        timelineAnalysis
                )
        ) {

            scopes.add(
                    LuckUncertaintyScope.DECADE
            );

            scopes.add(
                    LuckUncertaintyScope.TIMELINE
            );

            affectsCurrentResult =
                    true;

            reasons.add(
                    "조회 날짜 안에 정확히 계산된 대운 경계가 있으나 조회시각이 없어 경계 전후를 하나로 선택할 수 없습니다."
            );
        }

        if (
                !affectsCurrentResult
        ) {

            reasons.add(
                    "현재 날짜의 세운·월운 및 확정된 운 경계는 하루 전체에서 동일하므로 조회시간 미입력이 현재 선택 결과를 직접 바꾸지는 않습니다."
            );
        }

        evidences.add(
                new LuckUncertaintyEvidence(
                        LuckUncertaintyCause
                                .REFERENCE_TIME_UNSPECIFIED,
                        scopes,
                        affectsCurrentResult,
                        affectsCurrentResult
                                ? "조회시간이 없어 현재 운의 경계 전후를 완전히 확정할 수 없습니다."
                                : "조회시간은 입력되지 않았지만 현재 운 선택은 날짜 전체에서 동일합니다.",
                        reasons
                )
        );
    }

    /*
     * =================================================
     * 출생시간 미상
     * =================================================
     */

    private void addNatalTimeUnknownEvidence(

            LuckTimelineAnalysis timelineAnalysis,

            List<LuckUncertaintyEvidence> evidences

    ) {

        EnumSet<LuckUncertaintyScope> scopes =
                EnumSet.of(
                        LuckUncertaintyScope.NATAL
                );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "원국 completeChart=false이므로 시주를 생성하지 않습니다."
        );

        reasons.add(
                "HOUR Palace와 시주 기반 Evidence를 임의로 보충하지 않습니다."
        );

        if (
                hasRelevantDecadeBoundaryUncertainty(
                        timelineAnalysis
                )
        ) {

            scopes.add(
                    LuckUncertaintyScope.DECADE
            );

            scopes.add(
                    LuckUncertaintyScope.TIMELINE
            );

            reasons.add(
                    "현재 조회시점의 대운 교운범위에도 출생시간 정밀도 제한이 반영되어 있습니다."
            );
        }

        evidences.add(
                new LuckUncertaintyEvidence(
                        LuckUncertaintyCause
                                .NATAL_TIME_UNKNOWN,
                        scopes,
                        true,
                        "출생시간이 없어 시주와 HOUR Palace를 확정할 수 없습니다.",
                        reasons
                )
        );
    }

    /*
     * =================================================
     * 현재 조회구간에 실제로 영향을 주는
     * 대운 경계 불확실성 확인
     * =================================================
     */

    private boolean hasRelevantDecadeBoundaryUncertainty(
            LuckTimelineAnalysis timelineAnalysis
    ) {

        DecadeLuckSelectionAnalysis selection =
                timelineAnalysis
                        .decadeSelectionAnalysis();

        if (
                !selection
                        .selectionAvailable()
        ) {

            return false;
        }

        /*
         * =================================================
         * ACTIVE Candidate
         * =================================================
         */

        for (
                DecadeLuckCandidate candidate
                : selection.candidates()
        ) {

            DecadeLuckPeriod period =
                    candidate.period();

            if (
                    hasUncertainBoundary(
                            period
                    )
                            && timelineAnalysis
                            .segments()
                            .stream()
                            .filter(
                                    segment ->
                                            segment
                                                    .decadeState()
                                                    .active()
                            )
                            .filter(
                                    segment ->
                                            segment
                                                    .decadeCandidate()
                                                    .orElseThrow()
                                                    == candidate
                            )
                            .anyMatch(
                                    segment ->
                                            segment
                                                    .extent()
                                                    .uncertaintyExists()
                            )
            ) {

                return true;
            }
        }

        List<DecadeLuckPeriod> periods =
                selection
                        .decadeLuckAnalysis()
                        .periods();

        if (
                periods.isEmpty()
        ) {

            return false;
        }

        /*
         * =================================================
         * 첫 대운 시작 불확실성
         * =================================================
         */

        DecadeLuckPeriod first =
                periods.get(0);

        if (
                startBoundaryUncertain(
                        first
                )

                        && timelineAnalysis
                        .segmentsOf(
                                LuckTimelineDecadeState
                                        .BEFORE_FIRST
                        )
                        .stream()
                        .anyMatch(
                                segment ->
                                        segment
                                                .extent()
                                                .uncertaintyExists()
                        )
        ) {

            return true;
        }

        /*
         * =================================================
         * 마지막 생성 대운 종료 불확실성
         * =================================================
         */

        DecadeLuckPeriod last =
                periods.get(
                        periods.size() - 1
                );

        return endBoundaryUncertain(
                last
        )

                && timelineAnalysis
                .segmentsOf(
                        LuckTimelineDecadeState
                                .AFTER_GENERATED_RANGE
                )
                .stream()
                .anyMatch(
                        segment ->
                                segment
                                        .extent()
                                        .uncertaintyExists()
                );
    }

    /*
     * =================================================
     * DATE_ONLY 하루 안에 정확히 알려진
     * 대운 경계가 존재하는가?
     * =================================================
     */

    private boolean crossesExactDecadeBoundary(
            LuckTimelineAnalysis timelineAnalysis
    ) {

        LuckReferenceContext referenceContext =
                timelineAnalysis
                        .referenceContext();

        if (
                !referenceContext
                        .dateOnly()
        ) {

            return false;
        }

        DecadeLuckSelectionAnalysis selection =
                timelineAnalysis
                        .decadeSelectionAnalysis();

        if (
                !selection
                        .selectionAvailable()
        ) {

            return false;
        }

        ZonedDateTime dayStart =
                referenceContext
                        .possibleStartInclusive();

        ZonedDateTime dayEnd =
                referenceContext
                        .possibleEndExclusive();

        for (
                DecadeLuckPeriod period
                : selection
                .decadeLuckAnalysis()
                .periods()
        ) {

            /*
             * 정확한 시작 경계
             */

            ZonedDateTime earliestStart =
                    period
                            .earliestStartDateTime()
                            .withZoneSameInstant(
                                    referenceContext.zoneId()
                            );

            ZonedDateTime latestStart =
                    period
                            .latestStartDateTime()
                            .withZoneSameInstant(
                                    referenceContext.zoneId()
                            );

            if (
                    earliestStart
                            .toInstant()
                            .equals(
                                    latestStart.toInstant()
                            )

                            && insideDay(
                            earliestStart,
                            dayStart,
                            dayEnd
                    )
            ) {

                return true;
            }

            /*
             * 정확한 종료 경계
             */

            ZonedDateTime earliestEnd =
                    period
                            .earliestEndDateTimeExclusive()
                            .withZoneSameInstant(
                                    referenceContext.zoneId()
                            );

            ZonedDateTime latestEnd =
                    period
                            .latestEndDateTimeExclusive()
                            .withZoneSameInstant(
                                    referenceContext.zoneId()
                            );

            if (
                    earliestEnd
                            .toInstant()
                            .equals(
                                    latestEnd.toInstant()
                            )

                            && insideDay(
                            earliestEnd,
                            dayStart,
                            dayEnd
                    )
            ) {

                return true;
            }
        }

        return false;
    }

    /*
     * =================================================
     * 대운 Boundary
     * =================================================
     */

    private boolean hasUncertainBoundary(
            DecadeLuckPeriod period
    ) {

        return startBoundaryUncertain(
                period
        )
                || endBoundaryUncertain(
                period
        );
    }

    private boolean startBoundaryUncertain(
            DecadeLuckPeriod period
    ) {

        return !period
                .earliestStartDateTime()
                .toInstant()
                .equals(
                        period
                                .latestStartDateTime()
                                .toInstant()
                );
    }

    private boolean endBoundaryUncertain(
            DecadeLuckPeriod period
    ) {

        return !period
                .earliestEndDateTimeExclusive()
                .toInstant()
                .equals(
                        period
                                .latestEndDateTimeExclusive()
                                .toInstant()
                );
    }

    private boolean insideDay(

            ZonedDateTime boundary,

            ZonedDateTime dayStartInclusive,

            ZonedDateTime dayEndExclusive

    ) {

        return !boundary
                .isBefore(
                        dayStartInclusive
                )

                && boundary
                .isBefore(
                        dayEndExclusive
                );
    }
}