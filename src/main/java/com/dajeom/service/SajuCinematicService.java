package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuAnalysisResponse.InterpretationResponse;
import com.dajeom.dto.SajuAnalysisResponse.PillarResponse;
import com.dajeom.dto.SajuCinematicResponse;
import com.dajeom.dto.SajuMonthForecastResponse;
import com.dajeom.dto.SajuYearForecastResponse;
import com.dajeom.entity.SajuAnalysis;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class SajuCinematicService {

    /**
     * 종합 사주 분석 결과와 연도별 미래 흐름을
     * 프론트엔드에서 영상처럼 재생할 수 있는 장면 데이터로 변환한다.
     */
    public SajuCinematicResponse createCinematic(
            SajuAnalysis analysis,
            SajuAnalysisResponse analysisResponse,
            List<SajuYearForecastResponse> yearForecasts
    ) {
        validateInput(
                analysis,
                analysisResponse
        );

        Long analysisId =
                analysis.getId();

        Long profileId =
                analysis.getBirthProfile()
                        .getId();

        String profileName =
                safeText(
                        analysis.getBirthProfile()
                                .getName(),
                        "사용자"
                );

        List<SajuYearForecastResponse> safeYearForecasts =
                yearForecasts == null
                        ? List.of()
                        : yearForecasts.stream()
                        .filter(
                                Objects::nonNull
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        SajuYearForecastResponse::year
                                )
                        )
                        .toList();

        String personality =
                findInterpretation(
                        analysisResponse,
                        "성격"
                );

        String romance =
                findInterpretation(
                        analysisResponse,
                        "연애"
                );

        String wealth =
                findInterpretation(
                        analysisResponse,
                        "재물"
                );

        String career =
                findInterpretation(
                        analysisResponse,
                        "직업"
                );

        String business =
                findInterpretation(
                        analysisResponse,
                        "창업"
                );

        String health =
                findInterpretation(
                        analysisResponse,
                        "건강"
                );

        String study =
                findInterpretation(
                        analysisResponse,
                        "시험"
                );

        String dominantElement =
                analysisResponse.elementBalance() == null
                        ? null
                        : analysisResponse.elementBalance()
                        .dominant();

        String weakestElement =
                analysisResponse.elementBalance() == null
                        ? null
                        : analysisResponse.elementBalance()
                        .weakest();

        String dominantTenGod =
                analysisResponse.tenGodBalance() == null
                        ? null
                        : analysisResponse.tenGodBalance()
                        .dominant();

        String strengthClassification =
                analysisResponse.strength() == null
                        ? null
                        : analysisResponse.strength()
                        .classification();

        String usefulElement =
                analysisResponse.strength() == null
                        ? null
                        : analysisResponse.strength()
                        .usefulGod();

        String favorableElement =
                analysisResponse.strength() == null
                        ? null
                        : analysisResponse.strength()
                        .favorableGod();

        String unfavorableElement =
                analysisResponse.strength() == null
                        ? null
                        : analysisResponse.strength()
                        .unfavorableGod();

        String summary =
                safeText(
                        analysisResponse.summary(),
                        profileName
                                + "님의 만세력 원국과 오행, 십성의 배치를 바탕으로 "
                                + "타고난 성향과 앞으로의 흐름을 살펴봅니다."
                );

        String currentDaeun =
                createCurrentDaeunNarration(
                        analysisResponse
                );

        List<String> manseHighlights =
                extractManseHighlights(
                        analysisResponse
                );

        List<String> strengths =
                createStrengthHighlights(
                        dominantElement,
                        dominantTenGod,
                        strengthClassification
                );

        List<String> weaknesses =
                createWeaknessHighlights(
                        dominantElement,
                        dominantTenGod,
                        strengthClassification,
                        weakestElement
                );

        String outerPersonality =
                createOuterPersonalityNarration(
                        personality,
                        dominantElement,
                        dominantTenGod
                );

        String innerPersonality =
                createInnerPersonalityNarration(
                        dominantElement,
                        dominantTenGod,
                        strengthClassification
                );

        String relationshipNarration =
                createRelationshipNarration(
                        dominantTenGod
                );

        String advice =
                createFinalAdvice(
                        usefulElement,
                        favorableElement,
                        unfavorableElement,
                        weakestElement,
                        health,
                        study
                );

        SajuYearForecastResponse strongestForecast =
                findStrongestForecast(
                        safeYearForecasts
                );

        List<SajuCinematicResponse.CinematicScene> scenes =
                new ArrayList<>();

        int order = 1;

        /*
         * 1. 오프닝
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.OPENING,
                        "DESTINY_GATE",
                        "CALM",
                        "FADE",
                        7,
                        profileName
                                + "님의 운명의 문이 열립니다",
                        profileName
                                + "님이 태어난 순간의 기운을 따라 "
                                + "타고난 성향과 성격, 현재의 큰 흐름, "
                                + "앞으로 기회가 강해지는 시기와 조심해야 할 월을 살펴보겠습니다.",
                        "만세력으로 풀어보는 당신의 이야기",
                        null,
                        List.of(
                                "만세력 원국",
                                "성향과 성격",
                                "현재 대운",
                                "연도별 미래 흐름",
                                "월별 영향 기간"
                        ),
                        List.of()
                )
        );

        /*
         * 2. 만세력
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.MANSE_CALENDAR,
                        "FOUR_PILLARS",
                        "SERIOUS",
                        "LIGHT_FLASH",
                        12,
                        "당신이 태어난 순간의 만세력",
                        "연주와 월주, 일주와 시주는 "
                                + "태어난 순간의 네 가지 기둥을 뜻합니다. "
                                + "이 네 기둥 안에 일간과 오행, 십성, 지장간, "
                                + "합과 충의 기본 구조가 담겨 있습니다.",
                        "사주의 네 기둥이 타고난 기본 구조를 보여줍니다.",
                        null,
                        manseHighlights.isEmpty()
                                ? List.of(
                                "연주",
                                "월주",
                                "일주",
                                "시주",
                                "오행과 십성"
                        )
                                : manseHighlights,
                        List.of(
                                "연주·월주·일주·시주",
                                "오행과 음양",
                                "십성과 지장간"
                        )
                )
        );

        /*
         * 3. 기본 기운
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.BASIC_ENERGY,
                        "CORE_ENERGY",
                        "CALM",
                        "ZOOM_IN",
                        12,
                        "타고난 기본 기운",
                        summary,
                        createBasicEnergySubtitle(
                                analysisResponse,
                                dominantElement,
                                weakestElement
                        ),
                        null,
                        createBasicEnergyHighlights(
                                analysisResponse,
                                dominantElement,
                                weakestElement,
                                strengthClassification,
                                usefulElement
                        ),
                        List.of(
                                "일간",
                                "오행 분포",
                                "신강·신약 보조 판정",
                                "용신·희신·기신"
                        )
                )
        );

        /*
         * 4. 겉 성격
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.PERSONALITY_OUTER,
                        "OUTER_PERSONALITY",
                        "SMILE",
                        "SLIDE_UP",
                        12,
                        "사람들에게 보이는 당신",
                        outerPersonality,
                        "사회생활에서 먼저 드러나는 태도와 행동 방식입니다.",
                        null,
                        createOuterPersonalityHighlights(
                                dominantElement,
                                dominantTenGod
                        ),
                        List.of(
                                "월주",
                                "오행의 우세 기운",
                                "십성 분포"
                        )
                )
        );

        /*
         * 5. 속마음
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.PERSONALITY_INNER,
                        "INNER_NATURE",
                        "SERIOUS",
                        "FADE",
                        12,
                        "겉으로 드러나지 않는 속마음",
                        innerPersonality,
                        "혼자 있을 때 나타나는 생각과 감정의 처리 방식입니다.",
                        null,
                        createInnerPersonalityHighlights(
                                dominantTenGod,
                                strengthClassification
                        ),
                        List.of(
                                "일주",
                                "일간의 세력",
                                "비겁·인성·식상 작용"
                        )
                )
        );

        /*
         * 6. 장점
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.STRENGTHS,
                        "STRENGTH_LIGHT",
                        "SMILE",
                        "LIGHT_FLASH",
                        11,
                        "당신이 가진 강점",
                        createListNarration(
                                strengths,
                                "당신의 장점은 한 가지 재능이 아니라 "
                                        + "타고난 기운을 현실에서 사용하는 방식에서 드러납니다."
                        ),
                        "강점을 알고 사용하면 같은 운도 더 좋은 방향으로 흐를 수 있습니다.",
                        null,
                        strengths,
                        List.of(
                                "우세 오행",
                                "주요 십성",
                                "신강·신약 보조 판정"
                        )
                )
        );

        /*
         * 7. 단점과 보완점
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.WEAKNESSES,
                        "SHADOW_WARNING",
                        "WARNING",
                        "FADE",
                        12,
                        "반복해서 조심해야 할 성향",
                        createListNarration(
                                weaknesses,
                                "강하게 드러나는 기운은 장점이 되기도 하지만 "
                                        + "지나치면 같은 실수를 반복하게 만들 수 있습니다."
                        ),
                        "단점을 없애기보다 반복되는 행동을 줄이는 것이 중요합니다.",
                        null,
                        weaknesses,
                        List.of(
                                "과다 오행",
                                "부족 오행",
                                "주요 십성",
                                "기신 보조 판정"
                        )
                )
        );

        /*
         * 8. 재물운
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.WEALTH,
                        "WEALTH_FLOW",
                        "CALM",
                        "SLIDE_UP",
                        12,
                        "돈이 들어오고 나가는 방식",
                        safeText(
                                wealth,
                                "재물운은 돈이 들어오는 힘만 보는 것이 아닙니다. "
                                        + "수입을 만드는 식상과 재성, 돈을 나누거나 빼앗길 수 있는 "
                                        + "비견과 겁재의 작용을 함께 살펴야 합니다."
                        ),
                        "버는 힘과 지키는 습관을 함께 살펴봅니다.",
                        null,
                        List.of(
                                "수입을 만드는 힘",
                                "지출과 분산 작용",
                                "계약과 금전 관계",
                                "재성을 감당하는 일간의 힘"
                        ),
                        List.of(
                                "식상",
                                "편재·정재",
                                "비견·겁재",
                                "현재 대운"
                        )
                )
        );

        /*
         * 9. 직업과 사업운
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.CAREER_BUSINESS,
                        "CAREER_PATH",
                        "SERIOUS",
                        "ZOOM_IN",
                        14,
                        "직업과 사업의 방향",
                        combineNarrations(
                                career,
                                business,
                                "직업운은 관성과 식상, 인성의 균형을 통해 살펴봅니다. "
                                        + "사업운은 추진력만으로 결정되지 않으며 재성과 비겁, "
                                        + "현재 대운의 흐름을 함께 확인해야 합니다."
                        ),
                        "어떤 역할과 환경에서 능력이 잘 드러나는지 살펴봅니다.",
                        null,
                        List.of(
                                "직업 적성",
                                "조직생활",
                                "전문성",
                                "사업과 독립",
                                "계약과 운영"
                        ),
                        List.of(
                                "식상",
                                "재성",
                                "관성",
                                "인성",
                                "현재 대운"
                        )
                )
        );

        /*
         * 10. 사람복
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.RELATIONSHIPS,
                        "RELATIONSHIP_THREADS",
                        "CALM",
                        "FADE",
                        12,
                        "사람이 붙는 방식과 사람복",
                        relationshipNarration,
                        "사람복은 관계의 수보다 관계의 질을 보는 것이 중요합니다.",
                        null,
                        List.of(
                                "협력 관계",
                                "경쟁 관계",
                                "사람과 돈의 경계",
                                "부탁과 책임의 구분"
                        ),
                        List.of(
                                "비견·겁재",
                                "합·충·형·파·해",
                                "원국의 인간관계 작용"
                        )
                )
        );

        /*
         * 11. 연애운
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.ROMANCE,
                        "ROMANCE_MOON",
                        "SMILE",
                        "SLIDE_UP",
                        12,
                        "연애와 마음의 표현",
                        safeText(
                                romance,
                                "연애에서는 좋아하는 마음을 표현하는 방식과 "
                                        + "서운함을 처리하는 방식이 중요하게 작용합니다. "
                                        + "배우자궁과 재성·관성, 식상의 흐름을 함께 살펴야 합니다."
                        ),
                        "감정 표현과 관계의 반복 패턴을 살펴봅니다.",
                        null,
                        List.of(
                                "감정 표현",
                                "관계의 주도권",
                                "서운함을 처리하는 방식",
                                "배우자궁의 작용"
                        ),
                        List.of(
                                "일지",
                                "재성·관성",
                                "식상",
                                "합과 충"
                        )
                )
        );

        /*
         * 12. 현재 대운
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.CURRENT_DAEUN,
                        "DAEUN_RIVER",
                        "SERIOUS",
                        "ZOOM_IN",
                        14,
                        "지금 지나고 있는 큰 운",
                        currentDaeun,
                        "대운은 약 10년 동안 이어지는 인생의 큰 계절입니다.",
                        null,
                        createCurrentDaeunHighlights(
                                analysisResponse
                        ),
                        List.of(
                                "현재 대운",
                                "대운의 천간과 지지",
                                "원국과 대운의 관계",
                                "대운의 십성"
                        )
                )
        );

        /*
         * 13. 미래 전체 흐름
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.FUTURE_OVERVIEW,
                        "FUTURE_PATH",
                        "CALM",
                        "FADE",
                        13,
                        "앞으로 펼쳐질 큰 흐름",
                        createFutureOverviewNarration(
                                safeYearForecasts
                        ),
                        "연도별 흐름과 월 단위 영향 기간을 함께 살펴봅니다.",
                        null,
                        safeYearForecasts.stream()
                                .limit(
                                        5
                                )
                                .map(
                                        forecast ->
                                                forecast.year()
                                                        + "년 · "
                                                        + forecast.theme()
                                )
                                .toList(),
                        collectForecastEvidenceDescriptions(
                                safeYearForecasts,
                                5
                        )
                )
        );

        /*
         * 14. 앞으로 3개 연도 영상 장면
         */
        for (
                SajuYearForecastResponse forecast
                : safeYearForecasts.stream()
                .limit(
                        3
                )
                .toList()
        ) {
            scenes.add(
                    createScene(
                            order++,
                            SajuCinematicResponse.SceneType.YEAR_FORECAST,
                            selectYearVisualKey(
                                    forecast
                            ),
                            forecast.scores()
                                    .caution()
                                    >= 70
                                    ? "WARNING"
                                    : "SERIOUS",
                            "SLIDE_UP",
                            14,
                            forecast.year()
                                    + "년의 흐름",
                            forecast.summary(),
                            forecast.headline(),
                            convertPeriod(
                                    forecast.influencePeriod()
                            ),
                            createYearHighlights(
                                    forecast
                            ),
                            forecast.evidences()
                                    .stream()
                                    .map(
                                            SajuMonthForecastResponse
                                                    .ForecastEvidence::description
                                    )
                                    .filter(
                                            Objects::nonNull
                                    )
                                    .limit(
                                            5
                                    )
                                    .toList()
                    )
            );
        }

        /*
         * 15. 가장 강한 영향 기간
         */
        if (
                strongestForecast != null
        ) {
            scenes.add(
                    createScene(
                            order++,
                            SajuCinematicResponse.SceneType.MONTH_PERIOD,
                            "MONTHLY_TIMELINE",
                            "SERIOUS",
                            "ZOOM_IN",
                            15,
                            "가장 강하게 작용하는 월",
                            createPeakPeriodNarration(
                                    strongestForecast
                            ),
                            strongestForecast
                                    .influencePeriod()
                                    .description(),
                            convertPeriod(
                                    strongestForecast
                                            .influencePeriod()
                            ),
                            createPeakMonthHighlights(
                                    strongestForecast
                            ),
                            strongestForecast.evidences()
                                    .stream()
                                    .map(
                                            SajuMonthForecastResponse
                                                    .ForecastEvidence::description
                                    )
                                    .filter(
                                            Objects::nonNull
                                    )
                                    .limit(
                                            6
                                    )
                                    .toList()
                    )
            );

            /*
             * 16. 조심해야 할 기간
             */
            scenes.add(
                    createScene(
                            order++,
                            SajuCinematicResponse.SceneType.CAUTION_PERIOD,
                            "CAUTION_RED_MOON",
                            "WARNING",
                            "LIGHT_FLASH",
                            14,
                            "이 시기에는 무엇을 조심해야 할까",
                            createCautionNarration(
                                    strongestForecast
                            ),
                            "특정 사건을 단정하는 것이 아니라 반복되기 쉬운 실수를 미리 줄이는 해설입니다.",
                            convertPeriod(
                                    strongestForecast
                                            .influencePeriod()
                            ),
                            limitList(
                                    strongestForecast.cautions(),
                                    6
                            ),
                            strongestForecast.evidences()
                                    .stream()
                                    .map(
                                            SajuMonthForecastResponse
                                                    .ForecastEvidence::description
                                    )
                                    .filter(
                                            Objects::nonNull
                                    )
                                    .limit(
                                            5
                                    )
                                    .toList()
                    )
            );

            /*
             * 17. 기회 활용 기간
             */
            scenes.add(
                    createScene(
                            order++,
                            SajuCinematicResponse.SceneType.OPPORTUNITY_PERIOD,
                            "OPPORTUNITY_LIGHT",
                            "SMILE",
                            "LIGHT_FLASH",
                            13,
                            "좋게 활용하면 기회가 되는 부분",
                            createOpportunityNarration(
                                    strongestForecast
                            ),
                            "운은 기다리는 것보다 현실적인 행동으로 활용하는 것이 중요합니다.",
                            convertPeriod(
                                    strongestForecast
                                            .influencePeriod()
                            ),
                            limitList(
                                    strongestForecast.opportunities(),
                                    6
                            ),
                            List.of(
                                    "집중 작용월의 장점을 현실적인 행동으로 연결합니다."
                            )
                    )
            );
        }

        /*
         * 18. 현실 조언
         */
        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.ADVICE,
                        "FINAL_ADVICE",
                        "SMILE",
                        "FADE",
                        14,
                        profileName
                                + "님에게 필요한 현실적인 조언",
                        advice,
                        "좋은 운은 준비해서 사용하고 주의 운은 반복되는 실수를 줄이는 데 활용합니다.",
                        null,
                        createAdviceHighlights(
                                usefulElement,
                                favorableElement,
                                unfavorableElement,
                                weakestElement
                        ),
                        List.of(
                                "원국",
                                "신강·신약",
                                "용신·희신·기신",
                                "현재 대운",
                                "세운과 월운"
                        )
                )
        );

        /*
         * 19. 해석 근거
         */
        scenes.add(
                createScene(
                        order,
                        SajuCinematicResponse.SceneType.EVIDENCE,
                        "SECRET_LEDGER",
                        "CALM",
                        "SLIDE_UP",
                        11,
                        "해석에 사용된 명리학적 근거",
                        "이 해설은 신살 하나나 특정 숫자 하나만으로 판단하지 않습니다. "
                                + "만세력 원국과 오행, 십성, 신강·신약 보조 판정, "
                                + "합·충·형·파·해, 현재 대운, 세운과 월운에서 "
                                + "같은 작용이 얼마나 반복되는지를 함께 살펴봅니다.",
                        "신살은 보조 근거로 사용하고 원국과 운의 반복 작용을 우선합니다.",
                        null,
                        mergeStringLists(
                                manseHighlights,
                                collectForecastEvidenceDescriptions(
                                        safeYearForecasts,
                                        8
                                )
                        ),
                        List.of(
                                "원국",
                                "오행",
                                "십성",
                                "신강·신약",
                                "용신·희신·기신",
                                "대운",
                                "세운",
                                "월운",
                                "합·충·형·파·해",
                                "신살 보조 해석"
                        )
                )
        );

        int totalDurationSeconds =
                scenes.stream()
                        .mapToInt(
                                SajuCinematicResponse
                                        .CinematicScene::durationSeconds
                        )
                        .sum();

        return new SajuCinematicResponse(
                analysisId,
                profileId,
                profileName
                        + "님의 천명 해설",
                "만세력으로 보는 타고난 성향과 앞으로의 흐름",
                totalDurationSeconds,
                scenes,
                "이 영상형 해설은 만세력과 명리학적 계산을 바탕으로 한 참고 분석입니다. "
                        + "미래의 특정 사건이나 결과를 확정하지 않으며, "
                        + "몇 년도 몇 월에 어떤 작용과 선택 가능성이 강해질 수 있는지를 설명합니다."
        );
    }

    /**
     * 영상 장면 객체를 생성한다.
     */
    private SajuCinematicResponse.CinematicScene createScene(
            int order,
            SajuCinematicResponse.SceneType sceneType,
            String visualKey,
            String characterExpressionKey,
            String transitionKey,
            int durationSeconds,
            String title,
            String narration,
            String subtitle,
            SajuCinematicResponse.CinematicPeriod period,
            List<String> highlights,
            List<String> evidences
    ) {
        return new SajuCinematicResponse.CinematicScene(
                order,
                sceneType,
                safeText(
                        visualKey,
                        "DEFAULT_SCENE"
                ),
                safeText(
                        characterExpressionKey,
                        "CALM"
                ),
                safeText(
                        transitionKey,
                        "FADE"
                ),
                Math.max(
                        durationSeconds,
                        1
                ),
                normalizeNarration(
                        title
                ),
                normalizeNarration(
                        narration
                ),
                normalizeNarration(
                        subtitle
                ),
                period == null
                        ? SajuCinematicResponse
                        .CinematicPeriod
                        .empty()
                        : period,
                safeList(
                        highlights
                ),
                safeList(
                        evidences
                )
        );
    }

    /**
     * 해석 목록에서 특정 분야의 설명을 찾는다.
     */
    private String findInterpretation(
            SajuAnalysisResponse response,
            String category
    ) {
        if (
                response == null
                        || response.interpretations() == null
        ) {
            return null;
        }

        return response.interpretations()
                .stream()
                .filter(
                        Objects::nonNull
                )
                .filter(
                        interpretation ->
                                Objects.equals(
                                        category,
                                        interpretation.category()
                                )
                )
                .map(
                        InterpretationResponse::content
                )
                .filter(
                        this::notBlank
                )
                .findFirst()
                .orElse(
                        null
                );
    }

    /**
     * 만세력 핵심 정보를 영상 화면용 목록으로 만든다.
     */
    private List<String> extractManseHighlights(
            SajuAnalysisResponse response
    ) {
        List<String> result =
                new ArrayList<>();

        if (
                response.pillars() != null
        ) {
            for (
                    PillarResponse pillar
                    : response.pillars()
            ) {
                if (
                        pillar == null
                                || !pillar.known()
                ) {
                    continue;
                }

                StringBuilder builder =
                        new StringBuilder();

                builder.append(
                        pillar.type()
                );

                builder.append(
                        ": "
                );

                builder.append(
                        safeText(
                                pillar.ganZhiKorean(),
                                pillar.ganZhi()
                        )
                );

                if (
                        notBlank(
                                pillar.ganZhi()
                        )
                ) {
                    builder.append(
                            " ("
                    );

                    builder.append(
                            pillar.ganZhi()
                    );

                    builder.append(
                            ")"
                    );
                }

                if (
                        notBlank(
                                pillar.stemTenGod()
                        )
                ) {
                    builder.append(
                            " · "
                    );

                    builder.append(
                            pillar.stemTenGod()
                    );
                }

                if (
                        notBlank(
                                pillar.twelveStage()
                        )
                ) {
                    builder.append(
                            " · "
                    );

                    builder.append(
                            pillar.twelveStage()
                    );
                }

                result.add(
                        builder.toString()
                );
            }
        }

        if (
                notBlank(
                        response.dayMaster()
                )
        ) {
            result.add(
                    "일간: "
                            + response.dayMaster()
            );
        }

        if (
                response.elementBalance() != null
        ) {
            result.add(
                    "우세 오행: "
                            + safeText(
                            response.elementBalance()
                                    .dominant(),
                            "확인 필요"
                    )
            );

            result.add(
                    "부족 오행: "
                            + safeText(
                            response.elementBalance()
                                    .weakest(),
                            "확인 필요"
                    )
            );
        }

        if (
                response.strength() != null
        ) {
            result.add(
                    "신강·신약: "
                            + safeText(
                            response.strength()
                                    .classification(),
                            "확인 필요"
                    )
            );

            result.add(
                    "용신: "
                            + safeText(
                            response.strength()
                                    .usefulGod(),
                            "확인 필요"
                    )
            );
        }

        return result.stream()
                .filter(
                        this::notBlank
                )
                .distinct()
                .limit(
                        10
                )
                .toList();
    }

    /**
     * 타고난 기본 기운 자막을 만든다.
     */
    private String createBasicEnergySubtitle(
            SajuAnalysisResponse response,
            String dominantElement,
            String weakestElement
    ) {
        String dayMaster =
                safeText(
                        response.dayMaster(),
                        "일간"
                );

        return dayMaster
                + "을 중심으로 "
                + safeText(
                dominantElement,
                "우세 오행"
        )
                + " 기운이 두드러지고 "
                + safeText(
                weakestElement,
                "부족 오행"
        )
                + " 기운이 상대적으로 적게 나타납니다.";
    }

    private List<String> createBasicEnergyHighlights(
            SajuAnalysisResponse response,
            String dominantElement,
            String weakestElement,
            String strengthClassification,
            String usefulElement
    ) {
        List<String> values =
                new ArrayList<>();

        addValue(
                values,
                "일간",
                response.dayMaster()
        );

        addValue(
                values,
                "우세 오행",
                dominantElement
        );

        addValue(
                values,
                "부족 오행",
                weakestElement
        );

        addValue(
                values,
                "신강·신약",
                strengthClassification
        );

        addValue(
                values,
                "용신",
                usefulElement
        );

        return List.copyOf(
                values
        );
    }

    /**
     * 겉으로 보이는 성격 설명.
     */
    private String createOuterPersonalityNarration(
            String personality,
            String dominantElement,
            String dominantTenGod
    ) {
        if (
                notBlank(
                        personality
                )
        ) {
            return personality;
        }

        return "사람들에게는 "
                + elementOuterPersonality(
                dominantElement
        )
                + " 모습이 먼저 드러날 수 있습니다. "
                + tenGodOuterPersonality(
                dominantTenGod
        );
    }

    /**
     * 실제 속마음 설명.
     */
    private String createInnerPersonalityNarration(
            String dominantElement,
            String dominantTenGod,
            String strengthClassification
    ) {
        String strengthText =
                switch (
                        safeText(
                                strengthClassification,
                                ""
                        )
                        ) {
                    case "신강" ->
                            "자신의 판단을 쉽게 바꾸지 않고 스스로 해결하려는 마음이 강할 수 있습니다.";

                    case "신약" ->
                            "겉으로는 침착해 보여도 주변의 평가와 상황을 세밀하게 의식할 수 있습니다.";

                    case "중화" ->
                            "상황에 따라 주도적으로 움직이기도 하고 다른 사람의 의견을 받아들이기도 합니다.";

                    default ->
                            "중요한 문제는 혼자 오래 생각한 뒤 결론을 내리는 편일 수 있습니다.";
                };

        return elementInnerPersonality(
                dominantElement
        )
                + " "
                + tenGodInnerPersonality(
                dominantTenGod
        )
                + " "
                + strengthText;
    }

    private List<String> createOuterPersonalityHighlights(
            String dominantElement,
            String dominantTenGod
    ) {
        List<String> values =
                new ArrayList<>();

        values.add(
                elementKeyword(
                        dominantElement
                )
        );

        values.add(
                tenGodKeyword(
                        dominantTenGod
                )
        );

        values.add(
                "사회적인 태도"
        );

        values.add(
                "책임을 대하는 방식"
        );

        return values.stream()
                .filter(
                        this::notBlank
                )
                .distinct()
                .toList();
    }

    private List<String> createInnerPersonalityHighlights(
            String dominantTenGod,
            String strengthClassification
    ) {
        List<String> values =
                new ArrayList<>();

        addValue(
                values,
                "주요 십성",
                dominantTenGod
        );

        addValue(
                values,
                "일간 세력",
                strengthClassification
        );

        values.add(
                "감정을 처리하는 방식"
        );

        values.add(
                "혼자 품는 고민"
        );

        return List.copyOf(
                values
        );
    }

    /**
     * 장점 목록을 만든다.
     */
    private List<String> createStrengthHighlights(
            String dominantElement,
            String dominantTenGod,
            String strengthClassification
    ) {
        Set<String> result =
                new LinkedHashSet<>();

        result.addAll(
                elementStrengths(
                        dominantElement
                )
        );

        result.addAll(
                tenGodStrengths(
                        dominantTenGod
                )
        );

        if (
                "신강".equals(
                        strengthClassification
                )
        ) {
            result.add(
                    "추진력이 강함"
            );

            result.add(
                    "위기에서 쉽게 물러서지 않음"
            );

        } else if (
                "신약".equals(
                        strengthClassification
                )
        ) {
            result.add(
                    "주변 상황을 세밀하게 파악함"
            );

            result.add(
                    "협력과 조율 능력이 있음"
            );

        } else {
            result.add(
                    "상황에 맞춰 균형을 조절함"
            );
        }

        return result.stream()
                .filter(
                        this::notBlank
                )
                .limit(
                        7
                )
                .toList();
    }

    /**
     * 단점과 보완점 목록을 만든다.
     */
    private List<String> createWeaknessHighlights(
            String dominantElement,
            String dominantTenGod,
            String strengthClassification,
            String weakestElement
    ) {
        Set<String> result =
                new LinkedHashSet<>();

        result.addAll(
                elementWeaknesses(
                        dominantElement
                )
        );

        result.addAll(
                tenGodWeaknesses(
                        dominantTenGod
                )
        );

        if (
                "신강".equals(
                        strengthClassification
                )
        ) {
            result.add(
                    "자신의 판단만 고집하지 않기"
            );

            result.add(
                    "모든 책임을 혼자 떠안지 않기"
            );

        } else if (
                "신약".equals(
                        strengthClassification
                )
        ) {
            result.add(
                    "주변 반응에 지나치게 흔들리지 않기"
            );

            result.add(
                    "결정을 계속 미루지 않기"
            );
        }

        if (
                notBlank(
                        weakestElement
                )
        ) {
            result.add(
                    weakestElement
                            + " 기운을 생활 습관과 환경으로 보완하기"
            );
        }

        return result.stream()
                .filter(
                        this::notBlank
                )
                .limit(
                        7
                )
                .toList();
    }

    /**
     * 사람복 설명.
     */
    private String createRelationshipNarration(
            String dominantTenGod
    ) {
        String base =
                "사람복은 단순히 주변에 사람이 많은지를 보는 것이 아닙니다. "
                        + "누가 도움을 주는지, 누가 책임과 돈을 함께 요구하는지, "
                        + "어떤 관계에서 기운이 빠지는지를 함께 봐야 합니다. ";

        return base
                + switch (
                safeText(
                        dominantTenGod,
                        ""
                )
                ) {
            case "비견" ->
                    "대등한 사람과 잘 어울리지만 주도권 경쟁이 생기지 않도록 역할을 분명히 하는 것이 좋습니다.";

            case "겁재" ->
                    "사람이 쉽게 붙을 수 있지만 친분과 돈 문제를 섞으면 손해가 커질 수 있습니다.";

            case "식신" ->
                    "편안하게 대화하고 챙겨주는 관계에서 좋은 인연이 이어질 가능성이 큽니다.";

            case "상관" ->
                    "솔직한 표현이 장점이지만 말이 너무 강해 상대가 부담을 느끼지 않도록 조절해야 합니다.";

            case "편재" ->
                    "활동적인 사람과 인연이 넓어질 수 있지만 관계가 많아질수록 지출과 책임도 함께 늘 수 있습니다.";

            case "정재" ->
                    "약속을 지키고 현실적인 신뢰를 보여주는 사람과 관계가 오래 이어질 수 있습니다.";

            case "편관" ->
                    "강한 사람이나 책임이 큰 관계가 붙을 수 있으므로 일방적으로 끌려가지 않는 것이 중요합니다.";

            case "정관" ->
                    "예의와 책임을 지키는 관계를 선호하며 기준이 분명한 사람과 잘 맞을 수 있습니다.";

            case "편인" ->
                    "독특한 생각과 전문성을 가진 사람과 연결될 수 있지만 혼자 판단하고 거리를 두지 않도록 주의해야 합니다.";

            case "정인" ->
                    "정서적으로 이해해주고 안정감을 주는 사람에게 신뢰를 느끼는 편일 수 있습니다.";

            default ->
                    "친분과 책임, 금전 관계를 구분하면 좋은 관계를 오래 유지하는 데 도움이 됩니다.";
        };
    }

    /**
     * 현재 대운 설명.
     */
    private String createCurrentDaeunNarration(
            SajuAnalysisResponse response
    ) {
        if (
                response.luck() == null
        ) {
            return "현재 대운 정보를 확인할 수 없습니다. "
                    + "대운은 약 10년 동안 이어지는 큰 흐름이므로 "
                    + "원국과 세운, 월운을 함께 살펴야 합니다.";
        }

        String currentDecade =
                safeText(
                        response.luck()
                                .currentDecade(),
                        "현재 대운"
                );

        String direction =
                safeText(
                        response.luck()
                                .direction(),
                        "대운 흐름"
                );

        String note =
                safeText(
                        response.luck()
                                .note(),
                        "대운과 원국의 오행·십성 관계를 함께 살펴야 합니다."
                );

        return "현재는 "
                + currentDecade
                + "의 흐름을 지나고 있습니다. "
                + direction
                + " 방식으로 대운이 이어지며, "
                + "이 큰 흐름 위에 매년의 세운과 매월의 월운이 겹쳐 현실의 변화가 나타납니다. "
                + note;
    }

    private List<String> createCurrentDaeunHighlights(
            SajuAnalysisResponse response
    ) {
        if (
                response.luck() == null
        ) {
            return List.of(
                    "현재 대운 확인 필요"
            );
        }

        List<String> result =
                new ArrayList<>();

        addValue(
                result,
                "현재 대운",
                response.luck()
                        .currentDecade()
        );

        addValue(
                result,
                "대운 방향",
                response.luck()
                        .direction()
        );

        addValue(
                result,
                "대운 시작",
                response.luck()
                        .startAge()
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 미래 전체 흐름 내레이션.
     */
    private String createFutureOverviewNarration(
            List<SajuYearForecastResponse> forecasts
    ) {
        if (
                forecasts == null
                        || forecasts.isEmpty()
        ) {
            return "앞으로의 흐름은 현재 대운과 매년 들어오는 세운, "
                    + "그리고 절기 기준 월운을 함께 계산해 살펴봅니다.";
        }

        StringBuilder builder =
                new StringBuilder();

        builder.append(
                "앞으로의 흐름은 한 해의 운만으로 결정되지 않습니다. "
        );

        for (
                SajuYearForecastResponse forecast
                : forecasts.stream()
                .limit(
                        4
                )
                .toList()
        ) {
            builder.append(
                    forecast.year()
            );

            builder.append(
                    "년에는 "
            );

            builder.append(
                    forecast.theme()
            );

            builder.append(
                    " 작용이 두드러질 수 있습니다. "
            );
        }

        builder.append(
                "각 해의 전조월과 집중 작용월, 잔여 영향월을 함께 확인하는 것이 중요합니다."
        );

        return builder.toString();
    }

    /**
     * 가장 강한 영향 기간 설명.
     */
    private String createPeakPeriodNarration(
            SajuYearForecastResponse forecast
    ) {
        if (
                forecast == null
                        || forecast.influencePeriod() == null
        ) {
            return "집중 작용 기간을 확인할 수 없습니다.";
        }

        SajuYearForecastResponse.InfluencePeriod period =
                forecast.influencePeriod();

        return formatYearMonth(
                period.startYearMonth()
        )
                + "부터 "
                + forecast.theme()
                + "과 관련된 움직임이 서서히 나타날 수 있습니다. "
                + formatYearMonth(
                period.peakStartYearMonth()
        )
                + "부터 "
                + formatYearMonth(
                period.peakEndYearMonth()
        )
                + " 사이에 같은 작용이 가장 강하게 겹칠 가능성이 있으며, "
                + formatYearMonth(
                period.endYearMonth()
        )
                + "까지 결과 정리와 후속 흐름이 이어질 수 있습니다.";
    }

    private List<String> createPeakMonthHighlights(
            SajuYearForecastResponse forecast
    ) {
        if (
                forecast == null
                        || forecast.influencePeriod() == null
        ) {
            return List.of();
        }

        SajuYearForecastResponse.InfluencePeriod period =
                forecast.influencePeriod();

        List<String> result =
                new ArrayList<>();

        result.add(
                "전체 영향 기간: "
                        + formatYearMonth(
                        period.startYearMonth()
                )
                        + " ~ "
                        + formatYearMonth(
                        period.endYearMonth()
                )
        );

        result.add(
                "집중 작용 기간: "
                        + formatYearMonth(
                        period.peakStartYearMonth()
                )
                        + " ~ "
                        + formatYearMonth(
                        period.peakEndYearMonth()
                )
        );

        result.add(
                "핵심 주제: "
                        + forecast.theme()
        );

        result.add(
                "작용 강도: "
                        + forecast.confidence()
        );

        result.add(
                "근거 점수: "
                        + forecast.confidenceScore()
        );

        return List.copyOf(
                result
        );
    }

    private List<String> createYearHighlights(
            SajuYearForecastResponse forecast
    ) {
        List<String> result =
                new ArrayList<>();

        result.add(
                "핵심 주제: "
                        + safeText(
                        forecast.theme(),
                        "종합 흐름"
                )
        );

        if (
                forecast.influencePeriod() != null
        ) {
            result.add(
                    "영향 기간: "
                            + formatYearMonth(
                            forecast.influencePeriod()
                                    .startYearMonth()
                    )
                            + " ~ "
                            + formatYearMonth(
                            forecast.influencePeriod()
                                    .endYearMonth()
                    )
            );

            result.add(
                    "집중 기간: "
                            + formatYearMonth(
                            forecast.influencePeriod()
                                    .peakStartYearMonth()
                    )
                            + " ~ "
                            + formatYearMonth(
                            forecast.influencePeriod()
                                    .peakEndYearMonth()
                    )
            );
        }

        result.add(
                "직업 작용: "
                        + forecast.scores()
                        .career()
        );

        result.add(
                "재물 작용: "
                        + forecast.scores()
                        .wealth()
        );

        result.add(
                "변화 작용: "
                        + forecast.scores()
                        .change()
        );

        result.add(
                "주의 작용: "
                        + forecast.scores()
                        .caution()
        );

        return result.stream()
                .limit(
                        7
                )
                .toList();
    }

    /**
     * 조심해야 할 기간 설명.
     */
    private String createCautionNarration(
            SajuYearForecastResponse forecast
    ) {
        if (
                forecast == null
                        || forecast.cautions() == null
                        || forecast.cautions()
                        .isEmpty()
        ) {
            return "이 시기에는 감정적인 결정과 확인하지 않은 계약, "
                    + "무리한 지출과 서두른 이동을 줄이는 것이 좋습니다.";
        }

        return "이 기간에는 "
                + String.join(
                ", ",
                forecast.cautions()
                        .stream()
                        .filter(
                                this::notBlank
                        )
                        .limit(
                                4
                        )
                        .toList()
        )
                + " 같은 행동을 조심하는 것이 좋습니다. "
                + "반드시 나쁜 일이 생긴다는 뜻이 아니라, "
                + "원국·대운·세운·월운에서 반복되는 실수를 미리 줄이라는 의미입니다.";
    }

    /**
     * 기회 활용 기간 설명.
     */
    private String createOpportunityNarration(
            SajuYearForecastResponse forecast
    ) {
        if (
                forecast == null
                        || forecast.opportunities() == null
                        || forecast.opportunities()
                        .isEmpty()
        ) {
            return "기회가 강해지는 시기에는 준비된 행동과 현실적인 계획이 중요합니다.";
        }

        return "이 시기를 좋게 활용하려면 "
                + String.join(
                ", ",
                forecast.opportunities()
                        .stream()
                        .filter(
                                this::notBlank
                        )
                        .limit(
                                4
                        )
                        .toList()
        )
                + " 같은 행동이 도움이 됩니다. "
                + "운의 흐름을 실제 결과로 연결하려면 준비와 확인이 함께 필요합니다.";
    }

    /**
     * 최종 현실 조언.
     */
    private String createFinalAdvice(
            String usefulElement,
            String favorableElement,
            String unfavorableElement,
            String weakestElement,
            String health,
            String study
    ) {
        StringBuilder builder =
                new StringBuilder();

        builder.append(
                "좋은 운이 들어와도 준비가 없으면 기회가 지나갈 수 있고, "
                        + "주의 운이 들어와도 미리 확인하면 큰 문제 없이 넘어갈 수 있습니다. "
        );

        if (
                notBlank(
                        usefulElement
                )
        ) {
            builder.append(
                    "용신으로 분류된 "
            );

            builder.append(
                    usefulElement
            );

            builder.append(
                    " 기운을 생활환경과 일의 방식에 활용하는 것이 도움이 됩니다. "
            );
        }

        if (
                notBlank(
                        favorableElement
                )
        ) {
            builder.append(
                    "희신인 "
            );

            builder.append(
                    favorableElement
            );

            builder.append(
                    " 기운은 보완 방향으로 활용하고, "
            );
        }

        if (
                notBlank(
                        unfavorableElement
                )
        ) {
            builder.append(
                    "기신으로 분류된 "
            );

            builder.append(
                    unfavorableElement
            );

            builder.append(
                    " 기운이 강해지는 시기에는 무리한 확장과 감정적인 결정을 줄이는 것이 좋습니다. "
            );
        }

        if (
                notBlank(
                        weakestElement
                )
        ) {
            builder.append(
                    "상대적으로 부족한 "
            );

            builder.append(
                    weakestElement
            );

            builder.append(
                    " 기운은 생활 습관과 환경을 통해 균형을 맞추는 편이 좋습니다. "
            );
        }

        if (
                notBlank(
                        health
                )
        ) {
            builder.append(
                    health
            );

            builder.append(
                    " "
            );
        }

        if (
                notBlank(
                        study
                )
        ) {
            builder.append(
                    study
            );

            builder.append(
                    " "
            );
        }

        builder.append(
                "계약과 돈, 사람 관계에서는 감정적인 판단보다 기록과 재확인을 우선하는 것이 좋습니다."
        );

        return builder.toString();
    }

    private List<String> createAdviceHighlights(
            String usefulElement,
            String favorableElement,
            String unfavorableElement,
            String weakestElement
    ) {
        List<String> result =
                new ArrayList<>();

        addValue(
                result,
                "용신 활용",
                usefulElement
        );

        addValue(
                result,
                "희신 보완",
                favorableElement
        );

        addValue(
                result,
                "기신 주의",
                unfavorableElement
        );

        addValue(
                result,
                "부족 오행 보완",
                weakestElement
        );

        result.add(
                "중요한 계약은 다시 확인하기"
        );

        result.add(
                "사람과 돈 문제를 구분하기"
        );

        result.add(
                "감정적인 결정을 하루 미루기"
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 가장 작용이 강한 연도를 찾는다.
     */
    private SajuYearForecastResponse findStrongestForecast(
            List<SajuYearForecastResponse> forecasts
    ) {
        if (
                forecasts == null
                        || forecasts.isEmpty()
        ) {
            return null;
        }

        return forecasts.stream()
                .filter(
                        Objects::nonNull
                )
                .max(
                        Comparator.comparingInt(
                                this::forecastStrength
                        )
                )
                .orElse(
                        null
                );
    }

    private int forecastStrength(
            SajuYearForecastResponse forecast
    ) {
        if (
                forecast == null
                        || forecast.scores() == null
        ) {
            return 0;
        }

        SajuMonthForecastResponse.ForecastScores scores =
                forecast.scores();

        int maximum =
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
                                        Math.max(
                                                scores.romance(),
                                                scores.change()
                                        ),
                                        scores.caution()
                                )
                        )
                );

        return maximum
                + forecast.confidenceScore()
                / 4;
    }

    private String selectYearVisualKey(
            SajuYearForecastResponse forecast
    ) {
        if (
                forecast == null
        ) {
            return "YEAR_FLOW";
        }

        return switch (
                safeText(
                        forecast.theme(),
                        ""
                )
                ) {
            case "직업과 책임" ->
                    "CAREER_YEAR_FLOW";

            case "재물과 계약" ->
                    "WEALTH_YEAR_FLOW";

            case "사업과 독립" ->
                    "BUSINESS_YEAR_FLOW";

            case "사람과 관계" ->
                    "RELATIONSHIP_YEAR_FLOW";

            case "연애와 감정" ->
                    "ROMANCE_YEAR_FLOW";

            case "이동과 변화" ->
                    "CHANGE_YEAR_FLOW";

            case "주의와 정리" ->
                    "CAUTION_YEAR_FLOW";

            default ->
                    "YEAR_FLOW";
        };
    }

    /**
     * 미래 운세의 근거 문장들을 추출한다.
     */
    private List<String> collectForecastEvidenceDescriptions(
            List<SajuYearForecastResponse> forecasts,
            int limit
    ) {
        Set<String> values =
                new LinkedHashSet<>();

        if (
                forecasts == null
        ) {
            return List.of();
        }

        for (
                SajuYearForecastResponse forecast
                : forecasts
        ) {
            if (
                    forecast == null
                            || forecast.evidences() == null
            ) {
                continue;
            }

            for (
                    SajuMonthForecastResponse.ForecastEvidence evidence
                    : forecast.evidences()
            ) {
                if (
                        evidence != null
                                && notBlank(
                                evidence.description()
                        )
                ) {
                    values.add(
                            evidence.description()
                    );
                }

                if (
                        values.size()
                                >= limit
                ) {
                    break;
                }
            }

            if (
                    values.size()
                            >= limit
            ) {
                break;
            }
        }

        return values.stream()
                .limit(
                        limit
                )
                .toList();
    }

    /**
     * 연도 영향 기간을 영상용 기간 DTO로 변환한다.
     */
    private SajuCinematicResponse.CinematicPeriod convertPeriod(
            SajuYearForecastResponse.InfluencePeriod source
    ) {
        if (
                source == null
        ) {
            return SajuCinematicResponse
                    .CinematicPeriod
                    .empty();
        }

        return new SajuCinematicResponse.CinematicPeriod(
                source.startYearMonth(),
                source.peakStartYearMonth(),
                source.peakEndYearMonth(),
                source.endYearMonth(),
                formatYearMonth(
                        source.startYearMonth()
                )
                        + "부터 "
                        + formatYearMonth(
                        source.endYearMonth()
                )
                        + "까지"
        );
    }

    private String createListNarration(
            List<String> values,
            String fallback
    ) {
        List<String> safeValues =
                limitList(
                        values,
                        6
                );

        if (
                safeValues.isEmpty()
        ) {
            return fallback;
        }

        return String.join(
                ", ",
                safeValues
        )
                + " 같은 특징이 두드러질 수 있습니다.";
    }

    private String combineNarrations(
            String first,
            String second,
            String fallback
    ) {
        boolean hasFirst =
                notBlank(
                        first
                );

        boolean hasSecond =
                notBlank(
                        second
                );

        if (
                hasFirst
                        && hasSecond
        ) {
            return first
                    + " "
                    + second;
        }

        if (
                hasFirst
        ) {
            return first;
        }

        if (
                hasSecond
        ) {
            return second;
        }

        return fallback;
    }

    private String formatYearMonth(
            YearMonth value
    ) {
        if (
                value == null
        ) {
            return "기간 미정";
        }

        return value.getYear()
                + "년 "
                + value.getMonthValue()
                + "월";
    }

    private void addValue(
            List<String> values,
            String label,
            String value
    ) {
        if (
                !notBlank(
                        value
                )
        ) {
            return;
        }

        values.add(
                label
                        + ": "
                        + value
        );
    }

    private List<String> limitList(
            List<String> values,
            int limit
    ) {
        if (
                values == null
                        || values.isEmpty()
        ) {
            return List.of();
        }

        return values.stream()
                .filter(
                        this::notBlank
                )
                .map(
                        value ->
                                shorten(
                                        value,
                                        150
                                )
                )
                .distinct()
                .limit(
                        limit
                )
                .toList();
    }

    private List<String> mergeStringLists(
            List<String> first,
            List<String> second
    ) {
        Set<String> result =
                new LinkedHashSet<>();

        if (
                first != null
        ) {
            result.addAll(
                    first
            );
        }

        if (
                second != null
        ) {
            result.addAll(
                    second
            );
        }

        return result.stream()
                .filter(
                        this::notBlank
                )
                .limit(
                        12
                )
                .toList();
    }

    private <T> List<T> safeList(
            List<T> values
    ) {
        return values == null
                ? List.of()
                : values.stream()
                .filter(
                        Objects::nonNull
                )
                .toList();
    }

    private String normalizeNarration(
            String value
    ) {
        if (
                value == null
        ) {
            return "";
        }

        return value
                .replace(
                        "\r",
                        " "
                )
                .replace(
                        "\n",
                        " "
                )
                .replaceAll(
                        "\\s{2,}",
                        " "
                )
                .trim();
    }

    private String shorten(
            String value,
            int maximumLength
    ) {
        if (
                value == null
        ) {
            return null;
        }

        String normalized =
                normalizeNarration(
                        value
                );

        if (
                normalized.length()
                        <= maximumLength
        ) {
            return normalized;
        }

        return normalized.substring(
                0,
                Math.max(
                        maximumLength - 1,
                        0
                )
        )
                + "…";
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

    private void validateInput(
            SajuAnalysis analysis,
            SajuAnalysisResponse analysisResponse
    ) {
        if (
                analysis == null
        ) {
            throw new IllegalArgumentException(
                    "사주 분석 정보가 없습니다."
            );
        }

        if (
                analysis.getBirthProfile() == null
        ) {
            throw new IllegalArgumentException(
                    "사주 기본정보가 없습니다."
            );
        }

        if (
                analysisResponse == null
        ) {
            throw new IllegalArgumentException(
                    "사주 분석 결과가 없습니다."
            );
        }
    }

    private String elementKeyword(
            String element
    ) {
        return switch (
                safeText(
                        element,
                        ""
                )
                ) {
            case "목" ->
                    "성장과 확장";

            case "화" ->
                    "표현과 추진";

            case "토" ->
                    "안정과 책임";

            case "금" ->
                    "판단과 정리";

            case "수" ->
                    "관찰과 유연성";

            default ->
                    "균형과 조절";
        };
    }

    private String tenGodKeyword(
            String tenGod
    ) {
        return switch (
                safeText(
                        tenGod,
                        ""
                )
                ) {
            case "비견" ->
                    "독립성과 자기주도";

            case "겁재" ->
                    "경쟁과 승부욕";

            case "식신" ->
                    "꾸준함과 생산성";

            case "상관" ->
                    "표현력과 창의성";

            case "편재" ->
                    "활동성과 기회 포착";

            case "정재" ->
                    "현실감과 관리 능력";

            case "편관" ->
                    "결단력과 위기 대응";

            case "정관" ->
                    "책임감과 원칙";

            case "편인" ->
                    "직관과 독특한 사고";

            case "정인" ->
                    "학습력과 이해력";

            default ->
                    "복합적인 성향";
        };
    }

    private String elementOuterPersonality(
            String element
    ) {
        return switch (
                safeText(
                        element,
                        ""
                )
                ) {
            case "목" ->
                    "앞으로 나아가고 새로운 일을 만들어가려는";

            case "화" ->
                    "반응이 빠르고 자신의 생각을 분명하게 표현하는";

            case "토" ->
                    "안정적이고 맡은 일을 끝까지 책임지려는";

            case "금" ->
                    "기준이 분명하고 잘못된 부분을 빠르게 정리하는";

            case "수" ->
                    "상황을 먼저 관찰하고 유연하게 판단하는";

            default ->
                    "상황에 따라 태도를 조절하는";
        };
    }

    private String elementInnerPersonality(
            String element
    ) {
        return switch (
                safeText(
                        element,
                        ""
                )
                ) {
            case "목" ->
                    "속으로는 계속 성장하고 더 나은 방향으로 움직이고 싶은 마음이 있습니다.";

            case "화" ->
                    "속마음에는 인정받고 자신의 생각을 표현하고 싶은 욕구가 강할 수 있습니다.";

            case "토" ->
                    "마음속에서는 안정과 책임을 중요하게 생각하며 불확실한 상황을 부담스러워할 수 있습니다.";

            case "금" ->
                    "속으로 옳고 그름을 분명히 나누며 스스로 정한 기준을 쉽게 바꾸지 않을 수 있습니다.";

            case "수" ->
                    "속으로 많은 가능성을 생각하며 결론을 내리기 전에 오래 관찰하는 편일 수 있습니다.";

            default ->
                    "겉으로 보이는 모습보다 속으로 더 많은 생각과 고민을 품고 있을 수 있습니다.";
        };
    }

    private String tenGodOuterPersonality(
            String tenGod
    ) {
        return switch (
                safeText(
                        tenGod,
                        ""
                )
                ) {
            case "비견" ->
                    "자신의 방식과 판단을 중요하게 여기는 사람으로 보일 수 있습니다.";

            case "겁재" ->
                    "승부욕이 있고 쉽게 밀리지 않는 사람으로 보일 수 있습니다.";

            case "식신" ->
                    "편안하고 꾸준하며 맡은 일을 안정적으로 처리하는 사람으로 보일 수 있습니다.";

            case "상관" ->
                    "표현력이 강하고 기존 방식에 의문을 제기하는 사람으로 보일 수 있습니다.";

            case "편재" ->
                    "사람과 기회를 빠르게 연결하고 활동적으로 움직이는 사람으로 보일 수 있습니다.";

            case "정재" ->
                    "현실적이고 약속과 금전 관리를 중요하게 여기는 사람으로 보일 수 있습니다.";

            case "편관" ->
                    "강한 책임감과 결단력을 가진 사람으로 보일 수 있습니다.";

            case "정관" ->
                    "원칙과 질서를 지키며 신뢰를 중요하게 여기는 사람으로 보일 수 있습니다.";

            case "편인" ->
                    "독특한 관점과 직관을 가진 사람으로 보일 수 있습니다.";

            case "정인" ->
                    "차분하고 이해력이 높으며 배움을 중요하게 여기는 사람으로 보일 수 있습니다.";

            default ->
                    "상황과 상대에 따라 여러 성향이 함께 드러날 수 있습니다.";
        };
    }

    private String tenGodInnerPersonality(
            String tenGod
    ) {
        return switch (
                safeText(
                        tenGod,
                        ""
                )
                ) {
            case "비견" ->
                    "속으로는 다른 사람에게 의지하기보다 스스로 해결해야 마음이 편할 수 있습니다.";

            case "겁재" ->
                    "속으로 지는 것을 싫어하고 비교나 경쟁에 민감하게 반응할 수 있습니다.";

            case "식신" ->
                    "속으로 편안하고 안정적인 생활을 원하며 꾸준히 결과를 만들고 싶어 할 수 있습니다.";

            case "상관" ->
                    "속으로 답답한 규칙과 억압을 싫어하고 자신의 방식대로 표현하고 싶어 할 수 있습니다.";

            case "편재" ->
                    "속으로 더 많은 기회와 사람을 만나며 크게 움직이고 싶은 마음이 있을 수 있습니다.";

            case "정재" ->
                    "속으로 생활의 안정과 돈의 흐름을 중요하게 생각할 수 있습니다.";

            case "편관" ->
                    "속으로 긴장과 책임을 크게 느끼며 약한 모습을 쉽게 보이지 않을 수 있습니다.";

            case "정관" ->
                    "속으로 실수하지 않고 올바르게 행동해야 한다는 부담을 느낄 수 있습니다.";

            case "편인" ->
                    "속으로 남들이 보지 못하는 부분을 오래 생각하고 혼자만의 판단을 중요하게 여길 수 있습니다.";

            case "정인" ->
                    "속으로 이해받고 보호받는 안정적인 관계를 원할 수 있습니다.";

            default ->
                    "속마음을 쉽게 드러내기보다 상황을 충분히 생각한 뒤 표현하는 편일 수 있습니다.";
        };
    }

    private List<String> elementStrengths(
            String element
    ) {
        return switch (
                safeText(
                        element,
                        ""
                )
                ) {
            case "목" ->
                    List.of(
                            "새로운 일을 기획하는 힘",
                            "성장 가능성을 보는 능력",
                            "앞으로 밀고 나가는 추진력"
                    );

            case "화" ->
                    List.of(
                            "빠른 실행력",
                            "표현력과 설득력",
                            "주변 분위기를 움직이는 힘"
                    );

            case "토" ->
                    List.of(
                            "책임감",
                            "현실적인 판단",
                            "꾸준히 유지하고 관리하는 능력"
                    );

            case "금" ->
                    List.of(
                            "문제를 정확하게 구분하는 능력",
                            "원칙과 기준",
                            "정리와 결단력"
                    );

            case "수" ->
                    List.of(
                            "정보를 파악하는 능력",
                            "유연한 사고",
                            "상황에 맞게 방향을 바꾸는 능력"
                    );

            default ->
                    List.of(
                            "상황에 맞춰 균형을 조절하는 능력"
                    );
        };
    }

    private List<String> elementWeaknesses(
            String element
    ) {
        return switch (
                safeText(
                        element,
                        ""
                )
                ) {
            case "목" ->
                    List.of(
                            "일을 계속 벌이기만 하지 않기",
                            "결과가 늦다고 중간에 방향을 자주 바꾸지 않기"
                    );

            case "화" ->
                    List.of(
                            "감정과 행동을 너무 빠르게 결정하지 않기",
                            "흥분한 상태에서 말하지 않기"
                    );

            case "토" ->
                    List.of(
                            "변화를 지나치게 두려워하지 않기",
                            "책임과 걱정을 혼자 쌓아두지 않기"
                    );

            case "금" ->
                    List.of(
                            "사람과 상황을 너무 단정적으로 판단하지 않기",
                            "완벽하지 않다고 바로 잘라내지 않기"
                    );

            case "수" ->
                    List.of(
                            "생각만 오래 하고 행동을 미루지 않기",
                            "상황에 따라 입장을 지나치게 바꾸지 않기"
                    );

            default ->
                    List.of(
                            "한 가지 감정이나 판단에 지나치게 치우치지 않기"
                    );
        };
    }

    private List<String> tenGodStrengths(
            String tenGod
    ) {
        return switch (
                safeText(
                        tenGod,
                        ""
                )
                ) {
            case "비견" ->
                    List.of(
                            "독립성",
                            "자기주도",
                            "끝까지 버티는 힘"
                    );

            case "겁재" ->
                    List.of(
                            "승부욕",
                            "위기 대응",
                            "강한 추진력"
                    );

            case "식신" ->
                    List.of(
                            "꾸준함",
                            "생산성",
                            "사람을 편하게 만드는 힘"
                    );

            case "상관" ->
                    List.of(
                            "표현력",
                            "창의성",
                            "문제점을 발견하는 능력"
                    );

            case "편재" ->
                    List.of(
                            "기회를 빠르게 잡는 능력",
                            "활동성",
                            "사람과 정보를 연결하는 힘"
                    );

            case "정재" ->
                    List.of(
                            "현실 감각",
                            "재정 관리",
                            "약속을 지키는 태도"
                    );

            case "편관" ->
                    List.of(
                            "결단력",
                            "위기에서 움직이는 힘",
                            "강한 책임감"
                    );

            case "정관" ->
                    List.of(
                            "신뢰성",
                            "원칙",
                            "조직에서 책임지는 능력"
                    );

            case "편인" ->
                    List.of(
                            "직관력",
                            "독특한 아이디어",
                            "전문 분야에 몰입하는 힘"
                    );

            case "정인" ->
                    List.of(
                            "학습력",
                            "이해력",
                            "정보를 체계적으로 받아들이는 힘"
                    );

            default ->
                    List.of();
        };
    }

    private List<String> tenGodWeaknesses(
            String tenGod
    ) {
        return switch (
                safeText(
                        tenGod,
                        ""
                )
                ) {
            case "비견" ->
                    List.of(
                            "고집으로 관계를 밀어붙이지 않기",
                            "다른 사람의 의견도 확인하기"
                    );

            case "겁재" ->
                    List.of(
                            "경쟁심으로 손해를 감수하지 않기",
                            "친분과 돈 문제를 섞지 않기"
                    );

            case "식신" ->
                    List.of(
                            "편안함에 머물러 시작을 미루지 않기",
                            "좋은 사람 역할만 하다가 책임을 떠안지 않기"
                    );

            case "상관" ->
                    List.of(
                            "말로 관계를 끊지 않기",
                            "규칙을 무조건 답답하다고 거부하지 않기"
                    );

            case "편재" ->
                    List.of(
                            "기회만 보고 지출을 늘리지 않기",
                            "관계를 너무 넓혀 책임이 커지지 않게 하기"
                    );

            case "정재" ->
                    List.of(
                            "손해를 지나치게 두려워해 기회를 놓치지 않기",
                            "돈과 안정만으로 관계를 판단하지 않기"
                    );

            case "편관" ->
                    List.of(
                            "압박을 혼자 참다가 폭발하지 않기",
                            "강한 사람과 정면으로만 부딪히지 않기"
                    );

            case "정관" ->
                    List.of(
                            "실수하지 않으려다 결정을 미루지 않기",
                            "규칙과 책임을 혼자 떠안지 않기"
                    );

            case "편인" ->
                    List.of(
                            "혼자 판단하고 사람을 멀리하지 않기",
                            "생각이 많아 실행을 멈추지 않기"
                    );

            case "정인" ->
                    List.of(
                            "도움만 기다리지 않기",
                            "배우는 것에 머물지 말고 결과로 표현하기"
                    );

            default ->
                    List.of();
        };
    }
}