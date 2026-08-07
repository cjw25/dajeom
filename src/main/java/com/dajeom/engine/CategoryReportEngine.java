package com.dajeom.engine;

import com.dajeom.config.SajuCalculationPolicy;
import com.dajeom.model.CategoryInterpretation;
import com.dajeom.model.FourPillars;
import com.dajeom.model.InterpretationSignal;
import com.dajeom.model.InterpretationSignalAnalysis;
import com.dajeom.model.LuckAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.SajuReport;
import com.dajeom.model.SajuReportSectionModel;
import com.dajeom.rule.ReportTemplateProvider;
import com.dajeom.type.SajuReportCategory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 카테고리별 해석 신호를 이용해
 * 약 2,000자 분량의 최종 보고서를 생성한다.
 *
 * 외부 AI를 사용하지 않는 템플릿 기반 V1 엔진이다.
 */
@Component
public class CategoryReportEngine {

    public static final String RULE_VERSION =
            "DAJEOM_CATEGORY_REPORT_V1";

    public static final String GENERATION_MODE =
            "TEMPLATE";

    private static final int TARGET_MIN_LENGTH =
            1_800;

    private static final int TARGET_MAX_LENGTH =
            2_200;

    private final ReportTemplateProvider
            reportTemplateProvider;

    public CategoryReportEngine(
            ReportTemplateProvider
                    reportTemplateProvider
    ) {
        this.reportTemplateProvider =
                reportTemplateProvider;
    }

    public SajuReport generate(
            FourPillars fourPillars,
            NatalChartAnalysis natalChartAnalysis,
            LuckAnalysis luckAnalysis,
            InterpretationSignalAnalysis
                    interpretationSignalAnalysis
    ) {
        List<SajuReportSectionModel> sections =
                interpretationSignalAnalysis
                        .categories()
                        .stream()
                        .map(category ->
                                generateSection(
                                        category,
                                        fourPillars,
                                        natalChartAnalysis,
                                        luckAnalysis
                                )
                        )
                        .toList();

        int totalCharacterCount =
                sections.stream()
                        .mapToInt(
                                SajuReportSectionModel::
                                        characterCount
                        )
                        .sum();

        return new SajuReport(
                luckAnalysis
                        .annualLuck()
                        .targetYear(),

                createFullChineseName(
                        fourPillars
                ),

                createFullKoreanName(
                        fourPillars
                ),

                sections,

                totalCharacterCount,

                LocalDateTime.now(
                        SajuCalculationPolicy
                                .DEFAULT_ZONE_ID
                ),

                GENERATION_MODE,

                RULE_VERSION
        );
    }

    private SajuReportSectionModel generateSection(
            CategoryInterpretation interpretation,
            FourPillars fourPillars,
            NatalChartAnalysis natalChartAnalysis,
            LuckAnalysis luckAnalysis
    ) {
        SajuReportCategory category =
                interpretation.category();

        List<InterpretationSignal> signals =
                interpretation.signals();

        String coreSummary =
                createCoreSummary(
                        interpretation
                );

        StringBuilder content =
                new StringBuilder();

        appendParagraph(
                content,
                reportTemplateProvider.opening(
                        category
                )
        );

        appendParagraph(
                content,
                createChartIntroduction(
                        category,
                        fourPillars,
                        natalChartAnalysis,
                        luckAnalysis
                )
        );

        for (InterpretationSignal signal : signals) {
            appendParagraph(
                    content,
                    createSignalParagraph(
                            signal
                    )
            );
        }

        appendParagraph(
                content,
                reportTemplateProvider
                        .interpretationBridge(
                                category
                        )
        );

        appendParagraph(
                content,
                createScoreParagraph(
                        interpretation
                )
        );

        appendParagraph(
                content,
                reportTemplateProvider.advice(
                        category
                )
        );

        appendParagraph(
                content,
                reportTemplateProvider.closing(
                        category
                )
        );

        expandToTargetLength(
                content,
                category,
                fourPillars,
                natalChartAnalysis,
                luckAnalysis
        );

        String finalContent =
                normalizeContent(
                        content.toString()
                );

        if (finalContent.length()
                > TARGET_MAX_LENGTH) {

            finalContent =
                    trimAtSentenceBoundary(
                            finalContent,
                            TARGET_MAX_LENGTH
                    );
        }

        List<String> usedSignalCodes =
                signals.stream()
                        .map(
                                InterpretationSignal::
                                        signalCode
                        )
                        .distinct()
                        .toList();

        return new SajuReportSectionModel(
                category,

                createTitle(
                        category,
                        luckAnalysis
                                .annualLuck()
                                .targetYear()
                ),

                coreSummary,

                finalContent,

                finalContent.length(),

                interpretation.favorableScore(),

                interpretation.challengingScore(),

                interpretation.netScore(),

                usedSignalCodes
        );
    }

