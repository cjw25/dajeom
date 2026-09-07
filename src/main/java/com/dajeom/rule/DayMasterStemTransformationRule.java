package com.dajeom.rule;

import com.dajeom.domain.DayMasterStemTransformationAssessment;
import com.dajeom.domain.DayMasterStemTransformationCandidate;
import com.dajeom.domain.DayMasterStemTransformationCandidateAnalysis;
import com.dajeom.domain.DayMasterStemTransformationCandidateType;
import com.dajeom.domain.DayMasterStemTransformationReason;
import com.dajeom.domain.DayMasterStemTransformationState;
import com.dajeom.domain.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 일간이 직접 참여한 천간오합을
 *
 * - 真化
 * - 假化
 * - 不化
 * - 爭合
 *
 * 로 분류한다.
 *
 * =================================================
 * 중요
 * =================================================
 *
 * 점수 없음.
 * 퍼센트 없음.
 * 근 개수 합산 없음.
 *
 * =================================================
 *
 * 현재 엔진의 엄격한 真化 기준:
 *
 * 1. 일간이 실제 천간오합의 당사자
 *
 * 2. 상대가 월간 또는 시간
 *
 * 3. 화신이 월령에서 旺
 *
 * 4. 일간 본래 지지 근 없음
 *
 * 5. 일간을 생하는 인성 방향 없음
 *
 * 6. 일간과 같은 오행 비겁 방향 없음
 *
 * 7. 동일 합 상대가 하나뿐인 독합
 *
 * =================================================
 *
 * 真化 조건 중 일부가 깨졌지만
 * 실제 오합과 화하려는 구조가 남아 있으면
 * 假化로 분리한다.
 *
 * 다만 상대가 년간에만 있는 경우는
 * 현재 엔진의 보수적 기준에서 不化로 둔다.
 */
@Component
public class DayMasterStemTransformationRule {

    public List<DayMasterStemTransformationAssessment> assess(

            HeavenlyStem dayMaster,

            DayMasterStemTransformationCandidateAnalysis
                    candidateAnalysis

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                candidateAnalysis,
                "천간 화기 후보 분석은 필수입니다."
        );

        if (
                candidateAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "화기 후보 분석의 일간이 대상 일간과 일치하지 않습니다."
            );
        }

        return candidateAnalysis
                .candidates()
                .stream()
                .map(
                        this::assessOne
                )
                .toList();
    }

    private DayMasterStemTransformationAssessment assessOne(

            DayMasterStemTransformationCandidate candidate

    ) {

        Objects.requireNonNull(
                candidate,
                "화기 후보는 필수입니다."
        );

        /*
         * =================================================
         * 1. 爭合
         * =================================================
         *
         * 동일 일간을 합 상대가 복수로 당김.
         *
         * 이런 경우 진화/가화를
         * 억지로 확정하지 않는다.
         */
        if (
                candidate.competingCounterpart()

                        ||

                        candidate.candidateType()
                                == DayMasterStemTransformationCandidateType
                                .CONTESTED_TRANSFORMATION_REVIEW
        ) {

            return new DayMasterStemTransformationAssessment(
                    candidate,
                    DayMasterStemTransformationState.CONTESTED,
                    Set.of(
                            DayMasterStemTransformationReason
                                    .COMPETING_COUNTERPART
                    )
            );
        }

        /*
         * =================================================
         * 2. 真化
         * =================================================
         *
         * 이전 Candidate 단계에서 만든
         * 엄격한 진화 Context 전체를
         * 다시 확인한다.
         */
        if (
                candidate
                        .hasStrictTrueTransformationContext()
        ) {

            return new DayMasterStemTransformationAssessment(
                    candidate,
                    DayMasterStemTransformationState
                            .TRUE_TRANSFORMATION,
                    Set.of(
                            DayMasterStemTransformationReason
                                    .STRICT_TRUE_TRANSFORMATION_CONTEXT
                    )
            );
        }

        /*
         * =================================================
         * 3. 진화가 되지 못한 이유 수집
         * =================================================
         *
         * 숫자 합산이 아니라
         * 실제 명리 조건들을 그대로 저장한다.
         */
        EnumSet<DayMasterStemTransformationReason> reasons =
                EnumSet.noneOf(
                        DayMasterStemTransformationReason.class
                );

        if (
                !candidate
                        .transformationElementWang()
        ) {

            reasons.add(
                    DayMasterStemTransformationReason
                            .TRANSFORMATION_NOT_COMMANDING_SEASON
            );
        }

        if (
                candidate.rootPresent()
        ) {

            reasons.add(
                    DayMasterStemTransformationReason
                            .ORIGINAL_ROOT_REMAINS
            );
        }

        if (
                candidate
                        .generatingSupportPresent()
        ) {

            reasons.add(
                    DayMasterStemTransformationReason
                            .ORIGINAL_GENERATING_SUPPORT_REMAINS
            );
        }

        if (
                candidate
                        .sameElementSupportPresent()
        ) {

            reasons.add(
                    DayMasterStemTransformationReason
                            .ORIGINAL_SAME_ELEMENT_SUPPORT_REMAINS
            );
        }

        /*
         * 《滴天髓》 진화 예시는
         * 일간과의 합 상대를
         * 월간/시간에 두는 구조를 강조한다.
         *
         * 현재 엔진은 년간 단독 합을
         * 真化/假化로 무리하게 올리지 않는다.
         */
        if (
                !candidate
                        .occurrence()
                        .counterpartInMonthOrHour()
        ) {

            reasons.add(
                    DayMasterStemTransformationReason
                            .COUNTERPART_ONLY_YEAR_POSITION
            );

            return new DayMasterStemTransformationAssessment(
                    candidate,
                    DayMasterStemTransformationState
                            .NOT_TRANSFORMED,
                    Set.copyOf(
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 4. 假化
         * =================================================
         *
         * 실제 일간 오합이 있고
         * 합 상대도 월/시에 존재하지만,
         *
         * - 화신이 월령을 완전히 얻지 못함
         * 또는
         * - 일간 본래 근이 남음
         * 또는
         * - 인성/비겁 생부가 남음
         *
         * 등으로 真化가 되지 못하는 구조.
         *
         * 《滴天髓闡微》의 假化 설명과
         * 대응시키는 층이다.
         */
        if (
                candidate.candidateType()
                        == DayMasterStemTransformationCandidateType
                        .FAKE_OR_INCOMPLETE_TRANSFORMATION_REVIEW

                        ||

                        !candidate
                                .hasStrictTrueTransformationContext()
        ) {

            /*
             * 방어적으로 reasons가 비어 있는
             * 비정상 상황을 막는다.
             */
            if (reasons.isEmpty()) {

                throw new IllegalStateException(
                        "진화 조건을 충족하지 못했지만 실패 이유가 없습니다."
                );
            }

            return new DayMasterStemTransformationAssessment(
                    candidate,
                    DayMasterStemTransformationState
                            .FAKE_TRANSFORMATION,
                    Set.copyOf(
                            reasons
                    )
            );
        }

        /*
         * 현재 구조상 일반적으로 도달하지 않지만,
         * 새로운 CandidateType 추가 시
         * 조용히 잘못된 판정을 하지 않도록 막는다.
         */
        throw new IllegalStateException(
                "천간 화기 상태를 판정할 수 없습니다: "
                        +
                        candidate
        );
    }
}