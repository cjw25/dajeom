package com.dajeom.dto;

import com.dajeom.domain.BranchGroupMember;
import com.dajeom.domain.BranchGroupRelation;

import java.util.List;
import java.util.Objects;

public record BranchGroupRelationResponse(
        String type,
        String typeKorean,
        String typeHanja,

        String level,

        String element,
        String elementKorean,
        String elementHanja,

        List<MemberResponse> members
) {

    public BranchGroupRelationResponse {

        members =
                members == null
                        ? List.of()
                        : List.copyOf(
                        members
                );
    }

    public static BranchGroupRelationResponse from(
            BranchGroupRelation relation
    ) {

        Objects.requireNonNull(
                relation,
                "지지 그룹 관계가 필요합니다."
        );

        List<MemberResponse> members =
                relation.members()
                        .stream()
                        .map(
                                MemberResponse::from
                        )
                        .toList();

        return new BranchGroupRelationResponse(
                relation.type().name(),
                relation.type().korean(),
                relation.type().hanja(),

                relation.level(),

                relation.resultElement().name(),
                relation.resultElement().korean(),
                relation.resultElement().hanja(),

                members
        );
    }

    public record MemberResponse(
            String position,
            String positionKorean,
            String branch,
            String branchKorean
    ) {

        public static MemberResponse from(
                BranchGroupMember member
        ) {

            Objects.requireNonNull(
                    member,
                    "그룹 관계 참여 지지가 필요합니다."
            );

            return new MemberResponse(
                    member.position().name(),
                    member.position().korean(),
                    member.branch().hanja(),
                    member.branch().korean()
            );
        }
    }
}