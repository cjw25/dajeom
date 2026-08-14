package com.dajeom.analyzer;

import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.PassageEvidence;
import com.dajeom.model.PassageNeedAnalysis;
import com.dajeom.model.RemedyAnalysis;
import com.dajeom.model.RemedyCandidate;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.PassageNeedRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PassageBalanceState;
import com.dajeom.type.PassageBridgeState;
import com.dajeom.type.PassageConflictType;
import com.dajeom.type.PassageNeedLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-6.
 *
 * 五行의 다섯 상극 관계를 모두 검사하여
 * 통관(通關)이 실제로 필요한 구조인지 분석한다.
 *
 * =================================================
 *
 * 분석 요소
 *
 * 1.
 * 극하는 오행의 실제 세력
 *
 * 2.
 * 극받는 오행의 실제 세력
 *
 * 3.
 * 양쪽 세력 차이
 *
 * 4.
 * 통관 중간 오행의 실제 세력
 *
 * 5.
 * 일간이 상극 관계 안에 위치하는지
 *
 * 6.
 * 신약 일간이 상대편에 밀리는지
 *
 * 7.
 * 6-5 약 후보와 통관 방향이 일치하는지
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 6-6은 통관 "필요성"만 분석한다.
 *
 * 실제 통관 후보 오행의 역할:
 *
 * PRIMARY
 * SECONDARY
 * CONDITIONAL
 *
 * 결정은 6-7에서 수행한다.
 */
@Component
public class PassageNeedAnalyzer {

    private final PassageNeedRule
            passageNeedRule;

    public PassageNeedAnalyzer(
            PassageNeedRule passageNeedRule
    ) {

        this.passageNeedRule =
                Objects.requireNonNull(
                        passageNeedRule,
                        "PassageNeedRule이 필요합니다."
                );
    }

    public PassageNeedAnalysis analyze(

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

        validateConsistency(
                structure,
                remedy
        );

        ElementStrengthAnalysis elementStrength =
                structure
                        .dayMasterStrengthAnalysis()
                        .elementStrengthAnalysis();

        FiveElement dayMasterElement =
                structure
                        .dayMasterStrengthAnalysis()
                        .dayMasterElement();

        List<PassageEvidence> evidences =
                new ArrayList<>();

        for (
                PassageConflictType conflictType
                : PassageConflictType.values()
        ) {

            FiveElement controllerElement =
                    conflictType
                            .controllerElement();

            FiveElement controlledElement =
                    conflictType
                            .controlledElement();

            FiveElement bridgeElement =
                    conflictType
                            .bridgeElement();

            int controllerShare =
                    elementStrength
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    controllerElement
                            );

            int controlledShare =
                    elementStrength
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    controlledElement
                            );

            int bridgeShare =
                    elementStrength
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    bridgeElement
                            );

            int conflictFloor =
                    Math.min(
                            controllerShare,
                            controlledShare
                    );

            int sideGap =
                    Math.abs(
                            controllerShare
                                    - controlledShare
                    );

            PassageBalanceState balanceState =
                    passageNeedRule
                            .determineBalanceState(
                                    controllerShare,
                                    controlledShare
                            );

            PassageBridgeState bridgeState =
                    passageNeedRule
                            .determineBridgeState(
                                    bridgeShare
                            );

            boolean dayMasterOnControllerSide =
                    conflictType
                            .controllerIs(
                                    dayMasterElement
                            );

            boolean dayMasterOnControlledSide =
                    conflictType
                            .controlledIs(
                                    dayMasterElement
                            );

            boolean dayMasterInConflict =
                    dayMasterOnControllerSide
                            || dayMasterOnControlledSide;

            boolean dayMasterDisadvantaged =
                    passageNeedRule
                            .determineDayMasterDisadvantaged(
                                    conflictType,
                                    controllerShare,
                                    controlledShare,
                                    dayMasterElement,
                                    structure
                                            .dayMasterStrengthAnalysis()
                                            .level()
                            );

            PassageNeedLevel needLevel =
                    passageNeedRule
                            .determineNeedLevel(
                                    conflictType,
                                    controllerShare,
                                    controlledShare,
                                    balanceState,
                                    bridgeState,
                                    dayMasterElement,
                                    structure
                                            .dayMasterStrengthAnalysis()
                                            .level()
                            );

            RemedyCandidate bridgeRemedyCandidate =
                    remedy
                            .candidateOf(
                                    bridgeElement
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "통관 오행의 약 후보를 찾을 수 없습니다: "
                                                            + bridgeElement
                                            )
                            );

            evidences.add(
                    new PassageEvidence(
                            conflictType,
                            controllerShare,
                            controlledShare,
                            bridgeShare,
                            conflictFloor,
                            sideGap,
                            balanceState,
                            bridgeState,
                            needLevel,
                            dayMasterInConflict,
                            dayMasterOnControllerSide,
                            dayMasterOnControlledSide,
                            dayMasterDisadvantaged,
                            bridgeRemedyCandidate.role()
                    )
            );
        }

        /*
         * ================================================
         * 출력 정렬
         * ================================================
         *
         * 1.
         * 통관 필요도가 높은 관계
         *
         * 2.
         * 양쪽 세력이 더 팽팽한 관계
         *
         * 3.
         * 대립 최소세력이 큰 관계
         *
         * 순.
         *
         * 통관 점수를 합산하는 것은 아니다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        PassageEvidence evidence
                                ) ->
                                        evidence
                                                .needLevel()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        PassageEvidence evidence
                                                ) ->
                                                        evidence
                                                                .balanceState()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                PassageEvidence
                                                        ::conflictFloorBasisPoints
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .conflictType()
                                                .ordinal()
                        )
        );

        PassageNeedLevel overallNeed =
                evidences
                        .stream()
                        .map(
                                PassageEvidence
                                        ::needLevel
                        )
                        .max(
                                Comparator.comparingInt(
                                        PassageNeedLevel
                                                ::rank
                                )
                        )
                        .orElse(
                                PassageNeedLevel.NONE
                        );

        return new PassageNeedAnalysis(
                structure.dayMaster(),
                dayMasterElement,
                structure.monthBranch(),
                evidences,
                overallNeed,
                structure.completeChart()
        );
    }

    private void validateConsistency(

            StructureAnalysis structure,

            RemedyAnalysis remedy

    ) {

        if (
                structure.dayMaster()
                        != remedy.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 병약 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                structure.monthBranch()
                        != remedy.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 병약 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                structure.completeChart()
                        != remedy.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 병약 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }
}