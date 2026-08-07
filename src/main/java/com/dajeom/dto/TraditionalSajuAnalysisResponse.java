package com.dajeom.dto;

import com.dajeom.model.FourPillars;
import com.dajeom.model.InterpretationSignalAnalysis;
import com.dajeom.model.LuckAnalysis;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.SajuReport;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 정통 종합사주 계산부터
 * 최종 13개 분야 보고서까지 포함한 Response다.
 *
 * 기존 웹툰형 사주의 SajuAnalysisResponse와
 * 완전히 분리해서 사용한다.
 */
public record TraditionalSajuAnalysisResponse(

        String calendarType,

        LocalDate birthDate,

        LocalTime birthTime,

        String gender,

        int targetYear,

        PillarResponse yearPillar,

        PillarResponse monthPillar,

        PillarResponse dayPillar,

        PillarResponse hourPillar,

        String dayMaster,

        String dayMasterKorean,

        String dayMasterElement,

        String dayMasterElementKorean,

        String dayMasterYinYang,

        String dayMasterYinYangKorean,

        String fullSajuChinese,

        String fullSajuKorean,

        List<HiddenStemResponse> hiddenStems,

        List<TenGodResponse> tenGods,

        ElementDistributionResponse
        elementDistribution,

        ElementStrengthResponse
        elementStrength,

        RelationAnalysisResponse
        relationAnalysis,

        StrengthAnalysisResponse
        strengthAnalysis,

        StructureAnalysisResponse
        structureAnalysis,

        UsefulGodAnalysisResponse
        usefulGodAnalysis,

        LuckAnalysisResponse
        luckAnalysis,

        LuckInteractionAnalysisResponse
        luckInteractionAnalysis,

        InterpretationSignalAnalysisResponse
        interpretationSignalAnalysis,

        SajuReportResponse
        sajuReport
) {

    public TraditionalSajuAnalysisResponse {
        hiddenStems =
                hiddenStems == null
                        ? List.of()
                        : List.copyOf(
                        hiddenStems
                );

        tenGods =
                tenGods == null
                        ? List.of()
                        : List.copyOf(
                        tenGods
                );
    }

    public static TraditionalSajuAnalysisResponse of(
            NormalizedBirthData birthData,
            FourPillars fourPillars,
            NatalChartAnalysis natalChartAnalysis,
            LuckAnalysis luckAnalysis,
            LuckInteractionAnalysis
                    luckInteractionAnalysis,
            InterpretationSignalAnalysis
                    interpretationSignalAnalysis,
            SajuReport sajuReport
    ) {
        return new TraditionalSajuAnalysisResponse(

                birthData
                        .calendarType()
                        .name(),

                birthData
                        .birthDate(),

                birthData
                        .birthTime(),

                birthData
                        .gender()
                        .name(),

                luckAnalysis
                        .annualLuck()
                        .targetYear(),

                PillarResponse.from(
                        fourPillars
                                .yearPillar()
                ),

                PillarResponse.from(
                        fourPillars
                                .monthPillar()
                ),

                PillarResponse.from(
                        fourPillars
                                .dayPillar()
                ),

                PillarResponse.from(
                        fourPillars
                                .hourPillar()
                ),

                fourPillars
                        .dayPillar()
                        .stem()
                        .getChineseCharacter(),

                fourPillars
                        .dayPillar()
                        .stem()
                        .getKoreanName(),

                fourPillars
                        .dayPillar()
                        .stem()
                        .getElement()
                        .name(),

                fourPillars
                        .dayPillar()
                        .stem()
                        .getElement()
                        .getKoreanName(),

                fourPillars
                        .dayPillar()
                        .stem()
                        .getYinYang()
                        .name(),

                fourPillars
                        .dayPillar()
                        .stem()
                        .getYinYang()
                        .getKoreanName(),

                createFullChineseName(
                        fourPillars
                ),

                createFullKoreanName(
                        fourPillars
                ),

                natalChartAnalysis
                        .hiddenStems()
                        .stream()
                        .map(
                                HiddenStemResponse::from
                        )
                        .toList(),

                natalChartAnalysis
                        .tenGodAssignments()
                        .stream()
                        .map(
                                TenGodResponse::from
                        )
                        .toList(),

                ElementDistributionResponse.from(
                        natalChartAnalysis
                                .elementDistribution()
                ),

                ElementStrengthResponse.from(
                        natalChartAnalysis
                                .elementStrength()
                ),

                RelationAnalysisResponse.from(
                        natalChartAnalysis
                                .relationAnalysis()
                ),

                StrengthAnalysisResponse.from(
                        natalChartAnalysis
                                .strengthAnalysis()
                ),

                StructureAnalysisResponse.from(
                        natalChartAnalysis
                                .structureAnalysis()
                ),

                UsefulGodAnalysisResponse.from(
                        natalChartAnalysis
                                .usefulGodAnalysis()
                ),

                LuckAnalysisResponse.from(
                        luckAnalysis
                ),

                LuckInteractionAnalysisResponse.from(
                        luckInteractionAnalysis
                ),

                InterpretationSignalAnalysisResponse.from(
                        interpretationSignalAnalysis
                ),

                SajuReportResponse.from(
                        sajuReport
                )
        );
    }

    private static String createFullChineseName(
            FourPillars fourPillars
    ) {
        return String.join(
                " ",

                fourPillars
                        .yearPillar()
                        .chineseName(),

                fourPillars
                        .monthPillar()
                        .chineseName(),

                fourPillars
                        .dayPillar()
                        .chineseName(),

                fourPillars
                        .hourPillar()
                        .chineseName()
        );
    }

    private static String createFullKoreanName(
            FourPillars fourPillars
    ) {
        return String.join(
                " ",

                fourPillars
                        .yearPillar()
                        .koreanName(),

                fourPillars
                        .monthPillar()
                        .koreanName(),

                fourPillars
                        .dayPillar()
                        .koreanName(),

                fourPillars
                        .hourPillar()
                        .koreanName()
        );
    }
}