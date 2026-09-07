package com.dajeom.service;

import com.dajeom.domain.LifeInterpretationGenerationResult;
import com.dajeom.domain.LifeInterpretationPreparationResult;
import com.dajeom.domain.LifeInterpretationTextBundle;
import com.dajeom.dto.FiveElementDistributionDto;
import com.dajeom.dto.PublicSajuPillarsDto;
import com.dajeom.dto.SajuInterpretationResponse;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * AI 사주 종합풀이의 최종 실행 Service.
 *
 * =================================================
 * 입력
 * =================================================
 *
 * 1. PublicSajuPillarsDto
 *
 * 사용자에게 공개할 사주팔자 4기둥.
 *
 * -------------------------------------------------
 *
 * 2. FiveElementDistributionDto
 *
 * 사주팔자 8글자 기준 공개 오행분포.
 *
 * -------------------------------------------------
 *
 * 3. LifeInterpretationPreparationResult
 *
 * Java Rule Engine이 준비한
 * 최종 7개 AI Prompt.
 *
 * =================================================
 * 실행
 * =================================================
 *
 * LifeInterpretationPreparationResult
 *
 *      ↓
 *
 * LifeInterpretationGenerationService
 *
 *      ↓
 *
 * AI 7개 장문 생성
 *
 *      ↓
 *
 * LifeInterpretationGenerationResult
 *
 *      ↓
 *
 * 7개 완성 여부 확인
 *
 *      ↓
 *
 * LifeInterpretationTextBundle
 *
 *      ↓
 *
 * SajuInterpretationResponseAssembler
 *
 *      ↓
 *
 * SajuInterpretationResponse
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 1. 7개 중 하나라도 생성되지 않으면
 *    최종 공개 응답을 만들지 않는다.
 *
 * 2. generationAllowed=false인 영역을
 *    임의 텍스트로 채우지 않는다.
 *
 * 3. AI 생성 결과를 이 Service에서
 *    다시 명리적으로 해석하거나 수정하지 않는다.
 *
 * 4. 공개 응답에는:
 *
 *    - 4기둥
 *    - 오행분포
 *    - 7개 장문풀이
 *
 *    만 포함한다.
 */
@Service
public class SajuInterpretationExecutionService {

    private final LifeInterpretationGenerationService
            generationService;

    private final SajuInterpretationResponseAssembler
            responseAssembler;

    public SajuInterpretationExecutionService(

            LifeInterpretationGenerationService
                    generationService,

            SajuInterpretationResponseAssembler
                    responseAssembler

    ) {

        this.generationService =
                Objects.requireNonNull(
                        generationService,
                        "LifeInterpretationGenerationService는 필수입니다."
                );

        this.responseAssembler =
                Objects.requireNonNull(
                        responseAssembler,
                        "SajuInterpretationResponseAssembler는 필수입니다."
                );
    }

    /**
     * 준비된 Prompt를 실제 AI로 생성하고
     * 최종 사용자 공개 응답까지 반환한다.
     */
    public SajuInterpretationResponse execute(

            PublicSajuPillarsDto pillars,

            FiveElementDistributionDto fiveElements,

            LifeInterpretationPreparationResult preparation

    ) {

        Objects.requireNonNull(
                pillars,
                "PublicSajuPillarsDto는 필수입니다."
        );

        Objects.requireNonNull(
                fiveElements,
                "FiveElementDistributionDto는 필수입니다."
        );

        Objects.requireNonNull(
                preparation,
                "LifeInterpretationPreparationResult는 필수입니다."
        );

        /*
         * =================================================
         * 1. AI 생성
         * =================================================
         */

        LifeInterpretationGenerationResult generation =
                generationService
                        .generate(
                                preparation
                        );

        /*
         * =================================================
         * 2. 7개 완성 여부
         * =================================================
         *
         * 하나라도 없으면
         * 공개 응답을 만들지 않는다.
         */

        if (
                !generation.complete()
        ) {

            throw new LifeInterpretationIncompleteException(

                    generation.missingCategories(),

                    generation.blockedCategories()
            );
        }

        /*
         * =================================================
         * 3. 최종 본문 Bundle
         * =================================================
         */

        LifeInterpretationTextBundle texts =
                generation
                        .requireCompleteBundle();

        /*
         * =================================================
         * 4. 공개 Response
         * =================================================
         */

        return responseAssembler
                .assemble(
                        pillars,
                        fiveElements,
                        texts
                );
    }
}