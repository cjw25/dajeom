package com.dajeom.rule;

import com.dajeom.model.RemedyCandidate;
import com.dajeom.model.RemedyPathologyLink;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.RemedyActionType;
import com.dajeom.type.StructureStatus;
import com.dajeom.type.StructureUsefulGodCandidateRole;
import com.dajeom.type.StructureUsefulGodReasonType;
import com.dajeom.type.UsefulGodMethodApplicability;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-4.
 *
 * 격국용신 후보 선정 Rule.
 *
 * =================================================
 *
 * 일반 내격
 *
 * 1.
 * 주격 성격 요소
 *
 * 2.
 * 주격 파격 요소
 *
 * 3.
 * 파격을 실제로 복구하는 오행
 *
 * 을 분리한다.
 *
 * =================================================
 *
 * 예:
 *
 * 정관격
 * +
 * 상관견관
 *
 * 이라면:
 *
 * 상관 오행
 * -> 파격 요소
 *
 * 인성 등
 * -> 파격 복구 후보
 *
 * 가 될 수 있다.
 *
 * "파격이 있다"
 * ->
 * "파격 오행을 용신"
 *
 * 으로 처리하지 않는다.
 *
 * =================================================
 *
 * 특수격
 *
 * 건록·양인 등은
 * 현재 일반 내격용 성격/파격 Rule만으로
 * PRIMARY를 강제하지 않는다.
 *
 * 세부 특수격 Rule이 없는 상태에서는
 * 조건부 후보로만 남긴다.
 *
 * =================================================
 *
 * 종격
 *
 * 7-7에서 별도 처리한다.
 *
 * =================================================
 *
 * 최종 용신은 7-8에서 결정한다.
 */
@Component
public class StructureUsefulGodRule {

    /**
     * 6-5 RemedyCandidate가
     * 실제 주격 손상 복구 작용을 가지고 있는가.
     */
    public boolean damageRepairMatch(
            RemedyCandidate remedyCandidate
    ) {

        Objects.requireNonNull(
                remedyCandidate,
                "병약 후보가 필요합니다."
        );

        return remedyCandidate
                .remedyLinks()
                .stream()
                .map(
                        RemedyPathologyLink::actionType
                )
                .anyMatch(
                        action ->
                                action
                                        == RemedyActionType
                                        .REPAIR_STRUCTURE_DAMAGE
                );
    }

    public StructureUsefulGodCandidateRole determineRole(

            UsefulGodMethodApplicability methodApplicability,

            StructureStatus structureStatus,

            boolean standardPrimaryStructure,

            boolean specialPrimaryStructure,

            boolean formationSupportMatch,

            boolean damageElementMatch,

            boolean damageRepairMatch,

            ClimateHealthCrossValidationState strengthState,

            boolean followStructureSuppressed

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "격국용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                structureStatus,
                "격국 상태가 필요합니다."
        );

        Objects.requireNonNull(
                strengthState,
                "신강신약 교차검증 상태가 필요합니다."
        );

