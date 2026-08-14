package com.dajeom.rule;

import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.ExtremeStrengthStructureType;
import com.dajeom.type.FollowStructureUsefulGodCandidateRole;
import com.dajeom.type.FollowStructureUsefulGodReasonType;
import com.dajeom.type.FollowWeakDirection;
import com.dajeom.type.UsefulGodMethodApplicability;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-7.
 *
 * 종격용신 후보 선정 Rule.
 *
 * =================================================
 *
 * 종약
 *
 * 일반 신약 공식:
 *
 * COMPANION / RESOURCE 보강
 *
 * 을 사용하지 않는다.
 *
 * 종하는 방향:
 *
 * OUTPUT
 * WEALTH
 * OFFICER
 *
 * 의 흐름을 유지한다.
 *
 * =================================================
 *
 * 종강
 *
 * 일반 신강 공식:
 *
 * OUTPUT / WEALTH / OFFICER로
 * 설기·소모·극제
 *
 * 를 사용하지 않는다.
 *
 * COMPANION / RESOURCE의
 * 강한 흐름을 유지한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 종격 후보가 존재한다는 이유만으로
 * 이 Rule을 활성화하면 안 된다.
 *
 * 5-9에서 실제 주격으로
 * 종격이 선택되어야 한다.
 *
 * =================================================
 *
 * 현재 오행이 HIGH/VERY_HIGH라고 해서
 * 종격 후보를 자동으로 강등하지 않는다.
 *
 * 일반 억부와 달리 종격에서는
 * 이미 형성된 편중 흐름을 따르는 것이
 * 핵심이기 때문이다.
 *
 * =================================================
 *
 * 최종 주용신은 7-8에서 결정한다.
 */
@Component
public class FollowStructureUsefulGodRule {

    /**
     * 해당 오행 역할이
     * 종격의 핵심 방향 자체와 직접 일치하는가.
     */
    public boolean directFollowDirection(

            ExtremeStrengthStructureType structureType,

            FollowWeakDirection followWeakDirection,

            DayMasterElementRole role

    ) {

        Objects.requireNonNull(
                structureType,
                "극단구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                followWeakDirection,
                "종약 방향이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "일간 기준 오행 역할이 필요합니다."
        );

        /*
         * =================================================
         * 종강
         * =================================================
         */
        if (
                structureType
                        == ExtremeStrengthStructureType
                        .FOLLOW_STRONG_CANDIDATE
        ) {

            return role
                    == DayMasterElementRole.COMPANION

                    || role
                    == DayMasterElementRole.RESOURCE;
        }

        /*
         * =================================================
         * 종약
         * =================================================
         */
        if (
                structureType
                        != ExtremeStrengthStructureType
                        .FOLLOW_WEAK_CANDIDATE
        ) {

            return false;
        }

        return switch (
                followWeakDirection
                ) {

            case OUTPUT ->
                    role == DayMasterElementRole.OUTPUT;

            case WEALTH ->
                    role == DayMasterElementRole.WEALTH;

            case OFFICER ->
                    role == DayMasterElementRole.OFFICER;

            case MIXED ->
                    role == DayMasterElementRole.OUTPUT
                            || role == DayMasterElementRole.WEALTH
                            || role == DayMasterElementRole.OFFICER;

            case NONE ->
                    false;
        };
    }

