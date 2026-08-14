package com.dajeom.analyzer;

import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.model.ClimateElementCandidateAnalysis;
import com.dajeom.model.PassageCandidate;
import com.dajeom.model.PassageCandidateAnalysis;
import com.dajeom.model.PassageEvidence;
import com.dajeom.model.PassageNeedAnalysis;
import com.dajeom.model.RemedyAnalysis;
import com.dajeom.model.RemedyCandidate;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.PassageCandidateRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PassageCandidateRole;
import com.dajeom.type.PassageNeedLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-7.
 *
 * 6-6 통관 필요성 결과를
 * 실제 五行 통관 후보로 변환한다.
 *
 * =================================================
 *
 * 입력
 *
 * 6-6 PassageNeedAnalysis
 * 6-5 RemedyAnalysis
 * 6-3 ClimateElementCandidateAnalysis
 *
 * =================================================
 *
 * 五行 각각에 대해:
 *
 * - 통관 필요도
 * - 통관 오행 현재 존재량
 * - 일간이 직접 불리한지
 * - 병약 후보와 방향이 같은지
 * - 조후 후보와 방향이 같은지
 * - 다른 병/조후와 충돌하는지
 *
 * 를 확인한다.
 *
 * =================================================
 *
 * 결과
 *
 * PRIMARY
 * SECONDARY
 * CONDITIONAL
 * NOT_NEEDED
 *
 * =================================================
 *
 * 아직 최종 용신을 결정하지 않는다.
 */
@Component
public class PassageCandidateAnalyzer {

    private final PassageNeedAnalyzer
            passageNeedAnalyzer;

    private final PassageCandidateRule
            passageCandidateRule;

    public PassageCandidateAnalyzer(

            PassageNeedAnalyzer
                    passageNeedAnalyzer,

            PassageCandidateRule
                    passageCandidateRule

    ) {

        this.passageNeedAnalyzer =
                Objects.requireNonNull(
                        passageNeedAnalyzer,
                        "PassageNeedAnalyzer가 필요합니다."
                );

        this.passageCandidateRule =
                Objects.requireNonNull(
                        passageCandidateRule,
                        "PassageCandidateRule이 필요합니다."
                );
    }

    /**
     * 5차 StructureAnalysis와
     * 6-5 RemedyAnalysis를 가지고
     * 6-6 → 6-7을 연속 실행한다.
     */
    public PassageCandidateAnalysis analyze(

            StructureAnalysis structure,

            RemedyAnalysis remedy

    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                remedy,
                "병약 분석 결과가 필요합니다."
        );

        PassageNeedAnalysis passageNeed =
                passageNeedAnalyzer
                        .analyze(
                                structure,
                                remedy
                        );

        return analyze(
                passageNeed,
                remedy
        );
    }

    /**
     * 6-6 결과가 이미 존재하면
     * 그대로 재사용한다.
     *
     * 6차 전체 Engine에서는
     * 이 오버로드를 사용한다.
     */
    public PassageCandidateAnalysis analyze(

            PassageNeedAnalysis passageNeed,

            RemedyAnalysis remedy

    ) {

        Objects.requireNonNull(
                passageNeed,
                "통관 필요성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                remedy,
                "병약 분석 결과가 필요합니다."
        );

        validateConsistency(
                passageNeed,
                remedy
        );

        ClimateElementCandidateAnalysis climateCandidates =
                remedy
                        .climateElementCandidateAnalysis();

        List<PassageCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            List<PassageEvidence> passageEvidences =
                    passageNeed
                            .evidencesUsingBridge(
                                    element
                            );

            if (
                    passageEvidences.isEmpty()
            ) {

                throw new IllegalStateException(
                        "통관 오행에 대응하는 상극 Evidence가 없습니다: "
                                + element
                );
            }

            RemedyCandidate remedyCandidate =
                    remedy
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "병약 오행 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            ClimateElementCandidate climateCandidate =
                    climateCandidates
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "조후 오행 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            /*
             * 3차 오행 비중은
             * 조후/병약/통관 모두 같은 원국을
             * 기반으로 해야 한다.
             */
            if (
                    remedyCandidate
                            .existingShareBasisPoints()

                            != climateCandidate
                            .existingShareBasisPoints()
            ) {

                throw new IllegalStateException(
                        "병약과 조후 분석의 오행 비중이 일치하지 않습니다: "
                                + element
                );
            }

            int existingShare =
                    remedyCandidate
                            .existingShareBasisPoints();

            for (
                    PassageEvidence passageEvidence
                    : passageEvidences
            ) {

                if (
                        passageEvidence
                                .bridgeShareBasisPoints()

                                != existingShare
                ) {

                    throw new IllegalStateException(
                            "통관·병약·조후 분석의 오행 비중이 일치하지 않습니다: "
                                    + element
                    );
                }
            }

            PassageNeedLevel strongestNeed =
                    passageCandidateRule
                            .strongestNeedOf(
                                    passageEvidences
                            );

            PassageCandidateRole role =
                    passageCandidateRule
                            .determineRole(
                                    passageEvidences,
                                    remedyCandidate,
                                    climateCandidate
                            );

            boolean dayMasterDisadvantaged =
                    passageCandidateRule
                            .dayMasterDisadvantaged(
                                    passageEvidences
                            );

            boolean remedyConflict =
                    remedyCandidate
                            .hasConflict();

            boolean climateConflict =
                    climateCandidate
                            .hasConflict();

            candidates.add(
                    new PassageCandidate(
                            element,
                            role,
                            strongestNeed,
                            climateCandidate
                                    .presenceLevel(),
                            existingShare,
                            remedyCandidate.role(),
                            climateCandidate.role(),
                            passageEvidences,
                            passageCandidateRule
                                    .reasonsFor(
                                            passageEvidences,
                                            remedyCandidate,
                                            climateCandidate
                                    ),
                            dayMasterDisadvantaged,
                            remedyConflict,
                            climateConflict
                    )
            );
        }

        /*
         * ================================================
         * 출력 정렬
         * ================================================
         *
         * 1.
         * PRIMARY
         * SECONDARY
         * CONDITIONAL
         * NOT_NEEDED
         *
         * 2.
         * 통관 필요도
         *
         * 3.
         * 일간이 직접 불리한 후보
         *
         * 4.
         * 현재 통관 오행 비중이 적은 후보
         *
         * 순.
         *
         * 이것은 용신 점수가 아니다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        PassageCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        PassageCandidate candidate
                                                ) ->
                                                        candidate
                                                                .strongestNeed()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                PassageCandidate
                                                        ::dayMasterDisadvantaged
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                PassageCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new PassageCandidateAnalysis(
                passageNeed.dayMaster(),
                passageNeed.dayMasterElement(),
                passageNeed.monthBranch(),
                passageNeed,
                remedy,
                candidates,
                passageNeed.completeChart()
        );
    }

    private void validateConsistency(

            PassageNeedAnalysis passageNeed,

            RemedyAnalysis remedy

    ) {

        if (
                passageNeed.dayMaster()
                        != remedy.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "통관 필요성 분석과 병약 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                passageNeed.monthBranch()
                        != remedy.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "통관 필요성 분석과 병약 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                passageNeed.completeChart()
                        != remedy.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "통관 필요성 분석과 병약 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }
}