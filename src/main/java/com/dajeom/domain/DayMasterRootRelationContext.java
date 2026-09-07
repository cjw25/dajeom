package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 한 통근 지점과
 * 그 지지가 원국에서 맺고 있는 지지 관계.
 *
 * 아직:
 *
 * - 뿌리가 살아 있다
 * - 뿌리가 깨졌다
 * - 충으로 제거됐다
 * - 합으로 강화됐다
 *
 * 같은 해석을 하지 않는다.
 *
 * 단순히:
 *
 * "이 뿌리가 있는 지지에 어떤 관계들이 걸려 있는가"
 *
 * 를 보존한다.
 */
public record DayMasterRootRelationContext(

        StemGroundHit root,

        List<BranchRelation> branchRelations

) {

    public DayMasterRootRelationContext {

        Objects.requireNonNull(
                root,
                "통근 지점은 필수입니다."
        );

        branchRelations =
                branchRelations == null
                        ? List.of()
                        : List.copyOf(
                        branchRelations
                );

        for (BranchRelation relation : branchRelations) {

            Objects.requireNonNull(
                    relation,
                    "지지 관계 목록에 null이 포함될 수 없습니다."
            );

            boolean touchesRoot =
                    relation.leftPosition()
                            == root.branchPosition()
                            ||
                            relation.rightPosition()
                                    == root.branchPosition();

            if (!touchesRoot) {

                throw new IllegalArgumentException(
                        "통근 지점과 관계없는 BranchRelation이 포함되어 있습니다."
                );
            }
        }
    }

    public boolean hasBranchRelation() {

        return !branchRelations.isEmpty();
    }

    public PillarPosition rootPosition() {

        return root.branchPosition();
    }

    public EarthlyBranch rootBranch() {

        return root.branch();
    }

    public HiddenStemPhase rootPhase() {

        return root.hiddenStemPhase();
    }

    public StemGroundRelationType rootRelationType() {

        return root.relationType();
    }
}