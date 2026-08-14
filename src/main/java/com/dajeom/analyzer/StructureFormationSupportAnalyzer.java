package com.dajeom.analyzer;

import com.dajeom.model.DayMasterElementRelation;
import com.dajeom.model.DayMasterForceEvidence;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.StructureBalanceAnalysis;
import com.dajeom.model.StructureBalanceEvidence;
import com.dajeom.model.StructureFormationSupportAnalysis;
import com.dajeom.model.StructureFormationSupportEvidence;
import com.dajeom.model.StructureSupportSignal;
import com.dajeom.rule.StructureFormationSupportRule;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StructureSupportLevel;
import com.dajeom.type.StructureSupportPatternType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-5.
 *
 * 각 격 후보를 살려주는
 * 성격(成格) 요소를 분석한다.
 *
 * 입력:
 *
 * - 4차 DayMasterStrengthAnalysis
 * - 5-4 StructureBalanceAnalysis
 *
 * 사용하는 기존 정보:
 *
 * - 일간 기준 오행 역할
 * - 3차 최종 오행 세력
 * - 4-4 천간/지장간 분포
 * - 5-3 격 성립도
 * - 5-4 일간과 격의 강약 관계
 *
 * 중요한 원칙:
 *
 * 1.
 * 오행 세력을 다시 계산하지 않는다.
 *
 * 2.
 * 투간을 다시 계산하지 않는다.
 *
 * 3.
 * 이번에는 오직 성격 요소를 찾는다.
 *
 * 4.
 * 파격 요소는 다음 5-6에서 별도 분석한다.
 *
 * 5.
 * 비견/겁재 월령은 일반 내격 성격 분석을
 *    적용하지 않고 5-8로 넘긴다.
 */
@Component
public class StructureFormationSupportAnalyzer {

    private final StructureBalanceAnalyzer
            structureBalanceAnalyzer;

    private final StructureFormationSupportRule
            structureFormationSupportRule;

    public StructureFormationSupportAnalyzer(

            StructureBalanceAnalyzer
                    structureBalanceAnalyzer,

            StructureFormationSupportRule
                    structureFormationSupportRule

    ) {

        this.structureBalanceAnalyzer =
                Objects.requireNonNull(
                        structureBalanceAnalyzer,
                        "StructureBalanceAnalyzer가 필요합니다."
                );

        this.structureFormationSupportRule =
                Objects.requireNonNull(
                        structureFormationSupportRule,
                        "StructureFormationSupportRule이 필요합니다."
                );
    }

    /**
     * 5-1 ~ 5-4를 내부에서 연결한 뒤
     * 5-5를 수행한다.
     */
    public StructureFormationSupportAnalysis analyze(
            DayMasterStrengthAnalysis strength
    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        StructureBalanceAnalysis balance =
                structureBalanceAnalyzer
                        .analyze(
                                strength
                        );

        return analyze(
                strength,
                balance
        );
    }

    /**
     * 5-4 결과가 이미 계산되어 있을 경우
     * 그대로 재사용한다.
     *
     * 향후 5차 통합 Engine에서는
     * 이 메서드를 사용한다.
     */
    public StructureFormationSupportAnalysis analyze(

            DayMasterStrengthAnalysis strength,

            StructureBalanceAnalysis balance

    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                balance,
                "격국 강약 분석이 필요합니다."
        );

        validateConsistency(
                strength,
                balance
        );

        List<StructureFormationSupportEvidence>
                evidences =
                new ArrayList<>();

        StructureFormationSupportEvidence
                mainQiEvidence =
                null;

        for (
                StructureBalanceEvidence balanceEvidence
                : balance.evidences()
        ) {

            boolean specialStructureDeferred =
                    balanceEvidence
                            .specialStructureCandidate();

            List<StructureSupportSignal> signals;

            /*
             * 비견/겁재 월령은
             * 일반 내격 성격 패턴을 적용하지 않는다.
             */
            if (
                    specialStructureDeferred
            ) {

                signals =
                        List.of();

            } else {

                signals =
                        createSupportSignals(
                                strength,
                                balanceEvidence
                        );
            }

            StructureSupportLevel strongestLevel =
                    signals
                            .stream()
                            .map(
                                    StructureSupportSignal
                                            ::level
                            )
                            .max(
                                    Comparator.comparingInt(
                                            StructureSupportLevel
                                                    ::rank
                                    )
                            )
                            .orElse(
                                    StructureSupportLevel.NONE
                            );

            int meaningfulSupportCount =
                    (int) signals
                            .stream()
                            .filter(
                                    StructureSupportSignal
                                            ::meaningful
                            )
                            .count();

            StructureFormationSupportEvidence evidence =
                    new StructureFormationSupportEvidence(
                            balanceEvidence,
                            signals,
                            strongestLevel,
                            meaningfulSupportCount,
                            specialStructureDeferred
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
                            "월령 정기 성격 Evidence가 둘 이상 생성되었습니다."
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
                    "월령 정기 성격 Evidence를 생성하지 못했습니다."
            );
        }

