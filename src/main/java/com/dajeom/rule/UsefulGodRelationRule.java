package com.dajeom.rule;

import com.dajeom.model.UsefulGodCandidate;
import com.dajeom.model.UsefulGodPrimaryRelationEvidence;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodPrimaryRelationType;
import com.dajeom.type.UsefulGodRelationReasonType;
import com.dajeom.type.UsefulGodRelationRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-9.
 *
 * 주용신을 기준으로
 * 희신·기신을 판정한다.
 *
 * =================================================
 *
 * 절대 사용하지 않는 단순 규칙
 *
 * 1.
 * 용신을 생함
 * -> 무조건 희신
 *
 * 2.
 * 오행이 많음
 * -> 기신
 *
 * 3.
 * 7-8 NOT_SELECTED
 * -> 기신
 *
 * =================================================
 *
 * 희신은:
 *
 * - 주용신 생조
 * - 주용신 보호
 * - 7-8 보조 용신 기능
 *
 * 등을 함께 본다.
 *
 * =================================================
 *
 * 기신은:
 *
 * - 주용신 직접 극
 * - 실제 종격 흐름 파괴
 * - CORE 용신법 직접 충돌
 *
 * 같은 강한 부정 근거가 있어야 한다.
 *
 * =================================================
 *
 * 설기·극제 부담은
 * 바로 기신으로 만들지 않고
 * 우선 CONDITIONAL로 처리한다.
 */
@Component
public class UsefulGodRelationRule {

    /**
     * 하나의 후보 오행과
     * 하나의 주용신 사이의 모든 관계를 구한다.
     */
    public UsefulGodPrimaryRelationEvidence analyzePrimaryRelation(

            FiveElement candidateElement,

            FiveElement primaryElement

    ) {

        Objects.requireNonNull(
                candidateElement,
                "평가 대상 오행이 필요합니다."
        );

        Objects.requireNonNull(
                primaryElement,
                "주용신 오행이 필요합니다."
        );

        List<UsefulGodPrimaryRelationType> relations =
                new ArrayList<>();

        /*
         * 같은 오행.
         */
        if (
                candidateElement == primaryElement
        ) {

            relations.add(
                    UsefulGodPrimaryRelationType
                            .SAME_AS_PRIMARY
            );

            return new UsefulGodPrimaryRelationEvidence(
                    candidateElement,
                    primaryElement,
                    relations
            );
        }

        /*
         * 후보 -> 주용신 生
         */
        if (
                produces(
                        candidateElement,
                        primaryElement
                )
        ) {

            relations.add(
                    UsefulGodPrimaryRelationType
                            .GENERATES_PRIMARY
            );
        }

        /*
         * 후보 -> 주용신 剋
         */
        if (
                controls(
                        candidateElement,
                        primaryElement
                )
        ) {

            relations.add(
                    UsefulGodPrimaryRelationType
                            .CONTROLS_PRIMARY
            );
        }

        /*
         * 주용신 -> 후보 生
         *
         * 주용신 설기.
         */
        if (
                produces(
                        primaryElement,
                        candidateElement
                )
        ) {

            relations.add(
                    UsefulGodPrimaryRelationType
                            .DRAINS_PRIMARY
            );
        }

        /*
         * 주용신 -> 후보 剋
         *
         * 주용신이 제어력을 사용해야 함.
         */
        if (
                controls(
                        primaryElement,
                        candidateElement
                )
        ) {

            relations.add(
                    UsefulGodPrimaryRelationType
                            .CONTROLLED_BY_PRIMARY
            );
        }

        /*
         * 후보가
         * 주용신을 극하는 오행을 제어하는가.
         *
         * 이건 직접 생조는 아니지만
         * 주용신 보호 Evidence다.
         */
        FiveElement primaryThreat =
                controllerOf(
                        primaryElement
                );

        if (
                controls(
                        candidateElement,
                        primaryThreat
                )
        ) {

            relations.add(
                    UsefulGodPrimaryRelationType
                            .CONTROLS_PRIMARY_THREAT
            );
        }

        if (
                relations.isEmpty()
        ) {

            throw new IllegalStateException(
                    "오행 관계를 결정할 수 없습니다: "
                            + candidateElement
                            + " / "
                            + primaryElement
            );
        }

        return new UsefulGodPrimaryRelationEvidence(
                candidateElement,
                primaryElement,
                relations
        );
    }

