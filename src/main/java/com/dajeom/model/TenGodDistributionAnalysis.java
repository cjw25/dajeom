package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodPowerLevel;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-1.
 *
 * 원국 전체 십성 분포·세력 분석.
 *
 * =================================================
 *
 * 일간 자기 자신은
 * 십성 occurrence로 직접 포함하지 않는다.
 *
 * 하지만 같은 오행 내부의 final power를
 * 외부 비견·겁재에 과도하게 몰아주지 않기 위해
 * power allocation denominator에는
 * 일간 자신의 visible rawWeight가 포함된다.
 *
 * =================================================
 *
 * 즉:
 *
 * 일간 = 丁
 *
 * DAY 丁 자체를
 * "비견 한 개"로 추가하지는 않는다.
 *
 * 하지만 Fire 전체 final power를
 * 다른 丙/丁 source에 전부 배분하지도 않는다.
 */
public record TenGodDistributionAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        List<TenGodDistributionEvidence> evidences,

        List<TenGodPowerSummary> summaries,

        int totalExternalTenGodPower,

        int knownPillarCount,

        boolean completeChart

) {

    public TenGodDistributionAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        summaries =
                summaries == null
                        ? List.of()
                        : List.copyOf(
                        summaries
                );

        if (
                totalExternalTenGodPower < 0
        ) {

            throw new IllegalArgumentException(
                    "전체 외부 십성 세력은 음수일 수 없습니다."
            );
        }

        int expectedKnownPillarCount =
                completeChart
                        ? 4
                        : 3;

        if (
                knownPillarCount
                        != expectedKnownPillarCount
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성 상태와 knownPillarCount가 일치하지 않습니다."
            );
        }

        if (
                summaries.size()
                        != StructureTenGodType.values().length
        ) {

            throw new IllegalArgumentException(
                    "10-1 분석은 모든 십성 종류의 Summary를 보유해야 합니다."
            );
        }

        for (
                StructureTenGodType tenGodType
                : StructureTenGodType.values()
        ) {

            long count =
                    summaries
                            .stream()
                            .filter(
                                    summary ->
                                            summary.tenGodType()
                                                    == tenGodType
                            )
                            .count();

            if (
                    count != 1
            ) {

                throw new IllegalArgumentException(
                        "십성 Summary는 종류별로 정확히 하나씩 존재해야 합니다: "
                                + tenGodType
                );
            }
        }

        int calculatedTotalPower =
                evidences
                        .stream()
                        .mapToInt(
                                TenGodDistributionEvidence
                                        ::allocatedElementPower
                        )
                        .sum();

        if (
                calculatedTotalPower
                        != totalExternalTenGodPower
        ) {

            throw new IllegalArgumentException(
                    "전체 십성 세력과 Evidence 세력 합이 일치하지 않습니다."
            );
        }

        for (
                TenGodPowerSummary summary
                : summaries
        ) {

            int evidencePower =
                    evidences
                            .stream()
                            .filter(
                                    evidence ->
                                            evidence.tenGodType()
                                                    == summary.tenGodType()
                            )
                            .mapToInt(
                                    TenGodDistributionEvidence
                                            ::allocatedElementPower
                            )
                            .sum();

            if (
                    evidencePower
                            != summary.allocatedPower()
            ) {

                throw new IllegalArgumentException(
                        "십성 Summary와 Evidence 세력이 일치하지 않습니다: "
                                + summary.tenGodType()
                );
            }
        }
    }

    public Optional<TenGodPowerSummary> summaryOf(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return Optional.empty();
        }

        return summaries
                .stream()
                .filter(
                        summary ->
                                summary.tenGodType()
                                        == tenGodType
                )
                .findFirst();
    }

    public int powerOf(
            StructureTenGodType tenGodType
    ) {

        return summaryOf(
                tenGodType
        )
                .map(
                        TenGodPowerSummary::allocatedPower
                )
                .orElse(0);
    }

    public int shareBasisPointsOf(
            StructureTenGodType tenGodType
    ) {

        return summaryOf(
                tenGodType
        )
                .map(
                        TenGodPowerSummary::shareBasisPoints
                )
                .orElse(0);
    }

    public boolean present(
            StructureTenGodType tenGodType
    ) {

        return summaryOf(
                tenGodType
        )
                .map(
                        TenGodPowerSummary::present
                )
                .orElse(false);
    }

    public boolean visible(
            StructureTenGodType tenGodType
    ) {

        return summaryOf(
                tenGodType
        )
                .map(
                        TenGodPowerSummary::visible
                )
                .orElse(false);
    }

    public List<TenGodPowerSummary> dominantTenGods() {

        return summaries
                .stream()
                .filter(
                        TenGodPowerSummary
                                ::dominant
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        TenGodPowerSummary
                                                ::allocatedPower
                                )
                                .reversed()
                )
                .toList();
    }

    public List<TenGodPowerSummary> strongTenGods() {

        return summaries
                .stream()
                .filter(
                        summary ->
                                summary.powerLevel()
                                        == TenGodPowerLevel.STRONG
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        TenGodPowerSummary
                                                ::allocatedPower
                                )
                                .reversed()
                )
                .toList();
    }

    public List<TenGodPowerSummary> meaningfulTenGods() {

        return summaries
                .stream()
                .filter(
                        TenGodPowerSummary
                                ::meaningful
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        TenGodPowerSummary
                                                ::allocatedPower
                                )
                                .reversed()
                )
                .toList();
    }

    public List<TenGodPowerSummary> presentTenGods() {

        return summaries
                .stream()
                .filter(
                        TenGodPowerSummary
                                ::present
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        TenGodPowerSummary
                                                ::allocatedPower
                                )
                                .reversed()
                )
                .toList();
    }

    public List<TenGodDistributionEvidence> evidencesOf(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.tenGodType()
                                        == tenGodType
                )
                .toList();
    }

    /*
     * =================================================
     * 비겁
     * =================================================
     */

    public int companionFamilyPower() {

        return powerOf(
                StructureTenGodType.COMPANION
        )
                + powerOf(
                StructureTenGodType.ROB_WEALTH
        );
    }

    /*
     * =================================================
     * 식상
     * =================================================
     */

    public int outputFamilyPower() {

        return powerOf(
                StructureTenGodType.EATING_GOD
        )
                + powerOf(
                StructureTenGodType.HURTING_OFFICER
        );
    }

    /*
     * =================================================
     * 재성
     * =================================================
     */

    public int wealthFamilyPower() {

        return powerOf(
                StructureTenGodType.INDIRECT_WEALTH
        )
                + powerOf(
                StructureTenGodType.DIRECT_WEALTH
        );
    }

    /*
     * =================================================
     * 관살
     * =================================================
     */

    public int officerFamilyPower() {

        return powerOf(
                StructureTenGodType.SEVEN_KILLINGS
        )
                + powerOf(
                StructureTenGodType.DIRECT_OFFICER
        );
    }

    /*
     * =================================================
     * 인성
     * =================================================
     */

    public int resourceFamilyPower() {

        return powerOf(
                StructureTenGodType.INDIRECT_RESOURCE
        )
                + powerOf(
                StructureTenGodType.DIRECT_RESOURCE
        );
    }

    public List<TenGodPowerSummary> monthBranchTenGods() {

        return summaries
                .stream()
                .filter(
                        TenGodPowerSummary
                                ::monthBranchPresent
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        TenGodPowerSummary
                                                ::monthBranchAllocatedPower
                                )
                                .reversed()
                )
                .toList();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}