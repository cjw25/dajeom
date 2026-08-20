package com.dajeom.engine;

import com.dajeom.model.AnnualCrossContextMatch;
import com.dajeom.model.AnnualCrossFunctionalPatternEffect;
import com.dajeom.model.AnnualCrossFunctionalSynthesis;
import com.dajeom.model.AnnualCrossFunctionalSynthesisAnalysis;
import com.dajeom.model.AnnualCrossFunctionalSynthesisSegmentAnalysis;
import com.dajeom.model.AnnualLuckContextSignal;
import com.dajeom.model.AnnualLuckDecadeContextAnalysis;
import com.dajeom.model.AnnualResolvedCrossFunctionalAnalysis;
import com.dajeom.model.AnnualResolvedCrossFunctionalSegmentAnalysis;
import com.dajeom.type.AnnualContextTenGodMatchLevel;
import com.dajeom.type.AnnualCrossContextScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Annual CrossFunctional Pattern과
 * 14-6 Context Signal을
 * 구조적으로 연결하는 Synthesis Engine.
 *
 * =================================================
 *
 * Match Source:
 *
 * Pattern
 * ->
 * sourcePattern
 * ->
 * sourceSignal
 * ->
 * involvedTenGodTypes
 *
 * =================================================
 *
 * Context:
 *
 * AnnualLuckContextSignal.tenGodTypes
 *
 * =================================================
 *
 * 문자열 파싱 없음.
 *
 * code / summary / reasons를
 * Match 판정에 사용하지 않는다.
 *
 * =================================================
 *
 * Context Scope:
 *
 * annualSignals
 * ->
 * ANNUAL_COMMON
 *
 * segmentSignals
 * ->
 * DECADE_SEGMENT
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * EXACT는 독립 Evidence가 아니다.
 *
 * 2.
 * Context SUPPORTED로
 * Pattern Level을 승격하지 않는다.
 *
 * 3.
 * Context WEAKENED / CONFLICTED로
 * Pattern이나 Function을 삭제하지 않는다.
 *
 * 4.
 * 원국 REFERENCE Pattern은
 * 끝까지 REFERENCE다.
 *
 * 5.
 * tenGodTypes가 비어 있는 Signal은
 * 특정 Pattern에 강제로 연결하지 않는다.
 *
 * 6.
 * Annual Common과 Decade Segment Context를
 * 시간 provenance로 분리해서 보존한다.
 *
 * =================================================
 * Runtime Diagnostic
 * =================================================
 *
 * Cross Synthesis 성능 병목 진단을 위해
 *
 * - Segment 수
 * - Pattern Effect 수
 * - Context Signal 수
 * - Candidate pair 수
 * - Match 판정 시간
 * - Match 객체 생성 시간
 * - Synthesis 객체 생성 시간
 *
 * 을 기록한다.
 *
 * 판정 의미와 결과 구조는 변경하지 않는다.
 */