    /**
     * 핵심 종격 방향 자체는 아니지만
     * 생조 흐름을 이어주는 역할인가.
     */
    public boolean flowSupport(

            ExtremeStrengthStructureType structureType,

            FollowWeakDirection followWeakDirection,

            DayMasterElementRole role

    ) {

        Objects.requireNonNull(
                structureType,
                "극단구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                followWeakDirection,
                "종약 방향이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "일간 기준 오행 역할이 필요합니다."
        );

        /*
         * 종강에서는 비겁·인성이 모두
         * 직접 종강 흐름으로 처리된다.
         */
        if (
                structureType
                        == ExtremeStrengthStructureType
                        .FOLLOW_STRONG_CANDIDATE
        ) {

            return false;
        }

        if (
                structureType
                        != ExtremeStrengthStructureType
                        .FOLLOW_WEAK_CANDIDATE
        ) {

            return false;
        }

        return switch (
                followWeakDirection
                ) {

            /*
             * 식상 -> 재성
             */
            case OUTPUT ->
                    role == DayMasterElementRole.WEALTH;

            /*
             * 식상 -> 재성 -> 관살
             *
             * 재성 자체가 핵심이고
             * 식상은 재성을 생하며
             * 관살은 재성의 흐름을 이어받는다.
             */
            case WEALTH ->
                    role == DayMasterElementRole.OUTPUT
                            || role == DayMasterElementRole.OFFICER;

            /*
             * 재성 -> 관살
             */
            case OFFICER ->
                    role == DayMasterElementRole.WEALTH;

            /*
             * MIXED는 세 역할 자체가
             * 모두 직접 종하는 방향이므로
             * 별도 support로 두지 않는다.
             */
            case MIXED,
                 NONE ->
                    false;
        };
    }

    /**
     * 종하는 흐름을 깨는 역할인가.
     */
    public boolean breaksFollowFlow(

            ExtremeStrengthStructureType structureType,

            FollowWeakDirection followWeakDirection,

            DayMasterElementRole role

    ) {

        Objects.requireNonNull(
                structureType,
                "극단구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                followWeakDirection,
                "종약 방향이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "일간 기준 오행 역할이 필요합니다."
        );

        /*
         * =================================================
         * 종약
         * =================================================
         *
         * 비겁·인성은 약한 일간을 다시 세워
         * 종하는 흐름을 되돌릴 수 있다.
         */
        if (
                structureType
                        == ExtremeStrengthStructureType
                        .FOLLOW_WEAK_CANDIDATE
        ) {

            if (
                    role == DayMasterElementRole.COMPANION
                            || role == DayMasterElementRole.RESOURCE
            ) {

                return true;
            }

            /*
             * 종살/종관 계열에서 식상은
             * 관살을 제어하여 종하는 흐름을
             * 직접 깨뜨릴 수 있다.
             */
            return followWeakDirection
                    == FollowWeakDirection.OFFICER

                    && role
                    == DayMasterElementRole.OUTPUT;
        }

        /*
         * =================================================
         * 종강
         * =================================================
         *
         * 식상·재성·관살은
         * 강한 인비 흐름을 설기·소모·극제하여
         * 종강 흐름을 깨뜨릴 수 있다.
         */
        if (
                structureType
                        == ExtremeStrengthStructureType
                        .FOLLOW_STRONG_CANDIDATE
        ) {

            return role == DayMasterElementRole.OUTPUT
                    || role == DayMasterElementRole.WEALTH
                    || role == DayMasterElementRole.OFFICER;
        }

        return false;
    }

    /**
     * 같은 종격 방향 후보 중
     * 현재 실제 세력이 우세한지.
     *
     * exact maximum만 강제하지 않고
     * 500bp 이내는 같은 핵심 세력으로 인정한다.
     *
     * 이것은 확률이나 통계값이 아니라
     * 엔진 내부 보수적 동률 허용폭이다.
     */
    public boolean dominantFlowElement(

            int candidateShareBasisPoints,

            int maximumRelevantShareBasisPoints

    ) {

        validateShare(
                candidateShareBasisPoints
        );

        validateShare(
                maximumRelevantShareBasisPoints
        );

        if (
                maximumRelevantShareBasisPoints == 0
        ) {

            return false;
        }

        return candidateShareBasisPoints
                >= maximumRelevantShareBasisPoints - 500;
    }

    public FollowStructureUsefulGodCandidateRole determineRole(

            UsefulGodMethodApplicability methodApplicability,

            boolean followStructureMode,

            ExtremeStrengthStructureType structureType,

            boolean directFollowDirection,

            boolean flowSupport,

            boolean dominantFlowElement,

            boolean breaksFollowFlow,

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "종격용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                structureType,
                "극단구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                strengthState,
                "신강신약 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                structureState,
                "격국 교차검증 상태가 필요합니다."
        );

