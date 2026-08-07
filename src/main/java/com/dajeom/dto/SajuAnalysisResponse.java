package com.dajeom.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 기존 AI 사주 / 웹툰형 사주 분석 결과 Response다.
 *
 * 기존 SajuCalculatorService,
 * SajuEasyExplanationService,
 * SajuCinematicService에서 사용하는 응답 구조다.
 *
 * 정통 종합사주 V2 계산 결과는
 * TraditionalSajuAnalysisResponse를 사용한다.
 */
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

    public SajuAnalysisResponse {
        pillars =
                immutableList(
                        pillars
                );

        shinsal =
                immutableList(
                        shinsal
                );

        interpretations =
                immutableList(
                        interpretations
                );
    }

    /**
     * 오행·십성 등의 개수와 비율을 표현한다.
     */
    public record BalanceItemResponse(

            String name,

            int count,

            double percentage
    ) {
    }

    /**
     * 음양력 및 출생시간 계산 기준이다.
     */
    public record CalendarResponse(

            String calendarType,

            String solarDate,

            String lunarDate,

            boolean lunarLeapMonth,

            String birthTime,

            boolean birthTimeUnknown,

            String birthTimeBasis,

            String calculationBasis
    ) {
    }

    /**
     * 지지 내부의 지장간 하나를 표현한다.
     */
    public record HiddenStemResponse(

            String stem,

            String stemKorean,

            String element,

            String yinYang,

            String tenGod
    ) {
    }

    /**
     * 기존 웹툰 사주에서 사용하는 년주·월주·일주·시주 DTO다.
     */
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

        public PillarResponse {
            hiddenStems =
                    immutableList(
                            hiddenStems
                    );
        }
    }

    /**
     * 원국의 오행 분포다.
     */
    public record ElementBalanceResponse(

            List<BalanceItemResponse> items,

            String dominant,

            String weakest,

            int totalCount
    ) {

        public ElementBalanceResponse {
            items =
                    immutableList(
                            items
                    );
        }
    }

    /**
     * 원국의 음·양 분포다.
     */
    public record YinYangBalanceResponse(

            int yangCount,

            int yinCount,

            double yangPercentage,

            double yinPercentage,

            String balance
    ) {
    }

    /**
     * 원국의 십성 분포다.
     */
    public record TenGodBalanceResponse(

            List<BalanceItemResponse> items,

            String dominant
    ) {

        public TenGodBalanceResponse {
            items =
                    immutableList(
                            items
                    );
        }
    }

    /**
     * 기존 계산기의 신강·신약 보조 판정이다.
     */
    public record StrengthResponse(

            int score,

            String classification,

            String dayElement,

            List<String> supportingElements,

            List<String> drainingElements,

            String usefulGod,

            String favorableGod,

            String unfavorableGod,

            String note
    ) {

        public StrengthResponse {
            supportingElements =
                    immutableList(
                            supportingElements
                    );

            drainingElements =
                    immutableList(
                            drainingElements
                    );
        }
    }

    /**
     * 태원·태식·명궁·신궁 등의 보조 구조다.
     */
    public record StructureResponse(

            String taiYuan,

            String taiYuanKorean,

            String taiYuanNaYin,

            String taiXi,

            String taiXiKorean,

            String taiXiNaYin,

            String lifePalace,

            String lifePalaceKorean,

            String lifePalaceNaYin,

            String bodyPalace,

            String bodyPalaceKorean,

            String bodyPalaceNaYin,

            String note
    ) {
    }

    /**
     * 합·충·형·파·해 등의 관계 하나다.
     */
    public record RelationItemResponse(

            String category,

            String members,

            String description
    ) {
    }

    /**
     * 천간과 지지의 관계 분석 결과다.
     */
    public record RelationResponse(

            List<RelationItemResponse> heavenlyStemRelations,

            List<RelationItemResponse> earthlyBranchRelations
    ) {

        public RelationResponse {
            heavenlyStemRelations =
                    immutableList(
                            heavenlyStemRelations
                    );

            earthlyBranchRelations =
                    immutableList(
                            earthlyBranchRelations
                    );
        }
    }

    /**
     * 기존 웹툰 사주에서 사용하는 신살 정보다.
     */
    public record ShinsalResponse(

            String name,

            String location,

            String description
    ) {
    }

    /**
     * 한 해의 세운 정보다.
     */
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

    /**
     * 하나의 10년 대운 구간이다.
     */
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

        public DecadeFortuneResponse {
            yearlyFortunes =
                    immutableList(
                            yearlyFortunes
                    );
        }
    }

    /**
     * 기존 웹툰 사주의 대운 결과다.
     */
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

        public LuckResponse {
            decadeFortunes =
                    immutableList(
                            decadeFortunes
                    );
        }
    }

    /**
     * 소비자에게 보여주는 분야별 쉬운 해석이다.
     */
    public record InterpretationResponse(

            String category,

            String title,

            String content
    ) {
    }

    private static <T> List<T> immutableList(
            List<T> source
    ) {
        if (source == null) {
            return List.of();
        }

        return List.copyOf(
                source
        );
    }
}