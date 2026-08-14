package com.dajeom.rule;

import com.dajeom.model.RemedyCandidate;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.RemedyUsefulGodCandidateRole;
import com.dajeom.type.RemedyUsefulGodReasonType;
import com.dajeom.type.UsefulGodMethodApplicability;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-5.
 *
 * 6-5 약 후보를
 * 실제 병약용신 후보로 재검증한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 6-5 PRIMARY를 그대로 복사하지 않는다.
 *
 * 2.
 * 병약용신법 자체가 INACTIVE면
 *    후보도 만들지 않는다.
 *
 * 3.
 * 병약용신법 자체가 CONDITIONAL이면
 *    오행도 적극 선택하지 않는다.
 *
 * 4.
 * 다른 병 구조를 악화시키는 Conflict가 있으면
 *    CONDITIONAL.
 *
 * 5.
 * 이미 오행이 과도하면
 *    PRIMARY로 올리지 않는다.
 *
 * 6.
 * 신강신약 또는 격국과 직접 충돌하면
 *    CONDITIONAL.
 *
 * 7.
 * HIGH/CRITICAL 병을 직접 다루는 오행은
 *    가장 중요한 병약 후보가 될 수 있다.
 *
 * 8.
 * 의미 있는 병 구조 여러 개를 동시에
 *    완화하는 후보도 강하게 본다.
 *
 * 9.
 * 아직 미해결 병이 남아 있다는 이유만으로
 *    모든 후보를 강등하지 않는다.
 *
 * 다만 하나의 약한 병만 다루면서
 * 다른 의미 있는 병이 미해결 상태라면
 * PRIMARY를 보수적으로 SECONDARY로 낮출 수 있다.
 *
 * 10.
 * 최종 용신 결정은 7-8의 책임이다.
 */
@Component
public class RemedyUsefulGodRule {

    public RemedyUsefulGodCandidateRole determineRole(

            UsefulGodMethodApplicability methodApplicability,

            PathologySeverity overallPathologySeverity,

            RemedyCandidate sourceCandidate,

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState,

            boolean unresolvedMeaningfulPathologyPresent

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "병약용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                overallPathologySeverity,
                "전체 병 구조 심각도가 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "6-5 약 후보가 필요합니다."
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
         * 병약용신법 비활성
         * =================================================
         */
        if (
                methodApplicability.inactive()
        ) {

            return RemedyUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 6-5에서 실제 약 후보가 아님
         * =================================================
         */
        if (
                !sourceCandidate.candidate()
        ) {

            return RemedyUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * =================================================
         * 병약용신법 자체가 조건부
         * =================================================
         *
         * LOW 병 구조만 존재하는 상황 등.
         */
        if (
                methodApplicability.conditional()
        ) {

            return RemedyUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 6-5 자체 Conflict
         * =================================================
         */
        if (
                sourceCandidate.hasConflict()
        ) {

            return RemedyUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 6-5 조건부 후보
         * =================================================
         */
        if (
                sourceCandidate.conditional()
        ) {

            return RemedyUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 현재 오행이 이미 과다
         * =================================================
         */
        if (
                sourceCandidate.saturated()
        ) {

            return RemedyUsefulGodCandidateRole
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

            return RemedyUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        int meaningfulCount =
                sourceCandidate
                        .meaningfulAddressedPathologyCount();

        boolean addressesSevere =
                sourceCandidate
                        .addressesSeverePathology();

        /*
         * =================================================
         * 신강신약 / 격국 부담
         * =================================================
         *
         * 병을 직접 해결하는 방향은 인정하지만
         * 다른 핵심 구조에 부담이 있으므로
         * 한 단계 보수적으로 내린다.
         */
        if (
                strengthState.strained()
                        || structureState.strained()
        ) {

            if (
                    sourceCandidate.primary()
                            && (
                            addressesSevere
                                    || meaningfulCount >= 2
                    )
            ) {

                return RemedyUsefulGodCandidateRole
                        .SECONDARY;
            }

            return RemedyUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 강한 병 직접 대응
         * =================================================
         */
        if (
                addressesSevere
        ) {

            return RemedyUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * =================================================
         * 여러 의미 있는 병을 동시에 완화
         * =================================================
         */
        if (
                meaningfulCount >= 2
        ) {

            if (
                    sourceCandidate.primary()
            ) {

                return RemedyUsefulGodCandidateRole
                        .PRIMARY;
            }

            return RemedyUsefulGodCandidateRole
                    .SECONDARY;
        }

        /*
         * =================================================
         * 아직 미해결 의미 있는 병이 존재
         * =================================================
         *
         * 현재 후보가 병 하나만 다루고 있고
         * 핵심 병 대응까지는 아니라면
         * 단일 6-5 PRIMARY를 그대로 확정하지 않는다.
         */
        if (
                unresolvedMeaningfulPathologyPresent

                        && meaningfulCount <= 1

                        && sourceCandidate.primary()
        ) {

            return RemedyUsefulGodCandidateRole
                    .SECONDARY;
        }

        /*
         * =================================================
         * 깨끗한 6-5 PRIMARY
         * =================================================
         */
        if (
                sourceCandidate.primary()
        ) {

            return RemedyUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * =================================================
         * 깨끗한 6-5 SECONDARY
         * =================================================
         */
        if (
                sourceCandidate.secondary()
        ) {

            return RemedyUsefulGodCandidateRole
                    .SECONDARY;
        }

        /*
         * 정상적으로 여기까지 오면
         * 6-5 selected 계열이어야 하지만
         * 방어적으로 조건부 처리.
         */
        return RemedyUsefulGodCandidateRole
                .CONDITIONAL;
    }

    public List<RemedyUsefulGodReasonType> reasonsFor(

            UsefulGodMethodApplicability methodApplicability,

            PathologySeverity overallPathologySeverity,

            RemedyCandidate sourceCandidate,

            ClimateHealthCrossValidationState strengthState,

            ClimateHealthCrossValidationState structureState,

            boolean unresolvedMeaningfulPathologyPresent,

            RemedyUsefulGodCandidateRole finalRole

    ) {

        Objects.requireNonNull(
                methodApplicability,
                "병약용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                overallPathologySeverity,
                "전체 병 구조 심각도가 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "6-5 약 후보가 필요합니다."
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
                "병약용신 후보 역할이 필요합니다."
        );

        List<RemedyUsefulGodReasonType> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 7-1 방법 적용도
         * =================================================
         */
        if (
                methodApplicability.core()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .REMEDY_METHOD_CORE
            );

        } else if (
                methodApplicability.active()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .REMEDY_METHOD_ACTIVE
            );

        } else if (
                methodApplicability.conditional()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .REMEDY_METHOD_CONDITIONAL
            );
        }

        /*
         * =================================================
         * 전체 병 심각도
         * =================================================
         */
        switch (
                overallPathologySeverity
        ) {

            case CRITICAL ->
                    reasons.add(
                            RemedyUsefulGodReasonType
                                    .CRITICAL_PATHOLOGY_PRESENT
                    );

            case HIGH ->
                    reasons.add(
                            RemedyUsefulGodReasonType
                                    .HIGH_PATHOLOGY_PRESENT
                    );

            case MODERATE ->
                    reasons.add(
                            RemedyUsefulGodReasonType
                                    .MODERATE_PATHOLOGY_PRESENT
                    );

            case LOW ->
                    reasons.add(
                            RemedyUsefulGodReasonType
                                    .LOW_PATHOLOGY_PRESENT
                    );

            case NONE -> {
                // 병 구조가 없으면 별도 심각도 이유 없음.
            }
        }

        /*
         * =================================================
         * 6-5 원본 후보
         * =================================================
         */
        if (
                sourceCandidate.primary()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .SOURCE_PRIMARY
            );

        } else if (
                sourceCandidate.secondary()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .SOURCE_SECONDARY
            );

        } else if (
                sourceCandidate.conditional()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .SOURCE_CONDITIONAL
            );
        }

        /*
         * =================================================
         * 실제 병 구조 대응
         * =================================================
         */
        if (
                sourceCandidate
                        .addressesSeverePathology()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .ADDRESSES_SEVERE_PATHOLOGY
            );
        }

        int meaningfulCount =
                sourceCandidate
                        .meaningfulAddressedPathologyCount();

        if (
                meaningfulCount >= 2
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .ADDRESSES_MULTIPLE_PATHOLOGIES
            );

        } else if (
                meaningfulCount == 1
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .ADDRESSES_SINGLE_MEANINGFUL_PATHOLOGY
            );

        } else if (
                sourceCandidate.candidate()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .NO_MEANINGFUL_REMEDY_EVIDENCE
            );
        }

