package com.dajeom.domain;

import java.util.Objects;

/**
 * 官殺混雜의 최종 권위 판정 결과.
 *
 * =================================================
 *
 * 이 객체의 목적
 * =================================================
 *
 * 기존:
 *
 * - 食神制殺
 * - 傷官去官
 * - 去官留殺
 * - 去殺留官
 * - 기타 官殺 전용 purification
 *
 * 과
 *
 * 七殺 개별 node-level 羊刃合殺
 *
 * 을 마지막에 하나로 합친다.
 *
 * =================================================
 *
 * 중요한 점
 * =================================================
 *
 * generic stem binding 결과는
 * 이 객체의 입력 근거가 아니다.
 *
 * 즉:
 *
 * 天干五合 FULLY_BOUND
 *
 * 만으로
 *
 * mixtureCleared = true
 *
 * 가 될 수 없다.
 */
public record PatternOfficerKillingsFinalPurificationAnalysis(

        boolean mixed,

        PatternOfficerKillingsFinalPurificationStatus status,

        boolean clearedByStandardPurification,

        boolean clearedByYangBladeCompletion,

        PatternOfficerKillingsYangBladeCompletionAnalysis
        yangBladeCompletion

) {

    public PatternOfficerKillingsFinalPurificationAnalysis {

        Objects.requireNonNull(
                status,
                "官殺 최종 purification status는 필수입니다."
        );

        Objects.requireNonNull(
                yangBladeCompletion,
                "羊刃合殺 completion analysis는 필수입니다."
        );

        validate(
                mixed,
                status,
                clearedByStandardPurification,
                clearedByYangBladeCompletion,
                yangBladeCompletion
        );
    }

    /**
     * 官殺混雜이 최종적으로 완전히 정리됐는가.
     *
     * LifeClaim에서
     * AUTHORITY_COMPLEXITY_CAN_BE_CHANNELLED
     * 생성 여부를 판단할 때 사용할 최종 helper.
     */
    public boolean mixtureCleared() {

        return status
                == PatternOfficerKillingsFinalPurificationStatus.CLEARED;
    }

    /**
     * 일부만 정리됨.
     *
     * 이것을 mixtureCleared로 취급하면 안 된다.
     */
    public boolean partiallyCleared() {

        return status
                == PatternOfficerKillingsFinalPurificationStatus
                .PARTIALLY_CLEARED;
    }

    public boolean reviewRequired() {

        return status
                == PatternOfficerKillingsFinalPurificationStatus
                .REVIEW_REQUIRED;
    }

    public boolean unresolved() {

        return status
                == PatternOfficerKillingsFinalPurificationStatus
                .UNRESOLVED;
    }

    public boolean notMixed() {

        return status
                == PatternOfficerKillingsFinalPurificationStatus
                .NOT_MIXED;
    }

    /**
     * 羊刃合殺이 실제 최종 정리에 기여했는가.
     *
     * retained 七殺 전체가 처리된 경우에만 true.
     */
    public boolean yangBladeActuallyCompletedPurification() {

        return clearedByYangBladeCompletion
                &&
                yangBladeCompletion.complete();
    }

    private static void validate(

            boolean mixed,

            PatternOfficerKillingsFinalPurificationStatus status,

            boolean clearedByStandardPurification,

            boolean clearedByYangBladeCompletion,

            PatternOfficerKillingsYangBladeCompletionAnalysis
                    yangBladeCompletion

    ) {

        /*
         * 官殺混雜 자체가 아닌 경우.
         */
        if (!mixed) {

            if (
                    status
                            != PatternOfficerKillingsFinalPurificationStatus
                            .NOT_MIXED
            ) {

                throw new IllegalArgumentException(
                        "官殺混雜이 아니면 최종 상태는 NOT_MIXED여야 합니다."
                );
            }

            if (
                    clearedByStandardPurification
                            ||
                            clearedByYangBladeCompletion
            ) {

                throw new IllegalArgumentException(
                        "官殺混雜이 아닌 경우 purification 완료 근거를 가질 수 없습니다."
                );
            }

            return;
        }

        /*
         * mixed=true인데 NOT_MIXED는 불가능.
         */
        if (
                status
                        == PatternOfficerKillingsFinalPurificationStatus
                        .NOT_MIXED
        ) {

            throw new IllegalArgumentException(
                    "官殺混雜 구조에서 NOT_MIXED 상태를 사용할 수 없습니다."
            );
        }

        /*
         * 羊刃合殺에 의한 완료는
         * node-level completion이 COMPLETE일 때만 가능.
         */
        if (
                clearedByYangBladeCompletion
                        &&
                        !yangBladeCompletion.complete()
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺에 의한 官殺 정리 완료는 retained 七殺 전체가 처리된 경우에만 가능합니다."
            );
        }

        /*
         * CLEARED라면 적어도 하나의
         * 官殺 전용 완료 근거가 존재해야 한다.
         */
        if (
                status
                        == PatternOfficerKillingsFinalPurificationStatus
                        .CLEARED

                        &&

                        !clearedByStandardPurification
                        &&
                        !clearedByYangBladeCompletion
        ) {

            throw new IllegalArgumentException(
                    "CLEARED 상태에는 官殺 전용 정리 완료 근거가 필요합니다."
            );
        }

        /*
         * 완료 근거가 존재하는데 최종 상태가
         * 미정리/부분정리일 수 없다.
         */
        if (
                (
                        clearedByStandardPurification
                                ||
                                clearedByYangBladeCompletion
                )

                        &&

                        (
                                status
                                        == PatternOfficerKillingsFinalPurificationStatus
                                        .PARTIALLY_CLEARED

                                        ||

                                        status
                                                == PatternOfficerKillingsFinalPurificationStatus
                                                .UNRESOLVED
                        )
        ) {

            throw new IllegalArgumentException(
                    "전체 정리 근거가 존재하면 PARTIALLY_CLEARED 또는 UNRESOLVED가 될 수 없습니다."
            );
        }
    }
}