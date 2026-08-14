package com.dajeom.rule;

import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.model.PassageEvidence;
import com.dajeom.model.RemedyCandidate;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.PassageCandidateReasonType;
import com.dajeom.type.PassageCandidateRole;
import com.dajeom.type.PassageNeedLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-7.
 *
 * 6-6 통관 필요성을
 * 실제 통관 오행 후보 역할로 변환한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 통관 필요도가 NONE이면
 *    후보를 만들지 않는다.
 *
 * 2.
 * HIGH / CRITICAL 통관 필요는
 *    기본적으로 강한 후보 근거다.
 *
 * 3.
 * 병약이나 조후와 실제 충돌하면
 *    PRIMARY로 강행하지 않고 CONDITIONAL.
 *
 * 4.
 * 해당 오행이 이미 VERY_HIGH라면
 *    "통관이 필요하니 더 강화"라고
 *    단순 결론내리지 않는다.
 *
 * 5.
 * MODERATE 통관에서도:
 *
 *    - 일간 불리
 *    - 병약 방향 일치
 *    - 조후 방향 일치
 *
 *    등이 함께 존재하면
 *    PRIMARY까지 올라갈 수 있다.
 *
 * 6.
 * LOW이고 bridge가 이미 충분히 작동한다면
 *    별도 통관 후보로 선택하지 않을 수 있다.
 *
 * 7.
 * 이것은 용신 판정 Rule이 아니다.
 */
@Component
public class PassageCandidateRule {

    public PassageNeedLevel strongestNeedOf(
            List<PassageEvidence> evidences
    ) {

        Objects.requireNonNull(
                evidences,
                "통관 Evidence 목록이 필요합니다."
        );

        return evidences
                .stream()
                .map(
                        PassageEvidence
                                ::needLevel
                )
                .max(
                        Comparator.comparingInt(
                                PassageNeedLevel
                                        ::rank
                        )
                )
                .orElse(
                        PassageNeedLevel.NONE
                );
    }

    public boolean hasInsufficientBridge(
            List<PassageEvidence> evidences
    ) {

        Objects.requireNonNull(
                evidences,
                "통관 Evidence 목록이 필요합니다."
        );

        return evidences
                .stream()
                .filter(
                        PassageEvidence
                                ::passageRequired
                )
                .anyMatch(
                        PassageEvidence
                                ::bridgeInsufficient
                );
    }

    public boolean bridgeAlreadyFunctions(
            List<PassageEvidence> evidences
    ) {

        Objects.requireNonNull(
                evidences,
                "통관 Evidence 목록이 필요합니다."
        );

        List<PassageEvidence> required =
                evidences
                        .stream()
                        .filter(
                                PassageEvidence
                                        ::passageRequired
                        )
                        .toList();

        return !required.isEmpty()
                && required
                .stream()
                .allMatch(
                        PassageEvidence
                                ::bridgeAlreadyFunctions
                );
    }

    public boolean dayMasterDisadvantaged(
            List<PassageEvidence> evidences
    ) {

        Objects.requireNonNull(
                evidences,
                "통관 Evidence 목록이 필요합니다."
        );

        return evidences
                .stream()
                .anyMatch(
                        PassageEvidence
                                ::dayMasterDisadvantaged
                );
    }

