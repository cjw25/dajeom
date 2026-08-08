package com.dajeom.model;

import com.dajeom.type.BranchRelationType;
import com.dajeom.type.StemRelationType;

import java.util.List;
import java.util.Objects;

/**
 * 원국 전체의 천간/지지 관계 분석 결과.
 *
 * 아직 이 관계가:
 *
 * - 좋은지
 * - 나쁜지
 * - 어느 분야에 강하게 작용하는지
 *
 * 를 판단하지 않는다.
 *
 * 관계가 존재한다는 객관적인 구조만 저장한다.
 */
public record RelationAnalysis(

        List<StemRelation> stemRelations,

        List<BranchRelation> branchRelations

) {

    public RelationAnalysis {

        Objects.requireNonNull(
                stemRelations,
                "천간 관계 목록은 필수입니다."
        );

        Objects.requireNonNull(
                branchRelations,
                "지지 관계 목록은 필수입니다."
        );

        stemRelations =
                List.copyOf(
                        stemRelations
                );

        branchRelations =
                List.copyOf(
                        branchRelations
                );
    }

    public boolean hasStemRelation(
            StemRelationType type
    ) {

        Objects.requireNonNull(
                type,
                "천간 관계 종류는 필수입니다."
        );

        return stemRelations
                .stream()
                .anyMatch(
                        relation ->
                                relation.type()
                                        == type
                );
    }

    public boolean hasBranchRelation(
            BranchRelationType type
    ) {

        Objects.requireNonNull(
                type,
                "지지 관계 종류는 필수입니다."
        );

        return branchRelations
                .stream()
                .anyMatch(
                        relation ->
                                relation.type()
                                        == type
                );
    }
}