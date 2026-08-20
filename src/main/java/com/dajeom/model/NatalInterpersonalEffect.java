package com.dajeom.model;

/**
 * 원국 관계/합화 구조에서
 * 대인 반응에 필요한 구조적 사실을
 * 정규화한 Evidence.
 *
 * =================================================
 *
 * 사용 Source:
 *
 * 8차 RelationAnalysis
 *
 * +
 *
 * 9차 CombinationTransformationAnalysis
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 합 = 좋음
 * 충 = 나쁨
 *
 * 같은 단순 길흉 판정을 하지 않는다.
 *
 * 이 단계에서는:
 *
 * - 관계가 실제 존재하는가
 * - DAY가 관계에 참여하는가
 * - 관계가 중첩되는가
 * - 합화가 실제 활성되는가
 * - 원기능 감소가 있는가
 * - 결합 방해가 있는가
 *
 * 만 보존한다.
 *
 * 실제 대인 성향 판정은
 * 후속 CrossFunctionalPattern에서 수행한다.
 */
public record NatalInterpersonalEffect(

        int heavenlyStemRelationPairCount,

        int earthlyBranchRelationPairCount,

        boolean dayMasterInStemRelation,

        boolean dayBranchInRelation,

        int dayBranchRelationPairCount,

        boolean mixedEarthlyBranchRelationPair,

        boolean anyCombinationRelation,

        boolean activeTransformation,

        boolean strongTransformation,

        boolean originalFunctionReduction,

        boolean dayMasterInCombination,

        boolean dayMasterFunctionReduced,

        boolean dayBranchInCombination,

        boolean meaningfulEarthlyBranchObstruction,

        boolean severeEarthlyBranchObstruction,

        boolean completeChart

) {

    public NatalInterpersonalEffect {

        if (
                heavenlyStemRelationPairCount < 0
                        || earthlyBranchRelationPairCount < 0
                        || dayBranchRelationPairCount < 0
        ) {

            throw new IllegalArgumentException(
                    "관계 Pair 개수는 음수일 수 없습니다."
            );
        }

        if (
                dayBranchRelationPairCount
                        > earthlyBranchRelationPairCount
        ) {

            throw new IllegalArgumentException(
                    "DAY 지지 관계 개수는 "
                            + "전체 지지 관계 개수를 초과할 수 없습니다."
            );
        }

        if (
                strongTransformation
                        && !activeTransformation
        ) {

            throw new IllegalArgumentException(
                    "강한 합화는 "
                            + "활성 합화에 포함되어야 합니다."
            );
        }

        if (
                dayMasterFunctionReduced
                        && !dayMasterInCombination
        ) {

            throw new IllegalArgumentException(
                    "일간 기능 감소는 "
                            + "일간이 합 관계에 포함된 경우에만 가능합니다."
            );
        }

        if (
                severeEarthlyBranchObstruction
                        && !meaningfulEarthlyBranchObstruction
        ) {

            throw new IllegalArgumentException(
                    "심한 지지 결합 방해는 "
                            + "유의미 방해에 포함되어야 합니다."
            );
        }
    }
}