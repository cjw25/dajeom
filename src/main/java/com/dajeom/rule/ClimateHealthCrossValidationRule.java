package com.dajeom.rule;

import com.dajeom.model.ClimateHealthCandidate;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.StructureDamageSignal;
import com.dajeom.model.StructureSupportSignal;
import com.dajeom.type.ClimateHealthCandidateRole;
import com.dajeom.type.ClimateHealthCrossValidationReasonType;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-9.
 *
 * 6-8 후보를:
 *
 * - 신강·신약
 * - 주격의 성격
 * - 주격의 파격
 * - 종격 방향
 *
 * 과 교차검증한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 6-9는 검증 단계다.
 *
 * 6-8 SECONDARY를 PRIMARY로
 * 승격하지 않는다.
 *
 * 반대로:
 *
 * PRIMARY -> SECONDARY
 * PRIMARY -> DEFERRED
 * SECONDARY -> DEFERRED
 *
 * 하향은 가능하다.
 *
 * =================================================
 *
 * 2.
 * 일반 신약:
 *
 * COMPANION / RESOURCE
 * -> 일간 보강 방향
 *
 * OUTPUT / WEALTH / OFFICER
 * -> 추가 부담 가능
 *
 * =================================================
 *
 * 3.
 * 일반 신강:
 *
 * OUTPUT / WEALTH / OFFICER
 * -> 설기·소모·극제 방향
 *
 * COMPANION / RESOURCE
 * -> 추가 생조 가능
 *
 * =================================================
 *
 * 4.
 * 종격 후보는 일반 신강신약 공식을
 * 그대로 적용하면 안 된다.
 *
 * 종약 방향인데 비겁/인성을 강하게 넣거나,
 * 종강 방향인데 식재관을 강하게 넣는 후보는
 * 종격 흐름과 충돌할 수 있다.
 *
 * =================================================
 *
 * 5.
 * 5차 성격·파격의 실제 Element Evidence를
 * 그대로 재사용한다.
 *
 * 격국을 다시 계산하지 않는다.
 */
@Component
public class ClimateHealthCrossValidationRule {

    public ClimateHealthCrossValidationState
    determineStrengthState(

            DayMasterStrengthLevel strengthLevel,

            DayMasterElementRole candidateRole,

            boolean followStructureCandidate

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                candidateRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        /*
         * ================================================
         * 종격 후보
         * ================================================
         *
         * 정확한 종재/종살/종아 등의 세부 명칭을
         * 여기서 다시 추측하지 않는다.
         *
         * 현재 강약 방향을 이용해:
         *
         * 극약 + follow
         * -> 종약 계열
         *
         * 극강 + follow
         * -> 종강 계열
         *
         * 로만 보수적으로 검증한다.
         */
        if (
                followStructureCandidate
        ) {

            if (
                    weakSide(
                            strengthLevel
                    )
            ) {

                if (
                        candidateRole
                                == DayMasterElementRole.COMPANION

                                || candidateRole
                                == DayMasterElementRole.RESOURCE
                ) {

                    return ClimateHealthCrossValidationState
                            .CONTRADICTORY;
                }

                return ClimateHealthCrossValidationState
                        .SUPPORTED;
            }

            if (
                    strongSide(
                            strengthLevel
                    )
            ) {

                if (
                        candidateRole
                                == DayMasterElementRole.COMPANION

                                || candidateRole
                                == DayMasterElementRole.RESOURCE
                ) {

                    return ClimateHealthCrossValidationState
                            .SUPPORTED;
                }

                return ClimateHealthCrossValidationState
                        .CONTRADICTORY;
            }

            /*
             * 정상적인 종격 후보라면
             * 극단 강약측이어야 한다.
             *
             * 방어적으로 중화권에서는
             * 자동 유불리를 만들지 않는다.
             */
            return ClimateHealthCrossValidationState
                    .NEUTRAL;
        }

        /*
         * ================================================
         * 일반 극약 / 신약
         * ================================================
         */
        if (
                strengthLevel
                        == DayMasterStrengthLevel.EXTREME_WEAK

                        || strengthLevel
                        == DayMasterStrengthLevel.WEAK
        ) {

            if (
                    supportiveRole(
                            candidateRole
                    )
            ) {

                return ClimateHealthCrossValidationState
                        .SUPPORTED;
            }

            return ClimateHealthCrossValidationState
                    .STRAINED;
        }

        /*
         * ================================================
         * 약간 신약
         * ================================================
         *
         * BALANCED_WEAK에서는
         * 식재관을 바로 불리하다고 단정하지 않는다.
         */
        if (
                strengthLevel
                        == DayMasterStrengthLevel.BALANCED_WEAK
        ) {

            if (
                    supportiveRole(
                            candidateRole
                    )
            ) {

                return ClimateHealthCrossValidationState
                        .SUPPORTED;
            }

            return ClimateHealthCrossValidationState
                    .NEUTRAL;
        }

        /*
         * ================================================
         * 완전 중화
         * ================================================
         */
        if (
                strengthLevel
                        == DayMasterStrengthLevel.BALANCED
        ) {

            return ClimateHealthCrossValidationState
                    .NEUTRAL;
        }

        /*
         * ================================================
         * 약간 신강
         * ================================================
         */
        if (
                strengthLevel
                        == DayMasterStrengthLevel.BALANCED_STRONG
        ) {

            if (
                    releasingRole(
                            candidateRole
                    )
            ) {

                return ClimateHealthCrossValidationState
                        .SUPPORTED;
            }

            return ClimateHealthCrossValidationState
                    .NEUTRAL;
        }

        /*
         * ================================================
         * 신강 / 극강
         * ================================================
         */
        if (
                strengthLevel
                        == DayMasterStrengthLevel.STRONG

                        || strengthLevel
                        == DayMasterStrengthLevel.EXTREME_STRONG
        ) {

            if (
                    releasingRole(
                            candidateRole
                    )
            ) {

                return ClimateHealthCrossValidationState
                        .SUPPORTED;
            }

            return ClimateHealthCrossValidationState
                    .STRAINED;
        }

        return ClimateHealthCrossValidationState
                .NEUTRAL;
    }

