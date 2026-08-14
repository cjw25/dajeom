package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.SpecialStructureType;
import com.dajeom.type.StructureStatus;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차 전체 격국 분석 결과.
 *
 * 포함:
 *
 * 5-1 월령 격 후보
 * 5-2 투출 우선순위
 * 5-3 격 성립도
 * 5-4 일간 강약 적합성
 * 5-5 성격(成格)
 * 5-6 파격(破格)
 * 5-7 순도·혼잡도
 * 5-8 특수·종격 후보
 * 5-9 주격·보조격
 * 5-10 최종 격 상태
 *
 * 이후:
 *
 * - 조후
 * - 병약
 * - 통관
 * - 용신
 *
 * 분석에서 사용할 수 있다.
 */
public record StructureAnalysis(

        DayMasterStrengthAnalysis
        dayMasterStrengthAnalysis,

        MonthStructureCandidateAnalysis
        monthStructureCandidateAnalysis,

        StructureExposurePriorityAnalysis
        exposurePriorityAnalysis,

        StructureCandidateEstablishmentAnalysis
        establishmentAnalysis,

        StructureBalanceAnalysis
        balanceAnalysis,

        StructureFormationSupportAnalysis
        formationSupportAnalysis,

        StructureDamageAnalysis
        damageAnalysis,

        StructurePurityAnalysis
        purityAnalysis,

        SpecialStructureAnalysis
        specialStructureAnalysis,

        StructureSelectionAnalysis
        selectionAnalysis,

        StructureStatusEvidence
        statusEvidence

) {

    public StructureAnalysis {

        Objects.requireNonNull(
                dayMasterStrengthAnalysis,
                "신강·신약 분석이 필요합니다."
        );

        Objects.requireNonNull(
                monthStructureCandidateAnalysis,
                "월령 격 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                exposurePriorityAnalysis,
                "투출 우선순위 분석이 필요합니다."
        );

        Objects.requireNonNull(
                establishmentAnalysis,
                "격 성립도 분석이 필요합니다."
        );

        Objects.requireNonNull(
                balanceAnalysis,
                "격국 강약 분석이 필요합니다."
        );

        Objects.requireNonNull(
                formationSupportAnalysis,
                "성격 분석이 필요합니다."
        );

        Objects.requireNonNull(
                damageAnalysis,
                "파격 분석이 필요합니다."
        );

        Objects.requireNonNull(
                purityAnalysis,
                "순도 분석이 필요합니다."
        );

        Objects.requireNonNull(
                specialStructureAnalysis,
                "특수격 분석이 필요합니다."
        );

        Objects.requireNonNull(
                selectionAnalysis,
                "주격·보조격 분석이 필요합니다."
        );

        Objects.requireNonNull(
                statusEvidence,
                "최종 격 상태 Evidence가 필요합니다."
        );

        validateConsistency(
                dayMasterStrengthAnalysis,
                monthStructureCandidateAnalysis,
                exposurePriorityAnalysis,
                establishmentAnalysis,
                balanceAnalysis,
                formationSupportAnalysis,
                damageAnalysis,
                purityAnalysis,
                specialStructureAnalysis,
                selectionAnalysis,
                statusEvidence
        );
    }

    private static void validateConsistency(

            DayMasterStrengthAnalysis strength,

            MonthStructureCandidateAnalysis candidates,

            StructureExposurePriorityAnalysis exposure,

            StructureCandidateEstablishmentAnalysis establishment,

            StructureBalanceAnalysis balance,

            StructureFormationSupportAnalysis support,

            StructureDamageAnalysis damage,

            StructurePurityAnalysis purity,

            SpecialStructureAnalysis special,

            StructureSelectionAnalysis selection,

            StructureStatusEvidence statusEvidence

    ) {

        HeavenlyStem dayMaster =
                strength.dayMaster();

        if (
                dayMaster != candidates.dayMaster()
                        || dayMaster != exposure.dayMaster()
                        || dayMaster != establishment.dayMaster()
                        || dayMaster != balance.dayMaster()
                        || dayMaster != support.dayMaster()
                        || dayMaster != damage.dayMaster()
                        || dayMaster != purity.dayMaster()
                        || dayMaster != special.dayMaster()
                        || dayMaster != selection.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "5차 격국 분석 단계들의 일간이 서로 일치하지 않습니다."
            );
        }

        EarthlyBranch monthBranch =
                candidates.monthBranch();

        if (
                monthBranch != exposure.monthBranch()
                        || monthBranch != establishment.monthBranch()
                        || monthBranch != support.monthBranch()
                        || monthBranch != damage.monthBranch()
                        || monthBranch != purity.monthBranch()
                        || monthBranch != special.monthBranch()
                        || monthBranch != selection.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "5차 격국 분석 단계들의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                statusEvidence.primaryCandidate().kind()
                        != selection.primaryCandidate().kind()
        ) {

            throw new IllegalArgumentException(
                    "최종 상태 Evidence와 주격 후보 종류가 일치하지 않습니다."
            );
        }

        if (
                statusEvidence.primaryCandidate().standard()
        ) {

            if (
                    statusEvidence.primaryCandidate().standardTenGod()
                            != selection.primaryCandidate().standardTenGod()
            ) {

                throw new IllegalArgumentException(
                        "최종 상태 Evidence와 주격 일반격이 일치하지 않습니다."
                );
            }

        } else {

            if (
                    statusEvidence.primaryCandidate().specialType()
                            != selection.primaryCandidate().specialType()
            ) {

                throw new IllegalArgumentException(
                        "최종 상태 Evidence와 주격 특수격이 일치하지 않습니다."
                );
            }
        }
    }

    public HeavenlyStem dayMaster() {

        return dayMasterStrengthAnalysis
                .dayMaster();
    }

    public EarthlyBranch monthBranch() {

        return monthStructureCandidateAnalysis
                .monthBranch();
    }

    public StructureStatus status() {

        return statusEvidence
                .status();
    }

    public String statusKorean() {

        return status()
                .koreanName();
    }

    public String statusDescription() {

        return status()
                .description();
    }

    public StructureSelectionCandidate
    primaryCandidate() {

        return selectionAnalysis
                .primaryCandidate();
    }

    public String primaryNameKorean() {

        return selectionAnalysis
                .primaryNameKorean();
    }

    public boolean primaryIsStandard() {

        return selectionAnalysis
                .primaryIsStandard();
    }

    public boolean primaryIsSpecial() {

        return selectionAnalysis
                .primaryIsSpecial();
    }

    public Optional<StructureTenGodType>
    primaryStandardTenGod() {

        return selectionAnalysis
                .primaryStandardTenGod();
    }

    public Optional<SpecialStructureType>
    primarySpecialType() {

        return selectionAnalysis
                .primarySpecialType();
    }

    public List<StructureSelectionCandidate>
    secondaryCandidates() {

        return selectionAnalysis
                .secondaryCandidates();
    }

    public List<String> secondaryNamesKorean() {

        return secondaryCandidates()
                .stream()
                .map(
                        StructureSelectionCandidate
                                ::candidateNameKorean
                )
                .toList();
    }

    public boolean hasSecondaryStructure() {

        return selectionAnalysis
                .hasSecondaryStructure();
    }

    /**
     * 특수격 후보가 하나라도 존재하는가.
     *
     * 주격이 일반격이더라도
     * 특수 후보가 보조/보류 상태로
     * 남아 있을 수 있다.
     */
    public boolean hasSpecialStructureCandidate() {

        return specialStructureAnalysis
                .hasAnyCandidate();
    }

    /**
     * 특수구조 추가 검토가 필요한가.
     */
    public boolean requiresSpecialReview() {

        return status()
                .specialReview()

                || specialStructureAnalysis
                .requiresSpecialStructureReview();
    }

    public boolean primaryEstablished() {

        return status()
                .establishedFamily();
    }

    public boolean primaryBurdened() {

        return statusEvidence
                .balanceBurdened();
    }

    public boolean primaryDamaged() {

        return statusEvidence
                .meaningfulDamage();
    }

    public boolean primaryStronglyDamaged() {

        return statusEvidence
                .strongDamage();
    }

    public boolean primaryRelativelyPure() {

        return statusEvidence
                .relativelyPure();
    }

    public boolean primaryHighlyMixed() {

        return statusEvidence
                .highlyMixed();
    }

    public boolean completeChart() {

        return dayMasterStrengthAnalysis
                .completeChart();
    }
}