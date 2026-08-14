package com.dajeom.model;

import com.dajeom.type.SpecialStructureType;
import com.dajeom.type.StructureCandidateKind;
import com.dajeom.type.StructureCandidateRole;
import com.dajeom.type.StructureSelectionReasonType;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-9.
 *
 * 일반격과 특수격을 하나의
 * 후보 형태로 통합한 결과.
 *
 * STANDARD 후보는:
 *
 * standardTenGod
 * standardEvidence
 *
 * 를 사용한다.
 *
 * SPECIAL 후보는:
 *
 * specialType
 * specialEvidence
 *
 * 를 사용한다.
 *
 * 두 종류를 동시에 가질 수 없다.
 */
public record StructureSelectionCandidate(

        StructureCandidateKind kind,

        StructureCandidateRole role,

        StructureTenGodType standardTenGod,

        SpecialStructureType specialType,

        StructurePurityEvidence standardEvidence,

        SpecialStructureEvidence specialEvidence,

        List<StructureSelectionReasonType> reasons

) {

    public StructureSelectionCandidate {

        Objects.requireNonNull(
                kind,
                "격 후보 종류가 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "격 후보 역할이 필요합니다."
        );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                reasons
                        .stream()
                        .distinct()
                        .count()
                        != reasons.size()
        ) {

            throw new IllegalArgumentException(
                    "격 후보 선택 근거가 중복되었습니다."
            );
        }

        if (
                kind.standard()
        ) {

            Objects.requireNonNull(
                    standardTenGod,
                    "일반격 후보에는 십성이 필요합니다."
            );

            Objects.requireNonNull(
                    standardEvidence,
                    "일반격 후보에는 순도 Evidence가 필요합니다."
            );

            if (
                    specialType != null
                            || specialEvidence != null
            ) {

                throw new IllegalArgumentException(
                        "일반격 후보에 특수격 정보를 함께 넣을 수 없습니다."
                );
            }

            if (
                    standardEvidence.tenGod()
                            != standardTenGod
            ) {

                throw new IllegalArgumentException(
                        "일반격 십성과 Evidence가 일치하지 않습니다."
                );
            }

        } else {

            Objects.requireNonNull(
                    specialType,
                    "특수격 후보에는 특수격 타입이 필요합니다."
            );

            Objects.requireNonNull(
                    specialEvidence,
                    "특수격 후보에는 특수격 Evidence가 필요합니다."
            );

            if (
                    standardTenGod != null
                            || standardEvidence != null
            ) {

                throw new IllegalArgumentException(
                        "특수격 후보에 일반격 정보를 함께 넣을 수 없습니다."
                );
            }

            if (
                    specialEvidence.type()
                            != specialType
            ) {

                throw new IllegalArgumentException(
                        "특수격 타입과 Evidence가 일치하지 않습니다."
                );
            }
        }
    }

    public boolean standard() {

        return kind.standard();
    }

    public boolean special() {

        return kind.special();
    }

    public boolean primary() {

        return role.primary();
    }

    public boolean secondary() {

        return role.secondary();
    }

    public boolean deferred() {

        return role.deferred();
    }

    public boolean rejected() {

        return role.rejected();
    }

    public boolean selected() {

        return role.selected();
    }

    /**
     * 사용자/AI Grounding에서 사용할 수 있는
     * 기본 격 후보명.
     *
     * 최종 5-10에서 다시 확정한다.
     */
    public String candidateNameKorean() {

        if (
                standard()
        ) {

            return standardTenGod
                    .koreanName()
                    + "격";
        }

        return specialType
                .koreanName();
    }

    public boolean mainQiStandardCandidate() {

        return standard()
                && standardEvidence.mainQi();
    }

    public boolean exactMonthSpecialCandidate() {

        return special()
                && specialEvidence
                .exactMonthStructureCandidate();
    }

    public boolean followStructureCandidate() {

        return special()
                && specialType
                .followStructure();
    }
}