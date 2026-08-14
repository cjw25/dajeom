package com.dajeom.analyzer;

import com.dajeom.model.MonthStructureCandidate;
import com.dajeom.model.MonthStructureCandidateAnalysis;
import com.dajeom.model.StructureExposurePriorityAnalysis;
import com.dajeom.model.StructureExposurePriorityEvidence;
import com.dajeom.rule.StructureExposurePriorityRule;
import com.dajeom.type.StructureExposurePriority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-2.
 *
 * 5-1에서 추출한 월령 격 후보들을
 * 투출 근거의 직접성에 따라 정렬한다.
 *
 * 중요한 원칙:
 *
 * 이 Analyzer는
 * 주격을 결정하지 않는다.
 *
 * 예:
 *
 * 정기 미투출
 * +
 * 중기 월간 투출
 *
 * 이라면 중기가 Exposure Priority에서는
 * 정기보다 위에 올 수 있다.
 *
 * 그러나:
 *
 * "중기가 최종 주격"
 *
 * 으로 확정하지 않는다.
 *
 * 월령 정기의 sourceAuthority는 그대로 보존하고
 * 실제 격 성립은 5-3에서 재검토한다.
 */
@Component
public class StructureExposurePriorityAnalyzer {

    private final StructureExposurePriorityRule
            structureExposurePriorityRule;

    public StructureExposurePriorityAnalyzer(
            StructureExposurePriorityRule
                    structureExposurePriorityRule
    ) {

        this.structureExposurePriorityRule =
                Objects.requireNonNull(
                        structureExposurePriorityRule,
                        "StructureExposurePriorityRule이 필요합니다."
                );
    }

    public StructureExposurePriorityAnalysis analyze(
            MonthStructureCandidateAnalysis candidateAnalysis
    ) {

        Objects.requireNonNull(
                candidateAnalysis,
                "월령 격 후보 분석이 필요합니다."
        );

        List<StructureExposurePriorityEvidence> evidences =
                new ArrayList<>();

        StructureExposurePriorityEvidence mainQiEvidence =
                null;

        for (
                MonthStructureCandidate candidate
                : candidateAnalysis.candidates()
        ) {

            StructureExposurePriority priority =
                    structureExposurePriorityRule
                            .determinePriority(
                                    candidate
                            );

            int sourceAuthorityRank =
                    structureExposurePriorityRule
                            .determineSourceAuthorityRank(
                                    candidate
                            );

            boolean otherStemExposed =
                    candidate.exposedAtYearStem()
                            || candidate.exposedAtHourStem();

            StructureExposurePriorityEvidence evidence =
                    new StructureExposurePriorityEvidence(
                            candidate,
                            priority,
                            sourceAuthorityRank,
                            priority.rank(),
                            candidate.exposedAtMonthStem(),
                            otherStemExposed,
                            candidate.repeatedExposure(),
                            candidate.visibleStemCount(),
                            candidate.exposureSourceWeight()
                    );

            evidences.add(
                    evidence
            );

            if (
                    candidate.mainQi()
            ) {

                if (
                        mainQiEvidence != null
                ) {

                    throw new IllegalStateException(
                            "월령 정기 Evidence가 둘 이상 생성되었습니다."
                    );
                }

                mainQiEvidence =
                        evidence;
            }
        }

        if (
                mainQiEvidence == null
        ) {

            throw new IllegalStateException(
                    "월령 정기 Evidence를 생성하지 못했습니다."
            );
        }

        /*
         * ================================================
         * 투출 우선순위 정렬
         * ================================================
         *
         * 1.
         * StructureExposurePriority rank
         *
         * 2.
         * 같은 단계라면 반복 투출
         *
         * 3.
         * 실제 투출 위치 수
         *
         * 4.
         * Exposure source weight
         *
         * 5.
         * 월령 내부 지장간 원래 weight
         *
         * 순서로 정렬한다.
         *
         * 현재 월령은 정기/중기/여기가
         * 각각 하나이므로 실제 동률 가능성은
         * 매우 낮지만 비교 규칙을 명확히 둔다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        StructureExposurePriorityEvidence evidence
                                ) ->
                                        evidence
                                                .exposurePriority()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                StructureExposurePriorityEvidence
                                        ::repeatedExposure,
                                Comparator.reverseOrder()
                        )

                        .thenComparing(
                                StructureExposurePriorityEvidence
                                        ::visibleStemCount,
                                Comparator.reverseOrder()
                        )

                        .thenComparing(
                                StructureExposurePriorityEvidence
                                        ::exposureSourceWeight,
                                Comparator.reverseOrder()
                        )

                        .thenComparing(
                                evidence ->
                                        evidence
                                                .candidate()
                                                .hiddenStemWeight(),
                                Comparator.reverseOrder()
                        )
        );

        return new StructureExposurePriorityAnalysis(
                candidateAnalysis.dayMaster(),
                candidateAnalysis.monthBranch(),
                mainQiEvidence,
                evidences
        );
    }
}