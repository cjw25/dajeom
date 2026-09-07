package com.dajeom.calculation;

import com.dajeom.domain.LifeInterpretationCategory;
import com.dajeom.domain.LifeInterpretationPrompt;
import com.dajeom.domain.LifePeriodType;
import com.dajeom.domain.PastFlowNarrativeBundle;
import com.dajeom.domain.PeriodLifeClaimNarrativeDirective;
import com.dajeom.domain.PeriodLifeClaimNarrativeInstruction;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 지나온 흐름(PAST_FLOW) 전용 AI Prompt Builder.
 *
 * 미래 구간은 제외하고,
 * asOf 시점까지 시작된 기간 Claim만 AI에 전달한다.
 */
@Component
public class PastFlowInterpretationPromptBuilder {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "yyyy.MM.dd"
            );

    private static final int TARGET_MIN_CHARACTERS =
            1_800;

    private static final int TARGET_MAX_CHARACTERS =
            2_200;

    public LifeInterpretationPrompt build(
            PastFlowNarrativeBundle narrativeBundle,
            LocalDateTime asOf
    ) {

        Objects.requireNonNull(
                narrativeBundle,
                "PastFlowNarrativeBundle이 필요합니다."
        );

        Objects.requireNonNull(
                asOf,
                "해석 기준 시각이 필요합니다."
        );

        List<PeriodLifeClaimNarrativeInstruction> eligible =
                pastAndCurrentInstructions(
                        narrativeBundle,
                        asOf
                );

        boolean generationAllowed =
                !eligible.isEmpty();

        String userPrompt =
                generationAllowed
                        ? buildUserPrompt(
                        eligible,
                        asOf
                )
                        : buildNoPastFlowPrompt(
                        asOf
                );

        return new LifeInterpretationPrompt(
                LifeInterpretationCategory.PAST_FLOW,
                buildSystemPrompt(),
                userPrompt,
                generationAllowed
        );
    }

    private List<PeriodLifeClaimNarrativeInstruction>
    pastAndCurrentInstructions(
            PastFlowNarrativeBundle narrativeBundle,
            LocalDateTime asOf
    ) {

        List<PeriodLifeClaimNarrativeInstruction> result =
                new ArrayList<>();

        for (
                PeriodLifeClaimNarrativeInstruction instruction
                : narrativeBundle.orderedInstructions()
        ) {

            Objects.requireNonNull(
                    instruction,
                    "PeriodLifeClaimNarrativeInstruction은 null일 수 없습니다."
            );

            if (
                    instruction
                            .period()
                            .startInclusive()
                            .isAfter(
                                    asOf
                            )
            ) {

                continue;
            }

            result.add(
                    instruction
            );
        }

        result.sort(
                Comparator
                        .comparing(
                                (PeriodLifeClaimNarrativeInstruction instruction) ->
                                        instruction
                                                .period()
                                                .startInclusive()
                        )
                        .thenComparing(
                                instruction ->
                                        instruction
                                                .periodType()
                                                .ordinal()
                        )
                        .thenComparing(
                                instruction ->
                                        instruction
                                                .period()
                                                .key()
                        )
                        .thenComparing(
                                instruction ->
                                        instruction
                                                .claim()
                                                .code()
                                                .ordinal()
                        )
        );

        return List.copyOf(
                result
        );
    }

    private String buildSystemPrompt() {

        return """
                당신은 Java 명리 규칙 엔진이 이미 확정한 기간별 흐름을
                사용자가 이해하기 쉬운 한국어 장문으로 문장화하는 작성기입니다.

                사주를 다시 계산하거나 새로운 명리 판단을 추가하면 안 됩니다.
                제공된 기간과 해석 재료의 범위 안에서만 '지나온 흐름'을 작성하세요.

                반드시 다음 규칙을 지키세요.

                1. 제공되지 않은 실제 과거 사건을 만들어내지 마세요.
                취업, 이직, 퇴직, 창업, 합격, 승진, 결혼, 이혼, 출산,
                투자, 재산 증감, 사고, 질병, 수술 등의 사건을 임의로 확정하면 안 됩니다.

                2. 기간의 구조적 변화는 설명할 수 있지만
                그것이 특정 실제 사건을 반드시 일으켰다고 해석하지 마세요.

                3. 사용자의 과거를 이미 알고 있는 것처럼 단정하지 마세요.
                사건이 아니라 그 시기에 어떤 요구, 부담, 선택 방식,
                생활 방향이 중요해지기 쉬웠는지를 설명하세요.

                4. 대운, 세운, 월운, 일간, 월령, 십성, 격국,
                PeriodLifeClaim, PeriodLifeSignal, PeriodMyeongriInfluence 같은
                내부 명리 용어와 코드명을 최종 본문에 직접 노출하지 마세요.

                5. 긴 기간의 흐름을 먼저 설명하고,
                더 짧은 기간의 재료가 있을 경우 그 안에서 방향이 어떻게 보완되거나
                달라지는지 자연스럽게 연결하세요.

                6. 여러 기간 Claim을 시간순 목록처럼 기계적으로 나열하지 마세요.
                반복되는 주제와 변화의 방향을 중심으로 하나의 흐름으로 설명하세요.

                7. 현재 진행 중인 기간은 해석 기준 시점까지만 설명하세요.
                아직 지나오지 않은 미래 부분은 예측하지 마세요.

                8. 좋은 시기/나쁜 시기로 단순 평가하지 마세요.
                무엇이 중요해지기 쉬웠는지,
                어떤 부담이 커지기 쉬웠는지,
                어떤 대응 방식이 구조를 잘 쓰는 방향인지 함께 설명하세요.

                9. 분량은 한국어 문자 기준 1,800자 이상 2,200자 이하를 목표로 하세요.
                1,800자보다 짧게 끝내지 마세요.
                다만 분량을 맞추기 위해 실제 사건이나 새로운 명리 판단을 만들면 안 됩니다.

                10. 제공된 재료가 충분하다면 기간 사이의 공통점,
                변화 지점, 반복되는 주제, 대응 방식의 변화까지 충분히 풀어서 설명하세요.

                11. 제목, 번호 목록, 내부 근거 목록 없이
                자연스럽게 이어지는 한국어 본문만 출력하세요.

                12. 제공된 금지 해석은 다른 모든 작성 요구보다 우선합니다.
                """
                .trim();
    }

    private String buildUserPrompt(
            List<PeriodLifeClaimNarrativeInstruction> instructions,
            LocalDateTime asOf
    ) {

        StringBuilder builder =
                new StringBuilder();

        builder.append(
                "다음은 Java 규칙 엔진이 확정한 과거 및 현재까지의 기간별 흐름입니다.\n"
        );

        builder.append(
                "해석 기준일: "
        );

        builder.append(
                DATE_FORMATTER.format(
                        asOf.toLocalDate()
                )
        );

        builder.append(
                "\n\n"
        );

        appendPeriodTypeSection(
                builder,
                "장기적인 흐름",
                LifePeriodType.DAEWOON,
                instructions,
                asOf
        );

        appendPeriodTypeSection(
                builder,
                "연간 흐름",
                LifePeriodType.SEWOON,
                instructions,
                asOf
        );

        appendPeriodTypeSection(
                builder,
                "세부 흐름",
                LifePeriodType.WOLWOON,
                instructions,
                asOf
        );

        builder.append(
                "[최종 작성 방식]\n"
        );

        builder.append(
                "- 긴 기간의 변화 방향을 중심축으로 먼저 설명하세요.\n"
        );

        builder.append(
                "- 연간 또는 더 짧은 흐름의 재료가 있으면 큰 흐름을 구체화하거나 방향이 달라지는 지점을 설명할 때만 사용하세요.\n"
        );

        builder.append(
                "- 같은 의미가 여러 기간에 반복되면 각각 반복 설명하지 말고 반복되는 주제로 묶으세요.\n"
        );

        builder.append(
                "- 실제 사건을 알고 있는 것처럼 쓰지 마세요.\n"
        );

        builder.append(
                "- 현재 진행 중인 기간은 해석 기준일까지의 흐름만 설명하고 미래를 예측하지 마세요.\n"
        );

        builder.append(
                "- 변화 지점에서는 이전 흐름과 이후 흐름의 차이를 자연스럽게 설명하세요.\n"
        );

        builder.append(
                "- 제목 없이 본문만 출력하세요.\n"
        );

        builder.append(
                "- 최종 본문은 한국어 문자 기준 최소 "
        );

        builder.append(
                TARGET_MIN_CHARACTERS
        );

        builder.append(
                "자, 최대 "
        );

        builder.append(
                TARGET_MAX_CHARACTERS
        );

        builder.append(
                "자 범위로 작성하세요.\n"
        );

        builder.append(
                "- 1,800자에 도달하기 전에 요약을 끝내지 말고, 제공된 범위 안에서 기간 간 연결, 반복 주제, 변화 지점, 대응 방식을 충분히 설명하세요.\n"
        );

        builder.append(
                "- 분량 확보를 위해 존재하지 않는 사건이나 새로운 명리 판단을 추가하면 안 됩니다."
        );

        return builder
                .toString()
                .trim();
    }

    private void appendPeriodTypeSection(
            StringBuilder builder,
            String sectionTitle,
            LifePeriodType type,
            List<PeriodLifeClaimNarrativeInstruction> instructions,
            LocalDateTime asOf
    ) {

        List<PeriodLifeClaimNarrativeInstruction> filtered =
                instructions
                        .stream()
                        .filter(
                                instruction ->
                                        instruction
                                                .periodType()
                                                == type
                        )
                        .toList();

        if (filtered.isEmpty()) {

            return;
        }

        builder.append(
                "["
        );

        builder.append(
                sectionTitle
        );

        builder.append(
                "]\n\n"
        );

        String currentPeriodKey =
                null;

        for (
                PeriodLifeClaimNarrativeInstruction instruction
                : filtered
        ) {

            String periodKey =
                    instruction
                            .period()
                            .key();

            if (
                    !periodKey.equals(
                            currentPeriodKey
                    )
            ) {

                if (
                        currentPeriodKey != null
                ) {

                    builder.append(
                            "\n"
                    );
                }

                builder.append(
                        "기간: "
                );

                builder.append(
                        periodRangeText(
                                instruction,
                                asOf
                        )
                );

                builder.append(
                        "\n"
                );

                currentPeriodKey =
                        periodKey;
            }

            appendInstruction(
                    builder,
                    instruction
            );
        }

        builder.append(
                "\n"
        );
    }

    private void appendInstruction(
            StringBuilder builder,
            PeriodLifeClaimNarrativeInstruction instruction
    ) {

        PeriodLifeClaimNarrativeDirective directive =
                instruction.directive();

        builder.append(
                "해석 의미: "
        );

        builder.append(
                directive.coreMeaning()
        );

        builder.append(
                "\n"
        );

        if (
                !directive
                        .requiredDirections()
                        .isEmpty()
        ) {

            builder.append(
                    "반드시 반영할 방향:\n"
            );

            for (
                    String required
                    : directive.requiredDirections()
            ) {

                builder.append(
                        "- "
                );

                builder.append(
                        required
                );

                builder.append(
                        "\n"
                );
            }
        }

        if (
                !directive
                        .prohibitedExpansions()
                        .isEmpty()
        ) {

            builder.append(
                    "금지되는 사건 단정 또는 확장:\n"
            );

            for (
                    String prohibited
                    : directive.prohibitedExpansions()
            ) {

                builder.append(
                        "- "
                );

                builder.append(
                        prohibited
                );

                builder.append(
                        "\n"
                );
            }
        }
    }

    private String periodRangeText(
            PeriodLifeClaimNarrativeInstruction instruction,
            LocalDateTime asOf
    ) {

        LocalDate startDate =
                instruction
                        .period()
                        .startInclusive()
                        .toLocalDate();

        LocalDateTime endExclusive =
                instruction
                        .period()
                        .endExclusive();

        boolean current =
                !asOf.isBefore(
                        instruction
                                .period()
                                .startInclusive()
                )
                        &&
                        asOf.isBefore(
                                endExclusive
                        );

        if (current) {

            return DATE_FORMATTER.format(
                    startDate
            )
                    + " ~ "
                    + DATE_FORMATTER.format(
                    asOf.toLocalDate()
            )
                    + " 현재까지";
        }

        return DATE_FORMATTER.format(
                startDate
        )
                + " ~ "
                + DATE_FORMATTER.format(
                endExclusive.toLocalDate()
        );
    }

    private String buildNoPastFlowPrompt(
            LocalDateTime asOf
    ) {

        return """
                Java 규칙 엔진에서 해석 기준일까지 확정된 기간별 흐름 재료가 없습니다.

                이 상태에서는 원국만 이용하여 특정 나이 또는 특정 연도의
                과거 사건을 임의로 만들어내면 안 됩니다.

                지나온 흐름 본문 생성을 실행하지 마세요.

                해석 기준일: %s
                """
                .formatted(
                        DATE_FORMATTER.format(
                                asOf.toLocalDate()
                        )
                )
                .trim();
    }
}
