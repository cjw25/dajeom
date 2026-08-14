package com.dajeom.model;

import com.dajeom.type.LuckUncertaintyCause;
import com.dajeom.type.LuckUncertaintyKind;
import com.dajeom.type.LuckUncertaintyScope;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 정밀화 16차-6.
 *
 * 현재 LuckTimelineAnalysis에 존재하는
 * 불확실성과 데이터 제한을
 * 원인별 Evidence로 통합한다.
 *
 * =================================================
 *
 * 중요한 구분:
 *
 * hasLimitations()
 *
 * 입력·원국·데이터 제한 Evidence가
 * 하나라도 존재하는가.
 *
 * =================================================
 *
 * uncertaintyExists()
 *
 * 그중 현재 결과 자체에 영향을 주는
 * Evidence가 존재하는가.
 *
 * =================================================
 *
 * 따라서:
 *
 * 날짜만 입력했지만
 * 하루 종일 같은 대운·세운·월운이라면
 *
 * hasLimitations() = true
 *
 * uncertaintyExists()는
 * 다른 불확실성이 없다면 false일 수 있다.
 */
public record LuckUncertaintyAnalysis(

        LuckTimelineAnalysis timelineAnalysis,

        List<LuckUncertaintyEvidence> evidences

) {

    public LuckUncertaintyAnalysis {

        Objects.requireNonNull(
                timelineAnalysis,
                "불확실성 분석에는 LuckTimelineAnalysis가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        for (
                LuckUncertaintyEvidence evidence
                : evidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "LuckUncertaintyEvidence는 null일 수 없습니다."
            );
        }

        /*
         * =================================================
         * 같은 Cause는 하나의 통합 Evidence로 유지.
         * =================================================
         */

        for (
                int first = 0;
                first < evidences.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < evidences.size();
                    second++
            ) {

                if (
                        evidences
                                .get(first)
                                .cause()
                                == evidences
                                .get(second)
                                .cause()
                ) {

                    throw new IllegalArgumentException(
                            "동일 불확실성 Cause가 중복되었습니다: "
                                    + evidences
                                    .get(first)
                                    .cause()
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 상위 객체 Delegation
     * =================================================
     */

    public PalaceAnalysis natalAnalysis() {

        return timelineAnalysis
                .natalAnalysis();
    }

    public LuckReferenceContext referenceContext() {

        return timelineAnalysis
                .referenceContext();
    }

    public DecadeLuckSelectionAnalysis decadeSelectionAnalysis() {

        return timelineAnalysis
                .decadeSelectionAnalysis();
    }

    public AnnualLuckSelectionAnalysis annualSelectionAnalysis() {

        return timelineAnalysis
                .annualSelectionAnalysis();
    }

    public MonthlyLuckSelectionAnalysis monthlySelectionAnalysis() {

        return timelineAnalysis
                .monthlySelectionAnalysis();
    }

    /*
     * =================================================
     * 전체 제한
     * =================================================
     */

    public boolean hasLimitations() {

        return !evidences
                .isEmpty();
    }

    /*
     * =================================================
     * 실제 현재 결과 불확실성
     * =================================================
     */

    public boolean uncertaintyExists() {

        return evidences
                .stream()
                .anyMatch(
                        LuckUncertaintyEvidence
                                ::affectsCurrentResult
                );
    }

    public boolean fullyDeterminate() {

        return !uncertaintyExists();
    }

    /*
     * =================================================
     * 현재 결과에 영향을 주는 Evidence
     * =================================================
     */

    public List<LuckUncertaintyEvidence>
    impactingEvidences() {

        return evidences
                .stream()
                .filter(
                        LuckUncertaintyEvidence
                                ::affectsCurrentResult
                )
                .toList();
    }

    /*
     * =================================================
     * 참고용 제한
     * =================================================
     */

    public List<LuckUncertaintyEvidence>
    informationalEvidences() {

        return evidences
                .stream()
                .filter(
                        LuckUncertaintyEvidence
                                ::informationalOnly
                )
                .toList();
    }

    /*
     * =================================================
     * Cause
     * =================================================
     */

    public boolean hasCause(
            LuckUncertaintyCause cause
    ) {

        if (
                cause == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.cause()
                                        == cause
                );
    }

    public List<LuckUncertaintyEvidence>
    evidencesOf(
            LuckUncertaintyCause cause
    ) {

        if (
                cause == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.cause()
                                        == cause
                )
                .toList();
    }

    public Set<LuckUncertaintyCause>
    causes() {

        return evidences
                .stream()
                .map(
                        LuckUncertaintyEvidence
                                ::cause
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    /*
     * =================================================
     * Kind
     * =================================================
     */

    public List<LuckUncertaintyEvidence>
    evidencesOf(
            LuckUncertaintyKind kind
    ) {

        if (
                kind == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.kind()
                                        == kind
                )
                .toList();
    }

    public boolean hasKind(
            LuckUncertaintyKind kind
    ) {

        return !evidencesOf(
                kind
        )
                .isEmpty();
    }

    /*
     * =================================================
     * Scope
     * =================================================
     */

    public List<LuckUncertaintyEvidence>
    evidencesOf(
            LuckUncertaintyScope scope
    ) {

        if (
                scope == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        scope
                                )
                )
                .toList();
    }

    public boolean hasScopeUncertainty(
            LuckUncertaintyScope scope
    ) {

        return evidencesOf(
                scope
        )
                .stream()
                .anyMatch(
                        LuckUncertaintyEvidence
                                ::affectsCurrentResult
                );
    }

    /*
     * =================================================
     * 주요 편의 메서드
     * =================================================
     */

    public boolean natalTimeUnknown() {

        return hasCause(
                LuckUncertaintyCause
                        .NATAL_TIME_UNKNOWN
        );
    }

    public boolean referenceTimeUnspecified() {

        return hasCause(
                LuckUncertaintyCause
                        .REFERENCE_TIME_UNSPECIFIED
        );
    }

    public boolean decadeBoundaryUncertain() {

        return hasCause(
                LuckUncertaintyCause
                        .DECADE_BOUNDARY_UNCERTAIN
        );
    }

    public boolean annualBoundaryUncertain() {

        return hasCause(
                LuckUncertaintyCause
                        .ANNUAL_BOUNDARY_WITHOUT_REFERENCE_TIME
        );
    }

    public boolean monthlyBoundaryUncertain() {

        return hasCause(
                LuckUncertaintyCause
                        .MONTHLY_BOUNDARY_WITHOUT_REFERENCE_TIME
        );
    }

    public boolean decadeAnalysisUnavailable() {

        return hasCause(
                LuckUncertaintyCause
                        .DECADE_ANALYSIS_UNAVAILABLE
        );
    }

    public boolean decadeRangeLimited() {

        return hasCause(
                LuckUncertaintyCause
                        .DECADE_GENERATED_RANGE_EXHAUSTED
        );
    }

    /*
     * =================================================
     * Timeline 수준
     * =================================================
     */

    public boolean timelineUncertaintyExists() {

        return hasScopeUncertainty(
                LuckUncertaintyScope.TIMELINE
        );
    }

    public boolean multipleTimelinePossibilities() {

        LuckReferenceContext reference =
                referenceContext();

        if (
                reference.exact()
        ) {

            return timelineAnalysis
                    .possibleSegmentsAt(
                            reference.requireExactDateTime()
                    )
                    .size()
                    > 1;
        }

        return timelineAnalysis
                .segments()
                .stream()
                .anyMatch(
                        segment ->
                                segment
                                        .extent()
                                        .uncertaintyExists()
                )

                || decadeSelectionAnalysis()
                .uncertaintyExists()

                || annualSelectionAnalysis()
                .uncertaintyExists()

                || monthlySelectionAnalysis()
                .uncertaintyExists();
    }
}