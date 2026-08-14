package com.dajeom.rule;

import com.dajeom.model.UsefulGodMethodContribution;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.UsefulGodCandidateRole;
import com.dajeom.type.UsefulGodConvergenceLevel;
import com.dajeom.type.UsefulGodIntegrationReasonType;
import com.dajeom.type.UsefulGodMethodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-8.
 *
 * 여섯 용신법의 결과를 통합하는 Rule.
 *
 * =================================================
 *
 * 절대 사용하지 않는 방식
 *
 * 억부 PRIMARY = +5
 * 조후 PRIMARY = +5
 * 격국 PRIMARY = +5
 *
 * 총점 높은 오행 = 용신
 *
 * 같은 단순 점수 합산.
 *
 * =================================================
 *
 * 대신 다음 순서를 사용한다.
 *
 * 1.
 * 실제 종격 모드인가.
 *
 * 2.
 * CORE 방법이 무엇인가.
 *
 * 3.
 * CORE 방법의 PRIMARY가 같은 오행으로 수렴하는가.
 *
 * 4.
 * 다른 ACTIVE 방법이 추가로 지지하는가.
 *
 * 5.
 * CORE 방법에 직접 반대 Evidence가 있는가.
 *
 * 6.
 * CORE 방법에서 CONDITIONAL 경고가 있는가.
 *
 * 7.
 * ACTIVE 방법 충돌이 있는가.
 *
 * 8.
 * 해당 오행이 이미 과도한가.
 *
 * =================================================
 *
 * 복수 주용신 후보는 허용한다.
 *
 * 동률을 enum 순서나 오행 순번으로
 * 억지로 하나 선택하지 않는다.
 *
 * =================================================
 *
 * 종격 모드:
 *
 * FOLLOW_STRUCTURE가 CORE 판단축으로 우선한다.
 *
 * 다만 다른 실제 CORE 방법의
 * 직접 충돌 Evidence 자체를 삭제하지 않는다.
 *
 * FOLLOW_STRUCTURE가 PRIMARY라 하더라도
 * 다른 CORE 방법에서 직접 충돌이 존재하면
 * PRIMARY_USEFUL_GOD로 확정하지 않고
 * DEFERRED로 보존한다.
 *
 * 이는:
 *
 * - 종격 우선축
 * - 다른 CORE 충돌 Evidence
 *
 * 를 동시에 보존하기 위한 처리다.
 */
@Component
public class UsefulGodSelectionRule {

    public UsefulGodConvergenceLevel determineConvergenceLevel(

            List<UsefulGodMethodContribution> contributions,

            boolean followStructureMode

    ) {

        validateContributions(
                contributions
        );

        if (
                followStructureMode
        ) {

            UsefulGodMethodContribution follow =
                    contributionOf(
                            contributions,
                            UsefulGodMethodType.FOLLOW_STRUCTURE
                    );

            if (
                    follow.coreMethod()
                            && follow.primary()
            ) {

                return UsefulGodConvergenceLevel
                        .FOLLOW_CORE_PRIMARY;
            }

            if (
                    follow.coreMethod()
                            && follow.secondary()
            ) {

                return UsefulGodConvergenceLevel
                        .FOLLOW_CORE_SUPPORT;
            }

            if (
                    follow.conditionalCandidate()
            ) {

                return UsefulGodConvergenceLevel
                        .CONDITIONAL_ONLY;
            }

            return UsefulGodConvergenceLevel
                    .INACTIVE;
        }

        int corePrimaryCount =
                (int) contributions
                        .stream()
                        .filter(
                                UsefulGodMethodContribution::corePrimary
                        )
                        .count();

        int meaningfulSelectedCount =
                (int) contributions
                        .stream()
                        .filter(
                                UsefulGodMethodContribution
                                        ::meaningfulSelected
                        )
                        .count();

        int selectedCount =
                (int) contributions
                        .stream()
                        .filter(
                                UsefulGodMethodContribution::selected
                        )
                        .count();

        int conditionalCount =
                (int) contributions
                        .stream()
                        .filter(
                                UsefulGodMethodContribution
                                        ::conditionalCandidate
                        )
                        .count();

        if (
                corePrimaryCount >= 2
        ) {

            return UsefulGodConvergenceLevel
                    .MULTI_CORE_PRIMARY;
        }

        if (
                corePrimaryCount == 1
                        && meaningfulSelectedCount >= 2
        ) {

            return UsefulGodConvergenceLevel
                    .CORE_PRIMARY_WITH_SUPPORT;
        }

        if (
                corePrimaryCount == 1
        ) {

            return UsefulGodConvergenceLevel
                    .SINGLE_CORE_PRIMARY;
        }

        if (
                meaningfulSelectedCount >= 2
                        || selectedCount >= 2
        ) {

            return UsefulGodConvergenceLevel
                    .MULTI_METHOD_SUPPORT;
        }

        if (
                selectedCount == 1
        ) {

            return UsefulGodConvergenceLevel
                    .SINGLE_METHOD_SUPPORT;
        }

        if (
                conditionalCount > 0
        ) {

            return UsefulGodConvergenceLevel
                    .CONDITIONAL_ONLY;
        }

        return UsefulGodConvergenceLevel
                .INACTIVE;
    }

