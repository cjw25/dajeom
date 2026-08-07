package com.dajeom.analyzer;

import com.dajeom.model.ElementStrength;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.Pillar;
import com.dajeom.model.RelationImpactResult;
import com.dajeom.model.StrengthAnalysis;
import com.dajeom.model.StrengthEvidence;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.StrengthFactorType;
import com.dajeom.type.StrengthLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 월령, 통근, 천간 투출, 오행 세력 비율,
 * 합충형파해의 통근 영향을 이용해
 * 일간의 신강·신약을 판정한다.
 */
@Component
public class DayMasterStrengthAnalyzer {

    public static final String RULE_VERSION =
            "DAJEOM_STRENGTH_V2";

    private static final double BASE_SCORE =
            50.0;

    public StrengthAnalysis analyze(
            FourPillars fourPillars,
            List<HiddenStem> hiddenStems,
            ElementStrength elementStrength,
            RelationImpactResult relationImpact
    ) {
        HeavenlyStem dayMaster =
                fourPillars.dayPillar().stem();

        FiveElement dayMasterElement =
                dayMaster.getElement();

        List<StrengthEvidence> evidence =
                new ArrayList<>();

        double score = BASE_SCORE;

        evidence.add(
                new StrengthEvidence(
                        "BASE_SCORE",
                        StrengthFactorType.BASE_SCORE,
                        BASE_SCORE,
                        "일간 강약 판정의 기준점수 50점에서 시작함"
                )
        );

        SeasonalResult seasonalResult =
                analyzeSeasonalCommand(
                        dayMasterElement,

                        fourPillars
                                .monthPillar()
                                .branch()
                                .getElement()
                );

        score += seasonalResult.scoreChange();

        evidence.add(
                seasonalResult.evidence()
        );

        RootResult rootResult =
                analyzeRoots(
                        dayMasterElement,
                        hiddenStems
                );

        score += rootResult.scoreChange();

        evidence.addAll(
                rootResult.evidence()
        );

        VisibleStemResult visibleStemResult =
                analyzeVisibleStems(
                        dayMasterElement,
                        fourPillars
                );

        score += visibleStemResult.scoreChange();

        evidence.addAll(
                visibleStemResult.evidence()
        );

        ElementBalanceResult balanceResult =
                analyzeElementBalance(
                        dayMasterElement,
                        elementStrength
                );

        score += balanceResult.scoreChange();

        evidence.add(
                balanceResult.evidence()
        );

        score += relationImpact.scoreChange();

        evidence.addAll(
                relationImpact.evidence()
        );

        double normalizedScore =
                clampAndRound(score);

        StrengthLevel level =
                StrengthLevel.fromScore(
                        normalizedScore
                );

        return new StrengthAnalysis(
                dayMaster,

                dayMasterElement,

                level,

                normalizedScore,

                balanceResult.supportPercentage(),

                balanceResult.pressurePercentage(),

                seasonalResult.gainsSeason(),

                rootResult.rootCount() > 0,

                rootResult.rootCount(),

                relationImpact.scoreChange(),

                evidence,

                RULE_VERSION
        );
    }

    private SeasonalResult analyzeSeasonalCommand(
            FiveElement dayMasterElement,
            FiveElement monthElement
    ) {
        if (monthElement == dayMasterElement) {
            return seasonalResult(
                    true,
                    15.0,
                    "SEASON_SAME_ELEMENT",
                    "월지의 오행이 일간과 같아 월령의 직접적인 도움을 받음"
            );
        }

        if (monthElement.generates(dayMasterElement)) {
            return seasonalResult(
                    true,
                    12.0,
                    "SEASON_RESOURCE_SUPPORT",
                    "월지의 오행이 일간을 생해 계절의 생조를 받음"
            );
        }

        if (dayMasterElement.generates(monthElement)) {
            return seasonalResult(
                    false,
                    -8.0,
                    "SEASON_OUTPUT_DRAIN",
                    "일간이 월지의 오행을 생해 계절적으로 기운이 설기됨"
            );
        }

        if (dayMasterElement.controls(monthElement)) {
            return seasonalResult(
                    false,
                    -10.0,
                    "SEASON_WEALTH_CONSUMPTION",
                    "일간이 월지의 오행을 극해야 하므로 계절적으로 힘이 소모됨"
            );
        }

        if (monthElement.controls(dayMasterElement)) {
            return seasonalResult(
                    false,
                    -15.0,
                    "SEASON_OFFICER_CONTROL",
                    "월지의 오행이 일간을 극해 계절적인 압박을 받음"
            );
        }

        throw new IllegalStateException(
                "월령과 일간의 오행 관계를 판단할 수 없습니다."
        );
    }

