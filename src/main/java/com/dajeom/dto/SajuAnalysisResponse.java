package com.dajeom.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SajuAnalysisResponse(

        Long analysisId,

        Long profileId,

        String status,

        String name,

        CalendarResponse calendar,

        String zodiac,

        String dayMaster,

        String summary,

        List<PillarResponse> pillars,

        ElementBalanceResponse elementBalance,

        YinYangBalanceResponse yinYangBalance,

        TenGodBalanceResponse tenGodBalance,

        StructureResponse structure,

        StrengthResponse strength,

        RelationResponse relations,

        List<ShinsalResponse> shinsal,

        LuckResponse luck,

        List<InterpretationResponse> interpretations,

        String disclaimer,

        LocalDateTime completedAt
) {

    public record CalendarResponse(

            String inputCalendarType,

            String solarDate,

            String lunarDate,

            boolean lunarLeapMonth,

            String birthTime,

            boolean birthTimeUnknown,

            String timeAccuracy,

            String calculationStandard
    ) {
    }

    public record HiddenStemResponse(

            String stem,

            String stemKorean,

            String element,

            String yinYang,

            String tenGod
    ) {
    }

    public record PillarResponse(

            String type,

            boolean known,

            String ganZhi,

            String ganZhiKorean,

            String stem,

            String stemKorean,

            String branch,

            String branchKorean,

            String stemElement,

            String branchElement,

            String stemYinYang,

            String branchYinYang,

            String stemTenGod,

            List<HiddenStemResponse> hiddenStems,

            String twelveStage,

            String naYin,

            String xun,

            String xunKong,

            String note
    ) {
    }

    public record BalanceItemResponse(

            String name,

            int count,

            double percentage
    ) {
    }

    public record ElementBalanceResponse(

            List<BalanceItemResponse> items,

            String dominant,

            String weakest,

            int totalCount
    ) {
    }

    public record YinYangBalanceResponse(

            int yangCount,

            int yinCount,

            double yangPercentage,

            double yinPercentage,

            String balance
    ) {
    }

    public record TenGodBalanceResponse(

            List<BalanceItemResponse> items,

            String dominant
    ) {
    }

    public record StructureResponse(

            String fetalOrigin,

            String fetalOriginKorean,

            String fetalOriginNaYin,

            String fetalBreath,

            String fetalBreathKorean,

            String fetalBreathNaYin,

            String lifePalace,

            String lifePalaceKorean,

            String lifePalaceNaYin,

            String bodyPalace,

            String bodyPalaceKorean,

            String bodyPalaceNaYin,

            String note
    ) {
    }

    public record StrengthResponse(

            int score,

            String classification,

            String dayMasterElement,

            List<String> supportingElements,

            List<String> consumingElements,

            String usefulGod,

            String favorableGod,

            String unfavorableGod,

            String note
    ) {
    }

    public record RelationItemResponse(

            String category,

            String members,

            String description
    ) {
    }

    public record RelationResponse(

            List<RelationItemResponse> heavenlyStemRelations,

            List<RelationItemResponse> earthlyBranchRelations
    ) {
    }

    public record ShinsalResponse(

            String name,

            String location,

            String description
    ) {
    }

    public record YearFortuneResponse(

            int year,

            int age,

            String ganZhi,

            String ganZhiKorean,

            String element,

            String tenGod,

            String xun,

            String xunKong
    ) {
    }

    public record DecadeFortuneResponse(

            int startAge,

            int endAge,

            int startYear,

            int endYear,

            String ganZhi,

            String ganZhiKorean,

            String element,

            String tenGod,

            String xun,

            String xunKong,

            boolean current,

            List<YearFortuneResponse> yearlyFortunes
    ) {
    }

    public record LuckResponse(

            boolean forward,

            String direction,

            String startAge,

            String startDate,

            boolean approximate,

            String currentDecade,

            List<DecadeFortuneResponse> decadeFortunes,

            String note
    ) {
    }

    public record InterpretationResponse(

            String category,

            String title,

            String content
    ) {
    }
}