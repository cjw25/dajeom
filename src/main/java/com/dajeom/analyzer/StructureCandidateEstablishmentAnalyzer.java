package com.dajeom.analyzer;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.MonthStructureCandidateAnalysis;
import com.dajeom.model.StructureCandidateEstablishmentAnalysis;
import com.dajeom.model.StructureEstablishmentEvidence;
import com.dajeom.model.StructureExposurePriorityAnalysis;
import com.dajeom.model.StructureExposurePriorityEvidence;
import com.dajeom.rule.StructureEstablishmentRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-3.
 *
 * 정기·중기·여기 격 후보의
 * 실제 성립도를 분석한다.
 *
 * 입력:
 *
 * 4차 DayMasterStrengthAnalysis
 * 5-1 MonthStructureCandidateAnalysis
 * 5-2 StructureExposurePriorityAnalysis
 *
 * 출력:
 *
 * StructureCandidateEstablishmentAnalysis
 *
 * 중요한 원칙:
 *
 * 1.
 * 정기/중기/여기를 서로 다른 기준으로 본다.
 *
 * 2.
 * 3차 오행 세력을 다시 계산하지 않는다.
 *
 * 3.
 * 투출 여부를 다시 계산하지 않는다.
 *
 * 4.
 * 아직 주격을 결정하지 않는다.
 *
 * 5.
 * 비견/겁재는 일반 내격으로 확정하지 않고
 *    특수구조 검토 후보로 보존한다.
 */
@Component
public class StructureCandidateEstablishmentAnalyzer {

    private final MonthStructureCandidateAnalyzer
            monthStructureCandidateAnalyzer;

    private final StructureExposurePriorityAnalyzer
            structureExposurePriorityAnalyzer;

    private final StructureEstablishmentRule
            structureEstablishmentRule;

    public StructureCandidateEstablishmentAnalyzer(

            MonthStructureCandidateAnalyzer
                    monthStructureCandidateAnalyzer,

            StructureExposurePriorityAnalyzer
                    structureExposurePriorityAnalyzer,

            StructureEstablishmentRule
                    structureEstablishmentRule

    ) {

        this.monthStructureCandidateAnalyzer =
                Objects.requireNonNull(
                        monthStructureCandidateAnalyzer,
                        "MonthStructureCandidateAnalyzer가 필요합니다."
                );

        this.structureExposurePriorityAnalyzer =
                Objects.requireNonNull(
                        structureExposurePriorityAnalyzer,
                        "StructureExposurePriorityAnalyzer가 필요합니다."
                );

        this.structureEstablishmentRule =
                Objects.requireNonNull(
                        structureEstablishmentRule,
                        "StructureEstablishmentRule이 필요합니다."
                );
    }

    /**
     * 5-1 / 5-2를 내부에서 계산한다.
     */
    public StructureCandidateEstablishmentAnalysis analyze(
            DayMasterStrengthAnalysis strength
    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        MonthStructureCandidateAnalysis candidates =
                monthStructureCandidateAnalyzer
                        .analyze(
                                strength
                        );

        StructureExposurePriorityAnalysis exposure =
                structureExposurePriorityAnalyzer
                        .analyze(
                                candidates
                        );

        return analyze(
                strength,
                candidates,
                exposure
        );
    }

    /**
     * 이미 계산된 5-1 / 5-2 결과를
     * 재사용한다.
     *
     * 향후 5차 전체 Engine에서는
     * 이 메서드를 사용한다.
     */
    public StructureCandidateEstablishmentAnalysis analyze(

            DayMasterStrengthAnalysis strength,

            MonthStructureCandidateAnalysis candidates,

            StructureExposurePriorityAnalysis exposure

    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                candidates,
                "월령 격 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                exposure,
                "투출 우선순위 분석이 필요합니다."
        );

        validateConsistency(
                strength,
                candidates,
                exposure
        );

        List<StructureEstablishmentEvidence> evidences =
                new ArrayList<>();

        StructureEstablishmentEvidence mainQiEvidence =
                null;

        for (
                StructureExposurePriorityEvidence
                        exposureEvidence
                : exposure.orderedEvidences()
        ) {

            HeavenlyStem candidateStem =
                    exposureEvidence
                            .candidate()
                            .hiddenStem();

            FiveElement element =
                    determineElement(
                            candidateStem
                    );

            int elementFinalPower =
                    strength
                            .elementStrengthAnalysis()
                            .powerOf(
                                    element
                            );

            int elementShareBasisPoints =
                    strength
                            .elementStrengthAnalysis()
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    element
                            );

