package com.dajeom.rule;

import com.dajeom.model.PassageCandidate;
import com.dajeom.model.PassageEvidence;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.PassageNeedLevel;
import com.dajeom.type.PassageUsefulGodCandidateRole;
import com.dajeom.type.PassageUsefulGodReasonType;
import com.dajeom.type.UsefulGodMethodApplicability;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-6.
 *
 * 6-7 통관 후보를
 * 실제 통관용신 후보로 재검증한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 6-7 PRIMARY를 그대로 복사하지 않는다.
 *
 * 2.
 * 통관용신법이 INACTIVE면
 *    통관용신 후보도 만들지 않는다.
 *
 * 3.
 * 통관용신법이 CONDITIONAL이면
 *    실제 오행 후보도 조건부로 제한한다.
 *
 * 4.
 * 병약 또는 조후와 Conflict가 있으면
 *    CONDITIONAL.
 *
 * 5.
 * 이미 해당 오행이 과도하면
 *    PRIMARY로 사용하지 않는다.
 *
 * 6.
 * 신강신약 또는 격국과 직접 충돌하면
 *    CONDITIONAL.
 *
 * 7.
 * HIGH / CRITICAL 통관 필요
 * +
 * bridge 부족
 *
 * 이면 강한 PRIMARY 근거가 된다.
 *
 * 8.
 * 일간이 실제 상극 관계에서 불리하고
 * 통관 오행도 부족하면 우선도를 높인다.
 *
 * 9.
 * 병약·조후가 같은 오행을 지지하면
 * 추가적인 수렴 Evidence가 된다.
 *
 * 다만 통관 필요 자체가 약한데
 * 다른 방법과 일치한다는 이유만으로
 * 무조건 PRIMARY로 올리지는 않는다.
 *
 * 10.
 * bridge가 이미 충분히 작동한다면
 * 통관 오행 추가 강화 필요성은 낮게 본다.
 *
 * 11.
 * 최종 용신 결정은 7-8의 책임이다.
 */
@Component
public class PassageUsefulGodRule {

