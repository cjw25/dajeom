package com.dajeom.model;

import com.dajeom.type.BranchRelationType;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PillarPosition;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 지지 사이에서 발견된 관계.
 *
 * 2개 관계:
 *
 * - 육합
 * - 충
 * - 형
 * - 자형
 * - 해
 * - 파
 *
 * 3개 관계:
 *
 * - 삼합
 * - 삼형
 *
 * combinationElement는
 * 육합/삼합의 전통적인 합화 오행 후보다.
 *
 * 실제 합화 성립 여부는
 * 이후 별도 분석에서 판단한다.
 */
public record BranchRelation(

        BranchRelationType type,

        List<PillarPosition> positions,

        List<EarthlyBranch> branches,

        FiveElement combinationElement

) {

    public BranchRelation {

        Objects.requireNonNull(
                type,
                "지지 관계 종류는 필수입니다."
        );

        Objects.requireNonNull(
                positions,
                "기둥 위치 목록은 필수입니다."
        );

        Objects.requireNonNull(
                branches,
                "지지 목록은 필수입니다."
        );

        positions =
                List.copyOf(
                        positions
                );

        branches =
                List.copyOf(
                        branches
                );

        if (
                positions.size()
                        != branches.size()
        ) {

            throw new IllegalArgumentException(
                    "기둥 위치 수와 지지 수가 일치해야 합니다."
            );
        }

        if (
                positions.size() < 2
                        || positions.size() > 3
        ) {

            throw new IllegalArgumentException(
                    "지지 관계는 2개 또는 3개의 지지로 구성되어야 합니다."
            );
        }
    }

    /**
     * 육합/삼합의 합화 오행 후보.
     *
     * 충, 형, 해, 파 등에는 존재하지 않는다.
     */
    public Optional<FiveElement>
    optionalCombinationElement() {

        return Optional.ofNullable(
                combinationElement
        );
    }
}