    public UsefulGodCandidateRole determineRole(

            UsefulGodConvergenceLevel convergenceLevel,

            List<UsefulGodMethodContribution> contributions,

            ClimateElementPresenceLevel presenceLevel,

            boolean followStructureMode

    ) {

        Objects.requireNonNull(
                convergenceLevel,
                "용신법 수렴 단계가 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 세력 수준이 필요합니다."
        );

        validateContributions(
                contributions
        );

        /*
         * =================================================
         * 전체 Contribution 상태
         * =================================================
         *
         * 종격/일반 구조 모두 같은 Evidence 정의를 사용한다.
         */

        boolean coreConflict =
                contributions
                        .stream()
                        .anyMatch(
                                UsefulGodMethodContribution::coreConflict
                        );

        boolean activeConflict =
                contributions
                        .stream()
                        .anyMatch(
                                UsefulGodMethodContribution::activeConflict
                        );

        boolean coreConditional =
                contributions
                        .stream()
                        .anyMatch(
                                UsefulGodMethodContribution
                                        ::coreConditionalWarning
                        );

        boolean hasPositiveSelection =
                contributions
                        .stream()
                        .anyMatch(
                                UsefulGodMethodContribution::selected
                        );

        /*
         * =================================================
         * 종격 모드
         * =================================================
         *
         * FOLLOW_STRUCTURE CORE가 우선한다.
         *
         * 그러나 다른 실제 CORE 방법의
         * 충돌 Evidence를 지워버리지는 않는다.
         *
         * 기존 오류 원인:
         *
         * FOLLOW_STRUCTURE PRIMARY
         * ->
         * 바로 PRIMARY_USEFUL_GOD 반환
         * ->
         * 아래 일반 coreConflict 검증을 건너뜀
         *
         * 그 결과 UsefulGodCandidate 생성 시:
         *
         * PRIMARY_USEFUL_GOD
         * +
         * hasCoreConflict() == true
         *
         * 모순이 발생했다.
         */
        if (
                followStructureMode
        ) {

            UsefulGodMethodContribution follow =
                    contributionOf(
                            contributions,
                            UsefulGodMethodType.FOLLOW_STRUCTURE
                    );

            /*
             * FOLLOW_STRUCTURE 자체가
             * 종격 흐름을 깨는 경우.
             */
            if (
                    follow.followFlowBreaking()
                            || follow.directConflict()
            ) {

                return UsefulGodCandidateRole
                        .NOT_SELECTED;
            }

            /*
             * =================================================
             * 다른 CORE Evidence와 직접 충돌
             * =================================================
             *
             * 종격 우선이라는 이유로
             * 충돌 Evidence를 삭제하거나 무시하지 않는다.
             *
             * FOLLOW_STRUCTURE의 긍정 Evidence가 존재하면
             * 완전히 폐기하지 않고 DEFERRED로 보존한다.
             */
            if (
                    coreConflict
            ) {

                if (
                        hasPositiveSelection
                ) {

                    return UsefulGodCandidateRole
                            .DEFERRED;
                }

                return UsefulGodCandidateRole
                        .NOT_SELECTED;
            }

            /*
             * FOLLOW_STRUCTURE 자체가 조건부.
             */
            if (
                    follow.conditionalCandidate()
            ) {

                return UsefulGodCandidateRole
                        .DEFERRED;
            }

            /*
             * 다른 CORE 방법에서도
             * 조건부 경고가 존재하면
             * 주용신 확정을 보류한다.
             */
            if (
                    coreConditional
                            && hasPositiveSelection
            ) {

                return UsefulGodCandidateRole
                        .DEFERRED;
            }

            /*
             * FOLLOW_STRUCTURE가 CORE PRIMARY이고
             * CORE 직접 충돌/조건부가 없다면
             * 주용신 후보로 인정한다.
             */
            if (
                    convergenceLevel.followPrimary()
            ) {

                return UsefulGodCandidateRole
                        .PRIMARY_USEFUL_GOD;
            }

            /*
             * FOLLOW_STRUCTURE CORE SUPPORT.
             */
            if (
                    convergenceLevel.followSupport()
            ) {

                return UsefulGodCandidateRole
                        .SUPPORTING_USEFUL_GOD;
            }

            return UsefulGodCandidateRole
                    .NOT_SELECTED;
        }

        /*
         * =================================================
         * 일반 구조
         * CORE 직접 충돌
         * =================================================
         *
         * 다른 CORE 방법에서
         * 해당 오행을 직접 반대한다면
         * 주용신으로 확정하지 않는다.
         */
        if (
                coreConflict
        ) {

            if (
                    hasPositiveSelection
            ) {

                return UsefulGodCandidateRole
                        .DEFERRED;
            }

            return UsefulGodCandidateRole
                    .NOT_SELECTED;
        }

        /*
         * =================================================
         * CORE 조건부 경고
         * =================================================
         */
        if (
                coreConditional
                        && hasPositiveSelection
        ) {

            return UsefulGodCandidateRole
                    .DEFERRED;
        }

        /*
         * =================================================
         * ACTIVE 직접 충돌
         * =================================================
         */
        if (
                activeConflict
        ) {

            if (
                    convergenceLevel.multiCorePrimary()
            ) {

                /*
                 * 둘 이상의 CORE 방법이 강하게 수렴했지만
                 * ACTIVE 방법 하나에서 반대 Evidence가 있음.
                 *
                 * 완전 폐기하지 않고 보조로 낮춘다.
                 */
                return UsefulGodCandidateRole
                        .SUPPORTING_USEFUL_GOD;
            }

            if (
                    hasPositiveSelection
            ) {

                return UsefulGodCandidateRole
                        .DEFERRED;
            }

            return UsefulGodCandidateRole
                    .NOT_SELECTED;
        }

        /*
         * =================================================
         * 오행 과다
         * =================================================
         *
         * 종격이 아닌 일반 구조에서는
         * 현재 이미 지나치게 강한 오행을
         * 주용신으로 추가 강화하는 것을 제한한다.
         */
        if (
                presenceLevel.saturated()
        ) {

            if (
                    convergenceLevel.multiCorePrimary()
            ) {

                return UsefulGodCandidateRole
                        .SUPPORTING_USEFUL_GOD;
            }

            if (
                    hasPositiveSelection
            ) {

                return UsefulGodCandidateRole
                        .DEFERRED;
            }
        }

        /*
         * =================================================
         * 복수 CORE PRIMARY
         * =================================================
         */
        if (
                convergenceLevel
                        == UsefulGodConvergenceLevel
                        .MULTI_CORE_PRIMARY
        ) {

            return UsefulGodCandidateRole
                    .PRIMARY_USEFUL_GOD;
        }

        /*
         * =================================================
         * CORE PRIMARY + 다른 방법 수렴
         * =================================================
         */
        if (
                convergenceLevel
                        == UsefulGodConvergenceLevel
                        .CORE_PRIMARY_WITH_SUPPORT
        ) {

            return UsefulGodCandidateRole
                    .PRIMARY_USEFUL_GOD;
        }

        /*
         * =================================================
         * 단일 CORE PRIMARY
         * =================================================
         *
         * CORE 방법 자체가 이 명식에서
         * 핵심적으로 적용된다는 뜻이므로
         * 다른 충돌이 없다면 주용신 후보를 인정한다.
         */
        if (
                convergenceLevel
                        == UsefulGodConvergenceLevel
                        .SINGLE_CORE_PRIMARY
        ) {

            return UsefulGodCandidateRole
                    .PRIMARY_USEFUL_GOD;
        }

        /*
         * =================================================
         * 복수 방법 보조 수렴
         * =================================================
         */
        if (
                convergenceLevel
                        == UsefulGodConvergenceLevel
                        .MULTI_METHOD_SUPPORT
        ) {

            return UsefulGodCandidateRole
                    .SUPPORTING_USEFUL_GOD;
        }

        /*
         * =================================================
         * 단일 방법 보조
         * =================================================
         */
        if (
                convergenceLevel
                        == UsefulGodConvergenceLevel
                        .SINGLE_METHOD_SUPPORT
        ) {

            return UsefulGodCandidateRole
                    .SUPPORTING_USEFUL_GOD;
        }

        /*
         * =================================================
         * 조건부 Evidence만 존재
         * =================================================
         */
        if (
                convergenceLevel.conditionalOnly()
        ) {

            return UsefulGodCandidateRole
                    .DEFERRED;
        }

        return UsefulGodCandidateRole
                .NOT_SELECTED;
    }