        /*
         * 실제 종격 주격이 아님.
         */
        if (
                !followStructureMode
                        || methodApplicability.inactive()
        ) {

            return FollowStructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * 종하는 흐름을 깨는 오행.
         *
         * 아직 기신 판정은 아니지만
         * 종격용신 후보에서는 제외한다.
         */
        if (
                breaksFollowFlow
        ) {

            return FollowStructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * 6-9에서도 종격 방향과
         * 직접 충돌한다고 판단.
         */
        if (
                strengthState.contradictory()
        ) {

            return FollowStructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * 격국 측 직접 충돌.
         *
         * 종격 자체는 필요하더라도
         * 다른 구조와의 충돌을 보존한다.
         */
        if (
                structureState.contradictory()
        ) {

            if (
                    directFollowDirection
                            || flowSupport
            ) {

                return FollowStructureUsefulGodCandidateRole
                        .CONDITIONAL;
            }

            return FollowStructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * 강약/격국 부담.
         */
        if (
                strengthState.strained()
                        || structureState.strained()
        ) {

            if (
                    directFollowDirection
                            || flowSupport
            ) {

                return FollowStructureUsefulGodCandidateRole
                        .CONDITIONAL;
            }

            return FollowStructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * 종하는 직접 방향 가운데
         * 현재 실제 세력까지 중심인 오행.
         */
        if (
                directFollowDirection
                        && dominantFlowElement
        ) {

            return FollowStructureUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * 종하는 직접 방향.
         */
        if (
                directFollowDirection
        ) {

            return FollowStructureUsefulGodCandidateRole
                    .SECONDARY;
        }

        /*
         * 핵심 방향을 생조하여 이어주는 오행.
         */
        if (
                flowSupport
        ) {

            return FollowStructureUsefulGodCandidateRole
                    .SECONDARY;
        }

        return FollowStructureUsefulGodCandidateRole
                .NOT_NEEDED;
    }

    public List<FollowStructureUsefulGodReasonType> reasonsFor(

            UsefulGodMethodApplicability methodApplicability,

            boolean followStructureMode,

            ExtremeStrengthStructureType structureType,

            FollowWeakDirection followWeakDirection,

            DayMasterElementRole role,

            boolean directFollowDirection,

            boolean flowSupport,

            boolean dominantFlowElement,

            boolean breaksFollowFlow,

            com.dajeom.type.ClimateElementPresenceLevel presenceLevel,

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState,

            FollowStructureUsefulGodCandidateRole finalRole

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "종격용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                structureType,
                "극단구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                followWeakDirection,
                "종약 방향이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "오행 세력 수준이 필요합니다."
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
                finalRole,
                "종격용신 후보 역할이 필요합니다."
        );

        List<FollowStructureUsefulGodReasonType> reasons =
                new ArrayList<>();

        if (
                methodApplicability.core()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .FOLLOW_METHOD_CORE
            );

        } else if (
                methodApplicability.inactive()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .FOLLOW_METHOD_INACTIVE
            );
        }

        if (
                !followStructureMode
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .NO_FOLLOW_STRUCTURE_MODE
            );

            return List.copyOf(
                    reasons
            );
        }

        /*
         * =================================================
         * 종격 유형
         * =================================================
         */
        if (
                structureType
                        == ExtremeStrengthStructureType
                        .FOLLOW_WEAK_CANDIDATE
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .FOLLOW_WEAK_STRUCTURE
            );

            switch (
                    followWeakDirection
            ) {

                case OUTPUT ->
                        reasons.add(
                                FollowStructureUsefulGodReasonType
                                        .FOLLOW_OUTPUT_DIRECTION
                        );

                case WEALTH ->
                        reasons.add(
                                FollowStructureUsefulGodReasonType
                                        .FOLLOW_WEALTH_DIRECTION
                        );

                case OFFICER ->
                        reasons.add(
                                FollowStructureUsefulGodReasonType
                                        .FOLLOW_OFFICER_DIRECTION
                        );

                case MIXED ->
                        reasons.add(
                                FollowStructureUsefulGodReasonType
                                        .FOLLOW_MIXED_DIRECTION
                        );

                case NONE -> {
                    // 정상 종약 구조에서는 발생하지 않는다.
                }
            }

        } else if (
                structureType
                        == ExtremeStrengthStructureType
                        .FOLLOW_STRONG_CANDIDATE
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .FOLLOW_STRONG_STRUCTURE
            );

            if (
                    role
                            == DayMasterElementRole.COMPANION
            ) {

                reasons.add(
                        FollowStructureUsefulGodReasonType
                                .COMPANION_FOLLOW_DIRECTION
                );
            }

            if (
                    role
                            == DayMasterElementRole.RESOURCE
            ) {

                reasons.add(
                        FollowStructureUsefulGodReasonType
                                .RESOURCE_FOLLOW_DIRECTION
                );
            }
        }

