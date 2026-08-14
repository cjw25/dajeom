package com.dajeom.analyzer;

import com.dajeom.model.DayMasterGroundAnalysis;
import com.dajeom.model.DayMasterGroundEvidence;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.RootAnalysis;
import com.dajeom.model.RootEvidence;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.RootSourcePosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 4차-3.
 *
 * 일간의 득지 및 지지 기반을 분석한다.
 *
 * 중요한 원칙:
 *
 * RootAnalyzer에서 이미 계산한 통근을
 * 다시 계산하지 않는다.
 *
 * 2차의:
 *
 * RootAnalysis
 * RootEvidence
 *
 * 를 그대로 받아서
 * 신강·신약 판단용 의미로 재구성한다.
 *
 * 이렇게 해야:
 *
 * - 지장간
 * - 통근
 * - 득지
 *
 * 가 중복 계산되지 않는다.
 */
@Component
public class DayMasterGroundAnalyzer {

    public DayMasterGroundAnalysis analyze(
            ElementStrengthAnalysis elementStrength
    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        RootAnalysis dayMasterRootAnalysis =
                elementStrength
                        .basePowerAnalysis()
                        .dayMasterRootAnalysis();

        HeavenlyStem dayMaster =
                elementStrength
                        .dayMaster();

        FiveElement dayMasterElement =
                elementStrength
                        .dayMasterElement();

        /*
         * BasePowerAnalysis에서
         * 알려진 기둥 수를 그대로 사용한다.
         *
         * 출생시간 미상:
         *
         * 년지
         * 월지
         * 일지
         *
         * = 3
         *
         * 출생시간 확정:
         *
         * = 4
         */
        int knownBranchCount =
                elementStrength
                        .basePowerAnalysis()
                        .knownPillarCount();

        List<DayMasterGroundEvidence> evidences =
                new ArrayList<>();

        for (
                RootEvidence rootEvidence
                : dayMasterRootAnalysis.evidences()
        ) {

            /*
             * dayMasterRootAnalysis에
             * 다른 target stem이 섞여 있다면
             * 데이터 연결 오류다.
             */
            if (
                    rootEvidence.targetStem()
                            != dayMaster
            ) {

                throw new IllegalStateException(
                        "일간 통근 분석에 다른 천간의 RootEvidence가 포함되어 있습니다."
                );
            }

            evidences.add(
                    convert(
                            dayMaster,
                            rootEvidence
                    )
            );
        }

        int directRootWeight =
                evidences
                        .stream()
                        .filter(
                                DayMasterGroundEvidence
                                        ::directRoot
                        )
                        .mapToInt(
                                DayMasterGroundEvidence
                                        ::effectiveRootWeight
                        )
                        .sum();

        int supportingRootWeight =
                evidences
                        .stream()
                        .filter(
                                DayMasterGroundEvidence
                                        ::supportingRoot
                        )
                        .mapToInt(
                                DayMasterGroundEvidence
                                        ::effectiveRootWeight
                        )
                        .sum();

        int totalRootWeight =
                directRootWeight
                        + supportingRootWeight;

        /*
         * RootAnalysis에서 계산한 전체 세력과
         * 4-3에서 변환한 Evidence의 합계를
         * 반드시 맞춰본다.
         */
        if (
                totalRootWeight
                        != dayMasterRootAnalysis
                        .totalRootWeight()
        ) {

            throw new IllegalStateException(
                    "일간 통근 세력이 RootAnalysis와 일치하지 않습니다."
            );
        }

        int groundedBranchCount =
                countGroundedBranches(
                        evidences,
                        false
                );

        int directGroundedBranchCount =
                countGroundedBranches(
                        evidences,
                        true
                );

        return new DayMasterGroundAnalysis(
                dayMaster,
                dayMasterElement,
                knownBranchCount,
                groundedBranchCount,
                directGroundedBranchCount,
                directRootWeight,
                supportingRootWeight,
                totalRootWeight,
                evidences
        );
    }

    private DayMasterGroundEvidence convert(

            HeavenlyStem dayMaster,

            RootEvidence evidence

    ) {

        return new DayMasterGroundEvidence(
                dayMaster,
                evidence.sourcePosition(),
                evidence.sourceBranch(),
                evidence.hiddenStem(),
                evidence.hiddenStemRole(),
                evidence.relationType(),
                evidence.hiddenStemWeight(),
                evidence.positionWeightRate(),
                evidence.relationWeightRate(),
                evidence.effectiveWeight()
        );
    }

    /**
     * 통근이 존재하는 지지 위치의 수.
     *
     * 한 지지 안에 여러 RootEvidence가 있어도
     * 해당 지지는 한 번만 센다.
     */
    private int countGroundedBranches(

            List<DayMasterGroundEvidence> evidences,

            boolean directOnly

    ) {

        EnumSet<RootSourcePosition> positions =
                EnumSet.noneOf(
                        RootSourcePosition.class
                );

        for (
                DayMasterGroundEvidence evidence
                : evidences
        ) {

            if (
                    directOnly
                            && !evidence.directRoot()
            ) {

                continue;
            }

            positions.add(
                    evidence.sourcePosition()
            );
        }

        return positions.size();
    }
}