    public List<UsefulGodIntegrationReasonType> reasonsFor(

            UsefulGodConvergenceLevel convergenceLevel,

            List<UsefulGodMethodContribution> contributions,

            ClimateElementPresenceLevel presenceLevel,

            boolean followStructureMode,

            UsefulGodCandidateRole finalRole

    ) {

        Objects.requireNonNull(
                convergenceLevel,
                "용신법 수렴 단계가 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 세력 수준이 필요합니다."
        );

        Objects.requireNonNull(
                finalRole,
                "최종 용신 후보 역할이 필요합니다."
        );

        validateContributions(
                contributions
        );

        List<UsefulGodIntegrationReasonType> reasons =
                new ArrayList<>();

        int corePrimaryCount =
                (int) contributions
                        .stream()
                        .filter(
                                UsefulGodMethodContribution::corePrimary
                        )
                        .count();

        int selectedCount =
                (int) contributions
                        .stream()
                        .filter(
                                UsefulGodMethodContribution::selected
                        )
                        .count();

        boolean coreConflict =
                contributions
                        .stream()
                        .anyMatch(
                                UsefulGodMethodContribution::coreConflict
                        );

        boolean activeConflict =
                contributions
                        .stream()
                        .anyMatch(
                                UsefulGodMethodContribution::activeConflict
                        );

        boolean coreConditional =
                contributions
                        .stream()
                        .anyMatch(
                                UsefulGodMethodContribution
                                        ::coreConditionalWarning
                        );

        boolean conditionalPresent =
                contributions
                        .stream()
                        .anyMatch(
                                UsefulGodMethodContribution
                                        ::conditionalCandidate
                        );

        if (
                corePrimaryCount > 0
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .CORE_METHOD_PRIMARY_PRESENT
            );
        }

