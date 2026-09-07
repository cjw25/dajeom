package com.dajeom.calculation;

import com.dajeom.domain.PatternOfficerKillingsFinalPurificationAnalysis;
import com.dajeom.domain.PatternOfficerKillingsFinalPurificationStatus;
import com.dajeom.domain.PatternOfficerKillingsYangBladeCompletionAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 官殺混雜 최종 purification Resolver.
 *
 * =================================================
 *
 * 입력
 * =================================================
 *
 * mixed
 *
 * 官殺混雜이 실제 존재하는가.
 *
 * -------------------------------------------------
 *
 * standardPurificationCleared
 *
 * 기존 官殺 전용 규칙에서
 * 전체 取清이 이미 완료됐는가.
 *
 * 예:
 *
 * - 去官留殺
 * - 去殺留官
 * - 食神制殺 계열
 * - 傷官去官 계열
 *
 * 등.
 *
 * generic 天干 binding은 여기에 넣으면 안 된다.
 *
 * -------------------------------------------------
 *
 * standardPurificationReviewRequired
 *
 * 기존 官殺 전용 규칙 자체가
 * 확정 불가능 상태인가.
 *
 * -------------------------------------------------
 *
 * yangBladeCompletion
 *
 * retained 七殺 개별 노드에 대한
 * 羊刃合殺 완료 분석.
 *
 * =================================================
 *
 * 최종 precedence
 * =================================================
 *
 * NOT MIXED
 *
 *     ↓
 *
 * STANDARD CLEARED
 *
 *     ↓
 *
 * STANDARD REVIEW
 *
 *     ↓
 *
 * YANG BLADE REVIEW
 *
 *     ↓
 *
 * YANG BLADE COMPLETE
 *
 *     ↓
 *
 * YANG BLADE PARTIAL
 *
 *     ↓
 *
 * UNRESOLVED
 *
 * =================================================
 *
 * 주의
 * =================================================
 *
 * 七殺 여러 개 중 하나만 羊刃合殺됐다고
 * CLEARED로 만들지 않는다.
 */
@Component
public class PatternOfficerKillingsFinalPurificationCalculator {

    public PatternOfficerKillingsFinalPurificationAnalysis calculate(

            boolean mixed,

            boolean standardPurificationCleared,

            boolean standardPurificationReviewRequired,

            PatternOfficerKillingsYangBladeCompletionAnalysis
                    yangBladeCompletion

    ) {

        Objects.requireNonNull(
                yangBladeCompletion,
                "PatternOfficerKillingsYangBladeCompletionAnalysis는 필수입니다."
        );

        /*
         * =================================================
         * 1. 官殺混雜 자체가 없음
         * =================================================
         */

        if (!mixed) {

            return new PatternOfficerKillingsFinalPurificationAnalysis(

                    false,

                    PatternOfficerKillingsFinalPurificationStatus
                            .NOT_MIXED,

                    false,

                    false,

                    yangBladeCompletion
            );
        }

        /*
         * =================================================
         * 2. 기존 官殺 전용 규칙에서 이미 완전 정리
         * =================================================
         *
         * 이 경우 羊刃합살 여부를 추가 조건으로 요구하지 않는다.
         */

        if (
                standardPurificationCleared
        ) {

            return new PatternOfficerKillingsFinalPurificationAnalysis(

                    true,

                    PatternOfficerKillingsFinalPurificationStatus
                            .CLEARED,

                    true,

                    false,

                    yangBladeCompletion
            );
        }

        /*
         * =================================================
         * 3. 기존 官殺 정리 자체가 검토 필요
         * =================================================
         *
         * 앞선 retained 대상 자체가 확정되지 않았다면
         * 羊刃합살 하나를 근거로 전체 완료로 올리지 않는다.
         */

        if (
                standardPurificationReviewRequired
        ) {

            return new PatternOfficerKillingsFinalPurificationAnalysis(

                    true,

                    PatternOfficerKillingsFinalPurificationStatus
                            .REVIEW_REQUIRED,

                    false,

                    false,

                    yangBladeCompletion
            );
        }

        /*
         * =================================================
         * 4. 羊刃合殺 node-level 검토 필요
         * =================================================
         */

        if (
                yangBladeCompletion
                        .reviewRequired()
        ) {

            return new PatternOfficerKillingsFinalPurificationAnalysis(

                    true,

                    PatternOfficerKillingsFinalPurificationStatus
                            .REVIEW_REQUIRED,

                    false,

                    false,

                    yangBladeCompletion
            );
        }

        /*
         * =================================================
         * 5. retained 七殺 전체가 羊刃合殺 완료
         * =================================================
         *
         * 여기서만 羊刃合殺을
         * 전체 purification 완료 근거로 인정한다.
         */

        if (
                yangBladeCompletion
                        .complete()
        ) {

            return new PatternOfficerKillingsFinalPurificationAnalysis(

                    true,

                    PatternOfficerKillingsFinalPurificationStatus
                            .CLEARED,

                    false,

                    true,

                    yangBladeCompletion
            );
        }

        /*
         * =================================================
         * 6. retained 七殺 일부만 처리
         * =================================================
         *
         * 핵심 버그 수정 지점.
         *
         * K1, K2가 남아 있는데
         * K1만 羊刃合殺이면
         *
         * CLEARED X
         * PARTIALLY_CLEARED O
         */

        if (
                yangBladeCompletion
                        .partial()
        ) {

            return new PatternOfficerKillingsFinalPurificationAnalysis(

                    true,

                    PatternOfficerKillingsFinalPurificationStatus
                            .PARTIALLY_CLEARED,

                    false,

                    false,

                    yangBladeCompletion
            );
        }

        /*
         * =================================================
         * 7. retained 七殺이 있지만 해결 없음
         * =================================================
         */

        return new PatternOfficerKillingsFinalPurificationAnalysis(

                true,

                PatternOfficerKillingsFinalPurificationStatus
                        .UNRESOLVED,

                false,

                false,

                yangBladeCompletion
        );
    }
}