    public UsefulGodRelationRole determineRole(

            UsefulGodCandidate sourceCandidate,

            List<UsefulGodPrimaryRelationEvidence> primaryRelations,

            boolean primaryAnchorPresent

    ) {

        Objects.requireNonNull(
                sourceCandidate,
                "7-8 용신 후보가 필요합니다."
        );

        Objects.requireNonNull(
                primaryRelations,
                "주용신 관계 Evidence가 필요합니다."
        );

        /*
         * =================================================
         * 주용신 자체
         * =================================================
         */
        if (
                sourceCandidate.primaryUsefulGod()
        ) {

            return UsefulGodRelationRole
                    .USEFUL_GOD;
        }

        /*
         * =================================================
         * 주용신이 아직 없음
         * =================================================
         *
         * 기준축 없이 희신·기신을 확정하지 않는다.
         */
        if (
                !primaryAnchorPresent
        ) {

            if (
                    sourceCandidate.supportingUsefulGod()
                            || sourceCandidate.deferred()
            ) {

                return UsefulGodRelationRole
                        .CONDITIONAL;
            }

            return UsefulGodRelationRole
                    .NEUTRAL;
        }

        boolean supportsPrimary =
                primaryRelations
                        .stream()
                        .anyMatch(
                                UsefulGodPrimaryRelationEvidence
                                        ::supportive
                        );

        boolean harmsPrimary =
                primaryRelations
                        .stream()
                        .anyMatch(
                                UsefulGodPrimaryRelationEvidence
                                        ::harmful
                        );

        boolean burdensPrimary =
                primaryRelations
                        .stream()
                        .anyMatch(
                                UsefulGodPrimaryRelationEvidence
                                        ::burdening
                        );

        /*
         * =================================================
         * 실제 종격 흐름 파괴
         * =================================================
         *
         * 가장 강한 기신 Evidence 중 하나.
         */
        if (
                sourceCandidate.followFlowBreaking()
        ) {

            return UsefulGodRelationRole
                    .UNFAVORABLE_GOD;
        }

        /*
         * =================================================
         * 주용신 직접 극
         * =================================================
         */
        if (
                harmsPrimary
        ) {

            /*
             * 복수 주용신 중 하나를 돕고
             * 다른 하나를 해치는 경우.
             *
             * 바로 기신으로 단정하지 않는다.
             */
            if (
                    supportsPrimary
                            || sourceCandidate
                            .supportingUsefulGod()
            ) {

                return UsefulGodRelationRole
                        .CONDITIONAL;
            }

            return UsefulGodRelationRole
                    .UNFAVORABLE_GOD;
        }

        /*
         * =================================================
         * CORE 방법 직접 충돌
         * =================================================
         */
        if (
                sourceCandidate.hasCoreConflict()
        ) {

            if (
                    supportsPrimary
                            || sourceCandidate
                            .supportingUsefulGod()
            ) {

                return UsefulGodRelationRole
                        .CONDITIONAL;
            }

            return UsefulGodRelationRole
                    .UNFAVORABLE_GOD;
        }

        /*
         * =================================================
         * 7-8 자체가 DEFERRED
         * =================================================
         */
        if (
                sourceCandidate.deferred()
        ) {

            return UsefulGodRelationRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 긍정 + 부담 혼재
         * =================================================
         */
        if (
                supportsPrimary
                        && burdensPrimary
        ) {

            return UsefulGodRelationRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 현재 오행 과다
         * =================================================
         *
         * 주용신을 돕는 기능은 인정하지만
         * 이미 해당 오행이 너무 강하면
         * 희신으로 단순 확정하지 않는다.
         */
        if (
                sourceCandidate.saturated()
                        && (
                        supportsPrimary
                                || sourceCandidate
                                .supportingUsefulGod()
                )
        ) {

            return UsefulGodRelationRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 주용신 지원 관계
         * =================================================
         */
        if (
                supportsPrimary
        ) {

            return UsefulGodRelationRole
                    .FAVORABLE_GOD;
        }

        /*
         * =================================================
         * 7-8 보조 용신
         * =================================================
         *
         * 직접 주용신 생조가 아니더라도
         * 억부·조후·격국·병약·통관 등에서
         * 이미 복수 검증을 통과한 보조 역할이라면
         * 희신 후보로 인정한다.
         */
        if (
                sourceCandidate.supportingUsefulGod()
        ) {

            if (
                    sourceCandidate.hasActiveConflict()
            ) {

                return UsefulGodRelationRole
                        .CONDITIONAL;
            }

            return UsefulGodRelationRole
                    .FAVORABLE_GOD;
        }

        /*
         * =================================================
         * ACTIVE 용신법 충돌
         * =================================================
         */
        if (
                sourceCandidate.hasActiveConflict()
        ) {

            return UsefulGodRelationRole
                    .CONDITIONAL;
        }

        /*
         * =================================================
         * 주용신 설기/극제 부담만 존재
         * =================================================
         *
         * 이 관계만으로 기신이라고 확정하지 않는다.
         */
        if (
                burdensPrimary
        ) {

            return UsefulGodRelationRole
                    .CONDITIONAL;
        }

        return UsefulGodRelationRole
                .NEUTRAL;
    }

    public List<UsefulGodRelationReasonType> reasonsFor(

            UsefulGodCandidate sourceCandidate,

            List<UsefulGodPrimaryRelationEvidence> primaryRelations,

            boolean primaryAnchorPresent,

            UsefulGodRelationRole finalRole

    ) {

        Objects.requireNonNull(
                sourceCandidate,
                "7-8 용신 후보가 필요합니다."
        );

        Objects.requireNonNull(
                primaryRelations,
                "주용신 관계 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                finalRole,
                "7-9 최종 관계 역할이 필요합니다."
        );

        List<UsefulGodRelationReasonType> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 7-8 원본 역할
         * =================================================
         */
        if (
                sourceCandidate.primaryUsefulGod()
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .SOURCE_PRIMARY_USEFUL_GOD
            );

        } else if (
                sourceCandidate.supportingUsefulGod()
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .SOURCE_SUPPORTING_USEFUL_GOD
            );

        } else if (
                sourceCandidate.deferred()
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .SOURCE_DEFERRED
            );

        } else {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .SOURCE_NOT_SELECTED
            );
        }

