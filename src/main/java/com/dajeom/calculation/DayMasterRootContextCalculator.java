package com.dajeom.calculation;

import com.dajeom.domain.BranchRelation;
import com.dajeom.domain.DayMasterGroundAnalysis;
import com.dajeom.domain.DayMasterRootContextAnalysis;
import com.dajeom.domain.DayMasterRootRelationContext;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.StemGroundHit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 일간 통근 지점과
 * 기존 원국 BranchRelation을 연결한다.
 *
 * 새로운 합충형파해 계산을 하지 않는다.
 *
 * 기존에 이미 계산된 BranchRelation을
 * 통근 지점에 연결하는 역할만 한다.
 *
 * 아직 관계의 길흉/강약을 해석하지 않는다.
 */
@Component
public class DayMasterRootContextCalculator {

    public DayMasterRootContextAnalysis analyze(

            HeavenlyStem dayMaster,

            DayMasterGroundAnalysis ground,

            List<BranchRelation> branchRelations

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                ground,
                "일간 뿌리 구조는 필수입니다."
        );

        if (
                ground.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "일간과 뿌리 분석 대상 천간이 일치하지 않습니다."
            );
        }

        List<BranchRelation> safeRelations =
                branchRelations == null
                        ? List.of()
                        : List.copyOf(
                        branchRelations
                );

        List<DayMasterRootRelationContext>
                exactContexts =
                buildContexts(
                        ground.exactStemRoots(),
                        safeRelations
                );

        List<DayMasterRootRelationContext>
                sameElementContexts =
                buildContexts(
                        ground.sameElementOtherStemRoots(),
                        safeRelations
                );

        return new DayMasterRootContextAnalysis(
                dayMaster,
                exactContexts,
                sameElementContexts
        );
    }

    private List<DayMasterRootRelationContext> buildContexts(

            List<StemGroundHit> roots,

            List<BranchRelation> branchRelations

    ) {

        if (
                roots == null
                        || roots.isEmpty()
        ) {

            return List.of();
        }

        List<DayMasterRootRelationContext> result =
                new ArrayList<>();

        for (StemGroundHit root : roots) {

            Objects.requireNonNull(
                    root,
                    "통근 목록에 null이 포함될 수 없습니다."
            );

            List<BranchRelation> touchingRelations =
                    branchRelations
                            .stream()
                            .filter(
                                    Objects::nonNull
                            )
                            .filter(
                                    relation ->
                                            relation.leftPosition()
                                                    == root.branchPosition()
                                                    ||
                                                    relation.rightPosition()
                                                            == root.branchPosition()
                            )
                            .toList();

            result.add(
                    new DayMasterRootRelationContext(
                            root,
                            touchingRelations
                    )
            );
        }

        return List.copyOf(
                result
        );
    }
}