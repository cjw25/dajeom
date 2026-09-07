package com.dajeom.calculation;

import com.dajeom.domain.DayMasterForceFormationAnalysis;
import com.dajeom.domain.DayMasterRelationAnalysis;
import com.dajeom.domain.DayMasterRelationType;
import com.dajeom.domain.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 일간 기준 세력 형성 구조 계산기.
 *
 * 이 클래스는 기존 DayMasterRelationAnalysis를
 * 명리 관계별로 정리만 한다.
 *
 * 숫자:
 * 없음
 *
 * 개수 비교:
 * 없음
 *
 * 신강/신약:
 * 판정하지 않음
 *
 * 득세:
 * 판정하지 않음
 */
@Component
public class DayMasterForceFormationCalculator {

    public DayMasterForceFormationAnalysis analyze(

            HeavenlyStem dayMaster,

            DayMasterRelationAnalysis relations

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                relations,
                "일간 생극관계 분석은 필수입니다."
        );

        if (
                relations.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "생극관계 분석의 일간이 대상 일간과 일치하지 않습니다."
            );
        }

        return new DayMasterForceFormationAnalysis(

                dayMaster,

                /*
                 * 천간에서 일간을 생함
                 */
                relations.visibleRelationsOf(
                        DayMasterRelationType.GENERATES_DAY_MASTER
                ),

                /*
                 * 천간에서 일간과 같은 오행
                 */
                relations.visibleRelationsOf(
                        DayMasterRelationType.SAME_ELEMENT
                ),

                /*
                 * 천간에서 일간이 생함
                 */
                relations.visibleRelationsOf(
                        DayMasterRelationType.GENERATED_BY_DAY_MASTER
                ),

                /*
                 * 천간에서 일간이 극함
                 */
                relations.visibleRelationsOf(
                        DayMasterRelationType.CONTROLLED_BY_DAY_MASTER
                ),

                /*
                 * 천간에서 일간을 극함
                 */
                relations.visibleRelationsOf(
                        DayMasterRelationType.CONTROLS_DAY_MASTER
                ),

                /*
                 * 지장간에서 일간을 생함
                 */
                relations.hiddenRelationsOf(
                        DayMasterRelationType.GENERATES_DAY_MASTER
                ),

                /*
                 * 지장간에서 일간과 같은 오행
                 */
                relations.hiddenRelationsOf(
                        DayMasterRelationType.SAME_ELEMENT
                ),

                /*
                 * 지장간에서 일간이 생함
                 */
                relations.hiddenRelationsOf(
                        DayMasterRelationType.GENERATED_BY_DAY_MASTER
                ),

                /*
                 * 지장간에서 일간이 극함
                 */
                relations.hiddenRelationsOf(
                        DayMasterRelationType.CONTROLLED_BY_DAY_MASTER
                ),

                /*
                 * 지장간에서 일간을 극함
                 */
                relations.hiddenRelationsOf(
                        DayMasterRelationType.CONTROLS_DAY_MASTER
                )
        );
    }
}