    private RootResult analyzeRoots(
            FiveElement dayMasterElement,
            List<HiddenStem> hiddenStems
    ) {
        double scoreChange = 0.0;

        int rootCount = 0;

        List<StrengthEvidence> evidence =
                new ArrayList<>();

        for (HiddenStem hiddenStem : hiddenStems) {
            if (hiddenStem
                    .stem()
                    .getElement()
                    != dayMasterElement) {
                continue;
            }

            double rootScore =
                    rootScore(
                            hiddenStem.role()
                    );

            rootCount++;

            scoreChange += rootScore;

            evidence.add(
                    new StrengthEvidence(
                            createRootCode(hiddenStem),

                            StrengthFactorType.ROOT_SUPPORT,

                            rootScore,

                            hiddenStem
                                    .branchPosition()
                                    .getKoreanName()
                                    + "의 "
                                    + hiddenStem
                                    .role()
                                    .getKoreanName()
                                    + "에 일간과 같은 "
                                    + dayMasterElement
                                    .getKoreanName()
                                    + " 기운이 있어 통근함"
                    )
            );
        }

        if (rootCount == 0) {
            evidence.add(
                    new StrengthEvidence(
                            "NO_DAY_MASTER_ROOT",

                            StrengthFactorType.ROOT_SUPPORT,

                            0.0,

                            "네 지지의 지장간에서 일간과 같은 오행의 뿌리가 확인되지 않음"
                    )
            );
        }

        return new RootResult(
                scoreChange,
                rootCount,
                evidence
        );
    }

    private VisibleStemResult analyzeVisibleStems(
            FiveElement dayMasterElement,
            FourPillars fourPillars
    ) {
        List<VisibleStemPosition> positions =
                List.of(
                        new VisibleStemPosition(
                                PillarPosition.YEAR_STEM,
                                fourPillars.yearPillar()
                        ),

                        new VisibleStemPosition(
                                PillarPosition.MONTH_STEM,
                                fourPillars.monthPillar()
                        ),

                        new VisibleStemPosition(
                                PillarPosition.HOUR_STEM,
                                fourPillars.hourPillar()
                        )
                );

        double scoreChange = 0.0;

        List<StrengthEvidence> evidence =
                new ArrayList<>();

        for (VisibleStemPosition position : positions) {
            FiveElement targetElement =
                    position
                            .pillar()
                            .stem()
                            .getElement();

            VisibleInfluence influence =
                    determineVisibleInfluence(
                            dayMasterElement,
                            targetElement
                    );

            scoreChange +=
                    influence.scoreChange();

            evidence.add(
                    new StrengthEvidence(
                            position.position().name()
                                    + "_"
                                    + influence.code(),

                            influence.factorType(),

                            influence.scoreChange(),

                            position
                                    .position()
                                    .getKoreanName()
                                    + " "
                                    + position
                                    .pillar()
                                    .stem()
                                    .getChineseCharacter()
                                    + "이(가) "
                                    + influence.description()
                    )
            );
        }

        return new VisibleStemResult(
                scoreChange,
                evidence
        );
    }

    private ElementBalanceResult analyzeElementBalance(
            FiveElement dayMasterElement,
            ElementStrength elementStrength
    ) {
        FiveElement resourceElement =
                findResourceElement(
                        dayMasterElement
                );

        double selfPercentage =
                elementStrength.percentageOf(
                        dayMasterElement
                );

        double resourcePercentage =
                elementStrength.percentageOf(
                        resourceElement
                );

        double supportPercentage =
                round(
                        selfPercentage
                                + resourcePercentage
                );

        double pressurePercentage =
                round(
                        100.0
                                - supportPercentage
                );

        double scoreChange;

        String code;

        String description;

        if (supportPercentage < 20.0) {
            scoreChange = -10.0;
            code = "SUPPORT_SHARE_VERY_LOW";

            description =
                    "비겁과 인성에 해당하는 생조 세력이 "
                            + supportPercentage
                            + "%로 매우 낮음";

        } else if (supportPercentage < 30.0) {
            scoreChange = -7.0;
            code = "SUPPORT_SHARE_LOW";

            description =
                    "비겁과 인성에 해당하는 생조 세력이 "
                            + supportPercentage
                            + "%로 낮음";

        } else if (supportPercentage < 40.0) {
            scoreChange = -4.0;
            code = "SUPPORT_SHARE_SLIGHTLY_LOW";

            description =
                    "비겁과 인성에 해당하는 생조 세력이 "
                            + supportPercentage
                            + "%로 다소 부족함";

        } else if (supportPercentage <= 60.0) {
            scoreChange = 0.0;
            code = "SUPPORT_SHARE_BALANCED";

            description =
                    "비겁과 인성에 해당하는 생조 세력이 "
                            + supportPercentage
                            + "%로 중화 범위에 있음";

        } else if (supportPercentage <= 75.0) {
            scoreChange = 6.0;
            code = "SUPPORT_SHARE_HIGH";

            description =
                    "비겁과 인성에 해당하는 생조 세력이 "
                            + supportPercentage
                            + "%로 강함";

        } else {
            scoreChange = 10.0;
            code = "SUPPORT_SHARE_VERY_HIGH";

            description =
                    "비겁과 인성에 해당하는 생조 세력이 "
                            + supportPercentage
                            + "%로 매우 강함";
        }

        StrengthEvidence evidence =
                new StrengthEvidence(
                        code,

                        StrengthFactorType.ELEMENT_BALANCE,

                        scoreChange,

                        description
                );

        return new ElementBalanceResult(
                scoreChange,

                supportPercentage,

                pressurePercentage,

                evidence
        );
    }

