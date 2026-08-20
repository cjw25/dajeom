package com.dajeom.model;

import com.dajeom.type.SpecialStructureType;
import com.dajeom.type.StructureStatus;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;
import java.util.Optional;

/**
 * 원국의 구조적 중심축.
 *
 * =================================================
 *
 * 사용 Source:
 *
 * 5차 StructureAnalysis
 *
 * =================================================
 *
 * 역할:
 *
 * 십성 Family 중 어떤 기능이
 * 단순히 존재하는 것을 넘어
 * 원국의 주격 중심으로 선택되었는지
 * 보존한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 격국은 Family Power에
 * 다시 점수로 더하지 않는다.
 *
 * 즉:
 *
 * 식상 Power
 * +
 * 식상격 추가점수
 *
 * 같은 중복 계산을 하지 않는다.
 *
 * 격국은:
 *
 * "어느 기능이 중심인가"
 *
 * 를 판단하는 중심성 Evidence다.
 */
public record NatalStructuralCenterEffect(

        StructureStatus status,

        String primaryStructureName,

        Optional<StructureTenGodType>
        primaryStandardTenGod,

        Optional<SpecialStructureType>
        primarySpecialType,

        boolean established,

        boolean burdened,

        boolean damaged,

        boolean stronglyDamaged,

        boolean relativelyPure,

        boolean highlyMixed,

        boolean specialReviewRequired,

        boolean completeChart

) {

    public NatalStructuralCenterEffect {

        Objects.requireNonNull(
                status,
                "격국 상태는 필수입니다."
        );

        Objects.requireNonNull(
                primaryStructureName,
                "주격 이름은 필수입니다."
        );

        if (
                primaryStructureName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "주격 이름은 비어 있을 수 없습니다."
            );
        }

        primaryStandardTenGod =
                primaryStandardTenGod == null
                        ? Optional.empty()
                        : primaryStandardTenGod;

        primarySpecialType =
                primarySpecialType == null
                        ? Optional.empty()
                        : primarySpecialType;

        if (
                primaryStandardTenGod.isPresent()
                        && primarySpecialType.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "주격은 일반격과 특수격을 "
                            + "동시에 가질 수 없습니다."
            );
        }
    }

    public boolean standardStructure() {

        return primaryStandardTenGod
                .isPresent();
    }

    public boolean specialStructure() {

        return primarySpecialType
                .isPresent();
    }
}