package com.dajeom.domain;

import java.util.Objects;

/**
 * 官殺混雜 최종 판정을 수행하는 과정에서 만들어진
 * 중간 결과와 최종 결과를 하나로 묶는다.
 *
 * =================================================
 * 구조
 * =================================================
 *
 * PatternOfficerKillingsStandardPurificationSnapshot
 *
 * +
 *
 * PatternOfficerKillingsYangBladeNodeSnapshot
 *
 * ↓
 *
 * PatternOfficerKillingsFinalPurificationInput
 *
 * ↓
 *
 * PatternOfficerKillingsFinalPurificationAnalysis
 *
 * =================================================
 *
 * 사용자 공개 DTO가 아니다.
 *
 * 내부 Rule 검증과 LifeClaim 연결 단계에서
 * "어떤 입력으로 어떤 최종 결과가 나왔는가"를
 * 추적하기 위한 객체다.
 */
public record PatternOfficerKillingsFinalPurificationPreparation(

        PatternOfficerKillingsStandardPurificationSnapshot standard,

        PatternOfficerKillingsYangBladeNodeSnapshot yangBlade,

        PatternOfficerKillingsFinalPurificationInput input,

        PatternOfficerKillingsFinalPurificationAnalysis analysis

) {

    public PatternOfficerKillingsFinalPurificationPreparation {

        Objects.requireNonNull(
                standard,
                "표준 官殺 purification snapshot은 필수입니다."
        );

        Objects.requireNonNull(
                yangBlade,
                "羊刃合殺 node snapshot은 필수입니다."
        );

        Objects.requireNonNull(
                input,
                "官殺 최종 purification input은 필수입니다."
        );

        Objects.requireNonNull(
                analysis,
                "官殺 최종 purification analysis는 필수입니다."
        );

        validateInputConsistency(
                standard,
                yangBlade,
                input
        );

        validateAnalysisConsistency(
                input,
                analysis
        );
    }

    public boolean mixed() {

        return analysis.mixed();
    }

    public boolean cleared() {

        return analysis.mixtureCleared();
    }

    public boolean partiallyCleared() {

        return analysis.partiallyCleared();
    }

    public boolean reviewRequired() {

        return analysis.reviewRequired();
    }

    public boolean unresolved() {

        return analysis.unresolved();
    }

    public boolean notMixed() {

        return analysis.notMixed();
    }

    /**
     * 羊刃合殺이 실제 최종 官殺 정리 완료에
     * 기여했는가.
     */
    public boolean clearedByYangBlade() {

        return analysis
                .yangBladeActuallyCompletedPurification();
    }

    private static void validateInputConsistency(

            PatternOfficerKillingsStandardPurificationSnapshot standard,

            PatternOfficerKillingsYangBladeNodeSnapshot yangBlade,

            PatternOfficerKillingsFinalPurificationInput input

    ) {

        if (
                input.mixed()
                        != standard.mixed()
        ) {

            throw new IllegalArgumentException(
                    "FinalPurificationInput의 mixed 값이 standard snapshot과 일치하지 않습니다."
            );
        }

        if (
                input.standardPurificationCleared()
                        != standard.cleared()
        ) {

            throw new IllegalArgumentException(
                    "FinalPurificationInput의 standard cleared 값이 standard snapshot과 일치하지 않습니다."
            );
        }

        if (
                input.standardPurificationReviewRequired()
                        != standard.reviewRequired()
        ) {

            throw new IllegalArgumentException(
                    "FinalPurificationInput의 standard review 값이 standard snapshot과 일치하지 않습니다."
            );
        }

        if (
                !input
                        .retainedKillingNodes()
                        .equals(
                                standard.retainedKillingNodes()
                        )
        ) {

            throw new IllegalArgumentException(
                    "FinalPurificationInput의 retained 七殺 노드가 standard snapshot과 일치하지 않습니다."
            );
        }

        if (
                !input
                        .yangBladeResolvedKillingNodes()
                        .equals(
                                yangBlade.resolvedKillingNodes()
                        )
        ) {

            throw new IllegalArgumentException(
                    "FinalPurificationInput의 羊刃合殺 resolved 노드가 yangBlade snapshot과 일치하지 않습니다."
            );
        }

        if (
                !input
                        .yangBladeReviewKillingNodes()
                        .equals(
                                yangBlade.reviewKillingNodes()
                        )
        ) {

            throw new IllegalArgumentException(
                    "FinalPurificationInput의 羊刃合殺 review 노드가 yangBlade snapshot과 일치하지 않습니다."
            );
        }
    }

    private static void validateAnalysisConsistency(

            PatternOfficerKillingsFinalPurificationInput input,

            PatternOfficerKillingsFinalPurificationAnalysis analysis

    ) {

        if (
                analysis.mixed()
                        != input.mixed()
        ) {

            throw new IllegalArgumentException(
                    "최종 官殺 analysis의 mixed 값이 input과 일치하지 않습니다."
            );
        }

        /*
         * 官殺混雜 자체가 아닌 경우
         * 최종 analysis도 반드시 NOT_MIXED.
         */
        if (
                !input.mixed()
                        &&
                        !analysis.notMixed()
        ) {

            throw new IllegalArgumentException(
                    "官殺混雜이 아닌 입력은 최종적으로 NOT_MIXED여야 합니다."
            );
        }

        /*
         * 표준 정리가 이미 완료됐는데
         * 최종 결과가 미정리일 수 없다.
         */
        if (
                input.standardPurificationCleared()
                        &&
                        !analysis.mixtureCleared()
        ) {

            throw new IllegalArgumentException(
                    "표준 官殺 purification이 완료된 경우 최종 결과도 CLEARED여야 합니다."
            );
        }

        /*
         * standard review라면
         * 최종 CLEARED로 임의 승격할 수 없다.
         */
        if (
                input.standardPurificationReviewRequired()
                        &&
                        analysis.mixtureCleared()
        ) {

            throw new IllegalArgumentException(
                    "표준 官殺 purification이 REVIEW_REQUIRED인 상태를 최종 CLEARED로 승격할 수 없습니다."
            );
        }
    }
}