        /*
         * =================================================
         * 비활성
         * =================================================
         */
        if (
                methodApplicability.inactive()
        ) {

            return StructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 종격
         * =================================================
         *
         * 일반 격국용신법에서
         * 종격 방향을 확정하지 않는다.
         *
         * 다만 기존 성격/복구 근거가 있으면
         * Evidence 보존을 위해 CONDITIONAL.
         */
        if (
                followStructureSuppressed
        ) {

            if (
                    formationSupportMatch
                            || damageRepairMatch
            ) {

                return StructureUsefulGodCandidateRole
                        .CONDITIONAL;
            }

            return StructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 특수격
         * =================================================
         *
         * 일반 내격 공식으로
         * 특수격용신을 확정하지 않는다.
         */
        if (
                specialPrimaryStructure
        ) {

            if (
                    strengthState.contradictory()
            ) {

                return StructureUsefulGodCandidateRole
                        .NOT_NEEDED;
            }

            if (
                    strengthState.supported()
            ) {

                return StructureUsefulGodCandidateRole
                        .CONDITIONAL;
            }

            return StructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * 정상적인 7-4 표준 경로는
         * 일반 내격 PRIMARY다.
         */
        if (
                !standardPrimaryStructure
        ) {

            return StructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 방법 자체가 CONDITIONAL
         * =================================================
         *
         * 주격 UNSTABLE 등이 대표적.
         */
        if (
                methodApplicability.conditional()
        ) {

            if (
                    formationSupportMatch
                            || damageRepairMatch
            ) {

                return StructureUsefulGodCandidateRole
                        .CONDITIONAL;
            }

            return StructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 순수 파격 오행
         * =================================================
         */
        if (
                damageElementMatch
                        && !formationSupportMatch
                        && !damageRepairMatch
        ) {

            return StructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 성격·파격이 동시에 걸림
         *
         * 또는
         *
         * 복구·파격이 동시에 걸림
         * =================================================
         */
        if (
                damageElementMatch
                        && (
                        formationSupportMatch
                                || damageRepairMatch
                )
        ) {

            return StructureUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 일간 강약 핵심 방향과 직접 모순
         * =================================================
         */
        if (
                strengthState.contradictory()
        ) {

            if (
                    formationSupportMatch
                            || damageRepairMatch
            ) {

                return StructureUsefulGodCandidateRole
                        .CONDITIONAL;
            }

            return StructureUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 파격 복구
         * =================================================
         *
         * 주격에 실제 손상이 있을수록
         * 복구 오행의 격국용신 가치가 높다.
         */
        if (
                damageRepairMatch
        ) {

            if (
                    strengthState.strained()
            ) {

                return StructureUsefulGodCandidateRole
                        .SECONDARY;
            }

            if (
                    structureStatus
                            == StructureStatus.COMPROMISED

                            || structureStatus
                            == StructureStatus.ESTABLISHED_WITH_STRAIN
            ) {

                return StructureUsefulGodCandidateRole
                        .PRIMARY;
            }

            if (
                    structureStatus
                            == StructureStatus.ESTABLISHED
            ) {

                return StructureUsefulGodCandidateRole
                        .SECONDARY;
            }

            return StructureUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 성격 요소
         * =================================================
         */
        if (
                formationSupportMatch
        ) {

            if (
                    strengthState.strained()
            ) {

                return StructureUsefulGodCandidateRole
                        .SECONDARY;
            }

            if (
                    structureStatus
                            == StructureStatus.UNSTABLE
            ) {

                return StructureUsefulGodCandidateRole
                        .CONDITIONAL;
            }

            return StructureUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * =================================================
         * 적극적인 격국 근거 없음
         * =================================================
         */
        return StructureUsefulGodCandidateRole
                .NOT_NEEDED;
    }

    public List<StructureUsefulGodReasonType> reasonsFor(

            UsefulGodMethodApplicability methodApplicability,

            StructureStatus structureStatus,

            boolean standardPrimaryStructure,

            boolean specialPrimaryStructure,

            boolean formationSupportMatch,

            boolean damageElementMatch,

            boolean damageRepairMatch,

            ClimateHealthCrossValidationState strengthState,

            boolean followStructureSuppressed,

            StructureUsefulGodCandidateRole finalRole

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "격국용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                structureStatus,
                "격국 상태가 필요합니다."
        );

        Objects.requireNonNull(
                strengthState,
                "신강신약 교차검증 상태가 필요합니다."
        );

        Objects.requireNonNull(
                finalRole,
                "격국용신 후보 역할이 필요합니다."
        );

        List<StructureUsefulGodReasonType> reasons =
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
                    StructureUsefulGodReasonType
                            .STRUCTURE_METHOD_CORE
            );

        } else if (
                methodApplicability.active()
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .STRUCTURE_METHOD_ACTIVE
            );

        } else if (
                methodApplicability.conditional()
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .STRUCTURE_METHOD_CONDITIONAL
            );
        }

        /*
         * =================================================
         * 주격 유형
         * =================================================
         */
        if (
                standardPrimaryStructure
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .STANDARD_PRIMARY_STRUCTURE
            );
        }

        if (
                specialPrimaryStructure
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .SPECIAL_PRIMARY_STRUCTURE
            );

            reasons.add(
                    StructureUsefulGodReasonType
                            .SPECIAL_STRUCTURE_CONSERVATIVE_REVIEW
            );
        }

        if (
                followStructureSuppressed
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .FOLLOW_STRUCTURE_SUPPRESSION
            );
        }

        /*
         * =================================================
         * 격국 상태
         * =================================================
         */
        switch (
                structureStatus
        ) {

            case ESTABLISHED ->
                    reasons.add(
                            StructureUsefulGodReasonType
                                    .STRUCTURE_ESTABLISHED
                    );

            case ESTABLISHED_WITH_STRAIN ->
                    reasons.add(
                            StructureUsefulGodReasonType
                                    .STRUCTURE_ESTABLISHED_WITH_STRAIN
                    );

            case COMPROMISED ->
                    reasons.add(
                            StructureUsefulGodReasonType
                                    .STRUCTURE_COMPROMISED
                    );

            case UNSTABLE ->
                    reasons.add(
                            StructureUsefulGodReasonType
                                    .STRUCTURE_UNSTABLE
                    );

            case SPECIAL_REVIEW ->
                    reasons.add(
                            StructureUsefulGodReasonType
                                    .STRUCTURE_SPECIAL_REVIEW
                    );
        }

        /*
         * =================================================
         * 성격 / 파격 / 복구
         * =================================================
         */
        if (
                formationSupportMatch
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .FORMATION_SUPPORT_ELEMENT
            );
        }

        if (
                damageElementMatch
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .DAMAGE_ELEMENT
            );
        }

        if (
                damageRepairMatch
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .DAMAGE_REPAIR_ELEMENT
            );
        }

        if (
                formationSupportMatch
                        && damageElementMatch
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .SUPPORT_AND_DAMAGE_MIXED
            );
        }

        if (
                damageRepairMatch
                        && damageElementMatch
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .REPAIR_AND_DAMAGE_MIXED
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
                    StructureUsefulGodReasonType
                            .STRENGTH_SUPPORTED
            );

        } else if (
                strengthState.neutral()
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .STRENGTH_NEUTRAL
            );

        } else if (
                strengthState.strained()
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .STRENGTH_STRAINED
            );

        } else if (
                strengthState.contradictory()
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .STRENGTH_CONTRADICTORY
            );
        }

        /*
         * =================================================
         * 최종 역할 설명
         * =================================================
         */
        if (
                finalRole.primary()
                        && damageRepairMatch
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .PRIMARY_BY_DAMAGE_REPAIR
            );

        } else if (
                finalRole.primary()
                        && formationSupportMatch
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .PRIMARY_BY_STRUCTURE_SUPPORT
            );
        }

        if (
                finalRole.secondary()
                        && strengthState.strained()
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .SECONDARY_BY_STRENGTH_STRAIN
            );
        }

        if (
                finalRole.conditional()

                        && (
                        damageElementMatch
                                || strengthState.requiresCaution()
                                || followStructureSuppressed
                                || specialPrimaryStructure
                )
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .CONDITIONAL_BY_CONFLICT
            );
        }

        if (
                !formationSupportMatch
                        && !damageRepairMatch
        ) {

            reasons.add(
                    StructureUsefulGodReasonType
                            .NO_POSITIVE_STRUCTURE_EVIDENCE
            );
        }

        return List.copyOf(
                reasons
        );
    }
}