            var level =
                    structureEstablishmentRule
                            .determineLevel(
                                    exposureEvidence,
                                    elementShareBasisPoints
                            );

            boolean standardInnerStructureEligible =
                    structureEstablishmentRule
                            .isStandardInnerStructureEligible(
                                    exposureEvidence
                                            .tenGod()
                            );

            boolean specialStructureReviewRequired =
                    structureEstablishmentRule
                            .requiresSpecialStructureReview(
                                    exposureEvidence
                                            .tenGod()
                            );

            StructureEstablishmentEvidence evidence =
                    new StructureEstablishmentEvidence(
                            exposureEvidence,
                            element,
                            elementFinalPower,
                            elementShareBasisPoints,
                            standardInnerStructureEligible,
                            specialStructureReviewRequired,
                            level
                    );

            evidences.add(
                    evidence
            );

            if (
                    evidence.mainQi()
            ) {

                if (
                        mainQiEvidence != null
                ) {

                    throw new IllegalStateException(
                            "월령 정기 격 Evidence가 둘 이상 생성되었습니다."
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
                    "월령 정기 격 Evidence를 생성하지 못했습니다."
            );
        }

        /*
         * ================================================
         * 성립도 정렬
         * ================================================
         *
         * 1.
         * 성립도
         *
         * 2.
         * 월령 source 권위
         *
         * 3.
         * 투출 우선순위
         *
         * 4.
         * 전체 오행에서 해당 오행의 비중
         *
         * 5.
         * 월령 내부 원래 지장간 weight
         *
         * 순으로 정렬한다.
         *
         * 이 정렬은 주격 결정이 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        StructureEstablishmentEvidence evidence
                                ) ->
                                        evidence
                                                .level()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                StructureEstablishmentEvidence
                                        ::sourceAuthorityRank,
                                Comparator.reverseOrder()
                        )

                        .thenComparing(
                                StructureEstablishmentEvidence
                                        ::exposureRank,
                                Comparator.reverseOrder()
                        )

                        .thenComparing(
                                StructureEstablishmentEvidence
                                        ::elementShareBasisPoints,
                                Comparator.reverseOrder()
                        )

                        .thenComparing(
                                StructureEstablishmentEvidence
                                        ::hiddenStemWeight,
                                Comparator.reverseOrder()
                        )
        );

        return new StructureCandidateEstablishmentAnalysis(
                strength.dayMaster(),
                candidates.monthBranch(),
                mainQiEvidence,
                evidences
        );
    }

    private void validateConsistency(

            DayMasterStrengthAnalysis strength,

            MonthStructureCandidateAnalysis candidates,

            StructureExposurePriorityAnalysis exposure

    ) {

        if (
                strength.dayMaster()
                        != candidates.dayMaster()

                        || strength.dayMaster()
                        != exposure.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "5차 분석 결과의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                candidates.monthBranch()
                        != exposure.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "5차 분석 결과의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                candidates.candidates().size()
                        != exposure.orderedEvidences().size()
        ) {

            throw new IllegalArgumentException(
                    "격 후보 수와 투출 Evidence 수가 일치하지 않습니다."
            );
        }
    }

    /**
     * 천간에서 오행을 구한다.
     *
     * 현재 HeavenlyStem의
     * fromIndex 표준 순서를 사용한다.
     *
     * 0 甲
     * 1 乙
     * 2 丙
     * 3 丁
     * 4 戊
     * 5 己
     * 6 庚
     * 7 辛
     * 8 壬
     * 9 癸
     */
    private FiveElement determineElement(
            HeavenlyStem stem
    ) {

        int index =
                stemIndexOf(
                        stem
                );

        return switch (
                index / 2
                ) {

            case 0 ->
                    FiveElement.WOOD;

            case 1 ->
                    FiveElement.FIRE;

            case 2 ->
                    FiveElement.EARTH;

            case 3 ->
                    FiveElement.METAL;

            case 4 ->
                    FiveElement.WATER;

            default ->
                    throw new IllegalStateException(
                            "천간 오행을 판정할 수 없습니다: "
                                    + stem
                    );
        };
    }

    private int stemIndexOf(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        for (
                int index = 0;
                index < 10;
                index++
        ) {

            if (
                    HeavenlyStem.fromIndex(
                            index
                    )
                            == stem
            ) {

                return index;
            }
        }

        throw new IllegalArgumentException(
                "알 수 없는 천간입니다: "
                        + stem
        );
    }
}