        if (
                unresolvedMeaningfulPathologyPresent
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .UNRESOLVED_MEANINGFUL_PATHOLOGY_REMAINS
            );
        }

        /*
         * =================================================
         * 6-5 Conflict / 과다
         * =================================================
         */
        if (
                sourceCandidate.hasConflict()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .SOURCE_REMEDY_CONFLICT
            );
        }

        if (
                sourceCandidate.saturated()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
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
                    RemedyUsefulGodReasonType
                            .STRENGTH_SUPPORTED
            );

        } else if (
                strengthState.neutral()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .STRENGTH_NEUTRAL
            );

        } else if (
                strengthState.strained()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .STRENGTH_STRAINED
            );

        } else if (
                strengthState.contradictory()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
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
                    RemedyUsefulGodReasonType
                            .STRUCTURE_SUPPORTED
            );

        } else if (
                structureState.neutral()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .STRUCTURE_NEUTRAL
            );

        } else if (
                structureState.strained()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .STRUCTURE_STRAINED
            );

        } else if (
                structureState.contradictory()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
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
                        && sourceCandidate
                        .addressesSeverePathology()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .PRIMARY_BY_SEVERE_REMEDY
            );

        } else if (
                finalRole.primary()
                        && meaningfulCount >= 2
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .PRIMARY_BY_MULTI_REMEDY
            );

        } else if (
                finalRole.primary()
                        && sourceCandidate.primary()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .PRIMARY_PRESERVED
            );
        }

        if (
                sourceCandidate.primary()
                        && !finalRole.primary()
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .PRIMARY_DOWNGRADED
            );
        }

        if (
                finalRole.conditional()

                        && (
                        sourceCandidate.hasConflict()
                                || sourceCandidate.saturated()
                                || strengthState.requiresCaution()
                                || structureState.requiresCaution()
                )
        ) {

            reasons.add(
                    RemedyUsefulGodReasonType
                            .CANDIDATE_DEFERRED_BY_CONFLICT
            );
        }

        return List.copyOf(
                reasons
        );
    }
}