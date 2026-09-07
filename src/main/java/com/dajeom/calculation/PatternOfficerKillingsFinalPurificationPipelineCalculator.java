package com.dajeom.calculation;

import com.dajeom.domain.PatternOfficerKillingsFinalPurificationAnalysis;
import com.dajeom.domain.PatternOfficerKillingsFinalPurificationInput;
import com.dajeom.domain.PatternOfficerKillingsYangBladeCompletionAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 官殺混雜 최종 판정 전체 Pipeline.
 *
 * =================================================
 * 처리 순서
 * =================================================
 *
 * 기존 官殺 전용 정리 규칙
 *
 *      ↓
 *
 * PatternOfficerKillingsFinalPurificationInput
 *
 *      ↓
 *
 * retained 七殺별
 * 羊刃合殺 Completion 판정
 *
 *      ↓
 *
 * PatternOfficerKillingsFinalPurificationCalculator
 *
 *      ↓
 *
 * PatternOfficerKillingsFinalPurificationAnalysis
 *
 * =================================================
 *
 * 이 Pipeline 이후 결과만
 * LifeClaim의 官殺 최종 권위 판정으로 사용한다.
 *
 * =================================================
 * 중요한 금지
 * =================================================
 *
 * 다음 사실만으로 최종 CLEARED를 만들면 안 된다.
 *
 * - 天干五合이 하나 존재
 * - generic FULLY_BOUND가 존재
 * - 羊刃 지지가 존재
 * - 七殺 여러 개 중 하나만 羊刃合殺
 */
@Component
public class PatternOfficerKillingsFinalPurificationPipelineCalculator {

    private final PatternOfficerKillingsYangBladeCompletionCalculator
            yangBladeCompletionCalculator;

    private final PatternOfficerKillingsFinalPurificationCalculator
            finalPurificationCalculator;

    public PatternOfficerKillingsFinalPurificationPipelineCalculator(

            PatternOfficerKillingsYangBladeCompletionCalculator
                    yangBladeCompletionCalculator,

            PatternOfficerKillingsFinalPurificationCalculator
                    finalPurificationCalculator

    ) {

        this.yangBladeCompletionCalculator =
                Objects.requireNonNull(
                        yangBladeCompletionCalculator,
                        "PatternOfficerKillingsYangBladeCompletionCalculator는 필수입니다."
                );

        this.finalPurificationCalculator =
                Objects.requireNonNull(
                        finalPurificationCalculator,
                        "PatternOfficerKillingsFinalPurificationCalculator는 필수입니다."
                );
    }

    /**
     * 官殺 최종 판정을 한 번에 수행한다.
     */
    public PatternOfficerKillingsFinalPurificationAnalysis calculate(
            PatternOfficerKillingsFinalPurificationInput input
    ) {

        Objects.requireNonNull(
                input,
                "PatternOfficerKillingsFinalPurificationInput은 필수입니다."
        );

        /*
         * =================================================
         * 1. 七殺 개별 羊刃合殺 완료 분석
         * =================================================
         *
         * 표준 官殺 정리가 이미 끝났더라도
         * empty set 기반 분석 객체를 생성한다.
         *
         * 이후 Final Calculator가
         * standardPurificationCleared를 우선 사용한다.
         */

        PatternOfficerKillingsYangBladeCompletionAnalysis
                yangBladeCompletion =
                yangBladeCompletionCalculator
                        .calculate(
                                input.retainedKillingNodes(),
                                input.yangBladeResolvedKillingNodes(),
                                input.yangBladeReviewKillingNodes()
                        );

        /*
         * =================================================
         * 2. 최종 官殺 정리
         * =================================================
         */

        return finalPurificationCalculator
                .calculate(

                        input.mixed(),

                        input.standardPurificationCleared(),

                        input.standardPurificationReviewRequired(),

                        yangBladeCompletion
                );
    }
}