package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간 왕쇠 판단 직전의
 * 세 가지 핵심 구조 자료.
 *
 * 전통적으로 흔히:
 *
 * 得令
 * 得地
 * 得勢
 *
 * 라고 묶어 설명하는 영역을
 * boolean이나 점수로 단순화하지 않고
 * 실제 명리 상태 그대로 보존한다.
 *
 * =================================================
 *
 * 월령:
 *
 * 旺 / 相 / 休 / 囚 / 死
 *
 * =================================================
 *
 * 지지:
 *
 * 일간의 실제 통근 구조
 *
 * - 동일 천간 근
 * - 같은 오행 다른 천간 연결
 * - 어느 지지인지
 * - EARLY / MIDDLE / MAIN
 *
 * =================================================
 *
 * 천간 세력:
 *
 * 천간에 드러난 생조와 동류
 *
 * =================================================
 *
 * 이 객체 자체는:
 *
 * - 신강
 * - 신약
 * - 중화
 *
 * 를 판정하지 않는다.
 */
public record DayMasterThreeFactorsAnalysis(

        HeavenlyStem dayMaster,

        DayMasterSeasonAnalysis season,

        DayMasterGroundAnalysis ground,

        DayMasterVisibleSupportPattern visibleSupportPattern,

        List<DayMasterVisibleStemRelation>
        visibleGeneratingRelations,

        List<DayMasterVisibleStemRelation>
        visibleSameElementRelations

) {

    public DayMasterThreeFactorsAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                season,
                "월령 계절 상태는 필수입니다."
        );

        Objects.requireNonNull(
                ground,
                "지지 뿌리 구조는 필수입니다."
        );

        Objects.requireNonNull(
                visibleSupportPattern,
                "천간 생조 구조는 필수입니다."
        );

        visibleGeneratingRelations =
                visibleGeneratingRelations == null
                        ? List.of()
                        : List.copyOf(
                        visibleGeneratingRelations
                );

        visibleSameElementRelations =
                visibleSameElementRelations == null
                        ? List.of()
                        : List.copyOf(
                        visibleSameElementRelations
                );

        if (
                season.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령 계절 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                ground.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "지지 뿌리 분석의 일간이 일치하지 않습니다."
            );
        }

        for (
                DayMasterVisibleStemRelation relation
                : visibleGeneratingRelations
        ) {

            Objects.requireNonNull(
                    relation,
                    "생조 천간 목록에 null이 포함될 수 없습니다."
            );

            if (
                    relation.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "생조 천간 관계의 일간이 일치하지 않습니다."
                );
            }

            if (
                    relation.relationType()
                            != DayMasterRelationType
                            .GENERATES_DAY_MASTER
            ) {

                throw new IllegalArgumentException(
                        "visibleGeneratingRelations에는 일간을 생하는 관계만 포함할 수 있습니다."
                );
            }
        }

        for (
                DayMasterVisibleStemRelation relation
                : visibleSameElementRelations
        ) {

            Objects.requireNonNull(
                    relation,
                    "동류 천간 목록에 null이 포함될 수 없습니다."
            );

            if (
                    relation.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "동류 천간 관계의 일간이 일치하지 않습니다."
                );
            }

            if (
                    relation.relationType()
                            != DayMasterRelationType
                            .SAME_ELEMENT
            ) {

                throw new IllegalArgumentException(
                        "visibleSameElementRelations에는 같은 오행 관계만 포함할 수 있습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 월령
     * =================================================
     */

    public FiveElementSeasonState seasonState() {

        return season
                .seasonState();
    }

    /*
     * =================================================
     * 지지 뿌리
     * =================================================
     */

    public DayMasterGroundPattern groundPattern() {

        return ground
                .pattern();
    }

    public List<StemGroundHit> exactStemRoots() {

        return ground
                .exactStemRoots();
    }

    public List<StemGroundHit> sameElementRoots() {

        return ground
                .sameElementOtherStemRoots();
    }

    /*
     * =================================================
     * 천간 생조
     * =================================================
     */

    public boolean hasVisibleGeneratingRelation() {

        return !visibleGeneratingRelations
                .isEmpty();
    }

    public boolean hasVisibleSameElementRelation() {

        return !visibleSameElementRelations
                .isEmpty();
    }
}