        EarthlyBranch monthBranch =
                strength
                        .elementStrengthAnalysis()
                        .basePowerAnalysis()
                        .monthCommandAnalysis()
                        .monthBranch();

        /*
         * 5-4까지의 후보 순서를 그대로 유지한다.
         *
         * 성격 요소가 많다고 해서
         * 성립도가 낮은 후보를 앞쪽으로
         * 옮기지 않는다.
         *
         * 최종 주격 결정은 5-9의 책임이다.
         */
        return new StructureFormationSupportAnalysis(
                strength.dayMaster(),
                monthBranch,
                mainQiEvidence,
                evidences
        );
    }

    private List<StructureSupportSignal>
    createSupportSignals(

            DayMasterStrengthAnalysis strength,

            StructureBalanceEvidence balanceEvidence

    ) {

        List<StructureSupportPatternType> patterns =
                structureFormationSupportRule
                        .requiredPatterns(
                                balanceEvidence.tenGod()
                        );

        if (
                patterns.isEmpty()
        ) {

            return List.of();
        }

        List<StructureSupportSignal> signals =
                new ArrayList<>();

        for (
                StructureSupportPatternType pattern
                : patterns
        ) {

            DayMasterElementRole supportingRole =
                    pattern
                            .supportingRole();

            DayMasterElementRelation relation =
                    strength
                            .elementRelationAnalysis()
                            .relationOf(
                                    supportingRole
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "성격 요소의 오행 관계를 찾을 수 없습니다: "
                                                            + supportingRole
                                            )
                            );

            FiveElement supportingElement =
                    relation
                            .targetElement();

            int elementFinalPower =
                    strength
                            .elementStrengthAnalysis()
                            .powerOf(
                                    supportingElement
                            );

            int elementShareBasisPoints =
                    strength
                            .elementStrengthAnalysis()
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    supportingElement
                            );

            List<DayMasterForceEvidence> sources =
                    strength
                            .forceFormationAnalysis()
                            .evidences()
                            .stream()
                            .filter(
                                    evidence ->
                                            evidence.role()
                                                    == supportingRole
                            )
                            .toList();

            int visibleSourceCount =
                    (int) sources
                            .stream()
                            .filter(
                                    DayMasterForceEvidence
                                            ::visibleStemSource
                            )
                            .count();

            int hiddenSourceCount =
                    (int) sources
                            .stream()
                            .filter(
                                    DayMasterForceEvidence
                                            ::hiddenStemSource
                            )
                            .count();

            int exposedHiddenSourceCount =
                    (int) sources
                            .stream()
                            .filter(
                                    DayMasterForceEvidence
                                            ::exposedHiddenStem
                            )
                            .count();

            StructureSupportLevel level =
                    structureFormationSupportRule
                            .determineLevel(
                                    elementFinalPower,
                                    elementShareBasisPoints,
                                    visibleSourceCount,
                                    hiddenSourceCount,
                                    exposedHiddenSourceCount
                            );

            signals.add(
                    new StructureSupportSignal(
                            pattern,
                            supportingRole,
                            supportingElement,
                            elementFinalPower,
                            elementShareBasisPoints,
                            visibleSourceCount,
                            hiddenSourceCount,
                            exposedHiddenSourceCount,
                            level
                    )
            );
        }

        return List.copyOf(
                signals
        );
    }

    private void validateConsistency(

            DayMasterStrengthAnalysis strength,

            StructureBalanceAnalysis balance

    ) {

        if (
                strength.dayMaster()
                        != balance.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 분석과 격국 강약 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                strength.dayMasterElement()
                        != balance.dayMasterElement()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 분석과 격국 강약 분석의 일간 오행이 일치하지 않습니다."
            );
        }

        if (
                strength.level()
                        != balance.dayMasterStrengthLevel()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 단계가 서로 일치하지 않습니다."
            );
        }

        if (
                strength.strengthBasisPoints()
                        != balance.dayMasterStrengthBasisPoints()
        ) {

            throw new IllegalArgumentException(
                    "일간 강도 지수가 서로 일치하지 않습니다."
            );
        }
    }
}