    public PassageUsefulGodCandidateRole determineRole(

            UsefulGodMethodApplicability methodApplicability,

            PassageCandidate sourceCandidate,

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "통관용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "6-7 통관 후보가 필요합니다."
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
         * =================================================
         * 통관용신법 자체가 비활성
         * =================================================
         */
        if (
                methodApplicability.inactive()
        ) {

            return PassageUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 6-7에서 실제 통관 후보가 아님
         * =================================================
         */
        if (
                !sourceCandidate.candidate()
        ) {

            return PassageUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 통관용신법 자체가 조건부
         * =================================================
         *
         * 전체 통관 필요 LOW 등이 대표적.
         */
        if (
                methodApplicability.conditional()
        ) {

            return PassageUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 병약 / 조후와 실제 충돌
         * =================================================
         */
        if (
                sourceCandidate.crossAnalysisConflict()
        ) {

            return PassageUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 6-7 자체 조건부
         * =================================================
         */
        if (
                sourceCandidate.conditional()
        ) {

            return PassageUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 현재 오행 과다
         * =================================================
         */
        if (
                sourceCandidate.saturated()
        ) {

            return PassageUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 신강신약 / 격국 직접 충돌
         * =================================================
         */
        if (
                strengthState.contradictory()
                        || structureState.contradictory()
        ) {

            return PassageUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        PassageNeedLevel need =
                sourceCandidate
                        .strongestNeed();

        boolean bridgeInsufficient =
                sourceCandidate
                        .bridgeInsufficient();

        boolean bridgeAlreadyFunctions =
                sourceCandidate
                        .bridgeAlreadyFunctions();

        boolean dayMasterDisadvantaged =
                sourceCandidate
                        .dayMasterDisadvantaged();

        boolean remedyAligned =
                sourceCandidate
                        .remedyAligned();

        boolean climateAligned =
                sourceCandidate
                        .climateAligned();

        /*
         * =================================================
         * 신강신약 / 격국 부담
         * =================================================
         *
         * 높은 통관 필요가 있어도
         * 다른 핵심 구조에 부담이 있으면
         * PRIMARY를 그대로 유지하지 않는다.
         */
        if (
                strengthState.strained()
                        || structureState.strained()
        ) {

            if (
                    sourceCandidate.primary()
                            && need.highPriority()
                            && bridgeInsufficient
            ) {

                return PassageUsefulGodCandidateRole
                        .SECONDARY;
            }

            return PassageUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * CRITICAL / HIGH
         *
         * bridge까지 실제 부족
         * =================================================
         */
        if (
                need.highPriority()
                        && bridgeInsufficient
        ) {

            return PassageUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * =================================================
         * 일간이 직접 불리한 상극 대치
         *
         * +
         *
         * 의미 있는 통관 필요
         *
         * +
         *
         * bridge 부족
         * =================================================
         */
        if (
                dayMasterDisadvantaged

                        && need.meaningful()

                        && bridgeInsufficient
        ) {

            return PassageUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * =================================================
         * MODERATE
         *
         * bridge 부족
         * +
         * 병약·조후 양쪽 수렴
         * =================================================
         */
        if (
                need.moderate()

                        && bridgeInsufficient

                        && remedyAligned

                        && climateAligned
        ) {

            return PassageUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * =================================================
         * MODERATE
         *
         * bridge 부족
         * +
         * 병약 또는 조후 한쪽 수렴
         * =================================================
         */
        if (
                need.moderate()

                        && bridgeInsufficient

                        && (
                        remedyAligned
                                || climateAligned
                )
        ) {

            return PassageUsefulGodCandidateRole
                    .SECONDARY;
        }

        /*
         * =================================================
         * bridge가 이미 일정 수준 작동
         * =================================================
         *
         * 통관 방향이 틀렸다는 뜻은 아니지만
         * 추가적인 강화 우선도는 낮춘다.
         */
        if (
                bridgeAlreadyFunctions
        ) {

            if (
                    need.highPriority()
            ) {

                return PassageUsefulGodCandidateRole
                        .SECONDARY;
            }

            if (
                    remedyAligned
                            || climateAligned
            ) {

                return PassageUsefulGodCandidateRole
                        .SECONDARY;
            }

            return PassageUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 깨끗한 6-7 PRIMARY
         * =================================================
         */
        if (
                sourceCandidate.primary()
        ) {

            return PassageUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * =================================================
         * 깨끗한 6-7 SECONDARY
         * =================================================
         */
        if (
                sourceCandidate.secondary()
        ) {

            return PassageUsefulGodCandidateRole
                    .SECONDARY;
        }

        return PassageUsefulGodCandidateRole
                .CONDITIONAL;
    }

    public List<PassageUsefulGodReasonType> reasonsFor(

            UsefulGodMethodApplicability methodApplicability,

            PassageCandidate sourceCandidate,

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState,

            PassageUsefulGodCandidateRole finalRole

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "통관용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "6-7 통관 후보가 필요합니다."
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
                "통관용신 후보 역할이 필요합니다."
        );

        List<PassageUsefulGodReasonType> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 방법 적용도
         * =================================================
         */
        if (
                methodApplicability.core()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .PASSAGE_METHOD_CORE
            );

        } else if (
                methodApplicability.active()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .PASSAGE_METHOD_ACTIVE
            );

        } else if (
                methodApplicability.conditional()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .PASSAGE_METHOD_CONDITIONAL
            );
        }

        /*
         * =================================================
         * 통관 필요도
         * =================================================
         */
        PassageNeedLevel need =
                sourceCandidate
                        .strongestNeed();

        if (
                need.critical()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .CRITICAL_PASSAGE_NEED
            );

        } else if (
                need.high()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .HIGH_PASSAGE_NEED
            );

        } else if (
                need.moderate()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .MODERATE_PASSAGE_NEED
            );

        } else if (
                need.low()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .LOW_PASSAGE_NEED
            );
        }

        /*
         * =================================================
         * 6-7 원본 역할
         * =================================================
         */
        if (
                sourceCandidate.primary()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .SOURCE_PRIMARY
            );

        } else if (
                sourceCandidate.secondary()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .SOURCE_SECONDARY
            );

        } else if (
                sourceCandidate.conditional()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .SOURCE_CONDITIONAL
            );
        }

        /*
         * =================================================
         * 실제 통관 오행 확보 상태
         * =================================================
         */
        if (
                sourceCandidate.bridgeInsufficient()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .BRIDGE_ABSENT_OR_WEAK
            );
        }

        if (
                sourceCandidate.bridgeAlreadyFunctions()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .BRIDGE_ALREADY_FUNCTIONING
            );
        }

        /*
         * =================================================
         * 일간 직접 불리
         * =================================================
         */
        if (
                sourceCandidate.dayMasterDisadvantaged()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .DAY_MASTER_DISADVANTAGED
            );
        }

