package com.dajeom.service;

import com.dajeom.domain.LifeInterpretationCategory;
import com.dajeom.domain.LifeInterpretationGenerationResult;
import com.dajeom.domain.LifeInterpretationPreparationResult;
import com.dajeom.domain.LifeInterpretationPrompt;
import com.dajeom.domain.LifeInterpretationPromptBundle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 최종 7개 사주 장문풀이 AI 생성 실행 Service.
 *
 * =================================================
 *
 * 입력
 *
 * LifeInterpretationPreparationResult
 *
 * 또는
 *
 * LifeInterpretationPromptBundle
 *
 * =================================================
 *
 * 처리
 *
 * 각 카테고리 Prompt 확인
 *
 * generationAllowed=true
 *      ↓
 * AI 호출
 *
 * generationAllowed=false
 *      ↓
 * AI 호출하지 않음
 *
 * =================================================
 *
 * 출력
 *
 * LifeInterpretationGenerationResult
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1. generationAllowed=false를 절대로 우회하지 않는다.
 *
 * 2. 특히 PAST_FLOW에 실제 기간 Claim이 없으면
 *    AI에게 원국만으로 과거를 만들어내게 하지 않는다.
 *
 * 3. AI가 빈 문자열을 반환하면 정상 생성으로 인정하지 않는다.
 *
 * 4. 일부 생성만 완료된 상태에서
 *    임의의 기본 문장으로 7개를 채우지 않는다.
 *
 * 5. 7개가 실제로 모두 생성됐을 때만
 *    LifeInterpretationTextBundle을 만들 수 있다.
 */
@Service
public class LifeInterpretationGenerationService {

    private final LifeInterpretationTextGenerator
            textGenerator;

    public LifeInterpretationGenerationService(
            LifeInterpretationTextGenerator textGenerator
    ) {

        this.textGenerator =
                Objects.requireNonNull(
                        textGenerator,
                        "LifeInterpretationTextGenerator는 필수입니다."
                );
    }

    /**
     * InterpretationPreparationService의
     * 최종 결과를 바로 생성한다.
     */
    public LifeInterpretationGenerationResult generate(
            LifeInterpretationPreparationResult preparation
    ) {

        Objects.requireNonNull(
                preparation,
                "LifeInterpretationPreparationResult는 필수입니다."
        );

        return generate(
                preparation.prompts()
        );
    }

    /**
     * 최종 PromptBundle의 7개 카테고리를
     * 순서대로 처리한다.
     */
    public LifeInterpretationGenerationResult generate(
            LifeInterpretationPromptBundle prompts
    ) {

        Objects.requireNonNull(
                prompts,
                "LifeInterpretationPromptBundle은 필수입니다."
        );

        Map<
                LifeInterpretationCategory,
                String
                > generatedTexts =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        List<LifeInterpretationCategory> blockedCategories =
                new ArrayList<>();

        /*
         * =================================================
         * LifeInterpretationCategory.values() 순서대로 실행
         * =================================================
         *
         * 현재 enum 선언 순서는:
         *
         * 1. TEMPERAMENT
         * 2. PAST_FLOW
         * 3. FAMILY_RELATIONSHIPS
         * 4. LOVE_MARRIAGE
         * 5. WEALTH
         * 6. WORK_ACHIEVEMENT
         * 7. HEALTH_LIFESTYLE
         *
         * 이 순서대로 AI 호출한다.
         */

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            LifeInterpretationPrompt prompt =
                    Objects.requireNonNull(
                            prompts.promptFor(
                                    category
                            ),
                            "PromptBundle에 "
                                    + category
                                    + " Prompt가 없습니다."
                    );

            validatePromptCategory(
                    category,
                    prompt
            );

            /*
             * =================================================
             * 생성 금지 Prompt
             * =================================================
             *
             * AI 호출 자체를 하지 않는다.
             */

            if (
                    !prompt.generationAllowed()
            ) {

                blockedCategories.add(
                        category
                );

                continue;
            }

            /*
             * =================================================
             * 실제 AI 호출
             * =================================================
             */

            String generated =
                    textGenerator.generate(
                            prompt
                    );

            String normalized =
                    normalizeGeneratedText(
                            category,
                            generated
                    );

            generatedTexts.put(
                    category,
                    normalized
            );
        }

        return new LifeInterpretationGenerationResult(
                generatedTexts,
                blockedCategories
        );
    }

    /*
     * =================================================
     * Prompt와 반복 카테고리가 같은지 확인
     * =================================================
     */

    private void validatePromptCategory(

            LifeInterpretationCategory expected,

            LifeInterpretationPrompt prompt

    ) {

        if (
                prompt.category()
                        != expected
        ) {

            throw new IllegalStateException(
                    "Prompt 카테고리가 일치하지 않습니다. expected="
                            + expected
                            + ", actual="
                            + prompt.category()
            );
        }
    }

    /*
     * =================================================
     * AI 반환값 검증
     * =================================================
     *
     * 생성된 본문을 수정하거나 재해석하지 않는다.
     *
     * 앞뒤 공백만 제거한다.
     */

    private String normalizeGeneratedText(

            LifeInterpretationCategory category,

            String generated

    ) {

        if (
                generated == null
        ) {

            throw new IllegalStateException(
                    category
                            + " AI 생성 결과가 null입니다."
            );
        }

        String normalized =
                generated.trim();

        if (
                normalized.isEmpty()
        ) {

            throw new IllegalStateException(
                    category
                            + " AI 생성 결과가 비어 있습니다."
            );
        }

        return normalized;
    }
}