    /**
     * 현재 오행이
     * 주격의 의미 있는 성격(成格) 요소와
     * 일치하는지.
     */
    public boolean matchesPrimaryStructureSupport(

            StructureAnalysis structure,

            FiveElement element

    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                element,
                "오행이 필요합니다."
        );

        if (
                !structure.primaryIsStandard()
        ) {

            return false;
        }

        return structure
                .primaryCandidate()
                .standardEvidence()
                .damageEvidence()
                .supportEvidence()
                .supportSignals()
                .stream()
                .filter(
                        signal ->
                                signal
                                        .level()
                                        .meaningful()
                )
                .map(
                        StructureSupportSignal
                                ::supportingElement
                )
                .anyMatch(
                        supportingElement ->
                                supportingElement
                                        == element
                );
    }

    /**
     * 현재 오행이
     * 주격의 실제 의미 있는 파격 요소와
     * 일치하는지.
     */
    public boolean matchesPrimaryStructureDamage(

            StructureAnalysis structure,

            FiveElement element

    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                element,
                "오행이 필요합니다."
        );

        if (
                !structure.primaryIsStandard()
        ) {

            return false;
        }

        return structure
                .primaryCandidate()
                .standardEvidence()
                .damageEvidence()
                .damageSignals()
                .stream()
                .filter(
                        StructureDamageSignal
                                ::meaningful
                )
                .map(
                        StructureDamageSignal
                                ::damagingElement
                )
                .filter(
                        Objects::nonNull
                )
                .anyMatch(
                        damagingElement ->
                                damagingElement
                                        == element
                );
    }

    public ClimateHealthCrossValidationState
    determineStructureState(

            StructureAnalysis structure,

            boolean supportMatch,

            boolean damageMatch

    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석이 필요합니다."
        );

        /*
         * 특수격 PRIMARY에는
         * 일반 내격 성격/파격 공식을
         * 강제로 적용하지 않는다.
         */
        if (
                structure.primaryIsSpecial()
        ) {

            return ClimateHealthCrossValidationState
                    .NEUTRAL;
        }

        /*
         * 동일 오행이:
         *
         * 성격 요소이면서
         * 다른 파격 패턴에도 연결될 수 있다.
         *
         * 이 경우 무조건 지지로 확정하지 않고
         * STRAINED로 보존한다.
         */
        if (
                supportMatch
                        && damageMatch
        ) {

            return ClimateHealthCrossValidationState
                    .STRAINED;
        }

        if (
                damageMatch
        ) {

            return ClimateHealthCrossValidationState
                    .CONTRADICTORY;
        }

        if (
                supportMatch
        ) {

            return ClimateHealthCrossValidationState
                    .SUPPORTED;
        }

        return ClimateHealthCrossValidationState
                .NEUTRAL;
    }

    public ClimateHealthCrossValidationState
    determineOverallState(

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState

    ) {

        Objects.requireNonNull(
                strengthState,
                "신강신약 검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                structureState,
                "격국 검증 상태가 필요합니다."
        );

        if (
                strengthState.contradictory()
                        || structureState.contradictory()
        ) {

            return ClimateHealthCrossValidationState
                    .CONTRADICTORY;
        }

        if (
                strengthState.strained()
                        || structureState.strained()
        ) {

            return ClimateHealthCrossValidationState
                    .STRAINED;
        }

        if (
                strengthState.supported()
                        || structureState.supported()
        ) {

            return ClimateHealthCrossValidationState
                    .SUPPORTED;
        }

        return ClimateHealthCrossValidationState
                .NEUTRAL;
    }

    /**
     * 6-8 역할을 교차검증한다.
     *
     * 절대로 상향하지 않는다.
     */
    public ClimateHealthCandidateRole
    determineValidatedRole(

            ClimateHealthCandidateRole baseRole,

            ClimateHealthCrossValidationState overallState

    ) {

        Objects.requireNonNull(
                baseRole,
                "6-8 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                overallState,
                "교차검증 상태가 필요합니다."
        );

        /*
         * 기존 REJECTED는 그대로 유지.
         */
        if (
                baseRole.rejected()
        ) {

            return ClimateHealthCandidateRole
                    .REJECTED;
        }

        /*
         * 6-8에서 이미 충돌/조건부로
         * DEFERRED된 후보는
         * 6-9에서 다시 올리지 않는다.
         */
        if (
                baseRole.deferred()
        ) {

            return ClimateHealthCandidateRole
                    .DEFERRED;
        }

        /*
         * 핵심 구조와 직접 충돌.
         */
        if (
                overallState.contradictory()
        ) {

            return ClimateHealthCandidateRole
                    .DEFERRED;
        }

        /*
         * 부담은 있지만
         * 직접 모순까지는 아닌 상태.
         *
         * PRIMARY
         * -> SECONDARY
         *
         * SECONDARY
         * -> DEFERRED
         */
        if (
                overallState.strained()
        ) {

            if (
                    baseRole.primary()
            ) {

                return ClimateHealthCandidateRole
                        .SECONDARY;
            }

            return ClimateHealthCandidateRole
                    .DEFERRED;
        }

        /*
         * SUPPORTED / NEUTRAL
         *
         * 기존 역할 유지.
         */
        return baseRole;
    }

    public List<ClimateHealthCrossValidationReasonType>
    reasonsFor(

            ClimateHealthCandidate candidate,

            DayMasterStrengthLevel strengthLevel,

            DayMasterElementRole dayMasterRole,

            boolean followStructureCandidate,

            boolean primarySpecialStructure,

            boolean structureSupportMatch,

            boolean structureDamageMatch,

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState,

            ClimateHealthCandidateRole validatedRole

    ) {

        Objects.requireNonNull(
                candidate,
                "6-8 후보가 필요합니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRole,
                "일간 기준 오행 역할이 필요합니다."
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
                validatedRole,
                "검증 이후 역할이 필요합니다."
        );

        List<ClimateHealthCrossValidationReasonType> reasons =
                new ArrayList<>();

        /*
         * ================================================
         * 종격 방향
         * ================================================
         */
        if (
                followStructureCandidate
        ) {

            if (
                    weakSide(
                            strengthLevel
                    )
            ) {

                if (
                        strengthState.supported()
                ) {

                    reasons.add(
                            ClimateHealthCrossValidationReasonType
                                    .FOLLOW_WEAK_DIRECTION_SUPPORTED
                    );

                } else if (
                        strengthState.contradictory()
                ) {

                    reasons.add(
                            ClimateHealthCrossValidationReasonType
                                    .FOLLOW_WEAK_DIRECTION_CONTRADICTORY
                    );
                }

            } else if (
                    strongSide(
                            strengthLevel
                    )
            ) {

                if (
                        strengthState.supported()
                ) {

                    reasons.add(
                            ClimateHealthCrossValidationReasonType
                                    .FOLLOW_STRONG_DIRECTION_SUPPORTED
                    );

                } else if (
                        strengthState.contradictory()
                ) {

                    reasons.add(
                            ClimateHealthCrossValidationReasonType
                                    .FOLLOW_STRONG_DIRECTION_CONTRADICTORY
                    );
                }
            }

        } else {

            /*
             * ============================================
             * 일반 신약
             * ============================================
             */
            if (
                    weakSide(
                            strengthLevel
                    )
            ) {

                if (
                        supportiveRole(
                                dayMasterRole
                        )
                ) {

                    reasons.add(
                            ClimateHealthCrossValidationReasonType
                                    .WEAK_DAY_MASTER_SUPPORT
                    );

                } else if (
                        strengthState.strained()
                ) {

                    reasons.add(
                            ClimateHealthCrossValidationReasonType
                                    .WEAK_DAY_MASTER_PRESSURE
                    );
                }
            }

            /*
             * ============================================
             * 일반 신강
             * ============================================
             */
            if (
                    strongSide(
                            strengthLevel
                    )
            ) {

                if (
                        releasingRole(
                                dayMasterRole
                        )
                ) {

                    reasons.add(
                            ClimateHealthCrossValidationReasonType
                                    .STRONG_DAY_MASTER_RELEASE
                    );

                } else if (
                        strengthState.strained()
                ) {

                    reasons.add(
                            ClimateHealthCrossValidationReasonType
                                    .STRONG_DAY_MASTER_OVER_SUPPORT
                    );
                }
            }

            if (
                    strengthLevel
                            == DayMasterStrengthLevel.BALANCED
            ) {

                reasons.add(
                        ClimateHealthCrossValidationReasonType
                                .BALANCED_DAY_MASTER_NEUTRAL
                );
            }
        }

        /*
         * ================================================
         * 격국
         * ================================================
         */
        if (
                primarySpecialStructure
        ) {

            reasons.add(
                    ClimateHealthCrossValidationReasonType
                            .SPECIAL_STRUCTURE_REVIEW
            );

        } else {

            if (
                    structureSupportMatch
            ) {

                reasons.add(
                        ClimateHealthCrossValidationReasonType
                                .PRIMARY_STRUCTURE_SUPPORT
                );
            }

            if (
                    structureDamageMatch
            ) {

                reasons.add(
                        ClimateHealthCrossValidationReasonType
                                .PRIMARY_STRUCTURE_DAMAGE
                );
            }

            if (
                    !structureSupportMatch
                            && !structureDamageMatch
            ) {

                reasons.add(
                        ClimateHealthCrossValidationReasonType
                                .PRIMARY_STRUCTURE_NEUTRAL
                );
            }
        }

        /*
         * ================================================
         * 역할 변화
         * ================================================
         */
        ClimateHealthCandidateRole baseRole =
                candidate.role();

        if (
                baseRole.rejected()
        ) {

            reasons.add(
                    ClimateHealthCrossValidationReasonType
                            .BASE_REJECTED_PRESERVED
            );

        } else if (
                baseRole.deferred()
        ) {

            reasons.add(
                    ClimateHealthCrossValidationReasonType
                            .BASE_DEFERRED_PRESERVED
            );

        } else if (
                baseRole.primary()
                        && validatedRole.secondary()
        ) {

            reasons.add(
                    ClimateHealthCrossValidationReasonType
                            .PRIMARY_DOWNGRADED_TO_SECONDARY
            );

        } else if (
                baseRole.selected()
                        && validatedRole.deferred()
        ) {

            reasons.add(
                    ClimateHealthCrossValidationReasonType
                            .SELECTED_CANDIDATE_DEFERRED
            );

        } else {

            reasons.add(
                    ClimateHealthCrossValidationReasonType
                            .BASE_ROLE_PRESERVED
            );
        }

        return List.copyOf(
                reasons
        );
    }

    private boolean supportiveRole(
            DayMasterElementRole role
    ) {

        return role
                == DayMasterElementRole.COMPANION

                || role
                == DayMasterElementRole.RESOURCE;
    }

    private boolean releasingRole(
            DayMasterElementRole role
    ) {

        return role
                == DayMasterElementRole.OUTPUT

                || role
                == DayMasterElementRole.WEALTH

                || role
                == DayMasterElementRole.OFFICER;
    }

    private boolean weakSide(
            DayMasterStrengthLevel level
    ) {

        return level
                == DayMasterStrengthLevel.BALANCED_WEAK

                || level
                == DayMasterStrengthLevel.WEAK

                || level
                == DayMasterStrengthLevel.EXTREME_WEAK;
    }

    private boolean strongSide(
            DayMasterStrengthLevel level
    ) {

        return level
                == DayMasterStrengthLevel.BALANCED_STRONG

                || level
                == DayMasterStrengthLevel.STRONG

                || level
                == DayMasterStrengthLevel.EXTREME_STRONG;
    }
}