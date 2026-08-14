package com.dajeom.analyzer;

import com.dajeom.model.HeavenlyStemCombinationCandidate;
import com.dajeom.model.HeavenlyStemCombinationCompetitionAnalysis;
import com.dajeom.model.HeavenlyStemCombinationCompetitionEvidence;
import com.dajeom.model.HeavenlyStemCombinationTransformationAnalysis;
import com.dajeom.model.HeavenlyStemCombinationTransformationEvidence;
import com.dajeom.rule.HeavenlyStemCombinationTransformationRule;
import com.dajeom.type.HeavenlyStemCombinationTransformationReasonType;
import com.dajeom.type.HeavenlyStemCombinationTransformationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-3.
 *
 * 9-1의 기본 합화 환경과
 * 9-2의 경쟁·방해 상태를 결합하여
 *
 * 천간합의 최종 합화 성립도를 판정한다.
 *
 * =================================================
 *
 * 9-3에서 결정:
 *
 * TRANSFORMED
 * PARTIALLY_TRANSFORMED
 * CONDITIONAL
 * COMBINED_NOT_TRANSFORMED
 * SUPPRESSED
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * Candidate를 삭제하지 않는다.
 *
 * 쟁합 구조라면
 * 각각의 후보에 개별 상태를 부여한다.
 *
 * =================================================
 *
 * 이 단계에서도:
 *
 * 원래 천간 오행 삭제
 * 실제 오행 세력 재계산
 * 지장간 변경
 *
 * 은 하지 않는다.
 *
 * 실제 기능 보존/변경은 9-9 책임이다.
 */
@Component
public class HeavenlyStemCombinationTransformationAnalyzer {

    private final HeavenlyStemCombinationTransformationRule
            heavenlyStemCombinationTransformationRule;

    public HeavenlyStemCombinationTransformationAnalyzer(
            HeavenlyStemCombinationTransformationRule
                    heavenlyStemCombinationTransformationRule
    ) {

        this.heavenlyStemCombinationTransformationRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationTransformationRule,
                        "HeavenlyStemCombinationTransformationRule이 필요합니다."
                );
    }

    public HeavenlyStemCombinationTransformationAnalysis analyze(
            HeavenlyStemCombinationCompetitionAnalysis
                    competitionAnalysis
    ) {

        Objects.requireNonNull(
                competitionAnalysis,
                "9-2 천간합 경쟁 분석이 필요합니다."
        );

        List<HeavenlyStemCombinationTransformationEvidence> evidences =
                new ArrayList<>();

        for (
                HeavenlyStemCombinationCompetitionEvidence competitionEvidence
                : competitionAnalysis.evidences()
        ) {

            HeavenlyStemCombinationCandidate candidate =
                    competitionEvidence.candidate();

            /*
             * =================================================
             * 최종 9-3 상태
             * =================================================
             */
            HeavenlyStemCombinationTransformationState state =
                    heavenlyStemCombinationTransformationRule
                            .determineState(
                                    candidate,
                                    competitionEvidence
                            );

            /*
             * =================================================
             * Evidence 이유
             * =================================================
             */
            List<HeavenlyStemCombinationTransformationReasonType> reasons =
                    heavenlyStemCombinationTransformationRule
                            .buildReasons(
                                    candidate,
                                    competitionEvidence,
                                    state
                            );

            evidences.add(
                    new HeavenlyStemCombinationTransformationEvidence(
                            candidate,
                            competitionEvidence,
                            state,
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 1.
         * 실제 합화 성립 상태
         *
         * 2.
         * 9-1 기본 적용도
         *
         * 3.
         * 원국 위치
         *
         * 순.
         *
         * 정렬 순서는 길흉 순위가 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        HeavenlyStemCombinationTransformationEvidence evidence
                                ) ->
                                        evidence
                                                .state()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator.comparingInt(
                                                (
                                                        HeavenlyStemCombinationTransformationEvidence evidence
                                                ) ->
                                                        evidence
                                                                .candidate()
                                                                .applicability()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .firstPosition()
                                                .ordinal()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .secondPosition()
                                                .ordinal()
                        )
        );

        return new HeavenlyStemCombinationTransformationAnalysis(
                competitionAnalysis.dayMaster(),
                competitionAnalysis.candidateAnalysis(),
                competitionAnalysis,
                evidences,
                competitionAnalysis.completeChart()
        );
    }
}