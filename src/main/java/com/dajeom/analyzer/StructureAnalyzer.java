package com.dajeom.analyzer;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.MonthStructureCandidateAnalysis;
import com.dajeom.model.SpecialStructureAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.StructureBalanceAnalysis;
import com.dajeom.model.StructureBalanceEvidence;
import com.dajeom.model.StructureCandidateEstablishmentAnalysis;
import com.dajeom.model.StructureDamageAnalysis;
import com.dajeom.model.StructureDamageEvidence;
import com.dajeom.model.StructureEstablishmentEvidence;
import com.dajeom.model.StructureExposurePriorityAnalysis;
import com.dajeom.model.StructureFormationSupportAnalysis;
import com.dajeom.model.StructureFormationSupportEvidence;
import com.dajeom.model.StructurePurityAnalysis;
import com.dajeom.model.StructurePurityEvidence;
import com.dajeom.model.StructureSelectionAnalysis;
import com.dajeom.model.StructureSelectionCandidate;
import com.dajeom.model.StructureStatusEvidence;
import com.dajeom.rule.StructureRule;
import com.dajeom.type.StructureStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 5차-10.
 *
 * 이미 계산된 5-1 ~ 5-9 결과를
 * 최종 StructureAnalysis로 통합한다.
 *
 * 이 Analyzer는:
 *
 * - 월령을 다시 계산하지 않는다.
 * - 투간을 다시 계산하지 않는다.
 * - 격 성립도를 다시 계산하지 않는다.
 * - 주격을 다시 결정하지 않는다.
 *
 * 오직 5-9에서 선택된 PRIMARY를 기준으로
 * 최종 StructureStatus를 결정한다.
 */
@Component
public class StructureAnalyzer {

    private final StructureRule
            structureRule;

    public StructureAnalyzer(
            StructureRule structureRule
    ) {

        this.structureRule =
                Objects.requireNonNull(
                        structureRule,
                        "StructureRule이 필요합니다."
                );
    }

    public StructureAnalysis analyze(

            DayMasterStrengthAnalysis strength,

            MonthStructureCandidateAnalysis candidates,

            StructureExposurePriorityAnalysis exposure,

            StructureCandidateEstablishmentAnalysis establishment,

            StructureBalanceAnalysis balance,

            StructureFormationSupportAnalysis support,

            StructureDamageAnalysis damage,

            StructurePurityAnalysis purity,

            SpecialStructureAnalysis special,

            StructureSelectionAnalysis selection

    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석이 필요합니다."
        );

        Objects.requireNonNull(
                candidates,
                "월령 격 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                exposure,
                "투출 우선순위 분석이 필요합니다."
        );

        Objects.requireNonNull(
                establishment,
                "격 성립도 분석이 필요합니다."
        );

        Objects.requireNonNull(
                balance,
                "격국 강약 분석이 필요합니다."
        );

        Objects.requireNonNull(
                support,
                "성격 분석이 필요합니다."
        );

        Objects.requireNonNull(
                damage,
                "파격 분석이 필요합니다."
        );

        Objects.requireNonNull(
                purity,
                "순도 분석이 필요합니다."
        );

        Objects.requireNonNull(
                special,
                "특수격 분석이 필요합니다."
        );

        Objects.requireNonNull(
                selection,
                "주격·보조격 분석이 필요합니다."
        );

        validateConsistency(
                strength,
                candidates,
                exposure,
                establishment,
                balance,
                support,
                damage,
                purity,
                special,
                selection
        );

        StructureSelectionCandidate primary =
                selection.primaryCandidate();

        StructureStatusEvidence statusEvidence;

        if (
                primary.special()
        ) {

            statusEvidence =
                    createSpecialStatusEvidence(
                            primary,
                            selection
                    );

        } else {

            statusEvidence =
                    createStandardStatusEvidence(
                            primary,
                            selection
                    );
        }

