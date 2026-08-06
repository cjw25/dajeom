package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuMonthForecastResponse;
import com.dajeom.dto.SajuYearForecastResponse;
import com.dajeom.entity.SajuAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SajuForecastService {

    private static final ZoneId KOREA_ZONE =
            ZoneId.of("Asia/Seoul");

    private static final int DEFAULT_FORECAST_YEARS = 10;

    private static final int MIN_FORECAST_YEAR = 1900;

    private static final int MAX_FORECAST_YEAR = 2100;

    private static final int CAREER = 0;

    private static final int WEALTH = 1;

    private static final int BUSINESS = 2;

    private static final int RELATIONSHIP = 3;

    private static final int ROMANCE = 4;

    private static final int CHANGE = 5;

    private static final int CAUTION = 6;

    private static final String[] STEMS = {
            "甲",
            "乙",
            "丙",
            "丁",
            "戊",
            "己",
            "庚",
            "辛",
            "壬",
            "癸"
    };

    private static final String[] STEMS_KOREAN = {
            "갑",
            "을",
            "병",
            "정",
            "무",
            "기",
            "경",
            "신",
            "임",
            "계"
    };

    private static final String[] BRANCHES = {
            "子",
            "丑",
            "寅",
            "卯",
            "辰",
            "巳",
            "午",
            "未",
            "申",
            "酉",
            "戌",
            "亥"
    };

    private static final String[] BRANCHES_KOREAN = {
            "자",
            "축",
            "인",
            "묘",
            "진",
            "사",
            "오",
            "미",
            "신",
            "유",
            "술",
            "해"
    };

    private static final String[] STEM_ELEMENTS = {
            "목",
            "목",
            "화",
            "화",
            "토",
            "토",
            "금",
            "금",
            "수",
            "수"
    };

    private static final String[] BRANCH_ELEMENTS = {
            "수",
            "토",
            "목",
            "목",
            "토",
            "화",
            "화",
            "토",
            "금",
            "금",
            "토",
            "수"
    };

    private final SajuCalculatorService sajuCalculatorService;

    /**
     * 현재 연도부터 앞으로 10년의 연도별 흐름을 반환한다.
     */
    public List<SajuYearForecastResponse> getYearForecasts(
            SajuAnalysis analysis,
            SajuAnalysisResponse analysisResponse
    ) {
        validateAnalysis(
                analysis
        );

        int currentYear =
                Year.now(
                        KOREA_ZONE
                ).getValue();

        int lastYear =
                Math.min(
                        currentYear
                                + DEFAULT_FORECAST_YEARS
                                - 1,
                        MAX_FORECAST_YEAR
                );

        SajuCalculatorService.ForecastBaseData baseData =
                sajuCalculatorService.calculateForecastBaseData(
                        analysis
                );

        List<SajuYearForecastResponse> forecasts =
                new ArrayList<>();

        for (
                int year = currentYear;
                year <= lastYear;
                year++
        ) {
            forecasts.add(
                    buildYearForecast(
                            analysis,
                            baseData,
                            year
                    )
            );
        }

        return List.copyOf(
                forecasts
        );
    }

    /**
     * 특정 연도의 연간 흐름과 12개월 월운을 반환한다.
     */
    public SajuYearForecastResponse getYearForecast(
            SajuAnalysis analysis,
            SajuAnalysisResponse analysisResponse,
            int year
    ) {
        validateAnalysis(
                analysis
        );

        validateYear(
                year
        );

        SajuCalculatorService.ForecastBaseData baseData =
                sajuCalculatorService.calculateForecastBaseData(
                        analysis
                );

        return buildYearForecast(
                analysis,
                baseData,
                year
        );
    }

    /**
     * 특정 연도와 월의 상세 월운을 반환한다.
     */
    public SajuMonthForecastResponse getMonthForecast(
            SajuAnalysis analysis,
            SajuAnalysisResponse analysisResponse,
            int year,
            int month
    ) {
        validateAnalysis(
                analysis
        );

        validateYear(
                year
        );

        validateMonth(
                month
        );

        SajuCalculatorService.ForecastBaseData baseData =
                sajuCalculatorService.calculateForecastBaseData(
                        analysis
                );

        SajuCalculatorService.ForecastLuckData daeun =
                resolveDaeunForYear(
                        baseData,
                        year
                );

        SajuCalculatorService.ForecastPillarData yearPillar =
                sajuCalculatorService.calculateYearForecastPillar(
                        analysis,
                        year
                );

        List<MonthDraft> drafts =
                buildMonthDrafts(
                        analysis,
                        baseData,
                        daeun,
                        yearPillar,
                        year
                );

        PeakRange peakRange =
                findPeakRange(
                        drafts
                );

        return drafts.stream()
                .filter(
                        draft ->
                                draft.month()
                                        == month
                )
                .findFirst()
                .map(
                        draft ->
                                draft.toResponse(
                                        determineMonthPhase(
                                                draft.month(),
                                                peakRange
                                        )
                                )
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "해당 월의 운세를 생성하지 못했습니다."
                                )
                );
    }

    /**
     * 특정 연도의 종합 흐름을 생성한다.
     */
    private SajuYearForecastResponse buildYearForecast(
            SajuAnalysis analysis,
            SajuCalculatorService.ForecastBaseData baseData,
            int year
    ) {
        validateYear(
                year
        );

        SajuCalculatorService.ForecastLuckData daeun =
                resolveDaeunForYear(
                        baseData,
                        year
                );

        SajuCalculatorService.ForecastPillarData yearPillar =
                sajuCalculatorService.calculateYearForecastPillar(
                        analysis,
                        year
                );

        List<MonthDraft> monthDrafts =
                buildMonthDrafts(
                        analysis,
                        baseData,
                        daeun,
                        yearPillar,
                        year
                );

        PeakRange peakRange =
                findPeakRange(
                        monthDrafts
                );

        List<SajuMonthForecastResponse> monthResponses =
                monthDrafts.stream()
                        .map(
                                draft ->
                                        draft.toResponse(
                                                determineMonthPhase(
                                                        draft.month(),
                                                        peakRange
                                                )
                                        )
                        )
                        .toList();

        ScoreResult yearScoreResult =
                evaluateForecastEnergy(
                        baseData,
                        daeun,
                        yearPillar,
                        null
                );

        SajuMonthForecastResponse.ForecastScores aggregatedScores =
                aggregateScores(
                        monthDrafts,
                        yearScoreResult.scores()
                );

        String theme =
                selectTheme(
                        aggregatedScores
                );

        List<SajuMonthForecastResponse.ForecastEvidence> evidences =
                mergeEvidences(
                        yearScoreResult.evidences(),
                        collectImportantMonthEvidences(
                                monthDrafts,
                                peakRange
                        )
                );

        int confidenceScore =
                calculateConfidenceScore(
                        baseData,
                        daeun,
                        evidences
                );

        SajuMonthForecastResponse.ConfidenceLevel confidence =
                determineConfidence(
                        confidenceScore,
                        evidences
                );

        YearMonth influenceStart =
                YearMonth.of(
                        year,
                        peakRange.influenceStartMonth()
                );

        YearMonth peakStart =
                YearMonth.of(
                        year,
                        peakRange.peakStartMonth()
                );

        YearMonth peakEnd =
                YearMonth.of(
                        year,
                        peakRange.peakEndMonth()
                );

        YearMonth influenceEnd =
                YearMonth.of(
                        year,
                        peakRange.influenceEndMonth()
                );

        SajuYearForecastResponse.InfluencePeriod influencePeriod =
                new SajuYearForecastResponse.InfluencePeriod(
                        influenceStart,
                        peakStart,
                        peakEnd,
                        influenceEnd,
                        theme,
                        createInfluencePeriodDescription(
                                influenceStart,
                                peakStart,
                                peakEnd,
                                influenceEnd,
                                theme
                        )
                );

        return new SajuYearForecastResponse(
                year,
                yearPillar.ganji(),
                year
                        + "년 "
                        + theme
                        + " 흐름",
                createYearHeadline(
                        year,
                        theme
                ),
                theme,
                createYearSummary(
                        year,
                        theme,
                        yearPillar.tenGod(),
                        daeun,
                        influencePeriod
                ),
                createPossibleEffects(
                        theme
                ),
                createOpportunities(
                        theme
                ),
                createCautions(
                        theme,
                        aggregatedScores.caution()
                ),
                influencePeriod,
                monthResponses,
                aggregatedScores,
                confidence,
                confidenceScore,
                evidences.size(),
                evidences,
                "이 해석의 신뢰 점수는 미래 사건의 발생 확률이 아니라, "
                        + "원국·대운·세운·월운에서 같은 작용이 얼마나 반복되는지를 나타냅니다."
        );
    }

    /**
     * 특정 연도의 12개월 월운 초안을 생성한다.
     */
    private List<MonthDraft> buildMonthDrafts(
            SajuAnalysis analysis,
            SajuCalculatorService.ForecastBaseData baseData,
            SajuCalculatorService.ForecastLuckData daeun,
            SajuCalculatorService.ForecastPillarData yearPillar,
            int year
    ) {
        List<MonthDraft> drafts =
                new ArrayList<>();

        for (
                int month = 1;
                month <= 12;
                month++
        ) {
            SajuCalculatorService.ForecastPillarData monthPillar =
                    sajuCalculatorService.calculateMonthForecastPillar(
                            analysis,
                            year,
                            month
                    );

            drafts.add(
                    buildMonthDraft(
                            baseData,
                            daeun,
                            yearPillar,
                            monthPillar
                    )
            );
        }

        return List.copyOf(
                drafts
        );
    }

    /**
     * 특정 월의 상세 운세 초안을 생성한다.
     */
    private MonthDraft buildMonthDraft(
            SajuCalculatorService.ForecastBaseData baseData,
            SajuCalculatorService.ForecastLuckData daeun,
            SajuCalculatorService.ForecastPillarData yearPillar,
            SajuCalculatorService.ForecastPillarData monthPillar
    ) {
        ScoreResult scoreResult =
                evaluateForecastEnergy(
                        baseData,
                        daeun,
                        yearPillar,
                        monthPillar
                );

        String theme =
                selectTheme(
                        scoreResult.scores()
                );

        int confidenceScore =
                calculateConfidenceScore(
                        baseData,
                        daeun,
                        scoreResult.evidences()
                );

        SajuMonthForecastResponse.ConfidenceLevel confidence =
                determineConfidence(
                        confidenceScore,
                        scoreResult.evidences()
                );

        int year =
                monthPillar.year();

        int month =
                Objects.requireNonNull(
                        monthPillar.month()
                );

        return new MonthDraft(
                year,
                month,
                monthPillar.ganji(),
                monthPillar.solarTermName(),
                monthPillar.periodStart(),
                monthPillar.periodEnd(),
                year
                        + "년 "
                        + month
                        + "월 "
                        + theme,
                createMonthHeadline(
                        theme
                ),
                createMonthSummary(
                        year,
                        month,
                        theme,
                        monthPillar.tenGod(),
                        daeun,
                        yearPillar
                ),
                createPossibleEffects(
                        theme
                ),
                createOpportunities(
                        theme
                ),
                createCautions(
                        theme,
                        scoreResult.scores()
                                .caution()
                ),
                scoreResult.scores(),
                confidence,
                confidenceScore,
                scoreResult.evidences()
        );
    }

    /**
     * 원국·대운·세운·월운을 함께 비교해
     * 분야별 작용 점수를 생성한다.
     */
    private ScoreResult evaluateForecastEnergy(
            SajuCalculatorService.ForecastBaseData baseData,
            SajuCalculatorService.ForecastLuckData daeun,
            SajuCalculatorService.ForecastPillarData yearPillar,
            SajuCalculatorService.ForecastPillarData monthPillar
    ) {
        int[] scores = {
                42,
                42,
                42,
                42,
                42,
                42,
                30
        };

        List<SajuMonthForecastResponse.ForecastEvidence> evidences =
                new ArrayList<>();

        List<FlowPillar> flows =
                new ArrayList<>();

        if (
                daeun != null
        ) {
            flows.add(
                    toFlowPillar(
                            "대운",
                            daeun,
                            0.90
                    )
            );
        }

        if (
                yearPillar != null
        ) {
            flows.add(
                    toFlowPillar(
                            "세운",
                            yearPillar,
                            1.00
                    )
            );
        }

        if (
                monthPillar != null
        ) {
            flows.add(
                    toFlowPillar(
                            "월운",
                            monthPillar,
                            0.85
                    )
            );
        }

        for (
                FlowPillar flow : flows
        ) {
            applyFlowPillar(
                    baseData,
                    flow,
                    scores,
                    evidences
            );
        }

        applyCrossFlowRelations(
                flows,
                scores,
                evidences
        );

        applyRepeatedSignals(
                flows,
                scores,
                evidences
        );

        applyStrengthContext(
                baseData,
                flows,
                scores,
                evidences
        );

        normalizeScores(
                scores
        );

        String primaryTenGod =
                monthPillar != null
                        ? monthPillar.tenGod()
                        : yearPillar == null
                        ? null
                        : yearPillar.tenGod();

        return new ScoreResult(
                new SajuMonthForecastResponse.ForecastScores(
                        scores[CAREER],
                        scores[WEALTH],
                        scores[BUSINESS],
                        scores[RELATIONSHIP],
                        scores[ROMANCE],
                        scores[CHANGE],
                        scores[CAUTION]
                ),
                primaryTenGod,
                distinctEvidences(
                        evidences
                )
        );
    }

    /**
     * 대운·세운·월운 하나의 작용을 점수에 반영한다.
     */
    private void applyFlowPillar(
            SajuCalculatorService.ForecastBaseData baseData,
            FlowPillar flow,
            int[] scores,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        applyTenGodScores(
                flow.tenGod(),
                scores,
                flow.weight()
        );

        if (
                notBlank(
                        flow.tenGod()
                )
        ) {
            addEvidence(
                    evidences,
                    flow.source()
                            + "_TEN_GOD",
                    flow.source()
                            + "의 "
                            + flow.ganji()
                            + "가 일간과 만나 "
                            + flow.tenGod()
                            + " 작용을 만듭니다."
            );
        }

        applyElementEffect(
                baseData,
                flow.stemElement(),
                flow.source()
                        + " 천간",
                flow.weight(),
                scores,
                evidences
        );

        applyElementEffect(
                baseData,
                flow.branchElement(),
                flow.source()
                        + " 지지",
                flow.weight(),
                scores,
                evidences
        );

        applyNatalRelations(
                baseData.natalPillars(),
                flow,
                scores,
                evidences
        );
    }

    /**
     * 십성의 작용을 분야별 점수에 반영한다.
     */
    private void applyTenGodScores(
            String tenGod,
            int[] scores,
            double weight
    ) {
        if (
                !notBlank(
                        tenGod
                )
        ) {
            return;
        }

        switch (
                tenGod
        ) {
            case "비견" -> {
                addWeighted(
                        scores,
                        CAREER,
                        5,
                        weight
                );

                addWeighted(
                        scores,
                        BUSINESS,
                        8,
                        weight
                );

                addWeighted(
                        scores,
                        RELATIONSHIP,
                        7,
                        weight
                );

                addWeighted(
                        scores,
                        CHANGE,
                        5,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        5,
                        weight
                );
            }

            case "겁재" -> {
                addWeighted(
                        scores,
                        BUSINESS,
                        9,
                        weight
                );

                addWeighted(
                        scores,
                        RELATIONSHIP,
                        9,
                        weight
                );

                addWeighted(
                        scores,
                        CHANGE,
                        7,
                        weight
                );

                addWeighted(
                        scores,
                        WEALTH,
                        -4,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        13,
                        weight
                );
            }

            case "식신" -> {
                addWeighted(
                        scores,
                        CAREER,
                        8,
                        weight
                );

                addWeighted(
                        scores,
                        WEALTH,
                        7,
                        weight
                );

                addWeighted(
                        scores,
                        BUSINESS,
                        8,
                        weight
                );

                addWeighted(
                        scores,
                        ROMANCE,
                        5,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        -2,
                        weight
                );
            }

            case "상관" -> {
                addWeighted(
                        scores,
                        CAREER,
                        8,
                        weight
                );

                addWeighted(
                        scores,
                        BUSINESS,
                        11,
                        weight
                );

                addWeighted(
                        scores,
                        CHANGE,
                        9,
                        weight
                );

                addWeighted(
                        scores,
                        RELATIONSHIP,
                        5,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        11,
                        weight
                );
            }

            case "편재" -> {
                addWeighted(
                        scores,
                        WEALTH,
                        15,
                        weight
                );

                addWeighted(
                        scores,
                        BUSINESS,
                        11,
                        weight
                );

                addWeighted(
                        scores,
                        RELATIONSHIP,
                        6,
                        weight
                );

                addWeighted(
                        scores,
                        CHANGE,
                        5,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        5,
                        weight
                );
            }

            case "정재" -> {
                addWeighted(
                        scores,
                        WEALTH,
                        13,
                        weight
                );

                addWeighted(
                        scores,
                        CAREER,
                        6,
                        weight
                );

                addWeighted(
                        scores,
                        BUSINESS,
                        5,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        -2,
                        weight
                );
            }

            case "편관" -> {
                addWeighted(
                        scores,
                        CAREER,
                        11,
                        weight
                );

                addWeighted(
                        scores,
                        CHANGE,
                        8,
                        weight
                );

                addWeighted(
                        scores,
                        RELATIONSHIP,
                        4,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        13,
                        weight
                );
            }

            case "정관" -> {
                addWeighted(
                        scores,
                        CAREER,
                        13,
                        weight
                );

                addWeighted(
                        scores,
                        RELATIONSHIP,
                        6,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        4,
                        weight
                );
            }

            case "편인" -> {
                addWeighted(
                        scores,
                        CAREER,
                        6,
                        weight
                );

                addWeighted(
                        scores,
                        CHANGE,
                        6,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        5,
                        weight
                );
            }

            case "정인" -> {
                addWeighted(
                        scores,
                        CAREER,
                        8,
                        weight
                );

                addWeighted(
                        scores,
                        RELATIONSHIP,
                        4,
                        weight
                );

                addWeighted(
                        scores,
                        CAUTION,
                        -3,
                        weight
                );
            }

            default -> {
            }
        }
    }

    /**
     * 용신·희신·기신 오행을 점수에 반영한다.
     */
    private void applyElementEffect(
            SajuCalculatorService.ForecastBaseData baseData,
            String element,
            String positionName,
            double weight,
            int[] scores,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        if (
                !notBlank(
                        element
                )
        ) {
            return;
        }

        if (
                Objects.equals(
                        element,
                        baseData.usefulElement()
                )
        ) {
            addWeighted(
                    scores,
                    CAREER,
                    8,
                    weight
            );

            addWeighted(
                    scores,
                    WEALTH,
                    8,
                    weight
            );

            addWeighted(
                    scores,
                    BUSINESS,
                    7,
                    weight
            );

            addWeighted(
                    scores,
                    RELATIONSHIP,
                    4,
                    weight
            );

            addWeighted(
                    scores,
                    CAUTION,
                    -5,
                    weight
            );

            addEvidence(
                    evidences,
                    "USEFUL_ELEMENT",
                    positionName
                            + "에서 용신으로 분류된 "
                            + element
                            + " 기운이 들어옵니다."
            );

        } else if (
                Objects.equals(
                        element,
                        baseData.favorableElement()
                )
        ) {
            addWeighted(
                    scores,
                    CAREER,
                    5,
                    weight
            );

            addWeighted(
                    scores,
                    WEALTH,
                    5,
                    weight
            );

            addWeighted(
                    scores,
                    BUSINESS,
                    5,
                    weight
            );

            addWeighted(
                    scores,
                    CHANGE,
                    3,
                    weight
            );

            addWeighted(
                    scores,
                    CAUTION,
                    -3,
                    weight
            );

            addEvidence(
                    evidences,
                    "FAVORABLE_ELEMENT",
                    positionName
                            + "에서 희신으로 분류된 "
                            + element
                            + " 기운이 보완 작용을 합니다."
            );
        }

        if (
                Objects.equals(
                        element,
                        baseData.unfavorableElement()
                )
        ) {
            addWeighted(
                    scores,
                    CHANGE,
                    6,
                    weight
            );

            addWeighted(
                    scores,
                    CAUTION,
                    11,
                    weight
            );

            addEvidence(
                    evidences,
                    "UNFAVORABLE_ELEMENT",
                    positionName
                            + "에서 기신으로 분류된 "
                            + element
                            + " 기운이 반복됩니다."
            );
        }
    }

    /**
     * 원국의 네 기둥과 대운·세운·월운의 관계를 계산한다.
     */
    private void applyNatalRelations(
            List<SajuCalculatorService.ForecastNatalPillarData> natalPillars,
            FlowPillar flow,
            int[] scores,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        if (
                natalPillars == null
                        || natalPillars.isEmpty()
        ) {
            return;
        }

        for (
                SajuCalculatorService.ForecastNatalPillarData natal
                : natalPillars
        ) {
            double weight =
                    flow.weight()
                            * natalWeight(
                            natal.type()
                    );

            String relationTarget =
                    natal.type()
                            + " "
                            + natal.ganji();

            applyStemRelation(
                    natal.stem(),
                    flow.stem(),
                    relationTarget,
                    flow,
                    weight,
                    scores,
                    evidences
            );

            applyBranchRelation(
                    natal.branch(),
                    flow.branch(),
                    relationTarget,
                    flow,
                    weight,
                    scores,
                    evidences
            );
        }
    }

    /**
     * 원국 천간과 운의 천간 관계를 계산한다.
     */
    private void applyStemRelation(
            String natalStem,
            String flowStem,
            String relationTarget,
            FlowPillar flow,
            double weight,
            int[] scores,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        if (
                !notBlank(
                        natalStem
                )
                        || !notBlank(
                        flowStem
                )
        ) {
            return;
        }

        if (
                isStemCombination(
                        natalStem,
                        flowStem
                )
        ) {
            addWeighted(
                    scores,
                    RELATIONSHIP,
                    7,
                    weight
            );

            addWeighted(
                    scores,
                    CAREER,
                    5,
                    weight
            );

            addWeighted(
                    scores,
                    WEALTH,
                    3,
                    weight
            );

            addEvidence(
                    evidences,
                    "STEM_COMBINATION",
                    relationTarget
                            + "의 천간과 "
                            + flow.source()
                            + " 천간이 합을 이루어 연결과 협력 작용이 생깁니다."
            );
        }

        if (
                isStemClash(
                        natalStem,
                        flowStem
                )
        ) {
            addWeighted(
                    scores,
                    CHANGE,
                    8,
                    weight
            );

            addWeighted(
                    scores,
                    CAUTION,
                    9,
                    weight
            );

            addWeighted(
                    scores,
                    CAREER,
                    4,
                    weight
            );

            addEvidence(
                    evidences,
                    "STEM_CLASH",
                    relationTarget
                            + "의 천간과 "
                            + flow.source()
                            + " 천간이 충을 이루어 판단과 방향이 부딪힐 수 있습니다."
            );
        }
    }

    /**
     * 원국 지지와 운의 지지 관계를 계산한다.
     */
    private void applyBranchRelation(
            String natalBranch,
            String flowBranch,
            String relationTarget,
            FlowPillar flow,
            double weight,
            int[] scores,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        if (
                !notBlank(
                        natalBranch
                )
                        || !notBlank(
                        flowBranch
                )
        ) {
            return;
        }

        if (
                isClash(
                        natalBranch,
                        flowBranch
                )
        ) {
            addWeighted(
                    scores,
                    CHANGE,
                    19,
                    weight
            );

            addWeighted(
                    scores,
                    CAUTION,
                    16,
                    weight
            );

            addWeighted(
                    scores,
                    RELATIONSHIP,
                    7,
                    weight
            );

            addEvidence(
                    evidences,
                    "BRANCH_CLASH",
                    relationTarget
                            + "의 지지와 "
                            + flow.source()
                            + " 지지가 충을 이루어 이동·변화·의견 충돌 작용이 커질 수 있습니다."
            );
        }

        if (
                isHarmony(
                        natalBranch,
                        flowBranch
                )
        ) {
            addWeighted(
                    scores,
                    RELATIONSHIP,
                    11,
                    weight
            );

            addWeighted(
                    scores,
                    CAREER,
                    5,
                    weight
            );

            addWeighted(
                    scores,
                    WEALTH,
                    4,
                    weight
            );

            addWeighted(
                    scores,
                    CAUTION,
                    -3,
                    weight
            );

            addEvidence(
                    evidences,
                    "BRANCH_HARMONY",
                    relationTarget
                            + "의 지지와 "
                            + flow.source()
                            + " 지지가 합을 이루어 협력과 연결 작용이 생깁니다."
            );
        }

        if (
                isHarm(
                        natalBranch,
                        flowBranch
                )
        ) {
            addWeighted(
                    scores,
                    RELATIONSHIP,
                    12,
                    weight
            );

            addWeighted(
                    scores,
                    CAUTION,
                    10,
                    weight
            );

            addEvidence(
                    evidences,
                    "BRANCH_HARM",
                    relationTarget
                            + "의 지지와 "
                            + flow.source()
                            + " 지지에서 해 작용이 나타나 오해나 숨은 불편함을 조심할 필요가 있습니다."
            );
        }

        if (
                isBreak(
                        natalBranch,
                        flowBranch
                )
        ) {
            addWeighted(
                    scores,
                    CHANGE,
                    8,
                    weight
            );

            addWeighted(
                    scores,
                    CAUTION,
                    7,
                    weight
            );

            addEvidence(
                    evidences,
                    "BRANCH_BREAK",
                    relationTarget
                            + "의 지지와 "
                            + flow.source()
                            + " 지지에서 파 작용이 나타나 기존 계획이 수정될 수 있습니다."
            );
        }

        if (
                isPunishment(
                        natalBranch,
                        flowBranch
                )
        ) {
            addWeighted(
                    scores,
                    CAUTION,
                    12,
                    weight
            );

            addWeighted(
                    scores,
                    RELATIONSHIP,
                    6,
                    weight
            );

            addEvidence(
                    evidences,
                    "BRANCH_PUNISHMENT",
                    relationTarget
                            + "의 지지와 "
                            + flow.source()
                            + " 지지에서 형 작용이 나타나 압박감과 감정적인 반응을 줄이는 것이 좋습니다."
            );
        }
    }

    /**
     * 대운·세운·월운 상호 간의 관계를 계산한다.
     */
    private void applyCrossFlowRelations(
            List<FlowPillar> flows,
            int[] scores,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        for (
                int first = 0;
                first < flows.size();
                first++
        ) {
            for (
                    int second = first + 1;
                    second < flows.size();
                    second++
            ) {
                FlowPillar left =
                        flows.get(
                                first
                        );

                FlowPillar right =
                        flows.get(
                                second
                        );

                double weight =
                        Math.min(
                                left.weight(),
                                right.weight()
                        )
                                * 0.75;

                if (
                        isStemCombination(
                                left.stem(),
                                right.stem()
                        )
                ) {
                    addWeighted(
                            scores,
                            CAREER,
                            5,
                            weight
                    );

                    addWeighted(
                            scores,
                            RELATIONSHIP,
                            6,
                            weight
                    );

                    addEvidence(
                            evidences,
                            "FLOW_STEM_COMBINATION",
                            left.source()
                                    + "과 "
                                    + right.source()
                                    + "의 천간이 합을 이루어 같은 방향의 작용이 이어집니다."
                    );
                }

                if (
                        isStemClash(
                                left.stem(),
                                right.stem()
                        )
                ) {
                    addWeighted(
                            scores,
                            CHANGE,
                            7,
                            weight
                    );

                    addWeighted(
                            scores,
                            CAUTION,
                            8,
                            weight
                    );

                    addEvidence(
                            evidences,
                            "FLOW_STEM_CLASH",
                            left.source()
                                    + "과 "
                                    + right.source()
                                    + "의 천간이 충을 이루어 선택과 판단이 흔들릴 수 있습니다."
                    );
                }

                if (
                        isClash(
                                left.branch(),
                                right.branch()
                        )
                ) {
                    addWeighted(
                            scores,
                            CHANGE,
                            13,
                            weight
                    );

                    addWeighted(
                            scores,
                            CAUTION,
                            12,
                            weight
                    );

                    addWeighted(
                            scores,
                            RELATIONSHIP,
                            5,
                            weight
                    );

                    addEvidence(
                            evidences,
                            "FLOW_BRANCH_CLASH",
                            left.source()
                                    + "과 "
                                    + right.source()
                                    + "의 지지가 충을 이루어 변화 작용이 겹칩니다."
                    );
                }

                if (
                        isHarmony(
                                left.branch(),
                                right.branch()
                        )
                ) {
                    addWeighted(
                            scores,
                            CAREER,
                            5,
                            weight
                    );

                    addWeighted(
                            scores,
                            WEALTH,
                            4,
                            weight
                    );

                    addWeighted(
                            scores,
                            RELATIONSHIP,
                            7,
                            weight
                    );

                    addEvidence(
                            evidences,
                            "FLOW_BRANCH_HARMONY",
                            left.source()
                                    + "과 "
                                    + right.source()
                                    + "의 지지가 합을 이루어 연결 작용이 반복됩니다."
                    );
                }

                if (
                        isHarm(
                                left.branch(),
                                right.branch()
                        )
                                || isBreak(
                                left.branch(),
                                right.branch()
                        )
                                || isPunishment(
                                left.branch(),
                                right.branch()
                        )
                ) {
                    addWeighted(
                            scores,
                            CHANGE,
                            6,
                            weight
                    );

                    addWeighted(
                            scores,
                            CAUTION,
                            9,
                            weight
                    );

                    addEvidence(
                            evidences,
                            "FLOW_COMPLEX_RELATION",
                            left.source()
                                    + "과 "
                                    + right.source()
                                    + " 사이에 형·파·해 계열의 복합 작용이 나타납니다."
                    );
                }
            }
        }
    }

    /**
     * 대운·세운·월운에서 같은 십성·오행·지지가 반복되는지 계산한다.
     */
    private void applyRepeatedSignals(
            List<FlowPillar> flows,
            int[] scores,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        Map<String, Long> tenGodCounts =
                flows.stream()
                        .filter(
                                flow ->
                                        notBlank(
                                                flow.tenGod()
                                        )
                        )
                        .collect(
                                Collectors.groupingBy(
                                        FlowPillar::tenGod,
                                        LinkedHashMap::new,
                                        Collectors.counting()
                                )
                        );

        tenGodCounts.forEach(
                (tenGod, count) -> {
                    if (
                            count >= 2
                    ) {
                        applyTenGodScores(
                                tenGod,
                                scores,
                                0.45
                                        + (count - 2)
                                        * 0.15
                        );

                        addEvidence(
                                evidences,
                                "REPEATED_TEN_GOD",
                                "대운·세운·월운 중 "
                                        + count
                                        + "개 구간에서 "
                                        + tenGod
                                        + " 작용이 반복됩니다."
                        );
                    }
                }
        );

        Map<String, Long> elementCounts =
                new LinkedHashMap<>();

        for (
                FlowPillar flow : flows
        ) {
            increaseCount(
                    elementCounts,
                    flow.stemElement()
            );

            increaseCount(
                    elementCounts,
                    flow.branchElement()
            );
        }

        elementCounts.forEach(
                (element, count) -> {
                    if (
                            count >= 3
                    ) {
                        add(
                                scores,
                                CHANGE,
                                4
                        );

                        if (
                                count >= 4
                        ) {
                            add(
                                    scores,
                                    CAUTION,
                                    4
                            );
                        }

                        addEvidence(
                                evidences,
                                "REPEATED_ELEMENT",
                                "대운·세운·월운에서 "
                                        + element
                                        + " 기운이 "
                                        + count
                                        + "회 반복되어 해당 오행의 작용이 강해집니다."
                        );
                    }
                }
        );

        Map<String, Long> branchCounts =
                flows.stream()
                        .filter(
                                flow ->
                                        notBlank(
                                                flow.branch()
                                        )
                        )
                        .collect(
                                Collectors.groupingBy(
                                        FlowPillar::branch,
                                        LinkedHashMap::new,
                                        Collectors.counting()
                                )
                        );

        branchCounts.forEach(
                (branch, count) -> {
                    if (
                            count >= 2
                    ) {
                        add(
                                scores,
                                CHANGE,
                                5
                        );

                        add(
                                scores,
                                CAUTION,
                                3
                        );

                        addEvidence(
                                evidences,
                                "REPEATED_BRANCH",
                                "운의 흐름에서 "
                                        + branch
                                        + " 지지가 반복되어 같은 성향의 작용이 이어질 수 있습니다."
                        );
                    }
                }
        );
    }

    /**
     * 신강·신약 보조 판정과 운의 오행 반복을 비교한다.
     */
    private void applyStrengthContext(
            SajuCalculatorService.ForecastBaseData baseData,
            List<FlowPillar> flows,
            int[] scores,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        if (
                !notBlank(
                        baseData.strengthClassification()
                )
        ) {
            return;
        }

        long dayElementCount =
                flows.stream()
                        .flatMap(
                                flow ->
                                        Stream.of(
                                                flow.stemElement(),
                                                flow.branchElement()
                                        )
                        )
                        .filter(
                                element ->
                                        Objects.equals(
                                                element,
                                                baseData.dayElement()
                                        )
                        )
                        .count();

        if (
                "신강".equals(
                        baseData.strengthClassification()
                )
                        && dayElementCount >= 2
        ) {
            add(
                    scores,
                    CAUTION,
                    6
            );

            add(
                    scores,
                    RELATIONSHIP,
                    4
            );

            addEvidence(
                    evidences,
                    "STRENGTH_OVERLAP",
                    "신강 보조 판정에서 일간과 같은 오행이 운에서도 반복되어 "
                            + "고집과 과도한 추진력을 조절할 필요가 있습니다."
            );
        }

        if (
                "신약".equals(
                        baseData.strengthClassification()
                )
                        && dayElementCount >= 2
        ) {
            add(
                    scores,
                    CAREER,
                    5
            );

            add(
                    scores,
                    RELATIONSHIP,
                    4
            );

            add(
                    scores,
                    CAUTION,
                    -3
            );

            addEvidence(
                    evidences,
                    "STRENGTH_SUPPORT",
                    "신약 보조 판정에서 일간을 돕는 같은 오행이 반복되어 "
                            + "추진력과 회복력을 보완할 수 있습니다."
            );
        }
    }

    /**
     * 현재 대운을 기준으로 조회 연도의 대운을 계산한다.
     */
    private SajuCalculatorService.ForecastLuckData resolveDaeunForYear(
            SajuCalculatorService.ForecastBaseData baseData,
            int targetYear
    ) {
        SajuCalculatorService.ForecastLuckData anchor =
                baseData.currentDaeun();

        if (
                anchor == null
                        || !notBlank(
                        anchor.ganji()
                )
        ) {
            return null;
        }

        if (
                targetYear >= anchor.startYear()
                        && targetYear <= anchor.endYear()
        ) {
            return anchor;
        }

        int shift = 0;

        int startYear =
                anchor.startYear();

        int endYear =
                anchor.endYear();

        while (
                targetYear < startYear
        ) {
            startYear -= 10;
            endYear -= 10;
            shift--;
        }

        while (
                targetYear > endYear
        ) {
            startYear += 10;
            endYear += 10;
            shift++;
        }

        int anchorCycleIndex =
                findGanjiCycleIndex(
                        anchor.ganji()
                );

        if (
                anchorCycleIndex < 0
        ) {
            return anchor;
        }

        int directionStep =
                "역행".equals(
                        anchor.direction()
                )
                        ? -1
                        : 1;

        int targetCycleIndex =
                Math.floorMod(
                        anchorCycleIndex
                                + directionStep
                                * shift,
                        60
                );

        String stem =
                STEMS[
                        targetCycleIndex % 10
                        ];

        String branch =
                BRANCHES[
                        targetCycleIndex % 12
                        ];

        return new SajuCalculatorService.ForecastLuckData(
                stem
                        + branch,
                STEMS_KOREAN[
                        targetCycleIndex % 10
                        ]
                        + BRANCHES_KOREAN[
                        targetCycleIndex % 12
                        ],
                stem,
                branch,
                STEM_ELEMENTS[
                        targetCycleIndex % 10
                        ],
                BRANCH_ELEMENTS[
                        targetCycleIndex % 12
                        ],
                calculateTenGod(
                        baseData.dayStem(),
                        stem
                ),
                Math.max(
                        0,
                        anchor.startAge()
                                + shift
                                * 10
                ),
                Math.max(
                        0,
                        anchor.endAge()
                                + shift
                                * 10
                ),
                startYear,
                endYear,
                anchor.direction()
        );
    }

    private int findGanjiCycleIndex(
            String ganji
    ) {
        if (
                !notBlank(
                        ganji
                )
        ) {
            return -1;
        }

        for (
                int index = 0;
                index < 60;
                index++
        ) {
            String candidate =
                    STEMS[
                            index % 10
                            ]
                            + BRANCHES[
                            index % 12
                            ];

            if (
                    candidate.equals(
                            ganji
                    )
            ) {
                return index;
            }
        }

        return -1;
    }

    private FlowPillar toFlowPillar(
            String source,
            SajuCalculatorService.ForecastLuckData data,
            double weight
    ) {
        return new FlowPillar(
                source,
                data.ganji(),
                data.stem(),
                data.branch(),
                data.stemElement(),
                data.branchElement(),
                data.tenGod(),
                weight
        );
    }

    private FlowPillar toFlowPillar(
            String source,
            SajuCalculatorService.ForecastPillarData data,
            double weight
    ) {
        return new FlowPillar(
                source,
                data.ganji(),
                data.stem(),
                data.branch(),
                data.stemElement(),
                data.branchElement(),
                data.tenGod(),
                weight
        );
    }

    private double natalWeight(
            String type
    ) {
        if (
                "일주".equals(
                        type
                )
        ) {
            return 1.15;
        }

        if (
                "월주".equals(
                        type
                )
        ) {
            return 1.05;
        }

        if (
                "년주".equals(
                        type
                )
        ) {
            return 0.90;
        }

        if (
                "시주".equals(
                        type
                )
        ) {
            return 0.85;
        }

        return 1.00;
    }

    /**
     * 일간과 대상 천간의 십성을 계산한다.
     */
    private String calculateTenGod(
            String dayStem,
            String targetStem
    ) {
        StemInfo day =
                getStemInfo(
                        dayStem
                );

        StemInfo target =
                getStemInfo(
                        targetStem
                );

        if (
                day == null
                        || target == null
        ) {
            return null;
        }

        boolean samePolarity =
                day.yang()
                        == target.yang();

        if (
                Objects.equals(
                        day.element(),
                        target.element()
                )
        ) {
            return samePolarity
                    ? "비견"
                    : "겁재";
        }

        if (
                Objects.equals(
                        produces(
                                day.element()
                        ),
                        target.element()
                )
        ) {
            return samePolarity
                    ? "식신"
                    : "상관";
        }

        if (
                Objects.equals(
                        controls(
                                day.element()
                        ),
                        target.element()
                )
        ) {
            return samePolarity
                    ? "편재"
                    : "정재";
        }

        if (
                Objects.equals(
                        controls(
                                target.element()
                        ),
                        day.element()
                )
        ) {
            return samePolarity
                    ? "편관"
                    : "정관";
        }

        if (
                Objects.equals(
                        produces(
                                target.element()
                        ),
                        day.element()
                )
        ) {
            return samePolarity
                    ? "편인"
                    : "정인";
        }

        return null;
    }

    private StemInfo getStemInfo(
            String stem
    ) {
        if (
                !notBlank(
                        stem
                )
        ) {
            return null;
        }

        return switch (
                stem
                ) {
            case "甲" ->
                    new StemInfo(
                            "목",
                            true
                    );

            case "乙" ->
                    new StemInfo(
                            "목",
                            false
                    );

            case "丙" ->
                    new StemInfo(
                            "화",
                            true
                    );

            case "丁" ->
                    new StemInfo(
                            "화",
                            false
                    );

            case "戊" ->
                    new StemInfo(
                            "토",
                            true
                    );

            case "己" ->
                    new StemInfo(
                            "토",
                            false
                    );

            case "庚" ->
                    new StemInfo(
                            "금",
                            true
                    );

            case "辛" ->
                    new StemInfo(
                            "금",
                            false
                    );

            case "壬" ->
                    new StemInfo(
                            "수",
                            true
                    );

            case "癸" ->
                    new StemInfo(
                            "수",
                            false
                    );

            default -> null;
        };
    }

    private String produces(
            String source
    ) {
        return switch (
                source
                ) {
            case "목" -> "화";
            case "화" -> "토";
            case "토" -> "금";
            case "금" -> "수";
            case "수" -> "목";
            default -> null;
        };
    }

    private String controls(
            String source
    ) {
        return switch (
                source
                ) {
            case "목" -> "토";
            case "토" -> "수";
            case "수" -> "화";
            case "화" -> "금";
            case "금" -> "목";
            default -> null;
        };
    }

    private boolean isStemCombination(
            String first,
            String second
    ) {
        return isPair(
                first,
                second,
                "甲",
                "己"
        )
                || isPair(
                first,
                second,
                "乙",
                "庚"
        )
                || isPair(
                first,
                second,
                "丙",
                "辛"
        )
                || isPair(
                first,
                second,
                "丁",
                "壬"
        )
                || isPair(
                first,
                second,
                "戊",
                "癸"
        );
    }

    private boolean isStemClash(
            String first,
            String second
    ) {
        return isPair(
                first,
                second,
                "甲",
                "庚"
        )
                || isPair(
                first,
                second,
                "乙",
                "辛"
        )
                || isPair(
                first,
                second,
                "丙",
                "壬"
        )
                || isPair(
                first,
                second,
                "丁",
                "癸"
        );
    }

    private boolean isClash(
            String first,
            String second
    ) {
        return isPair(
                first,
                second,
                "子",
                "午"
        )
                || isPair(
                first,
                second,
                "丑",
                "未"
        )
                || isPair(
                first,
                second,
                "寅",
                "申"
        )
                || isPair(
                first,
                second,
                "卯",
                "酉"
        )
                || isPair(
                first,
                second,
                "辰",
                "戌"
        )
                || isPair(
                first,
                second,
                "巳",
                "亥"
        );
    }

    private boolean isHarmony(
            String first,
            String second
    ) {
        return isPair(
                first,
                second,
                "子",
                "丑"
        )
                || isPair(
                first,
                second,
                "寅",
                "亥"
        )
                || isPair(
                first,
                second,
                "卯",
                "戌"
        )
                || isPair(
                first,
                second,
                "辰",
                "酉"
        )
                || isPair(
                first,
                second,
                "巳",
                "申"
        )
                || isPair(
                first,
                second,
                "午",
                "未"
        );
    }

    private boolean isHarm(
            String first,
            String second
    ) {
        return isPair(
                first,
                second,
                "子",
                "未"
        )
                || isPair(
                first,
                second,
                "丑",
                "午"
        )
                || isPair(
                first,
                second,
                "寅",
                "巳"
        )
                || isPair(
                first,
                second,
                "卯",
                "辰"
        )
                || isPair(
                first,
                second,
                "申",
                "亥"
        )
                || isPair(
                first,
                second,
                "酉",
                "戌"
        );
    }

    private boolean isBreak(
            String first,
            String second
    ) {
        return isPair(
                first,
                second,
                "子",
                "酉"
        )
                || isPair(
                first,
                second,
                "丑",
                "辰"
        )
                || isPair(
                first,
                second,
                "寅",
                "亥"
        )
                || isPair(
                first,
                second,
                "卯",
                "午"
        )
                || isPair(
                first,
                second,
                "巳",
                "申"
        )
                || isPair(
                first,
                second,
                "未",
                "戌"
        );
    }

    private boolean isPunishment(
            String first,
            String second
    ) {
        if (
                !notBlank(
                        first
                )
                        || !notBlank(
                        second
                )
        ) {
            return false;
        }

        if (
                first.equals(
                        second
                )
                        && Set.of(
                        "辰",
                        "午",
                        "酉",
                        "亥"
                ).contains(
                        first
                )
        ) {
            return true;
        }

        if (
                isPair(
                        first,
                        second,
                        "子",
                        "卯"
                )
        ) {
            return true;
        }

        Set<String> firstGroup =
                Set.of(
                        "寅",
                        "巳",
                        "申"
                );

        Set<String> secondGroup =
                Set.of(
                        "丑",
                        "戌",
                        "未"
                );

        return firstGroup.contains(
                first
        )
                && firstGroup.contains(
                second
        )
                || secondGroup.contains(
                first
        )
                && secondGroup.contains(
                second
        );
    }

    private boolean isPair(
            String first,
            String second,
            String left,
            String right
    ) {
        return Objects.equals(
                left,
                first
        )
                && Objects.equals(
                right,
                second
        )
                || Objects.equals(
                right,
                first
        )
                && Objects.equals(
                left,
                second
        );
    }

    /**
     * 12개월 중 작용이 가장 강한 구간을 찾는다.
     */
    private PeakRange findPeakRange(
            List<MonthDraft> drafts
    ) {
        MonthDraft strongest =
                drafts.stream()
                        .max(
                                Comparator.comparingInt(
                                        this::activationScore
                                )
                        )
                        .orElseThrow();

        int strongestMonth =
                strongest.month();

        int strongestScore =
                activationScore(
                        strongest
                );

        int peakStart =
                strongestMonth;

        int peakEnd =
                strongestMonth;

        int threshold =
                Math.max(
                        58,
                        strongestScore
                                - 9
                );

        while (
                peakStart > 1
        ) {
            MonthDraft previous =
                    drafts.get(
                            peakStart - 2
                    );

            if (
                    activationScore(
                            previous
                    )
                            < threshold
            ) {
                break;
            }

            peakStart--;
        }

        while (
                peakEnd < 12
        ) {
            MonthDraft next =
                    drafts.get(
                            peakEnd
                    );

            if (
                    activationScore(
                            next
                    )
                            < threshold
            ) {
                break;
            }

            peakEnd++;
        }

        int influenceStart =
                Math.max(
                        1,
                        peakStart - 1
                );

        int influenceEnd =
                Math.min(
                        12,
                        peakEnd + 1
                );

        return new PeakRange(
                influenceStart,
                peakStart,
                peakEnd,
                influenceEnd
        );
    }

    private SajuMonthForecastResponse.MonthPhase determineMonthPhase(
            int month,
            PeakRange range
    ) {
        if (
                month >= range.peakStartMonth()
                        && month <= range.peakEndMonth()
        ) {
            return SajuMonthForecastResponse
                    .MonthPhase
                    .PEAK;
        }

        if (
                month >= range.influenceStartMonth()
                        && month < range.peakStartMonth()
        ) {
            return SajuMonthForecastResponse
                    .MonthPhase
                    .PRELUDE;
        }

        if (
                month > range.peakEndMonth()
                        && month <= range.influenceEndMonth()
        ) {
            return SajuMonthForecastResponse
                    .MonthPhase
                    .AFTERMATH;
        }

        return SajuMonthForecastResponse
                .MonthPhase
                .NORMAL;
    }

    private int activationScore(
            MonthDraft draft
    ) {
        SajuMonthForecastResponse.ForecastScores scores =
                draft.scores();

        int strongestPositive =
                Math.max(
                        Math.max(
                                scores.career(),
                                scores.wealth()
                        ),
                        Math.max(
                                Math.max(
                                        scores.business(),
                                        scores.relationship()
                                ),
                                Math.max(
                                        scores.romance(),
                                        scores.change()
                                )
                        )
                );

        return Math.max(
                strongestPositive,
                scores.caution()
        );
    }

    /**
     * 월별 점수와 세운 점수를 합산해 연간 점수를 만든다.
     */
    private SajuMonthForecastResponse.ForecastScores aggregateScores(
            List<MonthDraft> drafts,
            SajuMonthForecastResponse.ForecastScores yearScores
    ) {
        return new SajuMonthForecastResponse.ForecastScores(
                combineScore(
                        drafts,
                        SajuMonthForecastResponse
                                .ForecastScores::career,
                        yearScores.career()
                ),
                combineScore(
                        drafts,
                        SajuMonthForecastResponse
                                .ForecastScores::wealth,
                        yearScores.wealth()
                ),
                combineScore(
                        drafts,
                        SajuMonthForecastResponse
                                .ForecastScores::business,
                        yearScores.business()
                ),
                combineScore(
                        drafts,
                        SajuMonthForecastResponse
                                .ForecastScores::relationship,
                        yearScores.relationship()
                ),
                combineScore(
                        drafts,
                        SajuMonthForecastResponse
                                .ForecastScores::romance,
                        yearScores.romance()
                ),
                combineScore(
                        drafts,
                        SajuMonthForecastResponse
                                .ForecastScores::change,
                        yearScores.change()
                ),
                combineScore(
                        drafts,
                        SajuMonthForecastResponse
                                .ForecastScores::caution,
                        yearScores.caution()
                )
        );
    }

    private int combineScore(
            List<MonthDraft> drafts,
            ToIntFunction<SajuMonthForecastResponse.ForecastScores> extractor,
            int yearScore
    ) {
        double average =
                drafts.stream()
                        .map(
                                MonthDraft::scores
                        )
                        .mapToInt(
                                extractor
                        )
                        .average()
                        .orElse(
                                0
                        );

        int maximum =
                drafts.stream()
                        .map(
                                MonthDraft::scores
                        )
                        .mapToInt(
                                extractor
                        )
                        .max()
                        .orElse(
                                0
                        );

        return normalizeScore(
                (int) Math.round(
                        average * 0.40
                                + maximum * 0.35
                                + yearScore * 0.25
                )
        );
    }

    private List<SajuMonthForecastResponse.ForecastEvidence>
    collectImportantMonthEvidences(
            List<MonthDraft> drafts,
            PeakRange range
    ) {
        List<SajuMonthForecastResponse.ForecastEvidence> result =
                new ArrayList<>();

        for (
                MonthDraft draft : drafts
        ) {
            if (
                    draft.month()
                            >= range.peakStartMonth()
                            && draft.month()
                            <= range.peakEndMonth()
            ) {
                result.addAll(
                        draft.evidences()
                );
            }
        }

        return distinctEvidences(
                result
        );
    }

    @SafeVarargs
    private final List<SajuMonthForecastResponse.ForecastEvidence>
    mergeEvidences(
            List<SajuMonthForecastResponse.ForecastEvidence>... evidenceLists
    ) {
        List<SajuMonthForecastResponse.ForecastEvidence> merged =
                new ArrayList<>();

        for (
                List<SajuMonthForecastResponse.ForecastEvidence> list
                : evidenceLists
        ) {
            if (
                    list != null
            ) {
                merged.addAll(
                        list
                );
            }
        }

        return distinctEvidences(
                merged
        );
    }

    private List<SajuMonthForecastResponse.ForecastEvidence>
    distinctEvidences(
            List<SajuMonthForecastResponse.ForecastEvidence> values
    ) {
        Map<String, SajuMonthForecastResponse.ForecastEvidence> unique =
                new LinkedHashMap<>();

        if (
                values == null
        ) {
            return List.of();
        }

        for (
                SajuMonthForecastResponse.ForecastEvidence value
                : values
        ) {
            if (
                    value == null
                            || !notBlank(
                            value.description()
                    )
            ) {
                continue;
            }

            String key =
                    String.valueOf(
                            value.type()
                    )
                            + "|"
                            + value.description();

            unique.putIfAbsent(
                    key,
                    value
            );
        }

        return unique.values()
                .stream()
                .limit(
                        12
                )
                .toList();
    }

    private void addEvidence(
            List<SajuMonthForecastResponse.ForecastEvidence> evidences,
            String type,
            String description
    ) {
        if (
                !notBlank(
                        description
                )
        ) {
            return;
        }

        boolean exists =
                evidences.stream()
                        .anyMatch(
                                evidence ->
                                        Objects.equals(
                                                description,
                                                evidence.description()
                                        )
                        );

        if (
                !exists
        ) {
            evidences.add(
                    new SajuMonthForecastResponse.ForecastEvidence(
                            type,
                            description
                    )
            );
        }
    }

    /**
     * 해석 근거의 반복 정도를 신뢰 점수로 환산한다.
     *
     * 사건 발생 확률이 아니라 계산 근거의 중첩 정도다.
     */
    private int calculateConfidenceScore(
            SajuCalculatorService.ForecastBaseData baseData,
            SajuCalculatorService.ForecastLuckData daeun,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        int score =
                28;

        if (
                notBlank(
                        baseData.dayStem()
                )
        ) {
            score += 8;
        }

        if (
                baseData.natalPillars()
                        .size()
                        >= 4
        ) {
            score += 8;

        } else if (
                baseData.natalPillars()
                        .size()
                        >= 3
        ) {
            score += 5;
        }

        if (
                notBlank(
                        baseData.usefulElement()
                )
        ) {
            score += 5;
        }

        if (
                notBlank(
                        baseData.favorableElement()
                )
        ) {
            score += 3;
        }

        if (
                notBlank(
                        baseData.unfavorableElement()
                )
        ) {
            score += 3;
        }

        if (
                daeun != null
        ) {
            score += 7;
        }

        if (
                baseData.birthTimeUnknown()
        ) {
            score -= 7;
        }

        Set<String> evidenceTypes =
                new LinkedHashSet<>();

        for (
                SajuMonthForecastResponse.ForecastEvidence evidence
                : evidences
        ) {
            if (
                    evidence == null
                            || !notBlank(
                            evidence.type()
                    )
            ) {
                continue;
            }

            evidenceTypes.add(
                    evidence.type()
            );

            if (
                    evidence.type()
                            .contains(
                                    "CLASH"
                            )
                            || evidence.type()
                            .contains(
                                    "PUNISHMENT"
                            )
                            || evidence.type()
                            .contains(
                                    "REPEATED"
                            )
            ) {
                score += 4;

            } else if (
                    evidence.type()
                            .contains(
                                    "DAEUN"
                            )
                            || evidence.type()
                            .contains(
                                    "FLOW"
                            )
            ) {
                score += 3;

            } else {
                score += 2;
            }
        }

        score +=
                Math.min(
                        evidenceTypes.size()
                                * 2,
                        12
                );

        return Math.max(
                35,
                Math.min(
                        score,
                        94
                )
        );
    }

    private SajuMonthForecastResponse.ConfidenceLevel determineConfidence(
            int confidenceScore,
            List<SajuMonthForecastResponse.ForecastEvidence> evidences
    ) {
        long repeatedOrLayeredCount =
                evidences.stream()
                        .filter(
                                evidence ->
                                        evidence != null
                                                && notBlank(
                                                evidence.type()
                                        )
                        )
                        .filter(
                                evidence ->
                                        evidence.type()
                                                .contains(
                                                        "REPEATED"
                                                )
                                                || evidence.type()
                                                .contains(
                                                        "FLOW"
                                                )
                                                || evidence.type()
                                                .contains(
                                                        "CLASH"
                                                )
                                                || evidence.type()
                                                .contains(
                                                        "PUNISHMENT"
                                                )
                        )
                        .count();

        if (
                confidenceScore >= 80
                        && repeatedOrLayeredCount >= 3
                        && evidences.size() >= 5
        ) {
            return SajuMonthForecastResponse
                    .ConfidenceLevel
                    .HIGH;
        }

        if (
                confidenceScore >= 60
                        && evidences.size() >= 3
        ) {
            return SajuMonthForecastResponse
                    .ConfidenceLevel
                    .MEDIUM;
        }

        return SajuMonthForecastResponse
                .ConfidenceLevel
                .REFERENCE;
    }

    /**
     * 분야별 점수 중 가장 강한 주제를 선택한다.
     */
    private String selectTheme(
            SajuMonthForecastResponse.ForecastScores scores
    ) {
        Map<String, Integer> candidates =
                new LinkedHashMap<>();

        candidates.put(
                "직업과 책임",
                scores.career()
        );

        candidates.put(
                "재물과 계약",
                scores.wealth()
        );

        candidates.put(
                "사업과 독립",
                scores.business()
        );

        candidates.put(
                "사람과 관계",
                scores.relationship()
        );

        candidates.put(
                "연애와 감정",
                scores.romance()
        );

        candidates.put(
                "이동과 변화",
                scores.change()
        );

        Map.Entry<String, Integer> strongest =
                candidates.entrySet()
                        .stream()
                        .max(
                                Map.Entry.comparingByValue()
                        )
                        .orElse(
                                Map.entry(
                                        "생활의 균형",
                                        0
                                )
                        );

        if (
                scores.caution()
                        >= 78
                        && scores.caution()
                        >= strongest.getValue()
                        + 4
        ) {
            return "주의와 정리";
        }

        return strongest.getKey();
    }

    private String createYearHeadline(
            int year,
            String theme
    ) {
        return switch (
                theme
                ) {
            case "직업과 책임" ->
                    year
                            + "년은 역할과 책임이 커지며 자신의 능력을 증명해야 하는 해입니다.";

            case "재물과 계약" ->
                    year
                            + "년은 수입과 지출, 계약의 움직임이 함께 커질 수 있는 해입니다.";

            case "사업과 독립" ->
                    year
                            + "년은 자신의 방식으로 일을 시작하거나 확장하려는 힘이 강해지는 해입니다.";

            case "사람과 관계" ->
                    year
                            + "년은 새로운 인연과 기존 관계의 재정리가 함께 나타날 수 있는 해입니다.";

            case "연애와 감정" ->
                    year
                            + "년은 감정과 관계의 방향을 분명하게 정하게 될 가능성이 큰 해입니다.";

            case "이동과 변화" ->
                    year
                            + "년은 직장·거주·생활방식의 변화를 고민하게 될 가능성이 큰 해입니다.";

            case "주의와 정리" ->
                    year
                            + "년은 무리하게 확장하기보다 문제를 정리하고 기본을 다지는 것이 중요한 해입니다.";

            default ->
                    year
                            + "년은 생활의 방향을 다시 조정하고 균형을 잡아가는 해입니다.";
        };
    }

    private String createYearSummary(
            int year,
            String theme,
            String tenGod,
            SajuCalculatorService.ForecastLuckData daeun,
            SajuYearForecastResponse.InfluencePeriod influencePeriod
    ) {
        String tenGodText =
                notBlank(
                        tenGod
                )
                        ? tenGod
                        + " 작용"
                        : "세운의 오행과 지지 작용";

        String daeunText =
                daeun == null
                        ? ""
                        : "현재 "
                        + daeun.ganjiKorean()
                        + " 대운의 "
                        + safeText(
                        daeun.tenGod(),
                        "복합"
                )
                        + " 작용 위에서 ";

        return year
                + "년에는 "
                + daeunText
                + tenGodText
                + "이 들어오면서 "
                + theme
                + "과 관련된 움직임이 커질 수 있습니다. "
                + influencePeriod.description()
                + " 특정 사건을 단정하기보다 이 기간에 반복되는 선택과 행동을 점검하는 것이 중요합니다.";
    }

    private String createMonthHeadline(
            String theme
    ) {
        return switch (
                theme
                ) {
            case "직업과 책임" ->
                    "업무와 책임이 커지며 실력을 보여줘야 하는 달입니다.";

            case "재물과 계약" ->
                    "돈과 계약이 움직이지만 조건을 꼼꼼하게 확인해야 하는 달입니다.";

            case "사업과 독립" ->
                    "새로운 일을 시작하거나 자신의 영향력을 넓히려는 힘이 강해지는 달입니다.";

            case "사람과 관계" ->
                    "사람이 붙고 관계가 움직이지만 경계를 분명히 해야 하는 달입니다.";

            case "연애와 감정" ->
                    "감정 표현이 커지고 관계의 방향이 분명해질 수 있는 달입니다.";

            case "이동과 변화" ->
                    "기존 계획이 바뀌거나 새로운 환경을 선택할 가능성이 커지는 달입니다.";

            case "주의와 정리" ->
                    "서두르기보다 확인하고 정리하는 과정이 중요한 달입니다.";

            default ->
                    "생활의 속도를 조절하고 균형을 잡는 것이 중요한 달입니다.";
        };
    }

    private String createMonthSummary(
            int year,
            int month,
            String theme,
            String tenGod,
            SajuCalculatorService.ForecastLuckData daeun,
            SajuCalculatorService.ForecastPillarData yearPillar
    ) {
        String monthRelation =
                notBlank(
                        tenGod
                )
                        ? tenGod
                        + "의 월운"
                        : "월운의 오행과 지지 관계";

        String layeredText =
                daeun == null
                        ? yearPillar.ganjiKorean()
                        + " 세운과 "
                        : daeun.ganjiKorean()
                        + " 대운, "
                        + yearPillar.ganjiKorean()
                        + " 세운 위에서 ";

        return year
                + "년 "
                + month
                + "월에는 "
                + layeredText
                + monthRelation
                + "이 겹치면서 "
                + theme
                + " 분야의 움직임이 커질 수 있습니다. "
                + "이달에 반드시 특정 사건이 발생한다는 뜻은 아니며, 관련 선택과 변화가 평소보다 눈에 띌 수 있다는 의미입니다.";
    }

    private List<String> createPossibleEffects(
            String theme
    ) {
        return switch (
                theme
                ) {
            case "직업과 책임" ->
                    List.of(
                            "업무 책임 증가",
                            "직책 또는 담당 업무 변화",
                            "평가와 성과에 대한 부담",
                            "새로운 기술이나 자격의 필요성"
                    );

            case "재물과 계약" ->
                    List.of(
                            "수입과 지출의 동시 증가",
                            "계약 조건 변경",
                            "새로운 수익 제안",
                            "예상하지 못한 비용 발생"
                    );

            case "사업과 독립" ->
                    List.of(
                            "사업 또는 부업 준비",
                            "프로젝트 시작",
                            "독립적인 역할 증가",
                            "성과에 따른 수입 변화"
                    );

            case "사람과 관계" ->
                    List.of(
                            "새로운 사람과의 연결",
                            "기존 관계의 재정리",
                            "협업과 경쟁의 동시 발생",
                            "책임이나 돈이 섞인 관계"
                    );

            case "연애와 감정" ->
                    List.of(
                            "새로운 만남 가능성",
                            "기존 관계의 방향 결정",
                            "감정 표현 증가",
                            "서운함과 기대의 충돌"
                    );

            case "이동과 변화" ->
                    List.of(
                            "이직 또는 부서 이동 고민",
                            "이사와 생활환경 변화",
                            "일정 변경",
                            "기존 계획 수정"
                    );

            case "주의와 정리" ->
                    List.of(
                            "일정 지연",
                            "감정적인 판단",
                            "계약이나 관계의 재검토",
                            "과로와 집중력 저하"
                    );

            default ->
                    List.of(
                            "생활 방향 재점검",
                            "우선순위 변경",
                            "새로운 선택에 대한 고민"
                    );
        };
    }

    private List<String> createOpportunities(
            String theme
    ) {
        return switch (
                theme
                ) {
            case "직업과 책임" ->
                    List.of(
                            "실적과 결과를 기록하기",
                            "새로운 업무를 배울 기회로 활용하기",
                            "역할과 책임 범위를 명확히 하기",
                            "자격증과 전문성을 활용하기"
                    );

            case "재물과 계약" ->
                    List.of(
                            "수입 구조 점검하기",
                            "계약 조건 비교하기",
                            "고정지출 정리하기",
                            "현실적인 예산을 먼저 세우기"
                    );

            case "사업과 독립" ->
                    List.of(
                            "작은 규모로 먼저 시험하기",
                            "수익 구조를 수치로 확인하기",
                            "역할과 지분을 문서화하기",
                            "자신의 전문성을 알리기"
                    );

            case "사람과 관계" ->
                    List.of(
                            "말보다 행동을 오래 관찰하기",
                            "협업 범위를 분명히 하기",
                            "금전과 친분을 구분하기",
                            "도움받은 부분과 책임을 기록하기"
                    );

            case "연애와 감정" ->
                    List.of(
                            "감정을 바로 단정하지 않기",
                            "상대의 속도를 존중하기",
                            "서운함을 구체적으로 표현하기",
                            "중요한 결정을 충분히 대화하기"
                    );

            case "이동과 변화" ->
                    List.of(
                            "변화의 이유를 명확히 하기",
                            "새로운 환경을 미리 조사하기",
                            "대안을 함께 준비하기",
                            "기존 일을 정리한 뒤 움직이기"
                    );

            case "주의와 정리" ->
                    List.of(
                            "미뤄둔 일을 정리하기",
                            "계약과 지출을 다시 확인하기",
                            "생활 리듬 회복하기",
                            "중요한 결정을 하루 이상 검토하기"
                    );

            default ->
                    List.of(
                            "생활의 우선순위 정리하기",
                            "무리한 계획을 줄이기",
                            "꾸준히 이어갈 수 있는 목표 세우기"
                    );
        };
    }

    private List<String> createCautions(
            String theme,
            int cautionScore
    ) {
        List<String> cautions =
                new ArrayList<>();

        switch (
                theme
        ) {
            case "직업과 책임" -> {
                cautions.add(
                        "감정적으로 직장을 그만두지 않기"
                );

                cautions.add(
                        "책임을 혼자 떠안지 않기"
                );

                cautions.add(
                        "구두 약속만 믿지 않기"
                );
            }

            case "재물과 계약" -> {
                cautions.add(
                        "예상 수입만 믿고 지출을 늘리지 않기"
                );

                cautions.add(
                        "계약서를 확인하지 않고 서명하지 않기"
                );

                cautions.add(
                        "친분만 믿고 돈을 빌려주지 않기"
                );
            }

            case "사업과 독립" -> {
                cautions.add(
                        "준비 없이 큰 비용을 투입하지 않기"
                );

                cautions.add(
                        "동업자의 역할과 지분을 애매하게 두지 않기"
                );

                cautions.add(
                        "매출보다 비용이 먼저 커지지 않게 관리하기"
                );
            }

            case "사람과 관계" -> {
                cautions.add(
                        "상대의 부탁을 무조건 대신 책임지지 않기"
                );

                cautions.add(
                        "사람과 돈 문제를 섞지 않기"
                );

                cautions.add(
                        "화가 난 상태에서 관계를 끊지 않기"
                );
            }

            case "연애와 감정" -> {
                cautions.add(
                        "서운함을 쌓아두다가 갑자기 폭발하지 않기"
                );

                cautions.add(
                        "상대의 말을 확인하지 않고 단정하지 않기"
                );

                cautions.add(
                        "감정만으로 중요한 결정을 서두르지 않기"
                );
            }

            case "이동과 변화" -> {
                cautions.add(
                        "대안 없이 기존 일을 먼저 정리하지 않기"
                );

                cautions.add(
                        "일정과 계약 조건을 다시 확인하기"
                );

                cautions.add(
                        "피곤한 상태에서 이동을 서두르지 않기"
                );
            }

            case "주의와 정리" -> {
                cautions.add(
                        "감정적인 메시지를 바로 보내지 않기"
                );

                cautions.add(
                        "확인하지 않은 송금이나 계약을 하지 않기"
                );

                cautions.add(
                        "과로한 상태에서 중요한 결정을 내리지 않기"
                );
            }

            default -> {
                cautions.add(
                        "한 번에 너무 많은 일을 벌이지 않기"
                );

                cautions.add(
                        "감정적으로 결론을 내리지 않기"
                );
            }
        }

        if (
                cautionScore >= 70
        ) {
            cautions.add(
                    "중요한 결정은 최소 하루 이상 다시 검토하기"
            );

            cautions.add(
                    "금전·계약·이동 관련 사항은 두 번 확인하기"
            );
        }

        return cautions.stream()
                .distinct()
                .limit(
                        5
                )
                .toList();
    }

    private String createInfluencePeriodDescription(
            YearMonth influenceStart,
            YearMonth peakStart,
            YearMonth peakEnd,
            YearMonth influenceEnd,
            String theme
    ) {
        return formatYearMonth(
                influenceStart
        )
                + "부터 움직임이 나타나기 시작하고, "
                + formatYearMonth(
                peakStart
        )
                + "부터 "
                + formatYearMonth(
                peakEnd
        )
                + " 사이에 "
                + theme
                + " 작용이 가장 강해질 수 있습니다. "
                + formatYearMonth(
                influenceEnd
        )
                + "까지 결과 정리와 후속 흐름이 이어질 수 있습니다.";
    }

    private String formatYearMonth(
            YearMonth value
    ) {
        return value.getYear()
                + "년 "
                + value.getMonthValue()
                + "월";
    }

    private void increaseCount(
            Map<String, Long> counts,
            String value
    ) {
        if (
                !notBlank(
                        value
                )
        ) {
            return;
        }

        counts.put(
                value,
                counts.getOrDefault(
                        value,
                        0L
                )
                        + 1L
        );
    }

    private void addWeighted(
            int[] scores,
            int index,
            int amount,
            double weight
    ) {
        add(
                scores,
                index,
                (int) Math.round(
                        amount
                                * weight
                )
        );
    }

    private void add(
            int[] scores,
            int index,
            int amount
    ) {
        scores[index] += amount;
    }

    private void normalizeScores(
            int[] scores
    ) {
        for (
                int index = 0;
                index < scores.length;
                index++
        ) {
            scores[index] =
                    normalizeScore(
                            scores[index]
                    );
        }
    }

    private int normalizeScore(
            int score
    ) {
        return Math.max(
                0,
                Math.min(
                        score,
                        100
                )
        );
    }

    private boolean notBlank(
            String value
    ) {
        return value != null
                && !value.isBlank();
    }

    private String safeText(
            String value,
            String fallback
    ) {
        return notBlank(
                value
        )
                ? value
                : fallback;
    }

    private void validateAnalysis(
            SajuAnalysis analysis
    ) {
        if (
                analysis == null
                        || analysis.getBirthProfile()
                        == null
        ) {
            throw new IllegalArgumentException(
                    "사주 분석 기본정보가 없습니다."
            );
        }
    }

    private void validateYear(
            int year
    ) {
        if (
                year < MIN_FORECAST_YEAR
                        || year > MAX_FORECAST_YEAR
        ) {
            throw new IllegalArgumentException(
                    "미래 운세 계산 지원 연도는 "
                            + MIN_FORECAST_YEAR
                            + "년부터 "
                            + MAX_FORECAST_YEAR
                            + "년까지입니다."
            );
        }
    }

    private void validateMonth(
            int month
    ) {
        if (
                month < 1
                        || month > 12
        ) {
            throw new IllegalArgumentException(
                    "조회 월은 1월부터 12월 사이여야 합니다."
            );
        }
    }

    private record FlowPillar(

            String source,

            String ganji,

            String stem,

            String branch,

            String stemElement,

            String branchElement,

            String tenGod,

            double weight

    ) {
    }

    private record StemInfo(

            String element,

            boolean yang

    ) {
    }

    private record ScoreResult(

            SajuMonthForecastResponse.ForecastScores scores,

            String tenGod,

            List<SajuMonthForecastResponse.ForecastEvidence> evidences

    ) {
    }

    private record PeakRange(

            int influenceStartMonth,

            int peakStartMonth,

            int peakEndMonth,

            int influenceEndMonth

    ) {
    }

    private record MonthDraft(

            int year,

            int month,

            String ganji,

            String solarTermName,

            java.time.LocalDateTime periodStart,

            java.time.LocalDateTime periodEnd,

            String title,

            String headline,

            String summary,

            List<String> possibleEffects,

            List<String> goodUses,

            List<String> cautions,

            SajuMonthForecastResponse.ForecastScores scores,

            SajuMonthForecastResponse.ConfidenceLevel confidence,

            int confidenceScore,

            List<SajuMonthForecastResponse.ForecastEvidence> evidences

    ) {

        private SajuMonthForecastResponse toResponse(
                SajuMonthForecastResponse.MonthPhase phase
        ) {
            return new SajuMonthForecastResponse(
                    year,
                    month,
                    ganji,
                    solarTermName,
                    periodStart,
                    periodEnd,
                    phase,
                    title,
                    headline,
                    summary,
                    possibleEffects,
                    goodUses,
                    cautions,
                    scores,
                    confidence,
                    confidenceScore,
                    evidences.size(),
                    evidences,
                    "이 월운은 특정 사건이 반드시 발생한다는 뜻이 아니라, "
                            + "원국·대운·세운·월운의 반복 작용을 바탕으로 "
                            + "관련 변화 가능성이 평소보다 강해질 수 있는 기간을 설명합니다."
            );
        }
    }
}