        /*
         * =================================================
         * PassageEvidence 규모
         * =================================================
         */
        int meaningfulCount =
                (int) sourceCandidate
                        .passageEvidences()
                        .stream()
                        .filter(
                                PassageEvidence
                                        ::meaningfulPassageRequired
                        )
                        .count();

        int highPriorityCount =
                (int) sourceCandidate
                        .passageEvidences()
                        .stream()
                        .filter(
                                PassageEvidence
                                        ::highPriority
                        )
                        .count();

        if (
                meaningfulCount >= 2
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .MULTIPLE_MEANINGFUL_PASSAGE_EVIDENCES
            );
        }

        if (
                highPriorityCount > 0
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .HIGH_PRIORITY_CONFLICT_ADDRESSED
            );
        }

        /*
         * =================================================
         * 병약·조후 수렴
         * =================================================
         */
        if (
                sourceCandidate.remedyAligned()
                        && sourceCandidate.climateAligned()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .REMEDY_AND_CLIMATE_ALIGNMENT
            );

        } else if (
                sourceCandidate.remedyAligned()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .REMEDY_ALIGNMENT
            );

        } else if (
                sourceCandidate.climateAligned()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .CLIMATE_ALIGNMENT
            );
        }

        /*
         * =================================================
         * 6-7 Conflict / 과다
         * =================================================
         */
        if (
                sourceCandidate.crossAnalysisConflict()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .SOURCE_PASSAGE_CONFLICT
            );
        }

        if (
                sourceCandidate.saturated()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
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
                    PassageUsefulGodReasonType
                            .STRENGTH_SUPPORTED
            );

        } else if (
                strengthState.neutral()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .STRENGTH_NEUTRAL
            );

        } else if (
                strengthState.strained()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .STRENGTH_STRAINED
            );

        } else if (
                strengthState.contradictory()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
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
                    PassageUsefulGodReasonType
                            .STRUCTURE_SUPPORTED
            );

        } else if (
                structureState.neutral()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .STRUCTURE_NEUTRAL
            );

        } else if (
                structureState.strained()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .STRUCTURE_STRAINED
            );

        } else if (
                structureState.contradictory()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .STRUCTURE_CONTRADICTORY
            );
        }

        /*
         * =================================================
         * 최종 역할
         * =================================================
         */
        if (
                finalRole.primary()

                        && need.highPriority()

                        && sourceCandidate.bridgeInsufficient()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .PRIMARY_BY_HIGH_PASSAGE_NEED
            );

        } else if (
                finalRole.primary()

                        && sourceCandidate
                        .dayMasterDisadvantaged()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .PRIMARY_BY_DAY_MASTER_PROTECTION
            );

        } else if (
                finalRole.primary()

                        && sourceCandidate
                        .anyCrossAnalysisAlignment()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .PRIMARY_BY_CROSS_METHOD_ALIGNMENT
            );

        } else if (
                finalRole.primary()
                        && sourceCandidate.primary()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .PRIMARY_PRESERVED
            );
        }

        if (
                sourceCandidate.primary()
                        && !finalRole.primary()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .PRIMARY_DOWNGRADED
            );
        }

        if (
                finalRole.conditional()

                        && (
                        sourceCandidate.crossAnalysisConflict()
                                || sourceCandidate.saturated()
                                || strengthState.requiresCaution()
                                || structureState.requiresCaution()
                )
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .CANDIDATE_DEFERRED_BY_CONFLICT
            );
        }

        if (
                !sourceCandidate
                        .strongestNeed()
                        .meaningful()
        ) {

            reasons.add(
                    PassageUsefulGodReasonType
                            .NO_MEANINGFUL_PASSAGE_EVIDENCE
            );
        }

        return List.copyOf(
                reasons
        );
    }
}