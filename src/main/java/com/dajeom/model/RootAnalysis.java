package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 하나의 천간에 대한 통근 분석 결과.
 *
 * 통근 여부만 저장하지 않고:
 *
 * - 직접 통근
 * - 동기 보조근
 * - 월지 통근
 * - 정기 통근
 * - 전체 통근 세력
 *
 * 을 분리해서 보존한다.
 */
public record RootAnalysis(

        HeavenlyStem targetStem,

        boolean rooted,

        boolean directRooted,

        boolean monthRooted,

        boolean mainHiddenStemRooted,

        int directRootWeight,

        int supportingRootWeight,

        int totalRootWeight,

        int evidenceCount,

        List<RootEvidence> evidences

) {

    public RootAnalysis {

        Objects.requireNonNull(
                targetStem,
                "통근 대상 천간이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                directRootWeight < 0
                        || supportingRootWeight < 0
                        || totalRootWeight < 0
        ) {
            throw new IllegalArgumentException(
                    "통근 가중치는 음수가 될 수 없습니다."
            );
        }

        if (
                evidenceCount
                        != evidences.size()
        ) {
            throw new IllegalArgumentException(
                    "통근 근거 개수와 실제 Evidence 개수가 일치하지 않습니다."
            );
        }

        if (
                totalRootWeight
                        != directRootWeight
                        + supportingRootWeight
        ) {
            throw new IllegalArgumentException(
                    "전체 통근 가중치가 세부 가중치 합계와 일치하지 않습니다."
            );
        }
    }

    /**
     * 가장 강한 하나의 통근 근거.
     */
    public Optional<RootEvidence> strongestEvidence() {

        return evidences
                .stream()
                .max(
                        Comparator.comparingInt(
                                RootEvidence::effectiveWeight
                        )
                );
    }

    /**
     * 뿌리가 전혀 없는 상태.
     */
    public boolean rootless() {

        return !rooted;
    }

    /**
     * 월지 + 정기에서 직접 뿌리를 확보했는지.
     *
     * 매우 강한 통근 근거로 이후
     * 신강/신약 Analyzer가 활용할 수 있다.
     */
    public boolean strongMonthMainRoot() {

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.monthRoot()
                                        && evidence.mainHiddenStemRoot()
                                        && evidence.directRoot()
                );
    }
}