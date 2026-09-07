package com.dajeom.domain;

import java.util.Objects;

/**
 * 하나의 일간 천간오합에 대한
 * 화기격 후보 Context.
 *
 * 보존:
 *
 * - 어떤 오합인가
 * - 화신 오행
 * - 화신의 월령 旺相休囚死
 * - 일간의 근 존재 여부
 * - 일간 생부 존재 여부
 * - 독합/쟁합 Context
 *
 * 아직 真化/假化를 확정하지 않는다.
 */
public record DayMasterStemTransformationCandidate(

        DayMasterStemCombinationOccurrence occurrence,

        FiveElementSeasonState transformationSeasonState,

        boolean rootPresent,

        boolean generatingSupportPresent,

        boolean sameElementSupportPresent,

        boolean competingCounterpart,

        DayMasterStemTransformationCandidateType candidateType

) {

    public DayMasterStemTransformationCandidate {

        Objects.requireNonNull(
                occurrence,
                "천간오합 정보는 필수입니다."
        );

        Objects.requireNonNull(
                transformationSeasonState,
                "화신의 월령 상태는 필수입니다."
        );

        Objects.requireNonNull(
                candidateType,
                "화기격 후보 상태는 필수입니다."
        );
    }

    public HeavenlyStem dayMaster() {

        return occurrence
                .dayMaster();
    }

    public HeavenlyStem counterpartStem() {

        return occurrence
                .counterpartStem();
    }

    public PillarPosition counterpartPosition() {

        return occurrence
                .counterpartPosition();
    }

    public FiveElement transformationElement() {

        return occurrence
                .transformationElement();
    }

    /**
     * 화신이 정확히 당령한 경우.
     *
     * 真化 확정이 아니라
     * 진화 검토의 중요한 조건이다.
     */
    public boolean transformationElementWang() {

        return transformationSeasonState
                == FiveElementSeasonState.WANG;
    }

    public boolean hasAnyOriginalSupport() {

        return generatingSupportPresent
                ||
                sameElementSupportPresent;
    }

    public boolean rootAbsent() {

        return !rootPresent;
    }

    /**
     * 진화 검토에 필요한 핵심 Context가
     * 형성되어 있는지.
     *
     * 이것도 真化 확정 메서드가 아니다.
     */
    public boolean hasStrictTrueTransformationContext() {

        return transformationElementWang()
                &&
                rootAbsent()
                &&
                !hasAnyOriginalSupport()
                &&
                !competingCounterpart
                &&
                occurrence
                        .counterpartInMonthOrHour();
    }
}