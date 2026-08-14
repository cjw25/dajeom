package com.dajeom.rule;

import com.dajeom.model.ClimateAdjustmentNeedAnalysis;
import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.ClimateUsefulGodCandidateRole;
import com.dajeom.type.ClimateUsefulGodReasonType;
import com.dajeom.type.UsefulGodMethodApplicability;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-3.
 *
 * 6-3 조후 후보를
 * 실제 7차 조후용신 후보로 재검증한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 6-3 PRIMARY를 그대로 복사하지 않는다.
 *
 * 2.
 * 7-1 조후용신법이 INACTIVE면
 *    조후용신 후보도 생성하지 않는다.
 *
 * 3.
 * 조후용신법이 CONDITIONAL이면
 *    실제 오행도 PRIMARY/SECONDARY로 확정하지 않는다.
 *
 * 4.
 * 6-3에서 이미 다른 조후축을 악화시키는
 *    Conflict가 있으면 CONDITIONAL.
 *
 * 5.
 * 오행이 이미 과도하면 CONDITIONAL.
 *
 * 6.
 * 신강신약 또는 격국과 직접 충돌하면
 *    CONDITIONAL.
 *
 * 7.
 * STRAINED는 완전 배제하지 않는다.
 *
 * 6-3 PRIMARY
 * -> 7-3 SECONDARY
 *
 * 6-3 SECONDARY
 * -> 7-3 CONDITIONAL
 *
 * 로 하향할 수 있다.
 *
 * 8.
 * 寒燥 / 熱濕 경쟁 구조에서
 * 중재 후보가 아닌 단일축 강한 후보는
 * 보수적으로 PRIMARY를 한 단계 낮출 수 있다.
 *
 * 9.
 * 최종 용신은 7-8에서 결정한다.
 */
@Component
public class ClimateUsefulGodRule {

    public ClimateUsefulGodCandidateRole determineRole(

            UsefulGodMethodApplicability methodApplicability,

            ClimateElementCandidate sourceCandidate,

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState,

            ClimateAdjustmentNeedAnalysis climateNeed

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "조후용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "6-3 조후 후보가 필요합니다."
        );

        Objects.requireNonNull(
                strengthState,
                "신강신약 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                structureState,
                "격국 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                climateNeed,
                "조후 필요성 분석이 필요합니다."
        );

