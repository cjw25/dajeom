package com.dajeom.model;

import com.dajeom.type.StructureTenGodType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Personality 사용자 Meaning에서
 * 우선 참조할 정확한 10십성 Selection.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 이 객체는 새로운 성격 의미를 만들지 않는다.
 *
 * 기존:
 *
 * - 5차 주격 중심성
 * - 10-1 십성 PowerLevel
 *
 * 만 사용하여
 * 어떤 정확한 십성을
 * 먼저 설명할지 분리한다.
 *
 * =================================================
 *
 * primaryProfiles
 *
 * 사용자 설명에서
 * 중심으로 볼 정확한 십성.
 *
 * =================================================
 *
 * supportingProfiles
 *
 * 존재와 작동이 의미 있지만
 * 중심까지는 아닌 정확한 십성.
 *
 * =================================================
 *
 * referenceProfiles
 *
 * 존재는 하지만
 * 사용자 성격을 강하게 단정할 수준은 아닌 십성.
 *
 * =================================================
 *
 * 이 목록에 함께 들어 있다는 이유만으로
 * 십성끼리 새로운 관계 Meaning을 만들지 않는다.
 */
public record NatalPersonalityExactTenGodSelection(

        NatalExactTenGodAnalysis exactTenGodAnalysis,

        List<NatalExactTenGodProfile> primaryProfiles,

        List<NatalExactTenGodProfile> supportingProfiles,

        List<NatalExactTenGodProfile> referenceProfiles,

        boolean completeChart

) {

    public NatalPersonalityExactTenGodSelection {

        Objects.requireNonNull(
                exactTenGodAnalysis,
                "Exact TenGod Selection에는 NatalExactTenGodAnalysis가 필요합니다."
        );

        primaryProfiles =
                primaryProfiles == null
                        ? List.of()
                        : List.copyOf(
                        primaryProfiles
                );

        supportingProfiles =
                supportingProfiles == null
                        ? List.of()
                        : List.copyOf(
                        supportingProfiles
                );

        referenceProfiles =
                referenceProfiles == null
                        ? List.of()
                        : List.copyOf(
                        referenceProfiles
                );

        if (
                exactTenGodAnalysis
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Exact TenGod Analysis와 Selection의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        Set<StructureTenGodType> seen =
                EnumSet.noneOf(
                        StructureTenGodType.class
                );

        validateGroup(
                exactTenGodAnalysis,
                primaryProfiles,
                seen,
                "PRIMARY"
        );

        validateGroup(
                exactTenGodAnalysis,
                supportingProfiles,
                seen,
                "SUPPORTING"
        );

        validateGroup(
                exactTenGodAnalysis,
                referenceProfiles,
                seen,
                "REFERENCE"
        );
    }

    /**
     * 해당 정확한 십성이
     * 사용자 설명 Selection에 포함되었는지.
     */
    public boolean selected(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return false;
        }

        return primaryProfiles
                .stream()
                .anyMatch(
                        profile ->
                                profile.tenGodType()
                                        == tenGodType
                )

                || supportingProfiles
                .stream()
                .anyMatch(
                        profile ->
                                profile.tenGodType()
                                        == tenGodType
                )

                || referenceProfiles
                .stream()
                .anyMatch(
                        profile ->
                                profile.tenGodType()
                                        == tenGodType
                );
    }

    private static void validateGroup(

            NatalExactTenGodAnalysis analysis,

            List<NatalExactTenGodProfile> profiles,

            Set<StructureTenGodType> seen,

            String groupName

    ) {

        for (
                NatalExactTenGodProfile profile
                : profiles
        ) {

            Objects.requireNonNull(
                    profile,
                    "Exact TenGod Selection Profile은 null일 수 없습니다."
            );

            NatalExactTenGodProfile canonical =
                    analysis
                            .requireProfile(
                                    profile.tenGodType()
                            );

            /*
             * =================================================
             * 원본 Profile Identity 보존
             * =================================================
             */

            if (
                    canonical
                            != profile
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Selection이 공식 Analysis Profile Identity를 보존하지 않았습니다: "
                                + profile.tenGodType()
                );
            }

            /*
             * =================================================
             * 없는 십성 선택 금지
             * =================================================
             */

            if (
                    !profile.present()
            ) {

                throw new IllegalArgumentException(
                        "출현하지 않은 십성을 Personality Selection에 넣을 수 없습니다: "
                                + profile.tenGodType()
                );
            }

            /*
             * =================================================
             * 그룹 간 중복 금지
             * =================================================
             */

            if (
                    !seen.add(
                            profile.tenGodType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Selection의 십성이 그룹 사이에서 중복되었습니다: "
                                + profile.tenGodType()
                                + ", group="
                                + groupName
                );
            }
        }
    }
}