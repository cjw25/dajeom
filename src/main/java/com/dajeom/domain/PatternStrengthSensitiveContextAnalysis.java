package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 신강·신약과 격성 구조를 결합한
 * 강약 민감 격국 Context.
 *
 * =================================================
 *
 * 여기서 자동 판정:
 *
 * - 인성이 과다하게 작용할 수 있는 구조
 * - 인성 과다 구조에서 재성이 이를 제어하는 구조
 * - 신약 상태에서 식상 누기가 과도해지는 구조
 * - 상관패인 계열에서 상관이 너무 가벼운 구조
 *
 * =================================================
 *
 * 여기서 판정하지 않음:
 *
 * - 최종 격국
 * - 종격
 * - 화격
 * - 최종 용신
 * - 희신/기신 점수
 *
 * =================================================
 *
 * 중요:
 *
 * DayMasterStrengthDecision이
 *
 * - TRUE_TRANSFORMATION
 * - FOLLOWING_STRUCTURE_ESTABLISHED
 *
 * 로 최종 확정된 경우에는
 * 일반 신강·신약 판정을 적용하지 않는다.
 *
 * 이 경우 strengthStatus == null은
 * "미확정"이나 "알 수 없음"이 아니라
 * "일반 왕쇠 판정 적용 대상 아님"을 의미한다.
 *
 * 특수구조를 임의의 STRONG / WEAK 상태로
 * 변환해서는 안 된다.
 */
public record PatternStrengthSensitiveContextAnalysis(

        HeavenlyStem dayMaster,

        DayMasterStrengthStatus strengthStatus,

        boolean excessiveResourceContext,

        boolean wealthModeratesExcessResource,

        boolean outputDrainWeakStructureActive,

        boolean hurtingOfficerTooLightForResourceRoute,

        Set<PatternStrengthSensitiveContextReason> reasons

) {

    public PatternStrengthSensitiveContextAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(
                        reasons
                );

        /*
         * =================================================
         * 일반 왕쇠 비적용 상태
         * =================================================
         *
         * TRUE_TRANSFORMATION /
         * FOLLOWING_STRUCTURE_ESTABLISHED 전용.
         *
         * 일반 왕쇠가 적용되지 않는데
         * 강약 민감 구조만 따로 활성화되는 것은
         * 의미적으로 모순이므로 전부 비활성 상태여야 한다.
         */
        if (
                strengthStatus == null
        ) {

            if (
                    excessiveResourceContext
                            || wealthModeratesExcessResource
                            || outputDrainWeakStructureActive
                            || hurtingOfficerTooLightForResourceRoute
                            || !reasons.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "일반 왕쇠 비적용 상태에서는 강약 민감 구조를 활성화할 수 없습니다."
                );
            }
        }
    }

    /**
     * 일반 신강·신약 판정이 최종적으로 적용되는가.
     */
    public boolean applicable() {

        return strengthStatus != null;
    }

    /**
     * 真化 또는 성립된 종격처럼
     * 일반 왕쇠 판정을 최종 해석축으로 사용하지 않는 경우.
     *
     * 임의의 강약 상태를 만들지 않고
     * 강약 민감 구조 자체를 비적용 상태로 둔다.
     */
    public static PatternStrengthSensitiveContextAnalysis
    notApplicable(
            HeavenlyStem dayMaster
    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        return new PatternStrengthSensitiveContextAnalysis(
                dayMaster,
                null,
                false,
                false,
                false,
                false,
                Set.of()
        );
    }

    public boolean strongSide() {

        return strengthStatus
                == DayMasterStrengthStatus.STRONG_LEANING

                ||

                strengthStatus
                        == DayMasterStrengthStatus.CLEARLY_STRONG;
    }

    public boolean weakSide() {

        return strengthStatus
                == DayMasterStrengthStatus.WEAK_LEANING

                ||

                strengthStatus
                        == DayMasterStrengthStatus.CLEARLY_WEAK;
    }
}