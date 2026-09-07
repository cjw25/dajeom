package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

public record BranchGroupRelation(
        BranchGroupRelationType type,
        String level,
        FiveElement resultElement,
        List<BranchGroupMember> members
) {

    public BranchGroupRelation {

        Objects.requireNonNull(
                type,
                "그룹 관계 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "그룹 관계 성립 수준이 필요합니다."
        );

        Objects.requireNonNull(
                resultElement,
                "그룹 관계 오행이 필요합니다."
        );

        Objects.requireNonNull(
                members,
                "그룹 관계 참여 지지가 필요합니다."
        );

        members =
                List.copyOf(
                        members
                );

        if (members.size() != 3) {

            throw new IllegalArgumentException(
                    "완전 삼합/방합은 정확히 세 개의 지지가 필요합니다."
            );
        }

        if (!"FULL".equals(level)) {

            throw new IllegalArgumentException(
                    "BranchGroupRelation은 FULL 관계만 생성합니다."
            );
        }
    }
}