    private String createTitle(
            SajuReportCategory category,
            int targetYear
    ) {
        if (category
                == SajuReportCategory.YEAR_LUCK) {

            return targetYear
                    + "년 "
                    + category.getKoreanName();
        }

        return category.getKoreanName();
    }

    private String createCoreSummary(
            CategoryInterpretation interpretation
    ) {
        InterpretationSignal primary =
                interpretation.primarySignal();

        if (primary == null) {
            return interpretation
                    .category()
                    .getDescription();
        }

        return primary.title()
                + ": "
                + primary.summary();
    }

    private String createChartIntroduction(
            SajuReportCategory category,
            FourPillars fourPillars,
            NatalChartAnalysis natal,
            LuckAnalysis luck
    ) {
        String dayMaster =
                fourPillars
                        .dayPillar()
                        .stem()
                        .getChineseCharacter()
                        + fourPillars
                        .dayPillar()
                        .stem()
                        .getKoreanName();

        String strength =
                natal
                        .strengthAnalysis()
                        .level()
                        .getKoreanName();

        String usefulGod =
                natal
                        .usefulGodAnalysis()
                        .primaryUsefulGod()
                        .element()
                        .getKoreanName();

        String favorableGod =
                natal
                        .usefulGodAnalysis()
                        .secondaryFavorableGod()
                        == null
                        ? "별도 보조 후보 없음"
                        : natal
                        .usefulGodAnalysis()
                        .secondaryFavorableGod()
                        .element()
                        .getKoreanName();

        String currentDecade =
                luck.currentDecadeLuck() == null
                        ? "대운 시작 전 또는 계산 범위 외"
                        : luck.currentDecadeLuck()
                        .pillar()
                        .koreanName();

        return switch (category) {
            case CURRENT_LUCK, YEAR_LUCK, FUTURE_LUCK ->
                    "이 명식은 "
                            + dayMaster
                            + " 일간이며 강약은 "
                            + strength
                            + "으로 판정되었다. 용신 1순위 후보는 "
                            + usefulGod
                            + ", 보조 희신 후보는 "
                            + favorableGod
                            + "이고 현재 대운은 "
                            + currentDecade
                            + "이다. 선택 연도의 세운은 "
                            + luck.annualLuck()
                            .pillar()
                            .koreanName()
                            + "이다.";

            default ->
                    "원국은 "
                            + createFullKoreanName(
                            fourPillars
                    )
                            + "이며 일간은 "
                            + dayMaster
                            + "이다. 일간 강약은 "
                            + strength
                            + "이고, 오행 세력에서 가장 강한 기운은 "
                            + natal.elementStrength()
                            .strongestElement()
                            .getKoreanName()
                            + ", 가장 약한 기운은 "
                            + natal.elementStrength()
                            .weakestElement()
                            .getKoreanName()
                            + "으로 계산되었다.";
        };
    }

    private String createSignalParagraph(
            InterpretationSignal signal
    ) {
        StringBuilder paragraph =
                new StringBuilder();

        paragraph.append(
                reportTemplateProvider
                        .signalParagraphPrefix(
                                signal.polarity()
                        )
        );

        paragraph.append(signal.title());
        paragraph.append("이라는 신호가 확인된다. ");
        paragraph.append(signal.summary());
        paragraph.append(". ");

        if (!signal.evidenceDescriptions()
                .isEmpty()) {

            paragraph.append("이 판단의 근거는 ");

            Set<String> uniqueEvidence =
                    new LinkedHashSet<>(
                            signal
                                    .evidenceDescriptions()
                    );

            paragraph.append(
                    String.join(
                            ", ",
                            uniqueEvidence
                    )
            );

            paragraph.append("이라는 점이다. ");
        }

        paragraph.append(
                "신호 강도는 "
        );

        paragraph.append(
                signal.strength()
                        .getKoreanName()
        );

        paragraph.append(
                "으로 평가되며, 이 특성은 항상 같은 방식으로 나타나는 것이 아니라 환경과 선택에 따라 장점 또는 부담으로 바뀔 수 있다."
        );

        return paragraph.toString();
    }