    /**
     * 통관 후보 최종 역할.
     *
     * 아직 6-8/6-9 이전이므로
     * 여러 PRIMARY가 존재할 수 있다.
     */
    public PassageCandidateRole determineRole(

            List<PassageEvidence> evidences,

            RemedyCandidate remedyCandidate,

            ClimateElementCandidate climateCandidate

    ) {

        Objects.requireNonNull(
                evidences,
                "통관 Evidence 목록이 필요합니다."
        );

        Objects.requireNonNull(
                remedyCandidate,
                "병약 후보가 필요합니다."
        );

        Objects.requireNonNull(
                climateCandidate,
                "조후 후보가 필요합니다."
        );

        PassageNeedLevel strongestNeed =
                strongestNeedOf(
                        evidences
                );

        if (
                strongestNeed.none()
        ) {

            return PassageCandidateRole
                    .NOT_NEEDED;
        }

        boolean insufficientBridge =
                hasInsufficientBridge(
                        evidences
                );

        boolean bridgeAlreadyFunctions =
                bridgeAlreadyFunctions(
                        evidences
                );

        boolean dayMasterDisadvantaged =
                dayMasterDisadvantaged(
                        evidences
                );

        ClimateElementPresenceLevel presence =
                climateCandidate
                        .presenceLevel();

        boolean remedyConflict =
                remedyCandidate
                        .hasConflict();

        boolean climateConflict =
                climateCandidate
                        .hasConflict();

        /*
         * ================================================
         * 1.
         * 다른 핵심 분석과 충돌
         * ================================================
         */
        if (
                remedyConflict
                        || climateConflict
        ) {

            return PassageCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 2.
         * 해당 오행이 이미 과도하게 많음
         * ================================================
         *
         * 통관 방향은 맞더라도
         * 단순 추가 강화는 위험하므로
         * 조건부로 남긴다.
         */
        if (
                presence.saturated()
        ) {

            return PassageCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 3.
         * HIGH / CRITICAL
         * ================================================
         */
        if (
                strongestNeed.highPriority()
        ) {

            return PassageCandidateRole
                    .PRIMARY;
        }

        /*
         * ================================================
         * 4.
         * MODERATE
         * ================================================
         */
        if (
                strongestNeed.moderate()
        ) {

            /*
             * 약한 일간이 직접 불리한 상극 관계라면
             * 통관을 더 중요하게 본다.
             */
            if (
                    dayMasterDisadvantaged
            ) {

                return PassageCandidateRole
                        .PRIMARY;
            }

            /*
             * bridge가 부족한데
             * 병약에서도 같은 오행을 1순위로 지목.
             */
            if (
                    insufficientBridge

                            && remedyCandidate
                            .primary()
            ) {

                return PassageCandidateRole
                        .PRIMARY;
            }

            /*
             * bridge 부족
             * +
             * 병약/조후 모두 같은 오행 방향.
             */
            if (
                    insufficientBridge

                            && remedyCandidate
                            .selected()

                            && climateCandidate
                            .selected()
            ) {

                return PassageCandidateRole
                        .PRIMARY;
            }

            return PassageCandidateRole
                    .SECONDARY;
        }

        /*
         * ================================================
         * 5.
         * LOW
         * ================================================
         *
         * 이미 bridge가 충분하고,
         * 병약/조후에서도 해당 오행을
         * 적극 요구하지 않는다면
         * 추가 통관 후보로 선정하지 않는다.
         */
        if (
                strongestNeed.low()
        ) {

            if (
                    bridgeAlreadyFunctions

                            && !dayMasterDisadvantaged

                            && !remedyCandidate.selected()

                            && !climateCandidate.selected()
            ) {

                return PassageCandidateRole
                        .NOT_NEEDED;
            }

            return PassageCandidateRole
                    .SECONDARY;
        }

        return PassageCandidateRole
                .NOT_NEEDED;
    }

    public List<PassageCandidateReasonType>
    reasonsFor(

            List<PassageEvidence> evidences,

            RemedyCandidate remedyCandidate,

            ClimateElementCandidate climateCandidate

    ) {

        Objects.requireNonNull(
                evidences,
                "통관 Evidence 목록이 필요합니다."
        );

        Objects.requireNonNull(
                remedyCandidate,
                "병약 후보가 필요합니다."
        );

        Objects.requireNonNull(
                climateCandidate,
                "조후 후보가 필요합니다."
        );

        List<PassageCandidateReasonType> reasons =
                new ArrayList<>();

        PassageNeedLevel strongestNeed =
                strongestNeedOf(
                        evidences
                );

        if (
                strongestNeed.critical()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .CRITICAL_PASSAGE_NEED
            );

        } else if (
                strongestNeed.high()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .HIGH_PASSAGE_NEED
            );

        } else if (
                strongestNeed.moderate()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .MEANINGFUL_PASSAGE_NEED
            );

        } else if (
                strongestNeed.low()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .LOW_PASSAGE_NEED
            );
        }

        boolean anyAbsent =
                evidences
                        .stream()
                        .filter(
                                PassageEvidence
                                        ::passageRequired
                        )
                        .anyMatch(
                                evidence ->
                                        evidence
                                                .bridgeState()
                                                .absent()
                        );

        boolean anyWeak =
                evidences
                        .stream()
                        .filter(
                                PassageEvidence
                                        ::passageRequired
                        )
                        .anyMatch(
                                evidence ->
                                        evidence
                                                .bridgeState()
                                                .weak()
                        );

        if (
                anyAbsent
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .BRIDGE_ABSENT
            );

        } else if (
                anyWeak
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .BRIDGE_WEAK
            );

        } else if (
                bridgeAlreadyFunctions(
                        evidences
                )
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .BRIDGE_ALREADY_FUNCTIONING
            );
        }

        if (
                dayMasterDisadvantaged(
                        evidences
                )
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .DAY_MASTER_DISADVANTAGED
            );
        }

        if (
                remedyCandidate.primary()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .REMEDY_PRIMARY_ALIGNMENT
            );

        } else if (
                remedyCandidate.secondary()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .REMEDY_SECONDARY_ALIGNMENT
            );
        }

        if (
                climateCandidate.primary()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .CLIMATE_PRIMARY_ALIGNMENT
            );

        } else if (
                climateCandidate.secondary()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .CLIMATE_SECONDARY_ALIGNMENT
            );
        }

        if (
                remedyCandidate.hasConflict()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .REMEDY_CONFLICT
            );
        }

        if (
                climateCandidate.hasConflict()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .CLIMATE_CONFLICT
            );
        }

        if (
                climateCandidate
                        .presenceLevel()
                        .saturated()
        ) {

            reasons.add(
                    PassageCandidateReasonType
                            .ELEMENT_SATURATED
            );
        }

        return List.copyOf(
                reasons
        );
    }
}