@Component
public class AnnualCrossFunctionalSynthesisAnalysisEngine {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AnnualCrossFunctionalSynthesisAnalysisEngine.class
            );

    private static final AtomicLong
            DIAGNOSTIC_SEQUENCE =
            new AtomicLong();

    public AnnualCrossFunctionalSynthesisAnalysis analyze(

            AnnualResolvedCrossFunctionalAnalysis
                    sourceAnalysis

    ) {

        Objects.requireNonNull(
                sourceAnalysis,
                "Annual CrossFunctional Synthesis에는 AnnualResolvedCrossFunctionalAnalysis가 필요합니다."
        );

        long diagnosticId =
                DIAGNOSTIC_SEQUENCE.incrementAndGet();

        long analysisStartedAt =
                System.nanoTime();

        boolean completeChart =
                sourceAnalysis
                        .completeChart();

        log.info(
                "[ANNUAL-CROSS-SYNTH-DIAG] id={} analysis-start available={} segmentCount={}",
                diagnosticId,
                sourceAnalysis.analysisAvailable(),
                sourceAnalysis.segmentAnalyses().size()
        );

        if (
                !sourceAnalysis
                        .analysisAvailable()
        ) {

            AnnualCrossFunctionalSynthesisAnalysis result =
                    new AnnualCrossFunctionalSynthesisAnalysis(
                            sourceAnalysis,
                            List.of(),
                            completeChart
                    );

            log.info(
                    "[ANNUAL-CROSS-SYNTH-DIAG] id={} analysis-complete unavailable=true elapsedMs={}",
                    diagnosticId,
                    elapsedMillis(
                            analysisStartedAt
                    )
            );

            return result;
        }

        List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
                segmentAnalyses =
                new ArrayList<>();

        int segmentIndex =
                0;

        for (
                AnnualResolvedCrossFunctionalSegmentAnalysis sourceSegment
                : sourceAnalysis.segmentAnalyses()
        ) {

            segmentIndex++;

            long segmentStartedAt =
                    System.nanoTime();

            log.info(
                    "[ANNUAL-CROSS-SYNTH-DIAG] id={} segment-start index={}",
                    diagnosticId,
                    segmentIndex
            );

            AnnualCrossFunctionalSynthesisSegmentAnalysis segmentAnalysis =
                    synthesizeSegment(
                            diagnosticId,
                            segmentIndex,
                            sourceSegment,
                            completeChart
                    );

            segmentAnalyses.add(
                    segmentAnalysis
            );

            log.info(
                    "[ANNUAL-CROSS-SYNTH-DIAG] id={} segment-complete index={} elapsedMs={}",
                    diagnosticId,
                    segmentIndex,
                    elapsedMillis(
                            segmentStartedAt
                    )
            );
        }

        long resultStartedAt =
                System.nanoTime();

        AnnualCrossFunctionalSynthesisAnalysis result =
                new AnnualCrossFunctionalSynthesisAnalysis(
                        sourceAnalysis,
                        List.copyOf(
                                segmentAnalyses
                        ),
                        completeChart
                );

        log.info(
                "[ANNUAL-CROSS-SYNTH-DIAG] id={} analysis-record-create elapsedMs={}",
                diagnosticId,
                elapsedMillis(
                        resultStartedAt
                )
        );

        log.info(
                "[ANNUAL-CROSS-SYNTH-DIAG] id={} analysis-complete elapsedMs={}",
                diagnosticId,
                elapsedMillis(
                        analysisStartedAt
                )
        );

        return result;
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    private AnnualCrossFunctionalSynthesisSegmentAnalysis
    synthesizeSegment(

            long diagnosticId,

            int segmentIndex,

            AnnualResolvedCrossFunctionalSegmentAnalysis
                    sourceSegment,

            boolean completeChart

    ) {

        long contextStartedAt =
                System.nanoTime();

        AnnualLuckDecadeContextAnalysis contextAnalysis =
                sourceSegment
                        .sourceSegment()
                        .annualDecadeContext();

        List<AnnualCrossFunctionalPatternEffect> patternEffects =
                sourceSegment
                        .patternEffects();

        List<AnnualLuckContextSignal> annualSignals =
                contextAnalysis
                        .annualSignals();

        List<AnnualLuckContextSignal> segmentSignals =
                contextAnalysis
                        .segmentSignals();

        long annualCandidateCount =
                (long) patternEffects.size()
                        *
                        annualSignals.size();

        long segmentCandidateCount =
                (long) patternEffects.size()
                        *
                        segmentSignals.size();

        log.info(
                "[ANNUAL-CROSS-SYNTH-DIAG] id={} segment={} shape patternCount={} annualSignalCount={} segmentSignalCount={} annualCandidatePairs={} segmentCandidatePairs={} shapeElapsedMs={}",
                diagnosticId,
                segmentIndex,
                patternEffects.size(),
                annualSignals.size(),
                segmentSignals.size(),
                annualCandidateCount,
                segmentCandidateCount,
                elapsedMillis(
                        contextStartedAt
                )
        );

        List<AnnualCrossFunctionalSynthesis> syntheses =
                new ArrayList<>(
                        patternEffects.size()
                );

        long totalResolveNanos =
                0L;

        long totalMatchCreateNanos =
                0L;

        long totalSynthesisCreateNanos =
                0L;

        long totalCandidatePairs =
                0L;

        long totalMatchedPairs =
                0L;

        int patternIndex =
                0;

        for (
                AnnualCrossFunctionalPatternEffect patternEffect
                : patternEffects
        ) {

            patternIndex++;

            List<AnnualCrossContextMatch> matches =
                    new ArrayList<>();

            AppendMatchDiagnostic annualDiagnostic =
                    appendMatches(
                            patternEffect,
                            annualSignals,
                            AnnualCrossContextScope
                                    .ANNUAL_COMMON,
                            completeChart,
                            matches
                    );

            AppendMatchDiagnostic segmentDiagnostic =
                    appendMatches(
                            patternEffect,
                            segmentSignals,
                            AnnualCrossContextScope
                                    .DECADE_SEGMENT,
                            completeChart,
                            matches
                    );

            totalResolveNanos +=
                    annualDiagnostic.resolveNanos()
                            +
                            segmentDiagnostic.resolveNanos();

            totalMatchCreateNanos +=
                    annualDiagnostic.matchCreateNanos()
                            +
                            segmentDiagnostic.matchCreateNanos();

            totalCandidatePairs +=
                    annualDiagnostic.candidateCount()
                            +
                            segmentDiagnostic.candidateCount();

            totalMatchedPairs +=
                    annualDiagnostic.matchedCount()
                            +
                            segmentDiagnostic.matchedCount();

            long synthesisStartedAt =
                    System.nanoTime();

            AnnualCrossFunctionalSynthesis synthesis =
                    new AnnualCrossFunctionalSynthesis(
                            patternEffect,
                            List.copyOf(
                                    matches
                            ),
                            completeChart
                    );

            totalSynthesisCreateNanos +=
                    System.nanoTime()
                            -
                            synthesisStartedAt;

            syntheses.add(
                    synthesis
            );

            if (
                    patternIndex == 1
                            || patternIndex % 100 == 0
                            || patternIndex == patternEffects.size()
            ) {

                log.info(
                        "[ANNUAL-CROSS-SYNTH-DIAG] id={} segment={} pattern-progress={}/{} matchesSoFar={} candidatePairsSoFar={}",
                        diagnosticId,
                        segmentIndex,
                        patternIndex,
                        patternEffects.size(),
                        totalMatchedPairs,
                        totalCandidatePairs
                );
            }
        }

        log.info(
                "[ANNUAL-CROSS-SYNTH-DIAG] id={} segment={} loop-complete patternCount={} candidatePairs={} matchedPairs={} resolveMs={} matchCreateMs={} synthesisCreateMs={}",
                diagnosticId,
                segmentIndex,
                patternEffects.size(),
                totalCandidatePairs,
                totalMatchedPairs,
                nanosToMillis(
                        totalResolveNanos
                ),
                nanosToMillis(
                        totalMatchCreateNanos
                ),
                nanosToMillis(
                        totalSynthesisCreateNanos
                )
        );

        long segmentRecordStartedAt =
                System.nanoTime();

        AnnualCrossFunctionalSynthesisSegmentAnalysis result =
                new AnnualCrossFunctionalSynthesisSegmentAnalysis(
                        sourceSegment,
                        List.copyOf(
                                syntheses
                        ),
                        completeChart
                );

        log.info(
                "[ANNUAL-CROSS-SYNTH-DIAG] id={} segment={} segment-record-create elapsedMs={}",
                diagnosticId,
                segmentIndex,
                elapsedMillis(
                        segmentRecordStartedAt
                )
        );

        return result;
    }

    /*
     * =================================================
     * Context Match
     * =================================================
     */

    private AppendMatchDiagnostic appendMatches(

            AnnualCrossFunctionalPatternEffect patternEffect,

            List<AnnualLuckContextSignal> signals,

            AnnualCrossContextScope contextScope,

            boolean completeChart,

            List<AnnualCrossContextMatch> results

    ) {

        long resolveNanos =
                0L;

        long matchCreateNanos =
                0L;

        long candidateCount =
                0L;

        long matchedCount =
                0L;

        for (
                AnnualLuckContextSignal signal
                : signals
        ) {

            candidateCount++;

            long resolveStartedAt =
                    System.nanoTime();

            AnnualContextTenGodMatchLevel matchLevel =
                    AnnualCrossContextMatch
                            .resolveMatchLevel(
                                    patternEffect,
                                    signal
                            );

            resolveNanos +=
                    System.nanoTime()
                            -
                            resolveStartedAt;

            if (
                    !matchLevel
                            .matched()
            ) {

                continue;
            }

            matchedCount++;

            long createStartedAt =
                    System.nanoTime();

            AnnualCrossContextMatch match =
                    new AnnualCrossContextMatch(
                            patternEffect,
                            signal,
                            contextScope,
                            matchLevel,
                            completeChart
                    );

            matchCreateNanos +=
                    System.nanoTime()
                            -
                            createStartedAt;

            results.add(
                    match
            );
        }

        return new AppendMatchDiagnostic(
                candidateCount,
                matchedCount,
                resolveNanos,
                matchCreateNanos
        );
    }

    /*
     * =================================================
     * Diagnostic Value
     * =================================================
     */

    private record AppendMatchDiagnostic(

            long candidateCount,

            long matchedCount,

            long resolveNanos,

            long matchCreateNanos

    ) {
    }

    /*
     * =================================================
     * Diagnostic Utility
     * =================================================
     */

    private long elapsedMillis(
            long startedAt
    ) {

        return (
                System.nanoTime()
                        -
                        startedAt
        )
                /
                1_000_000L;
    }

    private long nanosToMillis(
            long nanos
    ) {

        return nanos
                /
                1_000_000L;
    }
}