    private String createScoreParagraph(
            CategoryInterpretation interpretation
    ) {
        double favorable =
                interpretation.favorableScore();

        double challenging =
                interpretation.challengingScore();

        double net =
                interpretation.netScore();

        if (favorable > challenging) {
            return "이 카테고리에서는 유리한 신호의 합이 "
                    + favorable
                    + "점, 주의 신호의 합이 "
                    + challenging
                    + "점으로 계산되었다. 순점수는 "
                    + net
                    + "점이므로 장점을 활용할 여지가 더 크다. 다만 점수가 높다는 이유만으로 결과가 자동으로 보장되는 것은 아니며, 실제 행동과 환경이 함께 맞아야 한다.";
        }

        if (challenging > favorable) {
            return "이 카테고리에서는 유리한 신호의 합이 "
                    + favorable
                    + "점, 주의 신호의 합이 "
                    + challenging
                    + "점으로 계산되었다. 순점수는 "
                    + net
                    + "점이므로 관리해야 할 요소가 더 크게 나타난다. 이것은 나쁜 결과가 확정된다는 뜻이 아니라 준비 없이 움직일 때 부담이 커질 수 있다는 의미다.";
        }

        return "이 카테고리의 유리한 신호와 주의 신호는 서로 비슷한 수준이다. 특정한 한 방향으로 단정하기보다 상황에 따라 장점과 부담이 번갈아 나타날 수 있으므로 선택과 관리 방식이 결과를 크게 좌우한다.";
    }

    private void expandToTargetLength(
            StringBuilder content,
            SajuReportCategory category,
            FourPillars fourPillars,
            NatalChartAnalysis natal,
            LuckAnalysis luck
    ) {
        List<String> supplements =
                createSupplementParagraphs(
                        category,
                        fourPillars,
                        natal,
                        luck
                );

        int index = 0;

        while (content.length()
                < TARGET_MIN_LENGTH
                && index < supplements.size()) {

            appendParagraph(
                    content,
                    supplements.get(index)
            );

            index++;
        }

        if (content.length()
                < TARGET_MIN_LENGTH) {

            appendParagraph(
                    content,
                    "사주 해석은 가능성과 경향성을 설명하는 도구다. 같은 명식을 가진 사람도 성장 환경, 직업, 인간관계, 현재의 선택에 따라 전혀 다른 결과를 만들 수 있다. 따라서 이 보고서는 결정을 대신하는 자료가 아니라 자신의 반복 패턴을 확인하고 더 나은 선택을 하기 위한 참고 자료로 활용하는 것이 적절하다."
            );
        }
    }

    private List<String> createSupplementParagraphs(
            SajuReportCategory category,
            FourPillars fourPillars,
            NatalChartAnalysis natal,
            LuckAnalysis luck
    ) {
        List<String> supplements =
                new ArrayList<>();

        supplements.add(
                "십성 분포를 해석할 때는 천간에 드러난 기운과 지장간 안에 숨어 있는 기운을 구분해야 한다. 겉으로 바로 드러나는 성향과 가까운 관계나 위기 상황에서 나타나는 성향이 다를 수 있기 때문이다. 따라서 평소 모습만 보고 자신을 단정하기보다 반복적으로 어떤 상황에서 특정 반응이 나오는지 확인하는 것이 정확하다."
        );

        supplements.add(
                "합과 충이 동시에 존재하는 명식은 관계와 환경이 한 방향으로만 흘러가지 않는다. 어떤 시기에는 사람과 기회가 빠르게 연결되지만, 다른 시기에는 기존 관계를 정리하거나 역할을 바꿔야 할 수 있다. 변화 자체를 실패로 해석하지 않고 구조 조정의 과정으로 받아들이는 태도가 필요하다."
        );

        supplements.add(
                "현재 용신 후보는 "
                        + natal
                        .usefulGodAnalysis()
                        .primaryUsefulGod()
                        .element()
                        .getKoreanName()
                        + "으로 계산되었다. 이는 해당 오행과 관련된 색상이나 물건을 사용하면 무조건 운이 좋아진다는 뜻이 아니다. 실제 생활에서는 그 오행이 상징하는 행동 방식과 환경을 선택하는 것이 더 중요하다."
        );

        supplements.add(
                "선택 연도 "
                        + luck.annualLuck().targetYear()
                        + "년은 "
                        + luck.annualLuck()
                        .pillar()
                        .koreanName()
                        + " 세운이다. 세운은 1년 동안 직접 체감되는 사건과 선택의 속도를 보여주지만, 장기적인 결과는 현재 대운과 원국의 준비 상태에 따라 달라진다."
        );

        supplements.add(
                categorySpecificSupplement(
                        category,
                        fourPillars,
                        natal,
                        luck
                )
        );

        return supplements;
    }

