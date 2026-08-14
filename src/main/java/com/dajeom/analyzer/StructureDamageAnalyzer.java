package com.dajeom.analyzer;

import com.dajeom.calculator.StructureTenGodCalculator;
import com.dajeom.model.DayMasterElementRelation;
import com.dajeom.model.DayMasterForceEvidence;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.StructureDamageAnalysis;
import com.dajeom.model.StructureDamageEvidence;
import com.dajeom.model.StructureDamageSignal;
import com.dajeom.model.StructureFormationSupportAnalysis;
import com.dajeom.model.StructureFormationSupportEvidence;
import com.dajeom.rule.StructureDamageRule;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StructureDamageLevel;
import com.dajeom.type.StructureDamagePatternType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-6.
 *
 * 각 격 후보를 손상시키는
 * 파격(破格) Evidence를 분석한다.
 *
 * 입력:
 *
 * 4차 DayMasterStrengthAnalysis
 * 5-5 StructureFormationSupportAnalysis
 *
 * 사용하는 기존 정보:
 *
 * - 정확한 십성
 * - 일간 기준 오행 역할
 * - 3차 최종 오행 세력
 * - 천간/지장간 source
 * - 성격(成格) 패턴
 * - 격과 일간의 강약 관계
 *
 * 중요한 원칙:
 *
 * 1.
 * 오행 세력을 다시 계산하지 않는다.
 *
 * 2.
 * 파격 신호와 최종 파격 확정을 구분한다.
 *
 * 3.
 * 비견/겁재 월령은 일반 내격 Rule을
 *    적용하지 않고 5-8로 넘긴다.
 *
 * 4.
 * 상관견관·편인도식처럼
 *    정확한 음양 십성이 필요한 경우
 *    StructureTenGodCalculator를 사용한다.
 */
@Component
public class StructureDamageAnalyzer {

    private final StructureFormationSupportAnalyzer
            structureFormationSupportAnalyzer;

    private final StructureTenGodCalculator
            structureTenGodCalculator;

    private final StructureDamageRule
            structureDamageRule;

    public StructureDamageAnalyzer(

            StructureFormationSupportAnalyzer
                    structureFormationSupportAnalyzer,

            StructureTenGodCalculator
                    structureTenGodCalculator,

            StructureDamageRule
                    structureDamageRule

    ) {

        this.structureFormationSupportAnalyzer =
                Objects.requireNonNull(
                        structureFormationSupportAnalyzer,
                        "StructureFormationSupportAnalyzer가 필요합니다."
                );

        this.structureTenGodCalculator =
                Objects.requireNonNull(
                        structureTenGodCalculator,
                        "StructureTenGodCalculator가 필요합니다."
                );

        this.structureDamageRule =
                Objects.requireNonNull(
                        structureDamageRule,
                        "StructureDamageRule이 필요합니다."
                );
    }

    /**
     * 5-1 ~ 5-5를 내부에서 실행한 뒤
     * 5-6을 수행한다.
     */
    public StructureDamageAnalysis analyze(
            DayMasterStrengthAnalysis strength
    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        StructureFormationSupportAnalysis support =
                structureFormationSupportAnalyzer
                        .analyze(
                                strength
                        );

        return analyze(
                strength,
                support
        );
    }

    /**
     * 5-5 결과를 재사용한다.
     *
     * 향후 5차 전체 Engine에서는
     * 이 오버로드를 사용한다.
     */
    public StructureDamageAnalysis analyze(

            DayMasterStrengthAnalysis strength,

            StructureFormationSupportAnalysis support

    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                support,
                "성격 요소 분석이 필요합니다."
        );

        validateConsistency(
                strength,
                support
        );

        List<StructureDamageEvidence>
                evidences =
                new ArrayList<>();

        StructureDamageEvidence
                mainQiEvidence =
                null;

        for (
                StructureFormationSupportEvidence
                        supportEvidence
                : support.evidences()
        ) {

            boolean specialStructureDeferred =
                    supportEvidence
                            .specialStructureDeferred();

            List<StructureDamageSignal> signals;

            if (
                    specialStructureDeferred
            ) {

                signals =
                        List.of();

            } else {

                signals =
                        createDamageSignals(
                                strength,
                                supportEvidence
                        );
            }

            StructureDamageLevel strongestDamageLevel =
                    signals
                            .stream()
                            .map(
                                    StructureDamageSignal
                                            ::level
                            )
                            .max(
                                    Comparator.comparingInt(
                                            StructureDamageLevel
                                                    ::rank
                                    )
                            )
                            .orElse(
                                    StructureDamageLevel.NONE
                            );

            int meaningfulDamageCount =
                    (int) signals
                            .stream()
                            .filter(
                                    StructureDamageSignal
                                            ::meaningful
                            )
                            .count();

            StructureDamageEvidence evidence =
                    new StructureDamageEvidence(
                            supportEvidence,
                            signals,
                            strongestDamageLevel,
                            meaningfulDamageCount,
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
                            "월령 정기 파격 Evidence가 둘 이상 생성되었습니다."
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
                    "월령 정기 파격 Evidence를 생성하지 못했습니다."
            );
        }