        /*
         * ================================================
         * 조후용신법 자체가 비활성
         * ================================================
         */
        if (
                methodApplicability.inactive()
        ) {

            return ClimateUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * ================================================
         * 6-3에서 해당 오행이
         * 조후 후보가 아님
         * ================================================
         */
        if (
                !sourceCandidate.needed()
        ) {

            return ClimateUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * ================================================
         * 조후용신법 자체가 조건부
         * ================================================
         *
         * LOW 조후 필요 등에서는
         * 특정 오행을 적극적인 용신 후보로
         * 확정하지 않는다.
         */
        if (
                methodApplicability.conditional()
        ) {

            return ClimateUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 6-3 자체 충돌
         * ================================================
         */
        if (
                sourceCandidate.hasConflict()
        ) {

            return ClimateUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 6-3 조건부 후보
         * ================================================
         */
        if (
                sourceCandidate.conditional()
        ) {

            return ClimateUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 해당 오행이 이미 과도
         * ================================================
         */
        if (
                sourceCandidate.saturated()
        ) {

            return ClimateUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 신강신약 / 격국 직접 충돌
         * ================================================
         */
        if (
                strengthState.contradictory()
                        || structureState.contradictory()
        ) {

            return ClimateUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 신강신약 / 격국 부담
         * ================================================
         */
        if (
                strengthState.strained()
                        || structureState.strained()
        ) {

            if (
                    sourceCandidate.primary()
            ) {

                return ClimateUsefulGodCandidateRole
                        .SECONDARY;
            }

            return ClimateUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 寒燥 / 熱濕
         *
         * 한난과 조습의 보정 방향이 경쟁
         * ================================================
         *
         * 6-3 PRIMARY라도
         * 중재 후보가 아니면
         * 한 축을 고치면서 다른 축을
         * 자극할 가능성이 있으므로
         * 단독 PRIMARY를 보수적으로 낮춘다.
         *
         * 실제 직접 Conflict는
         * 위에서 이미 CONDITIONAL 처리됐다.
         */
        if (
                climateNeed.correctionConflict()

                        && sourceCandidate.primary()

                        && !sourceCandidate.bridgeCandidate()
        ) {

            return ClimateUsefulGodCandidateRole
                    .SECONDARY;
        }

        /*
         * ================================================
         * 깨끗한 6-3 PRIMARY
         * ================================================
         */
        if (
                sourceCandidate.primary()
        ) {

            return ClimateUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * ================================================
         * 깨끗한 6-3 SECONDARY
         * ================================================
         */
        if (
                sourceCandidate.secondary()
        ) {

            return ClimateUsefulGodCandidateRole
                    .SECONDARY;
        }

        return ClimateUsefulGodCandidateRole
                .CONDITIONAL;
    }

    public List<ClimateUsefulGodReasonType> reasonsFor(

            UsefulGodMethodApplicability methodApplicability,

            ClimateElementCandidate sourceCandidate,

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState,

            ClimateAdjustmentNeedAnalysis climateNeed,

            ClimateUsefulGodCandidateRole finalRole

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "조후용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "6-3 조후 후보가 필요합니다."
        );

        Objects.requireNonNull(
                strengthState,
                "신강신약 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                structureState,
                "격국 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                climateNeed,
                "조후 필요성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                finalRole,
                "7-3 최종 후보 역할이 필요합니다."
        );

        List<ClimateUsefulGodReasonType> reasons =
                new ArrayList<>();

        /*
         * ================================================
         * 7-1 방법 적용도
         * ================================================
         */
        if (
                methodApplicability.core()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .CLIMATE_METHOD_CORE
            );

        } else if (
                methodApplicability.active()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .CLIMATE_METHOD_ACTIVE
            );

        } else if (
                methodApplicability.conditional()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .CLIMATE_METHOD_CONDITIONAL
            );
        }

        /*
         * ================================================
         * 해당 후보가 실제로 다루는
         * 조후 필요도
         * ================================================
         */
        ClimateAdjustmentNeedLevel addressedNeed =
                sourceCandidate
                        .addressedNeedLevel();

        if (
                addressedNeed.critical()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .CRITICAL_CLIMATE_NEED
            );

        } else if (
                addressedNeed.high()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .HIGH_CLIMATE_NEED
            );

        } else if (
                addressedNeed.moderate()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .MODERATE_CLIMATE_NEED
            );

        } else if (
                addressedNeed.low()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .LOW_CLIMATE_NEED
            );
        }

        /*
         * ================================================
         * 6-3 원본 역할
         * ================================================
         */
        if (
                sourceCandidate.primary()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .SOURCE_PRIMARY
            );

        } else if (
                sourceCandidate.secondary()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .SOURCE_SECONDARY
            );

        } else if (
                sourceCandidate.conditional()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .SOURCE_CONDITIONAL
            );
        }

        /*
         * ================================================
         * 한난·조습 관계
         * ================================================
         */
        if (
                climateNeed.correctionConflict()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .DUAL_AXIS_COMPETING
            );

        } else if (
                climateNeed.correctionAligned()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .DUAL_AXIS_ALIGNED
            );
        }

        if (
                sourceCandidate.bridgeCandidate()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .CLIMATE_BRIDGE_CANDIDATE
            );
        }

        if (
                sourceCandidate.hasConflict()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .SOURCE_CLIMATE_CONFLICT
            );
        }

        /*
         * ================================================
         * 현재 오행량
         * ================================================
         */
        ClimateElementPresenceLevel presence =
                sourceCandidate
                        .presenceLevel();

        if (
                presence.absent()
                        || presence.low()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .ELEMENT_SCARCE
            );

        } else if (
                presence.moderate()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .ELEMENT_MODERATE
            );

        } else if (
                presence.high()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .ELEMENT_HIGH
            );

        } else if (
                presence.saturated()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .ELEMENT_SATURATED
            );
        }

        /*
         * ================================================
         * 신강신약 검증
         * ================================================
         */
        if (
                strengthState.supported()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .STRENGTH_SUPPORTED
            );

        } else if (
                strengthState.neutral()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .STRENGTH_NEUTRAL
            );

        } else if (
                strengthState.strained()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .STRENGTH_STRAINED
            );

        } else if (
                strengthState.contradictory()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .STRENGTH_CONTRADICTORY
            );
        }

        /*
         * ================================================
         * 격국 검증
         * ================================================
         */
        if (
                structureState.supported()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .STRUCTURE_SUPPORTED
            );

        } else if (
                structureState.neutral()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .STRUCTURE_NEUTRAL
            );

        } else if (
                structureState.strained()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .STRUCTURE_STRAINED
            );

        } else if (
                structureState.contradictory()
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .STRUCTURE_CONTRADICTORY
            );
        }

        /*
         * ================================================
         * 6-3 -> 7-3 역할 변화
         * ================================================
         */
        if (
                sourceCandidate.primary()
        ) {

            if (
                    finalRole.primary()
            ) {

                reasons.add(
                        ClimateUsefulGodReasonType
                                .PRIMARY_PRESERVED
                );

            } else {

                reasons.add(
                        ClimateUsefulGodReasonType
                                .PRIMARY_DOWNGRADED
                );
            }
        }

        if (
                finalRole.conditional()

                        && (
                        sourceCandidate.hasConflict()
                                || strengthState.requiresCaution()
                                || structureState.requiresCaution()
                                || sourceCandidate.saturated()
                )
        ) {

            reasons.add(
                    ClimateUsefulGodReasonType
                            .CANDIDATE_DEFERRED_BY_CONFLICT
            );
        }

        return List.copyOf(
                reasons
        );
    }
}