        /*
         * =================================================
         * 주용신 기준축 없음
         * =================================================
         */
        if (
                !primaryAnchorPresent
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .NO_PRIMARY_USEFUL_GOD_ANCHOR
            );

            if (
                    finalRole.conditional()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .CONDITIONAL_WITHOUT_PRIMARY_ANCHOR
                );
            }

            if (
                    finalRole.neutral()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .NEUTRAL_WITHOUT_DECISIVE_RELATION
                );
            }

            return List.copyOf(
                    reasons
            );
        }

        /*
         * =================================================
         * 주용신 관계
         * =================================================
         */
        for (
                UsefulGodPrimaryRelationEvidence relation
                : primaryRelations
        ) {

            if (
                    relation.sameAsPrimary()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .SAME_AS_PRIMARY_USEFUL_GOD
                );
            }

            if (
                    relation.generatesPrimary()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .GENERATES_PRIMARY_USEFUL_GOD
                );
            }

            if (
                    relation.controlsPrimaryThreat()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .CONTROLS_PRIMARY_THREAT
                );
            }

            if (
                    relation.controlsPrimary()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .CONTROLS_PRIMARY_USEFUL_GOD
                );

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .HARMS_PRIMARY_GOD
                );
            }

            if (
                    relation.drainsPrimary()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .DRAINS_PRIMARY_USEFUL_GOD
                );
            }

            if (
                    relation.controlledByPrimary()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .PRIMARY_CONTROLS_THIS_ELEMENT
                );
            }
        }

        int supportCount =
                (int) primaryRelations
                        .stream()
                        .filter(
                                UsefulGodPrimaryRelationEvidence
                                        ::supportive
                        )
                        .count();

        boolean supportsPrimary =
                supportCount > 0;

        boolean harmsPrimary =
                primaryRelations
                        .stream()
                        .anyMatch(
                                UsefulGodPrimaryRelationEvidence
                                        ::harmful
                        );

        boolean burdensPrimary =
                primaryRelations
                        .stream()
                        .anyMatch(
                                UsefulGodPrimaryRelationEvidence
                                        ::burdening
                        );

        if (
                supportCount >= 2
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .SUPPORTS_MULTIPLE_PRIMARY_GODS
            );
        }

        if (
                supportsPrimary
                        && (
                        harmsPrimary
                                || burdensPrimary
                )
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .MIXED_PRIMARY_RELATIONS
            );
        }

        /*
         * =================================================
         * 종격
         * =================================================
         */
        if (
                sourceCandidate.followStructureMode()
        ) {

            if (
                    sourceCandidate.followFlowBreaking()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .FOLLOW_FLOW_BREAKING
                );

            } else {

                sourceCandidate
                        .contributionOf(
                                com.dajeom.type.UsefulGodMethodType
                                        .FOLLOW_STRUCTURE
                        )
                        .filter(
                                contribution ->
                                        contribution.selected()
                        )
                        .ifPresent(
                                contribution ->
                                        addReason(
                                                reasons,
                                                UsefulGodRelationReasonType
                                                        .FOLLOW_STRUCTURE_ALIGNMENT
                                        )
                        );
            }
        }

        /*
         * =================================================
         * 7-8 충돌
         * =================================================
         */
        if (
                sourceCandidate.hasCoreConflict()
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .CORE_METHOD_CONFLICT
            );
        }

        if (
                sourceCandidate.hasActiveConflict()
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .ACTIVE_METHOD_CONFLICT
            );
        }

        if (
                sourceCandidate.saturated()
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .ELEMENT_SATURATION_CAUTION
            );
        }

        /*
         * =================================================
         * 최종 희신
         * =================================================
         */
        if (
                finalRole.favorableGod()
        ) {

            boolean directGeneration =
                    primaryRelations
                            .stream()
                            .anyMatch(
                                    UsefulGodPrimaryRelationEvidence
                                            ::generatesPrimary
                            );

            boolean protection =
                    primaryRelations
                            .stream()
                            .anyMatch(
                                    UsefulGodPrimaryRelationEvidence
                                            ::controlsPrimaryThreat
                            );

            if (
                    directGeneration
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .FAVORABLE_BY_PRIMARY_GENERATION
                );
            }

            if (
                    protection
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .FAVORABLE_BY_PRIMARY_PROTECTION
                );
            }

            if (
                    sourceCandidate.supportingUsefulGod()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .FAVORABLE_BY_EXISTING_SUPPORT_ROLE
                );
            }
        }

        /*
         * =================================================
         * 최종 기신
         * =================================================
         */
        if (
                finalRole.unfavorableGod()
        ) {

            if (
                    sourceCandidate.followFlowBreaking()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .UNFAVORABLE_BY_FOLLOW_BREAK
                );

            } else if (
                    harmsPrimary
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .UNFAVORABLE_BY_PRIMARY_DAMAGE
                );

            } else if (
                    sourceCandidate.hasCoreConflict()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .UNFAVORABLE_BY_CORE_CONFLICT
                );
            }
        }

        /*
         * =================================================
         * 최종 조건부
         * =================================================
         */
        if (
                finalRole.conditional()
        ) {

            if (
                    supportsPrimary
                            && (
                            harmsPrimary
                                    || burdensPrimary
                    )
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .CONDITIONAL_BY_MIXED_RELATION
                );
            }

            if (
                    burdensPrimary
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .CONDITIONAL_BY_PRIMARY_BURDEN
                );
            }

            if (
                    sourceCandidate.deferred()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .CONDITIONAL_BY_SOURCE_DEFERRED
                );
            }

            if (
                    sourceCandidate.saturated()
            ) {

                addReason(
                        reasons,
                        UsefulGodRelationReasonType
                                .CONDITIONAL_BY_SATURATION
                );
            }
        }

        if (
                finalRole.neutral()
        ) {

            addReason(
                    reasons,
                    UsefulGodRelationReasonType
                            .NEUTRAL_WITHOUT_DECISIVE_RELATION
            );
        }

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * 五行 생 관계
     * =================================================
     */
    public FiveElement producedElementOf(
            FiveElement source
    ) {

        Objects.requireNonNull(
                source,
                "기준 오행이 필요합니다."
        );

        return switch (
                source
                ) {

            case WOOD ->
                    FiveElement.FIRE;

            case FIRE ->
                    FiveElement.EARTH;

            case EARTH ->
                    FiveElement.METAL;

            case METAL ->
                    FiveElement.WATER;

            case WATER ->
                    FiveElement.WOOD;
        };
    }

    /*
     * =================================================
     * 해당 오행을 극하는 오행
     * =================================================
     */
    public FiveElement controllerOf(
            FiveElement target
    ) {

        Objects.requireNonNull(
                target,
                "대상 오행이 필요합니다."
        );

        return switch (
                target
                ) {

            case WOOD ->
                    FiveElement.METAL;

            case FIRE ->
                    FiveElement.WATER;

            case EARTH ->
                    FiveElement.WOOD;

            case METAL ->
                    FiveElement.FIRE;

            case WATER ->
                    FiveElement.EARTH;
        };
    }

    private boolean produces(

            FiveElement source,

            FiveElement target

    ) {

        return producedElementOf(
                source
        ) == target;
    }

    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return controllerOf(
                target
        ) == source;
    }

    private void addReason(

            List<UsefulGodRelationReasonType> reasons,

            UsefulGodRelationReasonType reason

    ) {

        if (
                !reasons.contains(
                        reason
                )
        ) {

            reasons.add(
                    reason
            );
        }
    }
}