package com.dajeom.calculation;

import com.dajeom.domain.LifeCategoryNarrativeDirective;
import com.dajeom.domain.LifeClaimNarrativeBundle;
import com.dajeom.domain.LifeClaimNarrativeDirective;
import com.dajeom.domain.LifeClaimNarrativeInstruction;
import com.dajeom.domain.LifeInterpretationCategory;
import com.dajeom.domain.LifeInterpretationPrompt;
import com.dajeom.domain.LifeInterpretationPromptBundle;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Java 명리 규칙 엔진이 확정한 LifeClaim Narrative를
 * AI가 문장화할 수 있는 Prompt로 변환한다.
 *
 * AI는 명리 계산이나 새로운 판단을 하지 않고,
 * 전달받은 해석 재료와 금지 범위 안에서만 글을 작성한다.
 */
@Component
public class LifeInterpretationPromptBuilder {

    private static final int TARGET_MIN_CHARACTERS =
            1_800;

    private static final int TARGET_MAX_CHARACTERS =
            2_200;

    public LifeInterpretationPromptBundle build(
            LifeClaimNarrativeBundle narrativeBundle
    ) {

        Objects.requireNonNull(
                narrativeBundle,
                "LifeClaimNarrativeBundle이 필요합니다."
        );

        Map<LifeInterpretationCategory, LifeInterpretationPrompt> prompts =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            List<LifeClaimNarrativeInstruction> instructions =
                    narrativeBundle
                            .instructionsFor(
                                    category
                            );

            boolean generationAllowed =
                    !instructions.isEmpty();

            String userPrompt =
                    generationAllowed
                            ? buildCategoryPrompt(
                            category,
                            instructions
                    )
                            : buildInsufficientClaimPrompt(
                            category
                    );

            prompts.put(
                    category,
                    new LifeInterpretationPrompt(
                            category,
                            buildSystemPrompt(),
                            userPrompt,
                            generationAllowed
                    )
            );
        }