    private String categorySpecificSupplement(
            SajuReportCategory category,
            FourPillars fourPillars,
            NatalChartAnalysis natal,
            LuckAnalysis luck
    ) {
        return switch (category) {
            case PERSONALITY ->
                    "성격의 강점은 고정된 능력이 아니라 반복적으로 사용하는 방식이다. 분석력이 있어도 실행하지 않으면 걱정이 되고, 신중함이 있어도 기준이 없으면 결정 지연이 된다. 자신의 장점을 실제 행동 규칙으로 만들어야 성향이 능력으로 바뀐다.";

            case PAST_LUCK ->
                    "과거운을 볼 때 기억에 남는 사건만 기준으로 삼으면 해석이 왜곡될 수 있다. 특별한 사건보다 반복적으로 느꼈던 분위기, 책임, 경제적 긴장, 관계의 거리감이 현재 성향에 더 큰 영향을 주었을 수 있다.";

            case PARENTS ->
                    "부모와의 관계는 나이가 들면서 역할이 바뀐다. 보호받는 입장에서 책임지는 입장으로 이동할 때 경제적·정서적 부담이 커질 수 있으므로, 미리 가능한 도움의 범위를 정하는 것이 필요하다.";

            case SIBLINGS ->
                    "형제·동료 관계에서는 경쟁을 없애려고 하기보다 공정한 기준을 만드는 것이 더 효과적이다. 성과와 책임을 객관적으로 나누면 경쟁은 발전의 자극이 될 수 있다.";

            case LOVE_STYLE ->
                    "감정 표현이 적거나 많다는 사실보다 상대가 이해할 수 있는 방식으로 표현하는지가 중요하다. 자신에게는 분명한 행동도 상대에게는 애매하게 느껴질 수 있으므로 확인과 설명이 필요하다.";

            case PARTNER_LUCK ->
                    "배우자운은 결혼 여부만을 의미하지 않는다. 장기 동업자, 함께 생활하는 사람, 중요한 결정을 공유하는 관계에도 같은 패턴이 나타날 수 있다.";

            case WEALTH ->
                    "재물운에서는 수입보다 순자산의 변화를 기록하는 것이 중요하다. 매달 들어온 돈, 고정비, 부채 감소액, 비상금 변화를 표로 관리하면 사주의 재물 성향을 현실적으로 보완할 수 있다.";

            case CAREER ->
                    "직업운을 키우려면 여러 기술을 얕게 늘리는 것보다 한 분야에서 문제를 끝까지 해결한 경험을 만드는 편이 유리하다. 결과물을 남길 수 있는 프로젝트가 경력의 신뢰도를 높인다.";

            case LEGAL_RISK ->
                    "분쟁이 생겼을 때 가장 위험한 행동은 감정적으로 긴 메시지를 반복해서 보내는 것이다. 사실관계, 요구사항, 기한을 짧고 명확하게 정리하는 방식이 훨씬 안전하다.";

            case HEALTH_TENDENCY ->
                    "건강 관리에서는 단기적인 보조제나 극단적인 운동보다 일정한 수면, 걷기, 체중 관리, 정기 검진이 더 중요하다. 증상이 있으면 의료 전문가의 판단을 우선해야 한다.";

            case CURRENT_LUCK ->
                    "현재 대운은 장기 환경이므로 한두 달의 결과로 좋고 나쁨을 판단하면 안 된다. 몇 년 동안 어떤 분야의 책임과 기회가 반복되는지를 관찰해야 정확한 흐름을 볼 수 있다.";

            case YEAR_LUCK ->
                    "세운이 유리한 해에는 실행 속도를 높이되 계약과 자금 검토를 생략하지 않는 것이 중요하다. 부담스러운 해에는 새로운 일을 전부 멈추기보다 손실 한도를 줄이고 검증 단계를 늘리는 편이 낫다.";

            case FUTURE_LUCK ->
                    "미래 대운을 준비할 때는 이직, 창업, 투자 같은 큰 결정만 생각하지 말고 자격, 포트폴리오, 인맥, 부채, 건강처럼 실행을 가능하게 하는 기반을 먼저 정리해야 한다.";
        };
    }

    private void appendParagraph(
            StringBuilder builder,
            String paragraph
    ) {
        if (paragraph == null
                || paragraph.isBlank()) {
            return;
        }

        if (!builder.isEmpty()) {
            builder.append("\n\n");
        }

        builder.append(
                paragraph.trim()
        );
    }

    private String normalizeContent(
            String content
    ) {
        return content
                .replaceAll(
                        "[ \\t]+",
                        " "
                )
                .replaceAll(
                        "\\n{3,}",
                        "\n\n"
                )
                .trim();
    }

    private String trimAtSentenceBoundary(
            String content,
            int maximumLength
    ) {
        if (content.length()
                <= maximumLength) {

            return content;
        }

        int searchStart =
                Math.max(
                        0,
                        maximumLength - 200
                );

        int lastPeriod =
                content.lastIndexOf(
                        ".",
                        maximumLength
                );

        if (lastPeriod < searchStart) {
            return content.substring(
                    0,
                    maximumLength
            ).trim();
        }

        return content.substring(
                0,
                lastPeriod + 1
        ).trim();
    }

    private String createFullChineseName(
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

    private String createFullKoreanName(
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