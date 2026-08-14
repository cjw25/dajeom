package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PathologyCategory;
import com.dajeom.type.PathologyPatternType;
import com.dajeom.type.PathologySeverity;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 6차-4.
 *
 * 원국 전체 병(病) 구조 분석 결과.
 *
 * 중요한 원칙:
 *
 * 병 구조가 여러 개 동시에 존재할 수 있다.
 *
 * 예:
 *
 * 재성 과다
 * +
 * 신약 일간의 재성 부담
 * +
 * 한조 환경
 *
 * 을 하나의 점수로 합쳐버리지 않는다.
 *
 * 각 Evidence를 별도로 보존하고
 * overallSeverity는 가장 강한 병 구조의
 * severity만 나타낸다.
 */
public record PathologyAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        List<PathologyEvidence> evidences,

        PathologySeverity overallSeverity,

        boolean completeChart

) {

    public PathologyAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                overallSeverity,
                "전체 병 구조 심각도가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        boolean duplicatedPattern =
                evidences
                        .stream()
                        .map(
                                PathologyEvidence
                                        ::patternType
                        )
                        .filter(
                                pattern ->
                                        !pattern
                                                .elementConcentrationPattern()
                        )
                        .distinct()
                        .count()
                        != evidences
                        .stream()
                        .filter(
                                evidence ->
                                        !evidence
                                                .patternType()
                                                .elementConcentrationPattern()
                        )
                        .count();

        if (
                duplicatedPattern
        ) {

            throw new IllegalArgumentException(
                    "동일한 병 구조 Evidence가 중복되었습니다."
            );
        }

        /*
         * ELEMENT_OVERCONCENTRATION은
         * 木火土金水 각각 발생할 수 있으므로
         * 패턴 타입만으로 중복 검사하지 않는다.
         */
        List<PathologyEvidence> concentrationEvidences =
                evidences
                        .stream()
                        .filter(
                                evidence ->
                                        evidence
                                                .patternType()
                                                .elementConcentrationPattern()
                        )
                        .toList();

        long uniqueConcentrationElements =
                concentrationEvidences
                        .stream()
                        .flatMap(
                                evidence ->
                                        evidence
                                                .relatedElements()
                                                .stream()
                        )
                        .distinct()
                        .count();

        if (
                uniqueConcentrationElements
                        != concentrationEvidences.size()
        ) {

            throw new IllegalArgumentException(
                    "동일 오행의 과다집중 Evidence가 중복되었습니다."
            );
        }

        PathologySeverity calculatedOverall =
                evidences
                        .stream()
                        .map(
                                PathologyEvidence
                                        ::severity
                        )
                        .max(
                                Comparator.comparingInt(
                                        PathologySeverity
                                                ::rank
                                )
                        )
                        .orElse(
                                PathologySeverity.NONE
                        );

        if (
                calculatedOverall
                        != overallSeverity
        ) {

            throw new IllegalArgumentException(
                    "전체 병 구조 심각도가 Evidence와 일치하지 않습니다."
            );
        }
    }

    public boolean hasPathology() {

        return !evidences.isEmpty();
    }

    public boolean hasMeaningfulPathology() {

        return evidences
                .stream()
                .anyMatch(
                        PathologyEvidence
                                ::meaningful
                );
    }

    public boolean hasSeverePathology() {

        return overallSeverity
                .severe();
    }

    public boolean hasCriticalPathology() {

        return overallSeverity
                .critical();
    }

    public List<PathologyEvidence>
    evidencesOf(
            PathologyCategory category
    ) {

        if (
                category == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.category()
                                        == category
                )
                .toList();
    }

    public List<PathologyEvidence>
    severeEvidences() {

        return evidences
                .stream()
                .filter(
                        PathologyEvidence
                                ::severe
                )
                .toList();
    }

    public List<PathologyEvidence>
    evidencesInvolving(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.involves(
                                        element
                                )
                )
                .toList();
    }

    public boolean hasPattern(
            PathologyPatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.patternType()
                                        == patternType
                );
    }

    public Optional<PathologyEvidence>
    strongestEvidence() {

        return evidences
                .stream()
                .max(
                        Comparator.comparingInt(
                                evidence ->
                                        evidence
                                                .severity()
                                                .rank()
                        )
                );
    }

    public int pathologyCount() {

        return evidences.size();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}