        if (
                directFollowDirection
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .DIRECT_FOLLOW_DIRECTION
            );
        }

        if (
                flowSupport
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .FOLLOW_FLOW_SUPPORT
            );
        }

        if (
                dominantFlowElement
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .DOMINANT_FOLLOW_ELEMENT
            );
        }

        if (
                breaksFollowFlow
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .BREAKS_FOLLOW_FLOW
            );
        }

        /*
         * =================================================
         * 현재 오행 세력
         * =================================================
         *
         * 종격에서는 HIGH/SATURATED를
         * 부정 근거로 사용하지 않는다.
         */
        if (
                presenceLevel.absent()
                        || presenceLevel.low()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .ELEMENT_SCARCE
            );

        } else if (
                presenceLevel.moderate()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .ELEMENT_MODERATE
            );

        } else if (
                presenceLevel.high()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .ELEMENT_HIGH
            );

        } else if (
                presenceLevel.saturated()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .ELEMENT_SATURATED
            );
        }

        /*
         * =================================================
         * 신강신약
         * =================================================
         */
        if (
                strengthState.supported()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .STRENGTH_SUPPORTED
            );

        } else if (
                strengthState.neutral()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .STRENGTH_NEUTRAL
            );

        } else if (
                strengthState.strained()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .STRENGTH_STRAINED
            );

        } else if (
                strengthState.contradictory()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .STRENGTH_CONTRADICTORY
            );
        }

        /*
         * =================================================
         * 격국
         * =================================================
         */
        if (
                structureState.supported()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .STRUCTURE_SUPPORTED
            );

        } else if (
                structureState.neutral()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .STRUCTURE_NEUTRAL
            );

        } else if (
                structureState.strained()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .STRUCTURE_STRAINED
            );

        } else if (
                structureState.contradictory()
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .STRUCTURE_CONTRADICTORY
            );
        }

        /*
         * =================================================
         * 결과 이유
         * =================================================
         */
        if (
                finalRole.primary()
                        && directFollowDirection
                        && dominantFlowElement
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .PRIMARY_BY_DOMINANT_FLOW
            );

        } else if (
                finalRole.primary()
                        && directFollowDirection
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .PRIMARY_BY_DIRECT_FOLLOW
            );
        }

        if (
                finalRole.secondary()
                        && flowSupport
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .SECONDARY_BY_FLOW_SUPPORT
            );
        }

        if (
                finalRole.conditional()
                        && (
                        strengthState.requiresCaution()
                                || structureState.requiresCaution()
                )
        ) {

            reasons.add(
                    FollowStructureUsefulGodReasonType
                            .CONDITIONAL_BY_CROSS_VALIDATION
            );
        }

        return List.copyOf(
                reasons
        );
    }

    private void validateShare(
            int shareBasisPoints
    ) {

        if (
                shareBasisPoints < 0
                        || shareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 비중은 0~10000bp 사이여야 합니다."
            );
        }
    }
}