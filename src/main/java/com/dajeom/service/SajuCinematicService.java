package com.dajeom.service;

import com.dajeom.dto.SajuAnalysisResponse;
import com.dajeom.dto.SajuAnalysisResponse.InterpretationResponse;
import com.dajeom.dto.SajuAnalysisResponse.PillarResponse;
import com.dajeom.dto.SajuCinematicResponse;
import com.dajeom.dto.SajuYearForecastResponse;
import com.dajeom.entity.SajuAnalysis;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class SajuCinematicService {

    private static final ZoneId KOREA_ZONE =
            ZoneId.of(
                    "Asia/Seoul"
            );

    private static final int MAX_SCENE_NARRATION_LENGTH = 560;

    /**
     * 심층 사주 해설을 영상 장면 데이터로 변환한다.
     *
     * 카테고리별 약 2,000자의 전체 해설은 결과 화면에 사용하고
     * 영상에서는 약 560자 이내의 핵심 내용만 사용한다.
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

        /*
         * yearForecasts는 기존 메서드 호환성을 위해 유지한다.
         *
         * 영상 해설에서는 특정 월별 예측이나
         * 다수 연도의 예언형 장면을 만들지 않는다.
         */

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

        int reportYear =
                Year.now(
                        KOREA_ZONE
                ).getValue();

        List<SajuCinematicResponse.CinematicScene> scenes =
                new ArrayList<>();

        int order = 1;

        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.OPENING,
                        "DESTINY_GATE",
                        "SERIOUS",
                        "FADE",
                        11,
                        profileName
                                + "의 사주를 시작할게",
                        "좋은 말만 골라서 하지 않을게. "
                                + "강한 부분은 왜 강한지, 약한 부분은 현실에서 어떤 문제를 만드는지 명확하게 짚어볼 거야. "
                                + "다만 특정 사건을 확정하거나 겁을 주는 방식으로 풀지는 않아. "
                                + "원국과 오행, 십성, 관계 구조와 현재 대운에서 반복되는 의미만 사용할게.",
                        "결론부터 말하고 근거와 현실적인 행동까지 이어서 설명할게.",
                        List.of(
                                "현대 종합사주",
                                "전통사주 심층풀이",
                                reportYear
                                        + "년 종합운",
                                "연애와 궁합",
                                "가족과 재물",
                                "관재수와 현재·미래운"
                        ),
                        List.of(
                                "원국 전체를 우선해서 판단해.",
                                "신살 하나로 결과를 단정하지 않아."
                        )
                )
        );

        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.MANSE_CALENDAR,
                        "FOUR_PILLARS",
                        "CALM",
                        "LIGHT_FLASH",
                        14,
                        "먼저 네 명식의 뼈대부터 볼게",
                        "연주와 월주, 일주와 시주는 따로 떨어진 글자가 아니야. "
                                + "각 기둥이 서로 어떤 관계를 맺는지 봐야 성격과 가족, 직업과 재물 흐름을 제대로 읽을 수 있어. "
                                + "특히 일간을 중심으로 오행과 십성이 어떻게 분포하고, "
                                + "합과 충이 어디에서 반복되는지를 먼저 확인해야 해.",
                        "한 글자보다 네 기둥 전체의 관계가 더 중요해.",
                        extractManseHighlights(
                                analysisResponse
                        ),
                        List.of(
                                "연주·월주·일주·시주",
                                "오행과 음양",
                                "십성과 지장간",
                                "합·충·형·파·해"
                        )
                )
        );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "타고난 종합운",
                        SajuCinematicResponse.SceneType.BASIC_ENERGY,
                        "CORE_ENERGY",
                        "SERIOUS",
                        "ZOOM_IN",
                        "타고난 종합운",
                        "네 사주의 가장 강한 힘과 반복되는 약점을 먼저 볼게."
                );

        order =
                addYearReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        reportYear
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "앞으로의 종합운",
                        SajuCinematicResponse.SceneType.FUTURE_OVERVIEW,
                        "FUTURE_PATH",
                        "CALM",
                        "FADE",
                        "앞으로의 종합운",
                        "정확한 날짜보다 앞으로 어떤 선택이 결과를 바꾸는지 볼게."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "과거운",
                        SajuCinematicResponse.SceneType.PERSONALITY_INNER,
                        "PAST_MEMORY",
                        "SERIOUS",
                        "FADE",
                        "과거운과 성장 과정",
                        "어릴 때 만들어진 생존 방식이 지금도 반복되는지 볼게."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "부모와의 연",
                        SajuCinematicResponse.SceneType.RELATIONSHIPS,
                        "FAMILY_THREADS",
                        "SERIOUS",
                        "SLIDE_UP",
                        "부모와의 연",
                        "부모의 실제 이혼이나 재혼을 단정하지 않고 관계 구조만 볼게."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "형제운",
                        SajuCinematicResponse.SceneType.RELATIONSHIPS,
                        "SIBLING_THREADS",
                        "CALM",
                        "FADE",
                        "형제와 가까운 사람의 운",
                        "경쟁과 협력, 책임 분담이 어떻게 나타나는지 볼게."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "연애 스타일",
                        SajuCinematicResponse.SceneType.ROMANCE,
                        "ROMANCE_MOON",
                        "SERIOUS",
                        "SLIDE_UP",
                        "네 연애 스타일",
                        "마음을 주는 방식과 관계에서 반복되는 약점을 볼게."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "만나기 쉬운 사람",
                        SajuCinematicResponse.SceneType.ROMANCE,
                        "MEETING_PERSON",
                        "CALM",
                        "FADE",
                        "네가 만나기 쉬운 사람",
                        "강하게 끌리는 사람과 실제로 안정적인 사람은 다를 수 있어."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "궁합이 좋은 사람",
                        SajuCinematicResponse.SceneType.ROMANCE,
                        "COMPATIBILITY",
                        "SMILE",
                        "LIGHT_FLASH",
                        "너와 궁합이 좋은 사람",
                        "오행 하나가 아니라 책임과 갈등 해결 방식까지 함께 볼게."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "금전운",
                        SajuCinematicResponse.SceneType.WEALTH,
                        "MONEY_FLOW",
                        "SERIOUS",
                        "ZOOM_IN",
                        "돈이 들어오고 나가는 방식",
                        "돈을 버는 능력과 지키는 능력은 따로 봐야 해."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "재물운",
                        SajuCinematicResponse.SceneType.WEALTH,
                        "WEALTH_VAULT",
                        "SERIOUS",
                        "SLIDE_UP",
                        "재물을 남기는 방식",
                        "수입보다 실제로 남는 구조가 있는지 볼게."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "관재수",
                        SajuCinematicResponse.SceneType.CAUTION_PERIOD,
                        "CAUTION_RED_MOON",
                        "WARNING",
                        "LIGHT_FLASH",
                        "관재수와 분쟁 성향",
                        "법적 사건을 예언하는 게 아니라 분쟁이 커지는 행동을 짚을게."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "현재운",
                        SajuCinematicResponse.SceneType.CURRENT_DAEUN,
                        "DAEUN_RIVER",
                        "SERIOUS",
                        "ZOOM_IN",
                        "현재의 큰 흐름",
                        "지금 반복되는 과제와 정리해야 할 부분을 볼게."
                );

        order =
                addReportScene(
                        scenes,
                        order,
                        analysisResponse,
                        "미래운",
                        SajuCinematicResponse.SceneType.FUTURE_OVERVIEW,
                        "FUTURE_GATE",
                        "CALM",
                        "FADE",
                        "앞으로의 미래운",
                        "특정 월을 맞히지 않고 유리한 선택과 불리한 습관을 구분할게."
                );

        scenes.add(
                createScene(
                        order++,
                        SajuCinematicResponse.SceneType.ADVICE,
                        "FINAL_ADVICE",
                        "SERIOUS",
                        "FADE",
                        16,
                        "마지막으로 가장 중요한 말만 할게",
                        createFinalAdvice(
                                analysisResponse
                        ),
                        "운을 기다리지 말고 네 선택 방식부터 바꿔.",
                        createAdviceHighlights(
                                analysisResponse
                        ),
                        List.of(
                                "강한 기운은 반복 가능한 능력으로 바꿔.",
                                "약한 기운은 생활 습관과 환경으로 보완해.",
                                "돈과 책임은 반드시 기록으로 남겨."
                        )
                )
        );

        scenes.add(
                createScene(
                        order,
                        SajuCinematicResponse.SceneType.EVIDENCE,
                        "SECRET_LEDGER",
                        "CALM",
                        "SLIDE_UP",
                        13,
                        "이 해설을 만든 근거",
                        "이 해설은 신살 하나나 듣기 좋은 표현으로 만든 게 아니야. "
                                + "일간과 월령, 오행 분포, 십성의 강약, 신강·신약 보조 판정, "
                                + "합·충·형·파·해와 현재 대운에서 같은 의미가 얼마나 반복되는지를 함께 봤어. "
                                + "근거가 부족한 부모의 이혼·재혼, 사고, 질병, 소송은 확정하지 않았어.",
                        "원국 전체와 반복되는 근거를 우선해서 해석했어.",
                        extractManseHighlights(
                                analysisResponse
                        ),
                        List.of(
                                "원국",
                                "오행",
                                "십성",
                                "신강·신약",
                                "용신·희신·기신",
                                "합·충·형·파·해",
                                "대운",
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
                        + "의 천명 심층 해설",
                "명쾌하고 날카롭게 풀어보는 현대사주와 전통사주",
                totalDurationSeconds,
                List.copyOf(
                        scenes
                ),
                "이 영상형 해설은 전통 명리 계산을 바탕으로 성향과 선택을 분석한 참고 콘텐츠야. "
                        + "질병, 사고, 이혼, 재혼, 소송과 특정 사건을 확정하는 예언으로 사용하면 안 돼."
        );
    }

    private int addYearReportScene(
            List<SajuCinematicResponse.CinematicScene> scenes,
            int order,
            SajuAnalysisResponse response,
            int reportYear
    ) {
        String title =
                findTitleContaining(
                        response,
                        reportYear
                                + "년 종합운"
                );

        if (
                title == null
        ) {
            return order;
        }

        scenes.add(
                createScene(
                        order,
                        SajuCinematicResponse.SceneType.YEAR_FORECAST,
                        "YEAR_FLOW",
                        "SERIOUS",
                        "LIGHT_FLASH",
                        calculateDuration(
                                title
                        ),
                        reportYear
                                + "년 종합운",
                        toSceneNarration(
                                title
                        ),
                        "올해 전체의 주제만 보고 특정 월은 예측하지 않아.",
                        List.of(
                                reportYear
                                        + "년 핵심 과제",
                                "선택과 완성",
                                "직업과 공부",
                                "돈과 책임"
                        ),
                        List.of(
                                "현재 연도 세운",
                                "현재 대운",
                                "원국의 주요 십성"
                        )
                )
        );

        return order + 1;
    }

    private int addReportScene(
            List<SajuCinematicResponse.CinematicScene> scenes,
            int order,
            SajuAnalysisResponse response,
            String reportTitle,
            SajuCinematicResponse.SceneType sceneType,
            String visualKey,
            String expressionKey,
            String transitionKey,
            String sceneTitle,
            String subtitle
    ) {
        String report =
                findReport(
                        response,
                        reportTitle
                );

        if (
                report == null
                        || report.isBlank()
        ) {
            return order;
        }

        scenes.add(
                createScene(
                        order,
                        sceneType,
                        visualKey,
                        expressionKey,
                        transitionKey,
                        calculateDuration(
                                report
                        ),
                        sceneTitle,
                        toSceneNarration(
                                report
                        ),
                        subtitle,
                        List.of(
                                reportTitle,
                                "핵심 결론",
                                "명리 근거",
                                "현실적인 행동"
                        ),
                        List.of(
                                "심층 결과 보고서에서 핵심 문장을 추출했어."
                        )
                )
        );

        return order + 1;
    }

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
                normalizeText(
                        title
                ),
                normalizeText(
                        narration
                ),
                normalizeText(
                        subtitle
                ),
                SajuCinematicResponse
                        .CinematicPeriod
                        .empty(),
                sanitizeList(
                        highlights
                ),
                sanitizeList(
                        evidences
                )
        );
    }

    private String findReport(
            SajuAnalysisResponse response,
            String title
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
                                title.equals(
                                        interpretation.title()
                                )
                )
                .map(
                        InterpretationResponse::content
                )
                .filter(
                        value ->
                                value != null
                                        && !value.isBlank()
                )
                .findFirst()
                .orElse(
                        null
                );
    }

    private String findTitleContaining(
            SajuAnalysisResponse response,
            String titleKeyword
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
                                interpretation.title() != null
                                        && interpretation.title()
                                        .contains(
                                                titleKeyword
                                        )
                )
                .map(
                        InterpretationResponse::content
                )
                .filter(
                        value ->
                                value != null
                                        && !value.isBlank()
                )
                .findFirst()
                .orElse(
                        null
                );
    }

    /**
     * 심층 해설을 영상용 길이로 줄인다.
     */
    private String toSceneNarration(
            String content
    ) {
        String normalized =
                normalizeText(
                        content
                );

        if (
                normalized.length()
                        <= MAX_SCENE_NARRATION_LENGTH
        ) {
            return normalized;
        }

        String[] sentences =
                normalized.split(
                        "(?<=[.!?])\\s+"
                );

        StringBuilder builder =
                new StringBuilder();

        for (
                String sentence : sentences
        ) {
            if (
                    sentence == null
                            || sentence.isBlank()
            ) {
                continue;
            }

            if (
                    builder.length()
                            + sentence.length()
                            + 1
                            > MAX_SCENE_NARRATION_LENGTH
            ) {
                break;
            }

            if (
                    !builder.isEmpty()
            ) {
                builder.append(
                        " "
                );
            }

            builder.append(
                    sentence.trim()
            );
        }

        if (
                builder.isEmpty()
        ) {
            return normalized.substring(
                    0,
                    MAX_SCENE_NARRATION_LENGTH - 1
            )
                    + "…";
        }

        return builder.toString();
    }

    private int calculateDuration(
            String narration
    ) {
        if (
                narration == null
                        || narration.isBlank()
        ) {
            return 12;
        }

        int calculated =
                11
                        + narration.length()
                        / 45;

        return Math.max(
                12,
                Math.min(
                        25,
                        calculated
                )
        );
    }

    private List<String> extractManseHighlights(
            SajuAnalysisResponse response
    ) {
        Set<String> result =
                new LinkedHashSet<>();

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
                        safeText(
                                pillar.type(),
                                "기둥"
                        )
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
                        pillar.stemTenGod() != null
                                && !pillar.stemTenGod()
                                .isBlank()
                ) {
                    builder.append(
                            " · "
                    );

                    builder.append(
                            pillar.stemTenGod()
                    );
                }

                if (
                        pillar.twelveStage() != null
                                && !pillar.twelveStage()
                                .isBlank()
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
                response.dayMaster() != null
                        && !response.dayMaster()
                        .isBlank()
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
                response.tenGodBalance() != null
        ) {
            result.add(
                    "주요 십성: "
                            + safeText(
                            response.tenGodBalance()
                                    .dominant(),
                            "확인 필요"
                    )
            );
        }

        if (
                response.strength() != null
        ) {
            result.add(
                    "일간 세력: "
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
                        value ->
                                value != null
                                        && !value.isBlank()
                )
                .limit(
                        12
                )
                .toList();
    }

    private String createFinalAdvice(
            SajuAnalysisResponse response
    ) {
        String dominantElement =
                response.elementBalance() == null
                        ? "강한 기운"
                        : safeText(
                        response.elementBalance()
                                .dominant(),
                        "강한 기운"
                );

        String weakestElement =
                response.elementBalance() == null
                        ? "부족한 기운"
                        : safeText(
                        response.elementBalance()
                                .weakest(),
                        "부족한 기운"
                );

        String usefulGod =
                response.strength() == null
                        ? "균형을 잡는 기운"
                        : safeText(
                        response.strength()
                                .usefulGod(),
                        "균형을 잡는 기운"
                );

        return "네 사주에서 "
                + dominantElement
                + " 기운은 분명한 장점이지만 지나치게 사용하면 같은 실수를 반복하게 돼. "
                + weakestElement
                + " 기운은 결함이 아니라 생활 습관과 환경으로 보완해야 할 부분이야. "
                + "용신으로 분류된 "
                + usefulGod
                + " 방향을 참고하되 실제 결정은 돈과 시간, 책임 범위를 기준으로 내려. "
                + "사람과 돈이 함께 움직이는 일은 반드시 기록으로 남기고, "
                + "감정이 강한 날에는 중요한 결정을 바로 내리지 마. "
                + "네가 바꿔야 할 건 운이 아니라 선택하는 방식이야.";
    }

    private List<String> createAdviceHighlights(
            SajuAnalysisResponse response
    ) {
        List<String> result =
                new ArrayList<>();

        if (
                response.strength() != null
        ) {
            addValue(
                    result,
                    "용신",
                    response.strength()
                            .usefulGod()
            );

            addValue(
                    result,
                    "희신",
                    response.strength()
                            .favorableGod()
            );

            addValue(
                    result,
                    "기신 주의",
                    response.strength()
                            .unfavorableGod()
            );
        }

        if (
                response.elementBalance() != null
        ) {
            addValue(
                    result,
                    "보완 오행",
                    response.elementBalance()
                            .weakest()
            );
        }

        result.add(
                "돈과 책임은 기록으로 남겨."
        );

        result.add(
                "감정적인 결정은 하루 미뤄."
        );

        result.add(
                "작게 검증한 뒤 확장해."
        );

        return List.copyOf(
                result
        );
    }

    private void addValue(
            List<String> values,
            String label,
            String value
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return;
        }

        values.add(
                label
                        + ": "
                        + value
        );
    }

    private List<String> sanitizeList(
            List<String> values
    ) {
        if (
                values == null
                        || values.isEmpty()
        ) {
            return List.of();
        }

        return values.stream()
                .filter(
                        Objects::nonNull
                )
                .map(
                        this::normalizeText
                )
                .filter(
                        value ->
                                !value.isBlank()
                )
                .distinct()
                .toList();
    }

    private String normalizeText(
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

    private String safeText(
            String value,
            String fallback
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return fallback;
        }

        return value;
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
}