    private VisibleInfluence determineVisibleInfluence(
            FiveElement dayMasterElement,
            FiveElement targetElement
    ) {
        if (targetElement == dayMasterElement) {
            return new VisibleInfluence(
                    "PEER_SUPPORT",

                    StrengthFactorType
                            .VISIBLE_PEER_SUPPORT,

                    5.0,

                    "일간과 같은 오행으로 비겁의 도움을 줌"
            );
        }

        if (targetElement.generates(dayMasterElement)) {
            return new VisibleInfluence(
                    "RESOURCE_SUPPORT",

                    StrengthFactorType
                            .VISIBLE_RESOURCE_SUPPORT,

                    4.0,

                    "일간을 생하는 인성으로 작용함"
            );
        }

        if (dayMasterElement.generates(targetElement)) {
            return new VisibleInfluence(
                    "OUTPUT_DRAIN",

                    StrengthFactorType.OUTPUT_DRAIN,

                    -3.0,

                    "일간이 생해야 하는 식상으로 기운을 설기함"
            );
        }

        if (dayMasterElement.controls(targetElement)) {
            return new VisibleInfluence(
                    "WEALTH_CONSUMPTION",

                    StrengthFactorType
                            .WEALTH_CONSUMPTION,

                    -4.0,

                    "일간이 극해야 하는 재성으로 힘을 소모함"
            );
        }

        if (targetElement.controls(dayMasterElement)) {
            return new VisibleInfluence(
                    "OFFICER_CONTROL",

                    StrengthFactorType.OFFICER_CONTROL,

                    -5.0,

                    "일간을 극하는 관성으로 압박함"
            );
        }

        throw new IllegalStateException(
                "천간 오행의 영향 관계를 계산할 수 없습니다."
        );
    }

    private FiveElement findResourceElement(
            FiveElement dayMasterElement
    ) {
        for (FiveElement candidate
                : FiveElement.values()) {

            if (candidate.generates(
                    dayMasterElement
            )) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "일간을 생하는 인성 오행을 찾을 수 없습니다."
        );
    }

    private SeasonalResult seasonalResult(
            boolean gainsSeason,
            double scoreChange,
            String code,
            String description
    ) {
        return new SeasonalResult(
                gainsSeason,

                scoreChange,

                new StrengthEvidence(
                        code,

                        StrengthFactorType
                                .SEASONAL_COMMAND,

                        scoreChange,

                        description
                )
        );
    }

    private double rootScore(
            HiddenStemRole role
    ) {
        return switch (role) {
            case MAIN -> 8.0;
            case MIDDLE -> 5.0;
            case RESIDUAL -> 3.0;
        };
    }

    private String createRootCode(
            HiddenStem hiddenStem
    ) {
        return hiddenStem
                .branchPosition()
                .name()
                + "_ROOT_"
                + hiddenStem.role().name();
    }

    private double clampAndRound(
            double value
    ) {
        double clamped =
                Math.max(
                        0.0,
                        Math.min(
                                100.0,
                                value
                        )
                );

        return round(clamped);
    }

    private double round(
            double value
    ) {
        return Math.round(
                value * 100.0
        ) / 100.0;
    }

    private record SeasonalResult(
            boolean gainsSeason,
            double scoreChange,
            StrengthEvidence evidence
    ) {
    }

    private record RootResult(
            double scoreChange,
            int rootCount,
            List<StrengthEvidence> evidence
    ) {
    }

    private record VisibleStemResult(
            double scoreChange,
            List<StrengthEvidence> evidence
    ) {
    }

    private record ElementBalanceResult(
            double scoreChange,
            double supportPercentage,
            double pressurePercentage,
            StrengthEvidence evidence
    ) {
    }

    private record VisibleStemPosition(
            PillarPosition position,
            Pillar pillar
    ) {
    }

    private record VisibleInfluence(
            String code,
            StrengthFactorType factorType,
            double scoreChange,
            String description
    ) {
    }
}