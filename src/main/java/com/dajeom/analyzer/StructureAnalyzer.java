package com.dajeom.analyzer;

import com.dajeom.model.ElementStrength;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.StrengthAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.StructureResult;
import com.dajeom.model.TenGodAssignment;
import com.dajeom.type.StrengthLevel;
import com.dajeom.type.StructureType;
import com.dajeom.type.TenGod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 십성, 오행 세력, 신강·신약을 조합해
 * 주요 명리 구조를 판정한다.
 *
 * DAJEOM_STRUCTURE_V1 규칙을 사용한다.
 */
@Component
public class StructureAnalyzer {

    public static final String RULE_VERSION =
            "DAJEOM_STRUCTURE_V1";

    private static final int DOMINANT_TEN_GOD_COUNT =
            4;

    public StructureAnalysis analyze(
            FourPillars fourPillars,
            List<HiddenStem> hiddenStems,
            List<TenGodAssignment> tenGodAssignments,
            ElementStrength elementStrength,
            StrengthAnalysis strengthAnalysis
    ) {
        Map<TenGod, Integer> tenGodCounts =
                countTenGods(
                        tenGodAssignments
                );

        List<StructureResult> results =
                new ArrayList<>();

        results.add(
                analyzeWealthHeavyWeakSelf(
                        tenGodCounts,
                        strengthAnalysis
                )
        );

        results.add(
                analyzeOfficerKillingMixed(
                        tenGodCounts
                )
        );

        results.add(
                analyzeHurtingOfficerSeesOfficer(
                        tenGodCounts
                )
        );

        results.add(
                analyzeEatingGodControlsKilling(
                        tenGodCounts
                )
        );

        results.add(
                analyzeKillingResourceGeneration(
                        tenGodCounts
                )
        );

        results.add(
                analyzeOutputGeneratesWealth(
                        tenGodCounts
                )
        );

        results.add(
                analyzeWealthGeneratesOfficer(
                        tenGodCounts
                )
        );

        results.add(
                analyzeOfficerGeneratesResource(
                        tenGodCounts
                )
        );

        results.add(
                analyzeResourcePeerGeneration(
                        tenGodCounts
                )
        );

        results.add(
                analyzePeersCompeteForWealth(
                        tenGodCounts,
                        strengthAnalysis
                )
        );

        results.add(
                analyzeDominantGroup(
                        StructureType.WEALTH_DOMINANT,
                        wealthCount(tenGodCounts),
                        "WEALTH"
                )
        );

        results.add(
                analyzeDominantGroup(
                        StructureType.OFFICER_DOMINANT,
                        officerCount(tenGodCounts),
                        "OFFICER"
                )
        );

        results.add(
                analyzeDominantGroup(
                        StructureType.RESOURCE_DOMINANT,
                        resourceCount(tenGodCounts),
                        "RESOURCE"
                )
        );

        results.add(
                analyzeDominantGroup(
                        StructureType.OUTPUT_DOMINANT,
                        outputCount(tenGodCounts),
                        "OUTPUT"
                )
        );

        results.add(
                analyzeDominantGroup(
                        StructureType.PEER_DOMINANT,
                        peerCount(tenGodCounts),
                        "PEER"
                )
        );

        results.add(
                analyzeMissingGroup(
                        StructureType.NO_WEALTH,
                        wealthCount(tenGodCounts),
                        "WEALTH"
                )
        );

        results.add(
                analyzeMissingGroup(
                        StructureType.NO_OFFICER,
                        officerCount(tenGodCounts),
                        "OFFICER"
                )
        );

        results.add(
                analyzeMissingGroup(
                        StructureType.NO_RESOURCE,
                        resourceCount(tenGodCounts),
                        "RESOURCE"
                )
        );

        results.add(
                analyzeMissingGroup(
                        StructureType.NO_OUTPUT,
                        outputCount(tenGodCounts),
                        "OUTPUT"
                )
        );

        return new StructureAnalysis(
                results,
                RULE_VERSION
        );
    }

