package com.dajeom.model;

import com.dajeom.type.BranchRelationType;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 / 대운 / 세운 사이에서
 * 발견된 지지 관계.
 *
 * 2개 관계:
 * 육합, 충, 형, 자형, 해, 파
 *
 * 3개 관계:
 * 삼합, 삼형
 */
public record LuckBranchInteraction(

        BranchRelationType type,

        List<LuckPillarSource> sources,

        FiveElement combinationElement

) {

    public LuckBranchInteraction {

        Objects.requireNonNull(
                type,
                "지지 관계 종류는 필수입니다."
        );

        Objects.requireNonNull(
                sources,
                "관계 출처 목록은 필수입니다."
        );

        sources =
                List.copyOf(
                        sources
                );

        if (
                sources.size() < 2
                        || sources.size() > 3
        ) {

            throw new IllegalArgumentException(
                    "지지 관계는 2개 또는 3개의 기둥으로 구성되어야 합니다."
            );
        }
    }

    public Optional<FiveElement>
    optionalCombinationElement() {

        return Optional.ofNullable(
                combinationElement
        );
    }
}