        return new StructureDamageAnalysis(
                strength.dayMaster(),
                support.monthBranch(),
                mainQiEvidence,
                evidences
        );
    }

    private List<StructureDamageSignal>
    createDamageSignals(

            DayMasterStrengthAnalysis strength,

            StructureFormationSupportEvidence
                    supportEvidence

    ) {

        List<StructureDamagePatternType> patterns =
                structureDamageRule
                        .requiredPatterns(
                                supportEvidence.tenGod()
                        );

        if (
                patterns.isEmpty()
        ) {

            return List.of();
        }

        List<StructureDamageSignal> signals =
                new ArrayList<>();

        for (
                StructureDamagePatternType pattern
                : patterns
        ) {

            if (
                    pattern.absenceBased()
            ) {

                signals.add(
                        createAbsenceBasedSignal(
                                supportEvidence,
                                pattern
                        )
                );

            } else {

                signals.add(
                        createSourceBasedSignal(
                                strength,
                                pattern
                        )
                );
            }
        }

        return List.copyOf(
                signals
        );
    }

    /**
     * 일반 공격 source 기반 파격 Signal.
     *
     * 예:
     *
     * 상관견관
     * 편인도식
     * 재극인
     * 재생살
     * 비겁탈재
     */
    private StructureDamageSignal
    createSourceBasedSignal(

            DayMasterStrengthAnalysis strength,

            StructureDamagePatternType pattern

    ) {

        DayMasterElementRole damagingRole =
                Objects.requireNonNull(
                        pattern.damagingRole(),
                        "source 기반 파격에는 damagingRole이 필요합니다."
                );

        DayMasterElementRelation relation =
                strength
                        .elementRelationAnalysis()
                        .relationOf(
                                damagingRole
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "파격 source의 오행 관계를 찾을 수 없습니다: "
                                                        + damagingRole
                                        )
                        );

        FiveElement damagingElement =
                relation.targetElement();

        List<DayMasterForceEvidence> matchingSources =
                strength
                        .forceFormationAnalysis()
                        .evidences()
                        .stream()
                        .filter(
                                evidence ->
                                        evidence.role()
                                                == damagingRole
                        )
                        .filter(
                                evidence ->
                                        matchesExactTenGod(
                                                strength,
                                                evidence,
                                                pattern
                                        )
                        )
                        .toList();

        int visibleSourceCount =
                (int) matchingSources
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::visibleStemSource
                        )
                        .count();

        int hiddenSourceCount =
                (int) matchingSources
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::hiddenStemSource
                        )
                        .count();

        int exposedHiddenSourceCount =
                (int) matchingSources
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::exposedHiddenStem
                        )
                        .count();

        int elementFinalPower =
                strength
                        .elementStrengthAnalysis()
                        .powerOf(
                                damagingElement
                        );

        int elementShareBasisPoints =
                strength
                        .elementStrengthAnalysis()
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                damagingElement
                        );

        StructureDamageLevel level =
                structureDamageRule
                        .determineSourceDamageLevel(
                                elementFinalPower,
                                elementShareBasisPoints,
                                visibleSourceCount,
                                hiddenSourceCount,
                                exposedHiddenSourceCount
                        );

        return new StructureDamageSignal(
                pattern,
                damagingElement,
                elementFinalPower,
                elementShareBasisPoints,
                visibleSourceCount,
                hiddenSourceCount,
                exposedHiddenSourceCount,
                false,
                level
        );
    }

    /**
     * 제화 부족처럼
     * 특정 공격 source가 아니라
     * 필요한 성격 구조의 부재를 보는 Signal.
     */
    private StructureDamageSignal
    createAbsenceBasedSignal(

            StructureFormationSupportEvidence
                    supportEvidence,

            StructureDamagePatternType pattern

    ) {

        if (
                pattern
                        != StructureDamagePatternType
                        .SEVEN_KILLINGS_LACKS_REGULATION
        ) {

            throw new IllegalStateException(
                    "지원하지 않는 부재 기반 파격 패턴입니다: "
                            + pattern
            );
        }

        StructureDamageLevel level =
                structureDamageRule
                        .determineSevenKillingsRegulationDamage(
                                supportEvidence
                        );

        return new StructureDamageSignal(
                pattern,
                null,
                0,
                0,
                0,
                0,
                0,
                true,
                level
        );
    }

    /**
     * 패턴이 정확한 십성을 요구하지 않으면
     * 큰 십성 그룹만 맞으면 된다.
     *
     * 정확한 십성을 요구하면:
     *
     * 일간
     * VS
     * 실제 source 천간
     *
     * 을 계산해 확인한다.
     */
    private boolean matchesExactTenGod(

            DayMasterStrengthAnalysis strength,

            DayMasterForceEvidence source,

            StructureDamagePatternType pattern

    ) {

        if (
                !pattern.requiresExactTenGod()
        ) {

            return true;
        }

        StructureTenGodType actualTenGod =
                structureTenGodCalculator
                        .calculate(
                                strength.dayMaster(),
                                source.stem()
                        );

        return actualTenGod
                == pattern.requiredExactTenGod();
    }

    private void validateConsistency(

            DayMasterStrengthAnalysis strength,

            StructureFormationSupportAnalysis support

    ) {

        if (
                strength.dayMaster()
                        != support.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 분석과 성격 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                support.evidences()
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "성격 분석 Evidence가 존재하지 않습니다."
            );
        }
    }
}