    private StructureResult analyzeWealthHeavyWeakSelf(
            Map<TenGod, Integer> counts,
            StrengthAnalysis strengthAnalysis
    ) {
        int wealthCount =
                wealthCount(counts);

        boolean weak =
                isWeak(
                        strengthAnalysis.level()
                );

        double score = 0.0;

        List<String> codes =
                new ArrayList<>();

        List<String> descriptions =
                new ArrayList<>();

        if (wealthCount >= 3) {
            score += 45.0;
            codes.add("WEALTH_COUNT_HIGH");
            descriptions.add(
                    "재성 개수가 "
                            + wealthCount
                            + "개로 많음"
            );
        } else if (wealthCount == 2) {
            score += 25.0;
            codes.add("WEALTH_COUNT_MEDIUM");
            descriptions.add(
                    "재성 개수가 2개로 확인됨"
            );
        }

        if (weak) {
            score += 45.0;
            codes.add("DAY_MASTER_WEAK");
            descriptions.add(
                    "일간 강약이 "
                            + strengthAnalysis
                            .level()
                            .getKoreanName()
                            + "으로 판정됨"
            );
        }

        if (strengthAnalysis
                .supportPercentage() < 30.0) {

            score += 10.0;
            codes.add("SUPPORT_LOW");
            descriptions.add(
                    "일간 생조 세력이 "
                            + strengthAnalysis
                            .supportPercentage()
                            + "%로 낮음"
            );
        }

        boolean matched =
                wealthCount >= 3
                        && weak;

        return createResult(
                StructureType
                        .WEALTH_HEAVY_WEAK_SELF,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult analyzeOfficerKillingMixed(
            Map<TenGod, Integer> counts
    ) {
        int directOfficer =
                count(
                        counts,
                        TenGod.DIRECT_OFFICER
                );

        int sevenKillings =
                count(
                        counts,
                        TenGod.SEVEN_KILLINGS
                );

        boolean matched =
                directOfficer > 0
                        && sevenKillings > 0;

        double score =
                matched
                        ? 70.0
                        + Math.min(
                        30.0,
                        (
                                directOfficer
                                        + sevenKillings
                        ) * 5.0
                )
                        : 0.0;

        List<String> codes =
                new ArrayList<>();

        List<String> descriptions =
                new ArrayList<>();

        if (directOfficer > 0) {
            codes.add("DIRECT_OFFICER_PRESENT");
            descriptions.add(
                    "정관이 "
                            + directOfficer
                            + "개 존재함"
            );
        }

        if (sevenKillings > 0) {
            codes.add("SEVEN_KILLINGS_PRESENT");
            descriptions.add(
                    "편관이 "
                            + sevenKillings
                            + "개 존재함"
            );
        }

        return createResult(
                StructureType
                        .OFFICER_KILLING_MIXED,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult
    analyzeHurtingOfficerSeesOfficer(
            Map<TenGod, Integer> counts
    ) {
        int hurtingOfficer =
                count(
                        counts,
                        TenGod.HURTING_OFFICER
                );

        int directOfficer =
                count(
                        counts,
                        TenGod.DIRECT_OFFICER
                );

        boolean matched =
                hurtingOfficer > 0
                        && directOfficer > 0;

        double score =
                matched
                        ? 70.0
                        + Math.min(
                        30.0,
                        (
                                hurtingOfficer
                                        + directOfficer
                        ) * 5.0
                )
                        : 0.0;

        return createResult(
                StructureType
                        .HURTING_OFFICER_SEES_OFFICER,
                matched,
                score,
                listWhenPositive(
                        hurtingOfficer,
                        "HURTING_OFFICER_PRESENT"
                ),
                descriptions(
                        hurtingOfficer,
                        "상관",
                        directOfficer,
                        "정관"
                )
        );
    }

    private StructureResult
    analyzeEatingGodControlsKilling(
            Map<TenGod, Integer> counts
    ) {
        int eatingGod =
                count(
                        counts,
                        TenGod.EATING_GOD
                );

        int sevenKillings =
                count(
                        counts,
                        TenGod.SEVEN_KILLINGS
                );

        boolean matched =
                eatingGod > 0
                        && sevenKillings > 0;

        double score =
                matched
                        ? 70.0
                        + Math.min(
                        30.0,
                        (
                                eatingGod
                                        + sevenKillings
                        ) * 5.0
                )
                        : 0.0;

        List<String> codes =
                new ArrayList<>();

        List<String> descriptions =
                new ArrayList<>();

        addEvidence(
                codes,
                descriptions,
                eatingGod,
                "EATING_GOD_PRESENT",
                "식신"
        );

        addEvidence(
                codes,
                descriptions,
                sevenKillings,
                "SEVEN_KILLINGS_PRESENT",
                "편관"
        );

        return createResult(
                StructureType
                        .EATING_GOD_CONTROLS_KILLING,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult
    analyzeKillingResourceGeneration(
            Map<TenGod, Integer> counts
    ) {
        int sevenKillings =
                count(
                        counts,
                        TenGod.SEVEN_KILLINGS
                );

        int resources =
                resourceCount(counts);

        boolean matched =
                sevenKillings > 0
                        && resources > 0;

        double score =
                matched
                        ? 65.0
                        + Math.min(
                        35.0,
                        (
                                sevenKillings
                                        + resources
                        ) * 5.0
                )
                        : 0.0;

        List<String> codes =
                new ArrayList<>();

        List<String> descriptions =
                new ArrayList<>();

        addEvidence(
                codes,
                descriptions,
                sevenKillings,
                "SEVEN_KILLINGS_PRESENT",
                "편관"
        );

        addEvidence(
                codes,
                descriptions,
                resources,
                "RESOURCE_PRESENT",
                "인성"
        );

        return createResult(
                StructureType
                        .KILLING_RESOURCE_GENERATION,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult
    analyzeOutputGeneratesWealth(
            Map<TenGod, Integer> counts
    ) {
        int output =
                outputCount(counts);

        int wealth =
                wealthCount(counts);

        boolean matched =
                output > 0
                        && wealth > 0;

        double score =
                matched
                        ? 60.0
                        + Math.min(
                        40.0,
                        (
                                output
                                        + wealth
                        ) * 5.0
                )
                        : 0.0;

        List<String> codes =
                new ArrayList<>();

        List<String> descriptions =
                new ArrayList<>();

        addEvidence(
                codes,
                descriptions,
                output,
                "OUTPUT_PRESENT",
                "식상"
        );

        addEvidence(
                codes,
                descriptions,
                wealth,
                "WEALTH_PRESENT",
                "재성"
        );

        return createResult(
                StructureType
                        .OUTPUT_GENERATES_WEALTH,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult
    analyzeWealthGeneratesOfficer(
            Map<TenGod, Integer> counts
    ) {
        int wealth =
                wealthCount(counts);

        int officer =
                officerCount(counts);

        boolean matched =
                wealth > 0
                        && officer > 0;

        double score =
                matched
                        ? 60.0
                        + Math.min(
                        40.0,
                        (
                                wealth
                                        + officer
                        ) * 5.0
                )
                        : 0.0;

        List<String> codes =
                new ArrayList<>();

        List<String> descriptions =
                new ArrayList<>();

        addEvidence(
                codes,
                descriptions,
                wealth,
                "WEALTH_PRESENT",
                "재성"
        );

        addEvidence(
                codes,
                descriptions,
                officer,
                "OFFICER_PRESENT",
                "관성"
        );

        return createResult(
                StructureType
                        .WEALTH_GENERATES_OFFICER,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult
    analyzeOfficerGeneratesResource(
            Map<TenGod, Integer> counts
    ) {
        int officer =
                officerCount(counts);

        int resource =
                resourceCount(counts);

        boolean matched =
                officer > 0
                        && resource > 0;

        double score =
                matched
                        ? 60.0
                        + Math.min(
                        40.0,
                        (
                                officer
                                        + resource
                        ) * 5.0
                )
                        : 0.0;

        List<String> codes =
                new ArrayList<>();

        List<String> descriptions =
                new ArrayList<>();

        addEvidence(
                codes,
                descriptions,
                officer,
                "OFFICER_PRESENT",
                "관성"
        );

        addEvidence(
                codes,
                descriptions,
                resource,
                "RESOURCE_PRESENT",
                "인성"
        );

        return createResult(
                StructureType
                        .OFFICER_GENERATES_RESOURCE,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult
    analyzeResourcePeerGeneration(
            Map<TenGod, Integer> counts
    ) {
        int resource =
                resourceCount(counts);

        int peer =
                peerCount(counts);

        boolean matched =
                resource > 0
                        && peer > 0;

        double score =
                matched
                        ? 60.0
                        + Math.min(
                        40.0,
                        (
                                resource
                                        + peer
                        ) * 5.0
                )
                        : 0.0;

        List<String> codes =
                new ArrayList<>();

        List<String> descriptions =
                new ArrayList<>();

        addEvidence(
                codes,
                descriptions,
                resource,
                "RESOURCE_PRESENT",
                "인성"
        );

        addEvidence(
                codes,
                descriptions,
                peer,
                "PEER_PRESENT",
                "비겁"
        );

        return createResult(
                StructureType
                        .RESOURCE_PEER_GENERATION,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult
    analyzePeersCompeteForWealth(
            Map<TenGod, Integer> counts,
            StrengthAnalysis strengthAnalysis
    ) {
        int peer =
                peerCount(counts);

        int wealth =
                wealthCount(counts);

        boolean strong =
                isStrong(
                        strengthAnalysis.level()
                );

        boolean matched =
                peer >= 3
                        && wealth > 0
                        && strong;

        double score = 0.0;

        List<String> codes =
                new ArrayList<>();

        List<String> descriptions =
                new ArrayList<>();

        if (peer >= 3) {
            score += 45.0;
            codes.add("PEER_COUNT_HIGH");
            descriptions.add(
                    "비겁이 "
                            + peer
                            + "개로 많음"
            );
        }

        if (wealth > 0) {
            score += 25.0;
            codes.add("WEALTH_PRESENT");
            descriptions.add(
                    "재성이 "
                            + wealth
                            + "개 존재함"
            );
        }

        if (strong) {
            score += 30.0;
            codes.add("DAY_MASTER_STRONG");
            descriptions.add(
                    "일간이 "
                            + strengthAnalysis
                            .level()
                            .getKoreanName()
                            + "으로 판정됨"
            );
        }

        return createResult(
                StructureType
                        .PEERS_COMPETE_FOR_WEALTH,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult analyzeDominantGroup(
            StructureType structureType,
            int count,
            String codePrefix
    ) {
        boolean matched =
                count >= DOMINANT_TEN_GOD_COUNT;

        double score =
                Math.min(
                        100.0,
                        count * 20.0
                );

        List<String> codes =
                count > 0
                        ? List.of(
                        codePrefix
                                + "_COUNT_"
                                + count
                )
                        : List.of();

        List<String> descriptions =
                count > 0
                        ? List.of(
                        structureType
                                .getKoreanName()
                                + " 관련 십성이 "
                                + count
                                + "개 확인됨"
                )
                        : List.of();

        return createResult(
                structureType,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private StructureResult analyzeMissingGroup(
            StructureType structureType,
            int count,
            String codePrefix
    ) {
        boolean matched =
                count == 0;

        double score =
                matched
                        ? 100.0
                        : 0.0;

        List<String> codes =
                matched
                        ? List.of(
                        codePrefix + "_NOT_FOUND"
                )
                        : List.of(
                        codePrefix
                                + "_COUNT_"
                                + count
                );

        List<String> descriptions =
                matched
                        ? List.of(
                        structureType
                                .getKoreanName()
                                + " 조건에 해당하는 십성이 없음"
                )
                        : List.of(
                        "관련 십성이 "
                                + count
                                + "개 존재함"
                );

        return createResult(
                structureType,
                matched,
                score,
                codes,
                descriptions
        );
    }

    private Map<TenGod, Integer> countTenGods(
            List<TenGodAssignment> assignments
    ) {
        EnumMap<TenGod, Integer> counts =
                new EnumMap<>(TenGod.class);

        for (TenGod tenGod : TenGod.values()) {
            counts.put(tenGod, 0);
        }

        for (TenGodAssignment assignment : assignments) {
            counts.compute(
                    assignment.tenGod(),
                    (key, value) ->
                            value == null
                                    ? 1
                                    : value + 1
            );
        }

        return counts;
    }

    private int wealthCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.INDIRECT_WEALTH
        ) + count(
                counts,
                TenGod.DIRECT_WEALTH
        );
    }

    private int officerCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.SEVEN_KILLINGS
        ) + count(
                counts,
                TenGod.DIRECT_OFFICER
        );
    }

    private int resourceCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.INDIRECT_RESOURCE
        ) + count(
                counts,
                TenGod.DIRECT_RESOURCE
        );
    }

    private int outputCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.EATING_GOD
        ) + count(
                counts,
                TenGod.HURTING_OFFICER
        );
    }

    private int peerCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.FRIEND
        ) + count(
                counts,
                TenGod.ROB_WEALTH
        );
    }

    private int count(
            Map<TenGod, Integer> counts,
            TenGod tenGod
    ) {
        return counts.getOrDefault(
                tenGod,
                0
        );
    }

    private boolean isWeak(
            StrengthLevel level
    ) {
        return level
                == StrengthLevel.EXTREMELY_WEAK
                || level == StrengthLevel.WEAK
                || level == StrengthLevel.SLIGHTLY_WEAK;
    }

    private boolean isStrong(
            StrengthLevel level
    ) {
        return level
                == StrengthLevel.EXTREMELY_STRONG
                || level == StrengthLevel.STRONG
                || level == StrengthLevel.SLIGHTLY_STRONG;
    }

    private StructureResult createResult(
            StructureType structureType,
            boolean matched,
            double score,
            List<String> codes,
            List<String> descriptions
    ) {
        if (matched) {
            return StructureResult.matched(
                    structureType,
                    score,
                    codes,
                    descriptions
            );
        }

        return StructureResult.notMatched(
                structureType,
                score,
                codes,
                descriptions
        );
    }

    private void addEvidence(
            List<String> codes,
            List<String> descriptions,
            int count,
            String code,
            String koreanName
    ) {
        if (count <= 0) {
            return;
        }

        codes.add(code);

        descriptions.add(
                koreanName
                        + "이(가) "
                        + count
                        + "개 존재함"
        );
    }

    private List<String> listWhenPositive(
            int count,
            String code
    ) {
        if (count <= 0) {
            return List.of();
        }

        return List.of(code);
    }

    private List<String> descriptions(
            int firstCount,
            String firstName,
            int secondCount,
            String secondName
    ) {
        List<String> result =
                new ArrayList<>();

        if (firstCount > 0) {
            result.add(
                    firstName
                            + "이(가) "
                            + firstCount
                            + "개 존재함"
            );
        }

        if (secondCount > 0) {
            result.add(
                    secondName
                            + "이(가) "
                            + secondCount
                            + "개 존재함"
            );
        }

        return result;
    }
}