        return new StructureAnalysis(
                strength,
                candidates,
                exposure,
                establishment,
                balance,
                support,
                damage,
                purity,
                special,
                selection,
                statusEvidence
        );
    }

    private StructureStatusEvidence
    createSpecialStatusEvidence(

            StructureSelectionCandidate primary,

            StructureSelectionAnalysis selection

    ) {

        StructureStatus status =
                structureRule
                        .determineSpecialStatus();

        return new StructureStatusEvidence(
                primary,
                status,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                selection.secondaryCount(),
                selection.deferredCount()
        );
    }

    private StructureStatusEvidence
    createStandardStatusEvidence(

            StructureSelectionCandidate primary,

            StructureSelectionAnalysis selection

    ) {

        StructurePurityEvidence purityEvidence =
                Objects.requireNonNull(
                        primary.standardEvidence(),
                        "일반격 PRIMARY에는 StructurePurityEvidence가 필요합니다."
                );

        StructureDamageEvidence damageEvidence =
                purityEvidence
                        .damageEvidence();

        StructureFormationSupportEvidence supportEvidence =
                damageEvidence
                        .supportEvidence();

        StructureBalanceEvidence balanceEvidence =
                supportEvidence
                        .balanceEvidence();

        StructureEstablishmentEvidence establishmentEvidence =
                balanceEvidence
                        .establishmentEvidence();

        StructureStatus status =
                structureRule
                        .determineStandardStatus(
                                establishmentEvidence.level(),
                                balanceEvidence.balanceLevel(),
                                supportEvidence.strongestSupportLevel(),
                                damageEvidence.strongestDamageLevel(),
                                purityEvidence.purityLevel()
                        );

        boolean meaningfulEstablishment =
                establishmentEvidence
                        .meaningfulCandidate();

        boolean strongEstablishment =
                establishmentEvidence
                        .established();

        boolean balanceAcceptable =
                balanceEvidence
                        .acceptable();

        boolean balanceBurdened =
                balanceEvidence
                        .burdened();

        boolean meaningfulFormationSupport =
                supportEvidence
                        .hasMeaningfulSupport();

        boolean strongFormationSupport =
                supportEvidence
                        .hasStrongSupport();

        boolean meaningfulDamage =
                damageEvidence
                        .hasMeaningfulDamage();

        boolean strongDamage =
                damageEvidence
                        .hasStrongDamage();

        boolean relativelyPure =
                purityEvidence
                        .relativelyPure();

        boolean highlyMixed =
                purityEvidence
                        .highlyMixed();

        return new StructureStatusEvidence(
                primary,
                status,
                meaningfulEstablishment,
                strongEstablishment,
                balanceAcceptable,
                balanceBurdened,
                meaningfulFormationSupport,
                strongFormationSupport,
                meaningfulDamage,
                strongDamage,
                relativelyPure,
                highlyMixed,
                selection.secondaryCount(),
                selection.deferredCount()
        );
    }

    private void validateConsistency(

            DayMasterStrengthAnalysis strength,

            MonthStructureCandidateAnalysis candidates,

            StructureExposurePriorityAnalysis exposure,

            StructureCandidateEstablishmentAnalysis establishment,

            StructureBalanceAnalysis balance,

            StructureFormationSupportAnalysis support,

            StructureDamageAnalysis damage,

            StructurePurityAnalysis purity,

            SpecialStructureAnalysis special,

            StructureSelectionAnalysis selection

    ) {

        if (
                strength.dayMaster()
                        != candidates.dayMaster()

                        || strength.dayMaster()
                        != exposure.dayMaster()

                        || strength.dayMaster()
                        != establishment.dayMaster()

                        || strength.dayMaster()
                        != balance.dayMaster()

                        || strength.dayMaster()
                        != support.dayMaster()

                        || strength.dayMaster()
                        != damage.dayMaster()

                        || strength.dayMaster()
                        != purity.dayMaster()

                        || strength.dayMaster()
                        != special.dayMaster()

                        || strength.dayMaster()
                        != selection.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "5차 분석 결과들의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                candidates.monthBranch()
                        != exposure.monthBranch()

                        || candidates.monthBranch()
                        != establishment.monthBranch()

                        || candidates.monthBranch()
                        != support.monthBranch()

                        || candidates.monthBranch()
                        != damage.monthBranch()

                        || candidates.monthBranch()
                        != purity.monthBranch()

                        || candidates.monthBranch()
                        != special.monthBranch()

                        || candidates.monthBranch()
                        != selection.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "5차 분석 결과들의 월지가 서로 일치하지 않습니다."
            );
        }
    }
}