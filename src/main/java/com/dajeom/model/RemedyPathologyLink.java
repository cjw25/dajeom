package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.PathologyPatternType;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.RemedyActionType;
import com.dajeom.type.StructureDamagePatternType;

import java.util.Objects;

/**
 * 정밀화 6차-5.
 *
 * 하나의 약(藥) 오행 후보가
 * 하나의 병(病) 구조를
 * 어떤 방식으로 완화하는지 연결한다.
 *
 * pathologyElement:
 *
 * 병 구조가 특정 오행에 연결되어 있을 때
 * 그 병 오행을 보존한다.
 *
 * 예:
 *
 * 金 과다
 * ->
 * ELEMENT_OVERCONCENTRATION
 * ->
 * pathologyElement = METAL
 *
 * structureDamagePattern:
 *
 * PRIMARY_STRUCTURE_DAMAGE의 경우
 * 5차에서 실제 어떤 파격 패턴 때문에
 * 구조 손상이 발생했는지 보존한다.
 */
public record RemedyPathologyLink(

        PathologyPatternType pathologyPatternType,

        PathologySeverity pathologySeverity,

        FiveElement pathologyElement,

        RemedyActionType actionType,

        boolean direct,

        StructureDamagePatternType structureDamagePattern

) {

    public RemedyPathologyLink {

        Objects.requireNonNull(
                pathologyPatternType,
                "병 구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                pathologySeverity,
                "병 구조 심각도가 필요합니다."
        );

        Objects.requireNonNull(
                actionType,
                "약 작용 유형이 필요합니다."
        );

        if (
                pathologySeverity.none()
        ) {

            throw new IllegalArgumentException(
                    "약 후보는 NONE 병 구조와 연결할 수 없습니다."
            );
        }

        if (
                pathologyPatternType
                        == PathologyPatternType
                        .ELEMENT_OVERCONCENTRATION

                        && pathologyElement == null
        ) {

            throw new IllegalArgumentException(
                    "오행 과다집중 병 구조에는 대상 오행이 필요합니다."
            );
        }

        if (
                pathologyPatternType
                        == PathologyPatternType
                        .PRIMARY_STRUCTURE_DAMAGE
        ) {

            if (
                    structureDamagePattern == null
            ) {

                throw new IllegalArgumentException(
                        "주격 구조 손상 약 Evidence에는 실제 파격 유형이 필요합니다."
                );
            }

        } else {

            if (
                    structureDamagePattern != null
            ) {

                throw new IllegalArgumentException(
                        "주격 구조 손상 이외의 병에는 파격 유형을 지정할 수 없습니다."
                );
            }
        }
    }

    public boolean meaningfulPathology() {

        return pathologySeverity
                .meaningful();
    }

    public boolean severePathology() {

        return pathologySeverity
                .severe();
    }

    public boolean criticalPathology() {

        return pathologySeverity
                .critical();
    }

    public boolean indirect() {

        return !direct;
    }

    public boolean climateAction() {

        return actionType
                .climateAction();
    }

    public boolean structureAction() {

        return actionType
                .structureAction();
    }
}