        if (
                corePrimaryCount >= 2
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .MULTIPLE_CORE_METHODS_CONVERGE
            );
        }

        if (
                convergenceLevel
                        == UsefulGodConvergenceLevel
                        .CORE_PRIMARY_WITH_SUPPORT
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .CORE_PRIMARY_WITH_ADDITIONAL_SUPPORT
            );
        }

        if (
                selectedCount >= 2
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .MULTIPLE_METHODS_CONVERGE
            );

        } else if (
                selectedCount == 1
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .SINGLE_METHOD_SUPPORT
            );
        }

        if (
                followStructureMode
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .FOLLOW_STRUCTURE_MODE
            );

            UsefulGodMethodContribution follow =
                    contributionOf(
                            contributions,
                            UsefulGodMethodType.FOLLOW_STRUCTURE
                    );

            if (
                    follow.primary()
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .FOLLOW_CORE_PRIMARY
                );
            }

            if (
                    follow.secondary()
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .FOLLOW_CORE_SUPPORT
                );
            }

            if (
                    follow.followFlowBreaking()
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .FOLLOW_FLOW_BREAKING_ELEMENT
                );
            }
        }

        if (
                coreConflict
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .CORE_METHOD_DIRECT_CONFLICT
            );
        }

        if (
                activeConflict
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .ACTIVE_METHOD_DIRECT_CONFLICT
            );
        }

        if (
                coreConditional
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .CORE_METHOD_CONDITIONAL_WARNING
            );
        }

        if (
                conditionalPresent
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .CONDITIONAL_EVIDENCE_PRESENT
            );
        }

        if (
                presenceLevel.saturated()
                        && !followStructureMode
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .ELEMENT_SATURATION_CAUTION
            );
        }

        /*
         * =================================================
         * 최종 역할 이유
         * =================================================
         */
        if (
                finalRole.primaryUsefulGod()
        ) {

            if (
                    followStructureMode
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .PRIMARY_BY_FOLLOW_STRUCTURE
                );

            } else if (
                    convergenceLevel.multiCorePrimary()
                            || convergenceLevel
                            == UsefulGodConvergenceLevel
                            .CORE_PRIMARY_WITH_SUPPORT
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .PRIMARY_BY_CORE_CONVERGENCE
                );

            } else {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .PRIMARY_BY_CORE_METHOD
                );
            }
        }

        if (
                finalRole.supportingUsefulGod()
        ) {

            if (
                    convergenceLevel.multiMethod()
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .SUPPORTING_BY_METHOD_CONVERGENCE
                );

            } else {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .SUPPORTING_BY_SINGLE_METHOD
                );
            }

            if (
                    activeConflict
                            || presenceLevel.saturated()
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .SUPPORTING_AFTER_CAUTION
                );
            }
        }

        if (
                finalRole.deferred()
        ) {

            if (
                    coreConflict
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .DEFERRED_BY_CORE_CONFLICT
                );

            } else if (
                    activeConflict
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .DEFERRED_BY_ACTIVE_CONFLICT
                );

            } else if (
                    coreConditional
                            || conditionalPresent
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .DEFERRED_BY_CONDITIONAL_EVIDENCE
                );

            } else if (
                    presenceLevel.saturated()
            ) {

                reasons.add(
                        UsefulGodIntegrationReasonType
                                .DEFERRED_BY_SATURATION
                );
            }
        }

        if (
                finalRole.notSelected()
                        && selectedCount == 0
        ) {

            reasons.add(
                    UsefulGodIntegrationReasonType
                            .NO_ACTIVE_USEFUL_GOD_EVIDENCE
            );
        }

        return List.copyOf(
                reasons
        );
    }

    private UsefulGodMethodContribution contributionOf(

            List<UsefulGodMethodContribution> contributions,

            UsefulGodMethodType methodType

    ) {

        return contributions
                .stream()
                .filter(
                        contribution ->
                                contribution.methodType()
                                        == methodType
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "용신법 Contribution을 찾을 수 없습니다: "
                                                + methodType
                                )
                );
    }

    private void validateContributions(
            List<UsefulGodMethodContribution> contributions
    ) {

        Objects.requireNonNull(
                contributions,
                "용신법 Contribution 목록이 필요합니다."
        );

        if (
                contributions.size()
                        != UsefulGodMethodType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "6개 용신법 Contribution이 모두 필요합니다."
            );
        }
    }
}