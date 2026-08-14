package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodRelationReasonType;
import com.dajeom.type.UsefulGodRelationRole;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-9.
 *
 * 하나의 五行에 대한:
 *
 * 용신 / 희신 / 기신 / 조건부 / 중립
 *
 * 판정 결과.
 *
 * =================================================
 *
 * sourceCandidate
 *
 * 7-8 UsefulGodCandidate.
 *
 * =================================================
 *
 * primaryRelations
 *
 * 현재 존재하는 모든 주용신과
 * 해당 오행의 관계를 보존한다.
 *
 * 주용신이 복수라면 관계 Evidence도 복수다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * - 오행 과다 = 기신 아님
 * - 용신을 생함 = 무조건 희신 아님
 * - 7-8 NOT_SELECTED = 기신 아님
 * - 종격 흐름 파괴 = 강한 기신 근거
 */
public record UsefulGodRelationCandidate(

        FiveElement element,

        UsefulGodRelationRole role,

        UsefulGodCandidate sourceCandidate,

        List<UsefulGodPrimaryRelationEvidence> primaryRelations,

        List<UsefulGodRelationReasonType> reasons

) {

    public UsefulGodRelationCandidate {

        Objects.requireNonNull(
                element,
                "희신·기신 판정 오행이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "희신·기신 판정 역할이 필요합니다."
        );

        Objects.requireNonNull(
                sourceCandidate,
                "7-8 용신 통합 후보가 필요합니다."
        );

        /*
         * =================================================
         * Compact constructor 입력 정규화
         * =================================================
         *
         * record compact constructor body가 끝나기 전에는
         * this.primaryRelations / this.reasons가
         * 아직 자동 대입되지 않았다.
         *
         * 따라서 constructor 내부 검증에서는
         * 정규화된 parameter 변수만 사용한다.
         */

        primaryRelations =
                primaryRelations == null
                        ? List.of()
                        : List.copyOf(
                        primaryRelations
                );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                sourceCandidate.element()
                        != element
        ) {

            throw new IllegalArgumentException(
                    "7-8 후보와 7-9 후보의 오행이 일치하지 않습니다."
            );
        }

        EnumSet<FiveElement> primaryElements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                UsefulGodPrimaryRelationEvidence relation
                : primaryRelations
        ) {

            Objects.requireNonNull(
                    relation,
                    "주용신 관계 Evidence에는 null을 넣을 수 없습니다."
            );

            if (
                    relation.candidateElement()
                            != element
            ) {

                throw new IllegalArgumentException(
                        "주용신 관계 Evidence의 평가 대상 오행이 일치하지 않습니다."
                );
            }

            if (
                    !primaryElements.add(
                            relation.primaryElement()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 주용신에 대한 관계 Evidence가 중복되었습니다: "
                                + relation.primaryElement()
                );
            }
        }

        if (
                reasons
                        .stream()
                        .distinct()
                        .count()
                        != reasons.size()
        ) {

            throw new IllegalArgumentException(
                    "희신·기신 판정 근거가 중복되었습니다."
            );
        }

        if (
                sourceCandidate.primaryUsefulGod()
                        && !role.usefulGod()
        ) {

            throw new IllegalArgumentException(
                    "7-8 주용신 후보는 7-9에서 USEFUL_GOD로 유지되어야 합니다."
            );
        }

        if (
                role.usefulGod()
                        && !sourceCandidate.primaryUsefulGod()
        ) {

            throw new IllegalArgumentException(
                    "7-8 주용신이 아닌 오행을 USEFUL_GOD로 지정할 수 없습니다."
            );
        }

        /*
         * =================================================
         * constructor-local 관계 판정
         * =================================================
         *
         * harmsAnyPrimary()
         * supportsAnyPrimary()
         *
         * 를 호출하면 해당 메서드가
         * this.primaryRelations를 읽는다.
         *
         * compact constructor에서는 아직 field 대입 전이므로
         * 여기서는 parameter primaryRelations로 직접 계산한다.
         */

        boolean harmsAnyPrimary =
                primaryRelations
                        .stream()
                        .anyMatch(
                                UsefulGodPrimaryRelationEvidence
                                        ::harmful
                        );

        boolean supportsAnyPrimary =
                primaryRelations
                        .stream()
                        .anyMatch(
                                UsefulGodPrimaryRelationEvidence
                                        ::supportive
                        );

        if (
                role.favorableGod()
                        && harmsAnyPrimary
        ) {

            throw new IllegalArgumentException(
                    "주용신 직접 손상 관계가 있는 오행을 희신으로 확정할 수 없습니다."
            );
        }

        if (
                role.unfavorableGod()
                        && supportsAnyPrimary
                        && !sourceCandidate.followFlowBreaking()
        ) {

            throw new IllegalArgumentException(
                    "주용신 지원 관계가 있는 오행은 추가 충돌 검토 없이 기신으로 단정할 수 없습니다."
            );
        }
    }

    public boolean usefulGod() {

        return role.usefulGod();
    }

    public boolean favorableGod() {

        return role.favorableGod();
    }

    public boolean unfavorableGod() {

        return role.unfavorableGod();
    }

    public boolean conditional() {

        return role.conditional();
    }

    public boolean neutral() {

        return role.neutral();
    }

    public boolean positive() {

        return role.positive();
    }

    public boolean supportsAnyPrimary() {

        return primaryRelations
                .stream()
                .anyMatch(
                        UsefulGodPrimaryRelationEvidence
                                ::supportive
                );
    }

    public boolean harmsAnyPrimary() {

        return primaryRelations
                .stream()
                .anyMatch(
                        UsefulGodPrimaryRelationEvidence
                                ::harmful
                );
    }

    public boolean burdensAnyPrimary() {

        return primaryRelations
                .stream()
                .anyMatch(
                        UsefulGodPrimaryRelationEvidence
                                ::burdening
                );
    }

    public boolean mixedPrimaryRelations() {

        boolean positive =
                supportsAnyPrimary();

        boolean negative =
                harmsAnyPrimary()
                        || burdensAnyPrimary();

        return positive
                && negative;
    }

    public int supportedPrimaryCount() {

        return (int) primaryRelations
                .stream()
                .filter(
                        UsefulGodPrimaryRelationEvidence
                                ::supportive
                )
                .count();
    }

    public int harmedPrimaryCount() {

        return (int) primaryRelations
                .stream()
                .filter(
                        UsefulGodPrimaryRelationEvidence
                                ::harmful
                )
                .count();
    }

    public int burdenedPrimaryCount() {

        return (int) primaryRelations
                .stream()
                .filter(
                        UsefulGodPrimaryRelationEvidence
                                ::burdening
                )
                .count();
    }

    public boolean supportsMultiplePrimaryGods() {

        return supportedPrimaryCount() >= 2;
    }

    public boolean hasPrimaryAnchor() {

        return !primaryRelations.isEmpty();
    }

    public boolean sourceSupportingUsefulGod() {

        return sourceCandidate
                .supportingUsefulGod();
    }

    public boolean sourceDeferred() {

        return sourceCandidate
                .deferred();
    }

    public boolean sourceNotSelected() {

        return sourceCandidate
                .notSelected();
    }

    public boolean followFlowBreaking() {

        return sourceCandidate
                .followFlowBreaking();
    }

    public boolean coreConflict() {

        return sourceCandidate
                .hasCoreConflict();
    }

    public boolean activeConflict() {

        return sourceCandidate
                .hasActiveConflict();
    }

    public boolean saturated() {

        return sourceCandidate
                .saturated();
    }

    public int existingShareBasisPoints() {

        return sourceCandidate
                .existingShareBasisPoints();
    }

    public double existingSharePercent() {

        return existingShareBasisPoints()
                / 100.0;
    }

    public boolean hasReason(
            UsefulGodRelationReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }
}