        return new LifeInterpretationPromptBundle(
                prompts
        );
    }

    private String buildSystemPrompt() {

        return """
                당신은 Java 명리 규칙 엔진이 이미 확정한 해석 재료를
                자연스러운 한국어 장문으로 문장화하는 작성기입니다.

                당신의 역할은 사주를 다시 계산하거나 판단하는 것이 아닙니다.
                제공된 해석 재료와 작성 지침의 범위 안에서만 글을 작성해야 합니다.

                반드시 다음 규칙을 지키세요.

                1. 제공되지 않은 새로운 사주 판단을 추가하지 마세요.

                2. 제공된 여러 해석 재료를 단순 나열하지 말고,
                서로 어떤 방식으로 연결되는지 자연스러운 하나의 글로 설명하세요.

                3. 내부 계산 용어, enum 이름, 코드명, LifeClaim, ClaimCode,
                BasisCode 같은 내부 표현을 사용자에게 노출하지 마세요.

                4. 명리 구조를 사용자가 이해할 수 있는 생활 언어로 바꾸어 설명하세요.

                5. 미래나 실제 사건을 단정하지 마세요.
                특정 연도, 나이, 금액, 질병명, 직업명, 배우자 특징 등
                제공되지 않은 구체 사실을 임의로 만들어내지 마세요.

                6. 좋은 사주/나쁜 사주처럼 전체를 단순 평가하지 마세요.
                강점, 부담이 커지는 조건, 조절 방향을 함께 설명하세요.

                7. 재물, 결혼, 건강, 가족, 직업과 관련된 실제 사건을
                근거 없이 만들어내지 마세요.

                8. 같은 의미를 표현만 바꾸어 반복하면서 분량을 채우지 마세요.

                9. 서로 다른 해석 재료가 있다면 각각 따로 나열하지 말고
                한 사람 안에서 어떻게 동시에 나타날 수 있는지 연결하세요.

                10. 최종 출력은 제목, 번호 목록, 분석 근거 목록 없이
                자연스럽게 이어지는 한국어 본문만 작성하세요.

                11. 화면에서 카테고리 제목을 별도로 표시하므로
                본문 안에서 카테고리 제목을 다시 출력하지 마세요.

                12. 분량은 한국어 문자 기준 1,800자 이상 2,200자 이하를 목표로 하세요.
                1,800자보다 짧게 끝내지 마세요.
                다만 분량을 맞추려고 새로운 사실이나 새로운 명리 판단을 만들면 안 됩니다.

                13. 해석 재료가 충분하다면 각 재료의 의미,
                서로 결합되었을 때의 특징,
                부담이 커지는 상황,
                현실적인 조절 방식까지 충분히 풀어서 설명해 분량을 확보하세요.

                14. 제공된 금지 해석과 금지 확장은
                다른 모든 작성 요구보다 우선합니다.
                """
                .trim();
    }

    private String buildCategoryPrompt(
            LifeInterpretationCategory category,
            List<LifeClaimNarrativeInstruction> instructions
    ) {

        if (instructions.isEmpty()) {

            throw new IllegalArgumentException(
                    "LifeClaimNarrativeInstruction이 없는 카테고리의 일반 Prompt는 만들 수 없습니다."
            );
        }

        LifeCategoryNarrativeDirective categoryDirective =
                instructions
                        .get(0)
                        .categoryDirective();

        validateCategoryInstructions(
                category,
                categoryDirective,
                instructions
        );

        StringBuilder builder =
                new StringBuilder();

        builder.append(
                "현재 작성할 해석 영역은 \""
        );

        builder.append(
                category.koreanLabel()
        );

        builder.append(
                "\"입니다.\n\n"
        );

        builder.append(
                "[이 영역의 초점]\n"
        );

        builder.append(
                categoryDirective.focus()
        );

        builder.append(
                "\n\n"
        );

        appendStringList(
                builder,
                "[이 영역에서 반드시 지킬 방향]",
                categoryDirective.requiredDirections()
        );

        appendStringList(
                builder,
                "[이 영역에서 금지되는 확장 해석]",
                categoryDirective.prohibitedExpansions()
        );

        builder.append(
                "[Java 규칙 엔진이 확정한 해석 재료]\n\n"
        );

        int materialNumber =
                1;

        for (
                LifeClaimNarrativeInstruction instruction
                : instructions
        ) {

            LifeClaimNarrativeDirective directive =
                    instruction
                            .claimDirective();

            builder.append(
                    "해석 재료 "
            );

            builder.append(
                    materialNumber
            );

            builder.append(
                    "\n"
            );

            builder.append(
                    "핵심 의미: "
            );

            builder.append(
                    directive.coreMeaning()
            );

            builder.append(
                    "\n"
            );

            appendStringList(
                    builder,
                    "반드시 반영할 방향",
                    directive.requiredDirections()
            );

            appendStringList(
                    builder,
                    "이 재료에서 금지되는 확장 해석",
                    directive.prohibitedExpansions()
            );

            builder.append(
                    "\n"
            );

            materialNumber++;
        }

        builder.append(
                "[최종 작성 요구]\n"
        );

        builder.append(
                "- 위 해석 재료만 사용하세요.\n"
        );

        builder.append(
                "- 여러 재료가 있으면 서로 연결하여 한 사람의 일관된 특성으로 풀어쓰세요.\n"
        );

        builder.append(
                "- 강점만 나열하지 말고 부담이 커지는 조건과 현실적인 조절 방향도 함께 설명하세요.\n"
        );

        builder.append(
                "- 실제로 일어난 사건을 알고 있는 것처럼 쓰지 마세요.\n"
        );

        builder.append(
                "- 같은 내용을 표현만 바꾸어 반복하지 마세요.\n"
        );

        builder.append(
                "- 제목 없이 자연스럽게 이어지는 본문만 출력하세요.\n"
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
                "- 1,800자에 도달하기 전에 결론을 내리지 말고, 제공된 재료 안에서 의미·상호작용·상황별 표현·조절 방향을 충분히 설명하세요.\n"
        );

        builder.append(
                "- 분량 확보를 위해 새로운 사건이나 새로운 명리 판단을 추가해서는 안 됩니다."
        );

        return builder
                .toString()
                .trim();
    }

    private String buildInsufficientClaimPrompt(
            LifeInterpretationCategory category
    ) {

        return """
                현재 "%s" 카테고리는 Java 규칙 엔진에서 확정된 해석 재료가 없습니다.

                이 상태에서는 일반적인 사주 상식이나 임의의 판단을 이용해
                본문을 만들어내면 안 됩니다.

                이 Prompt는 AI 생성 대상으로 사용하지 마세요.
                """
                .formatted(
                        category.koreanLabel()
                )
                .trim();
    }

    private void appendStringList(
            StringBuilder builder,
            String title,
            List<String> values
    ) {

        if (
                values == null
                        ||
                        values.isEmpty()
        ) {

            return;
        }

        builder.append(
                title
        );

        builder.append(
                "\n"
        );

        for (
                String value
                : values
        ) {

            builder.append(
                    "- "
            );

            builder.append(
                    value
            );

            builder.append(
                    "\n"
            );
        }

        builder.append(
                "\n"
        );
    }

    private void validateCategoryInstructions(
            LifeInterpretationCategory category,
            LifeCategoryNarrativeDirective categoryDirective,
            List<LifeClaimNarrativeInstruction> instructions
    ) {

        Objects.requireNonNull(
                categoryDirective,
                "LifeCategoryNarrativeDirective가 필요합니다."
        );

        if (
                categoryDirective.category()
                        != category
        ) {

            throw new IllegalArgumentException(
                    "CategoryDirective의 카테고리가 Prompt 생성 대상과 일치하지 않습니다."
            );
        }

        for (
                LifeClaimNarrativeInstruction instruction
                : instructions
        ) {

            Objects.requireNonNull(
                    instruction,
                    "NarrativeInstruction은 null일 수 없습니다."
            );

            if (
                    instruction.category()
                            != category
            ) {

                throw new IllegalArgumentException(
                        "NarrativeInstruction의 카테고리가 Prompt 생성 대상과 일치하지 않습니다."
                );
            }

            if (
                    instruction
                            .categoryDirective()
                            .category()
                            != category
            ) {

                throw new IllegalArgumentException(
                        "NarrativeInstruction 내부 CategoryDirective가 일치하지 않습니다."
                );
            }
        }
    }
}
